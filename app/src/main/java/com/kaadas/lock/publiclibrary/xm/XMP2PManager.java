package com.kaadas.lock.publiclibrary.xm;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceView;

import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.LogUtils;
import com.p2p.pppp_api.st_PPCS_Session;
import com.xm.sdk.apis.XMStreamComCtrl;
import com.xm.sdk.bean.ParamConnectDev;
import com.xm.sdk.bean.ParamConnectP2P;
import com.xm.sdk.bean.ParamInitDev;
import com.xm.sdk.interfaces.av.AVStreamListener;
import com.xm.sdk.interfaces.av.StreamListener;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.XmMovieViewController;
import com.xmitech.sdk.frame.FrameAV;
import com.xmitech.sdk.interfaces.AVFilterListener;
import com.xmitech.sdk.interfaces.VideoPackagedListener;

import org.json.JSONException;
import org.json.JSONObject;


public class XMP2PManager extends StreamListener  {
    private int handleSession;
    private String currentDid;

    /**
     * 设备升级
     */
    public static final String FUNCTION_GATEWAY_UPDATE="gateway_update";
    /**
     * 重启设备
     */
    public static final String FUNCTION_REBOOT_DEVICE="reboot_device";
    /**
     * 登录回调
     */
    public static final String FUNCTION_START_CONNECT="START_CONNECT";

    /**
     *  镜头旋转角度
     */
    public static final int SCREEN_ROTATE = 90;

    /**
     * 初始化实例 对象
     */
   /* private static class SingletonHolder
    {
        private static XmMovieViewController codecInstance = null;
        private static XMP2PManager instance = null;
        private static XMStreamComCtrl xmStreamComCtrl = new XMStreamComCtrl();

        static
        {
            codecInstance = new XmMovieViewController();

            instance = new XMP2PManager();
        }
    }*/

    private static XmMovieViewController codecInstance = null;
    private static XMP2PManager instance = null;
    private static XMStreamComCtrl xmStreamComCtrl = null;

//    public static  String serviceString="EBGDEIBIKEJPGDJMEBHLFFEJHPNFHGNMGBFHBPCIAOJJLGLIDEABCKOOGILMJFLJAOMLLMDIOLMGBMCGIO";
    public static  String serviceString="EBGDEJBJKEJLGHJKEIHCFMEDHENOHINHHHFOBCCGAAJOLJKNDIAFDDPGGELGIGLNAJNDKJCNPJNDAL";

    /**
     * 获取 单例 对象操作
     *      拿到 P2P实例 和 编解码库 实例
     * @return
     */
    public static XMP2PManager getInstance(){
        if (null == instance) {
            synchronized (XMP2PManager.class) {
                if (null == instance) {
                    instance = new XMP2PManager();
                }
            }
        }
        return instance;
    }

    /**
     * 获取 p2p通讯实例
     * @return
     */
    private static XMStreamComCtrl getInstanceP2P(){
        if (null == xmStreamComCtrl) {
            synchronized (XMStreamComCtrl.class) {
                if (null == xmStreamComCtrl) {
                    xmStreamComCtrl = new XMStreamComCtrl();
                }
            }
        }
        return xmStreamComCtrl;
    }

    /**
     * 获取 音视频编解码 单例
     * @return
     */
    private static XmMovieViewController getCodecInstance() {
        if (null == codecInstance) {
            synchronized (XmMovieViewController.class) {
                if (null == codecInstance) {
                    codecInstance = new XmMovieViewController();
                }
            }
        }
        return codecInstance;
    }

    private ConnectStatusListener mConnectListener = null;

    public interface ConnectStatusListener{
        //连接失败，失败code，关闭资源
        void onConnectFailed(int paramInt);
        //连接成功
        void onConnectSuccess();
        //连接状态信息
        void onStartConnect(String paramString);
        //连接失败，错误信息
        void onErrorMessage(String message);
        //通知设备OTA更新
        void onNotifyGateWayNewVersion(String paramString);
        //重启设备
        void onRebootDevice(String paramString);
    }

