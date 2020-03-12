package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewNotActivateActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.iv_wei_xin_code)
    ImageView ivWeiXinCode;
    @BindView(R.id.lock_activated)
    TextView lockActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_not_activate);
        ButterKnife.bind(this);
        ivWeiXinCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean b = saveImageToGallery(WifiLockAddNewNotActivateActivity.this, ivWeiXinCode);
                if (b) {
                    Toast.makeText(WifiLockAddNewNotActivateActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WifiLockAddNewNotActivateActivity.this, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }


    //保存文件到指定路径
    public boolean saveImageToGallery(Context context, ImageView imageView) {
        BitmapDrawable bmpDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bmp = bmpDrawable.getBitmap();
        if (bmp == null) {
            Toast.makeText(context, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
            return false;
        }
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnClick({R.id.back, R.id.help, R.id.lock_activated})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(WifiLockAddNewNotActivateActivity.this,WifiLockHelpActivity.class));
                break;
            case R.id.lock_activated:
                startActivity(new Intent(this,WifiLockAddNewThirdActivity.class));
                break;
        }
    }
}
