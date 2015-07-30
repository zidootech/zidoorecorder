package com.zidoo.recorder.pip;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tv.TvPipPopManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumPipReturn;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.zidoo.hdmi.recorder.R;
import com.zidoo.recorder.contants.APPConstants;

/**
 * 画中画服务
 * 
 * @author lic
 * 
 */
public class PipService extends Service implements Callback, OnClickListener, OnKeyListener, OnFocusChangeListener
{
	private final String	PREFER_NAME				= "config";
	private final String	PREFER_WINDOW_TYPE_X	= "wtX";
	private final String	PREFER_WINDOW_TYPE_Y	= "wtY";
	private final String	PREFER_WINDOW_TYPE_W	= "wtWidth";
	private final String	PREFER_WINDOW_TYPE_H	= "wtHeight";
	private final String	PREFER_SCALE_MODE		= "scale";

	MyHandler				mHandler				= new MyHandler();
	View					mVPip					= null;
	RelativeLayout			mRlController, mRlFrame;
	FrameLayout				mFmPip;
	LinearLayout			mLnController, mLnFrameBg, mLnScale, mLnAudio;
	View[]					mScales;
	ImageView				mImgSwitch;
	TextView				mTvReminds, mTvScale, mTvAudio, mTvNoSingle;
	ProgressBar				mPgbLoading;
	SurfaceView				mSfPip;

	VideoWindowType			mWindowType;
	boolean					mSetPositionOrSize;							// true:设置位置
	boolean					mIsFullScreen;
	boolean					mIsHdmiAudio			= true;
	boolean					mOperating				= false;
	boolean					mFirstStart				= true;
	float					mWidthScale, mHeightScale;
	int						mScreenWidth, mScreenHeight;
	int						mScaleMode				= 0;					// 0,按16:9缩放;1,按4:3缩放;2,自由缩放
	/** -1:未初始化,0:右边有间距,1右边没间距,2.右边没间距半透明,3.左边没间距,4,左边没间距半透明,5,左边有间距 */
	int						mControllerShowMode		= -1;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		DisplayMetrics mDMs = new DisplayMetrics();
		((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDMs);
		mScreenWidth = mDMs.widthPixels;
		mScreenHeight = mDMs.heightPixels;
		initReselution();

		mWindowType = new VideoWindowType();
		SharedPreferences sp = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
		mWindowType.width = sp.getInt(PREFER_WINDOW_TYPE_W, mScreenWidth * 3 / 8);
		mWindowType.height = sp.getInt(PREFER_WINDOW_TYPE_H, mScreenHeight * 3 / 8);
		mWindowType.x = sp.getInt(PREFER_WINDOW_TYPE_X, 0);
		mWindowType.y = sp.getInt(PREFER_WINDOW_TYPE_Y, mScreenHeight - mWindowType.height);
		mScaleMode = sp.getInt(PREFER_SCALE_MODE, 0);

		if (mHeightScale != 1) Toast.makeText(getApplicationContext(), getString(R.string.reminds_resolution, mScreenHeight + "p"), Toast.LENGTH_LONG).show();
		super.onCreate();
	}

