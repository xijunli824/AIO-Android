package org.cmaaio.activity;

import java.util.List;

import org.cmaaio.common.AppDataController;
import org.cmaaio.common.Constants;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.util.BDUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = "whz";

	AlertDialog.Builder builder;

	/**
	 * 绑定成功，发请求到HomeActivity[在onNewIntent中进行处理]
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.d(TAG, "百度推送绑定成功----------->" + responseString);
		Intent intent = new Intent(BDUtils.ACTION_RESPONSE);
		intent.putExtra(BDUtils.RESPONSE_ERRCODE, errorCode);
		intent.putExtra(BDUtils.APPID, appid);
		intent.putExtra(BDUtils.USERID, userId);
		intent.putExtra(BDUtils.CHANNEID, channelId);
		intent.setClass(context, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 消息接收器
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = "接收到的透传消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		Log.i(TAG, messageString);
		final SharedPrefsConfig spc = SharedPrefsConfig
				.getSharedPrefsInstance(Cmaaio.getInstance());
		final String key = spc.getUserName() + SharedPrefsConfig.APPPUSH;
		boolean isPush = spc.getSharedPreferences().getBoolean(key, true);
		// 已设置不接收推送消息
		if (!isPush)
			return;
		String content = "none";// 显示内容
		Intent notificationIntent = new Intent();// 点击该通知后要跳转的Activity
		try {
			JSONObject jsonMsg = new JSONObject(message);
			JSONObject aps = (JSONObject) jsonMsg.get("aps");
			content = aps.getString("alert");// 显示标题
			String badge = aps.getString("badge");// 角标
			if (!jsonMsg.isNull("appid")) {
				JSONArray detail = null;
				try {
					detail = jsonMsg.getJSONArray("appid");
				} catch (Exception e) {
					e.printStackTrace();
				}
				String appid = detail.getString(0);// 应用的id
				CMAAppEntity app = AppDataController.getInstance().getAppById(
						appid.toString());
				// 类型决定
				if (app != null) {
					if (app.appType.contains(CMAAppEntity.WEBAPP)) {
						notificationIntent = new Intent();
						notificationIntent.putExtra("appUrl", app.appPath);
						notificationIntent.putExtra("appName", app.appName);
						SharedPrefsConfig sp = SharedPrefsConfig
								.getSharedPrefsInstance(context);
						String tag = sp.getUserName() + "_"
								+ SharedPrefsConfig.APPFULLSCREEN;
						String tagApp = sp.getUserName() + "_" + app.appId
								+ SharedPrefsConfig.APPFULLSCREEN;
						if (!sp.getSharedPreferences().getBoolean(tag, true)) {
							notificationIntent.putExtra("isFullScreen", false);
						} else {
							notificationIntent.putExtra(
									"isFullScreen",
									sp.getSharedPreferences().getBoolean(
											tagApp, true));
						}
						notificationIntent.setClass(context,
								WebViewActivity.class);
					} else if (app.appType.contains(CMAAppEntity.HYBRIDAPP)) {
						notificationIntent = new Intent();
						if (detail.length() < 3) {// 长度小于3表示只打开应用,不进详情
							notificationIntent.putExtra("hasDetail", false);
						} else {
							notificationIntent.putExtra("hasDetail", true);
							notificationIntent.putExtra("pushType",
									detail.get(1).toString());
							notificationIntent.putExtra("pushCode",
									detail.get(2).toString());
						}
						notificationIntent.putExtra("CurAppId", app.appId);// 当前进入phonegap的id
						notificationIntent.putExtra("appUrl", app.appPath);
						SharedPrefsConfig sp = SharedPrefsConfig
								.getSharedPrefsInstance(context);
						String tag = sp.getUserName() + "_"
								+ SharedPrefsConfig.APPFULLSCREEN;
						String tagApp = sp.getUserName() + "_" + app.appId
								+ SharedPrefsConfig.APPFULLSCREEN;
						if (!sp.getSharedPreferences().getBoolean(tag, true)) {
							notificationIntent.putExtra("isFullScreen", false);
						} else {
							notificationIntent.putExtra(
									"isFullScreen",
									sp.getSharedPreferences().getBoolean(
											tagApp, true));
						}
						notificationIntent
								.setClass(context, PGapActivity.class);
					} else if (app.appType.contains(CMAAppEntity.NATIVEAPP)) {
						ResolveInfo resolveInfo = null;
						PackageManager packageManager = context
								.getPackageManager();
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
							return;
						}
						notificationIntent = new Intent(Intent.ACTION_VIEW);
						notificationIntent
								.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						ComponentName comp = new ComponentName(app.appPath,
								resolveInfo.activityInfo.name);
						notificationIntent.setComponent(comp);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 通知管理器
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// 点击意图
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon);

		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				context)
				.setContentTitle(
						Cmaaio.getInstance().getResources()
								.getString(R.string.you_hava_one_push_msg))
				.setContentText(content)
				.setTicker(
						Cmaaio.getInstance().getResources()
								.getString(R.string.kd_msg_push))
				.setSmallIcon(R.drawable.icon)
				.setAutoCancel(true)
				.setLargeIcon(largeIcon)
				.setContentIntent(contentIntent)
				.setDefaults(
						Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE);
		// 把Notification传递给 NotificationManager
		mNotificationManager.notify(
				(int) SystemClock.currentThreadTimeMillis(), noti.build());
		// 如果homeactivity退出了,打开homeactivity
		if (Constants.isHomeFinish) {
			Intent responseIntent = null;
			responseIntent = new Intent(BDUtils.ACTION_MESSAGE);
			responseIntent.putExtra(BDUtils.EXTRA_MESSAGE, message);
			responseIntent.setClass(context, HomeActivity.class);
			responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(responseIntent);
		}
	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 */
	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "接收到的通知点击 title=\"" + title + "\" description=\""
				+ description + "\" customContent=" + customContentString;
		Log.i(TAG, notifyString);
	}

	/**
	 * 设置标签回调函数
	 */
	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, "百度推送设置标签成功---------》" + responseString);

	}

	/**
	 * 账号绑定错误回调函数
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		// TODO Auto-generated method stub
	}
}
