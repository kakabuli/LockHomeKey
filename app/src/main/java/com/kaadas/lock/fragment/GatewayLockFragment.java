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
import com.kaadas.lock.activity.home.BluetoothEquipmentDynamicActivity;
import com.kaadas.lock.activity.home.GatewayEquipmentDynamicActivity;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.utils.KeyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GatewayLockFragment extends Fragment implements View.OnClickListener {


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
        View view = inflater.inflate(R.layout.fragment_gateway_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        changeOpenLockStatus(1);
        tvMore.setOnClickListener(this);
        rlDeviceDynamic.setOnClickListener(this);
        return view;
    }

    private void initRecycleView() {
        List<BluetoothItemRecordBean> itemList1 = new ArrayList<>();
        itemList1.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList1, false));
        List<BluetoothItemRecordBean> itemList2 = new ArrayList<>();
        itemList2.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, false));
        itemList2.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_COMMON, "fjjf", false, false));
        itemList2.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", false, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList2, true));
        BluetoothRecordAdapter bluetoothRecordAdapter = new BluetoothRecordAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void changeOpenLockStatus(int status) {

/*        状态1.WiFi不在线
        状态（推拉）2： “已启动布防，长按开锁“

        状态3 ：“安全模式”  长按不可APP开锁，提示

            ““安全模式，无权限开门””

        状态（推拉）4：“已反锁，请门内开锁”

        状态5：“长按开锁”（表示关闭状态）

        状态6：”开锁中....“

        状态7：“锁已打开”.*/


        switch (status) {
            case 1:
                //WiFi不在线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.long_press_open_lock));
                tvInner.setTextColor(getResources().getColor(R.color.cF7FDFD));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getText(R.string.bluetooth_close_status));
                break;
            case 2:
                //“已启动布防，长按开锁“
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.wifi_not_online);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_not_online));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 3:
//                “安全模式”  长按不可APP开锁，提示
//            ““安全模式，无权限开门””
                break;
            case 4:
//                “已反锁，请门内开锁”
                break;
            case 5:
//                “长按开锁”（表示关闭状态）
                break;
            case 6:
//                ”开锁中....“
                break;
            case 7:
//                “锁已打开”.
                break;
            case 8:
                //WiFi连接中
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.wifi_connecting);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_connecting));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 9:
                //WiFi连接成功
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connect_success);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_connect_success));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 10:
//                WiFi连接失败
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.wifi_connect_fail);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_connect_fail));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;


        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_device_dynamic:
                intent = new Intent(getActivity(), GatewayEquipmentDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_more:
                intent = new Intent(getActivity(), GatewayEquipmentDynamicActivity.class);
                startActivity(intent);
                break;
        }
    }
}
