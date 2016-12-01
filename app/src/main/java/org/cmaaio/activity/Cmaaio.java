package org.cmaaio.activity;

import com.baidu.frontia.FrontiaApplication;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

public class Cmaaio extends FrontiaApplication {

	public static Cmaaio instance = null;

	private static Handler mHandler = null;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mHandler = new Handler(Looper.getMainLooper());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}
	}

	public static Cmaaio getInstance() {
		return instance;
	}

	public static Handler getMainHandler() {
		return mHandler;
	}
}
