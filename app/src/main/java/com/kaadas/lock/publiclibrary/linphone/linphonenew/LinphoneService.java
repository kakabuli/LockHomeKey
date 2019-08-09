package com.kaadas.lock.publiclibrary.linphone.linphonenew;

/*
LinphoneService.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.WindowManager;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.publiclibrary.NotificationManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.VideoActivity;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneAutoAccept;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.PhoneCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.callback.RegistrationCallback;
import com.kaadas.lock.publiclibrary.linphone.linphone.linphone.ContactsManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.linphone.LinphoneContact;
import com.kaadas.lock.publiclibrary.linphone.linphonenew.compatibility.Compatibility;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.MyLog;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.GeTui;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.Version;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Linphone service, reacting to Incoming calls, ...<br />
 * <p>
 * Roles include:<ul>
 * <li>Initializing LinphoneManager</li>
 * <li>Starting C libLinphone through LinphoneManager</li>
 * <li>Reacting to LinphoneManager state changes</li>
 * <li>Delegating GUI state change actions to GUI listener</li>
 * LinphoneService是标准的android后台服务，这个后台服务非常的关键，它需要完成的工作包括以下的内容。加载APP需要使用的资源，启动LinphoneManager这个APP的全局管理器，LinphoneManager 这个全局管理器会一方面管理LinphoneService 的实例，另一方面管理LinphoneCore这个核心实例，同时生成目前需要处理的事件通知的LinphoneCoreListenerBase实例并注册到LinphoneCore这个核心实例的事件监听器中。目前的LinphoneCoreListenerBase 实例只监听callState，globalState，registrationState这三个事件。如果这个时候有电话呼入，那么callState事件触发，callState事件当中state会是LinphoneCall.State.IncomingReceived，APP需要切换到电话呼入的操作界面。registrationState这个事件反馈的是用户的SIP账号到SIP服务器的注册状态，是成功注册还是注册失败等。
 */
public final class LinphoneService extends Service {
    /* Listener needs to be implemented in the Service as it calls
     * setLatestEventInfo and startActivity() which needs a context.
     */
    public static final String START_LINPHONE_LOGS = " ==== Phone information dump ====";


    private static LinphoneService instance;


    private LinphoneManager mLinphoneManager;

    public static boolean isReady() {
        return instance != null && instance.mTestDelayElapsed;
    }

    /**
     * @throws RuntimeException service not instantiated
     */
    public static LinphoneService instance() {
        try {
            if (isReady()) {
                return instance;
            } else {
                return instance;
            }
//            throw new RuntimeException("LinphoneService not instantiated yet");
        } catch (Exception e) {
            LogUtils.e("linphoneService" + e.toString());
            return instance;
        }

    }

    public Handler mHandler = new Handler();

    //	private boolean mTestDelayElapsed; // add a timer for testing
    private boolean mTestDelayElapsed = true; // no timer

    private boolean mDisableRegistrationStatus;
    private LinphoneCoreListenerBase mListener;
    private WindowManager mWindowManager;
    private LinphoneOverlay mOverlay;
    private Application.ActivityLifecycleCallbacks activityCallbacks;

    private static RegistrationCallback sRegistrationCallback;
    private static PhoneCallback sPhoneCallback;
    private static PhoneAutoAccept mAutoAccept;

    /*Believe me or not, but knowing the application visibility state on Android is a nightmare.
    After two days of hard work I ended with the following class, that does the job more or less reliabily.
    */
    class ActivityMonitor implements Application.ActivityLifecycleCallbacks {
        private ArrayList<Activity> activities = new ArrayList<Activity>();
        private boolean mActive = false;
        private int mRunningActivities = 0;

        class InactivityChecker implements Runnable {
            private boolean isCanceled;

            public void cancel() {
                isCanceled = true;
            }

            @Override
            public void run() {
                synchronized (LinphoneService.this) {
                    if (!isCanceled) {
                        if (ActivityMonitor.this.mRunningActivities == 0 && mActive) {
                            mActive = false;
                            LinphoneService.this.onBackgroundMode();
                        }
                    }
                }
            }
        }

