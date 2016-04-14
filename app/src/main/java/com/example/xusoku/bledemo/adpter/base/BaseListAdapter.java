package com.example.xusoku.bledemo.adpter.base;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/11/10
 */

/**
 * 基础的 ListAdapter 类
 * @param <Model>
 */
public abstract class BaseListAdapter<Model> extends BaseAdapter
{
    protected Activity mActivity = null;

    protected ArrayList<Model> mListData = null;

    protected BaseListAdapter(@NonNull Activity activity)
    {
        mActivity = activity;
        mListData = new ArrayList<>();
    }

    protected BaseListAdapter(@NonNull Activity activity, List<Model> models)
    {
        this(activity);
        setData(models);
    }

    /**
     * 设置数据
     */
    public void setData(Collection<? extends Model> models)
    {
        mListData.clear();
        if (models != null && !models.isEmpty()) {
            mListData.addAll(models);
        }

        notifyDataSetChanged();
    }

    /**
     * 加入数据
     */
    public void add(Model model)
    {
        if (model != null) {
            mListData.add(model);
        }
    }

    public void add(int pos, Model model)
    {
        if (model != null && pos >= 0 && pos < mListData.size()) {
            mListData.add(pos, model);
        }
    }

    public void addAll(Collection<? extends Model> models)
    {
        if (models != null && !models.isEmpty()) {
            mListData.addAll(models);
        }
    }

    public void addAll(int pos, Collection<? extends Model> models)
    {
        if (models != null && !models.isEmpty()
                && pos >= 0 && pos < mListData.size()) {
            mListData.addAll(pos, models);
        }
    }

    /**
     * 获取每个item的view id
     * @return
     */
    protected abstract int getItemViewId();

    protected abstract BaseViewHolder getViewHolder(int pos, View root);


    @Override
    public int getCount()
    {
        return mListData.size();
    }

    @Override
    public Model getItem(int position)
    {
        return mListData.isEmpty() ? null : mListData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BaseViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mActivity, getItemViewId(), null);
            holder = getViewHolder(position, convertView);

            convertView.setTag(holder);
        }
        else {
            //noinspection unchecked
            holder = (BaseViewHolder) convertView.getTag();
        }

        holder.mPosition = position;
        holder.bindView(mListData.get(position));

        return convertView;
    }


    protected abstract class BaseViewHolder
    {
        protected int mPosition = -1;

        protected View mRootView = null;

        protected BaseViewHolder(int pos, View root)
        {
            mPosition = pos;
            mRootView = root;
        }

        protected View findView(int id)
        {
            if (mRootView != null) {
                return mRootView.findViewById(id);
            }

            return null;
        }

        /**
         * 将数据和view 进行绑定
         */
        protected abstract void bindView(Model model);
    }
}
