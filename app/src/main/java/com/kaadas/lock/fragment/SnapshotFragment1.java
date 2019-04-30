package com.kaadas.lock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.PreviewActivity;
import com.kaadas.lock.adapter.PirHistoryAdapter;
import com.kaadas.lock.adapter.TimeAdapter;
import com.kaadas.lock.bean.MyDate;
import com.kaadas.lock.widget.GravityPopup;
import com.kaadas.lock.widget.GravityPopup.HidePopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SnapshotFragment1 extends CallBackBaseFragment{


    @BindView(R.id.pir_history_notic_tv)
    TextView pir_history_notic_tv;  //通知
    @BindView(R.id.pir_history_all_ll)
    LinearLayout pir_history_add_ll; //通知X
    @BindView(R.id.pir_history_add_cancle)
    LinearLayout pir_history_add_cancle;
    @BindView(R.id.history_rv_ff)
    RecyclerView history_rv_ff;


    @Override
    public View initView(LayoutInflater inflater,  ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_snapshot, container, false);
        return view;
    }

    @Override
    public void initOtherFunction() {
        initPIR();
    }

    //    public void testMethod(View view){
//        date_mPopupWindow.notifydatechangeMonth(month12,true);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.year_select_ll:
                mPopupWindow.showPopupWindow(v);

                break;
            case  R.id.day_select_ll:
                date_mPopupWindow.showPopupWindow(v);
                break;
            case  R.id.pir_history_add_cancle:
                pir_history_add_ll.setVisibility(View.GONE);
                break;

        }
    }

    PirHistoryAdapter pirHistoryAdapter;
    List<String> imageList=new ArrayList<>();
    int catEyeCount=-1;
    private  void initPIR(){
        pir_history_add_cancle.setOnClickListener(this);
        String format= String.format( getActivity().getResources().getString(R.string.pir_history_notic), catEyeCount);
        pir_history_notic_tv.setText(format);

        for (int i=0;i<20;i++){
            imageList.add("2019-04-"+i+"12:34:23");
        }
        pirHistoryAdapter = new PirHistoryAdapter(getActivity(),imageList);
        history_rv_ff.setLayoutManager(new LinearLayoutManager(getActivity()));
        history_rv_ff.setAdapter(pirHistoryAdapter);

        pirHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(getActivity(), PreviewActivity.class);
                startActivity(intent);
            }
        });
    }





}
