package com.kaadas.lock.utils;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaadas.lock.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by David on 2019/2/20
 */
public class AlertDialogUtil {
    private static AlertDialogUtil alertDialogUtil = null;
    private AlertDialog dialog;

    public static AlertDialogUtil getInstance() {
        if (alertDialogUtil == null) {
            alertDialogUtil = new AlertDialogUtil();
        }
        return alertDialogUtil;
    }

    //通用dialog，具体使用参考PersonalSystemSettingActivity中的loginOut,xml中有四种布局，
    //xml中有四种布局，
    // 1 have_edit_dialog（存在输入框，两个按钮）2 no_button_dialog(只有内容的对话框)
    //3 no_edit_singleButton(没有输入框，只有一个按钮)4 no_et_dialog(没有输入框，有两个按钮)
    //由于有输入框这种需要不同的约束，所以不进行封装。
    public AlertDialog  common(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialog);
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setContentView(view); // 加载自定义布局,可完全覆盖dialog窗口3
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        params.width = (int) (width * 0.8);
        window.setAttributes(params);
        return dialog;
    }

    //1 no_button_dialog只有内容的对话框
    public AlertDialog noButtonDialog(Context context, String content) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_button_dialog, null);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvContent.setText(content);
        return alertDialog;
    }

    public AlertDialog noButtonSingleLineDialog(Context context, String content) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_button_dialog, null);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvContent.setText(content);
        return alertDialog;
    }
    //没有标题，只有内容和一个按钮
    public AlertDialog singleButtonNoTitleDialog(Context context, String content,String query,String queryColor ,ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_edit_singlebutton_dialog_no_title, null);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_query = mView.findViewById(R.id.tv_button);
        tvContent.setText(content);
        tv_query.setText(query);
        tv_query.setTextColor(Color.parseColor(queryColor));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);

        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.right();
                }
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }


    //2  no_edit_singleButton
    public void noEditSingleButtonDialog(Context context, String title, String content, String query, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_edit_singlebutton_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_query = mView.findViewById(R.id.tv_button);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        tvContent.setText(content);
        tv_query.setText(query);
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.right();
                }
                alertDialog.dismiss();
            }
        });
    }


    //2  不能隐藏的
    public void noEditSingleCanNotDismissButtonDialog(Context context, String title, String content, String query, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_edit_singlebutton_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_query = mView.findViewById(R.id.tv_button);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        alertDialog.setCancelable(false);
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        tvContent.setText(content);
        tv_query.setText(query);
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (clickListener != null) {
                    clickListener.right();
                }

            }
        });
    }



    //no_et_dialog
    public void noEditTwoButtonDialogWidthDialog_color(Context context, String title, String content, String left, String right, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tv_query.setTextColor(Color.parseColor("#101010"));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        if ("".equals(title)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_query.setText(right);
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (clickListener != null) {
                    clickListener.left();
                }

            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (clickListener != null) {
                    clickListener.right();
                }
            }
        });
    }
    List<AlertDialog> dialogArray=new ArrayList<>();
    //no_et_dialog
    public void noEditTwoButtonDialogWidthDialog_color_padding(Context context, String title, String content, String left, String right, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_dialog_padding, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tv_query.setTextColor(Color.parseColor("#101010"));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        dialogArray.add(alertDialog);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                for (int i=0;i<dialogArray.size();i++){
                    AlertDialog alertDialog1= dialogArray.get(i);
                    if(alertDialog1!=null && alertDialog1.isShowing()){
                        alertDialog1.dismiss();
                    }
                }
            }
        });
        if ("".equals(title)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_query.setText(right);
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<dialogArray.size();i++){
                    AlertDialog alertDialog1= dialogArray.get(i);
                    if(alertDialog1!=null && alertDialog1.isShowing()){
                        alertDialog1.dismiss();
                    }
                }
                dialogArray.clear();
              //  alertDialog.dismiss();
                if (clickListener != null) {
                    clickListener.left();
                }

            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      alertDialog.dismiss();
                for (int i=0;i<dialogArray.size();i++){
                    AlertDialog alertDialog1= dialogArray.get(i);
                    if(alertDialog1!=null && alertDialog1.isShowing()){
                        alertDialog1.dismiss();
                    }
                }
                dialogArray.clear();
                if (clickListener != null) {
                    clickListener.right();
                }
            }
        });
    }

    //no_et_dialog
    public void noEditTwoButtonDialog(Context context, String title, String content, String left, String right, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        if ("".equals(title)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_query.setText(right);
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (clickListener != null) {
                    clickListener.left();
                }

            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (clickListener != null) {
                    clickListener.right();
                }
            }
        });
    }


    //no_et_dialog
    public void noEditTwoButtonDialogCanNotDismiss(Context context, String title, String content, String left, String right, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        alertDialog.setCancelable(false);
        if ("".equals(title)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_query.setText(right);
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.left();
                }
                alertDialog.dismiss();
            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.right();
                }
                alertDialog.dismiss();
            }
        });
    }

    //没有标题的对话框
    public void noEditTitleTwoButtonDialog(Context context, String content, String left, String right,String leftColor,String rightColor, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_title_two_button_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_cancel.setTextColor(Color.parseColor(leftColor));
        tv_query.setText(right);
        tv_query.setTextColor(Color.parseColor(rightColor));
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.left();
                }
                alertDialog.dismiss();
            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.right();
                }
                alertDialog.dismiss();
            }
        });
    }

    //有标题的对话框
    public void havaNoEditTwoButtonDialog(Context context, String title, String content, String left, String right, String rightColor,ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_two_button_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvTitle.setText(title);
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_query.setText(right);
        tv_query.setTextColor(Color.parseColor(rightColor));
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.left();
                }
                alertDialog.dismiss();
            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.right();
                }
                alertDialog.dismiss();
            }
        });
    }


    public interface ClickListener {
        void left();

        void right();
    }

    public interface MessageListener {
        void message(String data);
    }
}
