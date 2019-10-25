package com.kaadas.lock.activity.device.gatewaylock.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
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
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockDeletePasswordActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockPasswordAddActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockTempararyPwdAddActivity;
import com.kaadas.lock.adapter.GatewayLockPasswordAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockFunctionPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockFunctinView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;

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
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, GatewayLockFunctinView {
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


    private List<String> mList = new ArrayList<>();

    private GatewayLockPasswordAdapter gatewayLockPasswordAdapter;

    private LoadingDialog loadingDialog;

    private int maxPwdId = 0;

    //0表示列表还没有获取完成，无法添加，1表示列表获取异常无法添加，2表示可以进行添加
    private int isAddLockPwd = 0;
    private String userId;


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
    protected GatewayLockFunctionPresenter<GatewayLockFunctinView> createPresent() {
        return new GatewayLockFunctionPresenter<>();
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
        if (mList != null) {
            String pwdValue = (String) SPUtils2.get(this, KeyConstants.ADD_PWD_ID, "");
            if (!TextUtils.isEmpty(pwdValue)) {
                if (!mList.contains(pwdValue)){
                    mList.add(pwdValue);
                    gatewayLockPasswordAdapter.notifyDataSetChanged();
                    passwordPageChange(true);
                    SPUtils2.remove(this, KeyConstants.ADD_PWD_ID);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新获取数据

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
        intent.putExtra(KeyConstants.LOCK_PWD_NUMBER, mList.get(position));
        if (gatewayId != null && deviceId != null) {
            intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
            intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
            startActivityForResult(intent, KeyConstants.DELETE_PWD_REQUEST_CODE);
        }

    }

    public void passwordPageChange(boolean havePwd) {

        if (havePwd) {
            if (llHasData!=null&&tvNoUser!=null){
                llHasData.setVisibility(View.VISIBLE);
                tvNoUser.setVisibility(View.GONE);
            }
        } else {
            if (llHasData!=null&&tvNoUser!=null){
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
                if (isAddLockPwd == 0) {
                    ToastUtil.getInstance().showShort(R.string.be_beging_get_lock_pwd_no_add_pwd);
                    return;
                } else if (isAddLockPwd == 1) {
                    ToastUtil.getInstance().showShort(R.string.get_lock_pwd_throwable);
                    return;
                } else if (isAddLockPwd == 2) {
                    //减一主要是扣除09
                    if (mList != null && mList.size() < maxPwdId - 1) {
                        List<String> temparayList=new ArrayList<>();
                        temparayList.add("05");
                        temparayList.add("06");
                        temparayList.add("07");
                        temparayList.add("08");
                        if (mList.containsAll(temparayList)){
                            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, getString(R.string.password_full_and_delete_exist_code), getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                                @Override
                                public void left() {

                                }

                                @Override
                                public void right() {

                                }
                            });
                        }else{
                            intent = new Intent(this, GatewayLockTempararyPwdAddActivity.class);
                            intent.putExtra(KeyConstants.LOCK_PWD_LIST, (Serializable) mList);
                            intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                            intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                            startActivity(intent);
                        }
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

            case R.id.tv_synchronized_record:
                //点击同步
                getPwd();
                break;
        }
    }

    public void initData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        userId=MyApplication.getInstance().getUid();
        mPresenter.getLockPwdInfoEvent();
        //第一次进入该页面,由于锁上重置，删除，添加无法知道当前锁的信息所以只有第一次进入需要判断
        if (!TextUtils.isEmpty(userId)){
            int firstIn= (int) SPUtils.get(KeyConstants.FIRST_IN_GATEWAY_LOCK+userId+deviceId,1);
            //第一次进入
            if (firstIn==1){
                getPwd();
            }else{
                //读取数据库信息
                isAddLockPwd=2;
                maxPwdId=10;
               List<GatewayLockPwd> gatewayLockPwds= MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao().queryBuilder().where(GatewayLockPwdDao.Properties.DeviceId.eq(deviceId),GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId),GatewayLockPwdDao.Properties.Status.eq(1),GatewayLockPwdDao.Properties.Uid.eq(userId)).list();
                if (gatewayLockPwds!=null&&gatewayLockPwds.size()>0){
                    mList.clear();
                    for (GatewayLockPwd gatewayLockPwd:gatewayLockPwds){
                        if (!gatewayLockPwd.getNum().equals("09")){
                            mList.add(gatewayLockPwd.getNum());
                        }
                    }
                    if (mList!=null&&mList.size()>0){
                        passwordPageChange(true);
                        gatewayLockPasswordAdapter.notifyDataSetChanged();
                    }else{
                        passwordPageChange(false);
                    }
                }else{
                    passwordPageChange(false);
                }

            }
        }
    }
    public void getPwd(){
        //1获取最大用户密码数量
        if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
            isAddLockPwd=0;
            loadingDialog.show2(getString(R.string.get_gateway_lock_pwd_waiting));
            mPresenter.getLockPwdInfo(gatewayId, deviceId);
        }
    }

    @Override
    public void getLockOneSuccess(int pwdId) {
        int pwd = pwdId + 1;
        if (maxPwdId > 0 && pwd < maxPwdId) {
            if (pwd < 10) {
                mPresenter.getLockPwd(gatewayId, deviceId, "0" + pwd, maxPwdId, pwd);
            } else {
                mPresenter.getLockPwd(gatewayId, deviceId, pwd + "", maxPwdId, pwd);
            }

        }
    }

    @Override
    public void getLockSuccess(Map<String, Integer> map) {
        //获取开锁密码成功
        loadingDialog.dismiss();
        LogUtils.e(map.size() + "map集合大小");
        isAddLockPwd = 2;
        SPUtils.put(KeyConstants.FIRST_IN_GATEWAY_LOCK+ userId+deviceId,2);
         if (map.size() > 0) {
             mList.clear();
            for (String key : map.keySet()) {
                GatewayLockPwd gatewayLockPwd=new GatewayLockPwd();
                gatewayLockPwd.setDeviceId(deviceId);
                gatewayLockPwd.setGatewayId(gatewayId);
                gatewayLockPwd.setName("");
                gatewayLockPwd.setStatus(map.get(key));
                gatewayLockPwd.setNum(key);
                gatewayLockPwd.setUid(userId);
                Integer keyInt=Integer.parseInt(key);
                //用于zigbee锁是根据编号识别是永久密码，临时密码，还是胁迫密码
                if(keyInt<=4){
                    gatewayLockPwd.setTime(1);
                }else if (keyInt<=8&&keyInt>4){
                    gatewayLockPwd.setTime(2);
                }else if (keyInt==9){
                    gatewayLockPwd.setTime(3);
                }
                MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao().insert(gatewayLockPwd);
                if (map.get(key) == 1 && !key.equals("09")) {
                    mList.add(key);
                }
            }
            if (mList.size() > 0) {
                passwordPageChange(true);
                gatewayLockPasswordAdapter.notifyDataSetChanged();
            } else {
                passwordPageChange(false);
            }

        } else {
            passwordPageChange(false);
        }
         ToastUtil.getInstance().showShort(R.string.get_lock_pwd_list_success);
    }

    @Override
    public void getLockFail() {
        //获取开锁密码失败
        loadingDialog.dismiss();
        ToastUtil.getInstance().showShort(R.string.get_lock_pwd_list_fail);
        passwordPageChange(false);
        isAddLockPwd = 1;

    }

    @Override
    public void getLockThrowable(Throwable throwable) {
        LogUtils.e("获取开锁密码异常   " + throwable.getMessage());
        loadingDialog.dismiss();
        ToastUtil.getInstance().showShort(R.string.get_lock_pwd_list_fail);
        passwordPageChange(false);
        isAddLockPwd = 1;
    }

    @Override
    public void getLockInfoSuccess(int pwdNum) {
        //获取到总的次数
        maxPwdId = pwdNum;
        if (maxPwdId > 0) {
            mPresenter.getLockPwd(gatewayId, deviceId, "0" + 0, pwdNum, 0);
        }

    }

    @Override
    public void getLockInfoFail() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        isAddLockPwd=1;
        //passwordPageChange(false);
        ToastUtil.getInstance().showShort(getString(R.string.get_lock_info_fail));
        LogUtils.e("获取到锁信息失败   ");
    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        isAddLockPwd=1;
        //passwordPageChange(false);
        ToastUtil.getInstance().showShort(getString(R.string.get_lock_info_fail));
        LogUtils.e("获取到锁信息异常   ");
    }

    @Override
    public void addOnePwdLock(String pwdId) {
        if (mList!=null){
            if (!pwdId.equals("09")){
                mList.add(pwdId);
            }
        }
        if (gatewayLockPasswordAdapter!=null){
            gatewayLockPasswordAdapter.notifyDataSetChanged();
        }
        if (mList.size()==1){
            passwordPageChange(true);
        }


    }

    @Override
    public void deleteOnePwdLock(String pwdId) {
        if (mList!=null&&mList.size()>0){
            if (mList.contains(pwdId)){
                mList.remove(pwdId);
            }

        }
        if (gatewayLockPasswordAdapter!=null){
            gatewayLockPasswordAdapter.notifyDataSetChanged();
        }
        if (mList.size()==0){
            passwordPageChange(false);
        }

    }

    @Override
    public void deleteAllPwdLock() {
        if (mList!=null&&mList.size()>0){
            mList.clear();
        }
        if (gatewayLockPasswordAdapter!=null){
            passwordPageChange(false);
            gatewayLockPasswordAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void useSingleUse(String pwdId) {
        if (mList!=null&&mList.size()>0){
            if (mList.contains(pwdId)){
                mList.remove(pwdId);
            }

        }
        if (gatewayLockPasswordAdapter!=null){
            gatewayLockPasswordAdapter.notifyDataSetChanged();
        }
        if (mList.size()==0){
            passwordPageChange(false);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KeyConstants.DELETE_PWD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String lockNumber = data.getStringExtra(KeyConstants.LOCK_PWD_NUMBER);
                if (lockNumber != null) {
                    for (int i = 0; i < mList.size(); i++) {
                        if (lockNumber.equals(mList.get(i))) {
                            mList.remove(i);
                            if (mList.size() == 0) {
                                gatewayLockPasswordAdapter.notifyDataSetChanged();
                                passwordPageChange(false);
                            } else {
                                gatewayLockPasswordAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }

}
