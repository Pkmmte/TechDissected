package com.pkmmte.techdissected.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.FeedAdapter;
import com.pkmmte.techdissected.model.Article;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
	private List<Article> mFeed;
	private GridView mGrid;

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
		FeedAdapter adapter = new FeedAdapter(getActivity(), mFeed);
		mGrid.setAdapter(adapter);
	}

	private void generateTestFeed() {
		mFeed = new ArrayList<Article>();
		mFeed.add(new Article(Uri.parse("http://techdissected.com/wp-content/uploads/2014/07/Free-Alternatives-351x185.jpg"), "Syntonic Makes AT&T Users Exempt From Data Charges", "AT&T’s sponsored data program may have been a source of negativity at one time but now it’s working to consumer advantage thanks to Syntonic", "Mr. Banana", 0));
		mFeed.add(new Article(null, "Test", "Haiii", "Mr. Banana", 0));
		mFeed.add(new Article(null, "Test", "Haiii", "Mr. Banana", 0));
		mFeed.add(new Article(null, "Test", "Haiii", "Mr. Banana", 0));
	}
}