package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.epson.moverio.hardware.audio.AudioManager;
import com.epson.moverio.hardware.camera.CameraDevice;
import com.epson.moverio.hardware.camera.CameraManager;
import com.epson.moverio.hardware.camera.CameraProperty;
import com.epson.moverio.hardware.camera.CaptureDataCallback2;
import com.epson.moverio.hardware.camera.CaptureStateCallback2;
import com.epson.moverio.hardware.display.DisplayManager;
import com.epson.moverio.hardware.sensor.SensorData;
import com.epson.moverio.hardware.sensor.SensorDataListener;
import com.epson.moverio.hardware.sensor.SensorManager;
import com.epson.moverio.system.DeviceManager;
import com.epson.moverio.util.PermissionGrantResultCallback;

import java.io.IOException;
import java.util.List;

public class MoverioHardwareInfoSampleFragment extends androidx.fragment.app.Fragment implements SensorDataListener, CaptureStateCallback2, PermissionGrantResultCallback {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;

    private DeviceManager mDeviceManager = null;
    private DisplayManager mDisplayManager = null;
    private SensorManager mSensorManager = null;
    private CameraManager mCameraManager = null;
    private CameraDevice mCameraDevice = null;
    private AudioManager mAudioManager = null;

    private ToggleButton mToggleButton_allOpenClose = null;
    private Button mButton_updateHardwareInfo = null;
    // Product info
    private TextView mTextView_headsetProductName = null;
    private TextView mTextView_headsetSystemVersion = null;
    private TextView mTextView_headsetSerialNumber = null;
    // Display control
    private TextView mTextView_brightnessSupport = null;
    private TextView mTextView_autoBrightnessModeSupport = null;
    private TextView mTextView_manualBrightnessModeSupport = null;
    private TextView mTextView_displayMode2DSupport = null;
    private TextView mTextView_displayMode3DSupport = null;
    private TextView mTextView_displayStateSupport = null;
    private TextView mTextView_displayAutoSleepSupport = null;
    private TextView mTextView_displayUserSleepSupport = null;
    private TextView mTextView_displayDistanceSupport = null;
    // Sensor control
    private TextView mTextView_accSupport = null;
    private TextView mTextView_magSupport = null;
    private TextView mTextView_gyroSupport = null;
    private TextView mTextView_lightSupport = null;
    private TextView mTextView_gravSupport = null;
    private TextView mTextView_laSupport = null;
    private TextView mTextView_rvSupport = null;
    private TextView mTextView_uncalibMagSupport = null;
    private TextView mTextView_grvSupport = null;
    private TextView mTextView_uncalibGyroSupport = null;
    private TextView mTextView_stationarySupport = null;
    private TextView mTextView_motionSupport = null;
    private TextView mTextView_uncalibAccSupport = null;
    private TextView mTextView_tapSupport = null;
    // Camera control
    private TextView mTextView_captureInfoSupport = null;
    private TextView mTextView_autoExposureModeSupport = null;
    private TextView mTextView_manualExposureModeSupport = null;
    private TextView mTextView_cameraBrightnessSupport = null;
    private TextView mTextView_whiteBalanceSupport = null;
    private TextView mTextView_powerLineFrequencySupport = null;
    private TextView mTextView_focusModeSupport = null;
    // Audio control
    private TextView mTextView_volumeSupport = null;
    private TextView mTextView_volumeLimitSupport = null;
    private TextView mTextView_deviceModeSupport = null;
    private TextView mTextView_gainStepSupport = null;
    // Device control
    private TextView mTextView_headsetStateSupport = null;
    private TextView mTextView_headsetSerialNumberSupport = null;
    private TextView mTextView_deviceTemperatureSupport = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();

        createAll();

