package com.pkmmte.techdissected.util;

import android.net.Uri;

public class Request {
	public final Uri uri;
	public final int page;

	public Request(Uri uri, int page) {
		this.uri = uri;
		this.page = page;
	}

	public Request(Builder builder) {
		this.uri = builder.uri;
		this.page = builder.page;
	}

	public Uri getUri() {
		return uri;
	}

	public int getPage() {
		return page;
	}

	public static final class Builder {
		private Uri uri;
		private int page;

		public Builder(Uri uri, int page) {
			this.uri = uri;
			this.page = page;
		}

		public Builder uri(Uri uri) {
			this.uri = uri;
			return this;
		}

		public Builder page(int page) {
			this.page = page;
			return this;
		}

		public Request build() {
			return new Request(this);
		}
	}
}