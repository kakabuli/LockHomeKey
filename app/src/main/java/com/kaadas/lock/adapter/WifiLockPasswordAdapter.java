package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David on
 */


public class WifiLockPasswordAdapter extends BaseQuickAdapter<ForeverPassword, BaseViewHolder> {

    private final String[] weekdays;

    public WifiLockPasswordAdapter(@Nullable List<ForeverPassword> data, int layoutId) {
        super(layoutId, data);
        mContext = MyApplication.getInstance().getApplicationContext();
        weekdays = new String[]{
                mContext.getString(R.string.week_day),
                mContext.getString(R.string.monday),
                mContext.getString(R.string.tuesday),
                mContext.getString(R.string.wedensday),
                mContext.getString(R.string.thursday),
                mContext.getString(R.string.friday),
                mContext.getString(R.string.saturday)};
    }

    @Override
    protected void convert(BaseViewHolder helper, ForeverPassword bean) {
        List<ForeverPassword> data = getData();
        int itemCount = data.size();
        int pos = helper.getPosition();
        LogUtils.d(" itemCount " + itemCount + "----------pos " + pos);
        if (pos == itemCount - 1) {
            View view = helper.getView(R.id.my_view);
            view.setVisibility(View.GONE);
        } else {
            View view = helper.getView(R.id.my_view);
            view.setVisibility(View.VISIBLE);
        }

        if (bean.getItems() != null) {
            LogUtils.e("周计划是     " + Arrays.toString(bean.getItems().toArray()));
        }

        helper.setText(R.id.tv_num, bean.getNum());
        helper.setText(R.id.tv_nick, bean.getNickName());
        //0永久秘钥 1策略秘钥 2胁迫秘钥 3管理员秘钥 4无权限秘钥 254 一次性秘钥 255 无效值
        switch (bean.getType()) {
            case 0:
                helper.setText(R.id.tv_time, mContext.getString(R.string.foever_able));
                break;
            case 1:
                helper.setText(R.id.tv_time, "策略密码");
                break;
            case 2:
                helper.setText(R.id.tv_time, "胁迫密码");

                break;
            case 3:
                helper.setText(R.id.tv_time, "管理员密码");

                break;
            case 4:
                helper.setText(R.id.tv_time, "无权限密码");

                break;
            case 254:
                helper.setText(R.id.tv_time, "临时密码");

                break;
            case 255:
                helper.setText(R.id.tv_time, "无效值密码");

                break;
        }
        if (bean.getType() == 1) {

        } else if (bean.getType() == 2) {
            helper.setText(R.id.tv_time, DateUtils.getDateTimeFromMillisecond(bean.getStartTime()) + "-" + DateUtils.getDateTimeFromMillisecond(bean.getEndTime()));
        } else if (bean.getType() == 3) {  //周期密码
            String weeks = "";
            boolean isFirst = false;
            for (int i = 0; i < bean.getItems().size(); i++) {
                if ("1".equals(bean.getItems().get(i))) {
                    if (isFirst) {
                        weeks += "、" + weekdays[i];
                    } else {
                        weeks += weekdays[i];
                        isFirst = true;
                    }

                }
            }
            String startTime = getStartTime(bean);
            String endTime = getEndTime(bean);
            weeks = mContext.getString(R.string.pwd_will) + weeks + startTime + "-" + endTime + mContext.getString(R.string.force);
            helper.setText(R.id.tv_time, weeks);
        } else if (bean.getType() == 4) {
            helper.setText(R.id.tv_time, DateUtils.getDateTimeFromMillisecond(bean.getStartTime()) + "-" + DateUtils.getDateTimeFromMillisecond(bean.getEndTime()));
        } else if (bean.getType() == 5) {
            helper.setText(R.id.tv_time, MyApplication.getInstance().getString(R.string.temporary_password_used_once));
        }
        if ("09".equals(bean.getNum())) {
            helper.setText(R.id.tv_time, MyApplication.getInstance().getString(R.string.stress_password));
        }
    }

    private String getEndTime(ForeverPassword bean) {
        int endHour = (int) bean.getEndTime() / 60 / 60 / 1000;
        int endMinute = (int) bean.getEndTime() % (60 * 60 * 1000) / 60 / 1000;
        String endTime = (endHour > 9 ? "" + endHour : "0" + endHour) + ":" + (endMinute > 9 ? "" + endMinute : "0" + endMinute);
        return endTime;
    }

    private String getStartTime(ForeverPassword bean) {
        int startHour = (int) bean.getStartTime() / 60 / 60 / 1000;
        int startMinute = (int) bean.getStartTime() % (60 * 60 * 1000) / 60 / 1000;
        String startTime = (startHour > 9 ? "" + startHour : "0" + startHour) + ":" + (startMinute > 9 ? "" + startMinute : "0" + startMinute);
        return startTime;
    }
}
