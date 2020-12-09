package com.kaadas.lock.shulan;

import android.content.Context;

public interface IKeepAliveRuning {
    void onRuning(Context context);
    void onStop();
}
