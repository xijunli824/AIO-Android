package org.cmaaio.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class XProcessBar extends RelativeLayout  {
	private ProgressBar process = null;
	private String downUrl = null;
	
	public XProcessBar(Context context) {
		super(context);
		initControls();
		// TODO Auto-generated constructor stub
	}
	public XProcessBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initControls();
		// TODO Auto-generated constructor stub
	}
	public XProcessBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initControls();
		// TODO Auto-generated constructor stub
	}

	private void initControls(){
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		process = new ProgressBar(this.getContext(),null,android.R.attr.progressBarStyleHorizontal);
		process.setLayoutParams(lp);
		process.setMax(100);
		process.setProgress(0);
		this.addView(process);
	}
	public void setDownUrl(String url){
		downUrl = url;
	}
	
	public void onDownProcess(String url,long have,long total){
		if(!downUrl.equals(url))
			return;
		int gress = (int)(((float)have/total)*100);
		process.setMax(100);
		process.setProgress(gress);
		if(this.getVisibility() == View.GONE)
			this.setVisibility(View.VISIBLE);
	}
	public void setProgress(float gress){
		process.setProgress((int)(gress*100));
	}
	
}
