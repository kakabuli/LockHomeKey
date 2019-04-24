package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.home.CateyeEquipmentDynamicActivity;
import com.kaadas.lock.activity.home.GatewayEquipmentDynamicActivity;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatEyeFragment extends Fragment implements View.OnClickListener {

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
    @BindView(R.id.rl_device_dynamic)
    RelativeLayout rlDeviceDynamic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cat_eye_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        changeOpenLockStatus(1);
        tvMore.setOnClickListener(this);
        rlDeviceDynamic.setOnClickListener(this);
        return view;
    }

    private void initRecycleView() {
        List<BluetoothItemRecordBean> itemList1 = new ArrayList<>();
        itemList1.add(new BluetoothItemRecordBean("jff","jfj", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList1, false));
        List<BluetoothItemRecordBean> itemList2 = new ArrayList<>();
        itemList2.add(new BluetoothItemRecordBean("jff","jfji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, false));
        itemList2.add(new BluetoothItemRecordBean("jff","jfji", KeyConstants.BLUETOOTH_RECORD_COMMON, "fjjf", false, false));
        itemList2.add(new BluetoothItemRecordBean("jff","Jfjif", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", false, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList2, false));
        BluetoothRecordAdapter bluetoothRecordAdapter = new BluetoothRecordAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void changeOpenLockStatus(int status) {

      /*  在线：“点击，查看门外”

        离线：“设备已离线”*/


        switch (status) {
            case 1:
                //猫眼在线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.cateye_online);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.click_outside_door));
                tvInner.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.cateye_online));
                break;
            case 2:
//                设备已离线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.cateye_offline);
                ivInnerSmall.setVisibility(View.INVISIBLE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.device_has_offline));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.cateye_offline));
                break;


        }
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_device_dynamic:
                intent = new Intent(getActivity(), CateyeEquipmentDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_more:
                intent = new Intent(getActivity(), CateyeEquipmentDynamicActivity.class);
                startActivity(intent);
                break;
        }
    }
}
