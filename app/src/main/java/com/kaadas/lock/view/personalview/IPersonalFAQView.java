package com.kaadas.lock.view.personalview;

import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.GetFAQResult;


/**
 * 常见问题
 */
public interface IPersonalFAQView extends IBaseView {

    void getFAQSuccessListView(GetFAQResult baseResult, String s);

    void getFAQError(Throwable throwable);

    void getFAQFail(GetFAQResult baseResult);
}
