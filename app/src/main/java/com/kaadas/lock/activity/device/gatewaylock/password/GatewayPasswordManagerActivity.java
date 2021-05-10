package com.kaadas.lock.activity.device.gatewaylock.password;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.GatewayLockPasswordAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordManagerPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordManagerView;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.greenDao.manager.GatewayLockPasswordManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class GatewayPasswordManagerActivity extends BaseActivity<IGatewayLockPasswordManagerView, GatewayLockPasswordManagerPresenter<IGatewayLockPasswordManagerView>>
        implements IGatewayLockPasswordManagerView, BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
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
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    //List<ForeverPassword> pwdList = new ArrayList<>();
    private boolean isSync = false; //是不是正在同步锁中的密码

    private String gatewayId;
    private String deviceId;
    private GwLockInfo lockInfo;

    private List<GatewayPasswordPlanBean> mList = new ArrayList<>();

    private GatewayLockPasswordAdapter gatewayLockPasswordAdapter;

    private LoadingDialog loadingDialog;


    private String userId;

    private GatewayLockPasswordManager daoManager = new GatewayLockPasswordManager();
   String gatewayModel=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_password_manager);
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
    }

    @Override
    protected GatewayLockPasswordManagerPresenter<IGatewayLockPasswordManagerView> createPresent() {
        return new GatewayLockPasswordManagerPresenter<>();
    }

    private void initView() {
        tvContent.setText(getString(R.string.password));
        loadingDialog = LoadingDialog.getInstance(this);
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
        tvSynchronizedRecord.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            if(!deviceId.isEmpty() && !gatewayId.isEmpty()){

                //从数据库获取数据
                List<GatewayPasswordPlanBean> gatewayPasswordPlanBeans = daoManager.queryAll(deviceId, MyApplication.getInstance().getUid(), gatewayId);
                mList.clear();

                List<GatewayPasswordPlanBean> temp =new ArrayList<>();
                temp.addAll(gatewayPasswordPlanBeans);

                for (int i=0;i<temp.size();i++){
                    if(temp.get(i).getPasswordNumber()==9){
                        GatewayPasswordPlanBean gatewayPasswordPlanBean= temp.get(i);
                        gatewayPasswordPlanBeans.remove(gatewayPasswordPlanBean);
                    }
                }


                mList.addAll(gatewayPasswordPlanBeans);
                if (gatewayPasswordPlanBeans.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mList.sort(Comparator.naturalOrder());
                    }
                    gatewayLockPasswordAdapter.notifyDataSetChanged();
                    passwordPageChange(true);
                } else {
                    passwordPageChange(false);
                }
            }
        }catch (Exception e){

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, GatewayLockDeletePasswordActivity.class);
        if (gatewayId != null && deviceId != null) {
            intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
            intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
            GatewayPasswordPlanBean item = (GatewayPasswordPlanBean) adapter.getItem(position);
            intent.putExtra(KeyConstants.GATEWAY_PASSWORD_BEAN, item);
            startActivityForResult(intent, KeyConstants.DELETE_PWD_REQUEST_CODE);
        }
    }

    public void passwordPageChange(boolean havePwd) {
        if (havePwd) {
            if (llHasData != null && tvNoUser != null) {
                llHasData.setVisibility(View.VISIBLE);
                tvNoUser.setVisibility(View.GONE);
            }
        } else {
            if (llHasData != null && tvNoUser != null) {
                llHasData.setVisibility(View.GONE);
                tvNoUser.setVisibility(View.VISIBLE);
            }
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
                //新版锁
                String lockversion = lockInfo.getServerInfo().getLockversion();
                LogUtils.e("获取到 版本信息是   " + lockversion);
                if (!TextUtils.isEmpty(lockversion)) {
                    String lockModel = lockversion.split(";")[0];
                    if (!TextUtils.isEmpty(lockModel) &&( lockModel.equalsIgnoreCase("8100Z") ||  lockModel.equalsIgnoreCase("8100A"))) {
                        intent = new Intent(this, GatewayPasswordAddActivity.class);
                        intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                        intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                        if(!TextUtils.isEmpty(gatewayModel)  && gatewayModel.equals(KeyConstants.SMALL_GW2)){
                            intent.putExtra(KeyConstants.GATEWAY_MODEL,KeyConstants.SMALL_GW2);
                        }
                        startActivity(intent);
                        return;
                    }
                }
                intent = new Intent(this, GatewayLockTempararyPwdAddActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                if(!TextUtils.isEmpty(gatewayModel)  && gatewayModel.equals(KeyConstants.SMALL_GW2)){
                    intent.putExtra(KeyConstants.GATEWAY_MODEL,KeyConstants.SMALL_GW2);
                }

//                intent = new Intent(this, PasswordTestActivity.class);
//                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
//                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.tv_synchronized_record:
                  loadingDialog.show2(getString(R.string.get_gateway_lock_pwd_waiting));
                if(!TextUtils.isEmpty(gatewayModel)  &&  gatewayModel.equals(KeyConstants.SMALL_GW2)){
                   mPresenter.sysPassworByhttp(MyApplication.getInstance().getUid(),gatewayId,deviceId,"",null);
                }else{
//                    //点击同步
                     mPresenter.syncPassword(gatewayId, deviceId);
                }

                break;
        }
    }

    public void initData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID) + "";
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID) + "";
        lockInfo = (GwLockInfo) intent.getSerializableExtra(KeyConstants.GATEWAY_LOCK_INFO);
        userId = MyApplication.getInstance().getUid();
        gatewayModel =getIntent().getStringExtra(KeyConstants.GATEWAY_MODEL);
        //第一次进入该页面,由于锁上重置，删除，添加无法知道当前锁的信息所以只有第一次进入需要判断
