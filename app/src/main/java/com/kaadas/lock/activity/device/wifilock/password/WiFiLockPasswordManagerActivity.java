package com.kaadas.lock.activity.device.wifilock.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothPasswordAdapter;
import com.kaadas.lock.adapter.WifiLockCardAndFingerAdapter;
import com.kaadas.lock.adapter.WifiLockPasswordAdapter;
import com.kaadas.lock.bean.WiFiLockCardAndFingerShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockPasswordManagerPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockPasswordManagerView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WiFiLockPasswordManagerActivity extends BaseActivity<IWifiLockPasswordManagerView, WifiLockPasswordManagerPresenter<IWifiLockPasswordManagerView>>
        implements IWifiLockPasswordManagerView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.tv_no_password)
    TextView tvNoPassword;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private WifiLockPasswordAdapter passwordAdapter;
    private List<ForeverPassword> passwordList = new ArrayList<>();
    private int type;  // 1 密码  2指纹  3 卡片
    private List<WiFiLockCardAndFingerShowBean> cardAndFingerList = new ArrayList<>();
    private WifiLockCardAndFingerAdapter wifiLockCardAndFingerAdapter;
    private boolean havePassword = false;
    private String wifiSn;
    private static final int REQUEST_CODE = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_lock_password_manager);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra(KeyConstants.KEY_TYPE, 1);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        initView();
        initData();
        refreshLayout.autoRefresh();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initView() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //下拉刷新   如果正在同步，不刷新
                mPresenter.getPasswordList(wifiSn);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        String localPasswordCache = (String) SPUtils.get(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn, "");
        WiFiLockPassword wiFiLockPassword = null;
        if (!TextUtils.isEmpty(localPasswordCache)) {
            wiFiLockPassword = new Gson().fromJson(localPasswordCache, WiFiLockPassword.class);
        }
        if (type == 1) {
            headTitle.setText(R.string.password);
            passwordList = mPresenter.getShowPasswords(wiFiLockPassword);
            if (passwordList != null && passwordList.size() > 0) {
                havePassword = true;
            } else {
                havePassword = false;
            }
            tvNoPassword.setText(getString(R.string.no_password));
            initPasswordAdapter();
        } else if (type == 2) {
            headTitle.setText(R.string.fingerprint);
            tvNoPassword.setText(getString(R.string.no_finger));
            cardAndFingerList = mPresenter.getShowCardsFingers(wiFiLockPassword, type);
            if (cardAndFingerList != null && cardAndFingerList.size() > 0) {
                havePassword = true;
            } else {
                havePassword = false;
            }
            initCardAndFingerAdapter();
        } else if (type == 3) {
            headTitle.setText(R.string.door_card);
            cardAndFingerList = mPresenter.getShowCardsFingers(wiFiLockPassword, type);
            if (cardAndFingerList != null && cardAndFingerList.size() > 0) {
                havePassword = true;
            } else {
                havePassword = false;
            }
            tvNoPassword.setText(getString(R.string.no_card));
            initCardAndFingerAdapter();
        }
        changeState();
    }

    private void changeState() {
        if (havePassword) {
            recycleview.setVisibility(View.VISIBLE);
            tvNoPassword.setVisibility(View.GONE);
        } else {
            recycleview.setVisibility(View.GONE);
            tvNoPassword.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected WifiLockPasswordManagerPresenter<IWifiLockPasswordManagerView> createPresent() {
        return new WifiLockPasswordManagerPresenter<>();
    }

    private void initPasswordAdapter() {
        passwordAdapter = new WifiLockPasswordAdapter(passwordList, R.layout.item_bluetooth_password);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(passwordAdapter);
        passwordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(WiFiLockPasswordManagerActivity.this, WifiLockPasswordDetailActivity.class);
                ForeverPassword foreverPassword = passwordList.get(position);
                //输入密码类型
                intent.putExtra(KeyConstants.PASSWORD_TYPE, type);
                intent.putExtra(KeyConstants.TO_PWD_DETAIL, foreverPassword);  //密码数据
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);  //WiFiSN
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void initCardAndFingerAdapter() {
        wifiLockCardAndFingerAdapter = new WifiLockCardAndFingerAdapter(cardAndFingerList, R.layout.item_door_card_manager);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(wifiLockCardAndFingerAdapter);
        wifiLockCardAndFingerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(WiFiLockPasswordManagerActivity.this, WifiLockPasswordDetailActivity.class);
                WiFiLockCardAndFingerShowBean wiFiLockCardAndFingerShowBean = cardAndFingerList.get(position);
                //输入密码类型
                intent.putExtra(KeyConstants.PASSWORD_TYPE, type);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);  //WiFiSN
                intent.putExtra(KeyConstants.TO_PWD_DETAIL, wiFiLockCardAndFingerShowBean);  //密码数据
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onGetPasswordSuccess(WiFiLockPassword wiFiLockPassword) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
        }
        initData();
    }

    @Override
    public void onGetPasswordFailedServer(BaseResult baseResult) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
        }
        ToastUtil.getInstance().showLong(R.string.refresh_failed_please_retry_later);
    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
        }
        ToastUtil.getInstance().showLong(R.string.refresh_failed_please_retry_later);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mPresenter.getPasswordList(wifiSn);
        }
    }
}
