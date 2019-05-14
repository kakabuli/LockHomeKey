package com.kaadas.lock.activity.device.gatewaylock.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockDeletePasswordActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockPasswordAddActivity;
import com.kaadas.lock.adapter.GatewayLockPasswordAdapter;
import com.kaadas.lock.adapter.GatewayPasswordAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockFunctionPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockFunctinView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class GatewayPasswordManagerActivity extends BaseActivity<GatewayLockFunctinView, GatewayLockFunctionPresenter<GatewayLockFunctinView>>
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener,GatewayLockFunctinView {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
 /*   GatewayPasswordAdapter gatewayPasswordAdapter;*/

    @BindView(R.id.ll_add_password)
    LinearLayout llAddPassword;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;
    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    //List<ForeverPassword> pwdList = new ArrayList<>();
    private boolean isSync = false; //是不是正在同步锁中的密码

    private String gatewayId;
    private String deviceId;


    private List<String> mList=new ArrayList<>();

    private GatewayLockPasswordAdapter gatewayLockPasswordAdapter;

    private LoadingDialog loadingDialog;

    private int maxPwdId=0;

    //0表示列表还没有获取完成，无法添加，1表示列表获取异常无法添加，2表示可以进行添加
    private int isAddLockPwd=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_password_manager);
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
        LogUtils.e("onCreate");
    }

    @Override
    protected GatewayLockFunctionPresenter<GatewayLockFunctinView> createPresent() {
        return new GatewayLockFunctionPresenter<>();
    }

    private void initView() {
        tvContent.setText(getString(R.string.password));
        loadingDialog=LoadingDialog.getInstance(this);
        initRecycleview();
    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        llAddPassword.setOnClickListener(this);
    }


    private void initRecycleview() {
        gatewayLockPasswordAdapter = new GatewayLockPasswordAdapter(mList);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(gatewayLockPasswordAdapter);
        gatewayLockPasswordAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("onStart");
        if (mList!=null) {
            String pwdValue=(String) SPUtils2.get(this,KeyConstants.ADD_PWD_ID,"");
            if (!TextUtils.isEmpty(pwdValue)){
                mList.add(pwdValue);
                gatewayLockPasswordAdapter.notifyDataSetChanged();
                passwordPageChange(true);
                SPUtils2.remove(this,KeyConstants.ADD_PWD_ID);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新获取数据

        LogUtils.e("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, GatewayLockDeletePasswordActivity.class);
        intent.putExtra(KeyConstants.LOCK_PWD_NUMBER,mList.get(position));
        if (gatewayId!=null&&deviceId!=null){
            intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
            intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
            startActivityForResult(intent,KeyConstants.DELETE_PWD_REQUEST_CODE);
        }

    }

    public void passwordPageChange(boolean havePwd) {

        if (havePwd) {
            llHasData.setVisibility(View.VISIBLE);
            tvNoUser.setVisibility(View.GONE);
        } else {
           llHasData.setVisibility(View.GONE);
            tvNoUser.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_add_password:
                if (isAddLockPwd==0){
                    ToastUtil.getInstance().showShort(R.string.be_beging_get_lock_pwd_no_add_pwd);
                    return;
                }else if (isAddLockPwd==1){
                    ToastUtil.getInstance().showShort(R.string.get_lock_pwd_throwable);
                    return;
                }else if (isAddLockPwd==2){
                    if (mList!=null&&mList.size()<maxPwdId-1){
                            //减一主要是扣除09
                            intent = new Intent(this, GatewayLockPasswordAddActivity.class);
                            intent.putExtra(KeyConstants.LOCK_PWD_LIST,(Serializable) mList);
                            intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                            intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                            startActivity(intent);
                    }else{
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
        }
    }

    public void initData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        //1获取最大用户密码数量
        loadingDialog.show("正在获取密码列表，请稍后。。。");
        mPresenter.getLockPwdInfo(gatewayId,deviceId);

    }

    @Override
    public void getLockOneSuccess(int pwdId) {
        int pwd=pwdId+1;
        if (maxPwdId>0&&pwd<maxPwdId){
            if (pwd<10){
                mPresenter.getLockPwd(gatewayId,deviceId,"0"+pwd, maxPwdId,pwd);
            }else{
                mPresenter.getLockPwd(gatewayId,deviceId,pwd+"", maxPwdId,pwd);
            }

        }
    }

    @Override
    public void getLockSuccess(Map<String,Integer> map) {
        //获取开锁密码成功
        loadingDialog.dismiss();
        LogUtils.e(map.size()+"map集合大小");
        isAddLockPwd=2;
        if (map.size()>0){
            for (String key : map.keySet()) {
                if (map.get(key)==1&&!key.equals("09")){
                    mList.add(key);
                }
            }
            if (mList.size()>0){
                passwordPageChange(true);
                gatewayLockPasswordAdapter.notifyDataSetChanged();
            }else{
                passwordPageChange(false);
            }

        }else{
            passwordPageChange(false);
        }
    }

    @Override
    public void getLockFail() {
        //获取开锁密码失败
        loadingDialog.dismiss();
        ToastUtil.getInstance().showShort("获取密码列表失败");
        passwordPageChange(false);
        isAddLockPwd=1;

    }

    @Override
    public void getLockThrowable(Throwable throwable) {
        LogUtils.e("获取开锁密码异常   "+throwable.getMessage());
        loadingDialog.dismiss();
        ToastUtil.getInstance().showShort("获取密码列表失败");
        passwordPageChange(false);
        isAddLockPwd=1;
    }

    @Override
    public void getLockInfoSuccess(int pwdNum) {
        //获取到总的次数
        maxPwdId=pwdNum;
        if (maxPwdId>0){
            mPresenter.getLockPwd(gatewayId,deviceId,"0"+0, pwdNum,0);
        }

    }

    @Override
    public void getLockInfoFail() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }

        LogUtils.e("获取到锁信息失败   ");
    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        LogUtils.e("获取到锁信息异常   ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==KeyConstants.DELETE_PWD_REQUEST_CODE){
            if (resultCode== Activity.RESULT_OK){
                String lockNumber=data.getStringExtra(KeyConstants.LOCK_PWD_NUMBER);
                if (lockNumber!=null){
                    for (int i=0;i<mList.size();i++){
                        if (lockNumber.equals(mList.get(i))){
                            mList.remove(i);
                            if (mList.size()==0){
                                gatewayLockPasswordAdapter.notifyDataSetChanged();
                                passwordPageChange(false);
                            }else{
                                gatewayLockPasswordAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }



    }
}
