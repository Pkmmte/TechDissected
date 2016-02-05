package com.pkmmte.techdissected.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pkmmte.pkrss.Category;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.NavDrawerAdapter;
import com.pkmmte.techdissected.fragment.AboutFragment;
import com.pkmmte.techdissected.fragment.FavoritesFragment;
import com.pkmmte.techdissected.fragment.FeedFragment;
import com.pkmmte.techdissected.fragment.SettingsFragment;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.Utils;
import com.pkmmte.techdissected.view.PkDrawerLayout;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Toolbar.OnMenuItemClickListener {
	// For logging purposes
	private static final String TAG = MainActivity.class.getSimpleName();

	// Navigation Drawer
	private PkDrawerLayout mDrawerLayout;
	private NavDrawerAdapter mDrawerAdapter;
	private ListView mDrawerList;

	// Manager & Current Fragment
	private FragmentManager fragmentManager;
	private FeedFragment currentCategory;

	// Loaded status
	private boolean contentLoaded = false;

	// Views
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Build Custom Singleton
		Utils.buildSingleton(this);

		// [DEBUG] Enable logging for debugging purposes
		PkRSS.with(this).setLoggingEnabled(true);

		// Initialize basics
		initViews();
		initToolbar();
		initNavDrawer();
		fragmentManager = getSupportFragmentManager();

		if(handleIntent())
			return;

		// Select default category
		selectCategory(0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(!contentLoaded)
			selectCategory(0);
	}

	private void initViews() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		mDrawerLayout = (PkDrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
	}

	private void initToolbar() {
		toolbar.inflateMenu(R.menu.feed);
		toolbar.setOnMenuItemClickListener(this);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
		});
	}

	private void initNavDrawer() {
		mDrawerAdapter = new NavDrawerAdapter(this, Constants.CATEGORIES);

		// Add header
		View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_header, mDrawerList, false);
		mDrawerList.addHeaderView(headerView, null, false);

		// Add footer
		View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_footer, mDrawerList, false);
		mDrawerList.addFooterView(footerView, null, false);

		// Set list adapter
		mDrawerList.setAdapter(mDrawerAdapter);

		// Set OnClick Listeners
		mDrawerList.setOnItemClickListener(this);
		headerView.findViewById(R.id.btnFavorites).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new FavoritesFragment()).commit();

				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
		headerView.findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new SettingsFragment()).commit();

				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
		footerView.findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new AboutFragment()).commit();

				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
	}

	protected void selectCategory(int position) {
		contentLoaded = true;
		Category category = mDrawerAdapter.getItem(position);

		currentCategory = FeedFragment.newInstance(category);
		fragmentManager.beginTransaction().replace(R.id.feedContainer, currentCategory).commit();

		mDrawerAdapter.setCurrentPage(position);
		mDrawerLayout.closeDrawers();
	}

	protected boolean handleIntent() {
		// Get the launch intent
		final Intent launchIntent = getIntent();

		// Return false if intent is invalid
		if(launchIntent == null || launchIntent.getAction() == null)
			return false;

		// Handle intent appropriately
		if (launchIntent.getAction().equals(Intent.ACTION_VIEW)) {
			// Create ArticleActivity intent containing the received URL String
			Intent intent = new Intent(this, ArticleActivity.class);
			intent.putExtra(PkRSS.KEY_ARTICLE_URL, launchIntent.getDataString());

			// Clear launch intent action & start article activity
			setIntent(launchIntent.setAction(null));
			startActivity(intent);

			// Return true to indicate we handled it
			return true;
		}

		// Nothing was done, return false
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectCategory(position - mDrawerList.getHeaderViewsCount());
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				// TODO
				return true;
			case R.id.action_read:
				PkRSS.with(this).markAllRead(true);
				if (currentCategory != null)
					currentCategory.notifyDataSetChanged();
				return true;
			case R.id.action_unfavorite:
				new AlertDialog.Builder(this)
						.setTitle("Confirm Delete")
						.setMessage("Do you really want to delete all articles marked as favorite?")
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								PkRSS.with(MainActivity.this).deleteAllFavorites();
								if (currentCategory != null)
									currentCategory.notifyDataSetChanged();
							}})
						.setNegativeButton(android.R.string.no, null).show();
				return true;
			case R.id.action_website:
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.WEBSITE_URL)));
				return true;
			default:
				return false;
		}
	}
}