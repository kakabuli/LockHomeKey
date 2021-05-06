package com.kaadas.lock.publiclibrary;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.LogUtils;

import org.linphone.mediastream.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManager {
    public static final int notificationId = 4685;

    private static Notification notification;
    private static String channelId;

    public static Notification getNotification() {
        if (notification == null) {
            notification = new Notification();

        }
        return notification;
    }

    @TargetApi(26)
    public static void silentForegroundNotification(Service context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            LogUtils.e("启动通知");
            NotificationChannel channel = new NotificationChannel(notificationId + "", "普通通知", android.app.NotificationManager.IMPORTANCE_HIGH);
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId);
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setContentText("");

            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setAutoCancel(true);
            builder.setShowWhen(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            // 这里两个通知使用同一个id且必须按照这个顺序后调用startForeground
//            int id = NotificationUtil.nextNotifyId();
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
            LogUtils.e("shulan -notificationId-->" + notificationId);
            context.startForeground(notificationId, builder.build());
        }
    }

}
