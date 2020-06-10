package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockAddSuccessActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiApWifiSetUpPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockBleToWifiSetUpPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAPWifiSetUpView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockBleToWifiSetUpView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.widget.WifiCircleProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewBLEWIFICSwitchCheckWifiActivity extends BaseActivity<IWifiLockBleToWifiSetUpView, WifiLockBleToWifiSetUpPresenter<WifiLockBleToWifiSetUpPresenter>>
        implements IWifiLockBleToWifiSetUpView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.circle_progress_bar2)
    WifiCircleProgress circleProgressBar2;
    @BindView(R.id.tv_send_wifi_info)
    TextView tvSendWifiInfo;
    @BindView(R.id.iv_send_wifi_info)
    ImageView ivSendWifiInfo;
    @BindView(R.id.tv_set_success)
    TextView tvSetSuccess;
    @BindView(R.id.iv_set_success)
    ImageView ivSetSuccess;
    @BindView(R.id.tv_bind_success)
    TextView tvBindSuccess;
    @BindView(R.id.iv_bind_success)
    ImageView ivBindSuccess;

    private Animation animation;
//    SocketManager socketManager = SocketManager.getInstance();
    private int func;
    private String randomCode;
    private String wifiSn;
    private String sPassword;
    private String sSsid;
    private boolean isSuccess = false;
    private int times = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_check_wifi);
        ButterKnife.bind(this);

        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        sPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC, 0);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);
        Log.e("--kaadas--", "onCreate: sSsid " + sSsid + "   sPassword " + sPassword);
        thread.start();
        LogUtils.e("--kaadas--randomCode数据是==" + randomCode);
        LogUtils.e("--kaadas--功能集是=="+ func);

        circleProgressBar2.setValue(0);

        animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);//动画的反复次数
        animation.setFillAfter(false);//设置为true，动画转化结束后被应用

        changeState(1);
    }

    @Override
    protected WifiLockBleToWifiSetUpPresenter<WifiLockBleToWifiSetUpPresenter> createPresent() {
        return new WifiLockBleToWifiSetUpPresenter<>();
    }

    private void onBindSuccess() {
        startActivity(new Intent(this, WifiLockAddNewBindSuccesssActivity.class));

    }

    private void onBindFailed() {
        startActivity(new Intent(this, WifiLockAddNewBindFailedActivity.class));
    }

    @OnClick({R.id.back, R.id.help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
        }
    }

