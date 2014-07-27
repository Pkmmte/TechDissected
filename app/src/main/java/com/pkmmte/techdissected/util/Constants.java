package com.pkmmte.techdissected.util;

import com.pkmmte.techdissected.model.Category;
import java.util.ArrayList;
import java.util.List;

public class Constants {
	public static final String WEBSITE_URL = "http://techdissected.com";
	public static final String CATEGORY_URL = WEBSITE_URL + "/category";
	public static final String MAIN_FEED = WEBSITE_URL + "/feed";

	public static final String ARTICLE_ID = "ARTICLE ID";

	public static final List<Category> CATEGORIES = new Category.ListBuilder()
													.add("All Posts", MAIN_FEED)
													.add("News", CATEGORY_URL + "/news/feed")
													.add("Editorials", CATEGORY_URL + "/editorials-and-discussions/feed")
													.add("Web & Computing", CATEGORY_URL + "/web-and-computing/feed")
													.add("Google", CATEGORY_URL + "/google/feed")
													.add("Apple", CATEGORY_URL + "/apple/feed")
													.add("Microsoft", CATEGORY_URL + "/microsoft/feed")
													.add("Alternative Tech", CATEGORY_URL + "/alternative-tech/feed")
													.add("Automobile", CATEGORY_URL + "/automotive/feed")
													.add("Gaming", CATEGORY_URL + "/gaming/feed")
													.add("Satire", CATEGORY_URL + "/satire/feed")
													.add("Wearables", CATEGORY_URL + "/wearables/feed")
													.build();
}