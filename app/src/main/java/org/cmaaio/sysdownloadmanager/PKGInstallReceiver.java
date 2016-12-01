package org.cmaaio.sysdownloadmanager;

import org.cmaaio.activity.HomeActivity;
import org.cmaaio.activity.R;
import org.cmaaio.common.AppDataController;
import org.cmaaio.common.DownLoadFile;
import org.cmaaio.common.FileOp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class PKGInstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			String pkg = intent.getDataString();
			if (CMAAPPInatallerManager.currentNativeApp != null) {
				String[] paras = pkg.split(":");
				CMAAPPInatallerManager.currentNativeApp.appPath = paras[1];
				AppDataController.getInstance().add(CMAAPPInatallerManager.currentNativeApp);
				CMAAPPInatallerManager.uploadDownloadRecord(CMAAPPInatallerManager.currentNativeApp);// 上传下载记录
				CMAAPPInatallerManager.userManageAppPush(CMAAPPInatallerManager.currentNativeApp, 1);// 消息推送
				Handler mHandler = new Handler(Looper.getMainLooper());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (CMAAPPInatallerManager.currentAdapter != null) {
							CMAAPPInatallerManager.currentAdapter.notifyDataSetChanged();
							CMAAPPInatallerManager.currentNativeApp = null;
							CMAAPPInatallerManager.currentAdapter = null;
						}
						if (CMAAPPInatallerManager.currentBtn != null) {
							CMAAPPInatallerManager.currentBtn.setText(context.getResources().getString(R.string.app_have_install));
							CMAAPPInatallerManager.currentBtn.setOnClickListener(null);
						}
						if (CMAAPPInatallerManager.currentNativeSDPath != null) {
							FileOp.getInstance().delFile(CMAAPPInatallerManager.currentNativeSDPath);
							CMAAPPInatallerManager.currentNativeSDPath = null;
						}
					}
				});

				Intent forwardIntent = new Intent(context, HomeActivity.class);
				context.startActivity(forwardIntent);
			}

		}
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			String pkg = intent.getDataString();
		}
	}

}
