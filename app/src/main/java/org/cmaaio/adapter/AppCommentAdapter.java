package org.cmaaio.adapter;

import org.cmaaio.activity.R;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import org.cmaaio.entity.*;

public class AppCommentAdapter extends BaseAdapter {
	@SuppressWarnings("unused")
	private Context context = null;
	private LayoutInflater layoutInflater = null;
	public ArrayList<CommentEntity> dataSource = null;

	public AppCommentAdapter(Context context, ArrayList<CommentEntity> data) {
		this.context = context;
		this.dataSource = data;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return dataSource.size();
	}

	@Override
	public Object getItem(int position) {
		return dataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dataSource.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater
					.inflate(R.layout.item_appcomment, null);
			holder.level = (RatingBar) convertView.findViewById(R.id.level);
			holder.commentName = (TextView) convertView
					.findViewById(R.id.commentName);
			holder.commentDetail = (TextView) convertView
					.findViewById(R.id.commentDetail);
			holder.commentTime = (TextView) convertView
					.findViewById(R.id.commentTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CommentEntity obj = dataSource.get(position);
		holder.commentName.setText(obj.userName);
		holder.commentTime.setText(obj.time);
		holder.commentDetail.setText(obj.content);
		holder.level.setRating(obj.level);
		return convertView;
	}

	class ViewHolder {
		RatingBar level;
		TextView commentName;
		TextView commentTime;
		TextView commentDetail;
	}

}
