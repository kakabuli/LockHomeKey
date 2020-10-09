package com.kaadas.lock.publiclibrary.xm;

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
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.XmMovieViewController;
import com.xmitech.sdk.frame.FrameAV;
import com.xmitech.sdk.interfaces.AVFilterListener;

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
     * 初始化实例 对象
     */
    private static class SingletonHolder
    {
        private static XmMovieViewController codecInstance;
        private static XMP2PManager instance;
        private static XMStreamComCtrl xmStreamComCtrl = new XMStreamComCtrl();

        static
        {
            codecInstance = new XmMovieViewController();
            instance = new XMP2PManager();
        }
    }

    /**
     * 获取 单例 对象操作
     *      拿到 P2P实例 和 编解码库 实例
     * @return
     */
    public static XMP2PManager getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 获取 p2p通讯实例
     * @return
     */
    public static XMStreamComCtrl getInstanceP2P(){
        return SingletonHolder.xmStreamComCtrl;
    }

    /**
     * 获取 音视频编解码 单例
     * @return
     */
    public static XmMovieViewController getCodecInstance() {
        return SingletonHolder.codecInstance;
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
        void onVideoDataAVStreamHeader();
    }

    public void setOnAudioVideoStatusLinstener(AudioVideoStatusListener listener){
        this.mAudioVideoStatusListener = listener;
    }

    public void setOnConnectStatusListener(ConnectStatusListener connectListener){
        this.mConnectListener = connectListener;
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
            if(handleSession>0){
                getInstanceP2P().stopConnectDevice();
            }
        }
        LogUtils.e( "connectDevice: ====================");
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
            if(mConnectListener != null)
                mConnectListener.onConnectFailed(handleSession);
        }else {
            //获取P2P当前信息
            sessionStatus();
        }

        return handleSession;
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
        LogUtils.e(paramJSONObject.toString());
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
    }

    /**
     * 关闭音频流 回调
     * @param paramJSONObject
     */
    @Override
    public void onStopAudioStreamProcResult(JSONObject paramJSONObject) {
        super.onStopAudioStreamProcResult(paramJSONObject);
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
        LogUtils.e("paramArrayOfByte --->"+paramArrayOfByte.length);
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
        // 封装 丢进解码库  解码
        getCodecInstance().InputMediaH264(h264Frame);
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
        LogUtils.e("connectDevice -----> stopConnect");

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

        getCodecInstance().startRecordToMP4(filePath, width, height, Rate);
    }

    public void stopRecordMP4(){
        getCodecInstance().stopRecordToMP4();
    }

    public int startAudioStream(){
        return getInstanceP2P().startAudioStream();
    }

    public void play(){
        getCodecInstance().play();
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
}
