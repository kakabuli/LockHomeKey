package com.yun.software.kaadas.UI.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.Tools.GlideImageLoader;
import com.yun.software.kaadas.UI.activitys.ShopDetailsNewActivity;
import com.yun.software.kaadas.UI.activitys.WebViewActivity;
import com.yun.software.kaadas.UI.adapter.InfoListAdapter;
import com.yun.software.kaadas.UI.adapter.LabelsAdapter;
import com.yun.software.kaadas.UI.bean.BaseBody;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.bean.InfoDetails;
import com.yun.software.kaadas.UI.bean.SocialBanner;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.web.EcWebActivity;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;

public class InfoFragment extends BaseFragment {


    @BindView(R2.id.banner)
    Banner mBanner;

    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R2.id.labels)
    RecyclerView labels;

    InfoListAdapter listAdapter;

    private int startIndex = 1;
    private int pageSize = 10;
    private int total ;

    private List<InfoDetails> listBeans = new ArrayList<>();
    public static List<String> imageLinks = new ArrayList<>();
    public static List<String> bannerId = new ArrayList<>();

    private List<HotkeyBean> labelList = new ArrayList<>();
    private LabelsAdapter labelsAdapter;

    private String searcherKey;

    @Override
    protected int getContentViewLayoutID() {

        return R.layout.fragment_info;
    }

    @Override
    protected View getLoadingTargetView() {
        return mRefreshLayout;
    }

    @Override
    protected void initViewsAndEvents() {
        getLableData();
        labelsAdapter = new LabelsAdapter(labelList);
        labels.setAdapter(labelsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        labels.setLayoutManager(layoutManager);
        labelsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                for (HotkeyBean bean:labelList){
                    bean.setCheck(false);
                }
                labelList.get(position).setCheck(true);
                labelsAdapter.notifyDataSetChanged();
                searcherKey= labelList.get(position).getKey();
                startIndex = 1;
                getData();

            }
        });

        listAdapter=new InfoListAdapter(listBeans);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration = new GridItemDecoration.Builder(mContext);
        decoration.setColor(getResources().getColor(R.color.default_bg_color));
        decoration.setHorizontalSpan(getResources().getDimension(R.dimen.dk_dp_15));
        recyclerView.addItemDecoration(decoration.build());
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setAdapter(listAdapter);
        //此处必须开启 防止用户刷星时候进行下拉操作 会引起IndexOutOfBoundsException
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                startIndex = 1;
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
                //http://192.168.0.142:8089/shop/news/index.html?token=wv8K6gNo&id=155&type=banner_list_invitation
                String id = listBeans.get(position).getId();
                String url = ApiConstants.webUrl + "?token="+UserUtils.getToken()+"&id=" + id +"&type=banner_list_information";
                String shareUrl  = ApiConstants.webUrl + "?customerId="+UserUtils.getuserID()+"&id=" + id +"&type=banner_list_information";
                Bundle bundle = new Bundle();
                bundle.putString("type", Constans.SHARE_TYPE_ARTICLE);
                bundle.putString("id",id);
                bundle.putString("url",url);
                bundle.putString("title","动态");
                bundle.putBoolean("isShow", !TextUtils.isEmpty(UserUtils.getToken()));
                bundle.putString("shareUrl",shareUrl);
                readyGo(EcWebActivity.class,bundle);
            }
        });
        getData();
        getBanner();
    }

    private void getData() {

        toggleShowLoading(true,"正在加载");
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> page = new HashMap<>();
        page.put("pageNum",String.valueOf(startIndex));
        page.put("pageSize",String.valueOf(pageSize));
        map.put("page",page);
        Map<String,String> params = new HashMap<>();
        params.put("tags",searcherKey);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.ARTICLEAPP_INFORMATIONPAGE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                toggleRestore();
                if(startIndex == 1){
                    listBeans.clear();
                }
                Gson gson = new Gson();
                BaseBody<InfoDetails> baseBody = gson.fromJson(result,new TypeToken<BaseBody<InfoDetails>>(){}.getType());
                listBeans.addAll(baseBody.getRows());
                total = baseBody.total;
                listAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore(true);
                if(total==0){
                    toggleShowEmptyImage(true, R.drawable.information_missing_pages, new View.OnClickListener() {
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

            }
        },false);
    }

    private void getBanner() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("bannerName","banner_list_information");
        map.put("params",params);

        HttpManager.getInstance().post(mContext, ApiConstants.COMMONAPP_GETBANNERLIST, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {

                Gson gson = new Gson();
                List<String> bs = new ArrayList<>();
                if(null != bs){
                    bs.clear();
                }
                if(null != imageLinks){
                    imageLinks.clear();
                }
                if(null != bannerId){
                    bannerId.clear();
                }
                BaseBody<SocialBanner> baseBody = gson.fromJson(result,new TypeToken<BaseBody<SocialBanner>>(){}.getType());
                for (SocialBanner banner : baseBody.getRows()){
                    bs.add(banner.getImgUrl());
                    imageLinks.add(banner.getBannerType());
                    bannerId.add(banner.getBannerUrl());
                }

                mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR)
                        .setDelayTime(5000)
                        .setImages(bs)
                        .setImageLoader(new GlideImageLoader())
                        .setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                if("banner_type_h5".equals(imageLinks.get(position))){
                                    Intent intent_h5 = new Intent(getActivity(), WebViewActivity.class);
                                    intent_h5.putExtra("web_url", bannerId.get(position));
                                    startActivity(intent_h5);
                                }else if("banner_type_information".equals(imageLinks.get(position))){
                                    Intent intent_information = new Intent(getActivity(), WebViewActivity.class);
                                    intent_information.putExtra("web_url", ApiConstants.webUrl+"?id="+bannerId.get(position)+"&bannerType="+"banner_type_information");
                                    startActivity(intent_information);
                                }else if("banner_type_activity".equals(imageLinks.get(position))){
                                    Intent intent_activity = new Intent(getActivity(), WebViewActivity.class);
                                    intent_activity.putExtra("web_url", "https://www.kaishuzhijia.com/h5/index.html#/activity"+"?id="+bannerId.get(position)+"&bannerType="+"banner_type_activity");
                                    startActivity(intent_activity);
                                }else if ("banner_type_product".equals(imageLinks.get(position))){
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("type", ShopDetailsNewActivity.TYPE_NORMAL);
                                    bundle.putString("statue", "product_type_s");
                                    bundle.putString("id", bannerId.get(position));
                                    bundle.putString("actid", "");
                                    readyGo(ShopDetailsNewActivity.class, bundle);
                                }
                            }
                        })
                        .start();


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);


            }
        },false);
    }


    private void getLableData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.COMMONAPP_GETALLKEYVALUE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String keyjson = StringUtil.getJsonKeyStr(result,"new_type");
                Gson gson = new Gson();
                List<HotkeyBean> list = gson.fromJson(keyjson,new TypeToken <List<HotkeyBean>>(){}.getType());
                labelList.clear();
                labelList.add(new HotkeyBean("全部","",true));
                labelList.addAll(list);
                labelsAdapter.notifyDataSetChanged();
                searcherKey = labelList.get(0).getKey();
                getData();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

}
