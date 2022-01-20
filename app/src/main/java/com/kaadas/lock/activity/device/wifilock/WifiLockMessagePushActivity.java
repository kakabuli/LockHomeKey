package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockMessagePushActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView{
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.iv_wandering_alarm)
    ImageView ivWanderingAlarm;
    @BindView(R.id.rl_wandering_alarm)
    RelativeLayout rlWanderingAlarm;
    @BindView(R.id.iv_door_bell)
    ImageView ivDoorBell;
    @BindView(R.id.rl_door_bell)
    RelativeLayout rlDoorBell;
    @BindView(R.id.rl_lock_info)
    RelativeLayout rlLockInfo;
    @BindView(R.id.iv_lock_info)
    ImageView ivLockInfo;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.tvHint)
    TextView tvHint;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private int pushSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_message_push);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        //k9-v 改文案？
        if(BleLockUtils.isFuncSet(wifiLockInfo.getFunctionSet(), 0xB5)){
            tvHint.setText(getString(R.string.msg_push_switch_hint2));
        }
        initData();
    }


    private void initData() {
        if(wifiLockInfo != null){
            pushSwitch = wifiLockInfo.getPushSwitch();
            if(pushSwitch == 2){
                ivMessageFree.setSelected(true);
            }else{
                ivMessageFree.setSelected(false);
            }

        }
    }


    @OnClick({R.id.back, R.id.iv_message_free,R.id.rl_message_free})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.rl_message_free:
            case R.id.iv_message_free:
                if(ivMessageFree.isSelected()){
                    mPresenter.updateSwitchStatus(1,wifiSn);
                }else{
                    mPresenter.updateSwitchStatus(2,wifiSn);
                }
                break;
        }
    }


    @Override
    protected WifiLockMorePresenter<IWifiLockMoreView> createPresent() {
        return new WifiLockMorePresenter();
    }

    @Override
    public void onDeleteDeviceSuccess() {

    }

    @Override
    public void onDeleteDeviceFailed(Throwable throwable) {

    }

    @Override
    public void onDeleteDeviceFailedServer(BaseResult result) {

    }

    @Override
    public void modifyDeviceNicknameSuccess() {

    }

    @Override
    public void modifyDeviceNicknameError(Throwable throwable) {

    }

    @Override
    public void modifyDeviceNicknameFail(BaseResult baseResult) {

    }

    @Override
    public void onUpdatePushStatusSuccess(int status) {
        if(!WifiLockMessagePushActivity.this.isFinishing()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ToastUtil.getInstance().showShort(getString(R.string.modify_success));
                    if(status == 2){
                        ivMessageFree.setSelected(true);
                    }else {
                        ivMessageFree.setSelected(false);
                    }
                }
            });
        }

    }

    @Override
    public void onUpdatePushStatusFailed(BaseResult result) {
        /*if(!WifiLockMessagePushActivity.this.isFinishing()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.getInstance().showShort(getString(R.string.modify_failed));
                }
            });
        }*/
    }

    @Override
    public void onUpdatePushStatusThrowable(Throwable throwable) {

    }

    @Override
    public void onWifiLockActionUpdate() {

    }

    @Override
    public void noNeedUpdate() {

    }

    @Override
    public void snError() {

    }

    @Override
    public void dataError() {

    }

    @Override
    public void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, int type) {

    }

    @Override
    public void needMultiUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {

    }

    @Override
    public void readInfoFailed(Throwable throwable) {

    }

    @Override
    public void unknowError(String errorCode) {

    }

    @Override
    public void uploadSuccess(int type) {

    }

    @Override
    public void uploadFailed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
