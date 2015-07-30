package com.zidoo.recorder.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zidoo.hdmi.recorder.R;
import com.zidoo.recorder.time.WheelView;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class WheelMain {

	private View			view;
	public static WheelView	wv_year;
	public static WheelView	wv_month;
	public static WheelView	wv_day;
	public static WheelView	wv_hours;
	public static WheelView	wv_mins;

	public static WheelView	wv_seconds;
	public int				screenheight;
	private boolean			hasSelectTime;	// 用来判断是否选择显示时分秒的
	private static int		START_YEAR	= 1970, END_YEAR = 2200;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public WheelMain(View view) {
		super();
		this.view = view;
		hasSelectTime = false; // 构造方法中显示出时分秒
		setView(view);
	}

	public WheelMain(View view, boolean hasSelectTime) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}

	public void initDateTimePicker(int year, int month, int day) {
		this.initDateTimePicker(year, month, day, 0, 0, 0);
	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker(int year, int month, int day, int h, int m, int s) {
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DATE);
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		// wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		// wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		// wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_mins = (WheelView) view.findViewById(R.id.min);
		wv_seconds = (WheelView) view.findViewById(R.id.seconds);

		if (hasSelectTime) {
			wv_hours.setVisibility(View.VISIBLE);
			wv_mins.setVisibility(View.VISIBLE);
			wv_seconds.setVisibility(View.VISIBLE);

			wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
			wv_hours.setCyclic(true);// 可循环滚动
			// wv_hours.setLabel("时");// 添加文字
			wv_hours.setCurrentItem(h);

			wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			wv_mins.setCyclic(true);// 可循环滚动
			// wv_mins.setLabel("分");// 添加文字
			wv_mins.setCurrentItem(m);

			wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
			wv_seconds.setCyclic(true);// 可循环滚动
			// wv_seconds.setLabel("秒");// 添加文字
			wv_seconds.setCurrentItem(s);

		} else {
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
			wv_seconds.setVisibility(View.GONE);
		}

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 38;
		// if(hasSelectTime)
		// textSize = (screenheight / 100) * 5;
		// else
		// textSize = (screenheight / 100) * 6;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_seconds.TEXT_SIZE = textSize;

	}

	private String	currhour, currmin, currsec, currmounth, currday;

	public String getCurrentTime(boolean isdata) {
		StringBuffer sb = new StringBuffer();
		if (!isdata) {
			if (wv_hours.getCurrentItem() < 10) {
				currhour = "0" + String.valueOf(wv_hours.getCurrentItem());
			} else {
				currhour = String.valueOf(wv_hours.getCurrentItem());
			}
			if (wv_mins.getCurrentItem() < 10) {
				currmin = "0" + String.valueOf(wv_mins.getCurrentItem());
			} else {
				currmin = String.valueOf(wv_mins.getCurrentItem());
			}
			if (wv_seconds.getCurrentItem() < 10) {
				currsec = "0" + String.valueOf(wv_seconds.getCurrentItem());
			} else {
				currsec = String.valueOf(wv_seconds.getCurrentItem());
			}
			sb.append(currhour).append(":").append(currmin).append(":").append(currsec);
		} else {
			if ((wv_month.getCurrentItem() + 1) < 10) {
				currmounth = "0" + String.valueOf((wv_month.getCurrentItem() + 1));
			} else {

				currmounth = String.valueOf((wv_month.getCurrentItem() + 1));
			}
			if ((wv_day.getCurrentItem() + 1) < 10) {
				currday = "0" + String.valueOf((wv_day.getCurrentItem() + 1));
			} else {

				currday = String.valueOf((wv_day.getCurrentItem() + 1));
			}
			if (wv_hours.getCurrentItem() < 10) {
				currhour = "0" + String.valueOf(wv_hours.getCurrentItem());
			} else {
				currhour = String.valueOf(wv_hours.getCurrentItem());
			}
			if (wv_mins.getCurrentItem() < 10) {
				currmin = "0" + String.valueOf(wv_mins.getCurrentItem());
			} else {
				currmin = String.valueOf(wv_mins.getCurrentItem());
			}
			if (wv_seconds.getCurrentItem() < 10) {
				currsec = "0" + String.valueOf(wv_seconds.getCurrentItem());
			} else {
				currsec = String.valueOf(wv_seconds.getCurrentItem());
			}
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("/").append(currmounth).append("/").append(currday).append(" ").append(currhour).append(":").append(currmin)
					.append(":").append("00");
		}
		return sb.toString();
	}

	public static String getPtime() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		int houre = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);

		StringBuffer sb = new StringBuffer();
		sb.append(year).append("/").append(month).append("/").append(day).append(" ").append(houre).append(":").append(minute).append(":").append(seconds);
		return sb.toString();
	}

	@SuppressLint("SimpleDateFormat")
	public static Boolean compareTime(String msgtime) {
		System.out.println("bob  msgtime = " + msgtime);
		long msTime = 0;
		String mSysTime = getPtime();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date d1 = df.parse(msgtime);
			Date d2 = df.parse(mSysTime);
			msTime = d1.getTime() - d2.getTime();
		} catch (Exception e) {
			System.out.println("  ....error");
		}
		if (msTime >= 3000) {
			return true;
		} else {

			return false;
		}

	}

	public static Boolean compareStartTime(String msgtime) {
		long msTime = 0;
		String mSysTime = getPtime();

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date d1 = df.parse(msgtime);
			Date d2 = df.parse(mSysTime);
			msTime = d1.getTime() - d2.getTime();
			System.out.println("bob order_time = " + msgtime);
			System.out.println("bob df = " + mSysTime);
			System.out.println("bob msTime = " + msTime);

		} catch (Exception e) {
			System.out.println("  ....error");
		}
		msTime = msTime / 1000;
		if (msTime >= -2 && msTime <= 2) {
			return true;
		} else {

			return false;
		}
	}

	public static Boolean isYueYuTime(String msgtime) {
		long msTime = 0;
		String mSysTime = getPtime();

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date d1 = df.parse(msgtime);
			Date d2 = df.parse(mSysTime);
			msTime = d1.getTime() - d2.getTime();

		} catch (Exception e) {
			System.out.println("  ....error");
		}
		msTime = msTime / 1000;
		if (msTime >= -4) {
			return true;
		} else {
			return false;
		}
	}

	public String getTime() {
		StringBuffer mSb = new StringBuffer();
		if ((wv_month.getCurrentItem() + 1) < 10) {
			currmounth = "0" + String.valueOf((wv_month.getCurrentItem() + 1));
		} else {

			currmounth = String.valueOf((wv_month.getCurrentItem() + 1));
		}
		if ((wv_day.getCurrentItem() + 1) < 10) {
			currday = "0" + String.valueOf((wv_day.getCurrentItem() + 1));
		} else {

			currday = String.valueOf((wv_day.getCurrentItem() + 1));
		}
		mSb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append(currmounth).append("-").append(currday);
		return mSb.toString();
	}
}
