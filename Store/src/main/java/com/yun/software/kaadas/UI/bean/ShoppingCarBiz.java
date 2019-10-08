package com.yun.software.kaadas.UI.bean;

import android.widget.TextView;

import com.yun.software.kaadas.Utils.AmountUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yanliang
 * on 2018/8/31 15:02
 */

public class ShoppingCarBiz {

    /**
     * 增减数量，操作通用，数据不通用
     */
    public static <T> int addOrReduceGoodsNum(boolean isPlus, T t, TextView tvNum) {
        //        String currentNum = goods.getNumber().trim();
        String currentNum = tvNum.getText().toString();
        String num = "1";
        if (isPlus) {
            num = String.valueOf(Integer.parseInt(currentNum) + 1);
        } else {
            int i = Integer.parseInt(currentNum);
            if (i > 1) {
                num = String.valueOf(i - 1);
            } else {
                num = "1";
            }
        }
        //        String productID = goods.getProductID();
        //        MyLogUtils.i("addnumber","num="+num);
        tvNum.setText(num);
        return Integer.valueOf(num);
        //        goods.setNumber(num);
        //        updateGoodsNumber(productID, num);
    }

    public static boolean checkItemCheckAll(List<CarItem> lists) {
        if (lists == null) {
            return false;
        } else {
            for (int i = 0; i < lists.size(); i++) {
                if (!lists.get(i).isIscheck()) {
                    return false;
                }
            }
            return true;
        }

    }

    public static void setIsItemCheckAll(List<CarItem> lists, boolean ischekd) {
        if (lists == null) {
            return;
        } else {
            for (int i = 0; i < lists.size(); i++) {
                lists.get(i).setIscheck(ischekd);
            }

        }
    }

    public static boolean isAllChecke(List<CarItem> lists) {
        if (lists == null) {
            return false;
        } else {
            for (int i = 0; i < lists.size(); i++) {
                if (!lists.get(i).isIscheck()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String getTotalMoney(List<CarItem> mDatas) {
        double money = 0;

            for (int i1 = 0; i1 < mDatas.size(); i1++) {
                    if (mDatas.get(i1).isIscheck()) {
                        money = AmountUtil.add(money,Double.valueOf(mDatas.get(i1).getTotalMoney()),2);
                    }
            }
        DecimalFormat mFormat = new DecimalFormat("#0.00");
        String totalMoney=mFormat.format((double)money);
        return totalMoney ;
    }

    public static int getSelectCount(List<CarItem> mDatas) {
        int count = 0;

        for (int i1 = 0; i1 < mDatas.size(); i1++) {
            if (mDatas.get(i1).isIscheck()) {
                count ++;
            }
        }

        return count;
    }
//
//    public static String getIds(List<CarItem> mDatas) {
////
////        String ids = "";
////        for (int i = 0; i < mDatas.size(); i++) {
////            List<CarItem.ListBean> items = mDatas.get(i).getList();
////            for (int i1 = 0; i1 < items.size(); i1++) {
////                if (items.get(i1).getProduct().isIschecke()) {
////                    ids += items.get(i1).getId() + ",";
////                }
////            }
////        }
////        if (StringUtils.isEmpty(ids)) {
////            return null;
////        } else {
////            return ids.substring(0, ids.length() - 1);
////        }
//
//
//    }



}
