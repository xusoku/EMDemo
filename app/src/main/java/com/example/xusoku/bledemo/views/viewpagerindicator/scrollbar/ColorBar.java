package com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class ColorBar implements ScrollBar {
	protected Gravity gravity;
	protected View view;
	protected int color;
	protected int height;
	protected int width;
	private Paint paint;
	private int radius;

	public ColorBar(Context context, int color) {
		this(context, color, Gravity.CENTENT,0,0);
	}
	public ColorBar(Context context, int color, Gravity gravity) {
		this(context, color,gravity,0,0);
	}
	public ColorBar(Context context, int color, Gravity gravity,int height,int radius) {
		this.color = color;
		this.gravity = gravity;
		this.height=height;
		this.radius=radius;
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
	}

	public void draw(Canvas canvas, float left, float top, float right,
			float bottom) {
//		canvas.drawRect(left, top, right, bottom, paint);
		canvas.drawRoundRect(new RectF(left, top, right, bottom), radius, radius, paint);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		view.setBackgroundColor(color);
	}

	public void setHeight(int height) {
		this.height = height;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	@Override
	public int getHeight(int tabHeight) {
		if (height == 0 || height > tabHeight) {
			return tabHeight;
		}
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getWidth(int tabWidth) {
		if (width == 0 || width > tabWidth) {
			return tabWidth;
		}
		return width;
	}

	@Override
	public Gravity getGravity() {
		return gravity;
	}

	public void setGravity(Gravity gravity) {
		this.gravity = gravity;
	}

}
