package com.pkmmte.techdissected.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.Category;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.activity.SearchActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.view.PkSwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements FeedAdapter.OnArticleClickListener, Callback, PkSwipeRefreshLayout.OnRefreshListener {
	// Passed Arguments
	private Category category;
	private String search;

	// Feed list & adapter
	private List<Article> mFeed = new ArrayList<Article>();
	private FeedAdapter mAdapter;

	// Views
	private MenuItem refreshItem;
	private PkSwipeRefreshLayout mSwipeLayout;
	private GridView mGrid;
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

		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.action_swipe_1, R.color.action_swipe_2,
		                                     R.color.action_swipe_3, R.color.action_swipe_4);

		//
		initFeed();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.feed, menu);

		refreshItem = menu.findItem(R.id.action_refresh);
		if(mSwipeLayout.isRefreshing())
			refreshItem.setActionView(R.layout.refresh);

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
				PkRSS.with(getActivity()).load(category.getUrl()).search(search).skipCache().callback(this).async();
				return true;
			case R.id.action_read:
				PkRSS.with(getActivity()).markAllRead(true);
				mAdapter.notifyDataSetChanged();
				return true;
			case R.id.action_unfavorite:
				new AlertDialog.Builder(getActivity())
					.setTitle("Confirm Delete")
					.setMessage("Do you really want to delete all articles marked as favorite?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							PkRSS.with(getActivity()).deleteAllFavorites();
							mAdapter.notifyDataSetChanged();
						}})
					.setNegativeButton(android.R.string.no, null).show();
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
		menu.findItem(R.id.action_read).setVisible(!drawerOpen);
		menu.findItem(R.id.action_unfavorite).setVisible(!drawerOpen);
		menu.findItem(R.id.action_website).setVisible(!drawerOpen);
	}

	private void initViews(View v) {
		mSwipeLayout = (PkSwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
		mGrid = (GridView) v.findViewById(R.id.feedGrid);
		noContent = v.findViewById(R.id.noContent);
	}

	private void retrieveArguments() {
		Bundle bundle = getArguments();
		category = bundle.getParcelable(PkRSS.KEY_CATEGORY);
		search = bundle.getString(PkRSS.KEY_SEARCH);
	}

	private void initFeed() {
		if(mFeed != null && mFeed.size() > 0)
			refreshFeedContent();
		else {
			PkRSS.with(getActivity()).load(category.getUrl()).search(search).callback(this).async();
		}
	}

	private void setRefreshState(boolean refreshing) {
		// Toggle action bar progress
		mSwipeLayout.setRefreshing(refreshing);

		// I would use a ternary operator for this but it causes a NullPointerException for some reason...
		if(refreshItem != null && refreshing)
			refreshItem.setActionView(R.layout.refresh);
		else if(refreshItem != null)
			refreshItem.setActionView(null);
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
		mSwipeLayout.setScrollTarget(mGrid);

		mGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
			int currentVisibleItemCount = 0;
			int preLast = 0;

			@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				this.currentVisibleItemCount = visibleItemCount;
				final int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount - 1) {
					if (preLast != lastItem) { //to avoid multiple calls for last item
						PkRSS.with(getActivity())
							.load(category.getUrl())
							.search(search)
							.nextPage()
							.callback(FeedFragment.this)
							.async();
						preLast = lastItem;
					}
				}
			}
		});
	}

	@Override
	public void onClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(PkRSS.KEY_ARTICLE_ID, article.getId());
		intent.putExtra(PkRSS.KEY_CATEGORY_NAME, category.getName());
		intent.putExtra(PkRSS.KEY_FEED_URL, search == null ? category.getUrl() : category.getUrl() + "?s=" + Uri.encode(search));
		startActivity(intent);
	}

	@Override
	public void onAddFavorite(Article article, boolean favorite) {
		//
	}

	@Override
	public void onRefresh() {
		PkRSS.with(getActivity()).load(category.getUrl()).search(search).skipCache().callback(this).async();
	}

	@Override
	public void OnPreLoad() {
		setRefreshState(true);
	}

	@Override
	public void OnLoaded(List<Article> newArticles) {
		mFeed = PkRSS.with(getActivity()).get(category.getUrl(), search);
		setRefreshState(false);
		refreshFeedContent();
	}

	@Override
	public void OnLoadFailed() {
		setRefreshState(false);
		Toast.makeText(getActivity(), "Error loading feed. Check your internet connection and try again.", Toast.LENGTH_SHORT).show();
	}

	public static FeedFragment newInstance(Category category) {
		return newInstance(category, null);
	}

	public static FeedFragment newInstance(Category category, String search) {
		FeedFragment mFragment = new FeedFragment();
		Bundle args = new Bundle();
		args.putParcelable(PkRSS.KEY_CATEGORY, category == null ? Constants.DEFAULT_CATEGORY : category);
		args.putString(PkRSS.KEY_SEARCH, search);
		mFragment.setArguments(args);
		return mFragment;
	}
}