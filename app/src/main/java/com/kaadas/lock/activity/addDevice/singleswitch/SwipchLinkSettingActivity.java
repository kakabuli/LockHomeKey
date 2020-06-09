package com.kaadas.lock.activity.addDevice.singleswitch;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateFormatUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.TimeUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;

public class SwipchLinkSettingActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tv_content;
    // 1 表示全天
    int status=0 ;
    ImageView link_setting_all_day;
    TextView tvStart;
    TextView tvEnd;
    RelativeLayout swipch_link_rl_one,swipch_link_rl_two,swipch_link_rl_three;
    Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link_setting);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.effective_time));
        link_setting_all_day = findViewById(R.id.link_setting_all_day);
        link_setting_all_day.setOnClickListener(this);
        swipch_link_rl_one = findViewById(R.id.swipch_link_rl_one);
        swipch_link_rl_two = findViewById(R.id.swipch_link_rl_two);
        swipch_link_rl_three = findViewById(R.id.swipch_link_rl_three);
        tvStart = findViewById(R.id.tv_start);
        tvEnd = findViewById(R.id.tv_end);
        swipch_link_rl_one.setOnClickListener(this);
        swipch_link_rl_two.setOnClickListener(this);
        swipch_link_rl_three.setOnClickListener(this);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);


// 1 表示全天

         changeStatus(status);

          // setEffectiveTime();
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()) {
         case  R.id.link_setting_all_day:
             if(status==1){
                 status=0;
                 changeStatus(0);
             }else {
                   status=1;
                   changeStatus(1);
             }
             break;
         case R.id.swipch_link_rl_one:
             //开始
             TimeUtil.getInstance().getHourMinute(this, new TimeUtil.TimeListener() {
                 @Override
                 public void time(String hour, String minute) {
                     setStartTime(hour, minute);
                 }
             });
             break;
         case R.id.swipch_link_rl_two:
             //结束
             TimeUtil.getInstance().getHourMinute(this, new TimeUtil.TimeListener() {
                 @Override
                 public void time(String hour, String minute) {
                     setEndTime(hour, minute);
                 }
             });
             break;

         case R.id.btn_next:
             if (!NetUtil.isNetworkAvailable()) {
                 ToastUtil.getInstance().showShort(R.string.please_have_net_add_pwd);
                 return;
             }
             // 非全天
             if(status==0){
                 if (TextUtils.isEmpty(strStart)) {
                     AlertDialogUtil.getInstance().singleButtonNoTitleDialogNoLine(this, getString(R.string.swipch_link_setting_start_no),
                             getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                                 @Override
                                 public void left() {
                                 }
                                 @Override
                                 public void right() {
                                 }
                             }).show();
                //     ToastUtil.getInstance().showShort(R.string.select_start_time);
                     return;
                 }
                 if (TextUtils.isEmpty(strEnd)) {
                  //   ToastUtil.getInstance().showShort(R.string.select_end_time);
                     AlertDialogUtil.getInstance().singleButtonNoTitleDialogNoLine(this, getString(R.string.swipch_link_setting_end_no),
                             getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                                 @Override
                                 public void left() {
                                 }
                                 @Override
                                 public void right() {
                                 }
                             }).show();
                     return;
                 }
                 if (DateFormatUtils.hourMinuteChangeMillisecond(strEnd) <= DateFormatUtils.hourMinuteChangeMillisecond(strStart)) {
                   //  ToastUtil.getInstance().showShort(R.string.end_time_great_start_time);

                     AlertDialogUtil.getInstance().singleButtonNoTitleDialogNoLine(this, getString(R.string.swipch_link_setting_start_dayu_end),
                             getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                                 @Override
                                 public void left() {
                                 }
                                 @Override
                                 public void right() {
                                 }
                             }).show();
                     return;
                 }
             }
             break;


     }
    }
    private void changeStatus(int status){
        if(status==1){   // 全天
            link_setting_all_day.setBackgroundResource(R.mipmap.swipperlinkleft_yes);
            swipch_link_rl_one.setBackgroundColor(Color.parseColor("#FFEEEEEE"));
            swipch_link_rl_two.setBackgroundColor(Color.parseColor("#FFEEEEEE"));
            swipch_link_rl_three.setBackgroundColor(Color.parseColor("#FFEEEEEE"));
            swipch_link_rl_one.setClickable(false);
            swipch_link_rl_two.setClickable(false);
            swipch_link_rl_three.setClickable(false);
            tvStart.setText("");
            tvEnd.setText("");
        }else{
            link_setting_all_day.setBackgroundResource(R.mipmap.swipperlinkleft_no);
            swipch_link_rl_one.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            swipch_link_rl_two.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            swipch_link_rl_three.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            swipch_link_rl_one.setClickable(true);
            swipch_link_rl_two.setClickable(true);
            swipch_link_rl_three.setClickable(true);
        }

    }
    private void setEffectiveTime() {
        String startTime = DateUtils.currentLong2HourMin(System.currentTimeMillis());
        String endTime = DateUtils.currentLong2HourMin(System.currentTimeMillis() + 60 * 60 * 1000);
        String[] startSplit = startTime.split(":");
        String[] endSplit = endTime.split(":");
        setStartTime(startSplit[0], startSplit[1]);
        setEndTime(endSplit[0], endSplit[1]);
    }
    String strStart;//开始
    String strEnd;//结束
    private int startMin;
    private int startHour;
    private int endMin;
    private int endHour;
    private void setStartTime(String hour, String minute) {
        strStart = hour + ":" + minute;
        tvStart.setText(hour + ":" + minute);
        startHour = Integer.parseInt(hour);
        startMin = Integer.parseInt(minute);
        hintText();
    }

    private void setEndTime(String hour, String minute) {
        strEnd = hour + ":" + minute;
        tvEnd.setText(hour + ":" + minute);
        endHour = Integer.parseInt(hour);
        endMin = Integer.parseInt(minute);
        hintText();
    }
    /**
     * 密码将于每天15：25至 16：30重复生效
     * 密码将每周一 周二 周三 15：25至 16：30重复生效
     */
    public void hintText() {
//        if (TextUtils.isEmpty(strStart) || TextUtils.isEmpty(strEnd)) {
//            tvHint.setVisibility(View.INVISIBLE);
//        } else {
//            tvHint.setVisibility(View.VISIBLE);
//            String strHint = String.format(getString(R.string.week_hint), weekRule, strStart, strEnd);
//            tvHint.setText(strHint);
//        }
    }

}
