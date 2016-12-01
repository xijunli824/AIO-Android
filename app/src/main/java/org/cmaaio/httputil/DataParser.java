package org.cmaaio.httputil;

import java.util.ArrayList;
import java.util.Iterator;

import org.cmaaio.common.Base64;
import org.cmaaio.entity.AppListEntity;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.entity.CommentEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataParser {
	private static JSONArray getJsonArray(JSONObject obj, String key) {
		try {
			if (obj.isNull(key))
				return new JSONArray();
			else
				return obj.getJSONArray(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new JSONArray();
		}
	}

	public static int getInt(JSONObject obj, String key) {
		int result = 0;
		if (!obj.isNull(key))
			try {
				result = obj.getInt(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
		return result;
	}

	public static String getString(JSONObject obj, String key) {
		String result = "";
		if (!obj.isNull(key))
			try {
				result = obj.getString(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
		return result;
	}

	public static ArrayList<AppListEntity> parseAppList(String data) {
		ArrayList<AppListEntity> dataList = new ArrayList<AppListEntity>();
		try {
			JSONArray array = new JSONArray(data);
			JSONObject obj = array.getJSONObject(0);
			String success = DataParser.getString(obj, "result");
			if (success.equalsIgnoreCase("True")) {
				Iterator<String> it = obj.keys();
				while (it.hasNext()) {
					String key = it.next();
					if (!key.equals("result")) {
						JSONArray appList = obj.getJSONArray(key);
						AppListEntity listObj = new AppListEntity();
						listObj.appCategory = key;
						for (int i = 0; i < appList.length(); i++) {
							JSONObject appObj = appList.getJSONObject(i);
							CMAAppEntity app = new CMAAppEntity();
							app.appId = DataParser.getString(appObj, "AppId");
							app.appName = DataParser.getString(appObj, "AppName");
							app.appIcon = DataParser.getString(appObj, "AppImgUrl");
							app.DevType = DataParser.getString(appObj, "DevType");
							app.appDownUrl = DataParser.getString(appObj, "AppPackUrl");
							String type = DataParser.getString(appObj, "AppType");
							if (type.equals("1"))
								app.appType = CMAAppEntity.WEBAPP;
							else if (type.equals("2"))
								app.appType = CMAAppEntity.HYBRIDAPP;
							else if (type.equals("3"))
								app.appType = CMAAppEntity.NATIVEAPP;
							app.AppSize = DataParser.getString(appObj, "AppDesc");
							app.AppSize = DataParser.getString(appObj, "AppSize");
							app.AppVersion = DataParser.getString(appObj, "AppVersion");
							app.AppRequest = DataParser.getString(appObj, "AppRequest");
							app.AppDevelopers = DataParser.getString(appObj, "AppDevelopers");
							app.AppDevWebSite = DataParser.getString(appObj, "AppDevWebSite");
							app.AppDevEmail = DataParser.getString(appObj, "AppDevEmail");
							app.KeyWords = DataParser.getString(appObj, "KeyWords");
							app.AppRate = DataParser.getString(appObj, "AppRate");
							//add
							app.AppAliases= DataParser.getString(appObj, "AppAliases");
							
							listObj.appList.add(app);
						}
						dataList.add(listObj);
					}
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dataList = null;
			return null;
		}
		return dataList;
	}

	/**
	 * 获取评论详情
	 * 
	 * @param data
	 * @return
	 */
	public static ArrayList<CommentEntity> parseCommentDetailsList(String data) {
		ArrayList<CommentEntity> dataList = new ArrayList<CommentEntity>();
		try {
			JSONArray array = new JSONArray(data);
			JSONObject obj = array.getJSONObject(0);
			// System.out.println(obj.toString());

			String success = DataParser.getString(obj, "result");
			if (success.equalsIgnoreCase("True")) {
				Iterator<String> it = obj.keys();
				while (it.hasNext()) {
					String key = it.next();
					if (!key.equals("result")) {
						JSONArray appList = obj.getJSONArray(key);
						AppListEntity listObj = new AppListEntity();
						listObj.appCategory = key;
						for (int i = 0; i < appList.length(); i++) {
							JSONObject appObj = appList.getJSONObject(i);
							CommentEntity commentObj = new CommentEntity();
							commentObj.userName = DataParser.getString(appObj, "ADAccount");
							commentObj.time = DataParser.getString(appObj, "CreateDate");
							String content = "";
							try {
								content = new String(Base64.decode(DataParser.getString(appObj, "AppComment")));
							} catch (Exception e) {
								content = DataParser.getString(appObj, "AppComment");
							}

							commentObj.content = content;
							commentObj.level = DataParser.getInt(appObj, "Rate");
							dataList.add(commentObj);
						}
					}
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Jsons parse error:获取评论详情 !");
			e.printStackTrace();
			// dataList = null;
			// return null;
		}
		return dataList;
	}

	public static ArrayList<CMAAppEntity> parseUserAppList(String data) {
		ArrayList<CMAAppEntity> dataList = new ArrayList<CMAAppEntity>();

		try {
			JSONArray array = new JSONArray(data);
			JSONObject obj = array.getJSONObject(0);

			String success = DataParser.getString(obj, "result");
			if (success.equalsIgnoreCase("True")) {
				Iterator<String> it = obj.keys();
				while (it.hasNext()) {
					String key = it.next();
					if (!key.equals("result")) {
						JSONArray appList = obj.getJSONArray(key);
						for (int i = 0; i < appList.length(); i++) {
							if (appList.isNull(i))
								continue;
							JSONObject appObj = appList.getJSONObject(i);
							CMAAppEntity appEntity = new CMAAppEntity();
							appEntity.appId = DataParser.getString(appObj, "AppID");
							appEntity.badgeNum = DataParser.getInt(appObj, "badgeNum");
							dataList.add(appEntity);
						}
					}
				}
			}
		} catch (JSONException e) {
			System.out.println("Jsons parse error:获取角标信息 !");
			e.printStackTrace();
		}
		return dataList;
	}
}
