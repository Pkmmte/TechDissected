package com.pkmmte.techdissected.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class PkImageView extends ImageView {
	OnImageChangeListener onImageChangeListener;

	public PkImageView(Context context) {
		super(context);
	}

	public PkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnImageChangeListener(OnImageChangeListener listener) {
		this.onImageChangeListener = listener;
	}

	@Override public void setImageResource(int resId) {
		super.setImageResource(resId);
		Log.e("PkImageView", "new resource " + (onImageChangeListener != null));
		if (onImageChangeListener != null)
			onImageChangeListener.imageChanged();
	}

	@Override public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		Log.e("PkImageView", "new uri " + (onImageChangeListener != null));
		if (onImageChangeListener != null)
			onImageChangeListener.imageChanged();
	}

	@Override public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		Log.e("PkImageView", "new bitmap " + (onImageChangeListener != null));
		if (onImageChangeListener != null)
			onImageChangeListener.imageChanged();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		Log.e("PkImageView", "new drawable " + (onImageChangeListener != null));
		if (onImageChangeListener != null)
			onImageChangeListener.imageChanged();
	}

	public Bitmap getImageBitmap() {
		Drawable drawable = getDrawable();

		if (drawable == null) { // Don't do anything without a proper drawable
			return null;
		}
		else if (drawable instanceof BitmapDrawable) { // Use the getBitmap() method instead if BitmapDrawable
			return ((BitmapDrawable) drawable).getBitmap();
		}

		// Create Bitmap object out of the drawable
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static interface OnImageChangeListener {
		public void imageChanged();
	}
}