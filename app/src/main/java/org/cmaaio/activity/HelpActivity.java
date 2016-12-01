package org.cmaaio.activity;

import java.util.ArrayList;

import org.cmaaio.common.AnimationTool;
import org.cmaaio.util.ActivityManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class HelpActivity extends CMABaseActivity implements OnClickListener {
	private Button startBtn = null;
	private LinearLayout pageControl = null;
	private ArrayList<ImageView> pageControlList = null;
	private int kPageNum = 0;
	private int curPage = 0;

	private Gallery xgallery = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		initControls();
		xgallery = (Gallery) findViewById(R.id.gallery_view);
		GallerAdapter adapter = new GallerAdapter();
		xgallery.setAdapter(adapter);
		xgallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 5) {
					startBtn.setVisibility(View.VISIBLE);
				} else {
					startBtn.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		ActivityManager.getInstance().addActivity(this);
	}

	private void initControls() {
		kPageNum = 6;
		startBtn = (Button) this.findViewById(R.id.startBtn);
		startBtn.setOnClickListener(this);
		pageControl = (LinearLayout) this.findViewById(R.id.pageControl);
		initPageControl();
	}

	

	private void initPageControl() {
		pageControlList = new ArrayList<ImageView>();
		for (int i = 0; i < kPageNum; i++) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.width = AnimationTool.dip2px(this, 6);
			lp.height = AnimationTool.dip2px(this, 6);
			int margin = AnimationTool.dip2px(this, 5);
			lp.leftMargin = margin;
			lp.rightMargin = margin;
			ImageView view = new ImageView(this);
			view.setLayoutParams(lp);
			view.setEnabled(false);
			view.setBackgroundResource(R.drawable.dot);
			pageControl.addView(view);
			pageControlList.add(view);
		}
		pageControlList.get(curPage).setEnabled(true);
	}

	

	@Override
	public void onClick(View v) {
		if (v.equals(startBtn)) {
			// 保存已读
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = sp.edit();
			editor.putInt("isReadFlg", 1);
			editor.commit();

			Intent intent = new Intent();
			intent.setClass(HelpActivity.this, HomeActivity.class);
			startActivity(intent);
			this.finish();
		}
	}



	private class GallerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			ImageView imagePage = new ImageView(HelpActivity.this);
			imagePage.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
			imagePage.setScaleType(ScaleType.FIT_XY);
			if (position == 0) {
				imagePage.setImageResource(R.drawable.imagehelp0);
			} else if (position == 1) {
				imagePage.setImageResource(R.drawable.imagehelp1);
			} else if (position == 2) {
				imagePage.setImageResource(R.drawable.imagehelp2);
			} else if (position == 3) {
				imagePage.setImageResource(R.drawable.imagehelp3);
			} else if (position == 4) {
				imagePage.setImageResource(R.drawable.imagehelp4);
			} else if (position == 5) {

				imagePage.setImageResource(R.drawable.imagehelp5);
			}
			
			return imagePage;
		}

	}
}
