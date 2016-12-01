package org.cmaaio.activity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.cmaaio.adapter.ContactListAdapter;
import org.cmaaio.adapter.SearchAdapter;
import org.cmaaio.common.Constants;
import org.cmaaio.common.FileOp;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.common.XButton;
import org.cmaaio.db.DataHelper;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.ssl.EasySSLSocketFactory;
import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CommonUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.Intents.Insert;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ContactsActivity extends CMABaseActivity implements
		OnClickListener, TextWatcher {

	// 定义字符串常量，作为group和child视图中TextView的标记
	private static final String GROUP_TEXT = "group_text";
	private static final String CHILD_TEXT1 = "child_text1";

	private List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
	private Map<String, String> userMap = new HashMap<String, String>();

	private List<Map<String, String>> searchList = new ArrayList<Map<String, String>>();

	private ContactListAdapter myExpandableListAdapter;
	private ExpandableListView myExpandableListView;
	private ExpandableListView childListView;
	private LinearLayout layout_detail;

	private TextView moblie, workMoblie, fixTell, email;

	private LinearLayout layout_center;
	private Button backib, addContacts;// add by shrimp at 20130624
	private XButton btnReflesh = null;
	private TextView tittle;
	private String Eid;

	private String userId;

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
		super.initTaBar(1);

		myExpandableListView = (ExpandableListView) findViewById(R.id.exlist);
		childListView = (ExpandableListView) findViewById(R.id.child_list);
		layout_detail = (LinearLayout) findViewById(R.id.layout_detail);
		layout_center = (LinearLayout) findViewById(R.id.layout_center);

		searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
		searchET = (EditText) findViewById(R.id.contact_search_ET);
		searchClear = (ImageButton) findViewById(R.id.search_clear);
		addContacts = (Button) findViewById(R.id.add_contact_to_native);
		addContacts.setOnClickListener(this);
		searchET.addTextChangedListener(this);
		searchClear.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.search_LsitView);

		initTopButton();

		boolean bl = this.getIntent().getBooleanExtra("isDetail", false);
		if (bl) {
			tittle = (TextView) findViewById(R.id.contact_title);
			tittle.setText(getResources().getString(R.string.contact_info));
			btnReflesh.setVisibility(View.INVISIBLE);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					0);
			params.bottomMargin = 0; // 详细联系人界面置顶
			layout_center.setLayoutParams(params);
			userId = this.getIntent().getStringExtra("UserId");
			getGetUserInfo(userId);
			Eid = userId;
		} else {
			layout_detail.setVisibility(View.GONE);
			backib.setVisibility(View.INVISIBLE);
			getContactList();
			querySearchList();
		}

		ActivityManager.getInstance().addActivity(this);
	}

	private void initTopButton() {
		backib = (Button) findViewById(R.id.backib);
		backib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnReflesh = (XButton) this.findViewById(R.id.btn_reflesh);
		btnReflesh.setVisibility(View.VISIBLE);
		btnReflesh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DataTask task = new DataTask(ContactsActivity.this);
				task.execute(Constants.KServerurl + "GetAllOrgList", "");
			}
		});
	}

	/**
	 * 先查询所有的员工信息，为了增加快速查询更好的体验
	 */
	private void querySearchList() {
		new Thread() {
			public void run() {
				DataHelper dataHelper = new DataHelper(ContactsActivity.this);
				dataHelper.openDatabase();
				searchList = groupData = dataHelper.queryAllEmpInfoByEmpId();
				dataHelper.closeDatabase();
			};
		}.start();

	}

	private void groupList() {
		// 进行列表的初始化
		childListView.setVisibility(View.GONE);
		myExpandableListView.setVisibility(View.VISIBLE);
		layout_detail.setVisibility(View.GONE);
		myExpandableListAdapter = new ContactListAdapter(this, childData,
				groupData);
		myExpandableListView.setAdapter(myExpandableListAdapter);
		myExpandableListView.setGroupIndicator(null);
		// //去掉列表的分割线和GroupIndicator，可试验效果
		// myExpandableListView.setGroupIndicator(null);
		// myExpandableListView.setDivider(null);
		//
		myExpandableListView
				.setOnChildClickListener(new OnChildClickListener() {
					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {

						String corpId = childData.get(groupPosition)
								.get(childPosition).get("CorpId").trim();
						String corpName = childData.get(groupPosition)
								.get(childPosition).get("child_text1").trim();
						// add by shrimp at 20130622
						Intent intent = new Intent();
						intent.setClass(ContactsActivity.this,
								DepartmentActivity.class);
						intent.putExtra("comID", corpId);
						intent.putExtra("corpName", corpName);
						startActivity(intent);

						return false;
					}
				});

		myExpandableListView
				.setOnGroupClickListener(new OnGroupClickListener() {

					@Override
					public boolean onGroupClick(ExpandableListView parent,
							View v, int groupPosition, long id) {
						// TODO Auto-generated method stub
						/*
						 * 一定要注意！！！！！！！！ True if the click was handled（官方文档）
						 * 参考：http
						 * ://developer.android.com/reference/android/widget
						 * /ExpandableListView.OnChildClickListener.html
						 */
						return false;
						// return true;
					}
				});
		myExpandableListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态

					break;
				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					CommonUtil.hideSoftInput(ContactsActivity.this,
							ContactsActivity.this);
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
					CommonUtil.hideSoftInput(ContactsActivity.this,
							ContactsActivity.this);
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 员工手机
	 * 
	 * @return
	 */
	public LinearLayout createPhoneLayout() {

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		// userMap.put("UserMobile", "");
		String[] mobiles = userMap.get("UserMobile").split(";");

		for (int i = 0; i < mobiles.length; i++) {

			final LinearLayout phoneLayout = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) this
							.getResources().getDimension(R.dimen.gd_one_h));
			phoneLayout.setOrientation(LinearLayout.HORIZONTAL);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			phoneLayout.setLayoutParams(layoutParams);
			phoneLayout.setBackgroundResource(R.drawable.list_more_info);
			// phoneLayout.setBackground(this.getResources().getDrawable(R.drawable.list_more_info));

			// txt
			moblie = new TextView(this);
			LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			textviewParams.gravity = Gravity.CENTER_VERTICAL;
			textviewParams.weight = 1;
			textviewParams.leftMargin = 30;

			// txt.setGravity(Gravity.CENTER_HORIZONTAL);
			Boolean isShow = Boolean.parseBoolean(userMap.get(
					"UserMobilePublic").toLowerCase());
			Log.d("tag", "isShow=" + isShow);
			if (isShow) {
				moblie.setText(mobiles[i]);
			} else {
				moblie.setText("");
			}

			moblie.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.contact_icon_call, 0, 0, 0);
			moblie.setCompoundDrawablePadding(30);
			moblie.setTextAppearance(ContactsActivity.this,
					R.style.gd_all_text_lay);

			phoneLayout.addView(moblie, textviewParams);

			final String phone = moblie.getText().toString();
			OnClickListener listen1 = new OnClickListener() {
				public void onClick(View v) {
					if ("".equals(phone) || "无".equals(phone)) {
						return;
					}
					Intent phoneIntent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"
									+ phone));
					ContactsActivity.this.startActivity(phoneIntent);
				}
			};
			phoneLayout.setOnClickListener(listen1);
			layout.addView(phoneLayout);
			// line
			View line = new View(this);
			LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 1);
			line.setBackgroundResource(R.drawable.shape_line);
			// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
			line.setLayoutParams(lineParams);
			layout.addView(line);
		}
		return layout;
	}

	/**
	 * 员工电话
	 * 
	 * @return
	 */
	public LinearLayout createTelLayout() {

		// userMap.put("UserTel", "0768-8667788;0768-8667756");
		String[] mobiles = userMap.get("UserTel").split(";");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0; i < mobiles.length; i++) {

			final LinearLayout phoneLayout = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) this
							.getResources().getDimension(R.dimen.gd_one_h));
			phoneLayout.setOrientation(LinearLayout.HORIZONTAL);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			phoneLayout.setLayoutParams(layoutParams);
			phoneLayout.setBackgroundResource(R.drawable.list_more_info);
			// phoneLayout.setBackground(this.getResources().getDrawable(R.drawable.list_more_info));

			// txt
			fixTell = new TextView(this);
			LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			textviewParams.gravity = Gravity.CENTER_VERTICAL;
			textviewParams.weight = 1;
			textviewParams.leftMargin = 30;
			// txt.setGravity(Gravity.CENTER_HORIZONTAL);
			fixTell.setText(mobiles[i]);

			fixTell.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.contact_icon_call2, 0, 0, 0);
			fixTell.setCompoundDrawablePadding(30);
			fixTell.setTextAppearance(ContactsActivity.this,
					R.style.gd_all_text_lay);
			phoneLayout.addView(fixTell, textviewParams);

			final String phone = mobiles[i];
			OnClickListener listen1 = new OnClickListener() {
				public void onClick(View v) {
					if ("".equals(phone) || "无".equals(phone)) {
						return;
					}
					Intent phoneIntent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"
									+ phone));
					ContactsActivity.this.startActivity(phoneIntent);
				}
			};
			phoneLayout.setOnClickListener(listen1);
			layout.addView(phoneLayout);
			// line
			View line = new View(this);
			LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 1);
			line.setBackgroundResource(R.drawable.shape_line);
			// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
			line.setLayoutParams(lineParams);
			layout.addView(line);
		}
		return layout;
	}

	public LinearLayout createMailLayout() {

		String[] emails = userMap.get("UserEmail").split(";");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0; i < emails.length; i++) {

			final LinearLayout phoneLayout = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) this
							.getResources().getDimension(R.dimen.gd_one_h));
			phoneLayout.setOrientation(LinearLayout.HORIZONTAL);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			phoneLayout.setLayoutParams(layoutParams);
			phoneLayout.setBackgroundResource(R.drawable.list_more_info);
			// phoneLayout.setBackground(this.getResources().getDrawable(R.drawable.list_more_info));

			// txt
			email = new TextView(this);
			LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			textviewParams.gravity = Gravity.CENTER_VERTICAL;
			textviewParams.weight = 1;
			textviewParams.leftMargin = 30;

			email.setText(emails[i]);

			email.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.contact_icon_mail, 0, 0, 0);
			email.setCompoundDrawablePadding(30);
			email.setTextAppearance(ContactsActivity.this,
					R.style.gd_all_text_lay);

			phoneLayout.addView(email, textviewParams);

			final String email = emails[i];
			OnClickListener listen1 = new OnClickListener() {
				public void onClick(View v) {
					if ("".equals(email) || "无".equals(email)) {
						return;
					}
					sendMailByIntent();
				}
			};
			phoneLayout.setOnClickListener(listen1);
			layout.addView(phoneLayout);
			// line
			if (i < emails.length - 1) {
				View line = new View(this);
				LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT, 1);
				line.setBackgroundResource(R.drawable.shape_line);
				// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
				line.setLayoutParams(lineParams);
				layout.addView(line);
			}
		}
		return layout;
	}

	/**
	 * 员工工作电话
	 * 
	 * @return
	 */
	public LinearLayout createStaffPhoneLayout() {

		String[] StaffPhones = userMap.get("StaffPhone").split(";");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0; i < StaffPhones.length; i++) {

			final LinearLayout phoneLayout = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) this
							.getResources().getDimension(R.dimen.gd_one_h));
			phoneLayout.setOrientation(LinearLayout.HORIZONTAL);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			phoneLayout.setLayoutParams(layoutParams);
			phoneLayout.setBackgroundResource(R.drawable.list_more_info);
			// phoneLayout.setBackground(this.getResources().getDrawable(R.drawable.list_more_info));

			// txt
			workMoblie = new TextView(this);
			LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			textviewParams.gravity = Gravity.CENTER_VERTICAL;
			textviewParams.weight = 1;
			textviewParams.leftMargin = 30;

			Boolean isShow = Boolean.parseBoolean(userMap.get(
					"StaffPhonePublic").toLowerCase());
			Log.d("tag", "isShow=" + isShow);
			if (isShow) {
				workMoblie.setText(StaffPhones[i]);
			} else {
				workMoblie.setText("");
			}

			workMoblie.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.staffphone, 0, 0, 0);
			workMoblie.setCompoundDrawablePadding(30);
			workMoblie.setTextAppearance(ContactsActivity.this,
					R.style.gd_all_text_lay);

			phoneLayout.addView(workMoblie, textviewParams);

			final String num = workMoblie.getText().toString();
			OnClickListener listen1 = new OnClickListener() {
				public void onClick(View v) {
					if ("".equals(num) || "无".equals(num)) {
						return;
					}
					Intent phoneIntent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"
									+ num));
					ContactsActivity.this.startActivity(phoneIntent);
				}
			};
			phoneLayout.setOnClickListener(listen1);
			layout.addView(phoneLayout);

			// if (i < StaffPhones.length - 1) {
			// line
			View line = new View(this);
			LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 1);
			line.setBackgroundResource(R.drawable.shape_line);
			// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
			line.setLayoutParams(lineParams);
			layout.addView(line);
			// }
		}
		return layout;
	}

	private void detailinit() {

		searchLayout.setVisibility(View.GONE);

		if (SharedPrefsConfig.getSharedPrefsInstance(this).getUserId()
				.equals(Eid)) {
			userMap.put("StaffPhone",
					SharedPrefsConfig.getSharedPrefsInstance(this)
							.getUserStaffPhone());
		}
		myExpandableListView.setVisibility(View.GONE);
		childListView.setVisibility(View.GONE);
		layout_detail.setVisibility(View.VISIBLE);

		RemoteImageView contactImage = (RemoteImageView) findViewById(R.id.contact_icon);

		if (userMap == null || userMap.size() == 0) {
			return;
		}

		// String gender = userMap.get("UserGender");
		if ("男".equals(userMap.get("UserGender"))) {
			contactImage.setImageResource(R.drawable.pic_contact_male);
		} else if ("女".equals(userMap.get("UserGender"))) {
			contactImage.setImageResource(R.drawable.pic_contact_female);
		}

		LinearLayout out_layout = (LinearLayout) this
				.findViewById(R.id.out_Layout);

		out_layout.addView(createTelLayout());
		out_layout.addView(createPhoneLayout());
		out_layout.addView(createStaffPhoneLayout());
		out_layout.addView(createMailLayout());

		addContacts.setVisibility(View.VISIBLE);

		Log.w("tag", "StaffPhonePublic=" + userMap.get("StaffPhonePublic")
				+ ",UserMobilePublic=" + userMap.get("UserMobilePublic"));

		TextView tvName = (TextView) findViewById(R.id.name);
		tvName.setText(userMap.get("UserName"));

		TextView tvAddress = (TextView) findViewById(R.id.address);
		tvAddress.setText(userMap.get("CorpName") + " - "
				+ userMap.get("DeptName"));

		TextView tvUserPost = (TextView) findViewById(R.id.userPost);
		tvUserPost.setText(userMap.get("UserPost"));

		TextView tvPyTitle = (TextView) findViewById(R.id.pyTitle);
		tvPyTitle.setText(userMap.get("UserPinyin"));

		String UserImgUrl = userMap.get("UserImgUrl");
		// System.out.println("UserImgUrl===========>" + UserImgUrl);
		try {
			URLEncoder.encode(UserImgUrl, "UTF-8");
			UserImgUrl = UserImgUrl.replaceAll(" ", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		contactImage.setImageUrl(UserImgUrl);
	}

	private void sendMailByIntent() {
		String userEmail = userMap.get("UserEmail");
		String[] reciver = new String[] { userEmail };

		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
		startActivity(Intent.createChooser(myIntent, ""));

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				groupList();
				break;
			case 2:
				detailinit();
				break;
			case 3:
				Toast.makeText(ContactsActivity.this,
						R.string.no_data_or_data_have_error, Toast.LENGTH_LONG)
						.show();
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.layout_phone:
		// break;
		// case R.id.layout_tel:
		// break;
		// case R.id.layout_email:
		// break;
		// }
		switch (v.getId()) {
		case R.id.search_clear:
			searchET.getText().clear();
			searchClear.setVisibility(View.GONE);
			layout_center.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			break;
		case R.id.add_contact_to_native:
			addContact();
			break;

		default:
			break;
		}
	}

	private void addContact() {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.dir/person");
		intent.setType("vnd.android.cursor.dir/contact");
		intent.setType("vnd.android.cursor.dir/raw_contact");

		intent.putExtra(Insert.NAME, userMap.get("UserName"));
		// 个人手机
		intent.putExtra(Insert.PHONE_TYPE, Phone.TYPE_MOBILE);
		intent.putExtra(Insert.PHONE, moblie.getText().toString());
		// 工作手机
		intent.putExtra(Insert.SECONDARY_PHONE_TYPE, Phone.TYPE_WORK_MOBILE);
		intent.putExtra(Insert.SECONDARY_PHONE, workMoblie.getText().toString());
		// 工作座机
		intent.putExtra(Insert.TERTIARY_PHONE_TYPE, Phone.TYPE_FAX_WORK);
		intent.putExtra(Insert.TERTIARY_PHONE, fixTell.getText().toString());

		intent.putExtra(Insert.JOB_TITLE, userMap.get("UserPost"));
		intent.putExtra(Insert.COMPANY, userMap.get("CorpName"));

		intent.putExtra(Insert.EMAIL, Email.TYPE_WORK);
		intent.putExtra(Insert.EMAIL, email.getText().toString());

		startActivity(intent);
	}

	// add begin lhy 2013/06/20
	private void getContactList() {

		groupData.clear();
		childData.clear();
		DataHelper dataHelper = new DataHelper(ContactsActivity.this);
		dataHelper.openDatabase();
		groupData = dataHelper.queryZoneAndCorpInfo(childData);
		dataHelper.closeDatabase();
		handler.sendEmptyMessage(1);

		if (groupData == null || groupData.size() == 0) {
			DataTask task = new DataTask(ContactsActivity.this);
			task.execute(Constants.KServerurl + "GetAllOrgList", "");
		}
	}

	private void getGetUserInfo(String userId) {

		// DataHelper dataHelper = new DataHelper(ContactsActivity.this);
		// dataHelper.openDatabase();
		// userMap = dataHelper.queryEmpInfoByEmpId(userId);
		// dataHelper.closeDatabase();
		// // 如果数据库里面没有用户信息
		// if (userMap.size() == 0) {
		// userMap = new HashMap<String, String>();
		getGetUserInfoFromNet(userId);
		// } else {
		// handler.sendEmptyMessage(2);
		// }
	}

	private void getGetUserInfoFromNet(String userId) {
		HttpUtil con = new HttpUtil();
		con.iResult = ContactsActivity.this.onNetGetUserInfo;
		con.context = ContactsActivity.this;

		String postStrOther = "&userid=" + userId;
		con.executeTask(true, "GetSigleEmp", postStrOther);
	}

	private IResult onNetGetUserInfo = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				parseJsonUserInfoData(jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
				searchNative();
				Toast.makeText(ContactsActivity.this,
						R.string.get_one_staff_detail_msg, Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			searchNative();
			Toast.makeText(ContactsActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {

		}
	};

	private void searchNative() {
		DataHelper dataHelper = new DataHelper(ContactsActivity.this);
		dataHelper.openDatabase();
		userMap = dataHelper.queryEmpInfoByEmpId(userId);
		dataHelper.closeDatabase();
		handler.sendEmptyMessage(2);
	}

	// 获取一条员工详细信息
	private void parseJsonUserInfoData(String jsonStr) {
		try {
			JSONArray array = new JSONArray(jsonStr);
			JSONObject obj = array.getJSONObject(0);
			Log.e("TAG", obj.toString());
			String result = obj.getString("result");
			if ("False".equals(result)) {
				System.out.println("数据获取失败！！");
				return;
			}

			JSONArray josnArrayUser = obj.getJSONArray("User");
			JSONObject jsonObj = josnArrayUser.getJSONObject(0);
			jsonObj.toString();

			userMap.put("UserImgUrl", jsonObj.getString("UserImgUrl"));
			userMap.put("UserName", jsonObj.getString("UserName"));
			userMap.put("UserPinyin", jsonObj.getString("UserPinyin"));
			userMap.put("CorpName", jsonObj.getString("CorpName"));
			userMap.put("DeptName", jsonObj.getString("DeptName"));
			userMap.put("UserMobile", jsonObj.getString("UserMobile"));
			userMap.put("UserTel", jsonObj.getString("UserTel"));
			userMap.put("UserEmail", jsonObj.getString("UserEmail"));
			userMap.put("UserPost", jsonObj.getString("UserPost"));
			userMap.put("UserGender", jsonObj.getString("UserGender"));
			userMap.put("StaffPhone", jsonObj.isNull("StaffPhone") ? ""
					: jsonObj.getString("StaffPhone"));
			userMap.put("StaffPhonePublic",
					jsonObj.getString("StaffPhonePublic"));
			userMap.put("UserMobilePublic",
					jsonObj.getString("UserMobilePublic"));
			handler.sendEmptyMessage(2);

		} catch (JSONException e) {
			System.out.println("Jsons parse error !");
			e.printStackTrace();
			searchNative();
		}
	}

	/**
	 * 获取整个数据
	 * 
	 * @param jsonStr
	 */
	public void parseJsonGetAllOrgList(String jsonStr) {
		// groupData.clear();
		// childData.clear();
		DataHelper dataHelper = new DataHelper(ContactsActivity.this);
		groupData = new ArrayList<Map<String, String>>();
		childData = new ArrayList<List<Map<String, String>>>();

		Log.d("whz", "result=" + jsonStr);

		try {
			JSONArray array = new JSONArray(jsonStr);
			JSONObject obj = array.getJSONObject(0);

			String result = obj.getString("result");
			if ("False".equals(result)) {
				System.out.println("数据获取失败！！");
				return;
			}

			dataHelper.openDatabase();
			dataHelper.openTransaction();
			// 删除所有地区数据
			dataHelper.deleteZoneInfo();
			// 删除所有地区数据
			dataHelper.deleteCorpInfo();
			dataHelper.deleteDeptInfo();
			dataHelper.deleteEmpInfo();

			// 地区
			JSONArray josnArrayArea = obj.getJSONArray("Area");
			int areaLen = josnArrayArea.length();
			for (int i = 0; i < areaLen; i++) {
				JSONObject jsonObjArea = josnArrayArea.getJSONObject(i);

				String zoneId = jsonObjArea.getString("ZoneId");
				// 存储地区数据
				dataHelper.insertZoneInfo(zoneId,
						jsonObjArea.getString("Zones"));

				Map<String, String> groupMap = new HashMap<String, String>();
				groupMap.put(GROUP_TEXT, jsonObjArea.getString("Zones"));
				groupData.add(groupMap);

				// 省市
				JSONArray josnArrayCorpList = jsonObjArea
						.getJSONArray("CorpList");
				int corpListLen = josnArrayCorpList.length();
				// System.out.println("corpListLen====>" + corpListLen);

				List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
				for (int j = 0; j < corpListLen; j++) {
					JSONObject jsonCorps = josnArrayCorpList.getJSONObject(j);

					String corpId = jsonCorps.getString("CorpId");
					String corps = jsonCorps.getString("Corps");
					// 存储公司信息
					dataHelper.insertCorpInfo(zoneId, corpId, corps);

					Map<String, String> childMap = new HashMap<String, String>();
					childMap.put("CorpId", corpId);
					childMap.put(CHILD_TEXT1, corps);
					childList.add(childMap);

					// 部门
					JSONArray josnArrayDept = jsonCorps.getJSONArray("Dept");
					int deptListLen = josnArrayDept.length();
					for (int k = 0; k < deptListLen; k++) {
						JSONObject jsonDept = josnArrayDept.getJSONObject(k);
						// System.out.println("DeptName"+ k + ":" +
						// jsonDept.getString("DeptName"));
						String deptId = jsonDept.getString("DeptID");
						String deptName = jsonDept.getString("DeptName");
						// 存储部门数据
						dataHelper.insertDeptInfo(corpId, deptId, deptName);

						// 员工
						JSONArray josnArrayEmpList = jsonDept
								.getJSONArray("EmpList");
						int empListLen = josnArrayEmpList.length();
						for (int t = 0; t < empListLen; t++) {
							JSONObject jsonEmp = josnArrayEmpList
									.getJSONObject(t);
							// System.out.println("EmployName"+ t + ":" +
							// jsonEmp.getString("EmployName"));
							dataHelper.insertEmpInfo(
									deptId,
									jsonEmp.getString("EmployID"),
									jsonEmp.getString("EmployName"),
									jsonEmp.getString("Mobile"),
									jsonEmp.getString("UserImgUrl"),
									jsonEmp.getString("UserPinyin"),
									jsonEmp.getString("CorpName"),
									jsonEmp.getString("DeptName"),
									jsonEmp.getString("UserTel"),
									jsonEmp.getString("UserEmail"),
									jsonEmp.getString("UserPost"),
									jsonEmp.getString("UserGender"),
									jsonEmp.isNull("StaffPhone") ? "" : jsonEmp
											.getString("StaffPhone"), jsonEmp
											.getString("UserMobilePublic"),
									jsonEmp.getString("StaffPhonePublic"));
						}
					}

				}
				childData.add(childList);
			}
			// 事务是否成功
			dataHelper.setTransactionSuccessful();
		} catch (JSONException e) {
			System.out.println("Jsons parse error !");
			e.printStackTrace();
		} finally {
			dataHelper.endTransaction();
		}
		dataHelper.closeDatabase();
	}

	class DataTask extends AsyncTask<String, Integer, String> {
		public Context context = null;
		public boolean isPost = false;
		private ProgressDialog dialog = null;
		public String progressMsg = getResources().getString(R.string.upload);

		public DataTask(Context _context) {
			context = _context;
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框
			if (context != null) {
				dialog = ProgressDialog.show(context,
						getResources().getString(R.string.prompt), progressMsg,
						true, true);
				dialog.setCanceledOnTouchOutside(false); // 触屏不退出
				// dialog.setCancelable(false); // 返回键不退出
				dialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {
						Log.e("Cancel", "cancel=======>");
						DataTask.this.cancel(true);
					}
				});
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度
		}

		@Override
		protected String doInBackground(String... params) {
			String jsonString = null;
			try {
				String token = FileOp.DESencode(Constants.DESKey,
						Constants.DESIV,
						"dc|" + Constants.userName.replace("dc/", "") + "|"
								+ Constants.userPwd);
				String postStr = "tokenkey=" + token;
				HttpUtil http = new HttpUtil();
				String param = http.postStrToJson(postStr);
				String url = Constants.KServerIp + "User.ashx";
				HttpClient httpclient = getClient(url);
				HttpPost httppost = new HttpPost(url);
				StringEntity httpbody = new StringEntity(param, HTTP.UTF_8);
				httppost.setEntity(httpbody);
				httppost.addHeader("Accept-Encoding", "gzip");
				// httppost.setHeader("Content-Type", "application/json");
				HttpResponse response = httpclient.execute(httppost);

				InputStream is = response.getEntity().getContent();
				BufferedInputStream bis = new BufferedInputStream(is);

				bis.mark(2);
				// 取前两个字节
				byte[] header = new byte[2];
				int result = bis.read(header);
				// reset输入流到开始位置
				bis.reset();
				// 判断是否是GZIP格式
				int headerData = (int) ((header[0] << 8) | header[1] & 0xFF);
				// Gzip 流 的前两个字节是 0x1f8b
				if (result != -1 && headerData == 0x1f8b) {
					System.out.println("HttpTask  use GZIPInputStream  ");
					is = new GZIPInputStream(bis);
				} else {
					System.out.println("HttpTask  not use GZIPInputStream  ");
					is = bis;
				}
				InputStreamReader reader = new InputStreamReader(is, "utf-8");
				char[] data = new char[1024];
				int readSize;
				StringBuffer sb = new StringBuffer();
				while ((readSize = reader.read(data)) > 0) {
					sb.append(data, 0, readSize);
				}
				jsonString = sb.toString();
				bis.close();
				is.close();
				reader.close();
			} catch (Exception e) {
				Log.e("HttpTask", e.toString(), e);
			}

			try {
				System.out.println(jsonString);
				parseJsonGetAllOrgList(jsonString);
			} catch (Exception e) {
				handler.sendEmptyMessage(3);
			}

			return "";
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			// 任务完成
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		@Override
		protected void onPostExecute(String r) {
			handler.sendEmptyMessage(1);
			// 任务完成
			if (dialog != null) {
				dialog.dismiss();
			}
			querySearchList();
		}

		private HttpClient getClient(String url) {
			HttpClient httpclient = null;
			if (url.contains("https://")) {
				HttpParams par = new BasicHttpParams();
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(new Scheme("https",
						new EasySSLSocketFactory(), 443));
				ClientConnectionManager connManager = new ThreadSafeClientConnManager(
						par, schemeRegistry);
				httpclient = new DefaultHttpClient(connManager, par);
			} else
				httpclient = new DefaultHttpClient();
			return httpclient;
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

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		if (!"".equals(arg0.toString().trim())
				&& arg0.toString().trim() != null) {
			searchClear.setVisibility(View.VISIBLE);
			List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
			Log.d("whz", "searchList=" + searchList.size());
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < searchList.size(); i++) {
				if (searchList.get(i).get("child_text1").toString()
						.replace(" ", "").toLowerCase().trim()
						.contains(arg0.toString().replace(" ", "").trim())) {
					mList.add(searchList.get(i));
				}
			}
			Log.d("whz", "time=" + (System.currentTimeMillis() - startTime));
			searchAdapter(mList);
		} else {
			searchClear.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示查询结果
	 * 
	 * @param child
	 */
	private void searchAdapter(final List<Map<String, String>> child) {
		SearchAdapter adapter = new SearchAdapter(ContactsActivity.this, child);
		mListView.setAdapter(adapter);
		layout_center.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String userId = child.get(arg2).get("EmployID");
				Log.d("whz", "userId=" + userId);
				Intent intent = new Intent();
				intent.setClass(ContactsActivity.this, ContactsActivity.class);
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
					CommonUtil.hideSoftInput(ContactsActivity.this,
							ContactsActivity.this);
					break;
				}

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
	}
}
