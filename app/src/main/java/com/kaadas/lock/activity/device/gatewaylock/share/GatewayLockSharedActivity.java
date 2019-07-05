package com.kaadas.lock.activity.device.gatewaylock.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.FamilyMemberDetailActivity;
import com.kaadas.lock.adapter.DeviceShareAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaypresenter.GatewaySharedPresenter;
import com.kaadas.lock.mvp.view.gatewayView.IGatewaySharedView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareUserResultBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class GatewayLockSharedActivity extends BaseActivity<IGatewaySharedView, GatewaySharedPresenter<IGatewaySharedView>> implements IGatewaySharedView,View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    @BindView(R.id.ll_add_user)
    LinearLayout llAddUser;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;

    public static final int REQUEST_CODE = 11111;
    boolean querySuccess = false;

    private  List<DeviceShareUserResultBean.DataBean> shareData=new ArrayList<>();
    private DeviceShareAdapter shareAdapter;
    private String gatewayId;
    private String deviceId;
    private String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_shared_device_management);
        ButterKnife.bind(this);
        initView();
        initData();
        initRecyclerView();
        initListener();
        initRefresh();

    }


    private void initData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        uid=MyApplication.getInstance().getUid();
    }

    private void initRecyclerView() {
        if (shareData!=null){
            recycleview.setLayoutManager(new LinearLayoutManager(this));
            shareAdapter=new DeviceShareAdapter(shareData);
            recycleview.setAdapter(shareAdapter);
        }

    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        llAddUser.setOnClickListener(this);
        if (shareAdapter!=null){
            shareAdapter.setOnItemClickListener(this);
        }
    }

    private void initView() {
        tvContent.setText(getString(R.string.user_manage));
    }
    
    
    

    @Override
    protected GatewaySharedPresenter<IGatewaySharedView> createPresent() {
        return new GatewaySharedPresenter<>();
    }


    private void initRefresh() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //清除数据
                shareData.clear();
                shareAdapter.notifyDataSetChanged();
                //查找分享用户
                if (gatewayId!=null&&deviceId!=null){
                    mPresenter.getShareDeviceUser(gatewayId,deviceId,uid);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gatewayId!=null&&deviceId!=null){
            mPresenter.getShareDeviceUser(gatewayId,deviceId,uid);
        }
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            DeviceShareUserResultBean.DataBean shareResultBean = shareData.get(position);
            Intent intent = new Intent(this, GatewayLockShareUserNumberActivity.class);
            intent.putExtra(KeyConstants.GATEWAY_SHARE_USER, shareResultBean);
            intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
            intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
            startActivity(intent);


    }

    public void pageChange(boolean isNotData) {
        if (isNotData) {
            llHasData.setVisibility(View.GONE);
            tvNoUser.setVisibility(View.VISIBLE);
        } else {
            llHasData.setVisibility(View.VISIBLE);
            tvNoUser.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_add_user:
                if (NetUtil.isNetworkAvailable()) {
                    if (shareData.size() < 10) {
                        intent = new Intent(this, AddGatewayLockShareActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, "", getString(R.string.more_then_ten_member), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });
                    }
                }else {
                    ToastUtil.getInstance().showShort(R.string.query_fail_requery);
                }
                break;

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==REQUEST_CODE){
                String phone = data.getStringExtra(KeyConstants.AUTHORIZATION_TELEPHONE);
                if (gatewayId!=null&&deviceId!=null){
                    GatewayInfo gatewayInfo=MyApplication.getInstance().getGatewayById(gatewayId);
                    if (gatewayInfo!=null) {
                        if (!TextUtils.isEmpty(gatewayInfo.getServerInfo().getMeUsername()) && !TextUtils.isEmpty(gatewayInfo.getServerInfo().getMePwd())) {
                            mPresenter.shareDevice(2, gatewayId, deviceId, uid, phone, "", 1);
                        }else{
                            LogUtils.e("咪咪网为空需要重新注册");
                            String deviceSN = gatewayInfo.getServerInfo().getDeviceSN();
                            mPresenter.bindMimi(deviceSN, deviceSN);
                            ToastUtil.getInstance().showShort(getString(R.string.add_common_user_fail));
                        }
                    }

                }
            }
        }

    }


    @Override
    public void shareDeviceSuccess(DeviceShareResultBean resultBean) {
        if (gatewayId!=null&&deviceId!=null&&uid!=null){
            mPresenter.getShareDeviceUser(gatewayId,deviceId,uid);
        }
        ToastUtil.getInstance().showShort(R.string.add_common_user_success);
    }

    @Override
    public void shareDeviceFail() {
        ToastUtil.getInstance().showShort(R.string.add_common_user_fail);
    }

    @Override
    public void shareDeviceThrowable() {
        ToastUtil.getInstance().showShort(R.string.add_common_user_fail);
    }

    @Override
    public void shareUserListSuccess(List<DeviceShareUserResultBean.DataBean> shareUserBeans) {
        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
        }
        if (shareUserBeans!=null&&shareUserBeans.size()>0){
            shareData.clear();
            shareData.addAll(shareUserBeans);
            pageChange(false);
        }else{
            pageChange(true);
        }
        if (shareAdapter!=null){
            shareAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void shareUserListFail() {
        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
        }
        pageChange(true);
        ToastUtil.getInstance().showShort(R.string.get_share_user_fail);
    }

    @Override
    public void shareUserListThrowable() {
        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
        }
        pageChange(true);
        ToastUtil.getInstance().showShort(R.string.get_share_user_fail);
    }

    @Override
    public void bindMimiSuccess(String deviceSN) {
        LogUtils.e("绑定咪咪网成功");
    }

    @Override
    public void bindMimiFail(String code, String msg) {
        LogUtils.e("绑定咪咪网失败");
    }

    @Override
    public void bindMimiThrowable(Throwable throwable) {
        LogUtils.e("绑定咪咪网失败");
    }
}
