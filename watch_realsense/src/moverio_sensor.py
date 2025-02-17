import socket
import csv
import json
import threading
from pathlib import Path


class MoverioRecorder:
    def __init__(self, host_ip: str = "0.0.0.0", port: int = 8888, output_folder: str = "./data/glass/"):
        self.udp_ip = host_ip
        self.udp_port = port
        self.data_folder = Path(output_folder)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind((self.udp_ip, self.udp_port))
        self.running = False
        self.thread = None

        self.sensor_config = {
            "accelerometer": {
                "filename": "accelerometer_data.csv",
                "headers": ["timestamp", "nanos", "x", "y", "z"]
            },
            "gyroscope": {
                "filename": "gyroscope_data.csv",
                "headers": ["timestamp", "nanos", "x", "y", "z"]
            },
            "magnetic_field": {
                "filename": "magnetic_field_data.csv",
                "headers": ["timestamp", "nanos", "x", "y", "z", "accuracy"]
            },
            "linear_acceleration": {
                "filename": "linear_acceleration_data.csv",
                "headers": ["timestamp", "nanos", "x", "y", "z"]
            },
            "gravity": {
                "filename": "gravity_data.csv",
                "headers": ["timestamp", "nanos", "x", "y", "z"]
            },
            "rotation_vector": {
                "filename": "rotation_vector_data.csv",
                "headers": ["timestamp", "nanos", "x", "y", "z", "w"]
            }
        }

        self.file_handlers = {}
        self._initialize_csv_files()

    def _initialize_csv_files(self):
        self.data_folder.mkdir(parents=True, exist_ok=True)

        for sensor, config in self.sensor_config.items():
            filename = self.data_folder / config["filename"]
            #file_exists = filename.is_file()

            file = filename.open(mode='w', newline='')
            writer = csv.DictWriter(file, fieldnames=config["headers"])

            # if not file_exists:
            writer.writeheader()

            self.file_handlers[sensor] = {
                "file": file,
                "writer": writer
            }

    def write_to_csv(self, sensor_data):
        """
        Writes sensor data to the appropriate CSV file based on sensor type.

        Args:
            sensor_data (dict): Dictionary containing sensor data, including the key 'sensor_type'.
        """
        sensor_type = sensor_data.get("sensor_type")
        if sensor_type not in self.sensor_config:
            print(f"Unknown sensor type: {sensor_type}")
            return

        writer_info = self.file_handlers.get(sensor_type)
        if not writer_info:
            print(f"No writer found for sensor type: {sensor_type}")
            return

        writer = writer_info["writer"]

        sensor_data.pop("sensor_type", None)

        try:
            writer.writerow(sensor_data)
        except Exception as e:
            print(f"Error writing to CSV: {e}")
            print(f"Failed data: {json.dumps(sensor_data, indent=4)}")

    def _listen_and_record(self):
        """
        Internal method to listen for incoming UDP messages and write the data to CSV files.
        """
        try:
            while self.running:
                data, _ = self.sock.recvfrom(1024)
                if data == b"__EXIT__":
                    print("Received stop signal, exiting listener thread.")
                    break
                try:
                    sensor_data = json.loads(data.decode("utf-8"))
                    self.write_to_csv(sensor_data)
                except json.JSONDecodeError as e:
                    print(f"Error decoding JSON: {e}")
        except Exception as e:
            if self.running:
                print(f"Error during listening: {e}")

    def start(self):
        """
        Starts the UDP listener in a separate thread.
        """
        if not self.running:
            self.running = True
            self.thread = threading.Thread(target=self._listen_and_record)
            self.thread.start()
            print(f"Started listening on {self.udp_ip}:{self.udp_port} for sensor data")

    def stop(self):
        """
        Stops the UDP listener, waits for the thread to finish, and closes all CSV files.
        """
        if self.running:
            self.running = False
            try:
                self.sock.sendto(b"__EXIT__", (self.udp_ip, self.udp_port))
            except Exception as e:
                print(f"Failed to send stop signal: {e}")

            if self.thread is not None:
                self.thread.join()
                self.thread = None

            self._close_csv_files()

            print(f"Stopped listening on {self.udp_ip}:{self.udp_port} for sensor data")

    def _close_csv_files(self):
        for sensor, handler in self.file_handlers.items():
            try:
                handler["file"].close()
            except Exception as e:
                print(f"Error closing file for {sensor}: {e}")
        self.file_handlers.clear()

if __name__ == "__main__":
    recorder = MoverioRecorder(host_ip="192.168.179.15", port=46002, output_folder="./data/glass/")
    try:
        recorder.start()
        while True:
            command = input("All started. Type 'q' to stop all recorders:\n")
            if command.strip().lower() == 'q':
                break
    finally:
        print("\nServer will shut down...")
        recorder.stop()
