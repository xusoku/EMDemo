package com.example.xusoku.bledemo.views.viewpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


import com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar.ScrollBar;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


/**
 * Created by wbz360 on 2016/01/11.
 */
public class FixPageIndicator extends LinearLayout implements
		PageIndicator {
	
	private ViewPager mViewPager;
	private OnPageChangeListener mListener;
	private int mSelectedTabIndex = -1;
	final float density;
	private IndicatorAdapter indicatorAdapter;
	private int count;
	private int mPosition=0;

	public FixPageIndicator(Context context) {
		this(context, null);
	}

	public FixPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		ColorDrawable colorDrawable=new ColorDrawable(Color.WHITE);
		setDividerDrawable(colorDrawable);
		density = context.getResources().getDisplayMetrics().density;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
		this.mPosition = position;
		this.mPositionOffset = positionOffset;
//		Log.i("123", "position-"+position);
//		Log.i("123", "mPositionOffset-"+mPositionOffset);
		changeTabView();
		invalidate();
	}

	/*
	 * 此方法调用时机是，手指离开屏幕时，如果滑到下一个界面会立马调用，之后onPageScrolled继续执行
	 */	
	@Override
	public void onPageSelected(int arg0) {
//		Log.i("123", "mPosition:"+arg0);
		setCurrentTab(arg0);
		if (mListener != null) {
			mListener.onPageSelected(arg0);
		}
	}

	private void changeTabView() {
		if (indicatorAdapter != null) {
			View curView = getChildAt(mSelectedTabIndex);
			View nextView;
			if (mSelectedTabIndex != mPosition) {
				nextView = getChildAt(mPosition);
				indicatorAdapter.onPageScrolled(nextView, mPosition, 1 - mPositionOffset);
				indicatorAdapter.onPageScrolled(curView, mSelectedTabIndex,
						mPositionOffset);
			} else {
				indicatorAdapter.onPageScrolled(curView, mSelectedTabIndex,
						1 - mPositionOffset);
				if (mPosition == count - 1) {
					return;
				}
				nextView = getChildAt(mPosition + 1);
				indicatorAdapter.onPageScrolled(nextView, mPosition + 1, mPositionOffset);

			}

		}
		
	}


	public void setCurrentTab(int item) {

		mSelectedTabIndex = item;
		final int tabCount = getChildCount();
		for (int i = 0; i < tabCount; i++) {
			View child = getChildAt(i);
			final boolean isSelected = (i == mSelectedTabIndex);
			child.setSelected(isSelected);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
		setViewPager(view, 0);
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {

		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		final PagerAdapter adapter = view.getAdapter();
		if (adapter == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		view.setOnPageChangeListener(this);
		mSelectedTabIndex = initialPosition;
	
	}
	
	public void setIndicatorAdapter(IndicatorAdapter adapter) {
		indicatorAdapter = adapter;
		notifyDataSetChanged();
	}
	
	public void notifyDataSetChanged() {
		removeAllViews();
		final PagerAdapter adapter = mViewPager.getAdapter();
		count = adapter.getCount();
		if (mSelectedTabIndex > count) {
			mSelectedTabIndex = count - 1;
		} else if (mSelectedTabIndex < 0) {
			mSelectedTabIndex = 0;
		}
		mPosition=mSelectedTabIndex;
		mPositionOffset=0;
		for (int i = 0; i < count; i++) {
			if (indicatorAdapter != null) {
				addTabView(i);
			}
		}
		mViewPager.setCurrentItem(mSelectedTabIndex,false);
		if (mSelectedTabIndex==0){
			setCurrentTab(0);
		}
		if (indicatorAdapter != null) {
			
			indicatorAdapter.onPageScrolled(getChildAt(mSelectedTabIndex),
					mSelectedTabIndex, 1);

		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	private void addTabView(int index) {
		final View tabView = indicatorAdapter.getIndicatorView(index);
		tabView.setTag(index);
		tabView.setFocusable(true);
		tabView.setOnClickListener(mTabClickListener);
//		tabView.setBackgroundColor(Color.TRANSPARENT);
		addView(tabView, new LayoutParams(0, MATCH_PARENT, 1));
	}

	private final OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			final int oldSelected = mViewPager.getCurrentItem();
			final int newSelected = (Integer) view.getTag();
			mPosition=newSelected;
			mPositionOffset=0;
			if (newSelected != oldSelected) {
				if (indicatorAdapter != null) {
					indicatorAdapter.onPageScrolled(getChildAt(newSelected),
							newSelected, 1);
					indicatorAdapter.onPageScrolled(getChildAt(oldSelected),
							oldSelected, 0);

				}
				invalidate();
				mViewPager.setCurrentItem(newSelected, false);

			}

		}
	};

	/* ������scrollBar������÷��� */

	private ScrollBar scrollBar;

	private float mPositionOffset=0;

	public void setScrollBar(ScrollBar scrollBar) {
		this.scrollBar = scrollBar;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		Log.i("123", "canvas:");
		drawScrollBar(canvas);
	}
	
	private void drawScrollBar(Canvas canvas) {
		if (mViewPager == null || scrollBar == null || count == 0) {
//			Log.i("123", "return:");
			return;
		}
		View currentView = getChildAt(mPosition);
		float offsetX = 0;
		int offsetY = 0;
		int tabWidth = currentView.getWidth();
		int tabHeight = currentView.getHeight();
		int barWidth = scrollBar.getWidth(tabWidth);
		int barHeight = scrollBar.getHeight(tabHeight);
		offsetX=currentView.getLeft()+(tabWidth - barWidth) / 2+ tabWidth * mPositionOffset;
		switch (scrollBar.getGravity()) {
		case CENTENT_FLOAT:
		case CENTENT:
			offsetY = currentView.getTop()+(tabHeight - barHeight) / 2;
			break;
		case TOP:
		case TOP_FLOAT:
			offsetY = currentView.getTop();
			break;
		case BOTTOM:
		case BOTTOM_FLOAT:
		default:
			offsetY = currentView.getBottom()-barHeight;
			break;
		}
		
		scrollBar.draw(canvas, offsetX, offsetY, offsetX+barWidth, offsetY+barHeight);
	}

	
	/*可以重写下面方法绘制view bar*/
//	@Override
//	protected void dispatchDraw(Canvas canvas) {
//		boolean in = false;
//		if (scrollBar != null && scrollBar.getGravity() == Gravity.CENTENT_BACKGROUND) {
//			in = true;
//			drawSlideBar(canvas);
//		}
//		super.dispatchDraw(canvas);
//		if (scrollBar != null && scrollBar.getGravity() != Gravity.CENTENT_BACKGROUND) {
//			in = true;
//			drawSlideBar(canvas);
//		}
//		if (!in) {
//			inRun.stop();
//		}
//	}
}
