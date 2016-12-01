package org.cmaaio.adapter;

import java.util.ArrayList;

import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.ui.SlipButton;
import org.cmaaio.ui.SlipButton.OnChangedListener;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 消息推送设置适配器
 * 
 * @author hzwua
 * 
 */
public class PropAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CMAAppEntity> appList;
	public static String ALL_READ_BROATCAST = "org.cmaaio.adapter";

	private String curAppId;

	public PropAdapter(Context mContext, ArrayList<CMAAppEntity> list) {
		this.context = mContext;
		appList = list;
	}

	public int getCount() {
		return appList.size();
	}

	public Object getItem(int position) {
		return appList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.set_prop_item, null);
		TextView appName = (TextView) convertView
				.findViewById(R.id.prop_appName);
		RemoteImageView appIcon = (RemoteImageView) convertView
				.findViewById(R.id.prop_appIcon);
		SlipButton btnRead = (SlipButton) convertView
				.findViewById(R.id.btn_is_read);

		final CMAAppEntity appObj = appList.get(position);

		if (!appObj.appIcon.contains("http")) {
			int icon = context.getResources().getIdentifier(appObj.appIcon,
					"drawable", context.getPackageName());
			appIcon.setImageResource(icon);
		} else {
			appIcon.setImageUrl(appObj.appIcon);
		}

		final SharedPrefsConfig sp = SharedPrefsConfig
				.getSharedPrefsInstance(context);
		final String tag = sp.getUserName() + "_" + appObj.appId
				+ SharedPrefsConfig.APPPUSH;
		boolean isPush = sp.getSharedPreferences().getBoolean(tag, true);
		btnRead.setCheck(isPush);
		btnRead.SetOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
				Editor editor = sp.getEditor();
				String strPost = "&AppId=" + appObj.appId + "&ADAcount="
						+ sp.getUserName() + "&OS=Android" + "&Token="
						+ "&OSVer=4" + "&channelID="
						+ sp.getBaiduPushChannelId();
				Log.e("TAG", strPost);
				editor.putBoolean(tag, CheckState); // 打开
				editor.commit();
				if (CheckState) {
					strPost += "&Type=4";
				} else {
					strPost += "&Type=3";
				}
				push(strPost);
			}
		});

		Button allRead = (Button) convertView.findViewById(R.id.all_read);
		allRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				curAppId = appObj.appId;
				String strPost = "&AppId=" + appObj.appId + "&ADAcount="
						+ sp.getUserName() + "&Token=" + " " + "&OS=Android"
						+ "&OSVer=4" + "&BadgeNum=0";
				System.out.println(strPost);
				pushAllRead(strPost);
			}
		});
		// 任务没有"全部已读"
		if ("MIOfficeMission".equals(appObj.appId)) {
			allRead.setVisibility(View.GONE);
			appName.setText(Cmaaio.getInstance().getResources()
					.getString(R.string.task));
		} else if ("MIOfficeMessage".equals(appObj.appId)) {
			appName.setText(Cmaaio.getInstance().getResources()
					.getString(R.string.noti));
		} else {
			// appName.setText(appObj.appName);
			if (appObj.AppAliases.equals("")) {
				appName.setText(appObj.appName);
			} else {
				appName.setText(appObj.AppAliases);
			}
		}
		return convertView;
	}

	private void push(String strPost) {
		HttpUtil con = new HttpUtil();
		con.iResult = onManageAppPushResult;
		con.context = context;
		con.executeTask(true, "UserManageAppPush", strPost);
	}

	private IResult onManageAppPushResult = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				// JSONArray array = new JSONArray(jsonStr);
				// JSONObject obj = array.getJSONObject(0);
				// String result = obj.getString("result");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void OnCacnel() {

		}
	};

	private void pushAllRead(String strPost) {
		HttpUtil con = new HttpUtil();
		con.iResult = onPushAllReadResult;
		con.context = context;
		con.executeTask(true, "UserMinusBadge", strPost);
	}

	private IResult onPushAllReadResult = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				JSONArray array = new JSONArray(jsonStr);
				JSONObject obj = array.getJSONObject(0);
				String result = obj.getString("result");
				Log.e("onPushAllReadResult=====>", result);
				if ("True".equalsIgnoreCase(obj.getString("result"))) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("appID", curAppId);
					intent.putExtras(bundle);
					intent.setAction(ALL_READ_BROATCAST);
					context.sendBroadcast(intent);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void OnCacnel() {

		}
	};
}
