package com.kaadas.lock.publiclibrary.linphone.linphone.util;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceView;

import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneAutoAccept;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.RegistrationCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.linphone.PhoneBean;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneManager;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneService;
import com.kaadas.lock.utils.FileUtils;
import com.kaadas.lock.utils.LogUtils;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import static java.lang.Thread.sleep;


public class LinphoneHelper {
    private static ServiceWaitThread mServiceWaitThread;
    private static String mUsername, mPassword, mServerIP;
    private static AndroidVideoWindowImpl mAndroidVideoWindow;
    private static SurfaceView mRenderingView, mPreviewView;
    private static LinphoneCallStats callVideoStats;

    /**
     * 开启服务LinphoneService
     *
     * @param context 上下文
     */
    public static void startService(Context context) {
        if (!LinphoneService.isReady()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClass(context, LinphoneService.class);
            context.startService(intent);
        }
    }

    public static AndroidVideoWindowImpl getmAndroidVideoWindow() {
        return mAndroidVideoWindow;
    }

    /**
     * 设置 sip 账户信息
     *
     * @param username sip 账户
     * @param password 密码
     * @param serverIP sip 服务器
     */
    public static void setAccount(String username, String password, String serverIP) {
        mUsername = username;
        mPassword = password;
        mServerIP = serverIP;
    }

    /**
     * 添加注册状态、通话状态回调
     *
     * @param phoneCallback        通话回调
     * @param registrationCallback 注册状态回调
     */
    public static void addCallback(RegistrationCallback registrationCallback,
                                   PhoneCallback phoneCallback) {
        LinphoneService.addRegistrationCallback(registrationCallback);
        LinphoneService.addPhoneCallback(phoneCallback);
        if (LinphoneService.isReady()) {
        } else {
            mServiceWaitThread = new ServiceWaitThread(registrationCallback, phoneCallback);
            mServiceWaitThread.start();
        }
    }

    public static void addAutoAcceptCallBack(PhoneAutoAccept autoAccept) {
        LogUtils.e("设置猫眼状态监听   autoAccept=" + (autoAccept == null));
        LinphoneService.addAutoAccept(autoAccept);
    }

