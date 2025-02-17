from wear_mocap_ape.data_types import messaging
from wear_mocap_ape.record.est_output import EstOutputRecorder
from wear_mocap_ape.stream.listener.imu import ImuListener


class GalaxyRecorder:
    def __init__(self, listener: ImuListener, recoder: EstOutputRecorder) -> None:
        self.listener = listener
        self.recorder = recoder

    def start(self):
        messages = self.listener.listen_in_thread()
        self.recorder.record_in_thread(messages)

    def stop(self):
        self.listener.terminate()
        self.recorder.terminate()


def get_galaxy_recorder(host_ip,data_file: str, port: int) -> GalaxyRecorder:
    if not data_file.endswith(".csv"):
        raise ValueError("csv にしてください:" + data_file)
    listener = ImuListener(ip=host_ip, msg_size=messaging.watch_only_imu_msg_len, port=port)
    recoder = EstOutputRecorder(file=data_file)
    return GalaxyRecorder(listener, recoder)

if __name__ == "__main__":
    server = get_galaxy_recorder("192.168.8.104", "galaxy_right.csv", port=46066)
    try:
        server.start()
        while True:
            command = input("All started. Type 'q' to stop all recorders:\n")
            if command.strip().lower() == 'q':
                break
    finally:
        print("\nServer shut down.")
        server.stop()
