package org.cmaaio.adapter;

import java.util.ArrayList;

import org.cmaaio.activity.R;
import org.cmaaio.common.AppDataController;
import org.cmaaio.common.DownLoadFile;
import org.cmaaio.common.DownLoadManager;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.common.XProcessBar;
import org.cmaaio.entity.CMAAppEntity;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class AppItemAdapter extends BaseAdapter implements OnClickListener {
	private Context context = null;
	private LayoutInflater layoutInflater = null;
	public ArrayList<CMAAppEntity> dataSource = null;
	private OnClickListener onItemBtnClick = null;
	private DownLoadFile downLoadFile = null;

	public AppItemAdapter(Context context, ArrayList<CMAAppEntity> data) {
		this.context = context;
		this.dataSource = data;
		layoutInflater = LayoutInflater.from(context);
		downLoadFile = new DownLoadFile();
	}

	public void setOnItemBtnClick(OnClickListener listener) {
		onItemBtnClick = listener;
	}

	@Override
	public int getCount() {
		return dataSource.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = layoutInflater.inflate(R.layout.item_app, null);
		CMAAppEntity app = dataSource.get(position);
		RemoteImageView appLogo = (RemoteImageView) convertView.findViewById(R.id.reco_img);
		TextView appTitle = (TextView) convertView.findViewById(R.id.app_title);
		TextView appInfo = (TextView) convertView.findViewById(R.id.app_info);
		TextView appSize = (TextView) convertView.findViewById(R.id.app_size);
		RatingBar appStar = (RatingBar) convertView.findViewById(R.id.waitLevel);
		Button button = (Button) convertView.findViewById(R.id.app_but);
		XProcessBar downPrcessBar = (XProcessBar) convertView.findViewById(R.id.downProcessBar);
		appLogo.setImageUrl(app.appIcon);
		// if (app.AppAliases.equals("")) {
		// appTitle.setText(app.appName);
		// } else {
		appTitle.setText(app.AppAliases);
		// }

		appInfo.setText(context.getResources().getText(R.string.detail_app_version) + app.AppVersion);

		appSize.setText(app.AppSize);
		appStar.setRating(Float.parseFloat(app.AppRate));
		downPrcessBar.setDownUrl(app.appDownUrl);
		// Log.e("url", app.appDownUrl);
		// Log.e("type", app.appType);
		Log.d("name", app.appName);
		Log.d("id", app.appId);
		if (button != null) {
			button.setOnClickListener(this);
			button.setTag(position + "");
			if (AppDataController.getInstance().contain(app)) {
				CMAAppEntity oldApp = AppDataController.getInstance().getAppById(app.appId);
				// Log.e("oldAppVer======>", oldApp.AppVersion + " old");
				// Log.e("newAppVer======>", app.AppVersion + " new");
				// String newVer = app.AppVersion;
				// oldApp.AppVersion = "1.1";

				if (app.AppVersion.compareTo(oldApp.AppVersion) > 0) {
					float loadedPercent = DownLoadManager.getInstance().getDownProcess(app.appDownUrl);
					Log.e("更新已下载文件大小:", "" + loadedPercent);
					if (loadedPercent == 0) {
						button.setText(this.context.getResources().getString(R.string.this_app_update));
						button.setEnabled(true);
						button.setTextColor(Color.BLACK);
						downPrcessBar.setVisibility(View.GONE);
					} else {
						button.setText(this.context.getResources().getString(R.string.app_but_mes_goon));// 继续下载
						button.setEnabled(true);
						button.setTextColor(Color.BLACK);
						downPrcessBar.setVisibility(View.VISIBLE);
						downPrcessBar.setProgress(loadedPercent);
					}
				} else {
					button.setText(this.context.getResources().getString(R.string.app_but_mes_have));// 已添加
					button.setEnabled(false);
					button.setTextColor(Color.GRAY);
					downPrcessBar.setVisibility(View.GONE);
				}
			} else {
				float loadedPercent = DownLoadManager.getInstance().getDownProcess(app.appDownUrl);
				Log.e("已下载文件大小:", "" + loadedPercent);
				if (loadedPercent == 0) {
					button.setText(this.context.getResources().getString(R.string.app_but_mes_add));// 添加
					button.setEnabled(true);
					button.setTextColor(Color.BLACK);
					downPrcessBar.setVisibility(View.GONE);
				} else {
					button.setText(this.context.getResources().getString(R.string.app_but_mes_goon));// 继续下载
					button.setEnabled(true);
					button.setTextColor(Color.BLACK);
					downPrcessBar.setVisibility(View.VISIBLE);
					downPrcessBar.setProgress(loadedPercent);
				}

			}
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		if (onItemBtnClick != null) {
			onItemBtnClick.onClick(v);
		}
	}

}
