import os
import pandas as pd
import matplotlib.pyplot as plt

def plot_csv_files(folder_path):
    print(f"Checking folder: {folder_path}")
    if not os.path.exists(folder_path):
        print("Error: Folder does not exist.")
        return
    
    csv_files = [f for f in os.listdir(folder_path) if f.endswith(".csv")]
    if not csv_files:
        print("No CSV files found in the folder.")
        return
    
    for file in csv_files:
        file_path = os.path.join(folder_path, file)
        print(f"Processing file: {file_path}")
        try:
            df = pd.read_csv(file_path)
            if df.shape[1] < 3:
                print(f"Skipping {file}: Less than 3 columns.")
                continue
            
            timestamps = df.iloc[:, 0] + df.iloc[:, 1]
            
            time_diffs = timestamps.diff()
            
            plt.figure(figsize=(10, 6))
            plt.scatter(timestamps[1:], time_diffs[1:], alpha=0.5, s=5)
            plt.xlabel("Timestamp (Seconds)")
            plt.ylabel("Time Difference (Seconds)")
            plt.title(f"Timestamp Interval Plot of {file}")
            save_path = os.path.join(folder_path, f"{os.path.splitext(file)[0]}_interval.png")
            plt.savefig(save_path)
            plt.close()
            print(f"Saved plot to {save_path}")
        except Exception as e:
            print(f"Error reading {file}: {e}")


folder_path = "data\glass"  
plot_csv_files(folder_path)