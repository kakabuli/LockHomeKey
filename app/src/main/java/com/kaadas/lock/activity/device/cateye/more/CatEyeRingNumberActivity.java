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
import com.kaadas.lock.mvp.presenter.cateye.CatEyeRingNumberPresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeRingNumberView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatEyeRingNumberActivity extends BaseActivity<ICatEyeRingNumberView, CatEyeRingNumberPresenter<ICatEyeRingNumberView>> implements ICatEyeRingNumberView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.once)
    TextView once;
    @BindView(R.id.once_img)
    CheckBox onceImg;
    @BindView(R.id.once_layout)
    RelativeLayout onceLayout;
    @BindView(R.id.twice)
    TextView twice;
    @BindView(R.id.twice_img)
    CheckBox twiceImg;
    @BindView(R.id.twice_layout)
    RelativeLayout twiceLayout;
    @BindView(R.id.three)
    TextView three;
    @BindView(R.id.three_img)
    CheckBox threeImg;
    @BindView(R.id.three_layout)
    RelativeLayout threeLayout;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.four)
    TextView four;
    @BindView(R.id.four_img)
    CheckBox fourImg;
    @BindView(R.id.four_layout)
    RelativeLayout fourLayout;
    @BindView(R.id.five)
    TextView five;
    @BindView(R.id.five_img)
    CheckBox fiveImg;
    @BindView(R.id.five_layout)
    RelativeLayout fiveLayout;

    private String ring;
    private int selectRingNumber = 0;
    private int currentRingNumber = 0;
    private String gatewayId;
    private String deviceId;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_cateye_ringnumber);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        ring = intent.getStringExtra(KeyConstants.CAT_EYE_RING_NUMBER);
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (ring != null) {
            checkRingNumber(ring);
        }
    }

    private void checkRingNumber(String ring) {

        if ("1".equals(ring)) {
            onceImg.setChecked(true);
        } else if ("2".equals(ring)) {
            twiceImg.setChecked(true);
        } else if ("3".equals(ring)) {
            threeImg.setChecked(true);
        }else if ("4".equals(ring)){
            fourImg.setChecked(true);
        }else if ("5".equals(ring)){
            fiveImg.setChecked(true);
        }
        currentRingNumber = Integer.parseInt(ring);
    }

    @Override
    protected CatEyeRingNumberPresenter<ICatEyeRingNumberView> createPresent() {
        return new CatEyeRingNumberPresenter<>();
    }


    @OnClick({R.id.back, R.id.once_layout, R.id.twice_layout, R.id.three_layout,R.id.four_layout, R.id.five_layout, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.once_layout:
                onceImg.setChecked(true);
                twiceImg.setChecked(false);
                threeImg.setChecked(false);
                fourImg.setChecked(false);
                fiveImg.setChecked(false);
                selectRingNumber = 1;
                break;
            case R.id.twice_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(true);
                threeImg.setChecked(false);
                fourImg.setChecked(false);
                fiveImg.setChecked(false);
                selectRingNumber = 2;
                break;
            case R.id.three_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(false);
                threeImg.setChecked(true);
                fourImg.setChecked(false);
                fiveImg.setChecked(false);
                selectRingNumber = 3;
                break;
            case R.id.four_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(false);
                threeImg.setChecked(false);
                fourImg.setChecked(true);
                fiveImg.setChecked(false);
                selectRingNumber = 4;

                break;
            case R.id.five_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(false);
                threeImg.setChecked(false);
                fourImg.setChecked(false);
                fiveImg.setChecked(true);
                selectRingNumber = 5;
                break;
            case R.id.btn_save:
                if (selectRingNumber == currentRingNumber || selectRingNumber == 0) {
                    ToastUtils.showShort(R.string.current_ring_number_no_change);
                    return;
                }
                if (selectRingNumber != 0 && !TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    mPresenter.setRingNumber(gatewayId, deviceId, MyApplication.getInstance().getUid(), selectRingNumber);
                    alertDialog = AlertDialogUtil.getInstance().noButtonDialog(this, getString(R.string.take_effect_be_being));
                    alertDialog.setCancelable(false);
                }
                break;
        }
    }

    @Override
    public void setRingNumberSuccess(int number) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        currentRingNumber = number;
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(KeyConstants.RIGH_NUMBER, number);
        //设置返回数据
        CatEyeRingNumberActivity.this.setResult(RESULT_OK, intent);
        ToastUtils.showShort(R.string.set_success);
    }

    @Override
    public void setRingNumberFail() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        //设置失败
        LogUtils.e("设置失败    ");
        if (onceImg != null && twiceImg != null && threeImg != null&&fourImg!=null&&fiveImg!=null) {
            switch (currentRingNumber) {
                case 1:
                    onceImg.setChecked(true);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(false);
                    break;
                case 2:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(true);
                    threeImg.setChecked(false);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(false);
                    break;
                case 3:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(true);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(false);
                    break;
                case 4:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    fourImg.setChecked(true);
                    fiveImg.setChecked(false);
                    break;
                case 5:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(true);
                    break;
            }
        }
        ToastUtils.showShort(R.string.set_failed);
    }

    @Override
    public void setRingNumberThrowable(Throwable throwable) {
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
                    fourImg.setChecked(false);
                    fiveImg.setChecked(false);
                    break;
                case 2:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(true);
                    threeImg.setChecked(false);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(false);
                    break;
                case 3:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(true);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(false);
                    break;
                case 4:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    fourImg.setChecked(true);
                    fiveImg.setChecked(false);
                    break;
                case 5:
                    onceImg.setChecked(false);
                    twiceImg.setChecked(false);
                    threeImg.setChecked(false);
                    fourImg.setChecked(false);
                    fiveImg.setChecked(true);
                    break;
            }
        }
        ToastUtils.showShort(R.string.set_failed);
    }

}
