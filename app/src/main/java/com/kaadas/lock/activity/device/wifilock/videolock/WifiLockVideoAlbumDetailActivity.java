package com.kaadas.lock.activity.device.wifilock.videolock;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.MyAlbumPlayerPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoFifthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IMyAlbumPlayerView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAddSuccessView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.MediaPlayerWrapper;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.MediaStatus;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.StatusHelper;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockVideoAlbumDetailActivity extends BaseAddToApplicationActivity{

    private MediaPlayerWrapper mediaPlayer;
    private String filepath;
    private StatusHelper statusHelper;

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

        String stringExtra = getIntent().getStringExtra(KeyConstants.VIDEO_PIC_PATH);
        String name = getIntent().getStringExtra("NAME");

        tvName.setText(name);
        // initDate
        statusHelper = new StatusHelper(this);
        mediaPlayer = new MediaPlayerWrapper();
        mediaPlayer.setStatusHelper(statusHelper);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                LogUtils.d("davi surface "+"销毁了");
            }
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                LogUtils.d("davi holder "+holder);
                mediaPlayer.setSurfaceHolder(holder);
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
        // initData
        filepath = stringExtra;
        playOperation();
    }

    //播放操作
    private void playOperation() {
        if (filepath == null) {
            Toast.makeText(this, getResources().getString(R.string.no_video), Toast.LENGTH_SHORT).show();
            return;
        }
        mediaPlayer.setMediaPlayerFromUri(filepath);
        LogUtils.d("davi 走到这 "+Thread.currentThread().getName());
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

        Log.e("howard", "video duration = " + videoDuration);
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
                    //     LogUtil.i("play finish reset");
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
//        mPresenter.attachView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        mPresenter.detachView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatTimer();
        if (mediaPlayer != null) {
            mediaPlayer.releaseResource();
        }
    }

    void startRepeatTimer() {
        timerRunnable.run();
    }

    void stopRepeatTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    /*@Override
    protected MyAlbumPlayerPresenter<IMyAlbumPlayerView> createPresent() {
        return new MyAlbumPlayerPresenter<>();
    }*/

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
                    play();
                }
                break;
            case R.id.iv_pause:
                if (mediaPlayer != null && statusHelper.getMediaStatus() == MediaStatus.PLAYING) {
                    ivPlayStart.setVisibility(View.VISIBLE);
                    pause();
                } else {
                    ivPlayStart.setVisibility(View.GONE);
                    play();
                }
                break;
        }
    }
}
