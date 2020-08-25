package com.kaadas.lock.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;

/**
 * Created by hushucong
 * on 2020/6/30
 */
public class MessageDialog extends Dialog {

        private MessageDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        public static class Builder {

            private View mLayout;

            private TextView mMessage;

            private View.OnClickListener mMessageClickListener;

            private MessageDialog mDialog;

            public Builder(Context context) {
                mDialog = new MessageDialog(context, R.style.dialog_loading_view);
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //加载布局文件
                mLayout = inflater.inflate(R.layout.activity_message_dialog, null, false);
                //添加布局文件到 Dialog
                mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                mMessage = mLayout.findViewById(R.id.set_text);
            }

            /**
             * 设置 Message
             */
            public Builder setMessage(@NonNull int resId) {
                mMessage.setText(resId);
                return this;
            }


            public MessageDialog create() {
//                mMessage.setOnClickListener(view -> {
//                    mDialog.dismiss();
//                    mMessageClickListener.onClick(view);
//                });
                mDialog.setContentView(mLayout);
//                mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
//                mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                return mDialog;
            }
        }
    }
