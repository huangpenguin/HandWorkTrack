import cv2

video = cv2.VideoCapture("movie_20250204152757.mp4")
print(video.get(cv2.CAP_PROP_FPS))
print(video.get(cv2.CAP_PROP_FRAME_COUNT))