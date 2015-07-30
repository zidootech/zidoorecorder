package com.zidoo.recorder.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zidoo.recorder.service.RecorderService;

/**
 * 
 * 开机广播
 * 
 * @author jiangbo
 * 
 *         2014-6-10
 */
public class BootBroadcast extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// 开机广播
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			context.startService(new Intent(context, RecorderService.class));
		}
	}
}
