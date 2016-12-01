package org.cmaaio.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cmaaio.activity.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactChildAdapter extends BaseExpandableListAdapter {

	private static final String GROUP_TEXT = "group_text";

	private static final String CHILD_TEXT1 = "child_text1";
	private static final String CHILD_TEXT2 = "child_text2";
	private static final String CHILD_TEXT3 = "child_text3";
	private static final String USERGENDER = "child_text4";

	private Context context;

	List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

	public ContactChildAdapter(Context context, List<List<Map<String, String>>> childData, List<Map<String, String>> groupData) {
		super();
		this.context = context;
		this.groupData = groupData;
		this.childData = childData;

	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childData.get(groupPosition).get(childPosition).get(CHILD_TEXT1).toString();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.contacts_child_item, null);
			holder.genderImageview = (ImageView) convertView.findViewById(R.id.contacts_child_itel_image_view);
			holder.imageview = (ImageView) convertView.findViewById(R.id.contacts_child_image);
			holder.text = (TextView) convertView.findViewById(R.id.contacts_child_title);
			holder.text.setTextSize(16);// add by shrimp at 20130625
			holder.message = (TextView) convertView.findViewById(R.id.contacts_child_phone);
			holder.zc = (TextView) convertView.findViewById(R.id.contacts_child_zc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.message.setText(childData.get(groupPosition).get(childPosition).get(CHILD_TEXT2).toString());
		holder.text.setText(childData.get(groupPosition).get(childPosition).get(CHILD_TEXT1).toString());
		holder.zc.setText(childData.get(groupPosition).get(childPosition).get(CHILD_TEXT3).toString());

		String gender = childData.get(groupPosition).get(childPosition).get(USERGENDER).toString();
		if ("男".equals(gender)) {
			holder.genderImageview.setImageResource(R.drawable.pic_contact_list_man);
		} else if ("女".equals(gender)) {
			holder.genderImageview.setImageResource(R.drawable.pic_contact_list_female);
		} else {
			holder.genderImageview.setImageResource(R.drawable.nosex_small);
		}

		// if(childPosition%2 == 0) {
		// holder.imageview.setImageResource(R.drawable.arrow);
		// } else {
		// holder.imageview.setImageResource(R.drawable.arrow_unfold);
		// }
		holder.imageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + childData.get(groupPosition).get(childPosition).get(CHILD_TEXT2).toString()));
				context.startActivity(phoneIntent);
			}
		});

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupData.get(groupPosition).get(GROUP_TEXT).toString();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.contacts_group_item, null);
			holder.imageview = (ImageView) convertView.findViewById(R.id.contats_group_image);
			holder.text = (TextView) convertView.findViewById(R.id.item_group_text);
			holder.icon = (ImageView) convertView.findViewById(R.id.group_header);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.icon.setVisibility(View.GONE);
		if (!isExpanded) {
			holder.imageview.setImageResource(R.drawable.arrow);
			holder.text.setTextColor(Color.parseColor("#555555"));
		} else {
			holder.text.setTextColor(0xff379a51);
			holder.imageview.setImageResource(R.drawable.arrow_unfold);
		}
		holder.text.setText(getGroup(groupPosition).toString());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
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
