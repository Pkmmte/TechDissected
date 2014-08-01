package com.pkmmte.techdissected.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ShareActionProvider;
import com.pkmmte.techdissected.R;
import java.lang.reflect.Method;

public class CustomShareActionProvider extends ShareActionProvider
{
	private Context mContext;

	public CustomShareActionProvider(Context context)
	{
		super(context);
		this.mContext = context;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public View onCreateActionView()
	{
		View chooserView = super.onCreateActionView();

		// Set your drawable here
		Drawable icon = mContext.getResources().getDrawable(R.drawable.ic_action_share);
		Class clazz = chooserView.getClass();

		// Reflect all of this shit so that we can change the icon
		try {
			Method method = clazz.getMethod("setExpandActivityOverflowButtonDrawable", Drawable.class);
			method.invoke(chooserView, icon);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return chooserView;
	}
}