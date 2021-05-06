package com.kaadas.lock.activity.device.cateye.more;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.CatEyeVolumePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeVolumeView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatEyeVolumeActivity extends BaseActivity<ICatEyeVolumeView, CatEyeVolumePresenter<ICatEyeVolumeView>> implements ICatEyeVolumeView {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.low)
    TextView low;
    @BindView(R.id.once_img)
    CheckBox onceImg;
    @BindView(R.id.once_layout)
    RelativeLayout onceLayout;
    @BindView(R.id.center)
    TextView center;
    @BindView(R.id.twice_img)
    CheckBox twiceImg;
    @BindView(R.id.twice_layout)
    RelativeLayout twiceLayout;
    @BindView(R.id.high)
    TextView high;
    @BindView(R.id.three_img)
    CheckBox threeImg;
    @BindView(R.id.three_layout)
    RelativeLayout threeLayout;
    @BindView(R.id.btn_save)
    Button btnSave;
    private String volume;
    private int selectRingNumber = -1;
    private int currentRingNumber = 0;
    private String gatewayId;
    private String deviceId;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_cateye_volume);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected CatEyeVolumePresenter<ICatEyeVolumeView> createPresent() {
        return new CatEyeVolumePresenter<>();
    }

    private void initData() {
        Intent intent = getIntent();
        volume = intent.getStringExtra(KeyConstants.CAT_EYE_VOLUME);
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (volume != null) {
            checkRingNumber(volume);
        }
    }

    private void checkRingNumber(String ring) {
        if (getString(R.string.low).equals(ring)) {
            onceImg.setChecked(true);
            currentRingNumber = 1;
        } else if (getString(R.string.centre).equals(ring)) {
            twiceImg.setChecked(true);
            currentRingNumber = 2;
        } else if (getString(R.string.high).equals(ring)) {
            threeImg.setChecked(true);
            currentRingNumber = 0;
        }
    }


    @OnClick({R.id.back, R.id.once_layout, R.id.twice_layout, R.id.three_layout, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.once_layout:
                onceImg.setChecked(true);
                twiceImg.setChecked(false);
                threeImg.setChecked(false);
                selectRingNumber = 1; //低
                break;
            case R.id.twice_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(true);
                threeImg.setChecked(false);
                selectRingNumber = 2; //中
                break;
            case R.id.three_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(false);
                threeImg.setChecked(true);
                selectRingNumber = 0; //高
                break;

            case R.id.btn_save:
                if (selectRingNumber == currentRingNumber||selectRingNumber==-1) {
                    ToastUtil.getInstance().showShort(R.string.current_volume_no_change);
                    return;
                }
                if (selectRingNumber != -1 && !TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    mPresenter.setVolume(gatewayId, deviceId, MyApplication.getInstance().getUid(), selectRingNumber);
                    alertDialog = AlertDialogUtil.getInstance().noButtonDialog(this, getString(R.string.take_effect_be_being));
                    alertDialog.setCancelable(false);
                }
                break;
        }
    }


    @Override
    public void setVolumeSuccess(int number) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        currentRingNumber = number;
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(KeyConstants.VOLUME_NUMBER, number);
        //设置返回数据
        CatEyeVolumeActivity.this.setResult(RESULT_OK, intent);

        ToastUtil.getInstance().showShort(R.string.set_success);
    }

    @Override
    public void setVolumeFail() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        //设置失败
        LogUtils.e("设置失败    ");
        if (onceImg != null && twiceImg != null && threeImg != null) {
            switch (currentRingNumber) {
                case 1:
                    onceImg.setChecked(true);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    break;
                case 2:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(true);
                    threeImg.setChecked(false);
                    break;
                case 0:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(true);
                    break;
            }
        }
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void setVolumeThrowable(Throwable throwable) {
        LogUtils.e("设置异常    ");
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (onceImg != null && twiceImg != null && threeImg != null) {
            switch (currentRingNumber) {
                case 1:
                    onceImg.setChecked(true);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    break;
                case 2:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(true);
                    threeImg.setChecked(false);
                    break;
                case 0:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(true);
                    break;
            }
        }
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }
}


