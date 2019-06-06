package com.kaadas.lock.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.home.OldBluetoothOpenLockRecordActivity;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleFragment;
import com.kaadas.lock.mvp.presenter.OldBleLockPresenter;
import com.kaadas.lock.mvp.view.IOldBleLockView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.AnimationsContainer;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OldBleLockFragment extends BaseBleFragment<IOldBleLockView, OldBleLockPresenter<IOldBleLockView>>
        implements View.OnClickListener, IOldBleLockView {

    List<BluetoothRecordBean> list = new ArrayList<>();
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.iv_external_big)
    ImageView ivExternalBig;
    @BindView(R.id.iv_external_middle)
    ImageView ivExternalMiddle;
    @BindView(R.id.iv_external_small)
    ImageView ivExternalSmall;
    @BindView(R.id.iv_inner_small)
    ImageView ivInnerSmall;
    @BindView(R.id.iv_inner_middle)
    ImageView ivInnerMiddle;
    @BindView(R.id.tv_inner)
    TextView tvInner;
    @BindView(R.id.tv_external)
    TextView tvExternal;
    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.rl_has_data)
    RelativeLayout rlHasData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    private BleLockInfo bleLockInfo;
    private boolean isOpening;
    private Runnable lockRunnable;
    private boolean isConnectingDevice;
    private Handler handler = new Handler();
    private HomePageFragment.ISelectChangeListener listener;
    private HomePageFragment homeFragment;
    private boolean isCurrentFragment;
    private int position;
    BluetoothRecordAdapter bluetoothRecordAdapter;
    boolean hasData;
    private boolean isLoadingBleRecord;  //正在加载锁上数据
    private boolean isDestroy = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        bleLockInfo = (BleLockInfo) arguments.getSerializable(KeyConstants.BLE_LOCK_INFO);
        position = arguments.getInt(KeyConstants.FRAGMENT_POSITION);
        lockRunnable = new Runnable() {
            @Override
            public void run() {
                LogUtils.e(" 首页锁状态  反锁状态   " + bleLockInfo.getBackLock() + "    安全模式    " + bleLockInfo.getSafeMode() + "   布防模式   " + bleLockInfo.getArmMode());
                isOpening = false;
                changeOpenLockStatus(8);
                if (bleLockInfo.getBackLock() == 0) {  //等于0时是反锁状态
                    changeOpenLockStatus(6);
                }
                if (bleLockInfo.getSafeMode() == 1) {//安全模式
                    changeOpenLockStatus(5);
                }
                if (bleLockInfo.getArmMode() == 1) {//布防模式
                    changeOpenLockStatus(4);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_ble_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        tvMore.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        changeOpenLockStatus(12);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPresenter.isAttach() &&!isDestroy) {
            mPresenter.attachView(this);
        }
        mPresenter.getOpenRecordFromServer(1, bleLockInfo);
    }

    private void initView() {
        LogUtils.e("设备  HomeLockFragment  " + this);
        homeFragment = (HomePageFragment) getParentFragment();
        //切换到当前页面
        listener = new HomePageFragment.ISelectChangeListener() {
            @Override
            public void onSelectChange(boolean isSelect) {
                if (!isSelect) {
                    if (mPresenter.isAttach()) {
                        mPresenter.detachView();
                    }
                } else {
                    LogUtils.e("切换到当前界面  设备22  isdestroy  " + isDestroy + this + isCurrentFragment);
                    //切换到当前页面
                    if (!isDestroy ){
                        if (!mPresenter.isAttach()){
                            mPresenter.attachView(OldBleLockFragment.this);
                        }
                        if (isCurrentFragment) {
                            LogUtils.e("setBleLockInfo    12   ");
                            mPresenter.setBleLockInfo(bleLockInfo);
                            boolean auth = mPresenter.isAuth(bleLockInfo, true);
                            LogUtils.e("切换到当前界面 12  设备 isdestroy  " + isDestroy+ auth);
                            LogUtils.e(this + "   设置设备2  " + bleLockInfo.getServerLockInfo().toString());
                            mPresenter.getAllPassword(bleLockInfo, false);
                            isCurrentFragment = true;
                            onChangeInitView();
                        }
                    }

                }
            }
        };

        homeFragment.listenerSelect(listener);

        homeFragment.getPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                if (i == position  ) {
                    if (homeFragment.isSelectHome && !isDestroy){
                        mPresenter.attachView(OldBleLockFragment.this);
                        LogUtils.e("setBleLockInfo    13   ");
                        mPresenter.setBleLockInfo(bleLockInfo);
                        LogUtils.e(this + "   设置设备1  " + bleLockInfo.getServerLockInfo().toString());
                        boolean auth = mPresenter.isAuth(bleLockInfo, true);
                        if (auth) {
                            changeOpenLockStatus(8);
                        } else {
                            changeOpenLockStatus(12);
                        }
                        mPresenter.getAllPassword(bleLockInfo, false);
                    }
                    isCurrentFragment = true;
                } else {
                    if (mPresenter.isAttach()) {
                        mPresenter.detachView();
                    }
                    isCurrentFragment = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        LogUtils.e("设备position " + position + "    " + homeFragment.getCurrentPosition() + "     " + homeFragment.isSelectHome);
        if (position == 0 && position == homeFragment.getCurrentPosition() && homeFragment.isSelectHome &&!isDestroy) {
            if (!mPresenter.isAttach() ) {
                mPresenter.attachView(this);
            }

            LogUtils.e("setBleLockInfo    14   ");
            mPresenter.setBleLockInfo(bleLockInfo);
            mPresenter.isAuth(bleLockInfo, true);
            LogUtils.e(this + "  设置设备3  " + bleLockInfo.getServerLockInfo().toString());
            mPresenter.getAllPassword(bleLockInfo, false);
        } else {
            if (mPresenter.isAttach()) {
                mPresenter.detachView();
            }

        }

        if (position == 0 && position == homeFragment.getCurrentPosition()) {
            isCurrentFragment = true;
        } else {
            isCurrentFragment = false;
        }


        rlIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnectingDevice && !bleLockInfo.isAuth() && !isDestroy) {  //如果没有正在连接设备
                    //连接设备
                    mPresenter.attachView(OldBleLockFragment.this);
                    mPresenter.isAuth(bleLockInfo, true);
                    mPresenter.getAllPassword(bleLockInfo, false);

                }
            }
        });

        rlIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isOpening) {
                    LogUtils.e("长按  但是当前正在开锁状态   ");
                    return false;
                }
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    if (bleLockInfo.getBackLock() == 0 || bleLockInfo.getSafeMode() == 1) {  //反锁状态下或者安全模式下  长按不操作
                        if (bleLockInfo.getSafeMode() == 1) {
                            ToastUtil.getInstance().showLong(R.string.safe_mode_can_not_open);
                        } else if (bleLockInfo.getBackLock() == 0) {
                            ToastUtil.getInstance().showLong(R.string.back_lock_can_not_open);
                        }
                        return false;
                    }
                    mPresenter.openLock();
                }
                vibrate(getActivity(), 150);
                return false;
            }
        });
    }

    public void changePage() {
        if (hasData) {
            rlHasData.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            rlHasData.setEnabled(false);
        } else {
            rlHasData.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            rlHasData.setEnabled(true);
        }
    }

    @Override
    protected OldBleLockPresenter<IOldBleLockView> createPresent() {
        return new OldBleLockPresenter<>();
    }

    private void initRecycleView() {
        bluetoothRecordAdapter = new BluetoothRecordAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
        homeFragment.removeListener(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.e("Fragment    onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.e("Fragment    onDetach");
    }

    public void changeOpenLockStatus(int status) {
//        stopOpenLockAnimator();
//        stopCloseLockAnimator();
        if (!isAdded()) {
            return;
        }
        switch (status) {
            case 1:
                //手机蓝牙未打开

                break;
            case 2:
                //搜索门锁蓝牙....
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connecting_inner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_connecting));
                tvInner.setTextColor(getResources().getColor(R.color.c15A6F5));
                tvExternal.setVisibility(View.GONE);
                break;
            case 3:
                //门锁不在有效范围内（一般两米）

                break;
            case 4:
                //“已启动布防，长按开锁“
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.long_press_open_lock));
                tvInner.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.bu_fang_status));
                break;
            case 5:
                //“安全模式”  长按不可APP开锁，提示
                //            ““安全模式，无权限开门””
                break;
            case 6:
                //“已反锁，请门内开锁”
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_double_lock_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.GONE);
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.double_lock_status));
                break;
            case 8:
                //“长按开锁”（表示关闭状态）
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon  );
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(R.string.long_press_open_lock);
                tvInner.setTextColor(getResources().getColor(R.color.cF7FDFD));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.bluetooth_close_status));
                break;
            case 9:
                //”开锁中....“
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_icon);
                ivExternalMiddle.setVisibility(View.VISIBLE);
                ivExternalMiddle.setImageResource(R.mipmap.bluetooth_open_lock_middle_icon);
                ivExternalSmall.setVisibility(View.VISIBLE);
                ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.GONE);
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.is_lock));
                break;
            case 10:
                //“锁已打开”
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_icon);
                ivExternalMiddle.setVisibility(View.VISIBLE);
                ivExternalMiddle.setImageResource(R.mipmap.bluetooth_open_lock_middle_icon);
                ivExternalSmall.setVisibility(View.VISIBLE);
                ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.GONE);
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.open_lock_success));
                break;
            case 11:
                //蓝牙连接成功
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connect_success);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_connect_success));
                tvInner.setTextColor(getResources().getColor(R.color.c15A6F5));
                tvExternal.setVisibility(View.GONE);
                break;
            case 12:
                //蓝牙链接失败
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connect_fail);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_connect_fail));
                tvInner.setTextColor(getResources().getColor(R.color.c15A6F5));
                tvExternal.setVisibility(View.GONE);
                break;
            case 13:
                //手机蓝牙未链接
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_noconnect_inner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_no_connect));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setText(getString(R.string.equipment_out_of_range));
                break;
            case 15:
                //蓝牙锁布防被开启
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_bu_fang_inner_middle_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.GONE);
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.bu_fang_status));
                break;
            case 16:
                //蓝牙锁安全
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_security_big_icon);
                ivExternalMiddle.setVisibility(View.VISIBLE);
                ivExternalMiddle.setImageResource(R.mipmap.bluetooth_safe_external_middle_icon);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.long_press_open_lock));
                tvInner.setTextColor(getResources().getColor(R.color.cFEFEFE));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.safe_status));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_more:
                intent = new Intent(getActivity(), OldBluetoothOpenLockRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_synchronized_record:
                if (isLoadingBleRecord) { //如果正在加载锁上数据  不让用户再次点击
                    ToastUtil.getInstance().showShort(R.string.is_loading_lock_record);
                    return;
                }
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    LogUtils.e("同步开锁记录");
                    mPresenter.syncRecord();
                    list.clear();
                    if (bluetoothRecordAdapter != null) {
                        bluetoothRecordAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }


    /////////////////////////////////////////        回调           //////////////////////////////////////
    @Override
    public void onDeviceStateChange(boolean isConnected) {
        LogUtils.e("连接状态改变   " + isConnected);
        if (isConnected) {

        } else {
            if (!isConnectingDevice) {
                changeOpenLockStatus(12);
            }
        }
    }


    //震动milliseconds毫秒
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }


    @Override
    public void onStartSearchDevice() {
        LogUtils.e("开始搜索   ");
        changeOpenLockStatus(2);
    }

    @Override
    public void onSearchDeviceFailed(Throwable throwable) {
        changeOpenLockStatus(13);
    }

    @Override
    public void onSearchDeviceSuccess() {

    }


    @Override
    public void onNeedRebind(int errorCode) {
        changeOpenLockStatus(11);
    }

    @Override
    public void authResult(boolean isSuccess) {
        if (isSuccess) {
            changeOpenLockStatus(8);
        } else {
            changeOpenLockStatus(12);
        }
    }


    @Override
    public void onElectricUpdata(int power) {

    }

    @Override
    public void onElectricUpdataFailed(Throwable throwable) {
//        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
//        ToastUtil.getInstance().showShort(getText(R.string.not_power_was_received) + " " + throwable.toString());
    }

    @Override
    public void onBleOpenStateChange(boolean isOpen) {
        if (isOpen) {
            changeOpenLockStatus(2);
        } else {
            changeOpenLockStatus(1);
        }
    }

    @Override
    public void onGetOpenNumberSuccess(int number) {

    }

    @Override
    public void onGetOpenNumberFailed(Throwable throwable) { //获取开锁次数失败
//        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
//        ToastUtil.getInstance().showShort(getText(R.string.fail_get_open_lock_number) + " " + throwable.toString());

    }

    @Override
    public void notAdminMustHaveNet() {  //不是管理员开锁必须要有网络
        ToastUtil.getInstance().showLong(R.string.not_admin_must_have_net);
    }

    @Override
    public void inputPwd() {
        View mView = LayoutInflater.from(getActivity()).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(getActivity(), mView);
        tvTitle.setText(getString(R.string.input_open_lock_password));
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
                if (!StringUtil.randomJudge(name)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                mPresenter.realOpenLock(name, false);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void authFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));

    }

    @Override
    public void authServerFailed(BaseResult baseResult) {

    }


    @Override
    public void isOpeningLock() {
        openLockAnimator();
        isOpening = true;
//        changeOpenLockStatus(9);
    }
    AnimationsContainer.FramesSequenceAnimation openLockBig;
    AnimationsContainer.FramesSequenceAnimation openLockMiddle;
    AnimationsContainer.FramesSequenceAnimation openLockSmall;
    AnimationsContainer.FramesSequenceAnimation closeLock;
    public void openLockAnimator() {
        LogUtils.d("davi 开始新1 " + System.currentTimeMillis());
        ivExternalBig.setVisibility(View.VISIBLE);
//        ivExternalBig.setImageResource(R.drawable.open_lock_big);
        //优化后的帧动画
        openLockBig = AnimationsContainer.getInstance(R.array.open_lock_big, 26).createProgressDialogAnim(ivExternalBig);
        ivExternalMiddle.setVisibility(View.VISIBLE);
//        ivExternalMiddle.setImageResource(R.drawable.open_lock_middle);
        openLockMiddle = AnimationsContainer.getInstance(R.array.open_lock_middle, 26).createProgressDialogAnim(ivExternalMiddle);
        ivExternalSmall.setVisibility(View.GONE);
//        ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
        ivInnerMiddle.setVisibility(View.VISIBLE);
//        ivInnerMiddle.setImageResource(R.drawable.open_lock_small);
        openLockSmall = AnimationsContainer.getInstance(R.array.open_lock_small, 26).createProgressDialogAnim(ivInnerMiddle);
        ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
        tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
        tvExternal.setVisibility(View.VISIBLE);
        tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
        tvExternal.setText(getString(R.string.is_lock));


        LogUtils.d("davi 开始新2 " + System.currentTimeMillis());
        if (openLockBig != null) {
            openLockBig.start();
        }
        if (openLockMiddle != null) {
            openLockMiddle.start();
        }
        if (openLockSmall != null) {
            openLockSmall.start();
        }
        LogUtils.d("davi 开始新3 " + System.currentTimeMillis());
    }

    /**
     * 关锁动画
     */
    public void closeLockAnimator() {
        ivExternalBig.setVisibility(View.VISIBLE);
        ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon  );
        ivExternalMiddle.setVisibility(View.GONE);
