package com.pkmmte.techdissected.util;

public class Request {
	public final String url;
	public final int page;
	public final RSSManager.Callback callback;

	public Request(String url, int page, RSSManager.Callback callback) {
		this.url = url;
		this.page = page;
		this.callback = callback;
	}

	public Request(Builder builder) {
		this.url = builder.url;
		this.page = builder.page;
		this.callback = builder.callback;
	}

	public String getUrl() {
		return url;
	}

	public int getPage() {
		return page;
	}

	public RSSManager.Callback getCallback() {
		return callback;
	}

	public static final class Builder {
		private String url;
		private int page;
		private RSSManager.Callback callback;

		public Builder(String url) {
			this.url = url;
			this.page = 1;
		}

		public Builder url(String url) {
			this.url = url;
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