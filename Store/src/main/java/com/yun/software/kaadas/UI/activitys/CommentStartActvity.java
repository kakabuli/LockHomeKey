package com.yun.software.kaadas.UI.activitys;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yun.software.kaadas.Http.ApiConstants;
import com.yun.software.kaadas.Http.HttpManager;
import com.yun.software.kaadas.Http.OnIResponseListener;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.adapter.CommentListAdapter;
import com.yun.software.kaadas.UI.bean.FeedPicture;
import com.yun.software.kaadas.UI.bean.IndentInfo;
import com.yun.software.kaadas.UI.bean.OrderInfor;
import com.yun.software.kaadas.UI.bean.SubmitComment;
import com.yun.software.kaadas.UI.view.GridItemDecoration;
import com.yun.software.kaadas.UI.view.dialog.MeichooseImage;
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
import la.xiong.androidquick.tool.LogUtils;
import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/5/20
 */
public class CommentStartActvity extends BaseActivity {
//    List<FeedPicture> pictures;

    @BindView(R2.id.rv_list)
    RecyclerView recyclerView;



    private OrderInfor orderInfor;

    private CommentListAdapter listAdapter;

    private List<SubmitComment> listBeans = new ArrayList<>();

    private String productId = "";
    private int position =0;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_comment;
    }

    @Override
    protected void initViewsAndEvents() {
        orderInfor = getIntent().getParcelableExtra("bean");
        for (IndentInfo  infor :orderInfor.getIndentInfo()){
            listBeans.add(new SubmitComment(String.valueOf(infor.getProductId()),infor.getLogo(),String.valueOf(infor.getIndentId()),infor.getProductLabels()));
        }
        tvTitle.setText("评论");
        tvRight.setText("提交");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setTextColor(getResources().getColor(R.color.light_blue));
//        pictures = new ArrayList<>();
//        pictures.add(new FeedPicture("temp", false));
//        rcImgs.setHasFixedSize(true);
//        rcImgs.setLayoutManager(new GridLayoutManager(mContext, 3));
//        Drawable decoration = ContextCompat.getDrawable(this, R.drawable.album_decoration_white);
//                int itemSize = (ScreenUtils.getScreenWidth() - decoration.getIntrinsicWidth() * (3 + 1)) / 3;
//                  mCommentimgsAdapter = new CommentImgsAdapter(pictures, itemSize);
//                  rcImgs.setAdapter(mCommentimgsAdapter);
//                  mCommentimgsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//                    @Override
//                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                        switch (view.getId()) {
//                            case R.id.view_iamge:
//                                if (pictures.get(position).getPath().equals("temp")) {
//                                    initDialogChooseImage();
//                                } else {
//                                    List<String> strings = new ArrayList<>();
//                                    strings.add(pictures.get(position).getPath());
//                                    BigImageActivity.startImagePagerActivity(CommentStartActvity.this, strings, 0);
//                                }
//                                break;
//                            case R.id.view_delete:
//                                pictures.remove(position);
//                                if (pictures.size() < 4) {
//                                    if (pictures.get(pictures.size() - 1).getPath().equals("temp")) {
//                                    } else {
//                                        pictures.add(new FeedPicture("temp", false));
//                                    }
//                                }
//                                mCommentimgsAdapter.notifyDataSetChanged();
//                                break;
//                        }
//                    }
//                });

        listAdapter=new CommentListAdapter(listBeans,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加自定义分割线
        GridItemDecoration.Builder  decoration1 = new GridItemDecoration.Builder(this);
        decoration1.setColor(getResources().getColor(R.color.default_bg_color));
        decoration1.setHorizontalSpan(getResources().getDimension(R.dimen.dk_dp_15));
        recyclerView.addItemDecoration(decoration1.build());

        recyclerView.setAdapter(listAdapter);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    /**
     * 提交数据.
     */
    private void submit() {

        for (SubmitComment comment:listBeans){
            if (TextUtils.isEmpty(comment.getContent())){
                ToastUtil.showShort("请填写评论");
                return;
            }

            String urls ="";
            List<FeedPicture> pictures = comment.getPictures();
            if (pictures.size() < 1){
                return;
            }
            for (FeedPicture picture :pictures){
                urls += picture.getUrl() + ",";
            }
            if (urls.length() > 1){
                urls = urls.substring(0,urls.length() -1);
            }
            comment.setImgs(urls);

        }

        Map<String,Object> map = new HashMap<>();
        map.put("token", UserUtils.getToken());
        Map<String,Object> params = new HashMap<>();
        params.put("list",listBeans);
        map.put("params",params);
        HttpManager.getInstance().post(mContext, ApiConstants.TBCOMMENT_SAVE, map, new OnIResponseListener() {
            @Override
            public void onSucceed(String result) {
                finish();
            }
            @Override
            public void onFailed(String error) {
                ToastUtil.showShort(error);
            }
        },true);
    }


    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }




    public void initDialogChooseImage(String productId,int position) {
        MeichooseImage dialogChooseImage = new MeichooseImage(this, MeichooseImage.LayoutType.TITLE);
        dialogChooseImage.show();
        this.productId = productId;
        this.position = position;

    }

    private void uploadFile(File file) {
        IProgressDialog dialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(CommentStartActvity.this);
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
                    String imageUrl = jsonObject.getString("fileRewriteFullyQualifiedPath");

                    for (SubmitComment comment :listBeans){
                        if (productId.equals(comment.getProductId())){
                            List<FeedPicture> pictures = comment.getPictures();
                            pictures.add(pictures.size() - 1, new FeedPicture(file.getAbsolutePath(), true,imageUrl));

                            if (pictures.size() > 3) {
                                pictures.remove(pictures.size() - 1);
                            }
//                            int size = comment.getPictures().size();
//                            comment.getPictures().add(size -1,new FeedPicture(file.getAbsolutePath(),true,imageUrl));
                        }
                    }
                    listAdapter.notifyDataSetChanged();


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

                break;

            case UCrop.REQUEST_CROP://UCrop裁剪之后的处理
                if (resultCode == RESULT_OK) {
                    Uri resultUri = UCrop.getOutput(data);
                    File file = roadImageView(resultUri);
                    String filePath = file.getAbsolutePath();
                    LogUtils.iTag("文件大小", FileUtils.getFileSizeFromate(filePath));
                    uploadFile(file);
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
                .withMaxResultSize(300, 300)
                .withOptions(options)
                .start(this);
    }
}
