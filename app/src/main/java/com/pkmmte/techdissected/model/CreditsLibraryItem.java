package com.pkmmte.techdissected.model;

import android.net.Uri;

public class CreditsLibraryItem
{
	private Uri Avatar;
	private Uri Link;
	private String Title;
	private String Author;
	private String License;
	private boolean Expanded;
	
	public CreditsLibraryItem()
	{
		this.Avatar = null;
		this.Link = null;
		this.Title = "null";
		this.Author = "null";
		this.License = "null";
		this.Expanded = false;
	}
	
	public CreditsLibraryItem(Uri Avatar, Uri Link, String Title, String Author, String License)
	{
		this.Avatar = Avatar;
		this.Link = Link;
		this.Title = Title;
		this.Author = Author;
		this.License = License;
		this.Expanded = false;
	}
	
	public CreditsLibraryItem(Uri Avatar, Uri Link, String Title, String Author, String License, boolean Expanded)
	{
		this.Avatar = Avatar;
		this.Link = Link;
		this.Title = Title;
		this.Author = Author;
		this.License = License;
		this.Expanded = Expanded;
	}
	
	public CreditsLibraryItem(Builder builder)
	{
		this.Avatar = builder.Avatar;
		this.Link = builder.Link;
		this.Title = builder.Title;
		this.Author = builder.Author;
		this.License = builder.License;
		this.Expanded = builder.Expanded;
	}
	
	public void setAvatar(Uri Avatar) {
		this.Avatar = Avatar;
	}
	
	public void setLink(Uri Link) {
		this.Link = Link;
	}
	
	public void setTitle(String Title) {
		this.Title = Title;
	}
	
	public void setAuthor(String Author) {
		this.Author = Author;
	}
	
	public void setLicense(String License) {
		this.License = License;
	}
	
	public void setExpanded(boolean Expanded) {
		this.Expanded = Expanded;
	}
	
	public Uri getAvatar() {
		return this.Avatar;
	}
	
	public Uri getLink() {
		return this.Link;
	}
	
	public String getTitle() {
		return this.Title;
	}
	
	public String getAuthor() {
		return this.Author;
	}
	
	public String getLicense() {
		return this.License;
	}
	
	public boolean isExpanded() {
		return this.Expanded;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Avatar = [" + this.Avatar.toString() + "], ");
		builder.append("Link = [" + this.Link.toString() + "], ");
		builder.append("Title = [" + this.Title + "], ");
		builder.append("Author = [" + this.Author + "], ");
		builder.append("License = [" + this.License + "], ");
		builder.append("Expanded = [" + this.Expanded + "]");
		return builder.toString();
	}
	
	public static class Builder
	{
		private Uri Avatar;
		private Uri Link;
		private String Title;
		private String Author;
		private String License;
		private boolean Expanded;
		
		public Builder()
		{
			this.Avatar = null;
			this.Link = null;
			this.Title = "null";
			this.Author = "null";
			this.License = "null";
			this.Expanded = false;
		}
		
		public Builder avatar(Uri Avatar) {
			this.Avatar = Avatar;
			return this;
		}
		
		public Builder link(Uri Link) {
			this.Link = Link;
			return this;
		}
		
		public Builder title(String Title) {
			this.Title = Title;
			return this;
		}
		
		public Builder author(String Author) {
			this.Author = Author;
			return this;
		}
		
		public Builder license(String License) {
			this.License = License;
			return this;
		}
		
		public Builder Expanded(boolean Expanded) {
			this.Expanded = Expanded;
			return this;
		}
		
		public CreditsLibraryItem build()
		{
			return new CreditsLibraryItem(this);
		}
	}
}