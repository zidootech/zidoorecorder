package com.zidoo.recorder.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zidoo.hdmi.recorder.R;
import com.zidoo.recorder.tool.SoundTool;
import com.zidoo.recorder.tool.ZidooRecorderTool;

public class ChooseTime {
	private WheelMain					wheelMain;
	private EditText					txttime;
	private DateFormat					dateFormat			= new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
	private int							year, month, day, houre, minute, seconds;
	private int							currentYear, currentMonth, currentDay, currentHour, currentMinute, currentSecond;
	public int					mHour, mMin, mSec;
	private Button						btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12;
	private btnOnFocusChangeListener	mbtnOnFocusChangeListener;
	private Calendar					calendar;
	private Context						mContext			= null;
	private View						mTimepickerview		= null;
	public Dialog						dialog				= null;
	public static boolean				isDate				= true;
	public static String				dataMsg				= "";
	public String						maxVideoLength		= "";
	public String						mStrtime;
	private long						msTime				= 0;
	private long						msTimeAll			= 0;
	private int							i					= 0;															;
	private float						up					=1.0f;															;
	private float						down				=-1.0f	;															;
	private ZidooRecorderTool			mZidooRecorderTool	= null;
	public OnSetDateListener			mOnSetDateListener	= null;
	private View						mViewMonth, mViewDay, mViewToday, mViewSec;
	private TextView					mtitle, mPrompt, mShowtime;
	private ImageView					mViewPrompt;
	private static String []			mToday				= null;
	private static Boolean				state				= false;
	public interface OnSetDateListener {
		void setDate(String dataMsg, long ms);
	};

	private String	timeString	= "00:10:00";

	public ChooseTime(ZidooRecorderTool zidooRecorderTool, Context mContext, boolean isDate, String maxVideoLength, OnSetDateListener mOnSetDateListener, String timeString) {
		super();
		this.mContext = mContext;
		this.mZidooRecorderTool = zidooRecorderTool;
		this.isDate = isDate;
		this.maxVideoLength = maxVideoLength;
		this.timeString = timeString;

		this.mOnSetDateListener = mOnSetDateListener;
		// dialog = new Dialog(mContext, R.style.dialogViewStyle);
		Resources res = mContext.getResources();
		mToday = res.getStringArray(R.array.days);
		AlertDialog.Builder bulder = new AlertDialog.Builder(mContext);
		dialog = bulder.create();
		Window window = dialog.getWindow();
		dialog.setOnKeyListener(mOnKeyListener);
		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				mZidooRecorderTool.canleDialogTimer();
			}
		});
