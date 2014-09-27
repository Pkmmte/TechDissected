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
import com.pkmmte.techdissected.model.SettingsItem;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.Dialogs;
import com.pkmmte.techdissected.util.Utils;

public class SettingsFragment extends ListFragment
{
	// ID Keys
	private final int STORAGE_CACHE = 0;
	private final int READ_GRAYSCALE = 1;
	private final int CREDITS_LIBRARIES = 2;
	private final int CREDITS_DEV = 3;

	// App Preferences
	private SharedPreferences mPrefs;

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
		getListView().setSelector(R.drawable.selector_transparent_lgray);
		getListView().setDrawSelectorOnTop(false);

		// Calculate overall cache size asynchronously
		calculateCacheAsync();
	}

	private void addSettings()
	{
		mAdapter = new SettingsAdapter(getActivity());

		mAdapter.addHeader("Storage");
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title(getString(R.string.clear_cache))
			                 .description(getString(R.string.clear_cache_description))
			                 .id(STORAGE_CACHE)
			                 .build());

		mAdapter.addHeader("Reading");
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_CHECKBOX)
			                 .title("Grayscale Read")
			                 .description("Mark articles as read by grayscaling their preview images")
			                 .selected(mPrefs.getBoolean(Constants.PREF_READ, false))
			                 .id(READ_GRAYSCALE)
			                 .build());

		mAdapter.addHeader("Credits");
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title(getString(R.string.libraries))
			                 .description("Libraries used in this app")
			                 .id(CREDITS_LIBRARIES)
			                 .build());
		mAdapter.addDivider();
		mAdapter.addItem(new SettingsItem.Builder()
			                 .type(SettingsAdapter.TYPE_TEXT)
			                 .title("About Dev")
			                 .description("The guy responsible for making this app")
			                 .id(CREDITS_DEV)
			                 .build());
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id)
	{
		SettingsItem mSetting = mAdapter.getItem(position);

		switch(mSetting.getID()) {
			case STORAGE_CACHE:
				boolean success = Utils.clearCache(getActivity());
				Toast.makeText(getActivity(), success ? getString(R.string.cache_success) : getString(R.string.cache_fail), Toast.LENGTH_SHORT).show();
				calculateCacheAsync();
				break;
			case READ_GRAYSCALE:
				// Update preference
				boolean grayscaleRead = mPrefs.getBoolean(Constants.PREF_READ, false);
				Editor mEditor = mPrefs.edit();
				mEditor.putBoolean(Constants.PREF_READ, !grayscaleRead);
				mEditor.commit();

				// Update setting in list
				mAdapter.getSetting(READ_GRAYSCALE).setSelected(!grayscaleRead);
				mAdapter.notifyDataSetChanged();
				break;
			case CREDITS_DEV:
				Dialogs.getAboutDialog(getActivity()).show();
				break;
			case CREDITS_LIBRARIES:
				Dialogs.getCreditsLibraryDialog(getActivity()).show();
				break;
		}
	}

	private void calculateCacheAsync()
	{
		new AsyncTask<Void, Void, Void>() {
			private String cacheSize = "Unknown";

			@Override
			protected void onPreExecute() {
				mAdapter.getSetting(STORAGE_CACHE).setDescription(getString(R.string.clear_cache_description) + "\n" + getString(R.string.calculating));
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
					mAdapter.getSetting(STORAGE_CACHE).setDescription(getString(R.string.clear_cache_description) + "\n" + cacheSize);
					mAdapter.notifyDataSetChanged();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}