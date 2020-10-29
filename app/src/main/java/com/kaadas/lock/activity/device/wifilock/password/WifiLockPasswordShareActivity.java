package com.kaadas.lock.activity.device.wifilock.password;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SharedUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockPasswordShareActivity extends AppCompatActivity {

    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.tv_password)
    TextView tvPassword;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private String password;
    private WifiLockInfo wifiLockInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_password_share);
        ButterKnife.bind(this);
        String pwd = "";
        String wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        tvPassword.setText(pwd);
        tvTime.setText(" " + DateUtils.getStrFromMillisecond2(System.currentTimeMillis()));
        initPassword();
    }

    public static void main(String[] args) {

    }

    public void initPassword() {
        password = getPassword();

        String temp = "";
        if (!TextUtils.isEmpty(password)) {
            for (int length = 0; length < password.length(); length++) {
                temp = temp + " " + password.charAt(length) + " ";
            }
        }
        tvPassword.setText(temp);
        MyLog.getInstance().save("--kaadas调试--UI显示的临时密码==" + temp);

    }

    private String getPassword() {
        if (wifiLockInfo != null) {
            if (!TextUtils.isEmpty(wifiLockInfo.getWifiSN())) {

                String wifiSN = wifiLockInfo.getWifiSN();
                LogUtils.e("shulan wifiSN-->" + wifiSN);
                String randomCode = wifiLockInfo.getRandomCode();
                LogUtils.e("shulan randomCode-->" + randomCode);
                String time = (System.currentTimeMillis() / 1000 / 60 / 5) + "";
                LogUtils.e("--kaadas--wifiSN-  " + wifiSN);
                LogUtils.e("shulan time-->  " + time);
                MyLog.getInstance().save("--kaadas调试--wifiSN  " + wifiSN);
                MyLog.getInstance().save("--kaadas调试--randomCode  " + randomCode);
                MyLog.getInstance().save("--kaadas调试--System.currentTimeMillis()  " + System.currentTimeMillis());

                String content = wifiSN + randomCode + time;
                LogUtils.e("--kaadas--服务器获取的数据是  " + randomCode);

                LogUtils.e("--kaadas--本地数据是  " + content);
                byte[] data = content.toUpperCase().getBytes();
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(data);
                    byte[] digest = messageDigest.digest();
                    byte[] temp = new byte[4];
                    System.arraycopy(digest, 0, temp, 0, 4);
                    long l = Rsa.getInt(temp);
                    String text = (l % 1000000) + "";
                    LogUtils.e("--kaadas--转换之后的数据是     " + l + "    " + Rsa.bytes2Int(temp));
                    int offSet = (6 - text.length());
                    for (int i = 0; i < offSet; i++) {
                        text = "0" + text;
                    }
                    System.out.println("--kaadas--   testSha256 数据是   " + Rsa.bytesToHexString(messageDigest.digest()));
                    return text;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private String getVideoPassword() {
        if (wifiLockInfo != null) {
            if (!TextUtils.isEmpty(wifiLockInfo.getWifiSN())) {

                String wifiSN = wifiLockInfo.getWifiSN();
                String randomCode = wifiLockInfo.getRandomCode();
                randomCode = randomCode.substring(0,64) + randomCode.substring(randomCode.length() - 2);;
                String time = (System.currentTimeMillis() / 1000 / 60 / 5) + "";
                LogUtils.e("--kaadas--wifiSN-  " + wifiSN);
                MyLog.getInstance().save("--kaadas调试--wifiSN  " + wifiSN);
                MyLog.getInstance().save("--kaadas调试--randomCode  " + randomCode);
                MyLog.getInstance().save("--kaadas调试--System.currentTimeMillis()  " + System.currentTimeMillis());

                String content = wifiSN + randomCode + time;
                LogUtils.e("--kaadas--服务器获取的数据是  " + randomCode);

                LogUtils.e("--kaadas--本地数据是  " + content);
                byte[] data = content.toUpperCase().getBytes();
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(data);
                    byte[] digest = messageDigest.digest();
                    byte[] temp = new byte[4];
                    System.arraycopy(digest, 0, temp, 0, 4);
                    long l = Rsa.getInt(temp);
                    String text = (l % 1000000) + "";
                    LogUtils.e("--kaadas--转换之后的数据是     " + l + "    " + Rsa.bytes2Int(temp));
                    int offSet = (6 - text.length());
                    for (int i = 0; i < offSet; i++) {
                        text = "0" + text;
                    }
                    System.out.println("--kaadas--   testSha256 数据是   " + Rsa.bytesToHexString(messageDigest.digest()));
                    return text;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    @OnClick({R.id.back, R.id.tv_short_message, R.id.tv_wei_xin, R.id.tv_copy})
    public void onClick(View view) {
        String message = String.format(getString(R.string.share_content), password, tvNotice.getText().toString().trim());
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_short_message:
                SharedUtil.getInstance().sendShortMessage(message, this);
                break;
            case R.id.tv_wei_xin:
                if (SharedUtil.isWeixinAvilible(this)) {
                    SharedUtil.getInstance().sendWeiXin(message);
                } else {
                    ToastUtil.getInstance().showShort(R.string.telephone_not_install_wechat);
                }
                break;
            case R.id.tv_copy:
                SharedUtil.getInstance().copyTextToSystem(this, message);
                break;
        }
    }
}
