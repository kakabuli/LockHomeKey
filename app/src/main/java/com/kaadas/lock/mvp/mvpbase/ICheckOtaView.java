package com.kaadas.lock.mvp.mvpbase;

import com.kaadas.lock.publiclibrary.http.result.OTAResult;

public interface ICheckOtaView extends IBleView {

    void noNeedUpdate(); //  不需要更新

    void snError();  //Sn错误

    void dataError(); //数据参数错误

    void needOneUpdate(OTAResult.UpdateFileInfo appInfo,String SN,String version );

    void needTwoUpdate(OTAResult.UpdateFileInfo stackInfo,OTAResult.UpdateFileInfo appInfo,String SN,String version );

    void readInfoFailed(Throwable throwable);

    void unknowError(String errorCode); //   未知错误


}
