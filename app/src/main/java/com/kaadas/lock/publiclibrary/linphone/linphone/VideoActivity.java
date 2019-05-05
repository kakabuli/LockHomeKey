package com.kaadas.lock.publiclibrary.linphone.linphone;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

/***
 *
 */

public class VideoActivity extends Activity {
    private static VideoActivity instance;

    public static VideoActivity instance() {
        return instance;
    }


    public static boolean isInstanciated() {
        return instance != null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }
}