//        int firstIn = (int) SPUtils.get(KeyConstants.FIRST_IN_GATEWAY_LOCK + userId + deviceId, 0);
//        LogUtils.e("是否是第一次进入   " + firstIn);
//        if (firstIn == 0) { //默认是第一次进来
//            mPresenter.syncPassword(gatewayId, deviceId);
//            loadingDialog.show2(getString(R.string.get_gateway_lock_pwd_waiting));
//        } else {
        // TODO: 2019/11/26    获取缓存的数据
//        List<GatewayPasswordPlanBean> gatewayPasswordPlanBeans = daoManager.queryAll(deviceId, MyApplication.getInstance().getUid(), gatewayId);
//        mList.clear();
//        mList.addAll(gatewayPasswordPlanBeans);
//        if (gatewayPasswordPlanBeans.size() > 0) {
//            gatewayLockPasswordAdapter.notifyDataSetChanged();
//            passwordPageChange(true);
//        } else {
//            passwordPageChange(false);
//        }
//        }
    }



    @Override
    public void getLockInfoSuccess(int pwdNum) {
        //获取到总的次数
    }

    @Override
    public void getLockInfoFail() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        //passwordPageChange(false);
        ToastUtils.showShort(getString(R.string.get_lock_info_fail));
        LogUtils.e("获取到锁信息失败   ");
    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        //passwordPageChange(false);
        ToastUtils.showShort(getString(R.string.get_lock_info_fail));
        LogUtils.e("获取到锁信息异常   " + throwable.getMessage());
    }

    @Override
    public void syncPasswordComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if(passwordPlanBeans==null){
            ToastUtils.showLong(getString(R.string.pwd_list_null));
            return;
        }
        SPUtils.put(KeyConstants.FIRST_IN_GATEWAY_LOCK + userId + deviceId, 1);
        List<GatewayPasswordPlanBean> gatewayPasswordPlanBeans = parsePassword(passwordPlanBeans);
        LogUtils.e("获取全部密码  1  " + Arrays.toString(gatewayPasswordPlanBeans.toArray()));
        daoManager.insertAfterDelete(deviceId, MyApplication.getInstance().getUid(), gatewayId, gatewayPasswordPlanBeans);
        mList.clear();
        mList.addAll(gatewayPasswordPlanBeans);
        if (mList.size() > 0) {
            passwordPageChange(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mList.sort(Comparator.naturalOrder());
            }
            gatewayLockPasswordAdapter.notifyDataSetChanged();
        } else {
            passwordPageChange(false);
        }
    }


    @Override
    public void syncPasswordFailed(Throwable throwable) {
        LogUtils.e("获取开锁密码异常   " + throwable.getMessage());
        loadingDialog.dismiss();
        ToastUtils.showShort(R.string.get_lock_pwd_list_fail);
        passwordPageChange(false);
    }

    @Override
    public void addLockPwdFail(Throwable throwable) {

    }

    @Override
    public void addLockPwdSuccess(GatewayPasswordPlanBean gatewayPasswordPlanBean, String pwdValue) {

    }

    @Override
    public void setUserTypeSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean) {

    }

    @Override
    public void onLoadPasswordPlan(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {
        LogUtils.e("");
        List<GatewayPasswordPlanBean> gatewayPasswordPlanBeans = parsePassword(passwordPlanBeans);
        LogUtils.e("获取全部密码   2 " + Arrays.toString(gatewayPasswordPlanBeans.toArray()));
        daoManager.insertAfterDelete(deviceId, MyApplication.getInstance().getUid(), gatewayId, gatewayPasswordPlanBeans);
        mList.clear();
        mList.addAll(gatewayPasswordPlanBeans);
        passwordPageChange(true);
    }

    @Override
    public void onLoadPasswordPlanFailed(Throwable throwable) {
        hiddenLoading();
        ToastUtils.showLong(R.string.synv_failed);

    }

    @Override
    public void onLoadPasswordPlanComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {
        List<GatewayPasswordPlanBean> gatewayPasswordPlanBeans = parsePassword(passwordPlanBeans);
        LogUtils.e("获取全部密码   2 " + Arrays.toString(gatewayPasswordPlanBeans.toArray()));
        daoManager.insertAfterDelete(deviceId, MyApplication.getInstance().getUid(), gatewayId, gatewayPasswordPlanBeans);
        mList.clear();
        mList.addAll(gatewayPasswordPlanBeans);
        if (mList.size() > 0) {
            passwordPageChange(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mList.sort(Comparator.naturalOrder());
            }
            gatewayLockPasswordAdapter.notifyDataSetChanged();
        } else {
            passwordPageChange(false);
        }
        hiddenLoading();
    }


    @Override
    public void setUserTypeFailed(Throwable typeFailed) {

    }

    @Override
    public void setPlanSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean) {

    }

    @Override
    public void setPlanFailed(Throwable throwable) {

    }

    @Override
    public void deletePasswordSuccess() {

    }

    @Override
    public void deletePasswordFailed(Throwable throwable) {

    }

    @Override
    public void gatewayPasswordFull() {

    }

    public List<GatewayPasswordPlanBean> parsePassword(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {
        List<GatewayPasswordPlanBean> passwords = new ArrayList<>();
        for (Integer number : passwordPlanBeans.keySet()) {
            passwords.add(passwordPlanBeans.get(number));
        }
        return passwords;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void isSyncPlan() {
        showLoading(getString(R.string.is_sync_password_plan));
    }


}