        return inflater.inflate(R.layout.fragment_hardware_info, container, false);
    }

    private void createAll(){
        mDeviceManager = new DeviceManager(mContext, this);
        mDisplayManager = new DisplayManager(mContext, this);
        mSensorManager = new SensorManager(mContext, this);
        mCameraManager = new CameraManager(mContext, this);
        mAudioManager = new AudioManager(mContext, this);
    }
    private void openAll(){
        if(mDeviceManager.isHeadsetAttached()) {
            try {
                if(mDeviceManager.isDeviceManagerSupported()) mDeviceManager.open();
                else ;
                if(mDisplayManager.isDisplayManagerSupported()) mDisplayManager.open();
                else ;
                if(mSensorManager.isSensorManagerSupported()) mSensorManager.open(SensorManager.TYPE_ACCELEROMETER, this);
                else ;
                if(mCameraManager.isCameraManagerSupported()) mCameraDevice = mCameraManager.open(this, null, null);
                else ;
                if(mAudioManager.isAudioManagerSupported()) mAudioManager.open();
                else ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.w(TAG, "Headset dettached.");
        }
    }
    private void closeAll(){
        mDeviceManager.close();
        mDisplayManager.close();
        mSensorManager.close(this);
        mCameraManager.close(mCameraDevice);
        mAudioManager.close();
    }
    private void releaseAll(){
        mDeviceManager.release();
        mDeviceManager = null;
        mDisplayManager.release();
        mDisplayManager = null;
        mSensorManager.release();
        mSensorManager = null;
        mCameraManager.release();
        mCameraManager = null;
        mAudioManager.release();
        mAudioManager = null;
    }

    private final String HARDWARE_INFO_TAG_SUPPORTED = "✓";
    private final String HARDWARE_INFO_TAG_NOT_SUPPORTED = "×";

    private void setHardwareInfo(TextView textView, String info, boolean supported){
        if(supported) {
            textView.setText(HARDWARE_INFO_TAG_SUPPORTED + " " + info);
            textView.setTextColor(Color.GREEN);
        }
        else {
            textView.setText(HARDWARE_INFO_TAG_NOT_SUPPORTED + " " + info);
            textView.setTextColor(Color.RED);
        }
    }
    private void setHardwareInfo(TextView textView, String info, int color){
        textView.setText(info);
        textView.setTextColor(color);
    }
    private void setHardwareInfo(TextView textView, boolean supported){
        if(supported) {
            textView.setText("Supported");
            textView.setTextColor(Color.GREEN);
        }
        else {
            textView.setText("Not supported");
            textView.setTextColor(Color.RED);
        }
    }

    private void updateHardwareInfo(){
        updateProductInfo();
        updateDisplayInfo();
        updateSensorInfo();
        updateCameraInfo();
        updateAudioInfo();
        updateDeviceInfo();
    }
    private void updateProductInfo(){
        if(null != mDeviceManager&&mDeviceManager.isHeadsetAttached()) {
            setHardwareInfo(mTextView_headsetProductName, mDeviceManager.getHeadsetProductName(), Color.GREEN);
            setHardwareInfo(mTextView_headsetSystemVersion, mDeviceManager.getHeadsetSystemVersion(), Color.GREEN);
            if (mDeviceManager.isDeviceManagerSupported()) {
                setHardwareInfo(mTextView_headsetSerialNumber, mDeviceManager.getHeadsetSerialNumber(), mDeviceManager.isHeadsetSerialNumberAcquisitionSupported());
            }
            else {
                setHardwareInfo(mTextView_headsetSerialNumber, false);
            }
        }
        else {
            setHardwareInfo(mTextView_headsetProductName, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_headsetSystemVersion, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_headsetSerialNumber, "unknown", Color.GRAY);
        }
    }
    private void updateDisplayInfo() {
        if(null != mDeviceManager&&mDeviceManager.isHeadsetAttached()) {
            if (mDisplayManager.isDisplayManagerSupported()) {
                setHardwareInfo(mTextView_brightnessSupport, true);
                setHardwareInfo(mTextView_manualBrightnessModeSupport, "Manual", true);
                setHardwareInfo(mTextView_autoBrightnessModeSupport, "Automatic", mDisplayManager.isBrightnessModeControlSupported());
                setHardwareInfo(mTextView_displayMode2DSupport, "2D", true);
                setHardwareInfo(mTextView_displayMode3DSupport, "3D", true);
                setHardwareInfo(mTextView_manualBrightnessModeSupport, "Manual", true);
                setHardwareInfo(mTextView_displayStateSupport, mDisplayManager.isDisplayStateControlSupported());
                setHardwareInfo(mTextView_displayAutoSleepSupport, "Auto sleep", mDisplayManager.isDisplayAutoSleepEnabledControlSupported());
                setHardwareInfo(mTextView_displayUserSleepSupport, "User sleep", mDisplayManager.isDisplayUserSleepEnabledControlSupported());
                setHardwareInfo(mTextView_displayDistanceSupport, mDisplayManager.isScreenHorizontalShiftStepControlSupported());
            }
            else {
                setHardwareInfo(mTextView_brightnessSupport, false);
                setHardwareInfo(mTextView_manualBrightnessModeSupport,false);
                setHardwareInfo(mTextView_autoBrightnessModeSupport, false);
                setHardwareInfo(mTextView_displayMode2DSupport, false);
                setHardwareInfo(mTextView_displayMode3DSupport, false);
                setHardwareInfo(mTextView_manualBrightnessModeSupport, false);
                setHardwareInfo(mTextView_displayStateSupport, false);
                setHardwareInfo(mTextView_displayAutoSleepSupport, false);
                setHardwareInfo(mTextView_displayUserSleepSupport, false);
                setHardwareInfo(mTextView_displayDistanceSupport, false);
            }
        }
        else {
            setHardwareInfo(mTextView_brightnessSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_manualBrightnessModeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_autoBrightnessModeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_displayMode2DSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_displayMode3DSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_manualBrightnessModeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_displayStateSupport,"unknown", Color.GRAY);
            setHardwareInfo(mTextView_displayAutoSleepSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_displayUserSleepSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_displayDistanceSupport, "unknown", Color.GRAY);
        }
    }
    private void updateSensorInfo() {
        if(null != mDeviceManager&&mDeviceManager.isHeadsetAttached()) {
            if (mSensorManager.isSensorManagerSupported()) {
                List<Integer> senosrList = mSensorManager.getSupportedSensorList();
                setHardwareInfo(mTextView_accSupport, senosrList.contains(SensorManager.TYPE_ACCELEROMETER));
                setHardwareInfo(mTextView_magSupport, senosrList.contains(SensorManager.TYPE_MAGNETIC_FIELD));
                setHardwareInfo(mTextView_gyroSupport, senosrList.contains(SensorManager.TYPE_GYROSCOPE));
                setHardwareInfo(mTextView_lightSupport, senosrList.contains(SensorManager.TYPE_LIGHT));
                setHardwareInfo(mTextView_gravSupport, senosrList.contains(SensorManager.TYPE_GRAVITY));
                setHardwareInfo(mTextView_laSupport, senosrList.contains(SensorManager.TYPE_LINEAR_ACCELERATION));
                setHardwareInfo(mTextView_rvSupport, senosrList.contains(SensorManager.TYPE_ROTATION_VECTOR));
                setHardwareInfo(mTextView_uncalibMagSupport, senosrList.contains(SensorManager.TYPE_MAGNETIC_FIELD_UNCALIBRATED));
                setHardwareInfo(mTextView_grvSupport, senosrList.contains(SensorManager.TYPE_GAME_ROTATION_VECTOR));
                setHardwareInfo(mTextView_uncalibGyroSupport, senosrList.contains(SensorManager.TYPE_GYROSCOPE_UNCALIBRATED));
                setHardwareInfo(mTextView_stationarySupport, senosrList.contains(SensorManager.TYPE_STATIONARY_DETECT));
                setHardwareInfo(mTextView_motionSupport, senosrList.contains(SensorManager.TYPE_MOTION_DETECT));
                setHardwareInfo(mTextView_uncalibAccSupport, senosrList.contains(SensorManager.TYPE_ACCELEROMETER_UNCALIBRATED));
                setHardwareInfo(mTextView_tapSupport, senosrList.contains(SensorManager.TYPE_HEADSET_TAP_DETECT));
            }
            else {
                setHardwareInfo(mTextView_accSupport, false);
                setHardwareInfo(mTextView_magSupport, false);
                setHardwareInfo(mTextView_gyroSupport, false);
                setHardwareInfo(mTextView_lightSupport, false);
                setHardwareInfo(mTextView_gravSupport, false);
                setHardwareInfo(mTextView_laSupport, false);
                setHardwareInfo(mTextView_rvSupport, false);
                setHardwareInfo(mTextView_uncalibMagSupport, false);
                setHardwareInfo(mTextView_grvSupport, false);
                setHardwareInfo(mTextView_uncalibGyroSupport, false);
                setHardwareInfo(mTextView_stationarySupport, false);
                setHardwareInfo(mTextView_motionSupport, false);
                setHardwareInfo(mTextView_uncalibAccSupport, false);
                setHardwareInfo(mTextView_tapSupport, false);
            }
        }
        else {
            setHardwareInfo(mTextView_accSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_magSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_gyroSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_lightSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_gravSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_laSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_rvSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_uncalibMagSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_grvSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_uncalibGyroSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_stationarySupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_motionSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_uncalibAccSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_tapSupport, "unknown", Color.GRAY);
        }
    }
    private void updateCameraInfo() {
        if(null != mDeviceManager&&mDeviceManager.isHeadsetAttached()) {
            if (mCameraManager.isCameraManagerSupported()&&null != mCameraDevice) {
                CameraProperty property = mCameraDevice.getProperty();
                if(null != property) {
                    String captureInfo = "";
                    List<String> formatList = property.getSupportedCaptureDataFormat();
                    for(String format : formatList) {
                        captureInfo += "[" + format + "]" + System.lineSeparator();
                        property.setCaptureDataFormat(format);
                        mCameraDevice.setProperty(property);
                        property = mCameraDevice.getProperty();
                        List<int[]> captureInfoList = property.getSupportedCaptureInfo();
                        for (int[] info : captureInfoList) {
                            captureInfo += " " + info[0] + " x " + info[1] + " , " + info[2] + " [fps]" + System.lineSeparator();
                        }
                    }
                    setHardwareInfo(mTextView_captureInfoSupport, captureInfo, Color.GREEN);
                    setHardwareInfo(mTextView_manualExposureModeSupport, "Manual", true);
                    setHardwareInfo(mTextView_autoExposureModeSupport, "Automatic", true);
                    setHardwareInfo(mTextView_cameraBrightnessSupport, true);
                    String whitebalanceSupport = CameraProperty.WHITE_BALANCE_MODE_AUTO + System.lineSeparator()
                            + CameraProperty.WHITE_BALANCE_MODE_CLOUDY_DAYLIGHT + System.lineSeparator()
                            + CameraProperty.WHITE_BALANCE_MODE_DAYLIGHT + System.lineSeparator()
                            + CameraProperty.WHITE_BALANCE_MODE_FLUORESCENT + System.lineSeparator()
                            + CameraProperty.WHITE_BALANCE_MODE_INCANDESCENT + System.lineSeparator()
                            + CameraProperty.WHITE_BALANCE_MODE_TWILIGHT + System.lineSeparator();
                    setHardwareInfo(mTextView_whiteBalanceSupport, whitebalanceSupport, Color.GREEN);
                    String powerLineFreqSupport = "";
                    List<String> powerLineFreqList = property.getSupportedPowerLineFrequencyControlMode();
                    for (String mode : powerLineFreqList) {
                        powerLineFreqSupport += mode + System.lineSeparator();
                    }
                    setHardwareInfo(mTextView_powerLineFrequencySupport, powerLineFreqSupport, Color.GREEN);
                    if(!property.getSupportedFocusMode().isEmpty()) {
                        String focusModeSupport = "";
                        List<String> focusModeList = property.getSupportedFocusMode();
                        for (String mode : focusModeList) {
                            focusModeSupport += mode + System.lineSeparator();
                        }
                        setHardwareInfo(mTextView_focusModeSupport, focusModeSupport, Color.GREEN);
                    } else ;
                }
            }
            else {
                setHardwareInfo(mTextView_captureInfoSupport, false);
                setHardwareInfo(mTextView_manualExposureModeSupport, false);
                setHardwareInfo(mTextView_autoExposureModeSupport, false);
                setHardwareInfo(mTextView_cameraBrightnessSupport, false);
                setHardwareInfo(mTextView_whiteBalanceSupport, false);
                setHardwareInfo(mTextView_powerLineFrequencySupport, false);
                setHardwareInfo(mTextView_focusModeSupport, false);
            }
        }
        else {
            setHardwareInfo(mTextView_captureInfoSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_manualExposureModeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_autoExposureModeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_cameraBrightnessSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_whiteBalanceSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_powerLineFrequencySupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_focusModeSupport, "unknown", Color.GRAY);
        }
    }
    private void updateAudioInfo() {
        if(null != mDeviceManager&&mDeviceManager.isHeadsetAttached()) {
            if (mAudioManager.isAudioManagerSupported()) {
                setHardwareInfo(mTextView_volumeSupport, mAudioManager.isVolumeControlSupported());
                setHardwareInfo(mTextView_volumeLimitSupport, mAudioManager.isVolumeLimitModeControlSupported());
                if(null != mAudioManager.getSupportedDeviceMode()&&!mAudioManager.getSupportedDeviceMode().isEmpty()) {
                    String deviceModeSupport = "";
                    List<Integer> deviceModeList = mAudioManager.getSupportedDeviceMode();
                    for (Integer mode : deviceModeList) {
                        deviceModeSupport += (mode == AudioManager.DEVICE_MODE_BUILTIN_AUDIO ? "BUILTIN AUDIO" : (mode == AudioManager.DEVICE_MODE_AUDIO_JACK ? "AUDIO JACK" : "")) + System.lineSeparator();
                    }
                    setHardwareInfo(mTextView_deviceModeSupport, deviceModeSupport, Color.GREEN);
                } else ;
                setHardwareInfo(mTextView_gainStepSupport, mAudioManager.isGainStepControlSupported());
            }
            else {
                setHardwareInfo(mTextView_volumeSupport, false);
                setHardwareInfo(mTextView_volumeLimitSupport, false);
                setHardwareInfo(mTextView_deviceModeSupport, false);
                setHardwareInfo(mTextView_gainStepSupport, false);
            }
        }
        else {
            setHardwareInfo(mTextView_volumeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_volumeLimitSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_deviceModeSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_gainStepSupport, "unknown", Color.GRAY);
        }
    }
    private void updateDeviceInfo() {
        if(null != mDeviceManager&&mDeviceManager.isHeadsetAttached()) {
            if (mDeviceManager.isDeviceManagerSupported()) {
                setHardwareInfo(mTextView_headsetStateSupport, mDeviceManager.isHeadsetStateAcquisitionSupported());
                setHardwareInfo(mTextView_headsetSerialNumberSupport, mDeviceManager.isHeadsetSerialNumberAcquisitionSupported());
                setHardwareInfo(mTextView_deviceTemperatureSupport, mDeviceManager.isDeviceTemperatureAcquisitionSupported());
            }
            else {
                setHardwareInfo(mTextView_headsetStateSupport, false);
                setHardwareInfo(mTextView_headsetSerialNumberSupport, false);
                setHardwareInfo(mTextView_deviceTemperatureSupport, false);
            }
        }
        else {
            setHardwareInfo(mTextView_headsetStateSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_headsetSerialNumberSupport, "unknown", Color.GRAY);
            setHardwareInfo(mTextView_deviceTemperatureSupport, "unknown", Color.GRAY);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToggleButton_allOpenClose = (ToggleButton) view.findViewById(R.id.toggleButton_allOpenClose);
        mToggleButton_allOpenClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) openAll();
                else closeAll();
            }
        });

        mButton_updateHardwareInfo = (Button) view.findViewById(R.id.button_updateHardwareInfo);
        mButton_updateHardwareInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createAll();
