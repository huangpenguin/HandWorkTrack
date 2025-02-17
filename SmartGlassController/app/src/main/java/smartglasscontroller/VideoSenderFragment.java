package smartglasscontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Environment;

import com.epson.moverio.hardware.camera.CameraDevice;
import com.epson.moverio.hardware.camera.CameraManager;
import com.epson.moverio.hardware.camera.CaptureDataCallback;
import com.epson.moverio.hardware.camera.CaptureStateCallback;
import com.example.smartglasscontroller.R;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Fragment class for handling video sending via smart glasses.
 * Responsible for camera initialization, TCP socket communication, and video capture.
 */
public class VideoSenderFragment extends androidx.fragment.app.Fragment implements CaptureDataCallback {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext = null;
    private boolean isRecording = false;
    private EditText ipEditText;
    private EditText portEditText;
    private Button connectButton;

    private String ipAddress;
    private int port;
    private Socket socket;
    private BufferedReader inputReader;
    private OutputStream outputWriter;
    private Thread listenThread;

    private CameraManager mCameraManager = null;
    private CameraDevice mCameraDevice;
    private boolean isConnecting = false;

    private static final String PREFS_NAME = "SmartGlassPrefs";
    private static final String PREF_KEY_IP = "ipAddress";
    private static final String PREF_KEY_PORT = "port";

