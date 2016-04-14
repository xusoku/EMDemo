package com.example.xusoku.bledemo.views.viewpagerindicator;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar.ScrollBar;


public interface PageIndicator extends ViewPager.OnPageChangeListener {
	/**
	 * Bind the indicator to a ViewPager.
	 * 
	 * @param view
	 */
	void setViewPager(ViewPager view);

	/**
	 * Bind the indicator to a ViewPager.
	 * 
	 * @param view
	 * @param initialPosition
	 */
	void setViewPager(ViewPager view, int initialPosition);

	/**
	 * Set a page change listener which will receive forwarded events.
	 * 
	 * @param listener
	 */

	void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

	/**
	 * Notify the indicator that the fragment list has changed.
	 */
	void notifyDataSetChanged();

	/**
	 * 获取每一个pager对应的指示器tabitem，扩展性强，可以自定义view
	 */
	public interface IndicatorAdapter {

		public View getIndicatorView(int position);
		
		/**当page在互动的过程中，可以联动indicator
		 * 
		 * @param view view是当前的tabitem
		 * @param position 是当前的tabitem对应的索引
		 * @param selectPercent 是当前的tabitem在正常状态下，任何属性需要改变的百分比
		 * */
		public void onPageScrolled(View view, int position, float selectPercent);
	}
	
	/*tab上面的需要滑动的指示view*/
	public void setScrollBar(ScrollBar scrollBar) ;
}
