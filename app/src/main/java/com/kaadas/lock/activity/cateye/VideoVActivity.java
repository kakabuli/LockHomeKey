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
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.ForecastAdapter;
import com.kaadas.lock.bean.Forecast;
import com.kaadas.lock.bean.Weather;
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
        View.OnClickListener{

    private List<Forecast> forecasts=new ArrayList<>();

    @BindView(R.id.forecast_city_picker)
    DiscreteScrollView cityPicker;
    @BindView(R.id.video_h_no_lock)
    LinearLayout  video_h_no_lock;
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

    ForecastAdapter forecastAdapter=null;
    int selectPostion=-1;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_v);
        ButterKnife.bind(this);

        video_start_play.setOnClickListener(this);
        video_v_go.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        forecasts = Arrays.asList(
                new Forecast("卧室门锁", R.mipmap.lock_on_select, "16", Weather.PARTLY_CLOUDY),
                new Forecast("卧室门锁", R.mipmap.lock_on_select, "14", Weather.CLEAR),
                new Forecast("卧室门锁", R.mipmap.lock_on_select, "9", Weather.MOSTLY_CLOUDY),
                new Forecast("卧室门锁", R.mipmap.lock_on_select, "18", Weather.PARTLY_CLOUDY),
                new Forecast("卧室门锁", R.mipmap.lock_on_select, "6", Weather.PERIODIC_CLOUDS),
                new Forecast("卧室门锁", R.mipmap.lock_on_select, "20", Weather.CLEAR));

       // cityPicker = (DiscreteScrollView) findViewById(R.id.forecast_city_picker);
        cityPicker.setSlideOnFling(true);
        forecastAdapter=new ForecastAdapter(forecasts,this);
        cityPicker.setAdapter(forecastAdapter);
        cityPicker.addOnItemChangedListener(this);
        cityPicker.addScrollStateChangeListener(this);
        cityPicker.scrollToPosition(1);
    //    cityPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
        cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        cityPicker.setOffscreenItems(300);
        cityPicker.setOverScrollEnabled(false);


        forecastAdapter.setOnItemClickItem(new ForecastAdapter.OnItemClickItem() {
            @Override
            public void onItemClickItemMethod(int position) {
                if(selectPostion!=-1 && position== selectPostion){
                    Toast.makeText(VideoVActivity.this,"点击了:"+position,Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(forecasts.size()==0){
            cityPicker.setVisibility(View.GONE);
            video_h_no_lock.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable ForecastAdapter.ViewHolder holder, int position) {
        //viewHolder will never be null, because we never remove items from adapter's list
        Log.e(Tag,"onCurrentItemChanged=======>:"+position);
        selectPostion=position;
        if (holder != null) {
            holder.showText();
        }
    }

    String Tag="denganzhi1";
    @Override
    public void onScrollStart(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        Log.e(Tag,"onScrollStart=======>");
        holder.hideText();
    }

    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ForecastAdapter.ViewHolder currentHolder,
            @Nullable ForecastAdapter.ViewHolder newHolder) {
//        Log.e(Tag,"onScroll=======>");
//        Forecast current = forecasts.get(currentIndex);
//        if (newIndex >= 0 && newIndex < cityPicker.getAdapter().getItemCount()) {
//            Forecast next = forecasts.get(newIndex);
//            forecastView.onScroll(1f - Math.abs(position), current, next);
//        }
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
                },3000);
                break;
            case R.id.video_v_go:
                Intent intent=new Intent(VideoVActivity.this,VideoHActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_back:
               finish();
                break;
        }
    }

    @Override
    public void onScrollEnd(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        Log.e(Tag,"onScrollEnd=======>");
    }
}
