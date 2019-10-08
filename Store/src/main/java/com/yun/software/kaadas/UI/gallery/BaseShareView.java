package com.yun.software.kaadas.UI.gallery;

import android.content.Intent;
import android.view.View;

/**
 * Created by yanliang
 * on 2019/3/20
 */
public abstract class BaseShareView {
    public abstract View getLayoutView();

    public abstract void initDate(int postion);
    public abstract void saveImage();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

}
