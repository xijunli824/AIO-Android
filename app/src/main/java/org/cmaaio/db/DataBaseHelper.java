package org.cmaaio.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.WebView.FindListener;

/**
 * 
 * <pre>
 * [名 称]：DataBase										
 * [功 能]：数据库封装类										
 * [描 述]：存储数据，获取数据，更新数据等操作
 * </pre>
 * 
 * @author lhy
 * @创建时间
 */
public class DataBaseHelper implements Serializable {

	private static final long serialVersionUID = -7060210544600464481L;

	// private static final String TAG = "DataBaseHelper";

	private Context context;
	private SQLiteHelper dbOpenHelper;
	private SQLiteDatabase sqliteDatabase;

	/**
	 * 创建数据库对象
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		this.context = context;
	}

	/**
	 * 打开数据库
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DataBaseHelper open() throws SQLException {
		dbOpenHelper = new SQLiteHelper(context);
		sqliteDatabase = dbOpenHelper.getWritableDatabase();
		return this;
	}

	/**
	 * 关闭数据库
	 * 
	 * @return
	 * @throws SQLException
	 */
	public void close() {
		sqliteDatabase.close();
		dbOpenHelper.close();
	}

	public void openTransaction() {
		sqliteDatabase.beginTransaction();
	}

	public void setTransactionSuccessful() {
		sqliteDatabase.setTransactionSuccessful();
	}

	public void endTransaction() {
		sqliteDatabase.endTransaction();
	}

