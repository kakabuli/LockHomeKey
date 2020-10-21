package com.kaadas.lock.activity.device.wifilock.videolock;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.WiFiLockDetailActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockRealTimeActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockRealTimeVideoPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoCallingPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockRealTimeVideoView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoCallingView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BitmapUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.widget.AVLoadingIndicatorView;
import com.kaadas.lock.widget.MySurfaceView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;
import com.yun.software.kaadas.Utils.FileTool;
import com.yuv.display.MyBitmapFactory;

import org.linphone.mediastream.Log;

import java.io.DataInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import la.xiong.androidquick.tool.SizeUtils;
import la.xiong.androidquick.tool.ToastUtil;

public class WifiLockVideoCallingActivity extends BaseActivity<IWifiLockVideoCallingView,
        WifiLockVideoCallingPresenter<IWifiLockVideoCallingView>> implements IWifiLockVideoCallingView{

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
    RelativeLayout llyRecord;
    @BindView(R.id.iv_screenshot_bitmap)
    ImageView ivScreenshotBitmap;
    @BindView(R.id.iv_record_spot)
    ImageView ivRecordSpot;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_real_time_refuse_icon)
    ImageView ivRealTimeRefuseIcon;
    @BindView(R.id.rl_real_time)
    RelativeLayout rlRealTime;
    @BindView(R.id.rl_calling_time)
    RelativeLayout rlCallingTime;
    @BindView(R.id.iv_head_pic)
    ImageView ivHeadPic;
    @BindView(R.id.iv_big_head_pic)
    ImageView ivBigHeadPic;
    @BindView(R.id.tv_head_pic)
    TextView tvHeadPic;
    @BindView(R.id.tv_big_head_pic)
    TextView tvBigHeadPic;
    @BindView(R.id.tv_video_timestamp)
    TextView tvVideoTimeStamp;

    private Dialog dialog;
    private Dialog openDialog;

    private int cnt = 0;
    private Timer timer = null;
    private TimerTask timerTask = null;

    private boolean isConnect = false;
    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private boolean isPasswordShow = false;

    private int isCalling = 0;

    private InnerRecevier mInnerRecevier = null;

    private boolean isMute = false;

    private boolean isShowAudio = false;

    //门铃调用1次
    private boolean isDoorbelling = false;

    final RxPermissions rxPermissions = new RxPermissions(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_calling);

        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);

        isCalling = getIntent().getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_CALLING,0);
        if(isCalling == 0){
            rlCallingTime.setVisibility(View.GONE);
            rlRealTime.setVisibility(View.GONE);
            tvBigHeadPic.setVisibility(View.VISIBLE);
            ivBigHeadPic.setVisibility(View.VISIBLE);
            ivHeadPic.setVisibility(View.GONE);
            tvHeadPic.setVisibility(View.GONE);
            isDoorbelling = false;
        }else if(isCalling == 1){
            rlCallingTime.setVisibility(View.VISIBLE);
            rlRealTime.setVisibility(View.GONE);
            tvBigHeadPic.setVisibility(View.GONE);
            ivBigHeadPic.setVisibility(View.GONE);
            ivHeadPic.setVisibility(View.VISIBLE);
            tvHeadPic.setVisibility(View.VISIBLE);
            isDoorbelling = true;
        }

        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if(wifiLockInfo == null){
            LogUtils.e("shulan wifiLockInfo = searchVideoLock(wifiSn)");
            wifiLockInfo = searchVideoLock(wifiSn);
        }
        if (wifiLockInfo != null){
            mPresenter.settingDevice(wifiLockInfo);
            ivBigHeadPic.setImageResource(BleLockUtils.getDetailImageByModel(wifiLockInfo.getProductModel()));
            ivHeadPic.setImageResource(BleLockUtils.getDetailImageByModel(wifiLockInfo.getProductModel()));
            String lockNickname = wifiLockInfo.getLockNickname();
            tvHeadPic.setText(TextUtils.isEmpty(lockNickname) ? wifiLockInfo.getWifiSN() : lockNickname);
            tvBigHeadPic.setText(TextUtils.isEmpty(lockNickname) ? wifiLockInfo.getWifiSN() : lockNickname);
        }

        rlVideoLayout.setVisibility(View.GONE);
        rlMarkLayout.setVisibility(View.VISIBLE);
        llyRecord.setVisibility(View.GONE);
        avi.show();

        llyTemporaryPassword.setVisibility(View.GONE);
        tvTemporaryPassword.setText("");
    }

    @Override
    protected WifiLockVideoCallingPresenter<IWifiLockVideoCallingView> createPresent() {
        return new WifiLockVideoCallingPresenter<>();
    }

    @OnClick({R.id.back,R.id.mark_back,R.id.iv_answer_icon, R.id.iv_refuse_icon,R.id.iv_setting,
            R.id.iv_mute,R.id.iv_screenshot,R.id.iv_calling,R.id.iv_recoring,R.id.iv_album,R.id.iv_temporary_pwd,R.id.iv_real_time_refuse_icon})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.mark_back:
            case R.id.back:
