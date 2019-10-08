package com.yun.software.kaadas.UI.activitys;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bigkoo.pickerview.OptionsPickerView;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.AddressBean;
import com.yun.software.kaadas.UI.bean.ChinaAddress;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import la.xiong.androidquick.tool.ExceptionUtil;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/5/14
 */
public class AddOrEditAddress extends BaseActivity {
    @BindView(R2.id.tv_choice_address)
    TextView tvChoiceAddress;

    @BindView(R2.id.et_name)
    EditText etNameView;

    @BindView(R2.id.et_mobile)
    EditText etMobileView;

    @BindView(R2.id.et_address_detail)
    EditText etAddressDetail;


    private String province ;
    private String city ;
    private String area;

    private DisposableObserver<Integer> disposableObserver;
    private List<ChinaAddress> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    // 选择项
    int temoptions1 = 0, temoptions2 = 0, temoptions3 = 0;
    String provinceId = "0";
    String cityId = "0";
    String countryId = "0";
    private boolean isLoaded = false;

    private AddressBean addressBean;
    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_addoredit_address;
    }

    @Override
    protected void initViewsAndEvents() {
        parseJson();
        addressBean = getIntent().getParcelableExtra("bean");
        if (addressBean == null){
            tvTitle.setText("添加地址");
        }else {
            tvTitle.setText("修改地址");
            etNameView.setText(addressBean.getName());
            etMobileView.setText(addressBean.getPhone());
            etAddressDetail.setText(addressBean.getAddress());
            tvChoiceAddress.setText(addressBean.getProvince() + addressBean.getCity() + addressBean.getArea());
            tvChoiceAddress.setTextColor(getResources().getColor(R.color.color_333333));
            province = addressBean.getProvince();
            city = addressBean.getCity();
            area = addressBean.getArea();
        }

        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");


    }

    private  void parseJson(){
        isLoaded=true;
        String jsonstr = StringUtil.getJson(mContext, ApiConstants.ADDRESSNAME);
        final Map<String, String> maps = JSON.parseObject(jsonstr, new TypeReference<Map<String, String>>() {
        });
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                handleraddressJson(maps);
                e.onComplete();
            }

        });
        disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                Log.d("BackgroundActivity", "onNext=" + value);

            }

            @Override
            public void onError(Throwable e) {
                Log.d("BackgroundActivity", "onError=" + e);

            }

            @Override
            public void onComplete() {
                isLoaded=false;
                LogUtils.iTag("BackgroundActivity", "onComplete");

            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);


