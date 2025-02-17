package smartglasscontroller;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.epson.moverio.hardware.camera.CameraDevice;
import com.epson.moverio.hardware.camera.CameraManager;
import com.epson.moverio.hardware.camera.CaptureDataCallback;
import com.epson.moverio.hardware.camera.CaptureStateCallback;
import com.example.smartglasscontroller.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class VideoSenderFragment_old extends Fragment implements CaptureDataCallback {

    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private boolean isRecording = false;

    private EditText ipEditText;
    private EditText portEditText;
    private Button recordButton;

    private String ipAddress;
    private int port;

    int frameSize = 61440;
    int totalFrames = 10;
    int length = frameSize;
    byte[] chunk = new byte[8 + 4 + length];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the provided layout
        View view = inflater.inflate(R.layout.fragment_video_sender, container, false);

        // Initialize the UI components
        ipEditText = view.findViewById(R.id.editText_ip);
        portEditText = view.findViewById(R.id.editText_port);
        recordButton = view.findViewById(R.id.button_record_connect);

        // Initialize the camera manager
        Context context = getContext();
        mCameraManager = new CameraManager(context, null);

        // Set up the record button listener
        recordButton.setOnClickListener(v -> toggleRecording());

        return view;
    }
    private void toggleRecording() {
        if (isRecording) {
            // Stop recording
            stopRecording();
        } else {
            // Start recording
            startRecording();
        }
    }

    private void startRecording() {
        // Get IP address and port from user input
        ipAddress = ipEditText.getText().toString();
        try {
            port = Integer.parseInt(portEditText.getText().toString());
        } catch (NumberFormatException e) {
            port = 46004; // Default port
        }

        try {
            // Open the camera
            mCameraDevice = mCameraManager.open((CaptureStateCallback) null, this, null);


            // Start capturing video
            mCameraDevice.startCapture();

            // Update button text
            recordButton.setText("Stop Record");
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "IOException", Toast.LENGTH_SHORT).show();

        }
    }

    private void stopRecording() {
        if (mCameraDevice != null) {
            // Stop capturing video
            mCameraDevice.stopCapture();

            // Close the camera
            mCameraManager.close(mCameraDevice);
            mCameraDevice = null;
        }

        // Update button text
        recordButton.setText("Record Video");
        isRecording = false;
    }

    @Override
    public void onCaptureData(long timestamp, byte[] data) {
        // Send captured video data via UDP

        try {
            // Set up socket and address
            InetSocketAddress address = new InetSocketAddress(ipAddress, port);
            DatagramSocket socket = new DatagramSocket();

            byte[] timestampBytes = ByteBuffer.allocate(8).putLong(timestamp).array();

            for (int i = 0; i < totalFrames; i++) {
                int start = i * frameSize;
                // Prepare chunk
                System.arraycopy(timestampBytes, 0, chunk, 0, 8);
                System.arraycopy(ByteBuffer.allocate(4).putInt(i).array(), 0, chunk, 8, 4);
                System.arraycopy(data, start, chunk, 12, length);

                // Send packet
                DatagramPacket packet = new DatagramPacket(chunk, chunk.length, address);
                socket.send(packet);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Ensure camera is closed when fragment is destroyed
        if (mCameraDevice != null) {
            mCameraManager.close(mCameraDevice);
        }
    }
}




