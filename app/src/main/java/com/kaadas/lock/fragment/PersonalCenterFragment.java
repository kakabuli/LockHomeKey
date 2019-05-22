package com.kaadas.lock.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeelockNewScanActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.ProductActivationScanActivity;
import com.kaadas.lock.activity.my.AboutUsActivity;
import com.kaadas.lock.activity.my.PersonalFAQActivity;
import com.kaadas.lock.activity.my.PersonalMessageActivity;
import com.kaadas.lock.activity.my.PersonalSecuritySettingActivity;
import com.kaadas.lock.activity.my.PersonalSystemSettingActivity;
import com.kaadas.lock.activity.my.PersonalUpdateHeadDataActivity;
import com.kaadas.lock.activity.my.UserFeedbackActivity;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.MyFragmentPresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.UserNickResult;
import com.kaadas.lock.utils.BitmapUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StorageUtil;
import com.kaadas.lock.mvp.view.IMyFragmentView;
import com.kaadas.lock.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 我的Fragment
 *
 * @company kaadas
 * created at 2019/2/25 14:47
 */
public class PersonalCenterFragment extends BaseFragment<IMyFragmentView, MyFragmentPresenter<IMyFragmentView>> implements IMyFragmentView, View.OnClickListener {
    @BindView(R.id.message_layout)
    RelativeLayout messageLayout;
    @BindView(R.id.security_setting_layout)
    RelativeLayout securitySettingLayout;
    @BindView(R.id.faq_layout)
    RelativeLayout faqLayout;
    @BindView(R.id.system_setting_layout)
    RelativeLayout systemSettingLayout;
    @BindView(R.id.about_xk_layout)
    RelativeLayout aboutXkLayout;
    Unbinder unbinder;
    @BindView(R.id.head_second)
    RelativeLayout headSecond;
    @BindView(R.id.iv_photo)
    CircleImageView ivPhoto;
    //用户名称
    @BindView(R.id.head_portrait_name)
    TextView headPortraitName;
    @BindView(R.id.rl_user_feedback)
    RelativeLayout rlUserFeedback;
    @BindView(R.id.product_activition)
    RelativeLayout product_activition;

    private View mPersonlCenterView;
    private Bitmap changeBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mPersonlCenterView == null) {
            mPersonlCenterView = inflater.inflate(R.layout.fragment_my, container, false);
        }
        unbinder = ButterKnife.bind(this, mPersonlCenterView);
        rlUserFeedback.setOnClickListener(this);
        return mPersonlCenterView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("PersonalCenter---onResume");
        initView();
    }

    private void initView() {

        //用户名
        String userName = (String) SPUtils.get(SPUtils.USERNAME, "");
        if (!TextUtils.isEmpty(userName)) {
            headPortraitName.setText(userName);
        }else {
            //获取昵称
            String uid = (String) SPUtils.get(SPUtils.UID, "");
            mPresenter.getUserName(uid);
        }
        String photoPath = (String) SPUtils.get(KeyConstants.HEAD_PATH, "");
        if ("".equals(photoPath)) {
            mPresenter.downloadPicture(MyApplication.getInstance().getUid());
        } else {
            showImage(photoPath);
        }
    }

    @Override
    protected MyFragmentPresenter<IMyFragmentView> createPresent() {
        return new MyFragmentPresenter<>();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.message_layout, R.id.security_setting_layout, R.id.faq_layout, R.id.system_setting_layout, R.id.about_xk_layout, R.id.head_second,R.id.product_activition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.message_layout:
                Intent mMessageIntent = new Intent(getActivity(), PersonalMessageActivity.class);
                startActivity(mMessageIntent);
                break;
            case R.id.security_setting_layout:
                Intent mSercurityIntent = new Intent(getActivity(), PersonalSecuritySettingActivity.class);
                startActivity(mSercurityIntent);
                break;
            case R.id.faq_layout:
                Intent mFaq = new Intent(getActivity(), PersonalFAQActivity.class);
                startActivity(mFaq);
                break;
            case R.id.system_setting_layout:
                Intent mSystemSetting = new Intent(getActivity(), PersonalSystemSettingActivity.class);
                startActivity(mSystemSetting);
                break;
            case R.id.about_xk_layout:
                Intent aboutIntent=new Intent(getActivity(),AboutUsActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.head_second:
                Intent updateHeadData = new Intent(getActivity(), PersonalUpdateHeadDataActivity.class);
                startActivity(updateHeadData);
                break;
            case R.id.product_activition:
                Intent zigbeeLockIntent=new Intent(getActivity(), ProductActivationScanActivity.class);
                startActivity(zigbeeLockIntent);
                break;
        }
    }

    private void showImage(String photoPath) {
        if ("".equals(photoPath)) {
            ivPhoto.setImageDrawable(getResources().getDrawable(R.mipmap.default_head));
        } else {
            int degree = BitmapUtil.readPictureDegree(photoPath);
            changeBitmap = BitmapUtil.ratio(photoPath, 720, 720);
            /**
             * 把图片旋转为正的方向
             */
            Bitmap newbitmap = BitmapUtil.rotaingImageView(degree, changeBitmap);
            ivPhoto.setImageBitmap(newbitmap);
        }
    }

    @Override
    public void downloadPhoto(Bitmap bitmap) {
        ivPhoto.setImageBitmap(bitmap);
        StorageUtil.getInstance().saveServerPhoto(bitmap);
    }

    @Override
    public void downloadPhotoError(Throwable e) {
//        ToastUtil.getInstance().showShort( HttpUtils.httpProtocolErrorCode(getActivity(),e));

    }

    @Override
    public void getNicknameSuccess(UserNickResult userNickResult) {
        String nickName = userNickResult.getData().getNickName();
        SPUtils.put(SPUtils.USERNAME, nickName);
        headPortraitName.setText(nickName);
    }

    @Override
    public void getNicknameFail(BaseResult baseResult) {
        String account = (String) SPUtils.get(SPUtils.PHONEN, "");
        headPortraitName.setText(account);
    }

    @Override
    public void getNicknameError(Throwable throwable) {
        String account = (String) SPUtils.get(SPUtils.PHONEN, "");
        headPortraitName.setText(account);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_user_feedback:
                intent = new Intent(getActivity(), UserFeedbackActivity.class);
                startActivity(intent);
                break;
        }
    }
}