//        ivExternalMiddle.setImageResource(R.mipmap.);
        ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
        ivInnerMiddle.setVisibility(View.VISIBLE);
//        ivInnerMiddle.setImageResource(R.drawable.bluetooth_lock_close);
        closeLock = AnimationsContainer.getInstance(R.array.bluetooth_lock_close, 14).createProgressDialogAnim(ivInnerMiddle);
        ivInnerSmall.setVisibility(View.VISIBLE);
        ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
        tvInner.setVisibility(View.VISIBLE);
        tvInner.setText(R.string.long_press_open_lock);
        tvInner.setTextColor(getResources().getColor(R.color.cF7FDFD));
        tvExternal.setVisibility(View.VISIBLE);
        tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
        tvExternal.setText(getString(R.string.bluetooth_close_status));
        if (closeLock != null) {
            closeLock.start();
        }
    }

    /**
     * 停止关锁动画
     */
    public void stopCloseLockAnimator() {
        if (closeLock != null) {
            closeLock.stop();
        }
    }

    /**
     * 停止开锁动画
     */
    public void stopOpenLockAnimator() {
        if (openLockBig != null) {
            openLockBig.stop();
        }
        if (openLockMiddle != null) {
            openLockMiddle.stop();
        }
        if (openLockSmall != null) {
            openLockSmall.stop();
        }
    }
    @Override
    public void openLockSuccess() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopOpenLockAnimator();
                changeOpenLockStatus(10);
                handler.removeCallbacks(lockRunnable);
                handler.postDelayed(lockRunnable, 15 * 1000);  //十秒后退出开门状态
            }
        }, 3000);

    }

    @Override
    public void onLockLock() {  //关门
        closeLockAnimator();
        LogUtils.d("davi 关锁 " + System.currentTimeMillis());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("davi 停止关锁 " + System.currentTimeMillis());
                stopCloseLockAnimator();
                handler.removeCallbacks(lockRunnable);
                lockRunnable.run();
            }
        }, 1000);

    }

    @Override
    public void openLockFailed(Throwable throwable) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (throwable instanceof TimeoutException) {
                    ToastUtil.getInstance().showShort(getString(R.string.open_lock_failed));
                } else if (throwable instanceof BleProtocolFailedException) {
                    BleProtocolFailedException bleProtocolFailedException = (BleProtocolFailedException) throwable;
                    ToastUtil.getInstance().showShort(getString(R.string.open_lock_failed));
                } else {
                    ToastUtil.getInstance().showShort(getString(R.string.open_lock_failed));
                }
                lockRunnable.run();
                stopOpenLockAnimator();
            }
        }, 3000);
    }

    @Override
    public void onSafeMode() {
        changeOpenLockStatus(5);
    }

    @Override
    public void onArmMode() {
        changeOpenLockStatus(4);
    }

    @Override
    public void onBackLock() {
        changeOpenLockStatus(6);
    }

    @Override
    public void onWarringUp(int type) {

    }

    private String getOpenLockType(GetPasswordResult passwordResults, OpenLockRecord record) {
        String nickName = record.getUser_num() + "";
        if (passwordResults != null) {
            switch (record.getOpen_type()) {
                case BleUtil.PASSWORD:
                    List<ForeverPassword> pwdList = passwordResults.getData().getPwdList();
                    if (pwdList != null && pwdList.size() > 0) {
                        for (ForeverPassword password : pwdList) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                                nickName = password.getNickName();
                            }
                        }
                    }

                    break;
                case BleUtil.FINGERPRINT:
                    List<GetPasswordResult.DataBean.Fingerprint> fingerprints = passwordResults.getData().getFingerprintList();
                    if (fingerprints != null && fingerprints.size() > 0) {
                        for (GetPasswordResult.DataBean.Fingerprint password : fingerprints) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                                nickName = password.getNickName();
                            }
                        }
                    }

                    break;
                case BleUtil.RFID:  //卡片
                    List<GetPasswordResult.DataBean.Card> cards = passwordResults.getData().getCardList();
                    if (cards != null && cards.size() > 0) {
                        for (GetPasswordResult.DataBean.Card password : cards) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                                nickName = password.getNickName();
                            }
                        }
                    }

                    break;
                case BleUtil.PHONE:  //103
                    nickName = "App";
                    break;
            }
        } else {
            nickName = record.getUser_num() + "";
        }
        return nickName;
    }

    private void groupData(List<OpenLockRecord> lockRecords) {
        if (lockRecords.size() > 0) {
            hasData = true;
            changePage();
        } else {
            hasData = false;
            changePage();
        }
        list.clear();
        long lastDayTime = 0;
        for (int i = 0; i < lockRecords.size(); i++) {
            if (i >= 3) {
                break;
            }
            OpenLockRecord record = lockRecords.get(i);
            //获取开锁时间的毫秒数
            long openTime = DateUtils.standardTimeChangeTimestamp(record.getOpen_time());
            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);  //获取那一天开始的时间戳
            List<BluetoothItemRecordBean> itemList = new ArrayList<>();
            GetPasswordResult passwordResult = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
            String nickName = getOpenLockType(passwordResult, record);

            String open_time = record.getOpen_time();
            String[] split = open_time.split(" ");
            String strRight = split[1];
            String[] split1 = strRight.split(":");
            String time = split1[0] + ":" + split1[1];
            String titleTime = "";
            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(dayTime);
                itemList.add(new BluetoothItemRecordBean(nickName, record.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
                list.add(new BluetoothRecordBean(titleTime, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = list.get(list.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(nickName, record.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
            }
        }

        for (int i = 0; i < list.size(); i++) {
            BluetoothRecordBean bluetoothRecordBean = list.get(i);
            List<BluetoothItemRecordBean> bluetoothRecordBeanList = bluetoothRecordBean.getList();

            for (int j = 0; j < bluetoothRecordBeanList.size(); j++) {
                BluetoothItemRecordBean bluetoothItemRecordBean = bluetoothRecordBeanList.get(j);

                if (j == 0) {
                    bluetoothItemRecordBean.setFirstData(true);
                }
                if (j == bluetoothRecordBeanList.size() - 1) {
                    bluetoothItemRecordBean.setLastData(true);
                }

            }
            if (i == list.size() - 1) {
                bluetoothRecordBean.setLastData(true);
            }


        }
    }

    @Override
    public void onLoseRecord(List<Integer> numbers) {

    }

    @Override
    public void noData() {
        ToastUtil.getInstance().showShort(R.string.lock_no_record);
        hiddenLoading();
        isLoadingBleRecord = false;
    }

    @Override
    public void onLoadBleRecord(List<OpenLockRecord> lockRecords) {
        //获取到蓝牙的开锁记录
        list.clear();
        hiddenLoading();
        groupData(lockRecords);
        bluetoothRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadBleRecordFinish(boolean isComplete) {
        if (isComplete) {
            ToastUtil.getInstance().showShort(R.string.get_record_ble_success);
        } else {
            ToastUtil.getInstance().showShort(R.string.get_record_failed_please_wait);
            hiddenLoading();
        }
        //加载完了   设置正在加载数据
        isLoadingBleRecord = false;
    }

    @Override
    public void onLoadServerRecord(List<OpenLockRecord> lockRecords, int page) {
        LogUtils.e("收到服务器数据  " + lockRecords.size());
        groupData(lockRecords);
        LogUtils.d("davi list " + list.toString());
        bluetoothRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadServerRecordFailed(Throwable throwable) {
        //加载服务器开锁记录失败
//        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result) {
//        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(getActivity(), result.getCode()));
    }

    @Override
    public void onServerNoData() {
        hasData = false;
        changePage();
        //服务器没有开锁记录
//        ToastUtil.getInstance().showShort(R.string.server_no_data);
    }

    @Override
    public void noMoreData() {
//        ToastUtil.getInstance().showShort(R.string.no_more_data);
    }

    @Override
    public void onUploadServerRecordSuccess() {
        LogUtils.e("记录上传成功");
//        ToastUtil.getInstance().showShort(R.string.lock_record_upload_success);
    }

    @Override
    public void onUploadServerRecordFailed(Throwable throwable) {
//        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
        LogUtils.e("记录上传失败");
    }

    @Override
    public void onUploadServerRecordFailedServer(BaseResult result) {
//        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(getActivity(), result.getCode()));
    }

    @Override
    public void startBleRecord() {
        //开始获取蓝牙记录，禁止掉下拉刷新和上拉加载更多
        isLoadingBleRecord = true;
        showLoading(getString(R.string.is_loading_lock_record));
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onStartConnectDevice() {
        isConnectingDevice = true;
    }

    @Override
    public void onEndConnectDevice(boolean isSuccess) {
        isConnectingDevice = false;
        if (!isSuccess) {
            changeOpenLockStatus(12);
        }

    }

    @Override
    public void onGetPasswordSuccess(GetPasswordResult result) {

    }

    @Override
    public void onGetPasswordFailedServer(BaseResult result) {

    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {

    }

    /**
     * 没有权限
     */
    @Override
    public void noPermissions() {
        PermissionUtil.getInstance().requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, getActivity());
        ToastUtil.getInstance().showLong(R.string.please_allow_ble_permission);
        changeOpenLockStatus(12);
    }

    /**
     * 没有打开GPS 提示
     */
    @Override
    public void noOpenGps(){
        ToastUtil.getInstance().showLong(R.string.check_phone_not_open_gps_please_open);
    }

    private void onChangeInitView() {
        if (mPresenter.isAuth(bleLockInfo, true)) {
            LogUtils.e("设备内容更新，  " + bleLockInfo.getBattery());
            LogUtils.e("锁状态改变1   反锁模式  " + bleLockInfo.getBackLock() + "  布防模式   " + bleLockInfo.getArmMode()
                    + "   安全模式  " + bleLockInfo.getSafeMode() + "   管理模式  " + bleLockInfo.getAdminMode()
                    + "   动/自动模式  " + bleLockInfo.getAutoMode());
            changeOpenLockStatus(8);  //鉴权成功之后没有特殊状态
            onElectricUpdata(bleLockInfo.getBattery());
            if (bleLockInfo.getBackLock() == 0) {  //等于0时是反锁状态
                changeOpenLockStatus(6);
            }
            if (bleLockInfo.getSafeMode() == 1) {//安全模式
                changeOpenLockStatus(5);
            }
            if (bleLockInfo.getArmMode() == 1) {//布防模式
                changeOpenLockStatus(4);
            }
        }
    }


}
