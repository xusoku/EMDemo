package com.example.xusoku.bledemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by wbz360 on 2015/12/2.
 */
public class MyScrollView extends ScrollView
{

    private float xLast;
    private float yLast;
    private float xDistance;
    private float yDistance;
    public MyScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
//        LogUtils.i("123", "l-" + l + "t-" + t + "oldl-" + oldl + "oldt-" + oldt);
        if (onScrollChangedListener!=null){
            onScrollChangedListener.onScrollChanged(t);
        }

    }
    private OnScrollChangedListener onScrollChangedListener;
    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener){
        this.onScrollChangedListener=onScrollChangedListener;
    }
    public interface  OnScrollChangedListener{
       public void onScrollChanged(int scrollY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if(xDistance > yDistance){
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