    private AudioVideoStatusListener mAudioVideoStatusListener;

    public interface AudioVideoStatusListener{
        void onVideoDataAVStreamHeader(AVStreamHeader paramAVStreamHeader);
    }

    public void setOnAudioVideoStatusLinstener(AudioVideoStatusListener listener){
        this.mAudioVideoStatusListener = listener;
    }

    private XMP2PMqttCtrlListener mXMP2PMqttCtrlListener;

    public interface XMP2PMqttCtrlListener{
        void onMqttCtrl(JSONObject jsonObject);
    }

    public void setOnMqttCtrl(XMP2PMqttCtrlListener listener){
        this.mXMP2PMqttCtrlListener = listener;
    }

    public interface PlayDeviceRecordVideo{
        void onPlayDeviceRecordVideoProcResult(JSONObject jsonObject);
        void onPlayRecViewCtrlResult(JSONObject jsonObject);
        void onPushCmdRet(int cmdCode, JSONObject jsonString);
    }

    private PlayDeviceRecordVideo mPlayDeviceRecordVideoListener;

    public void setOnPlayDeviceRecordVideo(PlayDeviceRecordVideo listener){
        this.mPlayDeviceRecordVideoListener = listener;
    }

    public void setOnConnectStatusListener(ConnectStatusListener connectListener){
        this.mConnectListener = connectListener;
    }

    public boolean isConnected(int handleSession){
        return getInstanceP2P().isConnected(handleSession);
    }

    public int connectDevice(DeviceInfo paramDeviceInfo){
        LogUtils.e("connectDevice: did=" + paramDeviceInfo.getDeviceDid());
        LogUtils.e("connectDevice: serverString=" + paramDeviceInfo.getServiceString());
        LogUtils.e("connectDevice: sn=" + paramDeviceInfo.getDeviceSn());
        LogUtils.e("connectDevice: p2pPassword=" + paramDeviceInfo.getP2pPassword());
        if(currentDid!=null&&currentDid.equals(paramDeviceInfo.getDeviceDid())){
            if(getInstanceP2P().isConnected(handleSession)) {
                // 已经连接，可用的session
                if(mConnectListener != null)
                    mConnectListener.onConnectSuccess();
                return handleSession;
            }
        }else{
            /*if(handleSession>0){
                getInstanceP2P().stopConnectDevice();
            }*/
        }
        LogUtils.e( "shulan connectDevice: ====================");
        // 创建相关信息
        ParamConnectDev paramConnectDev = new ParamConnectDev();
        // 通道 默认是0
        paramConnectDev.setChannel(paramDeviceInfo.getCameraChannel());
        // 设备did
        paramConnectDev.setDid(paramDeviceInfo.getDeviceDid());
        // 登录p2p设备用户名
        paramConnectDev.setUserName(paramDeviceInfo.getDeviceSn());
        // 登录p2p设备用户名密码
        paramConnectDev.setPassword(paramDeviceInfo.getP2pPassword());
        // 设备SN
        paramConnectDev.setSn(paramDeviceInfo.getDeviceSn());
        // 当前连接的用户id,一般显示用户邮箱或者用户名
        paramConnectDev.setUserId("userId");

        // p2p服务器连接参数
        ParamConnectP2P paramConnectP2P = new ParamConnectP2P();
        // udp端口 0默认分配
        paramConnectP2P.setUdpPort(0);  // 默认
        // 连接模式
        paramConnectP2P.setConnectMode((byte) 126);//126  默认126
        // 按p2p服务器地址
        paramConnectP2P.setServiceString(paramDeviceInfo.getServiceString());
        // 监听各个p2p 函数调用 数据回调
        getInstanceP2P().setStreamListener(this);
        // 开始连接
        handleSession=getInstanceP2P().startConnectDevice(paramConnectDev, paramConnectP2P);
        LogUtils.e("handleSession==" + handleSession);
        // session>0时，则建立成功
        if(handleSession<0){
            currentDid=null;
            LogUtils.e("shulan--------------111");
            /*if(mConnectListener != null)
                mConnectListener.onConnectFailed(handleSession);*/
        }else {
            //获取P2P当前信息
            sessionStatus();
            currentDid = paramDeviceInfo.getDeviceDid();
        }

        return handleSession;
    }

