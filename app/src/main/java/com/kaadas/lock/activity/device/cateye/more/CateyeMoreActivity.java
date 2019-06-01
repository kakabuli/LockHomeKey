package com.kaadas.lock.activity.device.cateye.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.cateye.CateyeMoreDeviceInformationActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.CatEyeMorePresenter;
import com.kaadas.lock.mvp.view.cateye.IGatEyeView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.EditTextWatcher;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.CateEyeInfoBase;
import com.kaadas.lock.utils.greenDao.db.CateEyeInfoBaseDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.utils.greenDao.db.HistoryInfoDao;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class CateyeMoreActivity extends BaseActivity<IGatEyeView, CatEyeMorePresenter<IGatEyeView>> implements View.OnClickListener, IGatEyeView {
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

    @BindView(R.id.rl_smart_monitor)
    RelativeLayout rlSmartMonitor;
    @BindView(R.id.tv_resolution)
    TextView tvResolution;//视频分辨率
    @BindView(R.id.rl_resolution)
    RelativeLayout rlResolution;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    boolean smartMonitorStatus;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.rl_volume)
    RelativeLayout rlVolume;
    @BindView(R.id.tv_smart_monitor)
    TextView tvSmartMonitor;

    private String deviceName;
    private String gatewayId;
    private String deviceId;

    private String returnCatEyeInfo;
    private LoadingDialog loadingDialog;
    //0表示正在获取，1表示已获取成功，2表示已获取失败
    private int getCatInfoStatus = 0;
    private CateEyeInfo cateEyeInfo;

    private AlertDialog  deleteAlertDialog;
    private Context context;

    private int pirEnable=-1; //智能监测开关

    private int sdStatus=-1; //sd卡状态

    private String pirwander="";//pir徘徊监测
    CateEyeInfoBase cateEyeInfoBase=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_more);
        ButterKnife.bind(this);
        context = this;
        daoSession = MyApplication.getInstance().getDaoWriteSession();
        initData();
        initView();
        initClick();
        cateEyeInfoBase= daoSession.getCateEyeInfoBaseDao().queryBuilder()
                .where(CateEyeInfoBaseDao.Properties.DeviceId.eq(deviceId)).build()
                .unique();
        if(cateEyeInfoBase!=null){
            tvBell.setText(getString(R.string.the_tinkle_of_bells) + cateEyeInfoBase.getCurBellNum());//设置铃声值
                switch (cateEyeInfoBase.getBellVolume()){
                    case 1:
                        tvVolume.setText(getString(R.string.centre));
                        break;
                    case 2:

                        tvVolume.setText(getString(R.string.low));
                        break;

                    case 0:
                        tvVolume.setText(getString(R.string.high));
                        break;
                }
            tvRingnumber.setText(cateEyeInfoBase.getBellCount() + "");//响铃次数
            tvResolution.setText(cateEyeInfoBase.getResolution());
        }
    }

    @Override
    protected CatEyeMorePresenter<IGatEyeView> createPresent() {
        return new CatEyeMorePresenter<>();
    }

    private void initView() {
        tvContent.setText(getString(R.string.settting));
        if (!TextUtils.isEmpty(deviceName)) {
            tvDeviceName.setText(deviceName);
        }


    }

    private void initData() {
        //获取传递过来的数据
        loadingDialog = LoadingDialog.getInstance(this);
        Intent intent = getIntent();
        cateEyeInfo = (CateEyeInfo) intent.getSerializableExtra(KeyConstants.CATE_INFO);
        if (!TextUtils.isEmpty(cateEyeInfo.getServerInfo().getNickName())){
            deviceName = cateEyeInfo.getServerInfo().getNickName();
        }else{
            deviceName=cateEyeInfo.getServerInfo().getDeviceId();
        }
        gatewayId = cateEyeInfo.getGwID();
        deviceId = cateEyeInfo.getServerInfo().getDeviceId();
        if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getCatEyeInfo(gatewayId, deviceId, MyApplication.getInstance().getUid());
            loadingDialog.show(getString(R.string.get_cateye_info_wait));
        }

    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlSmartMonitor.setOnClickListener(this);
        rlRingNumber.setOnClickListener(this);//响铃次数
        rlResolution.setOnClickListener(this);//分辨率
        rlVolume.setOnClickListener(this);
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
                String deviceNickname = tvDeviceName.getText().toString().trim();
                editText.setText(deviceNickname);
                editText.setSelection(deviceNickname.length());
                editText.addTextChangedListener(new EditTextWatcher(this,null,editText,50));
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
                        if (TextUtils.isEmpty(name)){
                            ToastUtil.getInstance().showShort(getString(R.string.device_name_cannot_be_empty));
                            return;
                        }
                        //todo 判断名称是否修改
                        if (deviceNickname != null) {
                            if (deviceNickname.equals(name)) {
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }
                        if (gatewayId != null && deviceId != null) {
                            mPresenter.updateDeviceName(gatewayId, deviceId, name);
                        }
                        alertDialog.dismiss();
                    }
                });
                break;


            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }
                    @Override
                    public void right() {
                      //  if (gatewayId != null && deviceId != null) {
                        if (!TextUtils.isEmpty(gatewayId) &&  !TextUtils.isEmpty(deviceId)) {
                            mPresenter.deleteCatEye(gatewayId, deviceId, "net");
                            deleteAlertDialog=AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.delete_be_being));
                            deleteAlertDialog.setCancelable(false);
                        }
                    }

                });
                break;
            case R.id.rl_device_information:
                if (getCatInfoStatus == 0) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                } else if (getCatInfoStatus == 2) {
                    if(cateEyeInfoBase!=null){
                        Intent detailIntent = new Intent(this, CateyeMoreDeviceInformationActivity.class);
                        String jsonBase = new Gson().toJson(cateEyeInfoBase);
                        detailIntent.putExtra(KeyConstants.GET_CAT_EYE_INFO_BASE, jsonBase);
                        startActivity(detailIntent);
                        return;
                    }
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                } else {
                    if (!TextUtils.isEmpty(returnCatEyeInfo)) {
                            Intent detailIntent = new Intent(this, CateyeMoreDeviceInformationActivity.class);
                            detailIntent.putExtra(KeyConstants.GET_CAT_EYE_INFO, returnCatEyeInfo);
                            startActivity(detailIntent);
                    }
                }
                break;
            case R.id.rl_smart_monitor:
                if (getCatInfoStatus == 0) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                } else if (getCatInfoStatus == 2) {
                    if(cateEyeInfoBase!=null){
                        Intent smartEyeIntent = new Intent(this, SmartEyeActivity.class);
                        String jsonBase = new Gson().toJson(cateEyeInfoBase);
                        smartEyeIntent.putExtra(KeyConstants.GET_CAT_EYE_INFO, returnCatEyeInfo);
                        smartEyeIntent.putExtra(KeyConstants.GET_CAT_EYE_INFO_BASE, jsonBase);
                        startActivity(smartEyeIntent);
                        return;
                    }
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                }else{
                    if (returnCatEyeInfo != null) {
                        Intent smartEyeIntent = new Intent(this, SmartEyeActivity.class);
                        smartEyeIntent.putExtra(KeyConstants.GET_CAT_EYE_INFO, returnCatEyeInfo);
                        startActivity(smartEyeIntent);
                    }
                }
                break;
            case R.id.rl_ring_number:
                //响铃次数
                if (getCatInfoStatus == 0) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                } else if (getCatInfoStatus == 2) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                } else {
                    String ring = tvRingnumber.getText().toString().trim();
                    if (!TextUtils.isEmpty(ring) && !TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                        Intent ringtIntent = new Intent(this, CatEyeRingNumberActivity.class);
                        ringtIntent.putExtra(KeyConstants.CAT_EYE_RING_NUMBER, ring);
                        ringtIntent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                        ringtIntent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                        startActivityForResult(ringtIntent,KeyConstants.RING_NUMBER_REQUESET_CODE);
                        //startActivity(ringtIntent);
                    }
                }

                break;
            case R.id.rl_resolution:
                //分辨率
                if (getCatInfoStatus == 0) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                } else if (getCatInfoStatus == 2) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                } else {
                    String resolution = tvResolution.getText().toString().trim();
                    if (!TextUtils.isEmpty(resolution) && !TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                        Intent resolutionIntent = new Intent(this, CatEyeResolutionActivity.class);
                        resolutionIntent.putExtra(KeyConstants.CAT_EYE_RESOLUTION, resolution);
                        resolutionIntent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                        resolutionIntent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                        startActivityForResult(resolutionIntent,KeyConstants.RESOLUTION_REQUEST_CODE);
                    }
                }
                break;
                //音量
            case R.id.rl_volume:
                //音量
                if (getCatInfoStatus == 0) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_wait);
                    return;
                } else if (getCatInfoStatus == 2) {
                    ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
                    return;
                } else {
                    String volume = tvVolume.getText().toString().trim();
                    if(volume.equals("中")){
                        volume="低";
                    }else  if(volume.equals("低")){
                        volume="中";
                    }
                    if (!TextUtils.isEmpty(volume) && !TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                        Intent volumeIntent = new Intent(this, CatEyeVolumeActivity.class);
                        volumeIntent.putExtra(KeyConstants.CAT_EYE_VOLUME, volume);
                        volumeIntent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                        volumeIntent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                        startActivityForResult(volumeIntent,KeyConstants.VOLUME_REQUESET_CODE);
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
    private DaoSession daoSession;
    @Override
    public void getCatEyeInfoSuccess(CatEyeInfoBeanResult catEyeInfoBean, String payload) {

        if (catEyeInfoBean != null) {

            CatEyeInfoBeanResult.ReturnDataBean returnDataBean = catEyeInfoBean.getReturnData();
            //返回的数据
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            if (tvBell != null) {
                tvBell.setText(getString(R.string.the_tinkle_of_bells) + returnDataBean.getCurBellNum());//设置铃声值
            }
            if (tvRingnumber != null) {
                tvRingnumber.setText(returnDataBean.getBellCount() + "");//响铃次数
            }
            pirEnable=returnDataBean.getPirEnable();
            sdStatus=returnDataBean.getSdStatus();
            pirwander=returnDataBean.getPirWander();

            if (tvVolume!=null){
                switch (returnDataBean.getBellVolume()){
                    case 1:
                        tvVolume.setText(getString(R.string.centre));
                        break;
                    case 2:
                        tvVolume.setText(getString(R.string.low));

                        break;

                    case 0:
                        tvVolume.setText(getString(R.string.high));
                        break;
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


//            CateEyeInfoBase cateEyeInfoBase= daoSession.getCateEyeInfoBaseDao().queryBuilder()
//                    .where(CateEyeInfoBaseDao.Properties.DeviceId.eq(deviceId)).build()
//                    .unique();
            if(cateEyeInfoBase==null){
                cateEyeInfoBase =new CateEyeInfoBase(null, returnDataBean.getCurBellNum(),
                        returnDataBean.getBellVolume(), returnDataBean.getBellCount(),
                        returnDataBean.getResolution(),
                        deviceId, returnDataBean.getSW(),
                        returnDataBean.getHW(),
                        returnDataBean.getMCU(),
                        returnDataBean.getT200(),
                        returnDataBean.getMacaddr(),
                        returnDataBean.getIpaddr(),
                        returnDataBean.getWifiStrength(),
                        returnDataBean.getPirEnable(),
                        returnDataBean.getPirWander(),
                        returnDataBean.getSdStatus(),
                        gatewayId);
                daoSession.getCateEyeInfoBaseDao().insertOrReplace(cateEyeInfoBase);
            }else {
                 cateEyeInfoBase.setBellVolume(returnDataBean.getCurBellNum());
                 cateEyeInfoBase.setBellVolume(returnDataBean.getBellVolume());
                 cateEyeInfoBase.setBellCount( returnDataBean.getBellCount());
                 cateEyeInfoBase.setResolution(returnDataBean.getResolution());
                 cateEyeInfoBase.setDeviceId(deviceId);
                 cateEyeInfoBase.setSW(returnDataBean.getSW());
                 cateEyeInfoBase.setHW(returnDataBean.getHW());
                 cateEyeInfoBase.setMCU(returnDataBean.getMCU());
                 cateEyeInfoBase.setT200(returnDataBean.getT200());
                 cateEyeInfoBase.setMacaddr(returnDataBean.getMacaddr());
                 cateEyeInfoBase.setIpaddr(returnDataBean.getIpaddr());
                 cateEyeInfoBase.setWifiStrength(returnDataBean.getWifiStrength());
                 cateEyeInfoBase.setPirEnable(returnDataBean.getPirEnable());
                 cateEyeInfoBase.setPirWander(returnDataBean.getPirWander());
                 cateEyeInfoBase.setSdStatus(returnDataBean.getSdStatus());
                 cateEyeInfoBase.setGwid(gatewayId);
                daoSession.getCateEyeInfoBaseDao().insertOrReplace(cateEyeInfoBase);
            }
        }
    }

    @Override
    public void getCatEyeInfoFail() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
        getCatInfoStatus = 2;
    }

    @Override
    public void getCatEveThrowable(Throwable throwable) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_cateye_info_fail);
        getCatInfoStatus = 2;
    }



    @Override
    public void deleteDeviceSuccess() {
        //删除成功
        if (deleteAlertDialog!=null){
            deleteAlertDialog.dismiss();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void deleteDeviceFail() {
        if (deleteAlertDialog!=null){
            deleteAlertDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }

    @Override
    public void deleteDeviceThrowable(Throwable throwable) {
        if (deleteAlertDialog!=null){
            deleteAlertDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //铃声设置成功的返回
        if (requestCode==KeyConstants.RING_NUMBER_REQUESET_CODE){
            if (resultCode== Activity.RESULT_OK){
             int ringNumber= data.getIntExtra(KeyConstants.RIGH_NUMBER,0);
             if (tvRingnumber!=null){
                 tvRingnumber.setText(ringNumber+"");
              }
            }
        }
        //音量
        if (requestCode==KeyConstants.VOLUME_REQUESET_CODE){
            if (resultCode== Activity.RESULT_OK){
                int volumeNumber= data.getIntExtra(KeyConstants.VOLUME_NUMBER,-1);
                if (tvVolume!=null){
                    switch (volumeNumber){
                        case 1:
                            tvVolume.setText(getString(R.string.centre));
                        //    tvVolume.setText(getString(R.string.low));
                            break;
                        case 2:

                            tvVolume.setText(getString(R.string.low));
                            break;

                        case 0:
                            tvVolume.setText(getString(R.string.high));
                            break;
                    }

                }
            }
        }

        //分辨率
        if (requestCode==KeyConstants.RESOLUTION_REQUEST_CODE){
            if (resultCode== Activity.RESULT_OK){
                String resolution= data.getStringExtra(KeyConstants.RESOLUTION_NUMBER);
                if (tvResolution!=null){
                    tvResolution.setText(resolution);
                }
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().setPirEnableStates(1);
    }
}
