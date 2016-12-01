package org.cmaaio.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.apache.http.util.EncodingUtils;
import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.util.CMATool;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.Display;

public class Constants {
	private static Activity context = null;
	public static String rootPath = Environment.getExternalStorageDirectory()
			+ "/CMAAIO/";
	public static String cachePath = Environment.getExternalStorageDirectory()
			+ "/CMAAIO/Cache/";
	public static String dataFilePath = Environment
			.getExternalStorageDirectory() + "/CMAAIO/info/";// 存储用户信息
	private static String dataFileName = "userInfo.dat";
	public static String appFloder = "/APPs/";
	public static String version = "0.9.1";

	/**
	 * UAT打包
	 */
	// public static String KServerIp = "https://maiouat.capitaretail.com.cn/";
	// public static String KServerurl =
	// "https://maiouat.capitaretail.com.cn/api/";

	// UAT[未用，待测试]
	// public static String KServerIp = "http://maiouat.capitaretail.com.cn/";
	// public static String KServerurl =
	// "http://maiouat.capitaretail.com.cn/api/";

	/**
	 * 生产打包
	 */
	 public static String KServerIp = "https://maio.capitaretail.com.cn/";
	 public static String KServerurl =
	 "https://maio.capitaretail.com.cn/api/";

	public static String AsyncImageCache = "AsyncImg";
	public static String DownLoadCache = "DownLoadCache";
	public static String OSVersion = android.os.Build.VERSION.SDK;
	public static String AppVersion = "0,0";
	public static int screenW = 0;
	public static int screenH = 0;
	public static String DESIV = "cmamobil";// cmamobileencrpt,iv和key都必须是8位，ios上超过8位不报错，android会报错
	public static String DESKey = "mobileen";

	public static String NetFail = Cmaaio.getInstance().getResources()
			.getString(R.string.network_connection_fails);

	// for userinfo
	public static String userName = "";
	public static String userPwd = "";
	public static String userToken = "";
	public static String language = Cmaaio.getInstance().getResources()
			.getString(R.string.zh);
	public static String curAppId = "";

	public static String itellID = "";
	public static String itrackID = "";
	public static String iemID = "";
	public static boolean hasDetail = false;// 推送子应用是否进入详情的标识
	public static String pushType = "";// 进入详情的type
	public static String pushCode = "";// 进入详情的code
	public static String forwardValue = "";
	public static boolean isHomeFinish = true;
	/**
	 * 记录是否是离线登陆
	 */
	private static boolean isOfflineLogin = false;

	public static boolean isRemeberPWD = true;

	public static void setContext(Activity context) {
		Constants.context = context;
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = Constants.context
					.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(
					Constants.context.getPackageName(), 0);
			String version = packInfo.versionName;
			AppVersion = version;

			Display display = Constants.context.getWindowManager()
					.getDefaultDisplay();
			screenW = display.getWidth();
			screenH = display.getHeight();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveUserInfo() {
		String lau = Locale.getDefault().getLanguage();
		if (lau.equals("zh"))
			language = Cmaaio.getInstance().getString(R.string.zh);
		if (lau.equals("en"))
			language = Cmaaio.getInstance().getString(R.string.en);
		String xpwd = CMATool.encode(userPwd);
		String data = "userName=" + userName + "&userPwd=" + xpwd
				+ "&userToken=" + userToken + "&language=" + language
				+ "&curAppId=" + curAppId;
		File dataPath = new File(dataFilePath);
		if (!dataPath.exists())
			dataPath.mkdirs();
		File file = new File(dataFilePath + dataFileName);
		try {
			// 写入数据
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(data.getBytes());
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void readUserInfo() {
		File file = new File(dataFilePath + dataFileName);
		if (file.exists()) {
			try {
				FileInputStream in = new FileInputStream(file);
				int length = (int) file.length();
				byte[] temp = new byte[length];
				in.read(temp, 0, length);
				String data = EncodingUtils.getString(temp, "UTF-8");
				String[] items = data.split("&");
				for (int i = 0; i < items.length; i++) {
					String[] keyAndValue = items[i].split("=", -1);
					if (keyAndValue[0].equals("userName"))
						userName = keyAndValue[1];
					else if (keyAndValue[0].equals("userPwd")) {
						userPwd = SharedPrefsConfig.getSharedPrefsInstance(
								Cmaaio.getInstance()).getPassWord();
						// String xpwd=CMATool.decode(keyAndValue[1]);
						// userPwd = xpwd;
					} else if (keyAndValue[0].equals("userToken"))
						userToken = keyAndValue[1];
					else if (keyAndValue[0].equals("language"))
						language = keyAndValue[1];
					else if (keyAndValue[0].equals("curAppId"))
						curAppId = keyAndValue[1];
				}
				in.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void clearUserInfo() {
		String data = "userName=&userPwd=&userToken=";
		File dataPath = new File(dataFilePath);
		if (!dataPath.exists())
			dataPath.mkdirs();
		File file = new File(dataFilePath + dataFileName);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// 写入数据
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(data.getBytes());
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setOfflineLogin(boolean isOffline) {
		isOfflineLogin = isOffline;
	}

	public static boolean isOfflineLogin() {
		return isOfflineLogin;
	}
}
