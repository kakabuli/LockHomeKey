package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;

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


public class GatewayStressPasswordAdapter extends BaseQuickAdapter<ForeverPassword, BaseViewHolder> {


    public GatewayStressPasswordAdapter(@Nullable List<ForeverPassword> data, int layoutId) {
        super(layoutId, data);
        mContext = MyApplication.getInstance().getApplicationContext();

    }

    @Override
    protected void convert(BaseViewHolder helper, ForeverPassword bean) {

        helper.setText(R.id.tv_nick, bean.getNickName());
    }
}
