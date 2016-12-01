package org.cmaaio.common;

public interface DownLoadListener {
	public void onProcess(String url,long have,long total);
	public void onFinish(String url,String sdPath);
}
