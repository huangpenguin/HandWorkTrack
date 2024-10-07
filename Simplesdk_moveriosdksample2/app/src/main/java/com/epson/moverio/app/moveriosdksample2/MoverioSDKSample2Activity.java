package com.epson.moverio.app.moveriosdksample2;

import android.os.Bundle;

import com.epson.moverio.system.DeviceManager;
import com.epson.moverio.system.HeadsetStateCallback;
import com.epson.moverio.util.PermissionHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;

public class MoverioSDKSample2Activity extends FragmentActivity implements HeadsetStateCallback {
    private final String TAG = this.getClass().getSimpleName();

    private PermissionHelper mPermissionHelper = null;

    private ViewPager mViewPager = null;
    private FragmentStatePagerAdapter mFragmentStatePagerAdapter = null;
    private TabLayout mTabLayout = null;

    private DeviceManager mDeviceManager = null;

    private enum MoverioSDKSample {
        SDK_INFO(0, new MoverioSdkInfoSampleFragment(), "SDK INFO"),
        HARDWARE_INFO(1, new MoverioHardwareInfoSampleFragment(), "HARDWARE INFO"),
        DEVICE(2, new MoverioDeviceSampleFragment(), "DEVICE INFO"),
        DISPLAY(3, new MoverioDisplaySampleFragment(), "DISPLAY"),
        SENSOR(4, new MoverioSensorSampleFragment(), "SENSOR"),
        CAMERA(5, new MoverioCameraSampleFragment(), "CAMERA"),
        AUDIO(6, new MoverioAudioSampleFragment(), "AUDIO"),
        DEMO(7, new MoverioDemoFragment(), "DEMO"),
        APP_LAUNCH(8, new MoverioAppLauncherFragment(), "APP LAUNCH");

        private int id;
        private Fragment fragment;
        private String name;

        MoverioSDKSample(int _id, Fragment _fragment, String _name){
            this.id = _id;
            this.fragment = _fragment;
            this.name = _name;
        }

        protected int getId(){
            return this.id;
        }
        protected Fragment getFragment(){
            return this.fragment;
        }
        protected String getName(){
            return this.name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moverio_sdk_sample2);

        mPermissionHelper = new PermissionHelper(this);
        mDeviceManager = new DeviceManager(this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public CharSequence getPageTitle(int position) {
                return MoverioSDKSample.values()[position].getName();
            }
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                for(MoverioSDKSample sample : MoverioSDKSample.values()){
                    if(sample.getId() == position){
                        fragment = sample.getFragment();
                    }
                    else ;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return MoverioSDKSample.values().length;
            }
        };
        mViewPager.setAdapter(mFragmentStatePagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceManager.registerHeadsetStateCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceManager.unregisterHeadsetStateCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceManager.release();
        mDeviceManager = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onHeadsetAttached() {
        Snackbar.make(getWindow().getDecorView(), "Headset attached...", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onHeadsetDetached() {
        Snackbar.make(getWindow().getDecorView(), "Headset detached...", Snackbar.LENGTH_SHORT).show();

        mDeviceManager.close();
    }

    @Override
    public void onHeadsetDisplaying() {
        Snackbar.make(getWindow().getDecorView(), "Headset displaying...", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onHeadsetTemperatureError() {
        Snackbar.make(getWindow().getDecorView(), "Headset temperature error...", Snackbar.LENGTH_SHORT).show();
    }
}
