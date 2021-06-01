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
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StorageUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.mvp.view.personalview.IPersonalVerifyFingerPrintView;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;
import com.kaadas.lock.widget.BottomMenuDialog;
import com.kaadas.lock.widget.CircleImageView;

import androidx.annotation.RequiresApi;
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
    private CancellationSignal mCancellationSignal;//用于取消指纹识别

    private final String mAlias = "touch_id_key";//用于获取加密key
    private TranslateAnimation translateAnimation;
    private AlertDialog alertDialog;
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
        initDialog();
    }

    private void initDialog() {
        LogUtils.e("显示对话框");
        if (alertDialog!=null&&!alertDialog.isShowing()){
            alertDialog.show();
        }else {
            View mView = LayoutInflater.from(this).inflate(R.layout.personal_fingerprint_security, null);
            TextView mFingerCancel = mView.findViewById(R.id.finger_cancel);
            alertDialog = AlertDialogUtil.getInstance().common(this, mView);
            alertDialog.setCancelable(false);
            mFingerCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
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
        String code = CacheFloder.readHandPassword(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "handPassword");
       //手势密码
        if (!TextUtils.isEmpty(code)) {
            dialogBuilder.addMenu(R.string.hand_pwd, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent loginIntent = new Intent(mContext, PersonalVerifyGesturePasswordActivity.class);
                    loginIntent.putExtra(KeyConstants.SOURCE,"WelcomeActivity");
                    startActivity(loginIntent);
                    if (bottomMenuDialog != null) {
                        bottomMenuDialog.dismiss();
                        if (mCancellationSignal!=null){
                            mCancellationSignal.cancel();
                        }
                        finish();
                    }
                }
            });
        }
        //密码登录
        dialogBuilder.addMenu(R.string.pwd_select, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                startActivity(loginIntent);
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                    if (mCancellationSignal!=null){
                        mCancellationSignal.cancel();
                    }
                    finish();
                }
            }
        });
        //切换注册页面
        dialogBuilder.addMenu(R.string.select_register, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                startActivity(registerIntent);
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                    if (mCancellationSignal!=null){
                        mCancellationSignal.cancel();
                    }
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
            if (changeBitmap!=null){
                Bitmap newbitmap = BitmapUtil.rotaingImageView(degree, changeBitmap);
                fingerImage.setImageBitmap(newbitmap);
            }
        }
    }

    /**
     * 指纹识别回调监听
     */
    @TargetApi(23)
    private FingerprintManager.AuthenticationCallback callback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            if (alertDialog!=null){
                alertDialog.dismiss();
            }
            Intent successIntent = new Intent(mContext, MainActivity.class);
            startActivity(successIntent);
            ToastUtils.showShort(R.string.fingerprint_success);
            finish();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            LogUtils.e("指纹识别码错误"+errorCode);
            if (alertDialog!=null){
                alertDialog.dismiss();
            }
            if (errorCode==7){
                AlertDialogUtil.getInstance().noEditSingleButtonDialog(mContext, getString(R.string.app_name), getString(R.string.touch_id_call_limited), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
            }

        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
            if (alertDialog!=null){
                alertDialog.dismiss();
            }
            if (translateAnimation != null && fingeprintImg != null) {
                fingeprintImg.startAnimation(translateAnimation);
            }
            ToastUtils.showShort(R.string.fingerprint_fail_check);
        }

        @Override
        public void onAuthenticationFailed() {
            //指纹验证失败，指纹识别失败，可再验，该指纹不是系统录入的指纹。
            if (alertDialog!=null){
                alertDialog.dismiss();
            }

            if (translateAnimation != null && fingeprintImg != null) {
                fingeprintImg.startAnimation(translateAnimation);
            }

            ToastUtils.showShort(R.string.fingerprint_unidentifiable);
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

    private long lastClickBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis() - lastClickBackTime > 2000) {
                lastClickBackTime = System.currentTimeMillis();
                ToastUtils.showLong(R.string.exit);
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
