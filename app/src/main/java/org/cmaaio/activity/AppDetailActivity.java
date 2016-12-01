package org.cmaaio.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cmaaio.adapter.AppCommentAdapter;
import org.cmaaio.common.AppDataController;
import org.cmaaio.common.DownLoadListener;
import org.cmaaio.common.DownLoadManager;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.common.XButton;
import org.cmaaio.common.XProcessBar;
import org.cmaaio.common.XScrollView;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.entity.CommentEntity;
import org.cmaaio.httputil.DataParser;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.sysdownloadmanager.CMAAPPInatallerManager;
import org.cmaaio.sysdownloadmanager.PKGInstallReceiver;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CMATool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.w.song.widget.scroll.SlidePageView;

public class AppDetailActivity extends CMABaseActivity {
	private RemoteImageView appLogo = null;
	private TextView appName = null;
	private TextView appTypeVal = null;
	private RatingBar appLevel = null;
	private TextView appVer = null;
	private TextView appAuthor = null;
	private XButton opBtn = null;
	private XButton infoBtn = null;
	private XButton commentBtn = null;
	private XProcessBar downProcessBar = null;
	private SlidePageView appImgScrollView = null;// 改控件要求xml里必须有一个子控件
	private RemoteImageView appDefaultImg = null;
	private ArrayList<LinearLayout> appImgList = null;
	private TextView appSummary = null;
	private ListView commentList = null;
	private XScrollView infoArea = null;

	private AppCommentAdapter commentAdapter = null;
	public ArrayList<CommentEntity> dataSource = null;

	// add
	private Map<String, String> appInfoMap = new HashMap<String, String>();
	private ArrayList<String> appUrlList = null;
	private Resources resource = null;
	private LinearLayout opArea = null;
	private CMAAppEntity app = null;
	private String category;

	// download
	private PKGInstallReceiver nativeInstallReceiver;
	private String appId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("wjdebug", "appdetail oncreate");
		setContentView(R.layout.appdetail_activity);

		resource = this.getResources();

		super.initTaBar(3);
		super.initNavBar(resource.getString(R.string.detail_app_title),
				R.drawable.add_icon_comment);
		initControls();
		showAppInfo();

		// add begin lhy 2013/06/22
		appId = this.getIntent().getStringExtra("Appid");
		category = this.getIntent().getStringExtra("Category");
		getGetAppDetailInfo(appId);

