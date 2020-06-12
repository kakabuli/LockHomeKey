package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.DensityUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SwipchLinkActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView swipch_link_center_img,swipch_one_img,swipch_two_img,swipch_three_img;
    LinearLayout swipch_two,swipch_one,swipch_three;
    View swich_view_item_two;
    ImageView swipch_setting_btn,swich_link_link_switch,back;
    RelativeLayout tv_double_rl,three_double_rl;

    private WifiLockInfo wifiLockInfo;
    private int SwitchNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_link);

        swipch_link_center_img = findViewById(R.id.swipch_link_center_img);
        swipch_one = findViewById(R.id.swipch_one);
        swipch_two = findViewById(R.id.swipch_two);
        swich_link_link_switch = findViewById(R.id.swich_link_link_switch);

        swipch_one_img = findViewById(R.id.swipch_one_img);
        swipch_two_img = findViewById(R.id.swipch_two_img);
        swipch_three = findViewById(R.id.swipch_three);
        swipch_three_img = findViewById(R.id.swipch_three_img);
        swich_view_item_two = findViewById(R.id.swich_view_item_two);
        swipch_setting_btn = findViewById(R.id.swipch_setting_btn);

        tv_double_rl = findViewById(R.id.tv_double_rl);
        three_double_rl = findViewById(R.id.three_double_rl);
        back = findViewById(R.id.back);

        swipch_one.setOnClickListener(this);
        swipch_two.setOnClickListener(this);
        swipch_three.setOnClickListener(this);
        swipch_setting_btn.setOnClickListener(this);
        tv_double_rl.setOnClickListener(this);
        three_double_rl.setOnClickListener(this);
        swipch_link_center_img.setOnClickListener(this);
        swich_link_link_switch.setOnClickListener(this);
        back.setOnClickListener(this);

        swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_two);

        SwitchNumber = getIntent().getIntExtra(KeyConstants.SWITCH_NUMBER,1);

//        int open = SwitchNumber;

        switch (SwitchNumber){
            case 1:
                swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_one);
                swipch_two.setVisibility(View.GONE);
                swipch_three.setVisibility(View.GONE);
                break;
            case  2:
                swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_two);
                swipch_three.setVisibility(View.GONE);
                swich_view_item_two.setVisibility(View.GONE);
                tv_double_rl.setVisibility(View.VISIBLE);
                three_double_rl.setVisibility(View.GONE);
                break;

            case  3:
                swipch_link_center_img.setBackgroundResource(R.mipmap.swipch_link_three);

               ViewGroup.LayoutParams mylayoutParam1 =  swipch_one_img.getLayoutParams();
               RelativeLayout.LayoutParams rllayoutParam1 =( RelativeLayout.LayoutParams) (mylayoutParam1);
               rllayoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                ViewGroup.LayoutParams mylayoutParam2 =  swipch_two_img.getLayoutParams();
                RelativeLayout.LayoutParams rllayoutParam2 =( RelativeLayout.LayoutParams) (mylayoutParam2);
                rllayoutParam2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rllayoutParam2.topMargin= DensityUtil.dip2px(this,10);

                ViewGroup.LayoutParams mylayoutParam3 =  swipch_three_img.getLayoutParams();
                RelativeLayout.LayoutParams rllayoutParam3 =( RelativeLayout.LayoutParams) (mylayoutParam3);
                rllayoutParam3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rllayoutParam3.topMargin= DensityUtil.dip2px(this,10);

                      if(mylayoutParam1 instanceof  RelativeLayout.LayoutParams){
                         // Log.e("denganzhi","AAAAAAAAA");
                          swipch_one_img.setLayoutParams(rllayoutParam1);
                          swipch_two_img.setLayoutParams(mylayoutParam2);
                          swipch_three_img.setLayoutParams(mylayoutParam3);
                      }else if(mylayoutParam1 instanceof  LinearLayout.LayoutParams){
                        //  Log.e("denganzhi","bbbbbbbbbb");
                      }
                break;
        }
    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
////        SwitchNumber = getIntent().getIntExtra(KeyConstants.SWITCH_NUMBER,1);
//    }
    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case  R.id.swipch_one:

                 Intent intent= new Intent(this,SwipchLinkSettingActivity.class);
                 startActivity(intent);
                 break;
             case  R.id.swipch_two:
                  intent= new Intent(this,SwipchLinkSettingActivity.class);
                 startActivity(intent);
                 break;

             case  R.id.swipch_three:
                  intent= new Intent(this,SwipchLinkSettingActivity.class);
                 startActivity(intent);
                 break;
             case R.id.swipch_setting_btn:
                 intent= new Intent(this,SwipchSeetingArgus.class);
                 startActivity(intent);
                 break;

             case R.id.swich_link_link_switch:
                 swich_link_link_switch.setBackgroundResource(R.mipmap.swipperlinkleft_blue);
                 break;

             case R.id.back:
                 finish();
                 break;

         }
    }
}