//
    }
    private void handleraddressJson(Map<String, String> map) {
        try {
            if (map != null && !"".equals(map)) {
                String list = map.get("data");
                options1Items = JSON.parseObject(list, new TypeReference<List<ChinaAddress>>() {
                });

                for (int i = 0; i < options1Items.size(); i++) {
                    if (options1Items.get(i).getId().equals(provinceId)) {
                        temoptions1 = i;
                    }
                    ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                    if (options1Items == null) {
                        return;
                    }
                    if (options1Items.get(i).getChild() != null && options1Items.get(i).getChild().size() > 0) {
                        for (int c = 0; c < options1Items.get(i).getChild().size(); c++) {//遍历该省份的所有城市
                            if (cityId.equals(options1Items.get(i).getChild().get(c).getId())) {
                                temoptions2 = c;
                            }
                            ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                            String CityName = options1Items.get(i).getChild().get(c).getName();
                            CityList.add(CityName);//添加城市
                            //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                            if (options1Items.get(i).getChild().get(c).getChild() == null
                                    || options1Items.get(i).getChild().get(c).getChild().size() == 0) {
                                City_AreaList.add("");
                            } else {
                                for (int d = 0; d < options1Items.get(i).getChild().get(c).getChild().size(); d++) {//该城市对应地区所有数据
                                    String AreaName = options1Items.get(i).getChild().get(c).getChild().get(d).getName();
                                    if (countryId.equals(options1Items.get(i).getChild().get(c).getChild().get(d).getId())) {
                                        temoptions3 = d;
                                    }
                                    City_AreaList.add(AreaName);//添加该城市所有地区数据
                                }
                            }
                            Province_AreaList.add(City_AreaList);//添加该省所有地区数据
                        }
                    } else {
                        CityList.add("");
                        Province_AreaList.add(new ArrayList<String>());

                    }
                    /**
                     * 添加城市数据
                     */
                    options2Items.add(CityList);

                    /**
                     * 添加地区数据
                     */
                    options3Items.add(Province_AreaList);
                }

                isLoaded = false;

            } else {
                ToastUtil.showShort("解析错误");

            }
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }


    }
    @OnClick({R2.id.re_choice_address}  )
    public void onClick(View view){
        int i = view.getId();//用户信息
        if (i == R.id.re_choice_address) {
            if (!isLoaded) {
                ShowPickerView();
                hideKeyboard(view);
            } else {
//                    Tools.showInfo(mContext, "正在解析数据");
                ToastUtil.showShort("正在解析数据");
            }

        }

    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposableObserver!=null&&!disposableObserver.isDisposed()){
            disposableObserver.dispose();
        }

    }
    private void ShowPickerView() {// 弹出选择器

        final OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
//                submitArealist.clear();
                temoptions1 = options1;
                temoptions2 = options2;
                temoptions3 = options3;
                //返回的分别是三个级别的选中位置
                if (StringUtil.isEmpty(options2Items.get(options1).get(options2))) {
                    cityId = "0";
                    countryId = "0";
                    String tx = options1Items.get(options1).getPickerViewText();
                    tvChoiceAddress.setText(tx);
                    tvChoiceAddress.setTextColor(getResources().getColor(R.color.color_333333));
                    Map<String, String> map = new HashMap();
                    map.put("name", options1Items.get(options1).getName());
                    map.put("id", options1Items.get(options1).getId());
//                    submitArealist.add(map);
                    provinceId = options1Items.get(options1).getId();
                } else {
                    Map<String, String> map = new HashMap();
                    map.put("name", options1Items.get(options1).getName());
                    map.put("id", options1Items.get(options1).getId());
                    Map<String, String> map1 = new HashMap();
                    map1.put("name", options1Items.get(options1).getChild().get(options2).getName());
                    map1.put("id", options1Items.get(options1).getChild().get(options2).getId());
                    Map<String, String> map2 = new HashMap();
//                    submitArealist.add(map);
//                    submitArealist.add(map1);
//                    MyLogUtils.i("kankan","大小"+options1Items.get(options1).getChild().get(options2).getChild());
                    if(options1Items.get(options1).getChild().get(options2).getChild()!=null){
                        map2.put("name", options1Items.get(options1).getChild().get(options2).getChild().get(options3).getName());
                        map2.put("id", options1Items.get(options1).getChild().get(options2).getChild().get(options3).getId());
                        countryId = options1Items.get(options1).getChild().get(options2).getChild().get(options3).getId();
                       area = options1Items.get(options1).getChild().get(options2).getChild().get(options3).getName();
// submitArealist.add(map2);
                    }
                    String tx = options1Items.get(options1).getPickerViewText() +
                            options2Items.get(options1).get(options2) +
                            options3Items.get(options1).get(options2).get(options3);
                    tvChoiceAddress.setText(tx);
                    tvChoiceAddress.setTextColor(getResources().getColor(R.color.color_333333));
                    provinceId = options1Items.get(options1).getId();
                    province = options1Items.get(options1).getName();
                    cityId = options1Items.get(options1).getChild().get(options2).getId();
                    city = options1Items.get(options1).getChild().get(options2).getName();

//                    MyLogUtils.i(TAG, provinceId, cityId, countryId);
                }
//                if (submitArealist.size() > 0) {
//                    commitAddress = JSON.toJSONString(submitArealist);
//                }
//                MyLogUtils.i("kankan", "提交地址" + commitAddress);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.setSelectOptions(temoptions1, temoptions2, temoptions3);

        pvOptions.show();
    }

    @OnClick(R2.id.tv_right)
    public void onClickView(View view){
        addData();
    }

    public void addData(){

        String name = etNameView.getText().toString().trim();
        String phone = etMobileView.getText().toString().trim();
        String details = etAddressDetail.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            ToastUtil.showShort("收货人为空");
            return;
        }
        if (TextUtils.isEmpty(phone)){
            ToastUtil.showShort("手机号为空");
            return;
        }
        if (TextUtils.isEmpty(details)){
            ToastUtil.showShort("详细地址为空");
            return;
        }
        if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city)||TextUtils.isEmpty(area)){
            ToastUtil.showShort("请选择地址");
            return;
        }

        if (addressBean == null){
            Map<String,Object> map = new HashMap<>();
            map.put("token", UserUtils.getToken());
            Map<String,String> params = new HashMap<>();
            params.put("name",name);
            params.put("phone",phone);
            params.put("province",province);
            params.put("city",city);
            params.put("area",area);
            params.put("address",details);

            map.put("params",params);

            HttpManager.getInstance().post(mContext, ApiConstants.USERADDRESSAPP_SAVE, map, new OnIResponseListener() {
                @Override
                public void onSucceed(String result) {

                    ToastUtil.showShort("保存成功");
                    finish();
                }

                @Override
                public void onFailed(String error) {
                    ToastUtil.showShort(error);

                }
            },true);
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("token", UserUtils.getToken());

            Map<String,String> params = new HashMap<>();
            params.put("id",addressBean.getId());
            params.put("name",name);
            params.put("phone",phone);
            params.put("province",province);
            params.put("city",city);
            params.put("area",area);
            params.put("address",details);

            map.put("params",params);

            HttpManager.getInstance().post(mContext, ApiConstants.USERADDRESSAPP_UPDATE, map, new OnIResponseListener() {
                @Override
                public void onSucceed(String result) {

                    ToastUtil.showShort("修改成功");
                    finish();
                }

                @Override
                public void onFailed(String error) {
                    ToastUtil.showShort(error);

                }
            },true);
        }



    }
}
