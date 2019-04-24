package com.kaadas.lock.activity.device.bluetooth.password;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class BluetoothPasswordManagerDetailActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_editor)
    ImageView ivEditor;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_password_manager_detail);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.user_password));
        ivEditor.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_editor:
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                //TODO 获取到密码设置
              /*  if (password.getNickName()!=null){
                    editText.setText(password.getNickName());
                    editText.setSelection(password.getNickName().length());
                }*/
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.please_input_password_name));
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        //todo 获取到密码对比
                  /*      if (StringUtil.judgeNicknameWhetherSame(password.getNickName(),name)){
                            ToastUtil.getInstance().showShort(R.string.nickname_not_modify);
                            alertDialog.dismiss();
                            return;
                        }*/
                        //todo 更新昵称
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_delete:
                if (NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, "", getString(R.string.sure_delete_password), getString(R.string.cancel), getString(R.string.delete), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            //确认删除
                            //TODO 删除
                        }
                    });
                } else {
                    ToastUtil.getInstance().showLong(R.string.network_exception);
                }
                break;
        }
    }
}
