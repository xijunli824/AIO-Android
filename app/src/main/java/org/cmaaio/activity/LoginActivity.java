package org.cmaaio.activity;

import org.cmaaio.common.Constants;
import org.cmaaio.common.DownLoadManager;
import org.cmaaio.common.FileOp;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.httputil.HttpFactory;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.sysdownloadmanager.CMAAPPInatallerManager;
import org.cmaaio.ui.SlipButton;
import org.cmaaio.ui.SlipButton.OnChangedListener;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CMATool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private Button loginBtn = null;
	private EditText userName = null;
	private EditText userPwd = null;
	private SlipButton saveSwitch = null;
	private boolean isSaveUserInfo = false;
	private TextView loginType = null;

	private Resources resource = null;

	private LinearLayout loginLayout = null;

	private SharedPrefsConfig mConfig;
	private boolean isSave;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.login_activity);
		mConfig = SharedPrefsConfig.getSharedPrefsInstance(this);
		initView();
		initListener();
		currentVersion();
		// 设置全局变量
		Constants.setContext(this);

		Constants.readUserInfo();
		/**
		 * 设置用户与密码状态
		 */
		isSave = mConfig.getIsSavePasswd();
		saveSwitch.setCheck(isSave);
		if (isSave) {
			if (Constants.userPwd.length() > 0) {
				userPwd.setText(Constants.userPwd);
			}
		}

		if (Constants.userName.length() > 0) {
			userName.setText(Constants.userName);
		}

