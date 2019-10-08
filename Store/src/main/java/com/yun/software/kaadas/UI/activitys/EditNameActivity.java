package com.yun.software.kaadas.UI.activitys;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import la.xiong.androidquick.tool.ToastUtil;

public class EditNameActivity extends BaseActivity {


    @BindView(R2.id.edit_text)
    EditText editText;

    private String name;

    @Override
    protected boolean isLoadDefaultTitleBar() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_edit_name;
    }

    @Override
    protected void initViewsAndEvents() {

        tvTitle.setText("昵称");
        name = getIntent().getStringExtra("name");
        editText.setText(name);
    }

    @OnClick({R2.id.tv_sure})
    public void onClick(View view){
        int i = view.getId();
        if (i == R.id.tv_sure) {
            String name = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                Intent intent = new Intent();
                intent.putExtra("name", name);
                setResult(1, intent);
                finish();
            } else {
                ToastUtil.showShort("请输入昵称");
            }


        }
    }


}
