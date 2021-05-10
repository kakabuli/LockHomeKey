package com.kaadas.lock.activity.device.gatewaylock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.more.GatewayMoreActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.GatewayPasswordManagerActivity;
import com.kaadas.lock.activity.device.gatewaylock.share.GatewayLockSharedActivity;
import com.kaadas.lock.activity.device.gatewaylock.stress.old.GatewayLockStressDetailActivity;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockDetailPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDetailView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class GatewayLockFunctionActivity extends BaseActivity<GatewayLockDetailView, GatewayLockDetailPresenter<GatewayLockDetailView>> implements View.OnClickListener, GatewayLockDetailView {
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
    @BindView(R.id.iv_safe_protection)
    ImageView ivSafeProtection;
    @BindView(R.id.iv_four)
    ImageView ivFour;
    @BindView(R.id.tv_name_four)
    TextView tvNameFour;
    @BindView(R.id.ll_four)
    LinearLayout llFour;
    @BindView(R.id.gateway_lock_image)
    ImageView gatewayLockImage;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    private String   gatewayId;
    private String deviceId;
    private HomeShowBean showBean;
    private Handler mHandler = new Handler();

    private GwLockInfo lockInfo;


    private boolean getArmLock = false; //是否获取布防状态结束

    private boolean getArmLockSuccess = false; //是否获取布防成功

    private int armLock = 0;
    private int flagEvent = 0;
    String gatewayModel=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_function);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ButterKnife.bind(this);
        initView();
        initData();
        initClick();
        initListener();

        //动态设置状态栏高度
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(titleBar.getLayoutParams());
        lp.setMargins(0, getStatusBarHeight(), 0, 0);
        titleBar.setLayoutParams(lp);
    }

    private void initListener() {

    }

    @Override
    protected GatewayLockDetailPresenter<GatewayLockDetailView> createPresent() {
        return new GatewayLockDetailPresenter<>();
    }

    private void initView() {


        //密码
        ivOne.setImageResource(R.mipmap.bluetooth_password);
        tvNameOne.setText(R.string.password);
        //设备共享
        ivTwo.setImageResource(R.mipmap.share_icon);
        tvNameTwo.setText(R.string.device_share);

        //胁迫警告
        ivThree.setImageResource(R.mipmap.stress_warn_icon);
        tvNameThree.setText(R.string.stress_warn);
        //更多
        ivFour.setImageResource(R.mipmap.bluetooth_more);
        tvNameFour.setText(R.string.more);

    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        llOne.setOnClickListener(this);
        llTwo.setOnClickListener(this);
        llThree.setOnClickListener(this);
        llFour.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
        ivSafeProtection.setOnClickListener(this);
    }

    public void changLockStatus(int lockStatus) {
        if (isFinishing()) {
            return;
        }
        switch (lockStatus) {
            case KeyConstants.OPEN_LOCK:
                //可以开锁
                if (tvOpenClock != null) {
                    tvOpenClock.setClickable(true);
                    tvOpenClock.setText(R.string.click_lock);
                    tvOpenClock.setTextColor(getResources().getColor(R.color.c16B8FD));
                    tvOpenClock.setBackgroundResource(R.mipmap.open_lock_bj);
                }
                break;
            case KeyConstants.DEVICE_OFFLINE:
                //设备离线
                if (tvOpenClock != null) {
                    tvOpenClock.setClickable(false);
                    tvOpenClock.setText(getString(R.string.device_offline));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                    tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                }
                break;
            case KeyConstants.HAS_BEEN_LOCKED:
                //已反锁
                if (tvOpenClock != null) {
                    tvOpenClock.setText(getString(R.string.has_been_locked));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                    tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                }
                break;
            case KeyConstants.IS_LOCKING:
                //正在开锁中
                if (tvOpenClock != null) {
                    tvOpenClock.setText(getString(R.string.is_locking));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                    tvOpenClock.setBackgroundResource(R.mipmap.is_locking_bj);
                }
                break;
            case KeyConstants.OPEN_LOCK_SUCCESS:
                //开锁成功
                if (tvOpenClock != null) {
                    tvOpenClock.setText(getString(R.string.open_lock_success));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                    tvOpenClock.setBackgroundResource(R.mipmap.open_lock_success_bj);
                }
                break;
            case KeyConstants.OPEN_LOCK_FAILED:
                //开锁失败
                if (tvOpenClock != null) {
                    tvOpenClock.setText(getString(R.string.open_lock_failed));
                    tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                    tvOpenClock.setBackgroundResource(R.mipmap.open_lock_fail_bj);
                }
                break;

        }
    }

    private void initData() {
        Intent intent = getIntent();
        showBean = (HomeShowBean) intent.getSerializableExtra(KeyConstants.GATEWAY_LOCK_INFO);
        gatewayModel= getIntent().getStringExtra(KeyConstants.GATEWAY_MODEL);
        //设置电量
        if (showBean != null) {
            GwLockInfo gwLockInfo = (GwLockInfo) showBean.getObject();
            String lockversion = gwLockInfo.getServerInfo().getLockversion();
            if (!TextUtils.isEmpty(lockversion) && lockversion.contains(";") &&
                    (lockversion.split(";")[0].startsWith("8100Z") || lockversion.split(";")[0].startsWith("8100A"))) {
                gatewayLockImage.setImageResource(R.mipmap.bluetooth_lock_8100);
                tvType.setText(lockversion.split(";")[0]);
            }

            lockInfo = (GwLockInfo) showBean.getObject();
            if (lockInfo != null) {
                gatewayId = lockInfo.getGwID();
                if (!TextUtils.isEmpty(gatewayId)) {
                    GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
                    if (gatewayInfo != null) {
                        if (NetUtil.isNetworkAvailable()) {
                            dealWithPower(lockInfo.getPower(), lockInfo.getServerInfo().getEvent_str(), lockInfo.getPowerTimeStamp());
                            if (gatewayInfo.getEvent_str() != null) {
                                if (gatewayInfo.getEvent_str().equals("offline")) {
                                    dealWithPower(lockInfo.getPower(), "offline", lockInfo.getPowerTimeStamp());
                                }
                            }
                        } else {
                            dealWithPower(lockInfo.getPower(), "offline", lockInfo.getPowerTimeStamp());
                        }
                    }
                }
                if (!TextUtils.isEmpty(lockInfo.getServerInfo().getNickName())) {
                    tvName.setText(lockInfo.getServerInfo().getNickName());
                } else {
                    tvName.setText(lockInfo.getServerInfo().getDeviceId());
                }

                deviceId = lockInfo.getServerInfo().getDeviceId();
                mPresenter.getPowerData(lockInfo.getGwID(), lockInfo.getServerInfo().getDeviceId());
                mPresenter.closeLockNotify(deviceId);
                mPresenter.listenerDeviceOnline();
                mPresenter.getPublishNotify();
                mPresenter.listenerNetworkChange();
                /*//布防模式
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                    mPresenter.getArmLocked(MyApplication.getInstance().getUid(),gatewayId,deviceId);
                }*/
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
            case R.id.ll_one:
                //密码
                intent = new Intent(this, GatewayPasswordManagerActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                intent.putExtra(KeyConstants.GATEWAY_LOCK_INFO, lockInfo);
                intent.putExtra(KeyConstants.GATEWAY_MODEL,gatewayModel);
                startActivity(intent);

                break;
            case R.id.ll_two:
                //设备分享
                intent = new Intent(this, GatewayLockSharedActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                startActivity(intent);
                break;

            case R.id.ll_three:
                //胁迫警告
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    intent = new Intent(this, GatewayLockStressDetailActivity.class);
                    intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                    intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                    startActivity(intent);
                }

                break;

            case R.id.ll_four:
                //更多
                intent = new Intent(this, GatewayMoreActivity.class);
                if (!TextUtils.isEmpty(nickName)) {
                    lockInfo = (GwLockInfo) showBean.getObject();
                    if (lockInfo != null) {
                        lockInfo.getServerInfo().setNickName(nickName);
                    }
                }
                intent.putExtra(KeyConstants.GATEWAY_LOCK_INFO, showBean);
                startActivityForResult(intent, KeyConstants.DEVICE_DETAIL_BEAN_NUM);

                break;
            case R.id.tv_open_clock:
                //开锁
                //对话框
                String lockPwd = (String) SPUtils.get(KeyConstants.SAVA_LOCK_PWD + deviceId, "");
                if (flagEvent == 1) {
                    ToastUtils.showShort(getString(R.string.lock_already_offline));
                    return;
                } else if (TextUtils.isEmpty(lockPwd)) {
                    //密码为空
                    showPwdDialog();
                } else {
                    mPresenter.openLock(gatewayId, deviceId, lockPwd);
                    lockStatus = KeyConstants.IS_LOCKING;
                    changLockStatus(lockStatus);
                    tvOpenClock.setClickable(false);
                }

                break;
            case R.id.iv_safe_protection:
                if (getArmLock) {
                    if (getArmLockSuccess) {
                        if (armLock == 1) {
                            armLock = 0;
                        } else {
                            armLock = 1;
                        }
                        mPresenter.setArmLocked(MyApplication.getInstance().getUid(), gatewayId, deviceId, armLock);
                    } else {
                        ToastUtils.showShort(R.string.get_arm_lock_fail);
                    }
                } else {
                    ToastUtils.showShort(R.string.get_aram_lock);
                }


                break;

        }
    }

    private void showPwdDialog() {
        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
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
                    ToastUtils.showShort(R.string.random_verify_error);
                    return;
                }
                mPresenter.openLock(gatewayId, deviceId, pwd);
                lockStatus = KeyConstants.IS_LOCKING;
                changLockStatus(lockStatus);
                tvOpenClock.setClickable(false);
                alertDialog.dismiss();
            }
        });

    }


    private void dealWithPower(int power, String eventStr, String timeStamp) {
        //电量：80%
        if (power < 0) {
            power = 0;
        }
        int mPower = power / 2;
        String lockPower = mPower + "%";
        if (tvPower != null) {
            tvPower.setText(lockPower);
        }
        if (ivPower != null) {
            ivPower.setPower(mPower);
            if (eventStr.equals("online")) {
                if (mPower <= 20) {
                    ivPower.setColor(R.color.cFF3B30);
                    ivPower.setBorderColor(R.color.white);
                } else {
                    ivPower.setColor(R.color.c25F290);
                    ivPower.setBorderColor(R.color.white);
                }
                flagEvent = 0;
            } else {
                ivPower.setColor(R.color.cD6D6D6);
                ivPower.setBorderColor(R.color.c949494);
                flagEvent = 1;
            }
        }
        long readDeviceInfoTime = 0;
        if (timeStamp == null) {
            readDeviceInfoTime = System.currentTimeMillis();
        } else {
            readDeviceInfoTime = Long.parseLong(timeStamp);
        }
        //todo  读取电量时间
        if (readDeviceInfoTime != -1 && tvDate != null) {
            //60 * 60
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
        lockStatus = KeyConstants.OPEN_LOCK_SUCCESS;
        changLockStatus(lockStatus);
        tvOpenClock.setClickable(true);
    }

    @Override
    public void openLockFail() {
        //开锁失败
        lockStatus = KeyConstants.OPEN_LOCK_FAILED;
        changLockStatus(lockStatus);
        SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
        Runnable reconncetRunnable = new Runnable() {
            @Override
            public void run() {
                lockStatus = KeyConstants.OPEN_LOCK;
                changLockStatus(lockStatus);
                if (tvOpenClock != null) {
                    tvOpenClock.setClickable(true);
                }
            }
        };
        mHandler.postDelayed(reconncetRunnable, 3000);
    }

    @Override
    public void openLockThrowable(Throwable throwable) {
        //开锁异常
        lockStatus = KeyConstants.OPEN_LOCK_FAILED;
        changLockStatus(lockStatus);
        //清除密码
        SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
        Runnable reconncetRunnable = new Runnable() {
            @Override
            public void run() {
                lockStatus = KeyConstants.OPEN_LOCK;
                changLockStatus(lockStatus);
                if (tvOpenClock != null) {
                    tvOpenClock.setClickable(true);
                }
            }
        };
        mHandler.postDelayed(reconncetRunnable, 3000);

        LogUtils.e("开锁异常   " + throwable.getMessage());

    }

    @Override
    public void lockHasBeenClose() {
        //门锁已经关闭
        lockStatus = KeyConstants.OPEN_LOCK;
        changLockStatus(lockStatus);
    }

    @Override
    public void lockHasBeenOpen() {
        //门锁已经打开
    }

    @Override
    public void lockHasBeenThrowable(Throwable throwable) {
        LogUtils.e("门锁上报信息出现异常" + throwable.getMessage());
    }

    @Override
    public void getPowerDataSuccess(String deviceId, int power, String timestamp) {
        if (lockInfo != null) {
            if (lockInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                dealWithPower(power, lockInfo.getServerInfo().getEvent_str(), timestamp);
            }
        }

    }

    @Override
    public void getPowerDataFail(String deviceId, String timeStamp) {
        if (lockInfo != null) {
            if (lockInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                lockInfo.setPowerTimeStamp(timeStamp);
                dealWithPower(lockInfo.getPower(), lockInfo.getServerInfo().getEvent_str(), lockInfo.getPowerTimeStamp());
            }
        }
    }

    @Override
    public void getPowerThrowable() {

    }

    @Override
    public void gatewayStatusChange(String gatewayId, String eventStr) {
        //网关上下线
        if (lockInfo != null) {
            if (lockInfo.getGwID().equals(gatewayId)) {
                //当前猫眼所属的网关是上下线的网关
                //网关下线状态要跟着改变,网关上线时猫眼状态要等待猫眼通知才可以上线
                if (eventStr.equals("offline")) {
                    lockInfo.getServerInfo().setEvent_str(eventStr);
                    dealWithPower(lockInfo.getPower(), eventStr, lockInfo.getPowerTimeStamp());
                }
            }
        }
    }

    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
        if (lockInfo != null) {
            //设备上下线为当的设备
            if (lockInfo.getGwID().equals(gatewayId) && lockInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                lockInfo.getServerInfo().setEvent_str(eventStr);
                dealWithPower(lockInfo.getPower(), eventStr, lockInfo.getPowerTimeStamp());
            }
        }
    }

    @Override
    public void setArmLockedSuccess(int operatingMode) {
        //设置布防成功
        if (operatingMode == 1) {
            ivSafeProtection.setImageResource(R.mipmap.iv_open);
            armLock = 1;
        } else {
            ivSafeProtection.setImageResource(R.mipmap.iv_close);
            armLock = 0;
        }
    }

    @Override
    public void setArmLockedFail(String code) {
        //设置布防失败
        if ("405".equals(code)) {
            ToastUtils.showShort(getString(R.string.the_lock_no_support_func));
        } else {
            ToastUtils.showShort(getString(R.string.set_failed));
        }
    }

    @Override
    public void setArmLockedThrowable(Throwable throwable) {
        //设置布防异常
        ToastUtils.showShort(getString(R.string.set_failed));
    }

    @Override
    public void getArmLockedSuccess(int operatingMode) {
        //获取布防成功 0 撤防 非反锁  , 1 布防 , 2 反锁
        if (operatingMode == 1) {
            ivSafeProtection.setImageResource(R.mipmap.iv_open);
            armLock = 1;
        } else {
            ivSafeProtection.setImageResource(R.mipmap.iv_close);
            armLock = 0;
        }
        getArmLock = true;
        getArmLockSuccess = true;

    }

    @Override
    public void getArmLockedFail(String code) {
        //获取布防失败
        getArmLock = true;
        if ("405".equals(code)) {
            getArmLockSuccess = true;
        } else {
            getArmLockSuccess = false;
            ToastUtils.showShort(R.string.get_alarm_lock_fail);
        }


    }

    @Override
    public void getArmLockedThrowable(Throwable throwable) {
        //获取布防异常
        getArmLock = true;
        getArmLockSuccess = false;
        ToastUtils.showShort(R.string.get_alarm_lock_fail);
    }

    @Override
    public void networkChangeSuccess() {
        if (lockInfo != null) {
            dealWithPower(lockInfo.getPower(), "offline", lockInfo.getPowerTimeStamp());
        }
    }

    @Override
    public void deleteShareDeviceSuccess() {

    }

    @Override
    public void deleteShareDeviceFail() {

    }

    @Override
    public void deleteShareDeviceThrowable() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getIntent() != null) {
            getIntent().removeExtra(KeyConstants.GATEWAY_LOCK_INFO);
        }
    }

    String nickName = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KeyConstants.DEVICE_DETAIL_BEAN_NUM) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra(KeyConstants.NAME);
                if (name != null) {
                    if (tvName != null) {
                        tvName.setText(name);
                        nickName = name;
                    }
                }
            }
        }

    }

    //获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
