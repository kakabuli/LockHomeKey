package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.deviceAdd.AddZigbeeBindGatewayBean;

import java.util.List;

public class AddZigbeeBindGatewayAdapter extends BaseQuickAdapter<AddZigbeeBindGatewayBean, BaseViewHolder> {

    public AddZigbeeBindGatewayAdapter(@Nullable List<AddZigbeeBindGatewayBean> data) {
        super(R.layout.zigbee_bindgateway_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddZigbeeBindGatewayBean item) {
        helper.setText(R.id.nickname,item.getNickName());
        helper.setText(R.id.admin,item.getAdminId());
        helper.setText(R.id.gateway_id,item.getGatewayId());
        int status=item.getIsOnLine();
        //0离线，1在线
        if (status==0){
           helper.setImageResource(R.id.gateway_online_img,R.mipmap.wifi_disconnect);
           helper.setText(R.id.gateway_online_text,R.string.offline);
           helper.setTextColor(R.id.gateway_online_text, ContextCompat.getColor(MyApplication.getInstance(),R.color.c999999));
           helper.setImageResource(R.id.gateway_img,R.mipmap.gateway_detail_offline);
            helper.setImageResource(R.id.gateway_select,R.mipmap.choose_no);
        }else{
           helper.setImageResource(R.id.gateway_online_img,R.mipmap.wifi_connect);
           helper.setText(R.id.gateway_online_text,R.string.online);
           helper.setImageResource(R.id.gateway_img,R.mipmap.gateway_detail_lock);
           helper.setTextColor(R.id.gateway_online_text, ContextCompat.getColor(MyApplication.getInstance(),R.color.c1F96F7));
        }
        if (item.getIsAdmin()==1){
            ImageView imageView=helper.getView(R.id.gateway_select);
            imageView.setVisibility(View.VISIBLE);
            TextView textView=helper.getView(R.id.gateway_authorization);
            textView.setVisibility(View.INVISIBLE);
            if (item.isSelect()){
                helper.setImageResource(R.id.gateway_select,R.mipmap.choose_yes);
            }else{
                helper.setImageResource(R.id.gateway_select,R.mipmap.choose_no);
            }
        }else{
            ImageView imageView=helper.getView(R.id.gateway_select);
            imageView.setVisibility(View.INVISIBLE);
            TextView textView=helper.getView(R.id.gateway_authorization);
            textView.setVisibility(View.VISIBLE);
        }



    }
}
