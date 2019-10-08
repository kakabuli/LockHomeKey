package com.yun.software.kaadas.UI.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.CarItem;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.view.CommiteItemListView;

import java.util.ArrayList;
import java.util.List;

import la.xiong.androidquick.tool.Glid.GlidUtils;


/**
 * des:评论适配器
 * Created by xsf
 * on 2016.07.11:11
 */
public class OderListItemAdapter {

    private Context mContext;
    private CommiteItemListView mListview;
    private List<CarItem> mDatas;

    public OderListItemAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<CarItem>();
    }

    public OderListItemAdapter(Context context, List<CarItem> datas) {
        mContext = context;
        setDatas(datas);
    }

    //
    public void bindListView(CommiteItemListView listView) {
        if (listView == null) {
            throw new IllegalArgumentException("CommentListView is null....");
        }
        mListview = listView;
    }

    //
    public void setDatas(List<CarItem> datas) {
        if (datas == null) {
            datas = new ArrayList<CarItem>();
        }

        mDatas = datas;
    }

    public List<CarItem> getDatas() {
        return mDatas;
    }

    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    //
    public CarItem getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        if (position < mDatas.size()) {
            return mDatas.get(position);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private View getView(final int position) {
        CarItem carItem =mDatas.get(position);
        View convertView = View.inflate(mContext, R.layout.item_order_list_item, null);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        ImageView imageView = convertView.findViewById(R.id.image);
        GlidUtils.loadImageNormal(mContext,carItem.getLogo(),imageView);
        TextView tvAttr = convertView.findViewById(R.id.tv_attr);
        TextView tvMoney = convertView.findViewById(R.id.tv_money);
        TextView tvCount = convertView.findViewById(R.id.tv_count);

        tvName.setText(carItem.getSkuProductName());
        tvAttr.setText(carItem.getAttributeComboName());
        tvMoney.setText("¥"+carItem.getPrice());
        tvCount.setText("x" + carItem.getQty());

        return convertView;
    }


    public interface OderListItemClickLisener{
        void onclickshowOder(IndentInfo listBean);
        void onclickDownFile(IndentInfo listBean);
    }
    private OderListItemClickLisener oderListItemClickLisener;
    public void setOderListItemClickLisener(OderListItemClickLisener oderListItemClickLisener){
        this.oderListItemClickLisener=oderListItemClickLisener;

    }


    public void notifyDataSetChanged() {
        if (mListview == null) {
            throw new NullPointerException("listview is null, please bindListView first...");
        }
        mListview.removeAllViews();
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mDatas.size(); i++) {
            final int index = i;
            View view = getView(index);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            mListview.addView(view, index, layoutParams);
        }

    }
    public void destory() {

    }

}
