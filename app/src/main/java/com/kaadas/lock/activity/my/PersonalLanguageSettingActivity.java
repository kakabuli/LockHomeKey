package com.kaadas.lock.activity.my;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;

import net.sdvn.cmapi.util.LogUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalLanguageSettingActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.zh_text)
    TextView zhText;
    @BindView(R.id.zh_img)
    CheckBox zhImg;
    @BindView(R.id.zh_layout)
    RelativeLayout zhLayout;
    @BindView(R.id.cb_ct)
    CheckBox cbCt;
    @BindView(R.id.rl_tranitional_chinese)
    RelativeLayout rlTranitionalChinese;
    @BindView(R.id.en_text)
    TextView enText;
    @BindView(R.id.en_img)
    CheckBox enImg;
    @BindView(R.id.en_layout)
    RelativeLayout enLayout;
    @BindView(R.id.thai_text)
    TextView thaiText;
    @BindView(R.id.thai_img)
    CheckBox thaiImg;
    @BindView(R.id.thai_layout)
    RelativeLayout thaiLayout;
    @BindView(R.id.language_confirm)
    Button languageConfirm;
    private Context context;
    private String languageCurrent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_language_setting);
        ButterKnife.bind(this);
        context = MyApplication.getInstance();
        initData();
        tvContent.setText(R.string.language_setting);
        ivBack.setOnClickListener(this);

    }

    private void initData() {
        checkLag();
    }

    @OnClick({R.id.zh_layout, R.id.rl_tranitional_chinese, R.id.en_layout, R.id.thai_layout, R.id.language_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.zh_layout:
                zhImg.setChecked(true);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                languageCurrent = "zh";
                SPUtils.putProtect( "lag", "zh");
                break;
            case R.id.rl_tranitional_chinese:
                //繁体中文
                cbCt.setChecked(true);
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                languageCurrent = "tw";
                SPUtils.putProtect( "lag", "tw");
                break;
            case R.id.en_layout:
                zhImg.setChecked(false);
                enImg.setChecked(true);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                languageCurrent = "en";
                SPUtils.putProtect( "lag", "en");
                break;
            case R.id.thai_layout:
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(true);
                cbCt.setChecked(false);
                languageCurrent = "th";
                SPUtils.putProtect( "lag", "th");
                break;
            case R.id.language_confirm:
                if (!TextUtils.isEmpty(languageCurrent)) {
//                    SPUtils.putProtect( "checklag", true);
                    switchLanguage(languageCurrent);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
                break;
        }
    }

    /**
     * android语言切换
     *
     * @param language
     */
    protected void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("tw")) {
            config.locale = Locale.TAIWAN;
        } else if (language.equals("th")) {
            config.locale = new Locale("th");
        } else {
            config.locale = Locale.ENGLISH;
        }
        resources.updateConfiguration(config, dm);
        //保存设置语言的类型
        SPUtils.putProtect("language", language);
    }

 /*   protected void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals("tw")) {
            config.locale = Locale.TAIWAN;
        }  else if (language.equals("tai")) {
            config.locale = new Locale("th");
        } else {
            config.locale = Locale.ENGLISH;
        }
        resources.updateConfiguration(config, dm);
        //保存设置语言的类型
        CacheFloder.writeLanguage(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "language", language);
    }*/


  /*  private void checkLag() {
        //是否设置过语言
        String language = CacheFloder.readLanguage(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "language");
        if (TextUtils.isEmpty(language)) {
            Locale locale = getResources().getConfiguration().locale;
            String localeLanguage = locale.getLanguage();//获取本地语言
            if (locale.equals(Locale.SIMPLIFIED_CHINESE)) { //简体中文
                zhImg.setChecked(true);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                CacheFloder.writeLanguage(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "language", "zh");
            } else if (localeLanguage.endsWith("tai")) {
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(true);
                cbCt.setChecked(false);
                CacheFloder.writeLanguage(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "language", "tai");
            } else {
                zhImg.setChecked(false);
                enImg.setChecked(true);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                CacheFloder.writeLanguage(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "language", "en");
            }
        } else {
            if (language.equals("zh")) {
                zhImg.setChecked(true);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
            } else if (language.equals("tai")) {
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(true);
                cbCt.setChecked(false);
            } else {
                zhImg.setChecked(false);
                enImg.setChecked(true);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
            }
        }
    }*/

    private void checkLag() {
        //是否设置过语言
        boolean checklag = (Boolean) SPUtils.getProtect( "checklag", false);
        if (!checklag) {
            Locale locale = getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            LogUtils.d("davi local  "+locale.toLanguageTag());
            if (locale.equals( Locale.SIMPLIFIED_CHINESE)) { //简体中文
                zhImg.setChecked(true);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                SPUtils.putProtect("lag", "zh");
            } else if (locale.equals( Locale.TAIWAN)) { //繁体中文
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(true);
                SPUtils.putProtect( "lag", "tw");
            } else if (language.endsWith("th")) {
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(true);
                cbCt.setChecked(false);
                SPUtils.putProtect( "lag", "th");
            } else {
                zhImg.setChecked(false);
                enImg.setChecked(true);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                SPUtils.putProtect( "lag", "en");
            }
        } else {
            String lag = (String) SPUtils.getProtect( "lag", "");
            if (lag.equals("zh")) {
                zhImg.setChecked(true);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                SPUtils.putProtect( "lag", "zh");
            } else if (lag.equals("en")) {
                zhImg.setChecked(false);
                enImg.setChecked(true);
                thaiImg.setChecked(false);
                cbCt.setChecked(false);
                SPUtils.putProtect( "lag", "en");
            } else if (lag.equals("tw")) {
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(false);
                cbCt.setChecked(true);
                SPUtils.putProtect( "lag", "tw");
            } else if (lag.equals("th")) {
                zhImg.setChecked(false);
                enImg.setChecked(false);
                thaiImg.setChecked(true);
                cbCt.setChecked(false);
                SPUtils.putProtect("lag", "th");
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
