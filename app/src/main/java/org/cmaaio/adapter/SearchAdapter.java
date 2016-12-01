package org.cmaaio.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cmaaio.activity.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	private static final String CHILD_TEXT1 = "child_text1";
	private static final String CHILD_TEXT2 = "child_text2";
	private static final String CHILD_TEXT3 = "child_text3";
	private static final String USERGENDER = "child_text4";

	private List<Map<String, String>> mList;

	private Context mContext;

	public SearchAdapter(Context context, List<Map<String, String>> list) {
		this.mContext = context;
		if (list != null) {
			this.mList = list;
		} else {
			this.mList = new ArrayList<Map<String, String>>();
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.contacts_child_item, null);
			holder.genderImageview = (ImageView) convertView
					.findViewById(R.id.contacts_child_itel_image_view);
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.contacts_child_image);
			holder.text = (TextView) convertView
					.findViewById(R.id.contacts_child_title);
			holder.text.setTextSize(16);// add by shrimp at 20130625
			holder.message = (TextView) convertView
					.findViewById(R.id.contacts_child_phone);
			holder.zc = (TextView) convertView
					.findViewById(R.id.contacts_child_zc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(mList.get(position).get(CHILD_TEXT1));
		holder.message.setText(mList.get(position).get(CHILD_TEXT2));
		holder.zc.setText(mList.get(position).get(CHILD_TEXT3));

		String gender = mList.get(position).get(USERGENDER).toString();
		if ("男".equals(gender)) {
			holder.genderImageview
					.setImageResource(R.drawable.pic_contact_list_man);
		} else if ("女".equals(gender)) {
			holder.genderImageview
					.setImageResource(R.drawable.pic_contact_list_female);
		} else {
			holder.genderImageview.setImageResource(R.drawable.nosex_small);
		}
		/**
		 * 电话按钮监听器
		 */
		holder.imageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent phoneIntent = new Intent("android.intent.action.CALL",
						Uri.parse("tel:"
								+ mList.get(position).get(CHILD_TEXT2)
										.toString()));
				mContext.startActivity(phoneIntent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView genderImageview;
		ImageView imageview;
		TextView text;
		ImageView icon;
		TextView message;
		TextView zc;

	}

}
