package com.yun.software.kaadas.UI.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.CommentImgsAdapter;
import com.yun.software.kaadas.UI.bean.FeedPicture;
import com.yun.software.kaadas.UI.bean.HotkeyBean;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.UI.view.dialog.MeichooseImage;
import com.yun.software.kaadas.UI.view.dialog.ModleListSelectDialog;
import com.yun.software.kaadas.Utils.PhotoTool;
import com.yun.software.kaadas.Utils.UserUtils;
import com.yun.software.kaadas.base.BaseActivity;
import com.zhouyou.http.subsciber.IProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import la.xiong.androidquick.tool.FileUtils;
import la.xiong.androidquick.tool.Glid.GlidUtils;
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ScreenUtils;
import la.xiong.androidquick.tool.StringUtil;
import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/5/18
 */
public class ApplySaleAfterActivity extends BaseActivity {
    private IndentInfo orderInfor;
    private List<HotkeyBean> resons;
    List<FeedPicture> pictures;
    @BindView(R2.id.rc_imgs)
    RecyclerView rcImgs;
    CommentImgsAdapter mCommentimgsAdapter;
    @BindView(R2.id.re_choice_sa_reson)
    RelativeLayout reChoiceReason;
    @BindView(R2.id.tv_sale_reson)
    TextView tvSaleReson;
    @BindView(R2.id.et_feed_back)
    EditText etFeedBack;
    @BindView(R2.id.et_sale_phone_people)
    EditText etSalePhonePeople;
    @BindView(R2.id.et_sale_phone)
    EditText etSalePhone;
    @BindView(R2.id.tv_submit)
    TextView tvSubMit;
    @BindView(R2.id.iv_pic)
    ImageView ivPic;
    @BindView(R2.id.tv_goodname)
    TextView tvGoodname;
    @BindView(R2.id.tv_goodprice)
    TextView tvGoodprice;
    @BindView(R2.id.tv_goodnum)
    TextView tvGoodnum;
    private Map<Integer,String> urlMap = new HashMap<>();
    private int position;
    private String id;
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.after_sale;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
    }

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }


    @Override
    protected void initViewsAndEvents() {
        orderInfor = getIntent().getParcelableExtra("bean");
        id = getIntent().getStringExtra("id");
        tvTitle.setText("售后");
        tvRight.setText("提交");
        tvRight.setVisibility(View.VISIBLE);
        tvGoodname.setText(orderInfor.getProductName());
        tvGoodprice.setText("￥"+orderInfor.getPrice());
        tvGoodnum.setText("x"+orderInfor.getQty());
        GlidUtils.loadImageNormal(mContext,orderInfor.getLogo(),ivPic);
        tvRight.setTextColor(getResources().getColor(R.color.light_blue));
        pictures = new ArrayList<>();
        pictures.add(new FeedPicture("temp", false, ""));
        rcImgs.setHasFixedSize(true);
        rcImgs.setLayoutManager(new GridLayoutManager(mContext, 3));
        Drawable decoration = ContextCompat.getDrawable(this, R.drawable.album_decoration_white);
        int itemSize = (ScreenUtils.getScreenWidth() - decoration.getIntrinsicWidth() * (3 + 1)) / 3;
        mCommentimgsAdapter = new CommentImgsAdapter(pictures, itemSize);
        rcImgs.setAdapter(mCommentimgsAdapter);
        mCommentimgsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int i = view.getId();
                if (i == R.id.view_iamge) {
                    if (pictures.size() == 0) {
                        pictures.add(new FeedPicture("temp", false, ""));
                    }

                    if (pictures.get(position).getPath().equals("temp")) {
                        initDialogChooseImage();
                    } else {
                        List<String> strings = new ArrayList<>();
                        strings.add(pictures.get(position).getPath());
                        BigImageActivity.startImagePagerActivity(ApplySaleAfterActivity.this, strings, 0);
                    }

                } else if (i == R.id.view_delete) {
                    pictures.remove(position);
                    if (pictures.size() < 4) {
                        if (pictures.get(pictures.size() - 1).getPath().equals("temp")) {
                        } else {
                            pictures.add(new FeedPicture("temp", false, ""));
                        }
                    }
                    mCommentimgsAdapter.notifyDataSetChanged();

                }
            }
        });
        reChoiceReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resons==null||resons.size()==0){
                    ToastUtil.showShort("未获取到售后原因,请重试...");
                    getHotData();
                    return;
                }
                ModleListSelectDialog modleListSelectDialog = new ModleListSelectDialog(mContext,resons,tvSaleReson);
                modleListSelectDialog.show();
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReson();
            }
        });
        getHotData();
    }


    //
    private void initDialogChooseImage() {
        MeichooseImage dialogChooseImage = new MeichooseImage(this, MeichooseImage.LayoutType.TITLE);
        dialogChooseImage.show();
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
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                break;

            case UCrop.REQUEST_CROP://UCrop裁剪之后的处理
                if (resultCode == RESULT_OK) {
                    Uri resultUri = UCrop.getOutput(data);
                    File file = roadImageView(resultUri);
                    String filePath = file.getAbsolutePath();
                    LogUtils.iTag("文件大小", FileUtils.getFileSizeFromate(filePath));
                    FeedPicture feedPicture=new FeedPicture(filePath, true, "");
                    pictures.add(pictures.size() - 1, feedPicture);
                    if (pictures.size() > 3) {
                        pictures.remove(pictures.size() - 1);
                    }
                    mCommentimgsAdapter.notifyDataSetChanged();
                    uploadFile(feedPicture);



                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
                break;
            case UCrop.RESULT_ERROR://UCrop裁剪错误之后的处理
                final Throwable cropError = UCrop.getError(data);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadFile(FeedPicture feedPicture) {
        IProgressDialog dialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(ApplySaleAfterActivity.this);
                dialog.setMessage("请稍候...");
                return dialog;
            }
        };
        HttpManager.getInstance().uploadFile(new File(feedPicture.getPath()), dialog, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                //fileRewriteFullyQualifiedPath
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String imageUrl = jsonObject.getString("fileRewriteFullyQualifiedPath");
                    LogUtils.iTag("ceshi","图片地址"+imageUrl);
                    feedPicture.setUrl(imageUrl);
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
    //从Uri中加载图片 并将其转化成File文件返回
    private File roadImageView(Uri uri) {
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
        options.setCircleDimmedLayer(false);
        //设置是否展示矩形裁剪框
        options.setShowCropFrame(true);
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
                .withMaxResultSize(600, 600)
                .withOptions(options)
                .start(this);
    }

    private void submitReson(){
        String urls ="";

        
        if(pictures!=null&&pictures.size()>0){

            if (pictures.get(pictures.size() - 1).getPath().equals("temp")) {
                pictures.remove(pictures.size() - 1);
            }

            for (int i = 0; i < pictures.size(); i++) {
                LogUtils.iTag("imgsurl",pictures.get(i).getUrl());
            }

            for (FeedPicture picture :pictures){
                urls += picture.getUrl() + ",";
            }
            if (urls.length() > 1){
                urls = urls.substring(0,urls.length() -1);
            }
        }
        LogUtils.iTag("ceshi","picture"+urls);

        String reasonKey = "";
        String str_reason=tvSaleReson.getText().toString();
        String str_feed=etFeedBack.getText().toString();
        String str_Phone=etSalePhone.getText().toString();
        String str_phone_name=etSalePhonePeople.getText().toString();
        if(TextUtils.isEmpty(str_reason)||str_reason.equals("请选择")){
            ToastUtil.showShort("请选择售后原因");
            pictures.add(new FeedPicture("temp", false, ""));
            return;
        }
        for (HotkeyBean bean :resons){
            if (bean.getValue().equals(str_reason)){
                reasonKey = bean.getKey();
            }
        }

        if(TextUtils.isEmpty(str_feed)){
            ToastUtil.showShort("请输入描述问题");
            pictures.add(new FeedPicture("temp", false, ""));
            return;
        }
        if(TextUtils.isEmpty(str_Phone)){
            ToastUtil.showShort("请输入联系人电话");
            pictures.add(new FeedPicture("temp", false, ""));
            return;
        }
        if(TextUtils.isEmpty(str_phone_name)){
            ToastUtil.showShort("请输入联系人");
            pictures.add(new FeedPicture("temp", false, ""));
            return;
        }
        /**
         *"contactTel":"12545568554",                     //必填 string 电话
         *         	"contactName":"张三",                           //必填 string 联系人
         *         	"reasonType":"sas_003",                         //必填 string "退款类型"
         *         	"indentId":1,                                   //必填 int    "订单id"
         *         	"reason":"掉色",                                //必填 stirng "售后问题描述"
         *         	"indentDetailId":152,                           //必填 int 订单项明细Id
         *         	"imgs":""
         */
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("contactTel",str_Phone);
        params.put("contactName",str_phone_name);
        params.put("reasonType",reasonKey);
        params.put("indentId",id);
        params.put("reason",str_feed);
        params.put("indentDetailId",orderInfor.getId());
        params.put("imgs",urls);
        params.put("type","returnbill_type_stauts_maintain");
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.SUBMIT_AFTER_SALE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                ToastUtil.showShort("申请售后成功");
                finish();
//                Gson gson = new Gson();
//                orderInfor = gson.fromJson(result, OrderInfor.class);
//                setView();
                //
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        },true);
    }
    //todo 获取售后原因
    private void getHotData() {
        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        HttpManager.getInstance().post(mContext, ApiConstants.COMMONAPP_GETALLKEYVALUE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                String keyjson = StringUtil.getJsonKeyStr(result,"refund_type");
                Gson gson = new Gson();
                List<HotkeyBean> list = gson.fromJson(keyjson,new TypeToken<List<HotkeyBean>>(){}.getType());
                resons=new ArrayList<>();
                for (int i=0 ; i<list.size() ; i++){
                    resons.add(list.get(i));
                }

            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);

            }
        },false);
    }

}
