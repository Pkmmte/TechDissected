package com.pkmmte.techdissected.util;

import android.net.Uri;
import com.squareup.picasso.Picasso;

// TODO Make RequestCreator
public class RequestCreator {
	private final Request.Builder data;
	private final RSSManager rssManager;

	protected RequestCreator(RSSManager rssManager, Uri uri, int page) {
		this.rssManager = rssManager;
		this.data = new Request.Builder(uri, page);
	}

	public void callback(RSSManager.Callback callback) {
		//rssManager.parse(data.build().getUri());
	}

	public void end() {

	}
}