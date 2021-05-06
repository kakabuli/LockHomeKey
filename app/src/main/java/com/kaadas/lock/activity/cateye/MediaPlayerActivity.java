package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.MediaPlayerWrapper;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.MediaStatus;
import com.kaadas.lock.publiclibrary.linphone.linphone.player.StatusHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.FtpEnable;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ftp.FtpException;
import com.kaadas.lock.utils.ftp.FtpUtils;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.widget.LoadingDialog;
import com.kaidishi.lock.util.g711mux;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.widget.AppCompatSeekBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MediaPlayerActivity extends BaseActivity<ISnapShotView, SnapPresenter<ISnapShotView>> implements ISnapShotView {


    private MediaPlayerWrapper mediaPlayer;
    private String filepath;
    @BindView(R.id.current_time)
    TextView currentTimeTv;
    @BindView(R.id.remain_time)
    TextView remainTimeTv;
    @BindView(R.id.duration_seek_bar)
    AppCompatSeekBar durationSeekBar;
    @BindView(R.id.btn_play)
    ImageButton playBtn;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.rl_player_content)
    RelativeLayout player_content;
    @BindView(R.id.btn_close)
    ImageButton btn_close;
    @BindView(R.id.media_reload_download_tv)
    LinearLayout media_reload_download_tv;
