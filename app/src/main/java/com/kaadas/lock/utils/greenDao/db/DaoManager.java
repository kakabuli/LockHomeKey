package com.kaadas.lock.utils.greenDao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DaoManager {

	private static final String TAG = DaoManager.class.getSimpleName();

	// 数据库名称
	private static final String DB_NAME = "newKaadas.db";

	// 多线程访问
	private volatile static DaoManager manager;

	private static DaoMaster.DevOpenHelper helper;

	private static DaoMaster daoWriteMaster;

	private static DaoSession daoSession;

	private Context context;

	private DaoMaster.DevOpenHelper openHelper;

	public DaoManager(Context context) {
		this.context = context;
		openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
	}


	/**
	 * 使用单例模式获得操作数据库的对象
	 */
	public static DaoManager getInstance(Context context) {
		DaoManager instance = null;
		if (manager == null) {
			synchronized (DaoManager.class) {
				if (instance == null) {
					instance = new DaoManager(context);
					manager = instance;
				}
			}
		}
		return manager;
	}

	public void init(Context context) {
		this.context = context;
	}

	/**
	 * 获取可读数据库
	 */
	private SQLiteDatabase getReadableDatabase() {
		if (openHelper == null) {
			openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
		}
		SQLiteDatabase db = openHelper.getReadableDatabase();
		return db;
	}

	/**
	 * 获取可写数据库
	 */
	private DaoMaster getWritableDatabase() {
		if (openHelper == null) {
			openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
		}
		daoWriteMaster = new DaoMaster(openHelper.getWritableDatabase());
		return daoWriteMaster;
	}





	/**
	 * 完成对数据库的添加删除修改查询等的操作
	 * 注：仅仅是一个接口
	 */
	public DaoSession getDaoSession() {
		if (daoSession == null) {
			if (daoWriteMaster == null) {
				daoWriteMaster = getWritableDatabase();
			}
			daoSession = daoWriteMaster.newSession();
		}
		return daoSession;
	}

	/**
	 * 关闭所有的操作
	 * 注：数据库开启之后，使用完毕必须要关闭
	 */
	public void closeConnection() {
		closeHelper();
		closeDaoSession();
	}

	public void closeHelper() {
		if (helper != null) {
			helper.close();
			helper = null;
		}
	}

	public void closeDaoSession() {
		if (daoSession != null) {
			daoSession.clear();
			daoSession = null;
		}

	}


}