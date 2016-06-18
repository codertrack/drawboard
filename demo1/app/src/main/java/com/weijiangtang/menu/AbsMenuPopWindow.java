package com.weijiangtang.menu;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.weijiangtang.activity.DrawingActivity;


/**
 * Created by wukai on 16/3/10.
 */
abstract public class AbsMenuPopWindow {


	protected ViewGroup mView;

	protected PopupWindow mWindow;

	protected DrawingActivity mAc;

	public AbsMenuPopWindow(DrawingActivity activity, int layoutId,int width,int height){

		mView = (ViewGroup) LayoutInflater.from(activity).inflate(layoutId,null);
		mWindow = new PopupWindow(mView,width,height,true);
		mAc = activity;
		mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		initView();
	}


	public void showPopWindow(View location){
		mWindow.showAsDropDown(location,-50,50);
	}


	abstract  public void initView();
}
