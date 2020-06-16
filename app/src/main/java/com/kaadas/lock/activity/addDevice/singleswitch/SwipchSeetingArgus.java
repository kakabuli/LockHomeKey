package com.kaadas.lock.activity.addDevice.singleswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.SwitchNumberBean;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateFormatUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

import butterknife.BindView;

public class SwipchSeetingArgus extends AppCompatActivity implements View.OnClickListener {
    TextView tv_content,swipch_link_text_one,swipch_link_text_two,swipch_link_text_three,tv_start,swipch_link_setting_mac,swipch_link_setting_binding_time;
    ImageView iv_back;
    RelativeLayout swipch_setting_arugs_change,swipch_link_btn_one_rl,swipch_link_btn_two_rl,swipch_link_btn_three_rl;

    private int SwitchNumber;
    private WifiLockInfo wifiLockInfo;
    private String wifiSn;
    private String switch1Nickname;
    private String switch2Nickname;
    private String switch3Nickname;


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

    private void initData() {
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        //设备型号
//        tv_start.setText(wifiLockInfo.getProductModel());
        //键位开关
        List<SwitchNumberBean> switchNumberList = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber();
        for (SwitchNumberBean switchNumber : switchNumberList) {

            switch (switchNumber.getType()) {
                case 1://键位1开关
                    if (!switchNumber.getNickname().isEmpty()) {
                        switch1Nickname = switchNumber.getNickname();
                        swipch_link_text_one.setText(switchNumber.getNickname());
                    }
                    break;
                case 2://键位2开关
                    if (!switchNumber.getNickname().isEmpty()) {
                        switch2Nickname = switchNumber.getNickname();
                        swipch_link_text_two.setText(switchNumber.getNickname());
                    }
                    break;
                case 3://键位3开关
                    if (!switchNumber.getNickname().isEmpty()) {
                        switch3Nickname = switchNumber.getNickname();
                        swipch_link_text_three.setText(switchNumber.getNickname());
                    }
                    break;
            }
        }
        //MAC地址
        swipch_link_setting_mac.setText(wifiLockInfo.getSingleFireSwitchInfo().getMacaddr());
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

                            }
                        });

                break;
            case R.id.swipch_link_btn_one_rl:
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title1)
                        ,switch1Nickname
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });
                break;
            case R.id.swipch_link_btn_two_rl:
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title2)
                        ,switch2Nickname
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });
                break;

            case R.id.swipch_link_btn_three_rl:
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title3)
                        ,switch3Nickname
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });
                break;
            case R.id.iv_back:
                finish();
                break;

        }
    }
}
