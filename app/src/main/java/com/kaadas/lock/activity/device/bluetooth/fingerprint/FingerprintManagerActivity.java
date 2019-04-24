package com.kaadas.lock.activity.device.bluetooth.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.FingerprintManagerAdapter;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class FingerprintManagerActivity extends AppCompatActivity
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    FingerprintManagerAdapter fingerprintManagerAdapter;
    boolean isNotData = true;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;
    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    List<GetPasswordResult.DataBean.Fingerprint> list = new ArrayList<>();
    private BleLockInfo bleLockInfo;
    private boolean isSync = false; //是不是正在同步锁中的指纹


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manager);
        ButterKnife.bind(this);
        tvContent.setText(getString(R.string.fingerprint));
        ivBack.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        llAdd.setOnClickListener(this);
        pageChange();
        initRecycleview();
        list.add(new GetPasswordResult.DataBean.Fingerprint("fff", "fff", 1));
        initData();
    }

    private void initRecycleview() {
        fingerprintManagerAdapter = new FingerprintManagerAdapter(list, R.layout.item_fingerprint_manager);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(fingerprintManagerAdapter);
        fingerprintManagerAdapter.setOnItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, FingerprintManagerDetailActivity.class);
        intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
        intent.putExtra(KeyConstants.PASSWORD_NICK, list.get(position));
        startActivity(intent);
    }

    public void pageChange() {

        if (isNotData) {
            llHasData.setVisibility(View.GONE);
            tvNoUser.setVisibility(View.VISIBLE);
        } else {
            llHasData.setVisibility(View.VISIBLE);
            tvNoUser.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_add:
                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtil.getInstance().showShort(R.string.please_add_finger);
                    return;
                }
//                intent = new Intent(this, FingerprintLinkBluetoothActivity.class);
//                intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
//                startActivity(intent);
                break;

            case R.id.tv_synchronized_record:
                //同步
                if (isSync) {
                    ToastUtil.getInstance().showShort(R.string.is_sync_please_wait);
                } else {
                    //TODO 同步
                }
                break;
        }
    }

    public void initData() {
        if (list.size() > 0) {
            isNotData = false;
        } else {
            isNotData = true;
        }
        pageChange();
        fingerprintManagerAdapter.notifyDataSetChanged();
    }
}
