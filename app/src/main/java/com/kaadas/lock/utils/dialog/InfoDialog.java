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
 * on 2020/6/24
 */
public class InfoDialog extends Dialog {

        private InfoDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        public static class Builder {

            private View mLayout;

            private ImageView mIcon;
            private TextView mTitle;
            private TextView mMessage;
            private Button mButton;

            private View.OnClickListener mButtonClickListener;
            private View.OnClickListener mIconClickListener;

            private InfoDialog mDialog;

            public Builder(Context context) {
                mDialog = new InfoDialog(context, R.style.dialog_loading_view);
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //加载布局文件
                mLayout = inflater.inflate(R.layout.activity_dialog, null, false);
                //添加布局文件到 Dialog
                mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

//                mIcon = mLayout.findViewById(R.id.dialog_icon);
//                mTitle = mLayout.findViewById(R.id.dialog_title);
//                mMessage = mLayout.findViewById(R.id.dialog_message);
//                mButton = mLayout.findViewById(R.id.dialog_button);

                mIcon = mLayout.findViewById(R.id.set_status);
                mMessage = mLayout.findViewById(R.id.set_text);
            }

            /**
             * 通过 ID 设置 Dialog 图标
             * 加监听
             */
            public Builder setIcon(int resId , View.OnClickListener listener) {
                mIcon.setImageResource(resId);
                mIconClickListener = listener;
                return this;
            }

            /**
             * 用 Bitmap 作为 Dialog 图标
             * 加监听
             */
            public Builder setIcon(Bitmap bitmap , View.OnClickListener listener) {
                mIcon.setImageBitmap(bitmap);
                mIconClickListener = listener;
                return this;
            }

//            /**
//             * 设置 Dialog 标题
//             */
//            public Builder setTitle(@NonNull String title) {
//                mTitle.setText(title);
//                mTitle.setVisibility(View.VISIBLE);
//                return this;
//            }
//
            /**
             * 设置 Message
             */
            public Builder setMessage(@NonNull int resId) {
                mMessage.setText(resId);
                return this;
            }
//
//            /**
//             * 设置按钮文字和监听
//             */
//            public Builder setButton(@NonNull String text, View.OnClickListener listener) {
//                mButton.setText(text);
//                mButtonClickListener = listener;
//                return this;
//            }

            public InfoDialog create() {
                mIcon.setOnClickListener(view -> {
                    mDialog.dismiss();
                    mIconClickListener.onClick(view);
                });
                mDialog.setContentView(mLayout);
//                mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
//                mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
                return mDialog;
            }
        }
    }
