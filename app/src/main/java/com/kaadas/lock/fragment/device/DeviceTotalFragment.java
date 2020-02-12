package com.kaadas.lock.fragment.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by asqw1 on 2018/3/14.
 */

public class DeviceTotalFragment extends Fragment {
    @BindView(R.id.rb_device)
    RadioButton rbDevice;
    @BindView(R.id.rv_scene)
    RadioButton rvScene;
    @BindView(R.id.rg_device)
    RadioGroup rgDevice;
    @BindView(R.id.vp_device)
    NoScrollViewPager vpDevice;
    Unbinder unbinder;
    private View mView;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_device_total, container, false);
        }
        unbinder = ButterKnife.bind(this, mView);
        fragments.add(new DeviceFragment());
        fragments.add(new SceneFragment());
        rbDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (compoundButton.getId()){
                    case R.id.rb_device:
                        vpDevice.setCurrentItem(0);
                        break;
                    case R.id.rv_scene:
                        vpDevice.setCurrentItem(1);
                        break;
                }
            }
        });
        vpDevice.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }
        });
        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.rb_device, R.id.rv_scene, R.id.rg_device, R.id.vp_device, R.id.iv_add_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_device:
                rgDevice.check(R.id.rb_device);
                break;
            case R.id.rv_scene:
                rgDevice.check(R.id.rv_scene);
                break;
            case R.id.rg_device:
                break;
            case R.id.vp_device:
                break;
            case R.id.iv_add_device:
                if (vpDevice.getCurrentItem() == 0){
                    //添加设备
                    startActivity(new Intent(getContext(),DeviceAdd2Activity.class));
                }else if (vpDevice.getCurrentItem() == 1){
                    //添加场景
                }
                break;
        }
    }
}
