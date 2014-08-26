package com.pkmmte.techdissected.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.view.PkSwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FeedAdapter.OnArticleClickListener, PkSwipeRefreshLayout.OnRefreshListener {
	// Feed list & adapter
	private List<Article> mFeed = new ArrayList<Article>();
	private FeedAdapter mAdapter;

	// Views
	private PkSwipeRefreshLayout mSwipeLayout;
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
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.action_swipe_1, R.color.action_swipe_2,
		                                     R.color.action_swipe_3, R.color.action_swipe_4);

		//
		mFeed = PkRSS.with(getActivity()).getFavorites();

		if(mFeed.size() > 0) {
			mGrid.setVisibility(View.VISIBLE);
			noContent.setVisibility(View.GONE);
			mAdapter = new FeedAdapter(getActivity(), mFeed);
			mAdapter.setOnClickListener(this);
			mGrid.setAdapter(mAdapter);
			mSwipeLayout.setScrollTarget(mGrid);
		}
		else {
			noContent.setVisibility(View.VISIBLE);
			mGrid.setVisibility(View.GONE);
		}
	}

	private void initViews(View v) {
		mSwipeLayout = (PkSwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
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
	public void onRefresh() {
		mSwipeLayout.setRefreshing(false);
		mFeed = PkRSS.with(getActivity()).getFavorites();
	}
}