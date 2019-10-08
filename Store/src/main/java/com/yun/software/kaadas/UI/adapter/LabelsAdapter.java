package com.yun.software.kaadas.UI.adapter;

import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.ActDetails;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.view.CheckableButton;

import java.util.List;


public class LabelsAdapter extends BaseQuickAdapter<HotkeyBean,BaseViewHolder> {


    private List<HotkeyBean> datas ;
    public LabelsAdapter(List<HotkeyBean> datas) {
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
    protected void convert(BaseViewHolder helper, HotkeyBean item) {
        CheckableButton button = helper.getView(R.id.button);
        button.setChecked(item.isCheck());
        button.setText(item.getValue());
        helper.addOnClickListener(R.id.button);

    }
}
