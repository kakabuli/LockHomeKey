package com.kaadas.lock.activity.device.cateye.more;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.SafeModePresenter;
import com.kaadas.lock.mvp.presenter.cateye.SmartEyePresenter;
import com.kaadas.lock.mvp.view.cateye.ISmartEyeView;
import com.kaadas.lock.publiclibrary.mqtt.PowerResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartEyeActivity extends BaseActivity<ISmartEyeView, SmartEyePresenter<ISmartEyeView>> implements View.OnClickListener,ISmartEyeView {
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_smart_monitor)
    ImageView iv_smart_monitor;
    @BindView(R.id.rl_smart_linger)
    RelativeLayout rl_smart_linger;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_smart_monitor)
    RelativeLayout rlSmartMonitor;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.pir_smart_right_linger)
    ImageView pirSmartRightLinger;
    @BindView(R.id.pir_smart_right_default)
    ImageView pirSmartRightDefault;
    @BindView(R.id.rl_smart_default)
    RelativeLayout rlSmartDefault;
    @BindView(R.id.pir_wander)
    TextView pirWander;
    @BindView(R.id.sd_status)
    TextView sdStatus;

    private int sd=0;
    private String pir="";

    private ArrayList<String> options1Items = new ArrayList();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private OptionsPickerView pvOptions;

    private String pirText="";
    private String gatewayId;
    private String deviceId;

    private AlertDialog alertDialog;
    private AlertDialog pirEnableAlertDialog;
    private Context context;

    private int pirEnable=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_eye);
        ButterKnife.bind(this);
        context=this;
        initListener();
        initData();
        initOptionPicker();
    }

    @Override
    protected SmartEyePresenter<ISmartEyeView> createPresent() {
        return new SmartEyePresenter<>();
    }

    private void initData() {
        Intent intent = getIntent();
        String strCatEyeInfo = intent.getStringExtra(KeyConstants.GET_CAT_EYE_INFO);
        if (!TextUtils.isEmpty(strCatEyeInfo)) {
            CatEyeInfoBeanResult catEyeInfo = new Gson().fromJson(strCatEyeInfo, CatEyeInfoBeanResult.class);
            if (catEyeInfo!=null) {
                sd = catEyeInfo.getReturnData().getSdStatus();
                pir = catEyeInfo.getReturnData().getPirWander();
                gatewayId=catEyeInfo.getGwId();
                deviceId=catEyeInfo.getDeviceId();
                pirEnable=catEyeInfo.getReturnData().getPirEnable();
            }
        }
        if (sdStatus!=null&&pirWander!=null&&iv_smart_monitor!=null) {
            if (sd == 1) {
                //存在SD卡
                sdStatus.setText(getString(R.string.have));
                if (pirEnable==1){
                    iv_smart_monitor.setImageResource(R.mipmap.iv_open);
                }else{
                    iv_smart_monitor.setImageResource(R.mipmap.iv_close);
                }
            } else {
                //不存在SD卡
                sdStatus.setText(getString(R.string.no));
                //如果智能监测是打开的需要把智能检测关闭
                if (pirEnable==1){
                    mPresenter.setPirEnable(gatewayId,deviceId,MyApplication.getInstance().getUid(),0);
                }

            }
            if (!TextUtils.isEmpty(pir)){
                String str=pir.replace(",","/");
                pirWander.setText(str);
            }
        }
        setData();
    }

    private void initListener() {
        tv_content.setText(getString(R.string.smart_monitor));
        iv_smart_monitor.setOnClickListener(this);
        rl_smart_linger.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        rlSmartDefault.setOnClickListener(this);
    }

    private void setData() {
        //分子:由于小于等于分母的二分之一
        options1Items.add(1 + "");
        options1Items.add(2 + "");
        options1Items.add(3 + "");
        options1Items.add(4 + "");
        options1Items.add(5 + "");
        options1Items.add(6 + "");
        options1Items.add(7 + "");

        //选项2---1
        ArrayList<String> options2Items_01 = new ArrayList<>();
        for (int i = 3; i <= 15; i++) {
            options2Items_01.add(i + "");
        }
        //2
        ArrayList<String> options2Items_02 = new ArrayList<>();
        for (int i = 5; i <= 15; i++) {
            options2Items_02.add(i + "");
        }
        //3
        ArrayList<String> options2Items_03 = new ArrayList<>();
        for (int i = 7; i <= 15; i++) {
            options2Items_03.add(i + "");
        }

        //4
        ArrayList<String> options2Items_04 = new ArrayList<>();
        for (int i = 9; i <= 15; i++) {
            options2Items_04.add(i + "");
        }

        //5
        ArrayList<String> options2Items_05 = new ArrayList<>();
        for (int i = 11; i <= 15; i++) {
            options2Items_05.add(i + "");
        }

        //6
        ArrayList<String> options2Items_06 = new ArrayList<>();
        for (int i = 13; i <= 15; i++) {
            options2Items_06.add(i + "");
        }

        ArrayList<String> options2Items_07 = new ArrayList<>();
        options2Items_07.add(15 + "");


        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);
        options2Items.add(options2Items_03);
        options2Items.add(options2Items_04);
        options2Items.add(options2Items_05);
        options2Items.add(options2Items_06);
        options2Items.add(options2Items_07);
    }

    private void initOptionPicker() {//条件选择器初始化

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1) + ","
                        + options2Items.get(options1).get(options2);
                pirText = tx;
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)&&!TextUtils.isEmpty(pirText)){
                    LogUtils.e("进入了正在设置");
                    mPresenter.setPirWander(MyApplication.getInstance().getUid(),gatewayId,deviceId,pirText);
                    alertDialog=AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.take_effect_be_being));
                    alertDialog.setCancelable(false);
                }
            }

        })
                .setTitleText(getString(R.string.pir_linger))
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.parseColor("#999999"))
                .setSubmitColor(Color.parseColor("#999999"))
                .setCancelColor(Color.WHITE)
                .setSubmitText(getString(R.string.confirm))
                .setCancelText("")
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels(getString(R.string.next), getString(R.string.second_time), getString(R.string.second_time))
                .build();
        pvOptions.setPicker(options1Items, options2Items);//二级选择器

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_smart_monitor:
                if (sd!=1){
                    ToastUtil.getInstance().showShort(R.string.please_insert_sd_card);
                    return;
                }else{
                    if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                        if (pirEnable==1){
                            //打开了智能监测，需要关闭
                            mPresenter.setPirEnable(gatewayId,deviceId,MyApplication.getInstance().getUid(),0);
                            pirEnableAlertDialog=AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.close_pir_enable));
                            pirEnableAlertDialog.setCancelable(false);
                        }else{
                            mPresenter.setPirEnable(gatewayId,deviceId,MyApplication.getInstance().getUid(),1);
                            pirEnableAlertDialog=AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.open_pir_enable));
                            pirEnableAlertDialog.setCancelable(false);
                        }
                    }
                }

                break;
            case R.id.rl_smart_linger:
                //点击pir徘徊选择
                if (pvOptions != null) {
                    pvOptions.show();
                }
                break;
            case R.id.iv_back:
                finish();
                break;

            case R.id.rl_smart_default:
                //静默
                Intent intent = new Intent(SmartEyeActivity.this, CateDefaultActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                startActivity(intent);

                break;
        }


    }


    @Override
    public void setPirWanderSuccess() {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        if (pirWander!=null&&!TextUtils.isEmpty(pirText)){
            String pir=pirText.replace(",","/");
            pirWander.setText(pir);
        }

    }

    @Override
    public void setPirWanderFail() {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setPirWander(Throwable throwable) {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setPirEnableSuccess(int status) {
        if (pirEnableAlertDialog!=null){
            pirEnableAlertDialog.dismiss();
        }
        if (status==1){
            iv_smart_monitor.setImageResource(R.mipmap.iv_open);
        }else{
            iv_smart_monitor.setImageResource(R.mipmap.iv_close);
        }

    }

    @Override
    public void setPirEnableFail() {
        if (pirEnableAlertDialog!=null){
            pirEnableAlertDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.smart_check_set_fail);
    }

    @Override
    public void setPirEnableThrowable(Throwable throwable) {
        if (pirEnableAlertDialog!=null){
            pirEnableAlertDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.smart_check_set_fail);
    }
}