        ;

        private InactivityChecker mLastChecker;

        @Override
        public synchronized void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i("Activity created:" + activity);
            if (!activities.contains(activity))
                activities.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i("Activity started:" + activity);
        }

        @Override
        public synchronized void onActivityResumed(Activity activity) {
            Log.i("Activity resumed:" + activity);
            if (activities.contains(activity)) {
                mRunningActivities++;
                Log.i("runningActivities=" + mRunningActivities);
                checkActivity();
            }

        }

        @Override
        public synchronized void onActivityPaused(Activity activity) {
            Log.i("Activity paused:" + activity);
            if (activities.contains(activity)) {
                mRunningActivities--;
                Log.i("runningActivities=" + mRunningActivities);
                checkActivity();
            }

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i("Activity stopped:" + activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public synchronized void onActivityDestroyed(Activity activity) {
            Log.i("Activity destroyed:" + activity);
            if (activities.contains(activity)) {
                activities.remove(activity);
            }
        }

        void startInactivityChecker() {
            if (mLastChecker != null) mLastChecker.cancel();
            LinphoneService.this.mHandler.postDelayed(
                    (mLastChecker = new InactivityChecker()), 2000);
        }

        void checkActivity() {

            if (mRunningActivities == 0) {
                if (mActive) startInactivityChecker();
            } else if (mRunningActivities > 0) {
                if (!mActive) {
                    mActive = true;
                    LinphoneService.this.onForegroundMode();
                }
                if (mLastChecker != null) {
                    mLastChecker.cancel();
                    mLastChecker = null;
                }
            }
        }
    }

    protected void onBackgroundMode() {
        Log.i("App has entered background mode");
        if (LinphonePreferences.instance() != null && LinphonePreferences.instance().isFriendlistsubscriptionEnabled()) {
            if (LinphoneManager.isInstanciated())
                LinphoneManager.getInstance().subscribeFriendList(false);
        }
    }

    protected void onForegroundMode() {
        Log.i("App has left background mode");
        if (LinphonePreferences.instance() != null && LinphonePreferences.instance().isFriendlistsubscriptionEnabled()) {
            if (LinphoneManager.isInstanciated())
                LinphoneManager.getInstance().subscribeFriendList(true);
        }
    }

    private void setupActivityMonitor() {
        if (activityCallbacks != null) return;
        getApplication().registerActivityLifecycleCallbacks(activityCallbacks = new ActivityMonitor());
    }

    public boolean displayServiceNotification() {
        return LinphonePreferences.instance().getServiceNotificationVisibility();
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager.silentForegroundNotification(this);
        initData();
        return super.onStartCommand(intent, flags, startId);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private void initData() {
        setupActivityMonitor();
        // In case restart after a crash. Main in LinphoneActivity
        //  mNotificationTitle = "Linphone Service";
        MyLog.getInstance().save(" 启动linphone service.... ");
        // Needed in order for the two next calls to succeed, libraries must have been loaded first
        LinphonePreferences.instance().setContext(getBaseContext());
        LinphoneCoreFactory.instance().setLogCollectionPath(getFilesDir().getAbsolutePath());
        boolean isDebugEnabled = LinphonePreferences.instance().isDebugEnabled();
        LinphoneCoreFactory.instance().enableLogCollection(isDebugEnabled);
        LinphoneCoreFactory.instance().setDebugMode(isDebugEnabled, getString(R.string.app_name));
        // Dump some debugging information to the logs
        Log.i(START_LINPHONE_LOGS);
        dumpDeviceInformation();
        dumpInstalledLinphoneInformation();

        //Disable service notification for Android O
        if ((Version.sdkAboveOrEqual(Version.API26_O_80))) {
            LinphonePreferences.instance().setServiceNotificationVisibility(false);
            mDisableRegistrationStatus = true;
        }

        Compatibility.CreateChannel(this);


        if (LinphoneManager.getInstance() == null) {
            mLinphoneManager = new LinphoneManager(LinphoneService.this);
        }

        instance = this; // instance is ready once linphone manager has been created
        incomingReceivedActivityName = LinphonePreferences.instance().getActivityToLaunchOnIncomingReceived();
        try {
            incomingReceivedActivity = (Class<? extends Activity>) Class.forName(incomingReceivedActivityName);
        } catch (ClassNotFoundException e) {
            Log.e(e);
        }


        int linphone_port = LinphoneManager.getLc().getSipTransportPort();
        if (linphone_port > 0) {
            MyApplication.getInstance().setLinphone_port(linphone_port);
        }

        LinphoneManager.getLc().addListener(mListener = new LinphoneCoreListenerBase() {
            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                LogUtils.d("linphone状态改变 " + call.toString() + " " + "state " + state.toString() + " message " + message);
                if (instance == null) {
                    Log.i("Service not ready, discarding call state change to ", state.toString());
                    return;
                }
                if (state == LinphoneCall.State.IncomingReceived) {
                    boolean videoActivity = (boolean) SPUtils.get( "videoActivity", false);
                    if (mAutoAccept != null) {
                        mAutoAccept.incomingCall(call);
                    }
                    if (sPhoneCallback != null) {
                        sPhoneCallback.incomingCall(call);
                    }
                }
                if (state == State.Connected) {
                    if (sPhoneCallback != null) {
                        sPhoneCallback.callConnected();
                    }
                    if (mAutoAccept != null) {
                        mAutoAccept.callConnected();
                    }
                }
                if (state == State.StreamsRunning) {
                    if (sPhoneCallback != null) {
                        sPhoneCallback.Streaming();
                    }
                    if (mAutoAccept != null) {
                        mAutoAccept.Streaming();
                    }
                }
                if (state == State.OutgoingInit) {
                    //Enter the MODE_IN_COMMUNICATION mode as soon as possible, so that ringback
                    //is heard normally in earpiece or bluetooth receiver.
                    if (sPhoneCallback != null) {

                        sPhoneCallback.outgoingInit();
                    }
                }
                if (state == State.CallEnd || state == State.CallReleased || state == State.Error) {
                    if (sPhoneCallback != null) {
                        sPhoneCallback.error();
                    }
                    if (mAutoAccept != null) {
                        mAutoAccept.callReleased();
                    }
                    if (mAutoAccept != null && state == State.CallReleased) {
                        mAutoAccept.callFinish();
                    }
                    destroyOverlay();
                }


                if (state == State.CallEnd && call.getCallLog().getStatus() == CallStatus.Missed) {
                    android.util.Log.e(GeTui.VideoLog, "LinphoneService===>state:"+State.CallEnd+" call.getCallLog().getStatus():"+call.getCallLog().getStatus());
                    int missedCallCount = LinphoneManager.getLcIfManagerNotDestroyedOrNull().getMissedCallsCount();
                    String body;
                    if (missedCallCount > 1) {
//						body = getString(R.string.missed_calls_notif_body).replace("%i", String.valueOf(missedCallCount));
                    } else {
                        LinphoneAddress address = call.getRemoteAddress();
                        LinphoneContact c = ContactsManager.getInstance().findContactFromAddress(address);
                        if (c != null) {
                            body = c.getFullName();
                        } else {
                            body = address.getDisplayName();
                            if (body == null) {
                                body = address.asStringUriOnly();
                            }
                        }
                    }

                  //  long diff=System.currentTimeMillis() - MyApplication.getInstance().getIsComingTime();
                  //  android.util.Log.e(GeTui.VideoLog,"LinphoneService...."+diff);
                 //   if (diff < 25 * 1000 && VideoVActivity.isRunning) {
                    if (VideoVActivity.isRunning) {
                        // 屏幕亮了
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean  isAlready= (boolean) SPUtils.get(Constants.ALREADY_TOAST,false);
                                if(!isAlready){
                                    Toast.makeText(LinphoneService.this, getResources().getString(R.string.return_code_409), Toast.LENGTH_SHORT).show();
                                    //设备正忙
                                }
                                SPUtils.remove(Constants.ALREADY_TOAST);

                            }
                        }, 800);
                    }

                }

            }

            @Override
            public void globalState(LinphoneCore lc, LinphoneCore.GlobalState state, String message) {
                if (!mDisableRegistrationStatus && state == GlobalState.GlobalOn && displayServiceNotification()) {
//					sendNotification(IC_LEVEL_ORANGE, R.string.notification_started);
                }
            }

            //用户的SIP账号到SIP服务器的注册状态,注册状态监听
            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState registrationState, String smessage) {
                String state = registrationState.toString();
                if (sRegistrationCallback != null && state.equals(LinphoneCore.RegistrationState.RegistrationNone.toString())) {
                    sRegistrationCallback.registrationNone();
                } else if (sRegistrationCallback != null && state.equals(LinphoneCore.RegistrationState.RegistrationProgress.toString())) {
                    sRegistrationCallback.registrationProgress();
                } else if (sRegistrationCallback != null && state.equals(LinphoneCore.RegistrationState.RegistrationOk.toString())) {
                    sRegistrationCallback.registrationOk();
                } else if (sRegistrationCallback != null && state.equals(LinphoneCore.RegistrationState.RegistrationCleared.toString())) {
                    sRegistrationCallback.registrationCleared();
                } else if (sRegistrationCallback != null && state.equals(LinphoneCore.RegistrationState.RegistrationFailed.toString())) {
                    sRegistrationCallback.registrationFailed();
                }

            }
        });


        try {
            mStartForeground = getClass().getMethod("startForeground", mStartFgSign);
            mStopForeground = getClass().getMethod("stopForeground", mStopFgSign);
        } catch (NoSuchMethodException e) {
            Log.e(e, "Couldn't find startForeground or stopForeground");
        }

        if (!Version.sdkAboveOrEqual(Version.API26_O_80)
                || (ContactsManager.getInstance() != null && ContactsManager.getInstance().hasContactsAccess())) {
            getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, ContactsManager.getInstance());
        }

        if (displayServiceNotification()) {
            //  startForegroundCompat(NOTIF_ID, mNotif);
        }

        if (!mTestDelayElapsed) {
            // Only used when testing. Simulates a 5 seconds delay for launching service
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTestDelayElapsed = true;
                }
            }, 5000);
        }

        //make sure the application will at least wakes up every 10 mn
        Intent intent = new Intent(this, KeepAliveReceiver.class);
        PendingIntent keepAlivePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = ((AlarmManager) this.getSystemService(Context.ALARM_SERVICE));
        Compatibility.scheduleAlarm(alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 600000, keepAlivePendingIntent);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    public static void addRegistrationCallback(RegistrationCallback registrationCallback) {
        sRegistrationCallback = registrationCallback;
    }

    public static void addPhoneCallback(PhoneCallback phoneCallback) {
        sPhoneCallback = phoneCallback;
    }

    public static void addAutoAccept(PhoneAutoAccept autoAccept) {
        mAutoAccept = autoAccept;
    }

    public static void removeRegistrationCallback() {
        if (sRegistrationCallback != null) {
            sRegistrationCallback = null;
        }
    }

    public static void removePhoneCallback() {
        if (sPhoneCallback != null) {
            sPhoneCallback = null;
        }
    }

    public void removeAllCallback() {
        removePhoneCallback();
        removeRegistrationCallback();
    }

    public void createOverlay() {
        if (mOverlay != null) destroyOverlay();

        LinphoneCall call = LinphoneManager.getLc().getCurrentCall();
        if (call == null || !call.getCurrentParams().getVideoEnabled()) return;

        mOverlay = new LinphoneOverlay(this);
        WindowManager.LayoutParams params = mOverlay.getWindowManagerLayoutParams();
        params.x = 0;
        params.y = 0;
        mWindowManager.addView(mOverlay, params);
    }

    public void destroyOverlay() {
        if (mOverlay != null) {
            mWindowManager.removeViewImmediate(mOverlay);
            mOverlay.destroy();
        }
        mOverlay = null;
    }

    private enum IncallIconState {INCALL, PAUSE, VIDEO, IDLE}

    private IncallIconState mCurrentIncallIconState = IncallIconState.IDLE;

    private synchronized void setIncallIcon(IncallIconState state) {
        if (state == mCurrentIncallIconState) return;
        mCurrentIncallIconState = state;

        switch (state) {
            case IDLE:
                return;
        }

        if (LinphoneManager.getLc().getCallsNb() == 0) {
            return;
        }

        LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
        String userName = call.getRemoteAddress().getUserName();
        String domain = call.getRemoteAddress().getDomain();
        String displayName = call.getRemoteAddress().getDisplayName();
        LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(userName, domain, null);
        address.setDisplayName(displayName);

        LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(address);
        Uri pictureUri = contact != null ? null : null;
        String name = address.getDisplayName() == null ? address.getUserName() : address.getDisplayName();

    }

    public void refreshIncallIcon(LinphoneCall currentCall) {
        LinphoneCore lc = LinphoneManager.getLc();
        if (currentCall != null) {
            if (currentCall.getCurrentParams().getVideoEnabled() && currentCall.cameraEnabled()) {
                // checking first current params is mandatory
                setIncallIcon(IncallIconState.VIDEO);
            } else {
                setIncallIcon(IncallIconState.INCALL);
            }
        } else if (lc.getCallsNb() == 0) {
            setIncallIcon(IncallIconState.IDLE);
        } else if (lc.isInConference()) {
            setIncallIcon(IncallIconState.INCALL);
        } else {
            setIncallIcon(IncallIconState.PAUSE);
        }
    }

    @Deprecated
    public void addNotification(Intent onClickIntent, int iconResourceID, String title, String message) {
        //   addCustomNotification(onClickIntent, iconResourceID, title, message, true);
    }


    public void displayMessageNotification(String to, String fromSipUri, String fromName, String message) {


    }


    private static final Class<?>[] mSetFgSign = new Class[]{boolean.class};
    private static final Class<?>[] mStartFgSign = new Class[]{
            int.class, Notification.class};
    private static final Class<?>[] mStopFgSign = new Class[]{boolean.class};

    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    private String incomingReceivedActivityName;
    private Class<? extends Activity> incomingReceivedActivity = VideoActivity.class;




    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */

    private void dumpDeviceInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("DEVICE=").append(Build.DEVICE).append("\n");
        sb.append("MODEL=").append(Build.MODEL).append("\n");
        sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
        sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Supported ABIs=");
        for (String abi : Version.getCpuAbis()) {
            sb.append(abi + ", ");
        }
        sb.append("\n");
        Log.i(sb.toString());
    }

    private void dumpInstalledLinphoneInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException nnfe) {
        }

        if (info != null) {
            Log.i("Linphone version is ", info.versionName + " (" + info.versionCode + ")");
        } else {
            Log.i("Linphone version is unknown");
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (getResources().getBoolean(R.bool.kill_service_with_task_manager)) {
//            Log.d("Task removed, stop service");
            // If push is enabled, don't unregister account, otherwise do unregister
            if (LinphonePreferences.instance().isPushNotificationEnabled()) {
                LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
                if (lc != null) lc.setNetworkReachable(false);
            }
            stopSelf();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public synchronized void onDestroy() {
        LogUtils.e("walter", "linphoneservice onDestroy");
        Log.e(GeTui.VideoLog,"linphonservice...onDestory...");
        MyLog.getInstance().save("linphonservice...onDestory...");
        if (activityCallbacks != null) {
            getApplication().unregisterActivityLifecycleCallbacks(activityCallbacks);
            activityCallbacks = null;
        }

        destroyOverlay();
        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }

        instance = null;
        LinphoneManager.destroy();

        System.exit(0);

        super.onDestroy();
    }

}

