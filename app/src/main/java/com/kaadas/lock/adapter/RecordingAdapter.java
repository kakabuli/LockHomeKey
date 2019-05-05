package com.kaadas.lock.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.PirConst;
import com.kaadas.lock.utils.SPUtils2;

import java.util.List;


public class RecordingAdapter extends BaseQuickAdapter<String, BaseViewHolder> {



    public RecordingAdapter() {
        super(R.layout.recording_single_entry);
    }

    Context mContext=null;

    public RecordingAdapter(Context context, @Nullable List<String> data) {
        super(R.layout.recording_single_entry,data);
		this.mContext=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {


		helper.setText(R.id.pir_img_time, item);


		// helper.addOnClickListener(R.id.pir_img_rl);

		String result=(String) SPUtils2.get(MyApplication.getInstance(),item,"");

		String imgPath=(String) SPUtils2.get(MyApplication.getInstance(),item+ PirConst.IMG_DOWNLOAD_SUC,"");
		Log.e(PirConst.VideoLog,"item:"+item+ PirConst.IMG_DOWNLOAD_SUC+"  PirHistoryAdapter:"+imgPath);



		if(!TextUtils.isEmpty(result)){
		   helper.getView(R.id.pir_history_tv_cicle).setVisibility(View.INVISIBLE);
		}else {
			helper.getView(R.id.pir_history_tv_cicle).setVisibility(View.VISIBLE);
		}

     //   helper.addOnClickListener(R.id.img_pir_del);
     
    }
}