//                mPresenter.release();
                finish();
                break;
            case R.id.iv_real_time_refuse_icon:
            case R.id.iv_refuse_icon:
                mPresenter.stopConnect();
                avi.hide();
                tvTips.setVisibility(View.GONE);
                finish();
                break;
            case R.id.iv_answer_icon:
                LogUtils.e("shulan iv_answer_icon--"+isConnect);
                if(this.isConnect){
                    rlVideoLayout.setVisibility(View.VISIBLE);
                    rlMarkLayout.setVisibility(View.GONE);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(isShowAudio && mPresenter.startAudioStream() >= 0) {
                                if (!mPresenter.isEnableAudio()) {
                                    LogUtils.e("shulan enableAudio --> true");
                                    mPresenter.enableAudio(true);
                                    isMute = false;
                                    isShowAudio = false;
                                }
                            }
                        }
                    }).start();
                }
                break;
            case R.id.iv_mute:
                if(!isMute){
                    LogUtils.e("shulan 111+isMute" + isMute);
                    if(mPresenter.stopAudioStream() >= 0){
                        if(mPresenter.isEnableAudio()){
                            mPresenter.enableAudio(false);
                            isMute = true;
                            ivMute.setImageResource(R.mipmap.real_time_video_mute_seleted);

                        }
                    }

                }else{
                    LogUtils.e("shulan 222+isMute" + isMute);
                    if(mPresenter.startAudioStream() >=0){
                        if(!mPresenter.isEnableAudio()){
                            mPresenter.enableAudio(true);
                            isMute = false;
                            ivMute.setImageResource(R.mipmap.real_time_video_mute);
                        }
                    }

                }
                break;
            case R.id.iv_setting:
                Intent settingIntent = new Intent(WifiLockVideoCallingActivity.this, WifiLockRealTimeActivity.class);
                settingIntent.putExtra(KeyConstants.WIFI_SN,wifiSn);
                startActivity(settingIntent);
                mPresenter.release();
                break;
            case R.id.iv_album:
                Intent intent = new Intent(WifiLockVideoCallingActivity.this,WifiLockVideoAlbumActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
                startActivity(intent);
                mPresenter.release();
                break;
            case R.id.iv_recoring:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    ToastUtil.showShort("请先获取读写权限");
                }else{
                   /* // 勾选了不再提示
                    if (!ActivityCompat.shouldShowRequestPermissionRationale
                            (this,Manifest.permission.WRITE_EXTERNAL_STORAGE ) &&
                            !ActivityCompat.shouldShowRequestPermissionRationale
                            (this,Manifest.permission.READ_EXTERNAL_STORAGE )) {
                        // ...
                        ToastUtil.showShort("请先获取读写权限");
                    }else{*/
                        if(!ivRecoring.isSelected()){
                            ivRecoring.setSelected(true);
                            llyRecord.setVisibility(View.VISIBLE);
                            if(wifiLockInfo != null){
                                String filePath = FileTool.getVideoLockPath(this,wifiLockInfo.getWifiSN()).getPath() + File.separator +System.currentTimeMillis()+".mp4"  ;
                                mPresenter.startRecordMP4(filePath);
                            }

                        }else{
                            ivRecoring.setSelected(false);
                            llyRecord.setVisibility(View.GONE);
                            mPresenter.stopRecordMP4();
                        }
                        llyRecord.setVisibility(View.VISIBLE);
//                    }

                }
                break;
            case R.id.iv_calling:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    ToastUtil.showShort("请先获取麦克风权限");
                }else{
                    // 勾选了不再提示
                    if (! ActivityCompat.shouldShowRequestPermissionRationale
                            (this,Manifest.permission.RECORD_AUDIO )) {
                        // ...
                        ToastUtil.showShort("请先获取麦克风权限");
                    }else{
                        if(mPresenter.isTalkback()){
                            ivCalling.setSelected(false);
                            mPresenter.talkback(false);
                            mPresenter.stopTalkback();
                        }else{
                            ivCalling.setSelected(true);
                            mPresenter.talkback(true);
                            mPresenter.startTalkback();
                        }
                    }

                }

                break;
            case R.id.iv_screenshot:
                LogUtils.e("shulan iv_screenshot");
                mPresenter.snapImage();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.connectP2P();
            }
        }).start();