//                openAll();
                updateHardwareInfo();
  //              closeAll();
//                releaseAll();
            }
        });

        // Product info
        mTextView_headsetProductName = (TextView) view.findViewById(R.id.textView_headsetProductName);
        mTextView_headsetSystemVersion = (TextView) view.findViewById(R.id.textView_headsetSystemVersion);
        mTextView_headsetSerialNumber = (TextView) view.findViewById(R.id.textView_headsetSerialNumber);

        // Display control
        mTextView_brightnessSupport = (TextView) view.findViewById(R.id.textView_brightnessSupport);
        mTextView_autoBrightnessModeSupport = (TextView) view.findViewById(R.id.textView_autoBrightnessModeSupport);
        mTextView_manualBrightnessModeSupport = (TextView) view.findViewById(R.id.textView_manualBrightnessModeSupport);
        mTextView_displayMode2DSupport = (TextView) view.findViewById(R.id.textView_displayMode2DSupport);
        mTextView_displayMode3DSupport = (TextView) view.findViewById(R.id.textView_displayMode3DSupport);
        mTextView_displayStateSupport = (TextView) view.findViewById(R.id.textView_displayStateSupport);
        mTextView_displayAutoSleepSupport = (TextView) view.findViewById(R.id.textView_displayAutoSleepSupport);
        mTextView_displayUserSleepSupport = (TextView) view.findViewById(R.id.textView_displayUserSleepSupport);
        mTextView_displayDistanceSupport = (TextView) view.findViewById(R.id.textView_displayDistanceSupport);

        // Sensor control
        mTextView_accSupport = (TextView) view.findViewById(R.id.textView_accSupport);
        mTextView_magSupport = (TextView) view.findViewById(R.id.textView_magSupport);
        mTextView_gyroSupport = (TextView) view.findViewById(R.id.textView_gyroSupport);
        mTextView_lightSupport = (TextView) view.findViewById(R.id.textView_lightSupport);
        mTextView_gravSupport = (TextView) view.findViewById(R.id.textView_gravSupport);
        mTextView_laSupport = (TextView) view.findViewById(R.id.textView_laSupport);
        mTextView_rvSupport = (TextView) view.findViewById(R.id.textView_rvSupport);
        mTextView_uncalibMagSupport = (TextView) view.findViewById(R.id.textView_uncalibMagSupport);
        mTextView_grvSupport = (TextView) view.findViewById(R.id.textView_grvSupport);
        mTextView_uncalibGyroSupport = (TextView) view.findViewById(R.id.textView_uncalibGyroSupport);
        mTextView_stationarySupport = (TextView) view.findViewById(R.id.textView_stationarySupport);
        mTextView_motionSupport = (TextView) view.findViewById(R.id.textView_motionSupport);
        mTextView_uncalibAccSupport = (TextView) view.findViewById(R.id.textView_uncalibAccSupport);
        mTextView_tapSupport = (TextView) view.findViewById(R.id.textView_tapSupport);

        // Camera control
        mTextView_captureInfoSupport = (TextView) view.findViewById(R.id.textView_captureInfoSupport);
        mTextView_autoExposureModeSupport = (TextView) view.findViewById(R.id.textView_autoExposureModeSupport);
        mTextView_manualExposureModeSupport = (TextView) view.findViewById(R.id.textView_manualExposureModeSupport);
        mTextView_cameraBrightnessSupport = (TextView) view.findViewById(R.id.textView_cameraBrightnessSupport);
        mTextView_whiteBalanceSupport = (TextView) view.findViewById(R.id.textView_whiteBalanceSupport);
        mTextView_powerLineFrequencySupport = (TextView) view.findViewById(R.id.textView_powerLineFrequencySupport);
        mTextView_focusModeSupport = (TextView) view.findViewById(R.id.textView_focusModeSupport);

        // Audio control
        mTextView_volumeSupport = (TextView) view.findViewById(R.id.textView_volumeSupport);
        mTextView_volumeLimitSupport = (TextView) view.findViewById(R.id.textView_volumeLimitSupport);
        mTextView_deviceModeSupport = (TextView) view.findViewById(R.id.textView_deviceModeSupport);
        mTextView_gainStepSupport = (TextView) view.findViewById(R.id.textView_gainStepSupport);

        // Device control
        mTextView_headsetStateSupport = (TextView) view.findViewById(R.id.textView_headsetStateSupport);
        mTextView_headsetSerialNumberSupport = (TextView) view.findViewById(R.id.textView_headsetSerialNumberSupport);
        mTextView_deviceTemperatureSupport = (TextView) view.findViewById(R.id.textView_deviceTemperatureSupport);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseAll();
    }

    @Override
    public void onSensorDataChanged(SensorData data) {

    }

    @Override
    public void onCameraOpened() {

    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onCaptureStarted() {

    }

    @Override
    public void onCaptureStopped() {

    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onPreviewStopped() {

    }

    @Override
    public void onRecordStarted() {

    }

    @Override
    public void onRecordStopped() {

    }

    @Override
    public void onPictureCompleted() {

    }

    @Override
    public void onPermissionGrantResult(String permission, int grantResult) {
        updateHardwareInfo();
    }
}
