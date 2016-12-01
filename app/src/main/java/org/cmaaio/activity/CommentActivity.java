package org.cmaaio.activity;

import java.nio.charset.Charset;

import org.cmaaio.common.Base64;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CMATool;
import org.cmaaio.util.CommonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends CMABaseActivity {
	private TextView content = null;
	private RatingBar level = null;

	private String appId = null;
	private String appVersion = null;

	Handler handler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("wjdebug", "appdetail oncreate");
		setContentView(R.layout.comment_activity);

		super.initTaBar(3);
		super.initNavBar(this.getString(R.string.detail_app_btn_comment), R.drawable.add_comment_btn_send_default);

		// add begin lhy
		Intent intent = getIntent();
		appId = intent.getStringExtra("Appid") + "";
		appVersion = intent.getStringExtra("AppVersion") + "";
		// add end

		initControls();

		ActivityManager.getInstance().addActivity(this);
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
		this.rightBtn.setText(R.string.put_in);
		this.rightBtn.setTextColor(Color.WHITE);
		this.rightBtn.setTextSize(14);
		this.rightBtn.setOnClickListener(submitListener);
		content = (TextView) this.findViewById(R.id.content);
		level = (RatingBar) this.findViewById(R.id.level);
	}

	@Override
	public void onRightAction() {
		super.onRightAction();
	}

	// add begin lhy
	OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (CommonUtil.isFastDoubleClick()) {
				return;
			}
			getSaveAppCommentInfo();
		}
	};

	private void getSaveAppCommentInfo() {
		HttpUtil con = new HttpUtil();
		con.iResult = CommentActivity.this.onNetSaveAppCommentInfo;
		con.context = CommentActivity.this;
		con.isShowDialog = true;
		String base64Str = new String(Base64.encode(content.getText().toString().getBytes(Charset.forName("utf-8"))));
		Log.e("base64", base64Str);
		Log.e("base64", content.getText().toString());
		String userName = SharedPrefsConfig.getSharedPrefsInstance(CommentActivity.this).getUserName();
		String postStrOther = "&AppId=" + appId + "&ADAcount=" + userName + "&Version=" + appVersion + "&AppComment=" + base64Str + "&Rate=" + (int) level.getRating();

		con.executeTask(true, "SaveAppCommentInfo", postStrOther);
	}

	private IResult onNetSaveAppCommentInfo = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				System.out.println(jsonStr);
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);

				String result = obj.getString("result");
				if ("True".equalsIgnoreCase(result)) {
					handler.sendEmptyMessage(1);
				} else {
					handler.sendEmptyMessage(2);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(CommentActivity.this, R.string.sumbit_comment_error, Toast.LENGTH_SHORT).show();
			}
		}

		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1: {
					Toast.makeText(CommentActivity.this, R.string.sumbit_comment_success, Toast.LENGTH_LONG).show();
					CommentActivity.this.finish();
				}
					break;
				case 2: {
					Toast.makeText(CommentActivity.this, R.string.sumbit_comment_fail, Toast.LENGTH_LONG).show();
				}
					break;
				}
			}
		};

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(CommentActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void OnCacnel() {

		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (CommentActivity.this.getCurrentFocus() != null) {
				if (CommentActivity.this.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(CommentActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}
	// add end
}
