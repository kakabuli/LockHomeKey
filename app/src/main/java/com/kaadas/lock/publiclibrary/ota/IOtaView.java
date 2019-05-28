package com.kaadas.lock.publiclibrary.ota;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;

public interface IOtaView extends IBaseView {

    void onFailedServer(OTAResult result);

    void onGetOtaInfoSuccess(OTAResult.UpdateFileInfo updateFileInfo);

    void onFailed(Throwable throwable);
}
