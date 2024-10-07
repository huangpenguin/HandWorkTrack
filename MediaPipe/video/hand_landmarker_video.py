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
cv2.namedWindow('Hand Detection', cv2.WINDOW_NORMAL)
cv2.resizeWindow('Hand Detection', 800, 600)

while cap.isOpened():
    ret, frame = cap.read()
    #print(f"Frame read: {ret}")
    if not ret:
        print("Failed to read frame or reached end of video.")
        break

    # Convert the frame to MediaPipe Image format.
    mp_image = mp.Image(image_format=mp.ImageFormat.SRGB, data=frame)

    # Get the current frame's timestamp in milliseconds.
    timestamp_ms = int(cap.get(cv2.CAP_PROP_POS_MSEC))

    # Detect hand landmarks for the current frame.
    detection_result = detector.detect_for_video(mp_image, timestamp_ms)

    # Extract world landmarks and categorize by left and right hand.
    left_hand, right_hand = extract_world_landmarks(detection_result)

    if left_hand:
        print(f"Left hand landmarks: {left_hand.landmarks}")
    if right_hand:
        print(f"Right hand landmarks: {right_hand.landmarks}")

    # Draw the landmarks on the current frame.
    annotated_image = draw_landmarks_on_image(frame, detection_result)

    # Display the annotated frame.
    cv2.imshow('Hand Detection', annotated_image)
 
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
