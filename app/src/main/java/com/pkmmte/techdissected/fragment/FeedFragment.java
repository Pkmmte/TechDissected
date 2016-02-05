package com.pkmmte.techdissected.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class FeedFragment extends Fragment implements Callback, PkSwipeRefreshLayout.OnRefreshListener, FeedAdapter.ArticleListener, SwipeRefreshLayout.OnRefreshListener {
	// Passed Arguments
	private Category category;
	private String search;

	// Feed list & adapter
	private List<Article> mFeed = new ArrayList<>();
	private final FeedAdapter mFeedAdapter = new FeedAdapter(this);

	// Views
	private SwipeRefreshLayout swipeLayout;
	private RecyclerView mFeedList;
	private View noContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		retrieveArguments();
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		initList();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		//
		initFeed();
	}

	/*@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
	}*/

	public void notifyDataSetChanged() {
		mFeedAdapter.notifyDataSetChanged();
	}

	private void initViews(View v) {
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeLayout);
		mFeedList = (RecyclerView) v.findViewById(R.id.feedList);
		noContent = v.findViewById(R.id.noContent);
	}

	private void initList() {
		SharedPreferences preferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mFeedAdapter.setGrayscaleEnabled(preferences.getBoolean(Constants.PREF_READ, false));

		mFeedList.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.feed_columns)));
		mFeedList.setHasFixedSize(true);
		mFeedList.setAdapter(mFeedAdapter);

		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
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

	private void refreshFeedContent() {
		noContent.setVisibility(View.GONE);
		mFeedList.setVisibility(View.VISIBLE);

		mFeedAdapter.setArticles(mFeed);

		mFeedList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			private boolean loading;

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (loading || dy < 0)
					return;

				final LinearLayoutManager layoutManager = (LinearLayoutManager) mFeedList.getLayoutManager();
				final int childCount = layoutManager.getChildCount();
				final int itemCount = layoutManager.getItemCount();
				final int firstPosition = layoutManager.findFirstVisibleItemPosition();

				if (childCount + firstPosition >= itemCount) {
					loading = true;
					PkRSS.with(getActivity())
							.load(category.getUrl())
							.search(search)
							.nextPage()
							.callback(FeedFragment.this)
							.async();
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		PkRSS.with(getActivity())
				.load(category.getUrl())
				.search(search)
				.skipCache()
				.callback(this)
				.async();
	}

	@Override
	public void onPreload() {
		swipeLayout.setRefreshing(true);
	}

	@Override
	public void onLoaded(List<Article> newArticles) {
		mFeed = PkRSS.with(getActivity()).get(category.getUrl(), search);
		swipeLayout.setRefreshing(false);
		refreshFeedContent();
	}

	@Override
	public void onLoadFailed() {
		swipeLayout.setRefreshing(false);
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

	@Override
	public void onArticleClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(PkRSS.KEY_ARTICLE_ID, article.getId());
		intent.putExtra(PkRSS.KEY_CATEGORY_NAME, category.getName());
		intent.putExtra(PkRSS.KEY_FEED_URL, search == null ? category.getUrl() : category.getUrl() + "?s=" + Uri.encode(search));
		startActivity(intent);
	}

	@Override
	public void onArticleFavorite(Article article, boolean favorite) {
		//
	}
}