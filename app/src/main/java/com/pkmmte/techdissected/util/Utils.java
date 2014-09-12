package com.pkmmte.techdissected.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import com.pkmmte.pkrss.PkRSS;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.lang.reflect.Field;

public class Utils {
	public static void buildSingleton(Context context) {
		new PkRSS.Builder(context).handler(new Handler(Looper.getMainLooper())).buildSingleton();
	}

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