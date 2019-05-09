package com.kaadas.lock.activity.device.cateye.more;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.cateye.CateyeMoreDeviceInformationActivity;
import com.kaadas.lock.activity.device.gatewaylock.more.GatewayMoreActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.presenter.cateye.CatEyeMorePresenter;
import com.kaadas.lock.mvp.view.cateye.IGatEyeView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class CateyeMoreActivity extends BaseActivity<IGatEyeView, CatEyeMorePresenter<IGatEyeView>> implements View.OnClickListener,IGatEyeView {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    String name;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_bell)
    TextView tvBell; //铃声
    @BindView(R.id.rl_bell)
    RelativeLayout rlBell;
    @BindView(R.id.tv_ringnumber)
    TextView tvRingnumber; //响铃次数
    @BindView(R.id.rl_ring_number)
    RelativeLayout rlRingNumber;
    @BindView(R.id.iv_smart_monitor)
    ImageView ivSmartMonitor;
    @BindView(R.id.rl_smart_monitor)
    RelativeLayout rlSmartMonitor;
    @BindView(R.id.tv_resolution)
    TextView tvResolution;//视频分辨率
    @BindView(R.id.rl_resolution)
    RelativeLayout rlResolution;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    boolean smartMonitorStatus;
    
    private String deviceName;
    private String gatewayId;
    private String deviceId;

    private String returnCatEyeInfo;
    private LoadingDialog loadingDialog;
    //0表示正在获取，1表示已获取成功，2表示已获取失败
    private int  getCatInfoStatus=0;
    private CateEyeInfo cateEyeInfo;

    private int pirEnable=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_more);
        ButterKnife.bind(this);
        initData();
        initView();
        initClick();

    }

    @Override
    protected CatEyeMorePresenter<IGatEyeView> createPresent() {
        return new CatEyeMorePresenter<>();
    }

    private void initView() {
        tvContent.setText(getString(R.string.settting));
        if (!TextUtils.isEmpty(deviceName)){
            tvDeviceName.setText(deviceName);
        }


    }

    private void initData() {
      //获取传递过来的数据
        loadingDialog=LoadingDialog.getInstance(this);
      Intent intent=getIntent();
      cateEyeInfo = (CateEyeInfo) intent.getSerializableExtra(KeyConstants.CATE_INFO);
      deviceName=cateEyeInfo.getServerInfo().getNickName();
      gatewayId=cateEyeInfo.getGwID();
      deviceId=cateEyeInfo.getServerInfo().getDeviceId();
      if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
          mPresenter.getCatEyeInfo(gatewayId,deviceId, MyApplication.getInstance().getUid());
          loadingDialog.show(getString(R.string.get_cateye_info_wait));
      }

    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlSmartMonitor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_device_name:
                //设备名字
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_device_name));
                //获取到设备名称设置
                String deviceNickname=tvDeviceName.getText().toString().trim();
                editText.setText(deviceNickname);
                editText.setSelection(deviceNickname.length());
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        //todo 判断名称是否修改
                        if (deviceNickname!=null){
                            if (deviceNickname.equals(name)){
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }
                        if (gatewayId!=null&&deviceId!=null){
                            mPresenter.updateDeviceName(gatewayId,deviceId,name);
                        }
                        alertDialog.dismiss();
                    }
                });
                break;


            case R.id.btn_delete:
                if (getCatInfoStatus==0){
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                }else if (getCatInfoStatus==2){
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                }else {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            if (gatewayId != null && deviceId != null) {
                                mPresenter.deleteCatEye(gatewayId, deviceId, "net");
                            }
                        }

                    });
                }
                break;
            case R.id.rl_device_information:
                if (getCatInfoStatus==0){
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                }else if (getCatInfoStatus==2){
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                }else {
                    if (returnCatEyeInfo!=null) {
                        Intent detailIntent = new Intent(this, CateyeMoreDeviceInformationActivity.class);
                        detailIntent.putExtra(KeyConstants.GET_CAT_EYE_INFO,returnCatEyeInfo);
                        startActivity(detailIntent);
                    }
                }
                break;
            case R.id.rl_smart_monitor:
                if (getCatInfoStatus==0){
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                }else if (getCatInfoStatus==2){
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                }else if (pirEnable==1){
                    if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                        mPresenter.setPirEnable(gatewayId,deviceId,MyApplication.getInstance().getUid(),0);
                        loadingDialog.show("正在关闭智能监测");
                    }
                }else{
                    if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                        mPresenter.setPirEnable(gatewayId,deviceId,MyApplication.getInstance().getUid(),1);
                        loadingDialog.show("正在开启智能监测");
                    }
                }
                break;
        }
    }

    @Override
    public void updateDevNickNameSuccess(String name) {
        tvDeviceName.setText(name);
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(KeyConstants.NAME, name);
        //设置返回数据
        CateyeMoreActivity.this.setResult(RESULT_OK, intent);
        ToastUtil.getInstance().showShort(getString(R.string.update_nick_name));
    }

    @Override
    public void updateDevNickNameFail() {
        ToastUtil.getInstance().showShort(getString(R.string.update_nickname_fail));
    }

    @Override
    public void updateDevNickNameThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.update_nickname_fail));
    }

    @Override
    public void getCatEyeInfoSuccess(CatEyeInfoBeanResult catEyeInfoBean,String payload) {

        if (catEyeInfoBean != null) {

            CatEyeInfoBeanResult.ReturnDataBean returnDataBean=catEyeInfoBean.getReturnData();
            //返回的数据
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            if (tvBell != null) {
                tvBell.setText(getString(R.string.the_tinkle_of_bells)+returnDataBean.getCurBellNum());//设置铃声值
            }
            if (tvRingnumber != null) {
                tvRingnumber.setText(returnDataBean.getBellCount()+"");//响铃次数
            }
             pirEnable = returnDataBean.getPirEnable(); //1未开启，0为关闭
            if (ivSmartMonitor != null) {
                if (pirEnable == 1) {
                    ivSmartMonitor.setImageResource(R.mipmap.iv_open);
                } else {
                    ivSmartMonitor.setImageResource(R.mipmap.iv_close);
                }
            }
            if (tvResolution != null) {
                tvResolution.setText(returnDataBean.getResolution());
            }
            returnCatEyeInfo = payload;
            if (cateEyeInfo != null) {
                cateEyeInfo.setPower(returnDataBean.getPower());
            }
            getCatInfoStatus = 1;
        }
    }
    @Override
    public void getCatEyeInfoFail() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
        getCatInfoStatus=2;
}

    @Override
    public void getCatEveThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
        getCatInfoStatus=2;
    }

    @Override
    public void setPirEnableSuccess(int status) {
        //设置pir成功
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
       if (ivSmartMonitor!=null) {
           if (status == 1) {
               ivSmartMonitor.setImageResource(R.mipmap.iv_open);
           } else {
               ivSmartMonitor.setImageResource(R.mipmap.iv_close);
           }
       }

    }

    @Override
    public void setPirEnableFail() {
        //设置pir失败
        if (loadingDialog!=null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.smart_check_set_fail);
    }

    @Override
    public void setPirEnableThrowable(Throwable throwable) {
        //设置pir异常
        if (loadingDialog!=null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.smart_check_set_fail);

    }


    @Override
    public void deleteDeviceSuccess() {
        //删除成功
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void deleteDeviceFail() {
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }

    @Override
    public void deleteDeviceThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }
}
