package com.yun.software.kaadas.UI.activitys;

import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.base.BaseActivity;

public class BindWeixinActivity extends BaseActivity {
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_bind_weixin;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("绑定微信");
    }
}
