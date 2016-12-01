package org.cmaaio.sysdownloadmanager;

import java.util.ArrayList;
import java.util.List;

import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.HomeActivity;
import org.cmaaio.activity.R;
import org.cmaaio.adapter.AppItemAdapter;
import org.cmaaio.common.AppDataController;
import org.cmaaio.common.Constants;
import org.cmaaio.common.FileOp;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class CMAAPPInatallerManager {
	public static CMAAppEntity currentNativeApp = null;
	public static AppItemAdapter currentAdapter = null;
	public static Button currentBtn = null;
	public static String currentNativeSDPath = null;
	private static List<Thread> threadList = new ArrayList<Thread>();
	public static boolean canUnzip = true;
	public static String FINISH_UPDATE_BROATCAST = "org.cmaaio.sysdownloadmanager";

	public static void install(final Context context, String url, final String sdPath, final CMAAppEntity entity, final AppItemAdapter adapter) {
		threadList.clear();
		canUnzip = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				if (CMAAppEntity.HYBRIDAPP.equals(entity.appType)) {// html
					try {
						FileOp.upZipFile(sdPath, Constants.rootPath + Constants.userName + Constants.appFloder);
					} catch (Exception e) {
						Log.e("cmaio", "unzip failed:" + e.getMessage());
						Cmaaio.getMainHandler().post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(context, context.getString(R.string.app_install_failed), Toast.LENGTH_SHORT).show();
							}
						});

						return;
					}
					if (!canUnzip)
						return;

					// 更新
					if (AppDataController.getInstance().contain(entity)) {
						CMAAppEntity oldApp = AppDataController.getInstance().getAppById(entity.appId);
						if (entity.AppVersion.compareTo(oldApp.AppVersion) > 0) {
							FileOp.getInstance().delFile(sdPath);
							entity.appPath = "file://" + Constants.rootPath + Constants.userName + Constants.appFloder + entity.appName + "/index.html";
							AppDataController.getInstance().remove(oldApp);
							AppDataController.getInstance().add(entity);
							CMAAPPInatallerManager.uploadDownloadRecord(entity);// 上传下载记录
							CMAAPPInatallerManager.userManageAppPush(entity, 1);// 消息推送
						}
					}

					if (!AppDataController.getInstance().contain(entity)) {
						FileOp.getInstance().delFile(sdPath);
						entity.appPath = "file://" + Constants.rootPath + Constants.userName + Constants.appFloder + entity.appName + "/index.html";
						AppDataController.getInstance().add(entity);
						CMAAPPInatallerManager.uploadDownloadRecord(entity);// 上传下载记录
						CMAAPPInatallerManager.userManageAppPush(entity, 1);// 消息推送
					}
					// 回首页
					Intent intent = new Intent(context, HomeActivity.class);
					context.startActivity(intent);

				} else if (CMAAppEntity.NATIVEAPP.equals(entity.appType)) {// apk

					Intent promptInstall = new Intent(android.content.Intent.ACTION_VIEW);
					promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					promptInstall.setDataAndType(Uri.parse("file://" + sdPath), "application/vnd.android.package-archive");
					context.startActivity(promptInstall);
					// 设置成当前
					currentNativeApp = entity;
					currentAdapter = adapter;
					currentNativeSDPath = sdPath;

				}

				Handler mHandler = new Handler(Looper.getMainLooper());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			}
		});
		thread.start();
		threadList.add(thread);

	}

	public static void install(final Context context, String url, final String sdPath, final CMAAppEntity entity, final Button btn) {
		threadList.clear();
		canUnzip = true;
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (CMAAppEntity.HYBRIDAPP.equals(entity.appType)) {// html
					try {
						FileOp.upZipFile(sdPath, Constants.rootPath + Constants.userName + Constants.appFloder);
					} catch (Exception e) {
						Log.e("cmaio", "unzip failed:" + e.getMessage());
						Cmaaio.getMainHandler().post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(context, context.getString(R.string.app_install_failed), Toast.LENGTH_SHORT).show();
							}
						});
						return;
					}
					if (!canUnzip) {
						return;
					}

					// 更新
					if (AppDataController.getInstance().contain(entity)) {
						CMAAppEntity oldApp = AppDataController.getInstance().getAppById(entity.appId);
						if (entity.AppVersion.compareTo(oldApp.AppVersion) > 0) {
							FileOp.getInstance().delFile(sdPath);
							entity.appPath = "file://" + Constants.rootPath + Constants.userName + Constants.appFloder + entity.appName + "/index.html";
							AppDataController.getInstance().remove(oldApp);
							AppDataController.getInstance().add(entity);
							CMAAPPInatallerManager.uploadDownloadRecord(entity);// 上传下载记录
							CMAAPPInatallerManager.userManageAppPush(entity, 1);// 消息推送
						}
					}

					if (!AppDataController.getInstance().contain(entity)) {
						FileOp.getInstance().delFile(sdPath);
						entity.appPath = "file://" + Constants.rootPath + Constants.userName + Constants.appFloder + entity.appName + "/index.html";
						AppDataController.getInstance().add(entity);
						CMAAPPInatallerManager.uploadDownloadRecord(entity);// 上传下载记录
						CMAAPPInatallerManager.userManageAppPush(entity, 1);// 消息推送
					}
					// 回首页
					Intent intent = new Intent(context, HomeActivity.class);
					context.startActivity(intent);
					Handler mHandler = new Handler(Looper.getMainLooper());
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							btn.setText(context.getResources().getString(R.string.app_have_install));
							btn.setOnClickListener(null);
						}
					});

				} else if (CMAAppEntity.NATIVEAPP.equals(entity.appType)) {// apk

					Intent promptInstall = new Intent(android.content.Intent.ACTION_VIEW);
					promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					promptInstall.setDataAndType(Uri.parse("file://" + sdPath), "application/vnd.android.package-archive");
					context.startActivity(promptInstall);
					// 设置成当前
					currentNativeApp = entity;
					currentBtn = btn;
					currentNativeSDPath = sdPath;
				}
			}
		});
		thread.start();
		threadList.add(thread);
	}

	public static void install(final Context context, String url, final String sdPath, final CMAAppEntity entity) {
		threadList.clear();
		canUnzip = true;
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (CMAAppEntity.HYBRIDAPP.equals(entity.appType)) {// html
					try {
						FileOp.upZipFile(sdPath, Constants.rootPath + Constants.userName + Constants.appFloder);
					} catch (Exception e) {
						Log.e("cmaio", "unzip failed:" + e.getMessage());
					}
					if (!canUnzip) {
						return;
					}

					// 更新
					if (AppDataController.getInstance().contain(entity)) {
						CMAAppEntity oldApp = AppDataController.getInstance().getAppById(entity.appId);
						if (entity.AppVersion.compareTo(oldApp.AppVersion) > 0) {
							FileOp.getInstance().delFile(sdPath);
							entity.appPath = "file://" + Constants.rootPath + Constants.userName + Constants.appFloder + entity.appName + "/index.html";
							AppDataController.getInstance().remove(oldApp);
							AppDataController.getInstance().add(entity);
							CMAAPPInatallerManager.uploadDownloadRecord(entity);// 上传下载记录
							CMAAPPInatallerManager.userManageAppPush(entity, 1);// 消息推送
							// 广播通知安装成功
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							bundle.putString("NewAppID", entity.appId);
							bundle.putString("AppVersion", entity.AppVersion);
							bundle.putString("AppAliases", entity.AppAliases);
							intent.putExtras(bundle);
							intent.setAction(FINISH_UPDATE_BROATCAST);
							context.sendBroadcast(intent);
						}
					}

				} else if (CMAAppEntity.NATIVEAPP.equals(entity.appType)) {// apk

					Intent promptInstall = new Intent(android.content.Intent.ACTION_VIEW);
					promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					promptInstall.setDataAndType(Uri.parse("file://" + sdPath), "application/vnd.android.package-archive");
					context.startActivity(promptInstall);
					// 设置成当前
					currentNativeApp = entity;
					currentNativeSDPath = sdPath;
				}
			}
		});
		thread.start();
		threadList.add(thread);
	}

	public static void stopUnzip() {
		canUnzip = false;
		for (int i = 0; i < threadList.size(); i++) {
			threadList.get(i).interrupt();
		}
	}

	public static void uploadDownloadRecord(CMAAppEntity app) {
		SharedPrefsConfig spc = SharedPrefsConfig.getSharedPrefsInstance(Cmaaio.instance);
		String postStr = "&AppId=" + app.appId + "&OS=android" + "&ver=" + app.AppVersion + "&ADAcount=" + spc.getUserName();
		HttpUtil http = new HttpUtil();
		http.iResult = uploadDownloadRecordListener;
		http.context = Cmaaio.getInstance();
		http.isShowDialog = false;
		http.executeTask(true, "RegisterDownloadHistory", postStr);
	}

	private static IResult uploadDownloadRecordListener = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				Log.d("RegisterDownloadHistory", jsonStr);
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

	public static void userManageAppPush(CMAAppEntity app, int flag) {
		SharedPrefsConfig spc = SharedPrefsConfig.getSharedPrefsInstance(Cmaaio.instance);
		String postStr = "&AppId=" + app.appId + "&OS=android" + "&OSVer=4.1" + "&ADAcount=" + spc.getUserName() + "&Type=" + flag + "&Token=&channelID=" + spc.getBaiduPushChannelId();
		HttpUtil http = new HttpUtil();
		http.iResult = userManageAppPushListener;
		http.context = Cmaaio.getInstance();
		http.isShowDialog = false;
		http.executeTask(true, "UserManageAppPush", postStr);
	}

	private static IResult userManageAppPushListener = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				Log.d("UserManageAppPush", jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void OnFail(String errorMsg) {
			Log.e("UserManageAppPush", errorMsg);
		}

		@Override
		public void OnCacnel() {

		}
	};

}
