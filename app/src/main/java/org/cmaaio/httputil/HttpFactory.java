package org.cmaaio.httputil;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * HTTP请求工具类
 * 
 * @author wuhezhi
 * 
 * 
 */
public class HttpFactory {

	/**
	 * 带参数的get请求,结果返回Json格式
	 * 
	 * @param url
	 * @param params
	 * @param handler
	 */
	public static void get(final String url, final List<NameValuePair> params,
			final HttpResult handler) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... arg0) {
				HttpPost post = new HttpPost(url);
				try {
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse response = new DefaultHttpClient()
							.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						String result = EntityUtils.toString(response
								.getEntity());
						return result;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.d("whz", "onPostExecute result=" + result);
				handler.onFinsh();
				if (result != null) {
					handler.onSuccess(result);
				} else {
					handler.onFailure();
				}
			}
		}.execute();
	}

	/**
	 * 回调接口
	 * 
	 * @author golden
	 * 
	 */
	public interface HttpResult {
		public void onSuccess(String result);

		public void onFailure();

		public void onFinsh();
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
