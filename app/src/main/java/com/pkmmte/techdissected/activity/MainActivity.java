package com.pkmmte.techdissected.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.pkmmte.pkrss.Category;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.NavDrawerAdapter;
import com.pkmmte.techdissected.fragment.AboutFragment;
import com.pkmmte.techdissected.fragment.FavoritesFragment;
import com.pkmmte.techdissected.fragment.FeedFragment;
import com.pkmmte.techdissected.fragment.SettingsFragment;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.IabHelper;
import com.pkmmte.techdissected.util.IabResult;
import com.pkmmte.techdissected.util.Purchase;
import com.pkmmte.techdissected.util.Utils;
import com.pkmmte.techdissected.view.PkDrawerLayout;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
	// Action Bar
	private ActionBar actionBar;
	private String mTitle;

	// SystemBarTintManager & Configuration
	private SystemBarTintManager mTintManager;

	// Navigation Drawer
	private ActionBarDrawerToggle mDrawerToggle;
	private PkDrawerLayout mDrawerLayout;
	private NavDrawerAdapter mDrawerAdapter;
	private ListView mDrawerList;

	// Manager & Current Fragment
	private FragmentManager fragmentManager;
	private FeedFragment currentCategory;

	// Donation Helper Class & View
	private IabHelper mHelper;
	private View btnDonate;

	// Loaded status
	private boolean contentLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Build Custom Singleton
		Utils.buildSingleton(this);

		// [DEBUG] Enable logging for debugging purposes
		PkRSS.with(this).setLoggingEnabled(true);

		// Initialize basics
		initActionBar();
		initViews();
		initTranslucent();
		initNavDrawer();
		fragmentManager = getSupportFragmentManager();

		if(handleIntent())
			return;

		// Select default category
		selectCategory(0);

		// Set up donate function, if possible
		try {
			setupDonate();
		} catch (Exception e) {
			btnDonate.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(!contentLoaded)
			selectCategory(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Unbind IAP service, if possible
		try {
			if (mHelper != null)
				mHelper.dispose();
			mHelper = null;
		} catch (Exception e) { }
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try {
			if(currentCategory != null)
				currentCategory.toggleActionItems(menu, mDrawerLayout.isDrawerOpen(mDrawerList));
		} catch (Exception e) {}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}

	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setLogo(R.drawable.action_icon);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		if(mTitle != null)
			actionBar.setTitle(mTitle);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void initTranslucent()
	{
		// Return if user isn't on a version that supports this feature yet
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			return;

		// Set translucency window flags
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		// Initialize your Tint Manager
		mTintManager = new SystemBarTintManager(this);

		// Enable status bar tint and set to resource
		mTintManager.setStatusBarTintEnabled(true);
		mTintManager.setStatusBarTintColor(getResources().getColor(R.color.action_background));

		// Uncomment this line if you'd like to tint the nav bar as well
		//tintManager.setNavigationBarTintEnabled(true);

		// Set paddings & margins to main layout so they don't overlap the action/status bar
		SystemBarTintManager.SystemBarConfig config = mTintManager.getConfig();
		int actionBarSize = getResources().getDimensionPixelSize(R.dimen.action_height);
		mDrawerList.setPadding(0, actionBarSize + config.getStatusBarHeight(), 0, config.getPixelInsetBottom());
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) findViewById(R.id.feedContainer).getLayoutParams();
		params.setMargins(0, actionBarSize + config.getStatusBarHeight(), config.getPixelInsetRight(), 0);
	}

	private void initViews() {
		mDrawerLayout = (PkDrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
	}

	private void initNavDrawer() {
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.color.transparent, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				actionBar.setTitle(mTitle);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle(getString(R.string.app_name));
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		//mDrawerToggle.setDrawerIndicatorEnabled(false);

		mDrawerAdapter = new NavDrawerAdapter(this, Constants.CATEGORIES);

		// Add header
		View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_header, mDrawerList, false);
		mDrawerList.addHeaderView(headerView, null, false);

		// Add footer
		View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_footer, mDrawerList, false);
		mDrawerList.addFooterView(footerView, null, false);

		// Set list adapter
		mDrawerList.setAdapter(mDrawerAdapter);

		// Set OnClick Listeners
		mDrawerList.setOnItemClickListener(this);
		headerView.findViewById(R.id.btnFavorites).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new FavoritesFragment()).commit();

				mTitle = "Favorites";
				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
		headerView.findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new SettingsFragment()).commit();

				mTitle = "Settings";
				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
		footerView.findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragmentManager.beginTransaction().replace(R.id.feedContainer, new AboutFragment()).commit();

				mTitle = "About";
				mDrawerAdapter.setCurrentPage(-1);
				mDrawerLayout.closeDrawers();
			}
		});
		btnDonate = footerView.findViewById(R.id.btnDonate);
		btnDonate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mHelper.launchPurchaseFlow(MainActivity.this, "donate", 10001, new IabHelper.OnIabPurchaseFinishedListener() {
						public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
							if (result.isFailure()) {
								Toast.makeText(MainActivity.this,
								               "Error donating... :(",
								               Toast.LENGTH_LONG).show();
								return;
							}
							else if (purchase.getSku()
								.equals("donate")) {
								mHelper.consumeAsync(purchase, null);
							}
						}
					}, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, "Error donating... :(", Toast.LENGTH_LONG)
						.show();
				}
			}
		});
		footerView.findViewById(R.id.btnPKRSS).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.PKRSS_URL)));
			}
		});
	}

	protected void selectCategory(int position) {
		contentLoaded = true;
		Category category = mDrawerAdapter.getItem(position);
		mTitle = category.getName();
		actionBar.setTitle(mTitle);

		currentCategory = FeedFragment.newInstance(category);
		fragmentManager.beginTransaction().replace(R.id.feedContainer, currentCategory).commit();

		mDrawerAdapter.setCurrentPage(position);
		mDrawerLayout.closeDrawers();
	}

	private void setupDonate() {
		mHelper = new IabHelper(this.getApplicationContext(), getString(R.string.public_license_key));
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d("TAG", "Problem setting up In-app Billing: " + result);
				}
				Log.d("TAG", "Hooray, IAB is fully set up!");
			}
		});
	}

	protected boolean handleIntent() {
		// Get the launch intent
		final Intent launchIntent = getIntent();

		// Return false if intent is invalid
		if(launchIntent == null || launchIntent.getAction() == null)
			return false;

		// Handle intent appropriately
		if (launchIntent.getAction().equals(Intent.ACTION_VIEW)) {
			// Create ArticleActivity intent containing the received URL String
			Intent intent = new Intent(this, ArticleActivity.class);
			intent.putExtra(PkRSS.KEY_ARTICLE_URL, launchIntent.getDataString());

			// Clear launch intent action & start article activity
			setIntent(launchIntent.setAction(null));
			startActivity(intent);

			// Return true to indicate we handled it
			return true;
		}

		// Nothing was done, return false
		return false;
	}

	/**
	 * Returns the SystemBarTintManager instance for
	 * modifying tint or retrieving values inside subclasses.
	 *
	 * @return
	 */
	public SystemBarTintManager getTintManager()
	{
		return mTintManager;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectCategory(position - mDrawerList.getHeaderViewsCount());
	}
}