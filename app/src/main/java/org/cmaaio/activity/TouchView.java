package org.cmaaio.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {
	private Bitmap mBitmap;
	private Bitmap bitmap;
	private Canvas mCanvas;
	private Path mPath;
	private int y = 0;
	private DisplayMetrics dm;
	private Paint mBitmapPaint;// 画布的画笔
	private Paint mPaint;// 真实的画笔
	private float mX, mY;// 临时点坐标

	// 保存Path路径的集合,用List集合来模拟栈，用于后退步骤
	private static List<DrawPath> savePath;

	// 保存Path路径的集合,用List集合来模拟栈,用于前进步骤
	private static List<DrawPath> canclePath;

	// 记录Path路径的对象
	private DrawPath dp;

	private int screenWidth, screenHeight;// 屏幕長寬

	private class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
	}

	// 背景颜色
	// public static int color = Color.RED;
	public static int color = Color.parseColor("#fe0000");
	public static int srokeWidth = 4;

	/**
	 * 得到画笔
	 * 
	 * @return
	 */
	public Paint getPaint() {
		return mPaint;
	}

	/**
	 * 设置画笔
	 * 
	 * @param mPaint
	 */
	public void setPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	private void init(int w, int h) {
		screenWidth = Constant.bitMap.getWidth();
		screenHeight = Constant.bitMap.getHeight();
		Log.e("screen:", "w:" + screenWidth + ",h:" + screenHeight);
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		// 保存一次一次绘制出来的图形
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mCanvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
		mPaint.setStrokeWidth(srokeWidth);// 画笔宽度
		mPaint.setColor(color);

		savePath = new ArrayList<DrawPath>();
		canclePath = new ArrayList<DrawPath>();
	}

	private void initPaint() {
		Log.i("线程", "-------->>");
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
		mPaint.setStrokeWidth(srokeWidth);// 画笔宽度
		if (SaveDategram.y == 0) {
			mPaint.setColor(color);
		} else if (SaveDategram.y == 1) {
			mPaint.setColor(Color.BLACK);
		} else if (SaveDategram.y == 2) {
			mPaint.setColor(Color.GREEN);
		} else if (SaveDategram.y == 3) {
			mPaint.setColor(Color.RED);
		} else if (SaveDategram.y == 4) {
			mPaint.setColor(Color.BLUE);
		}
	}

	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		if (bitmap != null) {
			bitmap.recycle();
			mBitmap.recycle();
			bitmap = null;
			mBitmap = null;
		}
		Resources resources = getResources();
		if (!SaveDategram.sdImage.equals("")) {
			bitmap = BitmapAmplification(SaveDategram.sdImage);
			init(bitmap.getWidth(), bitmap.getHeight());
		} else {
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.mask);
			init(bitmap.getWidth(), bitmap.getHeight());
		}

	}

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		if (bitmap != null) {
			bitmap.recycle();
			mBitmap.recycle();
			bitmap = null;
			mBitmap = null;
		}
		Resources resources = getResources();
		if (!SaveDategram.sdImage.equals("")) {
			bitmap = BitmapAmplification(SaveDategram.sdImage);
			init(bitmap.getWidth(), bitmap.getHeight());
		} else {
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.mask);
			init(bitmap.getWidth(), bitmap.getHeight());
		}

	}

	public TouchView(Context context, int w, int h) {
		super(context);
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		if (bitmap != null) {
			bitmap.recycle();
			mBitmap.recycle();
			bitmap = null;
			mBitmap = null;
		}
		Resources resources = getResources();
		if (!SaveDategram.sdImage.equals("")) {
			bitmap = BitmapAmplification(SaveDategram.sdImage);
			init(bitmap.getWidth(), bitmap.getHeight());
		} else {
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.mask);
			init(bitmap.getWidth(), bitmap.getHeight());
		}

	}

	@Override
	public void onDraw(Canvas canvas) {
		// 背景颜色，这里颜色应该是
		// canvas.drawColor(color);
		// 将前面已经画过得显示出来
		canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		if (mPath != null) {
			// 实时的显示
			canvas.drawPath(mPath, mPaint);

		}
	}

	// 清空画布
	public void clear() {
		clearCanvas();// 清空画布
		if (savePath != null && savePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			savePath.removeAll(savePath);
		}
		invalidate();// 刷新

	}

	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public int undo() {
		fn(bitmap.getWidth(), bitmap.getHeight());
		if (savePath != null && savePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator(); // 重复保存
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
		} else {
			return -1;
		}
		return savePath.size();
	}

	/**
	 * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)， 然后从redo的集合里面取出最顶端对象， 画在画布上面即可。
	 */
	public int redo() {
		// 如果撤销你懂了的话，那就试试重做吧。
		if (canclePath.size() < 1)
			return canclePath.size();
		clearCanvas();
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉…
		if (canclePath != null && canclePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			DrawPath dPath = canclePath.get(canclePath.size() - 1);
			savePath.add(dPath);
			canclePath.remove(canclePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
		}
		return canclePath.size();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			initPaint();
			// 重置下一步操作
			canclePath = new ArrayList<DrawPath>();
			// 每次down下去重新new一个Path
			mPath = new Path();
			// 每一次记录的路径对象是不一样的
			dp = new DrawPath();
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	private void touch_start(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(mY - y);
		if (dx >= 4 || dy >= 4) {
			// 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		// 将一条完整的路径保存下来(相当于入栈操作)
		dp.path = mPath;
		dp.paint = mPaint;
		savePath.add(dp);
		mPath = null;// 重新置空
		// mPaint=null;
	}

	public void saveImage() {
		String ph = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(ph);
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(ph + "/nn.png");
			mBitmap.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {

		}

	}

	Bitmap bm1;
	Bitmap bp;
	Bitmap bitmap2 = null;

	public Bitmap BitmapAmplification(String path) {
		if (bm1 != null || !bm1.isRecycled()) {
			bm1.recycle();
			bm1 = null;

		}
		bm1 = BitmapFactory.decodeFile(path, new BitmapFactory.Options());
		return bm1;

		// if (bitmap2 != null) {
		// bitmap2.recycle();
		// bitmap2 = null;
		// if (bitmap != null) {
		// bitmap = null;
		// }
		// if (bm1 != null) {
		// bm1 = null;
		// }
		//
		// if (bp != null) {
		// bp = null;
		// }
		// }
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = true;
		// bm1 = BitmapFactory.decodeFile(path, options);
		// int scu = (int) options.outWidth / 550;
		// if (scu <= 0) {// 小于零
		// scu = 1;
		// }
		// options.inSampleSize = scu;
		// Log.v("nnn", "scu" + scu);
		// options.inJustDecodeBounds = false;
		// bitmap2 = BitmapFactory.decodeFile(path, options);
		// if (bitmap2 != null) {
		// int w = bitmap2.getWidth(); // 得到图片的宽度
		// int h = bitmap2.getHeight();// 得到图片的高度
		// Log.v("nnn", "宽" + w + "高" + h);
		// float ww = ((float) dm.widthPixels) / w;
		// float hh = ((float) dm.heightPixels) / h;
		// Matrix matrix = new Matrix();
		// matrix.postScale(ww, hh);
		// Log.v("nnn", "可以www");
		// Bitmap bp = Bitmap.createBitmap(bitmap2, 0, 0, w, h, matrix, true);
		// if (!bitmap2.isRecycled() && bitmap2 != null) {
		// bitmap2.recycle();
		// bitmap2 = null;
		// Log.v("nnn", "回收");
		// System.gc();
		// }
		// return bp;
		// }
		// return null;
	}

	public void fn(int w, int h) {
		screenWidth = w;
		screenHeight = h;
		clearCanvas();
		// 保存一次一次绘制出来的图形
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mCanvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
		mPaint.setStrokeWidth(srokeWidth);// 画笔宽度
		mPaint.setColor(color);
	}

	int k = 7;

	public void UPdataColor(int i) {
		if (i == 1) {
			if (mPaint == null) {
				initPaint();
			}

		} else if (i == 2) {

		} else if (i == 3) {

		} else if (i == 4) {
			k = k + 3;
			mPaint.setStrokeWidth(k);
		}
	}

	/**
	 * 清空画布的方法
	 */
	private void clearCanvas() {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		mCanvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		invalidate();
	}

}
