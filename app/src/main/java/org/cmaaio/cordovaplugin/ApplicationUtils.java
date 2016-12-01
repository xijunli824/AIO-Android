package org.cmaaio.cordovaplugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.cmaaio.activity.Cmaaio;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class ApplicationUtils extends CordovaPlugin {
	public ApplicationUtils() {

	}

	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		Log.d("whz", "action=" + action);
		if (action.equals("exit")) {
			if (this.cordova != null)
				Log.e("plugin", "" + args);

			// String[] param = {"3", "-1", "itellId or itrackId","通知"};
			this.cordova.onMessage("exit", args);
		} else if (action.equals("SMS")) {
			Log.d("sms", args.toString());
			JSONArray perArr = (JSONArray) args.get(0);
			String msg = args.get(1).toString();
			// SmsManager sms = SmsManager.getDefault();
			// PendingIntent pi = null;
			// for (int i = 0; i < perArr.length(); i++) {
			// pi = PendingIntent.getBroadcast(Cmaaio.getInstance(), 0, new
			// Intent(), 0);
			// sms.sendTextMessage(perArr.getString(i), null, msg, pi, null);
			// }
			StringBuilder sb = new StringBuilder("smsto:");
			for (int i = 0; i < perArr.length(); i++) {
				sb.append(perArr.get(i));
				if (i < perArr.length() - 1) {
					sb.append(";");
				}
			}
			Uri smsToUri = Uri.parse(sb.toString());
			Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.putExtra("sms_body", msg);
			Cmaaio.getInstance().startActivity(mIntent);
			callbackContext.success();
		} else if (action.equals("hideImm")) {
			InputMethodManager imm = (InputMethodManager) Cmaaio.getInstance()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(this.webView.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			callbackContext.success();
		}
		return true;
	}
}
