package com.zidoo.recorder.tool;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class FileManagerTool {
	
	private Context context = null;

	public DeviceManager mDevices = null;

	public ScanUSBOnListener mScanUSBOnListener = null;

	public interface ScanUSBOnListener {
		void onStarScan();

		void onEndScan();

		void onAddUSB(String path);

		void onExitUSB(String path);
	}

	public void setmScanUSBOnListener(ScanUSBOnListener mScanUSBOnListener) {
		this.mScanUSBOnListener = mScanUSBOnListener;
	}

	public FileManagerTool(Context context) {
		super();
		this.context = context;

	}

	public void startUSB() {
		if (mScanUSBOnListener != null) {
			mScanUSBOnListener.onStarScan();
		}
		mDevices = new DeviceManager(context, mScanUSBOnListener);
		inItBroadCast();
	}

	public void onDestroy() {
		try {
			context.unregisterReceiver(mReceiver);
		} catch (Exception e) {
		}
	}

	private void inItBroadCast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addDataScheme("file");
		context.registerReceiver(mReceiver, filter);
	}

	/* 增加热插拔功能 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String tmpstring = intent.getData().getPath();
				System.out.println("tmpstring ===" + tmpstring);
				if (intent.getAction().equals(intent.ACTION_MEDIA_REMOVED)
						|| intent.getAction().equals(
								intent.ACTION_MEDIA_BAD_REMOVAL)) {
					if (mDevices != null) {
						String deviceFileInfo = mDevices
								.remountDevice(tmpstring);
						if (deviceFileInfo != null) {
							if (mScanUSBOnListener != null) {
								mScanUSBOnListener.onExitUSB(deviceFileInfo);
							}
						}
					}
				} else if (intent.getAction().equals(
						intent.ACTION_MEDIA_MOUNTED)) {
					if (mDevices != null) {
						ArrayList<String> add_deviceFileInfo_list = mDevices
								.addMountDevices(tmpstring, false);
						if (add_deviceFileInfo_list.size() > 0) {
							// add_deviceFileInfo_list.get(0)
							if (mScanUSBOnListener != null) {
								mScanUSBOnListener
										.onAddUSB(add_deviceFileInfo_list
												.get(0));
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};
}
