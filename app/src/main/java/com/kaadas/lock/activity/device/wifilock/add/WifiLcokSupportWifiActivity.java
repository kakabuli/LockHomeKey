package com.kaadas.lock.activity.device.wifilock.add;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.RouteListAdapter;
import com.kaadas.lock.bean.RouteBean;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLcokSupportWifiActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.rv_route_list)
    RecyclerView rvRouteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_wifi);
        ButterKnife.bind(this);
        List<RouteBean> routeBeans = initRouteBean();
        rvRouteList.setLayoutManager(new LinearLayoutManager(this));
        RouteListAdapter routeListAdapter = new RouteListAdapter(routeBeans);
        rvRouteList.setAdapter(routeListAdapter);
    }

    @OnClick(R.id.back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    private List<RouteBean> initRouteBean() {
        List<RouteBean> routeBeans = new ArrayList<>();

        routeBeans.add(new RouteBean("HUAWEI", "WS860S\n" +
                "Hornor HiRouter"));
        routeBeans.add(new RouteBean("TP-LINK", "TL-1043ND\n" +
                "WDR2041N\n" +
                "TL-WR866N\n" +
                "TL-WR840N\n" +
                "TL-WR841N\n" +
                "TL-TW845N\n" +
                "TL-WDR5600\n" +
                "TL-WR703N\n" +
                "Archer-D7b\n" +
                "Archer-C9\n" +
                "TL-WDR5600\n" +
                "TL-WDR4310\n" +
                "TL-WDR7400"));
        routeBeans.add(new RouteBean("Mi", "R1CL\n" +
                "R1C\n" +
                "R3P\n" +
                "MI-3"));
        routeBeans.add(new RouteBean("ASUS", "RT-N16\nRT-N66U\nRT-AC87U"));
        routeBeans.add(new RouteBean("Tenda", "811RV2\n" +
                "F6\n" +
                "302R\n" +
                "N318\n" +
                "304R\nF9\nAC10\nAC6\nFH456\n837R\nAC9"));
        routeBeans.add(new RouteBean("MERCURY", "MW305R\n" +
                "MW310R\n" +
                "MW325R"));
        routeBeans.add(new RouteBean("360", "P3"));
        routeBeans.add(new RouteBean("Netgear", "R6220\n" +
                "KWGR614\n" +
                "R7000\n" +
                "R2000\nWNDR3800\nWDR3700v4\nR6300v2\nR6800\nR6120"));
        routeBeans.add(new RouteBean("Linksys", "EA6900"));
        routeBeans.add(new RouteBean("FAST", "FW310\n" +
                "FW450R"));
        routeBeans.add(new RouteBean("D-Link", "DIR-822\n" +
                "DIR-859\n" +
                "DIR-600LW\n" +
                "DIR-612\n" +
                "DIR-605L\n" +
                "DIR-809\n" +
                "DIR-619L"));
        routeBeans.add(new RouteBean("飞鱼星", "VF35A\nVF35A"));
        routeBeans.add(new RouteBean("TOTOLINK", "N301RT"));
        routeBeans.add(new RouteBean("大麦无线", "DW22D"));
        routeBeans.add(new RouteBean("PHICOMM", "FIR300C\nK3C"));
        routeBeans.add(new RouteBean("GAOKE", "Q370R"));
        routeBeans.add(new RouteBean("极路由", "HC5761\nHC5761"));
        routeBeans.add(new RouteBean("乐视", "LBA-047-CH"));
        routeBeans.add(new RouteBean("Netcore", "737W\nAC1"));
        routeBeans.add(new RouteBean("Antbang", "A3S\nA5"));
        routeBeans.add(new RouteBean("Youku", "YK-L1C"));
        routeBeans.add(new RouteBean("ZTE", "E5501S"));
        routeBeans.add(new RouteBean("UTT", "AW750"));
        routeBeans.add(new RouteBean("Buffalo", "WHR-HP-GN\nAG300H"));
        routeBeans.add(new RouteBean("CMCC", "AP218"));
        routeBeans.add(new RouteBean("ZyXEL", "NBG6617\nNBG6503"));
        routeBeans.add(new RouteBean("netis", "WF2533"));
        routeBeans.add(new RouteBean("TRENDnet", "TEW711BR"));
        routeBeans.add(new RouteBean("Haier", "RT-A6"));
        routeBeans.add(new RouteBean("SITECOM", "WLM4600INT v"));
        routeBeans.add(new RouteBean("EnGenius", "ESR350"));
        routeBeans.add(new RouteBean("BELKIN", "AC1800"));
        routeBeans.add(new RouteBean("CISCO", "WRVS4400N"));

        routeBeans.add(new RouteBean("LB-Link", "BL-9101\n" +
                "97\n" +
                "WR4000\nBL-AC886M\nBL-845R\nD9103"));
        routeBeans.add(new RouteBean("Lenovo", "R6400\nR3220"));
        routeBeans.add(new RouteBean("Yueme", "HG228GI"));
        routeBeans.add(new RouteBean("Newifi", "D1\nR6830"));
        return routeBeans;
    }


}
