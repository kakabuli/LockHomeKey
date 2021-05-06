package com.kaadas.lock.widget;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.DateAdapter;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;


/**
 * Created by 大灯泡 on 2016/1/15.
 * gravityPopup
 */
public class GravityPopup extends BasePopupWindow implements View.OnClickListener {

    RecyclerView date_select_rl;

    Context context;

    int lastSelect=-1;
    View lastView=null;
    boolean isFlag=false;

    public GravityPopup(Context context) {
        super(context);
        this.context=context;
        setPopupFadeEnable(true);
        bindEvent();
    }
    List<String> data=new ArrayList<>();
    public GravityPopup(Context context, List<String> data) {
        super(context);
        this.context=context;
        this.data= data;
        setPopupFadeEnable(true);
        bindEvent();
    }


    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_gravity);
    }
    DateAdapter dateAdapter;
    private void bindEvent() {
//        findViewById(R.id.tx_1).setOnClickListener(this);
//        findViewById(R.id.tx_2).setOnClickListener(this);
//        findViewById(R.id.tx_3).setOnClickListener(this);

        date_select_rl = findViewById(R.id.date_select_rl);

        LinearLayoutManager layoutManager=new LinearLayoutManager(context);  //LinearLayoutManager中定制了可扩展的布局排列接口，子类按照接口中的规范来实现就可以定制出不同排雷方式的布局了

        date_select_rl.setLayoutManager(layoutManager);

//        List<String> list=new ArrayList<>();
//        list.add("2019");
//        list.add("2018");
        dateAdapter=new DateAdapter(data);
        date_select_rl.setAdapter(dateAdapter);

        dateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Log.e("denganzhi1","lastSelect:"+lastSelect);

                 TextView date_select_tv=view.findViewById(R.id.date_select_item_tv);

                 if(lastSelect!=-1){
                     lastView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                     TextView last_tv= lastView.findViewById(R.id.date_select_item_tv);
                     last_tv.setTextColor(Color.parseColor("#333333"));
                 }

                  view.setBackgroundColor(Color.parseColor("#5EB7FF"));

                  date_select_tv.setTextColor(Color.parseColor("#FFFFFF"));

                  if(hidePopup!=null){
                      String select= data.get(position);
                      hidePopup.hidepopupMethod(select);
                  }

                  lastSelect= position;
                  lastView=view;

//                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                date_select_tv=view.findViewById(R.id.date_select_item_tv);
//                date_select_tv.setTextColor(Color.parseColor("#333333"));
            }
        });
    }

    @Override
    public void showPopupWindow() {
        setShowAnimation(getDefaultScaleAnimation());
        setDismissAnimation(getDefaultScaleAnimation(false));
        super.showPopupWindow();
    }

    @Override
    public void showPopupWindow(View anchorView) {
        if(isFlag){
            isFlag=false;
            dateAdapter=new DateAdapter(data);
            date_select_rl.setAdapter(dateAdapter);
            dateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                    Log.e("denganzhi1","lastSelect:"+lastSelect);

                    TextView date_select_tv=view.findViewById(R.id.date_select_item_tv);

                    if(lastSelect!=-1){
                        lastView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        TextView last_tv= lastView.findViewById(R.id.date_select_item_tv);
                        last_tv.setTextColor(Color.parseColor("#333333"));
                    }

                    view.setBackgroundColor(Color.parseColor("#5EB7FF"));

                    date_select_tv.setTextColor(Color.parseColor("#FFFFFF"));

                    if(hidePopup!=null){
                        String select= data.get(position);
                        hidePopup.hidepopupMethod(select);
                    }

                    lastSelect= position;
                    lastView=view;

//                view.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                date_select_tv=view.findViewById(R.id.date_select_item_tv);
//                date_select_tv.setTextColor(Color.parseColor("#333333"));
                }
            });
        }
        initAnimate();
        super.showPopupWindow(anchorView);
    }

    private void initAnimate() {

        int gravity = getPopupGravity();

        float in_fromX = 0;
        float in_toX = 0;
        float in_fromY = 0;
        float in_toY = 0;

        float exit_fromX = 0;
        float exit_toX = 0;
        float exit_fromY = 0;
        float exit_toY = 0;

        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
            case Gravity.START:
                in_fromX = 1f;
                exit_toX = 1f;
                break;
            case Gravity.RIGHT:
            case Gravity.END:
                in_fromX = -1f;
                exit_toX = -1f;
                break;
            case Gravity.CENTER_HORIZONTAL:
                break;
            default:
                break;
        }
        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                in_fromY = 1f;
                exit_toY = 1f;
                break;
            case Gravity.BOTTOM:
                in_fromY = -1f;
                exit_toY = -1f;
                break;
            case Gravity.CENTER_VERTICAL:
                break;
            default:
                break;
        }
        setShowAnimation(createTranslateAnimate(in_fromX, in_toX, in_fromY, in_toY));
        setDismissAnimation(createTranslateAnimate(exit_fromX, exit_toX, exit_fromY, exit_toY));
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tx_1:
//                ToastUtils.ToastMessage(getContext(), "click tx_1");
//                break;
//            case R.id.tx_2:
//                ToastUtils.ToastMessage(getContext(), "click tx_2");
//                break;
//            case R.id.tx_3:
//                ToastUtils.ToastMessage(getContext(), "click tx_3");
//                break;
//            default:
//                break;
//        }

    }

    public Animation createTranslateAnimate(float fromX, float toX, float fromY, float toY) {
        Animation result = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                fromX,
                Animation.RELATIVE_TO_PARENT,
                toX,
                Animation.RELATIVE_TO_PARENT,
                fromY,
                Animation.RELATIVE_TO_PARENT,
                toY);
        result.setDuration(300);
        return result;
    }


    public  interface  HidePopup{
         void hidepopupMethod(String select);
    }

    public HidePopup hidePopup;

    public void setHidePopup(HidePopup hidePopup) {
        this.hidePopup = hidePopup;
    }

    public void notifydatechangeMonth(List<String> list, boolean isFlag){

        this.data=list;
        this.isFlag=isFlag;
        Log.e("dengnazhi1",data.toString());

        //dateAdapter.notifyDataSetChanged();
    }
}
