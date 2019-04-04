package com.kaadas.lock.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;

import java.util.Locale;


/**
 * 更改语言
 */
public class CheckLanguageUtil {
    private static CheckLanguageUtil checkLanguageUtil = null;

    private CheckLanguageUtil() {
    }

    public static CheckLanguageUtil getInstance() {
        if (checkLanguageUtil == null) {
            checkLanguageUtil = new CheckLanguageUtil();
        }
        return checkLanguageUtil;
    }


    public void checkLag() {
        String lag = CacheFloder.readLanguage(ACache.get(MyApplication.getInstance()), MyApplication.getInstance().getUid() + "language");//是否存在语言设置
        Context context = MyApplication.getInstance();
        if (!TextUtils.isEmpty(lag)) {
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (lag.equals("zh")) {
                config.locale = Locale.SIMPLIFIED_CHINESE;
            } else if (lag.equals("tai")) {
                config.locale = new Locale("th");
            } else {
                config.locale = Locale.ENGLISH;
            }
            resources.updateConfiguration(config, dm);
        } else {
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Locale locale = context.getResources().getConfiguration().locale;
            Locale curLocale = context.getResources().getConfiguration().locale;
            String language = locale.getLanguage();

            if (Build.VERSION.SDK_INT < 24) {
                curLocale = Locale.getDefault();

            } else {
                /*
                 * 以下两种方法等价，都是获取经API调整过的系统语言列表（可能与用户实际设置的不同）
                 * 1.context.getResources().getConfiguration().getLocales()
                 * 2.LocaleList.getAdjustedDefault()
                 */
                // 获取用户实际设置的语言列表
                curLocale = LocaleList.getDefault().get(0);
            }
            if (curLocale.getCountry().equals("CN") || curLocale.toLanguageTag().indexOf("Hans") != -1) { //地区是CN的是中文简体  包含有Hans 都认为是中文简体
                config.locale = Locale.SIMPLIFIED_CHINESE;
            } else if (language.equals("th")) {
                config.locale = new Locale("th");
            } else {
                config.locale = Locale.ENGLISH;
            }
            resources.updateConfiguration(config, dm);
        }
    }


    public static final int LAG_EN = 0;
    public static final int LAG_ZH_CN = 1;
    public static final int LAG_ZH_TW = 2;
    public static final int LAG_TI = 3;


/*	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public static int getCurrentLag(Context context) {
		String language = CacheFloder.readLanguage(ACache.get(MyApplication.getInstance()),MyApplication.getInstance().getUid()+"language");
		if (!TextUtils.isEmpty(language)) {
			// 设置应用语言类型
			if (language.equals("zh")) {
				return LAG_ZH_CN;
			} else if (language.equals("tai")) {
				return LAG_TI;
			} else {
				return LAG_EN;
			}
		} else {
			Resources resources = context.getResources();
			Locale curLocale;
			if (Build.VERSION.SDK_INT < 24) {
				curLocale = context.getResources().getConfiguration().locale;
				// 7.0以上获取系统首选语言
			} else {
				// 获取用户实际设置的语言列表
				curLocale = LocaleList.getDefault().get(0);
			}
			String localeLanguage = curLocale.getLanguage();

			if (curLocale.getCountry().equals("CN") || curLocale.toLanguageTag().indexOf("Hans") != -1) { //地区是CN的是中文简体  包含有Hans 都认为是中文简体
				return LAG_ZH_CN;
			} else if (localeLanguage.equals("th")) {
				return LAG_TI;
			} else {
				return LAG_EN;
			}
		}
	}*/


}
