package com.pkmmte.techdissected.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.pkmmte.techdissected.R;
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
}