package com.kaadas.lock.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.my.PersonalUserAgreementActivity;
import com.kaadas.lock.activity.my.PrivacyActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


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
    //5 hava_title_content_no_button
    //由于有输入框这种需要不同的约束，所以不进行封装。
    public AlertDialog common(Context context, View view) {
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
    public AlertDialog singleButtonNoTitleDialog(Context context, String content, String query, String queryColor, ClickListener clickListener) {
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

    //没有标题，只有内容和一个按钮
    public AlertDialog singleButtonNoTitleDialogNoLine(Context context, String content, String query, String queryColor, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_edit_singlebutton_dialog_no_title_noline, null);
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

    public void permissionTipsDialog(Context context, String title, String content, String content1, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.permission_tips_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tvContentTisp = mView.findViewById(R.id.tv_content_tisp);
        TextView tv_query = mView.findViewById(R.id.tv_button);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        tvContent.setText(content);
        tvContentTisp.setText(content1);
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

    public void KaadasSingleButtonDialog(Context context, String title, String content, String query, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.kaadas_singlebutton_dialog, null);
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

        if("".equals(content)){
            tvContent.setVisibility(View.GONE);
        }else{
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(content);
        }
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
     //   tv_query.setTextColor(Color.parseColor("#101010"));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        alertDialog.setCancelable(false);//窗口外不隐藏
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
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
    public void noEditTwoButtonTwoContentDialog(Context context, String title, String content1, String content2,String left, String right, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_two_content_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tvContent1 = mView.findViewById(R.id.tv_content_1);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        TextView tv_view = mView.findViewById(R.id.tv_view);
        //   tv_query.setTextColor(Color.parseColor("#101010"));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        alertDialog.setCancelable(false);//窗口外不隐藏
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        tvContent.setText(content1);
        tvContent1.setText(content2);
        if("".equals(left)){
            tv_cancel.setVisibility(View.GONE);
            tv_view.setVisibility(View.GONE);
        }else{
            tv_cancel.setVisibility(View.VISIBLE);
            tv_cancel.setText(left);
            tv_view.setVisibility(View.VISIBLE);
        }
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



    //have_edit_dialog
    public void havaEditTwoButtonDialogWidthDialogEdit(Context context, String title, String content, String left, String right, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_etit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_hint);
        EditText tvContent = mView.findViewById(R.id.et_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
     //   tv_query.setTextColor(Color.parseColor("#101010"));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
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
        tvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                LogUtils.e("--kaadas--","输入后字符串 [ " + s.toString() + " ] 起始光标 [ " + start + " ] 输入数量 [ " + count+" ]");
            }
            @Override
            public void afterTextChanged(Editable s) {
//                LogUtils.e("--kaadas--","输入结束后的内容为 [" + s.toString()+" ] 即将显示在屏幕上");
                if (clickListener != null) {
                    clickListener.afterTextChanged(s.toString());
                }
            }
        });
    }

    List<AlertDialog> dialogArray = new ArrayList<>();

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
                for (int i = 0; i < dialogArray.size(); i++) {
                    AlertDialog alertDialog1 = dialogArray.get(i);
                    if (alertDialog1 != null && alertDialog1.isShowing()) {
                        alertDialog1.dismiss();
                    }
                }
            }
        });
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
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
                for (int i = 0; i < dialogArray.size(); i++) {
                    AlertDialog alertDialog1 = dialogArray.get(i);
                    if (alertDialog1 != null && alertDialog1.isShowing()) {
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
                try{
                    for (int i = 0; i < dialogArray.size(); i++) {
                        AlertDialog alertDialog1 = dialogArray.get(i);
                        if (alertDialog1 != null && alertDialog1.isShowing()) {
                            alertDialog1.dismiss();
                        }
                    }
                    dialogArray.clear();
                    if (clickListener != null) {
                        clickListener.right();
                    }
                }catch (Exception e){

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
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
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
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
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
    public void noEditTitleOneButtonDialog(Context context, String content, String right, String rightColor, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_title_one_button_dialog, null);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvContent.setText(content);
        tv_query.setText(right);
        tv_query.setTextColor(Color.parseColor(rightColor));
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

    public void titleTwoButtonKaadasDialog(Context context, String title,String content,String left, String right, String leftColor, String rightColor, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.kaadas_title_two_button_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvTitle.setText(title);
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

    //没有标题的对话框
    public void noEditTitleTwoButtonDialog(Context context, String content, String left, String right, String leftColor, String rightColor, ClickListener clickListener) {
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
    public void havaNoEditTwoButtonDialog(Context context, String title, String content, String left, String right, String rightColor, ClickListener clickListener) {
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

    //有标题的对话框
    public void havaNoEditTwoButtonDialog(Context context, String title, String content, String left, String right, String lefrColor,String rightColor, ClickListener clickListener) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_et_two_button_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvTitle.setText(title);
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_cancel.setTextColor(Color.parseColor(lefrColor));
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


    //    hava_title_content_no_button
    public void haveTitleContentNoButtonDialog(Context context, String title, String content ,Integer disappearTime) {
        View mView = LayoutInflater.from(context).inflate(R.layout.no_edit_button_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        if ("".equals(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        tvContent.setText(content);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {  //延时5秒消失
                alertDialog.dismiss();
            }
        }, disappearTime *1000);

    }

    //晾衣机风干弹窗
    public void clothesHangerMachineDialog(Context context,String content,String left,String right,
                                           String lefrColor,String rightColor,ClothesHangerMachineClickListener clothesHangerMachineClickListener){
        View mView = LayoutInflater.from(context).inflate(R.layout.clothes_hanger_machine_two_button_dialog, null);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        TextView tv_time_long = mView.findViewById(R.id.tv_time_long);
        TextView tv_time_short = mView.findViewById(R.id.tv_time_short);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_cancel.setTextColor(Color.parseColor(lefrColor));
        tv_query.setText(right);
        tv_query.setTextColor(Color.parseColor(rightColor));
        tv_time_short.setSelected(true);
        tv_time_short.setTextColor(Color.parseColor("#ffffff"));
        tv_time_long.setSelected(false);
        tv_time_long.setTextColor(Color.parseColor("#333333"));
        tv_time_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_time_short.setSelected(true);
                tv_time_short.setTextColor(Color.parseColor("#ffffff"));
                tv_time_long.setSelected(false);
                tv_time_long.setTextColor(Color.parseColor("#333333"));
            }
        });
        tv_time_long.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_time_long.setSelected(true);
                tv_time_long.setTextColor(Color.parseColor("#ffffff"));
                tv_time_short.setSelected(false);
                tv_time_short.setTextColor(Color.parseColor("#333333"));
            }
        });
        //取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clothesHangerMachineClickListener != null) {
                    clothesHangerMachineClickListener.left();
                }
                alertDialog.dismiss();
            }
        });
        //确定
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clothesHangerMachineClickListener != null) {
                    if(tv_time_short.isSelected()){
                        clothesHangerMachineClickListener.right(120);
                    }else{
                        clothesHangerMachineClickListener.right(240);
                    }
                }
                alertDialog.dismiss();
            }
        });
    }

    //声明于条款弹窗
    public void statementAndTermsDialog(Context context, String title, String content, String left, String right, ClickListener clickListener){
        View mView = LayoutInflater.from(context).inflate(R.layout.statements_and_terms_two_button_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        TextView tvContent = mView.findViewById(R.id.tv_content);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(context, mView);
        alertDialog.setCancelable(false);//窗口外不隐藏
        tvTitle.setText(title);
        tvContent.setText(content);
        tv_cancel.setText(left);
        tv_query.setText(right);
        String privacyPolicyStr = context.getResources().getString(R.string.privacy_policy);
        SpannableString privacyPolicySpannable = new SpannableString(privacyPolicyStr);
        LinkClickableSpan privacyPolicySpan = new LinkClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent primatyIntent = new Intent(context, PrivacyActivity.class);
                context.startActivity(primatyIntent);
            }
        };
        String termsOfUseStr = context.getResources().getString(R.string.user_protocol2);
        SpannableString termsOfUseSpannable = new SpannableString(termsOfUseStr);
        LinkClickableSpan termsOfUseSpan = new LinkClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent agreementIntent = new Intent(context, PersonalUserAgreementActivity.class);
                context.startActivity(agreementIntent);
            }
        };
        termsOfUseSpannable.setSpan(termsOfUseSpan, 0, privacyPolicySpannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        privacyPolicySpannable.setSpan(privacyPolicySpan, 0, privacyPolicySpannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvContent.append(privacyPolicySpannable);
        tvContent.append(context.getResources().getString(R.string.and));
        tvContent.append(termsOfUseSpannable);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
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
        alertDialog.setCanceledOnTouchOutside(false);
    }


    public interface ClothesHangerMachineClickListener{
        void left();
        void right(int time);
    }

    public interface ClickListener {
        void left();

        void right();

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(String toString);
    }

    public interface MessageListener {
        void message(String data);
    }
}
