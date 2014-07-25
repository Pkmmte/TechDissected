package com.pkmmte.techdissected.util;

import android.net.Uri;
import android.os.AsyncTask;
import com.pkmmte.techdissected.model.Article;
import com.squareup.picasso.Picasso;
import java.util.List;

// TODO Assign an id to each request (could be by AtomicLong.getInstance()) and
// TODO ... handle them in threads appropriately.
// TODO Also, make a builder for the RSSManager to set configuration such as parallel/serial threading.
public class RequestCreator {
	private final RSSManager rssManager;
	private final Request.Builder data;

	protected RequestCreator(RSSManager rssManager, String url) {
		this.rssManager = rssManager;
		this.data = new Request.Builder(url);
	}

	public RequestCreator page(int page) {
		this.data.page(page);
		return this;
	}

	public RequestCreator callback(RSSManager.Callback callback) {
		this.data.callback(callback);
		return this;
	}

	public List<Article> get() {
		final Request request = data.build();
		rssManager.parse(request.getUrl(), request.getPage());
		return rssManager.get();
	}

	public void async() {
		final Request request = data.build();
		rssManager.setCallback(request.getCallback());

		// TODO Create thread handler class to keep track of all these
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				rssManager.parse(request.getUrl(), request.getPage());
				return null;
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
}