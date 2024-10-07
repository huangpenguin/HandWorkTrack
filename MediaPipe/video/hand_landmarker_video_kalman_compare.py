import cv2
import numpy as np
import mediapipe as mp
from mediapipe.tasks.python import vision
from mediapipe.tasks import python
from mediapipe.framework.formats import landmark_pb2
import matplotlib.pyplot as plt

plt.ion() 
MARGIN = 10  # pixels
FONT_SIZE = 1
FONT_THICKNESS = 1
HANDEDNESS_TEXT_COLOR = (88, 205, 54)  # vibrant green

def create_kalman_filter():
    kalman = cv2.KalmanFilter(6, 3)  # 6 状态 (x, y, z, dx, dy, dz), 3 观测量 (x, y, z)
    kalman.measurementMatrix = np.eye(3, 6, dtype=np.float32)
    kalman.transitionMatrix = np.eye(6, 6, dtype=np.float32)
    kalman.processNoiseCov = np.eye(6, 6, dtype=np.float32) * 0.03
    kalman.measurementNoiseCov = np.eye(3, 3, dtype=np.float32) * 1
    kalman.errorCovPost = np.eye(6, 6, dtype=np.float32)
    return kalman

# 初始化 Kalman Filters
left_hand_kalman_filters = [create_kalman_filter() for _ in range(21)]  # 假设有21个关键点
right_hand_kalman_filters = [create_kalman_filter() for _ in range(21)]

def update_kalman_filter(kalman, measurement):
    '''Update Kalman Filter with new measurement.'''
    prediction = kalman.predict()
    kalman.correct(np.array(measurement, dtype=np.float32))  # 校正
    return prediction[:3]  # 返回平滑后的 (x, y, z)

def smooth_landmarks(landmarks, kalman_filters):
    '''Apply Kalman Filter to smooth hand landmarks.'''
    smoothed_landmarks = []
    for i, landmark in enumerate(landmarks):
        x, y, z = landmark
        kalman = kalman_filters[i]
        smoothed_position = update_kalman_filter(kalman, [x, y, z])
        smoothed_landmarks.append(smoothed_position)
    return smoothed_landmarks

class HandLandmarksData:
    def __init__(self, hand_type):
        self.hand_type = hand_type 
        self.landmarks = []

    def add_landmark(self, x, y, z):
        self.landmarks.append((x, y, z))

def extract_world_landmarks(detection_result):
    hand_landmarks_list = detection_result.hand_world_landmarks
    handedness_list = detection_result.handedness

    left_hand = None
    right_hand = None

    for idx in range(len(hand_landmarks_list)):
        hand_landmarks = hand_landmarks_list[idx]
        handedness = handedness_list[idx][0].category_name

        if handedness == "Left":
            left_hand = HandLandmarksData("Left")
            for landmark in hand_landmarks:
                left_hand.add_landmark(landmark.x, landmark.y, landmark.z)
        elif handedness == "Right":
            right_hand = HandLandmarksData("Right")
            for landmark in hand_landmarks:
                right_hand.add_landmark(landmark.x, landmark.y, landmark.z)

    return left_hand, right_hand

def draw_landmarks_on_image(rgb_image, detection_result):
    hand_landmarks_list = detection_result.hand_landmarks
    handedness_list = detection_result.handedness
    annotated_image = np.copy(rgb_image)

    # Loop through the detected hands to visualize.
    for idx in range(len(hand_landmarks_list)):
        hand_landmarks = hand_landmarks_list[idx]
        handedness = handedness_list[idx]

        # Draw the hand landmarks.
        hand_landmarks_proto = landmark_pb2.NormalizedLandmarkList()
        hand_landmarks_proto.landmark.extend([
            landmark_pb2.NormalizedLandmark(x=landmark.x, y=landmark.y, z=landmark.z) for landmark in hand_landmarks
        ])
        mp.solutions.drawing_utils.draw_landmarks(
            annotated_image,
            hand_landmarks_proto,
            mp.solutions.hands.HAND_CONNECTIONS,
            mp.solutions.drawing_styles.get_default_hand_landmarks_style(),
            mp.solutions.drawing_styles.get_default_hand_connections_style()
        )

        # Get the top left corner of the detected hand's bounding box.
        height, width, _ = annotated_image.shape
        x_coordinates = [landmark.x for landmark in hand_landmarks]
        y_coordinates = [landmark.y for landmark in hand_landmarks]
        text_x = int(min(x_coordinates) * width)
        text_y = int(min(y_coordinates) * height) - MARGIN

        # Draw handedness (left or right hand) on the image.
        cv2.putText(annotated_image, f"{handedness[0].category_name}",
                    (text_x, text_y), cv2.FONT_HERSHEY_DUPLEX,
                    FONT_SIZE, HANDEDNESS_TEXT_COLOR, FONT_THICKNESS, cv2.LINE_AA)

    return annotated_image

# STEP 2: Create a HandLandmarker object for video.
base_options = python.BaseOptions(model_asset_path='hand_landmarker.task')
options = vision.HandLandmarkerOptions(base_options=base_options, num_hands=2, running_mode=vision.RunningMode.VIDEO)
detector = vision.HandLandmarker.create_from_options(options)

# STEP 3: Load the input video.
cap = cv2.VideoCapture('C:/Users/huang/Desktop/New folder/video/WIN_20240920_16_20_50_Pro.mp4')
cv2.namedWindow('Original Hand Detection', cv2.WINDOW_NORMAL)
cv2.resizeWindow('Original Hand Detection', 600, 400)
cv2.namedWindow('Kalman Filtered Hand Detection', cv2.WINDOW_NORMAL)
cv2.resizeWindow('Kalman Filtered Hand Detection', 600, 400)

cv2.moveWindow('Original Hand Detection', 100, 100)
cv2.moveWindow('Kalman Filtered Hand Detection', 800, 100)


# 在每一帧视频中进行检测并分别显示两种效果
while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    # 创建未处理的帧副本，保留原始检测结果的显示
    original_frame = frame.copy()
    
    timestamp_ms = int(cap.get(cv2.CAP_PROP_POS_MSEC))
    # 将帧传递给检测器
    image = mp.Image(image_format=mp.ImageFormat.SRGB, data=frame)
    detection_result = detector.detect_for_video(image, timestamp_ms)

    # 提取世界坐标，未经 Kalman 滤波的原始结果
    left_hand, right_hand = extract_world_landmarks(detection_result)

    # 绘制未使用 Kalman 滤波的结果
    if left_hand or right_hand:
        original_annotated_image = draw_landmarks_on_image(original_frame, detection_result)
    
    # 对左手和右手的关键点进行 Kalman 滤波
    if left_hand:
        left_hand.landmarks = smooth_landmarks(left_hand.landmarks, left_hand_kalman_filters)
    if right_hand:
        right_hand.landmarks = smooth_landmarks(right_hand.landmarks, right_hand_kalman_filters)

    # 绘制使用 Kalman 滤波后的结果
    if left_hand or right_hand:
        kalman_annotated_image = draw_landmarks_on_image(frame, detection_result)

    # 同时显示原始和经过 Kalman 滤波后的图像
    cv2.imshow('Original Hand Detection', original_annotated_image)
    cv2.imshow('Kalman Filtered Hand Detection', kalman_annotated_image)

    if cv2.waitKey(100) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()

