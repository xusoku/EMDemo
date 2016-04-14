package com.example.xusoku.bledemo.views.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;


/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/11/26
 */
public class LoadMoreRecyclerView extends RecyclerView
{
    public final static String TAG = "LoadMoreRV";

    public final static int LOAD_MORE_FAILED = 0x11;

    public final static int LOAD_MORE_NO_MORE = 0x12;

    public final static int LOAD_MORE_SUCCESS = 0x13;

    private LoadMoreListener mLoadMoreListener = null;

    private LinearLayoutManager mLinearLayoutManager = null;

    private StaggeredGridLayoutManager mStaggerLayoutManager = null;

    private LoadMoreFooterViewController mFooterViewController = null;

    private boolean mIsLoadingMore = false;

    private final static int TYPE_STAGGER_LAYOUT = 0x14;
    private final static int TYPE_LINEAR_LAYOUT = 0x15;
    private final static int TYPE_GRID_LAYOUT = 0x16;

    private int mLayoutManagerType = 0;

    public LoadMoreRecyclerView(Context context)
    {
        this(context, null, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        getItemAnimator().setAddDuration(400);
        mFooterViewController = new LoadMoreFooterViewController(context);
    }

    public void setLoadMoreListener(LoadMoreListener listener)
    {
        mLoadMoreListener = listener;
    }

    public void setStaggerLayoutManager(StaggeredGridLayoutManager manager)
    {
        mStaggerLayoutManager = manager;
        mLayoutManagerType = TYPE_STAGGER_LAYOUT;
    }

    public void setLinearLayoutManager(LinearLayoutManager manager)
    {
        mLinearLayoutManager = manager;
        mLayoutManagerType = TYPE_LINEAR_LAYOUT;
    }

    /**
     * 刷新可见区域的几个items
     */
    public void refreshVisibleItems()
    {
        Adapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }

        int first = 0;
        int last = 0;
        switch (mLayoutManagerType) {
            case TYPE_LINEAR_LAYOUT:
                if (mLinearLayoutManager != null) {
                    first = mLinearLayoutManager.findFirstVisibleItemPosition();
                    last = mLinearLayoutManager.findLastVisibleItemPosition();
                }
                break;

            case TYPE_GRID_LAYOUT:
                break;

            case TYPE_STAGGER_LAYOUT:
                if (mStaggerLayoutManager != null) {
                    int [] firstInfo = new int[2];
                    mStaggerLayoutManager.findFirstVisibleItemPositions(firstInfo);

                    int [] lastInfo = new int [2];
                    mStaggerLayoutManager.findLastVisibleItemPositions(lastInfo);

                    first = Math.min(firstInfo[1], firstInfo[0])-1;
                    first = first < 0 ? 0 : first;

                    last = Math.max(lastInfo[0], lastInfo[1])+1;
                    last = last >= adapter.getItemCount() ? adapter.getItemCount() - 1 : last;
                }
                break;
        }

        if ((last - first) > 0) {
            adapter.notifyItemRangeChanged(first, last);
        }
    }

    public LoadMoreFooterViewController getLoadMoreFooterController()
    {
        return mFooterViewController;
    }

    public synchronized void loadMoreFinished(int state)
    {
        switch (state) {
            case LOAD_MORE_SUCCESS:
                mIsLoadingMore = false;
                mFooterViewController.loadMoreSuccess();
                break;

            case LOAD_MORE_FAILED:
                mIsLoadingMore = true;
                mFooterViewController.loadMoreFailed();

                break;
            case LOAD_MORE_NO_MORE:
                mIsLoadingMore = true;
                mFooterViewController.noMoreData();
                break;
        }
    }

    public void setFooterClickListener(View.OnClickListener listener)
    {
        mFooterViewController.getFooterView().setOnClickListener(listener);
    }

    public void setFooterText(String text)
    {
        mFooterViewController.setText(text);
    }

    public void showFooterMargin()
    {
        mFooterViewController.visibleMarginText(true);
    }

    @Override
    public void onScrollChanged(int l, int t, int ol, int ot)
    {
        super.onScrollChanged(l, t, ol, ot);
    }

    @Override
    public void onScrollStateChanged(int state)
    {
        super.onScrollStateChanged(state);

        switch (state) {
            case SCROLL_STATE_DRAGGING:
            case SCROLL_STATE_IDLE:
                if (mLoadMoreListener == null) {
                    break;
                }

                switch (mLayoutManagerType) {
                    case TYPE_LINEAR_LAYOUT:
                        if (mLinearLayoutManager != null) {
                            int size = mLinearLayoutManager.getItemCount();
                            int pos = mLinearLayoutManager.findLastVisibleItemPosition();
//                            //Loge(TAG, "Pos(" + pos  + ") Size; " + size);
                            if (!mIsLoadingMore && pos >= size - 2) {
                                startLoadMore();
                            }
                        }
                        break;

                    case TYPE_GRID_LAYOUT:
                        break;

                    case TYPE_STAGGER_LAYOUT:
                        if (mStaggerLayoutManager != null) {
                            int[] pos = new int[2];
                            mStaggerLayoutManager.findLastVisibleItemPositions(pos);

                            int size = mStaggerLayoutManager.getItemCount();
//                            Log.e(TAG, "Pos(" + pos[0] + ": " + pos[1] + ") Size; " + size);

                            if (!mIsLoadingMore &&
                                    (pos[0] >= size - 2 || pos[1] >= size - 2)) {
                                startLoadMore();
                            }
                        }
                        break;
                }
                break;
        }
    }

    public synchronized void startLoadMore()
    {

        mIsLoadingMore = true;
        mFooterViewController.loadMore();
        mLoadMoreListener.onNeedLoadMore();
    }

    public interface LoadMoreListener
    {
        void onNeedLoadMore();
    }


}
