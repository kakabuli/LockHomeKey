package com.kaadas.lock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class MyGridItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int[] attrs = new int[]{
            android.R.attr.listDivider
    };
    private int col;

    public MyGridItemDecoration(Context context, int col) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        mDivider = typedArray.getDrawable(0);
        mDivider.setAlpha(188);
        typedArray.recycle();
        this.col = col;
    }


    @Override
    //在getItemOffset()方法中设置了分割线的宽度，之后会在onDraw中进行绘制，这里面会为每一条边设置在画布上显示的位置
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);//绘制垂直分隔线
        drawHorizontal(c, parent);//绘制水平分隔线
        super.onDraw(c, parent, state);
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();//获取RecyclerView中子项的数目，然后遍历
        for (int i = 0; i < childCount; i++) {
            if ((i + 1) % col != 0) {//判断是否为最后一列，最后一列不用画垂直分隔线
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();//获取子项的布局参数

                int top = child.getTop() + params.topMargin;//设置分隔线顶部的位置
                int bottom = child.getBottom() + params.bottomMargin;//设置分隔线底部的位置，绘制的是垂直分隔线，顶部到底部的距离就是分隔线的高度
                int left = child.getRight() - params.rightMargin - 5;//设置分隔线左边的位置，这里我减了个5，为了让分割线不是贴着子项的左边，我在布局文件中没有设置margin。
                int right = left + 1;//设置分隔线右边的位置，左边到右边的距离就是分隔线的粗细值

                mDivider.setBounds(left, top, right, bottom);//设置绘画的边界
                mDivider.draw(c);
            }
        }
    }

    //水平分隔线
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        //水平分隔线的起始点就是子项的左边的起始点，右边起始点就是子项右边的结束点
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (childCount - i > col) {//判断是否为最后一行，最后一行不用画水平分隔线
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                //每个子项的线面会画一条分隔线，因此分隔线上边其实就是子项的底部位置再加上底部的margin，若是在Y轴方向上有偏移量，则也要加上
                int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                //底部的位置就是分隔线的上边加上分隔线的粗细值
                int bottom = top + 1;

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //RecyclerView的子项其实就是一个矩形区域，set方法就是设置了这个矩形区域每个边框线的粗细，参数依次是左、上、右、下
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
    }
}
