package com.pkmmte.techdissected.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import com.pkmmte.techdissected.view.HeaderGridView;
import java.util.ArrayList;
import java.util.List;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class FeedFragment extends Fragment implements FeedAdapter.OnArticleClickListener,
	OnRefreshListener, RSSManager.Callback {
	private List<Article> mFeed = new ArrayList<Article>();
	private View noContent;
	private HeaderGridView mGrid;
	private FeedAdapter mAdapter;
	private PullToRefreshLayout mPullToRefreshLayout;
	private Handler mHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		mAdapter = new FeedAdapter(getActivity());
		mGrid.setAdapter(mAdapter);
		mHandler = new Handler();
		return view;
	}

	private void initViews(View v) {
		mPullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.ptr_layout);
		noContent = v.findViewById(R.id.noContent);
		mGrid = (HeaderGridView) v.findViewById(R.id.feedGrid);
	}

	@Override
	public void onStart() {
		super.onStart();

		ActionBarPullToRefresh.from(getActivity())
			.allChildrenArePullable()
			.listener(this)
			.setup(mPullToRefreshLayout);

		mFeed = RSSManager.with(getActivity()).get();
		initFeed();
	}

	private void initFeed() {
		if(mFeed != null && mFeed.size() > 0)
			refreshFeedContent();
		else
			RSSManager.with(getActivity()).parseNext(Constants.HOME_FEED).callback(this).async();
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
						RSSManager.with(getActivity()).parseNext(Constants.HOME_FEED).callback(FeedFragment.this).async();
						preLast = lastItem;
					}
				}
			}
		});
	}

	@Override
	public void onClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(Constants.ARTICLE_ID, article.getId());
		startActivity(intent);
	}

	@Override
	public void onRefreshStarted(View view) {
		RSSManager.with(getActivity()).parse(Constants.HOME_FEED).callback(this).async();
	}

	@Override
	public void postParse(List<Article> articleList) {
		mFeed = articleList;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mPullToRefreshLayout.setRefreshComplete();
				refreshFeedContent();
			}
		});
		// TODO Add manager support for callbacks on UI thread (Builder feature)
	}
}