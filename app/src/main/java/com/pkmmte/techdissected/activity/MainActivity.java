package com.pkmmte.techdissected.activity;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.NavDrawerAdapter;
import com.pkmmte.techdissected.fragment.FeedFragment;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import com.pkmmte.techdissected.view.PkDrawerLayout;

public class MainActivity extends FragmentActivity {
	// Navigation Drawer
	private ActionBarDrawerToggle mDrawerToggle;
	private PkDrawerLayout mDrawerLayout;
	private NavDrawerAdapter mDrawerAdapter;
	private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RSSManager.with(this).setLoggingEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		initViews();
		initNavDrawer();

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.feedContainer, new FeedFragment()).commit();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				RSSManager.with(MainActivity.this).load(Constants.MAIN_FEED).search(query).async();
				Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if(id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initViews()
	{
		mDrawerLayout = (PkDrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
	}

	private void initNavDrawer() {
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_indicator, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerAdapter = new NavDrawerAdapter(this, Constants.CATEGORIES);

		// Add header
		View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_header, mDrawerList, false);
		mDrawerList.addHeaderView(headerView, null, false);

		// Set list adapter
		mDrawerList.setAdapter(mDrawerAdapter);
	}
}
