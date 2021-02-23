package com.yun.software.kaadas.UI.view.dialog;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.activitys.GoodcarDialogManager;
import com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity;
import com.yun.software.kaadas.UI.adapter.CouponItmeAdapter;
import com.yun.software.kaadas.UI.adapter.CouponSelListAdapter;
import com.yun.software.kaadas.UI.adapter.StyleItmeAdapter;
import com.yun.software.kaadas.UI.bean.CouponBean;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;
import com.yun.software.kaadas.Utils.UserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import la.xiong.androidquick.ui.eventbus.EventCenter;
import la.xiong.androidquick.ui.widget.writeDialog.BottomDialog;

/**
 * Created by yanliang
 * on 2019/5/21
 */
public class CouponDialog implements View.OnClickListener {
    private static CouponDialog instance = null;
    LinearLayout mLinClose;
    private BottomDialog mBottomDialog;
    RecyclerView mReItemStyle;
    CouponSelListAdapter listAdapter;
    Context mContext;
    RelativeLayout tvEmpty;

    private int type;
    private int selectPosition = 0;

    private List<CouponBean> list;

    public synchronized static CouponDialog getInstance() {
        if (instance == null) {
            instance = new CouponDialog();
        }
        return instance;
    }

    public CouponDialog() {

    }

    public void showDialog(FragmentManager mFragmentManager, Context context, List<CouponBean> list,int selectPosition) {
         this.mContext=context;
         this.list = list;
         this.selectPosition = selectPosition;
        if (mBottomDialog != null) {
            mBottomDialog.dismiss();
        }
        mBottomDialog = BottomDialog.create(mFragmentManager)
                .setLayoutRes(R.layout.dialog_coupon)
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initDialogbuy(v);
                    }

                })
                .setDimAmount(0.1f)
                .setCancelOutside(true)
                .setTag("comment")
                .show();
    }

    private void initDialogbuy(View v) {
        mLinClose=v.findViewById(R.id.lin_close);
        mReItemStyle=v.findViewById(R.id.item_style);
        tvEmpty = v.findViewById(R.id.rl_empty);
        TextView sureBtn = v.findViewById(R.id.btn_sure);
        TextView cancelBtn = v.findViewById(R.id.btn_cancel);


        listAdapter = new CouponSelListAdapter(list);
        mReItemStyle.setHasFixedSize(true);
        mReItemStyle.setLayoutManager(new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        mReItemStyle.setAdapter(listAdapter);


        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               for (int i=0; i<list.size(); i++){
                   if (i != position){
                       list.get(i).setCheck(false);
                   }
               }
                list.get(position).setCheck(!list.get(position).isCheck());
                listAdapter.setSelectPostion(position);
                selectPosition = position;

            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null && list.size() != 0){
                    EventBus.getDefault().post(new EventCenter<CouponBean>(Constans.MESSAGE_ORDER_COUPON,list.get(selectPosition)));
                    if (!list.get(selectPosition).isCheck()){
                        //不用券的时候,选中最后一个.
                        selectPosition = list.size() -1;
                    }
                }
                mBottomDialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomDialog.dismiss();
            }
        });

        if (list == null || list.size() == 0){
            mReItemStyle.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }else {
            listAdapter.setSelectPostion(selectPosition);
            if (selectPosition == list.size() -1){
                list.get(selectPosition).setCheck(false);
            }else {
                list.get(selectPosition).setCheck(true);
            }


        }


    }


    @Override
    public void onClick(View v) {

    }
}
