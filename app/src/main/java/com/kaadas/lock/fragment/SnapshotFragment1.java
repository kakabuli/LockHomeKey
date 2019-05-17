package com.kaadas.lock.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.PreviewActivity;
import com.kaadas.lock.adapter.PirHistoryAdapter;
import com.kaadas.lock.adapter.TimeAdapter;
import com.kaadas.lock.bean.MyDate;
import com.kaadas.lock.bean.PirEventBus;
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.FtpEnable;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.PirConst;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.FtpException;
import com.kaadas.lock.utils.ftp.FtpUtils;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.HistoryInfo;
import com.kaadas.lock.utils.greenDao.db.HistoryInfoDao;
import com.kaadas.lock.widget.GravityPopup;
import com.kaadas.lock.widget.GravityPopup.HidePopup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.http.GET;

//<ISnapShotView, SnapPresenter<ISnapShotView>>
//implements ISnapShotView
public class SnapshotFragment1 extends CallBackBaseFragment<ISnapShotView, SnapPresenter<ISnapShotView>> implements ISnapShotView {


    @BindView(R.id.pir_history_notic_tv)
    TextView pir_history_notic_tv;  //通知

    @BindView(R.id.pir_history_all_ll)
    LinearLayout pir_history_all_ll;

    @BindView(R.id.pir_history_add_cancle)
    LinearLayout pir_history_add_cancle;
    @BindView(R.id.history_rv_ff)
    SwipeMenuRecyclerView history_rv_ff;

    @BindView(R.id.history_refreshLayout_ff)
    SmartRefreshLayout history_refreshLayout_ff;
    @BindView(R.id.test_snap)
    Button test_snap;

    String ftpCmdPort="";//ftp端口
    String ftpCmdIp="";//ftpip
    boolean isFresh=false;
    Dialog dialog=null;
    LinkedList<String> newimageList=null;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat simpleDateFormatToday = new SimpleDateFormat("yyyyMMdd");
    String lastDate=null;
    boolean isVisibleToUserTo=true;
    String today=null;
    Date today_date=null;
    SimpleDateFormat simpleDateFormat0 = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected SnapPresenter<ISnapShotView> createPresent() {
        return new SnapPresenter();
    }

    @Override
    public View initView(LayoutInflater inflater,  ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_snapshot, container, false);
        return view;
    }

    @Override
    public void initOtherFunction() {
        initPIR();
    }

    //    public void testMethod(View view){
