package org.cmaaio.activity;

import org.cmaaio.util.ActivityManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TextEditActivity extends Activity implements OnClickListener {
	EditText redirect_sendmessage;
	private Button commitButton = null;
	private Button cancelButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.txt);
		redirect_sendmessage = (EditText) findViewById(R.id.redirect_sendmessage);
		commitButton = (Button) findViewById(R.id.commitBt);
		cancelButton = (Button) findViewById(R.id.backBt);
		cancelButton.setOnClickListener(this);
		commitButton.setOnClickListener(this);
		commitButton.setText(R.string.put_in);
		
		ActivityManager.getInstance().addActivity(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commitBt: // 提交
			String txt = redirect_sendmessage.getText().toString();
			if (txt.length() > 90) {
				txt = txt.substring(0, 90);
			}
			Intent intent = new Intent(TextEditActivity.this, SignatureActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", txt);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.backBt: // 取消
			finish();
			break;
		}

	}

}
