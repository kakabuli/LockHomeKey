package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class WifiLockOperationItemRecordAdapter extends BaseQuickAdapter<WifiLockOperationRecord, BaseViewHolder> {
    public WifiLockOperationItemRecordAdapter(int layoutResId, @Nullable List<WifiLockOperationRecord> data) {
        super(layoutResId, data);
    }

    public WifiLockOperationItemRecordAdapter(@Nullable List<WifiLockOperationRecord> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiLockOperationRecord record) {
        int size = getData().size();
        int position = helper.getPosition();
        TextView tvTime = helper.getView(R.id.tv_time);
        long time = record.getTime();
        String s = DateUtils.long2HourMin(time);
        tvTime.setText(TextUtils.isEmpty(s) ? "" : s);
        helper.getView(R.id.view_top).setVisibility(position == 0? View.INVISIBLE : View.VISIBLE);
        helper.getView(R.id.view_bottom).setVisibility(position == size-1 ? View.INVISIBLE : View.VISIBLE);


        TextView tvContent = helper.getView(R.id.tv_content);
        String content = BleUtil.getAlarmByType(record.getType(),mContext);
        TextView tvRight = helper.getView(R.id.tv_right);
        int type = record.getType();
        //1开锁 2关锁 3添加密钥 4删除密钥 5修改管理员密码 6自动模式 7手动模式 8安全模式切换 9常用模式切换 10反锁模式 11布防模式
        switch (type){
            case 1: //开锁
                tvRight.setVisibility(View.VISIBLE);
                tvContent.setText(record.getUserNickname());
                int pwdType = record.getPwdType();
                switch (pwdType){

                }
//                tvRight.setText();
                break;
            case 2: //关锁
                tvRight.setVisibility(View.GONE);
                break;
            case 3: //添加秘钥
                break;
            case 4: //删除秘钥
                break;
            case 5: //修改管理员密码
                break;
            case 6: //自动模式
                break;
            case 7: //手动模式
                break;
            case 8: //安全模式切换
                break;
            case 9: //常用模式切换
                break;
            case 10: //反锁模式
                break;
            case 11: //布防模式
                break;
        }


        LogUtils.e("Adapter 显示的   " + content);
         // 机械开锁/APP开锁/自动开锁/密码开锁/门卡开锁/指纹开锁
        tvContent.setText(content);
    }

    private String getStringByPwdType(int pwdType){
        String str = "";
        switch (pwdType){
            //密码类型：1密码 2指纹 3卡片 4APP用户

        }

        return str;
    }




}
