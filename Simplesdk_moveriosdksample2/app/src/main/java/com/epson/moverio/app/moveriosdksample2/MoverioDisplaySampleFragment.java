package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.epson.moverio.hardware.display.DisplayManager;
import com.epson.moverio.util.PermissionGrantResultCallback;
import com.epson.moverio.util.PermissionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MoverioDisplaySampleFragment extends androidx.fragment.app.Fragment implements PermissionGrantResultCallback {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;

    private DisplayManager mDisplayManager = null;

    private ToggleButton mToggleButton_displayOpenClose = null;
    private TextView mTextView_displayBrightness = null;
    private SeekBar mSeekBar_displayBrightness = null;
    private Switch mSwitch_brightnessMode = null;
    private Switch mSwitch_displayMode = null;
    private Switch mSwitch_displayState = null;
    private Switch mSwitch_displayAutoSleep = null;
    private Switch mSwitch_displayUserSleep = null;
    private TextView mTextView_screenHorizontalShiftStep = null;
    private SeekBar mSeekBar_screenHorizontalShiftStep = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        mDisplayManager = new DisplayManager(mContext, this);

        return inflater.inflate(R.layout.fragment_display, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToggleButton_displayOpenClose = (ToggleButton) view.findViewById(R.id.toggleButton_displayOpenClose);
        mToggleButton_displayOpenClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    try {
                        mDisplayManager.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mDisplayManager.close();
                }
            }
        });
        mTextView_displayBrightness = (TextView) view.findViewById(R.id.textView_displayBrightness);
        mSeekBar_displayBrightness = (SeekBar) view.findViewById(R.id.seekBar_displayBrightness);
        mSeekBar_displayBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int ret = mDisplayManager.setBrightness(progress);
                if(0 == ret) {
                    mTextView_displayBrightness.setText("Brightness:"+progress);
                }
                else {
                    mTextView_displayBrightness.setText("Brightness:err:");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSwitch_brightnessMode = (Switch) view.findViewById(R.id.switch_brightnessMode);
        mSwitch_brightnessMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDisplayManager.setBrightnessMode(DisplayManager.BRIGHTNESS_MODE_AUTOMATIC);
                }
                else {
                    mDisplayManager.setBrightnessMode(DisplayManager.BRIGHTNESS_MODE_MANUAL);
                }
            }
        });
        mSwitch_displayMode = (Switch) view.findViewById(R.id.switch_displayMode);
        mSwitch_displayMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDisplayManager.setDisplayMode(DisplayManager.DISPLAY_MODE_3D);
                }
                else {
                    mDisplayManager.setDisplayMode(DisplayManager.DISPLAY_MODE_2D);
                }
            }
        });
        mSwitch_displayState = (Switch) view.findViewById(R.id.switch_displayState);
        mSwitch_displayState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDisplayManager.setDisplayState(DisplayManager.DISPLAY_STATE_ON);
                }
                else {
                    mDisplayManager.setDisplayState(DisplayManager.DISPLAY_STATE_OFF);
                }
            }
        });
        mSwitch_displayAutoSleep = (Switch) view.findViewById(R.id.switch_displayAutoSleep);
        mSwitch_displayAutoSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDisplayManager.setDisplayAutoSleepEnabled(DisplayManager.DISPLAY_AUTO_SLEEP_ENABLE);
                }
                else {
                    mDisplayManager.setDisplayAutoSleepEnabled(DisplayManager.DISPLAY_AUTO_SLEEP_DISABLE);
                }
            }
        });
        mSwitch_displayUserSleep = (Switch) view.findViewById(R.id.switch_displayUserSleep);
        mSwitch_displayUserSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDisplayManager.setDisplayUserSleepEnabled(DisplayManager.DISPLAY_USER_SLEEP_ENABLE);
                }
                else {
                    mDisplayManager.setDisplayUserSleepEnabled(DisplayManager.DISPLAY_USER_SLEEP_DISABLE);
                }
            }
        });
        mTextView_screenHorizontalShiftStep = (TextView) view.findViewById(R.id.textView_screenHorizontalShiftStep);
        mSeekBar_screenHorizontalShiftStep = (SeekBar) view.findViewById(R.id.seekBar_screenHorizontalShiftStep);
        mSeekBar_screenHorizontalShiftStep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int ret = mDisplayManager.setScreenHorizontalShiftStep(progress);
                if(0 == ret) {
                    mTextView_screenHorizontalShiftStep.setText("Screen horizontal shift step:"+progress);
                }
                else {
                    mTextView_screenHorizontalShiftStep.setText("Screen horizontal shift step:err"+"("+progress+")");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        mDisplayManager.release();
        mDisplayManager = null;
    }

    @Override
    public void onPermissionGrantResult(String permission, int grantResult) {
        Snackbar.make(getActivity().getWindow().getDecorView(), permission + " is " + (PermissionHelper.PERMISSION_GRANTED == grantResult ? "GRANTED" : "DENIED"), Snackbar.LENGTH_SHORT).show();
    }
}
