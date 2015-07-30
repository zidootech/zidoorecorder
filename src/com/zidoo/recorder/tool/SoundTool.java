package com.zidoo.recorder.tool;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.view.KeyEvent;

import com.zidoo.hdmi.recorder.R;

/**
 * 声音播放类
 * 
 * @author jiangbo
 * 
 *         2013-12-5
 */
public class SoundTool {
	static SoundPool soundPool = null;
	@SuppressLint("UseSparseArrays")
	static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

	public static boolean ISSOUND = true;

	// static Context context_s = null;

	/**
	 * 初始化
	 * 
	 * 
	 * @author jiangbo
	 * @param context
	 *            2013-12-5
	 */
	public static int initSound(Context context) {
		// / context_s = context;
		if (soundPool == null) {
			soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

			soundMap.put(1, soundPool.load(context, R.raw.up, 1));
			soundMap.put(2, soundPool.load(context, R.raw.soud_theme0, 1));
			soundMap.put(3, soundPool.load(context, R.raw.left, 1));
			soundMap.put(4, soundPool.load(context, R.raw.right, 1));
			soundMap.put(5, soundPool.load(context, R.raw.menu, 1));
			soundMap.put(6, soundPool.load(context, R.raw.back, 1));
			soundMap.put(7, soundPool.load(context, R.raw.dpad_center, 1));
		}
		try {
			Uri uri = ContentUris.withAppendedId(
					Uri.parse("content://com.zidoo.sound.provide/"), 1);
			String resutl = context.getContentResolver().getType(uri);
			if (resutl != null) {
				String resu[] = resutl.split("]");
				if (resu[0].equals("1")) {
					ISSOUND = true;
				} else {
					ISSOUND = false;
				}
				// 主题
				int thmem = Integer.valueOf(resu[1]);
				return thmem;
			} else {
				ISSOUND = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * 播放声音
	 * 
	 * @author jiangbo
	 * @param keyCode
	 *            2013-12-5
	 */
	public static void soundKey(int keyCode) {
		if (!ISSOUND) {
			return;
		}
		try {
			// AudioManager mAudioManager = (AudioManager) context_s
			// .getSystemService(Context.AUDIO_SERVICE);
			// // 当前音量
			// int currentVolume = mAudioManager
			// .getStreamVolume(AudioManager.STREAM_MUSIC);
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				soundPool.play(soundMap.get(2), 1, 1, 1, 0, 1);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				soundPool.play(soundMap.get(2), 1, 1, 1, 0, 1);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				soundPool.play(soundMap.get(2), 1, 1, 1, 0, 1);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				soundPool.play(soundMap.get(2), 1, 1, 1, 0, 1);
				break;
			case KeyEvent.KEYCODE_BACK:
				soundPool.play(soundMap.get(6), 1, 1, 1, 0, 1);
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				soundPool.play(soundMap.get(7), 1, 1, 1, 0, 1);
				break;
			case KeyEvent.KEYCODE_MENU:
				soundPool.play(soundMap.get(5), 1, 1, 1, 0, 1);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
