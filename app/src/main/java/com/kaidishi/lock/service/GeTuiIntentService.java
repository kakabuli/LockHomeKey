package com.kaidishi.lock.service;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockCallingActivity;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MMKVUtils;
import com.kaadas.lock.utils.NotificationUtils;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.GeTui;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class GeTuiIntentService extends GTIntentService {

    private static final String TAG = "cid";

    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;

    public GeTuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    //onReceiveMessageData 处理透传消息<br>
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();



        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);

            // 这里就是透传收到的Json数据
            Log.d(TAG, "receiver payload = " + data);

            // 测试消息为了观察数据变化
            if (data.equals(getResources().getString(R.string.push_transmission_data))) {
                data = data + "-" + cnt;
                cnt++;
            }
            sendMessage(data, 0);
//            if(!Rom.isFlyme() || !Rom.isSmartisan()){
            sendNotification(data);
//            }
        }

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));
        Log.d("shulan", "onReceiveMessageData call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        Log.d(TAG, "----------------------------------------------------------------------------------------------");

    }

    private void sendNotification(String data) {
        TransmissionContentResult transmissionContentResult = new Gson().fromJson(data, TransmissionContentResult.class);
        if(transmissionContentResult == null){
            return;
        }
        String intentString = transmissionContentResult.getIntent();
        String longString = "";
        String[] split = intentString.split(";");
        for (int i = 0; i < split.length;i++){
            LogUtils.e(TAG+"shulan"+i+"-->" + split[i]);
            if(split[i].contains("S.stringType=")){
                intentString = split[i].split("S.stringType=")[1];
            }
            if(split[i].contains("l.longType=")){
                longString = split[i].split("l.longType=")[1];
            }
        }

        LogUtils.e(TAG+"shulan intentString-->" + intentString);
        intentString = new String(Base64.decode(intentString, Base64.DEFAULT));
        LogUtils.e(TAG+"shulan intentString-->" + intentString);
        Intent intent;
        if(intentString.contains("\"func\":\"doorbell\"") && intentString.contains("wifiSN")){
            JSONObject jsonObject = null;
            String wifiSN = "";
            try {
                jsonObject = new JSONObject(intentString);
                wifiSN = jsonObject.optString("wifiSN");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent = new Intent(GeTuiIntentService.this, WifiVideoLockCallingActivity.class);
            intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_CALLING,1);
            intent.putExtra("VIDEO_CALLING_IS_MAINACTIVITY",true);
            intent.putExtra(KeyConstants.WIFI_SN,wifiSN);
            LogUtils.e(TAG+"shulan WifiVideoLockCallingActivity-->");
        }else{
            intent = new Intent(GeTuiIntentService.this, MainActivity.class);
            LogUtils.e(TAG+"shulan MainActivity-->");
        }
        long time = 0;
        try {
            time = Long.parseLong(longString);
        }catch (Exception e){

        }
        if(System.currentTimeMillis() - time < 18000 ){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            NotificationUtils.sendNotification(GeTuiIntentService.this,transmissionContentResult.getTitle(),transmissionContentResult.getContent(), R.mipmap.ic_launcher,intent);
        }
    }

    // App初始化以后会回调这个方法
   // onReceiveClientId 接收 cid <br>
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        LogUtils.e("shulan -- onReceiveClientId-->clientid=" + clientid);
        sendMessage(clientid, 1);
        if(!Rom.isEmui() && !Rom.isMiui() && !clientid.equals(SPUtils.get(GeTui.JPUSH_ID,"").toString())){
            refreshedTokenToServer(clientid);
        }
    }
    // App初始化以后会回调这个方法
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
        LogUtils.e( "shulan onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);
        LogUtils.e("shulan onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

//        if (action == PushConsts.SET_TAG_RESULT) {
//            setTagResult((SetTagCmdMessage) cmdMessage);
//        } else if(action == PushConsts.BIND_ALIAS_RESULT) {
//            bindAliasResult((BindAliasCmdMessage) cmdMessage);
//        } else if (action == PushConsts.UNBIND_ALIAS_RESULT) {
//            unbindAliasResult((UnBindAliasCmdMessage) cmdMessage);
//        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
//            feedbackResult((FeedbackCmdMessage) cmdMessage);
//        }
    }

    //在线通知接收
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                        + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                        + message.getTitle() + "\ncontent = " + message.getContent());

        LogUtils.e("shulan onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
        LogUtils.e("shulan onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

//    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
//        String sn = setTagCmdMsg.getSn();
//        String code = setTagCmdMsg.getCode();
//
//        int text = R.string.add_tag_unknown_exception;
//        switch (Integer.valueOf(code)) {
//            case PushConsts.SETTAG_SUCCESS:
//                text = R.string.add_tag_success;
//                break;
//
//            case PushConsts.SETTAG_ERROR_COUNT:
//                text = R.string.add_tag_error_count;
//                break;
//
//            case PushConsts.SETTAG_ERROR_FREQUENCY:
//                text = R.string.add_tag_error_frequency;
//                break;
//
//            case PushConsts.SETTAG_ERROR_REPEAT:
//                text = R.string.add_tag_error_repeat;
//                break;
//
//            case PushConsts.SETTAG_ERROR_UNBIND:
//                text = R.string.add_tag_error_unbind;
//                break;
//
//            case PushConsts.SETTAG_ERROR_EXCEPTION:
//                text = R.string.add_tag_unknown_exception;
//                break;
//
//            case PushConsts.SETTAG_ERROR_NULL:
//                text = R.string.add_tag_error_null;
//                break;
//
//            case PushConsts.SETTAG_NOTONLINE:
//                text = R.string.add_tag_error_not_online;
//                break;
//
//            case PushConsts.SETTAG_IN_BLACKLIST:
//                text = R.string.add_tag_error_black_list;
//                break;
//
//            case PushConsts.SETTAG_NUM_EXCEED:
//                text = R.string.add_tag_error_exceed;
//                break;
//
//            default:
//                break;
//        }
//
//        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));
//    }

//    private void bindAliasResult(BindAliasCmdMessage bindAliasCmdMessage) {
//        String sn = bindAliasCmdMessage.getSn();
//        String code = bindAliasCmdMessage.getCode();
//
//        int text = R.string.bind_alias_unknown_exception;
//        switch (Integer.valueOf(code)) {
//            case PushConsts.BIND_ALIAS_SUCCESS:
//                text = R.string.bind_alias_success;
//                break;
//            case PushConsts.ALIAS_ERROR_FREQUENCY:
//                text = R.string.bind_alias_error_frequency;
//                break;
//            case PushConsts.ALIAS_OPERATE_PARAM_ERROR:
//                text = R.string.bind_alias_error_param_error;
//                break;
//            case PushConsts.ALIAS_REQUEST_FILTER:
//                text = R.string.bind_alias_error_request_filter;
//                break;
//            case PushConsts.ALIAS_OPERATE_ALIAS_FAILED:
//                text = R.string.bind_alias_unknown_exception;
//                break;
//            case PushConsts.ALIAS_CID_LOST:
//                text = R.string.bind_alias_error_cid_lost;
//                break;
//            case PushConsts.ALIAS_CONNECT_LOST:
//                text = R.string.bind_alias_error_connect_lost;
//                break;
//            case PushConsts.ALIAS_INVALID:
//                text = R.string.bind_alias_error_alias_invalid;
//                break;
//            case PushConsts.ALIAS_SN_INVALID:
//                text = R.string.bind_alias_error_sn_invalid;
//                break;
//            default:
//                break;
//
//        }

     //   Log.d(TAG, "bindAlias result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));

  //  }

//    private void unbindAliasResult(UnBindAliasCmdMessage unBindAliasCmdMessage) {
//        String sn = unBindAliasCmdMessage.getSn();
//        String code = unBindAliasCmdMessage.getCode();
//
//        int text = R.string.unbind_alias_unknown_exception;
//        switch (Integer.valueOf(code)) {
//            case PushConsts.UNBIND_ALIAS_SUCCESS:
//                text = R.string.unbind_alias_success;
//                break;
//            case PushConsts.ALIAS_ERROR_FREQUENCY:
//                text = R.string.unbind_alias_error_frequency;
//                break;
//            case PushConsts.ALIAS_OPERATE_PARAM_ERROR:
//                text = R.string.unbind_alias_error_param_error;
//                break;
//            case PushConsts.ALIAS_REQUEST_FILTER:
//                text = R.string.unbind_alias_error_request_filter;
//                break;
//            case PushConsts.ALIAS_OPERATE_ALIAS_FAILED:
//                text = R.string.unbind_alias_unknown_exception;
//                break;
//            case PushConsts.ALIAS_CID_LOST:
//                text = R.string.unbind_alias_error_cid_lost;
//                break;
//            case PushConsts.ALIAS_CONNECT_LOST:
//                text = R.string.unbind_alias_error_connect_lost;
//                break;
//            case PushConsts.ALIAS_INVALID:
//                text = R.string.unbind_alias_error_alias_invalid;
//                break;
//            case PushConsts.ALIAS_SN_INVALID:
//                text = R.string.unbind_alias_error_sn_invalid;
//                break;
//            default:
//                break;
//
//        }
//
//        Log.d(TAG, "unbindAlias result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));
//
//    }


    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    private void   sendMessage(String data, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = data;
      //  DemoApplication.sendMessage(msg);
    }

    private void refreshedTokenToServer(String jpush) {
        LogUtils.d(TAG, "sending token to server. token:" + jpush);
        uploadpushmethod(2,jpush);
        SPUtils.put(GeTui.JPUSH_ID,jpush);
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
