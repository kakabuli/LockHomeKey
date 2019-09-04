package com.kaadas.lock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.widget.UnderLineRadioBtn;

import java.util.ArrayList;
import java.util.Arrays;
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

    @BindView(R.id.rg_home)
    RadioGroup mRadioGroup;
    @BindView(R.id.vp_home)
    ViewPager viewPager;
    @BindView(R.id.ll_no_device)
    LinearLayout llNoDevice;
    @BindView(R.id.sc_title)
    HorizontalScrollView scTitle;
    private List<Fragment> fragments = new ArrayList<>();
    private List<Integer> realPositions = new ArrayList<>();  //保存的实际position
    private List<HomeShowBean> devices = new ArrayList<>();
    private Unbinder bind;
    boolean hasDevice = false;//是否有设备  默认没有设备
    private List<ISelectChangeListener> listeners = new ArrayList<>();
    private MainActivity activity;
    public boolean isSelectHome = true;
    private FragmentPagerAdapter adapter;

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
        initView();
        initData(devices);
        getScrollViewWidth();
        return view;
    }

    private void initView() {
//        viewPager.setOffscreenPageLimit(3);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int RadiobuttonId = group.getCheckedRadioButtonId();
                //获取radiobutton对象
                RadioButton bt = (RadioButton) group.findViewById(RadiobuttonId);
                //获取单个对象中的位置
                int index = group.indexOfChild(bt);
                //设置滚动位置，可使点击radiobutton时，将该radiobutton移动至第二位置
                scTitle.smoothScrollTo(bt.getLeft() - (int) (scTitle.getWidth() / 3), 0);

                //根据点击的radiobutton跳转至不同webview界面
                if (viewPager.getCurrentItem()  != index){
                    LogUtils.e("位置改变 1 getCurrentItem " + viewPager.getCurrentItem() + "  index  "+index);
                    viewPager.setCurrentItem(index);
                }else{
                    LogUtils.e("位置改变 2  不需要设置  getCurrentItem " + viewPager.getCurrentItem() + "  index  "+index);
                }

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(position);
                radioButton.setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void getScrollViewWidth() {
        scTitle.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (scTitle != null && scTitle.getWidth() > 0) {
                            int childCount = mRadioGroup.getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(i);
                                if (childCount > 2) {
                                    radioButton.setLayoutParams(new LinearLayout.LayoutParams((scTitle.getWidth() / 3), LinearLayout.LayoutParams.WRAP_CONTENT));
                                } else {
                                    radioButton.setLayoutParams(new LinearLayout.LayoutParams((scTitle.getWidth() / childCount), LinearLayout.LayoutParams.WRAP_CONTENT));
                                }
                            }
                        }
                    }
                });
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

    public synchronized void initData(final List<HomeShowBean> devices) {
        if (devices == null) {
            hasDevice = false;
            changePage();
            return;
        }

        hasDevice = true;
        changePage();

        realPositions.clear();

        if (fragments != null) {
            fragments.clear();
        }
        mRadioGroup.removeAllViews();
        //parentItemArr为商品类别对象集合
        for (int i = 0; i < devices.size(); i++) {
            //添加radiobutton及设置参数(方便动态加载radiobutton)
            UnderLineRadioBtn rb = new UnderLineRadioBtn(getContext());
            //根据下标获取商品类别对象
            HomeShowBean homeShowBean = devices.get(i);

            rb.setText(homeShowBean.getDeviceNickName());
            rb.setTextSize(13);
            rb.setGravity(Gravity.CENTER);
            //设置图片   根据类型不同显示不同的图片
            switch (homeShowBean.getDeviceType()) {
                case HomeShowBean.TYPE_BLE_LOCK:
                    rb.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getContext().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
                    BleLockInfo bleLockInfo = (BleLockInfo) devices.get(i).getObject();
                    String bleVersion = bleLockInfo.getServerLockInfo().getBleVersion();
                    boolean isOld = false;
                    if (TextUtils.isEmpty(bleVersion)) {
                        isOld = true;
                    } else {
                        if ("3".equals(bleVersion)  ) {
                            String functionSet = bleLockInfo.getServerLockInfo().getFunctionSet();
                            if ("0".equals(functionSet)){
                                isOld = true;
                            }
                        }else {
                            isOld = true;
                        }
                    }
                    if (isOld) {  //是不是老蓝牙模块
                        OldBleLockFragment oldBleLockFragment = new OldBleLockFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KeyConstants.BLE_LOCK_INFO, bleLockInfo);
                        bundle.putSerializable(KeyConstants.FRAGMENT_POSITION, i);
                        oldBleLockFragment.setArguments(bundle);
                        fragments.add(oldBleLockFragment);
                    } else {
                        BleLockFragment bleLockFragment = new BleLockFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KeyConstants.BLE_LOCK_INFO, bleLockInfo);
                        bundle.putSerializable(KeyConstants.FRAGMENT_POSITION, i);
                        bleLockFragment.setArguments(bundle);
                        fragments.add(bleLockFragment);
                    }
                    break;
                case HomeShowBean.TYPE_CAT_EYE:
                    rb.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getContext().getDrawable(R.drawable.home_rb_cat_eye_drawable), null, null);
                    CatEyeFragment catEyeFragment = new CatEyeFragment();
                    Bundle catEyeBundle = new Bundle();
                    catEyeBundle.putSerializable(KeyConstants.CATE_INFO, (CateEyeInfo) devices.get(i).getObject());
                    catEyeFragment.setArguments(catEyeBundle);
                    fragments.add(catEyeFragment);
                    break;
                case HomeShowBean.TYPE_GATEWAY_LOCK:
                    rb.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getContext().getDrawable(R.drawable.home_rb_lock_drawable), null, null);
                    GatewayLockFragment gatewayLockFragment = new GatewayLockFragment();
                    Bundle gwBundle = new Bundle();
                    gwBundle.putSerializable(KeyConstants.GATEWAY_LOCK_INFO, (GwLockInfo) devices.get(i).getObject());
                    gatewayLockFragment.setArguments(gwBundle);
                    fragments.add(gatewayLockFragment);
                    break;
            }
            //根据需要设置显示初始标签的个数，这里显示3个
            if (devices.size() > 2) {
                rb.setLayoutParams(new LinearLayout.LayoutParams((scTitle.getWidth() / 3), LinearLayout.LayoutParams.WRAP_CONTENT));
            } else {
                rb.setLayoutParams(new LinearLayout.LayoutParams((scTitle.getWidth() / devices.size()), LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            //设置背景为透明
            rb.setBackgroundResource(R.color.color_trans);
            //设置文字超过范围显示....
            rb.setLines(1);
//            rb.setSingleLine(true);
            rb.setEllipsize(TextUtils.TruncateAt.END);
            rb.setLineRadius(0);
            //设置下划线的高度
            rb.setLineHeight_ulb(4);
            //设置图片和文字之间的距离
            rb.setCompoundDrawablePadding(12);
            //设置边距
            rb.setPadding(0, 10, 0, 10);
            //**原生radiobutton是有小圆点的，要去掉圆点而且最好按以下设置，设置为null的话在4.x.x版本上依然会出现**
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            //向radiogroup中添加radiobutton
            mRadioGroup.addView(rb);
        }

        LogUtils.e("首页Fragment数据是   " + Arrays.toString(fragments.toArray()));
        if (adapter == null) {
            adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int i) {
                    return fragments.get(i);
                }

                @Override
                public long getItemId(int position) {
                    int hashCode = fragments.get(position).hashCode();
                    return hashCode;
                }

                @Override
                public int getItemPosition(@NonNull Object object) {
                    return POSITION_NONE;
                }

                @Override
                public int getCount() {
                    return fragments.size();
                }
            };
            viewPager.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
            LogUtils.e("首页Fragment数据是     adapter.notifyDataSetChanged();");
        }
        if (devices.size() == 0) {
            hasDevice = false;
            changePage();
            return;
        }


        UnderLineRadioBtn radioBtn = (UnderLineRadioBtn) mRadioGroup.getChildAt(0);
        radioBtn.setChecked(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }


    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        devices = MyApplication.getInstance().getHomeShowDevices();
        LogUtils.e("首页  设备个数是    " + devices.size());
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

    public void removeListener(ISelectChangeListener listener) {
        listeners.remove(listener);
    }

    public interface ISelectChangeListener {
        void onSelectChange(boolean isSelect);
    }


    public int getCurrentPosition() {
        if (viewPager == null){
            return 0;
        }
        return viewPager.getCurrentItem();
    }
}