//    private Thread thread = new Thread() {
//        @Override
//        public void run() {
//            super.run();
//            if (socketManager.isStart()) { //连接成功
//                LogUtils.e("连接成功");
//                byte[] bSsid ;
//                byte[] bPwd = sPassword.getBytes();
//                String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
//                if (sSsid.equals(wifiName)) {
//                    String pwdByteString = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, "");
//                    bSsid = Rsa.hex2byte2(pwdByteString);
//                } else {
//                    bSsid = sSsid.getBytes();
//                }
//
//                byte[] data = new byte[96];
//                System.arraycopy(bSsid, 0, data, 0, bSsid.length);
//                System.arraycopy(bPwd, 0, data, 32, bPwd.length);
//                int writeResult = socketManager.writeData(data);
//                if (writeResult == 0) {
//                    LogUtils.e("发送账号密码成功   开始读取数据");
//                    SocketManager.ReadResult readResult = socketManager.readWifiDataTimeout(60 * 1000);
//                    if (readResult.resultCode >= 0) { //读取成功
//                        String sResult = new String(readResult.data);
//                        LogUtils.e("读取成功   " + sResult);
//                        if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APSuccess")) {
//                            changeState(2);
//                            onSuccess();
//                            isSuccess = true;
//                            socketManager.destroy();
//                        } else if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APError")) {
//
//                            if (times < 5) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this,"请靠近路由器检查账号或密码是否输入正确", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this, WifiLockAddNewInputWifiActivity.class);
//                                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
//                                        intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
//                                        intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
//                                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times + 1);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                });
//                            } else { //五次失败
//                                socketManager.writeData("************************************************************************************************APClose".getBytes());
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        onWiFIAndPWDError();
//                                    }
//                                });
//
//                            }
//                        } else {
//                            onError(socketManager, -5);
//                        }
//                    } else {
//                        onError(socketManager, -1);
//                        LogUtils.e("读数据失败   " + writeResult);
//                    }
//                } else { //写数据失败
//                    LogUtils.e("写数据失败   " + writeResult);
//                    onError(socketManager, -4);
//                }
//            } else {  //连接失败
//                LogUtils.e("连接失败");
//                onError(socketManager, -2);
//            }
//        }
//    };
    private Thread thread = new Thread() {
      @Override
      public void run() {
        super.run();

            byte[] bSsid = new byte[42];
            byte[] bPwd = new byte[70];
          System.arraycopy(sPassword.getBytes(),0,bPwd,0,sPassword.getBytes().length);
//            bPwd = sPassword.getBytes();

            String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
              if (sSsid.equals(wifiName)) {
                  String pwdByteString = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, "");
//                  bSsid = Rsa.hex2byte2(pwdByteString);
                  System.arraycopy(Rsa.hex2byte2(pwdByteString),0,bSsid,0,Rsa.hex2byte2(pwdByteString).length);

              } else {
//                  bSsid = sSsid.getBytes();
                  System.arraycopy(sSsid.getBytes(),0,bSsid,0,sSsid.getBytes().length);

              }
            mPresenter.listenerCharacterNotify();

             mPresenter.sendSSIDAndPWD(bSsid,bPwd);


//              byte[] data = new byte[96];
//              System.arraycopy(bSsid, 0, data, 0, bSsid.length);
//              System.arraycopy(bPwd, 0, data, 32, bPwd.length);
//              int writeResult = socketManager.writeData(data);
//              if (writeResult == 0) {
//                  LogUtils.e("发送账号密码成功   开始读取数据");
//                  SocketManager.ReadResult readResult = socketManager.readWifiDataTimeout(60 * 1000);
//                  if (readResult.resultCode >= 0) { //读取成功
//                      String sResult = new String(readResult.data);
//                      LogUtils.e("读取成功   " + sResult);
//                      if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APSuccess")) {
//                          changeState(2);
//                          onSuccess();
//                          isSuccess = true;
////                          socketManager.destroy();
//                      } else if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APError")) {
//
//                          if (times < 5) {
//                              runOnUiThread(new Runnable() {
//                                  @Override
//                                  public void run() {
//                                      Toast.makeText(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this,"请靠近路由器检查账号或密码是否输入正确", Toast.LENGTH_SHORT).show();
//                                      Intent intent = new Intent(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this, WifiLockAddNewInputWifiActivity.class);
//                                      intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
//                                      intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
//                                      intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
//                                      intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times + 1);
//                                      startActivity(intent);
//                                      finish();
//                                  }
//                              });
//                          } else { //五次失败
////                              socketManager.writeData("************************************************************************************************APClose".getBytes());
//                              runOnUiThread(new Runnable() {
//                                  @Override
//                                  public void run() {
//                                      onWiFIAndPWDError();
//                                  }
//                              });
//
//                          }
//                      } else {
////                          onError(socketManager, -5);
//                      }
//                  } else {
////                      onError(socketManager, -1);
//                      LogUtils.e("读数据失败   " + writeResult);
//                  }
//              } else { //写数据失败
//                  LogUtils.e("写数据失败   " + writeResult);
////                  onError(socketManager, -4);
//              }

      }
    };
    private void onWiFIAndPWDError() {
        AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this, "", "Wi-Fi账号或密码输错已超过5次", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

//                        onError(socketManager, -5);
                    }

                    @Override
                    public void right() {

//                        onError(socketManager, -5);
                    }
                });
    }

    /**
     * @param socketManager
     * @param errorCode     -1 读取失败  -2 连接失败  -3 校验失败
     */
    public void onError(SocketManager socketManager, int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    return;
                }
                finish();
//                socketManager.destroy();
                Toast.makeText(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this, R.string.bind_failed+"=="+errorCode, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this, WifiLockAddNewBindFailedActivity.class);
                startActivity(intent);
