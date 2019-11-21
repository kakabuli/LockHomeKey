package com.kaadas.lock.mvp.mvpbase;

import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;

public interface ICheckOtaView extends IBleView {

    void noNeedUpdate(); //  不需要更新

    void snError();  //Sn错误

    void dataError(); //数据参数错误

    /**
     *
     * @param appInfo
     * @param SN
     * @param version
     * @param type 1 只有蓝牙固件需要升级   2算法版升级  3摄像头升级
     */
    void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, String version, int type );

    /**
     * 两个文件需要升级
     * @param stackInfo
     * @param appInfo
     * @param SN
     * @param version
     */
    void needTwoUpdate(CheckOTAResult.UpdateFileInfo stackInfo, CheckOTAResult.UpdateFileInfo appInfo, String SN, String version );

    /**
     * 读取信息失败
     * @param throwable
     */
    void readInfoFailed(Throwable throwable);

    void unknowError(String errorCode); //   未知错误

}
