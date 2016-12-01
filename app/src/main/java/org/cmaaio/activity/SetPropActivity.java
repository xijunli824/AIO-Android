package org.cmaaio.activity;

import java.util.ArrayList;

import org.cmaaio.adapter.PropAdapter;
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
import android.widget.LinearLayout;
import android.widget.ListView;

/***
 * 推送
 * 
 * @author Administrator
 * 
 */

public class SetPropActivity extends CMABaseActivity {
	private Button back; // add by shrimp at 20130625
	private Button rightButton;
	private ListView propList;

	private SlipButton pushButton;
	private LinearLayout layoutList;

	ArrayList<CMAAppEntity> allAppList = new ArrayList<CMAAppEntity>(); // 所有app数据，一维

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.set_prop);
		super.initTaBar(2);// super.initTaBar();表示该activity不属于任何一组
		super.initNavBar(this.getResources().getString(R.string.pushCenter),
				R.drawable.add_icon_comment);

		initView();

		AppDataController.getInstance().getAppData();

		propList = (ListView) this.findViewById(R.id.propList);
		PropAdapter propAdatpter = new PropAdapter(this, allAppList);
		propList.setAdapter(propAdatpter);

		// 退出Activity
		ActivityManager.getInstance().addActivity(this);
	}

	private void initView() {
		rightButton = (Button) findViewById(R.id.rightBtn);
		rightButton.setVisibility(View.GONE);

		back = (Button) findViewById(R.id.backBtn);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		layoutList = (LinearLayout) findViewById(R.id.layout_list);
		pushButton = (SlipButton) findViewById(R.id.slip_btn_push_messge);
		final SharedPrefsConfig sp = SharedPrefsConfig
				.getSharedPrefsInstance(SetPropActivity.this);
		final String key = sp.getUserName() + SharedPrefsConfig.APPPUSH;
		boolean isPush = sp.getSharedPreferences().getBoolean(key, true);
		pushButton.setCheck(isPush);
		if (isPush) {
			layoutList.setVisibility(View.VISIBLE);
		} else {
			layoutList.setVisibility(View.INVISIBLE);
		}
		pushButton.SetOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
				Editor editor = sp.getEditor();
				editor.putBoolean(key, CheckState);
				editor.commit();
				if (CheckState) {
					layoutList.setVisibility(View.VISIBLE);

				} else {
					layoutList.setVisibility(View.INVISIBLE);

				}
			}

		});
		//pushButton.setVisibility(View.GONE);
	}

	private boolean syncAppData() {
		if (!AppDataController.getInstance().isModify) {
			return false;
		}
		allAppList.clear();
		allAppList.addAll(AppDataController.getInstance().getApp());
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		syncAppData();
	}

}
