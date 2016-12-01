package org.cmaaio.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;
import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.entity.CMAAppEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class AppDataController {
	private static AppDataController instance = new AppDataController();
	private ArrayList<CMAAppEntity> allAppList = new ArrayList<CMAAppEntity>();
	public boolean isModify = false;

	public static AppDataController getInstance() {
		return instance;
	}

	public void add(CMAAppEntity app) {
		allAppList.add(app);
		isModify = true;

		if (app.appName.toLowerCase().contains("itell")) { // itell和itrack名称，在后台固化
			Constants.itellID = app.appId;
		}
		if (app.appName.toLowerCase().contains("itrack")) {
			Constants.itrackID = app.appId;
		}
		if (app.appName.toLowerCase().contains("iem")) {//add iem
			Constants.iemID = app.appId;
		}
		saveAppData();
	}

	public void remove(CMAAppEntity app) {
		for (CMAAppEntity tmp : allAppList) {
			if (tmp.appId.equals(app.appId)) {
				if (app.appName.toLowerCase().contains("itell")) { // itell和itrack名称，在后台固化
					Constants.itellID = "noItell";
				}
				if (app.appName.toLowerCase().contains("itrack")) {
					Constants.itrackID = "noItrack";
				}
				if (app.appName.toLowerCase().contains("iem")) {//add iem
					Constants.iemID = "noIEM";
				}
				allAppList.remove(tmp);
				isModify = true;
				break;
			}
		}
		saveAppData();
		getAppData();
	}

	public void clear() {
		allAppList.clear();
	}

	public boolean contain(CMAAppEntity app) {
		boolean result = false;
		for (CMAAppEntity tmp : allAppList) {
			if (tmp.appId.equals(app.appId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public ArrayList<CMAAppEntity> getApp() {
		isModify = false;// 取完数据后将修改标志位复位，下一次数据变化会修改
		return allAppList;
	}

	// 初始化默认app,通知和任务
	private void initDefaultApp() {
		AppDataController.getInstance().clear();
		ArrayList<String> appname = new ArrayList<String>();
		appname.add(Cmaaio.getInstance().getResources().getString(R.string.task));
		appname.add(Cmaaio.getInstance().getResources().getString(R.string.noti));
		String[] icon = new String[] { "icon_rw", "icon_tz" };
		String[] path = new String[] { "file:///android_asset/www/Task/index.html", "file:///android_asset/www/Notice/index.html" };
		String[] appId = new String[] { "MIOfficeMission", "MIOfficeMessage" };
		for (int i = 0; i < appname.size(); i++) {
			CMAAppEntity tmpApp = new CMAAppEntity();
			// tmpApp.appId = i < appname.size() ? appname.get(i) : "App" + i;
			tmpApp.appId = appId[i];
			tmpApp.appName = i < appname.size() ? appname.get(i) : "App" + i;
			tmpApp.appIcon = "" + (i < icon.length ? icon[i] : "");
			tmpApp.appPath = path[i];
			tmpApp.appType = CMAAppEntity.HYBRIDAPP + CMAAppEntity.ROMAPP;
			AppDataController.getInstance().add(tmpApp);
		}
	}

	// 数据写入磁盘
	public void saveAppData() {
		try {
			JSONObject root = new JSONObject();
			JSONArray appList = new JSONArray();
			for (CMAAppEntity entity : allAppList) {
				JSONObject app = new JSONObject();
				app.put("appId", entity.appId);
				app.put("appName", entity.appName);
				app.put("appSum", entity.appSum);
				app.put("appIcon", entity.appIcon);
				app.put("appPath", entity.appPath);
				app.put("appType", entity.appType);
				app.put("inPage", entity.inPage);
				app.put("seatId", entity.seatId);
				// add
				app.put("appVersion", entity.AppVersion);
				// end
				//add 2014-01-03
				app.put("AppAliases", entity.AppAliases);
				appList.put(app);
			}
			root.put("list", appList);
			String data = root.toString();
			File path = new File(Constants.rootPath);
			File file = new File(Constants.rootPath + "/" + Constants.userName + "/" + "apps.plist");
			if (!path.exists())
				path.mkdirs();
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(data.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getString(JSONObject obj, String key) {
		String result = "";
		try {
			if (obj.has(key))
				return obj.getString(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 从磁盘中读取数据
	public void getAppData() {
		try {
			isModify = true;
			allAppList.clear();
			File file = new File(Constants.rootPath + "/" + Constants.userName + "/" + "apps.plist");
			if (file.exists()) {
				FileInputStream in = new FileInputStream(file);
				int length = (int) file.length();
				byte[] temp = new byte[length];
				in.read(temp, 0, length);
				String data = EncodingUtils.getString(temp, "UTF-8");
				JSONObject root = new JSONObject(data);
				JSONArray appList = root.getJSONArray("list");
				for (int i = 0; i < appList.length(); i++) {
					JSONObject appObj = appList.getJSONObject(i);
					CMAAppEntity entity = new CMAAppEntity();
					entity.appId = getString(appObj, "appId");
					entity.appName = getString(appObj, "appName");
					// add begin
					// if ("itell".equals(entity.appName)) { //
					// itell和itrack名称，在后台固化
					// Constants.itellID = entity.appId;
					// }
					// if ("itrack".equals(entity.appName)) {
					// Constants.itrackID = entity.appId;
					// }
					// add end
					// add begin
					if (entity.appName.toLowerCase().contains("itell")) { // itell和itrack名称，在后台固化
						Constants.itellID = entity.appId;
					}
					if (entity.appName.toLowerCase().contains("itrack")) {
						Constants.itrackID = entity.appId;
					}
					if (entity.appName.toLowerCase().contains("iem")) {//add iem
						Constants.iemID = entity.appId;
					}
					entity.AppVersion = getString(appObj, "appVersion");
					// add end
					entity.appSum = getString(appObj, "appSum");
					entity.appIcon = getString(appObj, "appIcon");
					entity.appPath = getString(appObj, "appPath");
					entity.appType = getString(appObj, "appType");
					entity.inPage = Integer.parseInt(getString(appObj, "inPage"));
					entity.seatId = Integer.parseInt(getString(appObj, "seatId"));
					//add
					entity.AppAliases=getString(appObj, "AppAliases");
					allAppList.add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (allAppList.size() == 0)
			initDefaultApp();
	}

	public CMAAppEntity getAppById(String id) {
		CMAAppEntity entity = null;
		// getAppData();
		for (int i = 0; i < allAppList.size(); i++) {
			CMAAppEntity temp = allAppList.get(i);
			if (id.equals(temp.appId)) {
				entity = temp;
				break;
			}
		}
		return entity;
	}
}
