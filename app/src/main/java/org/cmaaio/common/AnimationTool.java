package org.cmaaio.common;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

public class AnimationTool {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public void moveView(View target, int xOff, int yOff, long duration,
			AnimationToolListener listener) {
		final View targetView = target;
		final int xOffView = xOff;
		final int yOffView = yOff;
		final AnimationToolListener listenerView = listener;
		TranslateAnimation move = new TranslateAnimation(0, xOffView, 0,
				yOffView);
		move.setDuration(duration);
		move.setRepeatCount(0);
		move.setFillAfter(true);
		move.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				targetView.clearAnimation();
				int left = targetView.getLeft() + xOffView;
				int top = targetView.getTop() + yOffView;
				targetView.layout(left,
						top, targetView.getWidth()+left,
						targetView.getHeight()+top);
				if (listenerView != null)
					listenerView.onAnimationEnd();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}
		});
		targetView.startAnimation(move);
	}

	public void AlphaView(View target, float fromAlpha, float toAlpha,
			long duration, AnimationToolListener listener) {
		final View targetView = target;
		final float fromAlphaView = fromAlpha;
		final float toAlphaView = toAlpha;
		final AnimationToolListener listenerView = listener;
		AlphaAnimation alpha = new AlphaAnimation(fromAlphaView, toAlphaView);
		alpha.setDuration(duration);
		alpha.setRepeatCount(0);
		alpha.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				targetView.clearAnimation();
				targetView.getBackground().setAlpha((int) (255 * toAlphaView));
				if (listenerView != null)
					listenerView.onAnimationEnd();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}
		});
		targetView.startAnimation(alpha);
	}
}
