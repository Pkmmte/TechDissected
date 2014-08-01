package com.pkmmte.techdissected.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.SettingsAdapter;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.SettingsItem;
import com.pkmmte.techdissected.util.Utils;

public class SettingsFragment extends ListFragment
{
	// ID Keys
	private final int WALLPAPERS_CLOUD = 0;
	private final int WALLPAPERS_STORAGE = 1;
	private final int MISC_CACHE = 2;
	private final int MISC_ICON = 3;
	private final int CREDITS_PEOPLE = 4;
	private final int CREDITS_LIBRARIES = 5;

	// App Preferences
	private SharedPreferences mPrefs;
	private final String KEY_WALLPAPERS_CLOUD = "Wallpapers Cloud";
	private final String KEY_LAUNCHER_ICON = "Launcher Icon";

	// List Adapter
	private SettingsAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mPrefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
		addSettings();
		setListAdapter(mAdapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// Reinitialize our preferences if they somehow became null
		if(mPrefs == null)
			mPrefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

		// We have a custom divider so let's disable this
		getListView().setDivider(null);
		getListView().setDividerHeight(0);

		// Apply custom selector
		//getListView().setSelector(R.drawable.selector_transparent_lgray);
		getListView().setDrawSelectorOnTop(false);

		// Calculate overall cache size asynchronously
		calculateCacheAsync();
	}

	private void addSettings()
	{
		mAdapter = new SettingsAdapter(getActivity());

		mAdapter.addHeader("wallpapers");
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_CHECKBOX)
			                 .title(getString(R.string.cloud_wallpapers))
			                 .description(getString(R.string.cloud_wallpapers_description))
			                 .selected(mPrefs.getBoolean(KEY_WALLPAPERS_CLOUD, true))
			                 .id(WALLPAPERS_CLOUD)
			                 .build());
		mAdapter.addDivider();
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title(getString(R.string.save_location))
			                 .description(getString(R.string.save_location_description))
			                 .id(WALLPAPERS_STORAGE)
			                 .build());

		mAdapter.addHeader("miscellaneous");
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title(getString(R.string.clear_cache))
			                 .description(getString(R.string.clear_cache_description))
			                 .id(MISC_CACHE)
			                 .build());
		mAdapter.addDivider();
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_CHECKBOX)
			                 .title(getString(R.string.toggle_launcher_icon))
			                 .description(getString(R.string.toggle_launcher_icon_description))
			                 .selected(mPrefs.getBoolean(KEY_LAUNCHER_ICON, true))
			                 .id(MISC_ICON)
			                 .build());

		mAdapter.addHeader("credits");
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title(getString(R.string.people))
			                 .description(getString(R.string.people_description))
			                 .id(CREDITS_PEOPLE)
			                 .build());
		mAdapter.addDivider();
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title(getString(R.string.libraries))
			                 .description(getString(R.string.libraries_description))
			                 .id(CREDITS_LIBRARIES)
			                 .build());
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id)
	{
		SettingsItem mSetting = mAdapter.getItem(position);

		switch(mSetting.getID()) {
			case WALLPAPERS_CLOUD:
				// TODO
				break;
			case WALLPAPERS_STORAGE:
				// TODO
				break;
			case MISC_CACHE:
				// TODO
				break;
			case MISC_ICON:
				// TODO
				break;
			case CREDITS_PEOPLE:
				// TODO
				break;
			case CREDITS_LIBRARIES:
				// TODO
				break;
		}
	}

	private void calculateCacheAsync()
	{
		new AsyncTask<Void, Void, Void>() {
			private String cacheSize = "Unknown";

			@Override
			protected void onPreExecute() {
				mAdapter.getSetting(MISC_CACHE).setDescription(getString(R.string.clear_cache_description) + "\n" + getString(R.string.calculating));
				mAdapter.notifyDataSetChanged();
			}

			@Override
			protected Void doInBackground(Void... params) {
				cacheSize = Utils.humanReadableByteCount(Utils.getTotalCacheSize(getActivity()), true);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				try {
					mAdapter.getSetting(MISC_CACHE).setDescription(getString(R.string.clear_cache_description) + "\n" + cacheSize);
					mAdapter.notifyDataSetChanged();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}