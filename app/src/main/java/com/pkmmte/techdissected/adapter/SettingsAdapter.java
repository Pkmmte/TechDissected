package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.SettingsItem;
import java.util.ArrayList;
import java.util.List;

public class SettingsAdapter extends BaseAdapter {
	// View Types
	public static final int TYPE_HEADER = 0;
	public static final int TYPE_DIVIDER = 1;
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_CHECKBOX = 3;
	public static final int TYPE_MAX_COUNT = 4;

	// Essential Resources
	private List<SettingsItem> mSettings;
	private LayoutInflater mInflater;

	public SettingsAdapter(Context context) {
		this(context, new ArrayList<SettingsItem>());
	}

	public SettingsAdapter(Context context, List<SettingsItem> settings) {
		this.mSettings = settings;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(SettingsItem setting) {
		mSettings.add(setting);
	}

	public void addHeader(String title) {
		mSettings.add(new SettingsItem(SettingsAdapter.TYPE_HEADER, title));
	}

	public void addDivider() {
		mSettings.add(new SettingsItem(SettingsAdapter.TYPE_DIVIDER));
	}

	public SettingsItem getSetting(int id) {
		for(SettingsItem setting : mSettings) {
			if(setting.getID() == id)
				return setting;
		}

		return null;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != TYPE_HEADER && getItemViewType(position) != TYPE_DIVIDER;
	}

	@Override
	public int getItemViewType(int position) {
		return mSettings.get(position).getType();
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return mSettings.size();
	}

	@Override
	public SettingsItem getItem(int position) {
		return mSettings.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		SettingsItem mSetting = mSettings.get(position);
		int type = getItemViewType(position);

		if(convertView == null) {
			holder = new ViewHolder();

			switch(type) {
				case TYPE_HEADER:
					convertView = mInflater.inflate(R.layout.settings_header, parent, false);
					holder.txtTitle = (TextView) convertView.findViewById(R.id.txtHeader);
					holder.txtDescription = null;
					holder.imgCheckbox = null;
					break;
				case TYPE_DIVIDER:
					convertView = mInflater.inflate(R.layout.settings_divider, parent, false);
					holder.txtTitle = null;
					holder.txtDescription = null;
					holder.imgCheckbox = null;
					break;
				case TYPE_TEXT:
					convertView = mInflater.inflate(R.layout.settings_text, parent, false);
					holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
					holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
					holder.imgCheckbox = null;
					break;
				case TYPE_CHECKBOX:
					convertView = mInflater.inflate(R.layout.settings_checkbox, parent, false);
					holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
					holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
					holder.imgCheckbox = (ImageView) convertView.findViewById(R.id.imgCheckbox);
					break;
			}

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Don't edit anything for dividers
		if(type == TYPE_DIVIDER)
			return convertView;

		// Apply title
		holder.txtTitle.setText(mSetting.getTitle());

		// Return prematurely if type is header
		if(type == TYPE_HEADER)
			return convertView;

		// Apply description
		holder.txtDescription.setText(mSetting.getDescription());

		// Set checkbox state if applicable
		if(type == TYPE_CHECKBOX && holder.imgCheckbox != null)
			holder.imgCheckbox.setImageResource(mSetting.isSelected() ? R.drawable.ic_checkbox_on : R.drawable.ic_checkbox_off);

		// Return this row view
		return convertView;
	}

	private class ViewHolder {
		public TextView txtTitle;
		public TextView txtDescription;
		public ImageView imgCheckbox;
	}
}