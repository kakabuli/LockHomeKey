package com.kaadas.lock.fragment.record;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoAlbumDetailActivity;
import com.kaadas.lock.adapter.WifiLockRecordIAdapter;
import com.kaadas.lock.adapter.WifiLockVistorIAdapter;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockOpenRecordPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVistorRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockOpenRecordView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVistorRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
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

/**
 * Created by David on 2019/4/22
 */
public class WifiLockVistorRecordFragment extends BaseFragment<IWifiLockVistorRecordView, WifiLockVistorRecordPresenter<IWifiLockVistorRecordView>>
        implements IWifiLockVistorRecordView {
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    List<WifiVideoLockAlarmRecord> records = new ArrayList<>();
    WifiLockVistorIAdapter operationGroupRecordAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    private ProgressDialog progressDialog;//创建ProgressDialog

    private int currentPage = 1;   //当前的开锁记录时间
    View view;
    private Unbinder unbinder;
    private String wifiSn;

    private boolean isP2PConnect = false;
    private WifiLockInfo wifiLockInfoBySn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_bluetooth_open_lock_record, null);
        unbinder = ButterKnife.bind(this, view);
        wifiSn = getArguments().getString(KeyConstants.WIFI_SN);
        isP2PConnect = getArguments().getBoolean(KeyConstants.WIFI_VIDEO_LOCK_XM_CONNECT);
        wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        rlHead.setVisibility(View.GONE);

        initRecycleView();
        initRefresh();
        initData();
        createProgress();
        mPresenter.settingDevice(wifiLockInfoBySn);
        if(!isP2PConnect){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.connectP2P();
                }
            }).start();
        }
        return view;
    }

    private void initData() {
        String localRecord = (String) SPUtils.get(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSn, "");
        Gson gson = new Gson();
        List<WifiVideoLockAlarmRecord> records = gson.fromJson(localRecord, new TypeToken<List<WifiVideoLockAlarmRecord>>() {
        }.getType());
        groupData(records);
        operationGroupRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getWifiVideoLockGetAlarmList(1, wifiSn);
    }


    @Override
    protected WifiLockVistorRecordPresenter<IWifiLockVistorRecordView> createPresent() {
        return new WifiLockVistorRecordPresenter<>();
    }

    private void initRecycleView() {
        operationGroupRecordAdapter = new WifiLockVistorIAdapter(records, new WifiLockVistorIAdapter.VideoRecordCallBackLinstener() {
            @Override
            public void onVideoRecordCallBackLinstener(WifiVideoLockAlarmRecord record) {
                LogUtils.e("shulan onVideoRecordCallBackLinstener");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        int ret = mPresenter.searchRecordFileList(record.getFileDate());
//                        LogUtils.e("shulan searchRecordFileList-- > " + ret);
                        String path = FileTool.getVideoLockPath(getActivity(),record.getWifiSN()).getPath();
                        String fileName = path +  File.separator + record.get_id() + ".mp4";
                        if (new File(fileName).exists()){
                            Intent intent = new Intent(getActivity(), WifiLockVideoAlbumDetailActivity.class);
                            intent.putExtra(KeyConstants.VIDEO_PIC_PATH,fileName);
                            LogUtils.e("shulan createTime-->" + fileName);
                            fileName = DateUtils.getStrFromMillisecond2(record.getStartTime());
                            LogUtils.e("shulan filename-->" + fileName);
                            intent.putExtra("NAME",fileName);
                            startActivity(intent);
                        }else{
                            LogUtils.e("shulan 时间戳开始-->" + System.currentTimeMillis());
                            mPresenter.playDeviceRecordVideo(record,path);
                        }

                    }
                }).start();
            }
        });
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(operationGroupRecordAdapter);
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
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
        unbinder.unbind();
    }


    Handler handler = new Handler();

    @Override
    public void onLoadServerRecord(List<WifiVideoLockAlarmRecord> lockRecords, int page) {
        LogUtils.e("收到服务器数据  " + lockRecords.size());
        LogUtils.e("shulan page-->" + page);
        if (page == 1) {
            records.clear();
        }
        int size = records.size();
        currentPage = page + 1;
        groupData(lockRecords);

        if (size>0){
            operationGroupRecordAdapter.notifyItemRangeInserted(size,lockRecords.size());
        }else {
            operationGroupRecordAdapter.notifyDataSetChanged();
        }

        if (page == 1) { //这时候是刷新load
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
                if (records.size() > 0) {
                    lastRecord = records.get(records.size() - 1);
                    lastTimeHead = lastRecord.getDayTime();
                }
                WifiVideoLockAlarmRecord record = lockRecords.get(i);
                long openTime = Long.parseLong(record.getTime());
                String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime * 1000);
                String timeHead = sOpenTime.substring(0, 10);
                record.setDayTime(timeHead);
                if (!timeHead.equals(lastTimeHead)) { //添加头
                    record.setFirst(true);
                    if (lastRecord != null) {
                        lastRecord.setLast(true);
                    }
                }
                for(int j = 0 ;j < records.size();j++){
                    if(record.get_id() != records.get(j).get_id()){
                        records.add(record);
                    }
                }
            }
        }
    }

    @Override
    public void onLoadServerRecordFailed(Throwable throwable) {
        //加载服务器开锁记录失败
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
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
    public void onStopRecordMP4CallBack(MP4Info mp4Info,String fileName) {
        if(mp4Info.isResult()){
            LogUtils.e("shulan progressDialog11-->" +progressDialog);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog!=null){
                        LogUtils.e("shulan progressDialog11-->" +progressDialog);
                        progressDialog.dismiss();
                    }
                }
            });
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mp4Info.getFilePath()))));
            Intent intent = new Intent(getActivity(), WifiLockVideoAlbumDetailActivity.class);
            intent.putExtra(KeyConstants.VIDEO_PIC_PATH,mp4Info.getFilePath());
            LogUtils.e("shulan createTime-->" + fileName);
            fileName = DateUtils.getStrFromMillisecond2(Long.parseLong(fileName));
            LogUtils.e("shulan filename-->" + fileName);
            intent.putExtra("NAME",fileName);
            startActivity(intent);

        }
    }

    @Override
    public void onstartRecordMP4CallBack() {

    }

    @Override
    public void onStartProgress(long time) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null){
                    if (!progressDialog.isShowing()){
                        progressDialog.show();
                    }
                }
            }
        });
        if(time != 0)
            progressDialog.incrementProgressBy((int) time);
    }

    @Override
    public void onSuccessRecord(boolean flag) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!getActivity().isFinishing() && progressDialog != null){
                    if(progressDialog!=null){
                        progressDialog.dismiss();
                        LogUtils.e("shulan progressDialog2-->" +progressDialog);
                    }

                }
                if(!flag){
                    ToastUtil.getInstance().showLong("下载失败!");
                }
            }
        });
    }


    public void createProgress(){
        if (null == progressDialog) {
//            synchronized (ProgressDialog.class) {
//                if (null == progressDialog) {
                    progressDialog = new ProgressDialog(getActivity());
//                }
//            }
        }
        progressDialog.setMessage("正在下载...");
        progressDialog.setCancelable(false);//按空白地方 进度条窗口不会消失
        progressDialog.setMax(9000);
        progressDialog.incrementProgressBy(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
}
