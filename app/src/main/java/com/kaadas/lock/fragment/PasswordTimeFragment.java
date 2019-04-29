package com.kaadas.lock.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothPasswordManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothPasswordShareActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothUserPasswordAddActivity;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleFragment;
import com.kaadas.lock.mvp.presenter.AddTimePasswordPresenter;
import com.kaadas.lock.mvp.view.IAddTimePasswprdView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
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

/**
 * Created by David
 */

public class PasswordTimeFragment extends BaseBleFragment<IAddTimePasswprdView, AddTimePasswordPresenter<IAddTimePasswprdView>>
        implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, IAddTimePasswprdView, BaseQuickAdapter.OnItemClickListener {
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
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.ll_custom)
    LinearLayout llCustom;
    int timeStatus = 0;//时间策略
    private BleLockInfo bleLockInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_password_time, container, false);
        }
        ButterKnife.bind(this, mView);
        bleLockInfo = ((BluetoothUserPasswordAddActivity) getActivity()).getLockInfo();
        initRecycleview();
        btnRandomGeneration.setOnClickListener(this);
        btnConfirmGeneration.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
        return mView;
    }

    @Override
    protected AddTimePasswordPresenter<IAddTimePasswprdView> createPresent() {
        return new AddTimePasswordPresenter<>();
    }


    private void initRecycleview() {
        list.add(new ShiXiaoNameBean(getString(R.string.father), false));
        list.add(new ShiXiaoNameBean(getString(R.string.mother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_brother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.small_di_di), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_sister), false));
        list.add(new ShiXiaoNameBean(getString(R.string.rests), false));
        shiXiaoNameAdapter = new ShiXiaoNameAdapter(list);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
        recyclerView.setAdapter(shiXiaoNameAdapter);
        shiXiaoNameAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelected(false);
        }
        ShiXiaoNameBean shiXiaoNameBean = list.get(position);
        String name = shiXiaoNameBean.getName();
        etName.setText(name);
        etName.setSelection(name.length());
        list.get(position).setSelected(true);
        shiXiaoNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_random_generation:
                String password = StringUtil.makeRandomPassword();
                etPassword.setText(password);
                etPassword.setSelection(password.length());
                break;
            case R.id.btn_confirm_generation:
                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtil.getInstance().showShort(R.string.please_have_net_add_pwd);
                    return;
                }
                String strPassword = etPassword.getText().toString().trim();
                String nickName = etName.getText().toString().trim();
                if (!StringUtil.randomJudge(strPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                if (StringUtil.checkSimplePassword(strPassword)) {
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

                if (!StringUtil.nicknameJudge(nickName)) {
                    ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                    return;
                }
                if (0 == timeStatus) {
                    ToastUtil.getInstance().showShort(R.string.select_time_ce_lue);
                    return;
                } else if (KeyConstants.YONG_JIU == timeStatus) {
                    if (mPresenter.isAuth(bleLockInfo, true)) {
                        //todo 获取到时间设置
//                        mPresenter.setPwd(strPassword, 1, nickName, startMilliseconds, endMilliseconds);
                    }
                    //永久
                } else if (KeyConstants.ONE_DAY == timeStatus) {
                    //一天.
                    LogUtils.e("点击添加密码   ");
                    if (mPresenter.isAuth(bleLockInfo, true)) {
                        mPresenter.setPwd(strPassword, 2, nickName, System.currentTimeMillis(), System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                    }
                } else if (KeyConstants.CUSTOM == timeStatus) {
                    //自定义
                    //todo 获取到时间的处理
         /*           if (startMilliseconds == 0) {
                        ToastUtil.getInstance().showShort(R.string.select_take_effect_time);
                        return;
                    }
                    if (endMilliseconds == 0) {
                        ToastUtil.getInstance().showShort(R.string.select_end_time);
                        return;
                    }

                    if (startMilliseconds >= endMilliseconds) {
                        ToastUtil.getInstance().showShort(R.string.end_time_must_bigger_end_time);
                        return;
                    }
                    LogUtils.e("开始时间   " + DateUtils.getDateTimeFromMillisecond(startMilliseconds));
                    LogUtils.e("结束时间   " + DateUtils.getDateTimeFromMillisecond(endMilliseconds));
                    LogUtils.e("当前时间   " + DateUtils.getDateTimeFromMillisecond(System.currentTimeMillis()));
                    if (mPresenter.isAuth(bleLockInfo, true)) {
                        mPresenter.setPwd(strPassword, 4, nickName, startMilliseconds, endMilliseconds);
                    }*/
                }

                intent = new Intent(getActivity(), BluetoothPasswordShareActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_one:
                llCustom.setVisibility(View.GONE);
                timeStatus = KeyConstants.YONG_JIU;
                break;
            case R.id.rb_two:
                llCustom.setVisibility(View.GONE);
                timeStatus = KeyConstants.ONE_DAY;
                break;
            case R.id.rb_three:
                timeStatus = KeyConstants.CUSTOM;
                llCustom.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSetUserTypeSuccess() {

    }

    @Override
    public void onSetUserTypeFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void onSetTimePlanSuccess() {
        LogUtils.e("设置时间计划成功");
    }

    @Override
    public void onSetTimePlanFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void onSetPasswordSuccess(AddPasswordBean.Password password) {

    }

    @Override
    public void onSetPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.set_failed);
    }

    @Override
    public void onPwdFull() {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(getActivity(), getString(R.string.hint), getString(R.string.password_full_and_delete_exist_code), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {

            }
        });
    }

    @Override
    public void onUploadSuccess(String password, String number, String nickName) {
        LogUtils.e("添加密码成功   " + password.toString());
        Intent intent = new Intent();
        intent.setClass(MyApplication.getInstance(), BluetoothPasswordShareActivity.class);
        intent.putExtra(KeyConstants.TO_DETAIL_NUMBER, number);
        intent.putExtra(KeyConstants.TO_DETAIL_PASSWORD, password);
        intent.putExtra(KeyConstants.TO_DETAIL_TYPE, 1);
        intent.putExtra(KeyConstants.TO_DETAIL_NICKNAME, nickName);
        intent.putExtra(KeyConstants.TIME_CE_LUE, timeStatus);
        if (KeyConstants.CUSTOM == timeStatus) {
            //todo 获取到时间传过去
          /*  intent.putExtra(KeyConstants.CUSTOM_START_TIME, startMilliseconds);
            intent.putExtra(KeyConstants.CUSTOM_END_TIME, endMilliseconds);*/
        }
        startActivity(intent);
    }

    @Override
    public void onUploadFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(getContext(), BluetoothPasswordManagerActivity.class));
        getActivity().finish();
    }

    @Override
    public void onUploadFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(getContext(), BluetoothPasswordManagerActivity.class));
        getActivity().finish();
    }

    @Override
    public void startSetPwd() {
        showLoading(getString(R.string.is_setting));
    }

    @Override
    public void endSetPwd() {
        hiddenLoading();
    }

    @Override
    public void onSyncPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.set_failed);
    }
}
