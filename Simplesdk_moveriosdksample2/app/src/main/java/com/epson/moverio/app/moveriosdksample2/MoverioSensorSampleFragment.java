package com.epson.moverio.app.moveriosdksample2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.epson.moverio.hardware.sensor.SensorData;
import com.epson.moverio.hardware.sensor.SensorDataListener;
import com.epson.moverio.hardware.sensor.SensorManager;

import java.io.IOException;

public class MoverioSensorSampleFragment extends androidx.fragment.app.Fragment implements SensorDataListener {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext = null;

    private SensorManager mSensorManager = null;

    private Handler mHandler = null;

    private CheckBox mCheckBox_acc = null;
    private CheckBox mCheckBox_mag = null;
    private CheckBox mCheckBox_gyro = null;
    private CheckBox mCheckBox_light = null;
    private CheckBox mCheckBox_la = null;
    private CheckBox mCheckBox_grav = null;
    private CheckBox mCheckBox_rv = null;
    private CheckBox mCheckBox_grv = null;
    private CheckBox mCheckBox_uncalibAcc = null;
    private CheckBox mCheckBox_uncalibMag = null;
    private CheckBox mCheckBox_uncalibGyro = null;
    private CheckBox mCheckBox_motion = null;
    private CheckBox mCheckBox_stationary = null;
    private CheckBox mCheckBox_tap = null;
    private TextView mTextView_accResult = null;
    private TextView mTextView_magResult = null;
    private TextView mTextView_gyroResult = null;
    private TextView mTextView_lightResult = null;
    private TextView mTextView_laResult = null;
    private TextView mTextView_gravResult = null;
    private TextView mTextView_rvResult = null;
    private TextView mTextView_grvResult = null;
    private TextView mTextView_uncalibAccResult = null;
    private TextView mTextView_uncalibMagResult = null;
    private TextView mTextView_uncalibGyroResult = null;
    private TextView mTextView_motionResult = null;
    private TextView mTextView_stationaryResult = null;
    private TextView mTextView_tapResult = null;
    private TextView mTextView_accRate = null;
    private TextView mTextView_magRate = null;
    private TextView mTextView_gyroRate = null;
    private TextView mTextView_lightRate = null;
    private TextView mTextView_laRate = null;
    private TextView mTextView_gravRate = null;
    private TextView mTextView_rvRate = null;
    private TextView mTextView_grvRate = null;
    private TextView mTextView_uncalibAccRate = null;
    private TextView mTextView_uncalibMagRate = null;
    private TextView mTextView_uncalibGyroRate = null;
    private TextView mTextView_motionRate = null;
    private TextView mTextView_stationaryRate = null;
    private TextView mTextView_tapRate = null;

    private CalcurationRate mCalcurationRate_acc = null;
    private CalcurationRate mCalcurationRate_mag = null;
    private CalcurationRate mCalcurationRate_gyro = null;
    private CalcurationRate mCalcurationRate_light = null;
    private CalcurationRate mCalcurationRate_la = null;
    private CalcurationRate mCalcurationRate_grav = null;
    private CalcurationRate mCalcurationRate_rv = null;
    private CalcurationRate mCalcurationRate_grv = null;
    private CalcurationRate mCalcurationRate_uncalibAcc = null;
    private CalcurationRate mCalcurationRate_uncalibMag = null;
    private CalcurationRate mCalcurationRate_uncalibGyro = null;
    private CalcurationRate mCalcurationRate_motion = null;
    private CalcurationRate mCalcurationRate_stationary = null;
    private CalcurationRate mCalcurationRate_tap = null;

    private final SensorDataListener mSensorDataListener = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getContext();
        mSensorManager = new SensorManager(mContext);

