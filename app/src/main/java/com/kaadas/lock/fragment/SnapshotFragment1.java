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
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.FtpEnable;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.FtpException;
import com.kaadas.lock.utils.ftp.FtpUtils;
import com.kaadas.lock.utils.ftp.GeTui;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    String deviceId = "";
    String gatewayId="";
    String ftpCmdPort="";//ftp端口
    String ftpCmdIp="";//ftpip
    boolean isFresh=false;
    Dialog dialog=null;
    LinkedList<String> newimageList=null;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String lastDate=null;
    boolean isVisibleToUserTo=true;
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
        if (getArguments() != null) {
            deviceId = getArguments().getString(Constants.DEVICE_ID);
            gatewayId= getArguments().getString(Constants.GATEWAY_ID);
        }
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
                break;
            case  R.id.test_snap:


             // 唤醒FTP
           //     mPresenter.weakUpFTP(gatewayId,deviceId);
                break;
        }
    }

    PirHistoryAdapter pirHistoryAdapter;
    List<String> imageList=new ArrayList<>();
    int catEyeCount=-1;
    private  void initPIR(){
        pir_history_add_cancle.setOnClickListener(this);
        test_snap.setOnClickListener(this);
        String format= String.format( getActivity().getResources().getString(R.string.pir_history_notic), catEyeCount);
        pir_history_notic_tv.setText(format);

        for (int i=0;i<20;i++){
         //   imageList.add("2019-04-"+i+"12:34:23");
        }


     //   history_rv_ff.setSwipeItemClickListener(mItemClickListener); // RecyclerView Item点击监听。
        // 侧滑
        history_rv_ff.setSwipeMenuCreator(swipeMenuCreator);
        history_rv_ff.setSwipeMenuItemClickListener(mMenuItemClickListener);

        pirHistoryAdapter = new PirHistoryAdapter(getActivity(),imageList);
        history_rv_ff.setLayoutManager(new LinearLayoutManager(getActivity()));
        history_rv_ff.setAdapter(pirHistoryAdapter);


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
                if(newimageList.size()>0){
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
        if(str.equals(GeTui.FTP_IMAGE_COPY)  && MyApplication.getInstance().isPreviewActivity()){
            Log.e(GeTui.VideoLog,"===================PirHistoryFragment=============");
            pirHistoryAdapter.notifyDataSetChanged();
        }
    }


    // 点击日期
    @Override
    public void getPirData(int position) {
        super.getPirData(position);
        int selectDay= myDateList.get(position).getDay();
        if(selectDay<10){
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+"0"+myDateList.get(position).getDay();
        }else{
            currentDate = year_tv.getText().toString()+ time_tv.getText().toString()+""+myDateList.get(position).getDay();
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
        String str0=(String) SPUtils2.get(MyApplication.getInstance(),key,"");

        if(!TextUtils.isEmpty(str0)){
            if(newimageList!=null && newimageList.size()>0 && lastDate!=null  && lastDate.equals(currentDate)){
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.pull_refefresh_new_data),Toast.LENGTH_SHORT).show();
            }
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
        }

        // 唤醒FTP
     //   mPresenter.weakUpFTP(gatewayId,deviceId);
        Toast.makeText(getActivity(),currentDate,Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getActivity(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();



            int position = adapterPosition - history_rv_ff.getHeaderItemCount();
            imageList.remove(position);
            pirHistoryAdapter.notifyItemRemoved(position);
            Toast.makeText(getActivity(), "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();

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
