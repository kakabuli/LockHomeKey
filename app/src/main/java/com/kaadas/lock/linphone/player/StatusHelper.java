package com.kaadas.lock.linphone.player;

import android.content.Context;

public class StatusHelper {
    private MediaStatus mediaStatus;
    private Context context;
    public StatusHelper(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public MediaStatus getMediaStatus() {
        return mediaStatus;
    }

    public void setMediaStatus(MediaStatus mediaStatus) {
        this.mediaStatus = mediaStatus;
    }

}
