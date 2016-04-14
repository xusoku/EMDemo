package com.example.xusoku.bledemo.views.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Jack Tony
 * @date 2015/6/2
 */
public class ExRcvAdapterWrapper<T extends RecyclerView.Adapter> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * view的基本类型，这里只有头/底部/普通，在子类中可以扩展
     */
    public static final int TYPE_HEADER = 99930;

    public static final int TYPE_FOOTER = 99931;

    private static final String TAG = "ddd";

    private final RecyclerView.LayoutManager mLayoutManager;
    
    private T mWrappedAdapter;

    protected LinearLayout mHeaderWrapper = null;
    protected View mHeaderView = null;

    protected LinearLayout mFooterWrapper = null;
    protected View mFooterView = null;

    public ExRcvAdapterWrapper(@NonNull T adapter, RecyclerView.LayoutManager layoutManager) {
        mWrappedAdapter = adapter;
        mWrappedAdapter.registerAdapterDataObserver(getObserver());

        mLayoutManager = layoutManager;
        if (mLayoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = ((GridLayoutManager) mLayoutManager);
            final GridLayoutManager.SpanSizeLookup lookup = gridLayoutManager.getSpanSizeLookup();
            final int spanCount = gridLayoutManager.getSpanCount();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    final int type = getItemViewType(position);
                    if (type == TYPE_HEADER || type == TYPE_FOOTER) {
                        return spanCount;
                    } else {
                        return lookup.getSpanSize(position - getHeaderCount());
                    }
                }
            });
        }
    }

    /**
     * 返回adapter中总共的item数目，包括头部和底部
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        int headerOrFooter = 0;
        if (mHeaderView != null) {
            headerOrFooter++;
        }
        if (mFooterView != null) {
            headerOrFooter++;
        }
        return headerOrFooter + mWrappedAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderWrapper != null && position == 0) {
            return TYPE_HEADER;
        } else if (mFooterWrapper != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return mWrappedAdapter.getItemViewType(position - getHeaderCount());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderWrapper != null) {
            return new SimpleViewHolder(mHeaderWrapper);
        } else if (viewType == TYPE_FOOTER && mFooterWrapper != null) {
            return new SimpleViewHolder(mFooterWrapper);
        } else {
            return mWrappedAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    /**
     * 载入ViewHolder，这里仅仅处理header和footer视图的逻辑
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final int type = getItemViewType(position);
        if (type != TYPE_HEADER && type != TYPE_FOOTER) {
            mWrappedAdapter.onBindViewHolder(viewHolder, position - getHeaderCount());
        }
    }

    public void setHeaderView(View headerView) {
        if (mHeaderWrapper == null && headerView != null) {
            mHeaderWrapper = new LinearLayout(headerView.getContext());
            mHeaderWrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (mHeaderWrapper == null) {
            return;
        }

        mHeaderWrapper.removeAllViews();

        mHeaderView = headerView;
        if (mHeaderView != null) {
            ViewGroup parent = (ViewGroup) mHeaderView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }

            mHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mHeaderWrapper.addView(mHeaderView);
            setFulSpan(mHeaderWrapper);
        }
    }

    public void setFooterView(View footerView) {
        if (mFooterWrapper == null && footerView != null) {
            mFooterWrapper = new LinearLayout(footerView.getContext());
            mFooterWrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (mFooterWrapper == null) {
            return;
        }

        mFooterWrapper.removeAllViews();

        mFooterView = footerView;
        if (mFooterView != null) {
            ViewGroup parent = (ViewGroup) mFooterView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }

            mFooterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mFooterWrapper.addView(mFooterView);
            setFulSpan(mFooterWrapper);
        }
    }

    private void setFulSpan(View view) {
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
        }
        notifyDataSetChanged();
    }

    /**
     * @return recycle的头部视图
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    public View getHeaderWrapperView()
    {
        return mHeaderWrapper;
    }

    public View getFooterWrapperView()
    {
        return mFooterWrapper;
    }

    /**
     * 得到底部的视图
     */
    public View getFooterView() {
        return mFooterView;
    }

    public void removeHeaderView() {
        mHeaderView = null;
        // notifyItemRemoved(0);如果这里需要做头部的删除动画，
        // 可以复写这个方法，然后进行改写
        notifyDataSetChanged();
    }

    public void removeFooterView() {
        mFooterView = null;
        notifyItemRemoved(getItemCount());
        // 这里因为删除尾部不会影响到前面的pos的改变，所以不用刷新了。
    }

    public T getWrappedAdapter() {
        return mWrappedAdapter;
    }

    public int getHeaderCount() {
        return mHeaderView != null ? 1 : 0;
    }

    public int getFooterCount() {
        return mFooterView != null ? 1 : 0;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    @NonNull
    private RecyclerView.AdapterDataObserver getObserver() {
        return new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int count = getHeaderCount();
                notifyItemRangeInserted(positionStart + count, itemCount);
                notifyItemRangeChanged(positionStart + count + itemCount - 1, getItemCount() - itemCount - positionStart - getFooterCount());
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                int count = getHeaderCount();
                notifyItemRangeRemoved(count + positionStart, itemCount);
                notifyItemRangeChanged(count + positionStart, getItemCount() - count - positionStart - getFooterCount());
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                // TODO: 2015/11/23 还没支持转移的操作 
            }
        };
    }

    private static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }

    }




}
