package com.weijiangtang.views;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 对图片进行缩放和平移操作
 * 
 * @author Administrator
 * 
 */
public class ScaleImageView extends ImageView {

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScaleImageView(Context context) {
		super(context, null);
		init();
	}

	private Bitmap mBitmap;
	private Rect bitmapRect;// 图片宽高的矩形
	private Rect disRect;// 填充区域矩形
	// ////////////////////
	private Paint mPaint;
	private String mPenColor;
	private float mPenSize;
	public float mDownX;
	public float mDownY;
	private ArrayList<DrawPath> mSavePaths;
	public Paint mPaintEarse;
	private int mWidth;
	private int mHeight;

	public PaintMode mMode = PaintMode.Paint;
	private Canvas mCanvas;

	private Path mPath;

	private Bitmap mCacheBitmap;

	// //////////////////////

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
		bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		int w = getMeasuredWidth();
		int h = getMeasuredHeight();

		float scale_x = (float) w / bitmap.getWidth();
		float scale_y = (float) h / bitmap.getHeight();

		float scale = Math.min(scale_x, scale_y);

		int dsc_w = (int) (scale * bitmap.getWidth() + 0.5f);
		int dsc_h = (int) (scale * bitmap.getHeight() + 0.5f);

		int left = (w - dsc_w) >> 1;
		int top = (h - dsc_h) >> 1;

