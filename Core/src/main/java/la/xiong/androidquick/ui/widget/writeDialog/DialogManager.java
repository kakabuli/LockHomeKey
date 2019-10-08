package la.xiong.androidquick.ui.widget.writeDialog;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import la.xiong.androidquick.R;
import la.xiong.androidquick.tool.ToastUtil;

/**
 * Created by yanliang
 * on 2019/3/13
 */
public class DialogManager {
    private EditText mEditText;
    private TextView tvCancle;
    private TextView tvPublish;
    private Activity activity;
    private BottomDialog bottomDialog;

    private DialogManager() {
    }
    public static DialogManager instance = null;

    public static DialogManager getInstance() {
        if (instance == null) {
            //多个线程判断instance都为null时，在执行new操作时多线程会出现重复情况
            instance = new DialogManager();
        }
        return instance;
    }






    /**
     * 弹出评论dialog
     */
    public void showCommentDialog(FragmentManager mFragmentManager, Activity activity) {
        this.activity=activity;
        bottomDialog=BottomDialog.create(mFragmentManager)
                .setLayoutRes(R.layout.dialog_comment_view)
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initView(v);
                    }
                })
                .setDimAmount(0.6f)
                .setCancelOutside(false)
                .setTag("comment")
                .show();
    }
    private void initView(View v) {
        mEditText = (EditText) v.findViewById(R.id.edit_text);
        showInput(mEditText);
        tvCancle = (TextView) v.findViewById(R.id.tv_cancle);
        tvPublish = (TextView) v.findViewById(R.id.tv_publish);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
//                    mButton.setBackgroundResource(R.drawable.dialog_send_btn);
//                    mButton.setEnabled(false);
                } else {
//                    mButton.setBackgroundResource(R.drawable.dialog_send_btn_pressed);
//                    mButton.setEnabled(true);
                }
            }
        });

        mEditText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, 0);
            }
        });
        tvPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort("评论：" + mEditText.getText().toString());
            }
        });
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomDialog!=null){
                    bottomDialog.dismiss();
                }

            }
        });

    }
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

    }

}
