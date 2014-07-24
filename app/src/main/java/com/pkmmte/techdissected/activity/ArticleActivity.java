package com.pkmmte.techdissected.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import com.pkmmte.techdissected.util.Utils;
import com.pkmmte.techdissected.view.CustomShareActionProvider;
import com.pkmmte.techdissected.view.FlowLayout;
import com.pkmmte.techdissected.view.PkScrollView;
import com.squareup.picasso.Picasso;

/**
 * MY OH MY WHAT A MESS!!
 * TODO Clean up all this code... and put it in a ViewPager fragment
 */
public class ArticleActivity extends Activity {

	Article article;
	CustomShareActionProvider mShareActionProvider;
	float m_downX = 0;
	int lastTopValue = 0;
	private Drawable mActionBarBackgroundDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_article);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    //getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

	    mActionBarBackgroundDrawable = new ColorDrawable(getResources().getColor(R.color.action_background));
	    mActionBarBackgroundDrawable.setAlpha(0);
		getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

	    article = RSSManager.with(this).get(getIntent().getIntExtra(Constants.ARTICLE_ID, -1));
	    TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
	    TextView txtAuthor = (TextView) findViewById(R.id.txtAuthor);
	    TextView txtDate = (TextView) findViewById(R.id.txtDate);

	    FlowLayout tagContainer = (FlowLayout) findViewById(R.id.tagContainer);

	    final ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);
	    Picasso.with(this).load(article.getImage()).placeholder(R.drawable.placeholder).into(imgBanner);

	    PkScrollView mScroll = (PkScrollView) findViewById(R.id.sticky_scroll);
	    mScroll.setExtraTopOffset(getResources().getDimensionPixelSize(R.dimen.action_height));
	    mScroll.setOnScrollListener(new PkScrollView.PkScrollViewListener() {
		    @Override
		    public void onScrollChanged(PkScrollView scrollView, int x, int y, int oldx, int oldy) {
			    parallaxHeader(imgBanner);
			    final int headerHeight = imgBanner.getHeight() - getActionBar().getHeight();
			    final float ratio = (float) Math.min(Math.max(y, 0), headerHeight) / headerHeight;
			    final int newAlpha = (int) (ratio * 255);
			    mActionBarBackgroundDrawable.setAlpha(newAlpha);
		    }
	    });

	    txtTitle.setText(article.getTitle());
	    txtAuthor.setText(article.getAuthor());
	    txtDate.setText(Utils.getRelativeDate(article.getDate()));

	    LayoutInflater inflater = getLayoutInflater();
	    for(String tag : article.getTags()) {
		    Button btnTag = (Button) inflater.inflate(R.layout.tag, null);
		    btnTag.setText(tag);
		    tagContainer.addView(btnTag);
	    }

	    WebView webView = (WebView) findViewById(R.id.webView);
	    String mime = "text/html";
	    String encoding = "utf-8";

	    webView.setWebChromeClient(new WebChromeClient());
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
	    webView.setHorizontalScrollBarEnabled(false);
	    webView.setOnTouchListener(new View.OnTouchListener() {
		    public boolean onTouch(View v, MotionEvent event) {

			    switch (event.getAction()) {
				    case MotionEvent.ACTION_DOWN: {
					    // save the x
					    m_downX = event.getX();
				    }
				    break;

				    case MotionEvent.ACTION_MOVE:
				    case MotionEvent.ACTION_CANCEL:
				    case MotionEvent.ACTION_UP: {
					    // set x so that it doesn't move
					    event.setLocation(m_downX, event.getY());
				    }
				    break;

			    }

			    return false;
		    }
	    });

	    webView.setWebViewClient(new WebViewClient() {
		    boolean loaded;

		    @Override
		    public void onPageFinished(WebView view, String url) {
			    super.onPageFinished(view, url);
			    this.loaded = true;
		    }

		    @Override
		    public void onPageStarted(WebView view, String url, Bitmap favicon) {
			    super.onPageStarted(view, url, favicon);
			    this.loaded = false;
		    }

		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    if (!loaded) return false;

			    if (url.contains(".jpg")
				    || url.contains(".png")
				    || url.contains(".webp")
				    || url.contains(".gif")
				    || url.contains("/attachment/"))
				    Toast.makeText(ArticleActivity.this, "IMAGE!!", Toast.LENGTH_SHORT).show();
			    else {
				    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				    startActivity(i);
			    }

			    return true;
		    }

		    @Override
		    public void onLoadResource(WebView view, String url) {
			    if (!loaded) {
				    super.onLoadResource(view, url);
				    return;
			    }

			    Toast.makeText(ArticleActivity.this, url, Toast.LENGTH_SHORT).show();
			    if (url.contains(".jpg")
				    || url.contains(".png")
				    || url.contains(".webp")
				    || url.contains(".gif")
				    || url.contains("/attachment/"))
				    Toast.makeText(ArticleActivity.this, "IMAGE!!", Toast.LENGTH_SHORT).show();
			    else {
				    Toast.makeText(ArticleActivity.this, "Not an image...", Toast.LENGTH_SHORT)
					    .show();
				    super.onLoadResource(view, url);
			    }
		    }
	    });
	    webView.loadDataWithBaseURL(null, article.getContent(), mime, encoding, null);

	    webView.setBackgroundColor(getResources().getColor(R.color.app_background2));

	    configureShare(mShareActionProvider);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
	    mShareActionProvider = (CustomShareActionProvider) menu.findItem(R.id.action_share).getActionProvider();
	    configureShare(mShareActionProvider);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
	    if(id == android.R.id.home) {
		    finish();
		    return true;
	    }
        return super.onOptionsItemSelected(item);
    }

	private void parallaxHeader(View header)
	{
		Rect rect = new Rect();
		header.getLocalVisibleRect(rect);
		if (lastTopValue != rect.top){
			lastTopValue = rect.top;
			header.setY((float) (rect.top/2.0));
		}
	}

	/**
	 * Sets up the share intent.
	 * Will return prematurely if either the ShareActionProvider
	 * or current article are null/invalid.
	 *
	 * @param mShareActionProvider
	 */
	private void configureShare(CustomShareActionProvider mShareActionProvider)
	{
		// Can't configure if null
		if (mShareActionProvider == null)
			return;

		// This won't do any good without data to use
		if(article == null || article.getSource() == null)
			return;

		// Create and set the share intent
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.getSource().toString());
		mShareActionProvider.setShareIntent(shareIntent);
	}
}
