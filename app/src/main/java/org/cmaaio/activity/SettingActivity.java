package org.cmaaio.activity;

import org.cmaaio.common.Constants;
import org.cmaaio.common.DownLoadListener;
import org.cmaaio.common.DownLoadManager;
import org.cmaaio.common.FileOp;
import org.cmaaio.common.XProcessBar;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.sysdownloadmanager.CMAAPPInatallerManager;
import org.cmaaio.ui.ConfirmDialog;
import org.cmaaio.util.ActivityManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/***
 * 设置
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends CMABaseActivity {

	private LinearLayout propSel;

	private LinearLayout layoutExit;

	private LinearLayout layoutCheckAppVer = null;
	private LinearLayout layoutHelp = null;
	private LinearLayout layoutClear = null;

	private ProgressDialog dialog = null;

	private LinearLayout fullScreenLayout;

	private Handler handler;

	private String strApkURL = "http://www.dubblogs.cc:8751/Android/Test/Apk/EX04_14.apk";

	private View layoutModify = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.setting);
		super.initTaBar(2);

		propSel = (LinearLayout) this.findViewById(R.id.layout_send);
		propSel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, SetPropActivity.class);
				startActivity(intent);
			}
		});

		layoutExit = (LinearLayout) this.findViewById(R.id.layout_exit);
		layoutExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showConfirmDialog("Exit");
			}
		});

		layoutHelp = (LinearLayout) findViewById(R.id.layout_help);
		layoutHelp.setOnClickListener(listenerHelp);

		layoutCheckAppVer = (LinearLayout) findViewById(R.id.layout_check_app_ver);
		layoutCheckAppVer.setOnClickListener(listenerCheckAppVer);

		layoutClear = (LinearLayout) findViewById(R.id.layout_clear);
		layoutClear.setOnClickListener(listenerClear);

		fullScreenLayout = (LinearLayout) findViewById(R.id.setting_full_screen_layout);
		fullScreenLayout.setOnClickListener(fullScreenListener);

		layoutModify = findViewById(R.id.layout_modify);
		layoutModify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showModifyPwd();
			}
		});

		findViewById(R.id.layout_userinfo).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Constants.isOfflineLogin()) {
							Toast.makeText(SettingActivity.this, R.string.offline_mode, Toast.LENGTH_SHORT).show();
						}else{
							Intent intent = new Intent(SettingActivity.this,
									UserInfomationViewActivity.class);
							startActivity(intent);
						}
					}
				});
		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		showModifyView();
	};

	OnClickListener fullScreenListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, FullScreenActivity.class);
			startActivity(intent);

		}
	};
	// add begin lhy 2013/06/21
	OnClickListener listenerHelp = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, HelpActivity.class);
			startActivity(intent);
		}
	};

	OnClickListener listenerCheckAppVer = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (Constants.isOfflineLogin()) {
				Toast.makeText(SettingActivity.this, R.string.offline_mode, Toast.LENGTH_SHORT).show();
			}else{
				getCheckAppVer();
			}
		}
	};

	OnClickListener listenerClear = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			showConfirmDialog("Clear");
		}
	};

	private void showConfirmDialog(final String showType) {
		LayoutInflater li = (LayoutInflater) SettingActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = li.inflate(R.layout.confirm_dialog, null, false);

		final TextView tipTitle = ((TextView) view
				.findViewById(R.id.DialogTipTitle));
		if ("Exit".equals(showType)) {
			tipTitle.setText(R.string.exit_alert);
			view.findViewById(R.id.layout_1btn).setVisibility(View.GONE);
			view.findViewById(R.id.layout_2btn).setVisibility(View.VISIBLE);
		} else if ("CheckAppVer".equals(showType)) {
			tipTitle.setText(R.string.app_update_tips);
			view.findViewById(R.id.layout_1btn).setVisibility(View.GONE);
			((Button) view.findViewById(R.id.DialogCancel)).setText(this
					.getResources().getString(R.string.confirm_ignore));
			view.findViewById(R.id.layout_2btn).setVisibility(View.VISIBLE);
		} else if ("Clear".equals(showType)) {
			tipTitle.setText(R.string.clear_alert);
			view.findViewById(R.id.layout_1btn).setVisibility(View.GONE);
			view.findViewById(R.id.layout_2btn).setVisibility(View.VISIBLE);
		} else {
			tipTitle.setText(R.string.check_isnew);
			view.findViewById(R.id.layout_2btn).setVisibility(View.GONE);
			view.findViewById(R.id.layout_1btn).setVisibility(View.VISIBLE);
		}

		view.findViewById(R.id.DialogTip).setVisibility(View.GONE);

		final Dialog backDialog = new ConfirmDialog(SettingActivity.this,
				R.style.FullHeightDialog);
		backDialog.setContentView(view);
		backDialog.show();

		Button cancelButton = (Button) view.findViewById(R.id.DialogCancel);

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				backDialog.dismiss();
			}
		});

		Button okButton = (Button) view.findViewById(R.id.DialogOk);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ("Exit".equals(showType)) {
					Constants.clearUserInfo();
					SharedPrefsConfig.getSharedPrefsInstance(
							Cmaaio.getInstance()).setPassWord("");
					ActivityManager.getInstance().exit();
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(Cmaaio.getInstance(), LoginActivity.class);
					Cmaaio.getInstance().startActivity(intent);
					SettingActivity.this.finish();
				} else if ("Clear".equals(showType)) {
					backDialog.dismiss();
					clearCache();
				} else {
					backDialog.dismiss();
					// 下载apk包
					// DownLoadManager.getInstance().setDownListener(downListener);
					// showDownloadProgress(strApkURL);
					ActivityManager.getInstance().exit();
					SettingActivity.this.finish();

				}
			}
		});

		Button confirmButton = (Button) view.findViewById(R.id.dialog_confirm);

		confirmButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				backDialog.dismiss();
			}
		});
	}

	private void clearCache() {
		// 清除缓存
		dialog = ProgressDialog.show(SettingActivity.this, getResources()
				.getString(R.string.prompt),
				getResources().getString(R.string.clearing), true, true);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
					// TODO 删除图片缓存
					FileOp.getInstance().delFileDir(
							Constants.cachePath + Constants.AsyncImageCache
									+ "/");
					FileOp.getInstance().delFileDir(
							Environment.getExternalStorageDirectory()
									+ "/CMAAIO/" + Constants.userName + "/"
									+ Constants.DownLoadCache + "/");
					// TODO 删除其他缓存代码放这里...
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
			}
		};
	}

	private boolean checkUpdate(long version) {
		boolean isNewVer = false;
		// 版本初始化
		int currentVersion = 0;
		try {
			// 获取当前AndroidManifest.xml文件中的版本号
			currentVersion = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode; // 设置本地版本号
			System.out.println("currentVersion=========>" + currentVersion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (version > currentVersion) {
			isNewVer = true;
		}

		return isNewVer;
	}

	private void getCheckAppVer() {
		HttpUtil con = new HttpUtil();
		con.iResult = SettingActivity.this.onNetCheckAppVer;
		con.context = SettingActivity.this;

		String postStrOther = "&os=" + "Android";
		con.executeTask(true, "CheckAppVer", postStrOther);
	}

	private IResult onNetCheckAppVer = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			Log.d("whz", "result----------------->"+jsonStr);
			try {
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);
				System.out.println(obj.toString());

				if ("False".equals(obj.get("result"))) {
					Toast.makeText(SettingActivity.this, R.string.load_fail,
							Toast.LENGTH_SHORT).show();
					return;
				}

				strApkURL = obj.getString("address");
				// System.out.println("address=======>" +
				// obj.getString("address"));
				// strApkURL =
				// "http://gdown.baidu.com/data/wisegame/f24708c121939e2d/360weishi_165.apk";
				String loacalVersion = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName;
				String netVersion = obj.getString("ver");
				if (netVersion.compareTo(loacalVersion) > 0) {
					showConfirmDialog("CheckAppVer");
				} else {
					showConfirmDialog("New");
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(SettingActivity.this, R.string.check_app_type,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(SettingActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};
	// add end 2013/06/21

	// 下载
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
		View view = LayoutInflater.from(SettingActivity.this).inflate(
				R.layout.download_dialog, null);
		dialogDownPrcessBar = (XProcessBar) view
				.findViewById(R.id.downProcessBar);
		dialogDownPrcessBar.setDownUrl(url);
		view.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						DownLoadManager.getInstance().stopAll();
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
		public void onProcess(String url, long have, long total) {
			if (dialogDownPrcessBar != null)
				dialogDownPrcessBar.onDownProcess(url, have, total);

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

	protected void onDestroy() {
		DownLoadManager.getInstance().setDownListener(null);
		super.onDestroy();

	};

	/**
	 * 显示
	 */
	private void showModifyView() {
		// int day =
		// SharedPrefsConfig.getSharedPrefsInstance(this).getExpiredDay();
		if (SharedPrefsConfig.getSharedPrefsInstance(SettingActivity.this)
				.getUserName().startsWith("w_")) {
			layoutModify.setVisibility(View.VISIBLE);
		} else {
			layoutModify.setVisibility(View.GONE);
		}
	}

	// 修改密码
	private Dialog modifyDialog = null;
	private EditText etnew = null;

	private void showModifyPwd() {
		if (modifyDialog != null) {
			modifyDialog.dismiss();
		}
		View view = LayoutInflater.from(SettingActivity.this).inflate(
				R.layout.modify_pwd, null);
		final EditText etold = (EditText) view.findViewById(R.id.et_old_pwd);
		etnew = (EditText) view.findViewById(R.id.et_new_pwd);
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
							Toast.makeText(SettingActivity.this,
									getString(R.string.modify_not_input),
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (!etnew.getText().toString()
								.equals(etconfirm.getText().toString())) {
							Toast.makeText(SettingActivity.this,
									getString(R.string.modify_not_same),
									Toast.LENGTH_SHORT).show();
							return;
						}
						if ((etnew.getText().toString().length() < 8)) {
							Toast.makeText(
									SettingActivity.this,
									getString(R.string.modify_pwd_length_error),
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (etnew.getText().toString()
								.equals(etold.getText().toString())) {
							Toast.makeText(SettingActivity.this,
									getString(R.string.modify_cannot_same),
									Toast.LENGTH_SHORT).show();
							return;
						}
						String uname = SharedPrefsConfig
								.getSharedPrefsInstance(SettingActivity.this)
								.getUserName();
						String oldcode = FileOp.DESencode(Constants.DESKey,
								Constants.DESIV,
								"dc|" + uname.replace("dc/", "") + "|"
										+ etold.getText().toString());
						String newcode = FileOp.DESencode(Constants.DESKey,
								Constants.DESIV,
								"dc|" + uname.replace("dc/", "") + "|"
										+ etnew.getText().toString());
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
		con.context = SettingActivity.this;
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
					SharedPrefsConfig.getSharedPrefsInstance(
							SettingActivity.this).setPassWord("");
					SharedPrefsConfig.getSharedPrefsInstance(
							SettingActivity.this).setExpiredDay(-1);
					showModifyView();
					Toast.makeText(SettingActivity.this,
							getString(R.string.modify_ok), Toast.LENGTH_SHORT)
							.show();
					if (modifyDialog != null) {
						modifyDialog.dismiss();
					}
				} else {
					Toast.makeText(SettingActivity.this,
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
			Toast.makeText(SettingActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};
}
