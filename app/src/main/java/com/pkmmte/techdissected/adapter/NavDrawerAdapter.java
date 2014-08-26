package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pkmmte.pkrss.Category;
import com.pkmmte.techdissected.R;
import java.util.ArrayList;
import java.util.List;

public class NavDrawerAdapter extends BaseAdapter {
	// Essential Resources
	private final List<Category> mDrawerItems;
	private final Context mContext;

	// Current index and custom fonts
	private int currentPage;
	private int highlightColor;

	public NavDrawerAdapter(Context context) {
		this(context, new ArrayList<Category>());
	}

	public NavDrawerAdapter(Context context, List<Category> drawerItems) {
		this.mDrawerItems = drawerItems;
		this.mContext = context;
		this.currentPage = 0;
		this.highlightColor = context.getResources().getColor(R.color.action_overlay);
	}

	public void addItem(Category drawerItem) {
		mDrawerItems.add(drawerItem);
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDrawerItems.size();
	}

	@Override
	public Category getItem(int position) {
		return mDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_item, parent, false);

			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Set text
		holder.txtTitle.setText(mDrawerItems.get(position).getName());

		// Highlight if selected
		holder.txtTitle.setBackgroundColor((currentPage == position) ? highlightColor : Color.TRANSPARENT);

		return convertView;
	}

	private class ViewHolder {
		public TextView txtTitle;
	}
}