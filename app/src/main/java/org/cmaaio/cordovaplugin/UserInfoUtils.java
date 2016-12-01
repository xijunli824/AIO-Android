package org.cmaaio.cordovaplugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.common.Constants;
import org.cmaaio.db.DataBaseHelper;
import org.cmaaio.db.SharedPrefsConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class UserInfoUtils extends CordovaPlugin {
	private String userName = "";
	private String userPwd = "";
	private String userToken = "";
	private String language = Locale.getDefault().getLanguage();
	private String appID = "noApp";// 没有进入子应用时值为noApp
	private String device = "android";

	// add begin
	private float longitude = 0; // 精度
	private float latitude = 0; // 纬度
	private String itellID = "noItell";
	private String itrackID = "noItrack";
	private String iemID = "noIEM";
	private int width;
	private int height;

	// add end

	public UserInfoUtils() {

	}

	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		Log.d("whz", "UserInfoUtils action=" + action);
		if (action.equals("GetUserInfo")) {
			getUserInfo();
			JSONObject params = new JSONObject();
			params.put("userToken", userToken);
			params.put("userName", userName);
			params.put("userPassword", userPwd);
			params.put("width", width);
			params.put("height", height);

			if (language.equalsIgnoreCase("zh")) {
				language = Cmaaio.getInstance().getString(R.string.zh);
			}
			if (language.equalsIgnoreCase("en")) {
				language = Cmaaio.getInstance().getString(R.string.en);
			}
			params.put("language", language);
			params.put("appID", appID);
			params.put("device", device);
			// add begin
			params.put("longitude", longitude);
			params.put("latitude", latitude);
			params.put("itellID", itellID);
			params.put("itrackID", itrackID);
			// add end

			// 推送详情
			if (Constants.hasDetail) {
				params.put("pushFlag", 1);// 要进入详情的标识标识
			} else {
				params.put("pushFlag", 0);// 不要进入详情的标识
			}

			// modify
			params.put("IEMID", iemID);
			String temp = Constants.forwardValue;
			params.put("CMAAIO", temp);
			// Constants.forwardValue = "";// 用完清除
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));
		} else if (action.equals("WriteItrackCacheInfo")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(args.get(0).toString(), args.get(1).toString());
			db.writeItrackCacheByUser(sp.getUserName(), map);
			callbackContext.success();
		} else if (action.equals("GetItrackCacheInfo")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = db.queryItrackCacheByUser(
					sp.getUserName(), args.get(0).toString());

			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			if (array.length() == 0) {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, ""));
			} else {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, array.get(0).toString()));
			}
		} else if (action.equals("GetItellDraftInfo")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = db.queryItellByUser(sp.getUserName());
			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			JSONObject params = new JSONObject();
			params.put("itell", array);
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));
		} else if (action.equals("GetItrackDraftInfo")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = db
					.queryItrackByUser(sp.getUserName());
			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			JSONObject params = new JSONObject();
			params.put("itrack", array);
			// File file=new File(Constants.rootPath+"temp.txt");
			// try {
			// FileOutputStream fos=new FileOutputStream(file);
			// fos.write(params.toString().getBytes(),
			// 0,params.toString().getBytes().length );
			// fos.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));
		} else if (action.equals("WriteItellDrafts")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(args.get(0).toString(), args.get(1).toString());
			db.writeItellDraftsByUser(sp.getUserName(), map);
			callbackContext.success();
		} else if (action.equals("WriteItrackDrafts")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(args.get(0).toString(), args.get(1).toString());
			db.writeItrackDraftsByUser(sp.getUserName(), map);
			callbackContext.success();
		} else if (action.equals("DeleteItellDrafts")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			db.deleteItellDrafts(sp.getUserName(), args.get(0).toString());
			callbackContext.success();
		} else if (action.equals("DeleteItrackDrafts")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			db.deleteItrackDrafts(sp.getUserName(), args.get(0).toString());
			// 返回数据
			HashMap<String, String> map = db
					.queryItrackByUser(sp.getUserName());
			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			JSONObject params = new JSONObject();
			params.put("itrack", array);
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));
		} else if (action.equals("GetSettingInfo")) {
			JSONObject params = new JSONObject();
			Log.d("GetSettingInfo", "" + Constants.hasDetail);
			Log.d("GetSettingInfo", "" + Constants.pushType);
			Log.d("GetSettingInfo", "" + Constants.pushCode);
			// 推送详情
			if (Constants.hasDetail) {
				params.put("pushType", Constants.pushType);
				params.put("pushCode", Constants.pushCode);
			} else {
				params.put("pushType", "");
				params.put("pushCode", "");
			}
			params.put("itellID", itellID);
			params.put("itrackID", itrackID);
			params.put("IEMID", iemID);

			Log.d("whz", params + "");
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));
		}// modify .add
		else if (action.equals("GetApplicationCacheInfo")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = db.queryCacheDataByUserAndKey(sp
					.getUserName(), args.get(1).toString(), args.get(0)
					.toString());

			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			if (array.length() == 0) {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, ""));
			} else {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, array.get(0).toString()));
			}
		} else if (action.equals("WriteApplicationCacheInfo")) {
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(args.get(0).toString(), args.get(1).toString());
			db.writeCacheData(sp.getUserName(), args.get(2).toString(), map);
			callbackContext.success();
		} else if (action.equals("GetDraftInfo")) {// 应用名取
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = db.queryDraftDataByUser(
					sp.getUserName(), args.getString(0));
			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			JSONObject params = new JSONObject();
			params.put(args.getString(0), array);
			Log.w("whz", "查询==========" + params);
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));
		} else if (action.equals("WriteDrafts")) {// key value 应用名
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(args.get(0).toString(), args.get(1).toString());
			db.writeDraftData(sp.getUserName(), args.get(2).toString(), map);
			callbackContext.success();
		} else if (action.equals("DeleteDrafts")) {// key 应用名
			DataBaseHelper db = new DataBaseHelper(Cmaaio.getInstance());
			SharedPrefsConfig sp = SharedPrefsConfig
					.getSharedPrefsInstance(Cmaaio.getInstance());
			db.deleteDraftData(sp.getUserName(), args.get(1).toString(), args
					.get(0).toString());
			// 返回数据
			HashMap<String, String> map = db.queryDraftDataByUser(
					sp.getUserName(), args.getString(1));
			Log.i("whz", "map size()=" + map.size());
			JSONArray array = new JSONArray();
			Set<Entry<String, String>> entrys = map.entrySet();
			Iterator<Entry<String, String>> iterators = entrys.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				array.put(entry.getValue());
			}
			JSONObject params = new JSONObject();
			params.put(args.getString(1), array);
			Log.w("whz", "删除数据后查询==========" + params);
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, params));

		} else if (action.equals("openUrl")) {// 打开附件URL
			// 获取可下载的URL
			String downloadUrl = args.get(0).toString();
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri uri = Uri.parse(downloadUrl);
			intent.setData(uri);
			cordova.getActivity().startActivity(intent);
			callbackContext.success();
		}
		return true;
	}

	private void getUserInfo() {
		SharedPrefsConfig sp = SharedPrefsConfig.getSharedPrefsInstance(Cmaaio
				.getInstance());
		userName = sp.getUserName();
		userPwd = sp.getPassWord();
		userToken = sp.getToken();
		longitude = sp.getLongitude();
		latitude = sp.getLatitude();
		width = sp.getScreenWidth();
		height = sp.getScreenHeight();

		if (Constants.curAppId != null && !"".equals(Constants.curAppId)) {
			appID = Constants.curAppId;
		}

		// add begin
		if (Constants.itellID != null && !"".equals(Constants.itellID)) {
			itellID = Constants.itellID;
		}

		if (Constants.itrackID != null && !"".equals(Constants.itrackID)) {
			itrackID = Constants.itrackID;
		}

		if (Constants.iemID != null && !"".equals(Constants.iemID)) {
			iemID = Constants.iemID;
		}
		// add end
		Log.e("plugin", userName);
	}

}
