package com.kaadas.lock.utils;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create By denganzhi  on 2019/7/16
 * Describe
 */

public class MyLog {


    boolean isOpen=true;

    private static MyLog instance;
    Date now = new Date(); //获取当前时间
    SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy年MM月dd");// "yyyy-MM-dd HH:mm:ss.fff"
    SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy--MM--dd HH:mm:ss.SSS");// "yyyy-MM-dd HH:mm:ss.fff"

    String newFileTime=null;
    String writeFileTime=null;
    String dirPath =null;
    File file=null;
    FileWriter filerWriter=null;
    BufferedWriter bufWriter=null;
    public  void  init(Context mContext) throws IOException {

         newFileTime=  simpleDateFormat1.format(now);

         dirPath = mContext.getExternalFilesDir("").getAbsolutePath();
         file = new File(dirPath + File.separator + newFileTime
                +".txt");
         if(!file.exists()){
            file.createNewFile();

         }
         filerWriter = new FileWriter(file, true);
         bufWriter = new BufferedWriter(filerWriter);
    }

    private MyLog(){}
    public static MyLog getInstance()
    {
        if(instance == null)
        {
            synchronized(MyLog.class)
            {
                if(instance == null)
                {
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



    public  void save(String context){

        if(isOpen){
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
                if(file !=null || !file.exists()){
                    file.createNewFile();
                    filerWriter = new FileWriter(file, true);
                    bufWriter = new BufferedWriter(filerWriter);
                }

                writeFileTime= simpleDateFormat2.format(new Date());
                // 获得当前类名
                String clazz = Thread.currentThread() .getStackTrace()[3].getClassName();
                // 获得当前方法名
                String method = Thread.currentThread() .getStackTrace()[3].getMethodName();
                String methodPath= clazz+"."+method;
                context= methodPath+" "+writeFileTime+" "+ context;
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
