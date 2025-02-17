import socket
import struct
from pathlib import Path
import time

class VideoUDPRecorder:
    def __init__(self, host_ip="0.0.0.0", port=8888, output_folder="./data/glass/"):
        self.host = host_ip
        self.port = port
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((self.host, self.port))
        self.server_socket.listen(1)
        self.output_folder = Path(output_folder)
        self.output_folder.mkdir(parents=True, exist_ok=True)
        self.running = True
        self.tcp_start_time = None
        self.receive_start_time = None
        self.server_socket.settimeout(30.0)

    def start(self):
        print(f"Server listening on {self.host}:{self.port}")
        while self.running:
            try:
                client_socket, client_address = self.server_socket.accept()
                client_socket.settimeout(5.0)
                print(f"Client connected: {client_address}")
                self.handle_client(client_socket, client_address, self.output_folder)

            except Exception as e:
                print(f"Error during server operation: {e}")

    def handle_client(self, client_socket, client_address, output_folder):
        try:
            self.send_command(client_socket, "start")
            print(f"Sent 'start' to {client_address}")
            self.tcp_start_time = time.time()
            #print(f"Start command sent at: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(self.tcp_start_time))}")

            with open(output_folder / "video.csv", "w") as file:
                while self.running:
                    data = client_socket.recv(8)
                    if data :
                        if self.receive_start_time is None:
                            self.receive_start_time = time.time()
                        timestamp = struct.unpack("!Q", data)[0]
                        #print(f"Received Timestamp: {timestamp} from {client_address}")
                        file.write(f" {timestamp}\n")

                    else:
                        print(f"Client {client_address} disconnected.")
                        break

        except (ConnectionResetError, ConnectionAbortedError, BrokenPipeError) as e:
            print(f"Connection lost with {client_address}: {e}")
        finally:
            self.close_client_connection(client_socket)
            
    def get_tcp_start_time(self):

        return self.tcp_start_time
    
    def get_receive_start_time(self):

        return self.receive_start_time

    def send_command(self, client_socket, command):
        try:
            client_socket.sendall((command + "\n").encode())
        except Exception as e:
            print(f"Error sending command to {client_socket}: {e}")

    def close_client_connection(self, client_socket):
        if client_socket:
            try:
                self.send_command(client_socket, "stop")
                client_socket.close()
                print("Connection with client closed.")
            except Exception as e:
                print(f"Error closing client connection: {e}")

    def stop(self):
        self.running = False
        if self.server_socket:
            try:
                self.server_socket.close()
                print("Server socket closed.")
            except Exception as e:
                print(f"Error closing server socket: {e}")

if __name__ == "__main__":
    server = VideoUDPRecorder(host_ip="192.168.8.104", port=46004, output_folder="./data/glass/")
    try:
        server.start()
        while True:
            command = input("All started. Type 'q' to stop all recorders:\n")
            if command.strip().lower() == 'q':
                break
    finally:
        print("\nServer will shut down...")
        server.stop()
