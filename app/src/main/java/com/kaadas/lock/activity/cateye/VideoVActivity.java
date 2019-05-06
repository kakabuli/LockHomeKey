package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.ForecastAdapter;
import com.kaadas.lock.bean.Forecast;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.bean.Weather;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoVActivity extends AppCompatActivity implements
        DiscreteScrollView.ScrollStateChangeListener<ForecastAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ForecastAdapter.ViewHolder>,
        View.OnClickListener {


    @BindView(R.id.forecast_city_picker)
    DiscreteScrollView cityPicker;
    @BindView(R.id.video_h_no_lock)
    LinearLayout video_h_no_lock;
    @BindView(R.id.video_start_play)
    ImageView video_start_play;
    @BindView(R.id.video_connecting_tv)
    TextView video_connecting_tv;
    @BindView(R.id.video_hang_up)
    ImageView video_hang_up;
    @BindView(R.id.video_h_footer)
    LinearLayout video_h_footer;
    @BindView(R.id.video_play_time)
    TextView video_play_time;
    @BindView(R.id.video_v_go)
    ImageView video_v_go;
    @BindView(R.id.iv_back)
    ImageView iv_back;


    @BindView(R.id.video_v_surfaceview)
    View video_v_surfaceview;

    ForecastAdapter forecastAdapter = null;
    private int selectPostion = -1;

    Handler handler = new Handler();
    private CateEyeInfo cateEyeInfo;
    private boolean isCallIn;
    private List<GwLockInfo> gwLockInfos;
    private static final int REQUEST_CODE_CALL_COMING = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_v);
        ButterKnife.bind(this);

        initData();
        initView();

    }

    private void initData() {
        cateEyeInfo = (CateEyeInfo) getIntent().getSerializableExtra(KeyConstants.CATE_INFO);
        isCallIn = (boolean) getIntent().getSerializableExtra(KeyConstants.IS_CALL_IN);

        String gwID = cateEyeInfo.getGwID();
        List<HomeShowBean> homeShowDevices = MyApplication.getInstance().getHomeShowDevices();
        gwLockInfos = new ArrayList<>();
        //获取跟猫眼通一网关下的锁
        for (HomeShowBean homeShowBean : homeShowDevices) {
            if (homeShowBean.getDeviceType() == HomeShowBean.TYPE_GATEWAY_LOCK) {
                GwLockInfo gwLockInfo = (GwLockInfo) homeShowBean.getObject();
                if (gwLockInfo.getGwID().equals(gwID)) {
                    gwLockInfos.add(gwLockInfo);
                }
            }
        }
        if (isCallIn) {
            Intent intent = new Intent(this,CallComingActivity.class);
            startActivityForResult(intent,REQUEST_CODE_CALL_COMING);
        } else { //此处呼叫出去的逻辑
            callCatEye();
        }
    }


    //呼叫猫眼的逻辑
    private void callCatEye(){



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CALL_COMING){ //来电界面回调
          boolean isAcceptCall =   data.getBooleanExtra(KeyConstants.IS_ACCEPT_CALL, false);
          if (isAcceptCall){  //接听
              LogUtils.e("接听了电话");
          }else { //挂断
              LogUtils.e("挂断了电话");
              finish();
          }
        }
    }

    private void initView() {
        video_start_play.setOnClickListener(this);
        video_v_go.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        cityPicker.setSlideOnFling(true);
        forecastAdapter = new ForecastAdapter(gwLockInfos, this);
        cityPicker.setAdapter(forecastAdapter);
        cityPicker.addOnItemChangedListener(this);
        cityPicker.addScrollStateChangeListener(this);
        cityPicker.scrollToPosition(1);
        cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        cityPicker.setOffscreenItems(300);
        cityPicker.setOverScrollEnabled(false);


        forecastAdapter.setOnItemClickItem(new ForecastAdapter.OnItemClickItem() {
            @Override
            public void onItemClickItemMethod(int position) {
                if (selectPostion != -1 && position == selectPostion) {
                    Toast.makeText(VideoVActivity.this, "点击了:" + position, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (gwLockInfos.size() == 0) {
            cityPicker.setVisibility(View.GONE);
            video_h_no_lock.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable ForecastAdapter.ViewHolder holder, int position) {
        //viewHolder will never be null, because we never remove items from adapter's list
        Log.e(Tag, "onCurrentItemChanged=======>:" + position);
        selectPostion = position;
        if (holder != null) {
            holder.showText();
        }
    }

    String Tag = "denganzhi1";

    @Override
    public void onScrollStart(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        Log.e(Tag, "onScrollStart=======>");
        holder.hideText();
    }

    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ForecastAdapter.ViewHolder currentHolder,
            @Nullable ForecastAdapter.ViewHolder newHolder) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_start_play:
                video_start_play.setVisibility(View.GONE);
                video_connecting_tv.setVisibility(View.VISIBLE);
                video_hang_up.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        video_connecting_tv.setVisibility(View.GONE);
                        video_hang_up.setVisibility(View.GONE);
                        video_play_time.setVisibility(View.VISIBLE);
                        video_h_footer.setVisibility(View.VISIBLE);
                        video_v_surfaceview.setVisibility(View.VISIBLE);
                    }
                }, 3000);
                break;
            case R.id.video_v_go:
                Intent intent = new Intent(VideoVActivity.this, VideoHActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onScrollEnd(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        Log.e(Tag, "onScrollEnd=======>");
    }
}