    /**
     * 登录到 SIP 服务器
     */
    public static void login() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!LinphoneService.isReady()) {
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                loginToServer();
            }
        }).start();
    }

    public static void deleteUser() {
        deleteUserToServer();
    }

    /**
     * 呼叫指定号码
     *
     * @param num 呼叫号码
     */
    public static void callTo(String num, boolean isVideoCall) {
        if (!LinphoneService.isReady() || !LinphoneManager.isInstanceiated()) {
            return;
        }
        if (!num.equals("")) {
            PhoneBean phone = new PhoneBean();
            phone.setUserName(num);
            if (mServerIP == null) {
                Log.e("howard", "mServerIP==null");

            }
            phone.setHost(mServerIP);
            LinphoneUtils.getInstance().startSingleCallingTo(phone, isVideoCall);
        }
    }

    public static void inviteToVideo() {
        LinphoneUtils.getInstance().reinviteWithVideo();
    }

    /**
     * 接听来电
     */
    public static void acceptCall(String deviceIp) {
        LinphoneCall linphoneCall = LinphoneManager.getLc().getCurrentCall();
        LinphoneCore linphoneCore = LinphoneManager.getLc();
        Log.e("howard", "acceptCall deviceIp " + deviceIp);
        if (linphoneCall != null && linphoneCore != null) {
            LinphoneCallParams params = linphoneCore.createCallParams(linphoneCall);
            FileUtils.createFolder(Util.VIDEO_DIR);
            params.setRecordFile(Util.RECORD_VIDEO_PATH);
            if (deviceIp != null) {
                linphoneCore.setDeviceIp(deviceIp);
            }
            params.setVideoEnabled(true);
            try {
                Log.e("howard", "acceptCall acceptCallWithParams ");
                LinphoneManager.getLc().acceptCallWithParams(LinphoneManager.getLc().getCurrentCall(), params);

            } catch (LinphoneCoreException e) {
                Log.e("linphoneHelper", e.toString());
            }
        } else {
            Log.e("linphoneHelper", "call error");
        }

    }

    /**
     * 挂断当前通话
     */
    public static void hangUp() {
        LinphoneUtils.getInstance().hangUp();
    }

    public static void setAudioMulticastAddr(String ip) {
        LinphoneUtils.getInstance().setAudioMulticastAddr(ip);
    }

    public static String getAudioMulticastAddr() {
        return LinphoneUtils.getInstance().getAudioMulticastAddr();
    }

    public static void setVideoMulticastAddr(String ip) {
        LinphoneUtils.getInstance().setVideoMulticastAddr(ip);
    }

    public static String getVideoMulticastAddr() {
        return LinphoneUtils.getInstance().getVideoMulticastAddr();
    }

    /**
     * 切换静音
     *
     * @param isMicMuted 是否静音
     */
    public static void toggleMicro(boolean isMicMuted) {
        LinphoneUtils.getInstance().toggleMicro(isMicMuted);
    }

    public static void setVideoSize(int width, int height) {
        LinphoneUtils.getInstance().setVideoSize(width, height);
    }

    public static int getVideoWidth() {
        return LinphoneUtils.getInstance().getVideoWidth();
    }

    public static int getVideoHeight() {
        return LinphoneUtils.getInstance().getVideoHeight();
    }

    public static void setVideoSizeByName(String value) {
        LinphoneUtils.getInstance().setVideoSizeByName(value);
    }

    /**
     * 旋转视频
     *
     * @r 旋转角度 0,9,180,270
     */
    public static void setRotation(int r) {
        if (mAndroidVideoWindow != null) {
            mAndroidVideoWindow.setDisplayRotation(r);
        }
    }

    /**
     * 切换免提
     *
     * @param isSpeakerEnabled 是否免提
     *                         是否外放
     */
    public static void toggleSpeaker(boolean isSpeakerEnabled) {
        LinphoneUtils.getInstance().toggleSpeaker(isSpeakerEnabled);
    }

    public static void startRecording(String filePath) {
        LinphoneUtils.getInstance().StartRecordVideoPath(filePath);
    }

    public static void stopRecording() {
        LinphoneUtils.getInstance().stopRecording();
    }

    private static class ServiceWaitThread extends Thread {
        private PhoneCallback mPhoneCallback;
        private RegistrationCallback mRegistrationCallback;

        ServiceWaitThread(RegistrationCallback registrationCallback, PhoneCallback phoneCallback) {
            mRegistrationCallback = registrationCallback;
            mPhoneCallback = phoneCallback;
        }

        @Override
        public void run() {
            super.run();
            while (!LinphoneService.isReady()) {
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            LinphoneService.addPhoneCallback(mPhoneCallback);
            LinphoneService.addRegistrationCallback(mRegistrationCallback);
            mServiceWaitThread = null;
        }
    }

    /**
     * 登录 SIP 服务器
     */
    private static void loginToServer() {
        if (mUsername == null || mPassword == null || mServerIP == null) {
            throw new RuntimeException("The sip account is not configured.");
        }
        LinphoneUtils.getInstance().registerUserAuth(mUsername, mPassword, mServerIP);
    }

    private static void deleteUserToServer() {
        try {

            LinphoneUtils.getInstance().deleteUserAuth(mUsername, mPassword, mServerIP);
        } catch (Exception e) {
            Log.e("linphoneHelper", e.toString());
        }
    }

    public static boolean getVideoEnabled() {
        LinphoneCallParams remoteParams = LinphoneManager.getLc().getCurrentCall().getRemoteParams();
        return remoteParams != null && remoteParams.getVideoEnabled();
    }

    public static float getReceivedFramerate() {
        LinphoneCall call;
        call = LinphoneManager.getLc().getCurrentCall();
        if (call != null) {
            LinphoneCallParams remoteParams = call.getCurrentParams();
            if (remoteParams != null) return remoteParams.getReceivedFramerate();
        }
        return 0;
    }

    /**
     * 设置 SurfaceView
     *
     * @param renderingView 远程 SurfaceView
     * @param previewView   本地 SurfaceView
     */
    public static void setAndroidVideoWindow(final SurfaceView[] renderingView, final SurfaceView[] previewView) {
        mRenderingView = renderingView[0];
        mPreviewView = previewView[0];
        fixZOrder(mRenderingView, mPreviewView);
        mAndroidVideoWindow = new AndroidVideoWindowImpl(renderingView[0], previewView[0], new AndroidVideoWindowImpl.VideoWindowListener() {
            @Override
            public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                setVideoWindow(androidVideoWindow);
                renderingView[0] = surfaceView;
            }

            @Override
            public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {
                removeVideoWindow();
            }

            @Override
            public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                mPreviewView = surfaceView;
                setPreviewWindow(mPreviewView);
            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {
                removePreviewWindow();
            }
        });
    }

    /**
     * onResume
     */
    public static void onResume() {
        try {
            if (mRenderingView != null) {
                ((GLSurfaceView) mRenderingView).onResume();
            }

            if (mAndroidVideoWindow != null) {
                synchronized (mAndroidVideoWindow) {
                    LinphoneManager.getLc().setVideoWindow(mAndroidVideoWindow);
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * onPause
     */
    public static void onPause() {
        if (mAndroidVideoWindow != null) {
            synchronized (mAndroidVideoWindow) {
                LinphoneManager.getLc().setVideoWindow(null);
            }
        }

        if (mRenderingView != null) {
            ((GLSurfaceView) mRenderingView).onPause();
        }
    }

    /**
     * onDestroy
     */
    public static void onDestroy() {
        mPreviewView = null;
        mRenderingView = null;

        if (mAndroidVideoWindow != null) {
            mAndroidVideoWindow.release();
            mAndroidVideoWindow = null;
        }
    }

    private static void fixZOrder(SurfaceView rendering, SurfaceView preview) {
        rendering.setZOrderOnTop(false);
        preview.setZOrderOnTop(true);
        preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
    }

    private static void setVideoWindow(Object o) {
        try {
            if (LinphoneManager.getLc() != null)
                LinphoneManager.getLc().setVideoWindow(o);
        } catch (Exception e) {

        }
    }

    private static void removeVideoWindow() {
        LinphoneCore linphoneCore = LinphoneManager.getLc();
        if (linphoneCore != null) {
            linphoneCore.setVideoWindow(null);
        }
    }

    //设置当前用户视频流到SurfaceView 当中
    private static void setPreviewWindow(Object o) {
        LinphoneManager.getLc().setPreviewWindow(o);
    }

    private static void removePreviewWindow() {
        LinphoneManager.getLc().setPreviewWindow(null);
    }

    /**
     * 获取 LinphoneCore
     *
     * @return LinphoneCore
     */
    public static LinphoneCore getLC() {
        return LinphoneManager.getLc();
    }

//    public static float getStatus(int type){
//        LinphoneCall linphoneCall = LinphoneManager.getLc().getCurrentCall();
//        if (linphoneCall != null){
//            if(type ==1){callVideoStats=linphoneCall.getVideoStats();}else{
//                callVideoStats=linphoneCall.getAudioStats();
//            }
//
//            return callVideoStats.getLocalLossRate();
//        }
//        return 0.0f;
//    }

    public static float getStatus(int type) {
        try {
            if (LinphoneManager.getLc() != null) {
                LinphoneCall linphoneCall = LinphoneManager.getLc().getCurrentCall();
                if (linphoneCall != null) {
                    if (type == 1) {
                        callVideoStats = linphoneCall.getVideoStats();
                    } else {
                        callVideoStats = linphoneCall.getAudioStats();
                    }

                    return callVideoStats.getLocalLossRate();
                }
            }
        } catch (Exception e) {

        }
        return 0.0f;
    }

    public static float getTotalLossRate(int type) {
        LinphoneCall linphoneCall = LinphoneManager.getLc().getCurrentCall();
        if (linphoneCall != null) {
            if (type == 1) {
                callVideoStats = linphoneCall.getVideoStats();
            } else {
                callVideoStats = linphoneCall.getAudioStats();
            }

            return callVideoStats.getTotalLossRate();
        }
        return 0.0f;
    }
}

