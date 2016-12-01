package org.cmaaio.httputil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;
import org.cmaaio.common.Constants;
import org.cmaaio.common.FileOp;
import org.cmaaio.db.SharedPrefsConfig;
import org.cmaaio.ssl.EasySSLSocketFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public class HttpUtil {
	public Context context = null;
	public String msg = null;
	public IResult iResult = null;
	public boolean isShowDialog = true;

	public String postStrToJson(String postStr) {
		if (postStr.length() == 0)
			return "";
		String result = "";
		String[] parList = postStr.split("&", -1);
		result = "{";
		for (int i = 0; i < parList.length; i++) {
			int index = parList[i].indexOf("=");
			String key = parList[i].substring(0, index);
			String value = parList[i].substring(index + 1, parList[i].length());
			if (i == parList.length - 1)
				result += "\"" + key + "\":\"" + value + "\"";
			else
				result += "\"" + key + "\":\"" + value + "\",";
		}
		result = result + "}";
		return result;
	}

	private DataTask createTask(boolean isPost) {
		DataTask task = new DataTask();
		task.iResult = iResult;
		task.context = context;
		task.isPost = isPost;
		task.isShowDialog = this.isShowDialog;
		if (msg != null)
			task.progressMsg = msg;
		return task;
	}

	// 登录
	public void httpLogin(String userName, String passWd) {
		try {
			// URLEncoder.encode(token,"UTF-8")
			String token = FileOp.DESencode(Constants.DESKey, Constants.DESIV,
					"dc|" + userName.replace("dc/", "") + "|" + passWd);
			String postStr = "tokenkey=" + token + "&OS=Android&OSVersion="
					+ Constants.OSVersion;
			DataTask task = createTask(true);
			task.execute(Constants.KServerurl + "Login", postStrToJson(postStr));
		} catch (Exception e) {

		}
	}
	// add begin lhy 2013/08/20

	// 获取搜索联系人或应用程序
	public void httpGetAppinfoAndEmploy(String key) {
		try {
			String userName = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getUserName();
			String userPsw = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getPassWord();
			String token = FileOp.DESencode(Constants.DESKey, Constants.DESIV,
					"dc|" + userName.replace("dc/", "") + "|" + userPsw);

			String postStr = "tokenkey=" + token + "&key=" + key + "&OS="
					+ "Android" + "&Userid=" + userName;
			DataTask task = createTask(true);
			task.execute(Constants.KServerurl + "GetAppinfoAndEmploy",
					postStrToJson(postStr));
		} catch (Exception e) {

		}
	}

	// 获取app列表
	public void httpGetAppList() {
		try {
			String userName = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getUserName();
			String userPsw = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getPassWord();
			String token = FileOp.DESencode(Constants.DESKey, Constants.DESIV,
					"dc|" + userName.replace("dc/", "") + "|" + userPsw);

			// String postStr = "userid=" + userName + "&tokenkey=" + token +
			// "&OS=Android";
			String postStr = "userid=" + userName + "&tokenkey=" + token
					+ "&OS=" + "Android";
			DataTask task = createTask(true);
			task.execute(Constants.KServerurl + "GetAppInfo",
					postStrToJson(postStr));
		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param num
	 */
	public void modifyPhoneNumber(String num) {
		try {
			String userName = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getUserName();
			String userPsw = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getPassWord();
			String token = FileOp.DESencode(Constants.DESKey, Constants.DESIV,
					"dc|" + userName.replace("dc/", "") + "|" + userPsw);

			String postStr = "tokenkey=" + token + "&OS="
					+ "Android&userADCount=" + userName + "&telPhone=" + num;
			DataTask task = createTask(true);
			task.execute(Constants.KServerurl + "EditTellPhone",
					postStrToJson(postStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param isPost
	 *            // true:post请求,false:http请求
	 * @param serverUrl
	 * @param postStr
	 */
	public void executeTask(boolean isPost, String urlName, String postStrOther) {
		try {

			String userName = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getUserName();
			String userPsw = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getPassWord();
			String token = FileOp.DESencode(Constants.DESKey, Constants.DESIV,
					"dc|" + userName.replace("dc/", "") + "|" + userPsw);

			String postStr = "tokenkey=" + token + postStrOther;
			DataTask task = createTask(isPost);
			task.execute(Constants.KServerurl + urlName, postStrToJson(postStr));
		} catch (Exception e) {

		}
	}

	public void executeOtherUrl(boolean isPost, String url, String postString) {
		try {

			String userName = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getUserName();
			String userPsw = SharedPrefsConfig.getSharedPrefsInstance(context)
					.getPassWord();
			String token = FileOp.DESencode(Constants.DESKey, Constants.DESIV,
					"dc|" + userName.replace("dc/", "") + "|" + userPsw);

			String postStr = "tokenkey=" + token + postString;
			DataTask task = createTask(isPost);
			task.execute(url, postStrToJson(postStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// add end 2013/06/20
	public void registPush(String postStr) {
		executeTask(true, "User", postStr);
	}
}

class DataTask extends AsyncTask<String, Integer, String> {
	public Context context = null;
	public IResult iResult = null;
	public boolean isPost = false;
	private ProgressDialog dialog = null;
	public String progressMsg = Cmaaio.getInstance().getString(R.string.upload);
	private int flag = 0;
	public boolean isShowDialog = true;

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框
		if (isShowDialog) {
			if (context != null) {
				try {
					dialog = ProgressDialog.show(context, Cmaaio.getInstance()
							.getString(R.string.prompt), progressMsg, true,
							true);
					dialog.setOnCancelListener(new OnCancelListener() {
						public void onCancel(DialogInterface arg0) {
							DataTask.this.cancel(true);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度

	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		// params[0]请求url
		// params[1]post请求的postdata
		InputStream is = null;
		String result = "";
		try {
			if (isPost) {
				HttpClient httpclient = getClient(params[0]);
				HttpPost httppost = new HttpPost(params[0]);
				StringEntity httpbody = new StringEntity(params[1], HTTP.UTF_8);
				httppost.setEntity(httpbody);
				httppost.setHeader("Content-Type", "application/json");
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} else {
				HttpClient httpclient = getClient(params[0]);
				HttpGet httpget = new HttpGet(params[0]);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
		} catch (Exception e) {
			flag = -1;
			return Constants.NetFail;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			flag = -2;
			return "流数据转换失败!" + e.toString();
		}
		flag = 0;
		return result;
	}

	private HttpClient getClient(String url) {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient httpclient = null;
		if (url.contains("https://")) {
			HttpParams par = new BasicHttpParams();
			HttpConnectionParams.setSoTimeout(par, 10000);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), 443));
			ClientConnectionManager connManager = new ThreadSafeClientConnManager(
					par, schemeRegistry);
			httpclient = new DefaultHttpClient(connManager, par);
		} else
			httpclient = new DefaultHttpClient(httpParams);
		return httpclient;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (dialog != null) {
			dialog.dismiss();
		}
		if (iResult != null)
			iResult.OnCacnel();
	}

	@Override
	protected void onPostExecute(String r) {
		// 任务完成
		if (iResult != null) {
			if (flag == 0) {
				iResult.OnResult(r);
			} else {
				iResult.OnFail(r);
			}
		}
		if (dialog != null)
			dialog.dismiss();
	}
}
