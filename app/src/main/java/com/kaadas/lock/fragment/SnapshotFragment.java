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
import com.kaadas.lock.adapter.DateAdapter;
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

public class SnapshotFragment extends Fragment implements View.OnClickListener{



    private Unbinder unbinder;

    @BindView(R.id.pir_history_notic_tv)
    TextView pir_history_notic_tv;  //通知

    @BindView(R.id.pir_history_all_ll)
    LinearLayout pir_history_add_ll; //通知X

    @BindView(R.id.pir_history_add_cancle)
    LinearLayout pir_history_add_cancle;

    @BindView(R.id.year_select_ll)
    LinearLayout year_select_ll;

    @BindView(R.id.day_select_ll)
    LinearLayout day_select_ll;
    @BindView(R.id.time_select_rl)
    RecyclerView time_select_rl;
    TimeAdapter timeAdapter=null;




    List<String> yearList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();
    List<String> month12=new ArrayList<>(); //一年的12月份

    @BindView(R.id.year_tv)
    TextView year_tv;
    @BindView(R.id.time_tv)
    TextView time_tv; // 年份
    String[] weeks = {"日","一","二","三","四","五","六"};
    List<MyDate> myDateList=new ArrayList<>();  //保存每个月数据
    List<String> current_year_month_length=new ArrayList<>();  //改年的月
    List<MyDate> current_month_date=new ArrayList<>();   //保存改月数据
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    Calendar todayc = Calendar.getInstance();
    View lastView=null;
    TextView lastTop=null;
    TextView lastBottom=null;
    int lastPosition=-1;

    int day=-1;
    int month=-1;
    int week=-1;
    int year=-1;
    int lastyear=-1;
    int currentyear=-1;
    int currentMonth=-1;

