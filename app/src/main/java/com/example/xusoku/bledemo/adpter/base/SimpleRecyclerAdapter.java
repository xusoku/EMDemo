package com.example.xusoku.bledemo.adpter.base;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/12/22
 */
public abstract class SimpleRecyclerAdapter<Model>
        extends BaseSimpleRecyclerAdapter<Model, SimpleRecyclerAdapter.SimpleViewHolder>
{
    public SimpleRecyclerAdapter(@NonNull Activity activity)
    {
        super(activity);
    }

    public SimpleRecyclerAdapter(@NonNull Activity activity, BaseDataDelegate<Model> delegate)
    {
        super(activity, delegate);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new SimpleViewHolder(onCreateView(parent, viewType));
    }

    public abstract View onCreateView(ViewGroup parent, int viewType);

    public abstract void onBindModelToView(SimpleViewHolder viewHolder, int position);

    public class SimpleViewHolder extends BaseSimpleRecyclerAdapter.BaseViewHolder
    {

        public SimpleViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override
        public void bindModelToView(int position)
        {
            onBindModelToView(this, position);
        }
    }
}
