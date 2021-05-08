package com.kaadas.lock.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.FileUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by ${howard} on 2018/4/16.
 */

public class MediaFileDBDao {
    private final DatabaseHelper mHelper;
    private static MediaFileDBDao instance;

    public MediaFileDBDao(Context context) {
        mHelper = DatabaseHelper.getInstance(context);
    }

    public static MediaFileDBDao getInstance(Context context) {
        if (instance == null) {
            instance = new MediaFileDBDao(context);
        }
        return instance;
    }

    /**
     * @param name
     * @param createTime
     * @param type
     * @param path
     * @return 如果返回为true 插入成功，否则失败
     */
    public boolean add(String name, String createTime, int type, String path) {
        SQLiteDatabase db;
        try {
            db = mHelper.getWritableDatabase(MyApplication.getInstance().DB_KEY);
            ContentValues values = new ContentValues();
            values.put(DBTableConfig.MediaFile.NAME, name);
            values.put(DBTableConfig.MediaFile.CREATE_TIME, createTime);
            values.put(DBTableConfig.MediaFile.TYPE, type);
            values.put(DBTableConfig.MediaFile.PATH, path);
            long insert = db.insert(DBTableConfig.MediaFile.TABLE_NAME, null, values);
            db.close();
            return insert != -1;
        }catch (Exception e){

        }
        return false;
    }

    /**
     * 根据名称删除某个记录
     * @param name
     * @return
     */
    public boolean deleteFileByName(String name) {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase(MyApplication.getInstance().DB_KEY);
            String whereClause = DBTableConfig.MediaFile.NAME + "=?";
            String[] whereArgs = new String[]{name};
            int delete = db.delete(DBTableConfig.MediaFile.TABLE_NAME, whereClause,
                    whereArgs);
            db.close();
            return delete != 0;
        }catch (Exception e){

        }
        return false;
    }

    /**
     * 通过name来改变某条记录
     * @param name
     * @param times
     * @param type
     * @param path
     * @return 更新数据库某条记录
     */
    public boolean updateFile(String name, String times, int type, String path) {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase(MyApplication.getInstance().DB_KEY);
            ContentValues values = new ContentValues();
            values.put(DBTableConfig.MediaFile.NAME, name);
            values.put(DBTableConfig.MediaFile.CREATE_TIME, times);
            values.put(DBTableConfig.MediaFile.TYPE, type);
            values.put(DBTableConfig.MediaFile.PATH, path);
            String whereClause = DBTableConfig.MediaFile.NAME + "=?";
            String[] whereArgs = new String[]{name};
            int update = db.update(DBTableConfig.MediaFile.TABLE_NAME, values,
                    whereClause, whereArgs);

            db.close();

            return update != 0;
        }catch (Exception e){

        }
        return false;
    }

    /**
     * @return 查找所有媒体信息
     */
    public synchronized ArrayList<MediaItem> findAll() {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase(MyApplication.getInstance().DB_KEY);
            ArrayList<MediaItem> list = new ArrayList<>();
            String sql = "select " + DBTableConfig.MediaFile.NAME + "," + DBTableConfig.MediaFile.CREATE_TIME + "," + DBTableConfig.MediaFile.TYPE + ","
                    + DBTableConfig.MediaFile.PATH + " from " + DBTableConfig.MediaFile.TABLE_NAME + " order by time desc";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MediaItem bean = new MediaItem();
                    bean.setName(cursor.getString(0));
                    bean.setCrateTime(cursor.getString(1));
                    bean.setMediaType(cursor.getInt(2));
                    bean.setPath(cursor.getString(3));
                    list.add(bean);
                }
                cursor.close();
            }
            db.close();
            return list;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

	public synchronized ArrayList<MediaItem> findAllByDeviceId(String deviceId) {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase(MyApplication.getInstance().DB_KEY);
            ArrayList<MediaItem> list = new ArrayList<>();
            String sql = "select " + DBTableConfig.MediaFile.NAME + "," + DBTableConfig.MediaFile.CREATE_TIME + "," + DBTableConfig.MediaFile.TYPE + ","
                    + DBTableConfig.MediaFile.PATH + " from " + DBTableConfig.MediaFile.TABLE_NAME + " order by time desc";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MediaItem bean = new MediaItem();
                    bean.setName(cursor.getString(0));
                    bean.setCrateTime(cursor.getString(1));
                    bean.setMediaType(cursor.getInt(2));
                    bean.setPath(cursor.getString(3));
                    list.add(bean);
                }
                cursor.close();
            }
            db.close();
            ArrayList<MediaItem> result = new ArrayList<>();
            for (MediaItem item:list){
                if (item.getName().indexOf(deviceId)!=-1){ //如果设备名称中包含有设备Id则认识是这个设备下的回访文件
                    result.add(item);
                }
            }
            return result;
        }catch (Exception e){

        }
        return new ArrayList<>();
	}


	public Observable<ArrayList<MediaItem>> ObservableFindAllByDeviceId(final String deviceId) {
		Observable<ArrayList<MediaItem>> arrayListObservable = Observable.create(new ObservableOnSubscribe<ArrayList<MediaItem>>() {
			@Override
			public void subscribe(ObservableEmitter<ArrayList<MediaItem>> e) throws Exception {
				ArrayList<MediaItem> mediaItems = findAllByDeviceId(deviceId);
				if (mediaItems!=null){
					for (int i=0;i<mediaItems.size();i++){
						if (!FileUtils.isFileExist(mediaItems.get(i).getPath())) {
							deleteFileByName(mediaItems.get(i).getName());
							mediaItems.remove(mediaItems.get(i));
						}
					}
				}
				e.onNext(mediaItems);
			}
		});
		return arrayListObservable;
	}


    public Observable<ArrayList<MediaItem>> ObservableFindAll() {
        Observable<ArrayList<MediaItem>> arrayListObservable = Observable.create(new ObservableOnSubscribe<ArrayList<MediaItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MediaItem>> e) throws Exception {
                ArrayList<MediaItem> mediaItems = findAll();
                if (mediaItems!=null){
                    for (int i=0;i<mediaItems.size();i++){
                        if (!FileUtils.isFileExist(mediaItems.get(i).getPath())) {
                            deleteFileByName(mediaItems.get(i).getName());
                            mediaItems.remove(mediaItems.get(i));
                        }
                    }
                }
                e.onNext(mediaItems);
            }
        });
        return arrayListObservable;
    }




    public MediaItem findItemByName(String name) {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase(MyApplication.getInstance().DB_KEY);
            String sql = "select " + DBTableConfig.MediaFile.CREATE_TIME + ","
                    + DBTableConfig.MediaFile.TYPE + "," + DBTableConfig.MediaFile.PATH
                    + " from " + DBTableConfig.MediaFile.TABLE_NAME
                    + " where " + DBTableConfig.MediaFile.NAME + "=?";
            Cursor cursor = db.rawQuery(sql, new String[]{name});
            MediaItem bean = new MediaItem();
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    bean.setName(name);
                    bean.setCrateTime(cursor.getString(0));
                    bean.setMediaType(cursor.getInt(1));
                    bean.setPath(cursor.getString(2));

                }
                cursor.close();
            }
            db.close();

            return bean;
        }catch (Exception e){

        }
        return null;
    }
}
