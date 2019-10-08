package com.yun.software.kaadas.UI.activitys;

import android.widget.EditText;

import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/5/21
 */
public class GoodcarDialogManager {
    private EditText etTotal;
    private String total="1";
    private String maxValue;

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public EditText getEtTotal() {
        return etTotal;
    }

    public void setEtTotal(EditText etTotal) {
        this.etTotal = etTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void addOrReduceGoodsNum(boolean isPlus) {
        if (isPlus) {
            if (Integer.parseInt(total) >= Integer.parseInt(maxValue)){
                ToastUtil.showShort("您已经超过最大购买数量");
                return;
            }
            total = String.valueOf(Integer.parseInt(total) + 1);
        } else {
            int i = Integer.parseInt(total);
            if (i > 1) {
                total = String.valueOf(i - 1);
            } else {
                total = "1";
            }
        }
        if(etTotal!=null){
            etTotal.setText(total);
        }
    }


}
