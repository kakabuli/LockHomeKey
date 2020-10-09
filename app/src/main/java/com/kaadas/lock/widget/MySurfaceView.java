package com.kaadas.lock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder holder;
    private Bitmap bitmap;

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = this.getHolder();
    }


    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void drawCanvas(Bitmap bitmap){
        Canvas canvas = holder.lockCanvas();
        if(canvas != null){
            canvas.drawBitmap(bitmap, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        this.drawCanvas(bitmap);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.drawCanvas(bitmap);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
