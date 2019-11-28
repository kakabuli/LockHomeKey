package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.password.GatewayPasswordAddActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.GatewayLockPasswordShareActivity;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordTempPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordTempView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by David
 */

public class GatewayPasswordTemporaryFragment extends BaseFragment<IGatewayLockPasswordTempView,
        GatewayLockPasswordTempPresenter<IGatewayLockPasswordTempView>>
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, IGatewayLockPasswordTempView {
    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.et_name)
    EditText etName;
    List<ShiXiaoNameBean> list = new ArrayList<>();
    ShiXiaoNameAdapter shiXiaoNameAdapter;
    View mView;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;
    @BindView(R.id.pwd_manager_icon)
    ImageView pwdManagerIcon;
    @BindView(R.id.pwd_manager_grant_iv)
    ImageView pwdManagerGrantIv;
    @BindView(R.id.ll_nick_name)
    LinearLayout llNickName;
    private String gatewayId;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_password_gateway_temporary, container, false);
        }

        gatewayId = ((GatewayPasswordAddActivity) getActivity()).gatewayId;
        deviceId = ((GatewayPasswordAddActivity) getActivity()).deviceId;

        ButterKnife.bind(this, mView);
        btnRandomGeneration.setOnClickListener(this);
        btnConfirmGeneration.setOnClickListener(this);

        llNickName.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        return mView;
    }

    @Override
    protected GatewayLockPasswordTempPresenter<IGatewayLockPasswordTempView> createPresent() {
        return new GatewayLockPasswordTempPresenter<>();
    }


    public static PasswordTempFragment newInstance() {
        PasswordTempFragment fragment = new PasswordTempFragment();
        return fragment;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelected(false);
        }
        ShiXiaoNameBean shiXiaoNameBean = list.get(position);
        list.get(position).setSelected(true);
        shiXiaoNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_random_generation:
                String password = StringUtil.makeRandomPassword();
                etPassword.setText(password);
                etPassword.setSelection(password.length());
                break;
            case R.id.btn_confirm_generation:
                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtil.getInstance().showShort(R.string.please_have_net_add_temp_pwd);
                    return;
                }
                String strTemporaryPassword = etPassword.getText().toString().trim();

                if (!StringUtil.randomJudge(strTemporaryPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                if (StringUtil.checkSimplePassword(strTemporaryPassword)) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(getActivity(), getString(R.string.hint), getString(R.string.password_simple_please_reset), getString(R.string.go_on), getString(R.string.reinstall), new AlertDialogUtil.ClickListener() {
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
                showLoading(getString(R.string.is_setting_password));
                mPresenter.setTempPassword(deviceId, gatewayId, strTemporaryPassword);
                break;
        }
    }


    @Override
    public void getLockInfoSuccess(int maxPwd) {

    }

    @Override
    public void getLockInfoFail() {
        onFailed();
    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {
        onFailed();
    }

    @Override
    public void syncPasswordComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {

    }

    @Override
    public void syncPasswordFailed(Throwable throwable) {
        onFailed();
    }

    @Override
    public void addLockPwdFail(Throwable throwable) {
        onFailed();
    }

    @Override
    public void addLockPwdSuccess(GatewayPasswordPlanBean gatewayPasswordPlanBean, String pwdValue) {
        ToastUtil.getInstance().showLong(getString(R.string.set_success));
        hiddenLoading();

        //跳转到分享页面
        Intent intent = new Intent(getActivity(), GatewayLockPasswordShareActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
        intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
        //1表示永久密码，2表示临时密码
        intent.putExtra(KeyConstants.PWD_VALUE, pwdValue);
        intent.putExtra(KeyConstants.PWD_ID, gatewayPasswordPlanBean.getPasswordNumber()+"");
        intent.putExtra(KeyConstants.GATEWAY_PASSWORD_BEAN, gatewayPasswordPlanBean);
        startActivity(intent);
    }

    @Override
    public void setUserTypeSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean) {

    }

    @Override
    public void onLoadPasswordPlan(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {

    }

    @Override
    public void onLoadPasswordPlanFailed(Throwable throwable) {

    }

    @Override
    public void onLoadPasswordPlanComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {

    }



    @Override
    public void setUserTypeFailed(Throwable typeFailed) {

    }

    @Override
    public void setPlanSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean) {  //设置成功

    }

    @Override
    public void setPlanFailed(Throwable throwable) {


    }

    @Override
    public void deletePasswordSuccess() {

    }

    @Override
    public void deletePasswordFailed(Throwable throwable) {

    }


    private void onFailed() {
        hiddenLoading();
        //密码添加异常
        LogUtils.e("添加密码异常    ");
        AlertDialogUtil.getInstance().singleButtonNoTitleDialog(getActivity(), getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {

            }
        });
    }

    @Override
    public void gatewayPasswordFull() {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(getContext(), getString(R.string.hint), getString(R.string.password_full_and_delete_exist_code), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {

            }
        });
    }
}
