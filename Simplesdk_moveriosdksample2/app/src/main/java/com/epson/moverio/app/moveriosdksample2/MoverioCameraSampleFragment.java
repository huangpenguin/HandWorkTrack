package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.epson.moverio.hardware.camera.CameraDevice;
import com.epson.moverio.hardware.camera.CameraManager;
import com.epson.moverio.hardware.camera.CameraProperty;
import com.epson.moverio.hardware.camera.CaptureDataCallback;
import com.epson.moverio.hardware.camera.CaptureDataCallback2;
import com.epson.moverio.hardware.camera.CaptureStateCallback2;
import com.epson.moverio.util.PermissionGrantResultCallback;
import com.epson.moverio.util.PermissionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MoverioCameraSampleFragment extends androidx.fragment.app.Fragment implements CaptureStateCallback2, CaptureDataCallback, CaptureDataCallback2, PermissionGrantResultCallback {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;

    private CameraManager mCameraManager = null;
    private CameraDevice mCameraDevice = null;
    private final CaptureStateCallback2 mCaptureStateCallback2 = this;
    private final CaptureDataCallback mCaptureDataCallback = this;
    private final CaptureDataCallback2 mCaptureDataCallback2 = this;

    private ToggleButton mToggleButton_cameraOpenClose = null;
    private ToggleButton mToggleButton_captureStartStop = null;
    private ToggleButton mToggleButton_previewStartStop = null;
    private CheckBox mCheckBox_stateCallback = null;
    private CheckBox mCheckBox_dataCallback = null;
    private CheckBox mCheckBox_preview = null;
    private Button mButton_takePicture = null;
    private ToggleButton mToggleButton_recording = null;
    private Switch mSwitch_autoExposure = null;
    private TextView mTextView_exposure = null;
    private SeekBar mSeekBar_exposure = null;
    private TextView mTextView_brightness = null;
    private SeekBar mSeekBar_brightness = null;
    private TextView mTextView_whitebalance = null;
    private RadioGroup mRadioGroup_whitebalance = null;
    private TextView mTextView_powerLineFrequency = null;
    private RadioGroup mRadioGroup_powerLineFrequency = null;
    private TextView mTextView_captureFormat = null;
    private RadioGroup mRadioGroup_captureFormat = null;
    private TextView mTextView_indicatorMode = null;
    private RadioGroup mRadioGroup_indicatorMode = null;
    private SurfaceView mSurfaceView_preview = null;

    private TextView mTextView_captureState = null;
    private Spinner mSpinner_captureInfo = null;

    private Switch mSwitch_autoFocus = null;
    private TextView mTextView_focus = null;
    private SeekBar mSeekBar_focus = null;
    private TextView mTextView_cameraGain = null;
    private SeekBar mSeekBar_cameraGain = null;

    private TextView mTextView_framerate = null;
    private CalcurationRate mCalcurationRate_framerate = null;

    private TextView mTextView_test = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        mCameraManager = new CameraManager(mContext, this);
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToggleButton_cameraOpenClose = (ToggleButton) view.findViewById(R.id.toggleButton_cameraOpenClose);
        mToggleButton_cameraOpenClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    try {
                        mCameraDevice = mCameraManager.open(
                                (mCheckBox_stateCallback.isChecked() ? mCaptureStateCallback2 : null),
                                (mCheckBox_dataCallback.isChecked() ? mCaptureDataCallback : null),
                                (mCheckBox_preview.isChecked() ? mSurfaceView_preview.getHolder() : null));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mCameraManager.close(mCameraDevice);
                    mCameraDevice = null;
                }
            }
        });
        mToggleButton_captureStartStop = (ToggleButton) view.findViewById(R.id.toggleButton_captureStartStop);
        mToggleButton_captureStartStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextView_test.setText(getCameraProperty());
                if(isChecked){
                    mCameraDevice.startCapture();
                }
                else {
                    mCameraDevice.stopCapture();
                }
            }
        });
        mToggleButton_previewStartStop = (ToggleButton) view.findViewById(R.id.toggleButton_previewStartStop);
        mToggleButton_previewStartStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mCameraDevice.startPreview();
                }
                else {
                    mCameraDevice.stopPreview();
                }
            }
        });
        mCheckBox_stateCallback = (CheckBox) view.findViewById(R.id.checkBox_stateCallback);
        mCheckBox_dataCallback = (CheckBox) view.findViewById(R.id.checkBox_dataCallback);
        mCheckBox_preview = (CheckBox) view.findViewById(R.id.checkBox_preview);
        mTextView_captureState = (TextView) view.findViewById(R.id.textView_captureState);
        mSpinner_captureInfo = (Spinner) view.findViewById(R.id.spinner_cpatureInfo);
        mSpinner_captureInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mCameraDevice != null)
                    mSpinner_captureInfo.setAdapter(new CaptureInfoAdapter(mContext, android.R.layout.simple_spinner_item, mCameraDevice.getProperty().getSupportedCaptureInfo()));

                return false;
            }
        });
        mSpinner_captureInfo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int[] item = (int[]) parent.getSelectedItem();
                CameraProperty property = mCameraDevice.getProperty();
                property.setCaptureSize(item[0], item[1]);
                property.setCaptureFps(item[2]);
                mCameraDevice.setProperty(property);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mButton_takePicture = (Button) view.findViewById(R.id.button_takePicture);
        mButton_takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "image_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + ".jpg";
                mCameraDevice.takePicture(new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName));
            }
        });
        mToggleButton_recording = (ToggleButton) view.findViewById(R.id.toggleButton_recording);
        mToggleButton_recording.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String fileName = "movie_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + ".mp4";
                    mCameraDevice.startRecord(new File(mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName));
                }
                else {
                    mCameraDevice.stopRecord();
                }
            }
        });
        mSwitch_autoExposure = (Switch) view.findViewById(R.id.switch_autoExposure);
        mSwitch_autoExposure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CameraProperty property = mCameraDevice.getProperty();
                if(isChecked){
                    property.setExposureMode(CameraProperty.EXPOSURE_MODE_AUTO);
                }
                else {
                    property.setExposureMode(CameraProperty.EXPOSURE_MODE_MANUAL);
                }
                mCameraDevice.setProperty(property);

                mTextView_test.setText(getCameraProperty());
            }
        });
        mTextView_exposure = (TextView) view.findViewById(R.id.textView_exposure);
        mSeekBar_exposure = (SeekBar) view.findViewById(R.id.seekBar_exposure);
        mSeekBar_exposure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CameraProperty property = mCameraDevice.getProperty();
                property.setExposureStep(progress + property.getExposureStepMin());
                mCameraDevice.setProperty(property);
                mTextView_exposure.setText("Exposure:"+(progress + property.getExposureStepMin()));

                mTextView_test.setText(getCameraProperty());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTextView_brightness = (TextView) view.findViewById(R.id.textView_brightness);
        mSeekBar_brightness = (SeekBar) view.findViewById(R.id.seekBar_brightness);
        mSeekBar_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CameraProperty property = mCameraDevice.getProperty();
                property.setBrightness(progress + property.getBrightnessMin());
                int ret = mCameraDevice.setProperty(property);
                mTextView_brightness.setText("Brightness:"+(progress + property.getBrightnessMin()));

                mTextView_test.setText(getCameraProperty());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTextView_whitebalance = (TextView) view.findViewById(R.id.textView_whitebalance);
        mRadioGroup_whitebalance = (RadioGroup) view.findViewById(R.id.radioGroup_whitebalance);
        mRadioGroup_whitebalance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                CameraProperty property = mCameraDevice.getProperty();
                switch (checkedId){
                    case R.id.radioButton_auto:
                        property.setWhiteBalanceMode(CameraProperty.WHITE_BALANCE_MODE_AUTO);
                        break;
                    case R.id.radioButton_cloudyDaylight:
                        property.setWhiteBalanceMode(CameraProperty.WHITE_BALANCE_MODE_CLOUDY_DAYLIGHT);
                        break;
                    case R.id.radioButton_daylight:
                        property.setWhiteBalanceMode(CameraProperty.WHITE_BALANCE_MODE_DAYLIGHT);
                        break;
                    case R.id.radioButton_fluorescent:
                        property.setWhiteBalanceMode(CameraProperty.WHITE_BALANCE_MODE_FLUORESCENT);
                        break;
                    case R.id.radioButton_incandescent:
                        property.setWhiteBalanceMode(CameraProperty.WHITE_BALANCE_MODE_INCANDESCENT);
                        break;
                    case R.id.radioButton_twilight:
                        property.setWhiteBalanceMode(CameraProperty.WHITE_BALANCE_MODE_TWILIGHT);
                        break;
                    default:
                        Log.w(TAG, "id="+checkedId);
                        break;
                }
                mCameraDevice.setProperty(property);
                mTextView_whitebalance.setText("White balance:"+property.getWhiteBalanceMode());

                mTextView_test.setText(getCameraProperty());
            }
        });
        mTextView_powerLineFrequency = (TextView) view.findViewById(R.id.textView_powerLineFrequency);
        mRadioGroup_powerLineFrequency = (RadioGroup) view.findViewById(R.id.radioGroup_powerLineFrequency);
        mRadioGroup_powerLineFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                CameraProperty property = mCameraDevice.getProperty();
                switch (checkedId){
                    case R.id.radioButton_50Hz:
                        property.setPowerLineFrequencyControlMode(CameraProperty.POWER_LINE_FREQUENCY_CONTROL_MODE_50HZ);
                        break;
                    case R.id.radioButton_60Hz:
                        property.setPowerLineFrequencyControlMode(CameraProperty.POWER_LINE_FREQUENCY_CONTROL_MODE_60HZ);
                        break;
                    case R.id.radioButton_disable:
                        property.setPowerLineFrequencyControlMode(CameraProperty.POWER_LINE_FREQUENCY_CONTROL_MODE_DISABLE);
                        break;
                    default:
                        Log.w(TAG, "id="+checkedId);
                        break;
                }
                mCameraDevice.setProperty(property);
                mTextView_powerLineFrequency.setText("Power line frequency:"+property.getPowerLineFrequencyControlMode());

                mTextView_test.setText(getCameraProperty());
            }
        });

        // Capture format : deprecated
        mTextView_captureFormat = (TextView) view.findViewById(R.id.textView_captureFormat);
        mRadioGroup_captureFormat = (RadioGroup) view.findViewById(R.id.radioGroup_captureFormat);
        mRadioGroup_captureFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                CameraProperty property = mCameraDevice.getProperty();
                switch (checkedId){
                    case R.id.radioButton_rgb565:
                        property.setCaptureDataFormat(CameraProperty.CAPTURE_DATA_FORMAT_RGB_565);
                        break;
                    case R.id.radioButton_argb8888:
                        property.setCaptureDataFormat(CameraProperty.CAPTURE_DATA_FORMAT_ARGB_8888);
                        break;
                    case R.id.radioButton_yuy2:
                        property.setCaptureDataFormat(CameraProperty.CAPTURE_DATA_FORMAT_YUY2);
                        break;
                    case R.id.radioButton_h264:
                        property.setCaptureDataFormat(CameraProperty.CAPTURE_DATA_FORMAT_H264);
                        break;
                    default:
                        Log.w(TAG, "id="+checkedId);
                        break;
                }

                // Change capture size & framerate.
                List<int[]> list = property.getSupportedCaptureInfo();
                property.setCaptureSize(list.get(0)[0], list.get(0)[1]);
                property.setCaptureFps(list.get(0)[2]);

                mCameraDevice.setProperty(property);
                mTextView_captureFormat.setText("Capture format:"+property.getCaptureDataFormat());

                mTextView_test.setText(getCameraProperty());
            }
        });

        mTextView_indicatorMode = (TextView) view.findViewById(R.id.textView_indicatorMode);
        mRadioGroup_indicatorMode = (RadioGroup) view.findViewById(R.id.radioGroup_indicatorMode);
        mRadioGroup_indicatorMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                CameraProperty property = mCameraDevice.getProperty();
                switch (checkedId){
                    case R.id.radioButton_indicatorMode_auto:
                        property.setIndicatorMode(CameraProperty.INDICATOR_MODE_AUTO);
                        break;
                    case R.id.radioButton_indicatorMode_on:
                        property.setIndicatorMode(CameraProperty.INDICATOR_MODE_ON);
                        break;
                    case R.id.radioButton_indicatorMode_off:
                        property.setIndicatorMode(CameraProperty.INDICATOR_MODE_OFF);
                        break;
                    default:
                        Log.w(TAG, "id="+checkedId);
                        break;
                }
                mCameraDevice.setProperty(property);
                mTextView_indicatorMode.setText("Indicator mode:"+property.getIndicatorMode());

                mTextView_test.setText(getCameraProperty());
            }
        });

        mSwitch_autoFocus = (Switch) view.findViewById(R.id.switch_autoFocus);
        mSwitch_autoFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CameraProperty property = mCameraDevice.getProperty();
                if(isChecked){
                    property.setFocusMode(CameraProperty.FOCUS_MODE_AUTO);
                }
                else {
                    property.setFocusMode(CameraProperty.FOCUS_MODE_MANUAL);
                }
                mCameraDevice.setProperty(property);

                mTextView_test.setText(getCameraProperty());
            }
        });
        mTextView_focus = (TextView) view.findViewById(R.id.textView_focus);
        mSeekBar_focus = (SeekBar) view.findViewById(R.id.seekBar_focus);
        mSeekBar_focus.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CameraProperty property = mCameraDevice.getProperty();
                property.setFocusDistance(progress + property.getFocusDistanceMin());
                int ret = mCameraDevice.setProperty(property);
                mTextView_focus.setText("Focus:" + mCameraDevice.getProperty().getFocusDistance()+"(" + mCameraDevice.getProperty().getFocusDistanceMin() + " - " + mCameraDevice.getProperty().getFocusDistanceMax() + "), AF="+mCameraDevice.getProperty().getFocusMode() + "(" + ret + ")");

                mTextView_test.setText(getCameraProperty());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTextView_cameraGain = (TextView) view.findViewById(R.id.textView_cameraGain);
        mSeekBar_cameraGain = (SeekBar) view.findViewById(R.id.seekBar_cameraGain);
        mSeekBar_cameraGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CameraProperty property = mCameraDevice.getProperty();
                property.setGain(progress + property.getGainMin());
                int ret = mCameraDevice.setProperty(property);
                mTextView_cameraGain.setText("Gain:"+property.getGain());

                mTextView_test.setText(getCameraProperty());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSurfaceView_preview = (SurfaceView) view.findViewById(R.id.surfaceView_preview);

        mTextView_framerate = (TextView) view.findViewById(R.id.textView_framerate);
        mCalcurationRate_framerate = new CalcurationRate(mTextView_framerate);
        mCalcurationRate_framerate.start();

        mTextView_test = (TextView) view.findViewById(R.id.textView_test);
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
        mCameraManager.release();
        mCameraManager = null;
        mCalcurationRate_framerate.finish();
    }

    @Override
    public void onCaptureData(long timestamp, byte[] data) {
        mTextView_captureState.setText("onCaptureData:"+timestamp+",size:"+data.length);
        mCalcurationRate_framerate.updata();
    }

    @Override
    public void onCameraOpened() {
        Log.d(TAG, "onCameraOpened");
        mTextView_captureState.setText("onCameraOpened");


        initView(mCameraDevice.getProperty());

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onCameraClosed() {
        Log.d(TAG, "onCameraClosed");
        mTextView_captureState.setText("onCameraClosed");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onCaptureStarted() {
        Log.d(TAG, "onCaptureStarted");
        mTextView_captureState.setText("onCaptureStarted");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onCaptureStopped() {
        Log.d(TAG, "onCaptureStopped");
        mTextView_captureState.setText("onCaptureStopped");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onPreviewStarted() {
        Log.d(TAG, "onPreviewStarted");
        mTextView_captureState.setText("onPreviewStarted");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onPreviewStopped() {
        Log.d(TAG, "onPreviewStopped");
        mTextView_captureState.setText("onPreviewStopped");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onRecordStarted() {
        Log.d(TAG, "onRecordStarted");
        mTextView_captureState.setText("onRecordStarted");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onRecordStopped() {
        Log.d(TAG, "onRecordStopped");
        mTextView_captureState.setText("onRecordStopped");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onPictureCompleted() {
        Log.d(TAG, "onPictureCompleted");
        mTextView_captureState.setText("onPictureCompleted");

        mTextView_test.setText(getCameraProperty());
    }

    @Override
    public void onPermissionGrantResult(String permission, int grantResult) {
        Snackbar.make(getActivity().getWindow().getDecorView(), permission + " is " + (PermissionHelper.PERMISSION_GRANTED == grantResult ? "GRANTED" : "DENIED"), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCaptureData(long timestamp, ByteBuffer data) {
        mTextView_captureState.setText("onCaptureData(ByteBuffer):"+data.limit());
        mCalcurationRate_framerate.updata();
    }

    private class CaptureInfoAdapter extends ArrayAdapter {
        private Context context = null;
        private int textViewResourceId = 0;
        private List<int[]> captureInfoList = null;

        public CaptureInfoAdapter(Context _context, int _textViewResourceId, List<int[]> _captureInfoList) {
            super(_context, _textViewResourceId, _captureInfoList);

            context = _context;
            textViewResourceId = _textViewResourceId;
            captureInfoList = _captureInfoList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView)super.getView(position, convertView, parent);
            if(null != captureInfoList) {
                int[] captureInfo = captureInfoList.get(position);
                v.setText(String.valueOf(captureInfo[0] + "x" + captureInfo[1] + ", " + captureInfo[2] + "[fps]"));
            }
            else {
                v.setText("Unknown");
            }
            return v;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView)super.getDropDownView(position, convertView, parent);
            if(null != captureInfoList) {
                int[] captureInfo = captureInfoList.get(position);
                v.setText(String.valueOf(captureInfo[0] + "x" + captureInfo[1] + ", " + captureInfo[2] + "[fps]"));
            }
            else {
                v.setText("Unknown");
            }
            return v;
        }
    }

    private void initView(CameraProperty property){
        if(null == property) {
            Log.w(TAG, "CameraProperty is null...");
            return ;
        } else ;
        mSpinner_captureInfo.setAdapter(new CaptureInfoAdapter(mContext, android.R.layout.simple_spinner_item, mCameraDevice.getProperty().getSupportedCaptureInfo()));
        mSeekBar_exposure.setMax(property.getExposureStepMax() - property.getExposureStepMin());
        mSeekBar_brightness.setMax(property.getBrightnessMax() - property.getBrightnessMin());
        mSeekBar_focus.setMax(property.getFocusDistanceMax() - property.getFocusDistanceMin());
        mSeekBar_cameraGain.setMax(property.getGainMax() - property.getGainMin());

        updateView();
    }

    class CalcurationRate {
        TextView textView = null;
        int count = 0;
        long startTime = 0, endTime = 0;
        float rate = 0;

        public CalcurationRate(TextView _textView){
            textView = _textView;
        }
        public void start(){
            count = 0;
            startTime = System.currentTimeMillis();
            endTime = 0;
            rate = 0;
        }

        public void updata(){
            endTime = System.currentTimeMillis();
            count++;
            if((endTime - startTime) > 1000){
                rate = count*1000/(endTime - startTime);
                startTime = endTime;
                count = 0;
                textView.setText(String.valueOf(rate));
            }
            else ;
        }
        public void finish(){
            count = 0;
            startTime = 0;
            endTime = 0;
            rate = 0;
        }
    }

    private String getCameraProperty(){
        String str = "";

        CameraProperty property = mCameraDevice.getProperty();
        if(null != property) {
            str += "info :" + property.getCaptureSize()[0] + ", " + property.getCaptureSize()[1] + ", " + property.getCaptureFps() + ", " + property.getCaptureDataFormat() + System.lineSeparator();
            str += "expo :" + property.getExposureMode() + ", " + property.getExposureStep() + ", bright:" + property.getBrightness() + System.lineSeparator();
            str += "WB   :" + property.getWhiteBalanceMode() + ", PLF  :" + property.getPowerLineFrequencyControlMode() + ", Indi :" + property.getIndicatorMode() + System.lineSeparator();
            str += "Focus:" + property.getFocusMode() + ", " + property.getFocusDistance() + ", Gain  :" + property.getGain() + System.lineSeparator();
        } else str = null;

        return str;
    }

    private void updateView(){
        CameraProperty property = mCameraDevice.getProperty();
        if(null == property) return ;
        else ;

        // exposure
        if(property.getExposureMode().equals(CameraProperty.EXPOSURE_MODE_AUTO)) {
            mSwitch_autoExposure.setChecked(true);
            mTextView_exposure.setText("-");
        }
        else if(property.getExposureMode().equals(CameraProperty.EXPOSURE_MODE_MANUAL)) {
            mSwitch_autoExposure.setChecked(false);
            mSeekBar_exposure.setProgress(property.getExposureStepMin() + property.getExposureStep());
            mTextView_exposure.setText("Exposure:" + property.getExposureStep() + ":" + "(" + property.getExposureStepMin() + " - " + property.getExposureStepMax() + ")");
        }

        // brightness
        mSeekBar_brightness.setProgress(property.getBrightnessMin() + property.getBrightness());
        mTextView_brightness.setText("Brightness:" + property.getBrightness() + ":" + "(" + property.getBrightnessMin() + " - " + property.getBrightness() + ")");

        // whitebalance
        if(property.getExposureMode().equals(CameraProperty.EXPOSURE_MODE_AUTO)) {
            mSwitch_autoExposure.setChecked(true);
            mTextView_exposure.setText("-");
        }
        else if(property.getExposureMode().equals(CameraProperty.EXPOSURE_MODE_MANUAL)) {
            mSwitch_autoExposure.setChecked(false);
            mSeekBar_exposure.setProgress(property.getExposureStepMin() + property.getExposureStep());
            mTextView_exposure.setText("Exposure:" + property.getExposureStep() + ":" + "(" + property.getExposureStepMin() + " - " + property.getExposureStepMax() + ")");
        }

        // whitebalance
        switch(property.getWhiteBalanceMode()) {
            case CameraProperty.WHITE_BALANCE_MODE_AUTO:
                mRadioGroup_whitebalance.check(R.id.radioButton_auto);
                break;
            case CameraProperty.WHITE_BALANCE_MODE_CLOUDY_DAYLIGHT:
                mRadioGroup_whitebalance.check(R.id.radioButton_cloudyDaylight);
                break;
            case CameraProperty.WHITE_BALANCE_MODE_DAYLIGHT:
                mRadioGroup_whitebalance.check(R.id.radioButton_daylight);
                break;
            case CameraProperty.WHITE_BALANCE_MODE_FLUORESCENT:
                mRadioGroup_whitebalance.check(R.id.radioButton_fluorescent);
                break;
            case CameraProperty.WHITE_BALANCE_MODE_INCANDESCENT:
                mRadioGroup_whitebalance.check(R.id.radioButton_incandescent);
                break;
            case CameraProperty.WHITE_BALANCE_MODE_TWILIGHT:
                mRadioGroup_whitebalance.check(R.id.radioButton_twilight);
                break;
            default:
                break;
        }
        mTextView_whitebalance.setText("White balance:"+property.getWhiteBalanceMode()+","+property.getWhiteBalanceTemperature());

        // power line frequency
        switch(property.getPowerLineFrequencyControlMode()) {
            case CameraProperty.POWER_LINE_FREQUENCY_CONTROL_MODE_50HZ:
                mRadioGroup_powerLineFrequency.check(R.id.radioButton_50Hz);
                break;
            case CameraProperty.POWER_LINE_FREQUENCY_CONTROL_MODE_60HZ:
                mRadioGroup_powerLineFrequency.check(R.id.radioButton_60Hz);
                break;
            case CameraProperty.POWER_LINE_FREQUENCY_CONTROL_MODE_DISABLE:
                mRadioGroup_powerLineFrequency.check(R.id.radioButton_disable);
                break;
            default:
                break;
        }
        mTextView_powerLineFrequency.setText("Power line frequency:"+property.getPowerLineFrequencyControlMode());

        // capture format
        switch(property.getCaptureDataFormat()) {
            case CameraProperty.CAPTURE_DATA_FORMAT_RGB_565:
                mRadioGroup_captureFormat.check(R.id.radioButton_rgb565);
                break;
            case CameraProperty.CAPTURE_DATA_FORMAT_ARGB_8888:
                mRadioGroup_captureFormat.check(R.id.radioButton_argb8888);
                break;
            case CameraProperty.CAPTURE_DATA_FORMAT_YUY2:
                mRadioGroup_captureFormat.check(R.id.radioButton_yuy2);
                break;
            case CameraProperty.CAPTURE_DATA_FORMAT_H264:
                mRadioGroup_captureFormat.check(R.id.radioButton_h264);
                break;
            default:
                break;
        }
        mTextView_captureFormat.setText("Capture format:"+property.getCaptureDataFormat());

        // indicator mode
        switch(property.getIndicatorMode()) {
            case CameraProperty.INDICATOR_MODE_AUTO:
                mRadioGroup_indicatorMode.check(R.id.radioButton_indicatorMode_auto);
                break;
            case CameraProperty.INDICATOR_MODE_ON:
                mRadioGroup_indicatorMode.check(R.id.radioButton_indicatorMode_on);
                break;
            case CameraProperty.INDICATOR_MODE_OFF:
                mRadioGroup_indicatorMode.check(R.id.radioButton_indicatorMode_off);
                break;
            default:
                break;
        }
        mTextView_indicatorMode.setText("Capture format:"+property.getIndicatorMode());

        // focus
        if(property.getFocusMode().equals(CameraProperty.FOCUS_MODE_AUTO)) {
            mSwitch_autoFocus.setChecked(true);
            mTextView_focus.setText("-");
        }
        else if(property.getFocusMode().equals(CameraProperty.FOCUS_MODE_MANUAL)) {
            mSwitch_autoFocus.setChecked(false);
            mSeekBar_focus.setProgress(property.getFocusDistanceMin() + property.getFocusDistance());
            mTextView_focus.setText("Focus:" + property.getFocusDistance() + ":" + "(" + property.getFocusDistanceMin() + " - " + property.getFocusDistanceMax() + ")");
        }

        // gain
        mSeekBar_cameraGain.setProgress(property.getGainMin() + property.getGain());
        mTextView_cameraGain.setText("Gain:" + property.getGain() + ":" + "(" + property.getGainMin() + " - " + property.getGainMax() + ")");
    }
}
