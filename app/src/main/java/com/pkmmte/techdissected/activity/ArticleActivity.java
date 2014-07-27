package com.pkmmte.techdissected.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.fragment.ArticleFragment;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;

public class ArticleActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_article);

	    Article article = RSSManager.with(this).get(getIntent().getIntExtra(Constants.ARTICLE_ID, -1));
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    fragmentManager.beginTransaction().replace(R.id.container, ArticleFragment.newInstance(article)).commit();
    }
}
