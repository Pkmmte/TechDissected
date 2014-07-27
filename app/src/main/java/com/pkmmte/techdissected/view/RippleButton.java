package com.pkmmte.techdissected.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Region;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import com.pkmmte.techdissected.R;

/**
 * Testing...
 */
public class RippleButton extends Button {

	private float mDownX;
	private float mDownY;

	private float mRadius;

	private Paint mPaint;
	private Paint mPaint2;

	public RippleButton(final Context context) {
		super(context);
		init();
	}

	public RippleButton(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RippleButton(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAlpha(100);
		mPaint2 = new Paint();
		mPaint2.setColor(getResources().getColor(R.color.action_background));
		mPaint2.setAlpha(127);
	}

	@Override
	public boolean onTouchEvent(@NonNull final MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mDownX = event.getX();
			mDownY = event.getY();

			ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", 0,
			                                                 getWidth() * 3.0f);
			animator.setInterpolator(new AccelerateInterpolator());
			animator.setDuration(2000);
			animator.start();
		}
		return super.onTouchEvent(event);
	}

	public void setRadius(final float radius) {
		mRadius = radius;
		if (mRadius > 0) {
			RadialGradient radialGradient = new RadialGradient(mDownX, mDownY,
			                                                   mRadius * 3, Color.TRANSPARENT, Color.BLACK,
			                                                   Shader.TileMode.CLAMP);
			//mPaint.
			mPaint.setShader(radialGradient);
		}
		invalidate();
	}

	private Path mPath = new Path();
	private Path mPath2 = new Path();

	@Override
	protected void onDraw(@NonNull final Canvas canvas) {
		super.onDraw(canvas);

		mPath2.reset();
		mPath2.addCircle(mDownX, mDownY, mRadius, Path.Direction.CW);

		canvas.clipPath(mPath2);

		mPath.reset();
		mPath.addCircle(mDownX, mDownY, mRadius / 3, Path.Direction.CW);

		canvas.clipPath(mPath, Region.Op.DIFFERENCE);

		canvas.drawCircle(mDownX, mDownY, mRadius, mPaint2);
	}
}