package com.yun.software.kaadas.UI.activitys;

import android.view.View;

import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.base.BaseActivity;

import butterknife.OnClick;

public class ProductSeviceActivity extends BaseActivity {
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_product_service;
    }

    @Override
    protected void initViewsAndEvents() {

    }


    @OnClick(R2.id.tv_sure)
    public void OnViewClick(View view){
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
    }

}
