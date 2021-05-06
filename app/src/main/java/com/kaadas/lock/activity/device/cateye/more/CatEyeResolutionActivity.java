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
import com.kaadas.lock.mvp.presenter.cateye.CatEyeResolutionPresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeResolutionView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatEyeResolutionActivity extends BaseActivity<ICatEyeResolutionView, CatEyeResolutionPresenter<ICatEyeResolutionView>> implements ICatEyeResolutionView {

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
    @BindView(R.id.btn_save)
    Button btnSave;

    private String gatewayId;
    private String deviceId;
    private String resolution;

    private String currentResolution;
    private String selectResolution;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_cateye_resolution);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent=getIntent();
        resolution=intent.getStringExtra(KeyConstants.CAT_EYE_RESOLUTION);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (resolution!=null) {
            checkResolution(resolution);
        }
    }

    private void checkResolution(String resolution) {
        if ("1280x720".equals(resolution)){
            onceImg.setChecked(true);
        }else if ("960x540".equals(resolution)){
            twiceImg.setChecked(true);
        }
        currentResolution=resolution;
    }




    @Override
    protected CatEyeResolutionPresenter<ICatEyeResolutionView> createPresent() {
        return new CatEyeResolutionPresenter<>();
    }


    @OnClick({R.id.back, R.id.once_layout, R.id.twice_layout, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.once_layout:
                onceImg.setChecked(true);
                twiceImg.setChecked(false);
                selectResolution="1280x720";
                break;
            case R.id.twice_layout:
                onceImg.setChecked(false);
                twiceImg.setChecked(true);
                selectResolution="960x540";
                break;
            case R.id.btn_save:
                if (currentResolution.equals(selectResolution)||TextUtils.isEmpty(selectResolution)){
                    ToastUtil.getInstance().showShort(R.string.current_resolution_no_change);
                    return;
                }
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                    mPresenter.setResolution(gatewayId,deviceId, MyApplication.getInstance().getUid(),selectResolution);
                    alertDialog= AlertDialogUtil.getInstance().noButtonDialog(this,getString(R.string.take_effect_be_being));
                    alertDialog.setCancelable(false);
                }
                break;
        }
    }

    @Override
    public void setResolutionSuccess(String resolution) {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        currentResolution=resolution;
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(KeyConstants.RESOLUTION_NUMBER, resolution);
        //设置返回数据
        CatEyeResolutionActivity.this.setResult(RESULT_OK, intent);
        ToastUtil.getInstance().showShort(getString(R.string.set_success));

    }

    @Override
    public void setResolutionFail() {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        if (currentResolution!=null&&onceImg!=null&&twiceImg!=null) {
            selectResolution=currentResolution;
            if ("1280x720".equals(currentResolution)) {
                onceImg.setChecked(true);
                twiceImg.setChecked(false);
            } else if ("960x540".equals(currentResolution)) {
                onceImg.setChecked(false);
                twiceImg.setChecked(true);
            }
        }
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void setResolutionThrowable(Throwable throwable) {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        if (currentResolution!=null&&onceImg!=null&&twiceImg!=null) {
            if ("1280x720".equals(currentResolution)) {
                onceImg.setChecked(true);
                twiceImg.setChecked(false);
            } else if ("960x540".equals(currentResolution)) {
                onceImg.setChecked(false);
                twiceImg.setChecked(true);
            }
        }
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }
}

