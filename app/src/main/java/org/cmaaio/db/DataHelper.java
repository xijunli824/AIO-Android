package org.cmaaio.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * DataHelper类
 * 
 * @author Administrator
 * 
 */
public class DataHelper {
	Context context = null;
	DataBaseHelper dbHelper = null;

	private static final String GROUP_TEXT = "group_text";
	private static final String CHILD_TEXT1 = "child_text1";
	private static final String CHILD_TEXT2 = "child_text2";
	private static final String CHILD_TEXT3 = "child_text3";
	private static final String USERGENDER = "child_text4";

	public DataHelper(Context context) {
		this.context = context;
		dbHelper = new DataBaseHelper(context);
	}

	public void openDatabase() {
		dbHelper.open();
	}

	public void closeDatabase() {
		dbHelper.close();
	}

	public void openTransaction() {
		dbHelper.openTransaction();
	}

	public void setTransactionSuccessful() {
		dbHelper.setTransactionSuccessful();
	}

	public void endTransaction() {
		dbHelper.endTransaction();
	}

	/**
	 * 创建地区
	 * 
	 * @param mContext
	 * @param user
	 *            用户信息
	 * @return
	 */
	public boolean insertZoneInfo(String zoneId, String zoneName) {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		long id = dbHelper.insertzZoneInfo(zoneId, zoneName);
		// dbHelper.close();

		if (id == -1) {
			return false;
		}
		return true;
	}

	public List<Map<String, String>> queryZoneAndCorpInfo(
			List<List<Map<String, String>>> childData) {

		List<Map<String, String>> zoneList = new ArrayList<Map<String, String>>();

		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		Cursor cursor = dbHelper.queryZoneInfo();
		if (cursor == null) {
			// dbHelper.close();
			return zoneList;
		}

		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			Map<String, String> zoneMap = new HashMap<String, String>();
			zoneMap.put("group_text", cursor.getString(2));
			// System.out.println("=========>" + cursor.getString(2));
			zoneList.add(zoneMap);

			String zoneId = cursor.getString(1);
			childData.add(queryCorpInfo(zoneId));
		}

		cursor.close();
		// dbHelper.close();

