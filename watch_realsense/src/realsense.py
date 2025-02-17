import queue

import cv2
import numpy as np
import pyrealsense2 as rs


class RealSenseRecorder:
    def __init__(self, mp4_file: str, timestamp_file: str, pipeline, config) -> None:
        self.pipeline = pipeline
        self.config = config

        self.video = cv2.VideoWriter(
            filename=mp4_file,
            fourcc=cv2.VideoWriter_fourcc(*"avc1"),
            fps=30,
            frameSize=(640, 480),
            isColor=True,
        )
        self.timestamp_text = open(timestamp_file, "w", encoding="utf8")

        self.q = queue.Queue(maxsize=1)

    def start(self):
        self.pipeline.start(self.config, self)

    def __call__(self, frames) -> None:
        frames = frames.as_frameset()
        time_ms = frames.get_timestamp()
        f_num = frames.get_frame_number()
        color_frame = frames.get_color_frame()
        color_image = np.asarray(color_frame.get_data())
        self.timestamp_text.write(f"{f_num},{time_ms:.3f}\n")

        self.video.write(color_image)
        if not self.q.full():
            self.q.put(color_image)

    def get(self) -> np.ndarray:
        if self.q.empty():
            return None
        return self.q.get()

    def stop(self):
        self.pipeline.stop()
        self.video.release()
        self.timestamp_text.close()


def get_realsense_recorder(mp4_file: str, timestamp_file: str) -> RealSenseRecorder:
    if not mp4_file.endswith(".mp4"):
        raise ValueError("拡張子を mp4 にしてください：" + mp4_file)
    if not timestamp_file.endswith(".csv"):
        raise ValueError("拡張子を csv にしてください：" + timestamp_file)

    ctx = rs.context()
    devices = ctx.query_devices()
    if devices.size() == 0:
        raise OSError("RGB Camera Not Found")

    pipeline = rs.pipeline()
    config = rs.config()

    config.enable_stream(
        stream_type=rs.stream.color,
        width=640,
        height=480,
        format=rs.format.bgr8,
        framerate=30,
    )

    pipeline_wrapper = rs.pipeline_wrapper(pipeline)
    pipeline_profile = config.resolve(pipeline_wrapper)
    device = pipeline_profile.get_device()

    rgbcam_exists = any(
        (s.get_info(rs.camera_info.name) == "RGB Camera") for s in device.sensors
    )
    if not rgbcam_exists:
        raise OSError("RGB Camera Not Found")

    sr = RealSenseRecorder(mp4_file, timestamp_file, pipeline, config)
    return sr


if __name__ == "__main__":
    rec = get_realsense_recorder("rgb.mp4", "timestamp.csv")
    rec.start()
    k = 1
    cv2.namedWindow("RealSense", cv2.WINDOW_AUTOSIZE)

    while k == 1:
        color_image = rec.get()
        k = cv2.getWindowProperty("RealSense", cv2.WND_PROP_VISIBLE)
        if color_image is None:
            continue
        cv2.imshow("RealSense", color_image)
        cv2.waitKey(1)

    rec.stop()
