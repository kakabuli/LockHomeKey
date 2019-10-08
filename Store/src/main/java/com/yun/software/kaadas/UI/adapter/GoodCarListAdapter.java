package com.yun.software.kaadas.UI.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.CarItem;

import java.util.List;

import butterknife.OnTextChanged;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.StringUtil;


public class GoodCarListAdapter extends BaseQuickAdapter<CarItem,BaseViewHolder> {


    private List<CarItem> datas ;
    public GoodCarListAdapter(List<CarItem> datas) {
        super(R.layout.adapter_item_good_car, datas);
        this.datas = datas;
    }


    @Override
    protected void convert(BaseViewHolder helper, CarItem item) {
        helper.addOnClickListener(R.id.v_add);
        helper.addOnClickListener(R.id.v_plus);
        if(item.isIscheck()){
            helper.setImageResource(R.id.iv_select_state,R.drawable.selectbox_sel);
        }else{
            helper.setImageResource(R.id.iv_select_state,R.drawable.selectbox_nor);
        }



        helper.setText(R.id.tv_name,item.getSkuProductName());
        helper.setText(R.id.tv_attr,item.getAttributeComboName());

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

        helper.addOnClickListener(R.id.re_state);
        helper.setText(R.id.tv_money,item.getPrice());
        helper.setText(R.id.et_total,StringUtil.toString(item.getQty()));


    }


}