//        mPresenter.startRealTimeVideo(mSufaceView);
        ivMute.setImageResource(R.mipmap.real_time_video_mute);
        isShowAudio = true;
        registerBroadcast();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    public void finish() {
        if(getIntent().getBooleanExtra("VIDEO_CALLING_IS_MAINACTIVITY",false)){
            startActivity(new Intent(this, MainActivity.class));
        }
        super.finish();
        LogUtils.e(this + "finish-----------");
        isCalling = 0;
        mPresenter.release();
    }

    @Override
    public void onConnectFailed(int paramInt) {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                if(!WifiLockVideoCallingActivity.this.isFinishing()){
                    if(avi != null){
                        avi.hide();
                    }
                    if(tvTips != null)
                        tvTips.setVisibility(View.GONE);
                    /*if(ivAnswerIcon != null)
                        ivAnswerIcon.setVisibility(View.GONE);
                    if(ivRefuseIcon != null)
                        ivRefuseIcon.setVisibility(View.GONE);
                    if(tvAnswer != null)
                        tvAnswer.setVisibility(View.GONE);
                    if(tvRefuse != null)
                        tvRefuse.setVisibility(View.GONE);*/
                    isConnect = false;
                    LogUtils.e(this + "");
                    if(paramInt == -3){
                        creteDialog("视频连接超时，请稍后再试");
                    }else{
                        creteDialog("网络异常，视频无法连接");
                    }
                }

            }
        });


    }

    @Override
    public void onConnectSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isCalling == 1){
                    avi.hide();
                    tvTips.setVisibility(View.GONE);

                }
            }
        });
        mPresenter.startRealTimeVideo(mSufaceView);
        this.isConnect = true;
        LogUtils.e("shulan onConnectSuccess-isConnect->" + isConnect);
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
                Bitmap bitmap = MyBitmapFactory.createMyBitmap(ints, width, height);
                LogUtils.e("shulan bitmap-->" + bitmap);
                Bitmap myBitmap = BitmapUtil.rotaingImageView(90,bitmap);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Glide.with(WifiLockVideoCallingActivity.this).load(myBitmap).into(ivScreenshotBitmap);
                        ivScreenshotBitmap.setVisibility(View.VISIBLE);
                    }
                });

                mPresenter.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivScreenshotBitmap.setVisibility(View.GONE);
                    }
                },3000);

                LogUtils.e("shulan path-->" + FileTool.getVideoLockPath(this,wifiLockInfo.getWifiSN()).getPath());

                String fileName = System.currentTimeMillis()+".png";
                BitmapUtil.save(FileTool.getVideoLockPath(this,wifiLockInfo.getWifiSN()).getPath() ,fileName,myBitmap);

                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(WifiLockVideoCallingActivity.this.getContentResolver(),
                            FileTool.getVideoLockPath(this,wifiLockInfo.getWifiSN()).getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
                // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                WifiLockVideoCallingActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(FileTool.getVideoLockPath(this,wifiLockInfo.getWifiSN()))));
            }

        }

    }

    @Override
    public void onstartRecordMP4CallBack() {
        runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  tvTime.setText("00:00:00");
                  ivRecordSpot.setVisibility(View.VISIBLE);
                  startTimer();

              }
          });


    }

    //开始视频回调，时间戳数据...
    @Override
    public void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvVideoTimeStamp.setText(DateUtils.getDateTimeFromMillisecond(paramAVStreamHeader.m_TimeStamp));
                if(isCalling == 0){
                    avi.hide();
                    tvTips.setVisibility(View.GONE);
                    rlVideoLayout.setVisibility(View.VISIBLE);
                    rlMarkLayout.setVisibility(View.GONE);

                }
            }
        });
        if(isCalling == 0){
            if(isShowAudio && mPresenter.startAudioStream() >= 0) {
                if (!mPresenter.isEnableAudio()) {
                    LogUtils.e("shulan enableAudio --> true");
                    mPresenter.enableAudio(true);
                    isMute = false;
                    isShowAudio = false;
                }
            }
        }

    }

    @Override
    public void recordAudidFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort("请先获取麦克风权限");
            }
        });
    }

    @Override
    public void openDoor(WifiLockOperationBean.EventparamsBean eventparams) {
        if(!WifiLockVideoCallingActivity.this.isFinishing()){
            if(eventparams.getEventCode() == 2){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openDialog();
                    }
                });
            }
        }
    }

    private void openDialog() {
        if(openDialog == null){
            openDialog = new Dialog(this, R.style.MyDialog);
        }
        // 获取Dialog布局
        View mView = LayoutInflater.from(this).inflate(R.layout.dialog_title_two_button_dialog, null);
   /*     tvTitle = mView.findViewById(R.id.tv_hint);
        tvTitle.setVisibility(View.GONE);*/
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView title = mView.findViewById(R.id.title);
        title.setText("温馨提示");
        tvContent.setText("门锁已打开，是否继续观看?");
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        tv_cancel.setText("继续播放");
        tv_cancel.setTextColor(Color.parseColor("#9A9A9A"));
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tv_query.setTextColor(Color.parseColor("#2096F8"));
        tv_query.setText("关闭");
        openDialog.setContentView(mView);

        Window window = openDialog.getWindow();
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        params.width = (int) (width * 0.8);
        window.setAttributes(params);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
                finish();
            }
        });
