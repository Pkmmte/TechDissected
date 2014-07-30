package com.pkmmte.techdissected.util;

import android.os.AsyncTask;
import com.pkmmte.techdissected.model.Article;
import java.util.List;
import java.util.Map;

// TODO Assign an id to each request (could be by AtomicLong.getInstance()) and
// TODO ... handle them in threads appropriately.
// TODO Also, make a builder for the RSSManager to set configuration such as parallel/serial threading.
public class RequestCreator {
	private final PkRSS pkRss;
	private final Request.Builder data;

	protected RequestCreator(PkRSS pkRss, String url) {
		this.pkRss = pkRss;
		this.data = new Request.Builder(url);
	}

	public RequestCreator search(String search) {
		this.data.search(search);
		return this;
	}

	public RequestCreator individual() {
		this.data.individual(true);
		return this;
	}

	public RequestCreator skipCache() {
		this.data.skipCache(true);
		return this;
	}

	public RequestCreator page(int page) {
		this.data.page(page);
		return this;
	}

	public RequestCreator nextPage() {
		Request request = data.build();
		String url = request.url;
		int page = request.page;

		if(request.search != null)
			url += "?s=" + request.search;

		Map<String, Integer> pageTracker = pkRss.getPageTracker();
		if(pageTracker.containsKey(url))
			page = pageTracker.get(url);

		this.data.page(page + 1);
		return this;
	}

	public RequestCreator callback(PkRSS.Callback callback) {
		this.data.callback(callback);
		return this;
	}

	public List<Article> get() {
		final Request request = data.build();
		pkRss.load(request.url, request.search, request.individual, request.skipCache, request.page);
		return pkRss.get(request.individual ? request.url + "feed/?withoutcomments=1" : request.url, request.search);
	}

	public Article getFirst() {
		return get().get(0);
	}

	public void async() {
		final Request request = data.build();
		pkRss.setCallback(request.callback);

		// TODO Create thread handler class to keep track of all these
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				pkRss.load(request.url, request.search, request.individual, request.skipCache, request.page);
				return null;
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
}