package com.pkmmte.techdissected.util;

import android.content.Context;
import android.util.SparseArray;
import com.pkmmte.techdissected.model.Article;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSSManager {
	// Global singleton instance
	private static RSSManager rssManager = null;

	// For issue tracking purposes
	private volatile boolean loggingEnabled;
	protected static final String TAG = "RSSManager";

	// Context is always useful for some reason.
	private Context mContext;

	// Our handy client for getting XML feed data
	private OkHttpClient httpClient = new OkHttpClient();

	// Reusable XML Parser
	private RSSParser rssParser = new RSSParser();

	// List of stored articles
	private List<Article> mArticles = new ArrayList<Article>();

	// Keep track of pages already loaded on specific feeds
	private Map<String, Integer> mPages = new HashMap<String, Integer>();

	//
	private Callback mCallback;

	//
	private boolean isParsing = false;

	public static  RSSManager with(Context context) {
		if(rssManager == null)
			rssManager = new RSSManager(context.getApplicationContext());
		return rssManager;
	}

	protected RSSManager(Context context) {
		this.mContext = context;
		try {
			this.httpClient.setCache(new Cache(context.getCacheDir(), 2000));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public void setLoggingEnabled(boolean enabled) {
		loggingEnabled = enabled;
	}

	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	public boolean isParsing() {
		return isParsing;
	}

	public void parse(String url) {
		parse(url, 1);
	}

	public void parse(String url, int page) {
		//if(isParsing)
		//	return;
		isParsing = true;

		//
		mPages.put(url, page);
		if(page > 1)
			url += "?paged=" + String.valueOf(page);

		Request request = new Request.Builder()
			.url(url)
			.build();

		String xmlString = null;

		try {
			// 1. get a http response
			Response response = httpClient.newCall(request).execute();

			// 2. construct a string from the response
			xmlString = response.body().string();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// 3. construct an InputSource from the string
		InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());

		// 4. start parsing with SAXParser and handler object
		// ( both must have been created before )
		mArticles.addAll(rssParser.parse(inputStream));

		if(mCallback != null)
			mCallback.postParse(mArticles);

		isParsing = false;
	}

	public void parseNext(String url) {
		if(!mPages.containsKey(url))
			mPages.put(url, 0);

		parse(url, mPages.get(url) + 1);
	}

	public List<Article> get() {
		return mArticles;
	}

	public Article get(int id) {
		for(Article article : mArticles) {
			if(article.getId() == id)
				return article;
		}

		return null;
	}

	public interface Callback {
		public void postParse(List<Article> articleList);
	}
}