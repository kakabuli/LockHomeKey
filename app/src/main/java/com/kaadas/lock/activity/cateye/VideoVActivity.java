package com.kaadas.lock.activity.cateye;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
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

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.ForecastAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.VideoPresenter;
import com.kaadas.lock.mvp.view.cateye.IVideoView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.linphone.MemeManager;
import com.kaadas.lock.publiclibrary.linphone.linphone.util.LinphoneHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentOrientation = newConfig.orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {  //横屏
            isLand = true;

            //根据竖屏时的状态显示
            if (ll_video_control1.getVisibility() == View.VISIBLE){
                ll_video_control2. setVisibility(View.VISIBLE);
            }else {
                ll_video_control2. setVisibility(View.GONE);
            }
            //隐藏 显示时间的View
            video_play_time.setVisibility(View.GONE);
            video_v_go.setImageResource(R.mipmap.video_to_portrait);
            ll_video_control1.setVisibility(View.GONE);
            rl_bottom .setVisibility(View.GONE);
            rl_title_bar.setVisibility(View.GONE);
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
            isLand = false;
            if (ll_video_control2.getVisibility() == View.VISIBLE){
                ll_video_control1.setVisibility(View.VISIBLE);
            }else {
                ll_video_control1.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(videoTime)){
                //隐藏 显示时间的View
                video_play_time.setVisibility(View.GONE);
            }else {
                //隐藏 显示时间的View
                video_play_time.setVisibility(View.VISIBLE);
            }

            video_v_go.setImageResource(R.mipmap.video_full_screen);
            ll_video_control2. setVisibility(View.GONE);
            rl_bottom .setVisibility(View.VISIBLE);
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
        super.onDestroy();
        isRunning = false;
        MemeManager.getInstance().videoActivityDisconnectMeme();
        LinphoneHelper.onDestroy();
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
                LogUtils.e("接听了电话");
                mPresenter.listenerCallStatus();
                String mDeviceIp = MemeManager.getInstance().getDeviceIp();
                LinphoneHelper.acceptCall(mDeviceIp);
                acceptCall();
            } else { //挂断
                LogUtils.e("挂断了电话");
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
        if (isLand){
            video_play_time.setVisibility(View.GONE);
        }else { //竖屏的时候才显示
            video_play_time.setVisibility(View.VISIBLE);
        }

        video_v_surfaceview.setVisibility(View.VISIBLE);
        //播放按钮
        video_start_play.setVisibility(View.GONE);
        //接通了猫眼  显示视频控制界面
        if (isLand){
            ll_video_control2.setVisibility(View.VISIBLE);
        }else {
            ll_video_control1.setVisibility(View.VISIBLE);
        }

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
                        LogUtils.e("当前状态是   isOpening    " + isOpening + "   isClosing   " + isClosing);
                        if (isOpening) {
                            ToastUtil.getInstance().showShort(R.string.is_opening_try_latter);
                            return;
                        }
                        if (isClosing) {
                            ToastUtil.getInstance().showShort(R.string.lock_already_open);
                            return;
                        }
                        LogUtils.e("执行开门  ");
                        GwLockInfo gwLockInfo = gwLockInfos.get(position);
                        mPresenter.openLock(gwLockInfo);
                    }
                }
            });
            if (tvTitle != null) {
                tvTitle.setText(cateEyeInfo.getServerInfo().getNickName());
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
                LogUtils.e("点击挂断");
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_screen_shot: //截屏
            case R.id.cb_screen_shot2: //截屏
                LogUtils.e("截屏  " + isChecked);
                mPresenter.toCapturePicture(cateEyeInfo.getServerInfo().getDeviceId());
                break;
            case R.id.cb_screen_record: //录屏
            case R.id.cb_screen_record2: //录屏
                if (isChecked) { //开启录屏
                    mPresenter.recordVideo(true, cateEyeInfo.getServerInfo().getDeviceId());
                } else {  //结束录屏
                    mPresenter.recordVideo(false, cateEyeInfo.getServerInfo().getDeviceId());
                }
                break;
            case R.id.cb_mute: //静音
            case R.id.cb_mute2: //静音
                if (isChecked) {  //开启静音
                    LinphoneHelper.toggleMicro(true);
                } else {  //关闭静音
                    LinphoneHelper.toggleMicro(false);
                }
                break;
            case R.id.cb_hands_free: //免提
            case R.id.cb_hands_free2: //免提
                if (isChecked) { //开启免提
                    LinphoneHelper.toggleSpeaker(true);
                } else { //关闭免提
                    LinphoneHelper.toggleSpeaker(false);
                }
                break;
        }
    }

    private void callFailed() {
        video_start_play.setVisibility(View.VISIBLE);
        video_connecting_tv.setVisibility(View.GONE);
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
        LogUtils.e("接通视频");
        acceptCall();
    }

    @Override
    public void onStreaming() {

    }

    @Override
    public void onCallFinish() {

    }

    @Override
    public void screenShotSuccess() {
        ToastUtil.getInstance().showShort(R.string.screen_success);
    }

    @Override
    public void screenShotFailed(Exception e) {
        ToastUtil.getInstance().showShort(R.string.screen_failed);
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
        LogUtils.e("当前状态是   isOpening    " + isOpening + "   isClosing   " + isClosing);
        ToastUtil.getInstance().showShort(R.string.open_lock_success);
        hiddenLoading();
    }

    @Override
    public void openLockFailed(Throwable throwable) {
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
