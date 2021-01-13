package com.kaadas.lock.fragment.help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.PersonalFAQAdapter;
import com.kaadas.lock.bean.FAQBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordWeekPresenter;
import com.kaadas.lock.mvp.presenter.personalpresenter.PersonalFAQPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordWeekView;
import com.kaadas.lock.mvp.view.personalview.IPersonalFAQView;
import com.kaadas.lock.publiclibrary.http.result.GetFAQResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalFAQLockHelpFragment  extends BaseFragment<IPersonalFAQView, PersonalFAQPresenter<IPersonalFAQView>>
        implements IPersonalFAQView, View.OnClickListener {

    @BindView(R.id.faq_recyclerView)
    RecyclerView faqRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<FAQBean> mFaqList;

    private PersonalFAQAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_faq_lock_help, null);
        ButterKnife.bind(this, view);
        mFaqList = new ArrayList<>();

        initView();
        initData();
        initListener();
        initRefresh();

        return view;
    }

    private void initView() {
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PersonalFAQAdapter(mFaqList);
        faqRecyclerView.setAdapter(adapter);
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

    private void initListener() {

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

    @Override
    protected PersonalFAQPresenter createPresent() {
        return new PersonalFAQPresenter();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

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
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
    }

    @Override
    public void getFAQFail(GetFAQResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(getActivity(), baseResult.getCode()));
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
}
