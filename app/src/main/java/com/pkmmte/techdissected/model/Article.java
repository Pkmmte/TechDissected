package com.pkmmte.techdissected.model;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Article {
	private List<String> tags;
	private Uri source;
	private Uri image;
	private String title;
	private String description;
	private String content;
	private String category;
	private String author;
	private long date;
	private int id;

	public Article() {
		this.tags = new ArrayList<String>();
		this.source = null;
		this.image = null;
		this.title = null;
		this.description = null;
		this.content = null;
		this.category = null;
		this.author = null;
		this.date = 0;
		long id = -1;
	}

	public Article(List<String> tags, Uri source, Uri image, String title, String description, String content, String category, String author, long date, int id) {
		this.tags = tags;
		this.source = source;
		this.image = image;
		this.title = title;
		this.description = description;
		this.content = content;
		this.category = category;
		this.author = author;
		this.date = date;
		this.id = id;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void setNewTag(String tag) {
		this.tags.add(tag);
	}

	public Uri getSource() {
		return source;
	}

	public void setSource(Uri source) {
		this.source = source;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Article{" +
			"tags=" + tags +
			", source=" + source +
			", image=" + image +
			", title='" + title + '\'' +
			", description='" + description + '\'' +
			", content='" + content + '\'' +
			", category='" + category + '\'' +
			", author='" + author + '\'' +
			", date=" + date +
			", id=" + id +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Article article = (Article) o;

		if (date != article.date) return false;
		if (id != article.id) return false;
		if (author != null ? !author.equals(article.author) : article.author != null) return false;
		if (category != null ? !category.equals(article.category) : article.category != null) {
			return false;
		}
		if (content != null ? !content.equals(article.content) : article.content != null)
			return false;
		if (description != null ? !description.equals(article.description)
			: article.description != null) {
			return false;
		}
		if (image != null ? !image.equals(article.image) : article.image != null) return false;
		if (source != null ? !source.equals(article.source) : article.source != null) return false;
		if (!tags.equals(article.tags)) return false;
		if (title != null ? !title.equals(article.title) : article.title != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = tags.hashCode();
		result = 31 * result + (source != null ? source.hashCode() : 0);
		result = 31 * result + (image != null ? image.hashCode() : 0);
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (content != null ? content.hashCode() : 0);
		result = 31 * result + (category != null ? category.hashCode() : 0);
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (int) (date ^ (date >>> 32));
		result = 31 * result + id;
		return result;
	}
}