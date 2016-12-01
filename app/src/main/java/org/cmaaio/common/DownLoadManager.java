package org.cmaaio.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.cmaaio.ssl.EasySSLSocketFactory;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

public class DownLoadManager {
	private static DownLoadManager instance = null;
	private DownLoadFile downLoadFile = new DownLoadFile();
	private static HashMap<String, Long> downUrlList = new HashMap<String, Long>();
	private ArrayList<DownloadTask> taskList = new ArrayList<DownloadTask>();
	private DownLoadListener listener = null;

	public DownLoadManager() {
		getDownList();
	}

	public ArrayList<DownloadTask> getCurrentTaskList() {
		return taskList;
	}

	public static DownLoadManager getInstance() {
		if (instance == null) {
			instance = new DownLoadManager();
		}
		return instance;
	}

	public void startDownLoad(String url) {
		if (url == null || url.length() == 0)
			return;
		if (url.contains(",") || url.contains(";"))
			Log.e("DownError", "url can not contain , ;");
		try {
			DownloadTask task = new DownloadTask();
			task.listener = this.listener;
			task.execute(url);
			taskList.add(task);
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}
	}

	public void stopDownLoad(String url) {
		if (url == null)
			return;
		synchronized (taskList) {

			for (DownloadTask task : taskList) {
				if (task.getUrl() != null)
					if (task.getUrl().equals(url)) {
						task.cancelTask();
						removeTask(url);
					}
			}
		}
	}

	public void stopAll() {
		synchronized (taskList) {
			for (DownloadTask task : taskList) {
				task.cancelTask();
			}
			taskList.clear();
		}
		
	}

	public void setDownListener(DownLoadListener listener) {
		this.listener = listener;
	}

	public boolean isDowning(String url) {
		boolean isRuning = false;
		synchronized (taskList) {
			for (DownloadTask task : taskList) {
				if (task.getUrl() == null)
					break;
				if (task.getUrl().equals(url) && !task.isCancelled()) {
					isRuning = true;
					break;
				}
			}
		}

		return isRuning;
	}

	public float getDownProcess(String url) {
		if (downUrlList.containsKey(url)) {
			long haveSize = downLoadFile.getFileSize(url);
			long totalSize = downUrlList.get(url);
			return (float) haveSize / (float) totalSize;
		} else
			return 0.0f;
	}

	private void saveDownList() {
		Log.e("downitem size:", "" + downUrlList.size());
		String downListPath = Environment.getExternalStorageDirectory()
				+ "/CMAAIO/" + Constants.userName + "/"
				+ Constants.DownLoadCache + "/";
		String downListName = "downing.list";
		String downList = "";
		Iterator<String> it = downUrlList.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			long process = downUrlList.get(key);
			downList += key + "," + process + ";";
		}
		File dataPath = new File(downListPath);
		if (!dataPath.exists())
			dataPath.mkdirs();
		File file = new File(downListPath + downListName);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		// 写入数据
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(downList.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getDownList() {
		String downListPath = Environment.getExternalStorageDirectory()
				+ "/CMAAIO/" + Constants.userName + "/"
				+ Constants.DownLoadCache + "/";
		String downListName = "downing.list";
		File file = new File(downListPath + downListName);
		if (file.exists()) {
			try {
				downUrlList.clear();
				FileInputStream in = new FileInputStream(file);
				int length = (int) file.length();
				byte[] temp = new byte[length];
				in.read(temp, 0, length);
				String data = EncodingUtils.getString(temp, "UTF-8");
				String[] items = data.split(";");
				for (int i = 0; i < items.length; i++) {
					String[] keyAndValue = items[i].split(",", -1);
					downUrlList.put(keyAndValue[0],
							Long.parseLong(keyAndValue[1]));
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void removeTask(String url) {
		String removeUrl = url;
		synchronized (taskList) {
			for (int i = 0; i < taskList.size(); i++) {
				DownloadTask task = taskList.get(i);
				if (task.getUrl() == null)
					break;
				if (task.getUrl().equals(removeUrl)) {
					taskList.remove(task);
					break;
				}
			}
		}

	}

	private void onFinish(String url) {
		removeTask(url);
	}

	class DownloadTask extends AsyncTask<String, Long, String> {
		private String downUrl = null;
		private long totalLen = 0;
		private boolean isCanRun = true;
		public DownLoadListener listener = null;

		public String getUrl() {
			return downUrl;
		}

		public void cancelTask() {
			isCanRun = false;
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			// 更新进度
			if (listener != null)
				listener.onProcess(downUrl, values[0], totalLen);
		}

		@Override
		protected String doInBackground(String... params) {
			downUrl = params[0];
			InputStream is = null;

			try {
				long haveSize = downLoadFile.getFileSize(downUrl);
				totalLen = downUrlList.get(downUrl) == null ? 0 : downUrlList
						.get(downUrl);
				if (haveSize > 0 && haveSize == totalLen) {
					if (listener != null)
						listener.onProcess(downUrl, haveSize, totalLen);
					onFinish(downUrl);
				} else {
					HttpParams httpParams = new BasicHttpParams();
					HttpConnectionParams.setSoTimeout(httpParams, 10000);
					HttpClient httpclient = null;
					if (downUrl.contains("https://")) {
						HttpParams par = new BasicHttpParams();
						HttpConnectionParams.setSoTimeout(par, 10000);
						SchemeRegistry schemeRegistry = new SchemeRegistry();
						schemeRegistry.register(new Scheme("https",
								new EasySSLSocketFactory(), 443));
						ClientConnectionManager connManager = new ThreadSafeClientConnManager(
								par, schemeRegistry);
						httpclient = new DefaultHttpClient(connManager, par);
					} else {
						httpclient = new DefaultHttpClient(httpParams);
					}

					HttpGet httpget = new HttpGet(downUrl);
					if (haveSize > 0)
						httpget.setHeader("Range", "bytes=" + haveSize + "-");
					HttpResponse response = httpclient.execute(httpget);

					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					totalLen = totalLen == 0 ? entity.getContentLength()
							: totalLen;
					// 保存正在下载列表进入磁盘
					downUrlList.put(downUrl, totalLen);
					saveDownList();
					if (is != null) {
						byte[] buf = new byte[4096];
						int len = -1;
						long haveRead = 0;
						while ((len = is.read(buf, 0, 4096)) != -1 && isCanRun) {
							haveRead += len;
							publishProgress(haveRead + haveSize);
							downLoadFile.saveFileToCache(downUrl, buf, 0, len,
									true);// 追加写入
						}
					}
					//onFinish(downUrl);
				}
			} catch (Exception e) {
				isCanRun=false;
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return downUrl;// onPostExecute 入参
		}

		@Override
		protected void onPostExecute(String result) {
			if (isCanRun) {// 任务正常结束
				onFinish(downUrl);
				if (listener != null)
					listener.onFinish(result,
							downLoadFile.getSDPathFromUrl(result));
			} else {
				removeTask(result);// 被停止
			}
			super.onPostExecute(result);
		}

	}
}
