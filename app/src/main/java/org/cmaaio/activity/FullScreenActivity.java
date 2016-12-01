package org.cmaaio.activity;

import java.util.ArrayList;

import org.cmaaio.adapter.FullScreenAdapter;
import org.cmaaio.common.AppDataController;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.ui.SlipButton;
import org.cmaaio.ui.SlipButton.OnChangedListener;
import org.cmaaio.util.ActivityManager;

import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class FullScreenActivity extends CMABaseActivity {
	private Button backBtn;
	private Button rightBtn;
	private SlipButton slipBtn;

	private ListView listView;
	private FullScreenAdapter adapter;

	ArrayList<CMAAppEntity> list = new ArrayList<CMAAppEntity>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.full_screen);
		super.initTaBar(2);
		super.initNavBar(this.getResources().getString(R.string.full_screen),
				R.drawable.add_icon_comment);

		initView();
		initListener();
		AppDataController.getInstance().getAppData();

		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		syncAppData();

	}

	private void initView() {
		rightBtn = (Button) findViewById(R.id.rightBtn);
		rightBtn.setVisibility(View.GONE);
		backBtn = (Button) findViewById(R.id.backBtn);
		slipBtn = (SlipButton) findViewById(R.id.full_screen_slip_btn);
		listView = (ListView) findViewById(R.id.full_screen_list_view);
		adapter = new FullScreenAdapter(list, this);
		listView.setAdapter(adapter);

	}

	private void initListener() {
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		final SharedPrefsConfig sp = SharedPrefsConfig
				.getSharedPrefsInstance(this);
		final String tag = sp.getUserName() + SharedPrefsConfig.APPFULLSCREEN;
		boolean isFullScreen = sp.getSharedPreferences().getBoolean(tag, true);
		slipBtn.setCheck(isFullScreen);
		if (isFullScreen) {
			listView.setVisibility(View.VISIBLE);

		} else {
			listView.setVisibility(View.GONE);

		}
		slipBtn.SetOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
				Editor editor = sp.getEditor();
				editor.putBoolean(tag, CheckState);
				editor.commit();
				if (CheckState) {
					listView.setVisibility(View.VISIBLE);

				} else {
					listView.setVisibility(View.GONE);

				}
			}
		});
	}

	private boolean syncAppData() {
		if (!AppDataController.getInstance().isModify) {
			return false;
		}
		list.clear();
		list.addAll(AppDataController.getInstance().getApp());
		return true;
	}
}