//		dialog.setOnCancelListener(new OnCancelListener() {
//		
//		@Override
//		public void onCancel(DialogInterface dialog) {
//			// TODO Auto-generated method stub
//			 handler.removeCallbacks(updateThread);
//			 System.out.println("Thread.currentThread().getName()=====  2  "+Thread.currentThread().getName());
//		}
//	});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				mZidooRecorderTool.startGoneDialog();
			}
		});
		window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mTimepickerview = inflater.inflate(R.layout.timepicker, null);
		mViewMonth = mTimepickerview.findViewById(R.id.viewMonth);
		mViewDay = mTimepickerview.findViewById(R.id.viewDay);
		mViewToday = mTimepickerview.findViewById(R.id.viewToDay);
		mViewSec = mTimepickerview.findViewById(R.id.viewSec);
		mViewPrompt = (ImageView) mTimepickerview.findViewById(R.id.imagePrompt);
		mtitle = (TextView) mTimepickerview.findViewById(R.id.title);
		mPrompt = (TextView) mTimepickerview.findViewById(R.id.Prompt);
		mShowtime = (TextView) mTimepickerview.findViewById(R.id.time);
		mViewMonth.setVisibility(View.GONE);
		mViewDay.setVisibility(View.GONE);
		mViewSec.setVisibility(View.GONE);
		mViewPrompt.setVisibility(View.GONE);
		mPrompt.setVisibility(View.GONE);
		getTime(mContext);
		hideBtn();
	}

	public void getTime(Context mContext) {

		// ScreenInfo screenInfo = new ScreenInfo(mContext);//设置宽高等像素大小
		// 构造wheelMain对象
		wheelMain = new WheelMain(mTimepickerview, true);// 实例化timepickerview
															// view 控件
		// wheelMain.screenheight = screenInfo.getHeight();
		calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		houre = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		seconds = calendar.get(Calendar.SECOND);
		wheelMain.initDateTimePicker(year, month, day, houre, minute, seconds);
		// dialog.setContentView(timepickerview);
		dialog.show();
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.setContentView(mTimepickerview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    handler.post(updateThread);  
		// TextView tv = (TextView) timepickerview.findViewById(R.id.tv);
		// Button set = (Button) timepickerview.findViewById(R.id.set);
		// Button cancel = (Button) timepickerview.findViewById(R.id.cancel);
		btn1 = (Button) mTimepickerview.findViewById(R.id.btn1);
		btn2 = (Button) mTimepickerview.findViewById(R.id.btn2);
		btn3 = (Button) mTimepickerview.findViewById(R.id.btn3);
		btn4 = (Button) mTimepickerview.findViewById(R.id.btn4);
		btn5 = (Button) mTimepickerview.findViewById(R.id.btn5);
		btn6 = (Button) mTimepickerview.findViewById(R.id.btn6);
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		mStrtime = dateformat1.format(new Date());
		btn6.setText(mStrtime);

		mbtnOnFocusChangeListener = new btnOnFocusChangeListener();
		btn1.setOnFocusChangeListener(mbtnOnFocusChangeListener);
		btn2.setOnFocusChangeListener(mbtnOnFocusChangeListener);
		btn3.setOnFocusChangeListener(mbtnOnFocusChangeListener);
		btn4.setOnFocusChangeListener(mbtnOnFocusChangeListener);
		btn5.setOnFocusChangeListener(mbtnOnFocusChangeListener);
		btn6.setOnFocusChangeListener(mbtnOnFocusChangeListener);
		
		btn1.setOnKeyListener(mBtnOnKeyListener);
		btn2.setOnKeyListener(mBtnOnKeyListener);
		btn3.setOnKeyListener(mBtnOnKeyListener);
		btn4.setOnKeyListener(mBtnOnKeyListener);
		btn5.setOnKeyListener(mBtnOnKeyListener);
		btn6.setOnKeyListener(mBtnOnKeyListener);
		
		btn6.setOnGenericMotionListener(mOnGenericMotionListener);
		btn3.setOnGenericMotionListener(mOnGenericMotionListener);
		btn4.setOnGenericMotionListener(mOnGenericMotionListener);
		btn5.setOnGenericMotionListener(mOnGenericMotionListener);
		
		btn3.setOnClickListener(mOnClickListener);
		btn4.setOnClickListener(mOnClickListener);
		btn5.setOnClickListener(mOnClickListener);
		btn6.setOnClickListener(mOnClickListener);
	}

	public void hideBtn() {
		if (!isDate) {
			String [] str = timeString.split(":");
			mHour = Integer.valueOf(str[0]);
			mMin = Integer.valueOf(str[1]);
			mSec = Integer.valueOf(str[2]);
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, mHour, mMin, mSec);
			mViewMonth.setVisibility(View.GONE);
			mViewDay.setVisibility(View.GONE);
			mViewToday.setVisibility(View.GONE);
			mShowtime.setVisibility(View.GONE);
			mViewSec.setVisibility(View.VISIBLE);
			mViewPrompt.setVisibility(View.VISIBLE);
			mPrompt.setVisibility(View.VISIBLE);
			mtitle.setText(mContext.getString(R.string.record_video_length));
			mPrompt.setText(mContext.getString(R.string.limit_time) + " " + maxVideoLength);
			// is=false;
		}
	}

	DialogInterface.OnKeyListener	mOnKeyListener	= new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					SoundTool.soundKey(keyCode);
					getCurr();
					System.out.println("bob  keyCode  = " + keyCode);
					if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ENTER
							|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
						if (mOnSetDateListener != null) {
							System.out.println("bob  isDate  = " + isDate);
							if (!isDate) {
								msTime =  mHour * 3600000 + mMin * 60000 + mSec * 1000;
								System.out.println("bob mHour = "+mHour);
								System.out.println("bob mMin = "+mMin);
								System.out.println("bob mSec = "+mSec);
								mOnSetDateListener.setDate(wheelMain.getCurrentTime(isDate), msTime);
							} else {
								state = false;
								msTime = currentDay * 24 * 3600000+currentHour * 3600000 + currentMinute * 60000 + currentSecond * 1000;
								msTimeAll=day* 24 * 3600000+houre*3600000+minute*60000+seconds*1000;
								System.out.println("bob..houre== "+houre+"minute== "+minute+"seconds==  "+seconds);
								System.out.println("bob..currentHour== "+currentHour+"currentMinute== "+currentMinute+"currentSecond=="+currentSecond);
//								if ((msTime-msTimeAll)>= 5000) {
//									
//								} else {
//									ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_length));
//								}
								
								dataMsg = wheelMain.getCurrentTime(isDate);
								mOnSetDateListener.setDate(dataMsg, msTime);
							 	dialog.dismiss();
								 handler.removeCallbacks(updateThread);
							}
						}
						
					}
				}
				return false;
			}

		};

	public class btnOnFocusChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()) {
				case R.id.btn1:

					final LinearLayout btn1_up = (LinearLayout) mTimepickerview.findViewById(R.id.btn1_up);
					final LinearLayout btn1_down = (LinearLayout) mTimepickerview.findViewById(R.id.btn1_down);
					// TODO Auto-generated method stub

					if (hasFocus) {
						btn1_up.setVisibility(View.VISIBLE);
						btn1_down.setVisibility(View.VISIBLE);
					} else {
						btn1_up.setVisibility(View.INVISIBLE);
						btn1_down.setVisibility(View.INVISIBLE);
					}
					break;
				case R.id.btn2:
					final LinearLayout btn2_up = (LinearLayout) mTimepickerview.findViewById(R.id.btn2_up);
					final LinearLayout btn2_down = (LinearLayout) mTimepickerview.findViewById(R.id.btn2_down);
					// TODO Auto-generated method stub
					if (hasFocus) {
						btn2_up.setVisibility(View.VISIBLE);
						btn2_down.setVisibility(View.VISIBLE);
					} else {
						btn2_up.setVisibility(View.INVISIBLE);
						btn2_down.setVisibility(View.INVISIBLE);
					}
					break;
				case R.id.btn3:
					final LinearLayout btn3_up = (LinearLayout) mTimepickerview.findViewById(R.id.btn3_up);
					final LinearLayout btn3_down = (LinearLayout) mTimepickerview.findViewById(R.id.btn3_down);
					// TODO Auto-generated method stub
					if (hasFocus) {
						btn3_up.setVisibility(View.VISIBLE);
						btn3_down.setVisibility(View.VISIBLE);
					} else {
						btn3_up.setVisibility(View.INVISIBLE);
						btn3_down.setVisibility(View.INVISIBLE);
					}
					break;
				case R.id.btn4:
					final LinearLayout btn4_up = (LinearLayout) mTimepickerview.findViewById(R.id.btn4_up);
					final LinearLayout btn4_down = (LinearLayout) mTimepickerview.findViewById(R.id.btn4_down);
					// TODO Auto-generated method stub
					if (hasFocus) {
						btn4_up.setVisibility(View.VISIBLE);
						btn4_down.setVisibility(View.VISIBLE);
					} else {
						btn4_up.setVisibility(View.INVISIBLE);
						btn4_down.setVisibility(View.INVISIBLE);
					}
					break;
				case R.id.btn5:
					final LinearLayout btn5_up = (LinearLayout) mTimepickerview.findViewById(R.id.btn5_up);
					final LinearLayout btn5_down = (LinearLayout) mTimepickerview.findViewById(R.id.btn5_down);
					// TODO Auto-generated method stub
					if (hasFocus) {
						btn5_up.setVisibility(View.VISIBLE);
						btn5_down.setVisibility(View.VISIBLE);
					} else {
						btn5_up.setVisibility(View.INVISIBLE);
						btn5_down.setVisibility(View.INVISIBLE);
					}
					break;
				case R.id.btn6:
					System.out.println(" R.id.btn6:");
					final LinearLayout btn6_up = (LinearLayout) mTimepickerview.findViewById(R.id.btn6_up);
					final LinearLayout btn6_down = (LinearLayout) mTimepickerview.findViewById(R.id.btn6_down);
					if (hasFocus) {
						btn6_up.setVisibility(View.VISIBLE);
						btn6_down.setVisibility(View.VISIBLE);
					} else {
						btn6_up.setVisibility(View.INVISIBLE);
						btn6_down.setVisibility(View.INVISIBLE);
					}
					break;
				default:
					break;
			}

		}

	}
