package com.kaadas.lock.linphone.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kaadas.lock.linphone.linphone.PhoneBean;
import com.kaadas.lock.linphonenew.LinphoneManager;
import com.kaadas.lock.utils.FileUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneNatPolicy;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.LpConfig;
import org.linphone.core.VideoSize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 语音通话工具类
 */

public class LinphoneUtils {
    private static final String TAG = "howard";
    private static volatile LinphoneUtils sLinphoneUtils;
    private LinphoneCore mLinphoneCore = null;

    public static LinphoneUtils getInstance() {
        if (sLinphoneUtils == null) {
            synchronized (LinphoneUtils.class) {
                if (sLinphoneUtils == null) {
                    sLinphoneUtils = new LinphoneUtils();
                }
            }
        }
        return sLinphoneUtils;
    }

    private LinphoneUtils() {
        mLinphoneCore = LinphoneManager.getLc();
        mLinphoneCore.enableEchoCancellation(true);
        mLinphoneCore.enableEchoLimiter(true);
        mLinphoneCore.enableDnsSrv(true);
        //mLinphoneCore.setDnsServers(null);
    }

    /**
     * 注册到服务器
     * @param name
     * @param password
     * @param host
     * @throws LinphoneCoreException
     */
    public void registerUserAuth(String name, String password, String host) {
        Log.e(TAG, "registerUserAuth name = " + name);
        Log.e(TAG, "registerUserAuth pw = " + password);
        Log.e(TAG, "registerUserAuth host = " + host);
        String identify = "sip:" + name + "@" + host;
        String proxy = "sip:"+host;
		LinphoneAddress proxyAddr;
		try {
			proxyAddr = LinphoneCoreFactory.instance().createLinphoneAddress(proxy);
		LinphoneAddress identifyAddr = LinphoneCoreFactory.instance().createLinphoneAddress(identify);
        LinphoneAuthInfo authInfo = LinphoneCoreFactory.instance().createAuthInfo(name, null, password,
                null, null, host);
			if (null!=mLinphoneCore){
				LinphoneProxyConfig prxCfg = mLinphoneCore.createProxyConfig(identifyAddr.asString(),
					proxyAddr.asStringUriOnly(), proxyAddr.asStringUriOnly(), true);
				//打开log
				LinphoneCoreFactory.instance().enableLogCollection(true);
				LinphoneCoreFactory.instance().setDebugMode(true, "kaadasLinphone");
				mLinphoneCore.enableIpv6(true);

				prxCfg.enableAvpf(false);
				prxCfg.setAvpfRRInterval(0);
				prxCfg.enableQualityReporting(false);
				prxCfg.setQualityReportingCollector(null);
				prxCfg.setQualityReportingInterval(0);
				prxCfg.enableRegister(true);
				prxCfg.setExpires(10);
				mLinphoneCore.addProxyConfig(prxCfg);
				mLinphoneCore.addAuthInfo(authInfo);
				mLinphoneCore.setDefaultProxyConfig(prxCfg);
			}else {
				LogUtils.e("mLinphoneCore:null");
			}
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
			LogUtils.e("LinphoneCoreException:"+e.toString());
		}
//        setStunServer("sip.linphone.org");
    }
    public void deleteUserAuth(String name, String password, String host) throws Exception {
        Log.e(TAG, "delete name = " + name);
        Log.e(TAG, "delete pw = " + password);
        Log.e(TAG, "delete host = " + host);
        String identify = "sip:" + name + "@" + host;
        String proxy = "sip:" + host;
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(host)) {

        }else {
        LinphoneAddress proxyAddr = LinphoneCoreFactory.instance().createLinphoneAddress(proxy);
        LinphoneAddress identifyAddr = LinphoneCoreFactory.instance().createLinphoneAddress(identify);
        LinphoneAuthInfo authInfo = LinphoneCoreFactory.instance().createAuthInfo(name, null, password,
                null, null, host);
        if (null !=mLinphoneCore){
			LinphoneProxyConfig prxCfg = mLinphoneCore.createProxyConfig(identifyAddr.asString(),
				proxyAddr.asStringUriOnly(), proxyAddr.asStringUriOnly(), true);
			//打开log
			prxCfg.enableAvpf(false);
			prxCfg.setAvpfRRInterval(0);
			prxCfg.enableQualityReporting(false);
			prxCfg.setQualityReportingCollector(null);
			prxCfg.setQualityReportingInterval(0);
			prxCfg.enableRegister(true);
			prxCfg.setExpires(0);
			mLinphoneCore.addProxyConfig(prxCfg);
			mLinphoneCore.addAuthInfo(authInfo);
			mLinphoneCore.setDefaultProxyConfig(prxCfg);
			mLinphoneCore.clearAuthInfos();
			mLinphoneCore.clearProxyConfigs();
		}else {
        	throw new Exception("mLinphoneCore null Exception ");
		}

//        setStunServer("sip.linphone.org");
        }
    }

    /**
     * 測試使用
	 * 拨打电话
     */
    public LinphoneCall startSingleCallingTo(PhoneBean bean, boolean isVideoCall) {
        LinphoneAddress address;
        LinphoneCall call = null;
        String deviceIp = (String) SPUtils.get(  "deviceIp", "");
        if (TextUtils.isEmpty(deviceIp)) {
            Log.e("howard", "deviceIp = " + deviceIp);
            mLinphoneCore.setDeviceIp(deviceIp);
        }
        try {
            address = mLinphoneCore.interpretUrl(bean.getUserName() + "@" + bean.getHost());
//            LinphoneManager.getInstance().newOutgoingCall(mAddress);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
            return null;
        }
        address.setDisplayName(bean.getDisplayName());

        LinphoneCallParams params = mLinphoneCore.createCallParams(null);
//        params.addCustomSdpMediaAttribute();
		FileUtils.createFolder(Util.VIDEO_DIR);
        params.setRecordFile(Util.RECORD_VIDEO_PATH);
        if (isVideoCall) {
            params.setVideoEnabled(true);
            params.enableLowBandwidth(false);
        } else {
            params.setVideoEnabled(false);
        }
        try {
            call = mLinphoneCore.inviteAddressWithParams(address, params);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
        return call;
    }

    public boolean reinviteWithVideo() {
//        LinphoneCore lc =  LinphoneManager.getLc();
        LinphoneCall lCall = mLinphoneCore.getCurrentCall();
        lCall.enableCamera(true);
        if (lCall == null) {
            Log.e("howard", "Trying to reinviteWithVideo while not in call: doing nothing");
            return false;
        }
        LinphoneCallParams params = lCall.getCurrentParamsCopy();
        // Check if video possible regarding bandwidth limitations
//        bm().updateWithProfileSettings(lc, params);

        // Abort if not enough bandwidth...

        params.setVideoEnabled(true);
        if (!params.getVideoEnabled()) {
            Log.e("howard", "Trying to getVideoEnabled");
            return false;
        }
        // Not yet in video call: try to re-invite with video
        mLinphoneCore.updateCall(lCall, params);
        return true;
    }

    public static final List<LinphoneCall> getLinphoneCalls(LinphoneCore lc) {
        // return a modifiable list
        return new ArrayList<LinphoneCall>(Arrays.asList(lc.getCalls()));
    }

    /**
     * 挂断电话
     */
    public void hangUp() {
        LinphoneCall currentCall = mLinphoneCore.getCurrentCall();
        if (currentCall != null) {
            mLinphoneCore.terminateCall(currentCall);
        } else if (mLinphoneCore.isInConference()) {
            mLinphoneCore.terminateConference();
        } else {
            mLinphoneCore.terminateAllCalls();
        }
    }

    public void setAudioMulticastAddr(String ip) {
        try {
            mLinphoneCore.enableAudioMulticast(true);
            mLinphoneCore.setAudioMulticastAddr(ip);
        } catch (LinphoneCoreException e) {

            e.printStackTrace();
        }
    }

    public String getAudioMulticastAddr() {
        return mLinphoneCore.getAudioMulticastAddr();
    }

    public void setVideoMulticastAddr(String ip) {
        try {
            mLinphoneCore.enableVideoMulticast(true);
            mLinphoneCore.setVideoMulticastAddr(ip);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

    public String getVideoMulticastAddr() {

        return mLinphoneCore.getVideoMulticastAddr();

    }

    /**
     * 是否静音
     *
     * @param isMicMuted
     */
    public void toggleMicro(boolean isMicMuted) {
        mLinphoneCore.muteMic(isMicMuted);
    }

    public void setVideoSizeByName(String value) {
        mLinphoneCore.setPreferredVideoSizeByName(value);
    }

    /**
     * 是否外放
     *
     * @param isSpeakerEnabled
     */
    public void toggleSpeaker(boolean isSpeakerEnabled) {
        mLinphoneCore.enableSpeaker(isSpeakerEnabled);
    }

    public void StartRecordVideoPath(String filePath) {
        mLinphoneCore.startConferenceRecording(filePath);
    }

    public void stopRecording() {
        mLinphoneCore.stopConferenceRecording();
    }

    public void setVideoSize(int width, int height) {
        mLinphoneCore.setPreferredVideoSize(new VideoSize(width, height));
    }

    public int getVideoWidth() {
        VideoSize preferredVideoSize = mLinphoneCore.getPreferredVideoSize();
        return preferredVideoSize.width;
    }

    public int getVideoHeight() {
        VideoSize preferredVideoSize = mLinphoneCore.getPreferredVideoSize();
        return preferredVideoSize.height;
    }

    public static void copyIfNotExist(Context context, int resourceId, String target) throws IOException {
        File fileToCopy = new File(target);
        if (!fileToCopy.exists()) {
            copyFromPackage(context, resourceId, fileToCopy.getName());
        }
    }

    public static void copyFromPackage(Context context, int resourceId, String target) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(target, 0);
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, readByte);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public static LpConfig getConfig(Context context) {
        LinphoneCore lc = getLc();
        if (lc != null) {
            return lc.getConfig();
        }

        if (LinphoneManager.isInstanceiated()) {
            Log.w("howard", "LinphoneManager not instanciated yet...");
            return LinphoneCoreFactory.instance().createLpConfig(context.getFilesDir().getAbsolutePath() + "/.linphonerc");
        }

        return LinphoneCoreFactory.instance().createLpConfig(LinphoneManager.getInstance().mLinphoneConfigFile);
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static LinphoneCore getLc() {
        if (!LinphoneManager.isInstanceiated()) {
            return null;
        }
        return LinphoneManager.getLcIfManagerNotDestroyOrNull();
    }

    private LinphoneNatPolicy getOrCreateNatPolicy() {
        LinphoneNatPolicy nat = mLinphoneCore.getNatPolicy();
        if (nat == null) {
            nat = mLinphoneCore.createNatPolicy();
        }
        return nat;
    }

    public void setStunServer(String stun) {
        Log.e("howard", "setStunServer");
        LinphoneNatPolicy nat = getOrCreateNatPolicy();
        nat.setStunServer(stun);

        if (stun != null && !stun.isEmpty()) {
            nat.enableStun(true);
        }
        mLinphoneCore.setNatPolicy(nat);
    }

    public static String getAddressDisplayName(LinphoneAddress address) {
        if (address.getDisplayName() != null) {
            return address.getDisplayName();
        } else {
            if (address.getUserName() != null) {
                return address.getUserName();
            } else {
                return address.asStringUriOnly();
            }
        }
    }
}
