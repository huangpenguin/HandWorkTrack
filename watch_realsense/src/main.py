import threading
import time
from galaxy import get_galaxy_recorder
from microphone_audio import get_audio_recorder
from moverio_sensor import MoverioRecorder
from moverio_video import VideoUDPRecorder
from pathlib import Path
import socket

host_ip = socket.gethostbyname(socket.gethostname())  # "192.168.8.104"
Path("./data").mkdir(parents=True, exist_ok=True)

# realsense = get_realsense_recorder("realsense.mp4", "realsense.csv")
audio = get_audio_recorder("./data/audio.mp3", "./data/audio.csv")
galaxy_left = get_galaxy_recorder(
    host_ip, "./data/galaxy_left.csv", port=46000)
galaxy_right = get_galaxy_recorder(
    host_ip, "./data/galaxy_right.csv", port=46001)
moverio_sensor = MoverioRecorder(
    host_ip, port=46002, output_folder="./data/glass/")
moverio_video = VideoUDPRecorder(
    host_ip, port=46004, output_folder="data/glass/")


def start_moverio():
    try:
        moverio_video.start()
    except Exception as e:
        print(f"Error during server operation: {e}")


moverio_video_thread = threading.Thread(target=start_moverio, daemon=True)
moverio_video_thread.start()

print("Waiting for Moverio connection...")
while moverio_video.receive_start_time is None:
    pass

print("starting other devices...")

try:
    with open("./data/first_timestamp.csv", "w", encoding="utf8") as f:

        # f.write(f"realsense,{time.time()}\n")
        # realsense.start()
        f.write(f"moverio_video,{moverio_video.receive_start_time}\n")

        f.write(f"galaxy_left,{time.time()}\n")
        galaxy_left.start()

        f.write(f"galaxy_right,{time.time()}\n")
        galaxy_right.start()

        f.write(f"audio,{time.time()}\n")
        audio.start()

        f.write(f"moverio_sensor,{time.time()}\n")
        moverio_sensor.start()

    # cv2.namedWindow("RealSense", cv2.WINDOW_AUTOSIZE)
    # while True:
    #     color_image = realsense.get()
    #     k = cv2.getWindowProperty("RealSense", cv2.WND_PROP_VISIBLE)
    #     if k == 0.0:
    #         break
    #     if color_image is None:
    #         continue
    #     cv2.imshow("RealSense", color_image)
    #     cv2.waitKey(1)

    while True:
        command = input("All started. Type 'q' to stop all recorders:\n")
        if command.strip().lower() == 'q':
            break

finally:
    print("Stopping all recorders...")
    # realsense.stop()
    recorders = [galaxy_left, galaxy_right, audio, moverio_sensor, moverio_video]
    # recorders = [moverio_sensor, moverio_video]
    for recorder in recorders:
        try:
            recorder.stop()
        except Exception as e:
            print(f"Error stopping {recorder}: {e}")

    moverio_video_thread.join()
    print("Stopped all recorders.")
    # cv2.destroyAllWindows()
