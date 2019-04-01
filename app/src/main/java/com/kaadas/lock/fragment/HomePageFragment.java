package com.kaadas.lock.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaadas.lock.R;


import java.util.ArrayList;
import java.util.List;


import butterknife.ButterKnife;


/**
 * Created by asqw1 on 2018/3/14.
 */

public class HomePageFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View  view = View.inflate(getActivity(), R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        return view;
    }
}
