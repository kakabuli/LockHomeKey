package com.kaadas.lock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.MediaPlayerActivity;
import com.kaadas.lock.activity.cateye.PreviewActivity;
import com.kaadas.lock.activity.cateye.RecordingPreviewActivity;
import com.kaadas.lock.adapter.PirHistoryAdapter;
import com.kaadas.lock.adapter.RecordingAdapter;
import com.kaadas.lock.mvp.presenter.RecordingPresenter;
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.IRecordingView;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.PirConst;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.db.MediaItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.linphone.mediastream.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class RecordingFragment extends CallBackBaseFragment <IRecordingView, RecordingPresenter<IRecordingView>> implements IRecordingView {


    @BindView(R.id.recording_rv_ff)
    SwipeMenuRecyclerView recording_rv_ff;
    RecordingFragment recordingFragment;
    @BindView(R.id.recording_btn)
    Button recording_btn;

    public RecordingFragment() {
        // Required empty public constructor
    }

    List<String> imageList=new ArrayList<>();

    @Override
    protected RecordingPresenter<IRecordingView> createPresent() {
        return new RecordingPresenter();
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);
        return view;
    }
    RecordingAdapter recordingAdapter;
    String currentDate=null;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    long currentTimeStamp=-1;
    @Override
    public void initOtherFunction() {

//        for (int i=0;i<20;i++){
//            imageList.add("2019-04-"+i+"12:34:23");
//        }
        if(day<10){
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+"0"+day;
        }else{
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+day;
        }
        try {
            currentTimeStamp= simpleDateFormat.parse(currentDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 侧滑
        recording_rv_ff.setSwipeMenuCreator(swipeMenuCreator);
        recording_rv_ff.setSwipeMenuItemClickListener(mMenuItemClickListener);

        recording_btn.setOnClickListener(this);
        recordingAdapter = new RecordingAdapter(getActivity(),currentDateItem);
        recording_rv_ff.setLayoutManager(new LinearLayoutManager(getActivity()));
        recording_rv_ff.setAdapter(recordingAdapter);



        mPresenter.fetchVideoAndImage(deviceId,getActivity());


        recordingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                int mediaType = currentDateItem.get(position).getMediaType();
                String name= currentDateItem.get(position).getName();
                String path=currentDateItem.get(position).getPath();
                SPUtils2.put(MyApplication.getInstance(),name+Constants.RECORDINGTAG,"looksuccess");
                //LinearLayout itemFather=(LinearLayout) view.getParent();
                if(mediaType==2){
                    Intent intent=new Intent(getActivity(), RecordingPreviewActivity.class);
                    intent.putExtra("path",path);
                    startActivity(intent);
                }else if(mediaType==1){
                    Intent intent=new Intent(getActivity(), MediaPlayerActivity.class);
                    intent.putExtra(Constants.MEDIA_PATH,path);
                    startActivity(intent);
                }
                view.findViewById(R.id.pir_history_tv_cicle).setVisibility(View.INVISIBLE);
            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recordingFun(String str) {
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.year_select_ll:
                mPopupWindow.showPopupWindow(v);
                break;
            case  R.id.day_select_ll:
                date_mPopupWindow.showPopupWindow(v);
                break;
            case  R.id.recording_btn:
             //  mPresenter.fetchVideoAndImage(deviceId,getActivity());
                mPresenter.deleteVideoAndImage(deviceId,getActivity());
                break;
        }
    }


    @Override
    public void getPirData(int position) {
        super.getPirData(position);

        int selectDay= myDateList.get(position).getDay();
        if(selectDay<10){
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+"0"+myDateList.get(position).getDay();
        }else{
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+myDateList.get(position).getDay();
        }
        try {
            currentTimeStamp= simpleDateFormat.parse(currentDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mPresenter.fetchVideoAndImage(deviceId,getActivity());

    }

    List<MediaItem> allMedia=new ArrayList<>();
    List<MediaItem> currentDateItem=new ArrayList<>();
    @Override
    public void showFetchResult(ArrayList<MediaItem> mediaItemArrayList) {
        Log.e("denganzhi1","mediaItemArrayList:"+mediaItemArrayList);

        allMedia.clear();
        currentDateItem.clear();

        allMedia= mediaItemArrayList;
        for (int i=0;i<allMedia.size();i++){
            MediaItem mediaItem= allMedia.get(i);
            long selectItemTime= Long.parseLong(mediaItem.getCreateTime());
            String name = mediaItem.getName();
        //    long selectItem= Long.parseLong(name.split("_")[0]);
            long nextDay = currentTimeStamp+86400000;
            if(selectItemTime>=currentTimeStamp && selectItemTime < nextDay) {
                if (name.contains(deviceId)) {
                   currentDateItem.add(mediaItem);
                }
            }
        }
        if(currentDateItem.size()==0){
            Toast.makeText(getActivity(),getResources().getString(R.string.no_data),Toast.LENGTH_SHORT).show();

        }
        recordingAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteResult(Boolean isFlag) {
         //   Toast.makeText(getActivity(),isFlag+"",Toast.LENGTH_SHORT).show();
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
//            {
//                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.selector_green)
//                        .setImage(R.mipmap.ic_action_add)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.selector_red)
//                        .setImage(R.mipmap.ic_action_close)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
//            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText(getResources().getString(R.string.delete))
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

//                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.selector_green)
//                        .setText("添加")
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            // int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            //   Toast.makeText(getActivity(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            int position = adapterPosition - recording_rv_ff.getHeaderItemCount();

           // Toast.makeText(getActivity(), "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();

            String name= currentDateItem.get(position).getName();

            mPresenter.deleteVideoAndImage(name,getActivity());
            String imagepath= currentDateItem.get(position).getPath();
            File file=new File(imagepath);
            file.delete();

            currentDateItem.remove(position);
            SPUtils2.remove(getActivity(),name+Constants.RECORDINGTAG);

            recordingAdapter.notifyItemRemoved(position);

        }
    };
}
