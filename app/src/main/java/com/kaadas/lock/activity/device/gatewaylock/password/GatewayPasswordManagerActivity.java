package com.kaadas.lock.activity.device.gatewaylock.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.GatewayLockPasswordAdapter;
import com.kaadas.lock.adapter.GatewayPasswordAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockFunctionPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockFunctinView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, GatewayPasswordManagerDetailActivity.class);
        intent.putExtra(KeyConstants.LOCK_PWD_NUMber,mList.get(position));
        startActivity(intent);
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
                intent = new Intent(this, GatewayPasswordAddActivity.class);
                startActivity(intent);
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
    public void getLockSuccess(Map<String,Integer> map) {
        //获取开锁密码成功
        loadingDialog.dismiss();
        LogUtils.e(map.size()+"map集合大小");
        if (map.size()>0){
            for (String key : map.keySet()) {
                if (map.get(key)==1){
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
        ToastUtil.getInstance().showShort("获取密码列表失败");
        passwordPageChange(false);

    }

    @Override
    public void getLockThrowable(Throwable throwable) {
        LogUtils.e("获取开锁密码异常   "+throwable.getMessage());
    }

    @Override
    public void getLockInfoSuccess(int pwdNum) {
        //获取到总的次数
        maxPwdId=pwdNum;
        if (pwdNum>0){
            for (int i=0;i<pwdNum;i++){
                mPresenter.getLockPwd(gatewayId,deviceId,"0"+i, pwdNum);
            }
        }

    }

    @Override
    public void getLockInfoFail() {

    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {

    }
}
