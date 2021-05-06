package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.MyAlbumPlayerPresenter;
import com.kaadas.lock.mvp.view.wifilock.IMyAlbumPlayerView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.MP4Info;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockDeviceRecordActivity extends BaseActivity<IMyAlbumPlayerView, MyAlbumPlayerPresenter<IMyAlbumPlayerView>>  implements IMyAlbumPlayerView {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.duration_seek_bar)
    SeekBar durationSeekBar;
    @BindView(R.id.video_surface)
    SurfaceView surfaceView;
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

    private Dialog dialog;

    private InnerRecevier mInnerRecevier = null;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;
    private WifiVideoLockAlarmRecord mWifiVideoLockAlarmRecord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_album_detail);
        ButterKnife.bind(this);

        mWifiVideoLockAlarmRecord = (WifiVideoLockAlarmRecord) getIntent().getSerializableExtra(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD_DATA);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if(wifiLockInfo != null){
            mPresenter.settingDevice(wifiLockInfo);
        }
        if(mWifiVideoLockAlarmRecord != null){
            tvName.setText(mWifiVideoLockAlarmRecord.getFileName() + "");
        }

        initSeekBar();


    }

    private void initSeekBar() {
        int videoDuration = mWifiVideoLockAlarmRecord.getVedioTime();

        LogUtils.e("video duration = " + videoDuration);
        if (videoDuration == 0) {
            Toast.makeText(this.getApplicationContext(), "Could not play this video.", Toast.LENGTH_SHORT).show();
            finish();
        }
        durationSeekBar.setProgress(0);
        durationSeekBar.setMax(videoDuration);

        tvDuration.setText(DateUtils.getStringTime1(videoDuration) + "");

        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
        mPresenter.attachView(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.connectP2P();
            }
        }).start();
        registerBroadcast();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
    protected MyAlbumPlayerPresenter<IMyAlbumPlayerView> createPresent() {
        return new MyAlbumPlayerPresenter<>();
    }

    @OnClick({R.id.back,R.id.iv_play_start,R.id.iv_pause})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_play_start:

                break;
            case R.id.iv_pause:

                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        mPresenter.release();
    }

    @Override
    public void onConnectFailed(int paramInt) {
        if(!WifiVideoLockDeviceRecordActivity.this.isFinishing()){
            mPresenter.handler.post(new Runnable() {

                @Override
                public void run() {
                    if(paramInt == -3){
                        creteDialog(getString(R.string.video_lock_xm_connect_time_out_1) + "");
                    }else{
                        creteDialog(getString(R.string.video_lock_xm_connect_failed_1) + "");
                    }
                }
            });

        }
    }

    @Override
    public void onConnectSuccess() {
        /*mPresenter.playDeviceRecordVideo(mWifiVideoLockAlarmRecord.getFileName(),
                mWifiVideoLockAlarmRecord.getFileDate(),surfaceView);*/
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

    }

    @Override
    public void onStopRecordMP4CallBack(MP4Info mp4Info, String name) {

    }

    @Override
    public void onstartRecordMP4CallBack() {

    }

    @Override
    public void onSuccessRecord(boolean b) {

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
        tv_cancel.setText(getString(R.string.close));
        tv_cancel.setTextColor(Color.parseColor("#9A9A9A"));
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tv_query.setTextColor(Color.parseColor("#2096F8"));
        tv_query.setText(getString(R.string.clothes_hanger_add_next));
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


                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.connectP2P();
                    }
                }).start();
            }
        });

        if(!WifiVideoLockDeviceRecordActivity.this.isFinishing()){
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
                        mPresenter.release();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //多任务
                        mPresenter.release();
                    }
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                mPresenter.release();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){// 解锁

            }

        }
    }


}
