package com.yun.software.kaadas.UI.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yun.software.kaadas.Comment.Constans;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.User;
import com.yun.software.kaadas.UI.view.dialog.GenderDialog;
import com.yun.software.kaadas.UI.view.dialog.MeichooseImage;
import com.yun.software.kaadas.Utils.PhotoTool;
import com.yun.software.kaadas.Utils.TimeUtil;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;
import com.zhouyou.http.subsciber.IProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.Glid.GlideCircleTransform;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ToastUtil;

public class UserInfoActivity extends BaseActivity {


    @BindView(R2.id.iv_img)
    ImageView imageView;

    @BindView(R2.id.user_name)
    TextView userNameView;

    @BindView(R2.id.tv_sex)
    TextView tvSexView;

    @BindView(R2.id.tv_birthday)
    TextView tvBirthdayView;

    @BindView(R2.id.tv_tel)
    TextView tvTel;

    private int grender = GenderDialog.GENDER_MALE;

    private GenderDialog genderDialog;

    private User user;

    private String userName;
    private String userLogo;
    private String userGender;
    private String userBirthday;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_user_info;
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }



    @Override
    protected void initViewsAndEvents() {
        tvTitle.setText("个人信息");
        getData();
    }
    private void getData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.CUSTOMERAPP_MYINFO, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                Gson gson = new Gson();
                user = gson.fromJson(result,User.class);
                UserUtils.setUserTel(user.getTel());
                UserUtils.setYanbaoka(user.isGetTimeDelay());
                setView();

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

    private void setView() {
        if(TextUtils.isEmpty(user.getLogo())){
//            GlidUtils.loadCircleImageView(mContext,R.drawable.pic_head_mine,imageView,R.drawable.pic_head_mine);
            imageView.setImageResource(R.drawable.pic_head_mine);
        }else{
            GlidUtils.loadCircleImageView(mContext,user.getLogo(),imageView,R.drawable.pic_head_mine);
        }
        tvTel.setText(user.getTel());

        userNameView.setText(user.getUserName());
        tvSexView.setText(user.getGenderCN());
        tvBirthdayView.setText(user.getBirthday());
        grender = user.getGender();
    }


    @OnClick({R2.id.ll_head_img,R2.id.ll_name,R2.id.ll_tel
            ,R2.id.ll_sex,R2.id.ll_birthday,R2.id.ll_address
        })
    public void onClick(View view){
        int i = view.getId();//头像
//用户名
//绑定手机
//修改性别
        if (i == R.id.ll_head_img) {
            initDialogChooseImage();

        } else if (i == R.id.ll_name) {
            Bundle bundle = new Bundle();
            bundle.putString("name", user.getUserName());
            readyGoForResult(EditNameActivity.class, Constans.REQUEST_CODE_USER_NAME, bundle);

        } else if (i == R.id.ll_tel) {
        } else if (i == R.id.ll_sex) {
            initDialogSelectGender();


        } else if (i == R.id.ll_birthday) {
            showBirthdayDialog();

        } else if (i == R.id.ll_address) {
            readyGo(ManageAddress.class);

        }

    }

    private void showBirthdayDialog() {

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        //startDate.set(2013,1,1);
        Calendar endDate = Calendar.getInstance();
        //endDate.set(2020,1,1);

        //正确设置方式 原因：注意事项有说明
        startDate.set(1900,0,1);
//        endDate.set(2020,11,31);

        TimePickerView.Builder builder = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = TimeUtil.formatData(TimeUtil.dateFormatYMDofChinese,date.getTime()/1000);
                tvBirthdayView.setText(time);
                userBirthday = time;
                user.setBirthday(userBirthday);
                updateData();
            }
        }).setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("生日")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setTextColorOut(Color.GRAY)
                .setTextColorCenter(Color.BLACK)
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false);//是否显示为对话框样式
        TimePickerView timePickerView = builder.build();
        timePickerView.show();


    }

    private void updateData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("logo",userLogo);
        params.put("gender",userGender);
        params.put("birthday",userBirthday);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.CUSTOMERAPP_UPDATE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {



            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }


    private void initDialogSelectGender() {
        if (genderDialog == null){
            genderDialog = new GenderDialog(this);
        }
        genderDialog.setSelect(grender);
        genderDialog.show();
        genderDialog.setSelectGenderListener(sex -> {
            if (sex == GenderDialog.GENDER_FEMALE){
                tvSexView.setText("女");
                grender = GenderDialog.GENDER_FEMALE;
                user.setGenderCN("女");
            }else if (sex == GenderDialog.GENDER_MALE){
                tvSexView.setText("男");
                grender = GenderDialog.GENDER_MALE;
                user.setGenderCN("男");
            }
            userGender = String.valueOf(grender);
            user.setGender(grender);
            updateData();
        });

    }

    private void initDialogChooseImage() {
        MeichooseImage dialogChooseImage = new MeichooseImage(this, MeichooseImage.LayoutType.TITLE);
        dialogChooseImage.show();
    }

    private void uploadFile(File file) {
        IProgressDialog dialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(UserInfoActivity.this);
                dialog.setMessage("请稍候...");
                return dialog;
            }
        };
        HttpManager.getInstance().uploadFile(file, dialog, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                //fileRewriteFullyQualifiedPath
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    userLogo = jsonObject.getString("fileRewriteFullyQualifiedPath");
                    user.setLogo(userLogo);
                    updateData();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        });
    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("user",user);
        setResult(1,intent);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoTool.GET_IMAGE_FROM_PHONE://选择相册之后的处理
                if (resultCode == RESULT_OK) {
                    //                    RxPhotoTool.cropImage(ActivityUser.this, );// 裁剪图片
                    initUCrop(data.getData());
                }

                break;
            case PhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == RESULT_OK) {
                    /* data.getExtras().get("data");*/
                    //                    RxPhotoTool.cropImage(ActivityUser.this, RxPhotoTool.imageUriFromCamera);// 裁剪图片
                    initUCrop(PhotoTool.imageUriFromCamera);
                }

                break;
            case PhotoTool.CROP_IMAGE://普通裁剪后的处理
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.pic_head_mine)
                        .error(R.drawable.pic_head_mine)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        ;

                Glide.with(mContext).
                        load(PhotoTool.cropImageUri).
                        apply(options).
                        thumbnail(0.5f).
                        into(imageView);

                break;

            case UCrop.REQUEST_CROP://UCrop裁剪之后的处理
                if (resultCode == RESULT_OK) {
                    Uri resultUri = UCrop.getOutput(data);
                    File file = roadImageView(resultUri, imageView);
                    //上传头像
                    uploadFile(file);
                    LogUtils.eTag(TAG,file.getAbsolutePath());
                    //                    RxSPTool.putContent(mContext, "AVATAR", resultUri.toString());
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
                break;
            case UCrop.RESULT_ERROR://UCrop裁剪错误之后的处理
                final Throwable cropError = UCrop.getError(data);
                break;

            case Constans.REQUEST_CODE_USER_NAME:
                if (resultCode == 1){
                    String name = data.getStringExtra("name");
                    userNameView.setText(name);
                    userName = name;
                    user.setUserName(userName);
                    updateData();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //从Uri中加载图片 并将其转化成File文件返回
    private File roadImageView(Uri uri, ImageView imageView) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.pic_head_mine)
                .error(R.drawable.pic_head_mine)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transform(new GlideCircleTransform(mContext))
                ;

        Glide.with(mContext).
                load(uri).
                apply(options).
                thumbnail(0.5f).
                into(imageView);
        return (new File(PhotoTool.getImageAbsolutePath(this, uri)));
    }

    private void initUCrop(Uri uri) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), imageName + ".jpeg"));

        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //设置隐藏底部容器，默认显示
        //options.setHideBottomControls(true);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimaryDark));

        //开始设置
        //设置最大缩放比例
        options.setMaxScaleMultiplier(5);
        //设置图片在切换比例时的动画
        options.setImageToCropBoundsAnimDuration(666);
        //设置裁剪窗口是否为椭圆
        options.setCircleDimmedLayer(true);
        //设置是否展示矩形裁剪框
        // options.setShowCropFrame(false);
        //设置裁剪框横竖线的宽度
        //options.setCropGridStrokeWidth(20);
        //设置裁剪框横竖线的颜色
        //options.setCropGridColor(Color.GREEN);
        //设置竖线的数量
        //options.setCropGridColumnCount(2);
        //设置横线的数量
        //options.setCropGridRowCount(1);

        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(300, 300)
                .withOptions(options)
                .start(this);
    }

}
