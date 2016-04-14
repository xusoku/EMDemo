package com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar;

import android.graphics.Canvas;

public interface ScrollBar {
	public static enum Gravity {
		/** 顶部占位 */
		TOP,
		/** 顶部覆盖在Indicator�? */
		TOP_FLOAT,
		/** 底部占位 */
		BOTTOM,
		/** 底部覆盖在Indicator�? */
		BOTTOM_FLOAT,
		/** 中间覆盖在Indicator�? */
		CENTENT,
		/** 中间，被Indicator覆盖 */
		CENTENT_FLOAT
	}

	/**
	 * SlideView显示的高�?
	 * 
	 * @return
	 */
	public int getHeight(int tabHeight);

	/**
	 * SlideView 显示的宽�?
	 * 
	 * @param tabWidth
	 * @return
	 */
	public int getWidth(int tabWidth);


	public void draw(Canvas canvas, float left, float top, float right, float bottom);

	/**
	 * 位置
	 * 
	 * @return
	 */
	public Gravity getGravity();

}
