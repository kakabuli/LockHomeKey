package com.kaadas.lock.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.fragment.DeviceFragment;
import com.kaadas.lock.fragment.HomePageFragment;
import com.kaadas.lock.fragment.PersonalCenterFragment;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.MainActivityPresenter;
import com.kaadas.lock.mvp.view.IMainActivityView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.ota.OTADialogActivity;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NotifyRefreshActivity;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.Rom;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;
import com.kaadas.lock.widget.NoScrollViewPager;
import com.kaidishi.lock.service.GeTuiPushService;

import net.sdvn.cmapi.CMAPI;
import net.sdvn.cmapi.ConnectionService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseBleActivity<IMainActivityView, MainActivityPresenter<IMainActivityView>>
        implements ViewPager.OnPageChangeListener, IMainActivityView, RadioGroup.OnCheckedChangeListener{
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;

    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.home_view_pager)
    NoScrollViewPager homeViewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private boolean isOnBackground = false;
    private static MainActivity instance;
    private static final int REQUEST_CODE_VPN_SERVICE = 11;

    public boolean isSelectHome = true;
    private NetWorkChangReceiver netWorkChangReceiver;
    private boolean isRegistered=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        PermissionUtil.getInstance().requestPermission(PermissionUtil.getInstance().permission, this);
        rg.setOnCheckedChangeListener(this);
        if (MyApplication.getInstance().getMqttService().getMqttClient() == null || !MyApplication.getInstance().getMqttService().getMqttClient().isConnected()) {
            MyApplication.getInstance().getMqttService().mqttConnection(); //连接mqtt
        }
        fragments.add(new HomePageFragment());
        fragments.add(new DeviceFragment());
        fragments.add(new PersonalCenterFragment());

        instance = this;
        homeViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        //首页的fragment不重新加载，导致各种问题
        homeViewPager.setOffscreenPageLimit(fragments.size());

        checkVpnService();

        //设置Linphone的监听

        mPresenter.initLinphone();

        boolean isfromlogin= getIntent().getBooleanExtra(Constants.ISFROMLOGIN,false);
        boolean ispush= (boolean) SPUtils.get(Constants.PUSHID,false);
        Log.e(GeTui.VideoLog,"isfromlogin:"+isfromlogin);
        if(isfromlogin){
             mPresenter.uploadpushmethod();
        }else if(!isfromlogin && !ispush){
             Log.e(GeTui.VideoLog,"重新上传pushid.......");
             mPresenter.uploadpushmethod();
        }
        registerNetwork();
        startcallmethod();
    }

    @Override
    protected MainActivityPresenter<IMainActivityView> createPresent() {
        return new MainActivityPresenter<>();
    }


    public interface HomeSelectListener {
        void onHomeSelectChange(boolean isSelect);
    }

    private List<HomeSelectListener> listeners = new ArrayList<>();

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_one:
                homeViewPager.setCurrentItem(0);
                break;
            case R.id.rb_two:
                homeViewPager.setCurrentItem(1);
                break;
            case R.id.rb_three:
                homeViewPager.setCurrentItem(2);
                break;
        }
    }


    public boolean isOnBackground() {
        return isOnBackground;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isOnBackground = false;
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }


    public static final MainActivity instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("LinphoneActivity not instantiated yet");
    }


    //检查vpn授权
    public void checkVpnService() {
        Intent prepare = ConnectionService.prepare(this);
        boolean resultvpn = prepare == null ? true : false;
        net.sdvn.cmapi.util.LogUtils.d(resultvpn + " 已授权 " + " 未授权 ");
        if (prepare != null) {
            startActivityForResult(prepare, REQUEST_CODE_VPN_SERVICE);
        } else {
            onActivityResult(REQUEST_CODE_VPN_SERVICE, RESULT_OK, null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  vpn  授权
        if (requestCode == REQUEST_CODE_VPN_SERVICE) {
            CMAPI.getInstance().onVpnPrepareResult(requestCode, resultCode);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            isSelectHome = true;
            for (HomeSelectListener listener : listeners) {
                listener.onHomeSelectChange(true);
            }
        } else {
            isSelectHome = false;
            for (HomeSelectListener listener : listeners) {
                listener.onHomeSelectChange(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    @Override
    public void onWarringUp(String warringContent) {
        ToastUtil.getInstance().showLong(warringContent);
    }

    @Override
    public void onDeviceInBoot(BleLockInfo bleLockInfo) {
        //todo ota 处理
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OTADialogActivity.class);
        intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);  //需要使用
        startActivity(intent);
    }

    @Override
    public void onCatEyeCallIn(CateEyeInfo cateEyeInfo) {
        Log.e(GeTui.VideoLog,"MainActivity---->跳入=====>VideoVActivity:"+cateEyeInfo);
        Intent intent = new Intent(this, VideoVActivity.class);
        intent.putExtra(KeyConstants.IS_CALL_IN, true);
        intent.putExtra(KeyConstants.CATE_INFO, cateEyeInfo);
        startActivity(intent);
    }

    @Override
    public void onGwEvent(int eventType, String deviceId,String gatewayId) {
        GatewayInfo gatewayInfo= MyApplication.getInstance().getGatewayById(gatewayId);
        if (gatewayInfo!=null) {
            String nickName = MyApplication.getInstance().getNickByDeviceId(deviceId);
            String content = null;
            switch (eventType) {
                case CatEyeEvent.EVENT_PIR:
                    content = String.format(getString(R.string.pir_notify), nickName);
                    break;
                case CatEyeEvent.EVENT_DOOR_BELL:
//                content = String.format(getString(R.string.door_bell), nickName);
//                ToastUtil.getInstance().showShort(content);
                    break;
                case CatEyeEvent.EVENT_HEAD_LOST:
                    content = String.format(getString(R.string.head_lost_notify), nickName);
                    break;
                case CatEyeEvent.EVENT_HOST_LOST:
                    content = String.format(getString(R.string.host_lost_notify), nickName);
                    break;
                case CatEyeEvent.EVENT_LOW_POWER:
                    content = String.format(getString(R.string.low_power_notify), nickName);
                    break;
            }
            ToastUtil.getInstance().showLong(content);
            LogUtils.e("猫眼报警的内容为   " + content);
        }
    }

    @Override
    public void uploadpush(BaseResult baseResult) {
        String code= baseResult.getCode();
        if(code.equals("200")){
            Log.e(GeTui.VideoLog,"push上传成功");
            SPUtils.put(Constants.PUSHID,true);
        }else{
            Log.e(GeTui.VideoLog,"push上传失败");
        }
    }

    @Override
    public void onGwLockEvent(int alarmCode, int clusterID, String deviceId,String gatewayId) {
        GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
        if (gatewayInfo != null) {
            String str = "";
            String nickName = MyApplication.getInstance().getNickByDeviceId(deviceId);
            if (clusterID == 257) {
                switch (alarmCode) {
                    case 0:    //门锁堵转报警
                        str = String.format(getString(R.string.lock_blocked_notify), nickName);
                        ToastUtil.getInstance().showShort(str);
                        break;
                    case 1:
                        str = String.format(getString(R.string.lock_resect_notify), nickName);
                        ToastUtil.getInstance().showShort(str);
                        break;
                    case 4:
                        str = String.format(getString(R.string.lock_system_notify), nickName);
                        ToastUtil.getInstance().showShort(str);
                        break;
                    case 6:
                        str = String.format(getString(R.string.lock_pick_proof_notify), nickName);
                        ToastUtil.getInstance().showShort(str);
                        break;
                    case 9:
                        str = String.format(getString(R.string.lock_stress_alarm_notify), nickName);
                        ToastUtil.getInstance().showShort(str);
                        break;
                }
                //电量
            } else if (clusterID == 1) {
                switch (alarmCode) {
                    case 16:
                        str = String.format(getString(R.string.low_power_notify), nickName);
                        ToastUtil.getInstance().showShort(str);
                        break;
                }
            }
        }
    }
    public NoScrollViewPager getViewPager() {

        return homeViewPager;
    }
    private Class userPushService = GeTuiPushService.class;
    Timer timer;
    private void    startcallmethod() {
        long startTime = 2500;
        if (Rom.isFlyme()) {
            startTime = 5000;
        }
        final String Tag1 = "sip_kaidishi";
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int linphone_port = MyApplication.getInstance().getLinphone_port();
                    String sip_pacage_invite = MyApplication.getInstance().getSip_package_invite();
                    Log.e(GeTui.VideoLog,"port:"+linphone_port);
                    Log.e(GeTui.VideoLog,"sip_pacage_invite:"+sip_pacage_invite);
                    if (!TextUtils.isEmpty(sip_pacage_invite)) {
                        if (linphone_port > 0) {
                            timer.cancel();
                            timer = null;
                            try {
                                //建立udp的服务
                                DatagramSocket datagramSocket = new DatagramSocket();
                                //准备数据，把数据封装到数据包中。
                                //	String data = TestUdp.und_package;
                                //创建了一个数据包
                                InetAddress inetAddress = InetAddress.getLocalHost();
                                String ipaddress = inetAddress.getHostAddress();
                                DatagramPacket packet = new DatagramPacket(sip_pacage_invite.getBytes(), sip_pacage_invite.getBytes().length, inetAddress, linphone_port);
                                //调用udp的服务发送数据包
                                datagramSocket.send(packet);
                                //关闭资源 ---实际上就是释放占用的端口号
                                datagramSocket.close();
                            } catch (SocketException e) {
                                e.printStackTrace();
                                Log.e(Tag1, "SocketException:" + e.getMessage());
                            }  /* catch (UnknownHostException e) {
								e.printStackTrace();
								Log.e(Tag1,"UnknownHostException"+e.getMessage());
							}  */ catch (IOException e) {
                                e.printStackTrace();
                                Log.e(Tag1, "IOException" + e.getMessage());
                            }
                        } else {
                            Log.e(Tag1, "获取端口失败");
                        }
                        Log.e(Tag1, "获取的端口是:" + linphone_port);
                    } else {
                        Log.e(Tag1, "Sip数据包为空");
                        timer.cancel();
                        timer = null;
                    }
                }
            }, startTime, 500);
            // 2500
        }
    }
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
//                MainActivity.this.finish();
//                System.exit(0);
                  moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //注册网络状态监听广播
    private void registerNetwork(){
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        registerReceiver(netWorkChangReceiver, filter);
        isRegistered = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegistered){
            if (netWorkChangReceiver!=null){
                unregisterReceiver(netWorkChangReceiver);
            }
        }

    }
}
