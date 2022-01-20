package com.kaadas.lock.activity.device.wifilock.newadd;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothSecondActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.adapter.DeviceBleWiFiSearchAdapter;
import com.kaadas.lock.adapter.DeviceSearchAdapter;
import com.kaadas.lock.adapter.inf.OnBindClickListener;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.SearchBleWiFiDevicePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.ISearchDeviceView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.kaadas.lock.utils.dialog.MessageDialog;
import com.kaadas.lock.widget.ScanDeviceRadarView;

public class WifiLockAddNewScanBLEActivity extends BaseActivity<ISearchDeviceView, SearchBleWiFiDevicePresenter<ISearchDeviceView>>
        implements ISearchDeviceView, OnBindClickListener {

    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private ScanDeviceRadarView mRadarView;
    @BindView(R.id.recycler_layout)
    LinearLayout recyclerLayout;
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.research)
    TextView research;

    private DividerItemDecoration dividerItemDecoration;
    private List<BluetoothDevice> mDevices;

    private DeviceSearchAdapter deviceSearchAdapter;
    private DeviceBleWiFiSearchAdapter deviceBleWiFiSearchAdapter;
    private boolean goToHelpActivity; //跳转到帮助页面

    private MessageDialog messageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ble_scan_radar);
        ButterKnife.bind(this);
        mRadarView = (ScanDeviceRadarView) findViewById(R.id.radar_view);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                //界面显示完再执行
                mRadarView.setSearching(true);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (i==-1){
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    ToastUtils.showShort(getString(R.string.aler_no_entry_location));
                    finish();
                    return;
                }
            }
        }
        showRecycler(false);
        initView();
        searchDevices();
    }
    @Override
    protected SearchBleWiFiDevicePresenter<ISearchDeviceView> createPresent() {
        return new SearchBleWiFiDevicePresenter<>();
    }

    private void searchDevices() {
        if (!GpsUtil.isOPen(this)){
            ToastUtils.showLong(R.string.check_phone_not_open_gps_please_open);
            return;
        }
        if(MyApplication.getInstance().isBleOpen()){
            mPresenter.searchDevices();
        }else {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, getString(R.string.ble_no_enable), getString(R.string.ble_no_enable_to_open), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {
                    mPresenter.searchDevices();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(String toString) {

                }
            });
        }
    }

    private void initView() {
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        searchRecycler.addItemDecoration(dividerItemDecoration);

    }

    //当有搜索到蓝牙设备时，显示recycler和重新搜索按钮。
    private void showRecycler(boolean haveDevices) {
        if (haveDevices) {
            recyclerLayout.setVisibility(View.VISIBLE);
            research.setVisibility(View.VISIBLE);
        } else {
            recyclerLayout.setVisibility(View.GONE);
            research.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!goToHelpActivity){
            LogUtils.e("--kaadas--onStop--detachView");
        }
    }

    //当没有搜索到蓝牙设备时，显示对话框。
    private void showNotScanDeviceDialog() {
        didnotdiscoverlock();

        new Handler().postDelayed(new Runnable() {
            public void run() {
            startActivity(new Intent(getApplicationContext(),WifiLockAddNewWiFiScanBLEFailedActivity.class));
                finish();
            }
        }, 2000); //延迟2秒跳转

    }
    public void didnotdiscoverlock(){
        //信息
        messageDialog = new MessageDialog.Builder(this)
                .setMessage(R.string.did_not_discover_lock)
                .create();
        messageDialog.show();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(messageDialog != null){
                    messageDialog.dismiss();

                }
            }
        }, 2000); //延迟2秒消失
    }

    @OnClick({R.id.back, R.id.help, R.id.research})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                goToHelpActivity = true;
                Intent helpIntent = new Intent(this, WifiLockHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.research:
                //获取数据
                searchDevices();
                break;
        }
    }

    @Override
    public void onItemClickListener(View view, int position, BluetoothDevice device) {
        //添加设备
        if (NetUtil.isNetworkAvailable()) {
            mPresenter.checkBind(device);
            showLoading(getString(R.string.is_checking_bind));
        } else {
            ToastUtils.showShort(R.string.noNet);
        }
    }

    @Override
    public void loadDevices(List<BluetoothDevice> devices) {
    }

    @Override
    public void loadBLEWiFiModelDevices(List<BluetoothDevice> devices, List<BluetoothLockBroadcastListBean> broadcastList) {
        if (devices == null) {
            showRecycler(false);
            return;
        }
        if (devices.size()==0){
            showRecycler(false);
            return;
        }

        showRecycler(true);

        mDevices = devices;

        if (deviceBleWiFiSearchAdapter == null) {
            deviceBleWiFiSearchAdapter = new DeviceBleWiFiSearchAdapter(mDevices);
            deviceBleWiFiSearchAdapter.setBindClickListener(this);
            deviceBleWiFiSearchAdapter.setBluetoothLockBroadcast(broadcastList);

            searchRecycler.setAdapter(deviceBleWiFiSearchAdapter);
        } else {
            deviceBleWiFiSearchAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onAlreadyBind(BluetoothDevice device,String uName) {
        LogUtils.e("设备名是1   " + uName);
        String name = "";
        if (!TextUtils.isEmpty(uName)){
            if (uName.contains("@")){ //邮箱用户
                //123456@qq.com
                int index = uName.indexOf("@");
                String head = uName.substring(0,index);
                if (head.length()>3){
                    name = head.substring(0, 3) + "***" + uName.substring(index, uName.length());
                }else {
//                name = head+"***"+uName.substring(index,uName.length());
                    name = uName;
                }
            }else { //手机用户
                if (uName.length()>=11){
                    name = uName.substring(uName.length() - 11, uName.length() - 8) + "***" + uName.substring(uName.length() - 3, uName.length());
                }else {
                    name = uName;
                }
            }
        }
        LogUtils.e("设备名是   " + name);
        binding(device, false,  String.format(getResources().getString(R.string.this_device_already_bind_reset),name ));
    }

    public static void main (String[] args){
        String uName = "8615338786472";
        String name = "";
        if (uName.contains("@")){ //邮箱用户
            //123456@qq.com
            int index = uName.indexOf("@");
            String head = uName.substring(0,index);
            if (head.length()>3){
                name = head.substring(0, 3) + "***" + uName.substring(index, uName.length());
            }else {
//                name = head+"***"+uName.substring(index,uName.length());
                name = uName;
            }
        }else { //手机用户
            if (uName.length()>=11){
                name = uName.substring(uName.length() - 11, uName.length() - 8) + "***" + uName.substring(uName.length() - 3, uName.length());
            }else {
                name = uName;
            }

        }
        System.out.println(name);
    }

    @Override
    public void onNoBind(BluetoothDevice device) {
        hiddenLoading();
        binding(device, true, getResources().getString(R.string.device_not_bind_to_bind));
    }

    private void binding(BluetoothDevice device, Boolean bindFlag, String content) {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), content, getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }
            @Override
            public void right() {
                mPresenter.bindDeviceInit(device, bindFlag);
                showLoading(getString(R.string.connecting_ble));
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(String toString) {

            }
        });


    }

    @Override
    public void onCheckBindFailed(Throwable throwable) {
        ToastUtils.showShort(getString(R.string.bind_failed) + HttpUtils.httpProtocolErrorCode(this, throwable));
        hiddenLoading();
    }

    @Override
    public void onStopScan() {
        LogUtils.e("--kaadas--onStopScan()");

        if (mDevices == null || mDevices.size()==0){
            LogUtils.e("--kaadas--mDevices=="+mDevices);
            showRecycler(false);
            showNotScanDeviceDialog();
            return;
        }
    }

    @Override
    public void onScanFailed(Throwable throwable) {
        ToastUtils.showShort(getString(R.string.scan_fail) + HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void onConnectBLEWIFISuccess(BluetoothLockBroadcastBean broadcastBean,int version) {
        LogUtils.e("shulan onConnectBLEWIFISuccess");
        hiddenLoading();
        Intent nextIntent = new Intent(this, WifiLockAddNewBLEWIFiSwitchActivity.class);
        nextIntent.putExtra(KeyConstants.BLE_VERSION, version);
        nextIntent.putExtra(KeyConstants.BLE_DEVICE_SN, broadcastBean.getDeviceSN());
        nextIntent.putExtra(KeyConstants.BLE_MAC, broadcastBean.getDeviceMAC());
        nextIntent.putExtra(KeyConstants.DEVICE_NAME, broadcastBean.getDeviceName());
        startActivity(nextIntent);

    }
    @Override
    public void onConnectedAndIsOldMode(int version,boolean isBind,String mac,String deviceName) {
        hiddenLoading();
        Intent nextIntent = new Intent(this, AddBluetoothSecondActivity.class);
//        nextIntent.putExtra(KeyConstants.DEVICE_TYPE, type);  //传递设备类型过去
        nextIntent.putExtra(KeyConstants.BLE_VERSION, version);
        nextIntent.putExtra(KeyConstants.IS_BIND, isBind);
        nextIntent.putExtra(KeyConstants.BLE_MAC, mac);
        nextIntent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
        startActivity(nextIntent);
    }

    @Override
    public void onConnectFailed() {
//        showLoading(getString(R.string.connect_failed));
        hiddenLoading();
        ToastUtils.showLong(R.string.connect_failed_retry);
    }

    @Override
    public void readSNFailed() {
//        showLoading(getString(R.string.read_info_failed));
        hiddenLoading();
        ToastUtils.showLong(R.string.connect_failed_retry);
    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void getPwd1() {
//        showLoading(getString(R.string.read_pwd1));
    }


    @Override
    public void getPwd1Success(String pwd1, boolean isBind,int version,String DeviceSn,String mac,String deviceName) {
        LogUtils.e("获取到pwd1   传递给下一个界面" + pwd1+"  SN " + DeviceSn);
        Intent nextIntent = new Intent(this, AddBluetoothSecondActivity.class);
//        nextIntent.putExtra(KeyConstants.DEVICE_TYPE, type);  //传递设备类型过去
        nextIntent.putExtra(KeyConstants.PASSWORD1, pwd1);  //
        nextIntent.putExtra(KeyConstants.IS_BIND, isBind);
        nextIntent.putExtra(KeyConstants.BLE_VERSION, version);
        nextIntent.putExtra(KeyConstants.BLE_DEVICE_SN, DeviceSn);

        nextIntent.putExtra(KeyConstants.BLE_MAC, mac);
        nextIntent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
        startActivity(nextIntent);
        hiddenLoading();
    }

    @Override
    public void pwdIsEmpty() {
        hiddenLoading();
        ToastUtils.showLong(R.string.server_data_error);
    }


    @Override
    public void getPwd1Failed(Throwable throwable) {
        hiddenLoading();
        ToastUtils.showLong(R.string.connect_failed_retry);
    }

    @Override
    public void getPwd1FailedServer(BaseResult result) {
        hiddenLoading();
        ToastUtils.showLong(R.string.connect_failed_retry);
    }

    @Override
    public void notice419() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint),
                getString(R.string.notice_419_call), getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        checkPermissions();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });


        hiddenLoading();
//        ToastUtil.getInstance().showLong(R.string.ble_not_test);
    }

    @Override
    public void onCheckBindFailedServer(String code) {
        hiddenLoading();
        ToastUtils.showLong(R.string.network_exception);
    }

    @Override
    public void checkBindFailed() {
        hiddenLoading();
        ToastUtils.showLong(R.string.network_exception);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        hiddenLoading();
    }

    /**
     * 拨打电话（直接拨打）
     * @param telPhone 电话
     */
    public void call(String telPhone){
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(telPhone));
        startActivity(intent);
    }

    private void checkPermissions() {
        try {
            XXPermissions.with(this)
                    .permission(Permission.CALL_PHONE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                call("tel:"+"4008005919");
                            }
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                        }
                    });


        }catch (Exception e){
            Log.d("", "checkPermissions: "  + e.getMessage());
        }
    }
}
