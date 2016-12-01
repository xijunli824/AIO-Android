package org.cmaaio.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.cmaaio.common.Constants;
import org.cmaaio.common.RemoteImageView;
import org.cmaaio.common.XButton;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.httputil.HttpFactory;
import org.cmaaio.httputil.HttpFactory.HttpResult;
import org.cmaaio.ui.SlipButton;
import org.cmaaio.ui.SlipButton.OnChangedListener;
import org.cmaaio.util.ActivityManager;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfomationViewActivity extends CMABaseActivity implements
		OnClickListener {

	private Map<String, String> userMap = null;

	private LinearLayout layout_detail;

	private Button backib;
	private XButton btnReflesh = null;
	private TextView tittle;

	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_view_activity);
		super.initTaBar(2);

		layout_detail = (LinearLayout) findViewById(R.id.layout_detail);

		initTopButton();

		tittle = (TextView) findViewById(R.id.contact_title);
		tittle.setText(R.string.user_setting);

		dialog = new ProgressDialog(this);
		dialog.setMessage(getResources().getText(R.string.upload));

		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getGetUserInfo();
	}

	private void initTopButton() {
		backib = (Button) findViewById(R.id.backib);
		backib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 创建个人电话
	 * 
	 * @return
	 */
	public LinearLayout createPhoneLayout() {

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		// userMap.put("UserMobile", "");
		String mobiles = userMap.get("UserMobile");

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
		TextView txt = new TextView(this);
		LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		textviewParams.gravity = Gravity.CENTER_VERTICAL;
		textviewParams.weight = 1;
		textviewParams.leftMargin = 30;

		// txt.setGravity(Gravity.CENTER_HORIZONTAL);
		txt.setText(mobiles);
		txt.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.contact_icon_call, 0, 0, 0);
		txt.setCompoundDrawablePadding(30);
		txt.setTextAppearance(UserInfomationViewActivity.this,
				R.style.gd_all_text_lay);

		phoneLayout.addView(txt, textviewParams);

		// OnClickListener listen1 = new OnClickListener() {
		// public void onClick(View v) {
		// Intent intent = new Intent(UserInfomationViewActivity.this,
		// UserInfomationModifyActivity.class);
		// startActivity(intent);
		// }
		// };
		// phoneLayout.setOnClickListener(listen1);
		layout.addView(phoneLayout);
		// line
		View line = new View(this);
		LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 1);
		line.setBackgroundResource(R.drawable.shape_line);
		// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
		line.setLayoutParams(lineParams);
		layout.addView(line);
		return layout;
	}

	/**
	 * 创建工作电话
	 * 
	 * @return
	 */
	public LinearLayout createTelLayout() {

		String mobiles = userMap.get("UserTel");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

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
		TextView txt = new TextView(this);
		LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		textviewParams.gravity = Gravity.CENTER_VERTICAL;
		textviewParams.weight = 1;
		textviewParams.leftMargin = 30;
		// txt.setGravity(Gravity.CENTER_HORIZONTAL);
		txt.setText(mobiles);

		txt.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.contact_icon_call2, 0, 0, 0);
		txt.setCompoundDrawablePadding(30);
		txt.setTextAppearance(UserInfomationViewActivity.this,
				R.style.gd_all_text_lay);
		phoneLayout.addView(txt, textviewParams);

		// final String phone = mobiles;
		// OnClickListener listen1 = new OnClickListener() {
		// public void onClick(View v) {
		// if ("".equals(phone) || "无".equals(phone)) {
		// return;
		// }
		// Intent intent = new Intent(UserInfomationViewActivity.this,
		// UserInfomationModifyActivity.class);
		// startActivity(intent);
		// }
		// };
		// phoneLayout.setOnClickListener(listen1);
		layout.addView(phoneLayout);
		// line
		View line = new View(this);
		LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 1);
		line.setBackgroundResource(R.drawable.shape_line);
		// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
		line.setLayoutParams(lineParams);
		layout.addView(line);
		return layout;
	}

	/**
	 * 创建邮箱
	 * 
	 * @return
	 */
	public LinearLayout createMailLayout() {

		String emails = userMap.get("UserEmail");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

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
		TextView txt = new TextView(this);
		LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		textviewParams.gravity = Gravity.CENTER_VERTICAL;
		textviewParams.weight = 1;
		textviewParams.leftMargin = 30;
		txt.setText(emails);

		txt.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.contact_icon_mail, 0, 0, 0);
		txt.setCompoundDrawablePadding(30);
		txt.setTextAppearance(UserInfomationViewActivity.this,
				R.style.gd_all_text_lay);

		phoneLayout.addView(txt, textviewParams);
		// final String email = emails[i];
		// OnClickListener listen1 = new OnClickListener() {
		// public void onClick(View v) {
		// if ("".equals(email) || "无".equals(email)) {
		// return;
		// }
		// sendMailByIntent();
		// }
		// };
		// phoneLayout.setOnClickListener(listen1);
		layout.addView(phoneLayout);
		// line
		// View line = new View(this);
		// LinearLayout.LayoutParams lineParams = new
		// LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1);
		// line.setBackgroundResource(R.drawable.shape_line);
		// //
		// line.setBackground(this.getResources().getDrawable(R.drawable.shape_line));
		// line.setLayoutParams(lineParams);
		// layout.addView(line);
		return layout;
	}

	/**
	 * 创建工作电话
	 * 
	 * @return
	 */
	public LinearLayout createStaffPhoneLayout() {

		String staffPhone = userMap.get("StaffPhone");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

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
		TextView txt = new TextView(this);
		LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		textviewParams.gravity = Gravity.CENTER_VERTICAL;
		textviewParams.weight = 1;
		textviewParams.leftMargin = 30;

		txt.setText(staffPhone);

		txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.staffphone, 0,
				R.drawable.arrow, 0);
