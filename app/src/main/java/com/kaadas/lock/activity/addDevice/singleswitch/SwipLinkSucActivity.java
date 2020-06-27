package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.singlefireswitchpresenter.SingleFireSwitchSettingPresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;
import com.kaadas.lock.publiclibrary.bean.SwitchNumberBean;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.postbean.ModifySwitchNickBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindingSingleFireSwitchBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class SwipLinkSucActivity  extends BaseActivity<SingleFireSwitchView, SingleFireSwitchSettingPresenter<SingleFireSwitchView>> implements View.OnClickListener,SingleFireSwitchView{
    TextView tv_content,swipch_link_join_tv;
    Button btn_next;
    ImageView swipch_link_join_img,iv_back;
    LinearLayout swipch_link_ll_1,swipch_link_ll_2,swipch_link_ll_3;
    EditText switch_Number_NickName_1,switch_Number_NickName_2,switch_Number_NickName_3;

    // 两次点击按钮之间的最小点击间隔时间(单位:ms)
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    // 最后一次点击的时间
    private long lastClickTime;

    private int link;
    private WifiLockInfo wifiLockInfo;
    private String wifiSn;
    private AddSingleFireSwitchBean addSingleFireSwitchBean;
    private BindingSingleFireSwitchBean bindingSingleFireSwitchBean;
    private ModifySwitchNickBean modifySwitchNickBean;
    private List< ModifySwitchNickBean.nickname >  switchNickname= new ArrayList<>();
    @SerializedName("switch")
    private SingleFireSwitchInfo params;
    private SwitchNumberBean switchNumberBean;
    @SerializedName("switchArray")
    private List<SwitchNumberBean> switchNumber = new ArrayList<>();

    private String switchNumberNickName1;
    private String switchNumberNickName2;
    private String switchNumberNickName3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_suc);

        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.add_success));
        btn_next = findViewById(R.id.btn_next);
        iv_back = findViewById(R.id.iv_back);
        swipch_link_join_img = findViewById(R.id.swipch_link_join_img);
        swipch_link_join_tv = findViewById(R.id.swipch_link_join_tv);
        swipch_link_ll_1 = findViewById(R.id.swipch_link_ll_1);
        swipch_link_ll_2 = findViewById(R.id.swipch_link_ll_2);
        swipch_link_ll_3 = findViewById(R.id.swipch_link_ll_3);
        switch_Number_NickName_1 = findViewById(R.id.switch_Number_NickName_1);
        switch_Number_NickName_2 = findViewById(R.id.switch_Number_NickName_2);
        switch_Number_NickName_3 = findViewById(R.id.switch_Number_NickName_3);
        btn_next.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        initData();

    }

    @Override
    protected SingleFireSwitchSettingPresenter<SingleFireSwitchView> createPresent() {
        return new SingleFireSwitchSettingPresenter<>();
    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        addSingleFireSwitchBean = (AddSingleFireSwitchBean) getIntent().getSerializableExtra(KeyConstants.SWITCH_MODEL);

        link = addSingleFireSwitchBean.getType();
        chageLink(link);

        bindingSingleFireSwitchBean = new BindingSingleFireSwitchBean(wifiSn,wifiLockInfo.getUid(),wifiLockInfo.getLockNickname(),addSingleFireSwitchBean.getParams());

    }

    private void chageLink(int id){
        switch (id){
            case 1:
                swipch_link_join_tv.setText(getString(R.string.swipch_link_join_network_suc1));
                swipch_link_join_img.setBackgroundResource(R.mipmap.swipch_link_join_network_suc1);
                swipch_link_ll_2.setVisibility(View.GONE);
                swipch_link_ll_3.setVisibility(View.GONE);
                break;
            case 2:
                swipch_link_join_tv.setText(getString(R.string.swipch_link_join_network_suc2));
                swipch_link_join_img.setBackgroundResource(R.mipmap.swipch_link_join_network_suc2);
                swipch_link_ll_3.setVisibility(View.GONE);
                break;
            case 3:
                swipch_link_join_tv.setText(getString(R.string.swipch_link_join_network_suc3));
                swipch_link_join_img.setBackgroundResource(R.mipmap.swipch_link_join_network_suc3);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {// 两次点击的时间间隔大于最小限制时间，则触发点击事件
            lastClickTime = currentTime;
            // 这里触发点击事件
            switch (v.getId()) {
                case R.id.btn_next:
                    //构建绑定开关model
                    buildDeviceModel();

                    break;
                case R.id.iv_back:
                    Intent intent = new Intent(SwipLinkSucActivity.this, SwipchLinkOne.class);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    startActivity(intent);
                    break;
            }
        }
    }
    private void buildDeviceModel() {
        switch (link){
            case 1:
                //取值
                switchNumberNickName1 = switch_Number_NickName_1.getText().toString();
                break;
            case 2:
                //取值
                switchNumberNickName1 = switch_Number_NickName_1.getText().toString();
                switchNumberNickName2 = switch_Number_NickName_2.getText().toString();
                break;
            case 3:
                //取值
                switchNumberNickName1 = switch_Number_NickName_1.getText().toString();
                switchNumberNickName2 = switch_Number_NickName_2.getText().toString();
                switchNumberNickName3 = switch_Number_NickName_3.getText().toString();
                break;
        }
//        if (switchNumberNickName1 == null || switchNumberNickName1.length() == 0){
//            return;
////            switch_Number_NickName_1.setText(switch_Number_NickName_1.getHint());
//        }
//        if (switchNumberNickName2 == null || switchNumberNickName2.length() == 0){
////            switch_Number_NickName_2.setText(switch_Number_NickName_2.getHint());
//            return;
//
//        }
//        if (switchNumberNickName3 == null || switchNumberNickName3.length() == 0){
////            switch_Number_NickName_3.setText(switch_Number_NickName_3.getHint());
//            return;
//
//        }

        for (int i = 1 ; i <= link ; i++){
            LogUtils.e("--kaadas--SwitchNumberBean--==" + new SwitchNumberBean(i,0,0,0,0,i==1?switchNumberNickName1:i==2?switchNumberNickName2:switchNumberNickName3));
            switchNumber.add(new SwitchNumberBean(i,0,0,0,0,i==1?switchNumberNickName1:i==2?switchNumberNickName2:switchNumberNickName3));

            switchNickname.add(new ModifySwitchNickBean.nickname(i==1?switchNumberNickName1:i==2?switchNumberNickName2:switchNumberNickName3,i));

        }
        params = new SingleFireSwitchInfo(1,addSingleFireSwitchBean.getMac(),addSingleFireSwitchBean.getTimestamp(),switchNumber,switchNumber.size());
        bindingSingleFireSwitchBean = new BindingSingleFireSwitchBean(wifiSn,wifiLockInfo.getUid(),wifiLockInfo.getLockNickname(),params);

        modifySwitchNickBean = new ModifySwitchNickBean(wifiSn, wifiLockInfo.getUid(), switchNickname);
        //发送绑定
        bindingDevice();

    }

    private void bindingDevice() {
        mPresenter.updateSwitchNickname(modifySwitchNickBean);
    }
    @Override
    public void settingDeviceSuccess() {

    }

    @Override
    public void settingDeviceFail() {

    }

    @Override
    public void settingDeviceThrowable() {

    }

    @Override
    public void gettingDeviceSuccess() {

    }

    @Override
    public void gettingDeviceFail() {

    }

    @Override
    public void gettingDeviceThrowable() {

    }

    @Override
    public void addDeviceSuccess(AddSingleFireSwitchBean addSingleFireSwitchBean) {

    }

    @Override
    public void addDeviceFail() {

    }

    @Override
    public void addDeviceThrowable() {

    }

    @Override
    public void bindingAndModifyDeviceSuccess() {
        MyApplication.getInstance().getAllDevicesByMqtt(true);
//        Intent intent=new Intent(SwipLinkSucActivity.this, WiFiLockDetailActivity.class);
        Intent intent=new Intent(SwipLinkSucActivity.this, MainActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void bindingAndModifyDeviceFail() {
        Intent intent=new Intent(SwipLinkSucActivity.this,SwipLinkFailActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void bindingAndModifyDeviceThrowable() {
        Intent intent=new Intent(SwipLinkSucActivity.this,SwipLinkFailActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void showLoading(String content) {

    }

    @Override
    public void showLoadingNoCancel(String content) {

    }

    @Override
    public void hiddenLoading() {

    }
}
