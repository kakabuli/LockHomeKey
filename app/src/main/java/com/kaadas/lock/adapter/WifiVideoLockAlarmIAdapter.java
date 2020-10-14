package com.kaadas.lock.adapter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoDeviceRecordActivity;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.RotateTransformation;

import java.util.List;

import la.xiong.androidquick.tool.SizeUtils;

public class WifiVideoLockAlarmIAdapter extends BaseQuickAdapter<WifiVideoLockAlarmRecord, BaseViewHolder> {
    List<WifiVideoLockAlarmRecord> data;

    private VideoRecordCallBackLinstener mListener;
    public interface VideoRecordCallBackLinstener{
        void onVideoRecordCallBackLinstener(WifiVideoLockAlarmRecord record);
    }

    public void setVideoRecordCallBackLinstener(VideoRecordCallBackLinstener listener){
        this.mListener = listener;
    }

    public WifiVideoLockAlarmIAdapter(@Nullable List<WifiVideoLockAlarmRecord> data,VideoRecordCallBackLinstener listener) {
        super(R.layout.item_wifi_video_lock_alarm_record,data);
        this.data = data;
        this.mListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiVideoLockAlarmRecord record) {
        boolean first = record.isFirst();
        boolean last = record.isLast();

        int size = getData().size();
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvDayTime = helper.getView(R.id.tv_day_time);
//        TextView tvContent = helper.getView(R.id.tv_content);
        TextView tvRight = helper.getView(R.id.tv_right);

        long time = Long.parseLong(record.getTime());
        String s = DateUtils.currentLong2HourMin(time * 1000);
        tvTime.setText(TextUtils.isEmpty(s) ? "" : s);
        tvDayTime.setVisibility( first? View.VISIBLE : View.GONE);
        //设置天时间
        String dayTime = record.getDayTime();
        if (!TextUtils.isEmpty(dayTime)) {
            if (dayTime.equals(DateUtils.getCurrentYMD())) {
                dayTime = mContext.getString(R.string.today);
            }
            tvDayTime.setText(dayTime+"");

        }
        int position = helper.getPosition();
        View view = helper.getView(R.id.view_bottom);

        view.setVisibility((position == size -1 || last) ? View.INVISIBLE : View.VISIBLE);
//        helper.getView(R.id.view_bottom_1).setVisibility((position == size -1 || last) ? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.driver).setVisibility(last ? View.VISIBLE : View.GONE);

        ImageView ivContent = helper.getView(R.id.iv_content);
        ImageView iv = helper.getView(R.id.iv);
        ImageView ivPlay = helper.getView(R.id.iv_paly);

        RelativeLayout rlPic = helper.getView(R.id.rl_pic);

/*        if(record.getThumbUrl() != null ){
            if(record.isThumbState()){
                ivContent.setVisibility(View.VISIBLE);
                view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(1), SizeUtils.dp2px(70)));
            }else{
                if(record.getFileDate() == null){
                    ivContent.setVisibility(View.GONE);
                    view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(1),SizeUtils.dp2px(27)));
                }
            }
        }*/

        if(record.getThumbUrl() == null && !record.isThumbState()){
//            if(record.getFileName() == null){
            rlPic.setVisibility(View.GONE);
            view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(1),SizeUtils.dp2px(27)));
//            }
        }else{
            rlPic.setVisibility(View.VISIBLE);
            view.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dp2px(1), SizeUtils.dp2px(70)));
        }

        switch (record.getType()){
            case 0x10://您的智能门锁低电量，请及时更换
                tvRight.setText(mContext.getText(R.string.wifi_lock_alarm_low_power));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_battery);
                rlPic.setVisibility(View.GONE);
                break;
            case 0x20:// 您的门锁有故障，请注意
                tvRight.setText(mContext.getText(R.string.wifi_lock_alarm_problem));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_fault);
                break;
            case 0x03:// 门锁错误验证多次，门锁系统锁定90秒
                tvRight.setText(mContext.getText(R.string.wifi_lock_alarm_lock_5min_1));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_error);
                break;
            case 0x08:// 门锁正在被机械钥匙开启，请回家或联系保安查看
                tvRight.setText(mContext.getText(R.string.wifi_lock_alarm_opens));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_prylock);
                break;
            case 0x04:// 已监测到您的门锁被撬，请联系家人或小区保安
                tvRight.setText(mContext.getText(R.string.wifi_lock_alarm_lock_broken));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_prylock);
                break;
            case 0x70:// 徘徊报警
                tvRight.setText(mContext.getText(R.string.wandering_alarm));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_alert);
                break;
            case 0x02:// 有人使用劫持密码开启门锁，赶紧联系或报警
                tvRight.setText(mContext.getText(R.string.wifi_lock_alarm_hijack));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_warning);
                break;
            case 0x01:// 锁定报警
                tvRight.setText("锁定报警");
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_locking);
                break;
            case 0x40:// 布防报警
                tvRight.setText(mContext.getText(R.string.warring_defence));
                iv.setImageResource(R.mipmap.video_lock_alarm_icon_protection);
                break;
        }

        if(record.getThumbUrl()!=null && !record.getThumbUrl().isEmpty()){
            Glide.with(mContext).load(record.getThumbUrl())
                    .apply(new RequestOptions().error(R.mipmap.img_video_lock_default).placeholder(R.mipmap.img_video_lock_default)
                            .transform(new RotateTransformation(270.0f))).into(ivContent);
        }

        if(record.isThumbState() && record.getFileName() != null){
            ivPlay.setVisibility(View.VISIBLE);
        }else{
            ivPlay.setVisibility(View.GONE);
        }

        ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(record.getFileDate() != null && !record.getFileDate().isEmpty()){
                    if(mListener != null){
                        mListener.onVideoRecordCallBackLinstener(record);
                    }
                    /*LogUtils.e("shulan WifiVideoLockAlarmIAdapter-->fileDate-->" + record.getFileDate());
                    Intent intent = new Intent(mContext, WifiLockVideoDeviceRecordActivity.class);
                    intent.putExtra(KeyConstants.WIFI_SN,record.getWifiSN());
                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD_DATA,record);
                    mContext.startActivity(intent);*/
                }
            }
        });


    }
}
