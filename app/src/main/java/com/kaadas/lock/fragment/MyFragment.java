package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.GatewayBindListFuc;
import com.kaadas.lock.publiclibrary.mqtt.MqttService;
import com.kaadas.lock.publiclibrary.mqtt.PublishResult;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class MyFragment extends Fragment {
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
    private Disposable disposable;

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
    }

    public static int MessageId = 0;

    private void initData() {
      /*  JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func", "gatewayBindList");
            jsonObject.put("uid", MyApplication.getInstance().getUid());
            MqttService service = MyApplication.getInstance().getMqttService();
            service.mqttPublish(service.getMqttClient(), "/request/app/func", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        GatewayBindListFuc gatewayBindListFuc = new GatewayBindListFuc();
        gatewayBindListFuc.setFunc("gatewayBindList");
        gatewayBindListFuc.setUid(MyApplication.getInstance().getUid());
        MqttService service = MyApplication.getInstance().getMqttService();
        MqttMessage message = getMessage(gatewayBindListFuc);

        disposable = service.mqttPublish("/request/app/func", message)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<PublishResult>() {
                    @Override
                    public boolean test(PublishResult publishResult) throws Exception {
                        if (publishResult.isPublishResult()) { //发布成功
                            LogUtils.e("发布成功");
                        } else {  //发布失败
                            LogUtils.e("发布失败");
                            return false;
                        }
                        return publishResult.getAsyncActionToken().getMessageId() == message.getId();
                    }
                })
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .flatMap(new Function<PublishResult, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(PublishResult publishResult) throws Exception {
                        if (publishResult.isPublishResult()) { //发布成功
                            return MyApplication.getInstance().getMqttService().listenerDataBack();
                        } else {
                            return null;
                        }
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtils.e("收到消息   " + s);
                        if (disposable !=null&&!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("发布消息失败     " + throwable.getMessage());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) mView.getParent()).removeView(mView);
        unbinder.unbind();
    }

    @OnClick({R.id.publish, R.id.disconnect, R.id.connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish:
                initData();
                break;
            case R.id.disconnect:
                MyApplication.getInstance().getMqttService().mqttDisconnect();
                break;

        }
    }

    public MqttMessage getMessage(Object o) {
        String payload = new Gson().toJson(o);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(2);
        mqttMessage.setRetained(false);
        mqttMessage.setId(MessageId++);;
        mqttMessage.setPayload(payload.getBytes());
        return mqttMessage;
    }


}
