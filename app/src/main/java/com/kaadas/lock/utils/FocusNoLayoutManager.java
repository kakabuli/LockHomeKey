package com.kaadas.lock.utils;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class FocusNoLayoutManager extends LinearLayoutManager {

    private static final String TAG = FocusNoLayoutManager.class.getName();

    public FocusNoLayoutManager(Context context) {
        super(context);
    }

    public FocusNoLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FocusNoLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate) {
        return this.requestChildRectangleOnScreen(parent,child,rect,immediate,false);
    }

    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        return false;
    }
}
