package org.cmaaio.activity;

import org.cmaaio.common.Constants;
import org.cmaaio.common.XButton;
import org.cmaaio.db.SharedPrefsConfig;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CMABaseActivity extends Activity {
	public XButton backBtn = null;
	private TextView titleView = null;
	public XButton rightBtn = null;
	private RadioButton homeBtn = null;
	private RadioButton contactBtn = null;
	private RadioButton setBtn = null;
	private RadioButton addBtn = null;
	private RadioButton drawBtn = null;
	private int tabBarH = 0;
	private int navBarH = 0;
	public int groupIndex = -1;// 用来标识当前activity属于tabbar中的哪个组

	private int indexNu = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final float scale = this.getResources().getDisplayMetrics().density;
		tabBarH = (int) (60 * scale + 0.5f);// 在布局文件中为60dip，这里转换为px
	}

	@Override
	public void onResume() {
		super.onResume();
		setCurrentTabbar(groupIndex);
	}

	public void setCurrentTabbar(int index) {
		if (index == 0 && homeBtn != null)
			homeBtn.setChecked(true);
		if (index == 1 && contactBtn != null)
			contactBtn.setChecked(true);
		if (index == 2 && setBtn != null)
			setBtn.setChecked(true);
		if (index == 3 && addBtn != null)
			addBtn.setChecked(true);
		if (index == 4 && drawBtn != null)
			drawBtn.setChecked(true);
	}

	//
	public void initTaBar() {
		homeBtn = (RadioButton) this.findViewById(R.id.homeRadio);
		contactBtn = (RadioButton) this.findViewById(R.id.contactRadio);
		setBtn = (RadioButton) this.findViewById(R.id.setRadio);
		addBtn = (RadioButton) this.findViewById(R.id.addRadio);
		drawBtn = (RadioButton) this.findViewById(R.id.drawRadio);
		homeBtn.setOnClickListener(onTabbarClick);
		contactBtn.setOnClickListener(onTabbarClick);
		setBtn.setOnClickListener(onTabbarClick);
		addBtn.setOnClickListener(onTabbarClick);
		drawBtn.setOnClickListener(onTabbarClick);
	}

	public void initTaBar(int group) {
		this.groupIndex = group;
		homeBtn = (RadioButton) this.findViewById(R.id.homeRadio);
		contactBtn = (RadioButton) this.findViewById(R.id.contactRadio);
		setBtn = (RadioButton) this.findViewById(R.id.setRadio);
		addBtn = (RadioButton) this.findViewById(R.id.addRadio);
		drawBtn = (RadioButton) this.findViewById(R.id.drawRadio);
		homeBtn.setOnClickListener(onTabbarClick);
		contactBtn.setOnClickListener(onTabbarClick);
		setBtn.setOnClickListener(onTabbarClick);
		addBtn.setOnClickListener(onTabbarClick);
		drawBtn.setOnClickListener(onTabbarClick);
	}

	public void initNavBar(String title, int rightImg) {
		backBtn = (XButton) this.findViewById(R.id.backBtn);
		if (backBtn != null)
			backBtn.setOnClickListener(onNavClick);
		rightBtn = (XButton) this.findViewById(R.id.rightBtn);
		if (rightBtn != null && rightImg != -1) {
			rightBtn.setOnClickListener(onNavClick);
			rightBtn.setBackgroundResource(rightImg);
			rightBtn.setVisibility(View.VISIBLE);
		}
		if (rightImg == -1)
			rightBtn.setVisibility(View.GONE);
		titleView = (TextView) this.findViewById(R.id.nav_title);
		if (titleView != null)
			titleView.setText(title);
	}

	// 子类覆盖该函数必须调用super的改函数,否则会影响涂鸦功能
	public void onSelectTab(int index) {
		if (this.groupIndex == index)
			return;
		if (!this.getClass().equals(HomeActivity.class)
				&& !this.getClass().equals(ContactsActivity.class)
				&& !this.getClass().equals(SettingActivity.class)
				&& !this.getClass().equals(AddAppActivity.class)
				&& !this.getClass().equals(SignatureActivity.class)) {
			// this.finish();
		}
		Intent intent = new Intent();
		if (index == 0) {
			intent.setClass(CMABaseActivity.this, HomeActivity.class);
		}
		if (index == 1) {
			intent.setClass(CMABaseActivity.this, ContactsActivity.class);
		}
		if (index == 2) {
			intent.setClass(CMABaseActivity.this, SettingActivity.class);
		}
		if (index == 3) {
			intent.setClass(CMABaseActivity.this, AddAppActivity.class);
		}
		if (index == 4) {// 秦调用涂鸦功能
			if (this.getClass().equals(SignatureActivity.class))
				return;
			if (Constant.bitMap != null) {
				Constant.bitMap.recycle();
				Constant.bitMap = null;
			}
			Constant.bitMap = shot();
			intent.setClass(CMABaseActivity.this, SignatureActivity.class);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		if (index == 3) {
			this.overridePendingTransition(R.anim.up_in, R.anim.down_out);
		} else {
			this.overridePendingTransition(0, 0);// 无动画
		}
	}

	private Bitmap shot() {
		Rect frame = new Rect();
		View view = this.getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame(frame);
		navBarH = frame.top;
		Display display = this.getWindowManager().getDefaultDisplay();
		view.setDrawingCacheEnabled(true);// 允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
		Bitmap allBmp = view.getDrawingCache();
		@SuppressWarnings("deprecation")
		Bitmap bmp = Bitmap.createBitmap(allBmp, 0, navBarH,
				display.getWidth(), display.getHeight() - tabBarH - navBarH);
		view.destroyDrawingCache();
		return bmp;
	}

	private OnClickListener onTabbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(homeBtn))
				onSelectTab(0);
			if (v.equals(contactBtn))
				if (SharedPrefsConfig
						.getSharedPrefsInstance(CMABaseActivity.this)
						.getUserName().startsWith("w_")) {
					Toast.makeText(CMABaseActivity.this,
							getString(R.string.modify_no_contract_permit),
							Toast.LENGTH_SHORT).show();
				} else {
					onSelectTab(1);
				}
			if (v.equals(setBtn))
				onSelectTab(2);
			if (v.equals(addBtn))
				if (Constants.isOfflineLogin()) {
					Toast.makeText(CMABaseActivity.this, R.string.offline_mode,
							Toast.LENGTH_SHORT).show();
					addBtn.setChecked(false);
					setCurrentTabbar(groupIndex);
				} else {
					onSelectTab(3);
				}

			if (v.equals(drawBtn))
				if (Constants.isOfflineLogin()) {
					Toast.makeText(CMABaseActivity.this, R.string.offline_mode,
							Toast.LENGTH_SHORT).show();
					drawBtn.setChecked(false);
					setCurrentTabbar(groupIndex);
				} else {
					onSelectTab(4);
				}
		}
	};

	public void onRightAction() {

	}

	private OnClickListener onNavClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(backBtn))
				CMABaseActivity.this.finish();
			if (v.equals(rightBtn))
				onRightAction();
		}
	};
}
