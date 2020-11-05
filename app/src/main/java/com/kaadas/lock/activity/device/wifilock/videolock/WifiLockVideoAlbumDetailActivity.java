package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.FileItemBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.MyAlbumPlayerPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoFifthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IMyAlbumPlayerView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAddSuccessView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.MediaPlayerWrapper;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.MediaStatus;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.StatusHelper;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.FileUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.RotateTransformation;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.AVLoadingIndicatorView;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;
import com.yun.software.kaadas.Utils.FileTool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockVideoAlbumDetailActivity extends BaseActivity<IMyAlbumPlayerView, MyAlbumPlayerPresenter<IMyAlbumPlayerView>>  implements IMyAlbumPlayerView{

    private MediaPlayerWrapper mediaPlayer;
    private String filepath;
    private StatusHelper statusHelper;

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.duration_seek_bar)
    SeekBar durationSeekBar;
    @BindView(R.id.video_surface)
    SurfaceView surfaceView;
    @BindView(R.id.video_surface_1)
    SurfaceView surfaceView1;
    @BindView(R.id.lly_bottom_bar)
    LinearLayout llyBootomBar;
    @BindView(R.id.iv_play_start)
    ImageView ivPlayStart;
    @BindView(R.id.iv_pause)
    ImageView ivPause;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.iv_cache)
    ImageView ivCache;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;
    private WifiVideoLockAlarmRecord record;

    private String path;

    private Dialog dialog;

    private boolean isPlay = false;

    private InnerRecevier mInnerRecevier = null;

    private boolean isRecordSuccess = false;

    private long currentTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            timerHandler.postDelayed(timerRunnable, 100);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_album_detail);
        ButterKnife.bind(this);
        filepath = getIntent().getStringExtra(KeyConstants.VIDEO_PIC_PATH);
        String name = getIntent().getStringExtra("NAME");

        tvName.setText(name);
        // initDate
        statusHelper = new StatusHelper(this);
        mediaPlayer = new MediaPlayerWrapper();
        mediaPlayer.setStatusHelper(statusHelper);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.setSurfaceHolder(holder);
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

        if(new File(filepath).exists()){
            avi.setVisibility(View.GONE);
            avi.hide();
            tvTips.setVisibility(View.GONE);
            // initData
            surfaceView1.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
            playOperation();
            isPlay = false;
            ivCache.setVisibility(View.GONE);
            isRecordSuccess = true;
        }else{
            record = (WifiVideoLockAlarmRecord) getIntent().getSerializableExtra("record");
            wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
            path = FileTool.getVideoCacheFolder(this,wifiSn).getPath();
            wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

            avi.setVisibility(View.VISIBLE);
            avi.show();
            tvTips.setVisibility(View.VISIBLE);
            if(record.getThumbUrl()!=null && !record.getThumbUrl().isEmpty()){
                Glide.with(this).load(record.getThumbUrl())
                        .apply(new RequestOptions().dontAnimate()
                                .transform(new RotateTransformation(90f))).into(ivCache);
            }else{
                Glide.with(this).load(R.mipmap.img_video_lock_default).into(ivCache);
            }
            surfaceView1.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.GONE);
            isPlay = true;
            isRecordSuccess = false;
            realTimeOperation();
            ivCache.setVisibility(View.VISIBLE);

        }

    }

    private void realTimeOperation() {
        if(wifiLockInfo!=null){
            ivPlayStart.setVisibility(View.GONE);
            llyBootomBar.setVisibility(View.GONE);
            mPresenter.settingDevice(wifiLockInfo);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.connectP2P();
                }
            }).start();
        }
    }

    //播放操作
    private void playOperation() {
        if (filepath == null) {
            Toast.makeText(this, getResources().getString(R.string.no_video), Toast.LENGTH_SHORT).show();
            return;
        }
        mediaPlayer.setMediaPlayerFromUri(filepath);
        statusHelper.setMediaStatus(MediaStatus.START);
        mediaPlayer.prepare();
        initEvent();
        autoPlayVideo();
    }

    private void initEvent() {
        ivPlayStart.setVisibility(View.GONE);
        mediaPlayer.setPlayerCallback(new MediaPlayerWrapper.PlayerCallback() {
            @Override
            public void updateProgress() {

            }

            @Override
            public void updateInfo() {
                initSeekBar();
            }

            @Override
            public void requestFinish() {
                reset();
            }
        });
    }

    //更新进度
    private void autoPlayVideo() {
        timerHandler.post(timerRunnable);
    }

    private void updateProgress() {
        if (statusHelper.getMediaStatus() == MediaStatus.PLAYING && mediaPlayer.getCurrentPosition() > 0) {
            int duration = mediaPlayer.getCurrentPosition();
            durationSeekBar.setProgress(duration);
            updateStartValueView(duration);
        }
    }

    private void updateStartValueView(int progress) {
        Date date = new Date(progress);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        final String formatted = formatter.format(date);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(formatted);
            }
        });
    }

    private void initSeekBar() {
        long videoDuration = mediaPlayer.getDuration();

        if (videoDuration == 0) {
            Toast.makeText(this.getApplicationContext(), "Could not play this video.", Toast.LENGTH_SHORT).show();
            finish();
        }
        durationSeekBar.setProgress(0);
        durationSeekBar.setMax((int) videoDuration);
        Date date = new Date(videoDuration);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String formatted = formatter.format(date);
        tvDuration.setText(formatted);

        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                    }
                    updateStartValueView(progress);
                } else if (progress >= seekBar.getMax()) {
                    reset();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatTimer();
        if (mediaPlayer != null) {
            mediaPlayer.releaseResource();
        }

        unRegisterBroadcast();
    }


    @Override
    public void finish() {
        super.finish();
        mPresenter.release();

        try{
            if(!isRecordSuccess){
                if(!filepath.isEmpty()){
                    if(new File(filepath).exists()){
                        new File(filepath).delete();
                    }
                }
            }
        }catch (Exception e){

        }

    }

    @Override
    protected MyAlbumPlayerPresenter<IMyAlbumPlayerView> createPresent() {
        return new MyAlbumPlayerPresenter<>();
    }

    void startRepeatTimer() {
        timerRunnable.run();
    }

    void stopRepeatTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void reset() {
        ivPlayStart.setVisibility(View.VISIBLE);
//        pause();
        updateStartValueView(0);
        durationSeekBar.setProgress(0);
        updateButtonController();
    }

    private void updateButtonController() {
        final boolean isPlaying = statusHelper.getMediaStatus() == MediaStatus.PLAYING;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivPause.setSelected(!isPlaying);
            }
        });
    }

    public void pause() {
        stopRepeatTimer();
        if (mediaPlayer != null && statusHelper.getMediaStatus() == MediaStatus.PLAYING) {
            mediaPlayer.pauseByUser();
            updateButtonController();
        }
    }

    public void play() {
        startRepeatTimer();
        boolean isPlaying = statusHelper.getMediaStatus() == MediaStatus.PLAYING;
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            updateButtonController();
        }
    }

    @OnClick({R.id.back,R.id.iv_play_start,R.id.iv_pause})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_play_start:
                if (mediaPlayer != null && statusHelper.getMediaStatus() == MediaStatus.PLAYING) {
//                    pause();
                } else {
                    ivPlayStart.setVisibility(View.GONE);
                    if(!isPlay){

                        play();
                    }else{
                        surfaceView.setVisibility(View.VISIBLE);
                        surfaceView1.setVisibility(View.GONE);
                        playOperation();
                        isPlay = false;
                    }
                }
                break;
            case R.id.iv_pause:
                if (mediaPlayer != null && statusHelper.getMediaStatus() == MediaStatus.PLAYING) {
                    ivPlayStart.setVisibility(View.VISIBLE);
                    pause();
                } else {
                    ivPlayStart.setVisibility(View.GONE);
                    if(!isPlay){

                        play();
                    }else{
                        surfaceView.setVisibility(View.VISIBLE);
                        surfaceView1.setVisibility(View.GONE);
                        playOperation();
                        isPlay = false;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectFailed(int paramInt) {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                if(!WifiLockVideoAlbumDetailActivity.this.isFinishing()){
                    if(avi != null){
                        avi.hide();
                    }
                    if(tvTips != null)
                        tvTips.setVisibility(View.GONE);
                    if(paramInt == -3){
                        creteDialog(getString(R.string.video_lock_xm_connect_time_out) + "");
                    }else{
                        creteDialog(getString(R.string.video_lock_xm_connect_failed) + "");
                    }
                }

            }
        });
    }

    @Override
    public void onConnectSuccess() {

        XMP2PManager.getInstance().setSurfaceView(surfaceView1);
        mPresenter.playDeviceRecordVideo(record,path);
    }

    @Override
    public void onStartConnect(String paramString) {

    }

    @Override
    public void onErrorMessage(String message) {

    }

    @Override
    public void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader) {

    }

    @Override
    public void onVideoFrameUsed(H264Frame h264Frame) {
        if(h264Frame.getFrameTimeStamp() == 0){
            isRecordSuccess = true;
            mPresenter.release();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivPlayStart.setVisibility(View.VISIBLE);
                    llyBootomBar.setVisibility(View.VISIBLE);
//                    playOperation();
                }
            });
        }else{
            isRecordSuccess = false;
        }
    }

    @Override
    public void onStopRecordMP4CallBack(MP4Info mp4Info, String name) {
        if(mp4Info.isResult()){
            filepath = mp4Info.getFilePath();

            if(WifiLockVideoAlbumDetailActivity.this.isFinishing() && !isRecordSuccess){
                try {
                    if(!mp4Info.getFilePath().isEmpty()){

                        File file = new File(mp4Info.getFilePath());
                        if(file.exists()){
                            file.delete();
                        }
                    }
                }catch (Exception e){

                }
            }
        }else {

            try {
                if(!mp4Info.getFilePath().isEmpty()){
                    File file = new File(mp4Info.getFilePath());
                    if(file.exists()){
                        file.delete();
                    }
                }
            }catch (Exception e){

            }

        }


    }

    @Override
    public void onstartRecordMP4CallBack() {
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                avi.hide();
                tvTips.setVisibility(View.GONE);
                ivCache.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSuccessRecord(boolean b) {
        mPresenter.release();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.getInstance().showShort("找不到文件");
            }
        });
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
                finish();
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

        if(!WifiLockVideoAlbumDetailActivity.this.isFinishing()){
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

                        try{
                            if(!isRecordSuccess){
                                if(!filepath.isEmpty()){

                                    if(new File(filepath).exists()){
                                        new File(filepath).delete();
                                    }
                                }
                            }
                        }catch (Exception e){

                        }

                        mPresenter.release();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //多任务
                        try{
                            if(!isRecordSuccess){
                                if(!filepath.isEmpty()){

                                    if(new File(filepath).exists()){
                                        new File(filepath).delete();
                                    }
                                }
                            }

                        }catch (Exception e){

                        }
                        mPresenter.release();
                    }
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                try{

                    if(!isRecordSuccess){
                        if(!filepath.isEmpty()){

                            if(new File(filepath).exists()){
                                new File(filepath).delete();
                            }
                        }
                    }
                }catch (Exception e){

                }
                mPresenter.release();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){// 解锁

            }

        }
    }
}
