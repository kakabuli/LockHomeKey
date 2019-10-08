package com.yun.software.kaadas.UI.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.activitys.CommitOrderActivity;
import com.yun.software.kaadas.UI.activitys.GoodcarDialogManager;
import com.yun.software.kaadas.UI.activitys.MyOderActivity;
import com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity;
import com.yun.software.kaadas.UI.adapter.StyleItmeAdapter;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;
import com.yun.software.kaadas.UI.view.RecyclerItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.SizeUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;
import la.xiong.androidquick.ui.widget.writeDialog.BottomDialog;

/**
 * Created by yanliang
 * on 2019/5/21
 */
public class PayDialog implements View.OnClickListener {
    private static PayDialog instance = null;
    LinearLayout mLinClose;
    private BottomDialog mBottomDialog;
    private GoodcarDialogManager mGcarManager;

    Context mContext;

    ImageView checkWeichatView;
    ImageView checkZhifubaoView;
    private String price;

    private String orderId;

    public synchronized static PayDialog getInstance() {
        if (instance == null) {
            instance = new PayDialog();
        }
        return instance;
    }

    public PayDialog() {
        mGcarManager=new GoodcarDialogManager();
    }

    public void payDialog(FragmentManager mFragmentManager, Context context,String id,String price) {
         this.mContext=context;
         this.orderId = id;
         this.price = price;
        if (mBottomDialog != null) {
            mBottomDialog.dismiss();
        }
        mBottomDialog = BottomDialog.create(mFragmentManager)
                .setLayoutRes(R.layout.dialog_pay)
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initDialogbuy(v);
                    }

                })
                .setDimAmount(0.5f)
                .setCancelOutside(false)
                .setTag("pay")
                .show();
    }

    private void initDialogbuy(View v) {
        ImageView closeView = v.findViewById(R.id.close);
        LinearLayout llWeichatView = v.findViewById(R.id.ll_weichat);
        checkWeichatView = v.findViewById(R.id.check_weichat);
        LinearLayout llZhifubaoView = v.findViewById(R.id.ll_zhifubao);
        checkZhifubaoView = v.findViewById(R.id.check_zhifubao);
        Button button = v.findViewById(R.id.button);

        TextView priceView = v.findViewById(R.id.price);
        priceView.setText(price);

        closeView.setOnClickListener(this);
        llWeichatView.setOnClickListener(this);
        llZhifubaoView.setOnClickListener(this);
        button.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.close) {
            mBottomDialog.dismiss();
            EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_SUBMIT_ORDER));

        } else if (i == R.id.ll_weichat) {
            checkWeichatView.setImageResource(R.drawable.icon_pay_sel);
            checkZhifubaoView.setImageResource(R.drawable.icon_pay_nor);

        } else if (i == R.id.ll_zhifubao) {
            checkWeichatView.setImageResource(R.drawable.icon_pay_nor);
            checkZhifubaoView.setImageResource(R.drawable.icon_pay_sel);

        } else if (i == R.id.button) {
            pay();

        }

    }

    private void pay(){

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("indentId",orderId);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.PAYAPP_PAYMENT, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("支付成功");
                mBottomDialog.dismiss();
                EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_PAY_SUCCESS));
            }

            @Override
            public void onFailed(String error) {

            }
        },true);
    }



}
