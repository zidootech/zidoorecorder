package com.zidoo.recorder.tool;

import java.io.Serializable;

import com.mstar.hdmirecorder.HdmiRecorder;

public class RecorderInfo implements Serializable{
	String rootPath = "";

	String name = "";

	public String path = "";

	/**
	 * 分辨率
	 */
	int resolution = ZidooRecorderTool.VIDEO_HD;
	/**
	 * 格式
	 */
	int format = HdmiRecorder.FORMAT_MP4;
	/**
	 * 录像长度时间 -1 为自动
	 */
	long recorderTime = -1;
	//视频长度
	String recorderTime_str = "--:--:--";
	/**
	 * 预约时间
	 */
	public String order_time = "--:--:--";
	
	long order_time_ms = -1;
	/**
	 * 当前时间
	 */
	String current_time = "--:--:--";
	/**
	 * 状态 1 正在录像 2 预约录像
	 */
	int statu = 0;
	@Override
	public String toString() {
		return "RecorderInfo [rootPath=" + rootPath + ", name=" + name
				+ ", path=" + path + ", resolution=" + resolution + ", format="
				+ format + ", recorderTime=" + recorderTime
				+ ", recorderTime_str=" + recorderTime_str + ", order_time="
				+ order_time + ", order_time_ms=" + order_time_ms
				+ ", current_time=" + current_time + ", statu=" + statu + "]";
	}
	
	
	
}
