package org.cmaaio.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

/**

 */

public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {
	private RadioGroup mainTab;
	private TabHost mTabHost;

	// Intent
	private Intent mHomeIntent; // 首页
	private Intent mContactIntent; // 通讯录
	private Intent mSettingIntent; // 设置
	private Intent mAddAppIntent; // 应用添加
	private Intent mSignatureIntent; // 批注

	private final static String TAB_TAG_HOME = "tab_tag_home";
	private final static String TAB_TAG_CONTACT = "TAB_TAG_CONTACT";
	private final static String TAB_TAG_SETTING = "TAB_TAG_SETTING";
	private final static String TAB_TAG_ADDAPP = "TAB_TAG_ADDAPP";
	private final static String TAB_TAG_SIGN = "TAB_TAG_SIGN";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		mainTab = (RadioGroup) findViewById(R.id.main_tab);
		mainTab.setOnCheckedChangeListener(this);
		prepareIntent();
		setupIntent();
	}

	/**
	 * 鍑嗗tab鐨勫唴瀹笽ntent
	 */

	private void prepareIntent() {
		mHomeIntent = new Intent(this, HomeActivity.class);
		mContactIntent = new Intent(this, ContactsActivity.class);
		mSettingIntent = new Intent(this, SettingActivity.class);
		mAddAppIntent = new Intent(this, AddAppActivity.class);
		mSignatureIntent = new Intent(this, SignatureActivity.class);
	}

	/**
	 * 
	 */
	private void setupIntent() {
		Constant.tabHost = getTabHost();
		this.mTabHost = Constant.tabHost;
		TabHost localTabHost = this.mTabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_HOME, R.string.tabhost_mess,
				R.drawable.home_but, mHomeIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_CONTACT,
				R.string.tabhost_mess, R.drawable.contact_but, mContactIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_SETTING,
				R.string.tabhost_mess, R.drawable.setting_but, mSettingIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_ADDAPP, R.string.tabhost_mess,
				R.drawable.home_but, mAddAppIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_SIGN, R.string.tabhost_mess,
				R.drawable.home_but, mSignatureIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	private Bitmap shot() {
		View view = getWindow().getCurrentFocus();
		Display display = this.getWindowManager().getDefaultDisplay();
		view.layout(0, 0, display.getWidth(), display.getHeight());
		view.setDrawingCacheEnabled(true);// 允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
		return bmp;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button0:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_HOME);

			break;
		case R.id.radio_button1:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_CONTACT);
			break;
		case R.id.radio_button2:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_SETTING);
			break;
		case R.id.radio_button3:
			this.mTabHost.setCurrentTabByTag(TAB_TAG_ADDAPP);
			break;
		case R.id.radio_button4:
			if (Constant.bitMap != null) {
				Constant.bitMap = null;
			}
			Constant.bitMap = shot();
			this.mTabHost.setCurrentTabByTag(TAB_TAG_SIGN);
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		new AlertDialog.Builder(this).setTitle(R.string.prompt).setMessage(R.string.exit_alert)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(R.string.confirm_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“确认”后的操作
						finish();
					}
				})
				.setNegativeButton(R.string.confirm_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击“返回”后的操作,这里不设置没有任何操作
					}
				}).show();
	}
}