        mHandler = new Handler();

        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextView_accRate = (TextView) view.findViewById(R.id.textView_accRate);
        mTextView_magRate = (TextView) view.findViewById(R.id.textView_magRate);
        mTextView_gyroRate = (TextView) view.findViewById(R.id.textView_gyroRate);
        mTextView_lightRate = (TextView) view.findViewById(R.id.textView_lightRate);
        mTextView_laRate = (TextView) view.findViewById(R.id.textView_laRate);
        mTextView_gravRate = (TextView) view.findViewById(R.id.textView_gravRate);
        mTextView_rvRate = (TextView) view.findViewById(R.id.textView_rvRate);
        mTextView_grvRate = (TextView) view.findViewById(R.id.textView_grvRate);
        mTextView_uncalibAccRate = (TextView) view.findViewById(R.id.textView_uncalibAccRate);
        mTextView_uncalibMagRate = (TextView) view.findViewById(R.id.textView_uncalibMagRate);
        mTextView_uncalibGyroRate = (TextView) view.findViewById(R.id.textView_uncalibGyroRate);
        mTextView_motionRate = (TextView) view.findViewById(R.id.textView_motionRate);
        mTextView_stationaryRate = (TextView) view.findViewById(R.id.textView_stationaryRate);
        mTextView_tapRate = (TextView) view.findViewById(R.id.textView_tapRate);
        mCalcurationRate_acc = new CalcurationRate(mTextView_accRate);
        mCalcurationRate_mag = new CalcurationRate(mTextView_magRate);
        mCalcurationRate_gyro = new CalcurationRate(mTextView_gyroRate);
        mCalcurationRate_light = new CalcurationRate(mTextView_lightRate);
        mCalcurationRate_la = new CalcurationRate(mTextView_laRate);
        mCalcurationRate_grav = new CalcurationRate(mTextView_gravRate);
        mCalcurationRate_rv = new CalcurationRate(mTextView_rvRate);
        mCalcurationRate_grv = new CalcurationRate(mTextView_grvRate);
        mCalcurationRate_uncalibAcc = new CalcurationRate(mTextView_uncalibAccRate);
        mCalcurationRate_uncalibMag = new CalcurationRate(mTextView_uncalibMagRate);
        mCalcurationRate_uncalibGyro = new CalcurationRate(mTextView_uncalibGyroRate);
        mCalcurationRate_motion = new CalcurationRate(mTextView_motionRate);
        mCalcurationRate_stationary = new CalcurationRate(mTextView_stationaryRate);
        mCalcurationRate_tap = new CalcurationRate(mTextView_tapRate);

        mCalcurationRate_acc.start();
        mCalcurationRate_mag.start();
        mCalcurationRate_gyro.start();
        mCalcurationRate_light.start();
        mCalcurationRate_la.start();
        mCalcurationRate_grav.start();
        mCalcurationRate_rv.start();
        mCalcurationRate_grv.start();
        mCalcurationRate_uncalibAcc.start();
        mCalcurationRate_uncalibMag.start();
        mCalcurationRate_uncalibGyro.start();
        mCalcurationRate_motion.start();
        mCalcurationRate_stationary.start();
        mCalcurationRate_tap.start();