//                if (errorCode == -1) {
//                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "读取失败", Toast.LENGTH_SHORT).show();
//                } else if (errorCode == -2) {
//                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//                } else if (errorCode == -3) {
//                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "管理员密码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
//                } else if (errorCode == -4) {
//                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "写数据错误", Toast.LENGTH_SHORT).show();
//                } else if (errorCode == -5) {
//                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "返回错误", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }


    /**
     * @param status 1初始状态  2 发送wifi信息成功  3设置成功 正在绑定  4
     */
    private void changeState(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case 1:
                        ivSendWifiInfo.setImageResource(R.mipmap.wifi_lock_add_state_refresh);
                        ivSetSuccess.setImageResource(R.mipmap.wifi_lock_add_state_wait);
                        ivBindSuccess.setImageResource(R.mipmap.wifi_lock_add_state_wait);

                        tvSendWifiInfo.setTextColor(getResources().getColor(R.color.color_333));
                        tvSetSuccess.setTextColor(getResources().getColor(R.color.color_cdcdcd));
                        tvBindSuccess.setTextColor(getResources().getColor(R.color.color_cdcdcd));

                        ivSendWifiInfo.startAnimation(animation);//開始动画
                        ivSetSuccess.clearAnimation();
                        ivBindSuccess.clearAnimation();

                        circleProgressBar2.setValue(10);

                        break;
                    case 2:
                        ivSendWifiInfo.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivSetSuccess.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivBindSuccess.setImageResource(R.mipmap.wifi_lock_add_state_wait);

                        tvSendWifiInfo.setTextColor(getResources().getColor(R.color.color_333));
                        tvSetSuccess.setTextColor(getResources().getColor(R.color.color_333));
                        tvBindSuccess.setTextColor(getResources().getColor(R.color.color_cdcdcd));

                        ivSendWifiInfo.clearAnimation();//開始动画
                        ivSetSuccess.startAnimation(animation);
                        ivBindSuccess.clearAnimation();

                        circleProgressBar2.setValue(40);
                        break;
                    case 3:
                        ivSendWifiInfo.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivBindSuccess.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivBindSuccess.setImageResource(R.mipmap.wifi_lock_add_state_refresh);

                        tvSendWifiInfo.setTextColor(getResources().getColor(R.color.color_333));
                        tvSetSuccess.setTextColor(getResources().getColor(R.color.color_333));
                        tvBindSuccess.setTextColor(getResources().getColor(R.color.color_333));

                        ivSendWifiInfo.clearAnimation();//開始动画
                        ivSetSuccess.clearAnimation();
                        ivBindSuccess.startAnimation(animation);

                        circleProgressBar2.setValue(80);
                        break;

                }

            }
        });
    }

    private void onSuccess() {
        LogUtils.e("--kaadas--开始往服务器发送绑定命令");
//        WifiUtils.getInstance(MyApplication.getInstance()).disableWiFi();
        mPresenter.bindDevice(wifiSn, wifiSn, MyApplication.getInstance().getUid(),randomCode, sSsid, func);

            runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeState(3);
            }
        });
    }


    @Override
    public void onBindSuccess(String wifiSn) {
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        Toast.makeText(this, R.string.bind_failed+"--1", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAddNewBindFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed+"--2", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAddNewBindFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onUpdateSuccess() {
        onSuccess();
//        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
//        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
//        startActivity(intent);
    }

    @Override
    public void onUpdateFailed() {
//        Toast.makeText(this, R.string.bind_failed+"--3", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAddNewBindFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {
//        Toast.makeText(this, R.string.bind_failed+"--4", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, WifiLockAddNewBindFailedActivity.class);
//        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
//        startActivity(intent);
    }

    @Override
    public void onSendSuccess(int index){
        changeState(index);
    }

    @Override
    public void onSendFailed() {
//        Toast.makeText(this, R.string.bind_failed+"--5", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, WifiLockAddNewBindFailedActivity.class);
//        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
//        startActivity(intent);
    }

    @Override
    public void onReceiverFailed() {
//        Toast.makeText(this, R.string.bind_failed+"--6", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, WifiLockAddNewBindFailedActivity.class);
//        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
//        startActivity(intent);
    }

    @Override
    public void onReceiverSuccess() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }

    private void showWarring() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                this
                , "确定重新开始配网吗？",
                "取消", "确定", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //退出当前界面
                        Intent intent = new Intent(WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.this, WifiLockAddNewFirstActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        showWarring();
    }
}