//		if (Constants.userName.length() > 0 && Constants.userPwd.length() > 0) {
//
//			// add begin lhy 2013/06/21
//			saveSwitch.setCheck(true); // 激活SlipButton状态
//			isSaveUserInfo = true;
//			// add end
//		} else {
//			saveSwitch.setCheck(Constants.isRemeberPWD);
//			isSaveUserInfo = Constants.isRemeberPWD;
//		}

		ActivityManager.getInstance().addActivity(this);
		autoLogin();
	}

	/**
	 * 用户激活自动登陆
	 */
	private void autoLogin() {
		// 判断是否为网络，如果没用网络启用离线模式
		if (HttpFactory.isNetworkConnected(this)) {
			if (userName.getText().toString().length() != 0
					&& userPwd.getText().toString().length() != 0) {
				onlineLogin();
				Constants.setOfflineLogin(false);
			}
		} else {

			offlineLogin();
		}
	}

	/**
	 * 离线登陆
	 */
	private void offlineLogin() {
		String name = userName.getText().toString();
		String pwd = userPwd.getText().toString();
		if (name.length() != 0 && pwd.length() != 0) {
			if (name.equals(Constants.userName)
					&& pwd.equals(Constants.userPwd)) {
				Constants.setOfflineLogin(true);
				Intent intent = new Intent(this, HomeActivity.class);
				startActivity(intent);
				Toast.makeText(this, R.string.offline_mode, Toast.LENGTH_SHORT)
						.show();
				this.finish();
			}
		}
	}

	private void currentVersion() {
		/**
		 * app版本号
		 */
		int currentVersion = 0;
		String currentName = null;
		try {
			// currentVersion = getPackageManager().getPackageInfo(
			// getPackageName(), 0).versionCode;
			currentName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		loginType.setText("V" + currentName);
	}

	private void initView() {
		resource = this.getResources();
		saveSwitch = (SlipButton) this.findViewById(R.id.netset_myslipswitch);
		loginType = (TextView) this
				.findViewById(R.id.login_activity_update_type);
		loginBtn = (Button) this.findViewById(R.id.btn);
		loginBtn.setOnClickListener(this);
		userName = (EditText) this.findViewById(R.id.username);
		userPwd = (EditText) this.findViewById(R.id.password);
		loginLayout = (LinearLayout) findViewById(R.id.login_layout);

		userName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) loginLayout
						.getLayoutParams();
				if (arg1) {
					params.bottomMargin = CMATool.dip2px(LoginActivity.this,
							180);
				} else {
					params.bottomMargin = CMATool.dip2px(LoginActivity.this,
							120);
				}
			}
		});

	}

	private void initListener() {
		saveSwitch.SetOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
				Log.d("whz", "CheckState=" + CheckState);
				isSaveUserInfo = CheckState;
				isSave=CheckState;
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.equals(loginBtn)) {
			String msg = null;
			if (userName.length() == 0) {
				msg = resource.getString(R.string.login_user_name_hint);
			}

			if (userPwd.length() == 0) {
				msg = resource.getString(R.string.login_password_hint);
			}

			if (msg != null) {
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				return;
			}
			Constants.isRemeberPWD = isSaveUserInfo;
			onlineLogin();

		}
	}

	/**
	 * 启动登陆
	 */
	private void onlineLogin() {
		HttpUtil con = new HttpUtil();
		try {
			con.iResult = LoginActivity.this.onNetResult;
			con.context = LoginActivity.this;
			con.httpLogin(userName.getText().toString().toLowerCase(), userPwd
					.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IResult onNetResult = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				// jsonStr = "[{\"result\":\"true\",\"day\":\"3\"}]";
				// jsonStr = "[{\"result\":\"TO\"}]";
				// jsonStr = "[{\"result\":\"Freeze\"}]";
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);
				String result = obj.getString("result");
				if (result.equalsIgnoreCase("true")) {
					String userNm = userName.getText().toString().toLowerCase()
							.replace("dc/", "");// userName.getText().toString();
					String pwd = userPwd.getText().toString();
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserName(userNm);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setPassWord(pwd);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setIsSavePwd(isSaveUserInfo);

					String token = FileOp.DESencode(Constants.DESKey,
							Constants.DESIV, "dc|" + userNm.replace("dc/", "")
									+ "|" + pwd);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setToken(token);
					Constants.userName = userNm;
					Constants.userToken = token;
					if (isSave) {
						Constants.userPwd = pwd;
					} else {
						Constants.userPwd="";
//						Constants.clearUserInfo();
					}
					mConfig.setIsSavaPasswd(isSave);
					Constants.saveUserInfo();

					// 读取用户信息
					JSONObject staffMsg = obj.isNull("info") ? new JSONObject()
							: obj.getJSONObject("info");
					String staffName = staffMsg.isNull("UserName") ? ""
							: staffMsg.getString("UserName");
					String post = staffMsg.isNull("UserPost") ? "" : staffMsg
							.getString("UserPost");
					String userImgUrl = staffMsg.isNull("UserImgUrl") ? ""
							: staffMsg.getString("UserImgUrl");
					String stafUserId = staffMsg.isNull("UserId") ? ""
							: staffMsg.getString("UserId");
					String stafUserMobile = staffMsg.isNull("UserMobile") ? ""
							: staffMsg.getString("UserMobile");
					String tel = staffMsg.isNull("UserTel") ? "" : staffMsg
							.getString("UserTel");
					String gender = staffMsg.isNull("UserGender") ? ""
							: staffMsg.getString("UserGender");
					String mail = staffMsg.isNull("UserEmail") ? "" : staffMsg
							.getString("UserEmail");
					String crop = staffMsg.isNull("CorpName") ? "" : staffMsg
							.getString("CorpName");
					String nameEn = staffMsg.isNull("UserNameEn") ? ""
							: staffMsg.getString("UserNameEn");
					String dept = staffMsg.isNull("DeptName") ? "" : staffMsg
							.getString("DeptName");
					String staffPhone = staffMsg.isNull("StaffPhone") ? ""
							: staffMsg.getString("StaffPhone");
					boolean isShowPriNumber = Boolean.parseBoolean(staffMsg
							.getString("UserMobilePublic").toLowerCase()
							.toString());
					boolean isShowWorkNumber = Boolean.parseBoolean(staffMsg
							.getString("StaffPhonePublic").toLowerCase()
							.toString());
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setPNumber(isShowPriNumber);

					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setWNumber(isShowWorkNumber);

					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserId(stafUserId);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserMobile(stafUserMobile);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserTel(tel);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserGender(gender);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserMail(mail);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserCrop(crop);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserNameEn(nameEn);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserDept(dept);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserStaffName(staffName);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserImageUrl(userImgUrl);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserPost(post);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserStaffPhone(staffPhone);
					// 是否是第一次登录
					if (!obj.isNull("isFirst")) {
						String flag = obj.getString("isFirst");
						if (flag.equals("1")) {
							Toast.makeText(LoginActivity.this,
									getString(R.string.modify_pwd_first_login),
									Toast.LENGTH_SHORT).show();
							showModifyPwd();
							return;
						}
					}
					// 过期提示
					if (!obj.isNull("day")) {
						String daystr = obj.getString("day");
						Log.d("day", daystr);
						int day = Integer.parseInt(daystr);
						if (SharedPrefsConfig
								.getSharedPrefsInstance(LoginActivity.this)
								.getUserName().startsWith("w_")) // 非外包默认为0
						{
							SharedPrefsConfig.getSharedPrefsInstance(
									LoginActivity.this).setExpiredDay(day);
							if (day <= 3) {
								Toast.makeText(
										LoginActivity.this,
										String.format(
												getString(R.string.modify_tips3),
												day), Toast.LENGTH_SHORT)
										.show();
							}
						}
					}

					// 跳转
					Intent intent = new Intent();
					// SharedPreferences sp =
					// PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
					// int isRead = sp.getInt("isReadFlg", 1);//不进入帮助页
					// if (isRead == 0) {
					// intent.setClass(LoginActivity.this, HelpActivity.class);
					// } else {
					intent.setClass(LoginActivity.this, HomeActivity.class);
					// }
					startActivity(intent);
					LoginActivity.this.finish();
				} else if (result.equalsIgnoreCase("False")) {
					Toast.makeText(LoginActivity.this,
							resource.getString(R.string.login_name_pwd_wrong),
							Toast.LENGTH_SHORT).show();
				} else if (result.equalsIgnoreCase("TO")) {
					Toast.makeText(LoginActivity.this,
							resource.getString(R.string.modify_tips1),
							Toast.LENGTH_SHORT).show();
					showModifyPwd();
				} else if (result.equalsIgnoreCase("Freeze")) {
					Toast.makeText(LoginActivity.this,
							resource.getString(R.string.modify_tips2),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this,
							resource.getString(R.string.login_net_wrong),
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(LoginActivity.this, R.string.enter_error,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};

	// add begin lhy 2013/07/01
	// 点击空白区域隐藏软键盘
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// System.out.println("down");
			if (LoginActivity.this.getCurrentFocus() != null) {
				if (LoginActivity.this.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(LoginActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	// add end 2013/07/01
	private Dialog modifyDialog = null;
	private String nPwd = "";

	private void showModifyPwd() {
		if (modifyDialog != null) {
			modifyDialog.dismiss();
		}
		View view = LayoutInflater.from(LoginActivity.this).inflate(
				R.layout.modify_pwd, null);
		final EditText etold = (EditText) view.findViewById(R.id.et_old_pwd);
		final EditText etnew = (EditText) view.findViewById(R.id.et_new_pwd);
		final EditText etconfirm = (EditText) view
				.findViewById(R.id.et_confirm_pwd);
		view.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						DownLoadManager.getInstance().stopAll();
						CMAAPPInatallerManager.stopUnzip();
						modifyDialog.dismiss();
					}
				});
		view.findViewById(R.id.btn_modify).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (etold.getText().toString().equals("")
								|| etnew.getText().toString().equals("")
								|| etconfirm.getText().toString().equals("")) {
							Toast.makeText(LoginActivity.this,
									getString(R.string.modify_not_input),
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (!etnew.getText().toString()
								.equals(etconfirm.getText().toString())) {
							Toast.makeText(LoginActivity.this,
									getString(R.string.modify_not_same),
									Toast.LENGTH_SHORT).show();
							return;
						}
						if ((etnew.getText().toString().length() < 8)) {
							Toast.makeText(
									LoginActivity.this,
									getString(R.string.modify_pwd_length_error),
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (etnew.getText().toString()
								.equals(etold.getText().toString())) {
							Toast.makeText(LoginActivity.this,
									getString(R.string.modify_cannot_same),
									Toast.LENGTH_SHORT).show();
							return;
						}

						String oldcode = FileOp.DESencode(Constants.DESKey,
								Constants.DESIV, "dc|"
										+ userName.getText().toString()
												.replace("dc/", "") + "|"
										+ etold.getText().toString());
						String newcode = FileOp.DESencode(Constants.DESKey,
								Constants.DESIV, "dc|"
										+ userName.getText().toString()
												.replace("dc/", "") + "|"
										+ etnew.getText().toString());
						nPwd = etnew.getText().toString();
						modifyPwd(oldcode, newcode);
					}
				});
		modifyDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				getWindowManager().getDefaultDisplay().getWidth() * 90 / 100,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		modifyDialog.setContentView(view, params);
		Window window = modifyDialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		modifyDialog.setCanceledOnTouchOutside(false);
		modifyDialog.setCancelable(false);
		modifyDialog.show();
	}

	/**
	 * 修改密码
	 */
	private void modifyPwd(String oldcode, String newcode) {

		StringBuilder postStr = new StringBuilder();
		postStr.append("&oldpwd=").append(oldcode).append("&newpwd=")
				.append(newcode);
		HttpUtil con = new HttpUtil();
		con.iResult = onModifyPwdResult;
		con.context = LoginActivity.this;
		con.isShowDialog = true;
		con.executeTask(true, "chgPwd", postStr.toString());
	}

	/**
	 * 修改密码返回
	 */
	private IResult onModifyPwdResult = new IResult() {

		@Override
		public void OnResult(String jsonStr) {

			try {
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);
				String result = obj.getString("result");
				if (result.equalsIgnoreCase("true")) {
					if (modifyDialog != null) {
						modifyDialog.dismiss();
					}
					Toast.makeText(LoginActivity.this,
							getString(R.string.modify_ok), Toast.LENGTH_SHORT)
							.show();
					userPwd.setText(nPwd);
					// 保存信息
					String userNm = userName.getText().toString();
					String pwd = userPwd.getText().toString();
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setUserName(userNm);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setPassWord(pwd);
					SharedPrefsConfig
							.getSharedPrefsInstance(LoginActivity.this)
							.setIsSavePwd(isSaveUserInfo);
					// 跳转直接进主页
					Intent intent = new Intent();
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(LoginActivity.this);
					int isRead = sp.getInt("isReadFlg", 0);
					if (isRead == 0) {
						intent.setClass(LoginActivity.this, HelpActivity.class);
					} else {
						intent.setClass(LoginActivity.this, HomeActivity.class);
					}
					startActivity(intent);
					LoginActivity.this.finish();
				} else {
					Toast.makeText(LoginActivity.this,
							getString(R.string.modify_fail), Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				Log.e("modify pwd", jsonStr);
				e.printStackTrace();
			}

		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};

}
