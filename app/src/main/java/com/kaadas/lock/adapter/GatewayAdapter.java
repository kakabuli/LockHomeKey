package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

public class GatewayAdapter extends BaseQuickAdapter<HomeShowBean, BaseViewHolder> {


    public GatewayAdapter(@Nullable List<HomeShowBean> data) {
        super(R.layout.item_gateway_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeShowBean item) {
        /*helper.setText(R.id.device_name, item.getDeviceNickName());*/
        TextView textView=helper.getView(R.id.device_name);
        TextView tvDeviceStatus=helper.getView(R.id.tv_device_status);
        int deviceType = item.getDeviceType();
        ImageView ivDeviceType=helper.getView(R.id.iv_device_type);
        BatteryView batteryView= helper.getView(R.id.horizontalBatteryView);
        TextView powerView=helper.getView(R.id.device_power_text);
        String deviceStatus="";
        int power=0;
        int realPw=0;
        //猫眼
        if (HomeShowBean.TYPE_CAT_EYE==deviceType){
            ivDeviceType.setImageResource(R.mipmap.cat_eye_icon);
            CateEyeInfo cateEyeInfo= (CateEyeInfo) item.getObject();
            GatewayInfo  gatewayInfo=MyApplication.getInstance().getGatewayById(cateEyeInfo.getGwID());
            if (gatewayInfo!=null) {
                if ("online".equals(gatewayInfo.getEvent_str())) {
                    deviceStatus = cateEyeInfo.getServerInfo().getEvent_str();
                } else {
                    deviceStatus = "offline";
                }
            }
            power=cateEyeInfo.getPower();
            realPw=power;
            batteryView.setPower(power);
            powerView.setText(power+"%");
            if (!TextUtils.isEmpty(cateEyeInfo.getServerInfo().getDeviceId())){
                textView.setText(cateEyeInfo.getServerInfo().getNickName());
            }else{
                textView.setText(cateEyeInfo.getServerInfo().getDeviceId());
            }
        }else if (HomeShowBean.TYPE_GATEWAY_LOCK==deviceType){
            //网关

            GwLockInfo gwLockInfo= (GwLockInfo) item.getObject();
            GatewayInfo  gatewayInfo=MyApplication.getInstance().getGatewayById(gwLockInfo.getGwID());
            String lockversion = gwLockInfo.getServerInfo().getLockversion();
            ivDeviceType.setImageResource(R.mipmap.default_zigbee_lock_icon);

            if (!TextUtils.isEmpty(lockversion) && lockversion.contains(";") &&
                    (lockversion.split(";")[0].startsWith("8100Z") || lockversion.split(";")[0].startsWith("8100A"))) {
                ivDeviceType.setImageResource(R.mipmap.small_8100);
            }

            if (gatewayInfo!=null) {
                if ("online".equals(gatewayInfo.getEvent_str())) {
                    deviceStatus = gwLockInfo.getServerInfo().getEvent_str();
                } else {
                    deviceStatus = "offline";
                }
            }

            power=gwLockInfo.getPower();
            int realPower=power/2;
            realPw=realPower;
            batteryView.setPower(realPower);
            powerView.setText(realPower+"%");
            if (!TextUtils.isEmpty(gwLockInfo.getServerInfo().getNickName())){
                textView.setText(gwLockInfo.getServerInfo().getNickName());
            }else{
                textView.setText(gwLockInfo.getServerInfo().getDeviceId());
            }
        }

        if ("online".equals(deviceStatus)) {
            helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_connect);
            tvDeviceStatus.setText(R.string.online);
            tvDeviceStatus.setTextColor(mContext.getResources().getColor(R.color.c1F96F7));
            if (realPw<=20){
                batteryView.setColor(R.color.cFF3B30);
            }else{
                batteryView.setColor(R.color.c25F290);
            }


        } else {
            helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
            tvDeviceStatus.setText(R.string.offline);
            tvDeviceStatus.setTextColor(mContext.getResources().getColor(R.color.c999999));
            batteryView.setColor(R.color.cD6D6D6);
        }


    }
}
