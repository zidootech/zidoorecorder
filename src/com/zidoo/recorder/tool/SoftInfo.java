package com.zidoo.recorder.tool;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class SoftInfo implements Serializable {
	private String nameString = "";
	private String pathString = "";
	private String Total_Size = "";

	private String playTime = "";

	// 是否正在录制
	private boolean isRecorder = false;

	public SoftInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public String getPathString() {
		return pathString;
	}

	public void setPathString(String pathString) {
		this.pathString = pathString;
	}

	public boolean isRecorder() {
		return isRecorder;
	}

	public void setRecorder(boolean isRecorder) {
		this.isRecorder = isRecorder;
	}

	public String getTotal_Size() {
		return Total_Size;
	}

	public void setTotal_Size(String total_Size) {
		Total_Size = total_Size;
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

}
