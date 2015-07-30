package com.zidoo.recorder.service;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.zidoo.hdmi.recorder.R;
import com.zidoo.recorder.HomeActivity;
import com.zidoo.recorder.time.WheelMain;
import com.zidoo.recorder.tool.RecorderInfo;
import com.zidoo.recorder.tool.ZidooRecorderTool;

/**
 * 
 * 
 * @author jiangbo
 * 
 *         2014-9-10
 * 
 */
public class RecorderService extends Service {

	// 预约对象
	public static RecorderInfo	mYuYueRecorderInfo		= null;
	public static RecorderInfo	mYuYueRecorderInfo_last	= null;

	public static boolean		isYUYUETimeOK			= false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		try {
			initView();
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void initView() {
		isYUYUETimeOK = false;
		mYuYueRecorderInfo = ZidooRecorderTool.getLocalObject(RecorderService.this, ZidooRecorderTool.DATA_OBJECT);
		System.out.println("bob   service  initView");
		if (mYuYueRecorderInfo != null) {
			System.out.println("bob   service  mYuYueRecorderInfo != null");
			if (!WheelMain.isYueYuTime(mYuYueRecorderInfo.order_time)) {
				delete();
				System.out.println("bob   service  mYuYueRecorderInfo delete");
			}else
			{
				System.out.println("bob   service  mYuYueRecorderInfo ok");
			}
		}
		mYuYueRecorderInfo_last = mYuYueRecorderInfo;
		startYuyue();
	}
	
	
	private void delete()
	{
		try {
			File file = new File(mYuYueRecorderInfo.path);
			file.delete();
			ZidooRecorderTool.deleteLocalObject(RecorderService.this, ZidooRecorderTool.DATA_OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		RecorderService.mYuYueRecorderInfo = null;
		RecorderService.mYuYueRecorderInfo_last = null;
	}

	Timer		yuyueTimer		= new Timer();
	TimerTask	yuyueTimerTask	= null;

	private void startYuyue() {
		if (yuyueTimerTask != null) {
			yuyueTimerTask.cancel();
			yuyueTimerTask = null;
		}

		yuyueTimerTask = new TimerTask() {

			@Override
			public void run() {
				try {

					if (mYuYueRecorderInfo != null && mYuYueRecorderInfo_last != null) {
						
						if (!WheelMain.isYueYuTime(mYuYueRecorderInfo.order_time)) {
							delete();
							System.out.println("bob   service  startYuyue delete");
						}else {
							if (WheelMain.compareStartTime(mYuYueRecorderInfo.order_time)) {
								mYuYueRecorderInfo_last = null;
								System.out.println("bob -  yuyue time ok");
								System.out.println("bob - HomeActivity.isActivity = " + HomeActivity.isActivity);
								if (HomeActivity.isActivity) {
									RecorderService.this.sendBroadcast(new Intent("yuyuerecording.aciton"));
								} else {
									mhHandler.sendEmptyMessage(0);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		yuyueTimer.schedule(yuyueTimerTask, 1 * 1000, 1 * 1000);

	}

	Handler	mhHandler	= new Handler() {
							public void handleMessage(android.os.Message msg) {
								try {
									RecorderService.this.sendBroadcast(new Intent("yuyuerecording.stop.aciton"));
									// startDialog(RecorderService.this);
									try {
										isYUYUETimeOK = true;
										Intent intent = new Intent(RecorderService.this, HomeActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
										RecorderService.this.startActivity(intent);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							};
						};

	// public void startDialog(final Context mContext) {
	// final Dialog eixtDialog = new Dialog(mContext, R.style.lockDialog);
	// LayoutInflater inflater = LayoutInflater.from(mContext);
	// View dia_view = inflater.inflate(R.layout.re_dialog, null);
	// final TextView hit_text = (TextView) dia_view
	// .findViewById(R.id.delete_dialog_title);
	// final TextView msg_text = (TextView) dia_view
	// .findViewById(R.id.delete_dialog_msg);
	// final Button btu_set = (Button) dia_view
	// .findViewById(R.id.remane_input_ok);
	// final Button btu_exit = (Button) dia_view
	// .findViewById(R.id.remane_input_set);
	// hit_text.setText(mContext.getString(R.string.record_delete_hit));
	// msg_text.setText(mContext.getString(R.string.yuyue_time_ok_msg));
	// btu_set.setText(mContext.getString(R.string.start_recorder));
	// btu_exit.setText(mContext.getString(R.string.btn_canle));
	//
	// eixtDialog.setOnKeyListener(new OnKeyListener() {
	// public boolean onKey(DialogInterface dialog, int keyCode,
	// KeyEvent event) {
	// if (event.getAction() == KeyEvent.ACTION_DOWN) {
	// // SoundTool.soundKey(keyCode);
	// // if (keyCode == KeyEvent.KEYCODE_BACK) {
	// // return true;
	// // }
	// }
	// return false;
	// }
	// });
	//
	// btu_exit.setOnClickListener(new OnClickListener() {
	// public void onClick(View arg0) {
	// eixtDialog.dismiss();
	// }
	// });
	//
	// btu_set.setOnClickListener(new OnClickListener() {
	// public void onClick(View arg0) {
	// eixtDialog.dismiss();
	// }
	// });
	// eixtDialog.setContentView(dia_view);
	// Window window = eixtDialog.getWindow();
	// window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	// // Window win = eixtDialog.getWindow();
	// // LayoutParams params = win.getAttributes();
	// // params.x = 0;
	// // params.y = -20;
	// // win.setAttributes(params);
	// eixtDialog.show();
	// btu_exit.requestFocus();
	// }

	@Override
	public void onDestroy() {
		if (yuyueTimerTask != null) {
			yuyueTimerTask.cancel();
			yuyueTimerTask = null;
		}
		super.onDestroy();
	}

}
