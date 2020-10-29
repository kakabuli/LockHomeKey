package com.kaadas.lock.fragment.record;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoAlbumDetailActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoCallingActivity;
import com.kaadas.lock.adapter.WifiLockAlarmGroupRecordAdapter;
import com.kaadas.lock.adapter.WifiVideoLockAlarmIAdapter;
import com.kaadas.lock.bean.WifiLockAlarmRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockAlarmRecordPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiVideoLockAlarmRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAlarmRecordView;
import com.kaadas.lock.mvp.view.wifilock.IWifiVideoLockAlarmRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.AVLoadingIndicatorView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xmitech.sdk.MP4Info;
import com.yun.software.kaadas.Utils.FileTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class WifiVideoLockAlarmRecordFragment extends BaseFragment<IWifiVideoLockAlarmRecordView, WifiVideoLockAlarmRecordPresenter<IWifiVideoLockAlarmRecordView>>
        implements IWifiVideoLockAlarmRecordView {

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    WifiVideoLockAlarmIAdapter wifiLockAlarmGroupRecordAdapter;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    private int currentPage = 1;   //当前的开锁记录时间
    View view;
    private Unbinder unbinder;
    private String wifiSn;
    List<WifiVideoLockAlarmRecord> list = new ArrayList<>();
    private boolean isP2PConnect = false;
    private WifiLockInfo wifiLockInfoBySn;
//    private ProgressDialog progressDialog;//创建ProgressDialog


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_bluetooth_open_lock_record, null);
        unbinder = ButterKnife.bind(this, view);
        isP2PConnect = getArguments().getBoolean(KeyConstants.WIFI_VIDEO_LOCK_XM_CONNECT);
        wifiSn = getArguments().getString(KeyConstants.WIFI_SN);
        wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        initRecycleView();
        initRefresh();
        initData();
