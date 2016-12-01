package org.cmaaio.common;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache{
	public void saveImageToCache(String imageName,InputStream in){
		try {
			FileOp fileOp = FileOp.getInstance();
			if(fileOp != null)
				this.saveImageToCache(imageName, fileOp.input2byte(in));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void saveImageToCache(String imageName,byte[] data){
		String fileName = FileOp.getInstance().getLastPathFromUrl(imageName);
		FileOp.getInstance().saveDataToDisk(getImageCachePath()+fileName, data);
	}
	public Bitmap getImageFromCache(String imageName){
		String fileName =  FileOp.getInstance().getLastPathFromUrl(imageName);
		Bitmap result = null;
		byte[] data = FileOp.getInstance().getDataFromDisk(getImageCachePath()+fileName);
		if(data != null){
			result = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		if(result == null)
			FileOp.getInstance().delFile(getImageCachePath()+fileName);
		return result;
	}
	
	private String getImageCachePath(){
		return Constants.cachePath + Constants.AsyncImageCache+"/";
	}

}
