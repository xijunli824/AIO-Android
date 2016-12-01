package org.cmaaio.view.laucher;

import java.util.ArrayList;
import java.util.Locale;

import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.entity.CMAAppEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CMAAppEntity> appList;

	public DateAdapter(Context mContext, ArrayList<CMAAppEntity> list) {
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

	public void exchange(int startPosition, int endPosition) {
		Object endObject = getItem(endPosition);
		Object startObject = getItem(startPosition);
		appList.add(startPosition, (CMAAppEntity) endObject);
		appList.remove(startPosition + 1);
		appList.add(endPosition, (CMAAppEntity) startObject);
		appList.remove(endPosition + 1);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
		convertView.setTag("yes");// yes表示改app是正常app
		LinearLayout appItem = (LinearLayout) convertView.findViewById(R.id.appItem);
		TextView appName = (TextView) convertView.findViewById(R.id.appName);
		RemoteImageView appIcon = (RemoteImageView) convertView.findViewById(R.id.appIcon);

		TextView badgeNum = (TextView) convertView.findViewById(R.id.badge_num);
		badgeNum.setVisibility(View.GONE);
		CMAAppEntity appObj = appList.get(position);
		badgeNum.setTag(appObj.appId);// 设置tag用于更新角标
		if (appObj == null) {// 添加app
			appName.setText(R.string.jia_hao);
			appName.setBackgroundResource(R.drawable.red);
		} else if (appObj.appId.equals("none")) {// 补位app
			convertView.setTag("null");// null表示该app是补位空app
			appName.setText("");
			appName.setBackgroundDrawable(null);
			appItem.setBackgroundDrawable(null);
		} else {// 正常app

			if ("MIOfficeMission".equals(appObj.appId)) {
				appName.setText(Cmaaio.getInstance().getResources().getString(R.string.task));
			} else if ("MIOfficeMessage".equals(appObj.appId)) {
				appName.setText(Cmaaio.getInstance().getResources().getString(R.string.noti));
			} else {
				// appName.setText(appObj.appName);
				// appName.setText(appObj.AppAliases);
				if (appObj.AppAliases.equals("")) {
					appName.setText(appObj.appName);
				} else {
					appName.setText(appObj.AppAliases);
				}
			}

			if (!appObj.appIcon.contains("http")) {
				int icon = context.getResources().getIdentifier(appObj.appIcon, "drawable", context.getPackageName());
				appIcon.setImageResource(icon);
			} else {
				appIcon.setImageUrl(appObj.appIcon);
			}

			if (appObj.badgeNum > 0) {
				badgeNum.setVisibility(View.VISIBLE);
				badgeNum.setText(appObj.badgeNum + "");
			}
		}
		return convertView;
	}
}
