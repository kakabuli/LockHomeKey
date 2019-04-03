package com.kaadas.lock.activity.addDevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.DeviceAddItemAdapter;
import com.kaadas.lock.bean.deviceAdd.DeviceAddItemBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceAddActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.device_add)
    ImageView deviceAdd;
    @BindView(R.id.zigbee_image)
    ImageView zigbeeImage;
    @BindView(R.id.zigbee_text)
    TextView zigbeeText;
    @BindView(R.id.bluetooth_image)
    ImageView bluetoothImage;
    @BindView(R.id.bluetooth_text)
    TextView bluetoothText;
    @BindView(R.id.device_add_recycler)
    RecyclerView deviceAddRecycler;

    private DeviceAddItemAdapter deviceAddItemAdapter;

    private List<DeviceAddItemBean> deviceList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add);
        ButterKnife.bind(this);
        initData();
        initView();



    }


    private void initData() {
        deviceList=new ArrayList<>();
        DeviceAddItemBean deviceAddItemBean1=new DeviceAddItemBean();
        deviceAddItemBean1.setImageId(R.mipmap.k7);
        deviceAddItemBean1.setTypeText(getString(R.string.k7));
        deviceAddItemBean1.setType(0);
        deviceList.add(deviceAddItemBean1);

        DeviceAddItemBean deviceAddItemBean2=new DeviceAddItemBean();
        deviceAddItemBean2.setImageId(R.mipmap.k8);
        deviceAddItemBean2.setTypeText(getString(R.string.k8));
        deviceAddItemBean2.setType(1);
        deviceList.add(deviceAddItemBean2);


        DeviceAddItemBean deviceAddItemBean3=new DeviceAddItemBean();
        deviceAddItemBean3.setImageId(R.mipmap.k9);
        deviceAddItemBean3.setTypeText(getString(R.string.k9));
        deviceAddItemBean3.setType(2);
        deviceList.add(deviceAddItemBean3);

        DeviceAddItemBean deviceAddItemBean4=new DeviceAddItemBean();
        deviceAddItemBean4.setImageId(R.mipmap.s8);
        deviceAddItemBean4.setTypeText(getString(R.string.s8));
        deviceAddItemBean4.setType(3);
        deviceList.add(deviceAddItemBean4);

        DeviceAddItemBean deviceAddItemBean5=new DeviceAddItemBean();
        deviceAddItemBean5.setImageId(R.mipmap.kx);
        deviceAddItemBean5.setTypeText(getString(R.string.kx));
        deviceAddItemBean5.setType(4);
        deviceList.add(deviceAddItemBean5);

        DeviceAddItemBean deviceAddItemBean6=new DeviceAddItemBean();
        deviceAddItemBean6.setImageId(R.mipmap.other);
        deviceAddItemBean6.setTypeText(getString(R.string.other));
        deviceAddItemBean6.setType(5);
        deviceList.add(deviceAddItemBean6);
    }
    private void initView() {
        deviceAddRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        if (deviceList!=null&&deviceList.size()>0){
            deviceAddItemAdapter=new DeviceAddItemAdapter(deviceList);
            deviceAddRecycler.setAdapter(deviceAddItemAdapter);
        }
    }



    @OnClick({R.id.back, R.id.device_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.device_add:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
