package com.yun.software.kaadas.UI.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yanliang
 * on 2018/11/7 15:45
 */

public class BigImageActivity extends BaseActivity {
    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
    private TextView tvpager;
    private int imagesize;

    public static void startImagePagerActivity(Activity activity, List<String> imgUrls, int position){
        Intent intent = new Intent(activity, BigImageActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in,
                R.anim.fade_out);
    }


    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BigImageActivity.this.finish();
            BigImageActivity.this.overridePendingTransition(R.anim.fade_in,
                    R.anim.fade_out);
            return true;
        }
       return super.onKeyDown(keyCode,event);
    }


//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_welcome);
        super.onCreate(savedInstanceState);

    }


    @Override
    protected boolean isLoadDefaultTitleBar() {
        return false;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.act_image_pager;
    }

    @Override
    protected void initViewsAndEvents() {
        ViewPager viewPager =findViewById(R.id.pager);
        guideGroup = (LinearLayout) findViewById(R.id.guideGroup);
        tvpager=(TextView)findViewById(R.id.tv_pager);
        int startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        ArrayList<String> imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);
        imagesize=imgUrls.size();
        ImageAdapter mAdapter = new ImageAdapter(this);
        mAdapter.setDatas(imgUrls);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvpager.setText((position+1)+"/"+imagesize);
                //                for(int i=0; i<guideViewList.size(); i++){
                //                    guideViewList.get(i).setSelected(i==position ? true : false);
                //                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);
        if(imagesize>1){
            tvpager.setVisibility(View.VISIBLE);
        }else{
            tvpager.setVisibility(View.GONE);
        }
        tvpager.setText((startPos+1)+"/"+imgUrls.size());
        //            addGuideView(guideGroup, startPos, imgUrls);
        //        if(imgUrls.size()>3){
        //            tvpager.setVisibility(View.GONE);
        //            addGuideView(guideGroup, startPos, imgUrls);
        //        }else{
        //            tvpager.setVisibility(View.VISIBLE);
        //            addGuideView(guideGroup, startPos, imgUrls);
        //        }
    }


    private  class ImageAdapter extends PagerAdapter {

        private List<String> datas = new ArrayList<String>();
        private LayoutInflater inflater;
        private Context context;

        public void setDatas(List<String> datas) {
            if(datas != null )
                this.datas = datas;
        }

        public ImageAdapter(Context context){
            this.context = context;
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            if(datas == null) return 0;
            return datas.size();
        }
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if(view != null){
                final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);
                imageView.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(ImageView view, float x, float y) {
                        BigImageActivity.this.finish();
                        BigImageActivity.this.overridePendingTransition(R.anim.act_fade_in_center,
                                R.anim.act_fade_out_center);
                    }
                });

                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout)view).addView(loading);

                final String imgurl = datas.get(position);

                loading.setVisibility(View.VISIBLE);

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_empty_picture)
                        ;


                Glide.with(context).load(imgurl)
                        .thumbnail(0.1f)
                        .apply(options)
                        .listener(new RequestListener< Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loading.setVisibility(View.GONE);
                                return false;
                            }

                        })
                        .into(imageView);

                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }
}
