package com.pkmmte.techdissected.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.fragment.ArticleFragment;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.PkRSS;
import java.util.List;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ArticleActivity extends FragmentActivity implements OnRefreshListener {
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
	private PullToRefreshLayout mPullToRefreshLayout;
	private View noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_article);

		retrieveArguments();
	    initViews();
	    ActionBarPullToRefresh.from(this).listener(this).setup(mPullToRefreshLayout);
	    fragmentManager = getSupportFragmentManager();
	    showContent();
    }

	private void retrieveArguments() {
		Bundle args = getIntent().getExtras();
		categoryName = args.getString(Constants.KEY_CATEGORY_NAME, null);
		feedUrl = args.getString(Constants.KEY_FEED_URL, null);
		articleUrl = args.getString(Constants.KEY_ARTICLE_URL, null);
		articleId = args.getInt(Constants.KEY_ARTICLE_ID, -1);
	}

	private void initViews() {
		noContent = findViewById(R.id.noContent);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
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
				mPullToRefreshLayout.setRefreshing(true);
			}

			@Override
			protected Void doInBackground(Void... params) {
				currentArticle = PkRSS.with(ArticleActivity.this).load(articleUrl).individual().getFirst();
				return null;
			}

			@Override
			protected void onPostExecute(Void p) {
				noContent.setVisibility(View.GONE);
				mPullToRefreshLayout.setRefreshComplete();
				fragmentManager.beginTransaction().replace(R.id.articleContent, ArticleFragment.newInstance(currentArticle)).commit();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onRefreshStarted(View view) {

	}
}