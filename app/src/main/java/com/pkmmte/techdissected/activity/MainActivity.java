package com.pkmmte.techdissected.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.pkmmte.pkrss.Category;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.NavDrawerAdapter;
import com.pkmmte.techdissected.fragment.FavoritesFragment;
import com.pkmmte.techdissected.fragment.FeedFragment;
import com.pkmmte.techdissected.fragment.SettingsFragment;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.view.PkDrawerLayout;

public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
	// Action Bar
	private ActionBar actionBar;
	private String actionBarSubtitle;

	// Navigation Drawer
	private ActionBarDrawerToggle mDrawerToggle;
	private PkDrawerLayout mDrawerLayout;
	private NavDrawerAdapter mDrawerAdapter;
	private ListView mDrawerList;

	// Manager & Current Fragment
	private FragmentManager fragmentManager;
	private FeedFragment currentCategory;

	private boolean contentLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// [DEBUG] Enable logging for debugging purposes
		PkRSS.with(this).setLoggingEnabled(true);

		// Initialize basics
		initActionBar();
		initViews();
		initNavDrawer();
		fragmentManager = getSupportFragmentManager();

		if(handleIntent())
			return;

		// Select default category
		selectCategory(0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(!contentLoaded)
			selectCategory(0);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try {
			if(currentCategory != null)
				currentCategory.toggleActionItems(menu, mDrawerLayout.isDrawerOpen(mDrawerList));
		} catch (Exception e) {}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setSubtitle(actionBarSubtitle);
	}

	private void initViews() {
		mDrawerLayout = (PkDrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
	}

	private void initNavDrawer() {
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_indicator, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				actionBar.setSubtitle(actionBarSubtitle);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setSubtitle(null);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

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
		(headerView.findViewById(R.id.btnFavorites)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer,
				                                           new FavoritesFragment()).commit();

				actionBarSubtitle = "Favorites";
				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
		(headerView.findViewById(R.id.btnSettings)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new SettingsFragment()).commit();

				actionBarSubtitle = "Settings";
				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
	}

	protected void selectCategory(int position) {
		contentLoaded = true;
		Category category = mDrawerAdapter.getItem(position);
		actionBarSubtitle = category.getName();
		actionBar.setSubtitle(actionBarSubtitle);

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
			Toast.makeText(this, "Loaded from data: \n" + launchIntent.getDataString(), Toast.LENGTH_SHORT).show();

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
}