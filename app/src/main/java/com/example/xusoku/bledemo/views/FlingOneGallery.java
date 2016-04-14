package com.example.xusoku.bledemo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

/**
 * Created by xusoku on 2015/11/25.
 */
public class FlingOneGallery extends Gallery {

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean ret = super.dispatchTouchEvent(ev);
//        if(ret)
//        {
//            requestDisallowInterceptTouchEvent(true);
//        }
//        return ret;
//    }


    private int mCenterX;
    private float mZoomRatio = 0.2F;
    public FlingOneGallery(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        setStaticTransformationsEnabled(true);
        setCallbackDuringFling(false);
    }

    private static int getViewCenterX(View paramView)
    {
        return paramView.getLeft() + paramView.getWidth() / 2;
    }

    private int getCenterOfCoverflow()
    {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    public float getViewZoomRatio(View paramView)
    {
        float width = paramView.getWidth();
        int x = this.mCenterX - getViewCenterX(paramView);

        if (Math.abs(x) > width) {
            return 1.0F;
        }

        return Math.abs(1 + (width - Math.abs(x)) / width * mZoomRatio);
    }

    private void scaleParamView(View paramView, Transformation paramTransformation)
    {
        Matrix matrix = paramTransformation.getMatrix();
        float ratio = getViewZoomRatio(paramView);
        matrix.setScale(ratio, ratio);
        int height = paramView.getHeight();
        int width = paramView.getWidth();
        matrix.preTranslate(-(width / 2), -(height / 2));
        matrix.postTranslate(width / 2, height / 2);
    }

    @Override
    protected boolean getChildStaticTransformation(View paramView, Transformation paramTransformation)
    {
        scaleParamView(paramView, paramTransformation);
        if (paramView == getSelectedView()) {
            paramTransformation.setAlpha(1f);
        }
        else {
            paramTransformation.setAlpha(0.6f);
        }
        paramView.invalidate();

        return true;
    }

    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
        super.onFling(paramMotionEvent1, paramMotionEvent2, paramFloat1, paramFloat2);
        return false;
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
        this.mCenterX = getCenterOfCoverflow();
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void setZoomRatio(float paramFloat)
    {
        this.mZoomRatio = paramFloat;
    }






    public ViewPager viewPager;
    public void setViewPager(ViewPager viewPager)
    {
        this.viewPager=viewPager;
    }
    private  int mLastX=0;
    private  int mLastY=0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        int x= (int) ev.getX();
        int y= (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (viewPager!=null) {
                    viewPager.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX=x-mLastX;
                int deltaY=y-mLastY;
//                if (Math.abs(deltaY)>Math.abs(deltaX)) {
//                    if (viewPager!=null) {
//                        viewPager.requestDisallowInterceptTouchEvent(false);
//                    }
//                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }
}
