package com.kaidishi.lock.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.push.RemoteMessage;
import com.huawei.hms.push.SendException;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MMKVUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.GeTui;

import java.util.Arrays;

import io.reactivex.disposables.Disposable;

public class HuaWeiPushService extends HmsMessageService {
    private static final String TAG = "HuaWeiPushService";

    /**
     * When an app calls the getToken method to apply for a token from the server,
     * if the server does not return the token during current method calling, the server can return the token through this method later.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     *
     * @param token token
     */
    @Override
    public void onNewToken(String token) {
        LogUtils.d(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
            refreshedTokenToServer(token);
//            turnSwichpush(this,true);
        }
    }

    @Override
    public void onNewToken(String token, Bundle bundle) {
        super.onNewToken(token, bundle);
        LogUtils.d(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token) && !token.equals(SPUtils.get(GeTui.HUAWEI_KEY,"").toString())) {
            // This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
            refreshedTokenToServer(token);
            //设置是否接收Push通知栏消息调用,系统默认是允许显示通知栏消息
//            turnSwichpush(this,true);
        }
    }

    private void turnSwichpush(Context context,boolean flag) {
        if(flag){
            // 设置显示通知栏消息
            HmsMessaging.getInstance(context).turnOnPush().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    // 获取结果
                    if (task.isSuccessful()) {
                        LogUtils.i(TAG, "turnOnPush Complete");
                    } else {
                        LogUtils.e(TAG, "turnOnPush failed: ret=" + task.getException().getMessage());
                    }
                }
            });
        }else{
            HmsMessaging.getInstance(context).turnOffPush().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    // 获取结果
                    if (task.isSuccessful()) {
                        LogUtils.i(TAG, "turnOnPush Complete");
                    } else {
                        LogUtils.e(TAG, "turnOnPush failed: ret=" + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void refreshedTokenToServer(String huawei) {
        LogUtils.d(TAG, "sending token to server. token:" + huawei);
        uploadpushmethod(3,huawei);
        SPUtils.put(GeTui.HUAWEI_KEY,huawei);
    }

    /**
     * This method is used to receive downstream data messages.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     *
     * @param message RemoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        LogUtils.d(TAG, "onMessageReceived is called");
        if (message == null) {
            LogUtils.e(TAG, "Received message entity is null!");
            return;
        }
        // getCollapseKey() Obtains the classification identifier (collapse key) of a message.
        // getData() Obtains valid content data of a message.
        // getMessageId() Obtains the ID of a message.
        // getMessageType() Obtains the type of a message.
        // getNotification() Obtains the notification data instance from a message.
        // getOriginalUrgency() Obtains the original priority of a message.
        // getSentTime() Obtains the time when a message is sent from the server.
        // getTo() Obtains the recipient of a message.

        LogUtils.d(TAG, "getCollapseKey: " + message.getCollapseKey()
                + "\n getData: " + message.getData()
                + "\n getFrom: " + message.getFrom()
                + "\n getTo: " + message.getTo()
                + "\n getMessageId: " + message.getMessageId()
                + "\n getMessageType: " + message.getMessageType()
                + "\n getSendTime: " + message.getSentTime()
                + "\n getTtl: " + message.getTtl()
                + "\n getSendMode: " + message.getSendMode()
                + "\n getReceiptMode: " + message.getReceiptMode()
                + "\n getOriginalUrgency: " + message.getOriginalUrgency()
                + "\n getUrgency: " + message.getUrgency()
                + "\n getToken: " + message.getToken());

        // getBody() Obtains the displayed content of a message
        // getTitle() Obtains the title of a message
        // getTitleLocalizationKey() Obtains the key of the displayed title of a notification message
        // getTitleLocalizationArgs() Obtains variable parameters of the displayed title of a message
        // getBodyLocalizationKey() Obtains the key of the displayed content of a message
        // getBodyLocalizationArgs() Obtains variable parameters of the displayed content of a message
        // getIcon() Obtains icons from a message
        // getSound() Obtains the sound from a message
        // getTag() Obtains the tag from a message for message overwriting
        // getColor() Obtains the colors of icons in a message
        // getClickAction() Obtains actions triggered by message tapping
        // getChannelId() Obtains IDs of channels that support the display of messages
        // getImageUrl() Obtains the image URL from a message
        // getLink() Obtains the URL to be accessed from a message
        // getNotifyId() Obtains the unique ID of a message

        RemoteMessage.Notification notification = message.getNotification();
        if (notification != null) {
            LogUtils.d(TAG, "\n getTitle: " + notification.getTitle()
                    + "\n getTitleLocalizationKey: " + notification.getTitleLocalizationKey()
                    + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.getTitleLocalizationArgs())
                    + "\n getBody: " + notification.getBody()
                    + "\n getBodyLocalizationKey: " + notification.getBodyLocalizationKey()
                    + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.getBodyLocalizationArgs())
                    + "\n getIcon: " + notification.getIcon()
                    + "\n getImageUrl: " + notification.getImageUrl()
                    + "\n getSound: " + notification.getSound()
                    + "\n getTag: " + notification.getTag()
                    + "\n getColor: " + notification.getColor()
                    + "\n getClickAction: " + notification.getClickAction()
                    + "\n getIntentUri: " + notification.getIntentUri()
                    + "\n getChannelId: " + notification.getChannelId()
                    + "\n getLink: " + notification.getLink()
                    + "\n getNotifyId: " + notification.getNotifyId()
                    + "\n isDefaultLight: " + notification.isDefaultLight()
                    + "\n isDefaultSound: " + notification.isDefaultSound()
                    + "\n isDefaultVibrate: " + notification.isDefaultVibrate()
                    + "\n getWhen: " + notification.getWhen()
                    + "\n getLightSettings: " + Arrays.toString(notification.getLightSettings())
                    + "\n isLocalOnly: " + notification.isLocalOnly()
                    + "\n getBadgeNumber: " + notification.getBadgeNumber()
                    + "\n isAutoCancel: " + notification.isAutoCancel()
                    + "\n getImportance: " + notification.getImportance()
                    + "\n getTicker: " + notification.getTicker()
                    + "\n getVibrateConfig: " + Arrays.toString(notification.getVibrateConfig())
                    + "\n getVisibility: " + notification.getVisibility());
        }

        Boolean judgeWhetherIn10s = false;

        // If the messages are not processed in 10 seconds, the app needs to use WorkManager for processing.
        if (judgeWhetherIn10s) {
            startWorkManagerJob(message);
        } else {
            // Process message within 10s
            processWithin10s(message);
        }
    }

    private void startWorkManagerJob(RemoteMessage message) {
        LogUtils.d(TAG, "Start new Job processing.");
    }

    private void processWithin10s(RemoteMessage message) {
        LogUtils.d(TAG, "Processing now.");
    }

    @Override
    public void onMessageSent(String msgId) {
        LogUtils.d(TAG, "onMessageSent called, Message id:" + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        LogUtils.d(TAG, "onSendError called, message id:" + msgId + ", ErrCode:"
                + ((SendException) exception).getErrorCode() + ", description:" + exception.getMessage());
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }


    public void uploadpushmethod(int type , String JpushId) {
        String uid = MMKVUtils.getStringMMKV(SPUtils.UID);
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(JpushId)) {
            XiaokaiNewServiceImp.uploadPushId(uid, JpushId, type).subscribe(new BaseObserver<BaseResult>() {
                @Override
                public void onSuccess(BaseResult baseResult) {
                }

                @Override
                public void onAckErrorCode(BaseResult baseResult) {
                    LogUtils.e(GeTui.VideoLog, "pushid上传失败,服务返回:" + baseResult);
                }

                @Override
                public void onFailed(Throwable throwable) {
                    LogUtils.e(GeTui.VideoLog, "pushid上传失败");
                }

                @Override
                public void onSubscribe1(Disposable d) {
                }
            });
        }else {
            LogUtils.e("jpushid上传失败");
        }

    }

}
