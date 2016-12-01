package org.cmaaio.entity;

public class CMAAppEntity {
	//for metroui
	public String appId;
	public String appName;
	public String appSum;
	public String appIcon;
	public String appPath;//app启动路径
	public String appType;
	
	public boolean appIsGone;
	public int badgeNum = 0;
	
	public int inPage = -1;//初始值-1表示改app未被分配位置
	public int seatId = -1;//同上
	//for ui info
	public String appDownUrl = "";
	public String DevType = "";
	public String AppSize = "";
	public String AppVersion = "";
	public String AppRequest = "";
	public String AppDevelopers = "";
	public String AppDevWebSite = "";
	public String AppDevEmail = "";
	public String KeyWords = "";
	public String AppRate = "";
	//add
	public String AppAliases="";
	
	public static String WEBAPP = "web";
	public static String HYBRIDAPP = "hybrid";
	public static String NATIVEAPP = "native";
	public static String ROMAPP = "ROM";
}
