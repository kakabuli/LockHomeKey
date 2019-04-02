package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kaadas.lock.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by David.
 */

public class HomePageFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.ll_has_device)
    LinearLayout llHasDevice;
    @BindView(R.id.btn_add_device)
    Button btnAddDevice;
    ArrayList<String> radioButtonList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        rg.setOnCheckedChangeListener(this);
        addRadioButton();
        return view;
    }

    private void addRadioButton() {
        radioButtonList.add("111");
        radioButtonList.add("222");
        radioButtonList.add("333");
        for (int i = 0; i < radioButtonList.size(); i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(i);
            //设置RadioButton的背景
//            radioButton.setBackgroundResource(R.drawable.selector_button_jingxuan);
            //设置RadioButton的文本字体颜色
            radioButton.setTextColor(getResources().getColorStateList(R.color.white));
            //设置按钮的样式
//            radioButton.setButtonDrawable(null);
            radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
            //设置文字距离按钮四周的距离
            radioButton.setPadding(50, 0, 50, 0);
            //设置按钮文本
            radioButton.setText(radioButtonList.get(i));
            //设置字体加粗
            TextPaint tp = radioButton.getPaint();
            tp.setFakeBoldText(true);
            //设置RadioButton间距margin
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            //容器组装
            rg.addView(radioButton, params);
        }
        //设置默认值
        rg.check(0);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
