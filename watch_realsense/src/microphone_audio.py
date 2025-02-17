import sys
import typing

import numpy as np
import sounddevice as sd
import soundfile as sf


class AudioRecorder:
    def __init__(
        self,
        sound_file: str,
        time_file: str,
        input_device,
        channels: int = 1,
        block_duration_ms: int = 50,
    ):
        sampling_rate = round(input_device["default_samplerate"])

        self.sound_file = sf.SoundFile(
            sound_file, mode="w", samplerate=sampling_rate, channels=channels
        )
        self.time_file = open(time_file, mode="w")
        self.n_frames = 0

        self.stream = sd.InputStream(
            device=input_device["index"],
            channels=channels,
            callback=self,
            blocksize=int(sampling_rate * block_duration_ms / 1000),
            samplerate=sampling_rate,
        )

    def start(self):
        self.stream.start()

    def __call__(self, indata: np.ndarray, frames, time, status):
        if status:
            print(status, file=sys.stderr)

        time_ms = float(time.inputBufferAdcTime) * 1e3
        self.n_frames += frames
        
        # コピーする必要ある？
        sound = indata.copy()
        self.sound_file.write(sound)
        self.time_file.write(f"{self.n_frames:d},{time_ms:.3f}\n")

    def stop(self):
        self.stream.abort()
        self.sound_file.close()
        self.time_file.close()
        self.stream.close()


def check_usb_input(item: dict[str, typing.Any]) -> bool:
    return ("usb" in item["name"].lower()) and (item["max_input_channels"] > 0)


def get_audio_recorder(sound_file: str, timestamp_file: str) -> AudioRecorder:
    dl = sd.query_devices(device=None, kind=None)
    if not any(map(check_usb_input, dl)):
        raise OSError("USB audio input Not Found")
    input_device = min(
        filter(check_usb_input, dl), key=lambda x: x["default_high_input_latency"]
    )
    recoder = AudioRecorder(
        sound_file=sound_file,
        time_file=timestamp_file,
        input_device=input_device,
    )
    return recoder
