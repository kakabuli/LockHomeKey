package com.kaadas.lock.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.password.BlePasswordManagerActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothPasswordShareActivity;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothUserPasswordAddActivity;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleFragment;
import com.kaadas.lock.mvp.presenter.ble.AddTimePasswordPresenter;
import com.kaadas.lock.mvp.view.IAddTimePasswprdView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateFormatUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */

public class PasswordTimeFragment extends BaseBleFragment<IAddTimePasswprdView, AddTimePasswordPresenter<IAddTimePasswprdView>>
        implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, IAddTimePasswprdView, BaseQuickAdapter.OnItemClickListener {
    long startMilliseconds = 0;//开始毫秒数
    long endMilliseconds = 0;//结束毫秒数
    int timeStatus = 0;//时间策略
    @BindView(R.id.pwd_manager_icon)
    ImageView pwdManagerIcon;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.pwd_manager_grant_iv)
    ImageView pwdManagerGrantIv;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.ll_effective_time)
    LinearLayout llEffectiveTime;
    @BindView(R.id.ll_deadline)
    LinearLayout llDeadline;
    @BindView(R.id.ll_custom)
    LinearLayout llCustom;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;
    @BindView(R.id.tv_take_effect_date)
    TextView tvTakeEffectDate;
    @BindView(R.id.tv_take_effect_am_pm)
    TextView tvTakeEffectAmPm;
    @BindView(R.id.tv_take_effect_time)
    TextView tvTakeEffectTime;
    @BindView(R.id.tv_deadline_date)
    TextView tvDeadlineDate;
    @BindView(R.id.tv_deadline_am_pm)
    TextView tvDeadlineAmPm;
    @BindView(R.id.tv_deadline_time)
    TextView tvDeadlineTime;
    private BleLockInfo bleLockInfo;
    ShiXiaoNameAdapter shiXiaoNameAdapter;

    public static PasswordTimeFragment newInstance() {
        PasswordTimeFragment fragment = new PasswordTimeFragment();
        return fragment;
    }

    private CustomDatePicker mTimerPicker;
    private CustomDatePicker mTimerPicker1;
    List<ShiXiaoNameBean> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mFragmentView = inflater.inflate(R.layout.fragment_password_time, container, false);
        ButterKnife.bind(this, mFragmentView);
        initTimerPicker();

        rg.setOnCheckedChangeListener(this);
        rg.check(R.id.rb_two);//默认选中24小时
        llCustom.setVisibility(View.GONE);
        timeStatus = KeyConstants.ONE_DAY;

        llEffectiveTime.setOnClickListener(this);
        llDeadline.setOnClickListener(this);
        btnRandomGeneration.setOnClickListener(this);
        btnConfirmGeneration.setOnClickListener(this);


        bleLockInfo = ((BluetoothUserPasswordAddActivity) getActivity()).getLockInfo();
        mPresenter.isAuth(bleLockInfo, false);
        initRecycleview();
        initMonitor();
        return mFragmentView;

    }
    private void initMonitor() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    LogUtils.d("davi 改变了 onTextChanged");
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
        recycleview.setLayoutManager(new GridLayoutManager(getActivity(), 6));
        recycleview.setAdapter(shiXiaoNameAdapter);
        shiXiaoNameAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

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
                setstartTime(System.currentTimeMillis());
                setEndTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 开始时间
            case R.id.ll_effective_time:
                LogUtils.d("davi mTimerPicker " + mTimerPicker);
                // 日期格式为yyyy-MM-dd HH:mm
                mTimerPicker.show(startcurrentTime);
                break;
            // 结束时间
            case R.id.ll_deadline:

                mTimerPicker1.show(endcurrentTime);

                break;
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
                        mPresenter.setPwd(strPassword, 1, nickName, startMilliseconds, endMilliseconds);
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
                    if (startMilliseconds == 0) {
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
                    }
                }
                break;
        }
    }

    String startcurrentTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
    String endcurrentTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);

    private void initTimerPicker() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long after1 = formatter.parse("2019-02-14 15:30").getTime();
            long after2 = formatter.parse("2020-02-14 15:30").getTime();
            long diff = after2 - after1;

            String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
            String endTime = DateFormatUtils.long2Str((System.currentTimeMillis() + diff), true);

            // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
            mTimerPicker = new CustomDatePicker(getActivity(), new CustomDatePicker.Callback() {
                @Override
                public void onTimeSelected(long timestamp) {
                    setstartTime(timestamp);

                }
            }, beginTime, endTime);
            // 允许点击屏幕或物理返回键关闭
            mTimerPicker.setCancelable(true);
            // 显示时和分
            mTimerPicker.setCanShowPreciseTime(true);
            // 允许循环滚动
            mTimerPicker.setScrollLoop(false);
            // 允许滚动动画
            mTimerPicker.setCanShowAnim(true);


            // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
            mTimerPicker1 = new CustomDatePicker(getActivity(), new CustomDatePicker.Callback() {
                @Override
                public void onTimeSelected(long timestamp) {
                    setEndTime(timestamp);

                }
            }, beginTime, endTime);
            // 允许点击屏幕或物理返回键关闭
            mTimerPicker1.setCancelable(true);
            // 显示时和分
            mTimerPicker1.setCanShowPreciseTime(true);
            // 允许循环滚动
            mTimerPicker1.setScrollLoop(false);
            // 允许滚动动画
            mTimerPicker1.setCanShowAnim(true);
        } catch (Exception e) {
            Log.e("PasswordTimeFragment", e.getMessage());
        }
    }

    private void setEndTime(long timestamp) {
        //   mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
        endMilliseconds = timestamp;
        endcurrentTime = DateFormatUtils.long2Str(timestamp, true);
        deadlineLine(endcurrentTime);
        Log.e("PasswordTimeFragment", "选择的结束时间:" + endcurrentTime);
    }

    private void setstartTime(long timestamp) {
        //   mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
        startMilliseconds = timestamp;
        startcurrentTime = DateFormatUtils.long2Str(timestamp, true);

        takeEffectTimeProcess(startcurrentTime);
        Log.e("denganzhi1", "选择的时间是:" + startcurrentTime);
    }

    private void deadlineLine(String endcurrentTime) {
        String[] split = endcurrentTime.split(" ");
        String date = split[0];
        String[] time = split[1].split(":");
        String hour = time[0];
        String minute = time[1];
        tvDeadlineDate.setText(date);
        tvDeadlineTime.setText(hour + ":" + minute);
        if (Integer.parseInt(hour) > 12) {
            tvDeadlineAmPm.setText(getString(R.string.pm));
        } else {
            tvDeadlineAmPm.setText(getString(R.string.am));
        }

    }

    private void takeEffectTimeProcess(String fullTime) {
        //2019-03-10 18:58
        String[] split = fullTime.split(" ");
        String date = split[0];
        String[] time = split[1].split(":");
        String hour = time[0];
        String minute = time[1];
        tvTakeEffectDate.setText(date);
        tvTakeEffectTime.setText(hour + ":" + minute);
        if (Integer.parseInt(hour) > 12) {
            tvTakeEffectAmPm.setText(getString(R.string.pm));
        } else {
            tvTakeEffectAmPm.setText(getString(R.string.am));
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
            intent.putExtra(KeyConstants.CUSTOM_START_TIME, startMilliseconds);
            intent.putExtra(KeyConstants.CUSTOM_END_TIME, endMilliseconds);
        }
        startActivity(intent);
    }

    @Override
    public void onUploadFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(getContext(), BlePasswordManagerActivity.class));
        getActivity().finish();
    }

    @Override
    public void onUploadFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(R.string.lock_set_success_please_sync);
        startActivity(new Intent(getContext(), BlePasswordManagerActivity.class));
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

    @Override
    public void onTimePwdFull() {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.only_0to4);
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
        etName.setFocusable(true);
        etName.setFocusableInTouchMode(true);
        etName.requestFocus();
        list.get(position).setSelected(true);
        shiXiaoNameAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