//		txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow, 0);
		txt.setCompoundDrawablePadding(30);
		txt.setPadding(0, 0, 30, 0);
		txt.setTextAppearance(UserInfomationViewActivity.this,
				R.style.gd_all_text_lay);

		phoneLayout.addView(txt, textviewParams);

		OnClickListener listen1 = new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(UserInfomationViewActivity.this,
						UserInfomationModifyActivity.class);
				startActivity(intent);
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
		return layout;
	}

	private void detailinit() {

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

		/**
		 * 个人通话信息表
		 */

		LinearLayout out_layout = (LinearLayout) this
				.findViewById(R.id.out_Layout);
		out_layout.removeAllViews();

		out_layout.addView(createTelLayout());
		out_layout.addView(createPhoneLayout());

		out_layout.addView(createStaffPhoneLayout());
		out_layout.addView(createMailLayout());
		// 设置是否公开号码
		setNumber();

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

	@Override
	public void onClick(View v) {

	}

	/**
	 * 设置是否公开电话号码
	 */
	private void setNumber() {
		/**
		 * 个人号码、工作号码是否公开
		 */

		final SharedPrefsConfig spc = SharedPrefsConfig
				.getSharedPrefsInstance(this);
		boolean isP = spc.getPNumber();
		boolean isW = spc.getWNumber();

		final SlipButton privateBtn = (SlipButton) findViewById(R.id.set_private_number);
		final SlipButton workBtn = (SlipButton) findViewById(R.id.set_work_number);

		Log.d("whz", "isP=" + isP);
		Log.d("whz", "isW=" + isW);

		privateBtn.setCheck(isP);
		workBtn.setCheck(isW);
		/**
		 * 设置个人号码监听器
		 */

		privateBtn.SetOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(final boolean CheckState) {
				Log.d("whz", "privateBtn CheckState=" + CheckState);

				dialog.show();

				/**
				 * 封装请求参数
				 */
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("userADCount",
						Constants.userName));
				pairs.add(new BasicNameValuePair("type", "UserMobile"));
				pairs.add(new BasicNameValuePair("status", Boolean
						.toString(CheckState)));
				HttpFactory.get(Constants.KServerurl + "EditTellPhoneStatus",
						pairs, new HttpResult() {

							@Override
							public void onSuccess(String result) {// 请求成功
								try {
									boolean isSuccess = Boolean
											.parseBoolean(new JSONArray(result)
													.getJSONObject(0)
													.getString("result")
													.toLowerCase());
									if (isSuccess) {
										spc.setPNumber(CheckState);// 记录状态
									} else {
										spc.setPNumber(!CheckState);// 记录状态
										privateBtn.setCheck(!CheckState);// btn状态还原
										Toast.makeText(
												UserInfomationViewActivity.this,
												R.string.setting_error,
												Toast.LENGTH_SHORT).show();

									}

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure() {// 请求失败
								spc.setWNumber(!CheckState);
								privateBtn.setCheck(!CheckState);// btn状态还原
								Toast.makeText(UserInfomationViewActivity.this,
										R.string.setting_error,
										Toast.LENGTH_SHORT).show();

							}

							@Override
							public void onFinsh() {// 操作完成
								dialog.dismiss();
							}
						});
			}
		});

		/**
		 * 设置工作号码监听器
		 */
		workBtn.SetOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(final boolean CheckState) {
				Log.d("whz", "workBtn CheckState=" + CheckState);

				dialog.show();
				/**
				 * 封装请求参数
				 */
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("userADCount",
						Constants.userName));
				pairs.add(new BasicNameValuePair("type", "StaffPhone"));
				pairs.add(new BasicNameValuePair("status", Boolean
						.toString(CheckState)));
				HttpFactory.get(Constants.KServerurl + "EditTellPhoneStatus",
						pairs, new HttpResult() {

							@Override
							public void onSuccess(String result) {// 请求成功
								try {
									boolean isSuccess = Boolean
											.parseBoolean(new JSONArray(result)
													.getJSONObject(0)
													.getString("result")
													.toLowerCase());
									if (isSuccess) {
										spc.setWNumber(CheckState);// 保存状态
									} else {
										spc.setWNumber(!CheckState);// 保存状态
										workBtn.setCheck(!CheckState);// btn状态还原
										Toast.makeText(
												UserInfomationViewActivity.this,
												R.string.setting_error,
												Toast.LENGTH_SHORT).show();

									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure() {// 请求失败
								spc.setWNumber(!CheckState);
								workBtn.setCheck(!CheckState);// btn状态还原
								Toast.makeText(UserInfomationViewActivity.this,
										R.string.setting_error,
										Toast.LENGTH_SHORT).show();

							}

							@Override
							public void onFinsh() {// 操作完成
								dialog.dismiss();
							}
						});
			}
		});
	}

	private void getGetUserInfo() {

		userMap = new HashMap<String, String>();
		SharedPrefsConfig spc = SharedPrefsConfig.getSharedPrefsInstance(this);

		userMap.put("UserImgUrl", spc.getUserImageUrl());
		userMap.put("UserName", spc.getUserStaffName());
		userMap.put("UserPinyin", spc.getUserNameEn());
		userMap.put("CorpName", spc.getUserCrop());
		userMap.put("DeptName", spc.getUserDept());
		userMap.put("UserMobile", spc.getUserMobile());
		userMap.put("UserTel", spc.getUserTel());
		userMap.put("UserEmail", spc.getUserMail());
		userMap.put("UserPost", spc.getUserPost());
		userMap.put("StaffPhone", spc.getUserStaffPhone());
		userMap.put("UserId", spc.getUserId());

		// 如果数据库里面没有用户信息
		if (userMap.size() != 0) {
			detailinit();
		}
	}
}
