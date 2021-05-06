package com.kaadas.lock.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.PirConst;
import com.kaadas.lock.utils.SPUtils2;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.microedition.khronos.opengles.GL;


public class PirHistoryAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public PirHistoryAdapter() {
        super(R.layout.pir_history_single_entry);
    }

    Context mContext=null;

    public PirHistoryAdapter(Context context,@Nullable List<String> data) {
        super(R.layout.pir_history_single_entry,data);
		this.mContext=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {




		//helper.setText(R.id.pir_img_time, item.split(" ")[1]);
		helper.setText(R.id.pir_img_time, item);

		// helper.addOnClickListener(R.id.pir_img_rl);

		String result=(String) SPUtils2.get(MyApplication.getInstance(),item,"");

		String imgPath=(String) SPUtils2.get(MyApplication.getInstance(),item+ PirConst.IMG_DOWNLOAD_SUC,"");
		//Log.e(PirConst.VideoLog,"item:"+item+ PirConst.IMG_DOWNLOAD_SUC+"  PirHistoryAdapter:"+imgPath);


//		if(!TextUtils.isEmpty(imgPath)){
//			Glide.with(mContext).load(imgPath)
//				.error(R.mipmap.pir_history_item_default)
//				.diskCacheStrategy(DiskCacheStrategy.NONE)
//				.placeholder(R.mipmap.pir_history_item_default)
//				.into((ImageView) helper.getView(R.id.pir_img_icon));
//		}else{
//			((ImageView)helper.getView(R.id.pir_img_icon))
//				.setImageResource(R.mipmap.pir_history_item_default);
//		}


		RequestOptions options = new RequestOptions()
				.placeholder(R.mipmap.pir_history_item_default)      //加载成功之前占位图
				.error(R.mipmap.pir_history_item_default)      //加载错误之后的错误图
				.diskCacheStrategy(DiskCacheStrategy.NONE);

//		Glide.with(mContext).load(imgPath)
//				.error(R.mipmap.pir_history_item_default)
//				.diskCacheStrategy(DiskCacheStrategy.NONE)
//				.placeholder(R.mipmap.pir_history_item_default)
//				.into((ImageView) helper.getView(R.id.pir_img_icon));

		Glide.with(mContext).load(imgPath).apply(options).into((ImageView) helper.getView(R.id.pir_img_icon));


		if(!TextUtils.isEmpty(result)){
		   helper.getView(R.id.pir_history_tv_cicle).setVisibility(View.INVISIBLE);
		}else {
			helper.getView(R.id.pir_history_tv_cicle).setVisibility(View.VISIBLE);
		}

     //   helper.addOnClickListener(R.id.img_pir_del);
     
    }
}
