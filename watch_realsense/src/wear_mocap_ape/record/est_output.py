import logging
import queue
import datetime
import threading
from pathlib import Path

class EstOutputRecorder:
    def __init__(self, file: Path, tag: str = "REC EST OUTPUT"):
        self.__file = Path(file)
        self.__tag = tag
        self._active = False

        if tag == "REC PHONE IMU":
            header = [
                "absolute_time",  
                "ph_dt", "ph_h", "ph_m", "ph_s", "ph_ns",  
                "ph_rotvec_w", "ph_rotvec_x", "ph_rotvec_y", "ph_rotvec_z", "ph_rotvec_conf",  
                "ph_gyro_x", "ph_gyro_y", "ph_gyro_z",  
                "ph_lvel_x", "ph_lvel_y", "ph_lvel_z",  
                "ph_lacc_x", "ph_lacc_y", "ph_lacc_z",  
                "ph_pres",  
                "ph_grav_x", "ph_grav_y", "ph_grav_z" 
            ]
        else:
            header = [
                "absolute_time",               # time
                "delta_time",                  # dT (メッセージ間の時間差)
                "hour",                        # 時間 (時)
                "minute",                      # 時間 (分)
                "second",                      # 時間 (秒)
                "nanosecond",                  # ナノ秒
                "rot_quat_w",                  # 回転ベクトル クォータニオン w
                "rot_quat_x",                  # 回転ベクトル クォータニオン x
                "rot_quat_y",                  # 回転ベクトル クォータニオン y
                "rot_quat_z",                  # 回転ベクトル クォータニオン z
                "rot_quat_confidence",         # 回転ベクトル 信頼度
                "gyro_x",                      # ジャイロスコープ x 軸
                "gyro_y",                      # ジャイロスコープ y 軸
                "gyro_z",                      # ジャイロスコープ z 軸
                "lin_acc_x",                   # 累積加速度 x 軸
                "lin_acc_y",                   # 累積加速度 y 軸
                "lin_acc_z",                   # 累積加速度 z 軸
                "mean_acc_x",                  # 平均加速度 x 軸
                "mean_acc_y",                  # 平均加速度 y 軸
                "mean_acc_z",                  # 平均加速度 z 軸
                "pressure",                    # 大気圧 (hPa)
                "gravity_x",                   # 重力方向 x 軸
                "gravity_y",                   # 重力方向 y 軸
                "gravity_z",                   # 重力方向 z 軸
                "calib_quat_w",                # 校正されたクォータニオン w
                "calib_quat_x",                # 校正されたクォータニオン x
                "calib_quat_y",                # 校正されたクォータニオン y
                "calib_quat_z",                # 校正されたクォータニオン z
                "calib_pressure"               # 校正された圧力値 (hPa)
            ]

        if not self.__file.parent.exists():
            raise UserWarning(f"Directory does not exist {file.parent}")

        with open(self.__file, 'w') as fd:
            fd.write(",".join(header) + "\n")

        logging.info(f"[{self.__tag}] Writing to file {self.__file}")

    def terminate(self):
        self._active = False

    def record_in_thread(self, msg_q: queue.Queue):
        t = threading.Thread(target=self.write_queue_to_csv, args=(msg_q,))
        t.start()

    def write_queue_to_csv(self, msg_q: queue.Queue):
        self._active = True
        while self._active:
            try:
                msg = msg_q.get(timeout=2)
            except queue.Empty:
                logging.info(f"[{self.__tag}] no data")
                continue

            msg = msg.tolist()
            with open(self.__file, 'a') as fd:
                msg.insert(0, datetime.datetime.now())
                msg = [str(x) for x in msg]
                fd.write(",".join(msg) + "\n")
