package com.weijiangtang.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by wukai on 16/3/13.
 *
 * 处理拖动和缩放
 */
public class DragScaleImage extends ImageView {

	private float mDrawDownX;
	private float mDrawDownY;

	public DragScaleImage(Context context) {
		super(context);
	}

	public DragScaleImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DragScaleImage(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	private Matrix mCurrentMatrix = new Matrix();

	private int mPreDistance ;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action ==MotionEvent.ACTION_DOWN){
				//计算两指间的距离
				mCurrentMatrix = new Matrix();
				mDrawDownX = event.getX(0);
				mDrawDownY = event.getY(0);
				float dx = event.getX(0)-event.getX(1);
				float dy = event.getY(0)-event.getY(1);
				mPreDistance = (int) Math.sqrt(dx*dx+dy*dy);

		}else if (action == MotionEvent.ACTION_MOVE){
			float dx = event.getX(0)-event.getX(1);
			float dy = event.getY(0)-event.getY(1);
			float distance = (float) Math.sqrt(dx*dx+dy*dy);

			if (Math.abs(mPreDistance -distance)<10){
				int drawx = (int) (mDrawDownX-event.getX(0));
				int drawy = (int) (mDrawDownY-event.getY(0));
				mCurrentMatrix.postTranslate(drawx,drawy);
				invalidate();
			}else{

			}
		}


		return super.dispatchTouchEvent(event);
	}
}
