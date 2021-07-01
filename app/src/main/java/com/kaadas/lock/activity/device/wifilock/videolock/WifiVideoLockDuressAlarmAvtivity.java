package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.DuressAlarmAdapter;
import com.kaadas.lock.bean.DuressBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockDuressPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockDuressView;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StatusBarUtils;

import org.linphone.mediastream.Log;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WifiVideoLockDuressAlarmAvtivity extends BaseActivity<IWifiVideoLockDuressView, WifiVideoLockDuressPresenter<IWifiVideoLockDuressView>>
        implements IWifiVideoLockDuressView{

    private RecyclerView recycler;
    private ImageView mIvDuressSelect;
    private ImageView mBack;
    private String wifiSn = "";
    private DuressAlarmAdapter mDuressAlarmAdapter;
    private List<DuressBean> duressList;
    private int duressAlarmSwitch;
    private WifiLockInfo wifiLockInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_duress_alarm);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.white);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO)){
            int position = getIntent().getIntExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO,-1);
            if(position < 0) return;
            DuressBean bean = (DuressBean) getIntent().getSerializableExtra(KeyConstants.DURESS_PASSWORD_INfO);
            duressList.get(position).setPwdDuressSwitch(bean.getPwdDuressSwitch());
            duressList.get(position).setDuressAlarmAccount(bean.getDuressAlarmAccount());
            mDuressAlarmAdapter.setNewData(duressList);
            mDuressAlarmAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected WifiVideoLockDuressPresenter<IWifiVideoLockDuressView> createPresent() {
        return new WifiVideoLockDuressPresenter<>();
    }

    private void initData() {
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        initRecycleViewData();
        String localPasswordCache = (String) SPUtils.get(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn, "");
        WiFiLockPassword wiFiLockPassword = null;
        if (!TextUtils.isEmpty(localPasswordCache)) {
            wiFiLockPassword = new Gson().fromJson(localPasswordCache, WiFiLockPassword.class);
            duressList = mPresenter.setWifiLockPassword(wifiSn,wiFiLockPassword);
//            mDuressAlarmAdapter.setList(duressList);
            mDuressAlarmAdapter.setNewData(duressList);
        }else{
            mPresenter.getPasswordList(wifiSn);
        }
        if(wifiLockInfo != null){
            if(wifiLockInfo.getDuressAlarmSwitch() == 0){
                recycler.setVisibility(View.GONE);
                mIvDuressSelect.setSelected(false);
                duressAlarmSwitch = 0;
            }else{
                recycler.setVisibility(View.VISIBLE);
                mIvDuressSelect.setSelected(true);
                duressAlarmSwitch = 1;
            }
        }

    }

    private void initRecycleViewData() {
        mDuressAlarmAdapter = new DuressAlarmAdapter(R.layout.item_duress_alarm);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(mDuressAlarmAdapter);
        mDuressAlarmAdapter.setOnClickDuressNotificationListener((v, position, data) -> {
            Intent intent = new Intent(this,WifiVideoLockSettingDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.WIFI_SN,data.getWifiSN());
            intent.putExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO,position);
            intent.putExtra(KeyConstants.DURESS_PASSWORD_INfO,data);
//            startActivityForResult(intent,1012);
            startActivity(intent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setDuressAlarmSwitch();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initListener() {
        mIvDuressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    recycler.setVisibility(View.GONE);
                    mIvDuressSelect.setSelected(false);
                    duressAlarmSwitch = 0;
                }else{
                    recycler.setVisibility(View.VISIBLE);
                    mIvDuressSelect.setSelected(true);
                    duressAlarmSwitch = 1;
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDuressAlarmSwitch();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1012){
                int position = data.getIntExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO,-1);
                if(position < 0) return;
                duressList.get(position).setPwdDuressSwitch(data.getIntExtra("duress_alarm_toggle",0));
                duressList.get(position).setDuressAlarmAccount(data.getStringExtra("duress_alarm_phone"));
//                mDuressAlarmAdapter.setList();
                mDuressAlarmAdapter.setNewData(duressList);
                mDuressAlarmAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initView() {
        mIvDuressSelect = findViewById(R.id.iv_duress_select);
        mBack = findViewById(R.id.back);
        recycler = findViewById(R.id.recycler);

    }

    private void setDuressAlarmSwitch() {
        if(duressAlarmSwitch == wifiLockInfo.getDuressAlarmSwitch()){
            finish();
            return;
        }
        mPresenter.setDuressSwitch(wifiSn,duressAlarmSwitch);
    }


    @Override
    public void onGetPasswordSuccess(WiFiLockPassword wiFiLockPassword) {
        duressList = mPresenter.setWifiLockPassword(wifiSn,wiFiLockPassword);
//        mDuressAlarmAdapter.setList(duressList);
        mDuressAlarmAdapter.setNewData(duressList);
    }

    @Override
    public void onGetPasswordFailedServer(BaseResult baseResult) {

    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {

    }

    @Override
    public void onSettingDuress(BaseResult baseResult) {
        if("200".equals(baseResult.getCode() + "")){
            ToastUtils.showShort(R.string.set_success);
            finish();
        }else{
            ToastUtils.showShort(R.string.set_failed);
            finish();
        }
    }
}
