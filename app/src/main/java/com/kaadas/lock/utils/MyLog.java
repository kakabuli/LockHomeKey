package com.kaadas.lock.utils;

import android.content.Context;
import android.widget.Toast;

import com.kaadas.lock.R;

import org.linphone.mediastream.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Create By denganzhi  on 2019/7/16
 * Describe
 */

public class MyLog {


    boolean isOpen = true;

    private static MyLog instance;
    Date now = new Date(); //获取当前时间
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy_MM_dd");// "yyyy-MM-dd HH:mm:ss.fff"
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS");// "yyyy-MM-dd HH:mm:ss.fff"

    String newFileTime = null;
    String writeFileTime = null;
    String dirPath = "-凯迪仕-";
    File file = null;
    FileWriter filerWriter = null;
    BufferedWriter bufWriter = null;
    Context app_context = null;
    List<String> fileArr=new ArrayList<String>();
    public void init(Context mContext) {
        app_context = mContext;
        try {
            newFileTime = simpleDateFormat1.format(now);

            dirPath = mContext.getExternalFilesDir("").getAbsolutePath();
            file = new File(dirPath + File.separator + newFileTime
                    + ".txt");
            if (!file.exists()) {
                file.createNewFile();

            }
            File dirPathFile=new File(dirPath);
            fileArr.clear();
            for (String fileStr: dirPathFile.list()){
                if(!fileStr.contains(".txt")){
                    continue;
                }
                fileArr.add(fileStr);
            }
            if(fileArr.size()>5){
                Collections.sort(fileArr);
                Collections.reverse(fileArr);
                for (int i=5;i<fileArr.size();i++){
                    File delete=new File(dirPath + File.separator + fileArr.get(i));
                    delete.delete();
                }
            }

            filerWriter = new FileWriter(file, true);
            bufWriter = new BufferedWriter(filerWriter);
        } catch (Exception e) {
            Toast.makeText(mContext, mContext.getString(R.string.log_info), Toast.LENGTH_SHORT).show();
        }
    }

    private MyLog() {
    }

    public static MyLog getInstance() {
        if (instance == null) {
            synchronized (MyLog.class) {
                if (instance == null) {
                    instance = new MyLog();
                }
            }
        }
        return instance;
    }


//    private MyLog() {
//    }
//
//    private static class SingletonHolder {
//        private static final MyLog singleton = new MyLog();
//    }
//
//    public static MyLog getInstance() {
//
//        return SingletonHolder.singleton;
//
//    }


    public void save(String context) {

        if (isOpen) {
            try {
//                String newFileTime=  simpleDateFormat1.format(now);
//                String writeFileTime= simpleDateFormat2.format(now);
//                String dirPath = mContext.getExternalFilesDir("").getAbsolutePath();
//                File file = new File(dirPath + File.separator + newFileTime
//                        +".txt");
//                if(!file.exists()){
//                    file.createNewFile();
//                }
//                FileWriter filerWriter = new FileWriter(file, true);
//                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                if (file != null || !file.exists()) {
                    file.createNewFile();
                    filerWriter = new FileWriter(file, true);
                    bufWriter = new BufferedWriter(filerWriter);
                } else {
                    newFileTime = simpleDateFormat1.format(now);

                    dirPath = app_context.getExternalFilesDir("").getAbsolutePath();
                    file = new File(dirPath + File.separator + newFileTime
                            + ".txt");
                    if (!file.exists()) {
                        file.createNewFile();

                    }
                    filerWriter = new FileWriter(file, true);
                    bufWriter = new BufferedWriter(filerWriter);
                }

                writeFileTime = simpleDateFormat2.format(new Date());
                // 获得当前类名
                String clazz = Thread.currentThread().getStackTrace()[3].getClassName();
                // 获得当前方法名
                String method = Thread.currentThread().getStackTrace()[3].getMethodName();
                String methodPath = clazz + "." + method;
                context = methodPath + " " + writeFileTime + " " + context;
                bufWriter.write(context);
                bufWriter.newLine();
                bufWriter.flush();
                filerWriter.flush();
//                bufWriter.close();
//                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
