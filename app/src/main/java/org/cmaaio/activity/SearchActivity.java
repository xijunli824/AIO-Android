package org.cmaaio.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cmaaio.common.AppDataController;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.entity.CMAAppEntity;
import org.cmaaio.httputil.HttpUtil;
import org.cmaaio.httputil.IResult;
import org.cmaaio.util.ActivityManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SearchActivity extends Activity {
	private LinearLayout contactll;
	private LinearLayout appll;

	private EditText searchView;
	private TextView conText, appText;
	private ListView conList, appList;
	// private ImageView btnSearch;
	private AppAdapter appAdapter;
	private ConAdapter conAdapter;

	private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();

	/*
	 * private String[] childTemStr = new String[] { "李某某", "李某", "陈某", "林某" };
	 * private String[] childTemPhone = new String[] { "0755-86585524",
	 * "0755-86585523", "0755-25639852", "0755-26668855" };
	 * 
	 * private String[] appNames = new String[] { "iTrack", "iTell", "iLive",
	 * "通知", "iWork" }; private String[] appIcons = new String[] { "" +
	 * R.drawable.icon_itrack2, "" + R.drawable.icon_itell2, "" +
	 * R.drawable.icon_app1, "" + R.drawable.icon_task, "" +
	 * R.drawable.icon_app2 };
	 */
	private String[] appImages = new String[] { "0755-86585524",
			"0755-86585523", "0755-25639852", "0755-26668855", "" };

	public List<String> userIdList = new ArrayList<String>();
	public List<String> AppsIdList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_dialog);

		// add by shrimp at 20130622
		contactll = (LinearLayout) findViewById(R.id.contactll);
		appll = (LinearLayout) findViewById(R.id.appll);
		contactll.setVisibility(View.GONE);
		appll.setVisibility(View.GONE);

		searchView = (EditText) this.findViewById(R.id.searchbox);
		// btnSearch = (ImageView) findViewById(R.id.btn_search);
		conText = (TextView) this.findViewById(R.id.con_text);
		appText = (TextView) this.findViewById(R.id.app_text);

		appList = (ListView) this.findViewById(R.id.app_list);
		conList = (ListView) this.findViewById(R.id.con_list);

		appAdapter = new AppAdapter(this);
		appList.setAdapter(appAdapter);

		conAdapter = new ConAdapter();
		conList.setAdapter(conAdapter);
		// 取消
		findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		searchView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1,
					KeyEvent event) {
				if (event == null || event.getAction() == KeyEvent.ACTION_DOWN) {
					String searchString = searchView.getText().toString()
							.trim();
					if ("".equals(searchString)) {
						Toast.makeText(SearchActivity.this,
								R.string.search_content, Toast.LENGTH_SHORT)
								.show();
					} else {
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(SearchActivity.this
										.getCurrentFocus().getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
						conAdapter.names.clear();
						conAdapter.tels.clear();
						conAdapter.userGender.clear();
						conAdapter.userPost.clear();

						userIdList.clear();
						appAdapter.appNames.clear();
						appAdapter.appIcons.clear();
						AppsIdList.clear();
						getContactOrAppList(searchString);
					}
					return true;
				}
				return false;
			}
		});

		conText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (conList.getVisibility() == View.VISIBLE) {
					conList.setVisibility(View.GONE);
				} else {
					conList.setVisibility(View.VISIBLE);
				}
			}
		});
		appText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (appList.getVisibility() == View.VISIBLE) {
					appList.setVisibility(View.GONE);
				} else {
					appList.setVisibility(View.VISIBLE);
				}
			}
		});

		appList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				CMAAppEntity app = new CMAAppEntity();
				app.appId = AppsIdList.get(index);

				// false 未安装
				if (!AppDataController.getInstance().contain(app)) {
					Intent intent = new Intent();
					intent.putExtra("Appid", AppsIdList.get(index));
					intent.setClass(SearchActivity.this,
							AppDetailActivity.class);
					SearchActivity.this.startActivity(intent);
				} else {
					app = AppDataController.getInstance().getAppById(app.appId);
					Intent intent = new Intent();
					intent.putExtra("appUrl", app.appPath);
					intent.setClass(SearchActivity.this, PGapActivity.class);
					startActivity(intent);
				}
			}
		});
		/**
		 * 个人信息点击事件
		 */
		conList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SearchActivity.this, ContactsActivity.class);
				intent.putExtra("isDetail", true);
				intent.putExtra("UserId", userIdList.get(arg2));
				startActivity(intent);
			}
		});

		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}

	// add begin lhy 2013/06/20
	private void getContactOrAppList(String key) {
		HttpUtil con = new HttpUtil();
		con.iResult = SearchActivity.this.onNetResult;
		con.context = SearchActivity.this;
		con.httpGetAppinfoAndEmploy(key);
	}

	private IResult onNetResult = new IResult() {
		@Override
		public void OnResult(String jsonStr) {
			try {
				parseJsonData(jsonStr);
				// TODO
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(SearchActivity.this,
						R.string.get_search_contact_or_app, Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void OnFail(String errorMsg) {
			// TODO Auto-generated method stub
			Toast.makeText(SearchActivity.this, errorMsg, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void OnCacnel() {
			// TODO Auto-generated method stub

		}
	};

	// json解析
	private void parseJsonData(String jsonStr) {
		try {
			Map<String, String> mMap = null;
			JSONArray array = new JSONArray(jsonStr);
			JSONObject obj = array.getJSONObject(0);

			System.out.println(obj.toString());

			JSONArray arrayUsers = obj.getJSONArray("Users");

			int usersLen = arrayUsers.length();
			for (int i = 0; i < usersLen; i++) {
				mMap = new HashMap<String, String>();

				JSONObject jsonObjTemp = arrayUsers.getJSONObject(i);
				userIdList.add(jsonObjTemp.getString("Id"));
				conAdapter.names.add(jsonObjTemp.getString("Name"));
				String mobile = jsonObjTemp.getString("Mobile") + "";
				if (!"".equals(mobile) && !"无".equals(mobile)) {
					conAdapter.tels.add(mobile);
				} else {
					conAdapter.tels.add(jsonObjTemp.getString("LineTel") + "");
				}
				conAdapter.userGender.add(jsonObjTemp.getString("UserGender")
						+ "");
				Log.e("Tag", jsonObjTemp.getString("UserPost") + "");
				conAdapter.userPost.add(jsonObjTemp.getString("UserPost") + "");
				conAdapter.UserMobile.add(jsonObjTemp
						.getString("UserMobilePublic"));
				conAdapter.StaffPhone.add(jsonObjTemp
						.getString("StaffPhonePublic"));
				/**
				 * 封装查询出来的，用于显示
				 */
				// TODO 少邮箱，公司名，部门名，工作手机四个字段
				mMap.put("name", jsonObjTemp.getString("Name"));// 中文名
				mMap.put("userPinyin", jsonObjTemp.getString("UserPinyin"));// 英文名
				mMap.put("userPost", jsonObjTemp.getString("UserPost"));// 职务
				mMap.put("lineTel", jsonObjTemp.getString("LineTel"));// 办公座机
				mMap.put("moblie", jsonObjTemp.getString("Mobile"));// 个人手机
				mMap.put("userMobilePublic",
						jsonObjTemp.getString("UserMobilePublic"));// 是否公开个人电话
				mMap.put("StaffPhonePublic",
						jsonObjTemp.getString("StaffPhonePublic"));// 是否公开工作手机
				mMap.put("UserGender", jsonObjTemp.getString("UserGender"));// 性别
				mList.add(mMap);
			}

			conAdapter.notifyDataSetChanged();

			JSONArray arrayApps = obj.getJSONArray("Apps");

			// System.out.println(obj.getString("Apps"));
			int appsLen = arrayApps.length();
			for (int i = 0; i < appsLen; i++) {
				JSONObject jsonObjTemp = arrayApps.getJSONObject(i);
				System.out.println("appId======>" + i + ":"
						+ jsonObjTemp.getString("Id"));
				AppsIdList.add(jsonObjTemp.getString("Id") + "");
				appAdapter.appNames.add(jsonObjTemp.getString("Name") + "");
				appAdapter.appIcons.add(jsonObjTemp.getString("ImageUrl") + ""); // TODO
				// add
				appAdapter.appAliases.add(jsonObjTemp.getString("AppAliases")
						+ "");

				// 下载Icon图片
				appAdapter.appImages.add(appImages[0]);
			}
			appAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			System.out.println("Jsons parse error !");
			e.printStackTrace();
		}
	}

	// add end 2013/06/20

	private class AppAdapter extends BaseAdapter {

		public List<String> appNames = new ArrayList<String>();
		public List<String> appIcons = new ArrayList<String>();
		public List<String> appImages = new ArrayList<String>();
		public List<String> appAliases = new ArrayList<String>();
		

		public AppAdapter(Context context) {

		}

		@Override
		public int getCount() {
			return appNames.size();
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
			final ViewHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.app_search_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.app_child_title);
				holder.icon = (RemoteImageView) convertView
						.findViewById(R.id.app_child_icon);
				// holder.imageview = (ImageView)
				// convertView.findViewById(R.id.app_child_image);
				holder.message = (TextView) convertView
						.findViewById(R.id.txt_isinstall);
				// holder.text.setText(appNames.get(position));
				holder.text.setText(appAliases.get(position));
				// 缺省的icon
				holder.icon.setImageResource(R.drawable.defalutapp);

				holder.icon.setImageUrl(this.appIcons.get(position));

				CMAAppEntity app = new CMAAppEntity();
				app.appId = AppsIdList.get(position);
				if (AppDataController.getInstance().contain(app)) {
					holder.message.setText(R.string.app_have_install);
				}

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.tv.setText(array[position]);
			contactll.setVisibility(View.VISIBLE);
			appll.setVisibility(View.VISIBLE);

			return convertView;
		}

	}

	private class ConAdapter extends BaseAdapter {

		public List<String> names = new ArrayList<String>();
		public List<String> tels = new ArrayList<String>();
		public List<String> userGender = new ArrayList<String>();
		public List<String> userPost = new ArrayList<String>();
		public List<String> UserMobile = new ArrayList<String>();// 是否公开个人手机
		public List<String> StaffPhone = new ArrayList<String>();// 是否公开工作手机

		@Override
		public int getCount() {
			return names.size();
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
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater mInflater = (LayoutInflater) SearchActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.contacts_child_item,
						null);
				holder.genderImageview = (ImageView) convertView
						.findViewById(R.id.contacts_child_itel_image_view);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.contacts_child_image);
				holder.text = (TextView) convertView
						.findViewById(R.id.contacts_child_title);
				holder.textUserPost = (TextView) convertView
						.findViewById(R.id.contacts_child_zc);
				holder.message = (TextView) convertView
						.findViewById(R.id.contacts_child_phone);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(names.get(position));
			boolean isShowNumber=Boolean.parseBoolean(UserMobile.get(position).toLowerCase().trim());
			if (isShowNumber) {
				holder.message.setText(tels.get(position));
			}else {
				holder.message.setText("");
			}
			
			holder.textUserPost.setText(userPost.get(position));

			if ("男".equals(userGender.get(position))) {
				holder.genderImageview
						.setImageResource(R.drawable.pic_contact_list_man);
			} else if ("女".equals(userGender.get(position))) {
				holder.genderImageview
						.setImageResource(R.drawable.pic_contact_list_female);
			} else {
				holder.genderImageview.setImageResource(R.drawable.nosex_small);
			}

			final int n = position;
			holder.imageview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent phoneIntent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"
									+ tels.get(n)));
					SearchActivity.this.startActivity(phoneIntent);
				}
			});
			// add by shrimp at 20130622
			contactll.setVisibility(View.VISIBLE);
			appll.setVisibility(View.VISIBLE);

			return convertView;
		}

	}

	class ViewHolder {
		ImageView genderImageview;
		ImageView imageview;
		TextView text;
		TextView textUserPost;
		RemoteImageView icon;
		TextView message;
	}

}
