package com.yun.software.kaadas.UI.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.adapter.SaleAdaterTypeAdapter;
import com.yun.software.kaadas.UI.bean.HotkeyBean;

import java.util.List;

import la.xiong.androidquick.tool.StringUtil;

/**
 * Created by yanliang
 * on 2019/5/23
 */
public class ModleListSelectDialog  extends MeiDialog{
    RecyclerView mReItemStyle;
    private TextView tvSure;
    private List<HotkeyBean> mReasons;
    private SaleAdaterTypeAdapter mSaleAdapterTyppe;
    private TextView tvChoice;
    private String choiceStr;
    public ModleListSelectDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    public ModleListSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    public ModleListSelectDialog(Context context ,List<HotkeyBean> reasons,TextView tvCHoice) {
        super(context);
        this.mReasons=reasons;
        this.tvChoice=tvCHoice;
        initView(context);
    }

    public ModleListSelectDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView(context);
    }


    private void initView(Context context){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_list_type, null);
        setContentView(v);
        BindView(v);
        mLayoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.gravity = Gravity.CENTER;
    }
    private void BindView(View v){
        mReItemStyle=v.findViewById(R.id.item_style);
        tvSure=v.findViewById(R.id.tv_sure);
        mSaleAdapterTyppe=new SaleAdaterTypeAdapter(mReasons);
        mReItemStyle.setHasFixedSize(true);
        mReItemStyle.setLayoutManager(new LinearLayoutManager(mContext){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!StringUtil.isEmpty(choiceStr)){
                    tvChoice.setText(choiceStr);
                }
                dismiss();
            }
        });
        mReItemStyle.setNestedScrollingEnabled(false);
        mReItemStyle.setFocusable(false);
        mReItemStyle.setAdapter(mSaleAdapterTyppe);
        mSaleAdapterTyppe.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mSaleAdapterTyppe.setSelectPostion(position);
                choiceStr=mReasons.get(position).getValue();
            }
        });

    }


    public void setSelect(int gender){
        this.gender = gender;
    }

    private int gender = GENDER_MALE;

    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;


    public interface SelectGenderListener{
        void gender(int gender);
    }

    private GenderDialog.SelectGenderListener selectGenderListener;

    public void setSelectGenderListener(GenderDialog.SelectGenderListener selectGenderListener){
        this.selectGenderListener = selectGenderListener;
    }
}
