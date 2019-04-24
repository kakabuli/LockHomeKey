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
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BleLockFragment extends Fragment implements View.OnClickListener {

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
    @BindView(R.id.rl_device_dynamic)
    RelativeLayout rlDeviceDynamic;
    @BindView(R.id.tv_more)
    TextView tvMore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        changeOpenLockStatus(16);
        rlDeviceDynamic.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        return view;
    }

    private void initRecycleView() {
        List<BluetoothItemRecordBean> itemList1 = new ArrayList<>();
        itemList1.add(new BluetoothItemRecordBean("jff","jfjji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList1, false));
        List<BluetoothItemRecordBean> itemList2 = new ArrayList<>();
        itemList2.add(new BluetoothItemRecordBean("jff","jfji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, false));
        itemList2.add(new BluetoothItemRecordBean("jff","jfji", KeyConstants.BLUETOOTH_RECORD_COMMON, "fjjf", false, false));
        itemList2.add(new BluetoothItemRecordBean("jff","jfji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", false, true));
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
/*      状态及文案显示： 关锁状态--开启中--锁已打开

        状态1：手机蓝牙未打开

        状态2：搜索门锁蓝牙....

        状态.3：门锁不在有效范围内（一般两米）

        状态（推拉）4： “已启动布防，长按开锁“

        状态5 ：“安全模式”  长按不可APP开锁，提示

            ““安全模式，无权限开门””

        状态（推拉）6：“已反锁，请门内开锁”

        状态8：“长按开锁”（表示关闭状态）

        状态9：”开锁中....“

        状态10：“锁已打开”.*/

        switch (status) {
            case 1:
                //手机蓝牙未打开

                break;
            case 2:
                //搜索门锁蓝牙....
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connecting_inner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_connecting));
                tvInner.setTextColor(getResources().getColor(R.color.c15A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 3:
                //门锁不在有效范围内（一般两米）

                break;
            case 4:
                //“已启动布防，长按开锁“
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
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
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.double_lock_status));
                break;
            case 8:
                //“长按开锁”（表示关闭状态）
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.VISIBLE);
                ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
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
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
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
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.open_lock_success));
                break;
            case 11:
                //蓝牙连接成功
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
                tvInner.setText(getString(R.string.bluetooth_connect_success));
                tvInner.setTextColor(getResources().getColor(R.color.c15A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 12:
                //蓝牙链接失败
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connect_fail);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_connect_fail));
                tvInner.setTextColor(getResources().getColor(R.color.c15A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 13:
                //手机蓝牙未链接
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_noconnect_inner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.bluetooth_no_connect));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setText(getString(R.string.equipment_out_of_range));
                break;
    /*        case 14:
                //蓝牙锁关闭
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
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
                break;*/
            case 15:
                //蓝牙锁布防被开启
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_bu_fang_inner_middle_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.bluetooth_lock_bu_fang_inner_small_icon);
                tvInner.setVisibility(View.GONE);
//                tvInner.setText(getString();
//                tvInner.setTextColor();
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
//                ivExternalSmall.setImageResource();
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
            case R.id.rl_device_dynamic:
                intent = new Intent(getActivity(), BluetoothEquipmentDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_more:

                intent = new Intent(getActivity(), BluetoothEquipmentDynamicActivity.class);
                startActivity(intent);
                break;
        }
    }
}
