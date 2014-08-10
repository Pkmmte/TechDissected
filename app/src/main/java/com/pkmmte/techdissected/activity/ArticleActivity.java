package com.pkmmte.techdissected.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.fragment.ArticleFragment;
import com.pkmmte.techdissected.view.PkSwipeRefreshLayout;
import java.util.List;

public class ArticleActivity extends FragmentActivity implements PkSwipeRefreshLayout.OnRefreshListener {
	// Argument Variables
	private String categoryName = null;
	private String feedUrl = null;
	private String articleUrl = null;
	private int articleId = -1;

	// Reference to current article
	private Article currentArticle = null;

	// Fragment Manager
	private FragmentManager fragmentManager;

	// Views
	private PkSwipeRefreshLayout mSwipeLayout;
	private View noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_article);

		retrieveArguments();
	    initViews();
	    mSwipeLayout.setContentPullEnabled(false);
	    mSwipeLayout.setOnRefreshListener(this);
	    mSwipeLayout.setColorSchemeResources(R.color.action_swipe_1, R.color.action_swipe_2,
	                                         R.color.action_swipe_3, R.color.action_swipe_4);
	    fragmentManager = getSupportFragmentManager();
	    showContent();
    }

	private void retrieveArguments() {
		Bundle args = getIntent().getExtras();
		categoryName = args.getString(PkRSS.KEY_CATEGORY_NAME, null);
		feedUrl = args.getString(PkRSS.KEY_FEED_URL, null);
		articleUrl = args.getString(PkRSS.KEY_ARTICLE_URL, null);
		articleId = args.getInt(PkRSS.KEY_ARTICLE_ID, -1);
	}

	private void initViews() {
		noContent = findViewById(R.id.noContent);
		mSwipeLayout = (PkSwipeRefreshLayout) findViewById(R.id.swipeContainer);
	}

	private void showContent() {
		if(articleUrl != null && !articleUrl.isEmpty()) {
			if(currentArticle == null)
				loadArticle();
			else
				fragmentManager.beginTransaction().replace(R.id.articleContent, ArticleFragment.newInstance(currentArticle)).commit();
			return;
		}

		// Show current category as action bar title
		getActionBar().setTitle(categoryName);

		// Get list of articles
		List<Article> articleList = PkRSS.with(this).get(feedUrl);

		if(articleList == null || articleList.size() < 1)
			return;

		// Find article based on passed ID
		for(Article article : articleList) {
			if(article.getId() == articleId) {
				currentArticle = article;
				break;
			}
		}

		// Show current article
		fragmentManager.beginTransaction().replace(R.id.articleContent, ArticleFragment.newInstance(currentArticle)).commit();
	}

	private void loadArticle() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				noContent.setVisibility(View.VISIBLE);
				mSwipeLayout.setRefreshing(true);
			}

			@Override
			protected Void doInBackground(Void... params) {
				currentArticle = PkRSS.with(ArticleActivity.this).load(articleUrl).individual().getFirst();
				return null;
			}

			@Override
			protected void onPostExecute(Void p) {
				noContent.setVisibility(View.GONE);
				mSwipeLayout.setRefreshing(false);
				fragmentManager.beginTransaction().replace(R.id.articleContent, ArticleFragment.newInstance(currentArticle)).commit();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * Proxy method for setting a scroll target from child
	 * fragments. (Or other things with access to this Activity)
	 * @param target
	 */
	public void setScrollTarget(View target) {
		mSwipeLayout.setScrollTarget(target);
	}

	@Override
	public void onRefresh() {

	}
}