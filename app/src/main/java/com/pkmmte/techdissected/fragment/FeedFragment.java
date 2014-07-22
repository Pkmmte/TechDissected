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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements FeedAdapter.OnArticleClickListener {
	private List<Article> mFeed = new ArrayList<Article>();
	private View noContent;
	private GridView mGrid;
	private FeedAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		initViews(view);
		mAdapter = new FeedAdapter(getActivity());
		return view;
	}

	private void initViews(View v) {
		noContent = v.findViewById(R.id.noContent);
		mGrid = (GridView) v.findViewById(R.id.feedGrid);
	}

	@Override
	public void onStart() {
		super.onStart();

		mFeed = RSSManager.with(getActivity()).get();
		mAdapter = new FeedAdapter(getActivity(), mFeed);
		mGrid.setAdapter(mAdapter);

		initFeed();
	}

	private void initFeed() {
		if(mFeed != null && mFeed.size() > 0) {
			if(mAdapter == null)
				mAdapter = new FeedAdapter(getActivity(), mFeed);
			else
				mAdapter.updateFeed(mFeed);
			mGrid.setAdapter(mAdapter);
			mAdapter.setOnClickListener(FeedFragment.this);
		}
		else {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {

					RSSManager.with(getActivity()).parse(Constants.HOME_FEED);
					return null;
				}

				@Override
				protected void onPostExecute(Void p) {
					noContent.setVisibility(View.GONE);
					mGrid.setVisibility(View.VISIBLE);

					mFeed = RSSManager.with(getActivity()).get();
					if(mAdapter == null)
						mAdapter = new FeedAdapter(getActivity(), mFeed);
					else
						mAdapter.updateFeed(mFeed);
					mGrid.setAdapter(mAdapter);
					mAdapter.setOnClickListener(FeedFragment.this);
				}
			}.execute();
		}
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
}