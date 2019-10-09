package com.yun.software.kaadas.UI.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;

import java.net.PortUnreachableException;

public class GenderDialog extends MeiDialog {
    public GenderDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public GenderDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public GenderDialog(Context context) {
        super(context);
        initView();
    }

    public GenderDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }


    private void initView(){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_gender, null);

        ImageView ivFemale = dialogView.findViewById(R.id.iv_female);
        ImageView ivMale = dialogView.findViewById(R.id.iv_male);

        if (gender == GENDER_MALE){
            ivMale.setImageResource(R.drawable.btn_sel);
            ivFemale.setImageResource(R.drawable.btn_nor);
        }else {
            ivMale.setImageResource(R.drawable.btn_nor);
            ivFemale.setImageResource(R.drawable.btn_sel);
        }

        ivFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFemale.setImageResource(R.drawable.btn_sel);
                ivMale.setImageResource(R.drawable.btn_nor);

                if(selectGenderListener != null){
                    selectGenderListener.gender(GENDER_FEMALE);
                }
                dismiss();
            }
        });
        ivMale.setOnClickListener((v) -> {
            ivFemale.setImageResource(R.drawable.btn_nor);
            ivMale.setImageResource(R.drawable.btn_sel);
            if(selectGenderListener != null){
                selectGenderListener.gender(GENDER_MALE);

            }
            dismiss();
        });



        setContentView(dialogView);
        mLayoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.gravity = Gravity.CENTER;
    }


    public void setSelect(int gender){
        this.gender = gender;
        initView();


    }

    private int gender = GENDER_MALE;

    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;


    public interface SelectGenderListener{
        void gender(int gender);
    }

    private SelectGenderListener selectGenderListener;

    public void setSelectGenderListener(SelectGenderListener selectGenderListener){
        this.selectGenderListener = selectGenderListener;
    }

}
