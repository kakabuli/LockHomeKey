package com.yun.software.kaadas.UI.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.SizeUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;
import la.xiong.androidquick.ui.widget.writeDialog.BottomDialog;

import static com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity.TYPE_KANJIA;
import static com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity.TYPE_NORMAL;

/**
 * Created by yanliang
 * on 2019/5/21
 */
public class GoodCarDialog implements View.OnClickListener {
    private static GoodCarDialog instance = null;
    LinearLayout mLinClose;
    private BottomDialog mBottomDialog;
    private GoodcarDialogManager mGcarManager;
    ImageView ivAdd;
    ImageView ivCut;
    EditText etNumber;
    RecyclerView mReItemStyle;
    StyleItmeAdapter mStyleAdapter;
    Context mContext;
    private String residueNum;

    private int type;
    private int selectPosition = 0;

    private List<GoodsAttrBean> list;

    public synchronized static GoodCarDialog getInstance() {
        if (instance == null) {
            instance = new GoodCarDialog();
        }
        return instance;
    }

    public GoodCarDialog() {

    }

    public void buyDialog(FragmentManager mFragmentManager, Context context, List<GoodsAttrBean> list,int type,int selectPosition,String residueNum ,String selectColor) {
         this.mContext=context;
         this.list = list;
         this.type = type;
         this.residueNum = residueNum;
         this.selectPosition = selectPosition;
        if (mBottomDialog != null) {
            mBottomDialog.dismiss();
        }
        mGcarManager=new GoodcarDialogManager();
        mBottomDialog = BottomDialog.create(mFragmentManager)
                .setLayoutRes(R.layout.dialog_carbuy)
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initDialogbuy(v,selectColor);
                    }

                })
                .setDimAmount(0.1f)
                .setCancelOutside(false)
                .setTag("comment")
                .show();
    }

    private void initDialogbuy(View v,String selectColor) {
        mLinClose=v.findViewById(R.id.lin_close);
        ivCut=v.findViewById(R.id.v_cut);
        etNumber=v.findViewById(R.id.et_total);
        mReItemStyle=v.findViewById(R.id.item_style);
        mGcarManager.setEtTotal(etNumber);

        ivAdd=v.findViewById(R.id.v_add);
        TextView tvPrice = v.findViewById(R.id.price);
        TextView tvKucun = v.findViewById(R.id.kucun);
        ImageView imageView = v.findViewById(R.id.image);
        TextView addCart = v.findViewById(R.id.add_shopcart);
        TextView buyNow = v.findViewById(R.id.buy_now);
        for(int i = 0;i< list.size();i++){
            if(selectColor.equals(list.get(i).getAttributeComboName())){
                selectPosition = i;
            }
        }

        GoodsAttrBean bean = list.get(selectPosition);
        GlidUtils.loadImageNormal(mContext,bean.getLogo(),imageView);
        tvPrice.setText("¥"+bean.getPrice());
        tvKucun.setText("库存" + bean.getRealQty() + "件");
        tvKucun.setVisibility(View.GONE);
        if (type != TYPE_NORMAL){
            mGcarManager.setMaxValue(residueNum);
        }else {
            mGcarManager.setMaxValue(99+"");
        }


        if (type != TYPE_NORMAL){
            addCart.setVisibility(View.GONE);
            buyNow.setText("立即抢购");
            buyNow.setBackground(mContext.getDrawable(R.drawable.light_blue_btn_bg));
        }
        if (type == TYPE_KANJIA){
            buyNow.setText("发起砍价");
        }


        mLinClose.setOnClickListener(this);
        ivCut.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        addCart.setOnClickListener(this);
        buyNow.setOnClickListener(this);




        mStyleAdapter = new StyleItmeAdapter(list);
        mReItemStyle.setHasFixedSize(true);
        mReItemStyle.setLayoutManager(new GridLayoutManager(mContext,2));
        mReItemStyle.addItemDecoration(new RecyclerItemDecoration(SizeUtils.dp2px(20),2));
        mReItemStyle.setNestedScrollingEnabled(false);
        mReItemStyle.setFocusable(false);
        mReItemStyle.setAdapter(mStyleAdapter);
        mStyleAdapter.setSelectPostion(selectPosition);

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                 mGcarManager.setTotal(s.toString());
            }
        });
        mStyleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mStyleAdapter.setSelectPostion(position);
                selectPosition = position;
                GoodsAttrBean bean = list.get(position);
                GlidUtils.loadImageNormal(mContext,bean.getLogo(),imageView);
                tvPrice.setText("¥" + bean.getPrice());
                tvKucun.setText("库存" + bean.getRealQty());
                EventCenter center = new EventCenter(Constans.MESSAGE_GOODS_ATTR,selectPosition);
                EventBus.getDefault().post(center);

                if(null != priorityListener){
                    priorityListener.refreshPriorityUI(bean.getOldPrice(),bean.getPrice());
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lin_close) {
            mBottomDialog.dismiss();

        } else if (i == R.id.v_add) {
            mGcarManager.addOrReduceGoodsNum(true);

        } else if (i == R.id.v_cut) {
            mGcarManager.addOrReduceGoodsNum(false);

        } else if (i == R.id.add_shopcart) {
            if (TextUtils.isEmpty(UserUtils.getToken())) {
                Intent intent = new Intent(mContext, WxLoginActivity.class);
                mContext.startActivity(intent);
                return;
            }

            addShopcart(true);

        } else if (i == R.id.buy_now) {
            if (TextUtils.isEmpty(UserUtils.getToken())) {
                Intent intent = new Intent(mContext, WxLoginActivity.class);
                mContext.startActivity(intent);
                return;
            }


            //砍价
            if (type == TYPE_KANJIA) {

                startKanjia();
                return;
            }

            if (type != TYPE_NORMAL && (TextUtils.isEmpty(residueNum) || residueNum.equals("0"))) {
                ToastUtil.showShort("您已经超出购买数量");
                return;
            }

            buyNow();


        }

    }

    private void startKanjia() {

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("activityProductId",list.get(selectPosition).getBusinessId());
        params.put("agentProductId",list.get(selectPosition).getId());
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.APPBARGAIN_STARTBARGAIN, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("开始砍价");
                Bundle bundle = new Bundle();
                bundle.putString("id",StringUtil.getJsonKeyStr(result,"startProductId"));
                bundle.putString("agentProductId",list.get(selectPosition).getId());
                Intent intent = new Intent(mContext, KanjiaActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_FINISH_SHOP_DETAILS));
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },true);

    }

    private void buyNow() {

        Bundle bundle = new Bundle();
        if (type == TYPE_NORMAL){
            addShopcart(false);
        }else {
            bundle.putString("qty",mGcarManager.getTotal());
            bundle.putInt("type",type);
            bundle.putString("id",list.get(selectPosition).getBusinessId());
            bundle.putString("agentProductId",list.get(selectPosition).getId());
            Intent intent = new Intent(mContext, CommitOrderActivity.class);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            mContext.startActivity(intent);
        }


    }


    private  void addShopcart(boolean flag){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("agentProductId",list.get(selectPosition).getId());//productId
        params.put("qty",mGcarManager.getTotal());
        if (!flag){
            params.put("type","to_buy");
        }
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.SHOPCAR_SAVE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                if (flag){
                    ToastUtil.showShort("加入购物车成功");
                    mBottomDialog.dismiss();
                    EventBus.getDefault().post(new EventCenter(Constans.MESSAGE_REFRESH_CART));
                }else {
                    mBottomDialog.dismiss();

                    Bundle bundle = new Bundle();
                    if (type == TYPE_NORMAL){
                        String ids = StringUtil.getJsonKeyStr(result,"id");
                        ArrayList<String> items = new ArrayList<>();
                        items.add(ids);
                        bundle.putInt("type",type);
                        bundle.putStringArrayList("ids",items);

                    }
                    Intent intent = new Intent(mContext, CommitOrderActivity.class);
                    if (null != bundle) {
                        intent.putExtras(bundle);
                    }
                    mContext.startActivity(intent);

                }


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }


    /**
     * 自定义Dialog监听器
     */
    public interface Priority{
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void refreshPriorityUI(String colorType, String price);
    }

    private Priority priorityListener;

    public void setPriorityRefresh(Priority priorityListener){
        this.priorityListener = priorityListener;
    }
}
