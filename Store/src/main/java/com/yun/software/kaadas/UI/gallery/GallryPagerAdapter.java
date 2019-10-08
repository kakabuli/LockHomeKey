package com.yun.software.kaadas.UI.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yanliang
 * on 2019/3/20
 */
public class GallryPagerAdapter extends MyPageradapter {

    private final List<BaseShareView> mList;
    private final Context mContext;

    public GallryPagerAdapter(Context context, List<BaseShareView> list) {
        mList = list;
        mContext = context;
    }

    public void addAll(List<BaseShareView> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        BaseShareView baseShareView=mList.get(position);
        View view=baseShareView.getLayoutView();
        baseShareView.initDate(position);
        return view;
    }
    @Override
    public int getCount() {
        return mList.size();
    }
}
