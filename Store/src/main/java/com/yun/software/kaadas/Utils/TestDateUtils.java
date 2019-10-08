package com.yun.software.kaadas.Utils;

import com.yun.software.kaadas.UI.bean.AddressItem;
import com.yun.software.kaadas.UI.bean.CarItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanliang
 * on 2019/5/15
 */
public class TestDateUtils {



    public static List<CarItem> getcarItems() {
        List<CarItem> listBeans=new ArrayList<>();
        for (int i = 0; i <6 ; i++) {
            listBeans.add(new CarItem());
        }
        return listBeans;
    }

    public static List<AddressItem> getAddress() {
        List<AddressItem> listBeans=new ArrayList<>();
        for (int i = 0; i <3; i++) {
            listBeans.add(new AddressItem());
        }
        return listBeans;
    }


    public static List<Object> getStyles() {
        List<Object> listBeans=new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            listBeans.add(new AddressItem());
        }
        return listBeans;
    }
    public static List<String> getStyles2() {
        List<String> listBeans=new ArrayList<>();
        listBeans.add("多拍，错拍，不想要");
        listBeans.add("比喜欢效果不好");
        listBeans.add("货物与描述不符");
        listBeans.add("质量问题");
        listBeans.add("收到商品少件，破损或污渍");
        listBeans.add("卖家发错货");
        listBeans.add("假冒品牌");
        listBeans.add("其它");

        return listBeans;
    }

}
