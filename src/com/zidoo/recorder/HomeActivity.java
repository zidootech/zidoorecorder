package com.zidoo.recorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.flurry.android.FlurryAgent;
import com.zidoo.hdmi.recorder.R;
import com.zidoo.recorder.contants.APPConstants;
import com.zidoo.recorder.pip.PipService;
import com.zidoo.recorder.service.RecorderService;
import com.zidoo.recorder.tool.HdmiTool;
import com.zidoo.recorder.view.ZidooTypeface;

/**
 * 
 * 
 * @author jiangbo
 * 
 *         2014-9-5
 * 
 */

public class HomeActivity extends Activity {
    private HdmiTool mHdmiTool = null;
    public static boolean isActivity = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZidooTypeface.initTypeface(this);
        setContentView(R.layout.home_ac);
        APPConstants.sRecordActivityRunning = true;
        stopService(new Intent(this, PipService.class));

        isActivity = true;
        initData();
        Intent intent = getIntent();
        boolean isRecorder = false;
        if (intent != null) {
            String command = intent.getStringExtra("command");
            if (command != null && command.equals("tv")) {
                isRecorder = true;
            }
        }
        mHdmiTool = new HdmiTool(HomeActivity.this, isRecorder);
        // Button start = (Button) findViewById(R.id.home_ac_start);
        // start.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // System.out.println("bob----onclick");
        // // mZidooRecorderTool.strartRecordHDMI();
        // }
        // });
        // Button stop = (Button) findViewById(R.id.home_ac_stop);
        // stop.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // // mZidooRecorderTool.stopRecordHDMI();
        // }
        // });

    }

    @Override
    protected void onResume() {
        if (mHdmiTool != null) {
            mHdmiTool.onResume();
        }
        isActivity = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mHdmiTool != null) {
            mHdmiTool.onPause();
        }
        isActivity = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            FlurryAgent.onEndSession(this);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            FlurryAgent.onStartSession(this, "T66P794D7KVY7BQVJC99");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void initData() {
        Intent intent = new Intent(this, RecorderService.class);
        this.startService(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                if (mHdmiTool != null && mHdmiTool.back()) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_MENU: {
                if (mHdmiTool != null) {
                    mHdmiTool.disRecorderMenu();
                }
            }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT: {

            }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT: {

            }
                break;
            case KeyEvent.KEYCODE_DPAD_UP: {

            }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN: {

            }
                break;
            default:
                break;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        try {
            APPConstants.sRecordActivityRunning = false;
            isActivity = false;
            mHdmiTool.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
