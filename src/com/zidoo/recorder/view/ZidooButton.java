package com.zidoo.recorder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ZidooButton extends Button {

	public ZidooButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ZidooButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZidooButton(Context context) {
		super(context);
		init();
	}

	private void init() {
		this.setTypeface(ZidooTypeface.SIMPLIFIEDSTYLE);
	}

}
