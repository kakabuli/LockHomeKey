package com.kaadas.lock.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.RotateTransformation;

import java.security.MessageDigest;
import java.util.List;


import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class WifiLockVistorIAdapter extends BaseQuickAdapter<WifiVideoLockAlarmRecord, BaseViewHolder> {
    List<WifiVideoLockAlarmRecord> data;

    private VideoRecordCallBackLinstener mListener;
    public interface VideoRecordCallBackLinstener{
        void onVideoRecordCallBackLinstener(WifiVideoLockAlarmRecord record);
    }

    public void setVideoRecordCallBackLinstener(VideoRecordCallBackLinstener listener){
        this.mListener = listener;
    }

    public WifiLockVistorIAdapter(@Nullable List<WifiVideoLockAlarmRecord> data, VideoRecordCallBackLinstener listener) {
        super(R.layout.item_wifi_lock_vistor_record,data);
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
        helper.getView(R.id.view_bottom).setVisibility((position == size -1 || last) ? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.driver).setVisibility(last ? View.VISIBLE : View.GONE);

        ImageView ivContent = helper.getView(R.id.iv_content);
        ImageView ivPlay = helper.getView(R.id.iv_paly);

        if(record.getThumbUrl()!=null && !record.getThumbUrl().isEmpty()){
            Glide.with(mContext).load(record.getThumbUrl())
                    .apply(new RequestOptions().error(R.mipmap.img_video_lock_default).placeholder(R.mipmap.img_video_lock_default).dontAnimate()
                            .transform(new RotateTransformation(90f))).into(ivContent);
        }else{
            Glide.with(mContext).load(R.mipmap.img_video_lock_default).into(ivContent);
        }

        if(record.isThumbState() && record.getFileName() != null){
            ivPlay.setVisibility(View.VISIBLE);
        }else{
            ivPlay.setVisibility(View.GONE);
        }


        String content = BleUtil.getVideoAlarmByType(record.getType(), mContext);
        TextView tvRight = helper.getView(R.id.tv_right);
        tvRight.setText(content + "");

        ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(record.getFileDate() != null && !record.getFileDate().isEmpty()){

                    if(mListener != null){
                        mListener.onVideoRecordCallBackLinstener(record);
                    }
                    /*Intent intent = new Intent(mContext, WifiLockVideoDeviceRecordActivity.class);
                    intent.putExtra(KeyConstants.WIFI_SN,record.getWifiSN());
                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD_DATA,record);
                    mContext.startActivity(intent);*/
                }
            }
        });
    }


}
