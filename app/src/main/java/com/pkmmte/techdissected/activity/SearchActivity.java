package com.pkmmte.techdissected.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.fragment.FeedFragment;
import com.pkmmte.pkrss.Category;
import com.pkmmte.pkrss.PkRSS;

public class SearchActivity extends FragmentActivity {
	private Category category;
	private String search;

	private ActionBar actionBar;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		retrieveArguments();
		initActionBar();

		fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.searchContent, FeedFragment.newInstance(category, search)).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				search = query;
				actionBar.setSubtitle(search);
				fragmentManager.beginTransaction().replace(R.id.searchContent, FeedFragment.newInstance(category, search)).commit();
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
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void retrieveArguments() {
		category = getIntent().getParcelableExtra(PkRSS.KEY_CATEGORY);
		search = getIntent().getStringExtra(PkRSS.KEY_SEARCH);
	}

	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setSubtitle(search);
	}
}