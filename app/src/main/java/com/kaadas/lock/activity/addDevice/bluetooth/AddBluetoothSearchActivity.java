package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.adapter.DeviceAddItemAdapter;
import com.kaadas.lock.adapter.DeviceSearchAdapter;
import com.kaadas.lock.adapter.inf.OnBindClickListener;

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

    private Animation operatingAnim;
    private DeviceSearchAdapter deviceSearchAdapter;

    private TranslateAnimation translateAnimation;
    private DividerItemDecoration dividerItemDecoration;
    private List<String> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_search);
        ButterKnife.bind(this);
        initAnimation();
        initView();
        initData();
    }

    private void initView() {
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        searchRecycler.addItemDecoration(dividerItemDecoration);

    }
    private void showRecycler(Boolean flag){
        if (flag){
            recyclerLayout.setVisibility(View.VISIBLE);
            research.setVisibility(View.VISIBLE);
        }else{
            recyclerLayout.setVisibility(View.GONE);
            research.setVisibility(View.GONE);
        }
    }



    private void initAnimation() {
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.device_search);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        startAnimation();

    }


    private void initData() {
            mList=new ArrayList<>();
            mList.add("XK113213");
            mList.add("XK113213");
            mList.add("XK113213");
            mList.add("XK113213");
            mList.add("XK113213");
            mList.add("XK113213");
            deviceSearchAdapter=new DeviceSearchAdapter(mList);
            searchRecycler.setAdapter(deviceSearchAdapter);
            showRecycler(true);
            if (deviceSearchAdapter!=null){
                deviceSearchAdapter.setBindClickListener(this);
            }

    }

    /**
     * 启动搜索图片的动画
     */
    private void startAnimation() {
        if (operatingAnim != null) {
            deviceAddSearch.startAnimation(operatingAnim);
        } else {
            deviceAddSearch.setAnimation(operatingAnim);
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
                break;
        }
    }


    @Override
    public void onItemClickListener(View view,int position) {
        Intent secondIntent=new Intent(this,AddBluetoothSecondActivity.class);
        startActivity(secondIntent);
    }
}
