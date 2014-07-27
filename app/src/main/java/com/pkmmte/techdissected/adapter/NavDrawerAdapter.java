package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.Category;
import java.util.ArrayList;
import java.util.List;

public class NavDrawerAdapter extends BaseAdapter {
	// Essential Resources
	private final List<Category> mDrawerItems;
	private final Context mContext;

	// Current index and custom fonts
	private int currentPage;

	public NavDrawerAdapter(Context context) {
		this.mDrawerItems = new ArrayList<Category>();
		this.mContext = context;
		this.currentPage = 1;
	}

	public NavDrawerAdapter(Context context, List<Category> drawerItems) {
		this.mDrawerItems = drawerItems;
		this.mContext = context;
		this.currentPage = 1;
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
		holder.txtTitle.setBackgroundColor(mContext.getResources().getColor());
		// holder.txtTitle.setTypeface((currentPage == position) ? fontSelected : fontNormal);

		return convertView;
	}

	private class ViewHolder {
		public TextView txtTitle;
	}
}