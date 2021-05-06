package com.kaadas.lock.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter.ClothesHangerMachinePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.ClothesHangerMachineAllBean;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ClothedHangerMachineFragment extends BaseFragment<IClothesHangerMachineView, ClothesHangerMachinePresenter<IClothesHangerMachineView>>
        implements IClothesHangerMachineView {
    @BindView(R.id.tv_clothes_hanger_status)
    TextView tvClothesHangerStatus;
    @BindView(R.id.img_hanger_lighting)
    ImageView imgHangerLighting;
    @BindView(R.id.rl_hanger_lighting)
    RelativeLayout rlHangerLighting;
    @BindView(R.id.tv_hanger_lighting)
    TextView tvHangerLighting;
    @BindView(R.id.img_hanger_disinfect)
    ImageView imgHangerDisinfect;
    @BindView(R.id.rl_hanger_disinfect)
    RelativeLayout rlHangerDisinfect;
    @BindView(R.id.tv_hanger_disinfect)
    TextView tvHangerDisinfect;
    @BindView(R.id.img_hanger_airdry)
    ImageView imgHangerAirdry;
    @BindView(R.id.rl_hanger_airdry)
    RelativeLayout rlHangerAirdry;
    @BindView(R.id.tv_hanger_airdry)
    TextView tvHangerAirDry;
    @BindView(R.id.img_hanger_baking)
    ImageView imgHangerBaking;
    @BindView(R.id.rl_hanger_baking)
    RelativeLayout rlHangerBaking;
    @BindView(R.id.tv_hanger_baking)
    TextView tvHangerBaking;
    @BindView(R.id.img_hanger_voice)
    ImageView imgHangerVoice;
    @BindView(R.id.rl_hanger_voice)
    RelativeLayout rlHangerVoice;
    @BindView(R.id.img_hanger_lock)
    ImageView imgHangerLock;
    @BindView(R.id.rl_hanger_lock)
    RelativeLayout rlHangerLock;
    @BindView(R.id.iv_external_big)
    ImageView ivExternalBig;

    private String wifiSn = "";
    private ClothesHangerMachineAllBean hangerInfo;
    private Disposable progressDisposable;

    private AnimationDrawable motorUpAnimation;
    private AnimationDrawable motorDownAnimation;

    private int motorStatus = 0;

    private boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clothes_hanger_machine, null);
        ButterKnife.bind(this, view);
        isFirst = true;
        wifiSn = (String) getArguments().getSerializable(KeyConstants.WIFI_SN);
        hangerInfo= MyApplication.getInstance().getClothesHangerMachineBySn(wifiSn);
        if(hangerInfo != null){
            initData(view);
        }
        if(hangerInfo != null){
            mPresenter.getHangerAllStatus(wifiSn);
        }
        progressDisposable = Observable
                .interval(0, 1, TimeUnit.MINUTES)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if(aLong != 0){
                            updateCountDownTime(imgHangerLighting,tvHangerLighting);
                            updateCountDownTime(imgHangerAirdry,tvHangerAirDry);
                            updateCountDownTime(imgHangerBaking,tvHangerBaking);
                            updateCountDownTime(imgHangerDisinfect,tvHangerDisinfect);
                        }
                    }
                });
        return view;
    }

    private void updateCountDownTime(ImageView imageView,TextView textView) {
        if(imageView.isSelected()){
            int time = DateUtils.getTimeToInt(textView.getText().toString().trim());
            textView.setText(DateUtils.getStringTime3(--time));
        }
    }

    private void initData(View view) {
        if(hangerInfo.getLoudspeaker() == 1){
            imgHangerVoice.setSelected(true);
        }else{
            imgHangerVoice.setSelected(false);
        }

        if(hangerInfo.getChildLock() == 1){
            imgHangerLock.setSelected(true);
        }else{
            imgHangerLock.setSelected(false);
        }

        view.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                if(isFirst){
                    if(hangerInfo.getMotor() != null){
                        motorStatus = hangerInfo.getMotor().getStatus();
                        setHangerMotorAction(hangerInfo.getMotor().getAction(),hangerInfo.getMotor().getStatus());
                    }
                    isFirst = false;
                }
            }
        });

        if(hangerInfo.getOverload() == 1){
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_overload) + "");
        }
    }

    private void setHangerMotorAction(int action,int status) {
        if(action == 0){
            setHangerMotorStatus(KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_PAUSE);
            setHangerMotorStatus(status);
        }else if(action == 2){//上升
            if(status != 2){
                if(status == 1){
                    setHangerMotorStatus(status);
                }else{
                    setHangerMotorStatus(KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_UP);
                }
            }
        }else if(action == 1){//下降
            if(status != 1){
                if(status == 2){
                    setHangerMotorStatus(status);
                }else{
                    setHangerMotorStatus(KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_DOWN);
                }
            }
        }
    }


    private void setHangerMotorStatus(int status) {
        if(status == 0){
            motorStatus = status;
            ivExternalBig.setBackgroundResource(R.drawable.clothes_hanger_machine_home_big_img_3);
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_normal) + "");
        }else if(status == 1){
            motorStatus = status;
            ivExternalBig.setBackgroundResource(R.drawable.clothes_hanger_machine_home_big_img_1);
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_top) + "");
        }else if(status == 2){
            motorStatus = status;
            ivExternalBig.setBackgroundResource(R.drawable.clothes_hanger_machine_home_big_img_5);
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_bottom) + "");
        }else if(status == 3){
            ivExternalBig.setBackgroundResource(R.drawable.clothes_hanger_machine_home_big_img_3);
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_blocked) + "");
        }else if(status == KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_UP){
            startMotorUpAnimation();
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_up) + "");
        }else if(status == KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_DOWN){
            startMotorDownAnimation();
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_down) + "");
        }else if(status == KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_PAUSE){
            stopMotorDownAnimation();
            stopMotorUpAnimation();
            ivExternalBig.setBackgroundResource(R.drawable.clothes_hanger_machine_home_big_img_3);
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_normal) + "");
        }
    }

    private void startMotorUpAnimation() {
        LogUtils.e("shulan 2 motorStatus -----> " + motorStatus);
        if(motorStatus == 2){
            ivExternalBig.setBackgroundResource(R.drawable.hanger_motor_bottom_up);
        }else{
            ivExternalBig.setBackgroundResource(R.drawable.hanger_motor_up);
        }
        motorUpAnimation = (AnimationDrawable) ivExternalBig.getBackground();
        motorUpAnimation.start();
    }

    private void stopMotorUpAnimation(){
        if(motorUpAnimation != null){
            motorUpAnimation.stop();
        }
    }

    private void startMotorDownAnimation() {
        LogUtils.e("shulan 1 motorStatus -----> " + motorStatus);
        if(motorStatus == 1){
            ivExternalBig.setBackgroundResource(R.drawable.hanger_motor_top_down);
        }else{
            ivExternalBig.setBackgroundResource(R.drawable.hanger_motor_down);
        }
        motorDownAnimation = (AnimationDrawable) ivExternalBig.getBackground();
        motorDownAnimation.start();
    }

    private void stopMotorDownAnimation(){
        if(motorDownAnimation != null){
            motorDownAnimation.stop();
        }
    }

    @Override
    protected ClothesHangerMachinePresenter<IClothesHangerMachineView> createPresent() {
        return new ClothesHangerMachinePresenter<>();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDisposable != null) {
            progressDisposable.dispose();
        }
    }

    private long time = 0;
    private final int TIME_INTERVAL = 0;

    @OnClick({R.id.img_hanger_up,R.id.img_hanger_down,R.id.img_hanger_pause,R.id.rl_hanger_lighting,R.id.rl_hanger_disinfect,
                R.id.rl_hanger_airdry,R.id.rl_hanger_baking,R.id.rl_hanger_voice,R.id.rl_hanger_lock})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.img_hanger_up://2
                if(System.currentTimeMillis() - time > TIME_INTERVAL){
                    mPresenter.setHangerMotor(wifiSn,2);
                    time = System.currentTimeMillis();
                }
                break;
            case R.id.img_hanger_down://1
                if(System.currentTimeMillis() - time > TIME_INTERVAL){
                    mPresenter.setHangerMotor(wifiSn,1);
                    time = System.currentTimeMillis();
                }
                break;
            case R.id.img_hanger_pause://0
                if(System.currentTimeMillis() - time > TIME_INTERVAL){
                    mPresenter.setHangerMotor(wifiSn,0);
                    time = System.currentTimeMillis();
                }
                break;
            case R.id.rl_hanger_lighting:
                if(imgHangerLighting.isSelected()){
                    mPresenter.setHangerLighting(wifiSn,0);
                }else{
                    mPresenter.setHangerLighting(wifiSn,1);
                }
                break;
            case R.id.rl_hanger_disinfect:
                if(imgHangerDisinfect.isSelected()){
                    mPresenter.setHangerUV(wifiSn,0);
                }else{
                    mPresenter.setHangerUV(wifiSn,1);
                }
                break;
            case R.id.rl_hanger_airdry:
                if(imgHangerAirdry.isSelected()){
                    mPresenter.setAirDryTime(wifiSn,0);
                }else{
                    showFunctionTimeDialog("请选择风干时间",KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_AIR_DRY);
                }
                break;
            case R.id.rl_hanger_baking:
                if(imgHangerBaking.isSelected()){
                    mPresenter.setBakingTime(wifiSn,0);
                }else{
                    showFunctionTimeDialog("请选择烘干时间",KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_BAKING);
                }
                break;
            case R.id.rl_hanger_voice:
                if(imgHangerVoice.isSelected()){
                    mPresenter.setHangerVoice(wifiSn,0);
                }else {
                    mPresenter.setHangerVoice(wifiSn,1);
                }

                break;
            case R.id.rl_hanger_lock:
                if(imgHangerLock.isSelected()){
                    mPresenter.setHangerChildLock(wifiSn,0);
                }else {
                    mPresenter.setHangerChildLock(wifiSn,1);
                }

                break;
        }
    }

    private void showFunctionTimeDialog(String content,int function){
        AlertDialogUtil.getInstance().clothesHangerMachineDialog(getActivity(), content, getString(R.string.cancel), getString(R.string.confirm),
                "#A3A3A3", "#1F96F7", new AlertDialogUtil.ClothesHangerMachineClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right(int time) {
                        LogUtils.e("shulan  clotheshangermachine time--->" + time);
                        if(function == KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_BAKING){
                            mPresenter.setBakingTime(wifiSn,time);
                        }else if(function == KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_AIR_DRY){
                            mPresenter.setAirDryTime(wifiSn,time);
                        }
                    }
                });
    }

    @Override
    public void setAirDryTimeSuccess(int action,int countdown) {
        LogUtils.e("shulan --------setAirDryTimeSuccess");
        if(action == 0){
            imgHangerAirdry.setSelected(false);
            tvHangerAirDry.setVisibility(View.GONE);
        }else{
            imgHangerAirdry.setSelected(true);
            tvHangerAirDry.setVisibility(View.VISIBLE);
            if(countdown == 120){
                tvHangerAirDry.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 240){
                tvHangerAirDry.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 0){
                imgHangerAirdry.setSelected(false);
                tvHangerAirDry.setVisibility(View.GONE);
                tvHangerAirDry.setText(DateUtils.getStringTime3(countdown));
            }else{
                tvHangerAirDry.setText(DateUtils.getStringTime3(countdown));
            }
        }
    }

    @Override
    public void setAirDryTimeFailed() {

    }

    @Override
    public void setAirDryTimeThrowable(Throwable e) {

    }

    @Override
    public void setBakingTimeSuccess(int action,int countdown) {
        LogUtils.e("shulan --------setBakingTimeSuccess");
        if(action == 0){
            imgHangerBaking.setSelected(false);
            tvHangerBaking.setVisibility(View.GONE);
        }else{
            imgHangerBaking.setSelected(true);
            tvHangerBaking.setVisibility(View.VISIBLE);
            if(countdown == 120){
                tvHangerBaking.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 240){
                tvHangerBaking.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 0){
                imgHangerBaking.setSelected(false);
                tvHangerBaking.setVisibility(View.GONE);
                tvHangerBaking.setText(DateUtils.getStringTime3(countdown));
            }else{
                tvHangerBaking.setText(DateUtils.getStringTime3(countdown));
            }
        }
    }

    @Override
    public void setBakingTimeFailed() {

    }

    @Override
    public void setBakingTimeThrowable(Throwable e) {

    }

    @Override
    public void setChildLockSuccess(int action) {
        LogUtils.e("shulan --------setChildLockSuccess");
        if(action == 1){
            imgHangerLock.setSelected(true);
        }else{
            imgHangerLock.setSelected(false);
        }
    }

    @Override
    public void setChildLockFailed() {

    }

    @Override
    public void setChildLockThrowable(Throwable e) {

    }

    @Override
    public void setVoiceSuccess(int action) {
        LogUtils.e("shulan --------setVoiceSuccess");
        if(action == 1){
            imgHangerVoice.setSelected(true);
        }else{
            imgHangerVoice.setSelected(false);
        }
    }

    @Override
    public void setVoiceFailed() {

    }

    @Override
    public void setVoiceThrowable(Throwable e) {

    }

    @Override
    public void setUVSuccess(int action,int countdown) {
        LogUtils.e("shulan --------setUVSuccess");
        if(action == 1){
            imgHangerDisinfect.setSelected(true);
            tvHangerDisinfect.setVisibility(View.VISIBLE);
            if(countdown == 120){
                tvHangerDisinfect.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 240){
                tvHangerDisinfect.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 0){
                imgHangerDisinfect.setSelected(false);
                tvHangerDisinfect.setVisibility(View.GONE);
                tvHangerDisinfect.setText(DateUtils.getStringTime3(countdown));
            }else{
                tvHangerDisinfect.setText(DateUtils.getStringTime3(countdown));
            }
        }else{
            imgHangerDisinfect.setSelected(false);
            tvHangerDisinfect.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUVFailed() {

    }

    @Override
    public void setUVThrowable(Throwable e) {

    }

    @Override
    public void setLightingSuccess(int action,int countdown) {
        LogUtils.e("shulan --------setLightingSuccess");
        if(action == 1){
            imgHangerLighting.setSelected(true);
            tvHangerLighting.setVisibility(View.VISIBLE);
            if(countdown == 120){
                tvHangerLighting.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 240){
                tvHangerLighting.setText(DateUtils.getStringTime3(countdown));
            }else if(countdown == 0){
                imgHangerLighting.setSelected(false);
                tvHangerLighting.setVisibility(View.GONE);
                tvHangerLighting.setText(DateUtils.getStringTime3(countdown));
            }else{
                tvHangerLighting.setText(DateUtils.getStringTime3(countdown));
            }
        }else{
            imgHangerLighting.setSelected(false);
            tvHangerLighting.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLightingFailed() {

    }

    @Override
    public void setLightingThrowable(Throwable e) {

    }

    @Override
    public void setMotorSuccess(int action,int status) {
        setHangerMotorAction(action,status);
    }

    @Override
    public void setMotorFailed(int action) {
        setHangerMotorAction(action,KeyConstants.CLOTHES_HANGER_MACHINE_FUNCTION_MOTOR_PAUSE);
    }

    @Override
    public void setMotorThrowable(Throwable e) {

    }

    @Override
    public void setOverload(int overload) {
        if(overload == 1){
            tvClothesHangerStatus.setText(getActivity().getText(R.string.clothes_machine_hanger_status_overload) + "");
        }
    }
}
