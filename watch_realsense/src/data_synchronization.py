"""
時間を合わせて CSV ファイルを一つにまとめる。
main.py を実行してデータを取得した後に実行する。
"""

import pandas as pd

# 出力 CSV
output_csv = "data_order.csv"

# 入力 CSV
first_timestamp_csv = "first_timestamp.csv"
audio_csv = "audio.csv"
galaxy_left_csv = "galaxy_left.csv"
galaxy_right_csv = "galaxy_right.csv"
realsense_csv = "realsense.csv"

start_sec = pd.read_csv(first_timestamp_csv, index_col=0, header=None)
start_sec = start_sec[1]
start_sec -= start_sec.min()

audio_time = pd.read_csv(audio_csv, index_col=False, header=None)
audio_time[1] -= audio_time[1][0]
audio_time[1] *= 1e-3
audio_time[1] += start_sec["audio"]
audio_time.rename(columns={0: "index", 1: "time[s]"}, inplace=True)
audio_time["device"] = "audio"

df = pd.read_csv(galaxy_left_csv, index_col=False, usecols=["hour", "minute", "second", "nanosecond"])
time: pd.Series = df["hour"] * 3600 + df["minute"] * 60 + df["second"] + 1e-9 * df["nanosecond"]
time += start_sec["galaxy_left"] - time[0]
left_time = time.to_frame("time[s]").reset_index()
left_time["device"] = "galaxy_left"

df = pd.read_csv(galaxy_right_csv, index_col=False, usecols=["hour", "minute", "second", "nanosecond"])
time: pd.Series = df["hour"] * 3600 + df["minute"] * 60 + df["second"] + 1e-9 * df["nanosecond"]
time += start_sec["galaxy_right"] - time[0]
right_time = time.to_frame("time[s]").reset_index()
right_time["device"] = "galaxy_right"

realsense_time = pd.read_csv(realsense_csv, index_col=False, header=None)
realsense_time[1] -= realsense_time[1][0]
realsense_time[1] *= 1e-3
realsense_time[1] += start_sec["realsense"]
realsense_time.rename(columns={0: "index", 1: "time[s]"}, inplace=True)
realsense_time["device"] = "realsense"

result = pd.concat([audio_time, right_time, left_time, realsense_time], axis=0)
result.sort_values("time[s]", inplace=True, kind ="stable")

result.to_csv(output_csv, index=False)
