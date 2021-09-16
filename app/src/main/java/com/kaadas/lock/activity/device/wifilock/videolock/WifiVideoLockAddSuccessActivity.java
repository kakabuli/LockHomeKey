package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLcokSupportWifiActivity;
import com.kaadas.lock.adapter.AddBluetoothPairSuccessAdapter;
import com.kaadas.lock.bean.deviceAdd.AddBluetoothPairSuccessBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockSixthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoSixthView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.utils.EditTextWatcher;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockAddSuccessActivity extends BaseActivity<IWifiLockVideoSixthView
        , WifiVideoLockSixthPresenter<IWifiLockVideoSixthView>> implements IWifiLockVideoSixthView, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.lock)
    ImageView lock;
    @BindView(R.id.back)
    ImageView back;

    private List<AddBluetoothPairSuccessBean> mList;

    private AddBluetoothPairSuccessAdapter mAdapter;
    private String wifiSN;

    private String sSsid = "";

    private int func = 0;

    private boolean isUpdate = false;

    private String password = "";

    String name = "";

    private WifiLockVideoBindBean wifiLockVideoBindBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_lock_video_add_success);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
        initMonitor();

    }

    private void initMonitor() {
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setSelected(false);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initListener() {
        inputName.addTextChangedListener(new EditTextWatcher(this, null, inputName, 50));
    }

    @Override
    protected WifiVideoLockSixthPresenter<IWifiLockVideoSixthView> createPresent() {
        return new WifiVideoLockSixthPresenter<>();
    }

    private void initData() {
        mList = new ArrayList<>();
        mList.add(new AddBluetoothPairSuccessBean(getString(R.string.wifi_lock_my_home), false));
        mList.add(new AddBluetoothPairSuccessBean(getString(R.string.wifi_lock_bedroom), false));
        mList.add(new AddBluetoothPairSuccessBean(getString(R.string.wifi_lock_company), false));
        wifiSN = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC,0);
        isUpdate = getIntent().getBooleanExtra("update",false);
        password = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        wifiLockVideoBindBean = (WifiLockVideoBindBean) getIntent().getSerializableExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA);
    }

    private void initView() {
        recycler.setLayoutManager(new GridLayoutManager(this, 6));
        if (mList != null) {
            mAdapter = new AddBluetoothPairSuccessAdapter(mList);
            recycler.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
        }
        String name = inputName.getText().toString().trim();
        if (name != null) {
            inputName.setCursorVisible(false);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(TextUtils.isEmpty(inputName.getText().toString().trim())){
                name = wifiSN;
            }
            if(!isUpdate){
                /*mPresenter.bindDevice(wifiLockVideoBindBean.getWfId(),name,wifiLockVideoBindBean.getUserId(),
                        password,sSsid,func,3,
                        wifiLockVideoBindBean.getEventparams().getDevice_sn(),wifiLockVideoBindBean.getEventparams().getMac(),
                        wifiLockVideoBindBean.getEventparams().getDevice_did(),wifiLockVideoBindBean.getEventparams().getP2p_password()
                );*/
                mPresenter.setNickName(wifiSN, name);
            }else{
                mPresenter.setNickName(wifiSN, name);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        inputName.setCursorVisible(true);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelected(false);
        }
        AddBluetoothPairSuccessBean addBluetoothPairSuccessBean = mList.get(position);
        String name = addBluetoothPairSuccessBean.getName();
        inputName.setText(name);
        if (name != null) {
            inputName.setSelection(name.length());
        }
        inputName.setFocusable(true);
        inputName.setFocusableInTouchMode(true);
        inputName.requestFocus();
        mList.get(position).setSelected(true);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.save, R.id.back,R.id.tv_support_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                /*Intent backIntent = new Intent(this, MainActivity.class);
                startActivity(backIntent);*/
                if (TextUtils.isEmpty(name)) {
                    name = wifiSN;
                }
                showLoading(getString(R.string.is_saving_name));
                if(!isUpdate){
                    /*mPresenter.bindDevice(wifiLockVideoBindBean.getWfId(),name,wifiLockVideoBindBean.getUserId(),
                            password,sSsid,func,3,
                            wifiLockVideoBindBean.getEventparams().getDevice_sn(),wifiLockVideoBindBean.getEventparams().getMac(),
                            wifiLockVideoBindBean.getEventparams().getDevice_did(),wifiLockVideoBindBean.getEventparams().getP2p_password()
                    );*/
                    mPresenter.setNickName(wifiSN, name);
                }else{
                    mPresenter.setNickName(wifiSN, name);
                }
                break;
            case R.id.save:
                name = inputName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showShort(R.string.not_empty);
                    return;
                }
                if (!StringUtil.nicknameJudge(name)) {
                    ToastUtils.showShort(R.string.nickname_verify_error);
                    return;
                }

                showLoading(getString(R.string.is_saving_name));
                if(!isUpdate){
                    /*mPresenter.bindDevice(wifiLockVideoBindBean.getWfId(),name,wifiLockVideoBindBean.getUserId(),
                            password,sSsid,func,3,
                            wifiLockVideoBindBean.getEventparams().getDevice_sn(),wifiLockVideoBindBean.getEventparams().getMac(),
                            wifiLockVideoBindBean.getEventparams().getDevice_did(),wifiLockVideoBindBean.getEventparams().getP2p_password()
                    );*/
                    mPresenter.setNickName(wifiSN, name);
                }else{
                    mPresenter.setNickName(wifiSN, name);
                }


                break;
            case R.id.tv_support_list:
                startActivity(new Intent(this, WifiLcokSupportWifiActivity.class));
                break;
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return isCosumenBackKey();
        }
        return false;
    }

    private boolean isCosumenBackKey() {
        Intent backIntent = new Intent(this, MainActivity.class);
        startActivity(backIntent);
        return true;
    }

    @OnClick()
    public void onViewClicked() {
    }


    @Override
    public void onBindSuccess(String wifiSn) {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                hiddenLoading();
                MyApplication.getInstance().getAllDevicesByMqtt(true);
                Intent backIntent = new Intent(WifiVideoLockAddSuccessActivity.this, MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                hiddenLoading();
            }
        });
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                hiddenLoading();
            }
        });
    }

    @Override
    public void onUpdateSuccess(String wifiSn) {

    }

    @Override
    public void onUpdateFailed(BaseResult baseResult) {

    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {

    }

    @Override
    public void onCheckError(byte[] data) {

    }

    @Override
    public void onSetNameSuccess() {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                hiddenLoading();
                Intent backIntent = new Intent(WifiVideoLockAddSuccessActivity.this, MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
    }

    @Override
    public void onSetNameFailedNet(Throwable throwable) {
        hiddenLoading();
    }

    @Override
    public void onSetNameFailedServer(BaseResult baseResult) {
        hiddenLoading();
    }
}
