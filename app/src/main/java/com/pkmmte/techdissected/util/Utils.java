package com.pkmmte.techdissected.util;

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
import com.squareup.picasso.Picasso;
import java.io.IOException;
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