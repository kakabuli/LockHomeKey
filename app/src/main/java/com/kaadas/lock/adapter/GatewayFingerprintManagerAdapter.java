package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;

import java.util.List;

/**
 * Created by David
 */


public class GatewayFingerprintManagerAdapter extends BaseQuickAdapter<GetPasswordResult.DataBean.Fingerprint, BaseViewHolder> {


    public GatewayFingerprintManagerAdapter(@Nullable List<GetPasswordResult.DataBean.Fingerprint> data, int layoutId) {
        super(layoutId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetPasswordResult.DataBean.Fingerprint bean) {
        helper.setText(R.id.tv_num, bean.getNum());
        helper.setText(R.id.tv_nick, bean.getNickName());
    }


}
