package com.kaadas.lock.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.TimeAdapter;
import com.kaadas.lock.bean.MyDate;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.widget.GravityPopup;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create By denganzhi  on 2019/4/29
 * Describe
 */

public abstract class CallBackBaseFragment<T extends IBaseView, V
        extends BasePresenter<T>> extends Fragment  implements View.OnClickListener,IBaseView{


    Unbinder unbinder;
    @BindView(R.id.day_select_ll)
    LinearLayout day_select_ll;
    @BindView(R.id.time_select_rl)
    RecyclerView time_select_rl;
    TimeAdapter timeAdapter=null;

    @BindView(R.id.year_select_ll)
    LinearLayout year_select_ll;


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
    List<MyDate> current_month_date=new ArrayList<>();   //保存改月的日期

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


    protected V mPresenter;
    private LoadingDialog loadingDialog;
    private Handler bHandler = new Handler();

    String deviceId = "";
    String gatewayId="";

    String lastSelectMonth="";
    String lastSelectYear="";
    boolean snapShotFragment=false;
    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresent();
        mPresenter.attachView((T) this);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if (getArguments() != null) {
            deviceId = getArguments().getString(Constants.DEVICE_ID);
            gatewayId= getArguments().getString(Constants.GATEWAY_ID);
            snapShotFragment= getArguments().getBoolean(Constants.ISRECORDINGFRAGMENT);
        }
    }
    /**
     * 子类实现具体的构建过程
     *
     * @return
     */
    protected abstract V createPresent();

    @Override
    public void showLoading(String Content) {
        loadingDialog = LoadingDialog.getInstance(getContext());
        if (!getActivity().isFinishing()) {
            loadingDialog.show(Content);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= initView(inflater,container);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDatePicker();
        initOtherFunction();
    }

    public abstract View initView(LayoutInflater inflater, ViewGroup container);

    public abstract void initOtherFunction();
    boolean isSnapShotFragment=false;
    public void initDatePicker(){
        year_select_ll.setOnClickListener(this);
        day_select_ll.setOnClickListener(this);


        todayc.setTime(new Date());
        day= todayc.get(Calendar.DAY_OF_MONTH);
        month= todayc.get(Calendar.MONTH)+1;
        week=todayc.get(Calendar.DAY_OF_WEEK)-1;
        year=todayc.get(Calendar.YEAR);
        year_tv.setText(year+"");
        if(month<10){
            time_tv.setText("0"+month+"");
        }else{
            time_tv.setText(month+"");
        }
        yearList.add(year+"");
        lastyear= year-1;
        yearList.add(lastyear+"");
        Collections.reverse(yearList);


        currentyear= year;

        for (int i=1;i<=month;i++){
            String is= i+"";
            if(is.length()==1){
                is= "0"+i;
            }
            dateList.add(is);
        }
        String defaultCurrentDate=null;
        if(month<10){
            defaultCurrentDate= year+"0"+month;
        }else {
            defaultCurrentDate= year+month+"";
        }
        if(day<10){
            defaultCurrentDate= defaultCurrentDate+"0"+day;
        }else {
            defaultCurrentDate=defaultCurrentDate+day;
        }
        String key= deviceId+defaultCurrentDate;
        String str_d=(String) SPUtils2.get(MyApplication.getInstance(),key,"");
        Log.e(GeTui.VideoLog,"isSnapShotFragment------>:"+snapShotFragment+" key:"+key+"  str0:"+str_d);
        if(day<7){  //当日日期小于7天涉及上到一个月
            MyDate myDate=new MyDate(day, weeks[week]);
            if(!snapShotFragment){
                myDate.setChecked(true);
            }else {
                if(!TextUtils.isEmpty(str_d)){
                    myDate.setChecked(true);
                }
            }
            myDateList.add(myDate);
            for (int i = 1; i <= 6; i++) {
                todayc.add(Calendar.DAY_OF_MONTH, -1);// 前一天
               int newday= todayc.get(Calendar.DAY_OF_MONTH);
                week--;
                if(week==-1){
                    week=6;
                }
                myDate=new MyDate(newday, weeks[week]);
                myDateList.add(myDate);
                System.out.println("day==>"+day+"week:"+weeks[week]);
            }
        }else{  // 当日日期大于7天，不涉及到上一个月
            for (int i = day; i > 0; i--) {
                MyDate myDate=new MyDate(i, weeks[week]);
                if(i==day){
                    if(!snapShotFragment ){
                        myDate.setChecked(true);
                    }else {
                        if(!TextUtils.isEmpty(str_d)){
                            myDate.setChecked(true);
                        }
                    }
                }
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


                // 2019
                // 2018
                if(lastSelectYear.equals("")){
                    lastSelectYear= year+"";
                }
                if(select.equals(lastSelectYear)){
                    lastSelectYear=select;
                    mPopupWindow.dismiss();
                    return;
                }
                lastSelectYear=select;

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
                        for (int i=0;i<myDateList.size();i++){
                            myDateList.get(i).setChecked(false);
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
                // 5 月份
                // 4 月份
                if(lastSelectMonth.equals("")){
                    lastSelectMonth= month+"";
                }
                if(select.equals(lastSelectMonth)){
                    lastSelectMonth=select;
                    date_mPopupWindow.dismiss();
                    return;
                }
                lastSelectMonth=select;
                Log.e(GeTui.VideoLog,"lastSelect:"+lastSelectMonth);

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

//                for (int i=0;i<myDateList.size();i++){
//                    boolean isSelect= myDateList.get(0).isChecked();
//                    if(isSelect){
//                        myDateList.get(i).setChecked(false);
//                        timeAdapter.notifyItemChanged(position);
//                    }
//                }
                int date_month=Integer.parseInt(time_tv.getText().toString());
                String date_year =  year_tv.getText().toString();

                if(date_month==month && date_year.equals(year+"")){

                }else {
                    Log.e(GeTui.VideoLog,"date_month:"+date_month+"month:"+month+" year:"+year+" date_year:"+date_year);
                     for (int i=0;i<current_month_date.size();i++){
                         current_month_date.get(i).setChecked(false);
                    }
                }
                boolean isSelect= myDateList.get(0).isChecked();
                if(isSelect){
                    myDateList.get(0).setChecked(false);
                    timeAdapter.notifyItemChanged(0);
                }

                if(lastView!=null){
                    lastView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                }
                if(lastTop!=null){
                    lastTop.setTextColor(common_color);

                }
                if(lastBottom!=null){
                    lastBottom.setTextColor(Color.parseColor("#333333"));
                }

                if(lastPosition!=-1 && lastPosition<myDateList.size()){
                    myDateList.get(lastPosition).setChecked(false);
                }

                myDateList.get(position).setChecked(true);

                View childView= view.findViewById(R.id.time_select_item_ll);
                childView.setBackgroundColor(Color.parseColor("#5EB7FF"));
                TextView top = view.findViewById(R.id.time_select_item_top);
                top.setTextColor(Color.parseColor("#FFFFFF"));
                TextView bottom = view.findViewById(R.id.time_select_item_bottom);
                bottom.setTextColor(Color.parseColor("#FFFFFF"));

              //  Toast.makeText(getActivity(),myDateList.get(position).getDay()+"",Toast.LENGTH_SHORT).show();

                lastView=childView;
                lastTop= top;
                lastBottom=bottom;
                lastPosition=position;

                getPirData(position);

            }
        });
        // time_select_rl.smoothScrollToPosition(myDateList.size());
    }

    public void  getPirData(int position){

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

    protected GravityPopup mPopupWindow;
    protected GravityPopup date_mPopupWindow;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mPresenter.detachView();

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    @Override
    public void hiddenLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

}
