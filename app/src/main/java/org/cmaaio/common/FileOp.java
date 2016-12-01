package org.cmaaio.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.cmaaio.sysdownloadmanager.CMAAPPInatallerManager;

import android.os.Environment;
import android.util.Log;

public class FileOp {
//	private String dataPath = Environment.getExternalStorageDirectory() + "/CMAAIO/Cache/"; // 存储缓存数据
	private static FileOp instance = null;

	public static FileOp getInstance() {
		if (instance == null)
			instance = new FileOp();
		return instance;
	}

	public boolean haveSDK() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public String getCacheSize(String path) {
		long size = 0;
		try {
			File file = new File(path);
			size = getFolderSize(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (size > 0)
			return setFileSize(size);
		else
			return "";
	}
	private long getFolderSize(java.io.File file) throws Exception {
		long size = 0;
		java.io.File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}
		return size;
	}

	public long getFileSize(String filePath) {
		long size = 0;
		File file = new File(filePath);
		if (file.isFile())
			size = file.length();
		return size;
	}
	//返回url中最后一个文件名
	public String getLastPathFromUrl(String url){
		String result = null;
		if(url.contains("http://") || url.contains("https://")){
			String [] paths = url.split("/");
			result = paths[paths.length-1];
		}
		return result;
	}

	/**
	 * 
	 * 
	 * @param size
	 * @return
	 */
	public String setFileSize(long size) {
		DecimalFormat df = new DecimalFormat("###.##");
		float f = ((float) size / (float) (1024 * 1024));
		return df.format(new Float(f).doubleValue()) + "M";
	}

	//全路径删除
	public void delFile(String pathName) {
		try {
			File file = new File(pathName);
			if (file.exists())
				file.delete();
			else
				Log.e("wjdebug", pathName + "no exits");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//全路径删除
	public void delFileDir(String fullName) {
		File file = new File(fullName);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					this.delFileDir(files[i].getAbsolutePath());
				}
			}
			file.delete();
		}
	}

	public void saveDataToDisk(String filename, byte[] data) {
		try {
			String allPath = filename;
			int lastIndex = allPath.lastIndexOf('/');
			String path = allPath.substring(0, lastIndex);
			File f = new File(path);
			if (!f.exists())
				f.mkdirs();
			f = new File(allPath);
			if (!f.exists())
				f.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
			bos.write(data);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveDataToDisk(String filename, byte[] data, int off, int len,boolean isAppend) {
		try {
			String allPath =  filename;
			int lastIndex = allPath.lastIndexOf('/');
			String path = allPath.substring(0, lastIndex);
			File f = new File(path);
			if (!f.exists())
				f.mkdirs();
			f = new File(allPath);
			boolean isSuc = false;
			if (!f.exists()) {
				isSuc = f.createNewFile();
				if (!isSuc)
					return;
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(filename, isAppend));

			bos.write(data, off, len);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getDataFromDisk(String filename) {
		byte[] file = null;
		File f = new File(filename);
		if (f != null) {
			Date lastModifyTime = new Date(f.lastModified());
			Date nowTime = new Date();
			int hour = nowTime.getHours() - lastModifyTime.getHours();
			if (hour > 24)
				return null;
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				if (fis != null) {
					int len;
					len = fis.available();
					file = new byte[len];
					fis.read(file);
				}
			} catch (Exception e) {

			}
		}
		return file;
	}

	public byte[] input2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[4096];
		int rc = -1;
		while ((rc = inStream.read(buff, 0, 4096)) != -1) {
			swapStream.write(buff, 0, rc);
		}
		buff = null;
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	// DES
	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String DESencode(String key, String iv, String data) {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec IV = new IvParameterSpec(iv.getBytes());
			AlgorithmParameterSpec paramSpec = IV;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] bytes = cipher.doFinal(data.getBytes());
			return Base64.encode(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * DES算法，解密
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static String DESdecode(String key, String iv, String data) {
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec IV = new IvParameterSpec(iv.getBytes());
			AlgorithmParameterSpec paramSpec = IV;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			byte[] result = cipher.doFinal(Base64.decode(data));
			return new String(result);
		} catch (Exception e) {
			return "";
		}
	}

	/* 解压zip文件 */
	public static int upZipFile(String zipFilePath, String folderPath) throws ZipException,
			IOException {
		File zipFile = new File(zipFilePath);
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()&&CMAAPPInatallerManager.canUnzip) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d("upZipFile", "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "str = " + dirstr);
				File f = new File(dirstr);
				if(f.exists())
				{
					f.delete();
				}
				f.mkdir();
				continue;
			}
			Log.d("upZipFile", "ze.getName() = " + ze.getName());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		return 0;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}
}
