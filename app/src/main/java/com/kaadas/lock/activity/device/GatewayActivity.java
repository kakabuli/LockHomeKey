package com.kaadas.lock.activity.device;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.GatewayAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaypresenter.GatewayPresenter;
import com.kaadas.lock.mvp.view.gatewayView.GatewayView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.utils.KeyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/25
 */
public class GatewayActivity extends BaseActivity<GatewayView, GatewayPresenter<GatewayView>> implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    GatewayAdapter gatewayAdapter;
    @BindView(R.id.tv_gateway_status)
    TextView tvGatewayStatus;
    @BindView(R.id.gateway_nick_name)
    TextView gatewayNickName;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected GatewayPresenter<GatewayView> createPresent() {
        return new GatewayPresenter<>();
    }

    private void initView() {
        tvContent.setText(R.string.my_device);
        ivBack.setOnClickListener(this);
    }

    private void changeGatewayStatus(boolean gatewayOnline) {
        if (gatewayOnline) {
            tvGatewayStatus.setText(R.string.online);
            tvGatewayStatus.setTextColor(getResources().getColor(R.color.c1F96F7));
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.wifi_connect);
            tvGatewayStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        } else {
            tvGatewayStatus.setText(R.string.offline);
            tvGatewayStatus.setTextColor(getResources().getColor(R.color.c999999));
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.wifi_disconnect);
            tvGatewayStatus.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        DeviceDetailBean deviceDetailBean = (DeviceDetailBean) intent.getSerializableExtra(KeyConstants.DEVICE_DETAIL_BEAN);
        if (deviceDetailBean != null) {
            GatewayInfo gatewayInfo = (GatewayInfo) deviceDetailBean.getShowCurentBean();
            gatewayNickName.setText(gatewayInfo.getServerInfo().getDeviceNickName());
            if ("online".equals(gatewayInfo.getEvent_str())){
                changeGatewayStatus(true);
            }else{
                changeGatewayStatus(false);
            }
            List<HomeShowBean> homeShowBeans =mPresenter.getGatewayBindList(gatewayInfo.getServerInfo().getDeviceSN());
            initRecyclerview(homeShowBeans);
        }

    }

    private void initRecyclerview(List<HomeShowBean> homeShowBeans) {
        gatewayAdapter = new GatewayAdapter(homeShowBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gatewayAdapter);
        gatewayAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
