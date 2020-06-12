package com.kaadas.lock.activity.addDevice.singleswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.AlertDialogUtil;

import butterknife.BindView;

public class SwipchSeetingArgus extends AppCompatActivity implements View.OnClickListener {
    TextView tv_content;
    ImageView iv_back;
    RelativeLayout swipch_setting_arugs_change,swipch_link_btn_one_rl,swipch_link_btn_two_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipch_seeting_argus);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.setting));
        iv_back = findViewById(R.id.iv_back);
        swipch_setting_arugs_change = findViewById(R.id.swipch_setting_arugs_change);
        swipch_link_btn_one_rl = findViewById(R.id.swipch_link_btn_one_rl);
        swipch_link_btn_two_rl = findViewById(R.id.swipch_link_btn_two_rl);
        swipch_setting_arugs_change.setOnClickListener(this);
        swipch_link_btn_one_rl.setOnClickListener(this);
        swipch_link_btn_two_rl.setOnClickListener(this);
        iv_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.swipch_setting_arugs_change:
//(Context context, String title, String content, String left, String right
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_input_title_name),
                        getString(R.string.swipch_setting_pluease_input_context_name)
                        , getString(R.string.cancel),
                          getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });

                break;
            case R.id.swipch_link_btn_one_rl:
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialogEdit(
                        SwipchSeetingArgus.this,
                        getString(R.string.swipch_setting_pluease_dialog_title1)
                        , getString(R.string.cancel),
                        getString(R.string.query), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }
                        });
                break;
            case R.id.swipch_link_btn_two_rl:

                break;
            case R.id.iv_back:
                finish();
                break;

        }
    }
}
