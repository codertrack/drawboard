package com.weijiangtang.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.weijiangtang.views.ScaleImageView.PaintMode;
import java.util.ArrayList;


/**
 * Created by wukai on 16/3/9.
 */
public class DrawingView extends View {

	private Paint mPaint;
	private float mPenSize;
	private float mDownX;
	private float mDownY;
	private ArrayList<DrawPath> mSavePaths;

	private Canvas mCanvas;

	private Path mPath ;

	private Bitmap mCacheBitmap;

	public DrawingView(Context context) {
		super(context);
		init();

	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();

	}



	private void init(){
		mPenSize = 20f;
		mSavePaths= new ArrayList<DrawPath>();
		initPencil();
		initEraser();
	}

	/**
	 * 初始化普通画笔
	 */
	public void initPencil(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(mPenSize);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);

	}

	/**
	 * 初始化橡皮擦
	 */
	public void initEraser(){
		mPaintEarse = new Paint();
		mPaintEarse.setAntiAlias(true);
		mPaintEarse.setDither(true);
		mPaintEarse.setColor(Color.TRANSPARENT);
		mPaintEarse.setStrokeWidth(40);
		mPaintEarse.setStyle(Paint.Style.STROKE);
		mPaintEarse.setStrokeJoin(Paint.Join.ROUND);
		mPaintEarse.setStrokeCap(Paint.Cap.SQUARE);
		mPaintEarse.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
		//画轨迹
		if (mCacheBitmap != null && !mCacheBitmap.isRecycled()){
			canvas.drawBitmap(mCacheBitmap, 0, 0, null);

		}
		//实时显示笔迹
		if (mPath != null) {
			Log.d("tAG", "paint-mod->" + mMode);
			if (mMode == PaintMode.Paint) {
				canvas.drawPath(mPath, mPaint);
			}
		}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void drawOldPaths(){
		if (mSavePaths == null || mSavePaths.size() == 0)return;
		Log.d("TAG","onDraw path number:"+mSavePaths.size());
		for (DrawPath dp :mSavePaths) {
			mCanvas.drawPath(dp.path,dp.paint);
		}

		invalidate();
	}


	private void doTouchDown(MotionEvent event){
		mPath = new Path();
		mDownX = event.getX();
		mDownY = event.getY();
		mPath.moveTo(mDownX,mDownY);
		invalidate();
	}

	private void doTouchMove(MotionEvent event){
		int dx = (int) Math.abs(event.getX()-mDownX);
		int dy = (int) Math.abs(event.getY()-mDownY);
		if (dx>=4 || dy>=4){
			mPath.quadTo(mDownX,mDownY,event.getX(),event.getY());
		}

		if (mMode == PaintMode.ERASER){
			mCanvas.drawPath(mPath,mPaintEarse);
		}
		mDownX = event.getX();
		mDownY = event.getY();
		invalidate();
	}

	boolean downflag = false;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN){
			if (event.getPointerCount() == 1){
				doTouchDown(event);
			}
			downflag = true;
			return  true;

		}else if (action == MotionEvent.ACTION_MOVE){
				doTouchMove(event);

		}else if (action == MotionEvent.ACTION_UP){
			if (downflag){
				onTouchUp();
			}
		}

		return super.dispatchTouchEvent(event);

	}


	public void onTouchUp(){
		if (mMode == ScaleImageView.PaintMode.Paint){
			mCanvas.drawPath(mPath,mPaint);
			DrawPath dp = new DrawPath(mPath,new Paint(mPaint));
			mSavePaths.add(dp);
		}else if (mMode == ScaleImageView.PaintMode.ERASER){
			mCanvas.drawPath(mPath,mPaintEarse);
			DrawPath dp = new DrawPath(mPath,new Paint(mPaintEarse));
			mSavePaths.add(dp);
		}
		invalidate();
		mPath = null;
	}


	public Paint mPaintEarse;


	private int mWidth;
	private int mHeight;

	public ScaleImageView.PaintMode mMode = ScaleImageView.PaintMode.Paint;

	public void setPaintSize(int size){
		mPaint.setStrokeWidth(size);
	}

	public void setPaintColor(int c){
		mPaint.setColor(c);
	}


	public void clearCanvas(){
		initCanvas(mWidth,mHeight);
		initPencil();
		initEraser();
		invalidate();
	}


	public void undoAction(){
		initCanvas(mWidth,mHeight);
		invalidate();
		if (mSavePaths != null && mSavePaths.size()!= 0){
			mSavePaths.remove(mSavePaths.size() -1);
			drawOldPaths();
		}

	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		Log.d("TAG","mWidth="+mWidth+"mHeight="+mHeight);
		initCanvas(mWidth,mHeight);
	}

	/**
	 * 初始化画布
	 * @param width
	 * @param height
	 */
	public void initCanvas(int width,int height){
		if (mCacheBitmap != null){
			mCacheBitmap.recycle();
		}
		mCacheBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mCacheBitmap);
		mCanvas.drawColor(Color.TRANSPARENT);
	}







}
