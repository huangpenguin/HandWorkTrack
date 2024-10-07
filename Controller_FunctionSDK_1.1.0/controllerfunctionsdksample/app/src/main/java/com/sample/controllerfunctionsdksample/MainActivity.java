package com.sample.controllerfunctionsdksample;

import androidx.appcompat.app.AppCompatActivity;

import com.epson.moverio.btcontrol.BtCustomKey;
import com.epson.moverio.btcontrol.UIControl;
import com.epson.moverio.btcontrol.BtControllerLedMode;
import com.epson.moverio.btcontrol.KeyLock;


import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private UIControl ui;
    private BtCustomKey key;
    private BtControllerLedMode led;
    private KeyLock keyLock;

    static final private String VOLUME_UP = "VOLUME_UP";
    static final private String VOLUME_DOWN = "VOLUME_DOWN";
    static final private String TRIGGER = "TRIGGER";
    static final private String HOME = "HOME";
    static final private String BACK = "BACK";
    static final private String APP_SWITCH = "APP_SWITCH";

    static final private String POWER = "POWER";
    static final private String ALLKEY = "ALLKEY";

    static final private String KEY_CODE_VOLUME_UP = "KEY_CODE_VOLUME_UP";
    static final private String KEY_CODE_VOLUME_DOWN = "KEY_CODE_VOLUME_DOWN";
    static final private String KEY_CODE_FUNCTION = "KEY_CODE_FUNCTION";
    static final private String KEY_CODE_HOME = "KEY_CODE_HOME";
    static final  private String KEY_CODE_BACK = "KEY_CODE_BACK";
    static final private String KEY_CODE_APP_SWITCH = "KEY_CODE_APP_SWITCH";
    static final private String KEY_CODE_F1 = "KEY_CODE_F1";
    static final private String KEY_CODE_F2 = "KEY_CODE_F2";
    static final private String KEY_CODE_F3 = "KEY_CODE_F3";
    static final private String KEY_CODE_F4 = "KEY_CODE_F4";

    static final private String LED_MODE_NORMAL = "MODE_NORMAL";
    static final private String LED_MODE_OFF = "MODE_OFF";

    static final private String MIRROR_MODE = "MIRROR_MODE";
    static final private String TRACKPAD_MODE = "TRACKPAD_MODE";
    static final private String ASSIST_MODE = "ASSIST_MODE";

    static final private String INVALID_VALUE_NEGA_1 = "INVALID_VALUE";

    ArrayAdapter<String> adapterAssign1;
    Spinner spinnerAssign1;
    ArrayAdapter<String> adapterAssign2;
    Spinner spinnerAssign2;

    ArrayAdapter<String> adapterEnable;
    Spinner spinnerEnable;
    ArrayAdapter<String> adapterEnable2;
    Spinner spinnerEnable2;

    ArrayAdapter<String> adapterLed;
    Spinner spinnerLed;

    ArrayAdapter<String> adapterUi;
    Spinner spinnerUi;

    private int convertKey(String code) {
        switch(code) {
            case VOLUME_UP:
                return BtCustomKey.VOLUME_UP;
            case VOLUME_DOWN:
                return BtCustomKey.VOLUME_DOWN;
            case TRIGGER:
                return BtCustomKey.TRIGGER;
            case HOME:
                return BtCustomKey.HOME;
            case BACK:
                return BtCustomKey.BACK;
            case APP_SWITCH:
                return BtCustomKey.APP_SWITCH;
            case POWER:
                return BtCustomKey.POWER;
            case ALLKEY:
                return BtCustomKey.ALLKEY;
            case KEY_CODE_VOLUME_UP:
                return KeyEvent.KEYCODE_VOLUME_UP;
            case KEY_CODE_VOLUME_DOWN:
                return KeyEvent.KEYCODE_VOLUME_DOWN;
            case KEY_CODE_FUNCTION:
                return KeyEvent.KEYCODE_FUNCTION;
            case KEY_CODE_HOME:
                return KeyEvent.KEYCODE_HOME;
            case KEY_CODE_BACK:
                return KeyEvent.KEYCODE_BACK;
            case KEY_CODE_APP_SWITCH:
                return KeyEvent.KEYCODE_APP_SWITCH;
            case KEY_CODE_F1:
                return KeyEvent.KEYCODE_F1;
            case KEY_CODE_F2:
                return KeyEvent.KEYCODE_F2;
            case KEY_CODE_F3:
                return KeyEvent.KEYCODE_F3;
            case KEY_CODE_F4:
                return KeyEvent.KEYCODE_F4;
            default:
                return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ui = new UIControl(this);
        key = new BtCustomKey(this);
        led = new BtControllerLedMode(this);
        keyLock = new KeyLock(this);

        final Button setUiButton = findViewById(R.id.buttonSetUi);
        setUiButton.setOnClickListener(buttonClick);

        final Button getUiButton = findViewById(R.id.buttonGetUi);
        getUiButton.setOnClickListener(buttonClick);

        final Button setAssignButton = findViewById(R.id.buttonSetAssign);
        setAssignButton.setOnClickListener(buttonClick);

        final Button getAssignButton = findViewById(R.id.buttonGetAssign);
        getAssignButton.setOnClickListener(buttonClick);

        final Button setEnableButton = findViewById(R.id.buttonSetEnable);
        setEnableButton.setOnClickListener(buttonClick);

        final Button getEnableButton = findViewById(R.id.buttonGetEnable);
        getEnableButton.setOnClickListener(buttonClick);

        final Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(buttonClick);

        final Button setLedButton = findViewById(R.id.buttonSetLed);
        setLedButton.setOnClickListener(buttonClick);

        final Button getLedButton = findViewById(R.id.buttonGetLed);
        getLedButton.setOnClickListener(buttonClick);

        final Button setKeyLockButton = findViewById(R.id.buttonSetKeyLock);
        setKeyLockButton.setOnClickListener(buttonClick);

        final Button getKeyLockButton = findViewById(R.id.buttonGetKeyLock);
        getKeyLockButton.setOnClickListener(buttonClick);
        
        adapterUi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterUi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterUi.add(MIRROR_MODE);
        adapterUi.add(TRACKPAD_MODE);
        adapterUi.add(ASSIST_MODE);
        adapterUi.add(INVALID_VALUE_NEGA_1);
        spinnerUi = (Spinner) findViewById(R.id.spinner_ui);
        spinnerUi.setAdapter(adapterUi);

        adapterAssign1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterAssign1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAssign1.add(VOLUME_UP);
        adapterAssign1.add(VOLUME_DOWN);
        adapterAssign1.add(TRIGGER);
        adapterAssign1.add(HOME);
        adapterAssign1.add(BACK);
        adapterAssign1.add(APP_SWITCH);
        adapterAssign1.add(INVALID_VALUE_NEGA_1);
        spinnerAssign1 = (Spinner) findViewById(R.id.spinner_assign1);
        spinnerAssign1.setAdapter(adapterAssign1);

        adapterAssign2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterAssign2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAssign2.add(KEY_CODE_VOLUME_UP);
        adapterAssign2.add(KEY_CODE_VOLUME_DOWN);
        adapterAssign2.add(KEY_CODE_FUNCTION);
        adapterAssign2.add(KEY_CODE_HOME);
        adapterAssign2.add(KEY_CODE_BACK);
        adapterAssign2.add(KEY_CODE_APP_SWITCH);
        adapterAssign2.add(KEY_CODE_F1);
        adapterAssign2.add(KEY_CODE_F2);
        adapterAssign2.add(KEY_CODE_F3);
        adapterAssign2.add(KEY_CODE_F4);
        adapterAssign2.add(INVALID_VALUE_NEGA_1);
        spinnerAssign2 = (Spinner) findViewById(R.id.spinner_assign2);
        spinnerAssign2.setAdapter(adapterAssign2);

        adapterEnable = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterEnable.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterEnable.add(VOLUME_UP);
        adapterEnable.add(VOLUME_DOWN);
        adapterEnable.add(TRIGGER);
        adapterEnable.add(HOME);
        adapterEnable.add(BACK);
        adapterEnable.add(APP_SWITCH);
        adapterEnable.add(POWER);
        adapterEnable.add(ALLKEY);
        adapterEnable.add(INVALID_VALUE_NEGA_1);
        spinnerEnable = (Spinner) findViewById(R.id.spinner_enable);
        spinnerEnable.setAdapter(adapterEnable);

        adapterEnable2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterEnable2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterEnable2.add("ENABLE");
        adapterEnable2.add("DISABLE");
        spinnerEnable2 = (Spinner) findViewById(R.id.spinner_enable2);
        spinnerEnable2.setAdapter(adapterEnable2);

        adapterLed = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterLed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterLed.add(LED_MODE_NORMAL);
        adapterLed.add(LED_MODE_OFF);
        adapterLed.add(INVALID_VALUE_NEGA_1);
        spinnerLed = (Spinner) findViewById(R.id.spinner_led);
        spinnerLed.setAdapter(adapterLed);
    }

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int phyKey = 0;
            int keyCode = 0;
            int ledMode = 0;
            boolean ed;
            int uiMode = 0;
            switch (view.getId()) {
                case R.id.buttonSetUi:
                    if(spinnerUi.getSelectedItem() == MIRROR_MODE) {
                        uiMode = UIControl.UI_MODE_MIRROR;
                    } else if (spinnerUi.getSelectedItem() == TRACKPAD_MODE) {
                        uiMode = UIControl.UI_MODE_TRACK;
                    } else if (spinnerUi.getSelectedItem() == ASSIST_MODE) {
                        uiMode = UIControl.UI_MODE_ASSIST;
                    } else {
                        uiMode = -1;
                    }
                    Toast.makeText(getApplicationContext(),String.valueOf(ui.setUiMode(uiMode)), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonGetUi:
                    Toast.makeText(getApplicationContext(),String.valueOf(ui.getUiMode()), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonSetAssign:
                    phyKey = convertKey((String)spinnerAssign1.getSelectedItem());
                    keyCode = convertKey((String)spinnerAssign2.getSelectedItem());
                    Toast.makeText(getApplicationContext(),String.valueOf(key.setKeyAssign(phyKey, keyCode)), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonGetAssign:
                    phyKey = convertKey((String)spinnerAssign1.getSelectedItem());
                    Toast.makeText(getApplicationContext(),String.valueOf(key.getKeyAssign(phyKey)), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonSetEnable:
                    phyKey = convertKey((String)spinnerEnable.getSelectedItem());
                    if(spinnerEnable2.getSelectedItem() == "ENABLE") {
                        ed = true;
                    } else {
                        ed = false;
                    }
                    Toast.makeText(getApplicationContext(),String.valueOf(key.setKeyEnable(phyKey, ed)), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonGetEnable:
                    phyKey = convertKey((String)spinnerEnable.getSelectedItem());
                    Toast.makeText(getApplicationContext(),String.valueOf(key.getKeyEnable(phyKey)), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonReset:
                    key.resetToDefault();
                    break;
                case R.id.buttonSetLed:
                    if(spinnerLed.getSelectedItem() == LED_MODE_NORMAL) {
                        ledMode = BtControllerLedMode.MODE_NORMAL;
                    } else if (spinnerLed.getSelectedItem() == LED_MODE_OFF) {
                        ledMode = BtControllerLedMode.MODE_OFF;
                    }
                    else {
                        ledMode = -1;
                    }
                    Toast.makeText(getApplicationContext(),String.valueOf(led.setControllerLedMode(ledMode)), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonGetLed:
                    Toast.makeText(getApplicationContext(),String.valueOf(led.getControllerLedMode()), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.buttonSetKeyLock:
                    Toast.makeText(getApplicationContext(),String.valueOf(keyLock.setKeyLock(KeyLock.KEY_LOCK_ENABLE)), Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler(getMainLooper());
                    handler.postDelayed(new Runnable(){
                        public void run() {
                            if (keyLock.getKeyLock() == KeyLock.KEY_LOCK_ENABLE) {
                                Toast.makeText(getApplicationContext(),String.valueOf(keyLock.setKeyLock(KeyLock.KEY_LOCK_DISABLE)), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 10000);
                    break;
                case R.id.buttonGetKeyLock:
                    Toast.makeText(getApplicationContext(),String.valueOf(keyLock.getKeyLock()), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}