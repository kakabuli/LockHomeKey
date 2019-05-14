package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.HomePreseneter;
import com.kaadas.lock.mvp.view.IHomeView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.widget.UnderLineRadioBtn;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by David.
 */

public class HomePageFragment extends BaseFragment<IHomeView, HomePreseneter<IHomeView>> implements IHomeView, View.OnClickListener {

    @BindView(R.id.ll_has_device)
    LinearLayout llHasDevice;
    @BindView(R.id.btn_add_device)
    Button btnAddDevice;
    ArrayList<String> radioButtonList = new ArrayList<>();
    @BindView(R.id.rb_home1)
    UnderLineRadioBtn rbHome1;
    @BindView(R.id.rb_home2)
    UnderLineRadioBtn rbHome2;
    @BindView(R.id.rb_home3)
    UnderLineRadioBtn rbHome3;
    @BindView(R.id.rg_home)
    RadioGroup rgHome;
    @BindView(R.id.vp_home)
    ViewPager viewPager;
    @BindView(R.id.ll_no_device)
    LinearLayout llNoDevice;
    private List<Fragment> fragments = new ArrayList<>();
    private List<Integer> realPositions = new ArrayList<>();  //保存的实际position
    private List<HomeShowBean> devices = new ArrayList<>();
    private Unbinder bind;
    boolean hasDevice = false;//是否有设备  默认没有设备
    private List<ISelectChangeListener> listeners = new ArrayList<>();
    private MainActivity activity;
    private int currentPosition;
    public boolean isSelectHome = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        activity.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    isSelectHome = true;
                    for (ISelectChangeListener listener : listeners) {
                        listener.onSelectChange(true);
                    }
                } else {
                    isSelectHome = false;
                    for (ISelectChangeListener listener : listeners) {
                        listener.onSelectChange(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        bind = ButterKnife.bind(this, view);
        btnAddDevice.setOnClickListener(this);
        hasDevice = false;
        changePage();
        devices = MyApplication.getInstance().getHomeShowDevices();
        initData(devices);
        return view;
    }

    @Override
    protected HomePreseneter<IHomeView> createPresent() {
        return new HomePreseneter<>();
    }

    private void changePage() {
        if (hasDevice) {
            llHasDevice.setVisibility(View.VISIBLE);
            llHasDevice.setClickable(true);
            llNoDevice.setVisibility(View.GONE);
            llNoDevice.setClickable(false);
        } else {
            llHasDevice.setVisibility(View.GONE);
            llHasDevice.setClickable(false);
            llNoDevice.setVisibility(View.VISIBLE);
            llNoDevice.setClickable(true);
        }
    }

    public void initData(final List<HomeShowBean> devices) {
        if (devices == null) {
            hasDevice = false;
            changePage();
            return;
        }
        if (devices.size() == 0) {
            hasDevice = false;
            changePage();
            return;
        }
        hasDevice = true;
        changePage();
        fragments = new ArrayList<>();

        realPositions.clear();
        if (devices.size() == 0) {
            rbHome1.setVisibility(View.GONE);
            rbHome2.setVisibility(View.GONE);
            rbHome3.setVisibility(View.GONE);
        } else if (devices.size() == 1) {
            rbHome1.setVisibility(View.VISIBLE);
            rbHome2.setVisibility(View.GONE);
            rbHome3.setVisibility(View.GONE);
            rbHome1.setText(devices.get(0).getDeviceNickName());
            realPositions.add(0);
            if (devices.get(0).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(0).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }
        } else if (devices.size() == 2) {
            rbHome1.setVisibility(View.VISIBLE);
            rbHome2.setVisibility(View.VISIBLE);
            rbHome3.setVisibility(View.GONE);
            rbHome1.setText(devices.get(0).getDeviceNickName());
            rbHome2.setText(devices.get(1).getDeviceNickName());
            realPositions.add(0);
            realPositions.add(1);
            if (devices.get(0).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(0).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }
            if (devices.get(1).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(1).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }
        } else {
            rbHome1.setVisibility(View.VISIBLE);
            rbHome2.setVisibility(View.VISIBLE);
            rbHome3.setVisibility(View.VISIBLE);
            rbHome1.setText(devices.get(0).getDeviceNickName());
            rbHome2.setText(devices.get(1).getDeviceNickName());
            rbHome3.setText(devices.get(2).getDeviceNickName());
            realPositions.add(0);
            realPositions.add(1);
            realPositions.add(2);

            if (devices.get(0).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(0).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }

            if (devices.get(1).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(1).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }

            if (devices.get(2).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(2).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);

            }
        }

        for (int i = 0; i < devices.size(); i++) {
            //此处初始化Fragment
            switch (devices.get(i).getDeviceType()) {
                case HomeShowBean.TYPE_BLE_LOCK: //蓝牙锁:
                    BleLockInfo bleLockInfo =   (BleLockInfo) devices.get(i).getObject();
                    String bleVersion = bleLockInfo.getServerLockInfo().getBleVersion();
                    boolean isOld = false;
                    if (TextUtils.isEmpty(bleVersion)) {
                        isOld = true;
                    }else {
                        if (! "3".equals(bleVersion)){
                            isOld = true;
                        }
                    }
                    if (isOld){  //是不是老蓝牙模块
                        OldBleLockFragment oldBleLockFragment = new OldBleLockFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KeyConstants.BLE_LOCK_INFO, bleLockInfo);
                        bundle.putSerializable(KeyConstants.FRAGMENT_POSITION, i);
                        oldBleLockFragment.setArguments(bundle);
                        fragments.add(oldBleLockFragment);
                    }else {
                        BleLockFragment bleLockFragment = new BleLockFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KeyConstants.BLE_LOCK_INFO, bleLockInfo);
                        bundle.putSerializable(KeyConstants.FRAGMENT_POSITION, i);
                        bleLockFragment.setArguments(bundle);
                        fragments.add(bleLockFragment);
                    }
                    break;
                case HomeShowBean.TYPE_GATEWAY_LOCK: //网关锁:
                    GatewayLockFragment gatewayLockFragment = new GatewayLockFragment();
                    Bundle gwBundle = new Bundle();
                    gwBundle.putSerializable(KeyConstants.GATEWAY_LOCK_INFO, (GwLockInfo) devices.get(i).getObject());
                    gatewayLockFragment.setArguments(gwBundle);
                    fragments.add(gatewayLockFragment);
                    break;
                case HomeShowBean.TYPE_CAT_EYE: //猫眼:
                    fragments.add(new CatEyeFragment());
                    break;
                case HomeShowBean.TYPE_GATEWAY: //网关
                    fragments.add(new MyFragment());
                    break;
            }
        }


        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        if (devices.size() > 0) {
            viewPager.setCurrentItem(0);
            rbHome1.setChecked(true);
        }

        rbHome1.setScaleY((float) 1);
        rbHome1.setScaleX((float) 1);
        rbHome2.setScaleY((float) 0.85);
        rbHome2.setScaleX((float) 0.85);
        rbHome3.setScaleY((float) 0.85);
        rbHome3.setScaleX((float) 0.85);

        rgHome.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int realPosition = 0;
                switch (checkedId) {
                    case R.id.rb_home1:
                        realPosition = realPositions.get(0);
                        break;
                    case R.id.rb_home2:
                        realPosition = realPositions.get(1);
                        break;
                    case R.id.rb_home3:
                        realPosition = realPositions.get(2);
                        break;
                }
                resetRadioButton(realPosition);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                resetRadioButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void resetRadioButton(int realPosition) {
        int startPosition = 0;
        int endPosition = 0;
        int selectPosition = 0;
        if (realPosition == 0 || realPosition == 1) {  //如果是第一
            startPosition = 0;
            if (realPosition == devices.size() - 1) { //如果是最后一个的话
                endPosition = realPosition;
            } else {
                endPosition = 2;
            }
            selectPosition = realPosition;
        } else {
            startPosition = realPosition - 1;
            selectPosition = 1;
            if (realPosition == devices.size() - 1) {//如果是最后一个的话
                startPosition = realPosition - 2;
                endPosition = realPosition;
                selectPosition = 2;
            } else {
                endPosition = realPosition + 1;
            }
        }

        //设置radioButton的标题
        if (devices.size() > 3) {
            rbHome1.setText(devices.get(startPosition).getDeviceNickName());
            rbHome2.setText(devices.get(startPosition + 1).getDeviceNickName());
            rbHome3.setText(devices.get(endPosition).getDeviceNickName());
            realPositions.clear();
            for (int i = startPosition; i <= endPosition; i++) {
                realPositions.add(i);
            }
            if (devices.get(startPosition).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(startPosition).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }

            if (devices.get(startPosition + 1).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(startPosition + 1).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
            }

            if (devices.get(endPosition).getDeviceType() == HomeShowBean.TYPE_BLE_LOCK
                    || devices.get(endPosition).getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK
                    ) {
                rbHome3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
            } else {
                rbHome3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getActivity().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);

            }
        }
        //
        RadioButton radioButton = (RadioButton) rgHome.getChildAt(selectPosition);
        radioButton.setChecked(true);
        viewPager.setCurrentItem(realPosition);
        currentPosition = realPosition;
        rbHome1.setScaleY((float) 0.85);
        rbHome1.setScaleX((float) 0.85);
        rbHome2.setScaleY((float) 0.85);
        rbHome2.setScaleX((float) 0.85);
        rbHome3.setScaleY((float) 0.85);
        rbHome3.setScaleX((float) 0.85);
        if (selectPosition == 0) {
            rbHome1.setScaleY((float) 1);
            rbHome1.setScaleX((float) 1);
        } else if (selectPosition == 1) {
            rbHome2.setScaleY((float) 1);
            rbHome2.setScaleX((float) 1);
        } else if (selectPosition == 2) {
            rbHome3.setScaleY((float) 1);
            rbHome3.setScaleX((float) 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }


    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        devices = MyApplication.getInstance().getHomeShowDevices();
        LogUtils.e("首页  设备个数是   " + devices.size());
        initData(devices);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_add_device:
                intent = new Intent(getActivity(), DeviceAddActivity.class);
                startActivity(intent);
                break;
        }
    }

    public ViewPager getPager() {
        return viewPager;
    }

    public void listenerSelect(ISelectChangeListener listener) {
        listeners.add(listener);
    }

    public interface ISelectChangeListener {
        void onSelectChange(boolean isSelect);
    }


    public int getCurrentPosition() {
        return currentPosition;
    }
}
