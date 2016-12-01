package org.cmaaio.util;

import java.nio.charset.Charset;

import android.content.Context;

public class CMATool {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String encode(String str) {
		String enstr = "";
		String ling = "0000";
		char[] data = str.toCharArray();
		for (int i = 0; i < data.length; i++) {
			String unicode = Integer.toHexString(data[i]);
			enstr += ling.substring(0, 4 - unicode.length()) + Integer.toHexString(data[i]);
		}
		return enstr;
	}

	public static String decode(String str) {
		byte[] baKeyword = new byte[str.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			str = new String(baKeyword,Charset.forName("utf-16"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return str;
	}

}
