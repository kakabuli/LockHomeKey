package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;

import org.linphone.mediastream.Log;

import java.util.Arrays;
import java.util.List;

public class GatewayLockPasswordAdapter extends BaseQuickAdapter<GatewayPasswordPlanBean, BaseViewHolder> {
    private String[] weekdays;

    public GatewayLockPasswordAdapter(@Nullable List<GatewayPasswordPlanBean> data) {
        super(R.layout.item_gateway_password, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GatewayPasswordPlanBean item) {

        int position = helper.getPosition();
        helper.setText(R.id.tv_num, item.getPasswordNumber() + "");

        int num = item.getPasswordNumber();
        if (num > 4 && num < 9) {  //临时密码
            helper.setText(R.id.tv_time, R.string.permanent_once_validity);
        } else if (num == 9) { //胁迫密码
            helper.setText(R.id.tv_time, R.string.stress_password);
        } else {
            int userType = item.getUserType(); //userType: 0 永久性密钥   userType: 1 策略密钥   userType: 3 管理员密钥   userType: 4 无权限密钥
            if (userType == 0) {
                helper.setText(R.id.tv_time, R.string.permanent_validity);
            } else {  //策略密钥

                String planType = item.getPlanType();
                if ("year".equals(planType)) {
                    long startTime = item.getZigBeeLocalStartTime();
                    long endTime = item.getZigBeeLocalEndTime();
                    LogUtils.e("  startTime  "+startTime+"  endTime  "+endTime);
                    long startSeconds = startTime + BleCommandFactory.defineTime;
                    String start = DateUtils.getDateTimeFromMillisecond(startSeconds * 1000);//要上传的开锁时间
                    long endSeconds = endTime + BleCommandFactory.defineTime;
                    String end = DateUtils.getDateTimeFromMillisecond(endSeconds * 1000);//要上传的开锁时间
                    String content = mContext.getString(R.string.password_valid_shi_xiao) + "  " + start + "~" + end;
                    LogUtils.e("显示的内容是   "  +content );
                    helper.setText(R.id.tv_time, content);
//                    if (endTime - startTime == 24 * 60 * 60) {
//                        helper.setText(R.id.tv_time, mContext.getString(R.string.password_one_day_valid));
//                    } else {
//                        helper.setText(R.id.tv_time, content);
//                    }
                 } else if ("week".equals(planType)) {
                    if (weekdays == null) {
                        weekdays = new String[]{mContext.getString(R.string.week_day),
                                mContext.getString(R.string.monday),
                                mContext.getString(R.string.tuesday),
                                mContext.getString(R.string.wedensday),
                                mContext.getString(R.string.thursday),
                                mContext.getString(R.string.friday),
                                mContext.getString(R.string.saturday)};
                    }
                    int mask = item.getDaysMask();
                    String days = BleUtil.getStringByMask(mask, weekdays);
                    String strHint = String.format(mContext.getString(R.string.week_hint), days,
                            item.getStartHour() + ":" + item.getStartMinute(), item.getEndHour() + ":" + item.getEndMinute());
                    LogUtils.e("重复的天数是   " +days);
                    helper.setText(R.id.tv_time, strHint);
                } else {
                    helper.setText(R.id.tv_time, R.string.permanent_validity);
                }
            }
        }
        if (getData() != null && getData().size() == position + 1) {
            helper.getView(R.id.my_view).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.my_view).setVisibility(View.VISIBLE);
        }


    }
}
