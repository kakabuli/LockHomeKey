package com.kaadas.lock.activity.my;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.PersonalFAQAdapter;
import com.kaadas.lock.fragment.help.PersonalFAQHangerHelpFragment;
import com.kaadas.lock.fragment.help.PersonalFAQLockHelpFragment;
import com.kaadas.lock.fragment.help.WifiVideoLockCommonProblemHelpFragment;
import com.kaadas.lock.fragment.help.WifiVideoLockToConfigureHelpFragment;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.bean.FAQBean;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
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
import butterknife.OnClick;

public class PersonalFAQActivity extends BaseAddToApplicationActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.v_left)
    View vLeft;
    @BindView(R.id.v_right)
    View vRight;
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private PersonalFAQLockHelpFragment mPersonalFAQLockHelpFragment;
    private PersonalFAQHangerHelpFragment mPersonalFAQHangerHelpFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_faq);
        ButterKnife.bind(this);

        tvContent.setText(R.string.faq);
        initFragment();
    }

    @OnClick({R.id.iv_back,R.id.rl_left,R.id.rl_right})
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_left:
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                vLeft.setVisibility(View.VISIBLE);
                vRight.setVisibility(View.GONE);
                tvLeft.setTextColor(Color.parseColor("#1F96F7"));
                tvRight.setTextColor(Color.parseColor("#333333"));
                if( mPersonalFAQLockHelpFragment != null){
                    fragmentTransaction.show(mPersonalFAQLockHelpFragment);
                }else{
                    mPersonalFAQLockHelpFragment = new PersonalFAQLockHelpFragment();
                    fragmentTransaction.add(R.id.content,mPersonalFAQLockHelpFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.rl_right:
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                vLeft.setVisibility(View.GONE);
                vRight.setVisibility(View.VISIBLE);
                tvRight.setTextColor(Color.parseColor("#1F96F7"));
                tvLeft.setTextColor(Color.parseColor("#333333"));
                if( mPersonalFAQHangerHelpFragment != null){
                    fragmentTransaction.show(mPersonalFAQHangerHelpFragment);
                }else{
                    mPersonalFAQHangerHelpFragment = new PersonalFAQHangerHelpFragment();
                    fragmentTransaction.add(R.id.content,mPersonalFAQHangerHelpFragment);
                }
                fragmentTransaction.commit();
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {
        if (ft == null) {
            return;
        }
        if (mPersonalFAQLockHelpFragment != null) {
            ft.hide(mPersonalFAQLockHelpFragment);
        }
        if (mPersonalFAQHangerHelpFragment != null) {
            ft.hide(mPersonalFAQHangerHelpFragment);
        }
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        vLeft.setVisibility(View.VISIBLE);
        vRight.setVisibility(View.GONE);
        tvLeft.setTextColor(Color.parseColor("#1F96F7"));
        tvRight.setTextColor(Color.parseColor("#333333"));
        mPersonalFAQLockHelpFragment = new PersonalFAQLockHelpFragment();
        transaction.add(R.id.content, mPersonalFAQLockHelpFragment);
        transaction.commit();
    }
}