    public void initAPI(String s){
        getInstanceP2P().initAPI(s);
    }

    public void init(Context context){
        getCodecInstance().init(context);
    }


    /*============================= 数据 回调===================================*/


    @Override
    public void onStartConnect(JSONObject paramJSONObject) {
        super.onStartConnect(paramJSONObject);
        LogUtils.e("onStartConnect--" + paramJSONObject.toString());
        try {
            // 反馈 通知层
            sendP2PResult(paramJSONObject,FUNCTION_START_CONNECT);
            if (paramJSONObject.getString("result").equals("ok")){
                //通知连接成功
                if(mConnectListener != null)
                    mConnectListener.onConnectSuccess();
                return;
            }
            return;
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectfailed(int paramInt) {
        LogUtils.e("shulan--------------222");
        if(mConnectListener != null)
            mConnectListener.onConnectFailed(paramInt);
    }

    /**
     * 设备采集的麦克风  音频数据 回调
     * @param paramArrayOfByte
     * @param paramInt
     * @param paramAVStreamHeader
     */
    @Override
    public void onAudioDataAVStreamHeader(byte[] paramArrayOfByte, int paramInt, AVStreamHeader paramAVStreamHeader)
    {
        super.onAudioDataAVStreamHeader(paramArrayOfByte,paramInt,paramAVStreamHeader);
        FrameAV frameAV = new FrameAV();
        if (paramAVStreamHeader.m_VideoFrameModel == 18)
            // AAC 音频格式
            frameAV.setFrameType(1);
        else if (paramAVStreamHeader.m_VideoFrameModel == 16)
            // G711 音频格式
            frameAV.setFrameType(0);
        // 填充音频数据
        frameAV.setFrameData(paramArrayOfByte);
        // 填写时间戳
        frameAV.setFrameTimeStamp(paramAVStreamHeader.m_TimeStamp);
        // 丢进解码器解码
        getCodecInstance().InputMediaAudio(frameAV);

    }

    /**
     * 通知设备OTA升级
     * @param paramJSONObject
     */
    @Override
    public void onNotifyGateWayNewVersionProcResult(JSONObject paramJSONObject)
    {
        super.onNotifyGateWayNewVersionProcResult(paramJSONObject);
        LogUtils.e("shulan onNotifyGateWayNewVersionProcResult--"+paramJSONObject.toString());
        sendP2PResult(paramJSONObject, FUNCTION_GATEWAY_UPDATE);
    }

    /**
     * 重启设备
     * @param paramJSONObject
     */
    @Override
    public void onRebootDeviceProcResult(JSONObject paramJSONObject) {
        super.onRebootDeviceProcResult(paramJSONObject);
        sendP2PResult(paramJSONObject, FUNCTION_REBOOT_DEVICE);
    }


    /**
     * 打开音频流 回调
     * @param paramJSONObject
     */
    @Override
    public void onStartAudioStreamProcResult(JSONObject paramJSONObject) {
        super.onStartAudioStreamProcResult(paramJSONObject);
        LogUtils.e("shulan onStartAudioStreamProcResult-->" + paramJSONObject.toString());

    }

    /**
     * 关闭音频流 回调
     * @param paramJSONObject
     */
    @Override
    public void onStopAudioStreamProcResult(JSONObject paramJSONObject) {
        super.onStopAudioStreamProcResult(paramJSONObject);
        LogUtils.e("shulan onStopAudioStreamProcResult-->" + paramJSONObject.toString());
    }

    /**
     * 开启视频流 回调
     * @param paramJSONObject
     */
    @Override
    public void onStartVideoStreamProcResult(JSONObject paramJSONObject) {
        super.onStartVideoStreamProcResult(paramJSONObject);
    }

    /**
     * 关闭 视频流 回调
     * @param RetDic
     */
    @Override
    public void onStopVideoStreamProcResult(JSONObject RetDic) {
        super.onStopVideoStreamProcResult(RetDic);
    }

    /**
     *  视频流的 数据返回
     * @param paramArrayOfByte
     * @param paramInt
     * @param paramAVStreamHeader
     */
    @Override
    public void onVideoDataAVStreamHeader(byte[] paramArrayOfByte, int paramInt, AVStreamHeader paramAVStreamHeader) {
        super.onVideoDataAVStreamHeader(paramArrayOfByte, paramInt, paramAVStreamHeader);

        H264Frame h264Frame = new H264Frame();
        // H264 数据流
        h264Frame.setH264(paramArrayOfByte);
        // 数据流大小
        h264Frame.setSize(paramArrayOfByte.length);
        // 帧率
        h264Frame.setFrameRate(paramAVStreamHeader.m_AVFrameRate);
        // 帧类型
        h264Frame.setFrameModel(paramAVStreamHeader.m_FrameType);
        // 数据类型
        h264Frame.setDataType(paramAVStreamHeader.m_VideoType);
        // 时间戳
        h264Frame.setFrameTimeStamp(paramAVStreamHeader.m_TimeStamp);
        // 封装 丢进解码库  解码+
//        if(paramAVStreamHeader.m_VideoType == 0){
            getCodecInstance().InputMediaH264(h264Frame);
//        }else if(paramAVStreamHeader.m_VideoType == 1){
//            getCodecInstance().InputMP4H264(h264Frame);
//        }

        if(mAudioVideoStatusListener != null){
            this.mAudioVideoStatusListener.onVideoDataAVStreamHeader(paramAVStreamHeader);
        }

    }


    /**
     * 设置语言提示音的结果回调
     * @param paramJSONObject
     */
    @Override
    public void onSetToneLanguageProcResult(JSONObject paramJSONObject) {
        super.onSetToneLanguageProcResult(paramJSONObject);
    }

    /**
     * 设置同步时间的结果回调
     * @param paramJSONObject
     */
    @Override
    public void onSyncDevicetimeProcResult(JSONObject paramJSONObject) {
        super.onSyncDevicetimeProcResult(paramJSONObject);
    }

    /**
     * 关闭连接
     */
    public void stopConnect()
    {
        LogUtils.e("shulan connectDevice -----> stopConnect");
        getInstanceP2P().stopConnectDevice();
        currentDid=null;
    }

    /**
     * 关闭编解码库
     */
    public void stopCodec()
    {
        if(getCodecInstance().isRecord()){
            getCodecInstance().stopRecordToMP4();
        }
        getCodecInstance().stop();
        releaseAudio();
        LogUtils.e("shulan --------stopCodec");
    }

    /**
     *  设置渲染 画面 surfaceView
     * @param surfaceView
     */
    public void setSurfaceView(SurfaceView surfaceView){
        getCodecInstance().setSurfaceView(surfaceView);
    }

    /**
     *  音视频监听回调
     * @param avFilterListener
     */
    public void setAVFilterListener(AVFilterListener avFilterListener){
        LogUtils.e("shulan setAVFilterListener");
        getCodecInstance().setFilterListener(avFilterListener);
    }

    /**
     * 释放音频
     */
    private void releaseAudio()
    {
        getCodecInstance().releaseAudio();
    }

    /**
     * 获取当前p2p session信息
     *      如： p2p，relay模式
     *      ip地址 端口等
     */
    private void sessionStatus() {
        st_PPCS_Session st_ppcs_session = getInstanceP2P().getSeesionInfo();
        if (st_ppcs_session == null) {
            LogUtils.e( "----->连接信息=NULL");
            return;
        }

        LogUtils.e( "p2p----->DID=" + st_ppcs_session.getDID());
        LogUtils.e( "p2p----->Mode=" + st_ppcs_session.getMode());
        LogUtils.e("p2p----->MyLocal IP=" + st_ppcs_session.getMyLocalIP());
        LogUtils.e("p2p----->MyWan IP=" + st_ppcs_session.getMyWanIP());
        LogUtils.e("p2p----->Remote IP=" + st_ppcs_session.getRemoteIP());
        LogUtils.e("p2p----->Remote Port =" + st_ppcs_session.getRemotePort());
        LogUtils.e("p2p------>Connect Time="+st_ppcs_session.getConnectTime());
        LogUtils.e("p2p------>Skt="+st_ppcs_session.getSkt());
        int m_Mode = st_ppcs_session.getMode();
        if (m_Mode == 0) {
            LogUtils.e("P2P---->");
        } else {
            LogUtils.e("Relay---->");
        }
    }

    /**
     *  是否开启视频播放
     */
    public void isPlay(){
        getCodecInstance().isPlay();
    }

    /**
     * 统一处理 数据回调 错误值分析
     * @param paramJSONObject
     */
    public void sendP2PResult(JSONObject paramJSONObject, String paramString)
    {
        try {
            if (paramJSONObject.getString("result").equals("ok")){
                // 成功
                if(mConnectListener != null){
                    if(paramString.equals(FUNCTION_START_CONNECT)){
                        mConnectListener.onStartConnect(paramJSONObject.toString());
                    }else if(paramString.equals(FUNCTION_GATEWAY_UPDATE)){
                        mConnectListener.onNotifyGateWayNewVersion(paramJSONObject.toString());
                    }else if(paramString.equals(FUNCTION_REBOOT_DEVICE)){
                        mConnectListener.onRebootDevice(paramJSONObject.toString());
                    }
                }

                LogUtils.e("----->code");
                return;
            }
            if (paramJSONObject.getInt("errno") == 116)
            {
                //当116 时， T21视频模块无启动完成。继续发生命令
                if(mConnectListener != null)
                    mConnectListener.onConnectSuccess();
                return;
            }else {
                //响应错误码 返回
                if(mConnectListener != null)
                    mConnectListener.onErrorMessage(paramJSONObject.toString());
            }
        }catch (JSONException localJSONException) {
            if(mConnectListener != null)
                mConnectListener.onErrorMessage(paramJSONObject.toString());
        }
    }

    public void snapImage(){
        getCodecInstance().snapImageRgb();
    }

    /**
     * @param filePath 保存文件的路径
     * @param width  录制的视频的分辨率宽度
     * @param height 录制的视频的分辨率高度
     * @param Rate   录制的视频的帧率
     */
    public void startRecordMP4(String filePath, int width, int height, int Rate){
        LogUtils.e("shulan startRecordMP4");
        getCodecInstance().startRecordToMP4(filePath, width, height, Rate);

    }

    /**
     * @param filePath 保存文件的路径
     * @param width  录制的视频的分辨率宽度
     * @param height 录制的视频的分辨率高度
     * @param Rate   录制的视频的帧率
     * @param rotate 旋转角度 0，90，180，270
     */
    public void startRecordMP4(String filePath, int width, int height, int Rate,int rotate){
        LogUtils.e("shulan startRecordMP4");
        getCodecInstance().startRecordToMP4(filePath, width, height, Rate,rotate);

    }

    public void stopRecordMP4(){
        getCodecInstance().stopRecordToMP4();
    }

    public int startAudioStream(){
        return getInstanceP2P().startAudioStream();
    }

    public int stopAudioStream(){
        return getInstanceP2P().stopAudioStream();
    }

    public void play(){
        getCodecInstance().play();
    }

    /**
     * 设置解码 视频翻转 ：
     * @param rotate  0，90，180，270
     */
    public void setRotate(int rotate){
        getCodecInstance().setRotate(rotate);
    }

    public int startVideoStream(){
        return  XMP2PManager.getInstanceP2P().startVideoStream();
    }

    public boolean isEnableAudio(){
        return getCodecInstance().isEnableAudio();
    }

    public void enableAudio(boolean flag){
        XMP2PManager.getCodecInstance().enableAudio(flag);
    }

    //打开/关闭手机麦克风
    public void talkback(boolean enable){
        //true=打开,false=关闭
        XMP2PManager.getCodecInstance().talkback(enable);
    }

    public boolean isTalkback(){
        return XMP2PManager.getCodecInstance().isTalkback();
    }

    public void setVideoPackagedListener(VideoPackagedListener listener){
        XMP2PManager.getCodecInstance().setVideoPackagedListener(listener);
    }

    //[0]:宽度,[1]:高度
    public int[] getVideoResolution(){
        return XMP2PManager.getCodecInstance().getVideoResolution();
    }

    public void setAudioFrame(){
        LogUtils.e("shulan setAudioFrame");
        AudioFrame audioFrame = new AudioFrame();
        audioFrame.setSampRate(8000);//设置采样率，目前只支持8K
        audioFrame.setFrameType(3);//1：表示 G711A，3:表示AAC 音频格式
        XMP2PManager.getCodecInstance().initAudioRecordAndTrack(1, 8000);// 初始化麦克风参数，0：G711A,1:AAC，8000：采样率
        XMP2PManager.getCodecInstance().setAudioFrame(audioFrame);//设置编解码器参数
    }



    public void openCameraSpeaker(){
        XMP2PManager.getInstanceP2P().openCameraSpeaker();
    }

    public void closeCameraSpeaker(){
        XMP2PManager.getInstanceP2P().closeCameraSpeaker();
    }

    @Override
    public void onOpenCameraMicProcResult(JSONObject jsonObject) {
        super.onOpenCameraMicProcResult(jsonObject);
        //打开摄像机扬声器的结果回调
    }

    @Override
    public void onOpenCameraSpeakerProcResult(JSONObject jsonObject) {
        super.onOpenCameraSpeakerProcResult(jsonObject);
        // 关闭摄像机扬声器的结果回调
    }

    @Override
    public void onStartTalkbackProcResult(JSONObject jsonObject) {
        super.onStartTalkbackProcResult(jsonObject);
        //打开对讲语音的结果回调
        LogUtils.e("shulan onStartTalkbackProcResult--" + jsonObject.toString());
    }

    public void startTalkback(){
        XMP2PManager.getInstanceP2P().startTalkback();
    }

    public void stopTalkback(){
        XMP2PManager.getInstanceP2P().stopTalkback();
    }

    @Override
    public void onStopTalkbackProcResult(JSONObject jsonObject) {
        super.onStopTalkbackProcResult(jsonObject);
        // 关闭对讲语音的结果回调
        LogUtils.e("shulan onStopTalkbackProcResult--" + jsonObject.toString());
    }

    public int sendTalkBackAudioData(AudioFrame audioFrame){
        return XMP2PManager.getInstanceP2P().sendTalkBackAudioData(audioFrame);
    }

    /*
    ctrl : 默认值为true
    true打开回声消除算法，false：关闭回声消除算法
    */
    public void setAECM(boolean ctrl){
        XMP2PManager.getCodecInstance().setAECM(ctrl);
    }

    /**
     * 发送回放指定录像文件
     *
     * @param fileDate 文件日期(20180830)<br>
     * @param fileName 文件名(120159)<br>
     * @param type     类型  0正常播放<br>
     *                                     1快速回放<br>
     * @param cameraChannel 默认填 0
     * @return (错误代码参考APIS_Error.java)
     */
    public int playDeviceRecordVideo(String fileDate, String fileName, int type, int cameraChannel){
        return getInstanceP2P().playDeviceRecordVideo(fileDate,fileName,type,cameraChannel);
    }

    /**
     * 发送搜索指定日期指定通道指定类型的录像文件或抓拍图片命令
     *
     * @param searchType 搜索类型<br>
     *                   0全部录像<br>
     *                   1全时录像<br>
     *                   2报警录像<br>
     *                   3抓拍录像<br>
     *                   4抓拍图片<br>
     * @param date       搜索日期(20180830)
     * @param channel    搜索的通道号
     * @return (错误代码参考APIS_Error.java)
     */
    public int searchRecordFileList(int searchType, String date, int channel){
        LogUtils.e("shulan searchRecordFileList-->" );
        return getInstanceP2P().searchRecordFileList(searchType,date,channel);
    }


    @Override
    public void onPlayDeviceRecordVideoProcResult(JSONObject jsonObject) {
        super.onPlayDeviceRecordVideoProcResult(jsonObject);
        //回放录像文件返回的回调,
        if(mPlayDeviceRecordVideoListener != null){
            this.mPlayDeviceRecordVideoListener.onPlayDeviceRecordVideoProcResult(jsonObject);
        }
    }

    @Override
    public void onPushCmdRet(int i, JSONObject jsonObject) {
        super.onPushCmdRet(i, jsonObject);
        LogUtils.e("shulan onPushCmdRet--i=" + i + "--jsonObject->" + jsonObject.toString());
        if(mPlayDeviceRecordVideoListener != null){
            this.mPlayDeviceRecordVideoListener.onPushCmdRet(i,jsonObject);
        }
    }

    @Override
    public void onSearchRecordFileListProcResult(JSONObject jsonObject) {
        super.onSearchRecordFileListProcResult(jsonObject);

        LogUtils.e("shulan --onSearchRecordFileListProcResult--jsonObject--" + jsonObject.toString());
    }

    /**
     * 发送控制录像回放数据传输回调返回命令
     *
     * @param token         控制口令(参数使用PlayDeviceRecordVideoProcResult回调返回结果中的token字段)
     * @param id            控制命令字<br>
     *                      0:继续回放(继续播放文件)<br>
     *                      1:暂停回放(暂停设备传输数据,下次继续调用token播放)<br>
     *                      2:停止回放(设备没有传完数据,用户关闭文件,释放回放资源)<br>
     * @param camearChannel 摄像机通道号
     * @return (错误代码参考APIS_Error.java)
     */
    public int playRecViewCtrl(int token, int id, int camearChannel){
        return getInstanceP2P().playRecViewCtrl(token,id,camearChannel);
    }

    @Override
    public void onPlayRecViewCtrlProcResult(JSONObject jsonObject) {
        super.onPlayRecViewCtrlProcResult(jsonObject);
        //发送控制录像回放数据传输回调
        LogUtils.e("shulan onPlayRecViewCtrlProcResult");
        if(mPlayDeviceRecordVideoListener != null){
            this.mPlayDeviceRecordVideoListener.onPlayRecViewCtrlResult(jsonObject);
        }
    }

    public int mqttCtrl(int ctrl){
        return getInstanceP2P().mqttCtrl(ctrl);
    }

    @Override
    public void onMqttCtrlProResult(JSONObject jsonObject) {
        super.onMqttCtrlProResult(jsonObject);
        //设备响应操作mqtt动作，动作结果会在onPushCmdRet返回
        LogUtils.e("shulan onMqttCtrlProResult-->"+jsonObject.toString());
        if(mXMP2PMqttCtrlListener != null){
            this.mXMP2PMqttCtrlListener.onMqttCtrl(jsonObject);
        }
    }

    public void notifyGateWayNewVersion(){
        getInstanceP2P().notifyGateWayNewVersion();

    }
}
