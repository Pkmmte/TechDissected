package com.pkmmte.techdissected.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.CreditsLibraryAdapter;
import com.pkmmte.techdissected.model.CreditsLibraryItem;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import uk.co.senab.photoview.PhotoViewAttacher;

public class Utils {
	public static CharSequence getRelativeDate(long date) {
		return DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, 0);
	}

	public static boolean containsImage(String encoded) {
		String str = encoded.toLowerCase();
		return (str.contains(".jpg")
			 || str.contains(".png")
			 || str.contains(".gif")
			 || str.contains(".webp")
			 || str.contains(".jpeg"));
	}

	/**
	 * Deletes the specified directory.
	 * Returns true if successful, false if not.
	 *
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir)
	{
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				if (!deleteDir(new File(dir, children[i])))
					return false;
			}
		}
		return dir.delete();
	}

	public static boolean clearCache(Context context)
	{
		boolean result = false;

		LruCache pCache = getPicassoCache(context);
		if(pCache != null) {
			pCache.clear();
			result = true;
		}

		File cacheDir = context.getCacheDir();
		if(cacheDir != null && cacheDir.isDirectory())
			result = deleteDir(cacheDir);

		return result;
	}

	public static long getAppCacheSize(Context context)
	{
		long size = 0;

		File[] fileList = context.getCacheDir().listFiles();
		for (File mFile : fileList)
			size = size + mFile.length();

		return size;
	}

	/**
	 * For some reason, Picasso doesn't allow us to access its
	 * cache. I guess we'll just have to force our way in through
	 * Java reflection. Returns null if unsuccessful.
	 * <br>
	 * This may stop working in future versions of the Picasso library.
	 *
	 * @param context
	 * @return
	 */
	public static LruCache getPicassoCache(final Context context)
	{
		try {
			Field cacheField = Picasso.class.getDeclaredField("cache");
			cacheField.setAccessible(true);
			LruCache cache = (LruCache) cacheField.get(Picasso.with(context));
			return cache;
		}
		catch(Exception e) {
			return null;
		}
	}

	public static long getTotalCacheSize(Context context)
	{
		try {
			return (getAppCacheSize(context) + getPicassoCache(context).size());
		}
		catch(Exception e) {
			return getAppCacheSize(context);
		}
	}

	@SuppressLint("DefaultLocale")
	public static String humanReadableByteCount(long bytes, boolean si)
	{
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";

		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");

		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density.
	 *
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);

		return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 *
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);

		return dp;
	}

	public static Dialog getImageDialog(final Context context, final Uri uri) {
		final Dialog mDialog = new Dialog(context, R.style.Dialog_Fullscreen);
		final ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mDialog.setContentView(imageView);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
		                              WindowManager.LayoutParams.MATCH_PARENT);

		new AsyncTask<Void, Void, Void>() {
			private Bitmap imageBitmap;
			private PhotoViewAttacher mAttacher;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				imageView.setImageResource(R.drawable.placeholder);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					imageBitmap = Picasso.with(context).load(uri).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void p) {
				imageView.setImageBitmap(imageBitmap);
				mAttacher = new PhotoViewAttacher(imageView);
				mAttacher.update();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return mDialog;
	}

	public static Dialog getAboutDialog(final Context context)
	{
		// Create dialog base
		final Dialog mDialog = new Dialog(context);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.dialog_dev);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setCancelable(true);

		mDialog.findViewById(R.id.imgAvatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(Constants.DEV_URL)));
			}
		});

		// Return the dialog object
		return mDialog;
	}

	public static Dialog getCreditsLibraryDialog(final Context context)
	{
		// Create & configure ListView
		ListView mList = new ListView(context);
		mList.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mList.setSelector(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
		mList.setClickable(true);
		mList.setDivider(null);
		mList.setDividerHeight(0);
		mList.setHorizontalScrollBarEnabled(false);
		mList.setVerticalScrollBarEnabled(false);
		mList.setPadding(0, (int) Utils.convertDpToPixel(24, context), 0, (int) Utils.convertDpToPixel(24, context));
		mList.setClipToPadding(false);

		// Create dialog base
		final Dialog mDialog = new Dialog(context, R.style.Dialog_Transparent);
		mDialog.setContentView(mList);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setCancelable(true);

		// Add items
		final CreditsLibraryAdapter mAdapter = new CreditsLibraryAdapter(context);
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_chrisbanes))
			                 .link(Uri.parse("https://github.com/chrisbanes/ActionBar-PullToRefresh"))
			                 .title("ActionBar-PullToRefresh")
			                 .author("Chris Banes")
			                 .license(getApacheLicense("Copyright 2013 Chris Banes\n\n"))
			                 .build());
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_chrisbanes))
			                 .link(Uri.parse("https://github.com/chrisbanes/PhotoView"))
			                 .title("PhotoView")
			                 .author("Chris Banes")
			                 .license(getApacheLicense("Copyright 2011, 2012 Chris Banes\n\n"))
			                 .build());
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_pkmmte))
			                 .link(Uri.parse("https://github.com/Pkmmte/PkRSS"))
			                 .title("PkRSS")
			                 .author("Pkmmte")
			                 .license(getApacheLicense("Copyright 2014 Pkmmte Xeleon.\n\n"))
			                 .build());
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_square))
			                 .link(Uri.parse("https://github.com/square/picasso"))
			                 .title("Picasso")
			                 .author("Square")
			                 .license(getApacheLicense("Copyright 2013 Square, Inc.\n\n"))
			                 .build());
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_emilsjolander))
			                 .link(Uri.parse("https://github.com/emilsjolander/StickyScrollViewItems"))
			                 .title("StickyScrollViewItems")
			                 .author("emilsjolander")
			                 .license(getApacheLicense())
			                 .build());


		mList.setAdapter(mAdapter);

		mAdapter.setOnAvatarClickListener(new CreditsLibraryAdapter.onAvatarClickListener() {
			@Override
			public void onClick(Uri link) {
				context.startActivity(new Intent(Intent.ACTION_VIEW).setData(link));
			}
		});

		// Return the dialog object
		return mDialog;
	}

	public static Uri resToUri(Context context, int resId)
	{
		return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
	}

	public static String getApacheLicense()
	{
		return getApacheLicense(null);
	}

	public static String getApacheLicense(String header)
	{
		StringBuilder builder = new StringBuilder();

		if(header != null)
			builder.append(header);
		builder.append("Licensed under the Apache License, Version 2.0 (the \"License\");");
		builder.append("you may not use this file except in compliance with the License.");
		builder.append("You may obtain a copy of the License at\n\n");
		builder.append("   http://www.apache.org/licenses/LICENSE-2.0\n\n");
		builder.append("Unless required by applicable law or agreed to in writing, software");
		builder.append("distributed under the License is distributed on an \"AS IS\" BASIS,");
		builder.append("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
		builder.append("See the License for the specific language governing permissions and");
		builder.append("limitations under the License.");

		return builder.toString();
	}
}