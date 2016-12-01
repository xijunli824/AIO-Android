package org.cmaaio.activity;

import org.cmaaio.util.ActivityManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class WebViewActivity extends Activity {

	private final String TAG = WebViewActivity.class.getSimpleName();

	private WebView webView;
	private Button backBtn;
	private ImageButton preBtn, nextBtn;
	private TextView appTitle;
	private View inView;

	private String url;

	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_layout);
		ActivityManager.getInstance().addActivity(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage(getResources().getString(R.string.loading_msg));
		initControls();
		initListener();
		Intent intent = this.getIntent();
		String appUrl = intent.getExtras().getString("appUrl");
		String appName = intent.getExtras().getString("appName");
		boolean isFullScreen = intent.getBooleanExtra("isFullScreen", false);

		if (isFullScreen) {
			inView.setVisibility(View.GONE);
		} else {
			inView.setVisibility(View.VISIBLE);
		}

		appTitle.setText(appName);
		if (appUrl.toLowerCase().contains("http://")
				|| appUrl.toLowerCase().contains("https://")) {
			url = appUrl;
		} else {
			url = "http://" + appUrl;
		}
		Log.d(TAG, "url=" + url);
		webView.loadUrl(url);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void initControls() {
		webView = (WebView) findViewById(R.id.web_view_layout);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new CMAAIOWebViewClient());
		backBtn = (Button) findViewById(R.id.backBtn_web_view);
		preBtn = (ImageButton) findViewById(R.id.preBtn_web_view);
		nextBtn = (ImageButton) findViewById(R.id.nextBtn_web_view);
		appTitle = (TextView) findViewById(R.id.web_view_layout_title);
		inView = findViewById(R.id.web_view_bottom_layout);

	}

	public void initListener() {
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		preBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				webView.goBack();
			}
		});
		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				webView.goForward();
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class CMAAIOWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			Log.d(TAG, "onPageStarted=" + url);
			dialog.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d(TAG, "onPageFinished=" + url);
			dialog.dismiss();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.d(TAG, "errorCode=" + errorCode);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			Log.d(TAG, "error=" + error.toString());
			handler.proceed();
		}
	}

}
