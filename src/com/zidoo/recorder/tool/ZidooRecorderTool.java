package com.zidoo.recorder.tool;

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
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.zidoo.recorder.time.ChooseTime;
import com.zidoo.recorder.time.ChooseTime.OnSetDateListener;
import com.zidoo.recorder.time.WheelMain;
import com.zidoo.recorder.tool.FileManagerTool.ScanUSBOnListener;

/**
 * 
 * 
 * @author jiangbo
 * 
 *         2014-9-5
 * 
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ZidooRecorderTool {

	public static final String		DATA_OBJECT			= "recorderInfo.out";

	private Context					mContext			= null;
	private HdmiRecorder			mHdmiRecorder		= null;
	public boolean					mRecording			= false;
	private Dialog					mDialog				= null;

	private Dialog					mYunDialog			= null;

	// View
	// 标题
	private Button					mLeftTitleButton	= null;
	private Button					mRightTitleButton	= null;
	private int						mTitleIndex			= 0;
	// 标题右边
	private ArrayList<SoftInfo>		apk_List			= new ArrayList<SoftInfo>();
	private SoftAdatper				mSoftAdatper		= null;
	private ListView				mListView			= null;
	private int						mSelectIndex		= 0;
	private RelativeLayout			mNoData				= null;
	private RelativeLayout			mList_vi_li			= null;
	private RelativeLayout			mList_ritht_li		= null;
	private LinearLayout			mList_Rename		= null;
	private LinearLayout			mList_delete		= null;
	// 标题左边
	private LinearLayout			mLeftTitle			= null;
	private Button					mRecorderBtn		= null;
	private Button					mAppointmentBtn		= null;
	private LinearLayout			mRecorderLi			= null;
	// 本地路径
	private Button					mLocalBtn			= null;
	private TextView				mLocalPath			= null;
	// 分辨率
	private Button					mResolution_hd		= null;
	private Button					mResolution_vga		= null;
	private Button					mResolution_fhd		= null;
	private TextView				mResolution_p		= null;
	// 格式
	private Button					mFormat_mp4			= null;
	private Button					mFormat_ts			= null;
	// 预约时间
	private Button					mOrderCheckBox		= null;
	private int						chechIndex			= 0;
	private Button					mOrderTime			= null;
	// 录像长度
	private Button					mAutoTime			= null;
	private Button					mTecorderTime		= null;

	// 标题右边
	private RelativeLayout			mRithtTitle			= null;
	// 状态显示
	private TextView				mUsbStatuView		= null;
	// toast
	private RelativeLayout			mToastView			= null;

	private TextView				mToastmsgView		= null;
	// data
	private ArrayList<RecorderInfo>	recorderInfo_list	= new ArrayList<RecorderInfo>();
	private RecorderInfo			mRecorderInfo		= null;
	private RecorderInfo			mRecorderInfoIng	= null;
	// 正在录像
	private SoftInfo				mSoftInfo			= null;

	private LayoutInflater			inflater			= null;
	/**
	 * Usb工具类
	 * 
	 */
	private FileManagerTool			mFileManagerTool	= null;

	private HdmiTool				mHdmiTool			= null;

	public ArrayList<String>		deviceFileInfo_size	= new ArrayList<String>();

	// 默认记录上一次保存
	private final static String		RESOLUTION_SHARE	= "resolution_share";

	private final static String		FORMAT_SHARE		= "format_share";

	private boolean					isRecorder			= false;

	public ZidooRecorderTool(Context mContext, HdmiTool mHdmiTool, boolean isRecorder) {
		super();
		// isPlayVideo = false;
		this.mContext = mContext;
		this.mHdmiTool = mHdmiTool;
		this.isRecorder = isRecorder;
		inItBroadCast();
		inflater = LayoutInflater.from(mContext);
		mHdmiRecorder = new HdmiRecorder();
		mRecorderInfo = new RecorderInfo();

		initView(mContext);
		initData();
		// startYuyue();
	}

	public void setResolution(String currcuResolution) {

		mResolution_p.setText(currcuResolution);

	}

	private void initDialogView(View view) {
		mTitleIndex = 0;

		mToastView = (RelativeLayout) view.findViewById(R.id.dialog_toast_view_re);
		mToastmsgView = (TextView) view.findViewById(R.id.msg_toast_text);

		mLeftTitleButton = (Button) view.findViewById(R.id.dialog_view_left_title);
		mRightTitleButton = (Button) view.findViewById(R.id.dialog_view_right_title);
		mLeftTitleButton.setOnClickListener(mOnClickListener);
		mRightTitleButton.setOnClickListener(mOnClickListener);
		mLeftTitle = (LinearLayout) view.findViewById(R.id.dialog_view_left_li);
		mRecorderBtn = (Button) view.findViewById(R.id.dialog_view_recorder_btn);
		mAppointmentBtn = (Button) view.findViewById(R.id.dialog_view_appointment_btn);
		mRecorderLi = (LinearLayout) view.findViewById(R.id.dialog_view_list_li);
		mUsbStatuView = (TextView) view.findViewById(R.id.dialog_view_statu);

		mLocalBtn = (Button) view.findViewById(R.id.dialog_view_local_path_btn);
		mLocalPath = (TextView) view.findViewById(R.id.dialog_view_local_path_text);
		mResolution_hd = (Button) view.findViewById(R.id.dialog_view_re_hd_btn);
		mResolution_vga = (Button) view.findViewById(R.id.dialog_view_re_vga_btn);
		mResolution_fhd = (Button) view.findViewById(R.id.dialog_view_re_fhd_btn);
		mResolution_p = (TextView) view.findViewById(R.id.dialog_view_resolution_p);
		mFormat_mp4 = (Button) view.findViewById(R.id.dialog_view_format_mp4_btn);
		mFormat_ts = (Button) view.findViewById(R.id.dialog_view_format_ts_btn);
		mOrderTime = (Button) view.findViewById(R.id.dialog_view_appointment_year_btn);
		mTecorderTime = (Button) view.findViewById(R.id.dialog_view_length_h_btn);
		mAutoTime = (Button) view.findViewById(R.id.dialog_view_length_auto_btn);
		mAutoTime.setTag(1);
		mOrderCheckBox = (Button) view.findViewById(R.id.dialog_view_isport);
		mOrderTime.setFocusable(false);
		mOrderTime.setEnabled(false);
		mLocalBtn.setOnClickListener(mOnClickListener);
		mResolution_hd.setOnClickListener(mOnClickListener);
		mResolution_vga.setOnClickListener(mOnClickListener);
		mResolution_fhd.setOnClickListener(mOnClickListener);
		mFormat_mp4.setOnClickListener(mOnClickListener);
		mFormat_ts.setOnClickListener(mOnClickListener);

		int resolution_share = SharedPrefsUtil.getValue(mContext, RESOLUTION_SHARE, 1);

		int format_share = SharedPrefsUtil.getValue(mContext, FORMAT_SHARE, 1);

		if (resolution_share == 0) {
			mResolution_hd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mResolution_vga.setBackgroundResource(R.drawable.format_certer_select_btn_c);
			mResolution_fhd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mRecorderInfo.resolution = ZidooRecorderTool.VIDEO_VGA;
		} else if (resolution_share == 1) {
			mResolution_hd.setBackgroundResource(R.drawable.format_certer_select_btn_c);
			mResolution_vga.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mResolution_fhd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mRecorderInfo.resolution = ZidooRecorderTool.VIDEO_HD;
		} else if (resolution_share == 2) {
			mResolution_hd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mResolution_vga.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mResolution_fhd.setBackgroundResource(R.drawable.format_certer_select_btn_c);
			mRecorderInfo.resolution = ZidooRecorderTool.VIDEO_FHD;
		}

		if (format_share == 0) {
			mFormat_mp4.setBackgroundResource(R.drawable.format_certer_select_btn_c);
			mFormat_ts.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mRecorderInfo.format = HdmiRecorder.FORMAT_MP4;
		} else if (format_share == 1) {
			mFormat_mp4.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
			mFormat_ts.setBackgroundResource(R.drawable.format_certer_select_btn_c);
			mRecorderInfo.format = HdmiRecorder.FORMAT_TS;
		}

		mOrderTime.setOnClickListener(mOnClickListener);
		mTecorderTime.setOnClickListener(mOnClickListener);
		mAutoTime.setOnClickListener(mOnClickListener);
		mOrderCheckBox.setOnClickListener(mOnClickListener);
		chechIndex = 0;
		mOrderCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (chechIndex == 0) {
					chechIndex = 1;
					mOrderTime.setFocusable(true);
					mOrderTime.setBackgroundResource(R.drawable.year_time_right_select_btn_c);
					mOrderCheckBox.setBackgroundResource(R.drawable.chek_right_select_btn_c);
					mOrderTime.setEnabled(true);
				} else {
					chechIndex = 0;
					mOrderTime.setEnabled(false);
					mOrderTime.setFocusable(false);
					mOrderTime.setBackground(null);
					mOrderCheckBox.setBackgroundResource(R.drawable.chek_right_nomal_btn_c);
				}
			}
		});
		mRecorderBtn.setOnClickListener(mOnClickListener);
		mAppointmentBtn.setOnClickListener(mOnClickListener);

		// 右边部分
		mRithtTitle = (RelativeLayout) view.findViewById(R.id.dialog_view_right_li);
		mListView = (ListView) view.findViewById(R.id.home_ac_leftlistview);
		mList_vi_li = (RelativeLayout) view.findViewById(R.id.home_ac_leftlistview_li);
		mList_ritht_li = (RelativeLayout) view.findViewById(R.id.home_ac_leftlistview_left);
		mList_Rename = (LinearLayout) view.findViewById(R.id.home_ac_leftlistview_left_0);
		mList_delete = (LinearLayout) view.findViewById(R.id.home_ac_leftlistview_left_1);
		mList_Rename.setOnClickListener(mOnClickListener);
		mList_delete.setOnClickListener(mOnClickListener);
		mNoData = (RelativeLayout) view.findViewById(R.id.home_ac_notdata_icon);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SoftInfo softInfo = apk_List.get(position);
				System.out.println("bob--onclik==" + softInfo.getPathString());
				if (!mRecording) {
					playVideo(softInfo.getPathString());
				} else {
					Toast_MSG(mContext, mContext.getString(R.string.statu_reing_net_open), 0, -10, false);
				}
				// if (softInfo.isRecorder()) {
				// Toast_MSG(mContext,
				// mContext.getString(R.string.statu_reing_net_open),
				// 0, -10, false);
				// } else {
				// playVideo(softInfo.getPathString());
				// }
			}
		});
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSelectIndex = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		setView();
	}

	TextView	mStatuView	= null;
	Button		mDelete		= null;

	private void setListView() {
		int size = recorderInfo_list.size();
		mRecorderLi.removeAllViews();
		mStatuView = null;
		for (int i = 0; i < size; i++) {
			RecorderInfo recorderInfo = recorderInfo_list.get(i);
			final View view = inflater.inflate(R.layout.recorder_list_item_view, null);
			TextView name = (TextView) view.findViewById(R.id.recorder_list_item_view_name);
			TextView path = (TextView) view.findViewById(R.id.recorder_list_item_view_path);
			TextView format = (TextView) view.findViewById(R.id.recorder_list_item_view_fom);
			TextView yuyue_time = (TextView) view.findViewById(R.id.recorder_list_item_view_y_time);
			TextView all_time = (TextView) view.findViewById(R.id.recorder_list_item_view_y_alltime);
			TextView p_time = (TextView) view.findViewById(R.id.recorder_list_item_view_ptime);
			TextView statu = (TextView) view.findViewById(R.id.recorder_list_item_view_statu);
			Button delete = (Button) view.findViewById(R.id.recorder_list_item_view_delete);
			name.setText(recorderInfo.name);
			String path_p = "";
			if (recorderInfo.rootPath.contains("/mnt")) {
				path_p = recorderInfo.rootPath.replace("/mnt", "");
			}
			path.setText(path_p);
			String styles = "";
			if (recorderInfo.format == HdmiRecorder.FORMAT_MP4) {
				styles = "MP4/";
			} else {
				styles = "TS/";
			}
			if (recorderInfo.resolution == ZidooRecorderTool.VIDEO_HD) {
				styles = styles + "HD";
			} else if (recorderInfo.resolution == ZidooRecorderTool.VIDEO_VGA) {
				styles = styles + "VGA";
			} else {
				styles = styles + "FHD";
			}
			format.setText(styles);
			if (recorderInfo.statu == 1) {
				if (mRecording) {
					mStatuView = p_time;
					statu.setText(mContext.getText(R.string.statu_reing));
					statu.setTextColor(Color.parseColor("#6da327"));
				} else {
					recorderInfo.statu = 0;
					mStatuView = p_time;
					statu.setText(mContext.getText(R.string.statu_stop));
				}
			} else if (recorderInfo.statu == 2) {
				statu.setText(mContext.getText(R.string.statu_waite));
				delete.setVisibility(View.VISIBLE);
				mDelete = delete;
				final int c = i;
				delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteYuyue(view, c);

					}
				});
			} else {
				delete.setVisibility(View.GONE);
			}
			all_time.setText(recorderInfo.recorderTime_str);
			yuyue_time.setText(recorderInfo.order_time);
			mRecorderLi.addView(view);
			System.out.println("bob   " + recorderInfo.toString());
		}

	}

	/**
	 * 给内存文件修改权限
	 */
	public static void execMethod(String path) {

		String args[] = new String[3];
		args[0] = "chmod";
		args[1] = "777";
		args[2] = path;
		try {
			System.out.println("path======");
			Runtime.getRuntime().exec(args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private boolean startReFile(boolean isYueyue) {
		try {
			mRecorderInfo.rootPath = pRoot + ROOTPATH_STRING;
			System.out.println("bob mRecorderInfo.rootPath==" + mRecorderInfo.rootPath);
			File rootFile = new File(mRecorderInfo.rootPath);
			if (rootFile == null || !rootFile.exists()) {
				if (!rootFile.mkdirs()) {
					mHandler.sendMessage(mHandler.obtainMessage(5, "create record fie failed!"));
					return false;
				}

				rootFile.setReadable(true, false);
				rootFile.setWritable(true, false);

				execMethod(mRecorderInfo.rootPath);

				
				
			}

		} catch (Exception e) {
			mHandler.sendMessage(mHandler.obtainMessage(5, "create record fie failed!"));
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
			execMethod(path);
			long availableSize = FileOperate.getAvailableSize(path);
			System.out.println("bob --start availableSize = " +FileOperate.toSize(availableSize));
			if (availableSize <= 50 * FileOperate.mb) {
				Toast_MSG(mContext, "Insufficient storage space");
				file.delete();
				return false;
			}
			if (isYueyue) {
				file.delete();
			}
			Object object = mAutoTime.getTag();
			if (object != null) {
				int tag = (Integer) object;
				if (tag == 1) {
					mRecorderInfo.recorderTime = -1;
					mRecorderInfo.recorderTime_str = mContext.getString(R.string.re_time_auto);
				} else {
					mRecorderInfo.recorderTime_str = mTecorderTime.getText().toString();
				}
			} else {
				mRecorderInfo.recorderTime = -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendMessage(mHandler.obtainMessage(5, "create record fie failed!"));
			return false;
		}
		return true;
	}

	private void setAvailableRecorder(RecorderInfo recorderInfo) {
		System.out.println("bob  6");
		if (HdmiTool.isShowHDMI) {
			try {
				System.out.println("bob start.path = " + recorderInfo.path);
				String filePath = recorderInfo.path;
				String ext = filePath.substring(filePath.lastIndexOf("."), filePath.length());
				String name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
				String temp = filePath.substring(0, filePath.lastIndexOf("/"));
				String path = "";
				int conut = 1;

				if (name.contains("_")) {
					System.out.println("bob name = " + name);
					String countA = name.substring(name.lastIndexOf("_") + 1, name.length());
					System.out.println("bob countA = " + countA);
					conut = Integer.valueOf(countA);
					conut++;
					name = name.substring(0, name.lastIndexOf("_"));
				}
				path = temp + "/" + name + "_" + conut + ext;
				System.out.println("bob end.path = " + path);
				File file = new File(path);
				file.createNewFile();
				file.setReadable(true, false);
				file.setWritable(true, false);
				execMethod(path);
				recorderInfo.path = path;
				recorderInfo.name = file.getName();
				strartRecordHDMI(recorderInfo, false);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	View.OnClickListener	mOnClickListener	= new OnClickListener() {

													@Override
													public void onClick(View v) {
														switch (v.getId()) {
														case R.id.dialog_view_recorder_btn: {
															if (HdmiTool.isShowHDMI) {
																if (mRecording) {
																	stopRecordHDMI();
																} else {
																	if (startReFile(false)) {
																		strartRecordHDMI(mRecorderInfo, false);
																	}
																}
															} else {
																Toast_MSG(mContext, mContext.getString(R.string.no_infor_msg), 0, 0, true);
															}
														}
															break;
														case R.id.dialog_view_appointment_btn: {
															if (!HdmiTool.isShowHDMI) {
																Toast_MSG(mContext, mContext.getString(R.string.no_infor_msg), 0, 0, true);
																return;
															}
															if (startReFile(true)) {
																if (mOrderTime.isFocusable()) {
																	mRecorderInfo.order_time = mOrderTime.getText().toString();
																	if (WheelMain.compareTime(ChooseTime.dataMsg)) {

																	} else {
																		try {
																			File file = new File(mRecorderInfo.path);
																			file.delete();
																		} catch (Exception e) {
																			// TODO:
																			// handle
																			// exception
																		}
																		mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.appoint_above)));
																		return;
																	}
																	// if
																	// (mRecorderInfo.order_time_ms
																	// >
																	// WheelMain.getPtime(context))
																	// {
																	//
																	// }
																	// mRecorderInfo.order_time_ms;
																} else {
																	try {
																		File file = new File(mRecorderInfo.path);
																		file.delete();
																	} catch (Exception e) {
																		// TODO:
																		// handle
																		// exception
																	}
																	mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.choose_appoint)));
																	return;
																}

																int size = recorderInfo_list.size();
																for (int i = 0; i < size; i++) {
																	if (recorderInfo_list.get(i).statu == 2) {
																		recorderInfo_list.remove(i);
																		break;
																	}
																}
																try {
																	if (RecorderService.mYuYueRecorderInfo != null) {
																		File file = new File(RecorderService.mYuYueRecorderInfo.path);
																		file.delete();
																	}
																} catch (Exception e) {
																	e.printStackTrace();
																}
																RecorderInfo recorderInfo = new RecorderInfo();
																rePeatInfo(recorderInfo, mRecorderInfo);
																recorderInfo.statu = 2;
																RecorderService.mYuYueRecorderInfo = recorderInfo;
																RecorderService.mYuYueRecorderInfo_last = recorderInfo;
																recorderInfo_list.add(recorderInfo);
																System.out.println("bob----recorderInfo_list  add   btn");
																setListView();
																setLocalObject(mContext, RecorderService.mYuYueRecorderInfo, DATA_OBJECT);
															}
														}
															break;
														case R.id.dialog_view_left_title: {
															if (mTitleIndex != 0) {
																// if
																// (HdmiTool.isShowHDMI)
																// {
																mTitleIndex = 0;
																mLeftTitle.setVisibility(View.VISIBLE);
																mRithtTitle.setVisibility(View.GONE);
																mLeftTitleButton.setBackgroundResource(R.drawable.btn_one_c);
																mRightTitleButton.setBackgroundResource(R.drawable.btn_list_c);
																// } else {
																// Toast_MSG(mContext,
																// mContext.getString(R.string.no_infor),
																// 0, 0,
																// true);
																// }
															}
														}
															break;
														case R.id.dialog_view_right_title: {
															if (mTitleIndex != 1) {
																if (mRecording) {
																	Toast_MSG(mContext, mContext.getString(R.string.statu_reing_net_edit), 0, 0, true);
																} else {
																	mTitleIndex = 1;
																	mLeftTitleButton.setBackgroundResource(R.drawable.btn_c);
																	mRightTitleButton.setBackgroundResource(R.drawable.btn_one_list_c);

																	mLeftTitle.setVisibility(View.GONE);
																	mRithtTitle.setVisibility(View.VISIBLE);
																	System.out.println("bob---size 8= " + apk_List.size());
																	mSoftAdatper = new SoftAdatper(apk_List, mContext);
																	mListView.setAdapter(mSoftAdatper);
																}
															}
														}
															break;

														// 路径
														case R.id.dialog_view_local_path_btn: {
															try {
																if (rootPathIndex >= 0) {
																	rootPathIndex++;
																	if (rootPathIndex >= DeviceManager.deviceFileInfo_list.size()) {
																		rootPathIndex = 0;
																	}
																	pRoot = DeviceManager.deviceFileInfo_list.get(rootPathIndex);
																	String path_p = "";
																	if (pRoot.contains("/mnt")) {
																		path_p = pRoot.replace("/mnt", "");
																	}
																	if (path_p.contains("/samba")) {
																		path_p = path_p.replace("/samba", "");
																	}
																	mLocalPath.setText(path_p + ROOTPATH_STRING);
																}
															} catch (Exception e) {
																e.printStackTrace();
															}

														}
															break;

														// vga
														case R.id.dialog_view_re_vga_btn: {
															SharedPrefsUtil.putValue(mContext, RESOLUTION_SHARE, 0);
															mResolution_hd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mResolution_vga.setBackgroundResource(R.drawable.format_certer_select_btn_c);
															mResolution_fhd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mRecorderInfo.resolution = ZidooRecorderTool.VIDEO_VGA;
														}
															break;
														// HD
														case R.id.dialog_view_re_hd_btn: {
															SharedPrefsUtil.putValue(mContext, RESOLUTION_SHARE, 1);
															mResolution_hd.setBackgroundResource(R.drawable.format_certer_select_btn_c);
															mResolution_vga.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mResolution_fhd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mRecorderInfo.resolution = ZidooRecorderTool.VIDEO_HD;
														}
															break;
														// fhd
														case R.id.dialog_view_re_fhd_btn: {
															SharedPrefsUtil.putValue(mContext, RESOLUTION_SHARE, 2);
															mResolution_hd.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mResolution_vga.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mResolution_fhd.setBackgroundResource(R.drawable.format_certer_select_btn_c);
															mRecorderInfo.resolution = ZidooRecorderTool.VIDEO_FHD;
														}
															break;
														// mp4
														case R.id.dialog_view_format_mp4_btn: {
															SharedPrefsUtil.putValue(mContext, FORMAT_SHARE, 0);
															mFormat_mp4.setBackgroundResource(R.drawable.format_certer_select_btn_c);
															mFormat_ts.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mRecorderInfo.format = HdmiRecorder.FORMAT_MP4;
														}
															break;
														// ts
														case R.id.dialog_view_format_ts_btn: {
															SharedPrefsUtil.putValue(mContext, FORMAT_SHARE, 1);
															mFormat_mp4.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mFormat_ts.setBackgroundResource(R.drawable.format_certer_select_btn_c);
															mRecorderInfo.format = HdmiRecorder.FORMAT_TS;
														}
															break;
														// appointment
														case R.id.dialog_view_appointment_year_btn: {

															new ChooseTime(ZidooRecorderTool.this, mContext, true, "", new OnSetDateListener() {

																@Override
																public void setDate(String dataMsg, long ms) {
																	System.out.println("bob---setDate---" + dataMsg);
																	mOrderTime.setText(dataMsg);
																	mRecorderInfo.order_time_ms = ms;
																}
															}, "");

															// // ���ö���
															// //
															// window.setWindowAnimations(R.style.dialogViewAnim);
															// // ����
															// //
															// window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_back));
															// //
															// WindowManager.LayoutParams
															// lp =
															// window.getAttributes();
															// // lp.width =
															// WindowManager.LayoutParams.MATCH_PARENT;
															// // lp.height =
															// WindowManager.LayoutParams.MATCH_PARENT;
															// window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
															// dialog.show();
															// mOrderTime.setText("");
														}
															break;
														// auto
														case R.id.dialog_view_length_auto_btn: {
															mAutoTime.setBackgroundResource(R.drawable.format_certer_select_btn_c);
															mAutoTime.setTag(1);
															mTecorderTime.setBackgroundResource(R.drawable.time_right_nomal_btn_c);
														}
															break;
														// length
														case R.id.dialog_view_length_h_btn: {
															mTecorderTime.setBackgroundResource(R.drawable.time_right_select_btn_c);
															mAutoTime.setBackgroundResource(R.drawable.format_certer_nomal_btn_c);
															mAutoTime.setTag(0);
															String reSize = "";
															try {
																reSize = deviceFileInfo_size.get(rootPathIndex);
															} catch (Exception e) {
																reSize = "";
															}
															new ChooseTime(ZidooRecorderTool.this, mContext, false, reSize, new OnSetDateListener() {

																@Override
																public void setDate(String dataMsg, long ms) {
																	System.out.println("bob---setDate---" + dataMsg);
																	System.out.println("bob---ms---" + ms);
																	mTecorderTime.setText(dataMsg);
																	mRecorderInfo.recorderTime = ms;
																}
															}, mTecorderTime.getText().toString());
														}
															break;
														case R.id.home_ac_leftlistview_left_0: {
															mList_ritht_li.setVisibility(View.GONE);
															mListView.requestFocus();
															reNameDialogInInit(apk_List.get(mSelectIndex));
														}
															break;
														case R.id.home_ac_leftlistview_left_1: {
															mList_ritht_li.setVisibility(View.GONE);
															mListView.requestFocus();
															deleteDialogInInit(mContext.getString(R.string.delete_file), mContext.getString(R.string.delete_ok),
																	apk_List.get(mSelectIndex));
														}
															break;

														default:
															break;
														}
													}
												};

	public void initView(Context mContext) {
		this.mContext = mContext;
		SoundTool.initSound(mContext);
		mDialog = new Dialog(mContext, R.style.dialogViewStyle);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_view, null);
		initDialogView(view);
		mDialog.setContentView(view);
		Window window = mDialog.getWindow();
		// ���ö���
		// window.setWindowAnimations(R.style.dialogViewAnim);
		// ����
		// window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_back));
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		// window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mDialog.setOnKeyListener(mOnKeyListener);
		view.setOnHoverListener(mOnTouchListener);
		// setView();

		mHandler.sendEmptyMessage(1);
		mHandler.sendEmptyMessage(7);
		mHandler.sendMessage(mHandler.obtainMessage(9, 0, 0));
		initUsbData();
	}

	public void showDialog() {
		System.out.println("bob --RecorderService.isShowHDMI =  " + HdmiTool.isShowHDMI);
		if (mYunDialog != null && mYunDialog.isShowing()) {
			return;
		}
		if (mDialog != null) {
			if (!mDialog.isShowing()) {
				if (HdmiTool.isShowHDMI) {
					mTitleIndex = 0;
					mLeftTitle.setVisibility(View.VISIBLE);
					mRithtTitle.setVisibility(View.GONE);
					mLeftTitleButton.setBackgroundResource(R.drawable.btn_one_c);
					mRightTitleButton.setBackgroundResource(R.drawable.btn_list_c);
					reCoverData();
					mRecorderBtn.requestFocus();
				} else {
					notHDMI();
				}
				ChooseTime.isDate = true;
				mOrderTime.setText(WheelMain.getPtime());
				mDialog.show();
				startGoneDialog();
			}
		}
	}

	public void notHDMI() {
		if (mTitleIndex != 1) {
			mTitleIndex = 1;
			mLeftTitle.setVisibility(View.GONE);
			mRithtTitle.setVisibility(View.VISIBLE);
			mLeftTitleButton.setBackgroundResource(R.drawable.btn_c);
			mRightTitleButton.setBackgroundResource(R.drawable.btn_one_list_c);
			mRightTitleButton.requestFocus();
		}
	}

	int	recordFlag	= 0;

	public void strartRecordHDMI(final RecorderInfo mRecorderInfo, boolean isyuyue) {
		recordFlag++;
		canleRecord();
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		try {
			int pFlag = recordFlag;
			String path = mRecorderInfo.path;
			File file = new File(path);
			if (isyuyue) {
				file.createNewFile();
				file.setReadable(true, false);
				file.setWritable(true, false);
				execMethod(path);
			}
			mHdmiRecorder.setOnErrorListener(moErrorListener);
			mHdmiRecorder.setOnInfoListener(mOnInfoListener);
			mHdmiRecorder.set_output_file_path(path);
			mHdmiRecorder.set_output_format(mRecorderInfo.format);
			setWH();
			set_video_HD(mRecorderInfo);
			mHdmiRecorder.native_set_video_framerate(30);
			mHdmiRecorder.native_set_video_travelingMode(4);
			mHdmiRecorder.native_set_video_subSource(23);
			boolean ret = mHdmiRecorder.start();
			if (ret && pFlag == recordFlag) {

				SoundTool.ISSOUND = false;

				mRecording = true;
				SoftInfo softInfo = new SoftInfo();
				softInfo.setPathString(path);
				softInfo.setNameString(file.getName());
				softInfo.setRecorder(true);
				mSoftInfo = softInfo;
				apk_List.add(0, softInfo);
				mHandler.sendEmptyMessage(3);
				mHandler.sendEmptyMessage(1);
				// if (mRecorderInfo.recorderTime > 0) {

				// }
				if (isyuyue) {
					mRecorderInfo.statu = 1;
					startCount(mRecorderInfo);
				} else {
					RecorderInfo recorderInfo = new RecorderInfo();
					mRecorderInfo.order_time = "--:--:--";
					rePeatInfo(recorderInfo, mRecorderInfo);
					recorderInfo.statu = 1;
					mRecorderInfo.statu = 1;
					recorderInfo_list.add(0, recorderInfo);
					startCount(recorderInfo);
				}
				mRecorderInfoIng = new RecorderInfo();
				rePeatInfo(mRecorderInfoIng, mRecorderInfo);
				mHandler.sendEmptyMessage(2);
				System.out.println("bob----recorderInfo_list  add   start");
			} else {
				if (isyuyue) {
					if (recorderInfo_list.size() > 0) {
						recorderInfo_list.remove(0);
						mHandler.sendEmptyMessage(2);
					}
				}
				mRecording = false;

				file.delete();
				if (HdmiTool.isShowHDMI) {
					mHandler.sendMessage(mHandler.obtainMessage(5, "error"));
				} else {
					Toast_MSG(mContext, mContext.getString(R.string.no_infor_msg), 0, 0, true);
				}
			}
			System.out.println("bob---bRecording===" + mRecording);
		} catch (Exception e) {
			return;
		}
		// }
		// }).start();
	}

	Timer		reRecord	= new Timer();
	TimerTask	reTimerTask	= null;

	private void canleRecord() {
		if (reTimerTask != null) {
			reTimerTask.cancel();
			reTimerTask = null;
		}
	}

	public void reRecord() {
		if (!mRecording && reTimerTask != null) {
			canleRecord();
			if (startReFile(false)) {
				strartRecordHDMI(mRecorderInfo, false);
			}
		}
	}

	public void stopRecordIng() {
		if (mRecording) {
			stopRecordHDMI();
			canleRecord();
			reTimerTask = new TimerTask() {

				@Override
				public void run() {
					reTimerTask = null;
				}
			};
			reRecord.schedule(reTimerTask, 60 * 1000);
		}
	}

	public void stopRecordHDMI() {
		mRecorderInfoIng = null;
		canleCountTask();
		canleRecord();
		if (null != mHdmiRecorder && mRecording) {
			mHdmiRecorder.native_stop();
		}
		mRecording = false;
		if (mSoftInfo != null) {
			mSoftInfo.setRecorder(false);
			try {
				File file = new File(mSoftInfo.getPathString());
				long length = file.length();
				if (length < 0) {
					length = 0;
				}
				mSoftInfo.setTotal_Size(FileOperate.toSize(length));
				// MediaPlayer player = MediaPlayer.create(mContext,
				// Uri.parse(mSoftInfo.getPathString()));
				// if (player == null) {
				// player = new MediaPlayer();
				// player.setDataSource(mSoftInfo.getPathString());
				// }
				// if (player == null || player.getDuration() <= 0) {
				// file.delete();
				// apk_List.remove(mSoftInfo);
				// } else {
				//
				// mSoftInfo.setPlayTime(getVideoTimeFormat(player
				// .getDuration()));
				// player.pause();
				// player.stop();
				// player.release();
				// player = null;
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(4);
			mHandler.sendEmptyMessage(1);
			mSoftInfo = null;
		}
		int size = recorderInfo_list.size();
		for (int i = 0; i < size; i++) {
			if (recorderInfo_list.get(i).statu == 1) {
				recorderInfo_list.remove(i);
				break;
			}
		}
		setView();
		try {
			SoundTool.initSound(mContext);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	HdmiRecorder.OnInfoListener	mOnInfoListener	= new OnInfoListener() {

													@Override
													public void onInfo(com.mstar.hdmirecorder.HdmiRecorder mr, int what, int extra) {
														System.out.println("bob--mOnInfoListener what-===" + what);
														if (what == HdmiRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
															stopRecordHDMI();
															Toast_MSG(mContext, "Max duration reached");
														} else if (what == HdmiRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
															mHandler.removeMessages(1125);
															mHandler.sendEmptyMessageDelayed(1125, 1 * 1000);
														}

													}
												};

	private boolean storageError() {
		if (mRecorderInfoIng != null) {
			File file = new File(mRecorderInfoIng.path);
			if (file != null && file.exists()) {
				long length = file.length();
				if (length >= 1.8 * FileOperate.gb) {
					long availableSize = FileOperate.getAvailableSize(mRecorderInfoIng.path);
					if (availableSize >= 50 * FileOperate.mb) {
						RecorderInfo recorderInfoIng = mRecorderInfoIng;
						stopRecordHDMI();
						setAvailableRecorder(recorderInfoIng);

						return true;
					}
				}
			}
		}
		return false;
	}

	HdmiRecorder.OnErrorListener	moErrorListener	= new OnErrorListener() {

														@Override
														public void onError(com.mstar.hdmirecorder.HdmiRecorder mr, int what, int extra) {
															System.out.println("bob--moErrorListener what-===" + what);
															stopRecordHDMI();
															// Toast.makeText(mContext,
															// "error occur when recording what-=== "
															// + what,
															// Toast.LENGTH_LONG).show();
															if (what == HdmiRecorder.MEDIA_RECORDER_ERROR_UNKNOWN) {
																// We may have
																// run out of
																// space on the
																// sdcard.
																// Show the
																// toast.
																Toast_MSG(mContext, "Insufficient storage space");
															}
														}
													};
	/**
	 * 
	 * APK保存路径
	 */

	final static String				ROOTPATH_STRING	= "/HdmiRecorder";
	String							pRoot			= "";
	int								rootPathIndex	= -1;

	public void rePeatInfo(RecorderInfo newRecorderInfo, RecorderInfo oldRecorderInfo) {
		newRecorderInfo.rootPath = oldRecorderInfo.rootPath;
		newRecorderInfo.name = oldRecorderInfo.name;
		newRecorderInfo.path = oldRecorderInfo.path;
		newRecorderInfo.resolution = oldRecorderInfo.resolution;
		newRecorderInfo.format = oldRecorderInfo.format;
		newRecorderInfo.recorderTime = oldRecorderInfo.recorderTime;
		newRecorderInfo.recorderTime_str = oldRecorderInfo.recorderTime_str;
		newRecorderInfo.order_time = oldRecorderInfo.order_time;
		newRecorderInfo.order_time_ms = oldRecorderInfo.order_time_ms;
		newRecorderInfo.current_time = oldRecorderInfo.current_time;
		newRecorderInfo.statu = oldRecorderInfo.statu;
	}

	final static int	DISTIIME	= 1 * 1500;

	private void setView() {
		mHandler.removeMessages(55);
		if (mRecording) {
			mHdmiTool.mRecording.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessageDelayed(55, DISTIIME);
			mRecorderBtn.setText(mContext.getString(R.string.stop_recorder));
		} else {
			mHdmiTool.mRecording.setVisibility(View.GONE);
			mRecorderBtn.setText(mContext.getString(R.string.start_recorder));
		}
		setListView();
	}

	private Timer		countTimer		= new Timer();

	private TimerTask	countTimerTask	= null;

	private void canleCountTask() {
		if (countTimerTask != null) {
			countTimerTask.cancel();
			countTimerTask = null;
		}
		countTime = 0;
	}

	private int	countTime	= 0;

	private void startCount(final RecorderInfo mRecorderInfo) {
		canleCountTask();
		System.out.println("bob  mRecorderInfo.recorderTime  = " + mRecorderInfo.recorderTime);
		countTimerTask = new TimerTask() {
			public void run() {
				countTime++;
				if (mRecorderInfo.recorderTime > 0) {
					if (countTime * 1000 >= mRecorderInfo.recorderTime) {
						mHandler.sendEmptyMessage(COUNTTIMEOVER);
					}
				}
				mHandler.sendEmptyMessage(PRECORDERTIME);
				if (countTime % 4 == 1) {
					mHandler.sendEmptyMessage(PRECORDERTIMESIZE);
				}
			}
		};
		countTimer.schedule(countTimerTask, 0, 1 * 1000);
	}

	private String getTitle() {

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sDateFormat.format(new java.util.Date());
		if (mRecorderInfo.format == HdmiRecorder.FORMAT_MP4) {
			date = date + ".mp4";
		} else {
			date = date + ".ts";
		}
		return "video" + date;
	}

	final static int	COUNTTIMEOVER		= 0;
	final static int	PRECORDERTIME		= -1;
	final static int	PRECORDERTIMESIZE	= -9;
	final static int	EXITDIALOGOVER		= 20;
	Handler				mHandler			= new Handler() {
												public void handleMessage(android.os.Message msg) {
													switch (msg.what) {

													case EXITDIALOGOVER:
														if (mDialog != null && mDialog.isShowing()) {
															mDialog.dismiss();
														}
														break;

													// 计数完成
													case COUNTTIMEOVER:
														System.out.println("bob---record_over...");
														Toast_MSG(mContext, mContext.getString(R.string.record_over), 0, -10, false);
														stopRecordHDMI();
														break;
													// 计数
													case PRECORDERTIME:
														if (mStatuView != null) {
															mStatuView.setText(getVideoTime(countTime * 1000));
														}

														break;
													// 计数
													case PRECORDERTIMESIZE:
														try {

															if (mRecorderInfoIng != null) {
																File file = new File(mRecorderInfoIng.path);
																if (file != null && file.exists()) {
																	String fileSize = FileOperate.toSize(file.length());
																	long availableSize = FileOperate.getAvailableSize(mRecorderInfoIng.path);
																	System.out.println("bob -- videosize = " + fileSize+"   availableSize = "+FileOperate.toSize(availableSize));
																	if (availableSize >= 50 * FileOperate.mb && availableSize <= 100 * FileOperate.mb) {
																		Toast_MSG(mContext, "Insufficient storage space 100M");
																	} else if (availableSize <= 50 * FileOperate.mb) {
																		stopRecordHDMI();
																		Toast_MSG(mContext, "Insufficient storage space");
																	}
																}
															}

														} catch (Exception e) {
															e.printStackTrace();
														}
														break;
													case 1:
														// 没有数据的情况
														if (apk_List.size() > 0) {
															mList_vi_li.setVisibility(View.VISIBLE);
															mNoData.setVisibility(View.GONE);
														} else {
															mList_vi_li.setVisibility(View.GONE);
															mNoData.setVisibility(View.VISIBLE);
														}
														break;
													case 2:
														setView();
														break;
													case 3:
														// SoftInfo softInfo =
														// (SoftInfo) msg.obj;
														// System.out.println("bob--"
														// +
														// softInfo.getNameString());
														setData();
														break;
													case 4:
														// if (mSoftAdatper !=
														// null)
														// {
														// mSoftAdatper.notifyDataSetChanged();
														// }
														setData();
														// mSoftAdatper = new
														// SoftAdatper(apk_List,
														// mContext);
														// listView.setAdapter(mSoftAdatper);

														break;
													case 5: {
														String nameString = (String) msg.obj;
														Toast_MSG(mContext, nameString, 0, -10, false);
													}
														// String nameString =
														// (String) msg.obj;
														// showProgress("正在安装  "
														// +
														// nameString);
														break;
													case 25: {
														try {
															if (HdmiTool.isShowHDMI) {
																if (mRecording) {
																} else {
																	if (startReFile(false)) {
																		strartRecordHDMI(mRecorderInfo, false);
																	}
																}
															} else {
																Toast_MSG(mContext, mContext.getString(R.string.no_infor_msg), 0, 0, true);
															}
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
														break;

													case 6:
														try {
															deleteLocalObject(mContext, DATA_OBJECT);
															System.out.println("bob  --  yueyue rele");
															if (mRecording) {
																File file = new File(RecorderService.mYuYueRecorderInfo.path);
																if (file != null && file.exists()) {
																	file.delete();
																}
															} else {
																if (HdmiTool.isShowHDMI) {
																	strartRecordHDMI(RecorderService.mYuYueRecorderInfo, true);
																} else {
																	File file = new File(RecorderService.mYuYueRecorderInfo.path);
																	if (file != null && file.exists()) {
																		file.delete();
																	}
																	Toast_MSG(mContext, mContext.getString(R.string.no_infor), 0, 0, true);
																}
															}
															RecorderService.mYuYueRecorderInfo = null;
															RecorderService.mYuYueRecorderInfo_last = null;
														} catch (Exception e) {
															e.printStackTrace();
														}
														break;
													case 7: {
														new Thread(new Runnable() {
															public void run() {
																int size = DeviceManager.deviceFileInfo_list.size();
																String msgString = mContext.getString(R.string.statu_save_k);
																deviceFileInfo_size.clear();
																if (size > 0) {
																	msgString = "";
																	for (int i = 0; i < size; i++) {
																		String path = DeviceManager.deviceFileInfo_list.get(i);
																		try {
																			long totalSize = FileOperate.getAvailableSize(path);
																		//	if (totalSize > 0) {
																				String availableSize = FileOperate.toSize(totalSize);
																				String reSize = reTime(totalSize);
																				deviceFileInfo_size.add(reSize);
																				msgString = msgString + (i + 1) + " :\t" + path.replace("/mnt", "") + ROOTPATH_STRING + "\n"
																						+ mContext.getString(R.string.av_kj) + availableSize + "\n"
																						+ mContext.getString(R.string.av_re) + reSize + "\n\n";
															//				}
																		} catch (Exception e) {
																			e.printStackTrace();
																		}
																	}
																}
																mHandler.sendMessage(mHandler.obtainMessage(8, msgString));
															}
														}).start();
													}
														break;
													case 8: {
														String nameString = (String) msg.obj;
														mUsbStatuView.setText(nameString);
													}
														break;
													case 9: {
														int size = DeviceManager.deviceFileInfo_list.size();

														if (size <= 1) {
															mLocalBtn.setVisibility(View.GONE);
														} else {
															mLocalBtn.setVisibility(View.VISIBLE);
														}

														switch (msg.arg1) {
														case 0:
															if (size > 0) {

																rootPathIndex = 0;
																pRoot = DeviceManager.deviceFileInfo_list.get(0);
																String path_p = "";
																if (pRoot.contains("/mnt")) {
																	path_p = pRoot.replace("/mnt", "");
																}
																if (path_p.contains("/samba")) {
																	path_p = path_p.replace("/samba", "");
																}

																String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
																for (int m = 0; m < size; m++) {
																	String mmP = DeviceManager.deviceFileInfo_list.get(m);
																	if (!mmP.equals(externalStorageDirectory)) {
																		rootPathIndex = m;
																		pRoot = mmP;
																		if (pRoot.contains("/mnt")) {
																			path_p = pRoot.replace("/mnt", "");
																		}
																		if (path_p.contains("/samba")) {
																			path_p = path_p.replace("/samba", "");
																		}
																		break;
																	}
																}

																mLocalPath.setText(path_p + ROOTPATH_STRING);
															}
															break;
														case 1:
															if (size == 1) {
																pRoot = DeviceManager.deviceFileInfo_list.get(0);
																String path_p = "";
																if (pRoot.contains("/mnt")) {
																	path_p = pRoot.replace("/mnt", "");
																}
																if (path_p.contains("/samba")) {
																	path_p = path_p.replace("/samba", "");
																}
																mLocalPath.setText(path_p + ROOTPATH_STRING);
																rootPathIndex = 0;
															}
															break;
														case 2:
															if (msg.arg2 == 1) {
																if (size > 0) {
																	pRoot = DeviceManager.deviceFileInfo_list.get(0);
																	String path_p = "";
																	if (pRoot.contains("/mnt")) {
																		path_p = pRoot.replace("/mnt", "");
																	}
																	if (path_p.contains("/samba")) {
																		path_p = path_p.replace("/samba", "");
																	}
																	mLocalPath.setText(path_p + ROOTPATH_STRING);
																	rootPathIndex = 0;
																} else {
																	pRoot = "";
																	mLocalPath.setText("");
																	rootPathIndex = -1;
																}
															}

															break;

														default:
															break;
														}

													}
														break;
													case 55: {
														if (mHdmiTool.mRecording.getVisibility() == View.VISIBLE) {
															mHdmiTool.mRecording.setVisibility(View.GONE);
														} else {
															mHdmiTool.mRecording.setVisibility(View.VISIBLE);
														}
														mHandler.sendEmptyMessageDelayed(55, DISTIIME);
													}
														break;
													case 1125: {

														System.out.println("bob--mHandler start 1125");

														if (storageError()) {
															return;
														}
														System.out.println("bob--mHandler end 1125");
														stopRecordHDMI();
														// Show the
														// toast.
														Toast_MSG(mContext, "Insufficient storage space");
													}
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
			msg = msg + ":0" + min;
		} else {
			msg = msg + ":" + min;
		}
		if (ss < 10) {
			msg = msg + ":0" + ss;
		} else {
			msg = msg + ":" + ss;
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

	// private static String getFileLastModifiedTime(long fileModeyTile) {
	// Calendar cal = Calendar.getInstance();
	// SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	// cal.setTimeInMillis(fileModeyTile);
	// return formatter.format(cal.getTime());
	// }

	private void setData() {

		// mSoftAdatper = new SoftAdatper(apk_List, mContext);
		// listView.setAdapter(mSoftAdatper);
		System.out.println("bob---setData");
		System.out.println("bob---size = " + apk_List.size());

		if (mSoftAdatper == null) {
			mSoftAdatper = new SoftAdatper(apk_List, mContext);
			mListView.setAdapter(mSoftAdatper);
		} else {
			mSoftAdatper.notifyDataSetChanged();
		}
	}

	Timer		mExitTimer		= new Timer();
	TimerTask	mExitTimerTask	= null;
	int			mExitTime		= 8 * 1000;

	public void canleDialogTimer() {
		if (mExitTimerTask != null) {
			mExitTimerTask.cancel();
			mExitTimerTask = null;
		}
	}

	public void startGoneDialog() {
		canleDialogTimer();
		mExitTimerTask = new TimerTask() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(EXITDIALOGOVER);
			}
		};
		mExitTimer.schedule(mExitTimerTask, mExitTime);
	}

	OnHoverListener	mOnTouchListener	= new OnHoverListener() {

											@Override
											public boolean onHover(View v, MotionEvent event) {
												startGoneDialog();
												return false;
											}
										};

	OnKeyListener	mOnKeyListener		= new OnKeyListener() {

											@Override
											public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

												if (event.getAction() == KeyEvent.ACTION_DOWN) {

													startGoneDialog();

													SoundTool.soundKey(keyCode);
													switch (keyCode) {
													case KeyEvent.KEYCODE_BACK: {
														if (mList_ritht_li.getVisibility() == View.VISIBLE) {
															mList_ritht_li.setVisibility(View.GONE);
															mListView.requestFocus();
															return true;
														} else {
														//	mHdmiTool.disHintText();
														}
													}
														break;
													case KeyEvent.KEYCODE_MENU: {
														if (mList_ritht_li.getVisibility() == View.VISIBLE) {
															mList_ritht_li.setVisibility(View.GONE);
															mListView.requestFocus();
															return true;
														}
														if (mListView.isFocused()) {
															try {
																if (!apk_List.get(mSelectIndex).isRecorder()) {
																	mList_ritht_li.setVisibility(View.VISIBLE);
																	mList_Rename.requestFocus();
																	return true;
																} else {
																	Toast_MSG(mContext, mContext.getString(R.string.statu_reing_net_edit), 0, -10, false);
																}
															} catch (Exception e) {
																e.printStackTrace();
															}
															return true;
														}
														mDialog.dismiss();
													}
														break;
													case KeyEvent.KEYCODE_DPAD_LEFT: {
														if (mList_Rename.isFocused() || mList_delete.isFocused()) {
															mList_ritht_li.setVisibility(View.GONE);
															mListView.requestFocus();
															return true;
														}
													}
														break;
													case KeyEvent.KEYCODE_DPAD_RIGHT: {
														if ((mRightTitleButton.isFocused() || mLeftTitleButton.isFocused()) && mRithtTitle.getVisibility() == View.VISIBLE) {
															mListView.requestFocus();
															return true;
														}
														// else if
														// (mListView.isFocused())
														// {
														// try {
														// if
														// (!apk_List.get(mSelectIndex).isRecorder())
														// {
														// mList_ritht_li.setVisibility(View.VISIBLE);
														// mList_Rename.requestFocus();
														// return true;
														// } else {
														// Toast_MSG(mContext,
														// mContext.getString(R.string.statu_reing_net_edit),
														// 0, -10, false);
														// }
														// } catch (Exception e)
														// {
														// e.printStackTrace();
														// }
														// }
													}
														break;
													case KeyEvent.KEYCODE_DPAD_UP: {
														if (mList_Rename.isFocused()) {
															return true;
														}
														// if (mRecording &&
														// mRecorderBtn.isFocused())
														// {
														// return true;
														// }
													}
														break;
													case KeyEvent.KEYCODE_DPAD_DOWN: {
														if (mList_delete.isFocused()) {
															return true;
														}
														if (mDelete != null && (mAppointmentBtn.isFocused() || mRecorderBtn.isFocused())) {
															mDelete.requestFocus();
															return true;
														}
													}
														break;
													// case KeyEvent.KEYCODE_0:
													// {
													// storageError();
													// }
													// break;
													default:
														break;
													}
												}

												return false;
											}
										};

	/**
	 * 初始化USB接口
	 * 
	 * @author jiangbo 2014-7-29
	 */
	private void initData() {

		recorderInfo_list.clear();
		if (RecorderService.mYuYueRecorderInfo != null) {
			recorderInfo_list.add(RecorderService.mYuYueRecorderInfo);
			setView();
		} else {
			RecorderInfo mainDataInfo = getLocalObject(mContext, DATA_OBJECT);
			if (mainDataInfo != null) {
				RecorderService.mYuYueRecorderInfo = mainDataInfo;
				RecorderService.mYuYueRecorderInfo_last = mainDataInfo;
				recorderInfo_list.add(mainDataInfo);
				setView();
			}
		}
		if (RecorderService.isYUYUETimeOK && RecorderService.mYuYueRecorderInfo != null) {
			mHandler.sendEmptyMessageDelayed(6, 4 * 1000);
			mYunDialog = startDialog(mContext);
		}

		if (isRecorder) {
			mHandler.sendEmptyMessageDelayed(25, 3 * 1000);
		}

		RecorderService.isYUYUETimeOK = false;
		// mFileManagerTool = new FileManagerTool(mContext);
		// mFileManagerTool.setmScanUSBOnListener(mScanUSBOnListener);
		// rootPathIndex = -1;
		// mFileManagerTool.startUSB();
	}

	private void initUsbData() {
		apk_List.clear();
		mFileManagerTool = new FileManagerTool(mContext);
		mFileManagerTool.setmScanUSBOnListener(mScanUSBOnListener);
		rootPathIndex = -1;
		mFileManagerTool.startUSB();
	}

	private void inItBroadCast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("yuyuerecording.aciton");
		filter.addAction("yuyuerecording.stop.aciton");
		mContext.registerReceiver(mReceiver, filter);
	}

	/* 增加热插拔功能 */
	BroadcastReceiver	mReceiver	= new BroadcastReceiver() {
										@Override
										public void onReceive(Context context, Intent intent) {
											try {
												if (intent.getAction().equals("yuyuerecording.aciton")) {
													deleteLocalObject(mContext, DATA_OBJECT);
													System.out.println("bob  --  yueyue rele");
													if (mRecording) {
														int size = recorderInfo_list.size();
														for (int i = 0; i < size; i++) {
															if (recorderInfo_list.get(i).statu == 2) {
																recorderInfo_list.remove(i);
																break;
															}
														}
														setView();
													} else {
														strartRecordHDMI(RecorderService.mYuYueRecorderInfo, true);
													}
													RecorderService.mYuYueRecorderInfo = null;
													RecorderService.mYuYueRecorderInfo_last = null;
												} else {
													((Activity) mContext).finish();
												}
											} catch (Exception e) {
												e.printStackTrace();
											}

										}
									};

	private void reCoverData() {
		new Thread(new Runnable() {
			public void run() {
				// 去掉数据
				try {
					int size = apk_List.size();
					ArrayList<SoftInfo> remove_List = new ArrayList<SoftInfo>();
					for (int j = 0; j < size; j++) {
						SoftInfo softInfo = apk_List.get(j);
						File file = new File(softInfo.getPathString());
						if (file != null && file.exists()) {
							remove_List.add(softInfo);
						}
					}
					apk_List = remove_List;
					mHandler.sendEmptyMessage(4);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}

	/**
	 * 
	 * usb的监听
	 * 
	 */
	ScanUSBOnListener	mScanUSBOnListener	= new ScanUSBOnListener() {

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
																	if (!softInfo.getPathString().contains(path + ROOTPATH_STRING)) {
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
													new Thread(new Runnable() {

														@Override
														public void run() {
															// TODO
															// Auto-generated
															// method stub
															int size = DeviceManager.deviceFileInfo_list.size();
															System.out.println("bob usb ==size  " + size);
															for (int i = 0; i < size; i++) {
																System.out.println("bob usb == " + DeviceManager.deviceFileInfo_list.get(i));
																getAPKFile(DeviceManager.deviceFileInfo_list.get(i));
															}
															mHandler.sendEmptyMessage(1);
															mHandler.sendEmptyMessage(7);
															mHandler.sendMessage(mHandler.obtainMessage(9, 0, 0));
														}
													}).start();

												}

												@Override
												public void onAddUSB(final String path) {
													new Thread(new Runnable() {
														@Override
														public void run() {
															// TODO
															// Auto-generated
															// method stub
															System.out.println("bob usb == onAddUSB==" + path);
															getAPKFile(path);
															mHandler.sendEmptyMessage(1);
															mHandler.sendEmptyMessage(7);

															mHandler.sendMessage(mHandler.obtainMessage(9, 1, 0));
														}
													}).start();

												}
											};

	private void getAPKFile(String path) {
		try {
			System.out.println("bob--jjjjj==" + path + ROOTPATH_STRING);
			File file = new File(path + ROOTPATH_STRING);
			if (file != null && file.exists() && file.isDirectory() && file.canRead()) {
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
								if (length <= 0) {
									continue;
								}
								softInfo.setTotal_Size(FileOperate.toSize(length));
								// try {
								// MediaPlayer player = MediaPlayer.create(
								// mContext, Uri.parse(installPath));
								// if (player == null) {
								// player = new MediaPlayer();
								// player.setDataSource(installPath);
								// }
								// if (player == null
								// || player.getDuration() <= 0) {
								// file_list_file.delete();
								// continue;
								// } else {
								// long duration = player.getDuration();
								// System.out
								// .println("bob   installPath = "
								// + installPath);
								// System.out.println("bob   duration = "
								// + duration);
								// softInfo.setPlayTime(getVideoTimeFormat(duration));
								// player.pause();
								// player.stop();
								// player.release();
								// player = null;
								// }
								//
								// } catch (Exception e) {
								// e.printStackTrace();
								// }

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
				// if (path.contains("video")) {
				// return true;
				// }
				return true;
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
		return false;
	}

	private void playVideo(String path) {
		Intent movieIntent = new Intent();
		if (checkAppCanStart(mContext, "com.jrm.localmm")) {
			ComponentName cn = new ComponentName("com.jrm.localmm", "com.jrm.localmm.ui.video.VideoPlayerActivity");
			movieIntent.setComponent(cn);
		}
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		movieIntent.setAction(Intent.ACTION_VIEW);
		movieIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		movieIntent.setDataAndType(uri, "video/*");
		try {
			mContext.startActivity(movieIntent);
			// mDialog.dismiss();
			// isPlayVideo = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkAppCanStart(Context context, String pckName) {
		if (pckName != null) {
			Intent inTemp = context.getPackageManager().getLaunchIntentForPackage(pckName);
			if (inTemp == null) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void onDestroy() {
		try {
			canleCountTask();
			mFileManagerTool.onDestroy();
			mContext.unregisterReceiver(mReceiver);
			if (mDialog != null) {
				mDialog.dismiss();
			}
			mDialog = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
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
	 */
	public void Toast_MSG(Context context, String msg, int xOffset, int yOffset, boolean isHitPosition) {
		// Toast toa = new Toast(context);
		// // if (!isHitPosition) {
		// // toa.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
		// xOffset,
		// // yOffset);
		// // }
		// toa.setDuration(Toast.LENGTH_SHORT);
		// View tView = LayoutInflater.from(context).inflate(R.layout.msg_toast,
		// null);
		// TextView tvmsg = (TextView) tView.findViewById(R.id.msg_toast_text);
		// tvmsg.setText(msg);
		// toa.setView(tView);
		// toa.show();
		// if (mToastView != null) {
		// canleToastTask();
		// mToastmsgView.setText(msg);
		// mToastView.setVisibility(View.VISIBLE);
		// toastTimerTask = new TimerTask() {
		//
		// @Override
		// public void run() {
		// toastHandler.sendEmptyMessage(0);
		// }
		// };
		// toastTimer.schedule(toastTimerTask, 2000);
		// }
		Toast_MSG(context, msg);
	}

	public static void Toast_MSG(Context context, String msg) {
		Toast toa = new Toast(context);
		toa.setDuration(Toast.LENGTH_SHORT);
		View tView = LayoutInflater.from(context).inflate(R.layout.msg_toast, null);
		TextView tvmsg = (TextView) tView.findViewById(R.id.msg_toast_text);
		tvmsg.setText(msg);
		toa.setView(tView);
		toa.show();
	}

	Timer		toastTimer		= new Timer();
	TimerTask	toastTimerTask	= null;

	private void canleToastTask() {
		if (toastTimerTask != null) {
			toastTimerTask.cancel();
			toastTimerTask = null;
		}
	}

	Handler	toastHandler	= new Handler() {
								public void handleMessage(android.os.Message msg) {
									if (mToastView != null) {
										mToastView.setVisibility(View.GONE);
									}
								};
							};

	/**
	 * 设置本地对象
	 * 
	 * @author bob 2013-3-26
	 */
	public static void setLocalObject(Context context, RecorderInfo mainDataInfo, String name) {
		try {
			System.out.println("bob    setLocalObject");
			File file = new File(context.getFilesDir().getAbsolutePath() + "/" + name);
			file.deleteOnExit();
			file.createNewFile();
			ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
			oout.writeObject(mainDataInfo);
			oout.close();
			System.out.println("bob    setLocalObject  succuss ");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("bob    setLocalObject  faile ");
			System.out.println("bob " + e.getMessage());
		}
	}

	public static void deleteLocalObject(Context context, String name) {
		try {
			File file = new File(context.getFilesDir().getAbsolutePath() + "/" + name);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到本地对象
	 * 
	 * @author bob 2013-3-26
	 */
	public static RecorderInfo getLocalObject(Context context, String name) {
		File file = new File(context.getFilesDir().getAbsolutePath() + "/" + name);
		System.out.println("bob    getLocalObject");
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
				System.out.println("bob    getLocalObject  succuss ");
			} catch (Exception e) {
				mainDataInfo = null;
				e.printStackTrace();
			}
			System.out.println("bob    getLocalObject  faile ");
			return mainDataInfo;
		}
	}

	private void deleteDialogInInit(String hit, String msg, final SoftInfo softInfo) {
		final Dialog eixtDialog = new Dialog(mContext, R.style.lockDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View dia_view = inflater.inflate(R.layout.delete_dialog, null);
		final TextView hit_text = (TextView) dia_view.findViewById(R.id.delete_dialog_title);
		final TextView msg_text = (TextView) dia_view.findViewById(R.id.delete_dialog_msg);
		final Button btu_set = (Button) dia_view.findViewById(R.id.remane_input_ok);
		final Button btu_exit = (Button) dia_view.findViewById(R.id.remane_input_set);
		hit_text.setText(hit);
		msg_text.setText(msg);

		eixtDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					SoundTool.soundKey(keyCode);
				}
				return false;
			}
		});

		eixtDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				canleDialogTimer();
			}
		});
		eixtDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				startGoneDialog();
			}
		});
		eixtDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				startGoneDialog();
			}
		});
		btu_exit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				eixtDialog.dismiss();
			}
		});

		btu_set.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					File file = new File(softInfo.getPathString());
					if (file.delete()) {
						mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.delete_success)));
						apk_List.remove(mSelectIndex);
						mHandler.sendEmptyMessage(3);
						mHandler.sendEmptyMessage(1);
					} else {
						mHandler.sendMessage(mHandler.obtainMessage(5, "delete  file failed!"));
					}
				} catch (Exception e) {
					mHandler.sendMessage(mHandler.obtainMessage(5, "delete  file failed!"));
				}
				eixtDialog.dismiss();
			}
		});
		eixtDialog.setContentView(dia_view);
		Window win = eixtDialog.getWindow();
		LayoutParams params = win.getAttributes();
		params.x = 0;
		params.y = -20;
		win.setAttributes(params);
		eixtDialog.show();
		btu_exit.requestFocus();
	}

	private void deleteYuyue(final View view, final int c) {
		final Dialog eixtDialog = new Dialog(mContext, R.style.lockDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View dia_view = inflater.inflate(R.layout.delete_dialog, null);
		final TextView hit_text = (TextView) dia_view.findViewById(R.id.delete_dialog_title);
		final TextView msg_text = (TextView) dia_view.findViewById(R.id.delete_dialog_msg);
		final Button btu_set = (Button) dia_view.findViewById(R.id.remane_input_ok);
		final Button btu_exit = (Button) dia_view.findViewById(R.id.remane_input_set);
		hit_text.setText(mContext.getString(R.string.record_delete_hit));
		msg_text.setText(mContext.getString(R.string.record_delete));
		btu_set.setText(mContext.getString(R.string.delete_y));
		btu_exit.setText(mContext.getString(R.string.btn_canle));

		eixtDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					SoundTool.soundKey(keyCode);
				}
				return false;
			}
		});
		eixtDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				canleDialogTimer();
			}
		});
		eixtDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				startGoneDialog();
			}
		});
		eixtDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				startGoneDialog();
			}
		});
		btu_exit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				eixtDialog.dismiss();
			}
		});

		btu_set.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					File file = new File(RecorderService.mYuYueRecorderInfo.path);
					file.delete();
					RecorderService.mYuYueRecorderInfo = null;
					RecorderService.mYuYueRecorderInfo_last = null;
					recorderInfo_list.remove(c);
					mRecorderLi.removeView(view);
					mAppointmentBtn.requestFocus();
					mDelete = null;
					deleteLocalObject(mContext, DATA_OBJECT);
				} catch (Exception e) {
					e.printStackTrace();
				}
				eixtDialog.dismiss();
			}
		});
		eixtDialog.setContentView(dia_view);
		Window win = eixtDialog.getWindow();
		LayoutParams params = win.getAttributes();
		params.x = 0;
		params.y = -20;
		win.setAttributes(params);
		eixtDialog.show();
		btu_exit.requestFocus();
	}

	public void exit() {
		final Dialog eixtDialog = new Dialog(mContext, R.style.lockDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View dia_view = inflater.inflate(R.layout.delete_dialog, null);
		final TextView hit_text = (TextView) dia_view.findViewById(R.id.delete_dialog_title);
		final TextView msg_text = (TextView) dia_view.findViewById(R.id.delete_dialog_msg);
		final Button btu_set = (Button) dia_view.findViewById(R.id.remane_input_ok);
		final Button btu_exit = (Button) dia_view.findViewById(R.id.remane_input_set);
		hit_text.setText(mContext.getString(R.string.record_delete_hit));
		msg_text.setText(mContext.getString(R.string.exit_hit_msg));
		btu_set.setText(mContext.getString(R.string.exit_str));
		btu_exit.setText(mContext.getString(R.string.btn_canle));

		eixtDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					SoundTool.soundKey(keyCode);
				}
				return false;
			}
		});

		btu_exit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				eixtDialog.dismiss();
			}
		});

		btu_set.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				try {
					eixtDialog.dismiss();
					Toast_MSG(mContext, mContext.getString(R.string.statu_stop));
					((Activity) mContext).finish();
					stopRecordHDMI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		eixtDialog.setContentView(dia_view);
		Window win = eixtDialog.getWindow();
		LayoutParams params = win.getAttributes();
		params.x = 0;
		params.y = -20;
		win.setAttributes(params);
		eixtDialog.show();
		btu_exit.requestFocus();
	}

	private void reNameDialogInInit(final SoftInfo softInfo) {
		final Dialog eixtDialog = new Dialog(mContext, R.style.lockDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View dia_view = inflater.inflate(R.layout.remame_dialog, null);
		final TextView hit_text = (TextView) dia_view.findViewById(R.id.rename_input_hit);
		final EditText input_ch = (EditText) dia_view.findViewById(R.id.remane_input_edit_ch);
		final Button btu_set = (Button) dia_view.findViewById(R.id.remane_input_ok);
		final Button btu_exit = (Button) dia_view.findViewById(R.id.remane_input_set);
		hit_text.setText(mContext.getString(R.string.rename));
		String file_name = "";
		try {
			String filePath = softInfo.getPathString();
			File src = new File(filePath);
			file_name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
			if (src.isFile()) {
				try {
					file_name = file_name.substring(0, file_name.lastIndexOf("."));
				} catch (IndexOutOfBoundsException e) {

				}
			}
			input_ch.setText(file_name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		eixtDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					SoundTool.soundKey(keyCode);
				}
				return false;
			}
		});
		eixtDialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				canleDialogTimer();
			}
		});
		eixtDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				startGoneDialog();
			}
		});
		eixtDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				startGoneDialog();
			}
		});
		btu_exit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				eixtDialog.dismiss();
			}
		});

		btu_set.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String user = input_ch.getText().toString().trim();
				if (user.equals("")) {
					mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.input_rename)));
				} else {
					renameTarget(user, softInfo);
					eixtDialog.dismiss();
				}
			}
		});
		eixtDialog.setContentView(dia_view);
		Window win = eixtDialog.getWindow();
		LayoutParams params = win.getAttributes();
		params.x = 0;
		params.y = -20;
		win.setAttributes(params);
		eixtDialog.show();
		input_ch.requestFocus();
	}

	/**
	 * @param filePath
	 * @param newName
	 * @return -1:newName file is exist; -2:rename fail; 0:rename success;
	 */
	public void renameTarget(String newName, final SoftInfo softInfo) {
		String filePath = softInfo.getPathString();
		File src = new File(filePath);
		String ext = "";
		File dest;
		if (src.isFile()) {
			try {
				ext = filePath.substring(filePath.lastIndexOf("."), filePath.length());
			} catch (IndexOutOfBoundsException e) {
			}
		}
		String temp = filePath.substring(0, filePath.lastIndexOf("/"));
		String destPath = temp + "/" + newName + ext;
		dest = new File(destPath);
		// 跟现有文件重名
		if (dest.exists()) {
			mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.rename_success)));
		}
		// 成功
		if (src.renameTo(dest)) {
			mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.rename_success)));
			softInfo.setPathString(destPath);
			softInfo.setNameString(dest.getName());
			mSoftAdatper.notifyDataSetChanged();
		}
		// 失败
		else {
			mHandler.sendMessage(mHandler.obtainMessage(5, mContext.getString(R.string.rename_fail)));
		}
	}

	public void setWH() {
		int resolution[] = mHdmiTool.getVideInfo();
		int high = 0;
		int wigth = 0;
		if (resolution == null || resolution[0] > 1920 || resolution[1] > 1080) {
			high = 1080;
			wigth = 1920;
		} else {
			wigth = resolution[0];
			high = resolution[1];
		}
		mHdmiRecorder.native_set_video_high(high);
		mHdmiRecorder.native_set_video_wigth(wigth);
		System.out.println("bob recorder   wigth = " + wigth + "   high = " + high);
	}

	// 高清
	public static final int	VIDEO_HD	= 0;
	// 640*480
	public static final int	VIDEO_VGA	= 1;
	// 全高清
	public static final int	VIDEO_FHD	= 2;

	// VAG 1024000
	// hd 1024000*3
	// Fhd 1024000*5
	public void set_video_HD(RecorderInfo mRecorderInfo) {
		int re = 1024000 * 3;
		switch (mRecorderInfo.resolution) {
		case VIDEO_VGA:
			re = 1024000;
			break;
		case VIDEO_HD:
			re = 1024000 * 3;
			break;
		case VIDEO_FHD:
			re = 1024000 * 5;
			break;

		default:
			break;
		}
		mHdmiRecorder.native_set_video_encoder_bitrate(re);
		System.out.println("bob recorder   resolution = " + re);
	}

	public Dialog startDialog(final Context mContext) {
		final Dialog eixtDialog = new Dialog(mContext, R.style.lockDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View dia_view = inflater.inflate(R.layout.re_dialog, null);
		final TextView hit_text = (TextView) dia_view.findViewById(R.id.delete_dialog_title);
		final TextView msg_text = (TextView) dia_view.findViewById(R.id.delete_dialog_msg);
		final Button btu_set = (Button) dia_view.findViewById(R.id.remane_input_ok);
		final Button btu_exit = (Button) dia_view.findViewById(R.id.remane_input_set);
		hit_text.setText(mContext.getString(R.string.record_delete_hit));
		msg_text.setText(mContext.getString(R.string.yuyue_time_ok_msg));
		// btu_set.setText(mContext.getString(R.string.start_recorder));
		// btu_exit.setText(mContext.getString(R.string.btn_canle));

		eixtDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					// SoundTool.soundKey(keyCode);
					// if (keyCode == KeyEvent.KEYCODE_BACK) {
					// return true;
					// }
				}
				return false;
			}
		});

		btu_exit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				eixtDialog.dismiss();
			}
		});

		btu_set.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				eixtDialog.dismiss();
			}
		});
		eixtDialog.setContentView(dia_view);
		Window window = eixtDialog.getWindow();
		window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// Window win = eixtDialog.getWindow();
		// LayoutParams params = win.getAttributes();
		// params.x = 0;
		// params.y = -20;
		// win.setAttributes(params);
		eixtDialog.show();
		btu_exit.requestFocus();
		return eixtDialog;
	}
}
