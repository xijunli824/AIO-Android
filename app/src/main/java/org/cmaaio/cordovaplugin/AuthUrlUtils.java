package org.cmaaio.cordovaplugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.cmaaio.activity.Cmaaio;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.mine.fun.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class AuthUrlUtils extends CordovaPlugin {
	public static final int REQUEST_CODE = 0x0ba7c0de;
	private CallbackContext mCallbackContext = null;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		Log.d("whz", "AuthUrlUtils action=" + action);
		if (action.equals("AuthUrl")) {
			Log.e("auth https", "" + args.get(2));
			if (!args.isNull(2)) {
				// String url = args.get(2).toString();
				// url = "https://imoffice.capitaretail.com.cn";
				// HttpUtil util = new HttpUtil();
				// util.context = Cmaaio.getInstance();
				// util.iResult = authListener;
				// util.isShowDialog=false;
				// util.executeOtherUrl(true, url, "");
				callbackContext.success("ok");
			}
		} else if (action.equals("scan")) {
			mCallbackContext = callbackContext;
			Cmaaio.getMainHandler().post(new Runnable() {

				@Override
				public void run() {
//					Intent intentScan = new Intent();
//					intentScan.setClass(
//							AuthUrlUtils.this.cordova.getActivity(),
//							CaptureActivity.class);
//					// intentScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intentScan.addCategory(Intent.CATEGORY_DEFAULT);
//					AuthUrlUtils.this.cordova.startActivityForResult(
//							(CordovaPlugin) AuthUrlUtils.this, intentScan,
//							REQUEST_CODE);
				}
			});
			/*
			 * Intent intentScan = new Intent();
			 * intentScan.setClass(this.cordova.getActivity(),
			 * CaptureActivity.class);
			 * //intentScan.setClass(this.cordova.getActivity(),
			 * CaptureActivity.class);
			 * //intentScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
			 * this.cordova.startActivityForResult((CordovaPlugin)this,
			 * intentScan, REQUEST_CODE);
			 */
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			JSONObject obj = new JSONObject();
			if (resultCode == Activity.RESULT_OK) {
				try {// cancelled text format
					obj.put("cancelled", false);
					obj.put("text", intent.getStringExtra("result"));
					obj.put("format", intent.getStringExtra("format"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mCallbackContext.success(obj);
			} else {
				try {
					obj.put("cancelled", true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mCallbackContext.success(obj);
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("cancelled", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mCallbackContext.success(obj);

		}
	}

}
