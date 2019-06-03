package com.kaadas.lock.activity.cateye;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.ForecastAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.VideoPresenter;
import com.kaadas.lock.mvp.view.cateye.IVideoView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.ftp.GeTui;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class VideoVActivity extends BaseActivity<IVideoView, VideoPresenter<IVideoView>> implements
        DiscreteScrollView.ScrollStateChangeListener<ForecastAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ForecastAdapter.ViewHolder>,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, IVideoView {

    DiscreteScrollView cityPicker;
    LinearLayout video_h_no_lock;
    ImageView video_start_play;
    TextView video_connecting_tv;
    ImageView video_hang_up;
    TextView video_play_time;
    ImageView video_v_go;
    ImageView iv_back;
    SurfaceView video_v_surfaceview;
    ForecastAdapter forecastAdapter = null;
    SurfaceView videoPreview;
    ImageView hangup;
    CheckBox cbScreenShot;
    CheckBox cbMute;
    CheckBox cbHandsFree;
    CheckBox cbScreenRecord;
    TextView tvTitle;


    private int selectPostion = -1;
    private CateEyeInfo cateEyeInfo;
    private boolean isCallIn;
    private List<GwLockInfo> gwLockInfos;
    private static final int REQUEST_CODE_CALL_COMING = 101;
    public static boolean isRunning = false;
    private String Tag = "猫眼通话界面 ";
    private static final int REQUEST_PERMISSION_REQUEST_CODE = 102;
    private boolean isOpening; //正在开门
    private boolean isClosing; //正在关门

    private LinearLayout ll_video_control1;
    private LinearLayout ll_video_control2;
    private CheckBox cbScreenShot2;
    private CheckBox cbMute2;
    private CheckBox cbHandsFree2;
    private CheckBox cbScreenRecord2;
    private RelativeLayout rl_bottom;
    private RelativeLayout rl_title_bar;
    private boolean isLand; //是否横屏
    private ImageView hangup2;
    private String videoTime;
    TextView mTvLossratev;
    TextView mTvLossratea;
    private TextView mTvframerate;
    Task task = null;
    private Handler mHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                mTvLossratev.setText("video loss rate " + LinphoneHelper.getStatus(1) + "(" + LinphoneHelper.getTotalLossRate(1) + ")");
                mTvLossratea.setText("audio loss rate " + LinphoneHelper.getStatus(0) + "(" + LinphoneHelper.getTotalLossRate(0) + ")");
                mTvframerate.setText("frame rate " + LinphoneHelper.getReceivedFramerate());
            }
        }
    };
    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.e(Tag, "创建");
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置屏幕无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置该界面全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_v);
        ButterKnife.bind(this);
        isRunning = true;
        //找到View
        findViewByOrientation();
        initData();
        initView();
        requestPermissions();
        mPresenter.init(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        task = new Task();
        LogUtils.d("davi task " + task);
        timer.schedule(task, 0, 1000);
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 2;
            mHandler2.sendMessage(message);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentOrientation = newConfig.orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {  //横屏
            isLand = true;

            //根据竖屏时的状态显示
            if (ll_video_control1.getVisibility() == View.VISIBLE) {
                ll_video_control2.setVisibility(View.VISIBLE);
            } else {
                ll_video_control2.setVisibility(View.GONE);
            }
            //隐藏 显示时间的View
            video_play_time.setVisibility(View.GONE);
            video_v_go.setImageResource(R.mipmap.video_to_portrait);
            ll_video_control1.setVisibility(View.GONE);
            rl_bottom.setVisibility(View.GONE);
            rl_title_bar.setVisibility(View.GONE);
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            isLand = false;
            if (ll_video_control2.getVisibility() == View.VISIBLE) {
                ll_video_control1.setVisibility(View.VISIBLE);
            } else {
                ll_video_control1.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(videoTime)) {
                //隐藏 显示时间的View
                video_play_time.setVisibility(View.GONE);
            } else {
                //隐藏 显示时间的View
                video_play_time.setVisibility(View.VISIBLE);
            }

            video_v_go.setImageResource(R.mipmap.video_full_screen);
            ll_video_control2.setVisibility(View.GONE);
            rl_bottom.setVisibility(View.VISIBLE);
            rl_title_bar.setVisibility(View.VISIBLE);
        }

    }


    private void findViewByOrientation() {
        cityPicker = findViewById(R.id.forecast_city_picker);
        video_h_no_lock = findViewById(R.id.video_h_no_lock);
        video_start_play = findViewById(R.id.video_start_play);
        video_connecting_tv = findViewById(R.id.video_connecting_tv);
        video_hang_up = findViewById(R.id.video_hang_up);
        video_play_time = findViewById(R.id.video_play_time);
        video_v_go = findViewById(R.id.video_v_full);
        iv_back = findViewById(R.id.iv_back);
        video_v_surfaceview = findViewById(R.id.video_v_surfaceview);
        videoPreview = findViewById(R.id.video_preview);
        hangup = findViewById(R.id.iv_hangup);
        cbScreenShot = findViewById(R.id.cb_screen_shot);
        cbMute = findViewById(R.id.cb_mute);
        cbHandsFree = findViewById(R.id.cb_hands_free);
        cbScreenRecord = findViewById(R.id.cb_screen_record);
        tvTitle = findViewById(R.id.tv_title);

        hangup2 = findViewById(R.id.iv_hangup2);
        cbScreenShot2 = findViewById(R.id.cb_screen_shot2);
        cbMute2 = findViewById(R.id.cb_mute2);
        cbHandsFree2 = findViewById(R.id.cb_hands_free2);
        cbScreenRecord2 = findViewById(R.id.cb_screen_record2);

        ll_video_control1 = findViewById(R.id.ll_video_control1);
        ll_video_control2 = findViewById(R.id.ll_video_control2);
        rl_bottom = findViewById(R.id.rl_bottom);
        rl_title_bar = findViewById(R.id.rl_title_bar);

        video_v_go.setVisibility(View.GONE);

        mTvLossratev = (TextView) findViewById(R.id.tv_videolossrate);
        mTvLossratea = (TextView) findViewById(R.id.tv_audiolossrate);
        mTvframerate = (TextView) findViewById(R.id.tv_framerate);
    }


    private void requestPermissions() {
        //Manifest.permission.RECORD_AUDIO
        String[] strings = checkPermission(new String[]{Manifest.permission.RECORD_AUDIO});
        if (strings.length > 0) {
            ActivityCompat.requestPermissions(this, strings, REQUEST_PERMISSION_REQUEST_CODE);
        }
    }

    //检查权限
    public String[] checkPermission(String[] permissions) {
        List<String> noGrantedPermission = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                noGrantedPermission.add(permission);
            }
        }
        String[] permission = new String[noGrantedPermission.size()];
        return noGrantedPermission.toArray(permission);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LinphoneHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LinphoneHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        MemeManager.getInstance().videoActivityDisconnectMeme();
        LinphoneHelper.onDestroy();
        super.onDestroy();
        LogUtils.e(Tag,"界面被销毁");
        mHandler2.removeCallbacksAndMessages(null);
        if(timer!=null){
            timer.cancel();
        }
        fileHander.removeCallbacksAndMessages(null);

    }

    @Override
    protected VideoPresenter<IVideoView> createPresent() {
        return new VideoPresenter<>();
    }

    private void initData() {
        cateEyeInfo = (CateEyeInfo) getIntent().getSerializableExtra(KeyConstants.CATE_INFO);
        isCallIn = (boolean) getIntent().getSerializableExtra(KeyConstants.IS_CALL_IN);

        String gwID = cateEyeInfo.getGwID();
        List<HomeShowBean> homeShowDevices = MyApplication.getInstance().getHomeShowDevices();
        gwLockInfos = new ArrayList<>();
        //获取跟猫眼通一网关下的锁
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK) {
                GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                if (gwLockInfo.getGwID().equals(gwID)) {
                    gwLockInfos.add(gwLockInfo);
                }
            }
        }
        if (isCallIn) {
            Intent intent = new Intent(this, CallComingActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CALL_COMING);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CALL_COMING) { //来电界面回调
            boolean isAcceptCall = data.getBooleanExtra(KeyConstants.IS_ACCEPT_CALL, false);
            if (isAcceptCall) {  //接听
                LogUtils.e(Tag,"接听了电话");
                mPresenter.listenerCallStatus();
                String mDeviceIp = MemeManager.getInstance().getDeviceIp();
                Log.e(GeTui.VideoLog,"接听电话:"+mDeviceIp);
                LinphoneHelper.acceptCall(mDeviceIp);
                acceptCall();
            } else { //挂断
                LogUtils.e(Tag,"挂断了电话");
                mPresenter.hangup();
                finish();
            }
        }
    }

    /**
     * 接听电话的界面切换
     */
    public void acceptCall() {
        video_connecting_tv.setVisibility(View.GONE);
        video_hang_up.setVisibility(View.GONE);
        if (isLand) {
            video_play_time.setVisibility(View.GONE);
        } else { //竖屏的时候才显示
            video_play_time.setVisibility(View.VISIBLE);
        }

        video_v_surfaceview.setVisibility(View.VISIBLE);
        //播放按钮
        video_start_play.setVisibility(View.GONE);
        //接通了猫眼  显示视频控制界面
        if (isLand) {
            ll_video_control2.setVisibility(View.VISIBLE);
        } else {
            ll_video_control1.setVisibility(View.VISIBLE);
        }

        video_v_go.setVisibility(View.VISIBLE);
    }

    private void initView() {
        video_start_play.setOnClickListener(this);
        video_v_go.setOnClickListener(this);
        if (iv_back != null) {
            iv_back.setOnClickListener(this);
        }
        if (cityPicker != null) {
            cityPicker.setSlideOnFling(true);
            forecastAdapter = new ForecastAdapter(gwLockInfos, this);
            cityPicker.setAdapter(forecastAdapter);
            cityPicker.addOnItemChangedListener(this);
            cityPicker.addScrollStateChangeListener(this);
            cityPicker.scrollToPosition(1);
            cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());
            cityPicker.setOffscreenItems(300);
            cityPicker.setOverScrollEnabled(false);
            forecastAdapter.setOnItemClickItem(new ForecastAdapter.OnItemClickItem() {
                @Override
                public void onItemClickItemMethod(int position) {
                    if (selectPostion != -1 && position == selectPostion) {
                        LogUtils.e(Tag,"当前状态是   isOpening    " + isOpening + "   isClosing   " + isClosing);
                        if (isOpening) {
                            ToastUtil.getInstance().showShort(R.string.is_opening_try_latter);
                            return;
                        }
                        if (isClosing) {
                            ToastUtil.getInstance().showShort(R.string.lock_already_open);
                            return;
                        }
                        LogUtils.e(Tag,"执行开门  ");
                        GwLockInfo gwLockInfo = gwLockInfos.get(position);
                        if (gwLockInfo.getServerInfo().getEvent_str().equals("offline")){
                            ToastUtil.getInstance().showShort(getString(R.string.wifi_alreade_offline));
                            return;
                        }
                        if (NetUtil.isNetworkAvailable()){
                            mPresenter.openLock(gwLockInfo);
                        }else{
                            ToastUtil.getInstance().showShort(getString(R.string.wifi_alreade_offline));
                            return;
                        }
                    }
                }
            });
            if (tvTitle != null) {
                if(!TextUtils.isEmpty(cateEyeInfo.getServerInfo().getNickName())){
                    tvTitle.setText(cateEyeInfo.getServerInfo().getNickName());
                }else {
                    tvTitle.setText(cateEyeInfo.getServerInfo().getDeviceId());
                }



            }
        }


        cbHandsFree.setOnCheckedChangeListener(this);
        cbMute.setOnCheckedChangeListener(this);
        cbScreenRecord.setOnCheckedChangeListener(this);
        cbScreenShot.setOnCheckedChangeListener(this);
        hangup.setOnClickListener(this);

        cbHandsFree2.setOnCheckedChangeListener(this);
        cbMute2.setOnCheckedChangeListener(this);
        cbScreenRecord2.setOnCheckedChangeListener(this);
        cbScreenShot2.setOnCheckedChangeListener(this);

        hangup2.setOnClickListener(this);
        ll_video_control1.setVisibility(View.GONE);
        ll_video_control2.setVisibility(View.GONE);
        video_v_go.setVisibility(View.GONE);

        if (gwLockInfos.size() == 0) {
            if (cityPicker != null) {
                cityPicker.setVisibility(View.GONE);
            }
            if (video_h_no_lock != null) {
                video_h_no_lock.setVisibility(View.VISIBLE);
            }
        }
        LinphoneHelper.setAndroidVideoWindow(new SurfaceView[]{video_v_surfaceview}, new SurfaceView[]{videoPreview});
    }

    @Override
    public void onCurrentItemChanged(@Nullable ForecastAdapter.ViewHolder holder, int position) {
        selectPostion = position;
        if (holder != null) {
            holder.showText();
        }
    }


    @Override
    public void onScrollStart(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        holder.hideText();
    }

    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ForecastAdapter.ViewHolder currentHolder,
            @Nullable ForecastAdapter.ViewHolder newHolder) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_start_play:  //点击呼叫
                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtil.getInstance().showLong(R.string.current_net_not_enable);
                    return;
                }
                List<GatewayInfo> allGateway = MyApplication.getInstance().getAllGateway();
                GatewayInfo gatewayInfo = null;
                for (GatewayInfo info:allGateway){
                    if ( cateEyeInfo.getGwID().equals(info.getServerInfo().getDeviceSN())){
                        gatewayInfo = info;
                        break;
                    }
                }
                //网关不在线
                if (gatewayInfo!=null && !"online".equals(gatewayInfo.getEvent_str())) {
                    ToastUtil.getInstance().showLong(R.string.gw_offline);
                    return;
                }
                //猫眼设备不在线
                if (!"online".equals(cateEyeInfo.getServerInfo().getEvent_str())) {
                    ToastUtil.getInstance().showLong(R.string.cat_eye_offline);
                    return;
                }
                video_start_play.setVisibility(View.GONE);
                video_connecting_tv.setVisibility(View.VISIBLE);
                video_hang_up.setVisibility(View.GONE);
                mPresenter.callCatEye(cateEyeInfo);
                break;
            case R.id.video_v_full: //全屏按钮
                changeScreenOrientation();
                break;
            case R.id.iv_back:  //点击返回
                mPresenter.hangup();
                finish();
                break;
            case R.id.iv_hangup:  //点击挂断
            case R.id.iv_hangup2:  //点击挂断
                LogUtils.e(Tag,"点击挂断");
                mPresenter.hangup();
                finish();
                break;
        }
    }


    /**
     * 横竖屏切换
     */
    private void changeScreenOrientation() {
        //获取用户当前屏幕的横竖位置
        int currentOrientation = getResources().getConfiguration().orientation;
        //判断并设置用户点击全屏/半屏按钮的显示逻辑
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            //如果屏幕当前是横屏显示，则设置屏幕锁死为竖屏显示
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //如果屏幕当前是竖屏显示，则设置屏幕锁死为横屏显示
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onScrollEnd(@NonNull ForecastAdapter.ViewHolder holder, int position) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_screen_shot: //截屏
            case R.id.cb_screen_shot2: //截屏
                LogUtils.e(Tag,"截屏  " + isChecked);
//                cbScreenShot.setChecked(isChecked);
//                cbScreenShot2.setChecked(isChecked);
                mPresenter.toCapturePicture(cateEyeInfo.getServerInfo().getDeviceId());
                break;
            case R.id.cb_screen_record: //录屏
            case R.id.cb_screen_record2: //录屏
                if (isChecked) { //开启录屏
                    mPresenter.recordVideo(true, cateEyeInfo.getServerInfo().getDeviceId());
                } else {  //结束录屏
                    mPresenter.recordVideo(false, cateEyeInfo.getServerInfo().getDeviceId());
                }
                cbScreenRecord.setChecked(isChecked);
                cbScreenRecord2.setChecked(isChecked);
                break;
            case R.id.cb_mute: //静音
            case R.id.cb_mute2: //静音
                if (isChecked) {  //开启静音
                    LinphoneHelper.toggleMicro(true);
                } else {  //关闭静音
                    LinphoneHelper.toggleMicro(false);
                }
                cbMute.setChecked(isChecked);
                cbMute2.setChecked(isChecked);
                break;
            case R.id.cb_hands_free: //免提
            case R.id.cb_hands_free2: //免提
                if (isChecked) { //开启免提
                    LinphoneHelper.toggleSpeaker(true);
                } else { //关闭免提
                    LinphoneHelper.toggleSpeaker(false);
                }
                cbHandsFree.setChecked(isChecked);
                cbHandsFree2.setChecked(isChecked);
                break;
        }
    }

     Handler callFaiedHandler=new Handler(){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
         }
     };
    private void callFailed() {
        callFaiedHandler.post(new Runnable() {
            @Override
            public void run() {
                video_start_play.setVisibility(View.VISIBLE);
                video_connecting_tv.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCatEyeCallIn() {
        Intent intent = new Intent(this, CallComingActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CALL_COMING);
    }


    @Override
    public void loginMemeFailed() {
        //登录meme网失败
        ToastUtil.getInstance().showShort(R.string.link_failed);
        callFailed();
    }


    @Override
    public void onCallConnected() {
        LogUtils.e(Tag,"接通视频");
        acceptCall();
    }

    @Override
    public void onStreaming() {
        LogUtils.e(Tag,"有数据流");
    }

    @Override
    public void onCallFinish() {
        LogUtils.e(Tag,"通话结束");
        finish();
    }

    @Override
    public void screenShotSuccess() {
       // ToastUtil.getInstance().showShort(R.string.screen_success);
        Toast.makeText(VideoVActivity.this,getString(R.string.screen_success),Toast.LENGTH_SHORT).show();
    }

    Handler fileHander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    public void screenShotSuccessPath(String filePath) {
        if(!TextUtils.isEmpty(filePath)){
            fileHander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    File file=new File(filePath);
                    if(file.exists()){
                        Log.e(GeTui.VideoLog,"imagePath:"+file.getAbsolutePath());
                        try {
                            String path =file.getAbsolutePath();
                            String name=path.substring(path.lastIndexOf("/")+1,path.length());
                            MediaStore.Images.Media.insertImage(getContentResolver(), path, name, name);//插入图库
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        sendBroadcast(intent);
                        //这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
                    }
                }
            },1000);
        }
    }

    @Override
    public void screenShotFailed(Exception e) {
       // ToastUtil.getInstance().showShort(R.string.screen_failed);
        Toast.makeText(VideoVActivity.this,getString(R.string.screen_failed),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void recordTooShort() {
        cbScreenRecord.setChecked(true);
        ToastUtil.getInstance().showShort(R.string.video_must_record_5_seconds);
    }

    @Override
    public void wakeupSuccess() {

    }

    @Override
    public void wakeupFailed() {
        //唤醒失败
        ToastUtil.getInstance().showShort(R.string.call_failed);
        callFailed();

    }

    @Override
    public void wakeupFailedStateCode(String code) {

        if(!TextUtils.isEmpty(code) && code.equals("407")){
            Toast.makeText(VideoVActivity.this,getString(R.string.call_cateye_timeout),Toast.LENGTH_SHORT).show();
        }else if(!TextUtils.isEmpty(code) && code.equals("409")){
            Toast.makeText(VideoVActivity.this,getString(R.string.call_cateye_working),Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(VideoVActivity.this,getString(R.string.call_failed)+":"+code,Toast.LENGTH_SHORT).show();
        }
        callFailed();
    }

    @Override
    public void waitCallTimeout() {
        //等待猫眼呼叫35秒  没有呼叫过来
        ToastUtil.getInstance().showShort(R.string.call_time_out);
        callFailed();
    }

    @Override
    public void callTimes(String time) {
        videoTime = time;
        if (video_play_time != null) {
            video_play_time.setText(time + "");
        }
    }

    @Override
    public void onCatEyeOffline() {
        //猫眼离线
        ToastUtil.getInstance().showShort(R.string.call_failed_cat_eye_offline);
    }

    @Override
    public void inputPwd(GwLockInfo gwLockInfo) {
        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
        tvTitle.setText(getString(R.string.input_open_lock_password));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = editText.getText().toString().trim();
                if (!StringUtil.randomJudge(pwd)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                mPresenter.realOpenLock(gwLockInfo.getGwID(), gwLockInfo.getServerInfo().getDeviceId(), pwd);
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public void openLockSuccess() {
        isOpening = false;
        isClosing = true;
        LogUtils.e(Tag,"当前状态是   isOpening    " + isOpening + "   isClosing   " + isClosing);
        ToastUtil.getInstance().showShort(R.string.open_lock_success);
        hiddenLoading();
    }

    @Override
    public void openLockThrowable(Throwable throwable) {
        isOpening = false;
        ToastUtil.getInstance().showShort(R.string.open_lock_failed);
        hiddenLoading();
    }

    @Override
    public void openLockFailed() {
        isOpening = false;
        ToastUtil.getInstance().showShort(R.string.open_lock_failed);
        hiddenLoading();
    }
    @Override
    public void startOpenLock() {
        isOpening = true;
        showLoading(getString(R.string.is_open_lock));
    }

    @Override
    public void lockCloseSuccess() {
        isClosing = false;
    }

    @Override
    public void lockCloseFailed() {
        isClosing = false;
    }

    @Override
    public void onBackPressed() {
        mPresenter.hangup();
        super.onBackPressed();
    }

}