//        date_mPopupWindow.notifydatechangeMonth(month12,true);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat0.setTimeZone(TimeZone.getTimeZone("GMT+0"));
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
            case  R.id.pir_history_add_cancle:
                pir_history_all_ll.setVisibility(View.GONE);
                catEyeCount=0;
                break;
            case  R.id.test_snap:

                int selectDay= day;
                if(selectDay<10){
                    currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+"0"+selectDay;
                }else{
                    currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+selectDay;
                }
                Toast.makeText(getActivity(),currentDate,Toast.LENGTH_SHORT).show();

             // 唤醒FTP
           //     mPresenter.weakUpFTP(gatewayId,deviceId);
                break;

            case R.id.pir_history_all_ll:
                if(!isFresh){
                    pir_history_all_ll.setVisibility(View.GONE);
                    catEyeCount=0;

                    year_tv.setText(year+"");
                    time_tv.setText(month+"");
                    date_mPopupWindow.notifydatechangeMonth(current_year_month_length,true); //月份
                    myDateList.clear();
                    for (int i=0;i<current_month_date.size();i++){
                        if(i==0){
                            MyDate myDate= current_month_date.get(i);
                            myDate.setChecked(true);
                            myDateList.add(myDate);
                            continue;
                        }
                        MyDate myDate2= current_month_date.get(i);
                        myDate2.setChecked(false);
                        myDateList.add(myDate2);
                    }
                    timeAdapter.notifyDataSetChanged();

                    time_select_rl.scrollToPosition(0);

                    if(month<10){
                        currentDate = year+"0"+month;
                    }else {
                        currentDate = year+month+"";
                    }
                    if(day<10){
                        currentDate = currentDate+"0"+day;
                    }else{
                        currentDate = currentDate+day;
                    }
//                    if(day<10){
//                        currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+"0"+day;
//                    }else{
//                        currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+day;
//                    }
                    Log.e(GeTui.VideoLog,"currentDate:"+currentDate);
                    history_refreshLayout_ff.autoRefresh();
                }
                break;
        }
    }

    PirHistoryAdapter pirHistoryAdapter;
    List<String> imageList=new ArrayList<>();
    int catEyeCount=0;
    String key=null;
    private  void initPIR(){
        pir_history_add_cancle.setOnClickListener(this);
        pir_history_all_ll.setOnClickListener(this);
        test_snap.setOnClickListener(this);
        today= simpleDateFormatToday.format(new Date());
        try {
            today_date= simpleDateFormatToday.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        key =deviceId+ GeTui.CATEYE_KEY;
        catEyeCount= (int) SPUtils2.get(MyApplication.getInstance(),key,0);
        if(catEyeCount!=0 && catEyeCount!=-1){
            String format= String.format( getActivity().getResources().getString(R.string.pir_history_notic), catEyeCount);
            pir_history_notic_tv.setText(format);
            pir_history_all_ll.setVisibility(View.VISIBLE);
            SPUtils2.remove(getActivity(),key);
        }else{
            pir_history_all_ll.setVisibility(View.GONE);
        }

        for (int i=0;i<20;i++){
         //   imageList.add("2019-04-"+i+"12:34:23");
        }

     //   history_rv_ff.setSwipeItemClickListener(mItemClickListener); // RecyclerView Item点击监听。
        // 侧滑
        history_rv_ff.setSwipeMenuCreator(swipeMenuCreator);
        history_rv_ff.setSwipeMenuItemClickListener(mMenuItemClickListener);


        history_refreshLayout_ff.setEnableLoadMore(false);
        history_refreshLayout_ff.setEnableRefresh(true);

        pirHistoryAdapter = new PirHistoryAdapter(getActivity(),imageList);
        history_rv_ff.setLayoutManager(new LinearLayoutManager(getActivity()));
        history_rv_ff.setAdapter(pirHistoryAdapter);

        showDataFirst();

        pirHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(isFresh){
                    return;
                }
                String imgUrl= newimageList.get(position);
                String newImgUrl= imageList.get(position);
                SPUtils2.put(MyApplication.getInstance(),newImgUrl,"looksuccess");
                Intent intent=new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra("imgUrl",imgUrl);
                intent.putExtra("deviceId",deviceId);
                intent.putExtra("newImgUrl",newImgUrl);
                intent.putExtra("gatewayId",gatewayId);

                //LinearLayout itemFather=(LinearLayout) view.getParent();
                view.findViewById(R.id.pir_history_tv_cicle).setVisibility(View.INVISIBLE);

                startActivity(intent);
            }
        });

        history_refreshLayout_ff.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(TextUtils.isEmpty(currentDate)){
                    Toast.makeText(getActivity(), getResources().getString(R.string.snap_select_data), Toast.LENGTH_SHORT).show();
                    refreshlayout.finishRefresh();
                    return;
                }

                if(currentDate.equals(today)){
                    //	Toast.makeText(getActivity(),"今天",Toast.LENGTH_SHORT).show();
                    List<HistoryInfo> historyInfos=MyApplication.getInstance().getDaoWriteSession().getHistoryInfoDao().
                            queryBuilder().where(HistoryInfoDao.Properties.CreateDate.eq(today_date))
                            .where(HistoryInfoDao.Properties.Device_id.eq(deviceId)).list();
                    Log.e("denganzhi1","history-->result==>:"+historyInfos.toString());
                    if(historyInfos!=null && historyInfos.size() > 0){
                        if(newimageList==null){
                            newimageList= new LinkedList<>();
                        }
                        MyApplication.getInstance().getDaoWriteSession().getHistoryInfoDao().queryBuilder()
                                .where(HistoryInfoDao.Properties.CreateDate.eq(today_date))
                                .where(HistoryInfoDao.Properties.Device_id.eq(deviceId))
                                .buildDelete().executeDeleteWithoutDetachingEntities();

                        for (int i= 0 ;i <  historyInfos.size() ; i++){
                            String fileName= historyInfos.get(i).getFileName();
                            //newimageList.add
                            newimageList.addFirst(fileName);
                        }
                        showPirHistoryData();
                        history_refreshLayout_ff.finishRefresh();

                        String key= deviceId + currentDate.replace("-","");
                        String json=new Gson().toJson(newimageList);
                        SPUtils2.put(MyApplication.getInstance(),key,json);
                        return;
                    }
                }

                isFresh=true;
                pir_history_all_ll.setVisibility(View.GONE);
                catEyeCount=0;

                mPresenter.weakUpFTP(gatewayId,deviceId);

               // refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        history_refreshLayout_ff.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SnapshotPirFun(String str) {

        if(isVisibleToUserTo  && !MyApplication.getInstance().isPreviewActivity()  && !MyApplication.getInstance().isMediaPlayerActivity()){

            if(str.equals(GeTui.FTP_LIST_SUC)){
                newimageList= MyApplication.getInstance().getPirListImg();
                imageList.clear();
                showPirHistoryData();

            }else if(FtpException.DOWNLOADEXCEPTION.equals(str)){

                if(dialog!=null){
                    dialog.dismiss();
                }
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;

                newimageList= MyApplication.getInstance().getPirListImg();
                Log.e(GeTui.VideoLog,"newImageList=========>:"+newimageList);
                if(newimageList!=null && newimageList.size()>0){
                    imageList.clear();
                    showPirHistoryData();
                }else{
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.ftp_connection_fail),Toast.LENGTH_SHORT).show();
                }


            }else if(str.equals(GeTui.PIR_FTP_FAIL)){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.pir_ftp_fail),Toast.LENGTH_SHORT).show();
                if(dialog!=null){
                    dialog.dismiss();
                }
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;
            }else if(str.equals(GeTui.FTP_CONNECTION_FAIL)){
//			Log.e(GeTui.VideoLog,"isShowforeground:"+isShowforeground);
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.ftp_connection_fail),Toast.LENGTH_SHORT).show();
                if(dialog!=null){
                    dialog.dismiss();
                }
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;
            }else if(str.equals(GeTui.FTP_LOGIN_FAIL)){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.ftp_login_fail),Toast.LENGTH_SHORT).show();
                if(dialog!=null){
                    dialog.dismiss();
                }
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;

            }else if(GeTui.PIR_FTP_SUC.equals(str)){
                //Log.e(GeTui.VideoLog,"PirHistoryFragment====>pir触发成功.....");
            }else if(GeTui.FTP_DIR_NO.equals(str)){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.cat_eye_no_ftp),Toast.LENGTH_SHORT).show();
                if(dialog!=null){
                    dialog.dismiss();
                }
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;
            }
        }
        // 图片下载成功,并且拷贝成功
        if(str.equals(GeTui.FTP_IMAGE_COPY)   /*&& MyApplication.getInstance().isPreviewActivity() */ ){
            Log.e(GeTui.VideoLog,"===================PirHistoryFragment=============");
            pirHistoryAdapter.notifyDataSetChanged();
        }
    }

    // 收到EventBus Pir消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void imagePirSuccess(PirEventBus str) {
        //	if(str.equals(GeTui.CATEYE_PIR)){
        if(str!=null && str.getDeviceId().equals(deviceId)){
            catEyeCount++;
            if(catEyeCount!=0 && catEyeCount!=-1){
                String format= String.format( getActivity().getResources().getString(R.string.pir_history_notic), catEyeCount);
                pir_history_notic_tv.setText(format);
                pir_history_all_ll.setVisibility(View.VISIBLE);
                SPUtils2.remove(getActivity(),key);
            }
        }
        //	}
    }

    // 点击日期
    @Override
    public void getPirData(int position) {
        super.getPirData(position);
        int selectDay= myDateList.get(position).getDay();
        int selectMonth= Integer.parseInt(time_tv.getText().toString());

        if(selectMonth==0 && selectDay==0){
            Toast.makeText(getActivity(),getString(R.string.name_select_fail),Toast.LENGTH_LONG).show();
            return;
        }
        if(selectMonth < 10){
            currentDate = year_tv.getText().toString()+"0"+selectMonth;
        }else {
            currentDate = year_tv.getText().toString()+selectMonth;
        }
        if(selectDay<10){
            currentDate = currentDate+"0"+myDateList.get(position).getDay();
        }else{
            currentDate = currentDate+myDateList.get(position).getDay();
        }

        pir_history_all_ll.setVisibility(View.GONE);
        catEyeCount=0;
        pir_history_all_ll.setVisibility(View.GONE);
        if(isFresh){
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.data_get_loading),Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_loading,null);
        customizeDialog.setView(dialogView);
        dialog =customizeDialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕其他地方Dialog不消失
        dialog.setCancelable(false);//点击返回键不消失
        String key= deviceId+currentDate+"";
      //  Log.e(GeTui.VideoLog,"key------->:"+key);
        String str0=(String) SPUtils2.get(MyApplication.getInstance(),key,"");
        if(!TextUtils.isEmpty(str0)){
            if(newimageList!=null && newimageList.size()>0 && lastDate!=null  && lastDate.equals(currentDate)){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.pull_refefresh_new_data),Toast.LENGTH_SHORT).show();
                lastDate= currentDate;
            }
            if(newimageList!= null && newimageList.size()>0){
                newimageList.clear();
            }
            newimageList=new Gson().fromJson(str0, new TypeToken<LinkedList<String>>() {}.getType());
            if(newimageList!=null && newimageList.size()>0){
                if(dialog!=null){
                    dialog.dismiss();
                }
               // lastDate= currentDate;
                showPirHistoryData();

            }else{
                mPresenter.weakUpFTP(gatewayId,deviceId);
            }
        }else {
                mPresenter.weakUpFTP(gatewayId,deviceId);
        }
               lastDate= currentDate;
        // 唤醒FTP
     //   mPresenter.weakUpFTP(gatewayId,deviceId);
     //   Toast.makeText(getActivity(),currentDate,Toast.LENGTH_SHORT).show();
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

    String currentTimeFolder=null;
    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            if(isFresh){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.data_get_loading),Toast.LENGTH_SHORT).show();
                return;
            }

            // int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

         //   Toast.makeText(getActivity(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            int position = adapterPosition - history_rv_ff.getHeaderItemCount();

            //Toast.makeText(getActivity(), "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();

            try{
                //1557313701_picture.jpg
                String imageUrl = newimageList.get(position);
                newimageList.remove(position);
                Date remote_date0 = new Date(Long.parseLong(imageUrl.split("_")[0])*1000);
                String remote_time = simpleDateFormat0.format(remote_date0);
                currentTimeFolder = "orangecat-"+ remote_time;
                int end  =imageUrl.lastIndexOf("_");
                String imagePathTime= imageUrl.substring(0,end);
//                catEyeSingleEntryAdapter.notifyItemRemoved(position);
//                catEyeSingleEntryAdapter.notifyItemRangeChanged(position, currentPictuer.size() - position);
//                Log.e("denganzhi3","positon:"+position);
//                int start= imagePathName.lastIndexOf("/")+1;
//                int end  =imagePathName.lastIndexOf("_");
//                String imagePathTime= imagePathName.substring(start,end);  // 1554773091
//                int start0= imagePathName.lastIndexOf("/");
//                int start1=imagePathName.substring(0, start0).lastIndexOf("/");
//                String currentTimeFolder= imagePathName.substring(0, start0).substring(start1+1, imagePathName.substring(0, start0).length());
                //orangecat-20190409
                String imagePath =  MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +Constants.DOWNLOAD_IMAGE_FOLDER_NAME + File.separator + deviceId+File.separator+currentTimeFolder+File.separator+imageUrl;
                String audioPath =  MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +Constants.DOWNLOAD_AUDIO_FOLDER_NAME + File.separator + deviceId+File.separator+currentTimeFolder+File.separator+imagePathTime+"_audio.raw";
                String h264Path =  MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +Constants.DOWNLOAD_VIDEO_FOLDER_NAME + File.separator + deviceId+File.separator+currentTimeFolder+File.separator+imagePathTime+"_video.h264";
                String mp4Path =MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator +Constants.COMPOUND_FOLDER + File.separator + deviceId+File.separator+currentTimeFolder+File.separator+imagePathTime+".mp4";
                File imagefile= new File(imagePath);
                File audiofile=new File(audioPath);
                File h264file = new File(h264Path);
                File mp4file = new File(mp4Path);
                String item = imageList.get(position);
                String item_key=item+ PirConst.IMG_DOWNLOAD_SUC;
                SPUtils2.remove(getActivity(),item_key);
                SPUtils2.remove(getActivity(),item);
                imageList.remove(position);
                String key=deviceId+item.split(" ")[0].replace("-","");
                String result= new Gson().toJson(newimageList);
                SPUtils2.put(getActivity(),key,result);
                Log.e("denganzhi3","audiofile:"+imagefile.getAbsolutePath());
                Log.e("denganzhi3","audiofile:"+audiofile.getAbsolutePath());
                Log.e("denganzhi3","audiofile:"+h264file.getAbsolutePath());
                Log.e("denganzhi3","h264file:"+mp4file.getAbsolutePath());
                if(imagefile.exists()){
                    Log.e("denganzhi3","图片存在");
                    imagefile.delete();
                }
                if(audiofile.exists()){
                    Log.e("denganzhi3","音频存在");
                    audiofile.delete();
                }
                if(h264file.exists()){
                    Log.e("denganzhi3","h264存在");
                    h264file.delete();
                }
                if(mp4file.exists()){
                    Log.e("denganzhi3","mp4存在");
                    mp4file.delete();
                }
                }catch (Exception e){
                //	Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.delete_error),Toast.LENGTH_SHORT).show();
                }



                pirHistoryAdapter.notifyItemRemoved(position);

