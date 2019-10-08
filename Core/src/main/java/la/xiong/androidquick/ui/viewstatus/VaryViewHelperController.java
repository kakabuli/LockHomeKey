package la.xiong.androidquick.ui.viewstatus;


import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import la.xiong.androidquick.R;
import la.xiong.androidquick.tool.StringUtil;

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class VaryViewHelperController {

    private IVaryViewHelper helper;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelper(view));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        super();
        this.helper = helper;
    }

    public void showNetworkError(View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.view_status);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        textView.setText(helper.getContext().getResources().getString(R.string.common_no_network_msg));

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.no_network);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    public void showError(String errorMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.view_status);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!StringUtil.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_error_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.no_collection);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    public void showEmpty(String emptyMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.view_status);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!StringUtil.isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.no_data);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    public void showEmptyImage(@DrawableRes int id, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.view_image);
        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        if (id != 0) {
            imageView.setImageResource(id);
        } else {
            imageView.setImageResource(R.drawable.no_data);
        }
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    public void showLoading(String msg) {
        View layout = helper.inflate(R.layout.view_status);
        if (!StringUtil.isEmpty(msg)) {
            TextView textView = (TextView) layout.findViewById(R.id.message_info);
            textView.setText(msg);
        }
        helper.showLayout(layout);
    }
    public void showLoading(String msg, Context mcontext) {
        View layout = helper.inflate(R.layout.view_loading);
        ImageView imageView=layout.findViewById(R.id.iv_load);
        Glide.with(mcontext).load(R.drawable.load_gif).into(imageView);
        if (!StringUtil.isEmpty(msg)) {
            TextView textView = (TextView) layout.findViewById(R.id.tv_tips);
            textView.setText(msg);
        }
        helper.showLayout(layout);
    }

    public void restore() {
        helper.restoreView();
    }
}
