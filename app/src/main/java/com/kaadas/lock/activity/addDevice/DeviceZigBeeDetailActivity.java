package com.kaadas.lock.activity.addDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.adapter.ZigbeeDetailAdapter;
import com.kaadas.lock.bean.deviceAdd.AddZigbeeDetailItemBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceZigBeeDetailPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceZigBeeDetailActivity extends BaseActivity<DeviceZigBeeDetailView, DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView>> implements BaseQuickAdapter.OnItemClickListener,DeviceZigBeeDetailView {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private List<AddZigbeeDetailItemBean> mList;
    private ZigbeeDetailAdapter zigbeeDetailAdapter;

    private boolean flag=false; //判断是否有绑定的网列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_zigbee_detail);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView> createPresent() {
        return new DeviceZigBeeDetailPresenter<>();
    }

    private void initData() {

        mList = new ArrayList<>();
        AddZigbeeDetailItemBean gateway = new AddZigbeeDetailItemBean();
        gateway.setImageId(R.mipmap.gateway_icon);
        gateway.setText(getString(R.string.zigbee_gateway));
        gateway.setType(1);
        mList.add(gateway);

        AddZigbeeDetailItemBean catEye = new AddZigbeeDetailItemBean();
        catEye.setImageId(R.mipmap.cat_eye_icon);
        catEye.setText(getString(R.string.zigbee_cat_eye));
        catEye.setType(2);
        mList.add(catEye);

        AddZigbeeDetailItemBean lock = new AddZigbeeDetailItemBean();
        lock.setImageId(R.mipmap.zigbee_lock);
        lock.setText(getString(R.string.kaadas_lock));
        lock.setType(3);
        mList.add(lock);
    }

    private void initView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        if (mList != null) {
            zigbeeDetailAdapter = new ZigbeeDetailAdapter(mList);
            zigbeeDetailAdapter.setOnItemClickListener(this);
            recycler.setAdapter(zigbeeDetailAdapter);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter.mqttService.getMqttClient()!=null&&mPresenter.mqttService.getMqttClient().isConnected()){
            mPresenter.getGatewayBindList();
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (!NetUtil.isNetworkAvailable()){
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
            return;
        }
        if (mPresenter.mqttService.getMqttClient()==null){
           ToastUtil.getInstance().showShort(getString(R.string.mqtt_connection_fail));
           return;
        }
        if (!mPresenter.mqttService.getMqttClient().isConnected()){
            ToastUtil.getInstance().showShort(getString(R.string.mqtt_connection_fail));
            return;
        }


        AddZigbeeDetailItemBean detailItemBean = mList.get(position);
        //如果是网关直接跳转到网关添加流程，如果不是需要判断是否存在网关。
        if (detailItemBean.getType() == 1) {
            //直接跳转到网关添加流程
            Intent gatewayIntent = new Intent(this, AddGatewayFirstActivity.class);
            startActivity(gatewayIntent);
        } else {
            //获取绑定的网关列表，如果存在网关

            if (flag) {
                //跳转到网关列表
                Intent zigbeeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
                int type = detailItemBean.getType();
                zigbeeIntent.putExtra("type", type);
                startActivity(zigbeeIntent);
            } else {
                AlertDialogUtil.getInstance().havaNoEditTwoButtonDialog(this, getString(R.string.no_usable_gateway), getString(R.string.add_zigbee_device_first_pair_gateway), getString(R.string.cancel), getString(R.string.configuration), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //跳转到配置网关添加的流程
                        Intent gatewayIntent = new Intent(DeviceZigBeeDetailActivity.this, AddGatewayFirstActivity.class);
                        startActivity(gatewayIntent);
                    }
                });
            }
        }
    }



    @Override
    public void getGatewayBindList(List<ServerGatewayInfo> bindGatewayList) {
        if (bindGatewayList.size()>0){
            flag=true;
        }
    }

    @Override
    public void getGatewayBindFail() {
        LogUtils.e("获取绑定的网关列表失败");
    }

    @Override
    public void bindGatewayPublishFail(String fuc) {
        LogUtils.e("获取绑定的网关列表发布失败");
    }

    @Override
    public void getGatewayThrowable(Throwable throwable) {
        LogUtils.e("获取绑定的网关列表异常");
    }
}
