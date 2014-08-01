package com.pkmmte.techdissected.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.activity.SearchActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.util.Constants;
import java.util.ArrayList;
import java.util.List;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class FavoritesFragment extends Fragment implements FeedAdapter.OnArticleClickListener, OnRefreshListener {
	// Feed list & adapter
	private List<Article> mFeed = new ArrayList<Article>();
	private FeedAdapter mAdapter;

	// Views
	private PullToRefreshLayout mPullToRefreshLayout;
	private GridView mGrid;
	private View noContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();


		//
		ActionBarPullToRefresh.from(getActivity())
			.allChildrenArePullable()
			.listener(this)
			.setup(mPullToRefreshLayout);

		//
		mFeed = PkRSS.with(getActivity()).getFavorites();
		mGrid.setVisibility(View.VISIBLE);
		noContent.setVisibility(View.GONE);
		mAdapter = new FeedAdapter(getActivity(), mFeed);
		mAdapter.setOnClickListener(this);
		mGrid.setAdapter(mAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.feed, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				intent.putExtra(PkRSS.KEY_SEARCH, query);
				startActivity(intent);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh:
				mPullToRefreshLayout.setRefreshComplete();
				mFeed = PkRSS.with(getActivity()).getFavorites();
				return true;
			case R.id.action_website:
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.WEBSITE_URL)));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void toggleActionItems(Menu menu, boolean drawerOpen) {
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
		menu.findItem(R.id.action_website).setVisible(!drawerOpen);
	}

	private void initViews(View v) {
		mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
		mGrid = (GridView) v.findViewById(R.id.feedGrid);
		noContent = v.findViewById(R.id.noContent);
	}

	@Override
	public void onClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(PkRSS.KEY_ARTICLE_ID, article.getId());
		intent.putExtra(PkRSS.KEY_CATEGORY_NAME, "Favorites");
		intent.putExtra(PkRSS.KEY_FEED_URL, PkRSS.KEY_FAVORITES);
		startActivity(intent);
	}

	@Override
	public void onAddFavorite(Article article, boolean favorite) {
		//
	}

	@Override
	public void onRefreshStarted(View view) {
		mPullToRefreshLayout.setRefreshComplete();
		mFeed = PkRSS.with(getActivity()).getFavorites();
	}
}