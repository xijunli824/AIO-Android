package org.cmaaio.common;

import java.util.ArrayList;

import org.cmaaio.activity.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class XTabBar extends RelativeLayout implements OnClickListener {
	private Activity parentActivity = null;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private int itemW = 0;//大于0使用凯德样式，等于0使用看牙样式显示
	private int itemColor = Color.argb(202, 202, 205, 205);
	private int textColor = Color.WHITE;
	private int currentIndex = 0;
	private ArrayList<String> itemList = null;
	private ArrayList<XButton> itemViewList = null;
	private TextView slid = null;
	private XTabBarListener listener = null;
	
	public XTabBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public XTabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public XTabBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public void getScreen(Activity activity){
		parentActivity = activity;
		WindowManager manager = activity.getWindowManager();
		screenWidth = manager.getDefaultDisplay().getWidth();
		screenHeight = manager.getDefaultDisplay().getHeight();	
	}
	public void setItemWidth(int width){
		itemW = width;
	}
	public void setItemColor(int bgColor,int textColor){
		this.itemColor = bgColor;
		this.textColor = textColor;
	}
	public void setXTabBarListener(XTabBarListener listener){
		this.listener = listener;
	}
	public void clearItem(){
		if(itemList == null || itemViewList == null)
			return;
		itemList.clear();
		itemViewList.clear();
		this.removeAllViews();
		currentIndex = 0;
		slid = null;
	}
	public void addItem(String item){
		if(itemList == null){
			itemList = new ArrayList<String>();
			itemViewList = new ArrayList<XButton>();
		}
		itemList.add(item);
	}
	public void showView(){
		int btnW = itemW > 0 ? itemW : screenWidth/itemList.size();
		slid = new TextView(this.getContext());
		RelativeLayout.LayoutParams slp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		slp.width = btnW;
		slp.leftMargin = 0;
		slp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		slid.setLayoutParams(slp);
		if(itemW > 0)
			slid.setBackgroundResource(R.drawable.add_top_btn_default);
		else
			slid.setBackgroundColor(Color.RED);
		this.addView(slid);
		
		for(int i=0;i<itemList.size();i++){
			XButton btn = new XButton(this.getContext());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			lp.width = btnW;
			lp.topMargin = 0;
			lp.leftMargin = i*btnW;
			btn.setLayoutParams(lp);
			btn.setBackgroundColor(itemColor);
			btn.setTextColor(textColor);
			btn.setText(itemList.get(i));
			btn.setTextSize(13);
			btn.setId(i);
			btn.setOnClickListener(this);
			this.addView(btn);
			itemViewList.add(btn);
			if(i != 0){
				TextView separator = new TextView(this.getContext());
				RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				tlp.width = 2;
				tlp.topMargin = 0;
				tlp.leftMargin = i*btnW-1;
				separator.setLayoutParams(tlp);
				separator.setBackgroundColor(itemW > 0 ? Color.TRANSPARENT:Color.WHITE);
				this.addView(separator);
			}
		}
		selectItem(0,false);
	}
	public void selectItem(int selectIndex,boolean isAnimation){
		if(itemW> 0)
			itemViewList.get(currentIndex).setTextColor(textColor);
		final int index = selectIndex;
		final int btnW = itemW > 0 ? itemW : screenWidth/itemList.size();
		AnimationTool tool = new AnimationTool();
		tool.moveView(slid, (index-currentIndex)*btnW, 0, isAnimation ? 200 : 0, new AnimationToolListener(){
			@Override
			public void onAnimationEnd() {
				// TODO Auto-generated method stub
				RelativeLayout.LayoutParams slp = (RelativeLayout.LayoutParams)slid.getLayoutParams();
				slp.leftMargin = index*btnW;
				slid.setLayoutParams(slp);
				if(itemW> 0)
					itemViewList.get(index).setTextColor(Color.WHITE);
			}});
		currentIndex = index;	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.selectItem(v.getId(),true);
		if(this.listener != null)
			listener.onSelectItem(v.getId());
	}
}
