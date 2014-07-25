package com.pkmmte.techdissected.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
	OnRefreshListener {
	private List<Article> mFeed = new ArrayList<Article>();
	private View noContent;
	private HeaderGridView mGrid;
	private FeedAdapter mAdapter;
	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		mAdapter = new FeedAdapter(getActivity());
		mGrid.setAdapter(mAdapter);
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
		initFeed(false);
	}

	private void initFeed(boolean next) {
		if(mFeed != null && mFeed.size() > 0 && !next)
			refreshFeedContent();
		else {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					RSSManager.with(getActivity()).parseNext(Constants.HOME_FEED);
					return null;
				}

				@Override
				protected void onPostExecute(Void p) {
					mFeed = RSSManager.with(getActivity()).get();
					refreshFeedContent();
				}
			}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
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
			int currentScrollState = 0;
			int preLast = 0;

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				this.currentVisibleItemCount = visibleItemCount;
				final int lastItem = firstVisibleItem + visibleItemCount;
				if(lastItem == totalItemCount - 1) {
					if(preLast!=lastItem){ //to avoid multiple calls for last item
						Log.e("Last", "Last");
						initFeed(true);
						preLast = lastItem;
					}
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				this.currentScrollState = scrollState;
				//this.isScrollCompleted();
			}

			private void isScrollCompleted()
			{
				if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE && !RSSManager.with(getActivity()).isParsing())
					initFeed(true);
			}
		});
	}

	@Override
	public void onClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(Constants.ARTICLE_ID, article.getId());
		startActivity(intent);
		//Toast.makeText(getActivity(), "" + RSSManager.with(getActivity()).get(article.getId()).getId(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCategoryClick(String category) {

	}

	@Override
	public void onRefreshStarted(View view) {

	}
}