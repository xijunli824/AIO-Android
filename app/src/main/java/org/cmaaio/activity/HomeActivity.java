package org.cmaaio.activity;

import java.util.ArrayList;
import java.util.List;

import org.cmaaio.adapter.PropAdapter;
import org.cmaaio.common.AppDataController;
import org.cmaaio.common.Constants;
import org.cmaaio.common.DownLoadListener;
import org.cmaaio.common.DownLoadManager;
import org.cmaaio.common.FileOp;
import org.cmaaio.common.PGapView;
import org.cmaaio.common.XButton;
import org.cmaaio.common.XProcessBar;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.entity.AppListEntity;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.httputil.DataParser;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.sysdownloadmanager.CMAAPPInatallerManager;
import org.cmaaio.ui.ConfirmDialog;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.BDUtils;
import org.cmaaio.util.CMATool;
import org.cmaaio.util.CommonUtil;
import org.cmaaio.view.laucher.Configure;
import org.cmaaio.view.laucher.DateAdapter;
import org.cmaaio.view.laucher.DragGrid;
import org.cmaaio.view.laucher.ScrollLayout;
import org.cmaaio.weather.GetlocationService;
import org.cmaaio.weather.HttpOperator;
import org.cmaaio.weather.NetLoactionService;
import org.cmaaio.weather.WeatherInfo;
import org.cmaaio.weather.WeatherinfoCallBack;
import org.cmaaio.weather.YahooWeatherHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class HomeActivity extends CMABaseActivity {
	// add by shrimp at 20130625 for weather off
	private ImageView weatherIV;
	private TextView tempTV;
	/** GridView. */
	private LinearLayout linear;
	// private RelativeLayout relate;
	private DragGrid gridView;
	private ScrollLayout lst_views;
	private ImageView delImage;
	LinearLayout.LayoutParams param;

	TranslateAnimation left, right;
	Animation up, down;

	private XButton search;

	public static final int PAGE_SIZE = 12;
	ArrayList<DragGrid> gridviews = new ArrayList<DragGrid>();

	ArrayList<ArrayList<CMAAppEntity>> appPageList = new ArrayList<ArrayList<CMAAppEntity>>();// 每一页数据集合,二维
	ArrayList<CMAAppEntity> allAppList = new ArrayList<CMAAppEntity>();// 所有app数据，一维
	private ArrayList<CMAAppEntity> netAppList = new ArrayList<CMAAppEntity>(); // 服务器数据

	SensorManager sm;
	SensorEventListener lsn;
	boolean isClean = false;
	Vibrator vibrator;
	int rockCount = 0;

	private LinearLayout layout_position;
	// add by wj
	private PGapView newsView = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);
		super.initTaBar(0);// 表示此activity属于tabbar的第一个组

		// add by shrimp at 20130625 for weather off
		weatherIV = (ImageView) findViewById(R.id.weather_image);
		weatherIV.setVisibility(View.GONE);
		tempTV = (TextView) findViewById(R.id.temperature);
		tempTV.setVisibility(View.GONE);

		// initDefaultApp();

		AppDataController.getInstance().getAppData();

		init();
		initBDPush();
		CommonUtil.isOpenGPS(this);

		// add
		DownLoadManager.getInstance().setDownListener(downListener);
		// end

		runAnimation();
		delImage = (ImageView) findViewById(R.id.dels);

		search = (XButton) this.findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (SharedPrefsConfig.getSharedPrefsInstance(HomeActivity.this)
						.getUserName().startsWith("w_")) {
					Toast.makeText(HomeActivity.this,
							getString(R.string.modify_no_contract_permit),
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this, SearchActivity.class);
					startActivity(intent);
				}

			}
		});

		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		lsn = new SensorEventListener() {
			public void onSensorChanged(SensorEvent e) {
				if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					if (!isClean && rockCount >= 10) {
						isClean = true;
						rockCount = 0;
						vibrator.vibrate(100);
						CleanItems();
						return;
					}
					float newX = e.values[SensorManager.DATA_X];
					float newY = e.values[SensorManager.DATA_Y];
					float newZ = e.values[SensorManager.DATA_Z];
					// if ((newX >= 18 || newY >= 20||newZ >= 20 )&&rockCount<4)
					// {
					if ((newX >= 18 || newY >= 20 || newZ >= 20)
							&& rockCount % 2 == 0) {
						rockCount++;
						return;
					}
					if ((newX <= -18 || newY <= -20 || newZ <= -20)
							&& rockCount % 2 == 1) {
						rockCount++;
						return;
					}

				}
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};

		sm.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
		// 添加天气监听,首页加载完成
		IntentFilter filter = new IntentFilter();
		filter.addAction(GetlocationService.GETYAHOOWEATHERBROADCAST);
		filter.addAction(HOME_INIT_OK_BROADCAST);
		registerReceiver(getweatherReceiver, filter);

		// 添加 itell和itrack互调广播监听
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction(PGapActivity.RESTART_ITELL_ITRACK_BROADCAST);
		ifilter.addAction(PropAdapter.ALL_READ_BROATCAST);
		ifilter.addAction(CMAAPPInatallerManager.FINISH_UPDATE_BROATCAST);
		this.registerReceiver(restartItellOrItrackReceiver, ifilter);

		ActivityManager.getInstance().addActivity(this);

		Constants.isHomeFinish = false;
		showInitDialog();// 首页加载
		SharedPrefsConfig.getSharedPrefsInstance(this).setScreenWidth(
				(int) getWindowManager().getDefaultDisplay().getWidth());
		SharedPrefsConfig.getSharedPrefsInstance(this).setScreenHeight(
				(int) getWindowManager().getDefaultDisplay().getHeight());

		setWeatherView();
	}

	private void setWeatherView() {
		int ris = SharedPrefsConfig.getSharedPrefsInstance(HomeActivity.this)
				.getImageRis();
		String teml = SharedPrefsConfig.getSharedPrefsInstance(
				HomeActivity.this).getTeml();
		if (ris >= 0 && !"".equals(teml) && teml != null) {
			weatherIV.setImageResource(YahooWeatherHelper.m_ImageArr[ris][0]);
			weatherIV.setVisibility(View.VISIBLE);
			tempTV.setText(teml);
			tempTV.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	protected void onDestroy() {

		super.onDestroy();
		sm.unregisterListener(lsn);
		newsView.appView.handleDestroy();
		newsView.appView.getWebChromeClient().setWebView(null);
		// 停止取位置的服务
		Intent serviceIntent = new Intent(HomeActivity.this,
				GetlocationService.class);
		stopService(serviceIntent);
		Intent netserviceIntent = new Intent(HomeActivity.this,
				NetLoactionService.class);
		stopService(netserviceIntent);
		// 移除天气监听,首页加载完成的监听
		unregisterReceiver(getweatherReceiver);
		// 移除 itell和itrack互调广播监听
		unregisterReceiver(this.restartItellOrItrackReceiver);
		// 去下载监听
		DownLoadManager.getInstance().setDownListener(null);
		// 首页关闭的标识
		Constants.isHomeFinish = true;
		// 退出
		ActivityManager.getInstance().exit();
	}

	@Override
	public void onResume() {
		Log.d("onResume", "onResume");
		getAppList();
		if (syncAppData()) {
			loadAppData();
			loadMetroUI();
			setImagePosition(0);
		}
		super.onResume();
	}

	// 初始化控件
	public void init() {
		// relate = (RelativeLayout) findViewById(R.id.relate);

		layout_position = (LinearLayout) findViewById(R.id.layout_position);
		lst_views = (ScrollLayout) findViewById(R.id.views);
		org.cmaaio.view.laucher.Configure.init(HomeActivity.this);
		param = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		param.rightMargin = 20;
		param.leftMargin = 20;
		if (gridView != null) {
			lst_views.removeAllViews();
		}
		// add by wj
		newsView = (PGapView) this.findViewById(R.id.newsWebView);
		newsView.setContext(this);
		newsView.loadUrl("file:///android_asset/www/noticeAndNew/News/FirstNews.html");
		newsView.setOnTouchListener(newsViewListener);
		newsView.setWebViewTouch(false);// 关闭webview的touch事件
		// 进度
		newsView.setLoadingView(null);// newsView.setLoadingView(findViewById(R.id.loadingView));
	}

	/**
	 * 初始化百度账号
	 */
	private void initBDPush() {
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		// 设置自定义的通知样式，如果想使用系统默认的可以不加这段代码
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
				getApplicationContext(), resource.getIdentifier(
						"notification_custom_builder", "layout", pkgName),
				resource.getIdentifier("notification_icon", "id", pkgName),
				resource.getIdentifier("notification_title", "id", pkgName),
				resource.getIdentifier("notification_text", "id", pkgName));
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(resource.getIdentifier(
				"simple_notification_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(this, 1, cBuilder);

		// 以apikey的方式登录
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				BDUtils.getMetaValue(HomeActivity.this, "api_key"));
		/**
		 * 设置标签，但应用推送的时候没有用标签的方式进行推送，目前以单播的方式进行推送， 为了以后的拓展
		 */
		List<String> tags = new ArrayList<String>();
		tags.add(Constants.userName);
		PushManager.setTags(getApplicationContext(), tags);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String action = intent.getAction();
		// 处理绑定成功返回结果
		if (BDUtils.ACTION_RESPONSE.equals(action)) {
			int errorCode = intent.getIntExtra(BDUtils.RESPONSE_ERRCODE, 0);
			if (errorCode == 0) {// 等于说绑定成功
				String appid = intent.getStringExtra(BDUtils.APPID);
				String userid = intent.getStringExtra(BDUtils.USERID);
				String channelid = intent.getStringExtra(BDUtils.CHANNEID);
				// 状态保存
				SharedPrefsConfig spc = SharedPrefsConfig
						.getSharedPrefsInstance(HomeActivity.this);
				spc.setBaiduPushAppId(appid);
				spc.setBaiduPushUserId(userid);
				spc.setBaiduPushChannelId(channelid);
				// 账号绑定成功，把userid,appid,channeid传给后台，实现单播推送
				registPush();
			}
		}
	}

	public boolean syncAppData() {
		if (AppDataController.getInstance().isModify) {
			allAppList.clear();
			allAppList.addAll(AppDataController.getInstance().getApp());
			return true;
		} else
			return false;
	}

	// 初始化数据,app一维数据转换为二维数据
	public void loadAppData() {
		clearPage();
		int needPage = (int) Math.ceil(allAppList.size() / (float) PAGE_SIZE);
		int maxPage = 0;
		for (CMAAppEntity app : allAppList) {
			if (app.inPage != -1)
				maxPage = (app.inPage + 1) > maxPage ? (app.inPage + 1)
						: maxPage;
		}
		needPage = needPage > maxPage ? needPage : maxPage;
		for (int i = 0; i < needPage; i++)
			addPage();
		for (CMAAppEntity app : allAppList) {
			if (app.inPage == -1 && app.seatId == -1)
				addToPage(app);
			else
				addToPage(app, app.inPage, app.seatId);
		}
	}

	public void loadMetroUI() {
		lst_views.removeAllViews();
		gridviews.clear();
		for (int i = 0; i < Configure.countPages; i++) {
			lst_views.addView(addGridView(i));
		}

		lst_views.setPageListener(new ScrollLayout.PageListener() {
			public void page(int page) {
				setCurPage(page);
			}
		});
	}

	private void addPage() {
		Configure.countPages++;
		ArrayList<CMAAppEntity> page = new ArrayList<CMAAppEntity>();
		for (int i = 0; i < PAGE_SIZE; i++)
			page.add(getEmptyApp());
		appPageList.add(page);
	}

	private void clearPage() {
		Configure.countPages = 0;
		appPageList.clear();
	}

	private void addToPage(CMAAppEntity app) {
		boolean isSuc = false;
		for (int i = 0; i < Configure.countPages; i++) {
			for (int j = 0; j < PAGE_SIZE; j++) {
				CMAAppEntity tmpApp = appPageList.get(i).get(j);
				if (tmpApp.appId.equals("none")) {
					appPageList.get(i).remove(j);
					appPageList.get(i).add(j, app);
					isSuc = true;
					break;
				}
			}
			if (isSuc)
				break;
			if (i == Configure.countPages - 1 && !isSuc)
				addPage();
		}
	}

	// page start with 0
	private boolean addToPage(CMAAppEntity app, int page, int index) {
		boolean isSuc = false;
		for (int i = Configure.countPages; i < page + 1; i++)
			addPage();
		CMAAppEntity tmpApp = appPageList.get(page).get(index);
		if (tmpApp.appId.equals("none")) {
			appPageList.get(page).remove(index);
			appPageList.get(page).add(index, app);
			isSuc = true;
		}
		return isSuc;
	}

	public void CleanItems() {
		allAppList = new ArrayList<CMAAppEntity>();
		for (int i = 0; i < appPageList.size(); i++) {
			for (int j = 0; j < appPageList.get(i).size(); j++) {
				if (appPageList.get(i).get(j) != null
						&& !appPageList.get(i).get(j).appId.equals("none")) {
					allAppList.add(appPageList.get(i).get(j));
				}
			}
		}
		System.out.println(allAppList.size());
		loadAppData();
		lst_views.removeAllViews();
		gridviews = new ArrayList<DragGrid>();
		for (int i = 0; i < Configure.countPages; i++) {
			lst_views.addView(addGridView(i));
		}
		isClean = false;
		lst_views.snapToScreen(0);
	}

	public int getFristNonePosition(ArrayList<String> array) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) != null && array.get(i).toString().equals("none")) {
				return i;
			}
		}
		return -1;
	}

	public int getFristNullPosition(ArrayList<String> array) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) == null) {
				return i;
			}
		}
		return -1;
	}

	private CMAAppEntity getEmptyApp() {
		CMAAppEntity emptyApp = new CMAAppEntity();
		emptyApp.appId = "none";
		emptyApp.appName = "";
		return emptyApp;
	}

	private boolean isEmptyApp(CMAAppEntity app) {
		if (app.appId.equals("none"))
			return true;
		else
			return false;
	}

	private void showAddView(final int ii, final int arg2) {

	}

	public LinearLayout addGridView(int i) {
		// if (appPageList.get(i).size() < PAGE_SIZE)
		// appPageList.get(i).add(null);

		linear = new LinearLayout(HomeActivity.this);
		gridView = new DragGrid(HomeActivity.this);
		gridView.setScrollLayout(lst_views);// 设置scroll
		gridView.setAdapter(new DateAdapter(HomeActivity.this, appPageList
				.get(i)));
		gridView.setNumColumns(4);
		gridView.setHorizontalSpacing(0);
		gridView.setVerticalSpacing(0);
		final int ii = i;
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {

				if (appPageList.get(ii).get(arg2) == null)
					showAddView(ii, arg2);

			}
		});
		gridView.setSelector(R.drawable.grid_light);
		gridView.setPageListener(new DragGrid.G_PageListener() {

			public void page(int cases, int page) {
				switch (cases) {
				case 0:// 滑动页面
					lst_views.snapToScreen(page);
					setCurPage(page);
					new Handler().postDelayed(new Runnable() {

						public void run() {
							Configure.isChangingPage = false;
						}
					}, 800);
					break;
				case 1:// 删除按钮上来
					delImage.setBackgroundResource(R.drawable.del);
					delImage.setVisibility(View.VISIBLE);
					delImage.startAnimation(up);
					break;
				case 2:// 删除按钮变深
					delImage.setBackgroundResource(R.drawable.settings_icon_remove);
					Configure.isDelDark = true;
					break;
				case 3:// 删除按钮变淡
					delImage.setBackgroundResource(R.drawable.del);
					Configure.isDelDark = false;
					break;
				case 4:// 删除按钮下去
					delImage.startAnimation(down);
					break;
				case 5:// 删除松手动作
					CMAAppEntity delApp = appPageList.get(Configure.curentPage)
							.get(Configure.removeItem);
					if (delApp.appType.contains(CMAAppEntity.ROMAPP)) {
						Toast.makeText(HomeActivity.this, R.string.system_app,
								Toast.LENGTH_SHORT).show();
						delImage.startAnimation(down);
						return;
					}
					delImage.startAnimation(down);
					// Configure.isDelRunning = false;
					if (delApp != null) {
						AppDataController.getInstance().remove(delApp);
						FileOp.getInstance().delFileDir(
								Constants.rootPath + "/" + Constants.userName
										+ "/" + Constants.appFloder
										+ delApp.appName + "/");
						CMAAPPInatallerManager.userManageAppPush(delApp, 2);// 消息推送
					}
					appPageList.get(Configure.curentPage).add(
							Configure.removeItem, getEmptyApp());// null是添加样式，none是空白区域
					appPageList.get(Configure.curentPage).remove(
							Configure.removeItem + 1);// 删除老的item
					((DateAdapter) ((gridviews.get(Configure.curentPage))
							.getAdapter())).notifyDataSetChanged();
					break;
				}
			}
		});
		gridView.setOnItemChangeListener(new DragGrid.G_ItemChangeListener() {

			public void change(int from, int to, int count) {
				CMAAppEntity toApp = appPageList.get(
						Configure.curentPage - count).get(from);

				appPageList.get(Configure.curentPage - count).add(from,
						appPageList.get(Configure.curentPage).get(to));
				appPageList.get(Configure.curentPage - count).remove(from + 1);
				appPageList.get(Configure.curentPage).add(to, toApp);
				appPageList.get(Configure.curentPage).remove(to + 1);

				((DateAdapter) ((gridviews.get(Configure.curentPage - count))
						.getAdapter())).notifyDataSetChanged();
				((DateAdapter) ((gridviews.get(Configure.curentPage))
						.getAdapter())).notifyDataSetChanged();
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				int page = 0;
				for (int i = 0; i < gridviews.size(); i++) {
					if (view.equals(gridviews.get(i))) {
						page = i;
						break;
					}
				}
				CMAAppEntity app = appPageList.get(page).get(index);
				if (!Constants.isOfflineLogin()) {
					if (!isEmptyApp(app)) {
						Constants.curAppId = "";// 每次清空
						// Constants.saveUserInfo();
						if (app.appType.contains(CMAAppEntity.WEBAPP)) {
							CMAAppEntity newApp = checkUpdate(app);
							Intent intent = new Intent();
							if (newApp != null) {
								AppDataController.getInstance().remove(app);
								AppDataController.getInstance().add(newApp);
								intent.putExtra("appUrl", newApp.appPath);
								intent.putExtra("appName", newApp.appName);
							} else {
								intent.putExtra("appUrl", app.appPath);
								intent.putExtra("appName", app.appName);
							}

							SharedPrefsConfig sp = SharedPrefsConfig
									.getSharedPrefsInstance(HomeActivity.this);
							String tag = sp.getUserName()
									+ SharedPrefsConfig.APPFULLSCREEN;
							String tagApp = sp.getUserName() + "_" + app.appId
									+ SharedPrefsConfig.APPFULLSCREEN;
							if (!sp.getSharedPreferences()
									.getBoolean(tag, true)) {
								intent.putExtra("isFullScreen", false);
							} else {
								intent.putExtra(
										"isFullScreen",
										sp.getSharedPreferences().getBoolean(
												tagApp, true));
							}

							intent.setClass(HomeActivity.this,
									WebViewActivity.class);
							startActivity(intent);
						} else if (app.appType.contains(CMAAppEntity.HYBRIDAPP)) {
							CMAAppEntity newApp = checkUpdate(app);
							if (newApp != null) {
								shownUpdateDialog(app, newApp.appDownUrl);
							} else {
								Intent intent = new Intent();
								intent.putExtra("CurAppId", app.appId);
								intent.putExtra("appUrl", app.appPath);
								SharedPrefsConfig sp = SharedPrefsConfig
										.getSharedPrefsInstance(HomeActivity.this);
								String tag = sp.getUserName()
										+ SharedPrefsConfig.APPFULLSCREEN;
								String tagApp = sp.getUserName() + "_"
										+ app.appId
										+ SharedPrefsConfig.APPFULLSCREEN;
								if (!sp.getSharedPreferences().getBoolean(tag,
										true)) {
									intent.putExtra("isFullScreen", false);
								} else {
									intent.putExtra("isFullScreen",
											sp.getSharedPreferences()
													.getBoolean(tagApp, true));
								}

								intent.setClass(HomeActivity.this,
										PGapActivity.class);
								startActivity(intent);
							}

						} else if (app.appType.contains(CMAAppEntity.NATIVEAPP)) {
							ResolveInfo resolveInfo = null;
							PackageManager packageManager = getPackageManager();
							final Intent mainIntent = new Intent(
									Intent.ACTION_MAIN, null);
							mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							final List<ResolveInfo> appsListLeDou = packageManager
									.queryIntentActivities(mainIntent, 0);
							for (int i = 0; i < appsListLeDou.size(); i++) {
								resolveInfo = appsListLeDou.get(i);
								if (resolveInfo.activityInfo.packageName
										.equals(app.appPath)) {
									break;
								}
								resolveInfo = null;
							}
							if (resolveInfo == null) {
								Toast.makeText(HomeActivity.this,
										R.string.not_find_app,
										Toast.LENGTH_SHORT).show();
								if (app != null) {
									AppDataController.getInstance().remove(app);
									FileOp.getInstance().delFileDir(
											Constants.rootPath + "/"
													+ Constants.userName + "/"
													+ Constants.appFloder
													+ app.appName + "/");
								}
								appPageList.get(Configure.curentPage).remove(
										index);
								((DateAdapter) ((gridviews
										.get(Configure.curentPage))
										.getAdapter())).notifyDataSetChanged();
								return;
							}
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							ComponentName comp = new ComponentName(app.appPath,
									resolveInfo.activityInfo.name);
							intent.setComponent(comp);
							try {
								startActivity(intent);
							} catch (ActivityNotFoundException e) {
								Log.e("native",
										"No entrance activity found in package "
												+ app);
							}
						}
					}
				} else {
					Toast.makeText(HomeActivity.this, R.string.offline_mode,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		gridviews.add(gridView);
		linear.addView(gridviews.get(i), param);
		return linear;

	}

	public void runAnimation() {
		down = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.del_down);
		up = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.del_up);
		down.setAnimationListener(new Animation.AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation arg0) {

				delImage.setVisibility(View.GONE);
			}
		});

		right = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f);
		left = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0f);
		right.setDuration(25000);
		left.setDuration(25000);
		right.setFillAfter(true);
		left.setFillAfter(true);

		right.setAnimationListener(new Animation.AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

				// runImage.startAnimation(left);
			}
		});
		left.setAnimationListener(new Animation.AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

				// runImage.startAnimation(right);
			}
		});
		// runImage.startAnimation(right);
	}

	public void setCurPage(final int page) {
		setImagePosition(page);
	}

	private void setImagePosition(int page) {
		if (layout_position.getChildCount() > 0) {
			layout_position.removeAllViews();
		}
		int count = Configure.countPages;
		for (int i = 0; i < count; i++) {
			ImageView imageView = new ImageView(HomeActivity.this);
			imageView.setBackgroundResource(R.drawable.dot);
			if (i == page)
				imageView.setEnabled(true);
			else
				imageView.setEnabled(false);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					CMATool.dip2px(this, 6), CMATool.dip2px(this, 6));
			params.rightMargin = 2;
			params.leftMargin = 2;
			layout_position.addView(imageView, params);
		}
	}

	private OnTouchListener newsViewListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (!Constants.isOfflineLogin()) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					Intent intent = new Intent();
					intent.putExtra("appUrl",
							"file:///android_asset/www/noticeAndNew/News/index.html");
					intent.setClass(HomeActivity.this, PGapActivity.class);
					startActivity(intent);
				}
			} else {
				Toast.makeText(HomeActivity.this, R.string.offline_mode,
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	};

	private void registPush() {
		SharedPrefsConfig spc = SharedPrefsConfig
				.getSharedPrefsInstance(HomeActivity.this);
		String postStr = "&ADAcount=" + spc.getUserName()
				+ "&Token=&OS=Android&OSVer=4.1" + "&baiduAppID="
				+ spc.getBaiduPushAppId() + "&baiduUserID="
				+ spc.getBaiduPushUserId() + "&baiduChannelID="
				+ spc.getBaiduPushChannelId();
		HttpUtil http = new HttpUtil();
		http.iResult = registPushListener;
		http.context = HomeActivity.this;
		http.isShowDialog = false;
		http.registPush(postStr);
	}

	private IResult registPushListener = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				Log.d("whz", jsonStr);
				ArrayList<CMAAppEntity> applist = DataParser
						.parseUserAppList(jsonStr);

				int appSize2 = applist.size();
				for (int j = 0; j < appSize2; j++) {
					int appSize = allAppList.size();
					CMAAppEntity appTemp = applist.get(j);
					for (int i = 0; i < appSize; i++) {
						CMAAppEntity app = allAppList.get(i);
						if (app.appId.equals(appTemp.appId)) {
							app.badgeNum = appTemp.badgeNum;
							System.out.println("BadgeNum=====>" + app.badgeNum);
							continue;
						}
					}
				}
				Cmaaio.getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						((DateAdapter) ((gridviews.get(Configure.curentPage))
								.getAdapter())).notifyDataSetChanged();
					}
				});
				// GridView gv = gridviews.get(Configure.curentPage);
				// int appSize2 = applist.size();
				// for (int j = 0; j < appSize2; j++) {
				// CMAAppEntity appTemp = applist.get(j);
				// // appTemp.badgeNum = 5;
				// View badgeView = gv.findViewWithTag(appTemp.appId);
				// if (badgeView != null && appTemp.badgeNum > 0) {
				// badgeView.setVisibility(View.VISIBLE);
				// TextView tv = (TextView) badgeView;
				// tv.setText("" + appTemp.badgeNum);
				// android.util.Log.d("badgenum", "" + appTemp.appName + "id:" +
				// appTemp.appId);
				// }
				// }
				// 百度注册成功返回
				Cmaaio.getInstance().sendBroadcast(
						new Intent(HomeActivity.HOME_INIT_OK_BROADCAST));
				initAppPush();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void OnFail(String errorMsg) {
			Log.e("User", errorMsg);
		}

		@Override
		public void OnCacnel() {

		}
	};

	private void initAppPush() {
		List<CMAAppEntity> applist = AppDataController.getInstance().getApp();
		StringBuilder apps = new StringBuilder("&AppidList=");
		for (int i = 0; i < applist.size(); i++) {
			apps.append(applist.get(i).appId);
			if (i < applist.size() - 1) {
				apps.append(",");
			}
		}
		SharedPrefsConfig spc = SharedPrefsConfig
				.getSharedPrefsInstance(HomeActivity.this);
		String postStr = apps.toString() + "&ADAcount=" + spc.getUserName()
				+ "&Token=&OS=Android&OSVer=4.1" + "&baiduAppID="
				+ spc.getBaiduPushAppId() + "&baiduUserID="
				+ spc.getBaiduPushUserId() + "&baiduChannelID="
				+ spc.getBaiduPushChannelId();
		HttpUtil http = new HttpUtil();
		http.iResult = initAppPushListener;
		http.context = HomeActivity.this;
		http.isShowDialog = false;
		http.executeTask(true, "UserInit", postStr);
	}

	private IResult initAppPushListener = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			loadingDialog.dismiss();
			try {
				Log.d("whz", "OnResult jsonStr" + jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void OnFail(String errorMsg) {
			Log.e("whz", errorMsg);
			loadingDialog.dismiss();
		}

		@Override
		public void OnCacnel() {
			loadingDialog.dismiss();
		}
	};

	/**
	 * 获取天气
	 */
	private void getWeather() {
		String woeid = SharedPrefsConfig.getSharedPrefsInstance(this)
				.getYahoowoeid();
		String url = "http://weather.yahooapis.com/forecastrss?w=" + woeid
				+ "&u=c";// 2151330
		HttpOperator.getYahooWeather(url, new WeatherinfoCallBack() {
			@Override
			public void onSuccess(final WeatherInfo weather) {
				Cmaaio.getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						if (weather == null) {
							return;
						}
						SharedPrefsConfig.getSharedPrefsInstance(
								HomeActivity.this).setImageRis(
								Integer.parseInt(weather.getCode()));
						SharedPrefsConfig.getSharedPrefsInstance(
								HomeActivity.this).setTeml(
								weather.getTemperature(1) + "℃");

						weatherIV
								.setImageResource(YahooWeatherHelper.m_ImageArr[Integer
										.parseInt(weather.getCode())][0]);
						weatherIV.setVisibility(View.VISIBLE);
						tempTV.setText(weather.getTemperature(1) + "℃");
						tempTV.setVisibility(View.VISIBLE);
					}
				});

			}

			@Override
			public void onFail(String msg) {
				android.util.Log.e("weather", msg);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("whz", "onStart");
		 getWeather();// 先取一次天气
		Intent serviceIntent = new Intent(HomeActivity.this,
				GetlocationService.class);
		startService(serviceIntent);

		Intent netserviceIntent = new Intent(HomeActivity.this,
				NetLoactionService.class);
		startService(netserviceIntent);
	}

	/**
	 * 接收服务发送的通知,取天气
	 */
	private int dataRequestOkCount = 0;// 新闻加载成功,百度推送注册成功
	private BroadcastReceiver getweatherReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					GetlocationService.GETYAHOOWEATHERBROADCAST)) {
				getWeather();
			} else if (intent.getAction().equals(HOME_INIT_OK_BROADCAST)) {
				dataRequestOkCount++;
				if (loadingDialog != null && dataRequestOkCount == 2) {// 新闻加载成功,百度推送注册成功
					Log.d("whz", "onReceive dismiss");
					loadingDialog.dismiss();
				}
				// 检查更新
				if (dataRequestOkCount == 2) {
					getCheckAppVer();
				}
			}
		}

	};

	/**
	 * 接收itell或itrack互调用广播
	 */
	private BroadcastReceiver restartItellOrItrackReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}

			if (intent.getAction().equals(
					PGapActivity.RESTART_ITELL_ITRACK_BROADCAST)) {

				Bundle bundle = intent.getExtras();
				String appId = bundle.getString("appID");
				int bagdeNum = -1;
				try {
					bagdeNum = Integer.parseInt(bundle.getString("BagdeNum"));
				} catch (Exception e) {
					return;
				}
				// GridView gv = gridviews.get(Configure.curentPage);
				// View badgeView = gv.findViewWithTag(appId);
				// if (badgeView != null) {
				// if (bagdeNum > 0) {
				// badgeView.setVisibility(View.VISIBLE);
				// TextView tv = (TextView) badgeView;
				// tv.setText("" + bagdeNum);
				// } else {
				// badgeView.setVisibility(View.GONE);
				// }
				// }
				// updateBadgeNumber(appId, bagdeNum);
				if (bagdeNum >= 0) {// 大于等于0时执行更新操作
					int appSize = allAppList.size();
					for (int i = 0; i < appSize; i++) {
						CMAAppEntity app = allAppList.get(i);
						if (appId.equals(app.appId)) {
							app.badgeNum = bagdeNum;
							// 将角标数提交服务器
							updateBadgeNumber(appId, app.badgeNum);
							break;
						}
					}
				}
				Cmaaio.getMainHandler().post(new Runnable() {
					@Override
					public void run() {
						((DateAdapter) ((gridviews.get(Configure.curentPage))
								.getAdapter())).notifyDataSetChanged();

					}
				});

				if (PGapActivity.IS_BACK_FLAG.equals(bundle.getString("Flag"))) {
					String nextAppId = bundle.getString("NextID");
					CMAAppEntity app = AppDataController.getInstance()
							.getAppById(nextAppId);
					if (app == null) {
						Toast.makeText(HomeActivity.this,
								R.string.this_app_uninstalled,
								Toast.LENGTH_LONG).show();
						return;
					}

					Intent intentTemp = new Intent();
					intentTemp.putExtra("CurAppId", app.appId);
					intentTemp.putExtra("appUrl", app.appPath);

					SharedPrefsConfig sp = SharedPrefsConfig
							.getSharedPrefsInstance(HomeActivity.this);
					String tag = sp.getUserName()
							+ SharedPrefsConfig.APPFULLSCREEN;
					String tagApp = sp.getUserName() + "_" + app.appId
							+ SharedPrefsConfig.APPFULLSCREEN;
					if (!sp.getSharedPreferences().getBoolean(tag, true)) {
						intentTemp.putExtra("isFullScreen", false);
					} else {
						intentTemp.putExtra("isFullScreen", sp
								.getSharedPreferences()
								.getBoolean(tagApp, true));
					}
					intentTemp.setClass(HomeActivity.this, PGapActivity.class);
					startActivity(intentTemp);
				}

			} else if (intent.getAction()
					.equals(PropAdapter.ALL_READ_BROATCAST)) {
				Bundle bundle = intent.getExtras();
				String appId = bundle.getString("appID");
				// GridView gv = gridviews.get(Configure.curentPage);
				// View badgeView = gv.findViewWithTag(appId);
				// if (badgeView != null) {
				// badgeView.setVisibility(View.GONE);
				// }
				int appSize = allAppList.size();
				for (int i = 0; i < appSize; i++) {
					CMAAppEntity app = allAppList.get(i);
					if (appId.equals(app.appId)) {
						Log.e("tag=============>", appId);
						app.badgeNum = 0;
						((DateAdapter) ((gridviews.get(Configure.curentPage))
								.getAdapter())).notifyDataSetChanged();
						break;
					}
				}
			} else if (intent.getAction().equals(
					CMAAPPInatallerManager.FINISH_UPDATE_BROATCAST)) {
				if (downloadingDialog != null) {
					downloadingDialog.dismiss();
				}
				Bundle bundle = intent.getExtras();
				String appId = bundle.getString("NewAppID");
				String ver = bundle.getString("AppVersion");
				String aliases = bundle.getString("AppAliases");
				// Log.e("back appId=========>", appId);
				// Log.e("back AppVersion=========>", ver);
				int size = allAppList.size();
				for (int i = 0; i < size; i++) {
					CMAAppEntity app = allAppList.get(i);
					if (app.appId.equals(appId)) {
						app.AppVersion = ver;
						app.AppAliases = aliases;
						break;
					}
				}
				// refresh
				((DateAdapter) ((gridviews.get(Configure.curentPage))
						.getAdapter())).notifyDataSetChanged();
			}
		}
	};

	/*
	 * 将角标数提交服务器
	 */
	private void updateBadgeNumber(String appId, int badgeNum) {
		android.util.Log.e("badgeNum", "appId:=" + appId);
		if (badgeNum >= 0) {
			SharedPrefsConfig spc = SharedPrefsConfig
					.getSharedPrefsInstance(HomeActivity.this);
			String postStr = "&AppId=" + appId + "&ADAcount="
					+ spc.getUserName() + "&Token=&OS=Android&OSVer=4.1"
					+ "&BadgeNum=" + badgeNum + "&baiduAppID="
					+ spc.getBaiduPushAppId() + "&baiduUserID="
					+ spc.getBaiduPushUserId() + "&baiduChannelID="
					+ spc.getBaiduPushChannelId();
			HttpUtil http = new HttpUtil();
			http.iResult = updateBadgeNumListener;
			http.context = HomeActivity.this;
			http.isShowDialog = false;
			http.executeTask(true, "UserMinusBadge", postStr);
		}
	}

	private IResult updateBadgeNumListener = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				Log.d("UserMinusBadge", jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Log.e("UserMinusBadge", errorMsg);
		}

		@Override
		public void OnCacnel() {
			Log.e("UserMinusBadge", "OnCacnel");
		}
	};
	// 初始化页面
	private Dialog loadingDialog = null;
	public static String HOME_INIT_OK_BROADCAST = "HOME_INIT_OK_BROADCAST";

	private void showInitDialog() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
		View view = LayoutInflater.from(Cmaaio.getInstance()).inflate(
				R.layout.loading_dialog, null);
		loadingDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				getWindowManager().getDefaultDisplay().getWidth() * 95 / 100,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		loadingDialog.setContentView(view, params);
		Window window = loadingDialog.getWindow();
		window.setWindowAnimations(R.style.main_menu_animstyle);
		loadingDialog.setCanceledOnTouchOutside(false);
		if (!Constants.isOfflineLogin()) {
			if (loadingDialog != null) {
				loadingDialog.show();
			}
		}
	}

	// 检查cmaio应用更新
	private void getCheckAppVer() {
		HttpUtil con = new HttpUtil();
		con.iResult = onNetCheckAppVer;
		con.context = this;
		con.isShowDialog = false;
		String postStrOther = "&os=" + "Android";
		con.executeTask(true, "CheckAppVer", postStrOther);
	}

	private IResult onNetCheckAppVer = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);
				System.out.println(obj.toString());

				String strApkURL = obj.getString("address");
				String loacalVersion = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName;
				String netVersion = obj.getString("ver");
				// netVersion = 5;
				// strApkURL="http://gdown.baidu.com/data/wisegame/f24708c121939e2d/360weishi_165.apk";
				if (netVersion.compareTo(loacalVersion) > 0) {
					shownDownloadDialog(strApkURL);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		@Override
		public void OnFail(String errorMsg) {
			android.util.Log.d("check version", errorMsg);
		}

		@Override
		public void OnCacnel() {

		}
	};

	/**
	 * cmaio应用更新下载提示框
	 * 
	 * @param downUrl
	 */
	private Dialog AIOAppUpdateDialog = null;

	private void shownDownloadDialog(final String downUrl) {
		if (AIOAppUpdateDialog != null) {
			AIOAppUpdateDialog.dismiss();
		}
		LayoutInflater li = (LayoutInflater) HomeActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = li.inflate(R.layout.confirm_dialog, null, false);

		final TextView tipTitle = ((TextView) view
				.findViewById(R.id.DialogTipTitle));
		tipTitle.setText(R.string.app_update_tips);// app_has_newversion
		view.findViewById(R.id.layout_2btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.layout_1btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.dialog_confirm).setVisibility(View.GONE);
		view.findViewById(R.id.DialogTip).setVisibility(View.GONE);
		((Button) view.findViewById(R.id.DialogCancel)).setText(this
				.getResources().getString(R.string.confirm_ignore));
		AIOAppUpdateDialog = new ConfirmDialog(HomeActivity.this,
				R.style.FullHeightDialog);
		AIOAppUpdateDialog.setContentView(view);
		AIOAppUpdateDialog.show();

		Button cancelButton = (Button) view.findViewById(R.id.DialogCancel);

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AIOAppUpdateDialog.dismiss();
			}
		});

		Button okButton = (Button) view.findViewById(R.id.DialogOk);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AIOAppUpdateDialog.dismiss();
				ActivityManager.getInstance().exit();
				// DownLoadManager.getInstance().setDownListener(downListener);
				// showDownloadProgress(downUrl);
			}
		});

		Button confirmButton = (Button) view.findViewById(R.id.dialog_confirm);

		confirmButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AIOAppUpdateDialog.dismiss();
			}
		});
	}

	private Dialog downloadingDialog = null;
	private XProcessBar dialogDownPrcessBar = null;

	/**
	 * 下载进度框
	 * 
	 * @param url
	 */
	private void showDownloadProgress(final String url) {
		if (downloadingDialog != null) {
			downloadingDialog.dismiss();
		}
		View view = LayoutInflater.from(HomeActivity.this).inflate(
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

	private DownLoadListener downListener = new DownLoadListener() {

		@Override
		public void onProcess(final String url, final long have,
				final long total) {
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
				if (downloadingDialog != null) {
					downloadingDialog.dismiss();
				}
				Intent promptInstall = new Intent(
						android.content.Intent.ACTION_VIEW);
				promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				promptInstall.setDataAndType(Uri.parse("file://" + sdPath),
						"application/vnd.android.package-archive");
				startActivity(promptInstall);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// add
	private void getAppList() {
		HttpUtil con = new HttpUtil();
		con.context = this;
		con.iResult = onNetResult;
		con.isShowDialog = false;
		con.httpGetAppList();
	}

	private IResult onNetResult = new IResult() {

		@Override
		public void OnResult(String jsonStr) {
			ArrayList<AppListEntity> resultData = DataParser
					.parseAppList(jsonStr);
			int size = resultData.size();
			for (int i = 0; i < size; i++) {
				netAppList.addAll(resultData.get(i).appList);
			}
		}

		@Override
		public void OnFail(String errorMsg) {
		}

		@Override
		public void OnCacnel() {
		}
	};

	private CMAAppEntity checkUpdate(CMAAppEntity app) {
		int size = netAppList.size();
		for (int i = 0; i < size; i++) {
			CMAAppEntity newApp = netAppList.get(i);

			if (newApp.appId.equals(app.appId)) {
				// Log.i("appId=========>", newApp.appId);
				// Log.i("app.AppVersion=========>", app.AppVersion);
				// app.AppVersion = "1.1";
				if (newApp.AppVersion.compareTo(app.AppVersion) > 0) {
					return newApp;
				}
			}
		}

		return null;
	}

	private DownLoadListener downNewListener = new DownLoadListener() {

		@Override
		public void onProcess(final String url, final long have,
				final long total) {
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
				for (CMAAppEntity app : netAppList) {
					if (app.appDownUrl.equals(url)) {
						entity = app;
						break;
					}
				}
				if (entity != null) {
					CMAAPPInatallerManager.install(HomeActivity.this, url,
							sdPath, entity);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void shownUpdateDialog(final CMAAppEntity app, final String downUrl) {
		LayoutInflater li = (LayoutInflater) HomeActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = li.inflate(R.layout.confirm_dialog, null, false);

		final TextView tipTitle = ((TextView) view
				.findViewById(R.id.DialogTipTitle));
		tipTitle.setText(R.string.app_has_newversion);
		view.findViewById(R.id.layout_2btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.layout_1btn).setVisibility(View.VISIBLE);
		view.findViewById(R.id.dialog_confirm).setVisibility(View.GONE);
		((Button) view.findViewById(R.id.DialogCancel)).setText(this
				.getResources().getString(R.string.confirm_ignore));
		((Button) view.findViewById(R.id.DialogOk)).setText(this.getResources()
				.getString(R.string.this_app_update));
		final Dialog backDialog = new ConfirmDialog(HomeActivity.this,
				R.style.FullHeightDialog);
		backDialog.setContentView(view);
		backDialog.show();

		Button cancelButton = (Button) view.findViewById(R.id.DialogCancel);
		view.findViewById(R.id.DialogTip).setVisibility(View.GONE);

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				backDialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("CurAppId", app.appId);
				intent.putExtra("appUrl", app.appPath);
				SharedPrefsConfig sp = SharedPrefsConfig
						.getSharedPrefsInstance(HomeActivity.this);
				String tag = sp.getUserName() + SharedPrefsConfig.APPFULLSCREEN;
				String tagApp = sp.getUserName() + "_" + app.appId
						+ SharedPrefsConfig.APPFULLSCREEN;
				if (!sp.getSharedPreferences().getBoolean(tag, true)) {
					intent.putExtra("isFullScreen", false);
				} else {
					intent.putExtra("isFullScreen", sp.getSharedPreferences()
							.getBoolean(tagApp, true));
				}

				intent.setClass(HomeActivity.this, PGapActivity.class);
				startActivity(intent);
			}
		});

		Button okButton = (Button) view.findViewById(R.id.DialogOk);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				backDialog.dismiss();
				DownLoadManager.getInstance().setDownListener(downNewListener);
				Log.i("downUrl============>", downUrl);
				CMAAPPInatallerManager.stopUnzip();
				// DownLoadManager.getInstance().startDownLoad(downUrl);
				showDownloadProgress(downUrl);
			}
		});
	}

}
