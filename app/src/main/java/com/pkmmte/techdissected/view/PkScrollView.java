package com.pkmmte.techdissected.view;

import android.content.Context;
import android.util.AttributeSet;
import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;

public class PkScrollView extends StickyScrollView {
	private PkScrollViewListener mScrollListener = null;

	public PkScrollView(Context context)
	{
		super(context);
	}

	public PkScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PkScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void setOnScrollListener(PkScrollViewListener scrollListener)
	{
		this.mScrollListener = scrollListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy)
	{
		super.onScrollChanged(x, y, oldx, oldy);
		if(mScrollListener != null) {
			mScrollListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	public interface PkScrollViewListener
	{
		void onScrollChanged(PkScrollView scrollView, int x, int y, int oldx, int oldy);
	}
}