    /**
     * Inflates the layout for this fragment and initializes UI components.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_video_sender, container, false);

        ipEditText = view.findViewById(R.id.editText_ip);
        portEditText = view.findViewById(R.id.editText_port);
        connectButton = view.findViewById(R.id.button_record_connect);
        connectButton.setOnClickListener(v -> toggleConnection());

        loadPreferences();
        initializeCamera();

        return view;
    }

    /**
     * Saves IP and port preferences.
     */
    private void savePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_IP, ipEditText.getText().toString());
        editor.putInt(PREF_KEY_PORT, Integer.parseInt(portEditText.getText().toString()));
        editor.apply();
    }

    /**
     * Loads previously saved IP and port preferences.
     */
    private void loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedIp = sharedPreferences.getString(PREF_KEY_IP, "192.168.8.104");
        int savedPort = sharedPreferences.getInt(PREF_KEY_PORT, 46004);
        ipEditText.setText(savedIp);
        portEditText.setText(String.valueOf(savedPort));
    }

    /**
     * Initializes the camera device.
     */
    private void initializeCamera() {
        try {
            mContext = getContext();
            mCameraManager = new CameraManager(mContext);
            mCameraDevice = mCameraManager.open((CaptureStateCallback) null, this, null);
            if (mCameraDevice == null) {
                throw new IllegalStateException("Failed to open CameraDevice.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error initializing Camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
            writeLogToFile("Error initializing Camera: " + Log.getStackTraceString(e));
        }
    }

    /**
     * Toggles the connection between the smart glass and the server.
     */
    private void toggleConnection() {
        if (socket != null && socket.isConnected()) {
            stopRecording();
            disconnectSocket();
        } else {
            savePreferences();
            connectSocket();
        }
    }

    /**
     * Establishes a TCP connection to the server.
     */
    private void connectSocket() {
        if (isConnecting) return;
        isConnecting = true;

        ipAddress = ipEditText.getText().toString();
        try {
            port = Integer.parseInt(portEditText.getText().toString());
        } catch (NumberFormatException e) {
            port = 46004;
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            connectButton.setEnabled(false);
            Toast.makeText(getContext(), "Connecting...", Toast.LENGTH_SHORT).show();
            writeLogToFile("Connecting...");
        });

        new Thread(() -> {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ipAddress, port), 900000);
                inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputWriter = socket.getOutputStream();

                new Handler(Looper.getMainLooper()).post(() -> {
                    connectButton.setText("Disconnect");
                    connectButton.setEnabled(true);
                    Toast.makeText(getContext(), "Connected to server", Toast.LENGTH_SHORT).show();
                    writeLogToFile("Connected to server");
                });//Background thread

                listenForServerCommands();
            } catch (IOException e) {
                e.printStackTrace();
                postConnectionError("Connection failed: " + e.getMessage());
            } finally {
                isConnecting = false;
            }
        }).start();
    }

    /**
     * Disconnects the TCP socket.
     */
    private void disconnectSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (listenThread != null && listenThread.isAlive()) {
                listenThread.interrupt();
                listenThread = null;
            }
            postDisconnected();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the UI to reflect the disconnected state.
     */
    private void postDisconnected() {
        new Handler(Looper.getMainLooper()).post(() -> {
            connectButton.setText("Connect");
            connectButton.setEnabled(true);
            Toast.makeText(getContext(), "Please restart to run again", Toast.LENGTH_SHORT).show();
            writeLogToFile("Disconnected.Please restart to run again");
        });
    }

    /**
     * Handles connection errors and updates the UI.
     */
    private void postConnectionError(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            connectButton.setEnabled(true);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            writeLogToFile(message);
        });
    }

    /**
     * Listens for commands from the server to start or stop recording.
     */
    private void listenForServerCommands() {
        listenThread = new Thread(() -> {
            try {
                String line;
                while (socket != null && socket.isConnected()) {
                    line = inputReader.readLine();
                    if (line == null) break;
                    if (line.trim().equalsIgnoreCase("start")) {
                        startRecording();
                    } else if (line.trim().equalsIgnoreCase("stop")) {
                        stopRecording();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                reconnectSocket();
            }
        });
        listenThread.start();
    }

    /**
     * Attempts to reconnect the socket in case of failure.
     */
    private void reconnectSocket() {
        disconnectSocket();
        connectSocket();
    }

    /**
     * Handles incoming capture data from the camera.
     *
     * @param timestamp The timestamp of the data.
     * @param data      The captured data.
     */
    public void onCaptureData(long timestamp, byte[] data) {
        if (socket == null || !socket.isConnected()) {
            Log.e(TAG, "TCP socket is not connected.");
            return;
        }
        try {
            byte[] timestampBytes = ByteBuffer.allocate(8).putLong(timestamp).array();
            byte[] header = new byte[8];
            System.arraycopy(timestampBytes, 0, header, 0, 8);

            synchronized (outputWriter) {
                outputWriter.write(header);
                outputWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            reconnectSocket();
        }
    }

    /**
     * Starts recording video from the camera.
     */
    private void startRecording() {
        if (isRecording) return;
        isRecording = true;

        new Thread(() -> {
            try {
                if (mCameraDevice == null) {
                    Log.e(TAG, "CameraDevice is null after initialization!");
                    throw new IllegalStateException("CameraDevice is not initialized");
                }
                new Handler(Looper.getMainLooper()).post(() ->
                        {
                            Toast.makeText(getContext(), "CameraDevice initialized", Toast.LENGTH_SHORT).show();
                            writeLogToFile("CameraDevice initialized");
                        }
                );


                String fileName = "movie_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".mp4";
                File outputFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName);
                mCameraDevice.startCapture();
                mCameraDevice.startRecord(outputFile);
//                File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//                if (!outputDir.exists()) {
//                    outputDir.mkdirs();
//                }
//                File outputFile = new File(outputDir, fileName);
//
//                Log.d(TAG, "Saving video to: " + outputFile.getAbsolutePath());
//                writeLogToFile("Saving video to: " + outputFile.getAbsolutePath());
//                mCameraDevice.startCapture();
//                try {
//                    if (!outputFile.exists() && outputFile.createNewFile()) {
//
//                        mCameraDevice.startRecord(outputFile);
//                    } else {
//                        Log.e(TAG, "Failed to create output file.");
//                        writeLogToFile("Failed to create output file.");
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Error creating output file: " + e.getMessage());
//                    writeLogToFile("Error creating output file: " + e.getMessage());
//                    writeLogToFile("File exists: " + outputFile.exists());
//                    writeLogToFile("File created: " + outputFile.createNewFile());
//
//                }
                new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
                            writeLogToFile("Recording started");
                        }
                );

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(getContext(), "Error during recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            writeLogToFile("Error during recording: " + Log.getStackTraceString(e));
                        }
                );
            }
        }).start();
    }

    /**
     * Stops recording video from the camera.
     */
    private void stopRecording() {
        if (!isRecording) return;
        isRecording = false;

        new Thread(() -> {
            try {
                if (mCameraDevice != null) {
                    mCameraDevice.stopRecord();
                    mCameraDevice.stopCapture();
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    connectButton.setEnabled(true);
                    Toast.makeText(getContext(), "Recording stopped and video saved.", Toast.LENGTH_LONG).show();
                    writeLogToFile("Recording stopped and video saved.");
                });

                disconnectSocket();

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(getContext(), "Error stopping recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            writeLogToFile("Error stopping recording:  " + Log.getStackTraceString(e));
                        }
                );
            }
        }).start();
    }

    /**
     * Cleans up resources when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconnectSocket();
        if (mCameraManager != null) {
            try {
                mCameraManager.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing CameraManager", e);
                writeLogToFile("Error releasing CameraManager: " + Log.getStackTraceString(e));
            }
        }
    }

    private void writeLogToFile(String message) {
        try {
            File logFile = new File(getContext().getExternalFilesDir(null), "app_log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(new Date().toString() + ": " + message + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


