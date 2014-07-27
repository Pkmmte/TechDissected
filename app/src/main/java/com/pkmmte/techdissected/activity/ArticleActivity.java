package com.pkmmte.techdissected.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import com.pkmmte.techdissected.util.Utils;
import com.pkmmte.techdissected.view.CustomShareActionProvider;
import com.pkmmte.techdissected.view.FlowLayout;
import com.pkmmte.techdissected.view.PkScrollView;
import com.pkmmte.techdissected.view.RippleButton;
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
        setContentView(R.layout.activity_article);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    //getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

	    mActionBarBackgroundDrawable = new ColorDrawable(getResources().getColor(R.color.action_background));
	    mActionBarBackgroundDrawable.setAlpha(0);
		getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mActionBarBackgroundDrawable.setCallback( new Drawable.Callback() {
				@Override
				public void invalidateDrawable(Drawable who) {
					getActionBar().setBackgroundDrawable(who);
				}

				@Override
				public void scheduleDrawable(Drawable who, Runnable what, long when) {
				}

				@Override
				public void unscheduleDrawable(Drawable who, Runnable what) {
				}
			});
		}

	    article = RSSManager.with(this).get(getIntent().getIntExtra(Constants.ARTICLE_ID, -1));
	    TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
	    TextView txtAuthor = (TextView) findViewById(R.id.txtAuthor);
	    TextView txtDate = (TextView) findViewById(R.id.txtDate);

	    FlowLayout tagContainer = (FlowLayout) findViewById(R.id.tagContainer);

	    final ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);
	    Picasso.with(this).load(article.getImage()).placeholder(R.drawable.placeholder).into(imgBanner);
	    final FrameLayout imgContainer = (FrameLayout) findViewById(R.id.imgContainer);
		imgContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.getImageDialog(ArticleActivity.this, article.getImage()).show();
			}
		});

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
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    if (Utils.containsImage(url))
				    Utils.getImageDialog(ArticleActivity.this, Uri.parse(url)).show();
			    else
				    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

			    return true;
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
