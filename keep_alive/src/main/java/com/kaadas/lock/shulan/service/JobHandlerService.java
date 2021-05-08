package com.kaadas.lock.shulan.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.kaadas.lock.shulan.BuildConfig;
import com.kaadas.lock.shulan.config.KeepAliveConfig;
import com.kaadas.lock.shulan.utils.KeepAliveUtils;

/**
 * 定时器
 * 安卓5.0及以上
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobHandlerService extends JobService {
    private String TAG = this.getClass().getSimpleName();
    private static JobScheduler mJobScheduler;

    private static int EXECUTE_COUNT = 0;

    public static void startJob(Context context) {
        try {
            mJobScheduler = (JobScheduler) context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(10,
                    new ComponentName(context.getPackageName(),
                            JobHandlerService.class.getName())).setPersisted(true);
            /**
             * I was having this problem and after review some blogs and the official documentation,
             * I realised that JobScheduler is having difference behavior on Android N(24 and 25).
             * JobScheduler works with a minimum periodic of 15 mins.
             *
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0以上延迟1s执行
                builder.setMinimumLatency(KeepAliveConfig.JOB_TIME);
            } else {
                //每隔1s执行一次job
                builder.setPeriodic(KeepAliveConfig.JOB_TIME);
            }
            mJobScheduler.schedule(builder.build());

        } catch (Exception e) {
            if(BuildConfig.DEBUG)
            Log.e("startJob->", e.getMessage());
        }
    }

    public static void stopJob() {
        if (mJobScheduler != null)
            mJobScheduler.cancelAll();


    }

    private void startService(Context context) {
        try {
            if(BuildConfig.DEBUG)
            Log.e(TAG, "---》启动双进程保活服务");
            //启动本地服务
            Intent localIntent = new Intent(context, SLLocalService.class);
            //启动守护进程
            Intent guardIntent = new Intent(context, SLRemoteService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(localIntent);
//                startForegroundService(guardIntent);
            } else {
                startService(localIntent);
//                startService(guardIntent);
            }
        } catch (Exception e) {
            if(BuildConfig.DEBUG)
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
            ++EXECUTE_COUNT;
            if(BuildConfig.DEBUG)
            Log.e("JOB-->", " Job 执行 " + EXECUTE_COUNT);
            //7.0以上轮询
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startJob(this);
            }
            if (!KeepAliveUtils.isServiceRunning(JobHandlerService.this, getPackageName() + ":sllocal")
                    || !KeepAliveUtils.isRunningTaskExist(JobHandlerService.this, getPackageName() + ":slremote")) {
                if(BuildConfig.DEBUG)
                Log.e("JOB-->", " 重新开启了 服务 ");
                startService(this);
            }
        } catch (Exception e) {
            if(BuildConfig.DEBUG)
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e("JOB-->", " Job 停止");
        if (!KeepAliveUtils.isServiceRunning(JobHandlerService.this, getPackageName() + ":sllocal")
                || !KeepAliveUtils.isRunningTaskExist(JobHandlerService.this, getPackageName() + ":slremote")) {
            startService(this);
        }
        return false;
    }
}
