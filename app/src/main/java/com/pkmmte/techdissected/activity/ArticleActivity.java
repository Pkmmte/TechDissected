package com.pkmmte.techdissected.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.RSSManager;
import com.pkmmte.techdissected.util.Utils;
import com.squareup.picasso.Picasso;

/**
 * MY OH MY WHAT A MESS!!
 * TODO Clean up all this code... and put it in a ViewPager fragment
 */
public class ArticleActivity extends Activity {

	float m_downX = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
	    getActionBar().setDisplayHomeAsUpEnabled(true);

	    Article article = RSSManager.with(this).get(getIntent().getIntExtra(Constants.ARTICLE_ID, -1));
	    TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
	    TextView txtAuthor = (TextView) findViewById(R.id.txtAuthor);
	    TextView txtDate = (TextView) findViewById(R.id.txtDate);

	    ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);
	    Picasso.with(this).load(article.getImage()).placeholder(R.drawable.placeholder).into(imgBanner);

	    txtTitle.setText(article.getTitle());
	    txtAuthor.setText(article.getAuthor());
	    txtDate.setText(Utils.getRelativeDate(article.getDate()));


	    WebView webView = (WebView) findViewById(R.id.webView);
	    String mime = "text/html";
	    String encoding = "utf-8";

	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
	    webView.setHorizontalScrollBarEnabled(false);
	    webView.loadDataWithBaseURL(null, article.getContent(), mime, encoding, null);
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
		   // TODO

		    @Override
		    public void onLoadResource(WebView view, String url) {
			    Toast.makeText(ArticleActivity.this, url, Toast.LENGTH_SHORT).show();
			    if(url.contains(".jpg") || url.contains(".png") || url.contains(".webp") || url.contains(".gif") || url.contains("/attachment/"))
				    Toast.makeText(ArticleActivity.this, "IMAGE!!", Toast.LENGTH_SHORT).show();
			    else
				    Toast.makeText(ArticleActivity.this, "Not an image...", Toast.LENGTH_SHORT).show();
			    //super.onLoadResource(view, url);
		    }
	    });

	    webView.setBackgroundColor(getResources().getColor(R.color.app_background2));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
