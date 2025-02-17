import socket
import pyaudio
from pydub import AudioSegment
import io
import os
import threading

class AudioUDPRecorder:
    def __init__(self, udp_ip: str="0.0.0.0", udp_port=8888, data_folder: str = "data/glass/", chunk_size=3584, rate=44100, channels=1, format=pyaudio.paInt16):
        self.udp_ip = udp_ip
        self.udp_port = udp_port
        self.file_folder = data_folder
        self.chunk_size = chunk_size
        self.rate = rate
        self.channels = channels
        self.format = format
        self.audio_data = b""
        self.is_recording = False
        self.thread = None
        
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind((self.udp_ip, self.udp_port))

        self.ffmpeg_path = "C:\\Users\\huang\\Downloads\\ffmpeg-master-latest-win64-gpl\\bin"
        AudioSegment.converter = os.path.join(self.ffmpeg_path, "ffmpeg.exe")
        AudioSegment.ffprobe = os.path.join(self.ffmpeg_path, "ffprobe.exe")

        if not os.path.exists(self.file_folder):
            os.makedirs(self.file_folder)

    def save_audio_as_mp3(self, audio_data):
        audio_segment = AudioSegment(
            data=audio_data,
            sample_width=2,  # 16-bit PCM
            frame_rate=self.rate,
            channels=self.channels
        )
        
        mp3_audio = io.BytesIO()
        audio_segment.export(mp3_audio, format="mp3")

        file_path = os.path.join(self.file_folder, "received_audio.mp3")
        with open(file_path, "wb") as f:
            f.write(mp3_audio.getvalue())
        print(f"Audio saved as MP3 to {file_path}.")

    def start(self):
        if self.is_recording:
            print("Recording is already in progress.")
            return

        self.is_recording = True
        print(f"Started listening on {self.udp_ip}:{self.udp_port} for audio data")
        self.thread = threading.Thread(target=self._record)
        self.thread.start()

    def _record(self):
        self.audio_data = b""
        while self.is_recording:
            data, addr = self.sock.recvfrom(self.chunk_size)
            if data:
                self.audio_data += data
                
    def stop(self):
        if not self.is_recording:
            print("Recording is not in progress.")
            return

        self.is_recording = False
        if self.thread and self.thread.is_alive():
            self.thread.join()
        print("Recording stopped.")
        self.save_audio_as_mp3(self.audio_data)
        self.audio_data = b""

if __name__ == "__main__":
    recorder = AudioUDPRecorder(udp_ip="192.168.8.104", udp_port=46003, file_folder="./audio_files")
    
    try:
        recorder.start()
    except KeyboardInterrupt:
        recorder.stop()
