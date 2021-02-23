package com.yun.software.kaadas.UI.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.CouponNewListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.CouponBean;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import la.xiong.androidquick.tool.ToastUtil;
import la.xiong.androidquick.ui.eventbus.EventCenter;

public class CouponNewFragment extends BaseFragment {

    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.edit_text)
    EditText editText;

    @BindView(R2.id.root_view)
    LinearLayout rootView;

    private CouponNewListAdapter listAdapter;


    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private List<CouponBean> listBeans = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_coupon_new;
    }

    @Override
    protected View getLoadingTargetView() {
        return rootView;
    }

    @Override
    protected void initViewsAndEvents() {


        listAdapter = new CouponNewListAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(mContext);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dp_2));
        recyclerView.addItemDecoration(decoration.build());

        recyclerView.setAdapter(listAdapter);
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                listBeans.clear();
                getData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
                getData();
            }
        });
        listAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EventBus.getDefault().post(new EventCenter<>(Constans.MESSAGE_GOTO_HOME));
                getActivity().finish();
            }
        });
        getData();

    }

    @OnClick(R2.id.button)
    public void onClickView(View view){
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)){
            ToastUtil.showShort("请输入激活码");
            return;
        }
        sendData(text);
    }

    private void sendData(String text) {



        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("activationCode",text);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.COUPON_ACTIVATION, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("激活成功");
                startIndex = 1;
                listBeans.clear();
                getData();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },true);
    }


    private void getData() {
        toggleShowLoading(true,"正在加载");

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        HttpManager.getInstance().post(mContext, ApiConstants.COUPON_GETMYCOUPON, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                toggleRestore();
                Gson gson = new Gson();
                BaseBody<CouponBean> baseBody = gson.fromJson(result,new TypeToken<BaseBody<CouponBean>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;
                listAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
                if(total==0){
                    toggleShowEmptyImage(true, R.drawable.coupon_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleRestore();
                            getData();
                        }
                    });
                }
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
                toggleShowEmptyImage(true, R.drawable.loading_missing_pages, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRestore();
                        getData();
                    }
                });

            }
        },false);
    }


}
