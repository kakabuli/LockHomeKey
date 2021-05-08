package com.kaadas.lock.utils.db;


import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.LogUtils;

/**
 * Created by ${howard} on 2018/4/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "[DatabaseHelper]";
    public static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DBTableConfig.DATABASE_NAME, null, DBTableConfig.VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.e("數據建立");
        db.execSQL(DBTableConfig.MediaFile.CREATE_TABLE_MEDIA);
        db.execSQL(DBTableConfig.CatEyeInfo.CREATE_TABLE_CATEYE);
        db.execSQL(DBTableConfig.OpenLockRecord.CREATE_TABLE_ZIGBEE);
        db.execSQL(DBTableConfig.GatewayList.CREATE_TABLE_GATEWAYLIST);
        db.execSQL(DBTableConfig.GatewayDownDevList.CREATE_TABLE_GATEWAYDOWNDEV);



    }

    public void deleteSqlDB() {
        LogUtils.e("數據刪除");
        //TABLE_NAME 是要删除的数据库的名字
        getWritableDatabase(MyApplication.getInstance().DB_KEY).execSQL("DROP TABLE IF EXISTS " + DBTableConfig.DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Logger.e(TAG,"oldVersion:" + oldVersion + "newVersion:" + newVersion);
    }

}
