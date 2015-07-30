package com.zidoo.recorder.tool;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zidoo.hdmi.recorder.R;

public class SoftAdatper extends BaseAdapter {

	private ArrayList<SoftInfo> soft_list = new ArrayList<SoftInfo>();

	private Context mContext = null;

	private LayoutInflater mInflater = null;

	public SoftAdatper(ArrayList<SoftInfo> soft_list, Context mContext) {
		super();
		this.soft_list = soft_list;
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return soft_list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		SoftAdapterView softAdapterView = null;
		if (convertView == null) {
			softAdapterView = new SoftAdapterView();
			convertView = mInflater.inflate(R.layout.soft_adatper_view, null);
			softAdapterView.isReImageView = (ImageView) convertView
					.findViewById(R.id.soft_adatper_view_statu);
			softAdapterView.name = (TextView) convertView
					.findViewById(R.id.soft_adatper_view_name);
			softAdapterView.size = (TextView) convertView
					.findViewById(R.id.soft_adatper_view_size);
			// softAdapterView.statu = (TextView) convertView
			// .findViewById(R.id.soft_adatper_view_statu);
			convertView.setTag(softAdapterView);
		} else {
			softAdapterView = (SoftAdapterView) convertView.getTag();
		}
		SoftInfo softInfo = soft_list.get(position);
		if (softInfo.isRecorder()) {
			softAdapterView.isReImageView.setVisibility(View.VISIBLE);
			softAdapterView.size.setVisibility(View.GONE);
		} else {
			softAdapterView.isReImageView.setVisibility(View.GONE);
			softAdapterView.size.setVisibility(View.VISIBLE);
			softAdapterView.size.setText("Size: " + softInfo.getTotal_Size());
		}
		softAdapterView.name.setText(softInfo.getNameString());
		
		// softAdapterView.size.setText("size：" + softInfo.getTotal_Size()
		// + "\t\t" + "time：" + softInfo.getPlayTime());

		return convertView;
	}

	class SoftAdapterView {
		// ImageView icon;
		TextView name;
		TextView size;
		ImageView isReImageView ;
		// TextView statu;
	}

}
