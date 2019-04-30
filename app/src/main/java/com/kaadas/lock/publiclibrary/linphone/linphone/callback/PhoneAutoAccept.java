package com.kaadas.lock.publiclibrary.linphone.linphone.callback;

import org.linphone.core.LinphoneCall;

/**
 * Created by ${howard} on 2018/7/2.
 */

public class PhoneAutoAccept {
    /**
     * 自动接听电话
     * @param linphoneCall
     */
    public void incomingCall(LinphoneCall linphoneCall) {}
    /**
     * 电话接通
     */
    public void callConnected() {}
    /**
     * 释放通话
     */
    public void callReleased() {}
    public void Streaming(){}
    public void callFinish(){}
}
