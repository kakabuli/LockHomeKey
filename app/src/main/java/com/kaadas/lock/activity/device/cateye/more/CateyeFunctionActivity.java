package com.kaadas.lock.activity.device.cateye.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.VideoCallBackActivity;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.activity.device.gatewaylock.share.GatewayLockSharedActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.CatEyeFunctionPresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeFunctionView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ftp.GeTui;



import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/25
 */
public class CateyeFunctionActivity extends BaseActivity<ICatEyeFunctionView, CatEyeFunctionPresenter<ICatEyeFunctionView>> implements View.OnClickListener, ICatEyeFunctionView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_external_big)
    ImageView ivExternalBig;
    @BindView(R.id.iv_external_middle)
    ImageView ivExternalMiddle;
    @BindView(R.id.iv_external_small)
    ImageView ivExternalSmall;
    @BindView(R.id.iv_inner_small)
    ImageView ivInnerSmall;
    @BindView(R.id.iv_inner_middle)
    ImageView ivInnerMiddle;
    @BindView(R.id.tv_inner)
    TextView tvInner;
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    @BindView(R.id.tv_external)
    TextView tvExternal;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.ll_look_back)
    LinearLayout llLookBack;
    @BindView(R.id.ll_more)
    LinearLayout llMore;
    @BindView(R.id.iv_power)
    BatteryView ivPower;
    @BindView(R.id.iv_number)
    TextView iv_number;
    @BindView(R.id.device_share)
    LinearLayout deviceShare;
    private CateEyeInfo cateEyeInfo;
    private String gwId;
    private String devId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_function);
        ButterKnife.bind(this);
        initListener();
        initData();

    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        llLookBack.setOnClickListener(this);
        llMore.setOnClickListener(this);
        rlIcon.setOnClickListener(this);
        deviceShare.setOnClickListener(this);
    }

    @Override
    protected CatEyeFunctionPresenter<ICatEyeFunctionView> createPresent() {
        return new CatEyeFunctionPresenter<>();
    }

    private void initData() {
        HomeShowBean homeShowBean = (HomeShowBean) getIntent().getSerializableExtra(KeyConstants.CATE_INFO);
        if (homeShowBean != null) {
            cateEyeInfo = (CateEyeInfo) homeShowBean.getObject();
            if (cateEyeInfo != null) {
                if (!TextUtils.isEmpty(cateEyeInfo.getServerInfo().getNickName())) {
                    tvName.setText(cateEyeInfo.getServerInfo().getNickName());
                } else {
                    tvName.setText(cateEyeInfo.getServerInfo().getDeviceId());
                }
                gwId=cateEyeInfo.getGwID();
                devId=cateEyeInfo.getServerInfo().getDeviceId();
                if (!TextUtils.isEmpty(cateEyeInfo.getGwID())) {
                    GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(cateEyeInfo.getGwID());
                    if (gatewayInfo != null) {
                        if (NetUtil.isNetworkAvailable()) {
                            dealWithPower(cateEyeInfo.getPower(), cateEyeInfo.getServerInfo().getEvent_str(), cateEyeInfo.getPowerTimeStamp());
                            changeOpenLockStatus(cateEyeInfo.getServerInfo().getEvent_str());
                            if (gatewayInfo.getEvent_str() != null) {
                                if (gatewayInfo.getEvent_str().equals("offline")) {
                                    dealWithPower(cateEyeInfo.getPower(), "offline", cateEyeInfo.getPowerTimeStamp());
                                    changeOpenLockStatus("offline");
                                }
                            }

                        } else {
                            dealWithPower(cateEyeInfo.getPower(), "offline", cateEyeInfo.getPowerTimeStamp());
                            changeOpenLockStatus("offline");
                        }
                    }
                }
                mPresenter.getPowerData(cateEyeInfo.getGwID(), cateEyeInfo.getServerInfo().getDeviceId());
                mPresenter.getPublishNotify();//监听网关
                mPresenter.listenerDeviceOnline();//监听设备
                mPresenter.listenerNetworkChange();//监听网络变化
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
            case R.id.ll_look_back:
                List<GatewayInfo> allGateway = MyApplication.getInstance().getAllGateway();
                GatewayInfo gatewayInfo = null;
                for (GatewayInfo info : allGateway) {
                    if (cateEyeInfo.getGwID().equals(info.getServerInfo().getDeviceSN())) {
                        gatewayInfo = info;
                        break;
                    }
                }
                if (gatewayInfo == null) {
                    Toast.makeText(CateyeFunctionActivity.this, getString(R.string.refresh_geteway), Toast.LENGTH_SHORT).show();
                    return;
                }
                String meUserName = null;
                String mePwd = null;
                String gatewayId = null;
                String deviceId = null;
                try {
                    deviceId = cateEyeInfo.getServerInfo().getDeviceId();
                    gatewayId = cateEyeInfo.getGwID();
                    meUserName = gatewayInfo.getServerInfo().getMeUsername();
                    mePwd = gatewayInfo.getServerInfo().getMePwd();
                } catch (Exception e) {

                }
                Intent intentVideo = new Intent(CateyeFunctionActivity.this, VideoCallBackActivity.class);
                intentVideo.putExtra("gatewayId", gatewayId);
                intentVideo.putExtra("deviceId", deviceId);
                if (TextUtils.isEmpty(meUserName) || TextUtils.isEmpty(mePwd)) {
                    Toast.makeText(CateyeFunctionActivity.this, getString(R.string.refresh_geteway), Toast.LENGTH_SHORT).show();
                    return;
                }
                intentVideo.putExtra(Constants.MEUSERNAME, meUserName);
                intentVideo.putExtra(Constants.MEPWD, mePwd);
                startActivity(intentVideo);
                break;
            case R.id.ll_more:
                intent = new Intent(this, CateyeMoreActivity.class);
                intent.putExtra(KeyConstants.CATE_INFO, cateEyeInfo);
                startActivityForResult(intent, KeyConstants.UPDATE_DEVICE_NAME_REQUEST_CODE);
                break;
            case R.id.device_share:
                intent = new Intent(this, GatewayLockSharedActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gwId);
                intent.putExtra(KeyConstants.DEVICE_ID, devId);
                startActivity(intent);
                break;
            case R.id.rl_icon:
                Log.e(GeTui.VideoLog,"CatEyeFunction......"+VideoVActivity.isRunning);
                if (VideoVActivity.isRunning) {
                    Toast.makeText(CateyeFunctionActivity.this, getString(R.string.video_destory_time), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    intent = new Intent(this, VideoVActivity.class);
                    intent.putExtra(KeyConstants.IS_CALL_IN, false);
                    intent.putExtra(KeyConstants.CATE_INFO, cateEyeInfo);
                    startActivity(intent);
                }
                break;
        }
    }


    public void changeOpenLockStatus(String eventStr) {
        int status = 0;
        if ("online".equals(eventStr)) {
            status = 1;
        } else {
            status = 2;
        }

        switch (status) {
            case 1:
                //猫眼在线
                if (ivExternalBig != null && ivExternalMiddle != null && ivExternalSmall != null && ivInnerMiddle != null && ivInnerSmall != null && tvInner != null && tvExternal != null) {
                    ivExternalBig.setVisibility(View.VISIBLE);
                    ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon_back);
                    ivExternalMiddle.setVisibility(View.GONE);
                    ivExternalSmall.setVisibility(View.GONE);
                    ivInnerMiddle.setVisibility(View.VISIBLE);
                    ivInnerMiddle.setImageResource(R.mipmap.cateye_online);
                    ivInnerSmall.setVisibility(View.VISIBLE);
                    ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                    ivInnerSmall.setVisibility(View.GONE);
                    tvInner.setVisibility(View.VISIBLE);
                    tvInner.setText(getString(R.string.click_outside_door));
                    tvInner.setTextColor(getResources().getColor(R.color.white));
                    tvExternal.setVisibility(View.VISIBLE);
                    tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                    tvExternal.setText(getString(R.string.cateye_online));
                }
                break;
            case 2:
//                设备已离线
                if (ivExternalBig != null && ivExternalMiddle != null && ivExternalSmall != null && ivInnerMiddle != null && ivInnerSmall != null && tvInner != null && tvExternal != null) {
                    ivExternalBig.setVisibility(View.VISIBLE);
                    ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon_noline_back);
                    ivExternalMiddle.setVisibility(View.GONE);
                    ivExternalSmall.setVisibility(View.GONE);
                    ivInnerMiddle.setVisibility(View.VISIBLE);
                    ivInnerMiddle.setImageResource(R.mipmap.cateye_offline);
                    ivInnerSmall.setVisibility(View.INVISIBLE);
                    tvInner.setVisibility(View.VISIBLE);
                    tvInner.setText(getString(R.string.device_has_offline));
                    tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                    tvExternal.setVisibility(View.VISIBLE);
                    tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                    tvExternal.setText(getString(R.string.cateye_offline));
                }
                break;


        }
    }

    private void dealWithPower(int power, String eventStr, String timeStamp) {
        //电量：80%
        if (power > 100) {
            power = 100;
        }
        if (power < 0) {
            power = 0;
        }
        if (ivPower != null) {
            ivPower.setPower(power);
            iv_number.setText(power + "%");
            if (eventStr.equals("online")) {
                if (power <= 20) {
                    ivPower.setColor(R.color.cFF3B30);
                    ivPower.setBorderColor(R.color.white);
                } else {
                    ivPower.setColor(R.color.c25F290);
                    ivPower.setBorderColor(R.color.white);
                }

            } else {
                ivPower.setColor(R.color.cD6D6D6);
                ivPower.setBorderColor(R.color.c949494);
            }
        }
        //todo  读取电量时间
        long readDeviceInfoTime = 0;
        if (timeStamp == null) {
            readDeviceInfoTime = System.currentTimeMillis();
        } else {
            readDeviceInfoTime = Long.parseLong(timeStamp);
        }
        if (readDeviceInfoTime != -1 && tvDate != null) {
            if ((System.currentTimeMillis() - readDeviceInfoTime) < 60 * 60 * 1000) {
                //小于一小时
                tvDate.setText(getString(R.string.device_detail_power_date));
            } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 24 * 60 * 60 * 1000) {
                //小于一天
                tvDate.setText(getString(R.string.today) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 2 * 24 * 60 * 60 * 1000) {
                tvDate.setText(getString(R.string.yesterday) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else {
                tvDate.setText(DateUtils.formatYearMonthDay(readDeviceInfoTime));
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getIntent() != null) {
            getIntent().removeExtra(KeyConstants.CATE_INFO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KeyConstants.UPDATE_DEVICE_NAME_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra(KeyConstants.NAME);
                if (name != null) {
                    if (cateEyeInfo != null) {
                        cateEyeInfo.getServerInfo().setNickName(name);
                    }
                    if (tvName != null) {
                        tvName.setText(name);
                    }
                }
            }
        }

    }

    @Override
    public void getPowerDataSuccess(String deviceId, int power, String timestamp) {
        if (cateEyeInfo != null) {
            if (cateEyeInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                String eventStr = cateEyeInfo.getServerInfo().getEvent_str();
                dealWithPower(power, eventStr, timestamp);
                changeOpenLockStatus(eventStr);
            }
        }

    }

    @Override
    public void getPowerDataFail(String deviceId, String timeStamp) {
        //请求电量失败，证明离线
        if (cateEyeInfo != null) {
            if (cateEyeInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                cateEyeInfo.setPowerTimeStamp(timeStamp);
                String event = cateEyeInfo.getServerInfo().getEvent_str();
                dealWithPower(cateEyeInfo.getPower(), event, cateEyeInfo.getPowerTimeStamp());
                changeOpenLockStatus(event);
            }
        }


    }

    @Override
    public void getPowerThrowable() {

    }

    @Override
    public void gatewayStatusChange(String gatewayId, String eventStr) {
        //网关上下线
        if (cateEyeInfo != null) {
            if (cateEyeInfo.getGwID().equals(gatewayId)) {
                //当前猫眼所属的网关是上下线的网关
                //网关上下线状态要跟着改变
                if ("offline".equals(eventStr)) {
                    cateEyeInfo.getServerInfo().setEvent_str(eventStr);
                    LogUtils.e(cateEyeInfo.getPower() + "离线时猫眼的电量是多少  ");
                    dealWithPower(cateEyeInfo.getPower(), eventStr, cateEyeInfo.getPowerTimeStamp());
                    changeOpenLockStatus(eventStr);
                }
            }
        }
    }

    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
        //猫眼上下线
        if (cateEyeInfo != null) {
            //设备上下线为当的设备
            if (cateEyeInfo.getGwID().equals(gatewayId) && cateEyeInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                cateEyeInfo.getServerInfo().setEvent_str(eventStr);
                dealWithPower(cateEyeInfo.getPower(), eventStr, cateEyeInfo.getPowerTimeStamp());
                changeOpenLockStatus(eventStr);
            }
        }
    }

    @Override
    public void networkChangeSuccess() {
        if (cateEyeInfo != null) {
            changeOpenLockStatus("offline");
            dealWithPower(cateEyeInfo.getPower(), "offline", cateEyeInfo.getPowerTimeStamp());
        }
    }

}
