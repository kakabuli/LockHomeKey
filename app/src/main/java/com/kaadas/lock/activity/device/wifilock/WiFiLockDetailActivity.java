package com.kaadas.lock.activity.device.wifilock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkActivity;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkNo;
import com.kaadas.lock.activity.device.wifilock.family.WifiLockFamilyManagerActivity;
import com.kaadas.lock.activity.device.wifilock.password.WiFiLockPasswordManagerActivity;
import com.kaadas.lock.activity.device.wifilock.password.WifiLockPasswordShareActivity;
import com.kaadas.lock.adapter.WifiLockDetailAdapater;
import com.kaadas.lock.adapter.WifiLockDetailOneLineAdapater;
import com.kaadas.lock.bean.WifiLockFunctionBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockDetailPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockDetailView;
import com.kaadas.lock.publiclibrary.bean.ProductInfo;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.widget.MyGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WiFiLockDetailActivity extends BaseActivity<IWifiLockDetailView, WifiLockDetailPresenter<IWifiLockDetailView>>
        implements IWifiLockDetailView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_bluetooth_name)
    TextView tvBluetoothName;
    @BindView(R.id.iv_lock_icon)
    ImageView ivLockIcon;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_lock_type)
    TextView tvLockType;
    @BindView(R.id.iv_power)
    BatteryView ivPower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.detail_function_recyclerView)
    RecyclerView detailFunctionRecyclerView;
    @BindView(R.id.detail_function_onLine)
    RecyclerView detailFunctionOnLine;
    private WifiLockDetailAdapater adapater;
    private WifiLockDetailOneLineAdapater oneLineAdapater;
    private static final int TO_MORE_REQUEST_CODE = 101;
    String lockType;
    private String wifiSn;
    private WifiLockInfo wifiLockInfo;
    private WiFiLockPassword wiFiLockPassword;
    private RequestOptions options;
    private List<WifiLockFunctionBean> supportFunctions;
    private List<WifiLockShareResult.WifiLockShareUser> shareUsers;
    private List<ProductInfo> productList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_lock_detail);
        ButterKnife.bind(this);
        productList = MyApplication.getInstance().getProductInfos();

        Intent intent = getIntent();
        changeLockIcon(intent);
        ivBack.setOnClickListener(this);
        showLockType();
        initRecycleview();
        initData();
        String lockNickname = wifiLockInfo.getLockNickname();
        tvBluetoothName.setText(TextUtils.isEmpty(lockNickname) ? wifiLockInfo.getWifiSN() : lockNickname);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initData() {
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if (wifiLockInfo != null){
            String localPasswordCache = (String) SPUtils.get(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSn, "");
            if (!TextUtils.isEmpty(localPasswordCache)) {
                wiFiLockPassword = new Gson().fromJson(localPasswordCache, WiFiLockPassword.class);
            }
            String localShareUsers = (String) SPUtils.get(KeyConstants.WIFI_LOCK_SHARE_USER_LIST + wifiSn, "");
            if (!TextUtils.isEmpty(localShareUsers)) {
                shareUsers = new Gson().fromJson(localShareUsers, new TypeToken<List<WifiLockShareResult.WifiLockShareUser>>() {
                }.getType());
                LogUtils.e("本地的分享用户为  shareUsers  " + (shareUsers == null ? 0 : shareUsers.size()));
            }
            initPassword();
            mPresenter.getPasswordList(wifiSn);
            mPresenter.queryUserList(wifiSn);
            dealWithPower(wifiLockInfo.getPower(), wifiLockInfo.getUpdateTime());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.attachView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    private void showLockType() {
        if (wifiLockInfo == null) {
            tvLockType.setText("");
            return;
        }
        lockType = wifiLockInfo.getProductModel();
        if (!TextUtils.isEmpty(lockType)) {
            tvLockType.setText(lockType.contentEquals("K13")?"型号: "+getString(R.string.lan_bo_ji_ni):"型号: "+StringUtil.getSubstringFive(lockType));

            //适配服务器上的产品型号，适配不上则显示锁本地的研发型号
            for (ProductInfo productInfo:productList) {

                if (productInfo.getDevelopmentModel().contentEquals(lockType)){
                    LogUtils.e("--kaadas--productInfo.getProductModel()==" + productInfo.getProductModel());
                    tvLockType.setText(productInfo.getProductModel());
                }
            }

        }
    }

    private void changeLockIcon(Intent intent) {
        wifiSn = intent.getStringExtra(KeyConstants.WIFI_SN);
        LogUtils.e("获取到的设备Sn是   " + wifiSn);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if (wifiLockInfo != null) {
            if (!TextUtils.isEmpty(wifiLockInfo.getProductModel())){
                String model = wifiLockInfo.getProductModel();
                if (model != null) {
                    //本地图片有对应的产品则不获取缓存的产品型号图片，缓存没有则选择尝试下载
//                    if (BleLockUtils.getDetailImageByModel(model) == R.mipmap.bluetooth_lock_default) {
                        options = new RequestOptions()
                                .placeholder(R.mipmap.bluetooth_lock_default)      //加载成功之前占位图
                                .error(R.mipmap.bluetooth_lock_default)      //加载错误之后的错误图
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)    //只缓存最终的图片
                                .dontAnimate()                                    //直接显示图片
                                .fitCenter();//指定图片的缩放类型为fitCenter （是一种“中心匹配”的方式裁剪方式，它裁剪出来的图片长宽都会小于等于ImageView的大小，这样一来。图片会完整地显示出来，但是ImageView可能并没有被填充满）
//                            .centerCrop();//指定图片的缩放类型为centerCrop （是一种“去除多余”的裁剪方式，它会把ImageView边界以外的部分裁剪掉。这样一来ImageView会被填充满，但是这张图片可能不会完整地显示出来(ps:因为超出部分都被裁剪掉了）

                        for (ProductInfo productInfo : productList) {
                            try {
                                if (productInfo.getDevelopmentModel().contentEquals(model)) {

                                    //匹配型号获取下载地址
                                    Glide.with(this).load(productInfo.getAdminUrl()).apply(options).into(ivLockIcon);
                                    return;
                                }
                            } catch (Exception e) {
                                LogUtils.e("--kaadas--:" + e.getMessage());
                            }
                        }
//                    }
                }
                ivLockIcon.setImageResource(BleLockUtils.getDetailImageByModel(wifiLockInfo.getProductModel()));
            }
        }
    }

    @Override
    protected WifiLockDetailPresenter<IWifiLockDetailView> createPresent() {
        return new WifiLockDetailPresenter<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次显示界面都重新设置状态和电量
    }

    private void initPassword() {
        if (wiFiLockPassword != null) {
            for (WifiLockFunctionBean wifiLockFunctionBean : supportFunctions) {
                switch (wifiLockFunctionBean.getType()) {
                    case BleLockUtils.TYPE_PASSWORD:
                        List<WiFiLockPassword.PwdListBean> pwdList = wiFiLockPassword.getPwdList();
                        wifiLockFunctionBean.setNumber(pwdList == null ? 0 : pwdList.size());
                        break;
                    case BleLockUtils.TYPE_FINGER:
                        List<WiFiLockPassword.FingerprintListBean> fingerprintList = wiFiLockPassword.getFingerprintList();
                        wifiLockFunctionBean.setNumber(fingerprintList == null ? 0 : fingerprintList.size());
                        break;
                    case BleLockUtils.TYPE_CARD:
                        List<WiFiLockPassword.CardListBean> cardList = wiFiLockPassword.getCardList();
                        wifiLockFunctionBean.setNumber(cardList == null ? 0 : cardList.size());
                        break;
                    case BleLockUtils.TYPE_FACE_PASSWORD:
                        List<WiFiLockPassword.FaceListBean> faceList = wiFiLockPassword.getFaceList();
                        wifiLockFunctionBean.setNumber(faceList == null ? 0 : faceList.size());
                        break;
                }
            }
        } else {
            for (WifiLockFunctionBean wifiLockFunctionBean : supportFunctions) {
                switch (wifiLockFunctionBean.getType()) {
                    case BleLockUtils.TYPE_PASSWORD:
                        wifiLockFunctionBean.setNumber(0);
                        break;
                    case BleLockUtils.TYPE_FINGER:
                        wifiLockFunctionBean.setNumber(0);
                        break;
                    case BleLockUtils.TYPE_CARD:
                        wifiLockFunctionBean.setNumber(0);
                        break;
                    case BleLockUtils.TYPE_FACE_PASSWORD:
                        wifiLockFunctionBean.setNumber(0);
                        break;
                }
            }
        }
        for (WifiLockFunctionBean wifiLockFunctionBean : supportFunctions) {
            if (wifiLockFunctionBean.getType() == BleLockUtils.TYPE_SHARE) {
                if (shareUsers != null) {
                    wifiLockFunctionBean.setNumber(shareUsers.size());
                } else {
                    wifiLockFunctionBean.setNumber(0);
                }
            }
        }
        adapater.notifyDataSetChanged();
        oneLineAdapater.notifyDataSetChanged();
    }


    private void dealWithPower(int power, long updateTime) {
        //电量：80%
        if (power > 100) {
            power = 100;
        }
        if (power < 0) {
            power = 0;
        }
        String lockPower = power + "%";
        tvPower.setText(lockPower);
        //电量：80%
        if (ivPower != null) {
            ivPower.setPower(power);
            if (power <= 20) {
                ivPower.setColor(R.color.cFF3B30);
                ivPower.setBorderColor(R.color.white);
            } else {
                ivPower.setColor(R.color.c25F290);
                ivPower.setBorderColor(R.color.white);
            }
        }

        //todo  读取电量时间
        long readDeviceInfoTime = updateTime * 1000;
        long todayMillions = DateUtils.getTodayMillions();
        LogUtils.e("更新时间   " + readDeviceInfoTime + "  当前时间  " + System.currentTimeMillis() + "  相差时间 " + ((System.currentTimeMillis() - readDeviceInfoTime))
                + "  今天的时间  " + todayMillions + "    "

        );
        if ((System.currentTimeMillis() - readDeviceInfoTime) < 60 * 60 * 1000) {
            //小于一小时
            tvDate.setText(getString(R.string.device_detail_power_date));
        } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 24 * 60 * 60 * 1000) {
            //小于一天
            if (readDeviceInfoTime > todayMillions) {
                tvDate.setText(getString(R.string.today) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else {
                tvDate.setText(getString(R.string.yesterday) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            }
        } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 2 * 24 * 60 * 60 * 1000) {
            if (readDeviceInfoTime > todayMillions) {
                tvDate.setText(getString(R.string.yesterday) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else {
                tvDate.setText(DateUtils.formatYearMonthDay(readDeviceInfoTime));
            }
        } else {
            tvDate.setText(DateUtils.formatYearMonthDay(readDeviceInfoTime));
        }
    }

    private void initRecycleview() {
        if (wifiLockInfo != null) {

            String functionSet = wifiLockInfo.getFunctionSet(); //锁功能集
            int func;
            try {
                func = Integer.parseInt(functionSet);
            } catch (Exception e) {
                func = 0x64;
            }

            LogUtils.e("功能集是   " + func);
            supportFunctions = BleLockUtils.getWifiLockSupportFunction(func);
            LogUtils.e("获取到的功能集是   " + supportFunctions.size());
            if (wiFiLockPassword != null) {
                for (WifiLockFunctionBean wifiLockFunctionBean : supportFunctions) {
                    switch (wifiLockFunctionBean.getType()) {
                        case BleLockUtils.TYPE_PASSWORD:
                            List<WiFiLockPassword.PwdListBean> pwdList = wiFiLockPassword.getPwdList();
                            wifiLockFunctionBean.setNumber(pwdList == null ? 0 : pwdList.size());
                            break;
                        case BleLockUtils.TYPE_FINGER:
                            List<WiFiLockPassword.FingerprintListBean> fingerprintList = wiFiLockPassword.getFingerprintList();
                            wifiLockFunctionBean.setNumber(fingerprintList == null ? 0 : fingerprintList.size());
                            break;
                        case BleLockUtils.TYPE_CARD:
                            List<WiFiLockPassword.CardListBean> cardList = wiFiLockPassword.getCardList();
                            wifiLockFunctionBean.setNumber(cardList == null ? 0 : cardList.size());
                            break;
                        case BleLockUtils.TYPE_FACE_PASSWORD:
                            List<WiFiLockPassword.FaceListBean> faceList = wiFiLockPassword.getFaceList();
                            wifiLockFunctionBean.setNumber(faceList == null ? 0 : faceList.size());
                            break;
                    }
                }
            }

            MyGridItemDecoration dividerItemDecoration;
            if (supportFunctions.size() <= 2) {
                detailFunctionOnLine.setLayoutManager(new GridLayoutManager(this, 2));
                dividerItemDecoration = new MyGridItemDecoration(this, 2);
                detailFunctionRecyclerView.setVisibility(View.GONE);
                detailFunctionOnLine.setVisibility(View.VISIBLE);
            } else if (supportFunctions.size() <= 4) {
                dividerItemDecoration = new MyGridItemDecoration(this, 2);
                detailFunctionRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                detailFunctionRecyclerView.setVisibility(View.VISIBLE);
                detailFunctionOnLine.setVisibility(View.GONE);
            } else {
                detailFunctionRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                detailFunctionRecyclerView.setVisibility(View.VISIBLE);
                detailFunctionOnLine.setVisibility(View.GONE);
                dividerItemDecoration = new MyGridItemDecoration(this, 3);
            }

            detailFunctionOnLine.addItemDecoration(dividerItemDecoration);
            detailFunctionRecyclerView.addItemDecoration(dividerItemDecoration);

            adapater = new WifiLockDetailAdapater(supportFunctions, new WifiLockDetailAdapater.OnItemClickListener() {
                @Override
                public void onItemClick(int position, WifiLockFunctionBean bluetoothLockFunctionBean) {
                    Intent intent;
                    switch (bluetoothLockFunctionBean.getType()) {
                        case BleLockUtils.TYPE_PASSWORD:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 1);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FINGER:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 2);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_CARD:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 3);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_SMART_SWITCH:


                            if (wifiLockInfo.getSingleFireSwitchInfo() != null) {

                                int SwitchNumber = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber().size();

                                if (SwitchNumber > 0) {
                                    intent = new Intent(WiFiLockDetailActivity.this, SwipchLinkActivity.class);
                                    intent.putExtra(KeyConstants.SWITCH_NUMBER, SwitchNumber);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                    intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfo);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(WiFiLockDetailActivity.this, SwipchLinkNo.class);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                    startActivity(intent);
                                }
                            } else {

                                intent = new Intent(WiFiLockDetailActivity.this, SwipchLinkNo.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                startActivity(intent);
                            }
                            break;
                        case BleLockUtils.TYPE_SHARE:
                            intent = new Intent(WiFiLockDetailActivity.this, WifiLockFamilyManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_MORE:
                            intent = new Intent(WiFiLockDetailActivity.this, WifiLockMoreActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                            break;
                        case BleLockUtils.TYPE_OFFLINE_PASSWORD:
                            intent = new Intent(WiFiLockDetailActivity.this, WifiLockPasswordShareActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FACE_PASSWORD:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 4);
                            startActivity(intent);
                            break;
                    }
                }
            });
            oneLineAdapater = new WifiLockDetailOneLineAdapater(supportFunctions, new WifiLockDetailOneLineAdapater.OnItemClickListener() {
                @Override
                public void onItemClick(int position, WifiLockFunctionBean bluetoothLockFunctionBean) {
                    Intent intent;
                    switch (bluetoothLockFunctionBean.getType()) {
                        case BleLockUtils.TYPE_PASSWORD:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 1);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FINGER:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 2);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_CARD:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 3);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_SMART_SWITCH:

                            if (wifiLockInfo.getSingleFireSwitchInfo() != null) {

                                int SwitchNumber = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber().size();

                                if (SwitchNumber > 0) {
                                    intent = new Intent(WiFiLockDetailActivity.this, SwipchLinkActivity.class);
                                    intent.putExtra(KeyConstants.SWITCH_NUMBER, SwitchNumber);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                    intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfo);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(WiFiLockDetailActivity.this, SwipchLinkNo.class);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                    startActivity(intent);
                                }
                            } else {

                                intent = new Intent(WiFiLockDetailActivity.this, SwipchLinkNo.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                startActivity(intent);
                            }
                            break;
                        case BleLockUtils.TYPE_SHARE:
                            intent = new Intent(WiFiLockDetailActivity.this, WifiLockFamilyManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_MORE:
                            intent = new Intent(WiFiLockDetailActivity.this, WifiLockMoreActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                            break;
                        case BleLockUtils.TYPE_OFFLINE_PASSWORD:
                            intent = new Intent(WiFiLockDetailActivity.this, WifiLockPasswordShareActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FACE_PASSWORD:
                            intent = new Intent(WiFiLockDetailActivity.this, WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.KEY_TYPE, 4);
                            startActivity(intent);
                            break;
                    }
                }
            });
            detailFunctionRecyclerView.setAdapter(adapater);
            detailFunctionOnLine.setAdapter(oneLineAdapater);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_MORE_REQUEST_CODE && resultCode == RESULT_OK) {
            String newName = data.getStringExtra(KeyConstants.WIFI_LOCK_NEW_NAME);
            tvBluetoothName.setText(newName + "");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onGetPasswordSuccess(WiFiLockPassword wiFiLockPassword) {
        this.wiFiLockPassword = wiFiLockPassword;
        initPassword();

    }

    @Override
    public void onGetPasswordFailedServer(BaseResult baseResult) {

    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {

    }

    @Override
    public void querySuccess(List<WifiLockShareResult.WifiLockShareUser> users) {
        shareUsers = users;
        initPassword();
    }

    @Override
    public void queryFailedServer(BaseResult result) {

    }


    @Override
    public void queryFailed(Throwable throwable) {

    }
}