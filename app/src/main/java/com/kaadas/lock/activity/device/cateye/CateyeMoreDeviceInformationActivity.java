package com.kaadas.lock.activity.device.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.CateEyeInfoBase;
import com.kaadas.lock.utils.greenDao.db.CateEyeInfoBaseDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;


import java.security.Key;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David
 */
public class CateyeMoreDeviceInformationActivity extends BaseAddToApplicationActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_soft_version)
    TextView tvSoftVersion;
    @BindView(R.id.hardware_version)
    TextView hardwareVersion;
    @BindView(R.id.cat_mcu_version)
    TextView catMcuVersion;
    @BindView(R.id.tv_cat_t_version)
    TextView tvCatTVersion;
    @BindView(R.id.cat_mac_address)
    TextView catMacAddress;
    @BindView(R.id.tv_ip_address)
    TextView tvIpAddress;
    @BindView(R.id.tv_cat_wifi)
    TextView tvCatWifi;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_more_device_information);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        Intent intent = getIntent();
        String strCatEyeInfo =  intent.getStringExtra(KeyConstants.GET_CAT_EYE_INFO);
        if (!TextUtils.isEmpty(strCatEyeInfo)) {
            CatEyeInfoBeanResult catEyeInfo=new Gson().fromJson(strCatEyeInfo,CatEyeInfoBeanResult.class);
            tvSerialNumber.setText(catEyeInfo.getDeviceId());
            tvSoftVersion.setText(catEyeInfo.getReturnData().getSW());
            hardwareVersion.setText(catEyeInfo.getReturnData().getHW());
            catMcuVersion.setText(catEyeInfo.getReturnData().getMCU());
            tvCatTVersion.setText(catEyeInfo.getReturnData().getT200());
            catMacAddress.setText(catEyeInfo.getReturnData().getMacaddr());
            tvIpAddress.setText(catEyeInfo.getReturnData().getIpaddr());
            tvCatWifi.setText(catEyeInfo.getReturnData().getWifiStrength()+"");

        }else {
            String jsonBase = getIntent().getStringExtra(KeyConstants.GET_CAT_EYE_INFO_BASE);
            CateEyeInfoBase cateEyeInfoBase = new Gson().fromJson(jsonBase, CateEyeInfoBase.class);
            Log.e(GeTui.VideoLog,"cateEyeInfoBase:"+cateEyeInfoBase);
            tvSerialNumber.setText(cateEyeInfoBase.getDeviceId());
            tvSoftVersion.setText(cateEyeInfoBase.getSW());
            hardwareVersion.setText(cateEyeInfoBase.getHW());
            catMcuVersion.setText(cateEyeInfoBase.getMCU());
            tvCatTVersion.setText(cateEyeInfoBase.getT200());
            catMacAddress.setText(cateEyeInfoBase.getMacaddr());
            tvIpAddress.setText(cateEyeInfoBase.getIpaddr());
            tvCatWifi.setText(cateEyeInfoBase.getWifiStrength()+"");
        }


    }

    private void initView() {
        tvContent.setText(R.string.device_information);
    }


    private void initListener() {
        ivBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


}
