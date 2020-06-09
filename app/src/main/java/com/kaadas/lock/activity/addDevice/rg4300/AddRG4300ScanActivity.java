package com.kaadas.lock.activity.addDevice.rg4300;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothSecondActivity;
import com.kaadas.lock.adapter.DeviceSearchAdapter;
import com.kaadas.lock.adapter.inf.OnBindClickListener;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.bean.BluetoothLockBroadcastListBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.SearchDevicePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.ISearchDeviceView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRG4300ScanActivity extends BaseActivity<ISearchDeviceView, SearchDevicePresenter<ISearchDeviceView>>
        implements ISearchDeviceView, OnBindClickListener {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.recycler_layout)
    LinearLayout recyclerLayout;
    @BindView(R.id.research)
    Button research;
    @BindView(R.id.device_add_search)
    ImageView deviceAddSearch;
    @BindView(R.id.tv_is_searching)
    TextView tvIsSearching;

    private DeviceSearchAdapter deviceSearchAdapter;

    private DividerItemDecoration dividerItemDecoration;
    private List<BluetoothDevice> mDevices;
    List<BluetoothLockBroadcastListBean> broadcastList = new ArrayList<>();
    List<BluetoothLockBroadcastBean> broadcastItemList = new ArrayList<>();

    private ObjectAnimator ivGreenObjectAnimator;
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rg4300_scan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (i==-1){
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    ToastUtil.getInstance().showShort(getString(R.string.aler_no_entry_location));
                    finish();
                    return;
                }
            }
        }

        ButterKnife.bind(this);
        showRecycler(false);
        initView();
        initData();
    }

    private void initData() {
        //获取数据
        if (GpsUtil.isOPen(this)){
            initAnimation();
            tvIsSearching.setVisibility(View.VISIBLE);
            mPresenter.searchDevices();
        }else {
            ToastUtil.getInstance().showLong(R.string.check_phone_not_open_gps_please_open);
        }
    }

    @Override
    protected SearchDevicePresenter<ISearchDeviceView> createPresent() {
        return new SearchDevicePresenter<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
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

    //当没有搜索到蓝牙设备时，显示对话框。
    private void showNotScanDeviceDialog() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(this, getResources().getString(R.string.no_find_connect_device), getResources().getString(R.string.cancel), getResources().getString(R.string.rescan),"#333333","#1F96F7", new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
                finish();
            }
            @Override
            public void right() {
                //重新搜索设备.
                if (tvIsSearching!=null){
                    tvIsSearching.setVisibility(View.VISIBLE);
                }
                mPresenter.searchDevices();
            }
        });
    }


    private void initAnimation() {
        Path path = new Path();
        path.addOval(-38, -38, 38, 38, Path.Direction.CW);
        ivGreenObjectAnimator = ObjectAnimator.ofFloat(deviceAddSearch, View.TRANSLATION_X, View.TRANSLATION_Y, path);
        ivGreenObjectAnimator.setDuration(2000).setRepeatCount(ValueAnimator.INFINITE);
        ivGreenObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
        ivGreenObjectAnimator.start();
    }

    /**
     * 停止搜索图片
     * @param
     */
    private void stopAnimation() {
        deviceAddSearch.clearAnimation();
        if (ivGreenObjectAnimator!=null){
            ivGreenObjectAnimator.cancel();
            ivGreenObjectAnimator.setupEndValues();
        }
    }

    @OnClick({R.id.back, R.id.help, R.id.research})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent helpIntent = new Intent(this, DeviceAddHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.research:
                //获取数据
                if (GpsUtil.isOPen(this)){
                    initAnimation();
                    tvIsSearching.setVisibility(View.VISIBLE);
                    mPresenter.searchDevices();
                }else {
                    ToastUtil.getInstance().showLong(R.string.check_phone_not_open_gps_please_open);
                }
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
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    @Override
    public void loadDevices(List<BluetoothDevice> devices) {
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
        if (deviceSearchAdapter == null) {
            deviceSearchAdapter = new DeviceSearchAdapter(mDevices);
            deviceSearchAdapter.setBindClickListener(this);
            searchRecycler.setAdapter(deviceSearchAdapter);
        } else {
            deviceSearchAdapter.notifyDataSetChanged();
        }
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
//        broadcastItemList.add(broadcastBean);
//        broadcastList.add(new BluetoothLockBroadcastListBean(broadcastItemList, mDevices));
        if (deviceSearchAdapter == null) {
            deviceSearchAdapter = new DeviceSearchAdapter(mDevices);
            deviceSearchAdapter.setBindClickListener(this);
            deviceSearchAdapter.setBluetoothLockBroadcast(broadcastList);

            searchRecycler.setAdapter(deviceSearchAdapter);
        } else {
            deviceSearchAdapter.notifyDataSetChanged();
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
        });


    }

    @Override
    public void onCheckBindFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.bind_failed) + HttpUtils.httpProtocolErrorCode(this, throwable));
        hiddenLoading();
    }

    @Override
    public void onStopScan() {
        stopAnimation();
        tvIsSearching.setVisibility(View.INVISIBLE);

        if (mDevices == null) {
            showRecycler(false);
            showNotScanDeviceDialog();
            return;
        }
        if (mDevices.size()==0){
            showRecycler(false);
            showNotScanDeviceDialog();
            return;
        }
    }

    @Override
    public void onScanFailed(Throwable throwable) {
        stopAnimation();
        ToastUtil.getInstance().showShort(getString(R.string.scan_fail) + HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void onConnectBLEWIFISuccess(BluetoothLockBroadcastBean broadcastBean,int version) {

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
        ToastUtil.getInstance().showLong(R.string.connect_failed_retry);
    }

    @Override
    public void readSNFailed() {
//        showLoading(getString(R.string.read_info_failed));
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.connect_failed_retry);
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
        ToastUtil.getInstance().showLong(R.string.server_data_error);
    }


    @Override
    public void getPwd1Failed(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.connect_failed_retry);
    }

    @Override
    public void getPwd1FailedServer(BaseResult result) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.connect_failed_retry);
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
                        call("tel:"+"4001166667");
                    }
                });


        hiddenLoading();
//        ToastUtil.getInstance().showLong(R.string.ble_not_test);
    }

    @Override
    public void onCheckBindFailedServer(String code) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.network_exception);
    }

    @Override
    public void checkBindFailed() {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.network_exception);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        hiddenLoading();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:"+"4001166667");
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     * @param telPhone 电话
     */
    public void call(String telPhone){
        if(checkReadPermission(Manifest.permission.CALL_PHONE,REQUEST_CALL_PERMISSION)){
            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(telPhone));
            startActivity(intent);
        }
    }

    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }


}
