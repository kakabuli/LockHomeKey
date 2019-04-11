package com.kaadas.lock.activity.addDevice.zigbee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.adapter.AddZigbeeBindGatewayAdapter;
import com.kaadas.lock.bean.deviceAdd.AddBluetoothPairSuccessBean;
import com.kaadas.lock.bean.deviceAdd.AddZigbeeBindGatewayBean;
import com.kaadas.lock.bean.deviceAdd.AddZigbeeDetailItemBean;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZigbeeBindGatewayDetailActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.add_gateway)
    ImageView addGateway;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.button_next)
    Button buttonNext;

    private List<AddZigbeeBindGatewayBean> mList;
    private AddZigbeeBindGatewayAdapter addZigbeeBindGatewayAdapter;

    private AddZigbeeBindGatewayBean zigbeeBindGatewayBeanSelect;

    private int type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zigbee_bindgateway);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        type=intent.getIntExtra("type",0);
        initData();
        initView();
    }


    private void initData() {
        mList=new ArrayList<>();
        AddZigbeeBindGatewayBean one=new AddZigbeeBindGatewayBean();
        one.setNickName("凯迪仕智能网关");
        one.setGatewayId("GW1243434387");
        one.setAdminId("45478646464");
        one.setIsOnLine(0);
        one.setSelect(false);

        AddZigbeeBindGatewayBean two=new AddZigbeeBindGatewayBean();
        two.setNickName("凯迪仕网关");
        two.setGatewayId("424378643");
        two.setAdminId("443473212");
        two.setIsOnLine(1);
        two.setSelect(false);

        mList.add(one);
        mList.add(two);
    }

    private void initView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        if (mList!=null){
            addZigbeeBindGatewayAdapter=new AddZigbeeBindGatewayAdapter(mList);
            recycler.setAdapter(addZigbeeBindGatewayAdapter);
            addZigbeeBindGatewayAdapter.setOnItemClickListener(this);
        }
    }



    @OnClick({R.id.back, R.id.add_gateway, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_gateway:
                //跳转到添加网关
                Intent addGateway=new Intent(this, AddGatewayFirstActivity.class);
                startActivity(addGateway);
                break;
            case R.id.button_next:
                if (zigbeeBindGatewayBeanSelect==null){
                    ToastUtil.getInstance().showShort(getString(R.string.select_bindgateway));
                }else{
                    if (zigbeeBindGatewayBeanSelect.getIsOnLine()==0){
                        ToastUtil.getInstance().showShort(getString(R.string.gateway_offline));
                    }else{
                        if (type==2){
                            //跳转猫眼流程
                            Intent catEyeIntent=new Intent(this, AddDeviceCatEyeFirstActivity.class);
                            startActivity(catEyeIntent);
                        }else if (type==3){
                            //跳转zigbee锁流程
                            Intent intent=new Intent(this,AddZigbeeLockFirstActivity.class);
                            startActivity(intent);
                        }
                    }
                }


                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelect(false);
        }
        zigbeeBindGatewayBeanSelect=mList.get(position);
        //离线
        if (zigbeeBindGatewayBeanSelect.getIsOnLine()==0){
            ToastUtil.getInstance().showShort(getString(R.string.gateway_offline));
            return;
        }
        mList.get(position).setSelect(true);
        addZigbeeBindGatewayAdapter.notifyDataSetChanged();
    }
}
