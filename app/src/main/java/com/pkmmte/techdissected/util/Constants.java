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
											   .add(Uri.parse(""), Uri.parse("http://2.gravatar.com/avatar/7fe1076e3c4a4a324a1115b006a45e09.png"), "bretsmith", "Bret Smith", "I am a comedy writer whose passion for technology is too strong for me not to write about it. I love showing light users of tech something they haven't seen before. And Update your apps you animal.")
											   .add(Uri.parse(""), Uri.parse("http://1.gravatar.com/avatar/3ee266fc969b70275b135c26fff131b6.png"), "andrewmyrick", "Andrew Myrick", "I'm a lover of all things technology, which happens to work perfectly with the ideology behind Tech Dissected. I currently carry a Nexus 5 as well as an iPhone 5S and am contemplating ways to get my hands on a new Windows 8 phone. My life is an open book, but beware, I don't back down from a sports discussion, EVER.")
											   .add(Uri.parse(""), Uri.parse("http://1.gravatar.com/avatar/f8f21d028347069ee891db35ce7cacd6.png"), "guidot42", "Jeff Trocchio", "Apple IIe green screen is whence I came. Where I go, only technology knows. If its Automotive, Mobile, Gaming or Computer tech I'll try my best to give my thoughts on it.")
											   .add(Uri.parse(""), Uri.parse("http://1.gravatar.com/avatar/e4aae16c60bbc0ea8410273ca04a2a46.png"), "nschiwy", "Nick Schiwy", "Nick is an tech enthusiast, programmer and general geek. He attends the University of Maryland, College Park full time but still has plenty of time to keep up with all of the tech gossip that is going around!")
											   .add(Uri.parse(""), Uri.parse("http://2.gravatar.com/avatar/135620db4748ddb7fa8ce964437ef7d4.png"), "fredscholl", "Fred Scholl", "I'm an unabashed enthusiast of all things Android, open-source, and technology in general. I'm also an avid music lover and musician, playing guitar, bass, keyboards, and a host of other stringed instruments.")
											   .add(Uri.parse(""), Uri.parse("http://0.gravatar.com/avatar/ade1da653b6d0f6dc9eda57da8e0740d.png"), "christopherfoote", "Chris Foote", "Singer, guitarist and songwriter for the Grand Rapids, MI based band, Ars Nova. Google Glass Explorer. Google Helpouts guitar lesson provider. Photographer. Transhumanist. Technology enthusiast. Enlightened Ingresser.")
											   .add(Uri.parse(""), Uri.parse("http://2.gravatar.com/avatar/1e1afbcc16f7573015c126626179a09c.png"), "carissatrocchio", "Carissa Trocchio", "Sci-Fi/Fantasy, Android and video games keep me busy if I'm not cooking up a storm or knitting you an entire wardrobe.")
											   .add(Uri.parse(""), Uri.parse("http://0.gravatar.com/avatar/2e7b48eb0b31dab77910b3cebe3777ad.png"), "yugpatel", "Yug Patel", "I love everything Google. I like to code and design. I live and attend High School in Ahmedabad, India. In my spare time, I like to go cycling and spend some time learning about new stuff.")
											   .add(Uri.parse(""), Uri.parse("http://1.gravatar.com/avatar/b83bbda67f267b78f8b8f24cb43e618c.png"), "alvierodgers", "Alvie Rodgers", "When not spending time in the Ozarks as a penguin tamer, Alvie enjoys spending his free time discussing all things Android. After turning down the opportunity to direct the more recent Star Wars films, he chose to create some of the most influential videos of our time.")
											   .add(Uri.parse(""), Uri.parse("http://1.gravatar.com/avatar/6fb56ff6ee8c3411515c7a11d557153e.png"), "steelewotkyns", "Steele Wotkyns", "Steele Wotkyns is a freelance writer and principal of WotkynsPRplus in Flagstaff, Arizona.")
											   .add(Uri.parse(""), Uri.parse("http://0.gravatar.com/avatar/7380c70c69b1739023ddd6d6e3872034.png"), "davidstrausser", "David Strausser", "David is an avid tech enthusiast. A graduate of Penn State in Information Sciences and Technology, David has found himself heading business development for a bi-national tech start-up based in San Diego, CA but working primarily out of Tijuana, Mexico.")
											   .add(Uri.parse(""), Uri.parse("http://2.gravatar.com/avatar/222a04290db22994c6ba87d684c541d8.png"), "mattlucas", "Matt Lucas", "I work hard while having fun and learning everything that I can along the way. Specialties: VoIP, Big Data, Splunk, Linux. All sorts of fun stuff. I am a big Linux and Android guy, love learning new technologies and seeing what I can bring together to make something awesome.")
											   //.add(Uri.parse(""), Uri.parse("avatarurl"), "username", "Name", "Description")
											   .build();
}