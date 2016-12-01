package org.cmaaio.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public final static String DATABASE_NAME = "FengWoo_imove.db";
	public final static int DATABASE_VERSION = 3;// add
	// private static SQLiteHelper mInstance = null;
	public final static String USER = "User";
	public final static String AboutUser = "AboutUser";

	public static final String T_ZONE = "T_ZONE";
	public static final String T_CORP = "T_CORP";
	public static final String T_DEPT = "T_DEPT";
	public static final String T_EMPS = "T_EMPS";

	public static final String T_ITELL_DRAFTS = "T_ITELL_DRAFTS";
	public static final String T_ITRACK_DRAFTS = "T_ITRACK_DRAFTS";

	public static final String T_ITRACKCACHE = "T_ITRACKCACHE";
	public static final String T_PGAP_CACHE = "PGAP_CACHE";
	public static final String T_PGAP_DRAFT = "PGAP_DRAFT";

	public SQLiteHelper(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		/*
		 * String sql = ""; sql = "CREATE TABLE " + USER + "(" + "id integer," +
		 * "email TEXT,password TEXT, mobile TEXT,addTime TEXT,loginIp TEXT,loginTime TEXT,point integer,petName TEXT"
		 * + ",commentImages TEXT,sex integer,login integer,memo TEXT)";
		 * db.execSQL(sql);
		 */
		db.execSQL(createZoneTableSQL());
		db.execSQL(createCorpTableSQL());
		db.execSQL(createDeptTableSQL());
		db.execSQL(createEmpsTableSQL());

		db.execSQL(createItellDraftsFileSQL());
		db.execSQL(createItrackDraftsFileSQL());

		db.execSQL(createItrackCacheTableSQL());
		db.execSQL(createPGapCacheTableSQL());
		db.execSQL(createPGapDraftTableSQL());

	}

	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * String sql = "DROP TABLE IF EXISTS " + USER; db.execSQL(sql);
		 * onCreate(db);
		 */

		String sql = "DROP TABLE IF EXISTS ";
		db.execSQL(sql + T_ZONE);
		db.execSQL(sql + T_CORP);
		db.execSQL(sql + T_DEPT);
		db.execSQL(sql + T_EMPS);

		db.execSQL(sql + T_ITELL_DRAFTS);
		db.execSQL(sql + T_ITRACK_DRAFTS);
		db.execSQL(sql + T_PGAP_CACHE);
		db.execSQL(sql + T_PGAP_DRAFT);
		onCreate(db);
	}

	/**
	 * 创建地区表语句
	 * 
	 * @return SQL语句
	 */
	private String createZoneTableSQL() {
		StringBuffer sbfSQL = new StringBuffer();
		sbfSQL.append(" CREATE TABLE if not exists " + T_ZONE + " ( \n");
		sbfSQL.append(" _id INTEGER PRIMARY KEY,\n");
		sbfSQL.append(" " + "ZoneId" + " VARCHAR,\n"); // 地区ID
		sbfSQL.append(" " + "Zones" + " VARCHAR );\n"); // 地区名称

		return sbfSQL.toString();
	}

	/**
	 * 创建公司表语句
	 * 
	 * @return SQL语句
	 */
	private String createCorpTableSQL() {
		StringBuffer sbfSQL = new StringBuffer();
		sbfSQL.append(" CREATE TABLE if not exists " + T_CORP + " ( \n");
		sbfSQL.append(" _id INTEGER PRIMARY KEY,\n");
		sbfSQL.append(" " + "ZoneId" + " VARCHAR,\n"); // 地区ID
		sbfSQL.append(" " + "CorpId" + " VARCHAR,\n"); // 公司ID
		sbfSQL.append(" " + "Corps" + " VARCHAR );\n"); // 公司名称

		return sbfSQL.toString();
	}

	/**
	 * 创建部门表语句
	 * 
	 * @return SQL语句
	 */
	private String createDeptTableSQL() {
		StringBuffer sbfSQL = new StringBuffer();
		sbfSQL.append(" CREATE TABLE if not exists " + T_DEPT + " ( \n");
		sbfSQL.append(" _id INTEGER PRIMARY KEY,\n");
		sbfSQL.append(" " + "CorpId" + " VARCHAR,\n"); // 公司ID
		sbfSQL.append(" " + "DeptID" + " VARCHAR,\n"); // 部门ID
		sbfSQL.append(" " + "DeptName" + " VARCHAR );\n"); // 部门名称

		return sbfSQL.toString();
	}

	/**
	 * 创建员工表语句
	 * 
	 * @return SQL语句
	 */
	private String createEmpsTableSQL() {
		StringBuffer sbfSQL = new StringBuffer();
		sbfSQL.append(" CREATE TABLE if not exists " + T_EMPS + " ( \n");
		sbfSQL.append(" _id INTEGER PRIMARY KEY,\n");
		sbfSQL.append(" " + "DeptID" + " VARCHAR,\n"); // 部门ID
		sbfSQL.append(" " + "EmployID" + " VARCHAR,\n"); // 员工ID
		sbfSQL.append(" " + "EmployName" + " VARCHAR,\n"); // 员工名称
		sbfSQL.append(" " + "Mobile" + " VARCHAR,\n"); // 员工手机
		sbfSQL.append(" " + "UserImgUrl" + " VARCHAR,\n"); // 员工图片URL
		sbfSQL.append(" " + "UserPinyin" + " VARCHAR,\n"); // 员工名称拼音
		sbfSQL.append(" " + "CorpName" + " VARCHAR,\n"); // 员工所属公司
		sbfSQL.append(" " + "DeptName" + " VARCHAR,\n"); // 员工所属部门
		sbfSQL.append(" " + "UserTel" + " VARCHAR,\n"); // 员工电话
		sbfSQL.append(" " + "UserEmail" + " VARCHAR,\n"); // 员工Email
		sbfSQL.append(" " + "UserPost" + " VARCHAR,\n"); // 员工职称
		sbfSQL.append(" " + "UserGender" + " VARCHAR,\n"); // 员工职称
		// new
		sbfSQL.append(" " + "StaffPhone" + " VARCHAR,\n"); // 员工工作电话
		sbfSQL.append(" " + "UserMobilePublic" + " VARCHAR,\n");
		sbfSQL.append(" " + "StaffPhonePublic" + " VARCHAR );\n");

		return sbfSQL.toString();
	}

	/**
	 * create itell_drafts_file
	 * 
	 * @return
	 */
	private String createItellDraftsFileSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE if not exists " + T_ITELL_DRAFTS + " (\n");
		sb.append(" _id INTEGER PRIMARY KEY,\n");
		sb.append(" " + "userName" + " VARCHAR,\n");
		sb.append(" " + "itellKey" + " VARCHAR,\n");
		sb.append(" " + "itellValue" + " VARCHAR );\n");

		return sb.toString();
	}

	/**
	 * create itrack_drafts_file
	 * 
	 * @return
	 */
	private String createItrackDraftsFileSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE if not exists " + T_ITRACK_DRAFTS + " (\n");
		sb.append(" _id INTEGER PRIMARY KEY,\n");
		sb.append(" " + "userName" + " VARCHAR,\n");
		sb.append(" " + "itrackKey" + " VARCHAR,\n");
		sb.append(" " + "itrackValue" + " VARCHAR );\n");

		return sb.toString();
	}

	private String createItrackCacheTableSQL() {
		StringBuffer sbfSQL = new StringBuffer();
		sbfSQL.append(" CREATE TABLE if not exists " + T_ITRACKCACHE + " ( \n");
		sbfSQL.append(" _id INTEGER PRIMARY KEY,\n");
		sbfSQL.append(" " + "username" + " VARCHAR,\n"); // 用户名
		sbfSQL.append(" " + "cachekey" + " VARCHAR,\n"); // 键
		sbfSQL.append(" " + "cachevalue" + " VARCHAR );\n"); // 值

		return sbfSQL.toString();
	}

	private String createPGapCacheTableSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE if not exists " + T_PGAP_CACHE + " (\n");
		sb.append(" _id INTEGER PRIMARY KEY,\n");
		sb.append(" " + "userName" + " VARCHAR,\n");
		sb.append(" " + "cAppKey" + " VARCHAR,\n");
		sb.append(" " + "cKey" + " VARCHAR,\n");
		sb.append(" " + "cValue" + " VARCHAR );\n");

		return sb.toString();
	}

	private String createPGapDraftTableSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE if not exists " + T_PGAP_DRAFT + " (\n");
		sb.append(" _id INTEGER PRIMARY KEY,\n");
		sb.append(" " + "userName" + " VARCHAR,\n");
		sb.append(" " + "cAppKey" + " VARCHAR,\n");
		sb.append(" " + "cKey" + " VARCHAR,\n");
		sb.append(" " + "cValue" + " VARCHAR );\n");

		return sb.toString();
	}

}
