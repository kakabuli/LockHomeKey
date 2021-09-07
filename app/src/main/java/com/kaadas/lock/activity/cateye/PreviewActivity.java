package com.kaadas.lock.activity.cateye;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;

import com.bumptech.glide.Glide;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.FtpEnable;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.FtpException;
import com.kaadas.lock.utils.ftp.FtpUtils;
import com.kaadas.lock.utils.ftp.GeTui;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends BaseActivity<ISnapShotView, SnapPresenter<ISnapShotView>> implements ISnapShotView, View.OnClickListener{


    String imageUrl=null;
    String deviceId= null;
    String newImgUrl=null;
    String gatewayId=null;
    SimpleDateFormat simpleDateFormat8 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat0 = new SimpleDateFormat("yyyyMMdd");

    @BindView(R.id.loading_download_tv)
    TextView loading_download_tv;  // 下载中

    @BindView(R.id.preview_reload_download_tv)
    LinearLayout preview_reload_download_tv;  // 重新下载


    @BindView(R.id.history_loading_download_pb)
    ProgressBar history_loading_download_pb;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.preview_img)
    PhotoView preview_img;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.preview_look_video)
    Button preview_look_video;

    @BindView(R.id.preview_loading_download_rl)
    RelativeLayout preview_loading_download_rl;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    String imgPath=null;
    String currentTimeFolder=null;
    String remoteTimeFoler=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        tv_content.setText(getResources().getString(R.string.snapshot));
        preview_look_video.setOnClickListener(this);

        // 启用图片缩放功能
        preview_img.enable();

        iv_back.setOnClickListener(this);

        if(!EventBus.getDefault().isRegistered(this)){
            Log.e(GeTui.Fragment,"GalleryGridFragment1==>Regester");
            EventBus.getDefault().register(this);
        }
        MyApplication.getInstance().setPreviewActivity(true);
        simpleDateFormat0.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        imageUrl= getIntent().getStringExtra("imgUrl");  //1554945365_picture.jpg
        deviceId= getIntent().getStringExtra("deviceId");  //CH01183910018
        newImgUrl= getIntent().getStringExtra("newImgUrl") + "";  //2019-04-11 09:16:05

        gatewayId= getIntent().getStringExtra("gatewayId");
        currentTimeFolder = "orangecat-"+ newImgUrl.split(" ")[0].replace("-","");  //orangecat-20190411

        Date remote_date0 = new Date(Long.parseLong(imageUrl.split("_")[0])*1000);
        String remote_time = simpleDateFormat0.format(remote_date0);
        remoteTimeFoler = "orangecat-"+ remote_time;
        imgPath =  MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath() + File.separator + Constants.DOWNLOAD_IMAGE_FOLDER_NAME + File.separator + deviceId+File.separator+remoteTimeFoler+File.separator+imageUrl;
        File imgPathFile=new File(imgPath);
        Log.e(GeTui.VideoLog,"imagePath:"+imgPath+"文件是否存在:"+imgPathFile.exists()+"大小:"+imgPathFile.length());

        String filepath= (String) SPUtils.get(newImgUrl+GeTui.IMG_DOWNLOAD_SUC,"");
        if(TextUtils.isEmpty(filepath)){
            imgPathFile.delete();
        }

        if(imgPathFile.exists() && imgPathFile.length()>10){
            Glide.with(this).load(imgPathFile).into(preview_img);
            preview_look_video.setVisibility(View.VISIBLE);
        }else{
            // 本地图片不存在
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    downloadMenthod();
//                }
//            },1000);
            downloadMenthod();

        }

        preview_reload_download_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview_loading_download_rl.setVisibility(View.VISIBLE);
                preview_reload_download_tv.setVisibility(View.GONE);
                downloadMenthod();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void preViewPIR(String str) {
  //      Log.e("bbbbbbb","PreviewActivity................:"+str+"  isShowforeground:"+isShowforeground);
//        mqttHandler.removeCallbacksAndMessages(null);
//        if(!isShowforeground){
//            return;
//        }

        Log.e("bbbbbbb","有没有往下走了");

        if(MyApplication.getInstance().isMediaPlayerActivity()){
           return;
        }

        if (Constants.IMAGE.equals(str)){
//			LogUtils.d("davi 成功类型 "+str);
//			PirInfo pirInfo= pirInfos.get(downloadStart);
//			Log.e(GeTui.VideoLog,"所有图片的长度是:"+downloadStr.length+"GalleryGridFragment1................已经下载的第"+downloadStart+"张图片.....:"+pirInfo);
//			downloadStart++;
//			MyApplication.getInstance().getDaoWriteSession().delete(pirInfo);
//			if( downloadStart >= downloadStr.length){   // 4+1=5  5>=5
//
//				downloadComplete();
//			}
        }else if(FtpException.DOWNLOADEXCEPTION.equals(str)){
      //      Log.e(GeTui.VideoLog,"GalleryGridFragment1Sockek异常.........:"+isShowforeground);
            Toast.makeText(this,getResources().getString(R.string.ftp_connection_fail),Toast.LENGTH_SHORT).show();
            loadingfail();

        }else if(str.equals(GeTui.PIR_FTP_FAIL)){
            Toast.makeText(this,getResources().getString(R.string.pir_ftp_fail),Toast.LENGTH_SHORT).show();
            loadingfail();
        }else if(str.equals(GeTui.FTP_CONNECTION_FAIL)){
        //    Log.e(GeTui.VideoLog,"isShowforeground:"+isShowforeground);
            Toast.makeText(this,getResources().getString(R.string.ftp_connection_fail),Toast.LENGTH_SHORT).show();
            loadingfail();
        }else if(str.equals(GeTui.FTP_LOGIN_FAIL)){
            Toast.makeText(this,getResources().getString(R.string.ftp_login_fail),Toast.LENGTH_SHORT).show();
            loadingfail();
        }else if(GeTui.FTP_IMAGE_FAIL.equals(str)){
            Toast.makeText(this,getResources().getString(R.string.go_next_activity_fail),Toast.LENGTH_SHORT).show();
            loadingfail();
        }else if(GeTui.FTP_IMAGE_SUC.equals(str)){



        }else if(GeTui.PIR_FTP_SUC.equals(str)){

        }else if(GeTui.FTP_IMAGE_COPY.equals(str)){
            preview_loading_download_rl.setVisibility(View.GONE);
            File imgPathFile=new File(imgPath);
            Log.e(GeTui.VideoLog,"imagePath:"+imgPath+"文件是否存在:"+imgPathFile.exists()+"大小:"+imgPathFile.length());
            if(imgPathFile.exists() && imgPathFile.length()>10){
                Glide.with(this).load(imgPathFile).into(preview_img);
                preview_look_video.setVisibility(View.VISIBLE);
                SPUtils.put(newImgUrl+GeTui.IMG_DOWNLOAD_SUC,imgPath);
            }else{
                if(imgPathFile.length()<=10){
                    imgPathFile.delete();
                }
            }
        }else if(GeTui.PIR_FTP_SUC.equals(str)){

        }

    }

    private void downloadMenthod(){
      //  FtpUtils.getInstance().doorBellFTPService(deviceId,gatewayId);  //唤醒猫眼
        mPresenter.weakUpFTP(gatewayId,deviceId);


//        mqttHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(PreviewActivity.this,getResources().getString(R.string.ftp_weak_up_overtime),Toast.LENGTH_SHORT).show();
//                loadingfail();
//            }
//        },mqttOverTime);

    }
    private void loadingfail(){
        //	loading_download_pb.setText(getResources().getString(R.string.reload_download));
        //	loading_download_pb.setTextColor(Color.parseColor("#2031CA"));
        //history_loading_download_pb.setVisibility(View.GONE);
        preview_loading_download_rl.setVisibility(View.GONE);
        preview_reload_download_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.preview_look_video:
                Intent intent=new Intent(this, MediaPlayerActivity.class);
                intent.putExtra(Constants.PLAY_VIDEO_FLAG,Constants.CAT_EYE_VIDEO);
                intent.putExtra(Constants.CAT_EYE_URL,imgPath);
                intent.putExtra(Constants.DEVICE_ID,deviceId);
                intent.putExtra(Constants.GATEWAY_ID, gatewayId);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        MyApplication.getInstance().setPreviewActivity(false);
        super.onDestroy();
    }

    @Override
    protected SnapPresenter<ISnapShotView> createPresent() {
        return new SnapPresenter();
    }
    String ftpCmdPort="";//ftp端口
    String ftpCmdIp="";//ftpip
    @Override
    public void showFTPResultSuccess(FtpEnable ftpEnable) {
        ftpCmdPort= ftpEnable.getReturnData().getFtpCmdPort()+"";
        ftpCmdIp =  ftpEnable.getReturnData().getFtpCmdIp()+"";
        Log.e(GeTui.VideoLog,"ftpCmdPort:"+ftpCmdPort+" ftpCmdIp:"+ftpCmdIp);

        String[] downloadStr=new String[1];
        ///  sdap0/storage/orangecat-20190404/1554344487_picture.jpg
        String catEyePath= "sdap0/storage/"+remoteTimeFoler+File.separator+imageUrl;
        Log.e(GeTui.VideoLog,"PreviewActivity......."+catEyePath);
        downloadStr[0]=catEyePath;
        if(downloadStr!=null && downloadStr.length>0){
            //(final String ftpCmdIp, final String ftpCmdPort, final String[] urlStr, final String deviceId, final String folderName,final String singleUrl)
           FtpUtils.getInstance().downloadMultiFile(ftpCmdIp,ftpCmdPort,downloadStr,deviceId,Constants.DOWNLOAD_IMAGE_FOLDER_NAME,null);
        }else {

        }
        //sdap0/storage/orangecat-20190507/1557218820_picture.jpg
//        MyApplication.getInstance().setDownloadList(downloadStr);
    }

    @Override
    public void showFTPResultFail() {
        loadingfail();
        Toast.makeText(PreviewActivity.this,getResources().getString(R.string.ftp_weak_up_fail),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFTPOverTime() {
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               Toast.makeText(PreviewActivity.this,getResources().getString(R.string.download_overtime),Toast.LENGTH_SHORT).show();
               loadingfail();
           }
       });
    }
}
