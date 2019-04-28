package com.kaadas.lock.linphone.util;

import android.os.Environment;

import com.kaadas.lock.MyApplication;

import java.io.File;

/**
 * Created by ${howard} on 2018/4/12.
 */

public class Util {
    public final static String KAADAS = "kaadas";
//    public final static String KAADAS_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + KAADAS;
    public final static String KAADAS_DIR = MyApplication.getInstance().getExternalFilesDir("") + File.separator + KAADAS;
    public final static String VIDEO_DIR = KAADAS_DIR + "/videos";
    public final static String PICTURE_DIR = KAADAS_DIR + "/pictures";
    public final static String RECORD_VIDEO_PATH = VIDEO_DIR + "/"+"20181314520.mkv";
    //系统相册路径
//	public  static final String SYSTEM_ALBUM_PATH=Environment.getExternalStorageDirectory()
	public  static final String SYSTEM_ALBUM_PATH=MyApplication.getInstance().getExternalFilesDir("")
		+ File.separator + Environment.DIRECTORY_DCIM
		+File.separator+"Camera";

}