//        createProgress();
        rlHead.setVisibility(View.GONE);
        mPresenter.settingDevice(wifiLockInfoBySn);
        /*if(!isP2PConnect){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.connectP2P();
                }
            }).start();
        }*/
        return view;
    }

    private void initData() {
        String alarmCache = (String) SPUtils.get(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD + wifiSn, "");
        Gson gson = new Gson();
        List<WifiVideoLockAlarmRecord> records = gson.fromJson(alarmCache, new TypeToken<List<WifiVideoLockAlarmRecord>>() {
        }.getType());
        groupData(records);
        wifiLockAlarmGroupRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getWifiVideoLockGetAlarmList(1, wifiSn);
    }

    private void initRecycleView() {
        wifiLockAlarmGroupRecordAdapter = new WifiVideoLockAlarmIAdapter(list, new WifiVideoLockAlarmIAdapter.VideoRecordCallBackLinstener() {
            @Override
            public void onVideoRecordCallBackLinstener(WifiVideoLockAlarmRecord record) {
                if(wifiLockInfoBySn.getPowerSave() == 1){
                    powerStatusDialog();
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String path = FileTool.getVideoCacheFolder(getActivity(),record.getWifiSN()).getPath();
                        String fileName = path +  File.separator + record.get_id() + ".mp4";
                        if (new File(fileName).exists()){
                            Intent intent = new Intent(getActivity(), WifiLockVideoAlbumDetailActivity.class);
                            intent.putExtra(KeyConstants.VIDEO_PIC_PATH,fileName);

                            fileName = DateUtils.getStrFromMillisecond2(record.getStartTime());

                            intent.putExtra("NAME",fileName);
                            startActivity(intent);
                        }else{
                           /* getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(progressDialog != null){
                                        if (!progressDialog.isShowing()){
                                            progressDialog.show();
                                        }
                                    }
                                }
                            });*/
//                            mPresenter.connectPlayDeviceRecordVideo(record,path);
                            Intent intent = new Intent(getActivity(), WifiLockVideoAlbumDetailActivity.class);
                            intent.putExtra(KeyConstants.VIDEO_PIC_PATH,fileName);
                            fileName = DateUtils.getStrFromMillisecond2(record.getStartTime());
                            intent.putExtra("NAME",fileName);
                            intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
                            intent.putExtra("record",record);
                            startActivity(intent);
                        }
                    }
                });

            }
        });
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(wifiLockAlarmGroupRecordAdapter);


    }


    private void initRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.setEnableLoadMore(true);
                mPresenter.getWifiVideoLockGetAlarmList(1, wifiSn);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                mPresenter.getWifiVideoLockGetAlarmList(currentPage, wifiSn);
            }
        });
    }

    @Override
    protected WifiVideoLockAlarmRecordPresenter<IWifiVideoLockAlarmRecordView> createPresent() {
        return new WifiVideoLockAlarmRecordPresenter<>();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
        unbinder.unbind();
    }


    @Override
    public void onLoadServerRecord(List<WifiVideoLockAlarmRecord> alarmRecords, int page) {

        if (page == 1) {
            list.clear();
        }
        int size = list.size();
        currentPage = page + 1;
        groupData(alarmRecords);

        if (size>0){
            wifiLockAlarmGroupRecordAdapter.notifyItemRangeInserted(size,alarmRecords.size());
        }else {
            wifiLockAlarmGroupRecordAdapter.notifyDataSetChanged();
        }

        if (page == 1) { //这时候是刷新
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }

    }

    private void groupData(List<WifiVideoLockAlarmRecord> lockRecords) {
        String lastTimeHead = "";
        WifiVideoLockAlarmRecord lastRecord = null;
        if (lockRecords != null && lockRecords.size() > 0) {
            for (int i = 0; i < lockRecords.size(); i++) {
                if (list.size() > 0) {
                    lastRecord = list.get(list.size() - 1);
                    lastTimeHead = lastRecord.getDayTime();
                }
                WifiVideoLockAlarmRecord record = lockRecords.get(i);
                long openTime = 0;
                try{
                    openTime = Long.parseLong(record.getTime());
                }catch (Exception re){
                    re.printStackTrace();
                }

                String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime * 1000);
                String timeHead = sOpenTime.substring(0, 10);
                record.setDayTime(timeHead);
                if (!timeHead.equals(lastTimeHead)) { //添加头
                    record.setFirst(true);
                    if (lastRecord != null) {
                        lastRecord.setLast(true);
                    }
                }
                list.add(record);

            }
            removeGroupData();
        }
    }

    private void removeGroupData() {
//        List<WifiVideoLockAlarmRecord> data = new ArrayList<>();
//        data.addAll(list);
        for(int i = 0 ; i < list.size();i++){
//            String id = list.get(i).get_id();
            for(int j = list.size() - 1 ; j > i; j--){
                if(list.get(i).get_id() == list.get(j).get_id()){
                    list.remove(j);
                }
            }
        }
    }


    @Override
    public void onLoadServerRecordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
        //加载服务器开锁记录失败
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(getActivity(), result.getCode()));
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onServerNoData() {
        //服务器没有开锁记录
        refreshLayout.finishRefresh();
        refreshLayout.setEnableLoadMore(false);  //服务器没有数据时，不让上拉加载更多
        ToastUtil.getInstance().showShort(R.string.server_no_data_2);
    }

    @Override
    public void noMoreData() {
        ToastUtil.getInstance().showShort(R.string.no_more_data);
        refreshLayout.finishLoadMore();
        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void onStartProgress(long time) {

//        progressDialog.incrementProgressBy((int) time);
    }

    @Override
    public void onSuccessRecord(boolean flag) {
        /*mPresenter.release();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!getActivity().isFinishing() && progressDialog != null){
                    if(progressDialog!=null){
                        progressDialog.dismiss();

                    }

                }
                if(!flag){
                    ToastUtil.getInstance().showShort("下载失败!");
                }
            }
        });*/
    }

    @Override
    public void onConnectFailed(int paramInt) {
        if(paramInt < 0){
            isP2PConnect = false;
        }
    }

    @Override
    public void onConnectSuccess() {
        isP2PConnect = true;
    }

    @Override
    public void onStartConnect(String paramString) {

    }

    @Override
    public void onErrorMessage(String message) {

    }

    @Override
    public void onStopRecordMP4CallBack(MP4Info mp4Info,String fileName) {
        if(mp4Info.isResult()){
            if(mp4Info.isResult()){
                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog!=null){

                            progressDialog.dismiss();
                        }
                    }
                });*/
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mp4Info.getFilePath()))));
//                    ToastUtil.getInstance()
                Intent intent = new Intent(getActivity(), WifiLockVideoAlbumDetailActivity.class);
                intent.putExtra(KeyConstants.VIDEO_PIC_PATH,mp4Info.getFilePath());

                fileName = DateUtils.getStrFromMillisecond2(Long.parseLong(fileName));

                intent.putExtra("NAME",fileName);
                startActivity(intent);

            }
        }
    }

    @Override
    public void onstartRecordMP4CallBack() {

    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(getActivity(), "设置失败", "\n已开启省电模式，需唤醒门锁后再试\n",
                "确定", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    /*public void createProgress(){
        if (null == progressDialog) {
            synchronized (ProgressDialog.class) {
                if (null == progressDialog) {
                    progressDialog = new ProgressDialog(getActivity());
                }
            }
        }
        progressDialog.setMessage("正在下载...");
        progressDialog.setCancelable(false);//按空白地方 进度条窗口不会消失
        progressDialog.setMax(9000);
        progressDialog.incrementProgressBy(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }*/

}
