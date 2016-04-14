package com.example.xusoku.bledemo.adpter.base;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CommonPagerAdapter extends PagerAdapter
{

    List<View> viewList;

    public CommonPagerAdapter(List<View> viewList)
    {
        this.viewList = viewList;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return viewList.size();
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position)
    {

        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object)
    {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        // TODO Auto-generated method stub
        return view == object;
    }

}
