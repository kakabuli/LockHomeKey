package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.WiFiLockCardAndFingerShowBean;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;

import java.util.List;

/**
 * Created by David
 */


public class WifiLockCardAndFingerAdapter extends BaseQuickAdapter<WiFiLockCardAndFingerShowBean, BaseViewHolder> {


    public WifiLockCardAndFingerAdapter(@Nullable List<WiFiLockCardAndFingerShowBean> data, int layoutId) {
        super(layoutId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WiFiLockCardAndFingerShowBean bean) {
        List<WiFiLockCardAndFingerShowBean> data = getData();
        int itemCount = data.size();
        int pos = helper.getPosition();
        if (pos == itemCount - 1) {
            View view = helper.getView(R.id.my_view);
            view.setVisibility(View.GONE);
        } else {
            View view = helper.getView(R.id.my_view);
            view.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tv_num, bean.getNum());
        helper.setText(R.id.tv_nick, bean.getNickName());
    }


}