	private void initReselution()
	{
		int w = 0;
		int h = 0;
		// EnumDisplayResolutionType.E_DISPLAY_DACOUT_1080P_60
		switch (TvPictureManager.getInstance().GetResloution())
		{
		case 0:// 640x480p
			w = 640;
			h = 480;
			break;
		case 1:// 720x480i
		case 3:// 720x480p
			w = 720;
			h = 480;
			break;
		case 2:// 720x576i
		case 4:// 720x576p
			w = 720;
			h = 576;
			break;
		case 5:// 1280x720p_50Hz
		case 6:// 1280x720p_60Hz
			w = 1280;
			h = 720;
			break;
		case 7:// 1920x1080i_50Hz
		case 8:// 1920x1080i_60Hz
		case 9:// 1920x1080p_24Hz
		case 10:// 1920x1080p_25Hz
		case 11:// 1920x1080p_30Hz
		case 12:// 1920x1080p_50Hz
		case 13:// 1920x1080p_60Hz
			w = 1920;
			h = 1080;
			break;
		case 15:// 1280x1470p_50Hz
		case 16:// 1280x1470p_60Hz
		case 17:// 1280x1470p_24Hz
		case 18:// 1280x1470p_30Hz
			w = 1280;
			h = 1470;
			break;
		case 19:// 1920x2205p_24Hz
		case 20:// 1920x2205p_30Hz
			w = 1920;
			h = 2205;
			break;
		case 14:// 4K2Kp_30Hz
		case 21:// 4K2Kp_25Hz
		case 22:// 4K2Kp_24Hz
		case 23:// 4K2Kp_50Hz
		case 24:// 4K2Kp_60Hz
			w = 4096;
			h = 2160;
			break;
		case 25:// MAX
			w = 1920;
			h = 1080;
			break;

		default:
			w = 1920;
			h = 1080;
			break;
		}
		mWidthScale = (float) w / mScreenWidth;
		mHeightScale = (float) h / mScreenHeight;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (!APPConstants.sRecordActivityRunning)
		{
			if (mVPip == null)
			{
				initView();
				openPip();
				setAudio(mIsHdmiAudio);
				mImgSwitch.setSelected(mIsHdmiAudio);
			} else
			{
				if (mRlController.isShown())
				{
					hideController();
				} else
				{

					WindowManager.LayoutParams params = (WindowManager.LayoutParams) mVPip.getLayoutParams();
					params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
					((WindowManager) getSystemService(WINDOW_SERVICE)).updateViewLayout(mVPip, params);
					mRlController.setVisibility(View.VISIBLE);
				}
			}
		} else
		{
			this.stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 隐藏控制器
	 */
	private void hideController()
	{
		mRlController.setVisibility(View.GONE);
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) mVPip.getLayoutParams();
		params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		((WindowManager) getSystemService(WINDOW_SERVICE)).updateViewLayout(mVPip, params);
	}

	/**
	 * 初始化控件
	 */
	private void initView()
	{
		mVPip = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_pip, null);
		View rlPip = (RelativeLayout) mVPip.findViewById(R.id.rl_pip);
		mFmPip = (FrameLayout) rlPip.findViewById(R.id.fm_pip);
		mLnFrameBg = (LinearLayout) rlPip.findViewById(R.id.ln_bg);
		mPgbLoading = (ProgressBar) mFmPip.findViewById(R.id.pgb_loading);
		mRlFrame = (RelativeLayout) mFmPip.findViewById(R.id.rl_frame);
		mTvNoSingle = (TextView) mFmPip.findViewById(R.id.tv_no_single);

		mRlController = (RelativeLayout) mVPip.findViewById(R.id.rl_control);
		mLnController = (LinearLayout) mRlController.findViewById(R.id.ln_controller);
		mLnScale = (LinearLayout) mRlController.findViewById(R.id.ln_scale);
		mLnAudio = (LinearLayout) mRlController.findViewById(R.id.ln_audio);
		mImgSwitch = (ImageView) mLnAudio.findViewById(R.id.img_switch);
		mTvReminds = (TextView) mRlController.findViewById(R.id.tv_reminds);

		mSfPip = (SurfaceView) mFmPip.findViewById(R.id.sf_pip);
		SurfaceHolder holder = mSfPip.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);

		View move = mLnController.findViewById(R.id.tv_move);
		mTvScale = (TextView) mLnController.findViewById(R.id.tv_scale);
		mTvAudio = (TextView) mLnController.findViewById(R.id.tv_audio);
		View exit = mLnController.findViewById(R.id.tv_exit);
		View audioSwitch = mLnAudio.findViewById(R.id.rl_audio);

