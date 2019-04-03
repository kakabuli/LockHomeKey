package com.kaadas.lock.activity.my;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.MyMessageAdapter;
import com.kaadas.lock.bean.MyMessageBean;
import com.kaadas.lock.utils.DpPxConversion;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.widget.SlideRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/2
 */
public class MyMessageActivity extends AppCompatActivity implements View.OnClickListener, MyMessageAdapter.OnDeleteClickLister, MyMessageAdapter.OnItemClickLister {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_no_message)
    RelativeLayout rlNoMessage;
    @BindView(R.id.rv)
    SlideRecyclerView rv;
    MyMessageAdapter myMessageAdapter;
    @BindView(R.id.ll_has_message)
    LinearLayout llHasMessage;
    private DividerItemDecoration dividerItemDecoration;
    List<MyMessageBean> myMessageBeanList;
    boolean haveMessage = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.my_message);
        initRecycleView();
        initListener();
        changeView();
    }

    private void initRecycleView() {

        myMessageBeanList = new ArrayList<>();
        myMessageBeanList.add(new MyMessageBean(R.mipmap.default_head + "", "", 0l, "", "", KeyConstants.SYSTEM_MESSAGE));
        myMessageBeanList.add(new MyMessageBean(R.mipmap.default_head + "", "", 0l, "", "", KeyConstants.SHARE_DEVICE_AUTHORIZATION_MESSAGE));
        myMessageBeanList.add(new MyMessageBean(R.mipmap.default_head + "", "", 0l, "", "", KeyConstants.GATEWAY_AUTHORIZATION_MESSAGE));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        rv.addItemDecoration(new SpacesItemDecoration(DpPxConversion.getInstance().dp2px(this, 10)));
        myMessageAdapter = new MyMessageAdapter(this, myMessageBeanList, R.layout.item_my_message_system_message, R.layout.item_my_message_share_device_authorization_message);
        rv.setAdapter(myMessageAdapter);
    }

    private void initListener() {
        if (myMessageAdapter != null) {
            myMessageAdapter.setOnDeleteClickListener(this);
            myMessageAdapter.setOnItemClickListenerMessage(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onDeleteClick(View view, int position) {
        myMessageBeanList.remove(position);
        myMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {

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

    private void changeView() {
        if (haveMessage) {
            rlNoMessage.setVisibility(View.GONE);
            llHasMessage.setVisibility(View.VISIBLE);
        } else {
            rlNoMessage.setVisibility(View.VISIBLE);
            llHasMessage.setVisibility(View.GONE);
        }
    }
}