		destdrawRect = new Rect(left, top, left + dsc_w, dsc_h);
		mMode = PaintMode.Paint;
		clearCanvas();
		drawOldPaths();
		invalidate();
	}

	private float maxScale = 5f;
	private float minScale = 0.5f;
	private Matrix mMatrix = new Matrix();

	/**
	 * 设置缩放的最大值
	 * 
	 * @param max
	 */
	public void setMaxScale(float max) {
		maxScale = max;
	}

	/**
	 * 设置缩放的最小值
	 * 
	 * @param min
	 */
	public void setMinScale(float min) {
		minScale = min;
	}

	private boolean isFrist = true;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		Log.d("TAG", "mWidth=" + mWidth + "mHeight=" + mHeight);
		initCanvas(mWidth, mHeight);

		if (mBitmap == null)
			return;
		if (isFrist) {
			disRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
			// 计算宽的缩放倍数
			float scaleX = (float) getMeasuredWidth() / mBitmap.getWidth();
			// 计算高的缩放倍数
			float scaleY = (float) getMeasuredHeight() / mBitmap.getHeight();

			float scale = Math.min(scaleX, scaleY);
			System.out.println("scale = " + scale + " scaleX = " + scaleX
					+ " scaleY = " + scaleY);
			mMatrix.postScale(scale, scale);
			// 让图片缩放最小值为适配屏幕的0.5倍
			minScale = scale * minScale;
			// 让图片缩放最大值为适配屏幕的5倍
			maxScale = scale * maxScale;

			float translate_y = (getMeasuredHeight() - mBitmap.getHeight()
					* scale) / 2;
			mMatrix.postTranslate(0, translate_y);
			isFrist = false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doTouchDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			doTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			Log.d("TAG", "ACTION_UP");
			onTouchUp();
			break;
		default:
			break;
		}

		return true;
	}


	private Rect destdrawRect;

	@Override
	protected void onDraw(Canvas canvas) {
		// 画轨迹
		// 画背景图片

		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, bitmapRect, destdrawRect, null);
		}

		if (mCacheBitmap != null && !mCacheBitmap.isRecycled()) {
			canvas.drawBitmap(mCacheBitmap, 0, 0, null);
		}
	}

	// ///////////////////////////////////
	private void init() {
		mPenSize = 20f;
		mPenColor = "#00ff00";
		mSavePaths = new ArrayList<DrawPath>();
		initPencil();
		initEraser();
	}

	/**
	 * 初始化普通画笔
	 */
	public void initPencil() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0080);
		mPaint.setStrokeWidth(mPenSize);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	/**
	 * 初始化橡皮擦
	 */
	public void initEraser() {
		mPaintEarse = new Paint();
		mPaintEarse.setAntiAlias(true);
		mPaintEarse.setDither(true);
		mPaintEarse.setColor(Color.WHITE);
		mPaintEarse.setStrokeWidth(40);
		mPaintEarse.setStyle(Paint.Style.STROKE);
		mPaintEarse.setStrokeJoin(Paint.Join.ROUND);
		mPaintEarse.setStrokeCap(Paint.Cap.SQUARE);
		mPaintEarse.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

	}

	public enum PaintMode {
		Paint, ERASER, DRAG, SCALE
	}

	public void setPaintSize(int size) {
		mPaint.setStrokeWidth(size);
	}

	public void setPaintColor(int c) {
		mPaint.setColor(c);
	}

	public void clearCanvas() {
		initCanvas(mWidth, mHeight);
		if (mSavePaths.size() > 0) {
			mSavePaths.clear();
			if (mBitmap != null) {
				mCanvas.drawBitmap(mBitmap, bitmapRect, destdrawRect, null);
			}

		}
		initPencil();
		initEraser();
		invalidate();
	}

	public void undoAction() {
		// 重置画布
		initCanvas(mWidth, mHeight);
		if (mSavePaths.size() == 0) {
			mBitmap = null;
			invalidate();
		}
		Log.d("TAG", "undoAction-  mSavePaths.size-->" + mSavePaths.size());
		if (mSavePaths.size() != 0) {
			mSavePaths.remove(mSavePaths.size() - 1);
			drawOldPaths();
		}

	}

	/**
	 * 初始化画布
	 * 
	 * @param width
	 * @param height
	 */
	public void initCanvas(int width, int height) {
		if (mCacheBitmap != null) {
			mCacheBitmap.recycle();
		}
		mCacheBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mCacheBitmap);
		mCanvas.drawColor(Color.TRANSPARENT);
	}

	private void drawOldPaths() {
		// if (mSavePaths == null || mSavePaths.size() == 0) return;
		Log.d("TAG", "onDraw path number:" + mSavePaths.size());
		for (DrawPath dp : mSavePaths) {
			mCanvas.drawPath(dp.path, dp.paint);
		}
		invalidate();
	}

	private void doTouchDown(MotionEvent event) {
		mPath = new Path();
		mDownX = event.getX();
		mDownY = event.getY();
		mPath.moveTo(mDownX, mDownY);
		invalidate();
	}

	private void doTouchMove(MotionEvent event) {
		int dx = (int) Math.abs(event.getX() - mDownX);
		int dy = (int) Math.abs(event.getY() - mDownY);
		if (dx >= 4 || dy >= 4) {
			mPath.quadTo(mDownX, mDownY, event.getX(), event.getY());
		}

		if (mMode == PaintMode.ERASER) {
			mCanvas.drawPath(mPath, mPaintEarse);
		} else if (mMode == PaintMode.Paint) {
			mCanvas.drawPath(mPath, mPaint);
		}

		mDownX = event.getX();
		mDownY = event.getY();
		invalidate();
	}

	public void onTouchUp() {
		if (mMode == PaintMode.Paint) {
			mCanvas.drawPath(mPath, mPaint);
			DrawPath dp = new DrawPath(mPath, new Paint(mPaint));
			mSavePaths.add(dp);
		} else if (mMode == PaintMode.ERASER) {

			mCanvas.drawPath(mPath, mPaintEarse);
			// 如果笔记已经全部返回了
			if (mSavePaths.size() != 0) {
				DrawPath dp = new DrawPath(mPath, new Paint(mPaintEarse));
				mSavePaths.add(dp);
			}

		}
		invalidate();
		mPath = null;
	}


//
//	ScaleAndDrag mCallback;
//
//	public void setCallback(ScaleAndDrag callback){
//		mCallback = callback;
//	}
//	public interface ScaleAndDrag{
//		public void onScale(float px,float py,float sx,float sy);
//		public void onDrag(int disx,int disy);
//	}




}
