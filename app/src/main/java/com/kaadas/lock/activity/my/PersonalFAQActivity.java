package com.kaadas.lock.activity.my;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.PersonalFAQAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.bean.FAQBean;
import com.kaadas.lock.mvp.presenter.personalpresenter.PersonalFAQPresenter;
import com.kaadas.lock.publiclibrary.http.result.GetFAQResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.mvp.view.personalview.IPersonalFAQView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalFAQActivity extends BaseActivity<IPersonalFAQView, PersonalFAQPresenter<IPersonalFAQView>> implements IPersonalFAQView, View.OnClickListener {


    @BindView(R.id.faq_recyclerView)
    RecyclerView faqRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    private List<FAQBean> mFaqList;

    private PersonalFAQAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_faq);
        ButterKnife.bind(this);
        mFaqList = new ArrayList<>();
        initView();
        initData();
        initListener();
        initRefresh();
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.faq);
    }

    private void initView() {
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PersonalFAQAdapter(mFaqList);
        faqRecyclerView.setAdapter(adapter);
    }

    private void initRefresh() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (NetUtil.isNetworkAvailable()) {
                    //todo 国际化的时候记得传入此时的语言类型
                    mPresenter.getFAQList(1);
                } else {
                    ToastUtil.getInstance().showShort(R.string.noNet);
                }
                refreshLayout.finishRefresh(2000);
            }
        });
    }

    private void initData() {
        String mFaq = CacheFloder.readFAQ(ACache.get(MyApplication.getInstance()), "FAQ");
        if (mFaq != null) {
            Gson gson = new Gson();
            GetFAQResult getFAQResult = gson.fromJson(mFaq, GetFAQResult.class);
            getData(getFAQResult.getData());
        } else {
            if (NetUtil.isNetworkAvailable()) {
                //todo 国际化的时候记得传入此时的语言类型
                mPresenter.getFAQList(1);
            } else {
                ToastUtil.getInstance().showShort(R.string.noNet);
            }
        }
    }

    @Override
    protected PersonalFAQPresenter createPresent() {
        return new PersonalFAQPresenter();
    }


    private void initListener() {

    }


    @Override
    public void getFAQSuccessListView(GetFAQResult baseResult, String s) {
        if (baseResult.getData() != null) {
            CacheFloder.writeFAQ(ACache.get(MyApplication.getInstance()), "FAQ", s);
            getData(baseResult.getData());
        }
    }

    @Override
    public void getFAQError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void getFAQFail(GetFAQResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }


    private void getData(List<GetFAQResult.DataBean> dataBeans) {
        if (mFaqList != null) {
            mFaqList.clear();
        }
        for (int i = 0; i < dataBeans.size(); i++) {
            FAQBean faqBean = new FAQBean();
            faqBean.setTitle(dataBeans.get(i).getQuestion());
            faqBean.setContent(dataBeans.get(i).getAnswer());
            mFaqList.add(faqBean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
