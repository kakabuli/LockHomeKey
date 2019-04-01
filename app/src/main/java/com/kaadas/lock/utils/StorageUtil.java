package com.kaadas.lock.utils;

import android.graphics.Bitmap;


import com.kaadas.lock.MyApplication;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by David on 2019/3/7
 */
public class StorageUtil {
    private static StorageUtil storageUtil = null;


    public static StorageUtil getInstance() {
        if (storageUtil == null) {
            storageUtil = new StorageUtil();
        }
        return storageUtil;
    }


    public void saveServerPhoto(Bitmap mBitmap) {
        FileOutputStream b = null;
        File file = new File(MyApplication.getInstance().getExternalFilesDir("") + "/ImagePicker/cropTemp/");
        file.mkdirs();// 创建文件夹
        // 图片名字
      File photoPath=new File(file,"serverHead.png");
        try {
            b = new FileOutputStream(photoPath);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
            SPUtils.put(KeyConstants.HEAD_PATH, photoPath);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("写入图片异常：" + e.toString());
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
