package org.cmaaio.common;

public class DownLoadFile {
	public void saveFileToCache(String fileUrl, byte[] data, int off, int len,
			boolean isAppend) {
		String fileName = this.getLastPathFromUrl(fileUrl);
		FileOp.getInstance().saveDataToDisk(getDownLoadPath() + fileName, data,
				off, len, isAppend);
	}

	public byte[] getFileFromCache(String fileUrl) {
		String fileName = this.getLastPathFromUrl(fileUrl);
		byte[] result = null;
		result = FileOp.getInstance().getDataFromDisk(
				getDownLoadPath() + fileName);
		return result;
	}

	public void deleteFileFromCache(String fileUrl) {
		String fileName = this.getLastPathFromUrl(fileUrl);
		FileOp.getInstance().delFile(getDownLoadPath() + fileName);
	}

	public long getFileSize(String fileUrl) {
		long size = 0;
		String fileName = this.getLastPathFromUrl(fileUrl);
		size = FileOp.getInstance().getFileSize(getDownLoadPath() + fileName);
		return size;
	}

	public String getSDPathFromUrl(String url) {
		String fileName = this.getLastPathFromUrl(url);
		return getDownLoadPath() + fileName;
	}

	private String getLastPathFromUrl(String url) {
		String result = null;
		if (url != null && (url.contains("http://")||url.contains("https://"))) {
			String[] paths = url.split("/");
			result = paths[paths.length - 1];
		}
		return result;
	}

	private String getDownLoadPath() {
		return Constants.rootPath + Constants.userName + "/"
				+ Constants.DownLoadCache + "/";
	}
}
