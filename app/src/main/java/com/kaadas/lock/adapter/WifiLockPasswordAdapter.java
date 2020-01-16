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


    public WifiLockPasswordAdapter(@Nullable List<ForeverPassword> data, int layoutId) {
        super(layoutId, data);
        mContext = MyApplication.getInstance().getApplicationContext();

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
    }


}
