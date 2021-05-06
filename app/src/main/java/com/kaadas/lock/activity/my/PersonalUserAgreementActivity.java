package com.kaadas.lock.activity.my;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.PersonalUserAgreementAdapter;
import com.kaadas.lock.bean.PersonalUserAgreementBean;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalUserAgreementActivity extends BaseAddToApplicationActivity implements View.OnClickListener {


    @BindView(R.id.personal_user_agreement_recycler)
    RecyclerView personalUserAgreementRecycler;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;


    //总数据
    private List<PersonalUserAgreementBean> personalUserAgreementBeansList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_user_agreement);
        ButterKnife.bind(this);
        initView();
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.user_agreement);
    }


    private void initView() {
        getData();
        if (personalUserAgreementBeansList != null) {
            PersonalUserAgreementAdapter mAgreement = new PersonalUserAgreementAdapter(R.layout.personal_user_agrement_item, personalUserAgreementBeansList);
            personalUserAgreementRecycler.setLayoutManager(new LinearLayoutManager(this));
            personalUserAgreementRecycler.setAdapter(mAgreement);
        }
    }


    private void getData() {
        personalUserAgreementBeansList = new ArrayList<>();
        PersonalUserAgreementBean p0 = new PersonalUserAgreementBean();
        p0.setContent(getResources().getString(R.string.user_agreement_title));
        p0.setFlag(true);
        personalUserAgreementBeansList.add(p0);

        PersonalUserAgreementBean p1 = new PersonalUserAgreementBean();
        p1.setTitle(getResources().getString(R.string.user_agreement_one_title));
        p1.setContent(getResources().getString(R.string.user_agreement_one_content));
        p1.setFlag(false);
        personalUserAgreementBeansList.add(p1);
        PersonalUserAgreementBean p2 = new PersonalUserAgreementBean();
        p2.setTitle(getResources().getString(R.string.user_agreement_two_title));
        p2.setContent(getResources().getString(R.string.user_agreement_two_content));
        p2.setFlag(false);
        personalUserAgreementBeansList.add(p2);
        PersonalUserAgreementBean p3 = new PersonalUserAgreementBean();
        p3.setTitle(getResources().getString(R.string.user_agreement_three_title));
        p3.setContent(getResources().getString(R.string.user_agreement_three_content));
        p3.setFlag(false);
        personalUserAgreementBeansList.add(p3);
        PersonalUserAgreementBean p4 = new PersonalUserAgreementBean();
        p4.setTitle(getResources().getString(R.string.user_agreement_four_title));
        p4.setContent(getResources().getString(R.string.user_agreement_four_content));
        p4.setFlag(false);
        personalUserAgreementBeansList.add(p4);
        PersonalUserAgreementBean p5 = new PersonalUserAgreementBean();
        p5.setTitle(getResources().getString(R.string.user_agreement_five_title));
        p5.setContent(getResources().getString(R.string.user_agreement_five_content));
        p5.setFlag(false);
        personalUserAgreementBeansList.add(p5);
        PersonalUserAgreementBean p6 = new PersonalUserAgreementBean();
        p6.setTitle(getResources().getString(R.string.user_agreement_six_title));
        p6.setContent(getResources().getString(R.string.user_agreement_six_content));
        p6.setFlag(false);
        personalUserAgreementBeansList.add(p6);
        PersonalUserAgreementBean p7 = new PersonalUserAgreementBean();
        p7.setTitle(getResources().getString(R.string.user_agreement_seven_title));
        p7.setContent(getResources().getString(R.string.user_agreement_seven_content));
        p7.setFlag(false);
        personalUserAgreementBeansList.add(p7);

        PersonalUserAgreementBean p8 = new PersonalUserAgreementBean();
        p8.setTitle(getResources().getString(R.string.user_agreement_eight_title));
        p8.setContent(getResources().getString(R.string.user_agreement_eight_content));
        p8.setFlag(false);
        personalUserAgreementBeansList.add(p8);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
