package com.pkmmte.techdissected.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.activity.ArticleActivity;
import com.pkmmte.techdissected.util.Dialogs;
import com.pkmmte.techdissected.util.Utils;
import com.pkmmte.techdissected.view.CustomShareActionProvider;
import com.pkmmte.techdissected.view.FlowLayout;
import com.pkmmte.techdissected.view.PkScrollView;
import com.squareup.picasso.Picasso;

public class ArticleFragment extends Fragment {
	// Article
	private Article article;

	// Action Bar
	private ActionBar actionBar;
	private Drawable actionBarDrawable;
	private CustomShareActionProvider mShareActionProvider;
	private MenuItem menuFavorite;

	// WebView params
	private final String base = "file:///android_asset/";
	private final String mime = "text/html";
	private final String encoding = "utf-8";
	private final String history = null;

	// Parallax/Fading helper variables
	private int lastTopValue = 0;
	private int headerHeight;
	private int newAlpha;
	private float ratio;

	// Views
	private PkScrollView mScroll;
	private FlowLayout tagContainer;
	private FrameLayout imgContainer;
	private ImageView imgBanner;
	private TextView txtTitle;
	private TextView txtAuthor;
	private TextView txtDate;
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_article, container, false);
		initActionBar();
		initViews(view);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		// Attempt to get article content
		retrieveContent();

		// Configure WebView
		setupWebView();

		// Show article content, if available
		showContent();

		// Let the SwipeLayout know which is the ScrollView
		((ArticleActivity) getActivity()).setScrollTarget(mScroll);

		// Mark article as read
		if(article != null)
			article.markRead();

		// Change favorite icon state, if possible
		if(article != null && menuFavorite != null)
			menuFavorite.setIcon(article.isFavorite() ? R.drawable.ic_action_favorite_full : R.drawable.ic_action_favorite_empty);

		// Hacky custom fix for supporting FAB with sticky header
		mScroll.setExtraTopOffset(getResources().getDimensionPixelSize(R.dimen.action_height));

		// Scroll listener for parallax/fading effect
		mScroll.setOnScrollListener(new PkScrollView.PkScrollViewListener() {
			@Override
			public void onScrollChanged(PkScrollView scrollView, int x, int y, int oldx, int oldy) {
				parallaxBanner();
				fadeActionBar(y);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.article, menu);
		menuFavorite = menu.findItem(R.id.action_favorite);
		mShareActionProvider = (CustomShareActionProvider) menu.findItem(R.id.action_share).getActionProvider();
		setupShare();

		if(article != null)
			menuFavorite.setIcon(article.isFavorite() ? R.drawable.ic_action_favorite_full : R.drawable.ic_action_favorite_empty);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				getActivity().finish();

				return true;
			case R.id.action_favorite:
				if(article == null) {
					Toast.makeText(getActivity(), "Error loading article!", Toast.LENGTH_SHORT).show();
					return true;
				}

				boolean favorite = !article.isFavorite();
				if (article.saveFavorite(favorite))
					item.setIcon(favorite ? R.drawable.ic_action_favorite_full : R.drawable.ic_action_favorite_empty);

				return true;
			case R.id.action_refresh:
				// TODO
				return true;
			case R.id.action_unread:
				if(article == null) {
					Toast.makeText(getActivity(), "Error loading article!", Toast.LENGTH_SHORT).show();
					return true;
				}

				if(article.markRead(false))
					item.setVisible(false);
				else
					Toast.makeText(getActivity(), "Unable to mark article as read. Please try again.", Toast.LENGTH_SHORT).show();

				return true;
			case R.id.action_browser:
				if(article != null && article.getSource() != null)
					startActivity(new Intent(Intent.ACTION_VIEW).setData(article.getSource()));

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void toggleActionItems(Menu menu, boolean drawerOpen) {
		menu.findItem(R.id.action_share).setVisible(!drawerOpen);
		menu.findItem(R.id.action_favorite).setVisible(!drawerOpen);
		menu.findItem(R.id.action_browser).setVisible(!drawerOpen);
	}

	private void initActionBar() {
		// Get action bar instance
		actionBar = getActivity().getActionBar();

		// Enable the action bar home/up button
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Set custom fading drawable
		actionBarDrawable = new ColorDrawable(getResources().getColor(R.color.action_background));
		actionBarDrawable.setAlpha(0);
		actionBar.setBackgroundDrawable(actionBarDrawable);

		// Older versions require this callback
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			actionBarDrawable.setCallback( new Drawable.Callback() {
				@Override
				public void invalidateDrawable(Drawable who) {
					actionBar.setBackgroundDrawable(who);
				}

				@Override
				public void scheduleDrawable(Drawable who, Runnable what, long when) {
				}

				@Override
				public void unscheduleDrawable(Drawable who, Runnable what) {
				}
			});
		}
	}

	private void initViews(View v) {
		mScroll = (PkScrollView) v.findViewById(R.id.stickyScroll);
		tagContainer = (FlowLayout) v.findViewById(R.id.tagContainer);
		imgContainer = (FrameLayout) v.findViewById(R.id.imgContainer);
		imgBanner = (ImageView) v.findViewById(R.id.imgBanner);
		txtTitle = (TextView) v.findViewById(R.id.txtTitle);
		txtAuthor = (TextView) v.findViewById(R.id.txtAuthor);
		txtDate = (TextView) v.findViewById(R.id.txtDate);
		webView = (WebView) v.findViewById(R.id.webView);
	}

	private void retrieveContent() {
		Bundle bundle = getArguments();
		article = bundle.getParcelable(PkRSS.KEY_ARTICLE);
	}

	private void showContent() {
		// No content available; end here
		if(article == null) {
			Toast.makeText(getActivity(), "Error retrieving article!", Toast.LENGTH_SHORT).show();
			return;
		}

		// Attempt to configure share
		setupShare();

		// Asynchronously load the banner image
		Picasso.with(getActivity()).load(article.getImage()).placeholder(R.drawable.placeholder).into(imgBanner);

		// Expand banner upon click
		imgContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialogs.getImageDialog(getActivity(), article.getImage()).show();
			}
		});

		// Set header text
		txtTitle.setText(article.getTitle());
		txtAuthor.setText(article.getAuthor());
		txtDate.setText(Utils.getRelativeDate(article.getDate()));

		// Load actual article content async, after the view is drawn
		webView.loadDataWithBaseURL(base, getCleanContent(), mime, encoding, history);

		// End here if tags are already loaded (Prevent duplicates)
		if(tagContainer.getChildCount() > 0)
			return;

		// Loop through tags to create views and add them to the container
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		for(String tag : article.getTags()) {
			Button btnTag = (Button) inflater.inflate(R.layout.tag, tagContainer, false);
			btnTag.setText(tag);
			tagContainer.addView(btnTag);
		}
	}

	private void parallaxBanner()
	{
		Rect rect = new Rect();
		imgBanner.getLocalVisibleRect(rect);
		if (lastTopValue != rect.top){
			lastTopValue = rect.top;
			imgBanner.setY((float) (rect.top/2.0));
		}
	}

	private void fadeActionBar(int y) {
		headerHeight = imgBanner.getHeight() - actionBar.getHeight();
		ratio = (float) Math.min(Math.max(y, 0), headerHeight) / headerHeight;
		newAlpha = (int) (ratio * 255);
		actionBarDrawable.setAlpha(newAlpha);
	}

	private void setupWebView() {
		// Chrome Client for dynamic content
		webView.setWebChromeClient(new WebChromeClient());

		// Enable javascript & set vertical only
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		webView.setHorizontalScrollBarEnabled(false);

		// Make sure it doesn't move horizontally
		webView.setOnTouchListener(new View.OnTouchListener() {
			private float downX;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						// Save X value
						downX = event.getX();
					}
					break;
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP: {
						// Set X so it doesn't move
						event.setLocation(downX, event.getY());
					}
					break;
				}

				return false;
			}
		});

		// Catch content clicks and handle them appropriately
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (Utils.containsImage(url))
					Dialogs.getImageDialog(getActivity(), Uri.parse(url)).show();
				else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

				return true;
			}
		});

		// Workaround to fix background color
		webView.setBackgroundColor(getResources().getColor(R.color.app_background2));
	}

	/**
	 * Sets up the share intent.
	 * Will return prematurely if either the ShareActionProvider
	 * or current article are null/invalid.
	 */
	private void setupShare() {
		// Can't configure if null
		if (mShareActionProvider == null)
			return;

		// This won't do any good without data to use
		if(article == null || article.getSource() == null)
			return;

		// Build share content text
		String shareText = article.getTitle() + "\n\n" + article.getSource().toString();

		// Create and set the share intent
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
		mShareActionProvider.setShareIntent(shareIntent);
	}

	private String getCleanContent() {
		if(article == null)
			return null;

		return "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" /></head><body>" + article.getContent() + "</body></head>";
	}

	public static ArticleFragment newInstance(Article article)
	{
		ArticleFragment mFragment = new ArticleFragment();
		Bundle args = new Bundle();
		args.putParcelable(PkRSS.KEY_ARTICLE, article);
		mFragment.setArguments(args);
		return mFragment;
	}
}