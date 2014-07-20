package com.pkmmte.techdissected.model;

import android.net.Uri;

public class Article {
	private Uri image;
	private String title;
	private String description;
	private String author;
	private int id;

	public Article() {
		this.image = null;
		this.title = null;
		this.description = null;
		this.author = null;
		this.id = -1;
	}

	public Article(Uri image, String title, String description, String author, int id) {
		this.image = image;
		this.title = title;
		this.description = description;
		this.author = author;
		this.id = id;
	}

	public Uri getImage() {
		return image;
	}

	public void setImage(Uri image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + id;
		return result;
	}

	@Override
	public String toString() {
		return "Article{" +
			"title='" + title + '\'' +
			", description='" + description + '\'' +
			", id=" + id +
			'}';
	}
}