		mScales = new TextView[4];
		mScales[0] = mLnScale.findViewById(R.id.tv_scale_0);
		mScales[1] = mLnScale.findViewById(R.id.tv_scale_1);
		mScales[2] = mLnScale.findViewById(R.id.tv_scale_2);
		mScales[3] = mLnScale.findViewById(R.id.tv_scale_3);

		for (int i = 0; i < mScales.length; i++)
		{
			View v = mScales[i];
			v.setTag(i);
			v.setOnClickListener(this);
			v.setOnKeyListener(this);
			v.setOnFocusChangeListener(this);
		}
		mScales[mScaleMode].setSelected(true);

		move.setOnClickListener(this);
		mTvScale.setOnClickListener(this);
		mTvAudio.setOnClickListener(this);
		exit.setOnClickListener(this);
		audioSwitch.setOnClickListener(this);

		move.setOnFocusChangeListener(this);
		mTvScale.setOnFocusChangeListener(this);
		mTvAudio.setOnFocusChangeListener(this);
		exit.setOnFocusChangeListener(this);
		audioSwitch.setOnFocusChangeListener(this);

		move.setOnKeyListener(this);
		mTvScale.setOnKeyListener(this);
		mTvAudio.setOnKeyListener(this);
		exit.setOnKeyListener(this);
		audioSwitch.setOnKeyListener(this);
		mFmPip.setOnKeyListener(this);

		adjustPipFrame(mWindowType.x, mWindowType.y, mWindowType.width, mWindowType.height);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;

