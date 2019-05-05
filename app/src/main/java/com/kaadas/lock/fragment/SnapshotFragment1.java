package com.kaadas.lock.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.PreviewActivity;
import com.kaadas.lock.adapter.PirHistoryAdapter;
import com.kaadas.lock.adapter.TimeAdapter;
import com.kaadas.lock.bean.MyDate;
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.widget.GravityPopup;
import com.kaadas.lock.widget.GravityPopup.HidePopup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//<ISnapShotView, SnapPresenter<ISnapShotView>>
//implements ISnapShotView
public class SnapshotFragment1 extends CallBackBaseFragment<ISnapShotView, SnapPresenter<ISnapShotView>> implements ISnapShotView {


    @BindView(R.id.pir_history_notic_tv)
    TextView pir_history_notic_tv;  //通知
    @BindView(R.id.pir_history_all_ll)
    LinearLayout pir_history_add_ll; //通知X
    @BindView(R.id.pir_history_add_cancle)
    LinearLayout pir_history_add_cancle;
    @BindView(R.id.history_rv_ff)
    SwipeMenuRecyclerView history_rv_ff;

    @BindView(R.id.snapshot_smartrefresh)
    SmartRefreshLayout snapshot_smartrefresh;


    @Override
    protected SnapPresenter<ISnapShotView> createPresent() {
        return new SnapPresenter();
    }

    @Override
    public View initView(LayoutInflater inflater,  ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_snapshot, container, false);
        return view;
    }

    @Override
    public void initOtherFunction() {
        initPIR();
    }

    //    public void testMethod(View view){
//        date_mPopupWindow.notifydatechangeMonth(month12,true);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.year_select_ll:
                mPopupWindow.showPopupWindow(v);

                break;
            case  R.id.day_select_ll:
                date_mPopupWindow.showPopupWindow(v);
                break;
            case  R.id.pir_history_add_cancle:
                pir_history_add_ll.setVisibility(View.GONE);
                break;

        }
    }

    PirHistoryAdapter pirHistoryAdapter;
    List<String> imageList=new ArrayList<>();
    int catEyeCount=-1;
    private  void initPIR(){
        pir_history_add_cancle.setOnClickListener(this);
        String format= String.format( getActivity().getResources().getString(R.string.pir_history_notic), catEyeCount);
        pir_history_notic_tv.setText(format);

        for (int i=0;i<20;i++){
            imageList.add("2019-04-"+i+"12:34:23");
        }


        history_rv_ff.setSwipeItemClickListener(mItemClickListener); // RecyclerView Item点击监听。
        // 侧滑
        history_rv_ff.setSwipeMenuCreator(swipeMenuCreator);
        history_rv_ff.setSwipeMenuItemClickListener(mMenuItemClickListener);

        pirHistoryAdapter = new PirHistoryAdapter(getActivity(),imageList);
        history_rv_ff.setLayoutManager(new LinearLayoutManager(getActivity()));
        history_rv_ff.setAdapter(pirHistoryAdapter);


        pirHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(getActivity(), PreviewActivity.class);
                startActivity(intent);
            }
        });

        snapshot_smartrefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        snapshot_smartrefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }


    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
//            {
//                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.selector_green)
//                        .setImage(R.mipmap.ic_action_add)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.selector_red)
//                        .setImage(R.mipmap.ic_action_close)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
//            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText(getResources().getString(R.string.delete))
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

//                SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                        .setBackground(R.drawable.selector_green)
//                        .setText("添加")
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };


    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            // int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            Toast.makeText(getActivity(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();



            int position = adapterPosition - history_rv_ff.getHeaderItemCount();
            imageList.remove(position);
            pirHistoryAdapter.notifyItemRemoved(position);
            Toast.makeText(getActivity(), "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();

//            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
//            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
//                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
//            }
        }
    };


    /**
     * Item点击监听。
     */
    private SwipeItemClickListener mItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Toast.makeText(getActivity(), "第SwipeItemClickListener：" + position + "个", Toast.LENGTH_SHORT).show();
        }
    };






}
