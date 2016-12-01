package org.cmaaio.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cmaaio.util.ActivityManager;
import org.cmaaio.util.CMATool;
import org.cmaaio.util.CommonUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

//import dalvik.system.VMRuntime;

/**
 * 涂鸦页面
 * 
 * @author Administrator
 */
public class SignatureActivity extends CMABaseActivity implements
		OnClickListener {

	private Context mContext;// add by shrimp

	private Dialog menuDialog;

	private Button clearButton = null;
	private Button txtButton = null;
	private Button redoButton = null;
	private Button colorButton = null;
	public static String sdImage = "";
	private TouchView touchView = null;
	private FrameLayout layout = null;
	private View view;

	private Button emailButton = null;
	private Button cancelButton = null;// modify by shrimp at 20130625 to back
	List<DTextView> textViews = new ArrayList<DTextView>();
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private final static int CWJ_HEAP_SIZE = 6 * 1024 * 1024;
	public BroadcastReceiver receiver;
	private String MyPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/ScrawlImage.png";
	private Paint mPaint = null;

	private static final String COLORTYPEBLACK = "colorTypeBlack";
	private static final String COLORTYPEWHITE = "colorTypeWhite";
	private static final String COLORTYPERED = "colorTypeRed";
	private static final String COLORTYPEBLUE = "colorTypeBlue";
	private String colorType = COLORTYPEBLACK;

	private Intent it;

	public void onCreate(Bundle savedInstanceState) {
		// VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		// VMRuntime.getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.FindID();
		super.initNavBar(getResources().getString(R.string.doodle),
				R.drawable.top_toolbar_button_selector);
		setRightBtn();

		init();
		
		ActivityManager.getInstance().addActivity(this);
	}

	private void init() {
		mContext = this;
		cancelButton = (Button) findViewById(R.id.backBtn);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		emailButton = (Button) findViewById(R.id.rightBtn);
		emailButton.setVisibility(View.VISIBLE);
		emailButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				savePic(takeScreenShot(SignatureActivity.this), MyPath);
				sendEmail(mContext, null, null, null, MyPath);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setRightBtn() {
		this.rightBtn.setText(R.string.Signature_send);
		this.rightBtn.setTextColor(Color.WHITE);
		// 解决版本不兼容crash的问题 (modify by zl)
		if (android.os.Build.VERSION.SDK_INT > 10) {
			this.rightBtn.setScaleX(1.0f);
			this.rightBtn.setScaleY(1.0f);
		}
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.rightBtn
				.getLayoutParams();
		lp.rightMargin = CMATool.dip2px(this, 10);
		this.rightBtn.setLayoutParams(lp);
	}

	/**
	 * 查找控件ID
	 */
	public void FindID() {
		layout = (FrameLayout) this.findViewById(R.id.layout);
		touchView = (TouchView) this.findViewById(R.id.touch);
		redoButton = (Button) this.findViewById(R.id.main_undo);// 撤销
		clearButton = (Button) this.findViewById(R.id.main_clear);
		txtButton = (Button) this.findViewById(R.id.txt);
		colorButton = (Button) this.findViewById(R.id.main_color);
		ViewTreeObserver vto = touchView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				setImageToTouch();
				touchView.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
			}
		});

		redoButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		txtButton.setOnClickListener(this);
		colorButton.setOnClickListener(this);
	}

	private void setImageToTouch() {
		if (Constant.bitMap != null) {
			int srcW = Constant.bitMap.getWidth();
			int srcH = Constant.bitMap.getHeight();
			int tarW = touchView.getWidth();
			int tarH = touchView.getHeight();
			float scaleX = (float) tarW / srcW;
			float scaleY = (float) tarH / srcH;
			Matrix matrix = new Matrix();
			float scale = scaleX < scaleY ? scaleX : scaleY;
			matrix.postScale(scale, scale);
			matrix.postTranslate((tarW - srcW * scale) / 2, 0);
			Bitmap resizedBitmap = Bitmap.createBitmap(tarW, tarH,
					Bitmap.Config.ARGB_8888);
			Canvas mCanvas = new Canvas(resizedBitmap);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			mCanvas.drawRect(new Rect(0, 0, tarW, tarH), paint);
			mCanvas.drawBitmap(Constant.bitMap, matrix, paint);
			Drawable drawable = new BitmapDrawable(resizedBitmap);
			if (drawable != null) {
				saveImage(Constant.bitMap);
				touchView.setBackgroundDrawable(drawable);
			}
		}
	}

	public void saveImage(Bitmap b) {
		String ph = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(ph);
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(ph + "/nn.png");
			b.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 保存到sdcard
	private static void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取指定Activity的截屏，保存到png文件
	private Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		Log.i("TAG", "" + statusBarHeight);
		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);

		view.destroyDrawingCache();
		return b;
	}

	public int sendMailByIntent() {
		String[] reciver = new String[] { "181712000@qq.com" };
		String[] mySbuject = new String[] { "test" };
		String myCc = "cc";

		String mybody = "测试Email Intent"; // here put image
		// Bitmap mybody = shot();

		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
		myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
		myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
		myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
		startActivity(Intent.createChooser(myIntent, "mail test"));

		return 1;

	}

	@Override
	public void onRightAction() {
		super.onRightAction();
	}

	/**
	 * 控件事件
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commitBt:// 邮件
			this.sendMailByIntent();
			break;
		case R.id.main_undo: // 撤销
			layout.removeAllViews();
			if (textViews != null) {
				textViews.clear();
			}
			touchView.clear();
			layout.addView(touchView);
			break;
		case R.id.main_color: // 颜色
			if (CommonUtil.isFastDoubleClick()) {
				return;
			}
			showDialog();
			break;
		case R.id.txt: // 文字
			if (CommonUtil.isFastDoubleClick()) {
				return;
			}
			showDialogText();
			break;
		case R.id.main_clear: // 清除
			touchView.undo();
			if (textViews != null && textViews.size() > 0) {
				textViews.remove(textViews.size() - 1);
			}
			refreshFrameLayout();
			break;
		case R.id.btn_black: // 黑色
			SaveDategram.y = 1;
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			break;
		case R.id.btn_green: // 绿色
			SaveDategram.y = 2;
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			break;
		case R.id.btn_red: // 红色
			SaveDategram.y = 3;

			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			break;
		case R.id.btn_blue: // 蓝色
			SaveDategram.y = 4;
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			break;
		case R.id.btn_cancel:
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}

			break;
		case R.id.btn_black_text:
			colorType = COLORTYPEBLACK;
			it = new Intent(SignatureActivity.this, TextEditActivity.class);
			startActivityForResult(it, 1);

			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}

			break;
		case R.id.btn_red_text:
			colorType = COLORTYPERED;
			it = new Intent(SignatureActivity.this, TextEditActivity.class);
			startActivityForResult(it, 1);
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}

			break;
		case R.id.btn_blue_text:
			colorType = COLORTYPEBLUE;
			it = new Intent(SignatureActivity.this, TextEditActivity.class);
			startActivityForResult(it, 1);
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}

			break;
		case R.id.btn_white_text:
			colorType = COLORTYPEWHITE;
			it = new Intent(SignatureActivity.this, TextEditActivity.class);
			startActivityForResult(it, 1);
			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}

			break;
		case R.id.btn_cancel_text:

			if (menuDialog != null && menuDialog.isShowing()) {
				menuDialog.dismiss();
			}
			break;
		}
	}

	private void showDialogText() {
		view = getLayoutInflater().inflate(R.layout.item_dialog_text, null);

		Button btnRedText = (Button) view.findViewById(R.id.btn_red_text);
		Button btnBlackText = (Button) view.findViewById(R.id.btn_black_text);
		Button btnWhiteText = (Button) view.findViewById(R.id.btn_white_text);
		Button btnBlueText = (Button) view.findViewById(R.id.btn_blue_text);
		Button btnCancelText = (Button) view.findViewById(R.id.btn_cancel_text);
		btnRedText.setOnClickListener(this);
		btnBlackText.setOnClickListener(this);
		btnWhiteText.setOnClickListener(this);
		btnBlueText.setOnClickListener(this);
		btnCancelText.setOnClickListener(this);
		menuDialog();

	}

	private void showDialog() {
		view = getLayoutInflater().inflate(R.layout.item_dialog, null);

		Button btnRed = (Button) view.findViewById(R.id.btn_red);
		Button btnBlack = (Button) view.findViewById(R.id.btn_black);
		Button btnBlue = (Button) view.findViewById(R.id.btn_blue);
		Button btnGreen = (Button) view.findViewById(R.id.btn_green);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
		btnRed.setOnClickListener(this);
		btnBlack.setOnClickListener(this);
		btnBlue.setOnClickListener(this);
		btnGreen.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		menuDialog();
	}

	private void menuDialog() {
		menuDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		menuDialog.setContentView(view, new LayoutParams(getWindowManager()
				.getDefaultDisplay().getWidth() * 95 / 100,
				LayoutParams.WRAP_CONTENT));
		Window window = menuDialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// 设置显示位置
		menuDialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		menuDialog.setCanceledOnTouchOutside(true);
		menuDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				String temp = null;
				Bundle bundle = data.getExtras();
				if (bundle != null)
					temp = bundle.getString("name");
				refreshFrameLayout();

				DTextView tvSecond = new DTextView(SignatureActivity.this, null);
				String tag = this.colorType;
				if (tag == COLORTYPEBLACK) {
					tvSecond.setTextColor(Color.BLACK);
				} else if (tag == COLORTYPEWHITE) {
					tvSecond.setTextColor(Color.WHITE);
				} else if (tag == COLORTYPERED) {
					tvSecond.setTextColor(Color.RED);
				} else if (tag == COLORTYPEBLUE) {

					tvSecond.setTextColor(Color.BLUE);
				}
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				tvSecond.setText(temp);

				tvSecond.setLayoutParams(params);
				textViews.add(tvSecond);
				layout.addView(tvSecond);
			}
			break;

		}

	}

	// add by shrimp at 20130625 for email
	private static void sendEmail(Context context, String[] to, String subject,
			String body, String path) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		if (to != null) {
			email.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		}
		if (subject != null) {
			email.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		}
		if (body != null) {
			email.putExtra(android.content.Intent.EXTRA_TEXT, body);
		}
		if (path != null) {
			File file = new File(path);
			email.putExtra(android.content.Intent.EXTRA_STREAM,
					Uri.fromFile(file));
			email.setType("image/png");
		}

		context.startActivity(Intent.createChooser(email, Cmaaio.getInstance()
				.getString(R.string.select_send_app)));
	}

	/**
	 * 
	 */
	private void refreshFrameLayout() {
		layout.removeAllViews();
		layout.addView(touchView);
		if (textViews != null && textViews.size() > 0) {

			Iterator<DTextView> iter = textViews.iterator();
			while (iter.hasNext()) {
				DTextView textView = iter.next();
				FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textView
						.getLayoutParams();
				params.leftMargin = textView.getCurrentLayout()[0];
				params.topMargin = textView.getCurrentLayout()[1];
				params.gravity = Gravity.LEFT | Gravity.TOP;
				textView.setLayoutParams(params);
				layout.addView(textView);
			}
		}
	}

}
