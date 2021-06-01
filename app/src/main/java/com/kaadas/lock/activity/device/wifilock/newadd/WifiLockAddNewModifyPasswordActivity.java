package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.utils.WifiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewModifyPasswordActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;

    private int times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_modify_password);
        ButterKnife.bind(this);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES,1);
        thread.start();
    }


    SocketManager socketManager = SocketManager.getInstance();
    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            try {
                ret =socketManager.writeData("PinSimple".getBytes());
                LogUtils.e("写入状态  PinSimple  "+ret);
                Thread.sleep(89 * 1000);
                ret = socketManager.writeData("APError".getBytes());
                LogUtils.e("写入状态  APError1  "+ret);
                Thread.sleep(89 * 1000);
                ret = socketManager.writeData("APError".getBytes());
                LogUtils.e("写入状态  APError2  "+ret);
                Thread.sleep(89 * 1000);
                ret = socketManager.writeData("APError".getBytes());
                LogUtils.e("写入状态   APError3 "+ret);
                Thread.sleep(89 * 1000);
                ret = socketManager.writeData("ApClose".getBytes());
                LogUtils.e("写入状态  APError4·  "+ret);
                Thread.sleep(1000);
                socketManager.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @OnClick({R.id.back, R.id.help, R.id.continue_check})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this,WifiLockHelpActivity.class));
                break;
            case R.id.continue_check:
                String wifiName = NetUtil.getWifiName();
                LogUtils.e("连接状态  wifiName   " +wifiName+"  isConnected " +SocketManager.getInstance().isConnected());
                if (!(!TextUtils.isEmpty(wifiName ) && wifiName.contains("kaadas_AP")) || !SocketManager.getInstance().isConnected()){
                    Toast.makeText(this, "连接断开，请重新开始", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,WifiLockAddNewModifyPasswordDisconnectActivity.class));
                    socketManager.destroy();
                    return;
                }
                Intent intent = new Intent(this, WifiLockAddNewInputAdminPasswotdActivity.class);
                LogUtils.e(getLocalClassName()+"次数是   " + times + "  data 是否为空 "  );
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, times);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            showWarring();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }


    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                this
                , getString(R.string.activity_wifi_video_fifth_network),
                getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //退出当前界面
                        Intent intent = new Intent(WifiLockAddNewModifyPasswordActivity.this, WifiLockAddNewFirstActivity.class);
                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                        startActivity(intent);

                        finish();
                        socketManager.destroy();
                        thread.interrupt();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }
}
