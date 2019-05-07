package com.kaadas.lock.activity.device.gatewaylock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.more.GatewayMoreActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.GatewayPasswordManagerActivity;
import com.kaadas.lock.activity.device.gatewaylock.share.GatewaySharedDeviceManagementActivity;
import com.kaadas.lock.activity.device.gatewaylock.stress.GatewayStressPasswordManagerActivity;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.GatewayBindPresenter;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockDetailPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.GatewayBindView;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDetailView;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kaadas.lock.MyApplication.getInstance;

/**
 * Created by David
 */
public class GatewayLockFunctionActivity extends BaseActivity<GatewayLockDetailView, GatewayLockDetailPresenter<GatewayLockDetailView>> implements View.OnClickListener,GatewayLockDetailView{
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;//门锁
    @BindView(R.id.iv_power)
    BatteryView ivPower;

    @BindView(R.id.tv_power)
    TextView tvPower;//电量大小
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    List<BluetoothLockFunctionBean> list = new ArrayList<>();
    @BindView(R.id.iv_one)
    ImageView ivOne;
    @BindView(R.id.tv_name_one)
    TextView tvNameOne;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.iv_two)
    ImageView ivTwo;
    @BindView(R.id.tv_name_two)
    TextView tvNameTwo;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.iv_three)
    ImageView ivThree;
    @BindView(R.id.tv_name_three)
    TextView tvNameThree;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
   /* @BindView(R.id.iv_four)
    ImageView ivFour;
    @BindView(R.id.tv_name_four)
    TextView tvNameFour;
    @BindView(R.id.tv_number_four)
    TextView tvNumberFour;
    @BindView(R.id.ll_four)
    LinearLayout llFour;*/


    @BindView(R.id.tv_open_clock)
    TextView tvOpenClock;
    int lockStatus = -1;
    private String gatewayId;
    private String deviceId;
    private DeviceDetailBean deviceDetailBean;
    private boolean lockIsOpen=false;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_function);
        ButterKnife.bind(this);
        initView();
        initData();
        initClick();



    }

    @Override
    protected GatewayLockDetailPresenter<GatewayLockDetailView> createPresent() {
        return new GatewayLockDetailPresenter<>();
    }

    private void initView() {
        //密码
        ivOne.setImageResource(R.mipmap.bluetooth_password);
        tvNameOne.setText(R.string.password);
        //胁迫警告
        ivTwo.setImageResource(R.mipmap.stress_warn_icon);
        tvNameTwo.setText(getString(R.string.stress_warn));

        //更多
        ivThree.setImageResource(R.mipmap.bluetooth_more);
        tvNameThree.setText(R.string.more);


    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        llOne.setOnClickListener(this);
        llTwo.setOnClickListener(this);
        llThree.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
    }

    public void changLockStatus(int lockStatus) {
        switch (lockStatus) {
            case KeyConstants.OPEN_LOCK:
                //可以开锁
                if (tvOpenClock!=null) {
                    tvOpenClock.setClickable(true);
                    tvOpenClock.setText(R.string.click_lock);
                    tvOpenClock.setTextColor(getResources().getColor(R.color.c16B8FD));
                    tvOpenClock.setBackgroundResource(R.mipmap.open_lock_bj);
                }
                break;
            case KeyConstants.DEVICE_OFFLINE:
                //设备离线
                if (tvOpenClock!=null) {
                    tvOpenClock.setClickable(false);
                    tvOpenClock.setText(getString(R.string.device_offline));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                    tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                }
                break;
            case KeyConstants.HAS_BEEN_LOCKED:
                //已反锁
                if (tvOpenClock!=null) {
                    tvOpenClock.setText(getString(R.string.has_been_locked));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                    tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                }
                break;
            case KeyConstants.IS_LOCKING:
                //正在开锁中
                if (tvOpenClock!=null) {
                    tvOpenClock.setText(getString(R.string.is_locking));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                    tvOpenClock.setBackgroundResource(R.mipmap.is_locking_bj);
                }
                break;
            case KeyConstants.OPEN_LOCK_SUCCESS:
                //开锁成功
                if (tvOpenClock!=null) {
                    tvOpenClock.setText(getString(R.string.open_lock_success));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                    tvOpenClock.setBackgroundResource(R.mipmap.open_lock_success_bj);
                }
                break;
            case KeyConstants.OPEN_LOCK_FAILED:
                //开锁失败
                if (tvOpenClock!=null) {
                    tvOpenClock.setText(getString(R.string.open_lock_failed));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                    tvOpenClock.setBackgroundResource(R.mipmap.open_lock_fail_bj);
                }
                break;

        }
    }

    private void initData() {
        Intent intent=getIntent();
        deviceDetailBean= (DeviceDetailBean) intent.getSerializableExtra(KeyConstants.DEVICE_DETAIL_BEAN);
        //设置电量
        if(deviceDetailBean!=null){
            dealWithPower(deviceDetailBean.getPower());
            tvName.setText(deviceDetailBean.getDeviceName());
            GwLockInfo lockInfo= (GwLockInfo) deviceDetailBean.getShowCurentBean();
            gatewayId=lockInfo.getGwID();
            deviceId=lockInfo.getServerInfo().getDeviceId();
            mPresenter.closeLockNotify(deviceId);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_one:
                intent = new Intent(this, GatewayPasswordManagerActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                startActivity(intent);
                break;
            case R.id.ll_two:
                intent = new Intent(this, GatewayStressPasswordManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_three:
                intent = new Intent(this, GatewayMoreActivity.class);
                intent.putExtra(KeyConstants.DEVICE_DETAIL_BEAN,deviceDetailBean);
                startActivityForResult(intent,KeyConstants.DEVICE_DETAIL_BEAN_NUM);
                break;

            case R.id.tv_open_clock:
                //开锁
                //对话框
             String lockPwd= (String) SPUtils.get(KeyConstants.SAVA_LOCK_PWD+deviceId,"");
             if (TextUtils.isEmpty(lockPwd)){
                 //密码为空
                 if (lockIsOpen){
                    //门锁已经打开
                     ToastUtil.getInstance().showShort(R.string.lock_has_bean_close);
                     return;
                 }else{
                     showPwdDialog();
                 }
             }else{
                    if (lockIsOpen){
                        //门锁已经打开
                        ToastUtil.getInstance().showShort(R.string.lock_has_bean_close);
                        return;
                    }else{
                        mPresenter.openLock(gatewayId, deviceId,lockPwd);
                        lockStatus = KeyConstants.IS_LOCKING;
                        changLockStatus(lockStatus);
                        tvOpenClock.setClickable(false);
                    }
             }

             break;

        }
    }

    private void showPwdDialog() {
        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
        tvTitle.setText(getString(R.string.input_open_lock_password));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = editText.getText().toString().trim();
                if (!StringUtil.randomJudge(pwd)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                    mPresenter.openLock(gatewayId, deviceId,pwd);
                    lockStatus = KeyConstants.IS_LOCKING;
                    changLockStatus(lockStatus);
                    tvOpenClock.setClickable(false);
                    alertDialog.dismiss();
            }
        });

    }



    private void dealWithPower(int power) {
        //电量：80%
        if (power > 100) {
            power = 100;
        }
        if (power<0){
            power=0;
        }
        String lockPower = power + "%";
        tvPower.setText(lockPower);
        ivPower.setPower(power);
        ivPower.setColor(R.color.c25F290);
        ivPower.setBorderColor(R.color.white);
        //todo  读取电量时间
        long readDeviceInfoTime = System.currentTimeMillis();
        if (readDeviceInfoTime != -1) {
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
    public void openLockSuccess() {
        //开锁成功
        lockStatus=KeyConstants.OPEN_LOCK_SUCCESS;
        changLockStatus(lockStatus);
        tvOpenClock.setClickable(true);
    }

    @Override
    public void openLockFail() {
        //开锁失败
        lockStatus=KeyConstants.OPEN_LOCK_FAILED;
        changLockStatus(lockStatus);
        SPUtils.remove(KeyConstants.SAVA_LOCK_PWD+deviceId);
        Runnable reconncetRunnable = new Runnable() {
            @Override
            public void run() {
                lockStatus=KeyConstants.OPEN_LOCK;
                changLockStatus(lockStatus);
                if (tvOpenClock!=null) {
                    tvOpenClock.setClickable(true);
                }
            }
        };
        mHandler.postDelayed(reconncetRunnable, 3000);
    }

    @Override
    public void openLockThrowable(Throwable throwable) {
        //开锁异常
        lockStatus=KeyConstants.OPEN_LOCK_FAILED;
        changLockStatus(lockStatus);
        //清除密码
        SPUtils.remove(KeyConstants.SAVA_LOCK_PWD+deviceId);
        Runnable reconncetRunnable = new Runnable() {
            @Override
            public void run() {
                lockStatus=KeyConstants.OPEN_LOCK;
                changLockStatus(lockStatus);
                if (tvOpenClock!=null) {
                    tvOpenClock.setClickable(true);
                }
            }
        };
        mHandler.postDelayed(reconncetRunnable, 3000);

        LogUtils.e("开锁异常   "+throwable.getMessage());

    }

    @Override
    public void lockHasBeenClose() {
        //门锁已经关闭
        lockIsOpen=false;
        lockStatus=KeyConstants.OPEN_LOCK;
        changLockStatus(lockStatus);
    }

    @Override
    public void lockHasBeenOpen() {
        //门锁已经打开
        lockIsOpen=true;
    }

    @Override
    public void lockHasBeenThrowable(Throwable throwable) {
        LogUtils.e("门锁上报信息出现异常"+throwable.getMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==KeyConstants.DEVICE_DETAIL_BEAN_NUM){
            if (resultCode== Activity.RESULT_OK){
                String name=data.getStringExtra(KeyConstants.NAME);
                if (name!=null){
                    if (deviceDetailBean!=null){
                        deviceDetailBean.setDeviceName(name);
                    }
                    if (tvName!=null) {
                        tvName.setText(name);
                    }
                }
            }
        }

    }
}
