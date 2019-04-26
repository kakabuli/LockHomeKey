package com.kaadas.lock.activity.device;

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
import com.kaadas.lock.adapter.FingerprintManagerAdapter;
import com.kaadas.lock.adapter.GatewayAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;

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
    private List<DeviceDetailBean> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.my_device);
        initRecyclerview();
//        initData();
    }

    private void initData() {
        list = new ArrayList<>();
        DeviceDetailBean deviceDetailBean1 = new DeviceDetailBean();
        deviceDetailBean1.setDeviceName("凯迪仕智能门锁");
        deviceDetailBean1.setPower(60);
        deviceDetailBean1.setType(1);
        list.add(deviceDetailBean1);

        DeviceDetailBean deviceDetailBean2 = new DeviceDetailBean();
        deviceDetailBean2.setDeviceName("K9智能门锁");
        deviceDetailBean2.setPower(20);
        deviceDetailBean2.setType(2);
        list.add(deviceDetailBean2);
        gatewayAdapter.notifyDataSetChanged();
    }

    private void initRecyclerview() {
        list = new ArrayList<>();
        DeviceDetailBean deviceDetailBean1 = new DeviceDetailBean();
        deviceDetailBean1.setDeviceName("凯迪仕智能门锁");
        deviceDetailBean1.setPower(60);
        deviceDetailBean1.setType(1);
        list.add(deviceDetailBean1);

        DeviceDetailBean deviceDetailBean2 = new DeviceDetailBean();
        deviceDetailBean2.setDeviceName("K9智能门锁");
        deviceDetailBean2.setPower(20);
        deviceDetailBean2.setType(2);
        list.add(deviceDetailBean2);
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
