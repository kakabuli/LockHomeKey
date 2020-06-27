package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.singlefireswitchpresenter.SingleFireSwitchSettingPresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;
import com.kaadas.lock.publiclibrary.bean.SwitchNumberBean;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.postbean.ModifySwitchNickBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindingSingleFireSwitchBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateFormatUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SwipchSeetingArgus extends BaseActivity<SingleFireSwitchView, SingleFireSwitchSettingPresenter<SingleFireSwitchView>> implements View.OnClickListener,SingleFireSwitchView  {
    TextView tv_content,swipch_link_text_one,swipch_link_text_two,swipch_link_text_three,tv_start,swipch_link_setting_mac,swipch_link_setting_binding_time;
    ImageView iv_back;
    RelativeLayout swipch_setting_arugs_change,swipch_link_btn_one_rl,swipch_link_btn_two_rl,swipch_link_btn_three_rl;

    private int SwitchNumber;
    private WifiLockInfo wifiLockInfo;
    private WifiLockInfo wifiLockInfoChange;
    private BindingSingleFireSwitchBean bindingSingleFireSwitchBean;
    @SerializedName("switch")
    private SingleFireSwitchInfo params;

    private String wifiSn;
    private String switch1Nickname;
    private String switch2Nickname;
    private String switch3Nickname;
    private String switch1ChangeNickname;
    private String switch2ChangeNickname;
    private String switch3ChangeNickname;
    private ModifySwitchNickBean modifySwitchNickBean;
    private List< ModifySwitchNickBean.nickname >  switchNickname= new ArrayList<>();

    private static final int TO_SET_NICK_NAME_REQUEST_CODE = 10101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_seeting_argus);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.setting));
        iv_back = findViewById(R.id.iv_back);
        tv_start = findViewById(R.id.tv_start);
        swipch_link_setting_mac = findViewById(R.id.swipch_link_setting_mac);
        swipch_link_setting_binding_time = findViewById(R.id.swipch_link_setting_binding_time);
        swipch_setting_arugs_change = findViewById(R.id.swipch_setting_arugs_change);
        swipch_link_btn_one_rl = findViewById(R.id.swipch_link_btn_one_rl);
        swipch_link_btn_two_rl = findViewById(R.id.swipch_link_btn_two_rl);
        swipch_link_btn_three_rl = findViewById(R.id.swipch_link_btn_three_rl);
        swipch_link_text_one = findViewById(R.id.swipch_link_text_one);
        swipch_link_text_two = findViewById(R.id.swipch_link_text_two);
        swipch_link_text_three = findViewById(R.id.swipch_link_text_three);
        swipch_setting_arugs_change.setOnClickListener(this);
        swipch_link_btn_one_rl.setOnClickListener(this);
        swipch_link_btn_two_rl.setOnClickListener(this);
        swipch_link_btn_three_rl.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        initData();
        initRecycleview();

    }

    @Override
    protected SingleFireSwitchSettingPresenter<SingleFireSwitchView> createPresent() {
        return new SingleFireSwitchSettingPresenter<>();
    }

    private void initData() {
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfoChange = (WifiLockInfo) getIntent().getSerializableExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        //设备型号
//        tv_start.setText(wifiLockInfo.getProductModel());
        //键位开关
        List<SwitchNumberBean> switchNumberList = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber();
        for (SwitchNumberBean switchNumber : switchNumberList) {

            switch (switchNumber.getType()) {
                case 1://键位1开关
                    if (!TextUtils.isEmpty(switchNumber.getNickname())) {
                        switch1Nickname = switchNumber.getNickname();
                        switch1ChangeNickname = switch1Nickname;
                        swipch_link_text_one.setText(switchNumber.getNickname());
                    }
                    break;
                case 2://键位2开关
                    if (!TextUtils.isEmpty(switchNumber.getNickname())) {
                        switch2Nickname = switchNumber.getNickname();
                        switch2ChangeNickname = switch2Nickname;
                        swipch_link_text_two.setText(switchNumber.getNickname());
                    }
                    break;
                case 3://键位3开关
                    if (!TextUtils.isEmpty(switchNumber.getNickname())) {
                        switch3Nickname = switchNumber.getNickname();
                        switch3ChangeNickname = switch3Nickname;
                        swipch_link_text_three.setText(switchNumber.getNickname());
                    }
                    break;
            }
        }
        //MAC地址
        swipch_link_setting_mac.setText(wifiLockInfo.getSingleFireSwitchInfo().getMac());
        //绑定时间
        String bindingTime = DateUtils.timestampToDateSecond(wifiLockInfo.getSingleFireSwitchInfo().getSwitchBind());

        swipch_link_setting_binding_time.setText(bindingTime);
    }

    private void initRecycleview() {

        SwitchNumber = getIntent().getIntExtra(KeyConstants.SWITCH_NUMBER,1);

        switch (SwitchNumber){
            case 1:
                swipch_link_btn_one_rl.setVisibility(View.VISIBLE);
                swipch_link_btn_two_rl.setVisibility(View.GONE);
                swipch_link_btn_three_rl.setVisibility(View.GONE);
                break;
            case 2:
                swipch_link_btn_one_rl.setVisibility(View.VISIBLE);
                swipch_link_btn_two_rl.setVisibility(View.VISIBLE);
                swipch_link_btn_three_rl.setVisibility(View.GONE);
                break;
            case 3:
                swipch_link_btn_one_rl.setVisibility(View.VISIBLE);
                swipch_link_btn_two_rl.setVisibility(View.VISIBLE);
                swipch_link_btn_three_rl.setVisibility(View.VISIBLE);
                break;
        }




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.swipch_setting_arugs_change:
                //(Context context, String title,String content, String content, String left, String right
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_input_title_name)
                        ,getString(R.string.swipch_setting_pluease_input_context_name)
                        ,getString(R.string.cancel),
                          getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                                Intent intent = new Intent(SwipchSeetingArgus.this, SwipchLinkOne.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                startActivity(intent);
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {

                            }
                        });

                break;
            case R.id.swipch_link_btn_one_rl:
                AlertDialogUtil.getInstance().havaEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title1)
                        ,switch1ChangeNickname
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                            }
                            @Override
                            public void right() {
                                if (!switch1ChangeNickname.equals(switch1Nickname)){
                                    swipch_link_text_one.setText(switch1ChangeNickname);
                                    wifiLockInfoChange.getSingleFireSwitchInfo().getSwitchNumber().get(0).setNickname(switch1ChangeNickname);
                                    params = wifiLockInfoChange.getSingleFireSwitchInfo();
                                    bindingSingleFireSwitchBean = new BindingSingleFireSwitchBean(wifiSn,wifiLockInfoChange.getUid(),wifiLockInfoChange.getLockNickname(),params);

//                                    for (int i=1;i<=SwitchNumber;i++){
//                                        switchNickname.add(new ModifySwitchNickBean.nickname(i==1?switch1ChangeNickname:i==2?switch2ChangeNickname:switch3ChangeNickname,i));
//                                    }
                                    switchNickname.add(new ModifySwitchNickBean.nickname(switch1ChangeNickname,1));

                                    modifySwitchNickBean = new ModifySwitchNickBean(wifiSn, wifiLockInfo.getUid(), switchNickname);
                                    mPresenter.updateSwitchNickname(modifySwitchNickBean);
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {
                                switch1ChangeNickname = toString;
                            }
                        });
                break;
            case R.id.swipch_link_btn_two_rl:
                AlertDialogUtil.getInstance().havaEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title2)
                        ,switch2ChangeNickname
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                                if (!switch2ChangeNickname.equals(switch2Nickname)){
                                    swipch_link_text_two.setText(switch2ChangeNickname);
                                    wifiLockInfoChange.getSingleFireSwitchInfo().getSwitchNumber().get(1).setNickname(switch2ChangeNickname);
                                    params = wifiLockInfoChange.getSingleFireSwitchInfo();
                                    bindingSingleFireSwitchBean = new BindingSingleFireSwitchBean(wifiSn,wifiLockInfoChange.getUid(),wifiLockInfoChange.getLockNickname(),params);


//                                    for (int i=1;i<=SwitchNumber;i++){
//                                        switchNickname.add(new ModifySwitchNickBean.nickname(i==1?"0000":i==2?switch2ChangeNickname:switch3ChangeNickname,i));
//                                    }
                                    switchNickname.add(new ModifySwitchNickBean.nickname(switch2ChangeNickname,2));

                                    modifySwitchNickBean = new ModifySwitchNickBean(wifiSn, wifiLockInfo.getUid(), switchNickname);
                                    mPresenter.updateSwitchNickname(modifySwitchNickBean);
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {
                                switch2ChangeNickname = toString;
                            }

                        });
                break;

            case R.id.swipch_link_btn_three_rl:
                AlertDialogUtil.getInstance().havaEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title3)
                        ,switch3ChangeNickname
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                                if (!switch3ChangeNickname.equals(switch3Nickname)){
                                    swipch_link_text_three.setText(switch3ChangeNickname);
                                    wifiLockInfoChange.getSingleFireSwitchInfo().getSwitchNumber().get(2).setNickname(switch3ChangeNickname);
                                    params = wifiLockInfoChange.getSingleFireSwitchInfo();
                                    bindingSingleFireSwitchBean = new BindingSingleFireSwitchBean(wifiSn,wifiLockInfoChange.getUid(),wifiLockInfoChange.getLockNickname(),params);

                                    for (int i=1;i<=SwitchNumber;i++){
                                        switchNickname.add(new ModifySwitchNickBean.nickname(i==1?switch1ChangeNickname:i==2?switch2ChangeNickname:switch3ChangeNickname,i));
                                    }
                                    modifySwitchNickBean = new ModifySwitchNickBean(wifiSn, wifiLockInfo.getUid(), switchNickname);
                                    mPresenter.updateSwitchNickname(modifySwitchNickBean);
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {
                                switch3ChangeNickname = toString;

                            }

                        });
                break;
            case R.id.iv_back:

                if (!wifiLockInfoChange.equals(wifiLockInfo)) {
                    Intent intent = new Intent();
                    intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
                    setResult(TO_SET_NICK_NAME_REQUEST_CODE, intent);
                    finish();
                }
                break;

        }
    }

    @Override
    public void settingDeviceSuccess() {
        LogUtils.e("--kaadas--设置成功");
    }

    @Override
    public void settingDeviceFail() {
        refresh();
        Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void settingDeviceThrowable() {
        refresh();
        Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
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
        LogUtils.e("--kaadas--修改信息成功");
        MyApplication.getInstance().getAllDevicesByMqtt(true);
    }

    @Override
    public void bindingAndModifyDeviceFail() {
        refresh();

        Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void bindingAndModifyDeviceThrowable() {
        refresh();

        Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();

    }
    public void refresh() {

        finish();
        startActivity(getIntent());

    }
}
