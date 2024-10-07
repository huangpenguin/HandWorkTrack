package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.epson.moverio.hardware.audio.AudioManager;
import com.epson.moverio.util.PermissionGrantResultCallback;
import com.epson.moverio.util.PermissionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MoverioAudioSampleFragment extends androidx.fragment.app.Fragment implements PermissionGrantResultCallback {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;

    private AudioManager mAudioManager = null;

    private ToggleButton mToggleButton_audioOpenClose = null;
    private TextView mTextView_volume = null;
    private Button mButton_volumeUp = null;
    private Button mButton_volumeDown = null;
    private Switch mSwitch_volumeLimitMode = null;
    private Switch mSwitch_deviceMode = null;
    private TextView mTextView_gainStep = null;
    private Button mButton_gainStepUp = null;
    private Button mButton_gainStepDown = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        mAudioManager = new AudioManager(mContext, this);

        return inflater.inflate(R.layout.fragment_audio, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToggleButton_audioOpenClose = (ToggleButton) view.findViewById(R.id.toggleButton_audioOpenClose);
        mToggleButton_audioOpenClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    try {
                        mAudioManager.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mAudioManager.close();
                }
            }
        });
        mTextView_volume = (TextView) view.findViewById(R.id.textView_volume);
        mButton_volumeUp = (Button) view.findViewById(R.id.button_volumeUp);
        mButton_volumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int volume = mAudioManager.getVolume();
                int ret = mAudioManager.setVolume(volume + 1);
                if(0 == ret){
                    mTextView_volume.setText("Volume(" + mAudioManager.getVolumeMin() + "-" + mAudioManager.getVolumeMax()+ "):" + String.valueOf(volume + 1));
                }
                else {
                    mTextView_volume.setText("Volume(" + mAudioManager.getVolumeMin() + "-" + mAudioManager.getVolumeMax() + "):err(" + ret + ")");
                }
            }
        });
        mButton_volumeDown = (Button) view.findViewById(R.id.button_volumeDown);
        mButton_volumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int volume = mAudioManager.getVolume();
                int ret = mAudioManager.setVolume(volume - 1);
                if(0 == ret){
                    mTextView_volume.setText("Volume(" + mAudioManager.getVolumeMin() + "-" + mAudioManager.getVolumeMax() + "):" + String.valueOf(volume - 1));
                }
                else {
                    mTextView_volume.setText("Volume(" + mAudioManager.getVolumeMin() + "-" + mAudioManager.getVolumeMax() + "):err(" + ret + ")");
                }
            }
        });
        mSwitch_volumeLimitMode = (Switch) view.findViewById(R.id.switch_volumeLimitMode);
        mSwitch_volumeLimitMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mAudioManager.setVolumeLimitMode(AudioManager.VOLUME_LIMIT_MODE_ON);
                }
                else {
                    mAudioManager.setVolumeLimitMode(AudioManager.VOLUME_LIMIT_MODE_OFF);
                }
            }
        });
        mSwitch_deviceMode = (Switch) view.findViewById(R.id.switch_deviceMode);
        mSwitch_deviceMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mAudioManager.setDeviceMode(AudioManager.DEVICE_MODE_AUDIO_JACK);
                }
                else {
                    mAudioManager.setDeviceMode(AudioManager.DEVICE_MODE_BUILTIN_AUDIO);
                }
            }
        });
        mTextView_gainStep = (TextView) view.findViewById(R.id.textView_gainStep);
        mButton_gainStepUp = (Button) view.findViewById(R.id.button_gainStepUp);
        mButton_gainStepUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gainStep = mAudioManager.getGainStep();
                gainStep++;
                if(mAudioManager.getGainStepMin() <= gainStep&&gainStep <= mAudioManager.getGainStepMax()){
                    if(0 == mAudioManager.setGainStep(gainStep)){
                        mTextView_gainStep.setText("GainStep:" + String.valueOf(mAudioManager.getGainStep()));
                    }
                    else {
                        mTextView_gainStep.setText("GainStep:err");
                    }
                }
                else {
                    mTextView_gainStep.setText("GainStep:err");
                }
            }
        });
        mButton_gainStepDown = (Button) view.findViewById(R.id.button_gainStepDown);
        mButton_gainStepDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gainStep = mAudioManager.getGainStep();
                gainStep--;
                if(mAudioManager.getGainStepMin() <= gainStep&&gainStep <= mAudioManager.getGainStepMax()){
                    if(0 == mAudioManager.setGainStep(gainStep)){
                        mTextView_gainStep.setText("GainStep:" + String.valueOf(mAudioManager.getGainStep()));
                    }
                    else {
                        mTextView_gainStep.setText("GainStep:err");
                    }
                }
                else {
                    mTextView_gainStep.setText("GainStep:err");
                }
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
        mAudioManager.release();
        mAudioManager = null;
    }

    @Override
    public void onPermissionGrantResult(String permission, int grantResult) {
        Snackbar.make(getActivity().getWindow().getDecorView(), permission + " is " + (PermissionHelper.PERMISSION_GRANTED == grantResult ? "GRANTED" : "DENIED"), Snackbar.LENGTH_SHORT).show();
    }
}
