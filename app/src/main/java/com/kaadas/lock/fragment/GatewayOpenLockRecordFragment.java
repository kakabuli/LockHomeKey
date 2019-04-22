package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.utils.KeyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/22
 */
public class GatewayOpenLockRecordFragment extends Fragment {
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    List<BluetoothRecordBean> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_gateway_open_lock_record, null);
        ButterKnife.bind(this, view);
        initRecycleView();
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
        list.add(new BluetoothRecordBean("jfjfk", itemList2, false));
        List<BluetoothItemRecordBean> itemList3 = new ArrayList<>();
        itemList3.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, false));
        itemList3.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_COMMON, "fjjf", false, false));
        itemList3.add(new BluetoothItemRecordBean("jff", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", false, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList3, true));
        BluetoothRecordAdapter bluetoothRecordAdapter = new BluetoothRecordAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
