package org.cmaaio.activity;

import org.cmaaio.common.Constants;
import org.cmaaio.common.PGapView;
import org.cmaaio.ui.ResizeLayout;
import org.cmaaio.ui.ResizeLayout.OnResizeListener;
import org.cmaaio.util.ActivityManager;
import org.json.JSONArray;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PGapActivity extends CMABaseActivity {
	private PGapView mainWebView = null;
	private View inView;

	public static String RESTART_ITELL_ITRACK_BROADCAST = "org.cmaaio.common.PGapView";
	public static String IS_BACK_FLAG = "1"; // 是否是退出子应用，-1：退出，1：Itell和Itrack互调

	/**
	 * 键盘状态回调
	 */
	private ResizeLayout rl = null;
	private OnResizeListener immListener = new OnResizeListener() {

		@Override
		public void OnResize(int w, int h, int oldw, int oldh) {
			if (oldh < h) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Instrumentation ins = new Instrumentation();
						MotionEvent down = MotionEvent.obtain(
								SystemClock.uptimeMillis(),
								SystemClock.uptimeMillis(),
								MotionEvent.ACTION_DOWN, 240, 400, 0);
						ins.sendPointerSync(down);
						MotionEvent move = MotionEvent.obtain(
								SystemClock.uptimeMillis(),
								SystemClock.uptimeMillis(),
								MotionEvent.ACTION_MOVE, 240, 450, 0);
						ins.sendPointerSync(move);
						MotionEvent up = MotionEvent.obtain(
								SystemClock.uptimeMillis(),
								SystemClock.uptimeMillis(),
								MotionEvent.ACTION_UP, 240, 450, 0);
						ins.sendPointerSync(up);

						// Cmaaio.getMainHandler().post(new Runnable() {
						// @Override
						// public void run() {
						// mainWebView.loadUrl("javascript: down()");
						// }
						// });
					}
				}).start();

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("oncreate", "oncreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pgap_activity);
		this.initControls();
		super.initTaBar();
		inView = findViewById(R.id.pgap_layout_inView);

		// begin
		rl = (ResizeLayout) findViewById(R.id.re_layout);
		rl.setOnResizeListener(immListener);
		// end
		Intent intent = this.getIntent();
		String curAppId = intent.getStringExtra("CurAppId");// 当前进入phonegap的应用id
		if (curAppId != null) {
			Constants.curAppId = curAppId;
		} else {
			Constants.curAppId = "";
		}
		Constants.hasDetail = intent.getBooleanExtra("hasDetail", false);// 推送时,是否要进入详情
		if (Constants.hasDetail)// 要进详情时取详情信息
		{
			Constants.pushType = intent.getStringExtra("pushType");
			Constants.pushCode = intent.getStringExtra("pushCode");
		}
		String appUrl = intent.getExtras().get("appUrl").toString();
		boolean isAppFullScreen = intent.getBooleanExtra("isFullScreen", false);
		if (isAppFullScreen) {
			inView.setVisibility(View.GONE);
		} else {
			inView.setVisibility(View.VISIBLE);
		}
		mainWebView.setLoadingView(findViewById(R.id.loadingView));
		mainWebView.loadUrl(appUrl);
		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.e("onNewIntent", "onNewIntent");
		String curAppId = intent.getStringExtra("CurAppId");// 当前进入phonegap的应用id
		if (curAppId != null) {
			Constants.curAppId = curAppId;
		} else {
			Constants.curAppId = "";
		}
		Constants.hasDetail = intent.getBooleanExtra("hasDetail", false);// 推送时,是否要进入详情
		if (Constants.hasDetail)// 要进详情时取详情信息
		{
			Constants.pushType = intent.getStringExtra("pushType");
			Constants.pushCode = intent.getStringExtra("pushCode");
		}
		String appUrl = intent.getExtras().get("appUrl").toString();
		boolean isAppFullScreen = intent.getBooleanExtra("isFullScreen", false);
		if (isAppFullScreen) {
			inView.setVisibility(View.GONE);
		} else {
			inView.setVisibility(View.VISIBLE);
		}
		mainWebView.setLoadingView(findViewById(R.id.loadingView));
		mainWebView.loadUrl(appUrl);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("pgap", "pgap destroy");
		mainWebView.appView.handleDestroy();
		mainWebView.appView.getWebChromeClient().setWebView(null);
		// mainWebView.appView.destroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (mainWebView != null)
			mainWebView.onActivityResult(requestCode, resultCode, intent);

	}

	public void initControls() {
		mainWebView = (PGapView) this.findViewById(R.id.mainWebView);
		mainWebView.setContext(this);
	}

	// PGapView 接收到pgap的消息后回调改函数,相当于插件的onmessage函数
	public void onMessage(String id, Object data) {
		if (id.equals("exit")) {

			Log.d("whz", "onMessage id=" + id + ", data=" + data.toString());
			System.out.println("exit======>");

			try {
				JSONArray obj = (JSONArray) data;
				if (obj == null || obj.length() < 4) {
					this.finish();
					return;
				}
				if (obj.getString(0) == null || obj.getString(1) == null
						|| obj.getString(2) == null || obj.getString(3) == null) {
					this.finish();
					return;
				}
				// 新增互调传值
				if (obj.length() == 5) {
					Constants.forwardValue = obj.getString(4);
				} else {
					Constants.forwardValue = "";
				}
				Log.v("tag", "forwardValue=" + obj.getString(4));
				Log.v("tag", "obj=" + data.toString());
				// 测试数据： {"0", "1", "任务","通知"}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("BagdeNum", obj.getString(0));
				bundle.putString("Flag", obj.getString(1));
				bundle.putString("NextID", obj.getString(2));
				bundle.putString("appID", obj.getString(3));
				bundle.putString("forwardValue", Constants.forwardValue);
				intent.putExtras(bundle);
				intent.setAction(RESTART_ITELL_ITRACK_BROADCAST);
				sendBroadcast(intent);

			} catch (Exception e) {
				e.printStackTrace();
			}

			this.finish();
		} else if (id.equals("other")) {

		}
	}
}
