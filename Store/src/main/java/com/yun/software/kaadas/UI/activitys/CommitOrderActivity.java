package com.yun.software.kaadas.UI.activitys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.OderListItemAdapter;
import com.yun.software.kaadas.UI.bean.AddressBean;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CarItem;
import com.yun.software.kaadas.UI.bean.CommitOrderBean;
import com.yun.software.kaadas.UI.bean.CouponBean;
import com.yun.software.kaadas.UI.fragment.OrderStateFragment;
import com.yun.software.kaadas.UI.view.CommiteItemListView;
import com.yun.software.kaadas.UI.view.dialog.CouponDialog;
import com.yun.software.kaadas.Utils.AmountUtil;
import com.yun.software.kaadas.Utils.OrderStatue;
import com.yun.software.kaadas.Utils.TimeUtil;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

/**
 * Created by yanliang
 * on 2019/5/16
 */
public class CommitOrderActivity extends BaseActivity {
    @BindView(R2.id.commiteItem_list)
    CommiteItemListView commiteItemListView;
    @BindView(R2.id.tv_submit)
    TextView tvSubmit;
    @BindView(R2.id.lin_address)
    LinearLayout linaddress;
    @BindView(R2.id.tv_select_data)
    TextView tvSelectDate;
    @BindView(R2.id.re_select_data)
    RelativeLayout reSelectDate;

    @BindView(R2.id.tv_name)
    TextView tvUserNameView;

    @BindView(R2.id.tv_phone)
    TextView tvPhoneView;

    @BindView(R2.id.tv_address)
    TextView tvAddressView;

    @BindView(R2.id.tv_add_address)
    TextView tvaddAddressView;

    @BindView(R2.id.ll_address)
    LinearLayout llAddressView;

    @BindView(R2.id.rl_coupon)
    RelativeLayout rlCouponView;

    @BindView(R2.id.tv_coupon)
    TextView tvCouponView;

    @BindView(R2.id.view_coupon)
    View viewCoupon;

//    @BindView(R2.id.tv_coupon_value)
//    TextView tvCouponValueView;

//    @BindView(R2.id.rl_coupon_value)
//    RelativeLayout rlCouponValueView;

    @BindView(R2.id.tv_price)
    TextView tvPriceView;


    @BindView(R2.id.tv_order_price)
    TextView tvOrderPriceView;

    @BindView(R2.id.edit_text)
    EditText editText;

    private TimePickerView mTimePickerView;
    private TimePickerView pvTime;
    private String choicetime;
    private boolean isdefaultam=true;
    private Calendar startDate;

    private OderListItemAdapter oderListItemAdapter;

    private String anzhuangTime;

    private List<String> ids;//购物车中商品id

    private List<CouponBean> couponBeans ;

    private String liuyan;

    private CommitOrderBean orderBean;

    private int type =0;
    private String id;
    private String agentProductId;
    private String qty;
    private String couponId;
    private int selectCoupon;

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_commit_order;
    }

    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("填写订单");



        ids = getIntent().getStringArrayListExtra("ids");
        type = getIntent().getIntExtra("type",0);
        id = getIntent().getStringExtra("id");
        agentProductId = getIntent().getStringExtra("agentProductId");
        qty = getIntent().getStringExtra("qty");


         oderListItemAdapter = new OderListItemAdapter(mContext);
        commiteItemListView.setAdapter(oderListItemAdapter);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == ShopDetailsNewActivity.TYPE_NORMAL){
                    submitOrder();
                }else {
                    submitActivityOrder();
                }
            }
        });
        linaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 readyGo(ManageAddress.class);
            }
        });
        reSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceSelectTime();
            }
        });
        rlCouponView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponBeans != null && couponBeans.size() > 0){
                    CouponDialog.getInstance().showDialog(getSupportFragmentManager(),mContext,couponBeans,selectCoupon);
                }else {
                    getCoupon();
                }

            }
        });

        if (type == ShopDetailsNewActivity.TYPE_NORMAL){
            getData(ids);
        }else {
            getActivityData();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

//    @Override
//    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
//    }

    private void submitOrder() {
        if (orderBean.getAddress() == null){
            ToastUtil.showShort("请添加收货地址");
            return;
        }

        if (TextUtils.isEmpty(anzhuangTime)){
            ToastUtil.showShort("请选择安装日期");
            return;
        }

        liuyan = editText.getText().toString().trim();

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("shopcarIds",ids.toArray());
        params.put("addressId",orderBean.getAddress().getId());
        if (!TextUtils.isEmpty(couponId)){
            params.put("couponId",couponId);
        }
        params.put("buyerMsg",liuyan);
        params.put("installationDate",anzhuangTime);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_CREATEBASEINDENT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                String realPay = StringUtil.getJsonKeyStr(result,"realPay");
                String id = StringUtil.getJsonKeyStr(result,"id");
                String orderNo = StringUtil.getJsonKeyStr(result,"orderNo");

                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                bundle.putString("orderNo",orderNo);
                bundle.putString("price",realPay);
                bundle.putInt("type",type);
                readyGo(PayActivity.class,bundle);


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },true);

