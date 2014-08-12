package com.pkmmte.techdissected.util;

import android.net.Uri;
import com.pkmmte.pkrss.Category;
import com.pkmmte.techdissected.model.Author;
import java.util.List;

public class Constants {
	public static final String PREFS_NAME = "TechDissected";
	public static final String PREF_READ = "MARK READ";

	public static final String WEBSITE_URL = "http://techdissected.com";
	public static final String CATEGORY_URL = WEBSITE_URL + "/category";
	public static final String MAIN_FEED = WEBSITE_URL + "/feed";
	public static final String PKRSS_URL = "https://github.com/Pkmmte/PkRSS";
	public static final String DEV_URL = "http://pkmmte.com";

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
	public static final Category DEFAULT_CATEGORY = CATEGORIES.get(0);

	public static final List<Author> AUTHORS = new Author.ListBuilder()
											   .add(Uri.parse(""), Uri.parse("http://1.gravatar.com/avatar/a90d0d6e5f6cfa17851201f1d78937cf.png"), "cliffwade", "Cliff Wade", "Chief Editor/Founder of TeD. I'm an avid Linux user, that's addicted to music, electronics, the internet, computers, Android and everything tech related! Rocking a Moto X and a Moto G, with a Nexus 7 as well. Things that get me through each day: My son, my girlfriend, Nutter Butter cookies and gallons of sweet tea. If it beeps or makes noise, I'm interested. Gadgets and Gizmos are my specialty. If you have any questions just hit me up. Hope you enjoy the site!")
											   .build();
}