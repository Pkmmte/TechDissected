package com.pkmmte.techdissected.util;

public class Request {
	public final String url;
	public final String search;
	public final int page;
	public final RSSManager.Callback callback;

	public Request(String url, String search, int page, RSSManager.Callback callback) {
		this.url = url;
		this.search = search;
		this.page = page;
		this.callback = callback;
	}

	public Request(Builder builder) {
		this.url = builder.url;
		this.search = builder.search;
		this.page = builder.page;
		this.callback = builder.callback;
	}

	public String getUrl() {
		return url;
	}

	public String getSearchTerm() {
		return search;
	}

	public int getPage() {
		return page;
	}

	public RSSManager.Callback getCallback() {
		return callback;
	}

	public static final class Builder {
		private String url;
		private String search;
		private int page;
		private RSSManager.Callback callback;

		public Builder(String url) {
			this.url = url;
			this.search = null;
			this.page = 1;
			this.callback = null;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder search(String search) {
			this.search = search;
			return this;
		}

		public Builder page(int page) {
			this.page = page;
			return this;
		}

		public Builder callback(RSSManager.Callback callback) {
			this.callback = callback;
			return this;
		}

		public Request build() {
			return new Request(this);
		}
	}
}