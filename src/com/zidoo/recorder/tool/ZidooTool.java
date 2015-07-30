/*package com.zidoo.recorder.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.hdmirecorder.HdmiRecorder;
import com.mstar.hdmirecorder.HdmiRecorder.OnErrorListener;
import com.mstar.hdmirecorder.HdmiRecorder.OnInfoListener;
import com.zidoo.hdmi.recorder.R;
import com.zidoo.recorder.service.RecorderService;
import com.zidoo.recorder.tool.FileManagerTool.ScanUSBOnListener;

*//**
 * 
 * 
 * @author jiangbo
 * 
 *         2014-9-5
 * 
 *//*
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ZidooTool {

	// 预约对象
	private RecorderInfo mYuYueRecorderInfo = null;

	public static final String DATA_OBJECT = "recorderInfo.out";

	private Context mContext = null;
	private HdmiRecorder mHdmiRecorder = null;
	private boolean mRecording = false;
	// 标题右边
	private ArrayList<SoftInfo> apk_List = new ArrayList<SoftInfo>();

	// data
	private ArrayList<RecorderInfo> recorderInfo_list = new ArrayList<RecorderInfo>();
	private RecorderInfo mRecorderInfo = null;
	// 正在录像
	private SoftInfo mSoftInfo = null;

	*//**
	 * Usb工具类
	 * 
	 *//*
	private FileManagerTool mFileManagerTool = null;

	public ZidooTool(Context mContext) {
		super();
		this.mContext = mContext;
		mHdmiRecorder = new HdmiRecorder();
		mRecorderInfo = new RecorderInfo();
		initView();
	}

	private void initDialogView() {
		initData();
	}

	private boolean startReFile() {
		try {
			mRecorderInfo.rootPath = pRoot + ROOTPATH_STRING;
			System.out.println("bob mRecorderInfo.rootPath=="
					+ mRecorderInfo.rootPath);
			File rootFile = new File(mRecorderInfo.rootPath);
			if (rootFile == null || !rootFile.exists()) {
				if (!rootFile.mkdirs()) {
					mHandler.sendMessage(mHandler.obtainMessage(5,
							"create record fie failed!"));
					return false;
				}

				rootFile.setReadable(true, false);
				rootFile.setWritable(true, false);
			}

		} catch (Exception e) {
			mHandler.sendMessage(mHandler.obtainMessage(5,
					"create record fie failed!"));
			return false;
		}

		try {
			String title = getTitle();
			mRecorderInfo.name = title;
			String path = mRecorderInfo.rootPath + "/" + title;
			mRecorderInfo.path = path;
			File file = new File(path);
			file.createNewFile();
			file.setReadable(true, false);
			file.setWritable(true, false);
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendMessage(mHandler.obtainMessage(5,
					"create record fie failed!"));
			return false;
		}
		return true;
	}

	private void initView() {
		initDialogView();
	}



	int recordFlag = 0;

	public void strartRecordHDMI(final RecorderInfo mRecorderInfo) {
		recordFlag++;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int pFlag = recordFlag;
					String path = mRecorderInfo.path;
					File file = new File(path);
					mHdmiRecorder.set_output_file_path(path);
					mHdmiRecorder.set_output_format(mRecorderInfo.format);
					mHdmiRecorder.set_video_HD(mRecorderInfo.resolution);
					mHdmiRecorder.native_set_video_encoder_bitrate(1000000);
					mHdmiRecorder.native_set_video_framerate(30);
					mHdmiRecorder.native_set_video_travelingMode(4);
					mHdmiRecorder.native_set_video_subSource(23);
					mHdmiRecorder.setOnErrorListener(moErrorListener);
					mHdmiRecorder.setOnInfoListener(mOnInfoListener);
					boolean ret = mHdmiRecorder.start();
					if (!ret && pFlag == recordFlag) {
						mRecording = true;
					} else {
						mRecording = false;
						mHandler.sendMessage(mHandler.obtainMessage(5, "error"));
						file.delete();
					}
					System.out.println("bob---bRecording===" + mRecording);
				} catch (Exception e) {
					return;
				}
			}
		}).start();
	}

	public void stopRecordHDMI() {
		canleCountTask();
		if (null != mHdmiRecorder && mRecording) {
			mHdmiRecorder.native_stop();
		}
		mRecording = false;
	}

	HdmiRecorder.OnInfoListener mOnInfoListener = new OnInfoListener() {

		@Override
		public void onInfo(com.mstar.hdmirecorder.HdmiRecorder mr, int what,
				int extra) {
			if (what == HdmiRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
				stopRecordHDMI();
				Toast.makeText(mContext, "Max duration reached",
						Toast.LENGTH_LONG).show();
			} else if (what == HdmiRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
				stopRecordHDMI();
				// Show the toast.
				Toast.makeText(mContext, "Insufficient storage space",
						Toast.LENGTH_LONG).show();
			}

		}
	};

	HdmiRecorder.OnErrorListener moErrorListener = new OnErrorListener() {

		@Override
		public void onError(com.mstar.hdmirecorder.HdmiRecorder mr, int what,
				int extra) {
			System.out.println("bob--what-===" + what);
			stopRecordHDMI();
			Toast.makeText(mContext,
					"error occur when recording what-=== " + what,
					Toast.LENGTH_LONG).show();
			if (what == HdmiRecorder.MEDIA_RECORDER_ERROR_UNKNOWN) {
				// We may have run out of space on the sdcard.
				// Show the toast.
				Toast.makeText(mContext, "Insufficient storage space",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	private Timer countTimer = new Timer();

	private TimerTask countTimerTask = null;

	private void canleCountTask() {
		if (countTimerTask != null) {
			countTimerTask.cancel();
			countTimerTask = null;
		}
		countTime = 0;
	}

	private int countTime = 0;

	private void startCount() {
		canleCountTask();
		countTimerTask = new TimerTask() {
			public void run() {
				countTime++;
				if (mRecorderInfo.recorderTime > 0) {
					if (countTime >= mRecorderInfo.recorderTime) {
						mHandler.sendEmptyMessage(COUNTTIMEOVER);
					}
				}
				mHandler.sendEmptyMessage(PRECORDERTIME);
			}
		};
		countTimer.schedule(countTimerTask, 0, 1 * 1000);
	}

	final static int COUNTTIMEOVER = 0;
	final static int PRECORDERTIME = -1;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COUNTTIMEOVER:

				break;
			case PRECORDERTIME:

				break;

			default:
				break;
			}
		};
	};

	private String reTime(long size) {
		long lastSize = size - 10 * 1024 * 1024;
		if (lastSize > 0) {
			float hs = lastSize * 16000.0f / (2 * 1024 * 1024);
			return getVideoTime((long) hs);
		} else {
			return "0s";
		}
	}

	private String getVideoTime(long time) {
		int hour = 0;
		int min = 0;
		int ss = 0;
		hour = (int) (time / (1 * 60 * 60 * 1000));
		int hour_1 = (int) (time % (1 * 60 * 60 * 1000));
		if (hour_1 > 0) {
			min = (int) (hour_1 / (1 * 60 * 1000));
			int min_1 = (int) (hour_1 % (1 * 60 * 1000));
			if (min_1 > 0) {
				ss = (int) (min_1 / (1 * 1000));
			}
		}
		String msg = "";
		if (hour < 10) {
			msg = "0" + hour;
		} else {
			msg = "" + hour;
		}
		if (min < 10) {
			msg = msg + "：0" + min;
		} else {
			msg = msg + "：" + min;
		}
		if (ss < 10) {
			msg = msg + "：0" + ss;
		} else {
			msg = msg + "：" + ss;
		}
		return msg;
	}

	private String getVideoTimeFormat(long time) {
		int hour = 0;
		int min = 0;
		int ss = 0;
		hour = (int) (time / (1 * 60 * 60 * 1000));
		int hour_1 = (int) (time % (1 * 60 * 60 * 1000));
		if (hour_1 > 0) {
			min = (int) (hour_1 / (1 * 60 * 1000));
			int min_1 = (int) (hour_1 % (1 * 60 * 1000));
			if (min_1 > 0) {
				ss = (int) (min_1 / (1 * 1000));
			}
		}
		String msg = "";
		if (hour > 0) {
			msg = hour + "h";
		}
		if (min > 0) {
			msg = msg + min + "m";
		}
		if (ss > 0) {
			msg = msg + ss + "s";
		}
		return msg;
	}

	*//**
	 * 初始化USB接口
	 * 
	 * @author jiangbo 2014-7-29
	 *//*
	private void initData() {
		apk_List.clear();
		recorderInfo_list.clear();
		RecorderInfo mainDataInfo = getLocalObject(mContext, DATA_OBJECT);
		if (mainDataInfo != null) {
			recorderInfo_list.add(mainDataInfo);
		}
		mFileManagerTool = new FileManagerTool(mContext);
		mFileManagerTool.setmScanUSBOnListener(mScanUSBOnListener);
		mFileManagerTool.startUSB();
	}

	*//**
	 * 
	 * usb的监听
	 * 
	 *//*
	ScanUSBOnListener mScanUSBOnListener = new ScanUSBOnListener() {

		@Override
		public void onStarScan() {
		}

		@Override
		public void onExitUSB(final String path) {
			System.out.println("bob usb == onExitUSB==" + path);
			new Thread(new Runnable() {
				public void run() {
					// 去掉数据
					try {
						int size = apk_List.size();
						ArrayList<SoftInfo> remove_List = new ArrayList<SoftInfo>();
						for (int j = 0; j < size; j++) {
							SoftInfo softInfo = apk_List.get(j);
							if (!softInfo.getPathString().contains(
									path + ROOTPATH_STRING)) {
								remove_List.add(softInfo);
							}
						}
						apk_List = remove_List;
						mHandler.sendEmptyMessage(4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(1);
					mHandler.sendEmptyMessage(7);
					if (path.equals(pRoot)) {
						mHandler.sendMessage(mHandler.obtainMessage(9, 2, 1));
					} else {
						mHandler.sendMessage(mHandler.obtainMessage(9, 2, 0));
					}
				}
			}).start();
		}

		@Override
		public void onEndScan() {
			int size = DeviceManager.deviceFileInfo_list.size();
			System.out.println("bob usb ==size  " + size);
			for (int i = 0; i < size; i++) {
				System.out.println("bob usb == "
						+ DeviceManager.deviceFileInfo_list.get(i));
				getAPKFile(DeviceManager.deviceFileInfo_list.get(i));
			}
			mHandler.sendEmptyMessage(1);
			mHandler.sendEmptyMessage(7);
			mHandler.sendMessage(mHandler.obtainMessage(9, 0, 0));
		}

		@Override
		public void onAddUSB(final String path) {
			System.out.println("bob usb == onAddUSB==" + path);
			getAPKFile(path);
			mHandler.sendEmptyMessage(1);
			mHandler.sendEmptyMessage(7);

			mHandler.sendMessage(mHandler.obtainMessage(9, 1, 0));

		}
	};

	private void getAPKFile(String path) {
		try {
			System.out.println("bob--jjjjj==" + path + ROOTPATH_STRING);
			File file = new File(path + ROOTPATH_STRING);
			if (file != null && file.exists() && file.isDirectory()
					&& file.canRead()) {
				File[] file_list = file.listFiles();
				if (file_list != null) {
					int file_size = file_list.length;
					ArrayList<SoftInfo> softInfo_list = new ArrayList<SoftInfo>();
					for (int i = 0; i < file_size; i++) {
						File file_list_file = file_list[i];
						String installPath = file_list_file.getAbsolutePath();
						if (isRecorderFile(installPath)) {
							try {
								SoftInfo softInfo = new SoftInfo();
								softInfo.setPathString(installPath);
								softInfo.setNameString(file_list_file.getName());
								long length = file_list_file.length();
								if (length < 0) {
									length = 0;
								}
								softInfo.setTotal_Size(FileOperate
										.toSize(length));
								try {
									MediaPlayer player = MediaPlayer.create(
											mContext, Uri.parse(installPath));
									if (player == null
											|| player.getDuration() <= 0) {
										file_list_file.delete();
										continue;
									} else {
										softInfo.setPlayTime(getVideoTimeFormat(player
												.getDuration()));
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

								// mHandler.sendMessage(mHandler.obtainMessage(3,
								// softInfo));
								softInfo_list.add(softInfo);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					apk_List.addAll(softInfo_list);
					mHandler.sendEmptyMessage(3);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean isRecorderFile(String path) {
		try {
			String ext = path.substring(path.lastIndexOf(".") + 1);
			if (ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("ts")) {
				if (path.contains("video")) {
					return true;
				}
			}
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public void onDestroy() {
		try {
			canleCountTask();
			mFileManagerTool.onDestroy();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	*//**
	 * 
	 * Toast信息
	 * 
	 * @author jiangbo
	 * @param context
	 * @param msg
	 * @param xOffset
	 * @param yOffset
	 * @param isHitPosition
	 *            2013-12-17
	 *//*
	public void Toast_MSG(Context context, String msg, int xOffset,
			int yOffset, boolean isHitPosition) {
		Toast toa = new Toast(context);
		// if (!isHitPosition) {
		// toa.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
		// xOffset,
		// yOffset);
		// }
		toa.setDuration(Toast.LENGTH_SHORT);
		View tView = LayoutInflater.from(context).inflate(R.layout.msg_toast,
				null);
		TextView tvmsg = (TextView) tView.findViewById(R.id.msg_toast_text);
		tvmsg.setText(msg);
		toa.setView(tView);
		toa.show();
	}

	*//**
	 * 设置本地对象
	 * 
	 * @author bob 2013-3-26
	 *//*
	public static void setLocalObject(Context context,
			RecorderInfo mainDataInfo, String name) {
		try {
			File file = new File(context.getFilesDir().getAbsolutePath() + "/"
					+ name);
			file.deleteOnExit();
			file.createNewFile();
			ObjectOutputStream oout = new ObjectOutputStream(
					new FileOutputStream(file));
			oout.writeObject(mainDataInfo);
			oout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	*//**
	 * 得到本地对象
	 * 
	 * @author bob 2013-3-26
	 *//*
	public static RecorderInfo getLocalObject(Context context, String name) {
		File file = new File(context.getFilesDir().getAbsolutePath() + "/"
				+ name);
		if (!file.exists()) {
			return null;
		} else {
			RecorderInfo mainDataInfo = null;
			try {
				ObjectInputStream oin;
				oin = new ObjectInputStream(new FileInputStream(file));
				Object newPerson = oin.readObject();
				mainDataInfo = (RecorderInfo) newPerson;
				oin.close();
			} catch (Exception e) {
				mainDataInfo = null;
				e.printStackTrace();
			}
			return mainDataInfo;
		}
	}
}
*/