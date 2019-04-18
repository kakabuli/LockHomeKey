package com.kaadas.lock.activity.addDevice.bluetooth;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.adapter.DeviceSearchAdapter;
import com.kaadas.lock.adapter.inf.OnBindClickListener;
import com.kaadas.lock.utils.AlertDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothSearchActivity extends AppCompatActivity implements OnBindClickListener {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.recycler_layout)
    LinearLayout recyclerLayout;
    @BindView(R.id.research)
    Button research;
    @BindView(R.id.device_add_search)
    ImageView deviceAddSearch;
    @BindView(R.id.tv_is_searching)
    TextView tvIsSearching;

    private Animation operatingAnim;
    private DeviceSearchAdapter deviceSearchAdapter;

    private TranslateAnimation translateAnimation;
    private DividerItemDecoration dividerItemDecoration;
    private List<BluetoothDevice> mList;
    private ObjectAnimator traslateAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_search);
        ButterKnife.bind(this);

        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        initAnimation();
    }

    private void initView() {
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        searchRecycler.addItemDecoration(dividerItemDecoration);

    }

    //当有搜索到蓝牙设备时，显示recycler和重新搜索按钮。
    private void showRecycler(Boolean flag) {
        if (flag) {
            recyclerLayout.setVisibility(View.VISIBLE);
            research.setVisibility(View.VISIBLE);
        } else {
            recyclerLayout.setVisibility(View.GONE);
            research.setVisibility(View.GONE);
        }
    }

    //当没有搜索到蓝牙设备时，显示对话框。
    private void showDialog(Boolean isNoData) {
        if (isNoData) {
            AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(this, getResources().getString(R.string.no_find_connect_device), getResources().getString(R.string.cancel), getResources().getString(R.string.rescan), new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {
                }

                @Override
                public void right() {
                    //重新搜索设备
                }
            });
        }
    }


    private void initAnimation() {
        Path path = new Path();
        RectF rectF = new RectF(deviceAddSearch.getLeft(), deviceAddSearch.getTop(), deviceAddSearch.getRight(), deviceAddSearch.getBottom());
        path.addOval(rectF, Path.Direction.CW);
        traslateAnimator = ObjectAnimator.ofFloat(deviceAddSearch, "x", "y", path);
        traslateAnimator.start();
    }


    private void initData() {
        mList = new ArrayList<>();
        deviceSearchAdapter = new DeviceSearchAdapter(mList);
        searchRecycler.setAdapter(deviceSearchAdapter);
        showRecycler(true);
        if (deviceSearchAdapter != null) {
            deviceSearchAdapter.setBindClickListener(this);
        }

    }

    /**
     * 启动搜索图片的动画
     */
    private void startAnimation() {
        if (operatingAnim != null) {
            deviceAddSearch.startAnimation(operatingAnim);
        }
    }

    /**
     * 停止搜索图片
     *
     * @param
     */
    private void stopAnimation() {
        deviceAddSearch.clearAnimation();
    }


    @OnClick({R.id.back, R.id.help, R.id.research})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent helpIntent = new Intent(this, DeviceAddHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.research:
                initAnimation();


                break;
        }
    }


    @Override
    public void onItemClickListener(View view, int position,BluetoothDevice device) {
        //添加设备

        Intent secondIntent = new Intent(this, AddBluetoothSecondActivity.class);
        startActivity(secondIntent);
    }
}
