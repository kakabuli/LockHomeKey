package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.view.CheckableButton;

import java.util.List;


public class CommentLabelsAdapter extends BaseQuickAdapter<HotkeyBean,BaseViewHolder> {


    private List<HotkeyBean> datas ;
    public CommentLabelsAdapter(List<HotkeyBean> datas) {
        super(R.layout.item_radiobutton, datas);
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

    @Override
    public int getItemViewType(int position) {
        if (position == 0 ||position == 1 || position == 2){
            return 1;
        }else {
            return 2;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, HotkeyBean item) {

        CheckableButton button = helper.getView(R.id.button);
        if (helper.getItemViewType() == 1 ){
            button.setBackgroundResource(R.drawable.selector_bg_rb_comment);
        }else if (helper.getItemViewType() == 2){
            button.setBackgroundResource(R.drawable.selector_bg_rb_com);
        }


        button.setChecked(item.isCheck());
        button.setText(item.getValue());
        if (item.isCheck()){
            button.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            button.setTextColor(mContext.getResources().getColor(R.color.color_333333));
        }

        helper.addOnClickListener(R.id.button);

    }
}