//        readyGo(MyOderActivity.class);
    }

    private void submitActivityOrder(){
        if (orderBean.getAddress() == null){
            ToastUtil.showShort("请添加收货地址");
            return;
        }

        if (TextUtils.isEmpty(anzhuangTime)){
            ToastUtil.showShort("请选择安装日期");
            return;
        }

        String types = "";

        switch (type){
            case ShopDetailsNewActivity.TYPE_ZHONGCHOU:
                types = OrderStatue.INDENT_TYPE_CROWD;
                break;
            case ShopDetailsNewActivity.TYPE_PINTUAN:
                types = OrderStatue.INDENT_TYPE_GROUP;
                break;
            case ShopDetailsNewActivity.TYPE_MIAOSHA:
                types = OrderStatue.INDENT_TYPE_SECKILL;
                break;
            case ShopDetailsNewActivity.TYPE_KANJIA:
                types = OrderStatue.INDENT_TYPE_BARGAIN;
                break;

        }

        liuyan = editText.getText().toString().trim();

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("addressId",orderBean.getAddress().getId());
        params.put("buyerMsg",liuyan);
        params.put("installationDate",anzhuangTime);
        params.put("businessId",id);
        params.put("agentProductId",agentProductId);
        params.put("qty",qty);
        params.put("type",types);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_CREATEACTIVITYINDENT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                String realPay = StringUtil.getJsonKeyStr(result,"realPay");
                String id = StringUtil.getJsonKeyStr(result,"id");
                String orderNo = StringUtil.getJsonKeyStr(result,"orderNo");

//                PayDialog.getInstance().payDialog(getSupportFragmentManager(),mContext,id,realPay);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                bundle.putString("price",realPay);
                bundle.putString("orderNo",orderNo);
                bundle.putInt("type",type);
                readyGo(PayActivity.class,bundle);


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },true);

