package com.pkmmte.techdissected.model;

import android.net.Uri;

/**
 * Created by Family on 8/12/2014.
 */
public class ListBuilder {
	private Uri url;
	private Uri avatar;
	private String username;
	private String name;
	private String description;

	private ListBuilder() {
	}

	public static ListBuilder anAuthor() {
		return new ListBuilder();
	}

	public ListBuilder withUrl(Uri url) {
		this.url = url;
		return this;
	}

	public ListBuilder withAvatar(Uri avatar) {
		this.avatar = avatar;
		return this;
	}

	public ListBuilder withUsername(String username) {
		this.username = username;
		return this;
	}

	public ListBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public ListBuilder withDescription(String description) {
		this.description = description;
		return this;
	}

	public Author build() {
		Author author = new Author();
		author.setUrl(url);
		author.setAvatar(avatar);
		author.setUsername(username);
		author.setName(name);
		author.setDescription(description);
		return author;
	}
}
