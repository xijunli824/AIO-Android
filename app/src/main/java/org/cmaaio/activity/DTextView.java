package org.cmaaio.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;


public class DTextView extends TextView {
	private static final String TAG = "qt";

	private int mPreviousx = 0;
	private int mPreviousy = 0;

	// a array for save the drag position
	private int[] mCurrentLayout = new int[4];

	public int[] getCurrentLayout() {
		return mCurrentLayout;
	}

	public DTextView(Context context, AttributeSet attribute) {
		this(context, attribute, 0);
	}

	public DTextView(Context context, AttributeSet attribute, int style) {
		super(context, attribute, style);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int iAction = event.getAction();
		final int iCurrentx = (int) event.getX();
		final int iCurrenty = (int) event.getY();
		switch (iAction) {
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
			final int iLeft = getLeft();
			final int iTop = getTop();
			if (iDeltx != 0 || iDelty != 0)
				layout(iLeft + iDeltx, iTop + iDelty, iLeft + iDeltx
						+ getWidth(), iTop + iDelty + getHeight());

			mCurrentLayout[0] = iLeft + iDeltx;
			mCurrentLayout[1] = iTop + iDelty;
			mCurrentLayout[2] = iLeft + iDeltx + getWidth();
			mCurrentLayout[3] = iTop + iDelty + getHeight();

			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_UP:
			FrameLayout parent = (FrameLayout) this.getParent();
			parent.removeView(this);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this
					.getLayoutParams();
			params.leftMargin = mCurrentLayout[0];
			params.topMargin = mCurrentLayout[1];
			params.gravity = Gravity.LEFT | Gravity.TOP;
			this.setLayoutParams(params);
			parent.addView(this);
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}
}
