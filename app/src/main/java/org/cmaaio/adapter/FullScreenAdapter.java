package org.cmaaio.adapter;

import java.util.ArrayList;

import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.ui.SlipButton;
import org.cmaaio.ui.SlipButton.OnChangedListener;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.db.SharedPrefsConfig;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FullScreenAdapter extends BaseAdapter {

	private ArrayList<CMAAppEntity> list;
	private LayoutInflater inflater;
	private Context context;

	public FullScreenAdapter(ArrayList<CMAAppEntity> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_full_screen, null);
			holder.appName = (TextView) convertView
					.findViewById(R.id.item_full_screen_app_name);
			holder.appIcon = (RemoteImageView) convertView
					.findViewById(R.id.item_full_screen_app_icon);
			holder.slipButton = (SlipButton) convertView
					.findViewById(R.id.item_full_screen_slip_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CMAAppEntity cae = list.get(position);
		
		if ("MIOfficeMission".equals(cae.appId)) {
			holder.appName.setText(Cmaaio.getInstance().getResources().getString(R.string.task));
		} else if ("MIOfficeMessage".equals(cae.appId)) {
			holder.appName.setText(Cmaaio.getInstance().getResources().getString(R.string.noti));
		} else {
//			holder.appName.setText(cae.appName);
			if (cae.AppAliases.equals("")) {
				holder.appName.setText(cae.appName);
			} else {
				holder.appName.setText(cae.AppAliases);
			}
			
		}
		if (!cae.appIcon.contains("http")) {
			int icon = context.getResources().getIdentifier(cae.appIcon,
					"drawable", context.getPackageName());
			holder.appIcon.setImageResource(icon);
		} else {
			holder.appIcon.setImageUrl(cae.appIcon);
		}
		final SharedPrefsConfig sp = SharedPrefsConfig
				.getSharedPrefsInstance(context);
		final String tag = sp.getUserName() + "_" + cae.appId
				+ SharedPrefsConfig.APPFULLSCREEN;
		boolean isFullScreen = sp.getSharedPreferences().getBoolean(tag, true);
		if (isFullScreen) {
			holder.slipButton.setCheck(true);
		} else {
			holder.slipButton.setCheck(false);
		}
		holder.slipButton.SetOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(boolean CheckState) {
				Editor editor = sp.getEditor();

				editor.putBoolean(tag, CheckState);

				editor.commit();
			}
		});

		return convertView;
	}

	class ViewHolder {
		RemoteImageView appIcon;
		TextView appName;
		SlipButton slipButton;

	}
}
