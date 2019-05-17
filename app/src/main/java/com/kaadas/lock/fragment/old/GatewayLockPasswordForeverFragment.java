package com.kaadas.lock.fragment.old;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockPasswordShareActivity;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordForeverPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockPasswrodView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class GatewayLockPasswordForeverFragment extends BaseFragment<GatewayLockPasswrodView, GatewayLockPasswordForeverPresenter<GatewayLockPasswrodView>> implements GatewayLockPasswrodView{

    View mView;
    @BindView(R.id.pwd_manager_icon)
    ImageView pwdManagerIcon;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;
    Unbinder unbinder;
    private  List<String> pwdList;

    private List<String> addPwdIdList=new ArrayList<>();
    private String  gatewayId;
    private String  deviceId;
    private AlertDialog takeEffect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.gateway_lock_fragment_password_forever, container, false);
        }
        unbinder = ButterKnife.bind(this, mView);
        getData();
        initData();
        return mView;

    }

    private void getData() {
        pwdList= (List<String>) getArguments().get(KeyConstants.LOCK_PWD_LIST);
        gatewayId=getArguments().getString(KeyConstants.GATEWAY_ID);
        deviceId=getArguments().getString(KeyConstants.DEVICE_ID);
    }

    private void initData() {
        if (addPwdIdList!=null){
            addPwdIdList.add("00");
            addPwdIdList.add("01");
            addPwdIdList.add("02");
            addPwdIdList.add("03");
            addPwdIdList.add("04");
        }

        if (pwdList!=null){
            for (int i=0;i<pwdList.size();i++){
                //是否存在00-04的值，存在的话要删除list中的数据
                String pwdNum=pwdList.get(i);
                for (int j=0;j<addPwdIdList.size();j++){
                    if (pwdNum.equals(addPwdIdList.get(j))){
                        addPwdIdList.remove(j);
                    }
                }
            }
        }
    }

    @Override
    protected GatewayLockPasswordForeverPresenter<GatewayLockPasswrodView> createPresent() {
        return new GatewayLockPasswordForeverPresenter<>();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_random_generation, R.id.btn_confirm_generation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_random_generation:
                String password = StringUtil.makeRandomPassword();
                if (etPassword!=null){
                    etPassword.setText(password);
                    etPassword.setSelection(password.length());
                }
                break;
            case R.id.btn_confirm_generation:
                if (!NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noButtonDialog(getActivity(),getString(R.string.no_find_network));
                    return;
                }
                String strForeverPassword = etPassword.getText().toString().trim();
                if (!StringUtil.randomJudge(strForeverPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                if (StringUtil.checkSimplePassword(strForeverPassword)) {
                    AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(getActivity(), getString(R.string.password_simple_please_reset), getString(R.string.go_on), getString(R.string.reinstall),"#1F96F7","#1F96F7", new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }
                        @Override
                        public void right() {
                            etPassword.setText("");
                            return;
                        }
                    });
                    return;
                }
                addLockPwd(strForeverPassword);
                break;
        }
    }

    private void addLockPwd(String strForeverPassword) {
        if (addPwdIdList != null && addPwdIdList.size() > 0) {
            for (int p=0;p<addPwdIdList.size();p++){
                LogUtils.e("添加密码的编号   "+addPwdIdList.get(p));
                mPresenter.addLockPwd(gatewayId,deviceId,addPwdIdList.get(p),strForeverPassword);
                takeEffect=AlertDialogUtil.getInstance().noButtonDialog(getActivity(),getString(R.string.take_effect_be_being));
                takeEffect.setCancelable(false);
                break;
            }
        } else {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(getActivity(), getString(R.string.forever_pwd_is_full), getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });
        }
    }
    @Override
    public void addLockPwdSuccess(String pwdId,String pwdValue) {
        //密码添加成功
        addPwdIdList.remove(0);
        if (takeEffect!=null){
            takeEffect.dismiss();
        }


       //跳转到分享页面
        Intent intent=new Intent(getActivity(), GatewayLockPasswordShareActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
        intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
        //1表示永久密码，2表示临时密码
        intent.putExtra(KeyConstants.PWD_TYPE,1);
        intent.putExtra(KeyConstants.PWD_VALUE,pwdValue);
        intent.putExtra(KeyConstants.PWD_ID,pwdId);
        LogUtils.e(pwdId+"pwdId------"+pwdValue+"pwdValue");
        startActivity(intent);
    }

    @Override
    public void addLockPwdFail() {
        //密码添加失败
        LogUtils.e("添加密码失败");
        if (takeEffect!=null){
            takeEffect.dismiss();
        }
        if (getActivity()!=null) {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(getActivity(), getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });
        }
    }

    @Override
    public void addLockPwdThrowable(Throwable throwable) {
        //密码添加异常
        LogUtils.e("添加密码异常    ");
        if (takeEffect!=null){
            takeEffect.dismiss();
        }
        if (getActivity()!=null) {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(getActivity(), getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });
        }
    }


}