		return zoneList;
	}

	public boolean deleteZoneInfo() {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		dbHelper.deleteZoneInfo();

		// dbHelper.close();

		return true;
	}

	/**
	 * 创建公司
	 * 
	 * @param mContext
	 * @param
	 * @return
	 */
	public boolean insertCorpInfo(String zoneId, String corpId, String corps) {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		long id = dbHelper.insertzCorpInfo(zoneId, corpId, corps);
		// dbHelper.close();

		if (id == -1) {
			return false;
		}
		return true;
	}

	public List<Map<String, String>> queryCorpInfo(String zoneId) {

		List<Map<String, String>> corpList = new ArrayList<Map<String, String>>();

		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		Cursor cursor = dbHelper.queryCorpInfo(zoneId);
		if (cursor == null) {
			// dbHelper.close();
			return corpList;
		}

		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			Map<String, String> corpMap = new HashMap<String, String>();
			corpMap.put("CorpId", cursor.getString(2));
			corpMap.put("child_text1", cursor.getString(3));
			// System.out.println("=========>" + cursor.getString(3));
			corpList.add(corpMap);
		}

		cursor.close();
		// dbHelper.close();

		return corpList;
	}

	public boolean deleteCorpInfo() {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		dbHelper.deleteCorpInfo();

		// dbHelper.close();

		return true;
	}

	/**
	 * 创建部门
	 * 
	 * @param mContext
	 * @param
	 * @return
	 */
	public boolean insertDeptInfo(String corpId, String deptId, String deptName) {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		long id = dbHelper.insertzDeptInfo(corpId, deptId, deptName);
		// dbHelper.close();

		if (id == -1) {
			return false;
		}

		return true;
	}

	public List<Map<String, String>> queryDeptAndEmpInfo(
			List<List<Map<String, String>>> childData, String corpId) {

		List<Map<String, String>> deptList = new ArrayList<Map<String, String>>();

		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		Cursor cursor = dbHelper.queryDeptInfo(corpId);
		if (cursor == null) {
			// dbHelper.close();
			return deptList;
		}

		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			Map<String, String> deptMap = new HashMap<String, String>();
			deptMap.put(GROUP_TEXT, cursor.getString(3));
			// System.out.println("=========>" + cursor.getString(3));
			deptList.add(deptMap);

			childData.add(queryEmpInfo(cursor.getString(2)));
		}

		cursor.close();
		// dbHelper.close();

		return deptList;
	}

	public boolean deleteDeptInfo() {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		dbHelper.deleteDeptInfo();

		// dbHelper.close();

		return true;
	}

	/**
	 * 创建部门
	 * 
	 * @param mContext
	 * @param
	 * @return
	 */
	public boolean insertEmpInfo(String deptId, String employId,
			String employName, String mobile, String userImgUrl,
			String userPinyin, String corpName, String deptName,
			String userTel, String userEmail, String userPost,
			String userGender, String StaffPhone, String UserMobilePublic,
			String StaffPhonePublic) {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		long id = dbHelper.insertzEmpInfo(deptId, employId, employName, mobile,
				userImgUrl, userPinyin, corpName, deptName, userTel, userEmail,
				userPost, userGender, StaffPhone, UserMobilePublic,
				StaffPhonePublic);
		// dbHelper.close();

		if (id == -1) {
			return false;
		}

		return true;
	}

	public List<Map<String, String>> queryEmpInfo(String deptId) {

		List<Map<String, String>> empList = new ArrayList<Map<String, String>>();

		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		Cursor cursor = dbHelper.queryEmpInfo(deptId);
		if (cursor == null) {
			// dbHelper.close();
			return empList;
		}

		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			Map<String, String> empMap = new HashMap<String, String>();
			empMap.put("EmployID", cursor.getString(2));
			empMap.put(CHILD_TEXT1, cursor.getString(3));

			String mobile = cursor.getString(4);
			if (mobile != null && !"".equals(mobile)) {
				empMap.put(CHILD_TEXT2, mobile);
			} else {
				empMap.put(CHILD_TEXT2, cursor.getString(9));
			}
			empMap.put(CHILD_TEXT3,
					cursor.getString(cursor.getColumnIndex("UserPost")));
			empMap.put(USERGENDER,
					cursor.getString(cursor.getColumnIndex("UserGender")));

			// System.out.println("=========>" + cursor.getString(9));
			empList.add(empMap);
		}

		cursor.close();
		// dbHelper.close();
		return empList;
	}

	public boolean deleteEmpInfo() {
		// DataBaseHelper dbHelper = new DataBaseHelper(context);
		// dbHelper.open();

		dbHelper.deleteEmpInfo();

		// dbHelper.close();

		return true;
	}

	public Map<String, String> queryEmpInfoByEmpId(String empId) {

		Map<String, String> userMap = new HashMap<String, String>();

		Cursor cursor = dbHelper.queryEmpInfoByEmpId(empId);
		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);

			userMap.put("UserName", cursor.getString(3));
			userMap.put("UserMobile", cursor.getString(4));
			userMap.put("UserImgUrl", cursor.getString(5));
			userMap.put("UserPinyin", cursor.getString(6));
			userMap.put("CorpName", cursor.getString(7));
			userMap.put("DeptName", cursor.getString(8));
			userMap.put("UserTel", cursor.getString(9));
			userMap.put("UserEmail", cursor.getString(10));
			userMap.put("UserPost",
					cursor.getString(cursor.getColumnIndex("UserPost")));
			userMap.put("UserGender",
					cursor.getString(cursor.getColumnIndex("UserGender")));
			userMap.put("StaffPhone",
					cursor.getString(cursor.getColumnIndex("StaffPhone")));
			userMap.put("UserMobilePublic",
					cursor.getString(cursor.getColumnIndex("UserMobilePublic")));
			userMap.put("StaffPhonePublic",
					cursor.getString(cursor.getColumnIndex("StaffPhonePublic")));

		}
		cursor.close();

		return userMap;
	}

	/**
	 * 查询所员工个人信息
	 * 
	 * @return
	 */
	public List<Map<String, String>> queryAllEmpInfoByEmpId() {

		List<Map<String, String>> mList = new ArrayList<Map<String, String>>();

		Map<String, String> userMap = null;

		Cursor cursor = dbHelper.queryAllEmpInfoByEmpId();
		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			userMap = new HashMap<String, String>();
			userMap.put("child_text1", cursor.getString(3));
			userMap.put("child_text2", cursor.getString(4));
			userMap.put("child_text3",
					cursor.getString(cursor.getColumnIndex("UserPost")));
			userMap.put("child_text4",
					cursor.getString(cursor.getColumnIndex("UserGender")));
			userMap.put("DeptID",
					cursor.getString(cursor.getColumnIndex("DeptID")));
			userMap.put("EmployID",
					cursor.getString(cursor.getColumnIndex("EmployID")));

			mList.add(userMap);
		}
		cursor.close();

		return mList;
	}

}
