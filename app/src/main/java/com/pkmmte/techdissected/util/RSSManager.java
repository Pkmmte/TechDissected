package com.pkmmte.techdissected.util;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import com.pkmmte.techdissected.model.Article;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class RSSManager {
	// Global singleton instance
	private static RSSManager rssManager = null;

	// For issue tracking purposes
	private volatile boolean loggingEnabled;
	private static final String TAG = "RSSManager";

	// Context is always useful for some reason.
	private Context mContext;

	// Our handy client for getting XML feed data
	private OkHttpClient httpClient = new OkHttpClient();

	// Reusable XML Parser
	private RSSParser rssParser = new RSSParser();

	// List of stored articles
	private List<Article> mArticles = new ArrayList<Article>();

	public static  RSSManager with(Context context) {
		if(rssManager == null)
			rssManager = new RSSManager(context.getApplicationContext());
		return rssManager;
	}

	protected RSSManager(Context context) {
		this.mContext = context;
	}

	public void setLoggingEnabled(boolean enabled) {
		loggingEnabled = enabled;
	}

	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	public void parse(String url) {
		long time = System.currentTimeMillis();
		Request request = new Request.Builder()
			.url(url)
			.build();

		String xmlString = null;

		try {
			// 1. get a http response
			Response response = httpClient.newCall(request).execute();
			Log.e(TAG, "OkHttp response took " + (System.currentTimeMillis() - time) + "ms");

			// 2. construct a string from the response
			xmlString = response.body().string();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// 3. construct an InputSource from the string
		InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());

		Log.e(TAG, "Overall " + (System.currentTimeMillis() - time) + "ms");

		// 4. start parsing with SAXParser and handler object
		// ( both must have been created before )
		mArticles.addAll(rssParser.parse(inputStream));
	}

	public List<Article> get() {
		return mArticles;
	}
}