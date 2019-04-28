package com.kaadas.lock.linphone.player;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by Ads on 2016/5/2.
 */
public class MediaPlayerWrapper implements
        SurfaceTexture.OnFrameAvailableListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {
    public static String TAG = "PanoMediaPlayerWrapper";

    private StatusHelper statusHelper;

    private SurfaceTexture mSurfaceTexture;

    private MediaPlayer mMediaPlayer;

    private PlayerCallback playerCallback;
    private SurfaceHolder surfaceHolder;

    public MediaPlayerWrapper() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
    }


    public void setSurface(int mTextureID) {
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    public void doTextureUpdate(float[] mSTMatrix) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
    }

    public void openRemoteFile(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerFromUri(String uri) {
        try {
            mMediaPlayer.setDataSource(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
    }

    public void setMediaPlayerFromAssets(AssetFileDescriptor assetFileDescriptor) {
        try {
            mMediaPlayer.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getDeclaredLength()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
    }

    public void setStatusHelper(StatusHelper statusHelper) {
        this.statusHelper = statusHelper;
    }

    public void prepare() {
        if (statusHelper.getMediaStatus() == MediaStatus.START || statusHelper.getMediaStatus() == MediaStatus.STOPPED) {
            mMediaPlayer.prepareAsync();
        }
    }

    public void start() {
        MediaStatus panoStatus = statusHelper.getMediaStatus();
        if (panoStatus == MediaStatus.PREPARED || panoStatus == MediaStatus.PAUSED || panoStatus == MediaStatus.PAUSED_BY_USER || panoStatus == MediaStatus.COMPLETE) {
            Log.e("howard", "mediaplayer start");
            mMediaPlayer.start();
            statusHelper.setMediaStatus(MediaStatus.PLAYING);
        }
    }

    public void pause() {
        MediaStatus panoStatus = statusHelper.getMediaStatus();
        if (panoStatus == MediaStatus.PLAYING) {
            mMediaPlayer.pause();
            statusHelper.setMediaStatus(MediaStatus.PAUSED);
        }
    }

    public void pauseByUser() {
        MediaStatus panoStatus = statusHelper.getMediaStatus();
        if (panoStatus == MediaStatus.PLAYING) {
            mMediaPlayer.pause();
            statusHelper.setMediaStatus(MediaStatus.PAUSED_BY_USER);
        }
    }

    public void stop() {
        MediaStatus panoStatus = statusHelper.getMediaStatus();
        if (panoStatus == MediaStatus.PLAYING
                || panoStatus == MediaStatus.PREPARED
                || panoStatus == MediaStatus.PAUSED
                || panoStatus == MediaStatus.PAUSED_BY_USER) {
            mMediaPlayer.stop();
            statusHelper.setMediaStatus(MediaStatus.STOPPED);
        }
    }

    public void releaseResource() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(null);
            if (mSurfaceTexture != null) mSurfaceTexture = null;
            stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("howard", "player complete");
        statusHelper.setMediaStatus(MediaStatus.COMPLETE);
        if (playerCallback != null) {
            playerCallback.requestFinish();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "OnError - Error code: " + what + " Extra code: " + extra);
        switch (what) {
            case -1004:
                Log.d(TAG, "MEDIA_ERROR_IO");
                break;
            case -1007:
                Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                break;
            case 200:
                Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                break;
            case 100:
                Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                break;
            case -110:
                Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                break;
            case 1:
                Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                break;
            case -1010:
                Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                break;
        }
        switch (extra) {
            case 800:
                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case 702:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                break;
            case 701:
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                break;
            case 802:
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                break;
            case 801:
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                break;
            case 1:
                Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                break;
            case 3:
                Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                break;
            case 700:
                Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
        }
        return false;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        renderCallBack.renderImmediately();
        if (playerCallback != null) {
            playerCallback.updateProgress();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        statusHelper.setMediaStatus(MediaStatus.PREPARED);
        if (playerCallback != null) {
            playerCallback.updateInfo();
        }
        start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            MediaStatus panoStatus = statusHelper.getMediaStatus();
            if (panoStatus == MediaStatus.PLAYING
                    || panoStatus == MediaStatus.PAUSED
                    || panoStatus == MediaStatus.PAUSED_BY_USER)
                mMediaPlayer.seekTo(pos);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public interface PlayerCallback {
        void updateProgress();

        void updateInfo();

        void requestFinish();
    }

    public void setPlayerCallback(PlayerCallback playerCallback) {
        this.playerCallback = playerCallback;
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        mMediaPlayer.setDisplay(surfaceHolder);
    }
}
