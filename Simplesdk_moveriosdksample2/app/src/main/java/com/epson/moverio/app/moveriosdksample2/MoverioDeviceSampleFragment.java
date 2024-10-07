package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.epson.moverio.system.DeviceManager;
import com.epson.moverio.system.HeadsetStateCallback;
import com.epson.moverio.util.PermissionGrantResultCallback;
import com.epson.moverio.util.PermissionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MoverioDeviceSampleFragment extends androidx.fragment.app.Fragment implements HeadsetStateCallback, PermissionGrantResultCallback {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;

    private DeviceManager mDeviceManager = null;

    private ToggleButton mToggleButton_deviceOpenClose = null;
    private Button mButton_updateHeadsetInfo = null;
    private TextView mTextView_headsetProductName = null;
    private TextView mTextView_headsetSystemVersion = null;
    private TextView mTextView_headsetSerialNumber = null;
    private TextView mTextView_headsetState = null;
    private TextView mTextView_headsetDisplayLeftTemp = null;
    private TextView mTextView_headsetDisplayRightTemp = null;
    private TextView mTextView_headsetProcessorTemp = null;
    private TextView mTextView_headsetAttachDetach = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        mDeviceManager = new DeviceManager(mContext, this);
        mDeviceManager.registerHeadsetStateCallback(this);

        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToggleButton_deviceOpenClose = (ToggleButton) view.findViewById(R.id.toggleButton_deviceOpenClose);
        mToggleButton_deviceOpenClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    try {
                        mDeviceManager.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mDeviceManager.close();
                }
            }
        });
        mButton_updateHeadsetInfo = (Button) view.findViewById(R.id.button_updateHeadsetInfo);
        mButton_updateHeadsetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        mTextView_headsetProductName = (TextView) view.findViewById(R.id.textView_headsetProductName);
        mTextView_headsetSystemVersion = (TextView) view.findViewById(R.id.textView_headsetSystemVersion);
        mTextView_headsetSerialNumber = (TextView) view.findViewById(R.id.textView_headsetSerialNumber);
        mTextView_headsetState = (TextView) view.findViewById(R.id.textView_headsetState);
        mTextView_headsetDisplayLeftTemp = (TextView) view.findViewById(R.id.textView_headsetDisplayLeftTemp);
        mTextView_headsetDisplayRightTemp = (TextView) view.findViewById(R.id.textView_headsetDisplayRightTemp);
        mTextView_headsetProcessorTemp = (TextView) view.findViewById(R.id.textView_headsetProcessorTemp);
        mTextView_headsetAttachDetach = (TextView) view.findViewById(R.id.textView_headsetAttachDetach);
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    @Override
    public void onPause() {
        super.onPause();
        update();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDeviceManager.unregisterHeadsetStateCallback(this);
        mDeviceManager.release();
        mDeviceManager = null;
    }

    @Override
    public void onHeadsetAttached() {
        update();
    }

    @Override
    public void onHeadsetDetached() {
        update();
    }

    @Override
    public void onHeadsetDisplaying() {
        update();
    }

    @Override
    public void onHeadsetTemperatureError() {
        update();
    }

    private void update() {
        mTextView_headsetProductName.setText(String.valueOf(mDeviceManager.getHeadsetProductName()));
        mTextView_headsetSystemVersion.setText(String.valueOf(mDeviceManager.getHeadsetSystemVersion()));
        mTextView_headsetSerialNumber.setText(String.valueOf(mDeviceManager.getHeadsetSerialNumber()));
        mTextView_headsetState.setText(String.valueOf(String.valueOf(mDeviceManager.getHeadsetState())));
        mTextView_headsetDisplayLeftTemp.setText(String.valueOf(mDeviceManager.getDeviceTemperature(DeviceManager.DEVICE_ID_HEADSET_DISPLAY_L)));
        mTextView_headsetDisplayRightTemp.setText(String.valueOf(mDeviceManager.getDeviceTemperature(DeviceManager.DEVICE_ID_HEADSET_DISPLAY_R)));
        mTextView_headsetProcessorTemp.setText(String.valueOf(mDeviceManager.getDeviceTemperature(DeviceManager.DEVICE_ID_HEADSET_PROCESSOR)));
        mTextView_headsetAttachDetach.setText(mDeviceManager.isHeadsetAttached() ? "Headset attached" : "Headset detached");
    }

    @Override
    public void onPermissionGrantResult(String permission, int grantResult) {
        Snackbar.make(getActivity().getWindow().getDecorView(), permission + " is " + (PermissionHelper.PERMISSION_GRANTED == grantResult ? "GRANTED" : "DENIED"), Snackbar.LENGTH_SHORT).show();
    }
}