//    @BindView(R.id.btn_close2)
//    ImageView btn_close2;

    String imageName="";//图片地址 /sdap0/storage/orangecat-20181102/1541140662_picture.jpg
    String deviceId="";//设备id
    String remoteVideoPath="";//视频url(远程视频路径)
    String remoteAudioPath="";//音频url(远程图片路径)
    String ftpCmdPort="";//ftp端口
    String ftpCmdIp="";//ftpip
    String gatewayId="";//网关id
    String currentDayFolder;//当前天文件夹
    private StatusHelper statusHelper;
    LoadingDialog loadingDialog;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            timerHandler.postDelayed(timerRunnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        MyApplication.getInstance().setMediaPlayerActivity(true);
        Intent myIntent = getIntent();
        filepath = myIntent.getStringExtra(Constants.MEDIA_PATH);
        String playVideoFlag = myIntent.getStringExtra(Constants.PLAY_VIDEO_FLAG);
        loadingDialog = LoadingDialog.getInstance(this);
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

        if(Constants.CAT_EYE_VIDEO.equals(playVideoFlag)){
            //快照进入
            EventBus.getDefault().register(this);
            imageName = myIntent.getStringExtra(Constants.CAT_EYE_URL);
            deviceId = myIntent.getStringExtra(Constants.DEVICE_ID);
            gatewayId=  myIntent.getStringExtra(Constants.GATEWAY_ID);
            //本地有读取本地,否则下载
            String videoPath = getVideoPath(imageName, deviceId);
            LogUtils.d("davi videoPath "+videoPath);
            Log.e(GeTui.Movies_video,"MediaPlayerActivity====>:"+imageName+" deviceId:"+deviceId+" gatewayId:"+gatewayId);
            if (videoPath!=null){
                //本地有,直接播放
                filepath=videoPath;
                playOperation();
            }else {
                loadingDialog.show(getString(R.string.loading));
                //下载
              //  FtpUtils.getInstance().doorBellFTPService(deviceId,gatewayId);
                mPresenter.weakUpFTP(gatewayId,deviceId);
//                mqttHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MediaPlayerActivity.this,getResources().getString(R.string.ftp_weak_up_overtime),Toast.LENGTH_SHORT).show();
//                        loadingDialog.dismiss();
//                    }
//                },mqttOverTime);
                Log.e(GeTui.Movies_video,"MediaPlayerActivity.........发送唤醒FTP包");
            }

        }else {
            playOperation();
        }
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
                currentTimeTv.setText(formatted);
            }
        });
    }

    private void reset() {
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
                playBtn.setSelected(!isPlaying);
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
        remainTimeTv.setText(formatted);

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
    //根据图片路径获取视频路径
    public String getVideoPath(final String imageLocalPath,String deviceId) {
//        /storage/emulated/0/kaadas/catEyeImages/AC35EEA60F2F/1535012736_audio.raw
        int start = imageLocalPath.lastIndexOf("/");
        int end = imageLocalPath.lastIndexOf("_");
        currentDayFolder = FtpUtils.getInstance().getCurrentDayFolder(imageLocalPath);
        LogUtils.d("davi currentDayFolder "+currentDayFolder);
        String timeStamp=imageLocalPath.substring(start,end);
        LogUtils.d("davi timeStamp "+timeStamp);
        String videoPath= FtpUtils.getInstance().getDownloadPath(Constants.COMPOUND_FOLDER,deviceId,currentDayFolder);
        File file = new File(videoPath);
        if (file.exists()){
            File[] allfiles = file.listFiles();
            if (allfiles == null) {
                return null;
            }
            for (int k = 0; k < allfiles.length; k++) {
                final File fi = allfiles[k];
                LogUtils.d("davi 文件路径 "+fi.getPath());
                if (fi.isFile()&&fi.getPath().contains(timeStamp+Constants.COMPOUND_VIDEO_FORMAT)) {
                    return videoPath+File.separator+timeStamp+Constants.COMPOUND_VIDEO_FORMAT;
                }
            }
        }
        return null;
    }

    void stopRepeatTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    //更新进度
    private void autoPlayVideo() {
        timerHandler.post(timerRunnable);
    }

    @Override
    protected SnapPresenter<ISnapShotView> createPresent() {
        return new SnapPresenter<>();
    }

    @Override
    public void showFTPResultSuccess(FtpEnable ftpEnable) {
        ftpCmdPort= ftpEnable.getReturnData().getFtpCmdPort()+"";
        ftpCmdIp =  ftpEnable.getReturnData().getFtpCmdIp()+"";
        int i = imageName.lastIndexOf("_");
        String  prefixPath= imageName.substring(0,i);
        remoteVideoPath=prefixPath+"_"+Constants.VIDEO_SUFFIX;
        remoteAudioPath=prefixPath+"_"+Constants.AUDIO_SUFFIX;
        Log.e(GeTui.VideoLog,"MediaPlayerActivity==>"+ " ftpCmdPort:"+ftpCmdPort+" ftpCmdIp:"+ftpCmdIp+"remoteAudioPath:"+remoteAudioPath);
        FtpUtils.getInstance().downloadMultiFile(ftpCmdIp,ftpCmdPort,null,deviceId,Constants.DOWNLOAD_AUDIO_FOLDER_NAME,remoteAudioPath);
    }

    @Override
    public void showFTPResultFail() {
        Toast.makeText(MediaPlayerActivity.this,getResources().getString(R.string.ftp_weak_up_fail),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFTPOverTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                media_reload_download_tv.setVisibility(View.VISIBLE);
                player_content.setVisibility(View.GONE);
           //     btn_close2.setVisibility(View.VISIBLE);
                Toast.makeText(MediaPlayerActivity.this,getResources().getString(R.string.download_overtime_1),Toast.LENGTH_SHORT).show();
              //  finish();
            }
        });
    }
    public String videoDownloadPath="";
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mediaPlayerPIR(String str) {
         if(Constants.AUDIO.equals(str)){
             //音频类型获取成功,获取视频
             Log.e(GeTui.VideoLog,"MediaPlayerActivity下载视频"+"ftpCmdIp:"+ftpCmdIp+" ftpCmdPort:"+ftpCmdPort +" deviceId:"+deviceId +" remoteVideoPath:"+remoteVideoPath);
             FtpUtils.getInstance().downloadMultiFile(ftpCmdIp,ftpCmdPort,null,deviceId,Constants.DOWNLOAD_VIDEO_FOLDER_NAME,remoteVideoPath);
         }else if(Constants.VIDEO.equals(str)){
             Log.e(GeTui.Movies_video,"MediaPlayerActivity下载音频和视频合成");
             LogUtils.d("davi 视频获取时间 "+System.currentTimeMillis());
             //视频获取成功,
             g711mux muxer = new g711mux();
             videoDownloadPath=  FtpUtils.getInstance().getDownloadPath(Constants.COMPOUND_FOLDER,deviceId,currentDayFolder);
             LogUtils.d("davi imageName "+imageName);
             int start = imageName.lastIndexOf("/");
             int end = imageName.lastIndexOf("_");
             String timestamp = imageName.substring(start + 1,end);
             LogUtils.d("davi timestamp "+timestamp);
             videoDownloadPath= videoDownloadPath +File.separator+timestamp+Constants.COMPOUND_VIDEO_FORMAT;
             String videoPath= getLocalVideoUrl(timestamp);
             String audioPath= getLocalAudioUrl(timestamp);
             LogUtils.d("davi  videoPath "+videoPath+" audioPath "+audioPath+" videoDownloadPath "+videoDownloadPath);
             Log.e(GeTui.Movies_video,"videoPath:"+videoPath+"audioPath:"+audioPath +" videoDownloadPath "+videoDownloadPath);
             muxer.mux(videoPath,audioPath,videoDownloadPath,20,8000);
             filepath=videoDownloadPath;
             loadingDialog.dismiss();
             playOperation();
         } else if(FtpException.DOWNLOADEXCEPTION.equals(str)){
             Log.e(GeTui.VideoLog,"MediaPlayerActivity==>Sockek异常.........:");
             Toast.makeText(this,getResources().getString(R.string.ftp_exception),Toast.LENGTH_SHORT).show();
             loadingDialog.dismiss();
             media_reload_download_tv.setVisibility(View.VISIBLE);
             player_content.setVisibility(View.GONE);
        //     btn_close2.setVisibility(View.VISIBLE);
             //   finish();
         }else if(str.equals(GeTui.PIR_FTP_FAIL)){
             Toast.makeText(this,getResources().getString(R.string.pir_ftp_fail),Toast.LENGTH_SHORT).show();
             loadingDialog.dismiss();
             media_reload_download_tv.setVisibility(View.VISIBLE);
             player_content.setVisibility(View.GONE);
        //     btn_close2.setVisibility(View.VISIBLE);
             //   finish();
         }else if(str.equals(GeTui.FTP_CONNECTION_FAIL)){
             Toast.makeText(this,getResources().getString(R.string.ftp_connection_fail),Toast.LENGTH_SHORT).show();
             loadingDialog.dismiss();
             media_reload_download_tv.setVisibility(View.VISIBLE);
             player_content.setVisibility(View.GONE);
        //     btn_close2.setVisibility(View.VISIBLE);
             //   finish();
         }else if(str.equals(GeTui.FTP_LOGIN_FAIL)){
             Toast.makeText(this,getResources().getString(R.string.ftp_login_fail),Toast.LENGTH_SHORT).show();
             loadingDialog.dismiss();
             media_reload_download_tv.setVisibility(View.VISIBLE);
             player_content.setVisibility(View.GONE);
         //    btn_close2.setVisibility(View.VISIBLE);
             //   finish();
         }else if(str.equals(GeTui.FTP_VIDEO_FAIL)){
             Toast.makeText(this,getResources().getString(R.string.video_download_fail),Toast.LENGTH_SHORT).show();
             loadingDialog.dismiss();
             media_reload_download_tv.setVisibility(View.VISIBLE);
             player_content.setVisibility(View.GONE);
         //    btn_close2.setVisibility(View.VISIBLE);
         }else if(str.equals(GeTui.FTP_AUDIO_FAIL)){
             Toast.makeText(this,getResources().getString(R.string.video_download_fail),Toast.LENGTH_SHORT).show();
             loadingDialog.dismiss();
             media_reload_download_tv.setVisibility(View.VISIBLE);
             player_content.setVisibility(View.GONE);
        //     btn_close2.setVisibility(View.VISIBLE);
         }
    }

    @OnClick({R.id.btn_close, R.id.btn_play,R.id.media_reload_download_tv})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_play:
                if (mediaPlayer != null && statusHelper.getMediaStatus() == MediaStatus.PLAYING) {
                    pause();
                } else {
                    play();
                }
                break;
            case R.id.media_reload_download_tv:
                media_reload_download_tv.setVisibility(View.GONE);
                player_content.setVisibility(View.VISIBLE);
                loadingDialog.show(getString(R.string.loading));
                mPresenter.weakUpFTP(gatewayId,deviceId);
             //   btn_close2.setVisibility(View.GONE);
                break;
        }
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
    void startRepeatTimer() {
        timerRunnable.run();
    }


    private String getLocalAudioUrl(String timestamp) {

        String downloadPath=  FtpUtils.getInstance().getDownloadPath(Constants.DOWNLOAD_AUDIO_FOLDER_NAME,deviceId,currentDayFolder);
        return downloadPath+File.separator+timestamp+"_"+Constants.AUDIO_SUFFIX;
    }

    private String getLocalVideoUrl(String timestamp) {
        String downloadPath=FtpUtils.getInstance().getDownloadPath(Constants.DOWNLOAD_VIDEO_FOLDER_NAME,deviceId,currentDayFolder);
        return downloadPath+File.separator+timestamp+"_"+Constants.VIDEO_SUFFIX;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().setMediaPlayerActivity(false);
        loadingDialog.dismiss();
        stopRepeatTimer();
        if (mediaPlayer != null) {
            mediaPlayer.releaseResource();
        }
        EventBus.getDefault().unregister(this);
    }
}





