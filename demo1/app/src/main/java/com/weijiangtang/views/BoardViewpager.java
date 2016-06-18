package com.weijiangtang.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wukai on 16/3/14.
 */
public class BoardViewpager extends ViewPager {

	DrawingBorad mCurrentBoard;

	public BoardViewpager(Context context) {
		super(context);
	}

	public BoardViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mCurrentBoard.dispatchTouchEvent(ev);
	}

	public void setCurrentBoard(DrawingBorad b){
		mCurrentBoard = b;
	}
}
