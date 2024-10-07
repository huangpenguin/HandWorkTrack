package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.epson.moverio.hardware.display.DisplayManager;

import java.io.IOException;

public class MoverioDemoFragment extends androidx.fragment.app.Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;
    private DisplayManager mDisplayManager_moverio = null;
    private android.hardware.display.DisplayManager mDisplayManager_android = null;
    private Display[] mDisplayList = null;

    private ToggleButton mToggleButton = null;
    private SeekBar mSeekBar_screenHorizontalShiftStep = null;
    private SeekBar mSeekBar_textSize = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getContext();

        mDisplayManager_android = (android.hardware.display.DisplayManager)mContext.getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager_moverio = new DisplayManager(mContext);

        return inflater.inflate(R.layout.fragment_subtitle_demo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToggleButton = (ToggleButton) view.findViewById(R.id.toggleButton_demoStartStop);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mDisplay = getExternalDisplay();
                    startDemo(mDisplay);
                    try {
                        mDisplayManager_moverio.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    stopDemo(mDisplay);
                    mDisplayManager_moverio.close();
                }
            }
        });
        mSeekBar_screenHorizontalShiftStep = (SeekBar) view.findViewById(R.id.seekBar_screenHorizontalShiftStep_demo);
        mSeekBar_screenHorizontalShiftStep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDisplayManager_moverio.setScreenHorizontalShiftStep(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBar_textSize = (SeekBar) view.findViewById(R.id.seekBar_fontSize);
        mSeekBar_textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMoverioDemoPresentation.setTextSize(progress);
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
        mDisplayManager_moverio.close();
        mDisplayManager_moverio.release();
        mDisplayManager_moverio = null;
    }

    private MoverioDemoPresentation mMoverioDemoPresentation = null;
    private void startDemo(Display display){
        if(null == display||display.isValid()){
            Log.e(TAG, "not found display");
        } else ;
        mMoverioDemoPresentation = new MoverioDemoPresentation(mContext, display);
        mMoverioDemoPresentation.show();
    }
    private void stopDemo(Display display){
        if(null == display){
            Log.e(TAG, "not found display");
        } else ;
        mMoverioDemoPresentation.dismiss();
    }
    private final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    MoverioDemoPresentation presentation = (MoverioDemoPresentation)dialog;
                    int displayId = presentation.getDisplay().getDisplayId();
                    Log.d(TAG, "Presentation on display #" + displayId + " was dismissed.");
                }
            };
    private Display mDisplay = null;
    private Display getExternalDisplay(){
        Display display = null;
        mDisplayList = mDisplayManager_android.getDisplays(android.hardware.display.DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        for(Display d : mDisplayList){
            Log.e(TAG, d.getName()+","+d.getDisplayId()+","+d.getMode().toString()+","+d.getSupportedModes().toString());
            display = d;
        }
        return display;
    }
}
