package org.cmaaio.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cmaaio.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListAdapter extends BaseExpandableListAdapter{
    	

	//�����ַ�������Ϊgroup��child��ͼ��TextView�ı��
	private static final String GROUP_TEXT = "group_text";
	
	private static final String CHILD_TEXT1 = "child_text1";
	private static final String CHILD_TEXT2 = "child_text1";
	private Context context;
	
	List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
    List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
    	
    	//���췽����������ȷ��ʲô����
    	public ContactListAdapter(Context context ,List<List<Map<String, String>>> childData,List<Map<String, String>> groupData) {
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
		public View getChildView(final int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub	
			ViewHolder holder = null;
			if(convertView == null) {
				//����һ�ֽ���XML�����ļ��ķ�ʽ
				holder = new ViewHolder();
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.contacts_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.contats_image);
				holder.text = (TextView) convertView.findViewById(R.id.item_text);
				holder.text.setTextSize(15);//add by shrimp at 20130625 for childTextsize
				convertView.setTag(holder);				
			}else{
				holder=(ViewHolder)convertView.getTag();
			}

			holder.text.setText(getChild(groupPosition, childPosition).toString());
//			if(childPosition%2 == 0) {
//				holder.imageview.setImageResource(R.drawable.arrow);
//			} else {
//				holder.imageview.setImageResource(R.drawable.arrow_unfold);
//			}				
			holder.imageview.setImageResource(R.drawable.arrow);

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
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			ViewHolder holder = null;
			if(convertView == null) {
				//����һ�ֽ���XML�����ļ��ķ�ʽ
				holder = new ViewHolder();
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.contacts_group_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.contats_group_image);
				holder.text = (TextView) convertView.findViewById(R.id.item_group_text);
				holder.icon = (ImageView) convertView.findViewById(R.id.group_header);				
				convertView.setTag(holder);
				
			}else{
				holder=(ViewHolder)convertView.getTag();
			}
			holder.icon .setVisibility(View.GONE);			
			if(!isExpanded) {
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
			ImageView imageview;
			TextView text;
			ImageView icon;
		}
    }




