package com.kaadas.lock.activity.device.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothSharedDeviceManagementAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.BluetoothSharedDeviceManagementPresenter;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.IBluetoothSharedDeviceManagementView;
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
public class BluetoothSharedDeviceManagementActivity extends BaseActivity<IBluetoothSharedDeviceManagementView, BluetoothSharedDeviceManagementPresenter<IBluetoothSharedDeviceManagementView>> implements IBluetoothSharedDeviceManagementView, BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题
    @BindView(R.id.recycleview)
    RecyclerView recycleview;

    BluetoothSharedDeviceManagementAdapter bluetoothSharedDeviceManagementAdapter;
    List<BluetoothSharedDeviceBean.DataBean> list = new ArrayList<>();
    boolean isNotData = true;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    @BindView(R.id.ll_add_user)
    LinearLayout llAddUser;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;

    private BleLockInfo bleLockInfo;//蓝牙锁信息
    public static final int REQUEST_CODE = 100;
    boolean querySuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_shared_device_management);
        ButterKnife.bind(this);
        bluetoothSharedDeviceManagementAdapter = new BluetoothSharedDeviceManagementAdapter(list, R.layout.item_has_bluetooth_shared_device);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(bluetoothSharedDeviceManagementAdapter);
        bluetoothSharedDeviceManagementAdapter.setOnItemClickListener(this);
        ivBack.setOnClickListener(this);
        llAddUser.setOnClickListener(this);
        tvContent.setText(getString(R.string.user_manage));
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        initRefresh();
    }

    @Override
    protected BluetoothSharedDeviceManagementPresenter<IBluetoothSharedDeviceManagementView> createPresent() {
        return new BluetoothSharedDeviceManagementPresenter<>();
    }

    private void initRefresh() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //清除数据
                list.clear();
                bluetoothSharedDeviceManagementAdapter.notifyDataSetChanged();
                //todo 获取数据 李绪进---家庭成员
                queryUser();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryUser();
    }

    /**
     * 查询用户
     */
    public void queryUser() {
        if (bleLockInfo != null && bleLockInfo.getServerLockInfo() != null && bleLockInfo.getServerLockInfo().getLockName() != null) {
            if (NetUtil.isNetworkAvailable()) {
                mPresenter.queryUserList(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName());
            } else {
                ToastUtil.getInstance().showShort(R.string.noNet);
            }

        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BluetoothSharedDeviceBean.DataBean dataBean = list.get(position);
        Intent intent = new Intent(this, FamilyMemberDetailActivity.class);
        intent.putExtra(KeyConstants.COMMON_FAMILY_MEMBER_DATA, dataBean);
        startActivity(intent);
    }

    public void pageChange() {
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
                if (querySuccess == true) {
                    if (list.size() < 10) {
                        intent = new Intent(this, AddBluetoothFamilyMemberActivity.class);
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

                } else {
                    ToastUtil.getInstance().showShort(R.string.query_fail_requery);
                }
                break;

        }
    }

    @Override
    public void queryCommonUserListSuccess(BluetoothSharedDeviceBean bluetoothSharedDeviceBean) {
        //刷新完成
        refreshLayout.finishRefresh();
        querySuccess = true;
        List<BluetoothSharedDeviceBean.DataBean> dataBeanList = bluetoothSharedDeviceBean.getData();
        if (dataBeanList !=null){
            SPUtils.putProtect(KeyConstants.USER_MANAGE_NUMBER,dataBeanList.size() );
        }
        if (dataBeanList.size() > 0) {
            list.clear();
            list.addAll(dataBeanList);
            isNotData = false;
            bluetoothSharedDeviceManagementAdapter.notifyDataSetChanged();
        } else {
            isNotData = true;
        }
        pageChange();

    }

    @Override
    public void queryCommonUserListFail(BluetoothSharedDeviceBean bluetoothSharedDeviceBean) {
        querySuccess = false;
        //刷新完成
        refreshLayout.finishRefresh();
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, bluetoothSharedDeviceBean.getCode()));
    }

    @Override
    public void queryCommonUserListError(Throwable throwable) {
        querySuccess = false;
        //刷新完成
        refreshLayout.finishRefresh();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void addCommonUserSuccess(BaseResult baseResult) {
        queryUser();
        ToastUtil.getInstance().showShort(R.string.add_common_user_success);
    }

    @Override
    public void addCommonUserFail(BaseResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void addCommonUserError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (REQUEST_CODE == requestCode) {
                String phone = data.getStringExtra(KeyConstants.AUTHORIZATION_TELEPHONE);
                String uid = MyApplication.getInstance().getUid();
                if (bleLockInfo == null) {
                    return;
                }
                String devmac = bleLockInfo.getServerLockInfo().getMacLock();   // 添加用户
                String device_name = bleLockInfo.getServerLockInfo().getLockName();
                String device_nickname = bleLockInfo.getServerLockInfo().getLockNickName();
                String time = System.currentTimeMillis() + "";
                List<String> items = new ArrayList<>();
                mPresenter.addCommonUser(uid, phone, devmac, device_name, System.currentTimeMillis() + "", device_nickname, "3", time, items);
            }
        }
    }
}