		((WindowManager) getSystemService(WINDOW_SERVICE)).addView(mVPip, params);
		move.requestFocus();
	}

	/**
	 * 调整画中画边框
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void adjustPipFrame(int x, int y, int w, int h)
	{
		LayoutParams lp = (LayoutParams) mFmPip.getLayoutParams();
		lp.leftMargin = x;
		lp.topMargin = y;
		lp.width = w;
		lp.height = h;
		mFmPip.requestLayout();
	}

	/**
	 * 打开PIP
	 */
	private void openPip()
	{
		mOperating = true;
		mFmPip.setFocusable(false);
		mSfPip.setVisibility(View.VISIBLE);
		mPgbLoading.setVisibility(View.VISIBLE);
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				TvCommonManager commonManager = TvCommonManager.getInstance();
				TvPipPopManager pipPopManager = TvPipPopManager.getInstance();
				pipPopManager.setPipDisplayFocusWindow(EnumScalerWindow.E_MAIN_WINDOW);
				EnumInputSource current = commonManager.getCurrentInputSource();
				EnumPipReturn pipReturn = null;
				if (current == null)
				{
					commonManager.setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
					pipReturn = pipPopManager.enablePipTV(EnumInputSource.E_INPUT_SOURCE_STORAGE, EnumInputSource.E_INPUT_SOURCE_HDMI,
							getScaledWindowType(mWindowType.x, mWindowType.y, mWindowType.width, mWindowType.height));
				} else
				{
					if (current != EnumInputSource.E_INPUT_SOURCE_STORAGE) commonManager.setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
					pipReturn = pipPopManager.enablePipTV(current, EnumInputSource.E_INPUT_SOURCE_HDMI,
							getScaledWindowType(mWindowType.x, mWindowType.y, mWindowType.width, mWindowType.height));
				}
				if (pipReturn != EnumPipReturn.E_PIP_NOT_SUPPORT)
				{
					pipPopManager.setPipOnFlag(true);
				} else
				{
					Log.e("PipService", "PIP Error Prompt!!");
				}
				mHandler.sendEmptyMessage(MyHandler.LOAD_COMPLETE);
				mOperating = false;
			}
		}).start();
	}

	/**
	 * 显示画中画边框
	 */
	private void showPipFrame()
	{
		mLnFrameBg.setVisibility(View.VISIBLE);
		mRlFrame.setVisibility(View.VISIBLE);
		mLnScale.setVisibility(View.GONE);
		mSfPip.setVisibility(View.GONE);
		if (mTvNoSingle.isShown()) mTvNoSingle.setVisibility(View.GONE);
		if (mPgbLoading.isShown()) mPgbLoading.setVisibility(View.GONE);
		mFmPip.setFocusable(true);
		mFmPip.requestFocus();
		closePip();
	}

	/**
	 * 关闭画中画
	 */
	private void closePip()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				TvPipPopManager pipPopManager = TvPipPopManager.getInstance();
				pipPopManager.disablePip();
				pipPopManager.setPipOnFlag(false);
			}
		}).start();
	}

	/**
	 * 完全退出
	 */
	private void exit()
	{
		stopSelf();
	}

	/**
	 * 全屏
	 */
	private void fullScreen()
	{
		mIsFullScreen = true;
		adjustPipFrame(0, 0, mScreenWidth, mScreenHeight);
		TvPipPopManager.getInstance().setPipSubwindow(getScaledWindowType(0, 0, mScreenWidth, mScreenHeight));
		mFmPip.setFocusable(true);
		mFmPip.requestFocus();
		mRlController.setVisibility(View.GONE);
	}

	private VideoWindowType getScaledWindowType(int x, int y, int width, int height)
	{
		VideoWindowType videoWindowType = new VideoWindowType();
		videoWindowType.x = (int) (x * mWidthScale);
		videoWindowType.y = (int) (y * mHeightScale);
		videoWindowType.width = (int) (width * mWidthScale);
		videoWindowType.height = (int) (height * mHeightScale);
		return videoWindowType;
	}

	/**
	 * 切换音频
	 * 
	 * @param isHdmi
	 *            是否是hdmi音频
	 */
	private void setAudio(boolean isHdmi)
	{
		try
		{
			TvManager.getInstance().getAudioManager().setInputSource(isHdmi ? EnumInputSource.E_INPUT_SOURCE_HDMI : EnumInputSource.E_INPUT_SOURCE_STORAGE);
		} catch (TvCommonException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 改变位置
	 * 
	 * @param keyCode
	 */
	private void changePosition(int keyCode)
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFmPip.getLayoutParams();
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:
			lp.topMargin -= 10;
			if (lp.topMargin < 0) lp.topMargin = 0;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			lp.topMargin += 10;
			if (lp.topMargin + lp.height > mScreenHeight) lp.topMargin = mScreenHeight - lp.height;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			lp.leftMargin -= 10;
			if (lp.leftMargin < 0) lp.leftMargin = 0;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			lp.leftMargin += 10;
			if (lp.leftMargin + lp.width > mScreenWidth) lp.leftMargin = mScreenWidth - lp.width;
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			mWindowType.x = lp.leftMargin;
			mWindowType.y = lp.topMargin;
			mWindowType.width = lp.width;
			mWindowType.height = lp.height;

			openPip();

			mLnController.setAlpha(1);
			mLnController.findViewById(R.id.tv_move).requestFocus();
			mRlFrame.setVisibility(View.GONE);
			return;
		case KeyEvent.KEYCODE_BACK:
			openPip();

			mLnController.setAlpha(1);
			mLnController.findViewById(R.id.tv_move).requestFocus();
			mRlFrame.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		mFmPip.requestLayout();
	}

	@Override
	public void onDestroy()
	{
		mIsHdmiAudio = true;
		SharedPreferences.Editor editor = getSharedPreferences(PREFER_NAME, MODE_PRIVATE).edit();
		editor.putInt(PREFER_WINDOW_TYPE_X, mWindowType.x);
		editor.putInt(PREFER_WINDOW_TYPE_Y, mWindowType.y);
		editor.putInt(PREFER_WINDOW_TYPE_W, mWindowType.width);
		editor.putInt(PREFER_WINDOW_TYPE_H, mWindowType.height);
		editor.putInt(PREFER_SCALE_MODE, mScaleMode);
		editor.commit();

		closePip();
		setAudio(false);
		if (mVPip != null) ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mVPip);
		super.onDestroy();
	}

	/**
	 * 改变大小
	 * 
	 * @param keyCode
	 */
	private void changeSize(int keyCode)
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFmPip.getLayoutParams();
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:
			switch (mScaleMode)
			{
			case 0:// 16:9
				frameExpand(lp, 0.5625f);
				break;
			case 1:
				frameExpand(lp, 0.75f);
				break;
			case 2:// 自由缩放
				int cy = lp.height / 2 + lp.topMargin;
				lp.height += 10;
				if (lp.height > mScreenHeight) lp.height = mScreenHeight;
				int hh = lp.height / 2;
				lp.topMargin = cy + lp.height - hh > mScreenHeight ? mScreenHeight - lp.height : cy - hh;
				if (lp.topMargin < 0) lp.topMargin = 0;
				break;

			default:
				break;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			switch (mScaleMode)
			{
			case 0:
				frameShrink(lp, 0.5625f);
				break;
			case 1:
				frameShrink(lp, 0.75f);
				break;
			case 2:
				if (lp.height > 300)
				{
					lp.topMargin += 5;
					lp.height -= 10;
				}
				break;

			default:
				break;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			switch (mScaleMode)
			{
			case 0:
				frameShrink(lp, 0.5625f);
				break;
			case 1:
				frameShrink(lp, 0.75f);
				break;
			case 2:
				if (lp.width > 400)
				{
					lp.leftMargin += 5;
					lp.width -= 10;
				}
				break;

			default:
				break;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			switch (mScaleMode)
			{
			case 0:
				frameExpand(lp, 0.5625f);
				break;
			case 1:
				frameExpand(lp, 0.75f);
				break;
			case 2:
				int cx = lp.width / 2 + lp.leftMargin;
				lp.width += 10;
				if (lp.width > mScreenWidth) lp.width = mScreenWidth;
				int hw = lp.width / 2;
				lp.leftMargin = cx + lp.width - hw > mScreenWidth ? mScreenWidth - lp.width : cx - hw;
				if (lp.leftMargin < 0) lp.leftMargin = 0;
				break;

			default:
				break;
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			mWindowType.x = lp.leftMargin;
			mWindowType.y = lp.topMargin;
			mWindowType.width = lp.width;
			mWindowType.height = lp.height;
			openPip();

			mLnController.setAlpha(1);
			mLnController.findViewById(R.id.tv_scale).requestFocus();
			mRlFrame.setVisibility(View.GONE);
			return;
		case KeyEvent.KEYCODE_BACK:
			openPip();
			mLnController.setAlpha(1);
			mLnController.findViewById(R.id.tv_scale).requestFocus();
			mRlFrame.setVisibility(View.GONE);
			return;

		default:
			break;
		}
		mFmPip.requestLayout();
	}

	/**
	 * 放大边框
	 * 
	 * @param lp
	 * @param p
	 */
	private void frameExpand(RelativeLayout.LayoutParams lp, float p)
	{
		int l = lp.leftMargin;
		int t = lp.topMargin;
		int w = lp.width;
		int h = lp.height;

		final int cx = l + w / 2;
		final int cy = t + h / 2;

		final int ah = p == 0.5625 ? 9 : 12;

		if ((float) h / w > p)
		{
			if (w < mScreenWidth)
			{
				w += 16;
				if (w > mScreenWidth) w = mScreenWidth;
				int tempH = (int) (w * p);
				if (tempH > h)
				{
					if (tempH > mScreenHeight)
					{
						h = mScreenHeight;
						w = (int) (h / p);
					} else
					{
						h = tempH;
					}
				}
			}
		} else
		{
			if (h < mScreenHeight)
			{
				h += ah;
				if (h > mScreenHeight) h = mScreenHeight;
				int tempW = (int) (h / p);
				if (tempW > w)
				{
					if (tempW > mScreenWidth)
					{
						w = mScreenWidth;
						h = (int) (w * p);
					} else
					{
						w = tempW;
					}
				}
			}
		}

		int hw = w / 2;
		if (cx + w - hw <= mScreenWidth)
		{
			l = cx - hw;
			if (l < 0) l = 0;
		} else
		{
			l = mScreenWidth - w;
		}

		int hh = h / 2;
		if (cy + h - hh <= mScreenHeight)
		{
			t = cy - hh;
			if (t < 0) t = 0;
		} else
		{
			t = mScreenHeight - h;
		}

		lp.leftMargin = l;
		lp.topMargin = t;
		lp.width = w;
		lp.height = h;
	}

	/**
	 * 缩小边框
	 * 
	 * @param lp
	 * @param p
	 */
	private void frameShrink(RelativeLayout.LayoutParams lp, float p)
	{
		int l = lp.leftMargin;
		int t = lp.topMargin;
		int w = lp.width;
		int h = lp.height;

		final int cx = l + w / 2;
		final int cy = t + h / 2;
		final int mw = 400;
		final int mh = p == 0.5625 ? 225 : 300;
		final int dw = 12;
		final int dh = p == 0.5625 ? 9 : 12;

		if ((float) h / w > p)
		{
			if (h > mh)
			{
				h -= dh;
				if (h < mh) h = mh;
				int tempW = (int) (h / p);
				if (tempW < w)
				{
					if (tempW < mw)
					{
						w = mw;
						h = (int) (w * p);
					} else
					{
						w = tempW;
					}
				}
			}
		} else
		{
			if (w > mw)
			{
				w -= dw;
				if (w < mw) w = mw;
				int tempH = (int) (w * p);
				if (tempH < h)
				{
					if (tempH < mh)
					{
						h = mh;
						w = (int) (h / p);
					} else
					{
						h = tempH;
					}
				}
			}
		}

		lp.leftMargin = cx - w / 2;
		lp.topMargin = cy - h / 2;
		lp.width = w;
		lp.height = h;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		try
		{
			TvManager.getInstance().getPlayerManager().setDisplay(holder);
		} catch (TvCommonException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub

	}

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler
	{
		static final int	SET_AUDIO		= 0;
		static final int	LOAD_COMPLETE	= 1;

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case SET_AUDIO:
				setAudio(mIsHdmiAudio);
				break;
			case LOAD_COMPLETE:
				mLnFrameBg.setVisibility(View.GONE);
				mPgbLoading.setVisibility(View.GONE);
				// try
				// {
				// if
				// (!TvManager.getInstance().getPlayerManager().isSignalStable())
				// mTvNoSingle.setVisibility(View.VISIBLE);
				// } catch (TvCommonException e)
				// {
				// e.printStackTrace();
				// }
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if (hasFocus)
		{
			switch (v.getId())
			{
			case R.id.tv_scale:
				mLnScale.setVisibility(View.VISIBLE);
				mLnAudio.setVisibility(View.GONE);
				mTvReminds.setText(R.string.reminds_scale);
				break;
			case R.id.tv_audio:
				mLnScale.setVisibility(View.GONE);
				mLnAudio.setVisibility(View.VISIBLE);
				mTvReminds.setText(R.string.reminds_audio);
				break;
			case R.id.tv_move:
				mLnScale.setVisibility(View.GONE);
				mLnAudio.setVisibility(View.GONE);
				mTvReminds.setText(R.string.reminds_move);
				break;
			case R.id.tv_exit:
				mLnScale.setVisibility(View.GONE);
				mLnAudio.setVisibility(View.GONE);
				mTvReminds.setText(R.string.reminds_exit);
				break;
			case R.id.tv_scale_0:
				mTvReminds.setText(R.string.reminds_16_9);
				mTvScale.setSelected(true);
				break;
			case R.id.tv_scale_1:
				mTvReminds.setText(R.string.reminds_4_3);
				mTvScale.setSelected(true);
				break;
			case R.id.tv_scale_2:
				mTvReminds.setText(R.string.reminds_arbitrarily);
				mTvScale.setSelected(true);
				break;
			case R.id.tv_scale_3:
				mTvReminds.setText(R.string.reminds_full);
				mTvScale.setSelected(true);
				break;
			case R.id.rl_audio:
				mTvAudio.setSelected(true);
				v.findViewById(R.id.tv_audio_switch).setSelected(true);
				break;
			default:
				break;
			}
		} else
		{
			switch (v.getId())
			{
			case R.id.tv_scale_0:
			case R.id.tv_scale_1:
			case R.id.tv_scale_2:
			case R.id.tv_scale_3:
				mTvScale.setSelected(false);
				break;
			case R.id.rl_audio:
				mTvAudio.setSelected(false);
				v.findViewById(R.id.tv_audio_switch).setSelected(false);
				break;

			default:
				break;
			}
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
	{
		if (mOperating) return true;
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			int id = v.getId();
			if (id == R.id.fm_pip)
			{
				if (mIsFullScreen)
				{
					TvPipPopManager.getInstance().setPipSubwindow(getScaledWindowType(mWindowType.x, mWindowType.y, mWindowType.width, mWindowType.height));
					adjustPipFrame(mWindowType.x, mWindowType.y, mWindowType.width, mWindowType.height);
					mFmPip.setFocusable(false);
					mRlController.setVisibility(View.VISIBLE);
					mRlController.requestFocus();
					mIsFullScreen = false;
				} else
				{
					if (mSetPositionOrSize)
					{
						changePosition(keyCode);
					} else
					{
						changeSize(keyCode);
					}
				}
				return true;
			} else
			{
				switch (keyCode)
				{
				case KeyEvent.KEYCODE_DPAD_UP:
					return id == R.id.tv_scale_0 || id == R.id.rl_audio;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					return id == R.id.tv_scale_3 || id == R.id.rl_audio;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					switch (id)
					{
					case R.id.tv_scale_0:
					case R.id.tv_scale_1:
					case R.id.tv_scale_2:
					case R.id.tv_scale_3:
						return mTvScale.requestFocus();

					default:
						break;
					}
					break;
				case KeyEvent.KEYCODE_BACK:
					hideController();
					break;

				default:
					break;
				}
			}
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tv_scale_3:
			fullScreen();
			break;
		case R.id.tv_move:
			showPipFrame();
			mTvReminds.setText(R.string.reminds_move_operate);
			mSetPositionOrSize = true;
			break;
		case R.id.tv_scale_0:
		case R.id.tv_scale_1:
		case R.id.tv_scale_2:
			int i = (Integer) v.getTag();
			if (mScaleMode != i)
			{
				mScales[mScaleMode].setSelected(false);
				mScaleMode = i;
				mScales[mScaleMode].setSelected(true);
			}
		case R.id.tv_scale:
			showPipFrame();
			mSetPositionOrSize = false;
			switch (mScaleMode)
			{
			case 0:
				mTvReminds.setText(R.string.reminds_16_9_operate);
				break;
			case 1:
				mTvReminds.setText(R.string.reminds_4_3_operate);
				break;
			case 2:
				mTvReminds.setText(R.string.reminds_arbitrarily_operate);
				break;

			default:
				break;
			}
			break;
		case R.id.tv_audio:
		case R.id.rl_audio:
			mIsHdmiAudio = !mIsHdmiAudio;
			mHandler.removeMessages(MyHandler.SET_AUDIO);
			mHandler.sendEmptyMessageDelayed(MyHandler.SET_AUDIO, 500);
			mImgSwitch.setSelected(mIsHdmiAudio);
			break;
		case R.id.tv_exit:
			exit();
			break;

		default:
			break;
		}
	}
}
