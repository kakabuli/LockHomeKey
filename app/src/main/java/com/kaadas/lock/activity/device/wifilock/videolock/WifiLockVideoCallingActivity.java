package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockRealTimeActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockRealTimeVideoPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoCallingPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockRealTimeVideoView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoCallingView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BitmapUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.widget.AVLoadingIndicatorView;
import com.kaadas.lock.widget.MySurfaceView;
import com.yuv.display.MyBitmapFactory;

import org.linphone.mediastream.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import la.xiong.androidquick.tool.SizeUtils;

public class WifiLockVideoCallingActivity extends BaseActivity<IWifiLockVideoCallingView,
        WifiLockVideoCallingPresenter<IWifiLockVideoCallingView>> implements IWifiLockVideoCallingView ,SurfaceHolder.Callback{

    @BindView(R.id.iv_answer_icon)
    ImageView ivAnswerIcon;
    @BindView(R.id.iv_refuse_icon)
    ImageView ivRefuseIcon;
    @BindView(R.id.tv_answer)
    TextView tvAnswer;
    @BindView(R.id.tv_refuse)
    TextView tvRefuse;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;


    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.surface_view)
    SurfaceView mSufaceView;
    @BindView(R.id.tv_temporary_password)
    TextView tvTemporaryPassword;
    @BindView(R.id.lly_temporary_password)
    LinearLayout llyTemporaryPassword;

    @BindView(R.id.rl_video_layout)
    RelativeLayout rlVideoLayout;
    @BindView(R.id.rl_mark_layout)
    RelativeLayout rlMarkLayout;
    @BindView(R.id.iv_screenshot)
    ImageView ivScreenshot;
    @BindView(R.id.iv_mute)
    ImageView ivMute;
    @BindView(R.id.iv_calling)
    ImageView ivCalling;
    @BindView(R.id.iv_recoring)
    ImageView ivRecoring;
    @BindView(R.id.iv_album)
    ImageView ivAlbum;
    @BindView(R.id.lly_record)
    LinearLayout llyRecord;


    private boolean isConnect = false;
    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private boolean isPasswordShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_calling);

        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if (wifiLockInfo != null) mPresenter.settingDevice(wifiLockInfo);

        rlVideoLayout.setVisibility(View.GONE);
        rlMarkLayout.setVisibility(View.VISIBLE);
        llyRecord.setVisibility(View.GONE);
        avi.show();

        llyTemporaryPassword.setVisibility(View.GONE);
        tvTemporaryPassword.setText("");
       /* if(wifiLockInfo != null){
            mPresenter.connectP2P(wifiLockInfo.getDevice_did(),wifiLockInfo.getP2p_password(),wifiLockInfo.getDevice_sn());
        }*/

//        SurfaceHolder holder = mSufaceView.getHolder();
//        holder.addCallback(this);
    }

    @Override
    protected WifiLockVideoCallingPresenter<IWifiLockVideoCallingView> createPresent() {
        return new WifiLockVideoCallingPresenter<>();
    }

    @OnClick({R.id.back,R.id.iv_answer_icon, R.id.iv_refuse_icon,R.id.iv_setting,R.id.iv_mute,R.id.iv_screenshot,R.id.iv_calling,R.id.iv_recoring,R.id.iv_album,R.id.iv_temporary_pwd})
    public void onViewClicked(View view) {
        int ret = -999;
        switch (view.getId()){
            case R.id.back:
                mPresenter.release();
                finish();
                break;
            case R.id.iv_refuse_icon:
                mPresenter.stopConnect();
                avi.hide();
                tvTips.setVisibility(View.GONE);
                finish();
                break;
            case R.id.iv_answer_icon:
                if(isConnect){
//                    startActivity(new Intent(WifiLockVideoCallingActivity.this,WifiLockRealTimeVideoActivity.class));
//                    finish();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.startRealTimeVideo(mSufaceView);
                        }
                    }).start();

                    rlVideoLayout.setVisibility(View.VISIBLE);
                    rlMarkLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_mute:
                ret= XMP2PManager.getInstance().startAudioStream();
                if(ret>=0){
                    if(!XMP2PManager.getInstance().isEnableAudio()){
                        XMP2PManager.getInstance().enableAudio(true);
                    }
                }
                break;
            case R.id.iv_setting:
                startActivity(new Intent(WifiLockVideoCallingActivity.this, WifiLockRealTimeActivity.class));
                break;
            case R.id.iv_album:
                break;
            case R.id.iv_recoring:
                llyRecord.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_calling:
                break;
            case R.id.iv_screenshot:
                LogUtils.e("shulan iv_screenshot");
                XMP2PManager.getInstance().snapImage();
                break;
            case R.id.iv_temporary_pwd:
                if(!isPasswordShow){
                    tvTemporaryPassword.setText(getPassword() + "");
                    llyTemporaryPassword.setVisibility(View.VISIBLE);
                    isPasswordShow = true;
                }else{
                    isPasswordShow = false;
                    llyTemporaryPassword.setVisibility(View.GONE);
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        mPresenter.startRealTimeVideo(mSufaceView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    @Override
    public void onConnectFailed(int paramInt) {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                avi.hide();
                tvTips.setVisibility(View.GONE);
                ivAnswerIcon.setVisibility(View.GONE);
                ivRefuseIcon.setVisibility(View.GONE);
                tvAnswer.setVisibility(View.GONE);
                tvRefuse.setVisibility(View.GONE);
                if(paramInt == -3){
                    showFailedDialog("视频连接超时，请稍后再试","确定");
                }else{
                    showFailedDialog("网络异常，视频无法连接","取消");
                }
            }
        });


    }

    private void showFailedDialog(String content,String right) {
        AlertDialogUtil.getInstance().noEditTitleOneButtonDialog(WifiLockVideoCallingActivity.this, content, right,
                "#1F96F7", new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
                finish();
            }

            @Override
            public void right() {
                finish();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(String toString) {

            }
        });
    }

    @Override
    public void onConnectSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                avi.hide();
                tvTips.setVisibility(View.GONE);
//                rlVideoLayout.setVisibility(View.VISIBLE);
//                rlMarkLayout.setVisibility(View.GONE);
            }
        });
        this.isConnect = true;
    }

    @Override
    public void onStartConnect(String paramString) {
        LogUtils.e("shulan" + "onStartConnect: paramString-->" + paramString);
    }

    @Override
    public void onErrorMessage(String message) {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                avi.hide();
                tvTips.setVisibility(View.GONE);
            }
        });*/
        LogUtils.e("shulan" + "onErrorMessage: message-->" + message);
    }

    @Override
    public void onLastFrameRgbData(int[] ints, int width, int height, boolean b) {
        LogUtils.e("shulan ints="+ints,"--height=" +height + "--width=" + width +"--b=" +b);
        if(ints != null ){
            if(!b){
                Bitmap myBitmap = MyBitmapFactory.createMyBitmap(ints, width, height);
                LogUtils.e("shulan myBitmap-->" + myBitmap);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera" ;
                LogUtils.e("shulan path-->" + path);
                String fileName = System.currentTimeMillis()+".png";
                BitmapUtil.save(path ,fileName,myBitmap);

                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(WifiLockVideoCallingActivity.this.getContentResolver(),
                            new File(path).getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
                // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                WifiLockVideoCallingActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
            }

        }

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

    private Canvas mCanvas;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
/*        mCanvas = holder.lockCanvas();
        Matrix m = new Matrix();
        m.postRotate(90);
        mCanvas.drawBitmap();*/
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
