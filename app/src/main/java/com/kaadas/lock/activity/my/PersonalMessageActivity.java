package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.PersonalMessageAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.bean.PersonalMessageBean;
import com.kaadas.lock.mvp.presenter.personalpresenter.PersonalMessagePresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetMessageResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.mvp.view.personalview.IPersonalMessageView;
import com.kaadas.lock.widget.RecyclerViewNoBugLinearLayoutManager;
import com.kaadas.lock.widget.SlideRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalMessageActivity extends BaseActivity<IPersonalMessageView, PersonalMessagePresenter<IPersonalMessageView>> implements PersonalMessageAdapter.OnDeleteClickLister, IPersonalMessageView, PersonalMessageAdapter.OnItemClickLister, View.OnClickListener {


    @BindView(R.id.message_recycler)
    SlideRecyclerView messageRecycler;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_no_message)
    RelativeLayout rlNoMessage;
    @BindView(R.id.ll_has_message)
    LinearLayout llHasMessage;

    private ArrayList<PersonalMessageBean> mPersonalMessageList;
    private DividerItemDecoration dividerItemDecoration;
    private PersonalMessageAdapter messageAdapter;
    private int currentPage = 1;
    private boolean haveMessage = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        initView();
        changeView();
        initData();
        initRefresh();
        initListener();
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.my_message);
    }


    @Override
    protected PersonalMessagePresenter<IPersonalMessageView> createPresent() {
        return new PersonalMessagePresenter();
    }

    private void initView() {
        mPersonalMessageList = new ArrayList<>();
        RecyclerViewNoBugLinearLayoutManager linearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageRecycler.setLayoutManager(linearLayoutManager);
        dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_inset));
//        messageRecycler.addItemDecoration(dividerItemDecoration);
//        messageAdapter = new PersonalMessageAdapter(this, mPersonalMessageList, R.layout.personal_message_item);
        messageRecycler.addItemDecoration(new PersonalMessageActivity.SpacesItemDecoration(DensityUtil.dp2px( 10)));
        messageAdapter = new PersonalMessageAdapter(this, mPersonalMessageList, R.layout.item_my_message_system_message, R.layout.item_my_message_share_device_authorization_message);
        messageRecycler.setAdapter(messageAdapter);
    }

    private void initData() {
        mPresenter.getMessage(MyApplication.getInstance().getUid(), currentPage);
    }

    private void initListener() {
        if (messageAdapter != null) {
            messageAdapter.setOnDeleteClickListener(this);
            messageAdapter.setOnItemClickListenerMessage(this);
        }
    }

    private void initRefresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currentPage = 1;
                if (NetUtil.isNetworkAvailable()) {
                    if (mPersonalMessageList != null) {
                        mPersonalMessageList.clear();
                    }
                    mPresenter.getMessage(MyApplication.getInstance().getUid(), currentPage);
                }
                refreshLayout.finishRefresh(2000);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getMessage(MyApplication.getInstance().getUid(), currentPage);
                refreshLayout.finishLoadMore(2000);
            }
        });
    }

    private void getData(List<GetMessageResult.DataBean> dataBeans) {
        for (int i = 0; i < dataBeans.size(); i++) {
            PersonalMessageBean personalMessageBean = new PersonalMessageBean();
            personalMessageBean.setTitle(dataBeans.get(i).getTitle());
            personalMessageBean.setContent(dataBeans.get(i).getContent());
            personalMessageBean.setTime(dataBeans.get(i).getCreateTime());
            personalMessageBean.setId(dataBeans.get(i).get_id());
            personalMessageBean.setType(dataBeans.get(i).getType());
            mPersonalMessageList.add(personalMessageBean);
        }
        messageAdapter.notifyDataSetChanged();


    }


    @Override
    public void onDeleteClick(View view, int position) {
        PersonalMessageBean messageBean = mPersonalMessageList.get(position);
        mPresenter.deleteMessage(MyApplication.getInstance().getUid(), messageBean.getId(), position);
    }

    @Override
    public void getMessageSuccess(GetMessageResult getMessageResult) {
        if (getMessageResult.getData().size() > 0) {
            currentPage++;
            getData(getMessageResult.getData());
            haveMessage = true;
            changeView();
        }

    }

    @Override
    public void getMessageFail(GetMessageResult getMessageResult) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(this, getMessageResult.getCode()));
    }

    @Override
    public void getMessageError(Throwable e) {
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void deleteSuccess(int position) {
        mPersonalMessageList.remove(position);
        messageAdapter.notifyDataSetChanged();
        messageRecycler.closeMenu();
    }

    @Override
    public void deleteError(Throwable throwable) {
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void deleteFail(BaseResult baseResult) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void onItemClick(View view, int position) {
        //点击Item
        Intent mDetailIntent = new Intent(this, PersonalMessageDetailActivity.class);
        PersonalMessageBean messageBean = mPersonalMessageList.get(position);
        mDetailIntent.putExtra(KeyConstants.MESSAGE_DETAIL_TIME, messageBean.getTime());
        mDetailIntent.putExtra(KeyConstants.MESSAGE_DETAIL_TITLE, messageBean.getTitle());
        mDetailIntent.putExtra(KeyConstants.MESSAGE_DETAIL_CONTENT, messageBean.getContent());
        startActivity(mDetailIntent);

    }

    private void changeView() {
        if (haveMessage) {
            rlNoMessage.setVisibility(View.GONE);
            llHasMessage.setVisibility(View.VISIBLE);
        } else {
            rlNoMessage.setVisibility(View.VISIBLE);
            llHasMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.left = space;
//            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
//                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}
