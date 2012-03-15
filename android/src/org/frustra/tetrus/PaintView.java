package org.frustra.tetrus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class PaintView extends View implements OnGestureListener {
	
	private Bitmap mBitmap;
	protected Canvas mCanvas;
	protected Paint mPaint;
	protected boolean hasMovedSinceLastTouch = false;
	private GestureDetector gestureScanner = null;
	
	public PaintView(Context c) {
		super(c);
		Display display = ((Activity) c).getWindowManager().getDefaultDisplay();
		mBitmap = Bitmap.createBitmap(display.getWidth(), display.getHeight(), Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		
		gestureScanner = new GestureDetector(this);
		
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL);
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		onViewDraw(mCanvas);
		mPaint.setAlpha(0xFF);
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
	}
	
	protected void forceRedraw() {
		this.postInvalidate();
	}
	
	protected abstract void onViewDraw(Canvas canvas);
	public abstract boolean onUp(MotionEvent event);
	
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) onUp(event);
		boolean ret = gestureScanner.onTouchEvent(event);
		if (!ret) return false;
		invalidate();
		return true;
	}
}
