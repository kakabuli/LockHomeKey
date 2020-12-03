package com.kaadas.lock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.bean.ProductInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetailAdapter extends BaseQuickAdapter<HomeShowBean, BaseViewHolder> {

    private List<ProductInfo> productList = new ArrayList<>();
    private RequestOptions options;

    public DeviceDetailAdapter(@Nullable List<HomeShowBean> data,List<ProductInfo> product) {
        super(R.layout.fragment_device_item, data);
        productList = product;
        //LogUtils.e("--kaadas--productList==" +  productList);
        options = new RequestOptions()
                .placeholder(R.mipmap.default_zigbee_lock_icon)      //加载成功之前占位图
                .error(R.mipmap.default_zigbee_lock_icon)      //加载错误之后的错误图
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)    //只缓存最终的图片
                .dontAnimate()                                      //直接显示图片
//                .override(180, 352); //按照这个像素，调整图片的尺寸，不保持长宽比例
                .fitCenter();//指定图片的缩放类型为fitCenter （是一种“中心匹配”的方式裁剪方式，它裁剪出来的图片长宽都会小于等于ImageView的大小，这样一来。图片会完整地显示出来，但是ImageView可能并没有被填充满）
//                .centerCrop();//指定图片的缩放类型为centerCrop （是一种“去除多余”的裁剪方式，它会把ImageView边界以外的部分裁剪掉。这样一来ImageView会被填充满，但是这张图片可能不会完整地显示出来(ps:因为超出部分都被裁剪掉了）

    }

    //多次加载
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void convert(BaseViewHolder helper, HomeShowBean item) {
//        if (productList == null){
//            productList = MyApplication.getInstance().getProductInfos();
//        }
        BatteryView batteryView = helper.getView(R.id.horizontalBatteryView);
        TextView textView = helper.getView(R.id.device_name);
        if(helper.getLayoutPosition() == 0){
            helper.getView(R.id.view_1).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.view_1).setVisibility(View.GONE);
        }
        if (HomeShowBean.TYPE_GATEWAY == item.getDeviceType()) {
            //隐藏
            helper.getView(R.id.power_layout).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.power_layout).setVisibility(View.VISIBLE);
        }

        switch (item.getDeviceType()) {
            //猫眼
            case HomeShowBean.TYPE_CAT_EYE:
                CateEyeInfo cateEyeInfo = (CateEyeInfo) item.getObject();
                int power = cateEyeInfo.getPower();
                if (power > 100) {
                    power = 100;
                }
                if (power < 0) {
                    power = 0;
                }
                cateEyeInfo.getGwID();

                //根据当前的网关id，找出网关状态,网关离线猫眼也离线，不管服务器传过来什么值
                GatewayInfo catGatewayInfo = MyApplication.getInstance().getGatewayById(cateEyeInfo.getGwID());
                if (catGatewayInfo != null) {
                    if ("offline".equals(catGatewayInfo.getEvent_str())) {
                        isWifiDevice(true, helper, "offline", batteryView, power);
                    } else {
                        isWifiDevice(true, helper, cateEyeInfo.getServerInfo().getEvent_str(), batteryView, power);
                    }
                    helper.setImageResource(R.id.device_image, R.mipmap.cat_eye_icon);
                    batteryView.setPower(power);
                    helper.setText(R.id.device_power_text, power + "%");
                    if (!TextUtils.isEmpty(cateEyeInfo.getServerInfo().getNickName())) {
                        textView.setText(cateEyeInfo.getServerInfo().getNickName());
                    } else {
                        textView.setText(cateEyeInfo.getServerInfo().getDeviceId());
                    }

                }
                break;
            //网关锁
            case HomeShowBean.TYPE_GATEWAY_LOCK:
                GwLockInfo gwLockInfo = (GwLockInfo) item.getObject();
                int gwPower = gwLockInfo.getPower();
                int gatewayLockPower = 0;
                if (gwPower <= 0) {
                    gatewayLockPower = 0;
                } else {
                    gatewayLockPower = gwPower / 2;//网关锁电量需要除2
                }
                //根据当前的网关id，找出网关状态,网关离线猫眼也离线，不管服务器传过来什么值
                GatewayInfo lockGatewayInfo = MyApplication.getInstance().getGatewayById(gwLockInfo.getGwID());
                if (lockGatewayInfo != null) {
                    if ("offline".equals(lockGatewayInfo.getEvent_str())) {
                        isWifiDevice(true, helper, "offline", batteryView, gatewayLockPower);
                    } else {
                        isWifiDevice(true, helper, gwLockInfo.getServerInfo().getEvent_str(), batteryView, gatewayLockPower);
                    }
                    String lockversion = gwLockInfo.getServerInfo().getLockversion();
                    helper.setImageResource(R.id.device_image, R.mipmap.default_zigbee_lock_icon);

                    if (!TextUtils.isEmpty(lockversion) && lockversion.contains(";") &&
                            (lockversion.split(";")[0].startsWith("8100Z") || lockversion.split(";")[0].startsWith("8100A"))) {
                        helper.setImageResource(R.id.device_image, R.mipmap.small_8100);
                    }
                    batteryView.setPower(gatewayLockPower);
                    helper.setText(R.id.device_power_text, gatewayLockPower + "%");
                    if (!TextUtils.isEmpty(gwLockInfo.getServerInfo().getNickName())) {
                        textView.setText(gwLockInfo.getServerInfo().getNickName());
                    } else {
                        textView.setText(gwLockInfo.getServerInfo().getDeviceId());
                    }

                }
                break;
            //网关
            case HomeShowBean.TYPE_GATEWAY:
                GatewayInfo gatewayInfo = (GatewayInfo) item.getObject();
                isWifiDevice(true, helper, gatewayInfo.getEvent_str(), batteryView, 0);
                if (gatewayInfo.getServerInfo() != null && gatewayInfo.getServerInfo().getModel() != null && gatewayInfo.getServerInfo().getModel().equals(KeyConstants.SMALL_GW)) {
                    helper.setImageResource(R.id.device_image, R.mipmap.item_6030);
                } else if (gatewayInfo.getServerInfo() != null && gatewayInfo.getServerInfo().getModel() != null && gatewayInfo.getServerInfo().getModel().equals(KeyConstants.SMALL_GW2)) {
                    helper.setImageResource(R.id.device_image, R.mipmap.item_6030);
                } else {
                    helper.setImageResource(R.id.device_image, R.mipmap.gateway_icon);
                }

                textView.setText(item.getDeviceNickName());
                break;

            //蓝牙
            case HomeShowBean.TYPE_BLE_LOCK:
                String status = "";
                BleLockInfo bleLockInfo = (BleLockInfo) item.getObject();
                if (bleLockInfo != null) {
                    if (bleLockInfo.isAuth()) {
                        status = "online";
                    } else {
                        status = "offline";
                    }
                    int blePower = bleLockInfo.getBattery();
                    if (blePower > 100) {
                        blePower = 100;
                    }
                    if (blePower < 0) {
                        blePower = 0;
                    }

                    isWifiDevice(false, helper, status, batteryView, blePower);
                    batteryView.setPower(blePower);
                    helper.setText(R.id.device_power_text, blePower + "%");
                    textView.setText(bleLockInfo.getServerLockInfo().getLockNickName());

                    String model = bleLockInfo.getServerLockInfo().getModel();
                    //本地图片有对应的产品则不获取缓存的产品型号图片，缓存没有则选择尝试下载
//                    if (BleLockUtils.getSmallImageByModel(model) == R.mipmap.default_zigbee_lock_icon){
                        for (ProductInfo productInfo:productList) {
                            if (productInfo.getDevelopmentModel().contentEquals(model)){
//                                LogUtils.e("--kaadas--productList.DeviceListUrl==" + productInfo.getDeviceListUrl());
//                                LogUtils.e("--kaadas--productList.getDevelopmentModel==" + productInfo.getDevelopmentModel());
                                //匹配型号获取下载地址
//                                Glide.with(mContext).load(productInfo.getDeviceListUrl()).into((ImageView) helper.getView(R.id.device_image));
                                Glide.with(mContext).load(productInfo.getDeviceListUrl()).apply(options).into((ImageView) helper.getView(R.id.device_image));
                                return;
                            }
                        }
//                    }
                    LogUtils.e("--kaadas--:打印");

                    helper.setImageResource(R.id.device_image, BleLockUtils.getSmallImageByModel(model));

                }
                break;

            //wifi锁
            case HomeShowBean.TYPE_WIFI_LOCK:
            case HomeShowBean.TYPE_WIFI_VIDEO_LOCK:
                WifiLockInfo wifiLockInfo = (WifiLockInfo) item.getObject();
                if (wifiLockInfo != null) {
                    int power1 = wifiLockInfo.getPower();
                    isWifiDevice(false, helper, "online", batteryView, power1);

                    batteryView.setPower(power1);
                    helper.setText(R.id.device_power_text, power1 + "%");
                    textView.setText(wifiLockInfo.getLockNickname());
                    helper.setVisible(R.id.device_type_image, false);
                    helper.setVisible(R.id.device_type_text, false);

                    String model = wifiLockInfo.getProductModel();
                    if (model != null) {
                        //本地图片有对应的产品则不获取缓存的产品型号图片，缓存没有则选择尝试下载
//                        if (BleLockUtils.getSmallImageByModel(model) == R.mipmap.default_zigbee_lock_icon) {
                            for (ProductInfo productInfo : productList) {
                                try {

                                    if (productInfo.getDevelopmentModel().contentEquals(model)) {
//                                LogUtils.e("--kaadas--productList.getDevelopmentModel==" + productInfo.getDevelopmentModel());
//                                LogUtils.e("--kaadas--productList.DeviceListUrl==" + productInfo.getDeviceListUrl());
                                        //匹配型号获取下载地址
//                                Glide.with(mContext).load(productInfo.getDeviceListUrl()).into((ImageView) helper.getView(R.id.device_image));
                                        Glide.with(mContext).load(productInfo.getDeviceListUrl()).apply(options).into((ImageView) helper.getView(R.id.device_image));
                                        return;
                                    }
                                } catch (Exception e) {
                                    LogUtils.e("--kaadas--:" + e.getMessage());
                                }

                            }
//                        }
                    }
                    LogUtils.e("--kaadas--:打印");

                    helper.setImageResource(R.id.device_image, BleLockUtils.getSmallImageByModel(model));

                }
                break;
        }
    }

    public void isWifiDevice(boolean flag, BaseViewHolder helper, String status, BatteryView batteryView, int pw) {
        LogUtils.e(status + "===");
        if ("online".equals(status)) {
            //在线
            if (flag) {
                helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_connect);
            } else {
                helper.setImageResource(R.id.device_type_image, R.mipmap.bluetooth_connection);
            }
            helper.setText(R.id.device_type_text, R.string.online);
            if (pw <= 20) {
                batteryView.setColor(R.color.cFF3B30);
            } else {
                batteryView.setColor(R.color.c25F290);
            }
            helper.setTextColor(R.id.device_type_text, Color.parseColor("#1F96F7"));
        } else {
            //离线
            if (flag) {
                helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
            } else {
                helper.setImageResource(R.id.device_type_image, R.mipmap.bluetooth_disconnenction);
            }
            batteryView.setColor(R.color.cD6D6D6);
            helper.setText(R.id.device_type_text, R.string.offline);
            helper.setTextColor(R.id.device_type_text, Color.parseColor("#999999"));
        }
    }

    public void setProductList(List<ProductInfo> productInfo){
        if(productInfo.size() >0){
            this.productList.clear();
            this.productList.addAll(productInfo);
        }
    }
}
