package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.password.BlePasswordManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothPasswordShareActivity;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleFragment;
import com.kaadas.lock.mvp.presenter.ble.AddTempPresenter;
import com.kaadas.lock.mvp.view.IAddTempView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
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

public class PasswordTempFragment extends BaseBleFragment<IAddTempView, AddTempPresenter<IAddTempView>>
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, IAddTempView {
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
    private BleLockInfo bleLockInfo; //蓝牙设备信息

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_password_temporary, container, false);
        }
        ButterKnife.bind(this, mView);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        mPresenter.isAuth(bleLockInfo, false);  //自动连接  但是不提示用户
        initRecycleview();
        btnRandomGeneration.setOnClickListener(this);
        btnConfirmGeneration.setOnClickListener(this);
        initMonitor();
        return mView;

    }
    private void initMonitor() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                }
                shiXiaoNameAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public static PasswordTempFragment newInstance() {
        PasswordTempFragment fragment = new PasswordTempFragment();
        return fragment;
    }

    @Override
    protected AddTempPresenter<IAddTempView> createPresent() {
        return new AddTempPresenter<>();
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
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void afterTextChanged(String toString) {
                        }
                    });
                    return;
                }
                String temproaryPasswordName = etName.getText().toString().trim();
                if (!StringUtil.nicknameJudge(temproaryPasswordName)) {
                    ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                    return;
                }

                if (mPresenter.isAuth(bleLockInfo, true)) {
                    mPresenter.setPwd(strTemporaryPassword,
                            bleLockInfo.getServerLockInfo().getLockName(),
                            temproaryPasswordName);
                }

             /*   intent = new Intent(getActivity(), BluetoothPasswordShareActivity.class);
                startActivity(intent);*/
                break;
        }
    }

    @Override
    public void onStartSetPwd() {
        //开始设置密码
        showLoading(getString(R.string.is_setting_pwd));
    }

    @Override
    public void onEndSetPwd() {
        hiddenLoading();
    }

    @Override
    public void onSetPwdFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.set_temp_pwd_failed);
        hiddenLoading();
    }

    @Override
    public void onSetPwdFailedServer(BaseResult result) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(getActivity(), BlePasswordManagerActivity.class));
        getActivity().finish();
    }

    @Override
    public void onSetPwdSuccess() {
        ToastUtil.getInstance().showLong(R.string.set_temp_pwd_succcess);
    }

    @Override
    public void onUploadToServer() {

    }

    @Override
    public void onUpLoadSuccess(String password, String number, String nickName) {
        hiddenLoading();
        LogUtils.e("添加密码成功   " + password.toString());
        Intent intent = new Intent();
        intent.setClass(MyApplication.getInstance(), BluetoothPasswordShareActivity.class);
        intent.putExtra(KeyConstants.TO_DETAIL_NUMBER, number);
        intent.putExtra(KeyConstants.TO_DETAIL_PASSWORD, password);
        intent.putExtra(KeyConstants.TO_DETAIL_TYPE, 2);
        intent.putExtra(KeyConstants.TO_DETAIL_NICKNAME, nickName);
        intent.putExtra(KeyConstants.TIME_CE_LUE, KeyConstants.TEMP);
        startActivity(intent);

    }

    @Override
    public void onUploadFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        hiddenLoading();
        startActivity(new Intent(getActivity(), BlePasswordManagerActivity.class));
        getActivity().finish();
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
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(String toString) {
            }
        });
    }

    @Override
    public void onSyncPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(getString(R.string.sync_failed));
    }
}
