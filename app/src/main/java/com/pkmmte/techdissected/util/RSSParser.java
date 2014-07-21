package com.pkmmte.techdissected.util;

import android.net.Uri;
import android.text.Html;
import com.pkmmte.techdissected.model.Article;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class RSSParser {
	private List<Article> articleList = new ArrayList<Article>();
	private SimpleDateFormat dateFormat;
	private XmlPullParser xmlParser;

	protected RSSParser() {
		dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		initParser();
	}

	public List<Article> parse(InputStream input) {
		articleList.clear();

		try {
			xmlParser.setInput(input, null);
			Article article = new Article();
			int eventType = xmlParser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = xmlParser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if (tagname.equalsIgnoreCase("item")) {
							// create a new instance
							article = new Article();
						}
						else
							handleNode(tagname, article);
						break;
					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("item")) {
							// add article object to list
							articleList.add(article);
						}
						break;
					default:
						break;
				}
				eventType = xmlParser.next();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return articleList;
	}

	private boolean handleNode(String tag, Article article) {
		try {
			if(xmlParser.next() != XmlPullParser.TEXT)
				return false;

			if (tag.equalsIgnoreCase("link"))
				article.setSource(Uri.parse(xmlParser.getText()));
			else if (tag.equalsIgnoreCase("title"))
				article.setTitle(xmlParser.getText());
			else if (tag.equalsIgnoreCase("description"))
				article.setDescription(Html.fromHtml(xmlParser.getText()).toString());
			else if (tag.equalsIgnoreCase("encoded"))
				article.setContent(xmlParser.getText());
			else if (tag.equalsIgnoreCase("category"))
				article.setCategory(xmlParser.getText());
			else if (tag.equalsIgnoreCase("creator"))
				article.setAuthor(xmlParser.getText());
			else if (tag.equalsIgnoreCase("pubDate"))
				article.setDate(getParsedDate(xmlParser.getText()));

			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
			return false;
		}
	}

	private long getParsedDate(String encodedDate) {
		try {
			return dateFormat.parse(encodedDate).getTime();
		}
		catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private void initParser() {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xmlParser = factory.newPullParser();
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
}