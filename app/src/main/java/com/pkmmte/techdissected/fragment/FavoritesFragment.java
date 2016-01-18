package com.pkmmte.techdissected.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FeedAdapter.ArticleListener, SwipeRefreshLayout.OnRefreshListener {
	// Feed list & adapter
	private List<Article> mFeed = new ArrayList<>();
	private FeedAdapter mFeedAdapter = new FeedAdapter(this);

	// Views
	private SwipeRefreshLayout mSwipeLayout;
	private RecyclerView mFeedList;
	private View noContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		initList();
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
			mFeedList.setVisibility(View.VISIBLE);
			noContent.setVisibility(View.GONE);
			mFeedAdapter.setArticles(mFeed);
			mFeedList.setAdapter(mFeedAdapter);
		}
		else {
			noContent.setVisibility(View.VISIBLE);
			mFeedList.setVisibility(View.GONE);
		}
	}

	private void initViews(View v) {
		mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeLayout);
		mFeedList = (RecyclerView) v.findViewById(R.id.feedList);
		noContent = v.findViewById(R.id.noContent);
		mFeedList.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	private void initList() {
		SharedPreferences preferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mFeedAdapter.setGrayscaleEnabled(preferences.getBoolean(Constants.PREF_READ, false));

		mFeedList.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.feed_columns)));
		mFeedList.setHasFixedSize(true);
		mFeedList.setAdapter(mFeedAdapter);
	}

	@Override
	public void onRefresh() {
		mSwipeLayout.setRefreshing(false);
		mFeed = PkRSS.with(getActivity()).getFavorites();
	}

	@Override
	public void onArticleClick(Article article) {
		Intent intent = new Intent(getActivity(), ArticleActivity.class);
		intent.putExtra(PkRSS.KEY_ARTICLE_ID, article.getId());
		intent.putExtra(PkRSS.KEY_CATEGORY_NAME, "Favorites");
		intent.putExtra(PkRSS.KEY_FEED_URL, PkRSS.KEY_FAVORITES);
		startActivity(intent);
	}

	@Override
	public void onArticleFavorite(Article article, boolean favorite) {
		//
	}
}