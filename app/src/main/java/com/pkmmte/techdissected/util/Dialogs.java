package com.pkmmte.techdissected.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.adapter.CreditsLibraryAdapter;
import com.pkmmte.techdissected.model.CreditsLibraryItem;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.pkmmte.techdissected.util.Utils.getApacheLicense;
import static com.pkmmte.techdissected.util.Utils.resToUri;

public class Dialogs {


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

	public static Dialog getAboutDialog(final Context context) {
		// Create dialog base
		final Dialog mDialog = new Dialog(context);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.dialog_dev);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setCancelable(true);

		mDialog.findViewById(R.id.imgAvatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(Intent.ACTION_VIEW).setData(
					Uri.parse(Constants.DEV_URL)));
			}
		});

		// Return the dialog object
		return mDialog;
	}

	public static Dialog getCreditsLibraryDialog(final Context context) {
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
			                 .avatar(resToUri(context, R.drawable.credits_castorflex))
			                 .link(Uri.parse("https://github.com/castorflex/SmoothProgressBar"))
			                 .title("SmoothProgressBar")
			                 .author("castorflex")
			                 .license(getApacheLicense("Copyright 2014 Antoine Merle"))
			                 .build());
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_emilsjolander))
			                 .link(Uri.parse("https://github.com/emilsjolander/StickyScrollViewItems"))
			                 .title("StickyScrollViewItems")
			                 .author("emilsjolander")
			                 .license(getApacheLicense())
			                 .build());
		mAdapter.addItem(new CreditsLibraryItem.Builder()
			                 .avatar(resToUri(context, R.drawable.credits_jgilfelt))
			                 .link(Uri.parse("https://github.com/jgilfelt/SystemBarTint"))
			                 .title("SystemBarTint")
			                 .author("Jeff Gilfelt")
			                 .license(getApacheLicense("Copyright 2013 readyState Software Limited\n\n"))
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
}