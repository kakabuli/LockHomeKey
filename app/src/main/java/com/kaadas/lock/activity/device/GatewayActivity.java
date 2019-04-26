package com.kaadas.lock.activity.device;

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
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.GatewayAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.utils.KeyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/25
 */
public class GatewayActivity extends AppCompatActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
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
    @BindView(R.id.tv_gateway_type)
    TextView tvGatewayType;
    private List<GatewayDeviceDetailBean> list = new ArrayList<>();
    boolean gatewayOnline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.my_device);
        tvGatewayType.setText(getString(R.string.bluetooth_type)+"jfifj");
        initRecyclerview();
        initData();
        changeGatewayStatus();
    }

    private void changeGatewayStatus() {
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
       list.add(new GatewayDeviceDetailBean("fjfjfk",60,true,KeyConstants.CATEYE));
       list.add(new GatewayDeviceDetailBean("fjfjfk",100,false,KeyConstants.GATEWAY_LOCK));
        gatewayAdapter.notifyDataSetChanged();
    }

    private void initRecyclerview() {
/*        list = new ArrayList<>();
        DeviceDetailBean deviceDetailBean1 = new DeviceDetailBean();
        deviceDetailBean1.setDeviceName("凯迪仕智能门锁");
        deviceDetailBean1.setDeviceType(1);
        deviceDetailBean1.setPower(60);
        deviceDetailBean1.setType(1);
        list.add(deviceDetailBean1);

        DeviceDetailBean deviceDetailBean2 = new DeviceDetailBean();
        deviceDetailBean2.setDeviceName("K9智能门锁");
        deviceDetailBean2.setDeviceType(1);
        deviceDetailBean2.setPower(20);
        deviceDetailBean2.setType(2);
        list.add(deviceDetailBean2);*/
        gatewayAdapter = new GatewayAdapter(list);
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
