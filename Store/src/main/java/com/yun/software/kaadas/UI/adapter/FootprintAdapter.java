package com.yun.software.kaadas.UI.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.EarningDetailsBean;
import com.yun.software.kaadas.UI.bean.MyCollection;
import com.yun.software.kaadas.UI.view.CheckableButton;
import com.yun.software.kaadas.Utils.UserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.ToastUtil;

import static android.view.animation.Animation.ABSOLUTE;

/**
 * 分组列表适配器
 * Create by: chenWei.li
 * Date: 2018/11/3
 * Time: 下午8:19
 * Email: lichenwei.me@foxmail.com
 */
public class FootprintAdapter extends RecyclerView.Adapter<FootprintAdapter.ViewHolder> {

    private Context mContext;
    private List<MyCollection> mList;

    private boolean editStatus = false;

    public FootprintAdapter(Context context, List<MyCollection> list) {
        this.mContext = context;
        this.mList = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_foot_print, parent, false);
            viewHolder = new ViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCollection bean =  mList.get(position);

        holder.name.setText(bean.getName());
        holder.price.setText( bean.getPrice());
        GlidUtils.loadImageNormal(mContext,bean.getLogo(),holder.imageView);

        if (bean.isChecked()){
            holder.ivCheckView.setChecked(true);
            holder.ivCheckView.setBackgroundResource(R.drawable.selectbox_sel);
        }else {
            holder.ivCheckView.setChecked(false);
            holder.ivCheckView.setBackgroundResource(R.drawable.selectbox_nor);
        }


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bean.changeStatue();
                if (bean.isChecked()){
                    holder.ivCheckView.setChecked(true);
                    holder.ivCheckView.setBackgroundResource(R.drawable.selectbox_sel);
                }else {
                    holder.ivCheckView.setChecked(false);
                    holder.ivCheckView.setBackgroundResource(R.drawable.selectbox_nor);
                }
                if (onClickListener != null){
                    onClickListener.onClick(position);
                }
            }
        });

        //改变编辑状态
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.constraintLayout.getLayoutParams();
        if (editStatus){
            holder.ivCheckView.setVisibility(View.VISIBLE);
            holder.shopcart.setVisibility(View.GONE);
            params.setMargins(50,0,0,0);
        }else {
            holder.ivCheckView.setVisibility(View.INVISIBLE);
            params.setMargins(0,0,0,0);
            holder.shopcart.setVisibility(View.VISIBLE);
        }
        holder.constraintLayout.setLayoutParams(params);


        holder.shopcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShopCar(bean);
            }
        });

    }

    private void addShopCar(MyCollection bean){
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("agentProductId",bean.getProductId());
        params.put("qty","1");
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.SHOPCAR_SAVE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("加入购物车成功");


            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },true);
    }


    public interface OnClickListener{
        void onClick(int position);
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    /**
     * 判断position对应的Item是否是组的第一项
     *
     * @param position
     * @return
     */
    public boolean isItemHeader(int position) {
        if (position == 0 || mList.size() == 1) {
            return true;
        } else {
            String lastGroupName = mList.get(position - 1).getGroupName();
            String currentGroupName = mList.get(position).getGroupName();
            //判断上一个数据的组别和下一个数据的组别是否一致，如果不一致则是不同组，也就是为第一项（头部）
            if (lastGroupName.equals(currentGroupName)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获取position对应的Item组名
     *
     * @param position
     * @return
     */
    public String getGroupName(int position) {
        return mList.get(position).getGroupName();
    }



    public void setEditStatus(boolean editStatus) {
        this.editStatus = editStatus;
        notifyDataSetChanged();
    }


    /**
     * 自定义ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        CheckableButton ivCheckView;
        ConstraintLayout constraintLayout;
        ImageView imageView;
        TextView name;
        TextView price;
        ImageView shopcart;
        RelativeLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCheckView = itemView.findViewById(R.id.iv_check);
            imageView = itemView.findViewById(R.id.image);
            constraintLayout = itemView.findViewById(R.id.cl_parent);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            shopcart = itemView.findViewById(R.id.add_shopcart);
            rootView = itemView.findViewById(R.id.root_view);

        }
    }

}
