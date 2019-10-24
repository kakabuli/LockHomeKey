package com.yun.software.kaadas.UI.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.SearchListAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.view.TextFlowLayout;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;

public class SearchActivity extends BaseActivity {

    private String[] mLabels ;


    @BindView(R2.id.text_flow_layout)
    TextFlowLayout textFlowLayout;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R2.id.iv_close)
    ImageView ivCloseView;

    @BindView(R2.id.edit_text)
    EditText editTextView;

    @BindView(R2.id.tv_hot)
    TextView tvHotView;

    private SearchListAdapter adapter;
    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private String searcherKey;
    private List<GoodsBean> list = new ArrayList<>();

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_search;
    }

    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }

    @Override
    protected void initViewsAndEvents() {
        StatusBarUtil.setLightMode(this);
        getHotData();
        editTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && ivCloseView.getVisibility() == View.GONE) {
                    ivCloseView.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    ivCloseView.setVisibility(View.GONE);
                }
            }
        });


        adapter = new SearchListAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
                getSeacherData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (startIndex * pageSize >= total){
                    ToastUtil.showShort("没有更多数据了");
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                startIndex += 1;
                getSeacherData();
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                startToShopDetails(ShopDetailsNewActivity.TYPE_NORMAL,list.get(position));
            }
        });


    }


    private void startToShopDetails(int type,GoodsBean bean ){
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        bundle.putString("statue",bean.getBusinessType());
        bundle.putString("id",bean.getId());
        bundle.putString("actid",bean.getActivityProductId());
        readyGo(ShopDetailsNewActivity.class,bundle);
    }

    // 初始化标签
    private void initLabel() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 30, 10, 10);// 设置边距
        for (int i = 0; i < mLabels.length; i++) {

            final TextView textView = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.text_flow_layout_item,null);
            textView.setTag(i);
            textView.setText(mLabels[i]);
            textFlowLayout.addView(textView, layoutParams);
            // 标签点击事件
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searcherKey = mLabels[(int) textView.getTag()];
                    editTextView.setText(searcherKey);
                    showResult();
                }
            });
        }
    }

    @OnClick({R2.id.iv_close,R2.id.tv_search,R2.id.iv_back})
    public void OnClick(View view){
        int i = view.getId();
        if (i == R.id.iv_close) {
            editTextView.setText("");

        } else if (i == R.id.tv_search) {
            searcherKey = editTextView.getText().toString().trim();
            showResult();

        } else if (i == R.id.iv_back) {
            finish();

        }
    }

    private void showResult() {
        mRefreshLayout.setVisibility(View.VISIBLE);
        tvHotView.setVisibility(View.GONE);
        textFlowLayout.setVisibility(View.GONE);
        getSeacherData();
    }

    private void getSeacherData() {
        toggleRestore();
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        Map<String,Object> params = new HashMap<>();
        params.put("key",searcherKey);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.HOME_GETFIRSTSEARCH, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                    if(startIndex == 1){
                        list.clear();
                    }
                Gson gson = new Gson();
                BaseBody<GoodsBean> baseBody = gson.fromJson(result,new TypeToken<BaseBody<GoodsBean>>(){}.getType());
                list.addAll(baseBody.getRows());
                total = baseBody.total;
                adapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
                if (list.size() == 0){
                    toggleShowEmptyImage(true, R.drawable.contents_missing_pages, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                    }
                });

            }
        },true);
    }


    private void getHotData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.COMMONAPP_GETALLKEYVALUE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String keyjson = StringUtil.getJsonKeyStr(result,"hot_key");
                Gson gson = new Gson();
                List<HotkeyBean> list = gson.fromJson(keyjson,new TypeToken <List<HotkeyBean>>(){}.getType());
                mLabels = new String[list.size()];
                for (int i=0 ; i<list.size() ; i++){
                    mLabels[i] = list.get(i).getValue();
                }
                initLabel();
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }


}
