package com.yun.software.kaadas.UI.activitys;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.OderItemListAdapter;
import com.yun.software.kaadas.UI.adapter.OderItemListAdapter2;
import com.yun.software.kaadas.UI.adapter.SaftDetailImgsAdapter;
import com.yun.software.kaadas.UI.bean.AfterSaleInfor;
import com.yun.software.kaadas.UI.bean.FeedPicture;
import com.yun.software.kaadas.UI.bean.SaleAfaterDetailItem;
import com.yun.software.kaadas.UI.view.OrderItemListView2;
import com.yun.software.kaadas.Utils.OrderStatue;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ScreenUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/5/18
 */
public class AfterSaleDetailActivity extends BaseActivity {
    List<FeedPicture> pictures;
    @BindView(R2.id.rc_imgs)
    RecyclerView rcImgs;
    SaftDetailImgsAdapter mSaftDetailImgsAdapter;
    @BindView(R2.id.commiteItem_list)
    OrderItemListView2 commiteItemListView;
    @BindView(R2.id.tv_sale_after_state)
    TextView tvSaleAfterState;
    @BindView(R2.id.tv_sale_after_complete_time)
    TextView tvCompleteTime;
    @BindView(R2.id.re_sale_after_complete)
    RelativeLayout reSaleAfterComplete;
    @BindView(R2.id.tv_sale_after_apply_time)
    TextView tvSaleAfterApplyTime;
    @BindView(R2.id.tv_sale_after_apply_type)
    TextView tvSaleAfterApplyType;
    @BindView(R2.id.tv_sale_after_apply_people)
    TextView tvSaleAfterApplyPeople;
    @BindView(R2.id.tv_sale_after_apply_phone)
    TextView tvSaleAfterApplyPhone;
    @BindView(R2.id.tv_my_feed)
    TextView tvMyFeed;
    @BindView(R2.id.tv_sale_after_apply_feed)
    TextView tvSaleAfterApplyFeed;
    @BindView(R2.id.tv_sale_after_handle_time)
    TextView tvSaleAfterHandleTime;
    @BindView(R2.id.lin_feee_result)
    LinearLayout linFeedResult;

    @BindView(R2.id.rl_lianxiren)
    RelativeLayout rlLianxiren;

    @BindView(R2.id.rl_tell)
    RelativeLayout rlTell;

    @BindView(R2.id.view_1)
    View view1;

    @BindView(R2.id.view_2)
    View view2;

    private String detailid;
    private OderItemListAdapter oderListItemAdapter;

    private String statue;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_after_sale_detail;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected void initViewsAndEvents() {

        statue = getIntent().getStringExtra("statue");

        if (statue.equals(OrderStatue.INDENT_INFO_STATUS_REFUND_OF)  || statue.equals(OrderStatue.INDENT_INFO_STATUS_REFUND_TO_COMPLETE)){
            tvTitle.setText("退款详情");
            rlLianxiren.setVisibility(View.GONE);
            rlTell.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }else {
            tvTitle.setText("售后详情");
        }

        //售后中,退款中
        if (statue.equals(OrderStatue.INDENT_INFO_STATUS_AFTER_UNDERWAY) || statue.equals(OrderStatue.INDENT_INFO_STATUS_REFUND_OF)){
            linFeedResult.setVisibility(View.GONE);
        }


        detailid = getIntent().getStringExtra("id");
        pictures = new ArrayList<>();
        Drawable decoration = ContextCompat.getDrawable(this, R.drawable.album_decoration_white);
        int itemSize = (ScreenUtils.getScreenWidth() - decoration.getIntrinsicWidth() * (3 + 1)) / 3;
        mSaftDetailImgsAdapter = new SaftDetailImgsAdapter(pictures, itemSize);
        rcImgs.setHasFixedSize(true);
        rcImgs.setLayoutManager(new GridLayoutManager(mContext, 3));
        rcImgs.setAdapter(mSaftDetailImgsAdapter);
        mSaftDetailImgsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int i = view.getId();
                if (i == R.id.view_iamge) {
                    List<String> strings = new ArrayList<>();
                    strings.add(pictures.get(position).getUrl());
                    BigImageActivity.startImagePagerActivity(AfterSaleDetailActivity.this, strings, 0);


                }
            }
        });
        getData();
    }

    private void getData() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("id", detailid);
        map.put("token", UserUtils.getToken());
        map.put("params", params);
        HttpManager.getInstance().post(mContext, ApiConstants.AFTER_SALE_DETAIL, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                LogUtils.iTag("jieguo", result);
                SaleAfaterDetailItem item = JSON.parseObject(result, SaleAfaterDetailItem.class);
                tvSaleAfterState.setText(item.getStatusCN());
                LogUtils.iTag("jieguoitem.getStatusCN()", item.getStatusCN());
                tvSaleAfterApplyPeople.setText(item.getContactName());
                tvSaleAfterApplyPhone.setText(item.getContactTel());
                tvSaleAfterApplyTime.setText(item.getCreateDate());
                tvSaleAfterApplyType.setText(item.getReasonTypeCN());
                tvMyFeed.setText(item.getReason());
                tvSaleAfterApplyFeed.setText(item.getApproveResult());
                tvSaleAfterHandleTime.setText(item.getApproveDate());
                tvCompleteTime.setText(item.getApproveDate());
                if (!item.getStatus().equals("after_sale_status_finish")) {
//                    linFeedResult.setVisibility(View.GONE);
                    tvSaleAfterState.setVisibility(View.VISIBLE);
                    reSaleAfterComplete.setVisibility(View.GONE);

                } else {
                    reSaleAfterComplete.setVisibility(View.VISIBLE);
                    tvSaleAfterState.setVisibility(View.GONE);
//                    linFeedResult.setVisibility(View.VISIBLE);


                }

                if (item.getStatus().equals(OrderStatue.INDENT_INFO_STATUS_AFTER_COMPLETE) ||
                        item.getStatus().equals(OrderStatue.INDENT_INFO_STATUS_REFUND_TO_COMPLETE)  ||
                        item.getStatus().equals(OrderStatue.INDENT_STATUS_COMPLETED)  ||
                        item.getStatus().equals(OrderStatue.INDENT_STATUS_CANCELDE)
                ){
                    tvSaleAfterState.setBackgroundColor(Color.parseColor("#999999"));
                }

                if (!StringUtil.isEmpty(item.getTbIndentInfo())) {
                    List<AfterSaleInfor> infors = JSON.parseObject(item.getTbIndentInfo(), new TypeReference<List<AfterSaleInfor>>() {
                    });
                    if (infors != null && infors.size() > 0) {
                        OderItemListAdapter2 oderListItemAdapter = new OderItemListAdapter2(mContext);
                        commiteItemListView.setAdapter(oderListItemAdapter);
                        oderListItemAdapter.setDatas(infors);
                        oderListItemAdapter.notifyDataSetChanged();
                    }
                }
                List<String> imgs = StringUtil.getImageUrls(item.getImgs());
                if (imgs.size() > 0) {
                    for (int i = 0; i < imgs.size(); i++) {
                        pictures.add(new FeedPicture("temp2", false, imgs.get(i)));
                    }
                    mSaftDetailImgsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        }, false);
    }
}
