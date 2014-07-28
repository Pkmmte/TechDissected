package com.pkmmte.techdissected.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.model.Category;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import com.pkmmte.techdissected.view.HeaderGridView;
import java.util.ArrayList;
import java.util.List;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class FeedFragment extends Fragment implements FeedAdapter.OnArticleClickListener, OnRefreshListener, RSSManager.Callback {
	// Arguments Key
	protected static final String KEY_CATEGORY = "CATEGORY";

	// Feed Category
	private Category category;

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
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		mAdapter = new FeedAdapter(getActivity());
		mGrid.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		retrieveCategory();

		//
		ActionBarPullToRefresh.from(getActivity())
			.allChildrenArePullable()
			.listener(this)
			.setup(mPullToRefreshLayout);

		mFeed = RSSManager.with(getActivity()).get(category.getUrl());
		initFeed();
	}

	private void initViews(View v) {
		mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
		mGrid = (HeaderGridView) v.findViewById(R.id.feedGrid);
		noContent = v.findViewById(R.id.noContent);
	}

	private void retrieveCategory() {
		Bundle bundle = getArguments();
		category = bundle.getParcelable(KEY_CATEGORY);
	}

	private void initFeed() {
		if(mFeed != null && mFeed.size() > 0)
			refreshFeedContent();
		else
			RSSManager.with(getActivity()).load(category.getUrl()).callback(this).async();
	}

	private void refreshFeedContent() {
		noContent.setVisibility(View.GONE);
		mGrid.setVisibility(View.VISIBLE);

		if(mAdapter == null)
			mAdapter = new FeedAdapter(getActivity(), mFeed);
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
						RSSManager.with(getActivity()).load(
							category.getUrl()).nextPage().callback(FeedFragment.this).async();
						preLast = lastItem;
					}
				}
			}
		});
	}

	@Override
	public void onClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(Constants.KEY_ARTICLE_ID, article.getId());
		intent.putExtra(Constants.KEY_FEED_URL, category.getUrl());
		startActivity(intent);
	}

	@Override
	public void onRefreshStarted(View view) {
		RSSManager.with(getActivity()).load(category.getUrl()).callback(this).async();
	}

	@Override
	public void postParse(List<Article> articleList) {
		//mFeed = articleList;
		mFeed = RSSManager.with(getActivity()).get(category.getUrl());
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mPullToRefreshLayout.setRefreshComplete();
				refreshFeedContent();
			}
		});
		// TODO Add manager support for callbacks on UI thread (Builder feature)
	}

	public static FeedFragment newInstance(Category category)
	{
		FeedFragment mFragment = new FeedFragment();
		Bundle args = new Bundle();
		args.putParcelable(KEY_CATEGORY, category);
		mFragment.setArguments(args);
		return mFragment;
	}
}