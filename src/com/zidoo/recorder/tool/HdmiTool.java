package com.zidoo.recorder.tool;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.zidoo.hdmi.recorder.R;

/**
 * 
 * @author jiangbo
 * 
 *         2014-9-17
 * 
 */
public class HdmiTool
{

	private Context					mContext			= null;

	private boolean					isShow				= false;
	private SurfaceView				surfaceView			= null;
	private RelativeLayout			mNoInforRe			= null;
	private LinearLayout			mMenuHitLi			= null;
	public LinearLayout				mRecording			= null;

	private boolean					isFirstHdmi			= true;

	private SurfaceHolder.Callback	callback			= null;
	private SurfaceHolder			mSurfaceHolder		= null;

	private ZidooRecorderTool		mZidooRecorderTool	= null;

	public static boolean			isShowHDMI			= false;

	private TvPictureManager		mPicturemanger;

	public HdmiTool(Context mContext, boolean isRecorder)
	{
		super();
		this.mContext = mContext;
		surfaceView = (SurfaceView) ((Activity) mContext).findViewById(R.id.home_ac_downView_himiin_surfaceView);
		mNoInforRe = (RelativeLayout) ((Activity) mContext).findViewById(R.id.home_ac_noinfor);
		RelativeLayout mAllView = (RelativeLayout) ((Activity) mContext).findViewById(R.id.home_ac_downView_himiin_re);
		mAllView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				disRecorderMenu();
			}
		});
		mMenuHitLi = (LinearLayout) ((Activity) mContext).findViewById(R.id.home_ac_menue);
		mRecording = (LinearLayout) ((Activity) mContext).findViewById(R.id.home_ac_recording);
		mPicturemanger = TvPictureManager.getInstance();
		startHdmi();
		mZidooRecorderTool = new ZidooRecorderTool(mContext, this, isRecorder);
	}

	public int[] getVideInfo()
	{
		try
		{
			if (mPicturemanger != null)
			{
				VideoInfo videoInfo = mPicturemanger.getVideoInfo();
				if (videoInfo != null)
				{
					System.out.println("bob  h " + videoInfo.hResolution);
					System.out.println("bob  v " + videoInfo.vResolution);
					int resolution[] = new int[2];
					resolution[0] = videoInfo.hResolution;
					resolution[1] = videoInfo.vResolution;
					return resolution;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void startHdmi()
	{
		showHdmiOnSurfaceView();
		isShowHDMI = isDisHDMI();
		System.out.println("bob  isShowHDMI = " + isShowHDMI);
		mHandler.sendEmptyMessageDelayed(HIDE_SHOW, 1 * 1500);
	}

	Timer		timer		= new Timer();
	TimerTask	timerTast	= null;

	private void startChenkHDMI()
	{
		if (timerTast != null)
		{
			timerTast.cancel();
			timerTast = null;
		}
		timerTast = new TimerTask()
		{
			public void run()
			{
				isShowHDMI = isDisHDMI();
				if (isShowHDMI)
				{
					if (!isShow)
					{
						mHandler.sendEmptyMessage(DIS_HIME);
					}
				} else
				{
					if (isShow)
					{
						mHandler.sendEmptyMessage(HIDE_HIME);
					}
				}
			}
		};
		timer.schedule(timerTast, 2 * 1000, 2 * 1000);
	}

	// private ZidooRecorderTool mZidooRecorderTool = null;

	public void disRecorderMenu()
	{
		if (!isShow)
		{
		}
		if (mZidooRecorderTool != null)
		{
			mZidooRecorderTool.showDialog();
		}
	}

	public boolean back()
	{
		if (mZidooRecorderTool != null && mZidooRecorderTool.mRecording)
		{
			mZidooRecorderTool.exit();
			return true;
		}
		return false;
	}

	public void onResume()
	{
		startChenkHDMI();
	}

	public void onPause()
	{
		try
		{
			if (timerTast != null)
			{
				timerTast.cancel();
				timerTast = null;
			}
			changeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onDestroy()
	{
		try
		{
			if (timerTast != null)
			{
				timerTast.cancel();
				timerTast = null;
			}
			changeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
			hindHdmi();
			mZidooRecorderTool.onDestroy();
			changeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void disHdmi()
	{
		isShow = true;
		surfaceView.setVisibility(View.VISIBLE);
		mNoInforRe.setVisibility(View.GONE);

		changeInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI);

		setFillScale();

		if (mZidooRecorderTool != null)
		{
			int resolution[] = getVideInfo();
			if (resolution == null)
			{
				mZidooRecorderTool.setResolution("Unknown");
			} else
			{
				if (resolution[0] == 1920 && resolution[1] == 1080)
				{
					mZidooRecorderTool.setResolution("1080P");
				} else if (resolution[0] == 1280 && resolution[1] == 720)
				{
					mZidooRecorderTool.setResolution("720P");
				} else
				{
					mZidooRecorderTool.setResolution(resolution[0] + " x " + resolution[1]);
				}
			}

		}

		// if (isFirstHdmi) {
		// mMenuHitLi.setVisibility(View.VISIBLE);
		// mHandler.sendEmptyMessageDelayed(HIT_DIS, 4 * 1000);
		// } else {
		// mMenuHitLi.setVisibility(View.GONE);
		// }
		// isFirstHdmi = false;
		disHintText();
	}

	public void hindHdmi()
	{
		isShow = false;
		surfaceView.setVisibility(View.GONE);
		mNoInforRe.setVisibility(View.VISIBLE);
		if (mZidooRecorderTool != null)
		{
			mZidooRecorderTool.setResolution(mContext.getString(R.string.no_infor));
		}
	}

	public void disHintText()
	{
		if (isFirstHdmi)
		{
			mMenuHitLi.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessageDelayed(HIT_DIS, 6 * 1000);
		} else
		{
			mMenuHitLi.setVisibility(View.GONE);
		}
		isFirstHdmi = false;
	}

	private final static int	DIS_HIME	= 0;
	private final static int	HIDE_HIME	= 1;
	private final static int	HIDE_SHOW	= 2;
	private final static int	HIT_DIS		= 3;
	Handler						mHandler	= new Handler()
											{
												public void handleMessage(android.os.Message msg)
												{
													switch (msg.what)
													{
													case DIS_HIME:
														disHdmi();
														if (mZidooRecorderTool != null)
														{
															mZidooRecorderTool.reRecord();
														}
														break;
													case HIDE_HIME:
														if (mZidooRecorderTool != null)
														{
															mZidooRecorderTool.stopRecordIng();
														}
														hindHdmi();
														break;
													case HIDE_SHOW:

														isShowHDMI = isDisHDMI();
														if (isShowHDMI)
														{
															disHdmi();
														} else
														{
															hindHdmi();
														}
														startChenkHDMI();
														// disRecorderMenu();
														break;
													case HIT_DIS:
														mMenuHitLi.setVisibility(View.GONE);
														break;

													default:
														break;
													}
												};
											};

	public void setFillScale()
	{
		// hdmi in
		VideoWindowType videoWindowType = new VideoWindowType();

		videoWindowType.x = 0xffff;
		videoWindowType.y = 0xffff;
		videoWindowType.width = 0xffff;
		videoWindowType.height = 0xffff;
		if (TvManager.getInstance() != null)
		{

			PictureManager pictureManager = TvManager.getInstance().getPictureManager();
			if (pictureManager != null)
			{
				try
				{
					pictureManager.selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
					pictureManager.setDisplayWindow(videoWindowType);
					pictureManager.scaleWindow();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	public static boolean isDisHDMI()
	{
		boolean bRet = false;
		try
		{
			changeInputSource(EnumInputSource.E_INPUT_SOURCE_HDMI);
			bRet = TvManager.getInstance().getPlayerManager().isSignalStable();
		} catch (TvCommonException e)
		{
			e.printStackTrace();
		}
		return bRet;
	}

	public static void changeInputSource(EnumInputSource eis)
	{

		TvCommonManager commonService = TvCommonManager.getInstance();
		if (commonService != null)
		{
			EnumInputSource currentSource = commonService.getCurrentInputSource();
			if (currentSource != null)
			{
				if (currentSource.equals(eis))
				{
					return;
				}

				commonService.setInputSource(eis);
			}

		}

	}

	private void showHdmiOnSurfaceView()
	{
		mSurfaceHolder = surfaceView.getHolder();
		callback = new android.view.SurfaceHolder.Callback()
		{

			@Override
			public void surfaceDestroyed(SurfaceHolder holder)
			{
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder)
			{
				try
				{
					if (holder == null || holder.getSurface() == null || holder.getSurface().isValid() == false)
					{
						return;
					}
					if (TvManager.getInstance() != null)
					{
						TvManager.getInstance().getPlayerManager().setDisplay(mSurfaceHolder);
					}
				} catch (TvCommonException e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
			{
			}
		};
		mSurfaceHolder.addCallback((android.view.SurfaceHolder.Callback) callback);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
}
