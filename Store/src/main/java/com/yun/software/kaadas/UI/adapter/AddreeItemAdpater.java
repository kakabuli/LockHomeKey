package com.yun.software.kaadas.UI.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.AddressBean;
import com.yun.software.kaadas.UI.bean.AddressItem;

import java.util.List;

/**
 * Created by yanliang
 * on 2018/8/20 15:14
 */

public class AddreeItemAdpater extends BaseQuickAdapter<AddressBean,BaseViewHolder> {

    public AddreeItemAdpater(List<AddressBean> datas) {
        super(R.layout.adapter_address,datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddressBean item) {
              helper.addOnClickListener(R.id.ly_edit);
              helper.addOnClickListener(R.id.lin_checkicon);
              helper.addOnClickListener(R.id.ly_delete);

              helper.setText(R.id.tv_realname,item.getName());
              helper.setText(R.id.tv_phone,item.getPhone());
              helper.setText(R.id.tv_address,item.getProvince() + item.getCity() + item.getArea()+ " " + item.getAddress());



              if("1".equals(item.getIsDefault())){
                  helper.setImageResource(R.id.iv_check_img,R.drawable.selectbox_sel);
              }else{
                  helper.setImageResource(R.id.iv_check_img,R.drawable.selectbox_nor);
              }
//            helper.setText(R.id.tv_realname,item.getName()).setText(R.id.tv_phone,item.getTel()).setText(R.id.tv_address,item.getAddressPre()+"  "+item.getAddressDetail());

    }

}