		// 注册
		DownLoadManager.getInstance().setDownListener(downListener);
		nativeInstallReceiver = new PKGInstallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		filter.addDataScheme("package");
		registerReceiver(nativeInstallReceiver, filter);

		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(nativeInstallReceiver);
		DownLoadManager.getInstance().setDownListener(null);
		if (downloadingDialog != null) {
			downloadingDialog.dismiss();
		}
		super.onDestroy();
	}

	private void initControls() {
		appLogo = (RemoteImageView) this.findViewById(R.id.appLogo);
		appName = (TextView) this.findViewById(R.id.appName);
		appTypeVal = (TextView) this.findViewById(R.id.appTypeValue);
		appLevel = (RatingBar) this.findViewById(R.id.appLevel);
		appVer = (TextView) this.findViewById(R.id.appVerVal);
		appAuthor = (TextView) this.findViewById(R.id.appAuthorVal);
		opBtn = (XButton) this.findViewById(R.id.opBtn);
		opBtn.setPadding(0, -10, 0, 0);// xbutton控件有bug，所以要偏移10个像素
		infoBtn = (XButton) this.findViewById(R.id.infoBtn);
		infoBtn.setPadding(0, -10, 0, 0);
		commentBtn = (XButton) this.findViewById(R.id.commentBtn);
		commentBtn.setPadding(0, -10, 0, 0);
		appSummary = (TextView) this.findViewById(R.id.appSummary);
		commentList = (ListView) this.findViewById(R.id.commentList);
		infoArea = (XScrollView) this.findViewById(R.id.infoArea);
		appImgScrollView = (SlidePageView) this.findViewById(R.id.appAllImage);
		appDefaultImg = (RemoteImageView) this.findViewById(R.id.appImg1);
		downProcessBar = (XProcessBar) this.findViewById(R.id.downProcessBar);
		downProcessBar.setVisibility(View.GONE);
		appImgList = new ArrayList<LinearLayout>();

		// add
		opArea = (LinearLayout) this.findViewById(R.id.opArea);

		appLogo.setImageResource(R.drawable.defalutapp);

		infoBtn.setOnClickListener(clickListener);
		commentBtn.setOnClickListener(clickListener);

		dataSource = new ArrayList<CommentEntity>();
		commentAdapter = new AppCommentAdapter(this, dataSource);
		commentList.setAdapter(commentAdapter);
	}

	private boolean isInstall() {
		String appId = this.getIntent().getStringExtra("Appid");
		CMAAppEntity app = new CMAAppEntity();
		app.appId = appId;
		if (AppDataController.getInstance().contain(app)) {
			return true;
		} else {
			return false;
		}
	}

	private void clearAppImgScrollView() {
		for (LinearLayout tmp : appImgList) {
			appImgScrollView.removeView(tmp);
		}
		appImgList.clear();
	}

	private void addAppImg(String url) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width = CMATool.dip2px(this, 250);
		LinearLayout imageContain = new LinearLayout(this);
		imageContain.setLayoutParams(lp);
		int pading = CMATool.dip2px(this, 10);
		imageContain.setPadding(pading, pading, pading, pading);
		LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageLp.gravity = Gravity.CENTER;
		RemoteImageView temp = new RemoteImageView(this);
		temp.setBackgroundColor(Color.parseColor("#AAAAAA"));
		temp.setLayoutParams(imageLp);
		temp.setImageUrl(url);
		imageContain.addView(temp);
		appImgScrollView.addView(imageContain);
		appImgList.add(imageContain);
	}

	private void setAppInfoData() {

		if (isInstall()) {
			CMAAppEntity oldApp = AppDataController.getInstance().getAppById(
					appId);
			if (app.AppVersion.compareTo(oldApp.AppVersion) > 0) {
				opBtn.setText(resource.getString(R.string.this_app_update));
				opBtn.setOnClickListener(clickListener);
				rightBtn.setVisibility(View.GONE);
			} else {
				opBtn.setText(resource.getString(R.string.app_have_install));
				rightBtn.setVisibility(View.VISIBLE);
			}

		} else {
			opBtn.setText(resource.getString(R.string.app_install));
			opBtn.setOnClickListener(clickListener);
			rightBtn.setVisibility(View.INVISIBLE);
		}

		//appName.setText(appInfoMap.get("AppName"));
		appName.setText(appInfoMap.get("AppAliases"));
		String appTypeValue = appInfoMap.get("ATYPE");
		if (appTypeValue.equals("effict")) {

			appTypeVal.setText(R.string.app_type_effict);
		} else if (appTypeValue.equals("office")) {
			appTypeVal.setText(R.string.app_type_office);
		} else if (appTypeValue.equals("tool")) {
			appTypeVal.setText(R.string.app_type_tool);
		} else if (appTypeValue.equals("other")) {
			appTypeVal.setText(R.string.app_type_other);
		}

		Log.i("info", "appTypeVal :" + appInfoMap.get("ATYPE"));
		appLevel.setRating(Float.parseFloat(appInfoMap.get("AppRate")));
		appVer.setText(appInfoMap.get("AppVersion") + "");
		appAuthor.setText(appInfoMap.get("AppDevelopers") + "");

		// 简介
		appSummary.setText(appInfoMap.get("AppDesc"));

		appLogo.setImageResource(R.drawable.defalutapp);
		String appImgUrl = appInfoMap.get("AppImgUrl");
		if (!"".equals(appImgUrl)) {
			// 下载应用程序Icon
			// mImageLoader.DisplayImage(appImgUrl, appLogo, false);
			appLogo.setImageUrl(appImgUrl);
		}

		if (appUrlList != null && appUrlList.size() != 0) {
			clearAppImgScrollView();
			for (int i = 0; i < appUrlList.size(); i++) {
				if (i == 0) {
					appDefaultImg.setImageUrl(appUrlList.get(i));
				} else {
					addAppImg(appUrlList.get(i));
				}
			}
		}

	}

	// override
	@Override
	public void onRightAction() {
		super.onRightAction();

		Intent intent = new Intent();
		intent.putExtra("Appid", appInfoMap.get("AppId"));
		intent.putExtra("AppVersion", appInfoMap.get("AppVersion"));
		intent.setClass(AppDetailActivity.this, CommentActivity.class);
		startActivity(intent);
	}

	// op function
	private void installApp() {
		// 如果是web类型 app不用下载
		if (CMAAppEntity.WEBAPP.equals(app.appType)) {
			app.appPath = app.appDownUrl;// such as wwww.baidu.com
			if(AppDataController.getInstance().contain(app))
			{
				AppDataController.getInstance().remove(app);
			}
			CMAAPPInatallerManager.uploadDownloadRecord(app);
			CMAAPPInatallerManager.userManageAppPush(app, 1);// 推送
		} else {
			showDownload(app.appDownUrl);
		}
	}

	private void showAppInfo() {
		opArea.setBackgroundResource(R.drawable.add_tab_detail);
		infoBtn.setBackgroundResource(R.drawable.add_tab_btn_selected);
		infoBtn.setTextColor(Color.WHITE);
		commentBtn.setBackgroundResource(R.drawable.add_tab_btn_dafault);
		commentBtn.setTextColor(Color.parseColor("#999999"));
		commentList.setVisibility(View.GONE);
		infoArea.setVisibility(View.VISIBLE);
	}

	private void showAppSum() {
		opArea.setBackgroundResource(R.drawable.add_tab_comment);
		infoBtn.setBackgroundResource(R.drawable.add_tab_btn_dafault);
		infoBtn.setTextColor(Color.parseColor("#999999"));
		commentBtn.setBackgroundResource(R.drawable.add_tab_btn_selected);
		commentBtn.setTextColor(Color.WHITE);
		commentList.setVisibility(View.VISIBLE);
		infoArea.setVisibility(View.GONE);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.equals(opBtn))
				installApp();
			if (v.equals(infoBtn))
				showAppInfo();
			if (v.equals(commentBtn)) {
				showAppSum();
				// add begin lhy
				commentAdapter.dataSource.clear();
				// 获取评论
				getGetCommentDetailsInfo(appInfoMap.get("AppId"));
				// add end
			}

		}
	};

	// add begin lhy 2013/06/22
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				setAppInfoData();
				break;
			case 2:
				break;
			}
		}
	};

	private void getGetAppDetailInfo(String appId) {
		HttpUtil con = new HttpUtil();
		con.iResult = AppDetailActivity.this.onNetGetAppDetailInfo;
		con.context = AppDetailActivity.this;

		String postStrOther = "&AppId=" + appId;
		con.executeTask(true, "GetAppDetailInfo", postStrOther);
	}

	private IResult onNetGetAppDetailInfo = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				parseJsonAppDetailInfoData(jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(AppDetailActivity.this,
						R.string.getAppDetailMsg, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(AppDetailActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};

	// 获取单条App的详细信息
	private void parseJsonAppDetailInfoData(String jsonStr) {
		try {
			JSONArray array = new JSONArray(jsonStr);
			JSONObject obj = array.getJSONObject(0);

			System.out.println(obj.toString());

			String result = obj.getString("result");
			if ("False".equals(result)) {
				System.out.println("json获取单条App的详细信息！！");
				return;
			}

			JSONArray josnArrayUser = obj.getJSONArray("AppInfo");
			JSONObject jsonObj = josnArrayUser.getJSONObject(0);

			appInfoMap.put("AppId", jsonObj.getString("AppId"));
			appInfoMap.put("AppName", jsonObj.getString("AppName"));
			appInfoMap.put("AppImgUrl", jsonObj.getString("AppImgUrl"));
			appInfoMap.put("DevType", jsonObj.getString("DevType"));
			appInfoMap.put("AppPackUrl", jsonObj.getString("AppPackUrl"));
			appInfoMap.put("AppType", jsonObj.getString("AppType"));
			appInfoMap.put("AppDesc", jsonObj.getString("AppDesc"));

			appInfoMap.put("AppSize", jsonObj.getString("AppSize"));
			appInfoMap.put("AppVersion", jsonObj.getString("AppVersion"));
			appInfoMap.put("AppRequest", jsonObj.getString("AppRequest"));
			appInfoMap.put("AppDevelopers", jsonObj.getString("AppDevelopers"));
			appInfoMap.put("AppDevWebSite", jsonObj.getString("AppDevWebSite"));

			appInfoMap.put("AppDevEmail", jsonObj.getString("AppDevEmail"));
			appInfoMap.put("KeyWords", jsonObj.getString("KeyWords"));

			appInfoMap.put("AppRate", jsonObj.getString("AppRate"));
			Log.i("AppRate===========>", jsonObj.getString("AppRate"));
			appInfoMap.put("ATYPE", jsonObj.getString("ATYPE"));

			Log.i("info", "ATYPE" + jsonObj.getString("ATYPE"));
			//add
			appInfoMap.put("AppAliases", jsonObj.getString("AppAliases"));

			downProcessBar.setDownUrl(appInfoMap.get("AppPackUrl"));
			JSONArray josnArrayImage = jsonObj.getJSONArray("ImageList");
			appUrlList = new ArrayList<String>();
			int len = josnArrayImage.length();
			for (int i = 0; i < len; i++) {
				JSONObject jsonImageObj = josnArrayImage.getJSONObject(i);
				System.out.println(jsonImageObj.getString("AppMultiImgUrl"));
				appUrlList.add(jsonImageObj.getString("AppMultiImgUrl"));
			}

			// init app obj
			app = new CMAAppEntity();
			app.appId = appInfoMap.get("");
			app.appId = appInfoMap.get("AppId");
			app.appName = appInfoMap.get("AppName");
			app.appIcon = appInfoMap.get("AppImgUrl");
			app.DevType = appInfoMap.get("DevType");
			app.appDownUrl = appInfoMap.get("AppPackUrl");
			String type = appInfoMap.get("AppType");
			if (type.equals("1")) {
				app.appType = CMAAppEntity.WEBAPP;
			} else if (type.equals("2")) {
				app.appType = CMAAppEntity.HYBRIDAPP;
			} else if (type.equals("3")) {
				app.appType = CMAAppEntity.NATIVEAPP;
			}
			app.AppSize = appInfoMap.get("AppDesc");
			app.AppSize = appInfoMap.get("AppSize");
			app.AppVersion = appInfoMap.get("AppVersion");
			app.AppRequest = appInfoMap.get("AppRequest");
			app.AppDevelopers = appInfoMap.get("AppDevelopers");
			app.AppDevWebSite = appInfoMap.get("AppDevWebSite");
			app.AppDevEmail = appInfoMap.get("AppDevEmail");
			app.KeyWords = appInfoMap.get("KeyWords");
			app.AppRate = appInfoMap.get("AppRate");
			//add
			app.AppAliases=appInfoMap.get("AppAliases");
			// end
			handler.sendEmptyMessage(1);

		} catch (JSONException e) {
			System.out.println("Jsons parse error !");
			e.printStackTrace();
		}
	}

	private void getGetCommentDetailsInfo(String appId) {
		HttpUtil con = new HttpUtil();
		con.iResult = AppDetailActivity.this.onNetGetCommentDetailsInfo;
		con.context = AppDetailActivity.this;

		String postStrOther = "&AppId=" + appId;
		con.executeTask(true, "GetCommentDetailsInfo", postStrOther);
	}

	private IResult onNetGetCommentDetailsInfo = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				// parseJsonCommentDetailsInfo(jsonStr);
				commentAdapter.dataSource = DataParser
						.parseCommentDetailsList(jsonStr);
				commentAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(AppDetailActivity.this,
						R.string.getAppDetailMsg, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(AppDetailActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};

	private DownLoadListener downListener = new DownLoadListener() {

		@Override
		public void onProcess(String url, long have, long total) {

			if (dialogDownPrcessBar != null)
				dialogDownPrcessBar.onDownProcess(url, have, total);
		}

		@Override
		public void onFinish(String url, String sdPath) {
			try {
				CMAAPPInatallerManager.install(AppDetailActivity.this, url,
						sdPath, app, opBtn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Dialog downloadingDialog = null;
	private XProcessBar dialogDownPrcessBar = null;

	private void showDownload(final String url) {
		if (downloadingDialog != null) {
			downloadingDialog.dismiss();
		}
		View view = LayoutInflater.from(AppDetailActivity.this).inflate(
				R.layout.download_dialog, null);
		dialogDownPrcessBar = (XProcessBar) view
				.findViewById(R.id.downProcessBar);
		dialogDownPrcessBar.setDownUrl(url);
		view.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						DownLoadManager.getInstance().stopAll();
						CMAAPPInatallerManager.stopUnzip();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						downloadingDialog.dismiss();
					}
				});
		downloadingDialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				getWindowManager().getDefaultDisplay().getWidth() * 95 / 100,
				LayoutParams.WRAP_CONTENT);
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
	// add end
}
