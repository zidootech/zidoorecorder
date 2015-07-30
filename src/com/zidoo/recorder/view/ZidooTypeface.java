package com.zidoo.recorder.view;

import android.content.Context;
import android.graphics.Typeface;

/**
 * 
 * 
 * @author jiangbo
 * 
 *         2014-10-23
 * 
 */
public class ZidooTypeface {

	public static Typeface SIMPLIFIEDSTYLE = null;

	public static void initTypeface(Context context) {
		SIMPLIFIEDSTYLE = Typeface.createFromAsset(context.getAssets(),
				"fonts/roboto.ttf");
	}

}
