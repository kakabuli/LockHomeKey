package com.kaadas.lock.activity.device.gatewaylock.stress.old;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.stress.GatewayStressPasswordAddActivity;
import com.kaadas.lock.activity.device.gatewaylock.stress.GatewayStressWarnDetailActivity;
import com.kaadas.lock.adapter.GatewayLockStressPasswordAdapter;
import com.kaadas.lock.adapter.GatewayStressPasswordAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockStressDetailPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockStressDetailView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David
 */
public class GatewayLockStressDetailActivity extends BaseActivity<IGatewayLockStressDetailView,GatewayLockStressDetailPresenter<IGatewayLockStressDetailView>> implements  BaseQuickAdapter.OnItemClickListener,IGatewayLockStressDetailView {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.ll_add_password)
    LinearLayout llAddPassword;
    @BindView(R.id.recycleview_password)
    RecyclerView recycleviewPassword;
    @BindView(R.id.iv_app_notification)
    ImageView ivAppNotification;
    @BindView(R.id.rl_app_notification)
    RelativeLayout rlAppNotification;

    List<String> pwdList = new ArrayList<>();
    boolean appNotificationStatus = true;
    GatewayLockStressPasswordAdapter gatewayLockStressPasswordAdapter;
    
    private LoadingDialog loadingDialog;
    private String gatewayId;
    private String deviceId;
    //0表示列表还没有获取完成，无法添加，1表示列表获取异常无法添加，2表示可以进行添加
    private int isAddLockPwd=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_stress_password_manager);
        ButterKnife.bind(this);
        initView();
        initRecycleview();
        initData();
        
    }

    private void initView() {
        if (loadingDialog==null) {
            loadingDialog = LoadingDialog.getInstance(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (pwdList!=null) {
            String pwdId=(String) SPUtils2.get(this,KeyConstants.ADD_STRESS_PWD_ID,"");
            if (!TextUtils.isEmpty(pwdId)){
                pwdList.add(pwdId);
                gatewayLockStressPasswordAdapter.notifyDataSetChanged();
                SPUtils2.remove(this,KeyConstants.ADD_STRESS_PWD_ID);
            }
        }
    }

    @Override
    protected GatewayLockStressDetailPresenter<IGatewayLockStressDetailView> createPresent() {
        return new GatewayLockStressDetailPresenter<>();
    }

    private void initData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);

        appNotificationStatus = (boolean) SPUtils.get(KeyConstants.APP_NOTIFICATION_STATUS, true);
        if (appNotificationStatus) {
            ivAppNotification.setImageResource(R.mipmap.iv_open);
        } else {
            ivAppNotification.setImageResource(R.mipmap.iv_close);
        }
        if (gatewayId!=null&&deviceId!=null){
            //胁迫密码固定09编号
            mPresenter.getLockPwd(gatewayId,deviceId,"09");
            loadingDialog.show(getString(R.string.get_stress_pwd_stop));
        }

    }

    private void initRecycleview() {
        gatewayLockStressPasswordAdapter = new GatewayLockStressPasswordAdapter(pwdList, R.layout.item_gateway_stress_password);
        recycleviewPassword.setLayoutManager(new LinearLayoutManager(this));
        recycleviewPassword.setAdapter(gatewayLockStressPasswordAdapter);
        gatewayLockStressPasswordAdapter.setOnItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("密码管理界面  onResume()   ");


    }


    @OnClick({R.id.back, R.id.ll_add_password,R.id.rl_app_notification})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.ll_add_password:
                if (isAddLockPwd==0){
                    ToastUtil.getInstance().showShort(R.string.be_beging_get_lock_pwd_no_add_pwd);
                    return;
                }else if (isAddLockPwd==1) {
                    ToastUtil.getInstance().showShort(R.string.get_lock_pwd_throwable);
                    return;
                }else if (isAddLockPwd==2) {
                    if (pwdList.size() < 1) {
                        intent = new Intent(this, GatewayLockStressAddActivity.class);
                        intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                        intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                        startActivity(intent);
                    } else {
                        AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, getString(R.string.password_full_and_delete_exist_code), getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });

                    }
                }
                break;
            case R.id.rl_app_notification:

                if (appNotificationStatus) {
                    //打开状态 现在关闭
                    ivAppNotification.setImageResource(R.mipmap.iv_close);
                    SPUtils.put(KeyConstants.APP_NOTIFICATION_STATUS, false);
                } else {
                    //关闭状态 现在打开
                    ivAppNotification.setImageResource(R.mipmap.iv_open);
                    SPUtils.put(KeyConstants.APP_NOTIFICATION_STATUS, true);
                }
                appNotificationStatus = !appNotificationStatus;
                break;
        }
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, GatewayLockStressDeleteActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
        intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
        intent.putExtra(KeyConstants.LOCK_PWD_NUMBER,"09");
        startActivityForResult(intent,KeyConstants.DELETE_PWD_REQUEST_CODE);
    }

    @Override
    public void getStressPwdSuccess(int status) {
        //获取成功
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        isAddLockPwd=2;
        if (status==1){
            pwdList.add("09");
            gatewayLockStressPasswordAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void getStressPwdFail() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        isAddLockPwd=1;
        //获取失败
        ToastUtil.getInstance().showShort(R.string.get_stress_list_fail);

    }

    @Override
    public void getStreessPwdThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        isAddLockPwd=1;
        //获取异常
        ToastUtil.getInstance().showShort(R.string.get_stress_list_fail);
        LogUtils.e("获取胁迫密码异常   "+throwable.getMessage());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==KeyConstants.DELETE_PWD_REQUEST_CODE){
            if (resultCode== Activity.RESULT_OK){
                String lockNumber=data.getStringExtra(KeyConstants.LOCK_PWD_NUMBER);
                if (!TextUtils.isEmpty(lockNumber)){
                    pwdList.remove(lockNumber);
                    gatewayLockStressPasswordAdapter.notifyDataSetChanged();
                }
            }
        }



    }

}
