package com.pkmmte.techdissected.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SearchView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.activity.SearchActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.model.Category;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.PkRSS;
import com.pkmmte.techdissected.view.HeaderGridView;
import java.util.ArrayList;
import java.util.List;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class FeedFragment extends Fragment implements FeedAdapter.OnArticleClickListener, OnRefreshListener, PkRSS.Callback {
	// Passed Arguments
	private Category category;
	private String search;

	// Handler to modify views from background threads
	private Handler mHandler = new Handler(Looper.getMainLooper());

	// Feed list & adapter
	private List<Article> mFeed = new ArrayList<Article>();
	private FeedAdapter mAdapter;

	// Views
	private PullToRefreshLayout mPullToRefreshLayout;
	private HeaderGridView mGrid;
	private View noContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		retrieveArguments();
		if(search == null)
			setHasOptionsMenu(true);
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

		// TODO
		//mFeed = RSSManager.with(getActivity()).get(category.getUrl());
		initFeed();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.main, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				intent.putExtra(SearchActivity.KEY_SEARCH, query);
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
				refreshFeed();
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
		mGrid = (HeaderGridView) v.findViewById(R.id.feedGrid);
		noContent = v.findViewById(R.id.noContent);
	}

	private void retrieveArguments() {
		Bundle bundle = getArguments();
		category = bundle.getParcelable(Constants.KEY_CATEGORY);
		search = bundle.getString(SearchActivity.KEY_SEARCH);
	}

	private void initFeed() {
		if(mFeed != null && mFeed.size() > 0)
			refreshFeedContent();
		else {
			mPullToRefreshLayout.setRefreshing(true);
			PkRSS.with(getActivity()).load(category.getUrl()).search(search).callback(this).async();
		}
	}

	private void refreshFeedContent() {
		noContent.setVisibility(View.GONE);
		mGrid.setVisibility(View.VISIBLE);

		if(mAdapter == null) {
			mAdapter = new FeedAdapter(getActivity(), mFeed);
			mGrid.setAdapter(mAdapter);
		}
		else
			mAdapter.updateFeed(mFeed);

		mAdapter.setOnClickListener(FeedFragment.this);

		mGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
			int currentVisibleItemCount = 0;
			int preLast = 0;

			@Override public void onScrollStateChanged(AbsListView view, int scrollState) {}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				this.currentVisibleItemCount = visibleItemCount;
				final int lastItem = firstVisibleItem + visibleItemCount;
				if(lastItem == totalItemCount - 1) {
					if(preLast != lastItem){ //to avoid multiple calls for last item
						mPullToRefreshLayout.setRefreshing(true);
						PkRSS.with(getActivity()).load(category.getUrl()).search(search).nextPage().callback(FeedFragment.this).async();
						preLast = lastItem;
					}
				}
			}
		});
	}

	private void refreshFeed() {
		PkRSS.with(getActivity()).load(category.getUrl()).search(search).skipCache().callback(this).async();
	}

	@Override
	public void onClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(Constants.KEY_ARTICLE_ID, article.getId());
		intent.putExtra(Constants.KEY_CATEGORY_NAME, category.getName());
		intent.putExtra(Constants.KEY_FEED_URL, search == null ? category.getUrl() : category.getUrl() + "?s=" + search);
		startActivity(intent);
	}

	@Override
	public void onRefreshStarted(View view) {
		refreshFeed();
	}

	@Override
	public void postParse(List<Article> articleList) {
		//mFeed = articleList;
		mFeed = PkRSS.with(getActivity()).get(category.getUrl(), search);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mPullToRefreshLayout.setRefreshComplete();
				refreshFeedContent();
			}
		});
		// TODO Add manager support for callbacks on UI thread (Builder feature)
	}

	public static FeedFragment newInstance(Category category) {
		return newInstance(category, null);
	}

	public static FeedFragment newInstance(Category category, String search) {
		FeedFragment mFragment = new FeedFragment();
		Bundle args = new Bundle();
		args.putParcelable(Constants.KEY_CATEGORY, category == null ? Constants.DEFAULT_CATEGORY : category);
		args.putString(Constants.KEY_SEARCH, search);
		mFragment.setArguments(args);
		return mFragment;
	}
}