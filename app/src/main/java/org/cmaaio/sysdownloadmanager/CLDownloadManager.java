package org.cmaaio.sysdownloadmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.cmaaio.activity.Cmaaio;
import org.cmaaio.activity.R;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class CLDownloadManager {
	private Context mContext;
	private DownloadManager dm;
	private List<DownloadEvent> queue = new ArrayList<CLDownloadManager.DownloadEvent>();

	private CLDownloadManager() {
	}

	private static class SingletonHolder {
		public static final CLDownloadManager INSTANCE = new CLDownloadManager();
	}

	public static CLDownloadManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void initialise(Context context) {

		mContext = context;
		dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

					long downloadId = intent.getLongExtra(
							DownloadManager.EXTRA_DOWNLOAD_ID, 0);

					Query query = new Query();
					query.setFilterById(downloadId);

					Cursor c = dm.query(query);
					if (c.moveToFirst()) {
						if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(c
								.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
							String uriString = c
									.getString(c
											.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

							Intent promptInstall = new Intent(
									android.content.Intent.ACTION_VIEW);
							promptInstall
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							promptInstall.setDataAndType(Uri.parse(uriString),
									"application/vnd.android.package-archive");

							context.startActivity(promptInstall);
						}

						dequeue(downloadId);
					}
				}
			}
		};

		mContext.registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	public DownloadEvent findDownloadEvent(int id) {

		for (int i = 0; i < queue.size(); i++) {
			if (queue.get(i).getId() == id) {
				return queue.get(i);
			}
		}
		return null;
	}

	public boolean isDownloading(int id) {

		DownloadEvent de = findDownloadEvent(id);
		return de != null;
	}

	public void removeDownItem(int id) {

		for (int i = 0; i < queue.size(); i++) {
			if (queue.get(i).getId() == id) {
				dm.remove(queue.get(i).id);
				queue.remove(i);
			}
		}
	}

	public DownloadEvent downloadGame(int id, String url) {
		if (isDownloading(id)) {
			return null;
		}

		DownloadEvent de = new DownloadEvent(url);
		enqueue(de);
		return de;
	}

	public List<DownloadEvent> getDownloadingGames() {
		return queue;
	}

	private long enqueue(DownloadEvent de) {
		queue.add(de);
		de.id = dm.enqueue(de.request);

		return de.id;
	}

	public void dequeue(long id) {
		for (int i = 0; i < queue.size(); i++) {
			if (queue.get(i).getId() == id) {
				queue.remove(i);
			}
		}

	}

	public class DownloadEvent extends Observable {
		private long id;
		private Request request;

		public DownloadEvent(String url) {
			request = new Request(Uri.parse(url))
					.setAllowedNetworkTypes(
							DownloadManager.Request.NETWORK_WIFI
									| DownloadManager.Request.NETWORK_MOBILE)
					.setAllowedOverRoaming(false)
					.setTitle(mContext.getResources().getString(R.string.kd_app_update))
					.setDescription(mContext.getResources().getString(R.string.kd_app_update_load))
					.setDestinationInExternalPublicDir(
							Environment.DIRECTORY_DOWNLOADS, "temp.apk");
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public Request getRequest() {
			return request;
		}

		public void setRequest(Request request) {
			this.request = request;
		}

		public float getProgress() {
			DownloadManager.Query q = new DownloadManager.Query();
			q.setFilterById(getId());
			Cursor cursor = dm.query(q);
			cursor.moveToFirst();
			int bytes_downloaded = cursor
					.getInt(cursor
							.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			int bytes_total = cursor.getInt(cursor
					.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			cursor.close();

			return bytes_downloaded / bytes_total;
		}

		public int getDownloadingKB() {
			DownloadManager.Query q = new DownloadManager.Query();
			q.setFilterById(getId());
			Cursor cursor = dm.query(q);
			cursor.moveToFirst();
			int bytes_downloaded = cursor
					.getInt(cursor
							.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) / 1024;
			cursor.close();
			return bytes_downloaded;
		}

		public int getAllKB() {
			DownloadManager.Query q = new DownloadManager.Query();
			q.setFilterById(getId());
			Cursor cursor = dm.query(q);
			cursor.moveToFirst();
			int bytes_total = cursor.getInt(cursor
					.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)) / 1024;
			cursor.close();
			return bytes_total;
		}
	}
}
