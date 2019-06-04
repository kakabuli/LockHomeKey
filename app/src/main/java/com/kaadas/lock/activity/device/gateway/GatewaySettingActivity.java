package com.kaadas.lock.activity.device.gateway;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.adapter.GatewaySettingAdapter;
import com.kaadas.lock.bean.GatewaySettingItemBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaypresenter.GatewaySettingPresenter;
import com.kaadas.lock.mvp.view.gatewayView.GatewaySettingView;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetNetBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetZbChannelBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GwWiFiBaseInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.EditTextWatcher;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.GatewayBaseInfo;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockBaseInfo;
import com.kaadas.lock.utils.greenDao.db.CatEyeServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.utils.greenDao.db.GatewayBaseInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockBaseInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayServiceInfoDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewaySettingActivity extends BaseActivity<GatewaySettingView, GatewaySettingPresenter<GatewaySettingView>> implements GatewaySettingView, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    private List<GatewaySettingItemBean> gatewaySettingItemBeans = new ArrayList<>();
    private GatewaySettingAdapter gatewaySettingAdapter;
    private LoadingDialog loadingDialog;
    private String gatewayId;
    private String encryption;
    private String wifiName;
    private String wifiPwd;
    private String networkLan;
    private String networkMask;
    private String zbChannel;
    private GatewayBaseInfo gatewayBaseInfo=new GatewayBaseInfo();
    private String uid;
    private AlertDialog deleteDialog;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_setting);
        ButterKnife.bind(this);
        context=this;
        initView();
        initRecycler();
        initData();
        initGatewayData();
    }

    private void initView() {
        if (loadingDialog==null){
            loadingDialog=LoadingDialog.getInstance(this);
        }
    }


    private void initData() {
        GatewaySettingItemBean gatewaySettingItemBeanOne=new GatewaySettingItemBean();
        gatewaySettingItemBeanOne.setTitle(getString(R.string.gateway_setting));
        gatewaySettingItemBeanOne.setSetting(false);

        GatewaySettingItemBean gatewaySettingItemBeanTwo=new GatewaySettingItemBean();
        gatewaySettingItemBeanTwo.setTitle(getString(R.string.gateway_setting_firmware_version));
        gatewaySettingItemBeanTwo.setSetting(false);

        GatewaySettingItemBean gatewaySettingItemBeanThree=new GatewaySettingItemBean();
        gatewaySettingItemBeanThree.setTitle(getString(R.string.gateway_setting_wifi_name));
        gatewaySettingItemBeanThree.setSetting(true);

        GatewaySettingItemBean gatewaySettingItemBeanFour=new GatewaySettingItemBean();
        gatewaySettingItemBeanFour.setTitle(getString(R.string.gateway_setting_wifi_pwd));
        gatewaySettingItemBeanFour.setSetting(true);


        GatewaySettingItemBean gatewaySettingItemBeanFive=new GatewaySettingItemBean();
        gatewaySettingItemBeanFive.setTitle(getString(R.string.gateway_setting_lan_ip));
        gatewaySettingItemBeanFive.setSetting(false);

        GatewaySettingItemBean gatewaySettingItemBeanSix=new GatewaySettingItemBean();
        gatewaySettingItemBeanSix.setTitle(getString(R.string.gateway_setting_wan_ip));
        gatewaySettingItemBeanSix.setSetting(false);


        GatewaySettingItemBean gatewaySettingItemBeanSeven=new GatewaySettingItemBean();
        gatewaySettingItemBeanSeven.setTitle(getString(R.string.gateway_setting_lan_subnet_mask));
        gatewaySettingItemBeanSeven.setSetting(false);

        GatewaySettingItemBean gatewaySettingItemBeanEight=new GatewaySettingItemBean();
        gatewaySettingItemBeanEight.setTitle(getString(R.string.gateway_setting_wan_subnet_mask));
        gatewaySettingItemBeanEight.setSetting(false);

        GatewaySettingItemBean gatewaySettingItemBeanNight=new GatewaySettingItemBean();
        gatewaySettingItemBeanNight.setTitle(getString(R.string.gateway_wan_access_mode));
        gatewaySettingItemBeanNight.setSetting(false);

        GatewaySettingItemBean gatewaySettingItemBeanTen=new GatewaySettingItemBean();
        gatewaySettingItemBeanTen.setTitle(getString(R.string.gateway_coordinator_channel));
        gatewaySettingItemBeanTen.setSetting(true);

        gatewaySettingItemBeans.add(gatewaySettingItemBeanOne);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanTwo);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanThree);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanFour);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanFive);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanSix);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanSeven);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanEight);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanNight);
        gatewaySettingItemBeans.add(gatewaySettingItemBeanTen);

        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }

    }

    private void initRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,LinearLayoutManager.VERTICAL);
        recycler.addItemDecoration(dividerItemDecoration);
        gatewaySettingAdapter=new GatewaySettingAdapter(gatewaySettingItemBeans);
        recycler.setAdapter(gatewaySettingAdapter);
        gatewaySettingAdapter.setOnItemClickListener(this);
    }

    private void initGatewayData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        uid=MyApplication.getInstance().getUid();
        if (!TextUtils.isEmpty(gatewayId)){
            //先读取数据库
            GatewayBaseInfo gatewayBaseInfo=MyApplication.getInstance().getDaoWriteSession().getGatewayBaseInfoDao().queryBuilder().where(GatewayBaseInfoDao.Properties.GatewayId.eq(gatewayId), GatewayBaseInfoDao.Properties.Uid.eq(uid)).unique();
            if (gatewayBaseInfo!=null){
                setGatewayBaseInfo(gatewayBaseInfo);
            }
            mPresenter.getNetBasic(MyApplication.getInstance().getUid(),gatewayId,gatewayId);
            loadingDialog.show(getString(R.string.get_gateway_info_waitting));
        }

    }

    private void setGatewayBaseInfo(GatewayBaseInfo gatewayBaseInfo) {
        //网关
        gatewaySettingItemBeans.get(0).setContent(gatewayBaseInfo.getGatewayId());
        //固件版本号
        gatewaySettingItemBeans.get(1).setContent(gatewayBaseInfo.getSW());
        //局域网ip
        gatewaySettingItemBeans.get(4).setContent(gatewayBaseInfo.getLanIp());
        //广域网ip
        gatewaySettingItemBeans.get(5).setContent(gatewayBaseInfo.getWanIp());
        //局域网子网掩码
        gatewaySettingItemBeans.get(6).setContent(gatewayBaseInfo.getLanNetmask());
        //广域网子网掩码
        gatewaySettingItemBeans.get(7).setContent(gatewayBaseInfo.getWanNetmask());
        //网关广域网接入方式
        gatewaySettingItemBeans.get(8).setContent(gatewayBaseInfo.getWanType());
        gatewaySettingItemBeans.get(2).setContent(gatewayBaseInfo.getSsid());
        gatewaySettingItemBeans.get(3).setContent(gatewayBaseInfo.getPwd());
        gatewaySettingItemBeans.get(9).setContent(gatewayBaseInfo.getChannel());
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }

    }


    @Override
    protected GatewaySettingPresenter<GatewaySettingView> createPresent() {
        return new GatewaySettingPresenter<>();
    }


    @OnClick({R.id.back, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_gateway_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        if (gatewayId != null) {
                            mPresenter.unBindGateway(MyApplication.getInstance().getUid(),gatewayId);
                            deleteDialog=AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.delete_be_being));
                            deleteDialog.setCancelable(false);
                        }

                    }
                });


                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position){
            case 2:
                //wifi名称
                View mView = LayoutInflater.from(this).inflate(R.layout.have_one_et_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                TextView tvContent=mView.findViewById(R.id.tv_content);
                EditText editText = mView.findViewById(R.id.et_name);
                editText.setText(wifiName);
                editText.setHint(getString(R.string.please_input_wifi_name));
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
                if (wifiName!=null){
                    editText.setSelection(wifiName.length());
                }
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.update_wifi_name));
                tvContent.setText(getString(R.string.update_wifi_name_need_rebind_cateye));
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(name)){
                            ToastUtil.getInstance().showShort(R.string.wifi_name_not_null);
                            return;
                        }else if (wifiName.equals(name)){
                            ToastUtil.getInstance().showShort(R.string.wifi_name_no_update);
                            return;
                        }else{
                            mPresenter.setWiFi(MyApplication.getInstance().getUid(),gatewayId,gatewayId,encryption,name,wifiPwd);
                        }
                        alertDialog.dismiss();
                    }
                });
                break;
            case 3:
                //wifi密码
                View mViewPwd = LayoutInflater.from(this).inflate(R.layout.have_one_et_dialog, null);
                TextView tvTitlePwd = mViewPwd.findViewById(R.id.tv_title);
                TextView tvContentPwd=mViewPwd.findViewById(R.id.tv_content);
                EditText editTextPwd = mViewPwd.findViewById(R.id.et_name);
                editTextPwd.setText(wifiPwd);
                editTextPwd.setHint(getString(R.string.please_input_wifi_pwd));
                editTextPwd.addTextChangedListener(new EditTextWatcher(this,null,editTextPwd,63));
                editTextPwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(63)});
                if (wifiPwd!=null){
                    editTextPwd.setSelection(wifiPwd.length());
                }
                TextView tv_left= mViewPwd.findViewById(R.id.tv_left);
                TextView tv_right= mViewPwd.findViewById(R.id.tv_right);
                AlertDialog alertDialogPwd = AlertDialogUtil.getInstance().common(this, mViewPwd);
                tvTitlePwd.setText(getString(R.string.update_wifi_pwd));
                tvContentPwd.setText(getString(R.string.update_wifi_pwd_need_rebind_cateye));
                tv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogPwd.dismiss();
                    }
                });
                tv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pwd = editTextPwd.getText().toString().trim();

                        if (TextUtils.isEmpty(pwd)){
                            ToastUtil.getInstance().showShort(R.string.wifi_pwd_not_null);
                            return;
                        }else if (wifiPwd.equals(pwd)){
                            ToastUtil.getInstance().showShort(R.string.wifi_pwd_no_update);
                            return;
                        }else if (pwd.length()<8){
                            ToastUtil.getInstance().showShort(R.string.wifi_password_length);
                            return;
                        }else if (pwd.length()>63){
                            ToastUtil.getInstance().showShort(R.string.wifi_password_length);
                        } else{
                            mPresenter.setWiFi(MyApplication.getInstance().getUid(),gatewayId,gatewayId,encryption,wifiName,pwd);
                        }
                        alertDialogPwd.dismiss();
                    }
                });
                break;
           /* case 4:
            case 6:
                //配置局域网
                View mPairLan = LayoutInflater.from(this).inflate(R.layout.have_two_et_dialog, null);
                TextView tvTitleLan = mPairLan.findViewById(R.id.tv_title);
                EditText editTextOne = mPairLan.findViewById(R.id.et_one);
                editTextOne.setText(networkLan);
                editTextOne.setHint(R.string.input_lan_ip);
                if (!TextUtils.isEmpty(networkLan)){
                    editTextOne.setSelection(networkLan.length());
                }
                EditText editTextTwo=mPairLan.findViewById(R.id.et_two);
                editTextTwo.setHint(R.string.input_lan_mask);
                if (!TextUtils.isEmpty(networkMask)){
                    editTextTwo.setText(networkMask);
                    editTextTwo.setSelection(networkMask.length());
                }
                TextView tv_lan_left= mPairLan.findViewById(R.id.tv_left);
                TextView tv_lan_right= mPairLan.findViewById(R.id.tv_right);
                AlertDialog alertDialogLan = AlertDialogUtil.getInstance().common(this, mPairLan);
                tvTitleLan.setText(getString(R.string.pair_lan_mask));
                tv_lan_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogLan.dismiss();
                    }
                });
                tv_lan_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ip=editTextOne.getText().toString().trim();
                        String mask = editTextTwo.getText().toString().trim();

                        if (TextUtils.isEmpty(ip)){
                            ToastUtil.getInstance().showShort(R.string.lan_ip_not_null);
                            return;
                        }else if (!StringUtil.isInner(ip)){
                            ToastUtil.getInstance().showShort(R.string.input_right_lan_ip);
                            return;
                        }else if (TextUtils.isEmpty(mask)){
                           ToastUtil.getInstance().showShort(R.string.lan_mask_not_null);
                           return;
                        }
                        if (!TextUtils.isEmpty(gatewayId)){
                            mPresenter.setNetBasic(MyApplication.getInstance().getUid(),gatewayId,gatewayId,ip,mask);
                        }
                        alertDialogLan.dismiss();
                    }
                });
                break;*/
            case 9:
                //配置
                View mViewChannel = LayoutInflater.from(this).inflate(R.layout.have_one_et_dialog, null);
                TextView tvTitleChannel = mViewChannel.findViewById(R.id.tv_title);
                TextView tvContentChannel=mViewChannel.findViewById(R.id.tv_content);
                EditText editTextChannel = mViewChannel.findViewById(R.id.et_name);
                editTextChannel.setText(zbChannel);
                editTextChannel.setHint(getString(R.string.range_channel));
                editTextChannel.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextChannel.setMaxEms(2);
                if (zbChannel!=null){
                    editTextChannel.setSelection(zbChannel.length());
                }
                TextView tv_left_channel= mViewChannel.findViewById(R.id.tv_left);
                TextView tv_right_channel= mViewChannel.findViewById(R.id.tv_right);
                AlertDialog alertDialogChannel = AlertDialogUtil.getInstance().common(this, mViewChannel);
                tvTitleChannel.setText(getString(R.string.setting_channel));
                tvContentChannel.setText(getString(R.string.setting_channel_need_bind_lock));
                tv_left_channel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogChannel.dismiss();
                    }
                });
                tv_right_channel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String channel = editTextChannel.getText().toString().trim();
                        int channelInt=0;
                        if (!TextUtils.isEmpty(channel)){
                             channelInt=Integer.parseInt(channel);
                        }
                        if (TextUtils.isEmpty(channel)){
                            ToastUtil.getInstance().showShort(R.string.channel_not_null);
                            return;
                        }else if (zbChannel.equals(channel)){
                            ToastUtil.getInstance().showShort(R.string.channel_not_update);
                            return;
                        }else if (channelInt<11){
                            ToastUtil.getInstance().showShort(R.string.range_channel);
                            return;
                        }else if (channelInt>26){
                            ToastUtil.getInstance().showShort(R.string.range_channel);
                            return;
                        }else{
                            mPresenter.setZbChannel(MyApplication.getInstance().getUid(),gatewayId,gatewayId,channel);
                        }
                        alertDialogChannel.dismiss();
                    }
                });


                break;
        }
    }

    @Override
    public void getNetBasicSuccess(GetNetBasicBean basicBean) {
        mPresenter.getGatewayWifiPwd(gatewayId);
        //获取到

        if (gatewaySettingItemBeans!=null&&gatewaySettingItemBeans.size()>0){
           GetNetBasicBean.ReturnDataBean returnDataBean= basicBean.getReturnData();
            //网关
            gatewaySettingItemBeans.get(0).setContent(basicBean.getGwId());
            //固件版本号
            gatewaySettingItemBeans.get(1).setContent(returnDataBean.getSW());
            //局域网ip
            gatewaySettingItemBeans.get(4).setContent(returnDataBean.getLanIp());
            networkLan=returnDataBean.getLanIp();
            //广域网ip
            gatewaySettingItemBeans.get(5).setContent(returnDataBean.getWanIp());
            //局域网子网掩码
            gatewaySettingItemBeans.get(6).setContent(returnDataBean.getLanNetmask());
            networkMask=returnDataBean.getLanNetmask();
            //广域网子网掩码
            gatewaySettingItemBeans.get(7).setContent(returnDataBean.getWanNetmask());
            //网关广域网接入方式
            gatewaySettingItemBeans.get(8).setContent(returnDataBean.getWanType());

            gatewayBaseInfo.setGatewayId(basicBean.getGwId());
            gatewayBaseInfo.setDeviceIdUid(basicBean.getGwId()+uid);
            gatewayBaseInfo.setSW(returnDataBean.getSW());
            gatewayBaseInfo.setLanIp(returnDataBean.getLanIp());
            gatewayBaseInfo.setWanIp(returnDataBean.getWanIp());
            gatewayBaseInfo.setLanNetmask(returnDataBean.getLanNetmask());
            gatewayBaseInfo.setWanNetmask(returnDataBean.getWanNetmask());
            gatewayBaseInfo.setWanType(returnDataBean.getWanType());
            gatewayBaseInfo.setUid(uid);
        }
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void getNetBasicFail() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_net_basic_fail);
    }

    @Override
    public void getNetBasicThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_net_basic_fail);
    }

    @Override
    public void onGetWifiInfoSuccess(GwWiFiBaseInfo wiFiBaseInfo) {
        mPresenter.getZbChannel(MyApplication.getInstance().getUid(),gatewayId,gatewayId);
        if (gatewaySettingItemBeans!=null&&gatewaySettingItemBeans.size()>0){
            GwWiFiBaseInfo.ReturnDataBean gwWiFiBaseInfo=wiFiBaseInfo.getReturnData();
            gatewaySettingItemBeans.get(2).setContent(gwWiFiBaseInfo.getSsid());
            gatewaySettingItemBeans.get(3).setContent(gwWiFiBaseInfo.getPwd());
            encryption=gwWiFiBaseInfo.getEncryption();
            wifiName=gwWiFiBaseInfo.getSsid();
            wifiPwd=gwWiFiBaseInfo.getPwd();
            gatewayBaseInfo.setEncryption(encryption);
            gatewayBaseInfo.setSsid(wifiName);
            gatewayBaseInfo.setPwd(wifiPwd);
        }
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetWifiInfoFailed() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_wifi_info_fail);
    }

    @Override
    public void onGetWifiInfoThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_wifi_info_fail);
    }

    @Override
    public void getZbChannelSuccess(GetZbChannelBean getZbChannelBean) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        if (gatewaySettingItemBeans!=null&&gatewaySettingItemBeans.size()>0){
            gatewaySettingItemBeans.get(9).setContent(getZbChannelBean.getReturnData().getChannel());
            zbChannel=getZbChannelBean.getReturnData().getChannel();
            gatewayBaseInfo.setChannel(zbChannel);
        }
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }
        MyApplication.getInstance().getDaoWriteSession().insertOrReplace(gatewayBaseInfo);
    }

    @Override
    public void getZbChannelFail() {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(R.string.get_zb_channel_fail);
    }

    @Override
    public void getZbChannelThrowable(Throwable throwable) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        LogUtils.e(throwable.getMessage());
        ToastUtil.getInstance().showShort(throwable.getMessage());
    }

    @Override
    public void unbindGatewaySuccess() {
        if (deleteDialog!=null){
            deleteDialog.dismiss();
        }
        //清除数据库
        DaoSession daoSession=MyApplication.getInstance().getDaoWriteSession();
        daoSession.getGatewayBaseInfoDao().deleteByKey(gatewayId+uid);
        daoSession.getGatewayServiceInfoDao().queryBuilder().where(GatewayServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();//网关
        daoSession.getGatewayLockServiceInfoDao().queryBuilder().where(GatewayLockServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();//网关锁
        daoSession.getCatEyeServiceInfoDao().queryBuilder().where(CatEyeServiceInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();//猫眼

        //解绑成功
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void unbindGatewayFail() {
        if (deleteDialog!=null){
            deleteDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }

    @Override
    public void unbindGatewayThrowable(Throwable throwable) {
        if (deleteDialog!=null){
            deleteDialog.dismiss();
        }
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }

    @Override
    public void setWifiSuccess(String name,String pwd) {
        if (gatewaySettingItemBeans!=null&&gatewaySettingItemBeans.size()>0){
            gatewaySettingItemBeans.get(2).setContent(name);
            gatewaySettingItemBeans.get(3).setContent(pwd);
            wifiName=name;
            wifiPwd=pwd;
        }
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }
        ToastUtil.getInstance().showShort(getString(R.string.set_success));
    }

    @Override
    public void setWifiFail() {
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setWifiThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setNetLanSuccess(String ip,String mask) {
        if (gatewaySettingItemBeans!=null&&gatewaySettingItemBeans.size()>0){
            gatewaySettingItemBeans.get(4).setContent(ip);
            gatewaySettingItemBeans.get(6).setContent(mask);
            networkLan=ip;
            networkMask=mask;
        }
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }
        ToastUtil.getInstance().showShort(getString(R.string.set_success));

    }

    @Override
    public void setNetLanFail() {
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setNetLanThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.set_failed));
    }

    @Override
    public void setZbChannelSuccess(String channel) {
        if (gatewaySettingItemBeans!=null&&gatewaySettingItemBeans.size()>0){
            gatewaySettingItemBeans.get(9).setContent(channel);
            zbChannel=channel;
        }
        if (gatewaySettingAdapter!=null){
            gatewaySettingAdapter.notifyDataSetChanged();
        }
        ToastUtil.getInstance().showShort(R.string.set_success);
    }

    @Override
    public void setZbChannelFail() {
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void setZbChannelThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }
}
