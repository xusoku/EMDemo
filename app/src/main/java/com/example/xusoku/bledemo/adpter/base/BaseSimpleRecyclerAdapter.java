package com.example.xusoku.bledemo.adpter.base;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/12/7
 */

/**
 * 使用了数据委托的Adapter, 这样方便数据重用，
 * 加入了基本的Adapter功能，减少重复代码
 *
 * @param <Model>
 * @param <VH>
 */
public abstract class BaseSimpleRecyclerAdapter<Model,
        VH extends BaseSimpleRecyclerAdapter.BaseViewHolder> extends RecyclerView.Adapter<VH>
{
    protected Activity mActivity = null;

    protected LayoutInflater mLayoutInflater = null;

    protected BaseDataDelegate<Model> mDataDelegate = null;

    protected OnItemClickListener mItemClickListener = null;

    protected OnItemLongClickListener mItemLongClickListener = null;

    /**
     * Append 数据
     */
    private final Deque<Model> mAppendBuffer = new ArrayDeque<>();
    protected AppendRunnable mAppendRunnable = null;

    protected Handler mHandler = new Handler();

    public BaseSimpleRecyclerAdapter(@NonNull Activity activity)
    {
        this(activity, null);
    }

    public BaseSimpleRecyclerAdapter(@NonNull Activity activity, BaseDataDelegate<Model> delegate)
    {
        mActivity = activity;
        mLayoutInflater = LayoutInflater.from(activity);

        mDataDelegate = delegate;
        if (delegate == null) {
            mDataDelegate = new BaseSimpleDataDelegate<>();
        }
    }

    /**
     * 判断是否为有效位置
     */
    public boolean isValidPosition(int pos)
    {
        return pos >= 0 && pos < mDataDelegate.getSize();
    }

    /**
     * 重新设置数据
     * 移除所有的数据，再重新设置数据
     */
    public void setAll(Collection<? extends Model> models)
    {
        if (models == null) {
            models = new ArrayList<>();
        }

        mDataDelegate.clear();
        mDataDelegate.setAll(models);
        notifyDataSetChanged();
    }

    /**
     * 插入一个数据
     */
    public void insert(int position, Model model)
    {
        if (position < 0) {
            position = 0;
        }

        if (position > mDataDelegate.getSize()) {
            position = mDataDelegate.getSize()-1;
        }

        mDataDelegate.insert(position, model);
        notifyItemInserted(position);
    }

    /**
     * 加入一个数据到最后
     *
     * @param model
     */
    public void append(Model model)
    {
        mDataDelegate.append(model);
        notifyItemInserted(mDataDelegate.getSize()-1);
    }

    /**
     * 将数据全部加到最后
     */
    public synchronized void appendAll(Collection<? extends Model> models, boolean smooth)
    {
        appendAll(models, smooth, 50);
    }

    public synchronized void appendAll(Collection<? extends Model> models, boolean smooth, final int interTime)
    {
        if (models == null || models.isEmpty()) {
            return;
        }

        if (smooth) {
            if (mAppendBuffer.isEmpty()) {
                mAppendBuffer.addAll(models);
                mAppendRunnable = new AppendRunnable(interTime);
                mHandler.post(mAppendRunnable);
            }
            else {
                mAppendBuffer.addAll(models);
            }
        }
        else {
            for (Model m : models) {
                append(m);
            }
        }
    }

    /**
     * 移除一个数据
     * @param position
     */
    public void remove(int position)
    {
        mDataDelegate.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 清除所有的数据
     */
    public void clear()
    {
        mDataDelegate.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取一个数据
     */
    public Model getItem(int position)
    {
        return mDataDelegate.get(position);
    }


    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        mItemLongClickListener = listener;
    }


    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final VH holder, final int position)
    {
        if (holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(holder.itemView, position);
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    if (mItemLongClickListener != null) {
                        mItemLongClickListener.onItemLongClick(holder.itemView, position);
                    }
                    return false;
                }
            });
        }

        holder.bindModelToView(position);
    }

    @Override
    public int getItemCount()
    {
        return mDataDelegate.getSize();
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder
    {

        public BaseViewHolder(View itemView)
        {
            super(itemView);
        }

        public View findView(int id)
        {
            if (itemView == null) {
                return null;
            }
            return itemView.findViewById(id);
        }

        public abstract void bindModelToView(final int position);
    }

    protected class AppendRunnable implements Runnable
    {
        public int mInterTime = 0;

        public AppendRunnable(int time)
        {
            mInterTime = time;
        }

        @Override
        public void run()
        {
            if (!mAppendBuffer.isEmpty()) {
                append(mAppendBuffer.poll());
                mHandler.postDelayed(this, mInterTime);
            }
        }
    }

    /**
     * 数据委托
     */
    public static abstract class BaseDataDelegate<Model>
    {
        public abstract void insert(int position, Model model);

        public abstract void append(Model model);

        public abstract void set(int position, Model model);

        public abstract void setAll(Collection<? extends Model> list);

        public abstract void remove(int position);

        public abstract int getSize();

        public abstract void clear();

        public abstract Model get(int pos);
    }

    /**
     * 简单使用 ArrayList 的数据委托，一般已经满足需求
     * @param <Model>
     */
    public static class BaseSimpleDataDelegate<Model> extends BaseDataDelegate<Model>
    {
        public final ArrayList<Model> mListData = new ArrayList<>();

        public boolean isValidPosition(int pos)
        {
            return pos >= 0 && pos < mListData.size();
        }

        @Override
        public void insert(int position, Model model)
        {
            if (position < 0) {
                position = 0;
            }

            if (position > mListData.size()) {
                position = mListData.size();
            }

            mListData.add(position, model);
        }

        @Override
        public void append(Model model)
        {
            mListData.add(model);
        }

        @Override
        public void set(int position, Model model)
        {
            if (isValidPosition(position)) {
                mListData.set(position, model);
            }
        }

        @Override
        public void setAll(Collection<? extends Model> list)
        {
            mListData.clear();
            if (list == null || list.isEmpty()) {
                return;
            }

            mListData.addAll(list);
        }

        @Override
        public void remove(int position)
        {
            if (isValidPosition(position)) {
                mListData.remove(position);
            }
        }

        @Override
        public int getSize()
        {
            return mListData.size();
        }

        @Override
        public void clear()
        {
            mListData.clear();
        }

        @Override
        public Model get(int pos)
        {
            return isValidPosition(pos) ? mListData.get(pos) : null;
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(View view, int position);
    }
}
