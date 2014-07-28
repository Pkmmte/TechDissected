package com.pkmmte.techdissected.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import com.pkmmte.techdissected.fragment.ArticleFragment;
import com.pkmmte.techdissected.model.Article;
import java.util.ArrayList;
import java.util.List;

public class ArticlePagerAdapter extends FragmentPagerAdapter {
	private final List<ArticleFragment> articleList;

	public ArticlePagerAdapter(FragmentManager fragmentManager, List<Article> articles) {
		super(fragmentManager);

		articleList = new ArrayList<ArticleFragment>();
		for(Article article : articles)
			articleList.add(ArticleFragment.newInstance(article));
	}

	@Override
	public Fragment getItem(int position) {
		return articleList.get(position);
	}

	@Override
	public int getCount() {
		return articleList.size();
	}
}