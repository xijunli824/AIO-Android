package org.cmaaio.activity;

import org.cmaaio.util.ActivityManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.LinearLayout;
/***
 * ???????
 * @author Administrator
 *
 */
public class WelcomeActivity extends Activity {
    /** Called when the activity is first created. */
	private LinearLayout linear_layout;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.welcome);
        linear_layout =(LinearLayout)this.findViewById(R.id.linear_layout);
        DelayGo();
        
        ActivityManager.getInstance().addActivity(this);
    }
	 //延时跳转到主界面
	 private void DelayGo()
	 {
			new Handler().postDelayed(new Runnable() {
			public void run() {
				 Intent intent = new Intent();        
		         intent.setClass(WelcomeActivity.this, LoginActivity.class);
		         startActivity(intent);
		         WelcomeActivity.this.finish();
			}
		}, 3000);
	 }
}