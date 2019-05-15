package com.kaadas.lock.activity.device.cateye.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.CatEyeDefaultPresenter;
import com.kaadas.lock.mvp.view.IFingerprintManagerView;
import com.kaadas.lock.mvp.view.cateye.ICatEyeDefaultView;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetPirSlientBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CateDefaultActivity extends BaseActivity<ICatEyeDefaultView, CatEyeDefaultPresenter<ICatEyeDefaultView>> implements View.OnClickListener, ICatEyeDefaultView {
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_default_monitor)
    ImageView iv_default_monitor;
    @BindView(R.id.periodtime)
    TextView periodtime;
    @BindView(R.id.threshold)
    TextView threshold;
    @BindView(R.id.protecttime)
    TextView protecttime;
    @BindView(R.id.ust)
    TextView ust;
    @BindView(R.id.maxprohibition)
    TextView maxprohibition;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.save)
    Button save;
    private String gatewayId;
    private String deviceId;
    private LoadingDialog loadingDialog;

    private int enable = -1;
    private boolean getPirEnableFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_default);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();

    }

    @Override
    protected CatEyeDefaultPresenter<ICatEyeDefaultView> createPresent() {
        return new CatEyeDefaultPresenter<>();
    }

    private void initListener() {
        iv_default_monitor.setOnClickListener(this);
        save.setOnClickListener(this);

    }

    private void initData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getPirSlient(MyApplication.getInstance().getUid(), gatewayId, deviceId);
            loadingDialog.show(getString(R.string.get_pir_slient_param));
        }
    }

    private void initView() {
        tv_content.setText(getString(R.string.pir_default_tile));
        loadingDialog = LoadingDialog.getInstance(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_default_monitor:
                //关闭Enable
                if (enable == 1) {
                    enable = 0;
                    iv_default_monitor.setBackgroundResource(R.mipmap.iv_close);
                } else {
                    enable = 1;
                    iv_default_monitor.setBackgroundResource(R.mipmap.iv_open);
                }
                break;
            case R.id.save:
                if (getPirEnableFlag==false){
                    ToastUtil.getInstance().showShort(getString(R.string.get_pir_slient_fail));
                    return;
                }

                String periodtimeStr=periodtime.getText().toString().trim();
                String thresholdStr=threshold.getText().toString().trim();
                String protecttimeStr=protecttime.getText().toString().trim();
                String ustStr=ust.getText().toString().trim();
                String maxprohibitionStr=maxprohibition.getText().toString().trim();
                if (TextUtils.isEmpty(periodtimeStr)||TextUtils.isEmpty(thresholdStr)||TextUtils.isEmpty(protecttimeStr)||TextUtils.isEmpty(ustStr)||TextUtils.isEmpty(maxprohibitionStr)){
                    ToastUtil.getInstance().showShort(getString(R.string.parameter_not_empty));
                    return;
                }
                int periodtimeInt=Integer.parseInt(periodtimeStr);
                if (periodtimeInt>60||periodtimeInt<=0){
                    ToastUtil.getInstance().showShort(getString(R.string.please_input_legal_number));
                    return;
                }

                int thresholdInt=Integer.parseInt(thresholdStr);
                if (thresholdInt>60||thresholdInt<=0){
                    ToastUtil.getInstance().showShort(getString(R.string.please_input_legal_number));
                    return;
                }
                int protecttimeInt=Integer.parseInt(protecttimeStr);
                if (protecttimeInt>60||periodtimeInt<=0){
                    ToastUtil.getInstance().showShort(getString(R.string.please_input_legal_number));
                    return;
                }
                int ustInt=Integer.parseInt(ustStr);
                if (ustInt>60||ustInt<=0){
                    ToastUtil.getInstance().showShort(getString(R.string.please_input_legal_number));
                    return;
                }

                int maxprohibitionInt=Integer.parseInt(maxprohibitionStr);
                if (maxprohibitionInt>60||maxprohibitionInt<=0){
                    ToastUtil.getInstance().showShort(getString(R.string.please_input_legal_number));
                    return;
                }
                loadingDialog.show(getString(R.string.take_effect_be_being));
                mPresenter.setPirSlient(MyApplication.getInstance().getUid(),gatewayId,deviceId,ustInt,enable,maxprohibitionInt,periodtimeInt,protecttimeInt,thresholdInt);
                break;
        }

    }

    @Override
    public void getPirSlientSuccess(GetPirSlientBean.ReturnDataBean dataBean) {
        getPirEnableFlag=true;
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (periodtime != null) {
            periodtime.setText(dataBean.getPeriodtime() + "");
        }
        if (threshold != null) {
            threshold.setText(dataBean.getThreshold() + "");
        }
        if (protecttime != null) {
            protecttime.setText(dataBean.getProtecttime() + "");
        }
        if (ust != null) {
            ust.setText(dataBean.getUst() + "");
        }
        if (maxprohibition != null) {
            maxprohibition.setText(dataBean.getMaxprohibition() + "");
        }
        if (iv_default_monitor != null) {
            if (dataBean.getEnable() == 1) {
                iv_default_monitor.setImageResource(R.mipmap.iv_open);
                enable=1;
            } else {
                iv_default_monitor.setImageResource(R.mipmap.iv_close);
                enable=0;
            }
        }

    }

    @Override
    public void getPirSlientFail() {
        getPirEnableFlag=false;
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        ToastUtil.getInstance().showShort(R.string.get_pir_slient_fail);
    }

    @Override
    public void getPirSlientThrowable(Throwable throwable) {
        getPirEnableFlag=false;
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_pir_slient_fail);

    }

    @Override
    public void setPirSlientSuccess() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    @Override
    public void setPirSlientFail() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setPirSlientThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }
}
