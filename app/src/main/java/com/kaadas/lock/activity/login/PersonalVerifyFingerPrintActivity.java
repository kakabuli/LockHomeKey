package com.kaadas.lock.activity.login;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.personalpresenter.PersonalFingerPrintPresenter;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BitmapUtil;
import com.kaadas.lock.utils.EncryUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StorageUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.personalview.IPersonalVerifyFingerPrintView;
import com.kaadas.lock.widget.BottomMenuDialog;
import com.kaadas.lock.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//todo 指纹验证解锁页面 从启动页跳转
public class PersonalVerifyFingerPrintActivity extends BaseActivity<IPersonalVerifyFingerPrintView, PersonalFingerPrintPresenter<IPersonalVerifyFingerPrintView>> implements IPersonalVerifyFingerPrintView {


    @BindView(R.id.finger_click)
    LinearLayout fingerClick;

    @BindView(R.id.finger_more)
    TextView fingerMore;

    @BindView(R.id.finger_image)
    CircleImageView fingerImage;
    @BindView(R.id.fingeprint_img)
    ImageView fingeprintImg;

    private BottomMenuDialog.Builder dialogBuilder;
    private BottomMenuDialog bottomMenuDialog;
    private Context mContext;
    private Bitmap changeBitmap;
    private FingerprintManager mFingerprintManager;
    ;
    private CancellationSignal mCancellationSignal;//用于取消指纹识别

    private final String mAlias = "touch_id_key";//用于获取加密key
    private TranslateAnimation translateAnimation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_fingerprint_verify);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        initTouchId();

    }

    private void initTouchId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = getFingerprintManagerOrNull();
            if (mFingerprintManager != null) {
                mCancellationSignal = new CancellationSignal();
                //开始验证指纹
                mFingerprintManager.authenticate(new
                                FingerprintManager.CryptoObject(EncryUtils.getInstance().getCipher(mAlias))
                        , mCancellationSignal, 0, callback, null);
            }
        }
    }

    /**
     * 获取FingerprintManager
     *
     * @return FingerprintManager
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintManager getFingerprintManagerOrNull() {
        if (getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return getApplication().getSystemService(FingerprintManager.class);
        } else {
            return null;
        }
    }


    @Override
    protected PersonalFingerPrintPresenter<IPersonalVerifyFingerPrintView> createPresent() {
        return new PersonalFingerPrintPresenter<>();
    }


    private void initView() {
        String photoPath = (String) SPUtils.get(KeyConstants.HEAD_PATH, "");
        if ("".equals(photoPath)) {
            mPresenter.downloadPicture(MyApplication.getInstance().getUid());
        } else {
            showImage(photoPath);
        }
        setImageAnimation();

    }

    private void setImageAnimation() {
        //摇摆
        translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
        translateAnimation.setDuration(100);
        fingeprintImg.setAnimation(translateAnimation);


    }


    @OnClick({R.id.finger_click, R.id.finger_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.finger_click:
                //已经打开
                View mView = LayoutInflater.from(this).inflate(R.layout.personal_fingerprint_security, null);
                TextView mFingerCancel = mView.findViewById(R.id.finger_cancel);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                mFingerCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                break;
            case R.id.finger_more:
                showMoreDialog();
                break;
        }
    }

    //展示头像对话框
    private void showMoreDialog() {
        dialogBuilder = new BottomMenuDialog.Builder(this);
        dialogBuilder.addMenu(R.string.pwd_select, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                startActivity(loginIntent);
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                    finish();
                }
            }
        });
        dialogBuilder.addMenu(R.string.select_register, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                startActivity(registerIntent);
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                    finish();
                }
            }
        });
        bottomMenuDialog = dialogBuilder.create();
        bottomMenuDialog.show();
    }

    private void showImage(String photoPath) {
        if ("".equals(photoPath)) {
            fingerImage.setImageDrawable(getResources().getDrawable(R.mipmap.default_head));
        } else {
            int degree = BitmapUtil.readPictureDegree(photoPath);
            changeBitmap = BitmapUtil.ratio(photoPath, 720, 720);
            /**
             * 把图片旋转为正的方向
             */
            Bitmap newbitmap = BitmapUtil.rotaingImageView(degree, changeBitmap);
            fingerImage.setImageBitmap(newbitmap);
        }
    }

    /**
     * 指纹识别回调监听
     */
    @TargetApi(23)
    private FingerprintManager.AuthenticationCallback callback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Intent successIntent = new Intent(mContext, MainActivity.class);
            startActivity(successIntent);
            ToastUtil.getInstance().showShort(R.string.fingerprint_success);
            finish();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            ToastUtil.getInstance().showShort(R.string.fingerprint_fail);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
            if (translateAnimation != null && fingeprintImg != null) {
                fingeprintImg.startAnimation(translateAnimation);
            }
            ToastUtil.getInstance().showShort(R.string.fingerprint_fail_check);
        }

        @Override
        public void onAuthenticationFailed() {
            //指纹验证失败，指纹识别失败，可再验，该指纹不是系统录入的指纹。
            if (translateAnimation != null && fingeprintImg != null) {
                fingeprintImg.startAnimation(translateAnimation);
            }

            ToastUtil.getInstance().showShort(R.string.fingerprint_unidentifiable);
        }
    };


    @Override
    public void downloadPhoto(Bitmap bitmap) {
        fingerImage.setImageBitmap(bitmap);
        StorageUtil.getInstance().saveServerPhoto(bitmap);
    }

    @Override
    public void downloadPhotoError(Throwable e) {
//        ToastUtil.getInstance().showShort( HttpUtils.httpProtocolErrorCode(this,e));
    }


}
