package com.kaadas.lock.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.PirHistoryAdapter;
import com.kaadas.lock.adapter.RecordingAdapter;
import com.kaadas.lock.mvp.presenter.RecordingPresenter;
import com.kaadas.lock.mvp.presenter.SnapPresenter;
import com.kaadas.lock.mvp.view.IRecordingView;
import com.kaadas.lock.mvp.view.ISnapShotView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class RecordingFragment extends CallBackBaseFragment <IRecordingView, RecordingPresenter<IRecordingView>> implements IRecordingView {


    @BindView(R.id.recording_rv_ff)
    RecyclerView recording_rv_ff;
    RecordingFragment recordingFragment;

    public RecordingFragment() {
        // Required empty public constructor
    }

    List<String> imageList=new ArrayList<>();

    @Override
    protected RecordingPresenter<IRecordingView> createPresent() {
        return new RecordingPresenter();
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);
        return view;
    }
    RecordingAdapter recordingAdapter;
    @Override
    public void initOtherFunction() {
        for (int i=0;i<20;i++){
            imageList.add("2019-04-"+i+"12:34:23");
        }
        recordingAdapter = new RecordingAdapter(getActivity(),imageList);
        recording_rv_ff.setLayoutManager(new LinearLayoutManager(getActivity()));
        recording_rv_ff.setAdapter(recordingAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.year_select_ll:
                mPopupWindow.showPopupWindow(v);
                break;
            case  R.id.day_select_ll:
                date_mPopupWindow.showPopupWindow(v);
                break;
        }
    }
}