        // Accelerometer sensor.
        mTextView_accResult = (TextView) view.findViewById(R.id.textView_accResult);
        mCheckBox_acc = (CheckBox) view.findViewById(R.id.checkBox_acc);
        mCheckBox_acc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_ACCELEROMETER, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_ACCELEROMETER, mSensorDataListener);
                    mCalcurationRate_acc.finish();
                }
            }
        });

        // Magnetic field sensor.
        mTextView_magResult = (TextView) view.findViewById(R.id.textView_magResult);
        mCheckBox_mag = (CheckBox) view.findViewById(R.id.checkBox_mag);
        mCheckBox_mag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_MAGNETIC_FIELD, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_MAGNETIC_FIELD, mSensorDataListener);
                    mCalcurationRate_mag.finish();
                }
            }
        });

        // Gyroscope sensor.
        mTextView_gyroResult = (TextView) view.findViewById(R.id.textView_gyroResult);
        mCheckBox_gyro = (CheckBox) view.findViewById(R.id.checkBox_gyro);
        mCheckBox_gyro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_GYROSCOPE, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_GYROSCOPE, mSensorDataListener);
                    mCalcurationRate_gyro.finish();
                }
            }
        });

        // Light sensor.
        mTextView_lightResult = (TextView) view.findViewById(R.id.textView_lightResult);
        mCheckBox_light = (CheckBox) view.findViewById(R.id.checkBox_light);
        mCheckBox_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_LIGHT, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_LIGHT, mSensorDataListener);
                    mCalcurationRate_light.finish();
                }
            }
        });

        // Linear accelerometer sensor.
        mTextView_laResult = (TextView) view.findViewById(R.id.textView_laResult);
        mCheckBox_la = (CheckBox) view.findViewById(R.id.checkBox_la);
        mCheckBox_la.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_LINEAR_ACCELERATION, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_LINEAR_ACCELERATION, mSensorDataListener);
                    mCalcurationRate_la.finish();
                }
            }
        });

        // Gravity sensor.
        mTextView_gravResult = (TextView) view.findViewById(R.id.textView_gravResult);
        mCheckBox_grav = (CheckBox) view.findViewById(R.id.checkBox_grav);
        mCheckBox_grav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_GRAVITY, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_GRAVITY, mSensorDataListener);
                    mCalcurationRate_grav.finish();
                }
            }
        });

        // Rotation vector sensor.
        mTextView_rvResult = (TextView) view.findViewById(R.id.textView_rvResult);
        mCheckBox_rv = (CheckBox) view.findViewById(R.id.checkBox_rv);
        mCheckBox_rv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_ROTATION_VECTOR, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_ROTATION_VECTOR, mSensorDataListener);
                    mCalcurationRate_rv.finish();
                }
            }
        });

        // Game rotation vector sensor.
        mTextView_grvResult = (TextView) view.findViewById(R.id.textView_grvResult);
        mCheckBox_grv = (CheckBox) view.findViewById(R.id.checkBox_grv);
        mCheckBox_grv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_GAME_ROTATION_VECTOR, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_GAME_ROTATION_VECTOR, mSensorDataListener);
                    mCalcurationRate_grv.finish();
                }
            }
        });

        // Uncalibrated accelerometer sensor.
        mTextView_uncalibAccResult = (TextView) view.findViewById(R.id.textView_uncalibAccResult);
        mCheckBox_uncalibAcc = (CheckBox) view.findViewById(R.id.checkBox_uncalibAcc);
        mCheckBox_uncalibAcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_ACCELEROMETER_UNCALIBRATED, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_ACCELEROMETER_UNCALIBRATED, mSensorDataListener);
                    mCalcurationRate_uncalibAcc.finish();
                }
            }
        });

        // Uncalibrated magnetic field sensor.
        mTextView_uncalibMagResult = (TextView) view.findViewById(R.id.textView_uncalibMagResult);
        mCheckBox_uncalibMag = (CheckBox) view.findViewById(R.id.checkBox_uncalibMag);
        mCheckBox_uncalibMag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_MAGNETIC_FIELD_UNCALIBRATED, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_MAGNETIC_FIELD_UNCALIBRATED, mSensorDataListener);
                    mCalcurationRate_uncalibMag.finish();
                }
            }
        });

        // Uncalibrated gyroscope sensor.
        mTextView_uncalibGyroResult = (TextView) view.findViewById(R.id.textView_uncalibGyroResult);
        mCheckBox_uncalibGyro = (CheckBox) view.findViewById(R.id.checkBox_uncalibGyro);
        mCheckBox_uncalibGyro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_GYROSCOPE_UNCALIBRATED, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_GYROSCOPE_UNCALIBRATED, mSensorDataListener);
                    mCalcurationRate_uncalibGyro.finish();
                }
            }
        });

        // Motion detection sensor.
        mTextView_motionResult = (TextView) view.findViewById(R.id.textView_motionResult);
        mCheckBox_motion = (CheckBox) view.findViewById(R.id.checkBox_motion);
        mCheckBox_motion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_MOTION_DETECT, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_MOTION_DETECT, mSensorDataListener);
                    mCalcurationRate_motion.finish();
                }
            }
        });

        // Stationary detection sensor.
        mTextView_stationaryResult = (TextView) view.findViewById(R.id.textView_stationaryResult);
        mCheckBox_stationary = (CheckBox) view.findViewById(R.id.checkBox_stationary);
        mCheckBox_stationary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_STATIONARY_DETECT, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_STATIONARY_DETECT, mSensorDataListener);
                    mCalcurationRate_stationary.finish();
                }
            }
        });

        // Tap detection sensor.
        mTextView_tapResult = (TextView) view.findViewById(R.id.textView_tapResult);
        mCheckBox_tap = (CheckBox) view.findViewById(R.id.checkBox_tap);
        mCheckBox_tap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSensorManager.open(SensorManager.TYPE_HEADSET_TAP_DETECT, mSensorDataListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    mSensorManager.close(SensorManager.TYPE_HEADSET_TAP_DETECT, mSensorDataListener);
                    mCalcurationRate_tap.finish();
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
        mSensorManager.release();
        mSensorManager = null;
    }

    @Override
    public void onSensorDataChanged(final SensorData data) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch(data.type){
                    case SensorManager.TYPE_ACCELEROMETER:
                        mTextView_accResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]));
                        mCalcurationRate_acc.updata();
                        break;
                    case SensorManager.TYPE_MAGNETIC_FIELD:
                        mTextView_magResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]) + "," + String.valueOf(data.accuracy));
                        mCalcurationRate_mag.updata();
                        break;
                    case SensorManager.TYPE_GYROSCOPE:
                        mTextView_gyroResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]));
                        mCalcurationRate_gyro.updata();
                        break;
                    case SensorManager.TYPE_LIGHT:;
                        mTextView_lightResult.setText(String.format("%.4f", data.values[0]));
                        mCalcurationRate_light.updata();
                        break;
                    case SensorManager.TYPE_GRAVITY:
                        mTextView_gravResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]));
                        mCalcurationRate_grav.updata();
                        break;
                    case SensorManager.TYPE_LINEAR_ACCELERATION:
                        mTextView_laResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]));
                        mCalcurationRate_la.updata();
                        break;
                    case SensorManager.TYPE_ROTATION_VECTOR:
                        mTextView_rvResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]) + "," + String.format("%.4f", data.values[3]));
                        mCalcurationRate_rv.updata();
                        break;
                    case SensorManager.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                        mTextView_uncalibMagResult.setText(String.format("%.2f", data.values[0]) + "," + String.format("%.2f", data.values[1]) + "," + String.format("%.2f", data.values[2]) + "," + String.format("%.2f", data.values[3]) + "," + String.format("%.2f", data.values[4]) + "," + String.format("%.2f", data.values[5]));
                        mCalcurationRate_uncalibMag.updata();
                        break;
                    case SensorManager.TYPE_GAME_ROTATION_VECTOR:
                        mTextView_grvResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]) + "," + String.format("%.4f", data.values[3]));
                        mCalcurationRate_grv.updata();
                        break;
                    case SensorManager.TYPE_GYROSCOPE_UNCALIBRATED:
                        mTextView_uncalibGyroResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]) + "," + String.format("%.4f", data.values[3]) + "," + String.format("%.4f", data.values[4]) + "," + String.format("%.4f", data.values[5]));
                        mCalcurationRate_uncalibGyro.updata();
                        break;
                    case SensorManager.TYPE_STATIONARY_DETECT:
                        mTextView_stationaryResult.setText(String.format("%.4f", data.values[0]));
                        mCalcurationRate_stationary.updata();
                        break;
                    case SensorManager.TYPE_MOTION_DETECT:
                        mTextView_motionResult.setText(String.format("%.4f", data.values[0]));
                        mCalcurationRate_motion.updata();
                        break;
                    case SensorManager.TYPE_ACCELEROMETER_UNCALIBRATED:
                        mTextView_uncalibAccResult.setText(String.format("%.4f", data.values[0]) + "," + String.format("%.4f", data.values[1]) + "," + String.format("%.4f", data.values[2]) + "," + String.format("%.4f", data.values[3]) + "," + String.format("%.4f", data.values[4]) + "," + String.format("%.4f", data.values[5]));
                        mCalcurationRate_uncalibAcc.updata();
                        break;
                    case SensorManager.TYPE_HEADSET_TAP_DETECT:
                        mTextView_tapResult.setText(String.format("%.4f", data.values[0]));
                        mCalcurationRate_tap.updata();
                        break;
                    default:
                        break;
                }
            }
        });
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
}
