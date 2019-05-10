package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.utils.db.MediaItem;

import java.util.ArrayList;

/**
 * Create By denganzhi  on 2019/5/5
 * Describe
 */

public interface IRecordingView extends IBaseView {


    void  showFetchResult(ArrayList<MediaItem> mediaItemArrayList);


    void deleteResult(Boolean isFlag);


}
