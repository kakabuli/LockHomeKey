package com.kaadas.lock.activity.device.bluetooth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleCheckInfoActivity;
import com.kaadas.lock.mvp.presenter.ble.BleDeviceInfoPresenter;
import com.kaadas.lock.mvp.view.IDeviceInfoView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.ota.DownFileUtils;
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;
import com.kaadas.lock.publiclibrary.ota.face.FaceOtaActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BleDeviceInfoActivity extends BaseBleCheckInfoActivity<IDeviceInfoView, BleDeviceInfoPresenter> implements IDeviceInfoView, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.rl_bluetooth_module_version)
    RelativeLayout rlBluetoothModuleVersion;
    @BindView(R.id.tv_bluetooth_module_version)
    TextView tvBluetoothModuleVersion;
    @BindView(R.id.tv_lock_software_version)
    TextView tvLockSoftwareVersion;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.rv_3d_algorithm)
    TextView tv3dAlgorithm;
    @BindView(R.id.tv_3d_camera)
    TextView tv3dCamera;
    @BindView(R.id.lr_3d_algorithm)
    RelativeLayout lr3dAlgorithm;
    @BindView(R.id.lr_3d_camera)
    RelativeLayout lr3dCamera;
    private BleLockInfo bleLockInfo;
    private String deviceNickname;
    private String name;
    private CheckOTAResult.UpdateFileInfo currentAppInfo;
    private String filePath;
    private int algorithmNumber;
    private int algorithmOtaType;
    private int cameraNumber;
    private int cameraOtaType;
    private static int FACE_OTA_REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_authorization_device_information);

        FileDownloader.setup(this);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();

        deviceNickname = bleLockInfo.getServerLockInfo().getLockNickName();
        tvDeviceName.setText(deviceNickname);
        if (!"1".equals(bleLockInfo.getServerLockInfo().getIs_admin())) {
            rlDeviceName.setVisibility(View.VISIBLE);
        } else {
            rlDeviceName.setVisibility(View.GONE);
        }

        showLoading(getString(R.string.being_get_device_information));
        if (mPresenter.isAuth(bleLockInfo, true)) {
            mPresenter.getBluetoothDeviceInformation();
        }
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.device_info);
        rlBluetoothModuleVersion.setOnClickListener(this);

        tv3dCamera.setOnClickListener(this);
        tv3dAlgorithm.setOnClickListener(this);


        boolean supportFace = BleLockUtils.isSupportFace(bleLockInfo.getServerLockInfo().getFunctionSet());
        if (!supportFace) {
            lr3dAlgorithm.setVisibility(View.GONE);
            lr3dCamera.setVisibility(View.GONE);
        } else {
            lr3dAlgorithm.setVisibility(View.GONE);
            lr3dCamera.setVisibility(View.GONE);
//            lr3dAlgorithm.setVisibility(View.VISIBLE);
//            lr3dCamera.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected BleDeviceInfoPresenter createPresent() {
        return new BleDeviceInfoPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_bluetooth_module_version:

                break;
            case R.id.tv_3d_camera:
                showLoading(getString(R.string.is_check_version));
                String version = tv3dCamera.getText().toString().trim();
                mPresenter.checkOTAInfo(bleLockInfo.getServerLockInfo().getDeviceSN(), version, 3);
                break;
            case R.id.rv_3d_algorithm:
                showLoading(getString(R.string.is_check_version));
                String versionAlgorithm = tv3dAlgorithm.getText().toString().trim();
                mPresenter.checkOTAInfo(bleLockInfo.getServerLockInfo().getDeviceSN(), versionAlgorithm, 2);
                break;
        }
    }

    @Override
    public void SoftwareRevDataSuccess(String data) {
        if (data.contains("-")) {
            String[] split = data.split("-");
            String strModuleHardwareVersion = split[0];
            String strLockHardwareVersion = split[1];
            tvLockSoftwareVersion.setText(strLockHardwareVersion);
            tvBluetoothModuleVersion.setText(strModuleHardwareVersion);
        } else {
            tvBluetoothModuleVersion.setText(data);
        }

    }

    @Override
    public void SoftwareRevDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void HardwareRevDataSuccess(String data) {
        if (data.contains("-")) {
            String[] split = data.split("-");
            String strLockHardwareVersion = split[1];
            tvLockFirmwareVersion.setText(strLockHardwareVersion);
        } else {
            tvLockFirmwareVersion.setText(data);
        }

    }

    @Override
    public void HardwareRevDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void FirmwareRevDataSuccess(String data) {
        tvDeviceModel.setText(StringUtil.getSubstringFive(data));
    }

    @Override
    public void FirmwareRevDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void SerialNumberDataSuccess(String data) {
        tvSerialNumber.setText(data);
    }

    @Override
    public void modifyDeviceNicknameSuccess() {
        hiddenLoading();
        deviceNickname = name;
        tvDeviceName.setText(deviceNickname);
        bleLockInfo.getServerLockInfo().setLockNickName(deviceNickname);
        ToastUtil.getInstance().showLong(R.string.device_nick_name_update_success);
    }

    @Override
    public void modifyDeviceNicknameError(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void modifyDeviceNicknameFail(BaseResult baseResult) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void readDeviceInfoEnd() {
        hiddenLoading();
    }


    @Override
    public void readDeviceInfoFailed(Throwable throwable) {
        hiddenLoading();
    }

    @Override
    public void onReadModuleVersion(int moduleNumber, String version, int otaType) {
        if (moduleNumber == 1) {
            tv3dAlgorithm.setText(version);
            algorithmNumber = moduleNumber;
            algorithmOtaType = otaType;
        } else if (moduleNumber == 2) {
            tv3dCamera.setText(version);
            cameraNumber = moduleNumber;
            cameraOtaType = otaType;
        }
    }

    @Override
    public void onRequestOtaFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.open_ota_failed);
    }

    @Override
    public void onRequestOtaSuccess(String ssid, String password, String version, int number, int otaType, String path) {
        hiddenLoading();
        Intent intent = new Intent();
        intent.putExtra(OtaConstants.filePath, path);
        intent.putExtra(OtaConstants.wifiPassword, password);
        intent.putExtra(OtaConstants.wifiSSid, ssid);
        intent.putExtra(OtaConstants.otaType, otaType);
        intent.putExtra(OtaConstants.moduleNumber, number);
        intent.putExtra(OtaConstants.version, version);
        intent.setClass(BleDeviceInfoActivity.this, FaceOtaActivity.class);
        startActivityForResult(intent,FACE_OTA_REQUEST_CODE);
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户
        if (isConnected) {
            showLoading(getString(R.string.is_authing));
        } else {
            hiddenLoading();
            ToastUtil.getInstance().showLong(R.string.connet_failed_please_near);
        }
    }

    private void downFile(String version, int number, int otaType) {
        showLoadingNoCancel(getString(R.string.is_down_file));
        String PATH = getExternalFilesDir("").getAbsolutePath() + File.separator + "binFile";
        DownFileUtils.createFolder(PATH);
        String fileName = "Kaadas_module" + currentAppInfo.getFileVersion() + "_" + currentAppInfo.getFileMd5() + ".bin";
        filePath = PATH + "/" + fileName;
        DownFileUtils downFileUtils = new DownFileUtils() {
            @Override
            public void onFileExist(String url, String path) {
                hiddenLoading();
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    showLoadingNoCancel(getString(R.string.is_openning_ota));
                    mPresenter.startOTA((byte) number, (byte) otaType, version, filePath);
                }
            }

            @Override
            public void onDownFailed(String url, String path, Throwable throwable) {
                LogUtils.e("下载文件失败，  " + throwable.getMessage());
                hiddenLoading();
                ToastUtil.getInstance().showLong(R.string.down_file_failed_please_retry);
            }

            @Override
            public void onTaskExist(String url, String path) {
                hiddenLoading();
            }

            @Override
            public void onDownComplete(String url, String path) {
                hiddenLoading();
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    showLoading(getString(R.string.is_openning_ota));
                    mPresenter.startOTA((byte) number, (byte) otaType, version, filePath);
                }
            }

            @Override
            public void onDownProgressUpdate(String url, String path, int progress) {

            }
        };
        downFileUtils.downFile(currentAppInfo.getFileUrl(), filePath);
    }

    @OnClick(R.id.tv_device_name)
    public void onClick() {
        //设备名字
        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
        tvTitle.setText(getString(R.string.input_device_name));
        //获取到设备名称设置
        editText.setText(deviceNickname);
        editText.setSelection(deviceNickname.length());
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
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
                if (deviceNickname != null) {
                    if (deviceNickname.equals(name)) {
                        ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                        alertDialog.dismiss();
                        return;
                    }
                }
                showLoading(getString(R.string.upload_device_name));
                if (bleLockInfo != null && bleLockInfo.getServerLockInfo() != null && bleLockInfo.getServerLockInfo().getLockName() != null) {
                    mPresenter.modifyDeviceNickname(bleLockInfo.getServerLockInfo().getLockName(), MyApplication.getInstance().getUid(), name);
                }
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void on3DModuleEnterOta(int type, CheckOTAResult.UpdateFileInfo appInfo) {
        int otaType = 1;
        int number = 1;
        if (type == 2){
            otaType = algorithmOtaType;
            number = algorithmNumber;
        }else if (type == 3)  {
            otaType = cameraOtaType;
            number = cameraNumber;
        }
        showLoadingNoCancel(getString(R.string.is_enter_ota_module));
        currentAppInfo = appInfo;
        downFile(appInfo.getFileVersion(),(byte) number, (byte) otaType);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACE_OTA_REQUEST_CODE ){
            if (resultCode == RESULT_OK){
                if(mPresenter.isAuth(bleLockInfo,true)){
                    mPresenter.checkModuleNumber();
                    LogUtils.e("升级成功   重新获取");
                }
            }
        }
    }
}
