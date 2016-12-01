package org.cmaaio.util;

import org.cmaaio.common.Constants;

public class FileManager {
	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "/CMAAIO/Cache/" + Constants.AsyncImageCache+"/";
		} else {
			return CommonUtil.getRootFilePath() + "/CMAAIO/Cache/" + Constants.AsyncImageCache+"/";
		}
	}
}
