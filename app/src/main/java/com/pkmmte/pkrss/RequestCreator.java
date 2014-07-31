package com.pkmmte.pkrss;

import android.os.AsyncTask;
import java.util.List;
import java.util.Map;

public class RequestCreator {
	private final PkRSS singleton;
	private final Request.Builder data;

	protected RequestCreator(PkRSS singleton, String url) {
		this.singleton = singleton;
		this.data = new Request.Builder(url);
	}

	/**
	 * Assigns a reference tag to this request.
	 * Leaving this empty will automatically generate a tag.
	 *
	 * @param tag
	 * @return
	 */
	public RequestCreator tag(String tag) {
		this.data.tag(tag);
		return this;
	}

	/**
	 * Looks up a specific query on the RSS feed.
	 * The query string is automatically encoded.
	 *
	 * @param search
	 * @return
	 */
	public RequestCreator search(String search) {
		this.data.search(search);
		return this;
	}

	/**
	 * Threats this request as an individual article,
	 * rather than full feed. Use only if you are sure that
	 * the load URL belongs to a single article.
	 *
	 * @return
	 */
	public RequestCreator individual() {
		this.data.individual(true);
		return this;
	}

	/**
	 * Ignores already cached responses when making this
	 * request. Useful for refreshing feeds/articles.
	 *
	 * @return
	 */
	public RequestCreator skipCache() {
		this.data.skipCache(true);
		return this;
	}

	/**
	 * Loads a specific page of the RSS feed.
	 *
	 * @param page
	 * @return
	 */
	public RequestCreator page(int page) {
		this.data.page(page);
		return this;
	}

	/**
	 * Loads the next page of the current RSS feed.
	 * If no page was previously loaded, this will
	 * request the first page.
	 *
	 * @return
	 */
	public RequestCreator nextPage() {
		Request request = data.build();
		String url = request.url;
		int page = request.page;

		if(request.search != null)
			url += "?s=" + request.search;

		Map<String, Integer> pageTracker = singleton.getPageTracker();
		if(pageTracker.containsKey(url))
			page = pageTracker.get(url);

		this.data.page(page + 1);
		return this;
	}

	/**
	 * Adds a callback listener to this request.
	 *
	 * @param callback
	 * @return
	 */
	public RequestCreator callback(PkRSS.Callback callback) {
		this.data.callback(callback);
		return this;
	}

	/**
	 * Executes request and returns a full list containing all
	 * articles loaded from this request's URL. <br>
	 *
	 * If this request is marked as individual, the list will
	 * contain only 1 index. It is recommended to use getFirst()
	 * for individual requests instead.
	 *
	 * @return
	 */
	public List<Article> get() {
		final Request request = data.build();
		singleton.load(request.url, request.search, request.individual, request.skipCache, request.page, request.callback);
		return singleton.get(request.individual ? request.url + "feed/?withoutcomments=1" : request.url, request.search);
	}

	/**
	 * Executes request and returns the first Article associated
	 * with this request. This is useful for individual Article requests.
	 *
	 * @return
	 */
	public Article getFirst() {
		return get().get(0);
	}

	/**
	 * Executes request asynchronously. <br>
	 *
	 * Be sure to add a callback to handle this.
	 */
	public void async() {
		final Request request = data.build();

		// TODO Create thread handler class to keep track of all these
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				singleton.load(request.url, request.search, request.individual, request.skipCache, request.page, request.callback);
				return null;
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
}