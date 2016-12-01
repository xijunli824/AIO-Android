package org.cmaaio.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.util.ActivityManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class UserInfomationModifyActivity extends CMABaseActivity {

	private EditText et;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_mofify_activity);
		super.initTaBar(2);
		et = (EditText) findViewById(R.id.et_tel);
		et.setText(SharedPrefsConfig.getSharedPrefsInstance(this).getUserStaffPhone());
		findViewById(R.id.backib).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				modify();
				InputMethodManager imm = (InputMethodManager) Cmaaio.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		ActivityManager.getInstance().addActivity(this);
	}

	private void modify() {
		String num = et.getText().toString().trim();
		String regex="^([\\s0-9\\+]+)$";
		Pattern  pattern=Pattern.compile(regex);
		Matcher matcher= pattern.matcher(num);
		if(!matcher.find())
		{
			Toast.makeText(UserInfomationModifyActivity.this, getResources().getString(R.string.tips_invalid_phone), Toast.LENGTH_SHORT).show();
			return;
		}
		HttpUtil hu = new HttpUtil();
		hu.context = this;
		hu.iResult = cb;
		hu.modifyPhoneNumber(num);

	}

	IResult cb = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);
				String result = obj.getString("result");
				if (result.equalsIgnoreCase("true")) {
					SharedPrefsConfig.getSharedPrefsInstance(UserInfomationModifyActivity.this).setUserStaffPhone(et.getText().toString().trim());
					Toast.makeText(UserInfomationModifyActivity.this, getString(R.string.modify_mb_ok), Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(UserInfomationModifyActivity.this, getString(R.string.modify_mb_fail), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				Toast.makeText(UserInfomationModifyActivity.this, getString(R.string.modify_mb_fail), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(UserInfomationModifyActivity.this, getString(R.string.modify_mb_fail), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void OnCacnel() {

		}
	};

}