//        readyGo(MyOderActivity.class);
    }

    private void choiceSelectTime() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        if(startDate==null){
            startDate = Calendar.getInstance();
        }
        //        startDate.set(2017, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 2, 28);
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
//                startDate.setTime(date);
                choicetime=TimeUtil.getDateTime(date);
                String day=isdefaultam?"  上午":"  下午";
                tvSelectDate.setText(choicetime+day);
                anzhuangTime = choicetime + day;
                //                Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
            }
        }).setType(new boolean[]{true, true, true, false, false, false}).setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);

                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvTime.returnData();
                        pvTime.dismiss();

                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pvTime.dismiss();
                    }
                });
                //公农历切换
                CheckBox cb_am = (CheckBox) v.findViewById(R.id.cb_am);
                CheckBox cb_pm = (CheckBox) v.findViewById(R.id.cb_pm);
                if(isdefaultam){
                    cb_am.setChecked(true);
                    cb_pm.setChecked(false);
                }else{
                    cb_am.setChecked(false);
                    cb_pm.setChecked(true);
                }
                cb_am.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            cb_pm.setChecked(false);
                            isdefaultam=true;
                        }else{
                            cb_pm.setChecked(true);
                            isdefaultam=false;
                        }
                        //                        pvCustomLunar.setLunarCalendar(!pvCustomLunar.isLunarCalendar());
                        //                        //自适应宽
                        //                        setTimePickerChildWeight(v, isChecked ? 0.8f : 1f, isChecked ? 1f : 1.1f);
                    }
                });
                cb_pm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            isdefaultam=false;
                            cb_am.setChecked(false);
                        }else{
                            isdefaultam=true;
                            cb_am.setChecked(true);
                        }
                        //                        pvCustomLunar.setLunarCalendar(!pvCustomLunar.isLunarCalendar());
                        //                        //自适应宽
                        //                        setTimePickerChildWeight(v, isChecked ? 0.8f : 1f, isChecked ? 1f : 1.1f);
                    }
                });
            }
        }).setDate(selectedDate)
          .setRangDate(startDate, endDate).setSubmitColor(mContext.getResources().getColor(R.color.light_blue))
          .build();
        pvTime.show();

    }

    private void getData(List<String> ids) {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("shopcarIds",ids.toArray());
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_PREVIEWBASEINDENT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                Gson gson = new Gson();
                orderBean = gson.fromJson(result,CommitOrderBean.class);
                setView();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                finish();


            }
        },true);
    }

    private void getActivityData() {
        String types ="";
        switch (type){
            case ShopDetailsNewActivity.TYPE_KANJIA:
                types = "product_type_bargain";
                break;
            case ShopDetailsNewActivity.TYPE_MIAOSHA:
                types = "product_type_seckill";
                break;
            case ShopDetailsNewActivity.TYPE_ZHONGCHOU:
                types = "product_type_crowd";
                break;
            case ShopDetailsNewActivity.TYPE_PINTUAN:
                types = "product_type_group";
                break;

        }



        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("businessId",id);//活动Id
        params.put("agentProductId",agentProductId);//渠道商品Id
        params.put("qty",qty);//购买数量
        params.put("type",types);//活动商品类型
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.INDENT_PREVIEWACTIVITYINDENT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                Gson gson = new Gson();
                orderBean = gson.fromJson(result,CommitOrderBean.class);
                List<CarItem> carItemList = new ArrayList<>();
                CarItem carItem = new CarItem();
                carItem.setQty(orderBean.getAgentProduct().getQty());
                carItem.setSkuProductName(orderBean.getAgentProduct().getSkuProductName());
                carItem.setLogo(orderBean.getAgentProduct().getLogo());
                carItem.setPrice(orderBean.getAgentProduct().getPrice());
                carItem.setAttributeComboName(orderBean.getAgentProduct().getAttributeComboName());

                carItemList.add(carItem);
                orderBean.setShopcar(carItemList);

                setView();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                finish();


            }
        },true);
    }


    private void getCoupon() {

        if (ids == null || ids.size() == 0){
            ToastUtil.showShort("暂无可用券");
            return;
        }


        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("shopcarIds",ids);
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.COUPON_GETMYLIST, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                BaseBody<CouponBean> list = gson.fromJson(result,new TypeToken<BaseBody<CouponBean>>(){}.getType());
                couponBeans = new ArrayList<>();
                if (list.getRows() != null){
                    couponBeans.addAll(list.getRows());
                    couponBeans.add(new CouponBean("","暂无可用券"));
                    selectCoupon = couponBeans.size() -1;
                    for (int i=0; i<couponBeans.size();i++){
                        couponBeans.get(i).setPosition(i);
                        if (TextUtils.equals(orderBean.getCoupon().getId(),couponBeans.get(i).getId())){
                            selectCoupon = i;
                        }
                    }
                }
                CouponDialog.getInstance().showDialog(getSupportFragmentManager(),mContext,couponBeans,selectCoupon);
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },true);
    }




    private void setView() {
        //地址
        AddressBean addressBean =  orderBean.getAddress();
        if (addressBean == null){
            llAddressView.setVisibility(View.GONE);
            tvaddAddressView.setVisibility(View.VISIBLE);
        }else {
            llAddressView.setVisibility(View.VISIBLE);
            tvaddAddressView.setVisibility(View.GONE);
            tvUserNameView.setText(addressBean.getName());
            tvPhoneView.setText(addressBean.getPhone());
            tvAddressView.setText(addressBean.getProvince() + addressBean.getCity() + addressBean.getArea() + addressBean.getAddress());
        }

        //优惠券
        CouponBean couponBean = orderBean.getCoupon();
        if (couponBean == null ){
            couponBean = new CouponBean("0","暂无可用券");
            tvCouponView.setText(couponBean.getRemark());
            tvCouponView.setTextColor(getResources().getColor(R.color.color_999));
            couponId = couponBean.getId();

        }else {
            tvCouponView.setText("优惠¥"+couponBean.getCouponValue());
            tvCouponView.setTextColor(getResources().getColor(R.color.color_333));
        }

//        if (couponBean == null){
//            viewCoupon.setVisibility(View.GONE);
//            rlCouponView.setVisibility(View.GONE);
//            rlCouponValueView.setVisibility(View.GONE);
//        }else {
//            tvCouponView.setText(couponBean.getRemark());
//            tvCouponValueView.setText(couponBean.getCouponValue());
//            couponId = couponBean.getId();
//        }

        oderListItemAdapter.setDatas(orderBean.getShopcar());
        oderListItemAdapter.notifyDataSetChanged();

        tvPriceView.setText("¥"+orderBean.getTotalPrice());

        double orderPrice = Double.valueOf(orderBean.getTotalPrice());
        if (couponBean != null){
            orderPrice= AmountUtil.subtract(Double.valueOf(orderBean.getTotalPrice()),Double.valueOf(couponBean.getCouponValue()),2);
        }

        tvOrderPriceView.setText(String.valueOf(orderPrice));


    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        super.onEventComing(eventCenter);
        if(eventCenter.getEventCode()== Constans.MESSAGE_ORDER_COUPON){
            CouponBean couponBean = (CouponBean) eventCenter.getData();
            selectCoupon = couponBean.getPosition();
            if (couponBean.isCheck()){
                tvCouponView.setText("优惠¥"+couponBean.getCouponValue());
                tvCouponView.setTextColor(getResources().getColor(R.color.color_333));
                double orderPrice = Double.valueOf(orderBean.getTotalPrice());
                if (couponBean != null){
                    orderPrice= AmountUtil.subtract(Double.valueOf(orderBean.getTotalPrice()),Double.valueOf(couponBean.getCouponValue()),2);
                    couponId = couponBean.getId();
                }

                tvOrderPriceView.setText(orderPrice + "");
            }else {
                couponId = "";
                tvCouponView.setText("暂无可用券");
                tvOrderPriceView.setText(orderBean.getTotalPrice());
                selectCoupon = couponBeans.size() -1;
                tvCouponView.setTextColor(getResources().getColor(R.color.color_999));
            }


        }else if (eventCenter.getEventCode() == Constans.MESSAGE_PAY_SUCCESS){
            Bundle bundle=new Bundle();
            if (type == ShopDetailsNewActivity.TYPE_NORMAL){
                bundle.putInt("page", 2);

                readyGo(MyOderActivity.class, bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_MIAOSHA){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_SECKILL);
                bundle.putString("title","秒杀");
                readyGo(OrderStateFragment.class,bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_ZHONGCHOU){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_CROWD);
                bundle.putString("title","众筹");
                readyGo(OrderStateFragment.class,bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_PINTUAN){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_GROUP);
                bundle.putString("title","团购");
                readyGo(OrderStateFragment.class,bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_KANJIA){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_BARGAIN);
                bundle.putString("title","砍价");
                readyGo(OrderStateFragment.class,bundle);
            }
            finish();


        }else if (eventCenter.getEventCode() == Constans.MESSAGE_SUBMIT_ORDER){
            Bundle bundle=new Bundle();
            if (type == ShopDetailsNewActivity.TYPE_NORMAL){
                bundle.putInt("page", 1);
                readyGo(MyOderActivity.class, bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_MIAOSHA){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_SECKILL);
                bundle.putString("title","秒杀");
                readyGo(OrderStateFragment.class,bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_ZHONGCHOU){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_CROWD);
                bundle.putString("title","众筹");
                readyGo(OrderStateFragment.class,bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_PINTUAN){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_GROUP);
                bundle.putString("title","团购");
                readyGo(OrderStateFragment.class,bundle);
            }else if (type == ShopDetailsNewActivity.TYPE_KANJIA){
                bundle.putString("statue","");
                bundle.putString("type",OrderStatue.INDENT_TYPE_BARGAIN);
                bundle.putString("title","砍价");
                readyGo(OrderStateFragment.class,bundle);
            }
            finish();
        }else if (eventCenter.getEventCode() == Constans.MESSAGE_ORDER_ADDRESS){
            AddressBean addressBean = (AddressBean) eventCenter.getData();
            llAddressView.setVisibility(View.VISIBLE);
            tvaddAddressView.setVisibility(View.GONE);
            orderBean.setAddress(addressBean);
            tvUserNameView.setText(addressBean.getName());
            tvPhoneView.setText(addressBean.getPhone());
            tvAddressView.setText(addressBean.getProvince() + addressBean.getCity() + addressBean.getArea() + addressBean.getAddress());
        }
    }


}
