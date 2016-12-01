package org.cmaaio.db;

import org.cmaaio.util.CMATool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefsConfig {

	private static SharedPrefsConfig instance;
	private static Context mContext = null;
	private static final String name = "cmaaio.pref";
	public static String APPPUSH = "_apppush";
	public static String APPFULLSCREEN = "_appfullscreen";

	private String ISINIT = "isinit";
	private String yahoowoeid = "yahoowoeid";// yahoo woeid
	private String longitude = "longitude";// 经度
	private String latitude = "latitude";// 纬度

	private String isSavePasswd = "save";// 记录是否保存密码状态

	/**
	 * 记录保存天气状态
	 * 
	 * @param isSave
	 */

	private String teml = "teml";
	private String imageRis = "ris";

	public void setTeml(String temlString) {
		Editor editor = getEditor().putString(teml, temlString);
		editor.commit();
	}

	public String getTeml() {
		return getSharedPreferences().getString(teml, "");
	}

	public void setImageRis(int ris) {
		Editor editor = getEditor().putInt(imageRis, ris);
		editor.commit();
	}

	public int getImageRis() {
		return getSharedPreferences().getInt(imageRis, -1);
	}

	public void setIsSavaPasswd(boolean isSave) {
		Editor editor = getEditor().putBoolean(isSavePasswd, isSave);
		editor.commit();
	}

	public boolean getIsSavePasswd() {
		return getSharedPreferences().getBoolean(isSavePasswd, true);
	}

	public float getLongitude() {
		return getSharedPreferences().getFloat(this.longitude, 0f);
	}

	public void setLongitude(float longitude) {
		Editor editor = getEditor().putFloat(this.longitude, longitude);
		editor.commit();
	}

	public float getLatitude() {
		return getSharedPreferences().getFloat(this.latitude, 0f);
	}

	public void setLatitude(float latitude) {
		Editor editor = getEditor().putFloat(this.latitude, latitude);
		editor.commit();
	}

	/**
	 * 默认值为深圳的 woeid in yahoo
	 * 
	 * @return
	 */
	public String getYahoowoeid() {
		return getSharedPreferences().getString(this.yahoowoeid, "2161853");// 默认为深圳
	}

	public void setYahoowoeid(String yahoowoeid) {
		Editor editor = getEditor().putString(this.yahoowoeid, yahoowoeid);
		editor.commit();
	}

	private String userName = "userName";
	private String passWd = "passWd";
	private String token = "token";
	private String isSavePwd = "issavepwd";
	private String expiredDay = "expiredDay";

	public int getExpiredDay() {
		return getSharedPreferences().getInt(this.expiredDay, -1);
	}

	public void setExpiredDay(int expiredDay) {
		Editor editor = getEditor().putInt(this.expiredDay, expiredDay);
		editor.commit();
	}

	private String baiduPushAppId = "baiduAppId";
	private String baiduPushChannelId = "baiduPushChannelId";
	private String baiduPushUserId = "baiduPushUserId";

	private String screenWidth = "screenWidth";

	private String pNumber = "pNumber";
	private String wNumber = "wNumber";

	public int getScreenWidth() {
		return getSharedPreferences().getInt(this.screenWidth, 200);
	}

	public void setScreenWidth(int screenWidth) {
		Editor editor = getEditor().putInt(this.screenWidth, screenWidth);
		editor.commit();
	}

	public boolean getPNumber() {
		return getSharedPreferences().getBoolean(pNumber, true);
	}

	public void setPNumber(boolean is) {
		Editor editor = getEditor().putBoolean(this.pNumber, is);
		editor.commit();
	}

	public boolean getWNumber() {
		return getSharedPreferences().getBoolean(wNumber, true);
	}

	public void setWNumber(boolean is) {
		Editor editor = getEditor().putBoolean(this.wNumber, is);
		editor.commit();
	}

	private String screenHeight = "screenHeight";

	public int getScreenHeight() {
		return getSharedPreferences().getInt(this.screenHeight, 320);
	}

	public void setScreenHeight(int screenHeight) {
		Editor editor = getEditor().putInt(this.screenHeight, screenHeight);
		editor.commit();
	}

	public String getBaiduPushAppId() {
		return getSharedPreferences().getString(this.baiduPushAppId, "");
	}

	public void setBaiduPushAppId(String baiduPushAppId) {
		Editor editor = getEditor().putString(this.baiduPushAppId,
				baiduPushAppId);
		editor.commit();
	}

	public String getBaiduPushChannelId() {
		return getSharedPreferences().getString(this.baiduPushChannelId, "");
	}

	public void setBaiduPushChannelId(String baiduPushChannelId) {
		Editor editor = getEditor().putString(this.baiduPushChannelId,
				baiduPushChannelId);
		editor.commit();
	}

	public String getBaiduPushUserId() {
		return getSharedPreferences().getString(this.baiduPushUserId, "");
	}

	public void setBaiduPushUserId(String baiduPushUserId) {
		Editor editor = getEditor().putString(this.baiduPushUserId,
				baiduPushUserId);
		editor.commit();
	}

	public void setUserName(String userName) {
		Editor editor = getEditor().putString(this.userName, userName);
		editor.commit();
	}

	public String getUserName() {
		return getSharedPreferences().getString(userName, "");
	}

	public void setPassWord(String pwd) {
		String xpwd = CMATool.encode(pwd);
		Editor editor = getEditor().putString(passWd, xpwd);
		editor.commit();
	}

	public String getPassWord() {
		String xpwd = getSharedPreferences().getString(passWd, "");
		return CMATool.decode(xpwd);
	}

	public void setToken(String token) {
		Editor editor = getEditor().putString(this.token, token);
		editor.commit();
	}

	public String getToken() {
		return getSharedPreferences().getString(token, "");
	}

	public boolean getIsSavePwd() {
		return getSharedPreferences().getBoolean(isSavePwd, false);
	}

	public void setIsSavePwd(boolean isSave) {
		Editor editor = getEditor().putBoolean(isSavePwd, isSave);
		editor.commit();
	}

	public boolean isInit(Context context) {
		return getSharedPreferences().getBoolean(ISINIT, false);
	}

	public void setInit(boolean isInit) {
		Editor editor = getEditor().putBoolean(ISINIT, isInit);
		editor.commit();
	}

	public static SharedPrefsConfig getSharedPrefsInstance(Context context) {
		if (instance == null) {
			instance = new SharedPrefsConfig();
		}
		mContext = context;
		return instance;
	}

	public SharedPreferences getSharedPreferences() {
		return mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public Editor getEditor() {
		return getSharedPreferences().edit();
	}

	private String userId = "userId";
	private String userMobile = "userMobile";
	private String userCrop = "userCrop";
	private String userGender = "userGender";
	private String userNameEn = "userNameEn";
	private String userTel = "userTel";
	private String userEmail = "userEmail";
	private String userDept = "userDept";
	private String userStaffName = "userStaffName";
	private String userImgUrl = "userImgUrl";
	private String userPost = "userPost";
	private String userStaffPhone = "userStaffPhone";

	public void setUserStaffPhone(String staffPhone) {
		Editor editor = getEditor().putString(this.userStaffPhone, staffPhone);
		editor.commit();
	}

	public String getUserStaffPhone() {
		return getSharedPreferences().getString(this.userStaffPhone, "");
	}

	public void setUserPost(String post) {
		Editor editor = getEditor().putString(this.userPost, post);
		editor.commit();
	}

	public String getUserPost() {
		return getSharedPreferences().getString(this.userPost, "");
	}

	public void setUserImageUrl(String url) {
		Editor editor = getEditor().putString(this.userImgUrl, url);
		editor.commit();
	}

	public String getUserImageUrl() {
		return getSharedPreferences().getString(this.userImgUrl, "");
	}

	public void setUserStaffName(String staffName) {
		Editor editor = getEditor().putString(this.userStaffName, staffName);
		editor.commit();
	}

	public String getUserStaffName() {
		return getSharedPreferences().getString(this.userStaffName, "");
	}

	public void setUserDept(String dept) {
		Editor editor = getEditor().putString(this.userDept, dept);
		editor.commit();
	}

	public String getUserDept() {
		return getSharedPreferences().getString(this.userDept, "");
	}

	public void setUserMail(String mail) {
		Editor editor = getEditor().putString(this.userEmail, mail);
		editor.commit();
	}

	public String getUserMail() {
		return getSharedPreferences().getString(this.userEmail, "");
	}

	public void setUserTel(String tel) {
		Editor editor = getEditor().putString(this.userTel, tel);
		editor.commit();
	}

	public String getUserTel() {
		return getSharedPreferences().getString(this.userTel, "");
	}

	public void setUserNameEn(String nameEn) {
		Editor editor = getEditor().putString(this.userNameEn, nameEn);
		editor.commit();
	}

	public String getUserNameEn() {
		return getSharedPreferences().getString(this.userNameEn, "");
	}

	public void setUserGender(String gender) {
		Editor editor = getEditor().putString(this.userGender, gender);
		editor.commit();
	}

	public String getUserGender() {
		return getSharedPreferences().getString(this.userGender, "");
	}

	public void setUserCrop(String crop) {
		Editor editor = getEditor().putString(this.userCrop, crop);
		editor.commit();
	}

	public String getUserCrop() {
		return getSharedPreferences().getString(this.userCrop, "");
	}

	public void setUserId(String userId) {
		Editor editor = getEditor().putString(this.userId, userId);
		editor.commit();
	}

	public String getUserId() {
		return getSharedPreferences().getString(this.userId, "");
	}

	public void setUserMobile(String mobile) {
		Editor editor = getEditor().putString(this.userMobile, mobile);
		editor.commit();
	}

	public String getUserMobile() {
		return getSharedPreferences().getString(this.userMobile, "");
	}

}
