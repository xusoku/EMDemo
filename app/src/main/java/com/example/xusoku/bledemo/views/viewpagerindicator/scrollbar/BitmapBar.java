package com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @version 1.0 通过图片来设置滑动块
 */
public class BitmapBar implements ScrollBar {
	protected Gravity gravity;
	private Bitmap bitmap;
	
	public BitmapBar(Context context, int drawableId) {
		this(context, drawableId, Gravity.CENTENT);
	}

	public BitmapBar(Context context, int drawableId, Gravity gravity) {
		this.gravity = gravity;
		bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
		
	}
	public BitmapBar(Context context, Bitmap bitmap) {
		this(context, bitmap, Gravity.CENTENT);
	}
	public BitmapBar(Context context, Bitmap bitmap, Gravity gravity) {
		this.gravity = gravity;
		this.bitmap = bitmap;
	}
	

	@Override
	public void draw(Canvas canvas, float left, float top, float right,
			float bottom) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		Rect src=new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF dst=new RectF(left,top, right, bottom);
		canvas.drawBitmap(bitmap, src, dst, paint);
	}

	@Override
	public int getHeight(int tabHeight) {
		int height=bitmap.getHeight();
		if (height>tabHeight) {
			return tabHeight;
		}
		return height;
	}

	@Override
	public int getWidth(int tabWidth) {
		int width=bitmap.getWidth();
		if (width>tabWidth) {
			return tabWidth;
		}
		return width;
	}

	@Override
	public Gravity getGravity() {
		return gravity;
	}

	public BitmapBar setGravity(Gravity gravity) {
		this.gravity = gravity;
		return this;
	}

}
