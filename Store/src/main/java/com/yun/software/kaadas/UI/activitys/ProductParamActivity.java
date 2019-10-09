package com.yun.software.kaadas.UI.activitys;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.AAAAListAdapter;
import com.yun.software.kaadas.UI.adapter.ParamsListAdapter;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;
import com.yun.software.kaadas.UI.bean.ProductParams;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;

public class ProductParamActivity extends BaseActivity {

    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    String id ;

    ParamsListAdapter listAdapter;
    private List<ProductParams> listBeans = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_product_param;
    }

    @Override
    protected void initViewsAndEvents() {
        id = getIntent().getStringExtra("id");
        getData();
        listAdapter=new ParamsListAdapter(listBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(listAdapter);
    }

    @OnClick(R2.id.btn_sure)
    public void onClickView(View view){

        finish();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
    }


    /**
     *获取商品属性
     */
    private  void getData(){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.PARAMETERAPP_GETPARAMETERAPP, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                List<ProductParams> listAttr = gson.fromJson(result,new TypeToken<List<ProductParams>>(){}.getType());
                listBeans.addAll(listAttr);
                listAdapter.notifyDataSetChanged();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }



}
