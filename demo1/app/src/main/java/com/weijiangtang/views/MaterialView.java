package com.weijiangtang.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by wukai on 16/3/13.
 */
public class MaterialView extends ImageView {

	public DrawingView  mDrawingView;

	public MaterialView(Context context) {
		super(context);
	}

	public MaterialView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MaterialView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return mDrawingView.dispatchTouchEvent(event);
	}

	public void setDrawingView(DrawingView view){
		mDrawingView = view;
	}

}
