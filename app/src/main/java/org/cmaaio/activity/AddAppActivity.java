package org.cmaaio.activity;

import java.util.ArrayList;
import java.util.Locale;

import org.cmaaio.adapter.AppItemAdapter;
import org.cmaaio.common.AppDataController;
import org.cmaaio.common.DownLoadListener;
import org.cmaaio.common.DownLoadManager;
import org.cmaaio.common.XButton;
import org.cmaaio.common.XProcessBar;
import org.cmaaio.common.XTabBar;
import org.cmaaio.common.XTabBarListener;
import org.cmaaio.entity.AppListEntity;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.httputil.DataParser;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.sysdownloadmanager.CMAAPPInatallerManager;
import org.cmaaio.sysdownloadmanager.PKGInstallReceiver;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CMATool;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AddAppActivity extends CMABaseActivity {
	/** Called when the activity is first created. */
	private boolean isAdd = false;
	private ListView listView;
	private AppItemAdapter appAdapter = null;
	private ArrayList<CMAAppEntity> appList = null;
	ArrayList<AppListEntity> resultData = null;
	private HorizontalScrollView topTabBarScroll = null;
	private XTabBar topTabBar = null;
	private XButton previousBtn = null;
	private XButton nextBtn = null;
	int xh_count = 0;

	ProgressDialog xh_pDialog;
	private PKGInstallReceiver nativeInstallReceiver;
	public static String APPINSTALLACTION = "org.caaio.appinstallaction";

	private ArrayList<String> catList = new ArrayList<String>();
	private String category = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.add_app);
		super.initTaBar(3);
		super.initNavBar(this.getResources().getString(R.string.app_title), R.drawable.reflesh_button_selector);
		initControls();
		getAppList();
		// initTestData();
		DownLoadManager.getInstance().setDownListener(downListener);
		// 注册
		nativeInstallReceiver = new PKGInstallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		filter.addDataScheme("package");
		registerReceiver(nativeInstallReceiver, filter);

		// 安装监听
		IntentFilter appFilter = new IntentFilter();
		appFilter.addAction(APPINSTALLACTION);
		registerReceiver(appInstallReceiver, appFilter);

		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(nativeInstallReceiver);
		unregisterReceiver(appInstallReceiver);
		DownLoadManager.getInstance().setDownListener(null);
		if (downloadingDialog != null) {
			downloadingDialog.dismiss();
		}
		super.onDestroy();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (appAdapter != null) {
			appAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// override
	@Override
	public void onRightAction() {
		super.onRightAction();
		getAppList();

	}

	private void initControls() {

		// 当base里的ui无法满足时要重写rightbtn的布局,默认rightbtn是会缩放图片的
		this.rightBtn.setPadding(0, 0, 0, 0);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			this.rightBtn.setScaleX(1);
			this.rightBtn.setScaleY(1);
		}
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.rightBtn.getLayoutParams();
		lp.rightMargin = CMATool.dip2px(this, 10);
		this.rightBtn.setLayoutParams(lp);
		this.rightBtn.setText(R.string.reflesh_btn);
		this.rightBtn.setTextSize(14);
		// this.rightBtn.setOnClickListener(refleshBtnListener);

		this.backBtn.setVisibility(View.GONE);

		appList = new ArrayList<CMAAppEntity>();
		appAdapter = new AppItemAdapter(this, appList);
		appAdapter.setOnItemBtnClick(appOpListener);

		listView = (ListView) findViewById(R.id.listView_app);
		listView.setAdapter(appAdapter);
		listView.setVerticalScrollBarEnabled(false);
		listView.setOnItemClickListener(listener);
		listView.setDividerHeight(0);

		topTabBarScroll = (HorizontalScrollView) this.findViewById(R.id.topTabBarScroll);
		topTabBar = (XTabBar) this.findViewById(R.id.tabBar);
		topTabBar.setXTabBarListener(onTabBarSelect);
		topTabBar.setItemWidth(120);
		topTabBar.setItemColor(Color.TRANSPARENT, Color.BLACK);
		topTabBar.getScreen(this);// 必须在add后

		previousBtn = (XButton) this.findViewById(R.id.lastBtn);
		nextBtn = (XButton) this.findViewById(R.id.nextBtn);
		previousBtn.setOnClickListener(xButtonListener);
		nextBtn.setOnClickListener(xButtonListener);
	}

	private void setAppList() {
		if (resultData.size() == 0) {
			return;
		}
		catList.clear();
		topTabBar.clearItem();
		for (int i = 0; i < resultData.size(); i++) {
			String[] categorys = resultData.get(i).appCategory.split("@");
			if (categorys.length == 2) {
				String lau = Locale.getDefault().getLanguage();
				if (lau.equals("zh")) {
					topTabBar.addItem(categorys[1]);
					catList.add(categorys[1]);
				} else {
					topTabBar.addItem(categorys[0]);
					catList.add(categorys[0]);
				}
			} else if (categorys.length == 1) {
				topTabBar.addItem(categorys[0]);
				catList.add(categorys[0]);
			}
		}
		// 初始化类型
		if (catList.size() > 0) {
			this.category = catList.get(0);
		}
		topTabBar.showView();
		appList.clear();
		appList.addAll(resultData.get(0).appList);
		appAdapter.dataSource = appList;
		appAdapter.notifyDataSetChanged();
	}

	private void getAppList() {
		HttpUtil con = new HttpUtil();
		con.context = this;
		con.iResult = onNetResult;
		con.httpGetAppList();
	}

	private OnClickListener xButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int scrollX = 0;
			if (v.equals(previousBtn))
				scrollX = -1 * CMATool.dip2px(AddAppActivity.this, 80);
			if (v.equals(nextBtn))
				scrollX = CMATool.dip2px(AddAppActivity.this, 80);
			if (scrollX != 0)
				topTabBarScroll.smoothScrollBy(scrollX, 0);
		}
	};
	private XTabBarListener onTabBarSelect = new XTabBarListener() {
		@Override
		public void onSelectItem(int index) {
			appList.clear();
			category = catList.get(index);
			appList.addAll(resultData.get(index).appList);
			appAdapter.dataSource = appList;
			appAdapter.notifyDataSetChanged();
		}
	};
	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CMAAppEntity app = appAdapter.dataSource.get(position);
			Intent intent = new Intent();
			intent.setClass(AddAppActivity.this, AppDetailActivity.class);
			intent.putExtra("Appid", app.appId);
			intent.putExtra("Category", category);
			AddAppActivity.this.startActivity(intent);
			// finish();
		}
	};
	private OnClickListener appOpListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			String btnText = btn.getText().toString();
			int index = Integer.parseInt(v.getTag().toString());
			CMAAppEntity app = appAdapter.dataSource.get(index);

			// 如果是web类型 app刚不用下载
			if (CMAAppEntity.WEBAPP.equals(app.appType)) {
				app.appPath = app.appDownUrl;// such as wwww.baidu.com
				if(AppDataController.getInstance().contain(app))
				{
					AppDataController.getInstance().remove(app);
				}
				AppDataController.getInstance().add(app);
				appAdapter.notifyDataSetChanged();
				// 回首页
				Intent intent = new Intent(AddAppActivity.this, HomeActivity.class);
				startActivity(intent);
				CMAAPPInatallerManager.uploadDownloadRecord(app);// 上传下载记录
				CMAAPPInatallerManager.userManageAppPush(app, 1);//推送
			} else {
				// if (btnText.equals(AddAppActivity.this.getResources()
				// .getString(R.string.app_but_mes_goon))
				// || btnText.equals(AddAppActivity.this.getResources()
				// .getString(R.string.app_but_mes_add))) {
				// if (!DownLoadManager.getInstance()
				// .isDowning(app.appDownUrl)) {
				// DownLoadManager.getInstance().startDownLoad(
				// app.appDownUrl);
				// }
				//
				// btn.setText(AddAppActivity.this.getResources().getString(
				// R.string.app_but_mes_pause));
				//
				// } else if (btnText.equals(AddAppActivity.this.getResources()
				// .getString(R.string.app_but_mes_pause))) {
				// DownLoadManager.getInstance().stopDownLoad(app.appDownUrl);
				// btn.setText(AddAppActivity.this.getResources().getString(
				// R.string.app_but_mes_goon));
				showDownload(app.appDownUrl);
				// }
			}
		}
	};
	private DownLoadListener downListener = new DownLoadListener() {

		@Override
		public void onProcess(final String url, final long have, final long total) {
			for (int i = 0; i < listView.getChildCount(); i++) {
				View child = listView.getChildAt(i);
				XProcessBar downPrcessBar = (XProcessBar) child.findViewById(R.id.downProcessBar);
				if (downPrcessBar != null)
					downPrcessBar.onDownProcess(url, have, total);
			}
			if (dialogDownPrcessBar != null)
				Cmaaio.getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						dialogDownPrcessBar.onDownProcess(url, have, total);
					}
				});

		}

		@Override
		public void onFinish(String url, String sdPath) {
			try {
				CMAAppEntity entity = null;
				for (CMAAppEntity app : appAdapter.dataSource) {
					if (app.appDownUrl.equals(url)) {
						entity = app;
						break;
					}
				}
				CMAAPPInatallerManager.install(AddAppActivity.this, url, sdPath, entity, appAdapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private IResult onNetResult = new IResult() {

		@Override
		public void OnResult(String jsonStr) {
			resultData = DataParser.parseAppList(jsonStr);
			if (resultData != null) {
				setAppList();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
		}

		@Override
		public void OnCacnel() {
		}
	};

	private Dialog downloadingDialog = null;
	private XProcessBar dialogDownPrcessBar = null;

	private void showDownload(final String url) {
		if (downloadingDialog != null) {
			downloadingDialog.dismiss();
		}
		View view = LayoutInflater.from(AddAppActivity.this).inflate(R.layout.download_dialog, null);
		dialogDownPrcessBar = (XProcessBar) view.findViewById(R.id.downProcessBar);
		dialogDownPrcessBar.setDownUrl(url);
		view.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DownLoadManager.getInstance().stopAll();
				CMAAPPInatallerManager.stopUnzip();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (appAdapter != null)
					appAdapter.notifyDataSetChanged();
				downloadingDialog.dismiss();
			}
		});
		downloadingDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() * 95 / 100, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		downloadingDialog.setContentView(view, params);
		Window window = downloadingDialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		downloadingDialog.setCanceledOnTouchOutside(false);
		downloadingDialog.setCancelable(false);
		downloadingDialog.show();
		DownLoadManager.getInstance().startDownLoad(url);

	}

	private BroadcastReceiver appInstallReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String url = intent.getStringExtra("installPath");
			if (intent.getAction().equals(APPINSTALLACTION)) {
				showDownload(url);
			}
		}
	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Log.e("xxxx", "animation");
			DownLoadManager.getInstance().stopAll();
			CMAAPPInatallerManager.stopUnzip();
			finish();
			this.overridePendingTransition(0, R.anim.out_down);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
}