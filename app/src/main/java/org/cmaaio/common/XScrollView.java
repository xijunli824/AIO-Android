package org.cmaaio.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class XScrollView extends ScrollView {
	private GestureDetector mGestureDetector;   
	View.OnTouchListener mGestureListener;

	public XScrollView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	public XScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}
	public XScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}
	@Override  
	public boolean onInterceptTouchEvent(MotionEvent ev) {   
		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);   
	}

	private void init(){
		mGestureDetector = new GestureDetector(new YScrollDetector());   
		setFadingEdgeLength(0);   

	}
	class YScrollDetector extends SimpleOnGestureListener {   
		@Override  
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {   
			if(Math.abs(distanceY) > Math.abs(distanceX)) {   
		       return true;   
			}   
			return false;   
		}   
	}   

}
