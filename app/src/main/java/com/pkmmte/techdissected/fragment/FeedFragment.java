package com.pkmmte.techdissected.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.RSSManager;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
	private List<Article> mFeed;
	private GridView mGrid;
	private FeedAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
		mGrid = (GridView) view.findViewById(R.id.feedGrid);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		generateTestFeed();
		mAdapter = new FeedAdapter(getActivity(), mFeed);
		mGrid.setAdapter(mAdapter);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Log.e("WTF", "Y U NO WORK?!");
				RSSManager.with(getActivity()).parse("http://techdissected.com/feed/");
				return null;
			}

			@Override
			protected void onPostExecute(Void p) {
				mAdapter = new FeedAdapter(getActivity(), RSSManager.with(getActivity()).get());
				mGrid.setAdapter(mAdapter);
			}
		}.execute();
	}

	private void generateTestFeed() {
		mFeed = new ArrayList<Article>();
		Article article = new Article();

		article.setImage(Uri.parse(
			"http://techdissected.com/wp-content/uploads/2014/07/351x185xsyntonic-featured-351x185.jpg,q6da9e4.pagespeed.ic.2mQV1AG6D3.jpg"));
		article.setTitle("Syntonic Makes AT&T Users Exempt From Data Charges");
		article.setDescription(
			"AT&T’s sponsored data program may have been a source of negativity at one time but now it’s working to consumer advantage thanks to Syntonic");
		article.setAuthor("Cliff Wade");
		mFeed.add(article);
		article = new Article();
		article.setTitle("Syntonic Makes AT&T Users Exempt From Data Charges");
		article.setDescription(
			"AT&T’s sponsored data program may have been a source of negativity at one time but now it’s working to consumer advantage thanks to Syntonic");
		article.setAuthor("Cliff Wade");
		mFeed.add(article);
		article = new Article();
		article.setImage(Uri.parse("http://www.bradleycorp.com/image/4505/Designer-White.jpg"));
		article.setTitle("Syntonic Makes AT&T Users Exempt From Data Charges");
		article.setDescription(
			"AT&T’s sponsored data program may have been a source of negativity at one time but now it’s working to consumer advantage thanks to Syntonic");
		article.setAuthor("Cliff Wade");
		mFeed.add(article);
		mFeed.add(article);
		mFeed.add(article);
	}
}