package com.kaadas.lock.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;

import java.util.List;

/**
 * Created by David on 2019/2/28
 */
public class SharedUtil {
    private static SharedUtil sharedUtil = null;

    public static SharedUtil getInstance() {
        if (sharedUtil == null) {
            sharedUtil = new SharedUtil();
        }
        return sharedUtil;
    }
    /**
     * 检测是否安装微信
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    //微信好友分享
    public void sendWeiXin(String message) {
//初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = message;

//用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = message;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());  //transaction字段用与唯一标示一个请求
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

//调用api接口，发送数据到微信
        MyApplication.getInstance().getApi().sendReq(req);
    }

    //发送短信
    public void sendShortMessage( String message,Context context) {
      /*  Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        sendIntent.putExtra("sms_body", message);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        activity.startActivity(sendIntent);
        localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");*/
        /*Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("text/plain");

        localIntent.putExtra("android.intent.extra.TEXT", message);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        MyApplication.getInstance().startActivity(localIntent);*/

        Uri smsToUri = Uri.parse("smsto:");

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", message);

        context.startActivity(intent);

    }

    /**
     * 复制文本到系统剪切板 *
     */
    public void copyTextToSystem(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", text);
        cm.setPrimaryClip(mClipData);
        com.blankj.utilcode.util.ToastUtils.showShort(R.string.copy_success);
    }
    /**
     * 拨打电话
     * */
    public void callPhone(Activity activity,String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        activity.startActivity(intent);
    }
    /**
     * 网页跳转
     * */
    public void jumpWebsite(Activity activity,String website){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(website);//此处填链接
        intent.setData(content_url);
        activity.startActivity(intent);
    }
}
