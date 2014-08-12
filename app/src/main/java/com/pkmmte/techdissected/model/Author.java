package com.pkmmte.techdissected.model;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class Author {
	private Uri url;
	private Uri avatar;
	private String username;
	private String name;
	private String description;

	public Author() {
		this.url = null;
		this.avatar = null;
		this.username = null;
		this.name = null;
		this.description = null;
	}

	public Author(Uri url, Uri avatar, String username, String name, String description) {
		this.url = url;
		this.avatar = avatar;
		this.username = username;
		this.name = name;
		this.description = description;
	}

	public Uri getUrl() {
		return url;
	}

	public void setUrl(Uri url) {
		this.url = url;
	}

	public Uri getAvatar() {
		return avatar;
	}

	public void setAvatar(Uri avatar) {
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static class ListBuilder {
		private final List<Author> authorList;

		public ListBuilder() {
			authorList = new ArrayList<Author>();
		}

		public ListBuilder add(Uri url, Uri avatar, String username, String name, String description) {
			authorList.add(new Author(url, avatar, username, name, description));
			return this;
		}

		public List<Author> build() {
			return authorList;
		}
	}
}