package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.MyAlbumAdapter;
import com.kaadas.lock.bean.FileBean;
import com.kaadas.lock.bean.FileItemBean;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.DateUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.yun.software.kaadas.Utils.FileTool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockVideoAlbumActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;

    private MyAlbumAdapter adapter;

    List<FileBean> items = new ArrayList<>();

    String wifiSn = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_album);

        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        String filePath = FileTool.getVideoLockPath(this,wifiSn).getPath();

        new Thread(new Runnable() {
            @Override
            public void run() {
                searchFiles(filePath);

            }
        }).start();

    }

    private void initRecycleView() {
        adapter = new MyAlbumAdapter(items,WifiLockVideoAlbumActivity.this);
        recycleview.setLayoutManager(new LinearLayoutManager(WifiLockVideoAlbumActivity.this));
        recycleview.setAdapter(adapter);
    }

    private void searchFiles(String path) {
        if(path.isEmpty()){
            return;
        }
        File file = new File(path);
        File[] list = file.listFiles();

        List<FileItemBean> files = new ArrayList<>();
        String str = "";
        for(int i = 0 ; i < list.length;i++){
            if(list[i].isFile() && !list[i].isHidden()){
                str = list[i].getName().substring(list[i].getName().length() - 3 ,list[i].getName().length());
                if(str.equals("mp4")){
                    files.add(new FileItemBean(list[i].getName(),list[i].getPath(),list[i].toURI(),
                            list[i].lastModified(),"mp4",2));
                }else if(str.equals("jpg")){
                    files.add(new FileItemBean(list[i].getName(),list[i].getPath(),list[i].toURI(),
                            list[i].lastModified(),"jpg",1));
                }else if(str.equals("png")){
                    files.add(new FileItemBean(list[i].getName(),list[i].getPath(),list[i].toURI(),
                            list[i].lastModified(),"png",1));
                }
            }

        }

        Collections.sort(files, new Comparator<FileItemBean>() {
            @Override
            public int compare(FileItemBean o1, FileItemBean o2) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Date date1 = new Date(o1.getLastModified());
                Date date2 = new Date(o2.getLastModified());
                if(date1.before(date2)){
                    return 1;
                }else{
                   return -1;
                }
            }
        });

        for(FileItemBean item : files){
            LogUtils.e(item.toString());
        }

        int times = 0;
        String str1 = "";
        String str2 = "";
        int temp = 0;
        for(int j = 0 ; j <files.size() ; j++){
            str1 = DateUtils.getDayTimeFromat(files.get(j).getLastModified());
            if(j+1 < files.size()){
                str2 = DateUtils.getDayTimeFromat(files.get(j+1).getLastModified());
            }else{
                str2 = "";
            }

            if(!str1.equals(str2)){
                items.add(new FileBean(str1,files.subList(temp,j+1)));
                temp = j + 1;
                times++;
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initRecycleView();
            }
        });

        for(FileBean i : items){
            LogUtils.e(i.toString());
        }
    }

    @OnClick(R.id.back)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

        }
    }

}
