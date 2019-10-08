package com.yun.software.kaadas.UI.adapter;

import android.os.Handler;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.HotkeyBean;

import java.util.List;


public class SaleAdaterTypeAdapter extends BaseQuickAdapter<HotkeyBean,BaseViewHolder> {
    private List<HotkeyBean> datas ;
    private int selectPostion=-1;
    public SaleAdaterTypeAdapter(List<HotkeyBean> datas) {
        super(R.layout.adapter_item_sale_after_type, datas);
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        if (datas != null ){
            return datas.size();
        }else {
            return 0;
        }

    }
    private void specialUpdate() {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

            }
        };
        handler.post(r);
    }
    public void setSelectPostion(int selectPostion){
        this.selectPostion=selectPostion;
        notifyDataSetChanged();;

    }
    @Override
    protected void convert(BaseViewHolder helper, HotkeyBean item) {
        helper.setText(R.id.tv_des,item.getValue());
//        helper.setOnCheckedChangeListener(R.id.cb_state, new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    setSelectPostion(helper.getAdapterPosition());
//                }
//
//            }
//        });
        if(selectPostion>=0&helper.getAdapterPosition()==selectPostion){
            helper.setChecked(R.id.cb_state,true);
        }else{
            helper.setChecked(R.id.cb_state,false);
        }
//           if(selectPostion>=0&&selectPostion==helper.getAdapterPosition()){
//               helper.setTextColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_f0));
//               helper.setBackgroundColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_333));
//           }else{
//               helper.setTextColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_333));
//               helper.setBackgroundColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_f0));
//
//           }



//        helper.setText(R.id.tv_title,item.getName());
//        helper.setText(R.id.tv_total_look,item.getViewCount() + "人观看");
//        ImageView imageView = helper.getView(R.id.iv_bg);
//        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView,R.drawable.qizhi_empty_heng);

    }
}
