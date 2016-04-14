package com.example.xusoku.bledemo.views.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xusoku.bledemo.R;


/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2015/11/17
 */
public class LoadMoreFooterViewController
{

    private ProgressBar mLoadProgress = null;

    private TextView mLoadHintText = null;

    private View mRootView = null;

    public LoadMoreFooterViewController(@NonNull Context context)
    {
        mRootView = View.inflate(context, R.layout.layout_load_more_footer, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mRootView.setLayoutParams(params);
        initializeFooterView();
    }

    private View findViewById(int id)
    {
        return mRootView.findViewById(id);
    }

    public View getFooterView()
    {
        return mRootView;
    }

    private void initializeFooterView()
    {
        mLoadProgress = (ProgressBar) findViewById(R.id.loadProgress);
        mLoadHintText = (TextView) findViewById(R.id.loadHint);
    }

    public void loadMore()
    {
        mRootView.setVisibility(View.VISIBLE);
        mLoadProgress.setVisibility(View.VISIBLE);
        mLoadHintText.setText("正在加载更多...");
        mLoadHintText.setTextColor(Color.parseColor("#FFFFFF"));
        mRootView.setOnClickListener(null);
    }

    public void noMoreData()
    {
        mRootView.setVisibility(View.VISIBLE);
        mLoadProgress.setVisibility(View.GONE);
        mLoadHintText.setText("没有更多了");
        mLoadHintText.setTextColor(Color.parseColor("#989898"));
        mRootView.setOnClickListener(null);
    }

    public void setText(String text)
    {
        mLoadHintText.setText(text);
    }

    public void visibleMarginText(boolean visible)
    {
        findViewById(R.id.marginText).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void loadMoreSuccess()
    {
//        mRootView.setVisibility(View.GONE);
        mRootView.setOnClickListener(null);
    }

    public void loadMoreFailed()
    {
        mRootView.setVisibility(View.VISIBLE);
        mLoadProgress.setVisibility(View.GONE);
        mLoadHintText.setText("加载更多失败，点击重新加载");
    }

}
