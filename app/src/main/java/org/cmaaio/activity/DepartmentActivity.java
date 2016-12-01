package org.cmaaio.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cmaaio.adapter.ContactChildAdapter;
import org.cmaaio.adapter.SearchAdapter;
import org.cmaaio.db.DataHelper;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CommonUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author shrimp for contact activity at 20130622
 * 
 */
public class DepartmentActivity extends CMABaseActivity implements
		OnClickListener, TextWatcher {

	// 定义字符串常量，作为group和child视图中TextView的标记
	// private static final String GROUP_TEXT = "group_text";
	// private static final String CHILD_TEXT1 = "child_text1";
	// private static final String CHILD_TEXT2 = "child_text2";

	List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

	// private List<List<Map<String, String>>> childUserId = new
	// ArrayList<List<Map<String, String>>>();

	// ContactListAdapter myExpandableListAdapter;
	ExpandableListView myExpandableListView;
	ContactChildAdapter childListAdapter;
	ExpandableListView childListView;
	private LinearLayout layout_detail, expandableListViewLayout;
	private TextView tittle;

	private Button backib;// add by shrimp at 20130624

	/**
	 * 查询布局
	 */
	private RelativeLayout searchLayout;
	private EditText searchET;
	private ImageButton searchClear;
	private ListView mListView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity);
		myExpandableListView = (ExpandableListView) findViewById(R.id.exlist);
		childListView = (ExpandableListView) findViewById(R.id.child_list);
		// add by shrimp at 20130624 for backbutton
		backib = (Button) findViewById(R.id.backib);
		backib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		super.initTaBar(1);

		layout_detail = (LinearLayout) findViewById(R.id.layout_detail);
		layout_detail.setVisibility(View.GONE);
		tittle = (TextView) findViewById(R.id.contact_title);
		expandableListViewLayout = (LinearLayout) findViewById(R.id.layout_center);

		String comID = getIntent().getStringExtra("comID");
		String corpName = getIntent().getStringExtra("corpName");
		tittle.setText(corpName);

		groupData.clear();
		childData.clear();
		DataHelper dataHelper = new DataHelper(DepartmentActivity.this);
		dataHelper.openDatabase();
		groupData = dataHelper.queryDeptAndEmpInfo(childData, comID);
		dataHelper.closeDatabase();
		handler.sendEmptyMessage(1);

		searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
		searchET = (EditText) findViewById(R.id.contact_search_ET);
		searchClear = (ImageButton) findViewById(R.id.search_clear);
		mListView = (ListView) findViewById(R.id.search_LsitView);
		searchET.addTextChangedListener(this);
		searchClear.setOnClickListener(this);
		searchLayout.setVisibility(View.GONE);

		ActivityManager.getInstance().addActivity(this);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				childList();
				break;
			case 2:
				break;
			}
		}
	};

	private void childList() {

		myExpandableListView.setVisibility(View.GONE);
		childListView.setVisibility(View.VISIBLE);
		childListAdapter = new ContactChildAdapter(this, childData, groupData);
		childListView.setAdapter(childListAdapter);
		childListView.setGroupIndicator(null);

		childListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				String userId = childData.get(groupPosition).get(childPosition)
						.get("EmployID");

				// add by shrimp at 20130622
				Intent intent = new Intent();
				intent.setClass(DepartmentActivity.this, ContactsActivity.class);
				intent.putExtra("isDetail", true);
				intent.putExtra("UserId", userId);
				startActivity(intent);

				return false;
			}
		});

		childListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});

		childListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					CommonUtil.hideSoftInput(DepartmentActivity.this,
							DepartmentActivity.this);
					break;
				}

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});

		searchET.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1,
					KeyEvent event) {
				if (event == null || event.getAction() == KeyEvent.ACTION_DOWN) {
					CommonUtil.hideSoftInput(DepartmentActivity.this,
							DepartmentActivity.this);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_clear:
			searchET.getText().clear();
			searchClear.setVisibility(View.GONE);
			expandableListViewLayout.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 输入框监听器
	 */
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		if (!"".equals(arg0.toString().trim())
				&& arg0.toString().trim() != null) {
			searchClear.setVisibility(View.VISIBLE);

			List<Map<String, String>> mList = new ArrayList<Map<String, String>>();

			Log.d("whz", "groupData=" + groupData.size());
			Log.d("whz", "childData=" + childData.size());

			for (int i = 0; i < groupData.size(); i++) {
				for (int j = 0; j < childData.get(i).size(); j++) {
					if (childData.get(i).get(j).get("child_text1").toString()
							.replace(" ", "").toLowerCase().trim()
							.contains(arg0.toString().replace(" ", "").trim())) {
						Log.d("whz", "childData="
								+ childData.get(i).get(j).toString());
						mList.add(childData.get(i).get(j));
					}
				}
				searchAdapter(mList);
			}
		} else {
			searchClear.setVisibility(View.GONE);
		}
	}

	private void searchAdapter(final List<Map<String, String>> child) {
		SearchAdapter adapter = new SearchAdapter(DepartmentActivity.this,
				child);
		mListView.setAdapter(adapter);
		expandableListViewLayout.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String userId = child.get(arg2).get("EmployID");
				Intent intent = new Intent();
				intent.setClass(DepartmentActivity.this, ContactsActivity.class);
				intent.putExtra("isDetail", true);
				intent.putExtra("UserId", userId);
				startActivity(intent);
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					CommonUtil.hideSoftInput(DepartmentActivity.this,
							DepartmentActivity.this);
					break;
				}

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}
}
