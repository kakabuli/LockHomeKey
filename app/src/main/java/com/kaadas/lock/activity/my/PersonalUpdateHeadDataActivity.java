package com.kaadas.lock.activity.my;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.PersonalDataPresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.BitmapUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StorageUtil;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.mvp.view.IPersonalDataView;
import com.kaadas.lock.widget.BottomMenuDialog;
import com.kaadas.lock.widget.CircleImageView;
import com.kaadas.lock.widget.image.GlideImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PersonalUpdateHeadDataActivity extends BaseActivity<IPersonalDataView, PersonalDataPresenter<IPersonalDataView>> implements IPersonalDataView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.head_portrait_right)
    ImageView headPortraitRight;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.head_name_right)
    ImageView headNameRight;
    @BindView(R.id.head_portrait_name)
    TextView headPortraitName;
    @BindView(R.id.head_nickname_layout)
    RelativeLayout headNicknameLayout;
    @BindView(R.id.head_telNum)
    TextView headTelNum;
    @BindView(R.id.head_telNum_layout)
    RelativeLayout headTelNumLayout;
    private BottomMenuDialog.Builder dialogBuilder;
    private BottomMenuDialog bottomMenuDialog;
    public static final int PHOTO_REQUEST_CODE = 100;
    private ArrayList<ImageItem> images;
    private String photoPath;
    private Bitmap changeBitmap;
    public static final int REQUEST_PERMISSION_REQUEST_CODE = 1;
    private ImagePicker imagePicker;
//    boolean permission = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);
        initDefault();
        initView();
        getMessage();
        tvContent.setText(getString(R.string.personal_center));
        ivBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessage();
    }

    private void initView() {
        //获取头像
        String strPhotoPath = (String) SPUtils.get(KeyConstants.HEAD_PATH, "");
        if ("".equals(strPhotoPath)) {
            mPresenter.downloadPicture(MyApplication.getInstance().getUid());
        } else {
            showImage(strPhotoPath);
        }

    }

    private void getMessage() {
        //获取昵称
        String userName = (String) SPUtils.get(SPUtils.USERNAME, "");
        if (!TextUtils.isEmpty(userName)) {
            headPortraitName.setText(userName);
        }
        //获取手机号码
        String phone = (String) SPUtils.get(SPUtils.PHONEN, "");
        if (!TextUtils.isEmpty(phone)) {
            headTelNum.setText(StringUtil.phoneToHide(phone));
        }
    }

    @Override
    protected PersonalDataPresenter<IPersonalDataView> createPresent() {
        return new PersonalDataPresenter<>();
    }


    @OnClick({R.id.head_nickname_layout, R.id.rl_head})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_nickname_layout:
                Intent mHeadNickName = new Intent(this, PersonalUpdateNickNameActivity.class);
                startActivity(mHeadNickName);
                break;

            case R.id.rl_head:
//                showHeadDialog();
                checkPermissions();

                break;
        }
    }

    //展示头像对话框
    private void showHeadDialog() {
        dialogBuilder = new BottomMenuDialog.Builder(this);

        dialogBuilder.addMenu(R.string.zi_pai, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalUpdateHeadDataActivity.this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                startActivityForResult(intent, PHOTO_REQUEST_CODE);

                //  自拍
                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                }

            }
        });
        dialogBuilder.addMenu(R.string.select_photo_album, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //摄像头
                Intent intent = new Intent(PersonalUpdateHeadDataActivity.this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                //ImagePicker.getInstance().setSelectedImages(images);
                startActivityForResult(intent, PHOTO_REQUEST_CODE);


                if (bottomMenuDialog != null) {
                    bottomMenuDialog.dismiss();
                }


            }
        });
        bottomMenuDialog = dialogBuilder.create();
        bottomMenuDialog.show();
    }

    //展示图片
    private void showImage(String photoPath) {
        if ("".equals(photoPath)) {
            ivHead.setImageDrawable(getResources().getDrawable(R.mipmap.default_head));
        } else {
            int degree = BitmapUtil.readPictureDegree(photoPath);
            changeBitmap = BitmapUtil.ratio(photoPath, 720, 720);
            /**
             * 把图片旋转为正的方向
             */
            if (changeBitmap!=null){
                Bitmap newbitmap = BitmapUtil.rotaingImageView(degree, changeBitmap);
                ivHead.setImageBitmap(newbitmap);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == PHOTO_REQUEST_CODE) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                photoPath = images.get(0).path;
                if (null == photoPath || "".equals(photoPath)) {
                    photoPath = "";
                }
                uploadPhoto();

            } else {
                ToastUtils.showShort(R.string.no_data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGranted(this, Permission.RECORD_AUDIO) &&
                    XXPermissions.isGranted(this, Permission.Group.CALENDAR)) {
                com.blankj.utilcode.util.LogUtils.d("用户已经在权限设置页授予了录音和日历权限");
            } else {
                com.blankj.utilcode.util.LogUtils.d("用户没有在权限设置页授予权限");

            }
        }
    }

    //上传到服务器
    private void uploadPhoto() {
        LogUtils.d("davi photoPath " + photoPath);
        String uid = MyApplication.getInstance().getUid();
        mPresenter.uploadPicture(uid, photoPath);
    }


    //图片初始化默认值
    public void initDefault() {
        imagePicker = ImagePicker.getInstance();
        //
        imagePicker.setImageLoader(new GlideImageLoader());
        //设置只选一张图片
        imagePicker.setMultiMode(false);
        //矩形  长宽固定200
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        imagePicker.setFocusWidth(width);
        imagePicker.setFocusHeight(height);

        //是否显示摄像机
        imagePicker.setShowCamera(false);
        //矩形区域保存
        imagePicker.setSaveRectangle(true);
        //设置保存的图片大小
        imagePicker.setOutPutX(200);
        imagePicker.setOutPutY(200);
        //设置裁剪
        imagePicker.setCrop(true);
    }

    //权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
//                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getActivity(), getString(R.string.not_allow_permission_warring), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Intent intent = new Intent(this, ImageGridActivity.class);
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        }
    }

    @Override
    public void photoUploadSuccess() {
        //        mPresenter.downloadPicture(MyApplication.getInstance().getUid());
        Bitmap temp;
        int degree = BitmapUtil.readPictureDegree(photoPath);
        temp = BitmapUtil.ratio(photoPath, 720, 720);
        /**
         * 把图片旋转为正的方向
         */
        if (temp!=null){
            Bitmap newbitmap = BitmapUtil.rotaingImageView(degree, temp);
            downloadPhoto(newbitmap);
            temp = null;
        }
        SPUtils.put(KeyConstants.HEAD_PATH, photoPath);
    }

    @Override
    public void photoUploadFail(BaseResult baseResult) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void photoUploadError(Throwable throwable) {
        ToastUtils.showShort(getString(R.string.upload_hear) + HttpUtils.httpProtocolErrorCode(this, throwable));
    }


    @Override
    public void downloadPhoto(Bitmap bitmap) {
        StorageUtil.getInstance().saveServerPhoto(bitmap);
        ivHead.setImageBitmap(bitmap);
    }

    @Override
    public void downloadPhotoError(Throwable e) {
//        ToastUtil.getInstance().showShort( HttpUtils.httpProtocolErrorCode(this,e));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void checkPermissions() {
        try {
            XXPermissions.with(this)
                    .permission(Permission.CAMERA)
                    .permission(Permission.Group.STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                showHeadDialog();
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                        }
                    });

        }catch (Exception e){
            LogUtils.e( "checkPermissions: "  + e.getMessage());
        }
    }

}