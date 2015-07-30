package com.zidoo.recorder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ZidooTextView extends TextView {

	public ZidooTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ZidooTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZidooTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		this.setTypeface(ZidooTypeface.SIMPLIFIEDSTYLE);
	}

}