//        LogUtils.e("shulan -----+++++");
        if(!WifiLockVideoCallingActivity.this.isFinishing()){
            openDialog.show();
        }
    }

    @Override
    public void onStopRecordMP4CallBack(MP4Info mp4Info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivRecordSpot.setAnimation(null);
                stopTimer();
                // 最后通知图库更新
                if(mp4Info.isResult()){
                    WifiLockVideoCallingActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mp4Info.getFilePath()))));
                    mPresenter.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llyRecord.setVisibility(View.GONE);
                        }
                    },300);
                }
            }
        });
    }

    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        cnt = 0;
    }

    private void startTimer(){
        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTime.setText(DateUtils.getStringTime(cnt++));
                            ivRecordSpot.setVisibility(View.INVISIBLE);
                            mPresenter.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivRecordSpot.setVisibility(View.VISIBLE);
                                }
                            },500);
                        }
                    });
                }
            };
        }

        if(timer != null && timerTask != null )
            timer.schedule(timerTask,0,1000);

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



    public void creteDialog(String content){
        if(dialog == null){
            dialog = new Dialog(this, R.style.MyDialog);
        }
        // 获取Dialog布局
        View mView = LayoutInflater.from(this).inflate(R.layout.no_et_title_two_button_dialog, null);
   /*     tvTitle = mView.findViewById(R.id.tv_hint);
        tvTitle.setVisibility(View.GONE);*/
        TextView tvContent = mView.findViewById(R.id.tv_content);
        tvContent.setText(content + "");
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        tv_cancel.setText("关闭");
        tv_cancel.setTextColor(Color.parseColor("#9A9A9A"));
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tv_query.setTextColor(Color.parseColor("#2096F8"));
        tv_query.setText("重新连接");
        dialog.setContentView(mView);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        params.width = (int) (width * 0.8);
        window.setAttributes(params);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avi.setVisibility(View.VISIBLE);
                avi.show();
                tvTips.setVisibility(View.VISIBLE);
                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.connectP2P();
                    }
                }).start();
            }
        });
//        LogUtils.e("shulan -----+++++");
        if(!WifiLockVideoCallingActivity.this.isFinishing()){
            dialog.show();
        }

    }

    private void registerBroadcast(){
        if(mInnerRecevier == null){
            mInnerRecevier = new InnerRecevier();
        }
        IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mInnerRecevier, homeFilter);
    }

    private void unRegisterBroadcast(){
        if(mInnerRecevier != null){
            unregisterReceiver(mInnerRecevier);
        }
    }

    private class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // home键
                        LogUtils.e("shulan --home");
                        mPresenter.release();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //多任务
                        LogUtils.e("shulan --recent");
                        mPresenter.release();
                    }
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
                LogUtils.e("shulan -- screen_on");
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                LogUtils.e("shulan -- screen_off");
                mPresenter.release();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){// 解锁
                LogUtils.e("shulan -- 解锁");

            }

            }
        }

    private WifiLockInfo searchVideoLock(String wifiSN){
        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
        if (daoSession != null && daoSession.getCatEyeServiceInfoDao() != null) {
            List<WifiLockInfo> wifiLockInfos = daoSession.getWifiLockInfoDao().loadAll();
            if (wifiLockInfos != null && wifiLockInfos.size() > 0) {
                for (WifiLockInfo wifiLockInfo : wifiLockInfos) {
                    if(wifiLockInfo.getWifiSN().equals(wifiSN)){
                        return wifiLockInfo;
                    }
                }
            }
        }
        return null;
    }
}