	/**
	 * 存储地区数据
	 * 
	 * @param user
	 * @return
	 */
	public long insertzZoneInfo(String zoneId, String zoneName) {
		ContentValues args = new ContentValues();
		args.put("ZoneId", "" + zoneId);
		args.put("Zones", "" + zoneName);

		long id = -1;
		try {
			id = sqliteDatabase.insert(SQLiteHelper.T_ZONE, null, args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 查找地区
	 * 
	 * @return
	 */
	public Cursor queryZoneInfo() {
		Cursor mCursor = null;
		try {
			mCursor = sqliteDatabase.query(SQLiteHelper.T_ZONE, null, null,
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCursor;
	}

	/**
	 * 删除所有地区数据
	 */
	public int deleteZoneInfo() {

		return sqliteDatabase.delete(SQLiteHelper.T_ZONE, null, null);
	}

	/**
	 * 存储公司数据
	 * 
	 * @param user
	 * @return
	 */
	public long insertzCorpInfo(String zoneId, String corpId, String corps) {
		ContentValues args = new ContentValues();
		args.put("ZoneId", "" + zoneId);
		args.put("CorpId", "" + corpId);
		args.put("Corps", "" + corps);

		long id = -1;
		try {
			id = sqliteDatabase.insert(SQLiteHelper.T_CORP, null, args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 查找公司
	 * 
	 * @return
	 */
	public Cursor queryCorpInfo(String zoneId) {
		Cursor mCursor = null;
		// 根据地区名称查询所属公司
		String condtion = "ZoneId=" + "'" + zoneId + "'";
		try {
			mCursor = sqliteDatabase.query(SQLiteHelper.T_CORP, null, condtion,
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCursor;
	}

	/**
	 * 删除所有公司据
	 */
	public int deleteCorpInfo() {

		return sqliteDatabase.delete(SQLiteHelper.T_CORP, null, null);
	}

	/**
	 * 存储部门数据
	 * 
	 * @param user
	 * @return
	 */
	public long insertzDeptInfo(String corpId, String deptId, String deptName) {
		ContentValues args = new ContentValues();
		args.put("CorpId", "" + corpId);
		args.put("DeptID", "" + deptId);
		args.put("DeptName", "" + deptName);

		long id = -1;
		try {
			id = sqliteDatabase.insert(SQLiteHelper.T_DEPT, null, args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 查找部门
	 * 
	 * @return
	 */
	public Cursor queryDeptInfo(String corpId) {
		Cursor mCursor = null;
		// 根据公司名称查询所属部门
		String condtion = "CorpId=" + "'" + corpId + "'";
		try {
			mCursor = sqliteDatabase.query(SQLiteHelper.T_DEPT, null, condtion,
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCursor;
	}

	/**
	 * 删除所有部门数据
	 */
	public int deleteDeptInfo() {
		return sqliteDatabase.delete(SQLiteHelper.T_DEPT, null, null);
	}

	/**
	 * 存储员工数据
	 * 
	 * @param user
	 * @return
	 */
	public long insertzEmpInfo(String deptId, String employId,
			String employName, String mobile, String userImgUrl,
			String userPinyin, String corpName, String deptName,
			String userTel, String userEmail, String userPost,
			String userGender, String StaffPhone, String UserMobilePublic,
			String StaffPhonePublic) {

		ContentValues args = new ContentValues();
		args.put("DeptID", "" + deptId);
		args.put("EmployID", "" + employId);
		args.put("EmployName", "" + employName);

		args.put("Mobile", "" + mobile);
		args.put("UserImgUrl", "" + userImgUrl);
		args.put("UserPinyin", "" + userPinyin);

		args.put("CorpName", "" + corpName);
		args.put("DeptName", "" + deptName);
		args.put("UserTel", "" + userTel);
		args.put("UserEmail", "" + userEmail);
		args.put("UserPost", "" + userPost);
		args.put("UserGender", "" + userGender);
		// new
		args.put("StaffPhone", "" + StaffPhone);
		args.put("UserMobilePublic", "" + UserMobilePublic);
		args.put("StaffPhonePublic", "" + StaffPhonePublic);

		long id = -1;
		try {
			id = sqliteDatabase.insert(SQLiteHelper.T_EMPS, null, args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 根据部门ID查找员工
	 * 
	 * @return
	 */
	public Cursor queryEmpInfo(String deptId) {
		Cursor mCursor = null;
		// 根据部门ID查询所属员工
		String condtion = "DeptID=" + "'" + deptId + "'";
		try {
			mCursor = sqliteDatabase.query(SQLiteHelper.T_EMPS, null, condtion,
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCursor;
	}

	/**
	 * 删除所有员工数据
	 */
	public int deleteEmpInfo() {
		return sqliteDatabase.delete(SQLiteHelper.T_EMPS, null, null);
	}

	/**
	 * 根据员工ID查找员工
	 * 
	 * @return
	 */
	public Cursor queryEmpInfoByEmpId(String empId) {
		Cursor mCursor = null;
		// 根据部门ID查询所属员工
		String condtion = "EmployID=" + "'" + empId + "'";
		try {
			mCursor = sqliteDatabase.query(SQLiteHelper.T_EMPS, null, condtion,
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCursor;
	}
	/**
	 * 查询所有员工
	 * @return
	 */
	public Cursor queryAllEmpInfoByEmpId() {
		Cursor mCursor = null;
		try {
			mCursor = sqliteDatabase.query(SQLiteHelper.T_EMPS, null, null,
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mCursor;
	}

	/**
	 * 取itrack缓存
	 */
	public HashMap<String, String> queryItrackCacheByUser(String username,
			String key) {

		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_ITRACKCACHE,
					null, " username = ? and cachekey= ?", new String[] {
							username, key }, null, null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("cachekey")),
						cursor.getString(cursor.getColumnIndex("cachevalue")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return map;
	}

	/**
	 * get itell草稿箱
	 * 
	 * @param userName
	 * @return
	 */
	public HashMap<String, String> queryItellByUser(String userName) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_ITELL_DRAFTS,
					null, " userName = ?", new String[] { userName }, null,
					null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("itellKey")),
						cursor.getString(cursor.getColumnIndex("itellValue")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return map;
	}

	/**
	 * get itell草稿箱
	 * 
	 * @param userName
	 * @return
	 */
	public HashMap<String, String> queryItrackByUser(String userName) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_ITRACK_DRAFTS,
					null, " userName = ?", new String[] { userName }, null,
					null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("itrackKey")),
						cursor.getString(cursor.getColumnIndex("itrackValue")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return map;
	}

	/**
	 * 删除 itell草稿箱
	 * 
	 * @param userName
	 * @return
	 */
	public int deleteItellDrafts(String uname, String key) {
		try {
			open();
			return sqliteDatabase.delete(SQLiteHelper.T_ITELL_DRAFTS,
					" userName=? and itellKey=?", new String[] { uname, key });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -1;
	}

	/**
	 * 删除 itrack草稿箱
	 * 
	 * @param userName
	 * @return
	 */
	public int deleteItrackDrafts(String uname, String key) {
		try {
			open();
			return sqliteDatabase.delete(SQLiteHelper.T_ITRACK_DRAFTS,
					" userName=? and itrackKey=?", new String[] { uname, key });
			// String rawSql = "select count(*) as countNum from " +
			// SQLiteHelper.T_ITRACK_DRAFTS + "";
			// Cursor cursor = sqliteDatabase.rawQuery(rawSql, null);
			// int count = -1;
			// while (cursor.moveToNext()) {
			// count = cursor.getInt(cursor.getColumnIndex("countNum"));
			// }
			// return count;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -1;
	}

	/**
	 * 写入itell草稿箱
	 * 
	 * @param userName
	 * @param values
	 */
	public void writeItellDraftsByUser(String userName,
			HashMap<String, String> values) {
		try {
			open();
			Set<Entry<String, String>> sets = values.entrySet();
			Iterator<Entry<String, String>> iterators = sets.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("userName", userName);
				contentValues.put("itellKey", entry.getKey());
				contentValues.put("itellValue", entry.getValue());
				Cursor cursor = sqliteDatabase.query(
						SQLiteHelper.T_ITELL_DRAFTS, null,
						" userName = ? and itellKey = ?", new String[] {
								userName, entry.getKey() }, null, null, null);
				if (cursor.moveToNext()) {
					sqliteDatabase.update(SQLiteHelper.T_ITELL_DRAFTS,
							contentValues, " userName=? and itellKey =?",
							new String[] { userName, entry.getKey() });
				} else {
					sqliteDatabase.insert(SQLiteHelper.T_ITELL_DRAFTS, null,
							contentValues);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 写入itrack草稿箱
	 * 
	 * @param userName
	 * @param values
	 */
	public void writeItrackDraftsByUser(String userName,
			HashMap<String, String> values) {
		try {
			open();
			Set<Entry<String, String>> sets = values.entrySet();
			Iterator<Entry<String, String>> iterators = sets.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("userName", userName);
				contentValues.put("itrackKey", entry.getKey());
				contentValues.put("itrackValue", entry.getValue());
				Cursor cursor = sqliteDatabase.query(
						SQLiteHelper.T_ITRACK_DRAFTS, null,
						" userName = ? and itrackKey = ?", new String[] {
								userName, entry.getKey() }, null, null, null);
				if (cursor.moveToNext()) {
					sqliteDatabase.update(SQLiteHelper.T_ITRACK_DRAFTS,
							contentValues, " userName=? and itrackKey =?",
							new String[] { userName, entry.getKey() });
				} else {
					sqliteDatabase.insert(SQLiteHelper.T_ITRACK_DRAFTS, null,
							contentValues);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * 写itrack缓存
	 * 
	 * @param values
	 */
	public void writeItrackCacheByUser(String username,
			HashMap<String, String> values) {
		try {
			open();
			Set<Entry<String, String>> sets = values.entrySet();
			Iterator<Entry<String, String>> iterators = sets.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				ContentValues cv = new ContentValues();
				cv.put("username", username);
				cv.put("cachekey", entry.getKey());
				cv.put("cachevalue", entry.getValue());
				Cursor cursor = sqliteDatabase.query(
						SQLiteHelper.T_ITRACKCACHE, null,
						" username = ? and cachekey = ?", new String[] {
								username, entry.getKey() }, null, null, null);
				if (cursor.moveToNext()) {
					sqliteDatabase.update(SQLiteHelper.T_ITRACKCACHE, cv,
							" username=? and cachekey =?", new String[] {
									username, entry.getKey() });
				} else {
					sqliteDatabase.insert(SQLiteHelper.T_ITRACKCACHE, null, cv);
				}
			}
			close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * queryDataByUser
	 * 
	 * @param userName
	 * @param cAppKey
	 * @return
	 */
	public HashMap<String, String> queryCacheDataByUser(String userName,
			String cAppKey) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_PGAP_CACHE,
					null, " userName =? and cAppKey=? ", new String[] {
							userName, cAppKey }, null, null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("cKey")),
						cursor.getString(cursor.getColumnIndex("cValue")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return map;
	}

	/**
	 * 
	 * @param userName
	 * @param cAppKey
	 * @param key
	 * @return
	 */
	public HashMap<String, String> queryCacheDataByUserAndKey(String userName,
			String cAppKey, String key) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_PGAP_CACHE,
					null, " userName =? and cAppKey =? and cKey=? ",
					new String[] { userName, cAppKey, key }, null, null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("cKey")),
						cursor.getString(cursor.getColumnIndex("cValue")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return map;
	}

	/**
	 * 
	 * @param uname
	 * @param cAppKey
	 * @param ckey
	 * @return
	 */
	public int deleteCacheData(String uname, String cAppKey, String ckey) {
		try {
			open();
			return sqliteDatabase.delete(SQLiteHelper.T_PGAP_CACHE,
					" userName=? and cAppKey=? and cKey=?", new String[] {
							uname, cAppKey, ckey });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -1;
	}

	/**
	 * 
	 * @param userName
	 * @param cAppKey
	 * @param values
	 */
	public void writeCacheData(String userName, String cAppKey,
			HashMap<String, String> values) {
		try {
			open();
			Set<Entry<String, String>> sets = values.entrySet();
			Iterator<Entry<String, String>> iterators = sets.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("userName", userName);
				contentValues.put("cAppKey", cAppKey);
				contentValues.put("cKey", entry.getKey());
				contentValues.put("cValue", entry.getValue());
				Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_PGAP_CACHE,
						null, " userName =? and cAppKey =? and cKey =?",
						new String[] { userName, cAppKey, entry.getKey() },
						null, null, null);
				if (cursor.moveToNext()) {
					sqliteDatabase.update(SQLiteHelper.T_PGAP_CACHE,
							contentValues,
							" userName=? and cAppKey =? and cKey =?",
							new String[] { userName, cAppKey, entry.getKey() });
				} else {
					sqliteDatabase.insert(SQLiteHelper.T_PGAP_CACHE, null,
							contentValues);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * queryDataByUser
	 * 
	 * @param userName
	 * @param cAppKey
	 * @return
	 */
	public HashMap<String, String> queryDraftDataByUser(String userName,
			String cAppKey) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_PGAP_DRAFT,
					null, " userName =? and cAppKey=? ", new String[] {
							userName, cAppKey }, null, null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("cKey")),
						cursor.getString(cursor.getColumnIndex("cValue")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return map;
	}

	/**
	 * 
	 * @param userName
	 * @param cAppKey
	 * @param key
	 * @return
	 */
	public HashMap<String, String> queryDraftDataByUserAndKey(String userName,
			String cAppKey, String key) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			open();
			Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_PGAP_DRAFT,
					null, " userName =? and cAppKey =? and cKey=? ",
					new String[] { userName, cAppKey, key }, null, null, null);
			while (cursor.moveToNext()) {
				map.put(cursor.getString(cursor.getColumnIndex("cKey")),
						cursor.getString(cursor.getColumnIndex("cValue")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return map;
	}

	/**
	 * 
	 * @param uname
	 * @param cAppKey
	 * @param ckey
	 * @return
	 */
	public int deleteDraftData(String uname, String cAppKey, String ckey) {
		try {
			open();
			return sqliteDatabase.delete(SQLiteHelper.T_PGAP_DRAFT,
					" userName=? and cAppKey=? and cKey=?", new String[] {
							uname, cAppKey, ckey });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -1;
	}

	/**
	 * 
	 * @param userName
	 * @param cAppKey
	 * @param values
	 */
	public void writeDraftData(String userName, String cAppKey,
			HashMap<String, String> values) {
		try {
			open();
			Set<Entry<String, String>> sets = values.entrySet();
			Iterator<Entry<String, String>> iterators = sets.iterator();
			while (iterators.hasNext()) {
				Entry<String, String> entry = iterators.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("userName", userName);
				contentValues.put("cAppKey", cAppKey);
				contentValues.put("cKey", entry.getKey());
				contentValues.put("cValue", entry.getValue());
				Cursor cursor = sqliteDatabase.query(SQLiteHelper.T_PGAP_DRAFT,
						null, " userName =? and cAppKey =? and cKey =?",
						new String[] { userName, cAppKey, entry.getKey() },
						null, null, null);
				if (cursor.moveToNext()) {
					sqliteDatabase.update(SQLiteHelper.T_PGAP_DRAFT,
							contentValues,
							" userName=? and cAppKey =? and cKey =?",
							new String[] { userName, cAppKey, entry.getKey() });
				} else {
					sqliteDatabase.insert(SQLiteHelper.T_PGAP_DRAFT, null,
							contentValues);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

}
