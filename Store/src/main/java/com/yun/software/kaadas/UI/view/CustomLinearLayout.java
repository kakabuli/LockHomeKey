package com.yun.software.kaadas.UI.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Map;

/**
 * Created by yanliang
 * on 2019/8/6
 */
public class CustomLinearLayout extends LinearLayout {
    Map<String,View> childs;
    public CustomLinearLayout(Context context) {
        this(context,null);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);

    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,defStyleAttr);
        initView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childs.clear();
        int childcount=getChildCount();
        for (int i = 0; i < childcount; i++) {
            switch (i){
                case 0:
                    childs.put("banner",getChildAt(0));
                    break;
                case 1:
                    childs.put("video",getChildAt(1));
                    break;
                case 2:
                    childs.put("smartcateye",getChildAt(2));
                    break;
                case 3:
                    //执手
                    childs.put("product_type_k",getChildAt(3));
                    break;
                case 4:
                    //维拉
                    childs.put("product_type_s",getChildAt(4));
                    break;
                case 5:
                    //众筹
                    childs.put("product_type_crowd",getChildAt(5));
                    break;
                case 6:
                    //拼团
                    childs.put("product_type_group",getChildAt(6));
                    break;
                case 7:
                    //众筹
                    childs.put("product_type_seckill",getChildAt(7));
                    break;
                case 8:
                    //众筹
                    childs.put("product_type_bargain",getChildAt(8));
                    break;
            }

        }
    }
    private  void initView(){
       childs=new ArrayMap<>();
    }
    public void reSetlayoutView(List<String> indexkey){
        removeAllViews();
        addView(childs.get("banner"),0);
        addView(childs.get("video"),1);
        addView(childs.get("smartcateye"),2);
        for (int i =0; i < indexkey.size(); i++) {
            int index=i+3;
            addView(childs.get(indexkey.get(i)),index);
        }
        requestLayout();

    }
}