    @BindView(R.id.history_rv_ff)
    RecyclerView history_rv_ff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_snapshot, container, false);
        unbinder = ButterKnife.bind(this, view);

        year_select_ll.setOnClickListener(this);
        day_select_ll.setOnClickListener(this);
        pir_history_add_cancle.setOnClickListener(this);

        todayc.setTime(new Date());
        day= todayc.get(Calendar.DAY_OF_MONTH);
        month= todayc.get(Calendar.MONTH)+1;
        week=todayc.get(Calendar.DAY_OF_WEEK)-1;
        year=todayc.get(Calendar.YEAR);


        yearList.add(year+"");
        lastyear= year-1;
        yearList.add(lastyear+"");

        currentyear= year;

        for (int i=1;i<=month;i++){
            String is= i+"";
            if(is.length()==1){
                is= "0"+i;
            }
            dateList.add(is);
        }


        if(day<7){  //当日日期小于7天涉及上到一个月
            MyDate myDate=new MyDate(day, weeks[week]);
            myDateList.add(myDate);
            for (int i = 1; i <= 6; i++) {
                todayc.add(Calendar.DAY_OF_MONTH, -1);// 前一天
                day= todayc.get(Calendar.DAY_OF_MONTH);
                week--;
                if(week==-1){
                    week=6;
                }
                myDate=new MyDate(day, weeks[week]);
                myDateList.add(myDate);
                System.out.println("day==>"+day+"week:"+weeks[week]);
            }
        }else{  // 当日日期大于7天，不涉及到上一个月
            for (int i = day; i > 0; i--) {
                MyDate myDate=new MyDate(i, weeks[week]);
                week--;
                if(week==-1){
                    week=6;
                }
                myDateList.add(myDate);
            }
        }

        for (int i=0;i<myDateList.size();i++){
            current_month_date.add(myDateList.get(i));
        }

        for (int i=0;i<dateList.size();i++){
            current_year_month_length.add(dateList.get(i));
        }

        month12.add("01");
        month12.add("02");
        month12.add("03");
        month12.add("04");
        month12.add("05");
        month12.add("06");
        month12.add("07");
        month12.add("08");
        month12.add("09");
        month12.add("10");
        month12.add("11");
        month12.add("12");

        // 年份
        mPopupWindow = new GravityPopup(getActivity(),yearList);

        mPopupWindow.setHidePopup(new GravityPopup.HidePopup() {
            @Override
            public void hidepopupMethod(String select) {
                //    Integer select_year = Integer.parseInt(select);
//                if(select_year==year){
//                    return;
//                }
                int month0= Integer.parseInt(time_tv.getText().toString());
                //   int year0=  Integer.parseInt(year_tv.getText().toString());
                int year0= Integer.parseInt(select);
                if(year0==lastyear ){ //选择了2018刷新
                    if(date_mPopupWindow!=null){
                        date_mPopupWindow.notifydatechangeMonth(month12,true);
                    }
                    // 根据月份计算出改月有多少天
                    int month_max= getDayOfMonthLength(year0,month0);
                    week = getDayOfWeek(year0, month0, month_max);
                    Log.e("denganzhi1","=======>........year0:"+year0+"month0:"+month0);
                    Log.e("denganzhi1","week:"+week+" month_max:"+month_max+"currentYear:"+currentyear);
                    myDateList.clear();
                    for (int i = month_max; i > 0; i--) {
                        MyDate myDate=new MyDate(i, weeks[week]);
                        week--;
                        if(week==-1){
                            week=6;
                        }
                        myDateList.add(myDate);

                    }
                    timeAdapter.notifyDataSetChanged();
                    Log.e("denganzhi1","result:"+myDateList);
                    currentyear= year0;
                    year_tv.setText(select);
                    mPopupWindow.dismiss();
                }else if(year0==year && currentyear!=-1){  //选择2019
                    if(date_mPopupWindow!=null){  // 实现级联功能，刷新月份
                        date_mPopupWindow.notifydatechangeMonth(current_year_month_length,true);
                        time_tv.setText("0"+month);
                        // 刷新日期
                        myDateList.clear();
                        for (int i=0;i<current_month_date.size();i++){
                            myDateList.add(current_month_date.get(i));
                        }
                        timeAdapter.notifyDataSetChanged();
                        currentyear= year0;
                        year_tv.setText(select);
                        mPopupWindow.dismiss();
                    }
                }
            }

        });
        int gravity = Gravity.NO_GRAVITY;
        gravity |= Gravity.BOTTOM;
        mPopupWindow.setPopupGravity(gravity);
        mPopupWindow.setBackground(0);

        // 月份
        date_mPopupWindow = new GravityPopup(getActivity(),dateList);
        date_mPopupWindow.setHidePopup(new GravityPopup.HidePopup() {
            @Override
            public void hidepopupMethod(String select) {
                time_tv.setText(select);
                date_mPopupWindow.dismiss();

                int current_month= Integer.parseInt(select);
                currentMonth=current_month;
                myDateList.clear();
                if(current_month== month ){
                    for (int i=0;i<current_month_date.size();i++){
                        myDateList.add(current_month_date.get(i));
                    }
                    timeAdapter.notifyDataSetChanged();
                    return;
                }

                // 根据月份计算出改月有多少天
                int month_max= getDayOfMonthLength(currentyear,current_month);
                week = getDayOfWeek(currentyear, current_month, month_max);

                for (int i = month_max; i > 0; i--) {
                    MyDate myDate=new MyDate(i, weeks[week]);
                    week--;
                    if(week==-1){
                        week=6;
                    }
                    myDateList.add(myDate);

                }
                timeAdapter.notifyDataSetChanged();
                Log.e("denganzhi1","result:"+myDateList);
                Log.e("denganzhi1","week:"+week+" month_max:"+month_max+"currentYear:"+currentyear);

            }
        });
        int gravity1 = Gravity.NO_GRAVITY;
        gravity1 |= Gravity.BOTTOM;
        date_mPopupWindow.setPopupGravity(gravity1);
        date_mPopupWindow.setBackground(0);



        // 星期
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,true);  //LinearLayoutManager中定制了可扩展的布局排列接口，子类按照接口中的规范来实现就可以定制出不同排雷方式的布局了
        //配置布局，默认为vertical（垂直布局），下边这句将布局改为水平布局
        // layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        time_select_rl.setLayoutManager(layoutManager);
        timeAdapter = new TimeAdapter(myDateList,getActivity());
        time_select_rl.setAdapter(timeAdapter);
        final int common_color= year_tv.getCurrentTextColor();


        timeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if(lastView!=null){
                    lastView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                }
                if(lastTop!=null){
                    lastTop.setTextColor(common_color);

                }
                if(lastBottom!=null){
                    lastBottom.setTextColor(Color.parseColor("#333333"));
                }

                if(lastPosition!=-1){
                    myDateList.get(lastPosition).setChecked(false);
                }

                myDateList.get(position).setChecked(true);

                View childView= view.findViewById(R.id.time_select_item_ll);
                childView.setBackgroundColor(Color.parseColor("#5EB7FF"));
                TextView top = view.findViewById(R.id.time_select_item_top);
                top.setTextColor(Color.parseColor("#FFFFFF"));
                TextView bottom = view.findViewById(R.id.time_select_item_bottom);
                bottom.setTextColor(Color.parseColor("#FFFFFF"));

                Toast.makeText(getActivity(),myDateList.get(position).getDay()+"",Toast.LENGTH_SHORT).show();



                lastView=childView;
                lastTop= top;
                lastBottom=bottom;
                lastPosition=position;

            }
        });
        // time_select_rl.smoothScrollToPosition(myDateList.size());

        initPIR();


        return view;
    }

    // 获取某一年有多少月
    public static int getDayOfMonthLength(int year,int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        Date dateY=c.getTime();
        System.out.println(dateY);
        return c.get(Calendar.DAY_OF_MONTH);
    }
    // 获取某年某月某日是星期几
    public static int getDayOfWeek(int year,int month,int date){
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month-1, date); //输入类型为int类型
//				Date dateY=c.getTime();
//				System.out.println(dateY);
        int week=c.get(Calendar.DAY_OF_WEEK)-1;
        if(week<0){
            week = 0;
        }
        return week;
    }

    private GravityPopup mPopupWindow;
    private GravityPopup date_mPopupWindow;



    public void testMethod(View view){
        date_mPopupWindow.notifydatechangeMonth(month12,true);
    }

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