OnGenericMotionListener mOnGenericMotionListener =new OnGenericMotionListener() {
	
	@Override
	public boolean onGenericMotion(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		getCurr();
		final float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
		System.out.println("vscroll===   "+vscroll);
		if(v==btn6){
			if(vscroll==up){
				upDay();
			}
			if(vscroll==down){
				downDay();
			}
		}
		if(v==btn3){
			if(vscroll==up){
				upHour();
			}
			if(vscroll==down){
				downHour();
			}
			
		}
		if(v==btn4){
			if(vscroll==up){
				upMinute();
			}
			if(vscroll==down){
				downMinute();
			}
		}
		if(v==btn5){
			if(vscroll==up){
				upSecond();
			}
			if(vscroll==down){
				downSecond();
			}
			
		}
		return false;
	}
};
	OnClickListener	mOnClickListener	= new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method
				// stub
				getCurr();
				switch (v.getId()) {
					case R.id.btn3:
						downHour();
						break;
					case R.id.btn4:
						downMinute();
						break;
					case R.id.btn5:
						downSecond();
						break;
					case R.id.btn6:
						downDay();
						break;
					default:
						break;
				}
			}
		};

	OnKeyListener	mBtnOnKeyListener	= new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				getCurr();
				switch (v.getId()) {
					case R.id.btn1:
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
								case KeyEvent.KEYCODE_DPAD_UP:
									if (currentMonth - 1 < calendar.get(Calendar.MONTH)) {
										ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
									} else {
										currentMonth -= 1;
										// wheelMain.initDateTimePicker(year,currentMonth,day,houre,minute,seconds);
										wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
												currentSecond);
									}
									return true;
								case KeyEvent.KEYCODE_DPAD_DOWN:
									currentMonth += 1;

									if (currentMonth > calendar.get(Calendar.MONTH) && currentMonth != 12) {

										wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
												currentSecond);
									} else {
										ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
									}
									return true;
								default:
									break;
							}
						}
						break;
					case R.id.btn2:
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
								case KeyEvent.KEYCODE_DPAD_UP:
									if (currentDay - 1 < calendar.get(Calendar.DAY_OF_MONTH) && currentMonth <= calendar.get(Calendar.MONTH)) {
										ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
									} else {
										currentDay -= 1;
										// wheelMain.initDateTimePicker(year,month,currentDay,houre,minute,seconds);
										wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
												currentSecond);
									}
									return true;
								case KeyEvent.KEYCODE_DPAD_DOWN:
									currentDay += 1;
									Calendar time = Calendar.getInstance();
									time.clear();
									time.set(Calendar.YEAR, currentYear);
									// year年
									time.set(Calendar.MONTH, currentMonth);
									// Calendar对象默认一月为0,month月
									int mday = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
									mday = mday + 1;
									System.out.println("mday========" + mday);

									// System.out.println("calendar.getMaximum(currentDay)======"+calendar.getMaximum(calendar));
									if (currentDay > calendar.get(Calendar.DAY_OF_MONTH) && currentDay != mday
											|| currentMonth > calendar.get(Calendar.MONTH)) {

										wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
												currentSecond);
									} else {
										ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
									}
									return true;
								default:
									break;
							}
						}
						break;
					case R.id.btn6:
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
								case KeyEvent.KEYCODE_DPAD_UP:
									upDay();

									return true;

								case KeyEvent.KEYCODE_DPAD_DOWN:
									downDay();

									return true;
								default:
									break;
							}
						}
						break;
					case R.id.btn3:
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
								case KeyEvent.KEYCODE_DPAD_UP:
									upHour();
									return true;
								case KeyEvent.KEYCODE_DPAD_DOWN:
									downHour();
									return true;
								default:
									break;
							}
						}
						break;
					case R.id.btn4:
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
								case KeyEvent.KEYCODE_DPAD_UP:
									upMinute();
									return true;
								case KeyEvent.KEYCODE_DPAD_DOWN:
									downMinute();
									return true;
								default:
									break;
							}
						}
						break;
					case R.id.btn5:
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							switch (keyCode) {
								case KeyEvent.KEYCODE_DPAD_UP:
									upSecond();
									return true;
								case KeyEvent.KEYCODE_DPAD_DOWN:
									downSecond();
									return true;
								default:
									break;
							}
						}
						break;
					default:
						break;
				}
				return false;
			}
		};

	private void getCurr() {
		currentYear = wheelMain.wv_year.getCurrentItem() + 1970;
		currentMonth = wheelMain.wv_month.getCurrentItem();
		currentDay = wheelMain.wv_day.getCurrentItem() + 1;
		currentHour = wheelMain.wv_hours.getCurrentItem();
		mHour = wheelMain.wv_hours.getCurrentItem();
		currentMinute = wheelMain.wv_mins.getCurrentItem();
		mMin = wheelMain.wv_mins.getCurrentItem();
		currentSecond = wheelMain.wv_seconds.getCurrentItem();
		mSec = wheelMain.wv_seconds.getCurrentItem();
		
	}
	private void upDay(){
		if (i <= 1 && state == false) {
			i += 1;
			// if (i
			// <
			// mToday.length)
			// {
			//
			// btn6.setText(mToday[i]);
			// }
			Calendar time = Calendar.getInstance();
			time.clear();
			time.set(Calendar.YEAR, currentYear);
			// year年
			time.set(Calendar.MONTH, currentMonth);
			// Calendar对象默认一月为0,month月
			int mday = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
			currentDay += 1;
			mday = mday + 1;

			if (currentDay == mday && currentMonth == 12) {
				currentDay = 1;
				currentMonth = currentMonth + 1;
				currentYear += 1;
			}
//			System.out.println("currentDay== "+currentDay +
//					"calendar.get(Calendar.DAY_OF_MONTH== "+calendar.get(Calendar.DAY_OF_MONTH)
//					+"currentHour==   "+currentHour +"calendar.get(Calendar.HOUR_OF_DAY==  "+calendar.get(Calendar.HOUR_OF_DAY));
//			if(currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentHour<calendar.get(Calendar.HOUR_OF_DAY)){
//				currentHour=calendar.get(Calendar.HOUR_OF_DAY);
//				System.out.println("....up...  "+currentHour);
//			}
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
			btn6.setText(wheelMain.getTime());
			if (i == 2) {
				state = true;
			}

		} else if (i >= 0 && state == true) {
			i -= 1;
			// if (i
			// <
			// mToday.length)
			// {
			// btn6.setText(mToday[i]);
			// }
			currentDay -= 1;
			System.out.println("currentDay==  -1  " + currentDay);
			if (currentDay == 0) {
				currentMonth = currentMonth - 1;
			}
			Calendar time = Calendar.getInstance();
			time.clear();
			time.set(Calendar.YEAR, currentYear);
			// year年
			time.set(Calendar.MONTH, currentMonth);
			// Calendar对象默认一月为0,month月
			int mday = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
			if (currentDay == 0 && currentMonth == 1) {
				currentDay = mday;
				currentMonth = currentMonth - 1;
				currentYear += 1;
			}
			if(currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentHour<calendar.get(Calendar.HOUR_OF_DAY) ||
					currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentMinute<calendar.get(Calendar.MINUTE)||
					currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentHour<calendar.get(Calendar.HOUR_OF_DAY)
					&&currentMinute<calendar.get(Calendar.MINUTE)){
				currentHour=calendar.get(Calendar.HOUR_OF_DAY);
				currentMinute=calendar.get(Calendar.MINUTE);
			}
			// wheelMain.initDateTimePicker(year,month,currentDay,houre,minute,seconds);
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
			btn6.setText(wheelMain.getTime());
			if (i == 0) {
				state = false;
			}
		}
	}
	private void downDay(){
		if (i <= 1 && state == false) {
			i += 1;
			// if (i
			// <
			// mToday.length)
			// {
			//
			// btn6.setText(mToday[i]);
			// }
			Calendar time = Calendar.getInstance();
			time.clear();
			time.set(Calendar.YEAR, currentYear);
			// year年
			time.set(Calendar.MONTH, currentMonth);
			// Calendar对象默认一月为0,month月
			int mday = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
			currentDay += 1;
			mday = mday + 1;

			if (currentDay == mday && currentMonth == 12) {
				currentDay = 1;
				currentMonth = currentMonth + 1;
				currentYear += 1;
			}
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
			btn6.setText(wheelMain.getTime());
			if (i == 2) {
				state = true;
			}

		} else if (i >= 0 && state == true) {
			i -= 1;
			// if (i
			// <
			// mToday.length)
			// {
			// btn6.setText(mToday[i]);
			// }
			currentDay -= 1;
			System.out.println("currentDay==  -1  " + currentDay);
			if (currentDay == 0) {
				currentMonth = currentMonth - 1;
			}
			Calendar time = Calendar.getInstance();
			time.clear();
			time.set(Calendar.YEAR, currentYear);
			// year年
			time.set(Calendar.MONTH, currentMonth);
			// Calendar对象默认一月为0,month月
			int mday = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
			if (currentDay == 0 && currentMonth == 1) {
				currentDay = mday;
				currentMonth = currentMonth - 1;
				currentYear += 1;
			}
			if(currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentHour<calendar.get(Calendar.HOUR_OF_DAY) ||
					currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentMinute<calendar.get(Calendar.MINUTE)||
					currentDay== calendar.get(Calendar.DAY_OF_MONTH)&&currentHour<calendar.get(Calendar.HOUR_OF_DAY)
					&&currentMinute<calendar.get(Calendar.MINUTE)){
				currentHour=calendar.get(Calendar.HOUR_OF_DAY);
				currentMinute=calendar.get(Calendar.MINUTE);
			}
			// wheelMain.initDateTimePicker(year,month,currentDay,houre,minute,seconds);
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
			btn6.setText(wheelMain.getTime());
			if (i == 0) {
				state = false;
			}
		}
	}
	private void upHour(){
		if (isDate == false) {
			currentHour -= 1;
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
		} else {
			if (currentHour - 1 < calendar.get(Calendar.HOUR_OF_DAY) && currentMonth <= calendar.get(Calendar.MONTH)
					&& currentDay <= calendar.get(Calendar.DAY_OF_MONTH)) {
				ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
			} else {
				currentHour -= 1;
				wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
						currentSecond);
			}
		}
	}
	private void downHour(){
		currentHour += 1;
		if (isDate == false) {
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute, currentSecond);
		} else {
			if (currentHour > calendar.get(Calendar.HOUR_OF_DAY) && currentHour != 24
					|| currentMonth > calendar.get(Calendar.MONTH) || currentDay > calendar.get(Calendar.DAY_OF_MONTH)) {
				wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute, currentSecond);

			} else {
				ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
			}
		}
	}
	private void upMinute(){
		if (isDate == false) {
			currentMinute -= 1;
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
		} else {
			if (currentMinute - 1 < calendar.get(Calendar.MINUTE) && currentMonth <= calendar.get(Calendar.MONTH)
					&& currentDay <= calendar.get(Calendar.DAY_OF_MONTH)
					&& currentHour <= calendar.get(Calendar.HOUR_OF_DAY)) {
				ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
			} else {
				currentMinute -= 1;
				wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
						currentSecond);
			}
		}
	}
	private void downMinute(){
		currentMinute += 1;
		if (isDate == false) {

			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
		} else {
			if (currentMinute > calendar.get(Calendar.MINUTE) && currentMinute != 60
					|| currentMonth > calendar.get(Calendar.MONTH) || currentDay > calendar.get(Calendar.DAY_OF_MONTH)
					|| currentHour > calendar.get(Calendar.HOUR_OF_DAY)) {

				wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
						currentSecond);
			} else {
				ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
			}
		}
	}
	private void upSecond(){
		if (isDate == false) {
			currentSecond -= 1;
			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
		} else {

			if (currentSecond - 1 < calendar.get(Calendar.SECOND) && currentMonth <= calendar.get(Calendar.MONTH)
					&& currentDay <= calendar.get(Calendar.DAY_OF_MONTH)
					&& currentHour <= calendar.get(Calendar.HOUR_OF_DAY)
					&& currentMinute <= calendar.get(Calendar.MINUTE)) {
				ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
			} else {
				currentSecond -= 1;
				wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
						currentSecond);
			}
		}
	}
	private void downSecond(){
		currentSecond += 1;
		if (isDate == false) {

			wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
					currentSecond);
		} else if (isDate == true) {
			if (currentSecond > calendar.get(Calendar.SECOND) && currentSecond != 60
					|| currentMonth > calendar.get(Calendar.MONTH) || currentDay > calendar.get(Calendar.DAY_OF_MONTH)
					|| currentHour > calendar.get(Calendar.HOUR_OF_DAY)
					|| currentMinute > calendar.get(Calendar.MINUTE)) {

				wheelMain.initDateTimePicker(currentYear, currentMonth, currentDay, currentHour, currentMinute,
						currentSecond);
			} else {
				ZidooRecorderTool.Toast_MSG(mContext, mContext.getString(R.string.time_msg));
			}
		}
	}
//	Handler handler = new Handler() ;
//	private  Runnable updateThread = new Runnable() {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			handler.postDelayed(updateThread, 1000);
//			String ss=dateFormat.format(Calendar.getInstance().getTime());
//			mShowtime.setText(ChooseTime.this.mContext.getString(R.string.current_time) + " " + ss);
//		}
//		
//	};
	
	private String	cTime	;
	Handler handler = new Handler() ;
	private  Runnable updateThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(updateThread, 1000);
//			String ss=dateFormat.format(Calendar.getInstance().getTime());
			if(get24HourMode()){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				cTime=sdf.format(new Date());
			}else{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
				cTime=sdf.format(new Date());
			}
			mShowtime.setText(ChooseTime.this.mContext.getString(R.string.current_time) + " " + cTime);
		}
		
	};
	 private boolean get24HourMode() {
	        return android.text.format.DateFormat.is24HourFormat(mContext);
	    }

}