//            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
//            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
//                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
//            }
        }
    };
    String currentDate=null;
    @Override
    public void showFTPResultSuccess(FtpEnable ftpEnable) {
        ftpCmdPort= ftpEnable.getReturnData().getFtpCmdPort()+"";
        ftpCmdIp =  ftpEnable.getReturnData().getFtpCmdIp()+"";
        //  /sdap0/storage/orangecat-20190409
        String  remotePath=  Constants.CAT_EYE_FTP_NAME+currentDate;
        Log.e(GeTui.VideoLog,"FTP唤醒成功............"+ftpCmdIp+" 端口:"+ftpCmdPort);

        // 用户选择时间，猫眼deviceId
        FtpUtils.getInstance().loadPirListFile(ftpCmdIp,ftpCmdPort,remotePath,deviceId,currentDate);
    }

    @Override
    public void showFTPResultFail() {
      Toast.makeText(getActivity(),getResources().getString(R.string.ftp_weak_up_fail),Toast.LENGTH_SHORT).show();
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    public void showFTPOverTime() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),getResources().getString(R.string.connect_out_of_date),Toast.LENGTH_SHORT).show();
                if(dialog!=null){
                    dialog.dismiss();
                }
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;
            }
        });
    }

    private void showPirHistoryData(){
        if(newimageList!=null){
            if(imageList!=null && imageList.size()>0){
                imageList.clear();
            }
            for (int i=0;i<newimageList.size();i++){
                String iImages= newimageList.get(i);
                long iImagestart= Long.parseLong(iImages.split("_")[0])*1000;
                String endTime=simpleDateFormat.format(new Date(iImagestart));
                imageList.add(endTime);
            }
            //Log.e(GeTui.VideoLog,"PirHistoryFragment拿到快照了...."+str +" imageList:"+imageList);
            if(imageList!=null){
                history_refreshLayout_ff.finishRefresh();
                isFresh=false;
                Collections.sort(imageList);
                Collections.reverse(imageList);
                pirHistoryAdapter.notifyDataSetChanged();
                history_rv_ff.scrollToPosition(0);
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
            if(imageList.size()==0){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.history_pir_no),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDataFirst(){
        if(day<10){
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+"0"+day;
        }else{
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+day;
        }
        String key= deviceId+currentDate+"";
        String str0=(String) SPUtils2.get(MyApplication.getInstance(),key,"");
        if(!TextUtils.isEmpty(str0)){
            if(newimageList!= null && newimageList.size()>0){
                newimageList.clear();
            }
            newimageList=new Gson().fromJson(str0, new TypeToken<LinkedList<String>>() {}.getType());
            if(newimageList!=null && newimageList.size()>0){
                if(dialog!=null){
                    dialog.dismiss();
                }
                lastDate= currentDate;
                showPirHistoryData();
            }else{

            }
        }else{
            Toast.makeText(getActivity(),getString(R.string.snap_select_data),Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Item点击监听。
     */
//    private SwipeItemClickListener mItemClickListener = new SwipeItemClickListener() {
//        @Override
//        public void onItemClick(View itemView, int position) {
//            Toast.makeText(getActivity(), "第SwipeItemClickListener：" + position + "个", Toast.LENGTH_SHORT).show();
//        }
//    };






}
