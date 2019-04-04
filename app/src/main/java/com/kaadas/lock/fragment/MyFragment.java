package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.mqtt.GatewayBindListFuc;
import com.kaadas.lock.publiclibrary.mqtt.MqttService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MyFragment extends Fragment  {
    @BindView(R.id.user_manager)
    Button btnUserManager;
    Unbinder unbinder;
    @BindView(R.id.publish)
    Button publish;
    @BindView(R.id.disconnect)
    Button disconnect;
    @BindView(R.id.connect)
    Button connect;

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_mytest, container, false);
        }
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
      /*  JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func", "gatewayBindList");
            jsonObject.put("uid", MyApplication.getInstance().getUid());
            MqttService service = MyApplication.getInstance().getMqttService();
            service.mqttPublish(service.getMqttAndroidClient(), "/request/app/func", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

          GatewayBindListFuc gatewayBindListFuc=new GatewayBindListFuc();
        gatewayBindListFuc.setFunc("gatewayBindList");
        gatewayBindListFuc.setUid(MyApplication.getInstance().getUid());
         MqttService service=MyApplication.getInstance().getMqttService();
        service.mqttPublish(service.getMqttAndroidClient(),"/request/app/func",gatewayBindListFuc);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) mView.getParent()).removeView(mView);
        unbinder.unbind();
    }

    @OnClick({R.id.publish, R.id.disconnect,R.id.connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish:
                initData();
                break;
            case R.id.disconnect:
                MyApplication.getInstance().getMqttService().mqttDisconnect(MyApplication.getInstance().getMqttService().getMqttAndroidClient());
                break;
            case R.id.connect:
                MyApplication.getInstance().getMqttService().mqttConnection(MyApplication.getInstance().getMqttService().getMqttAndroidClient(),MyApplication.getInstance().getUid(),MyApplication.getInstance().getToken());
                break;
        }
    }


}
