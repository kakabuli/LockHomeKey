package com.kaadas.lock.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;


public class LinkClickableSpan extends ClickableSpan {
    @Override
    public void onClick(@NonNull View widget) {

    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(Color.parseColor("#0066A1"));
        ds.setUnderlineText(false);
    }
}
