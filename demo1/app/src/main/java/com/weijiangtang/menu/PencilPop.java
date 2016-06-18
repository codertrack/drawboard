package com.weijiangtang.menu;

import android.graphics.Color;
import android.view.View;

import com.weijiangtang.R;
import com.weijiangtang.activity.DrawingActivity;


/**
 * Created by wukai on 16/3/9.
 */
public class PencilPop extends AbsMenuPopWindow {

	private SelectListener mListen;
	private int[] vids =null;
	public PencilPop(DrawingActivity activity, int layoutId,int width,int height) {
		super(activity, layoutId, width,height);
	}

	@Override
	public void initView() {
		mListen = new SelectListener();
		vids = new int[]{
				R.id.select_blue,
				R.id.select_red,
				R.id.select_green,
				R.id.select_yellow,
				R.id.select_xian1,
				R.id.select_xian2,
				R.id.select_xian3
		};
		for (int i = 0; i < vids.length; i++) {
			mView.findViewById(vids[i]).setOnClickListener(mListen);

		}

	}

//	public void onSelectChange(final View view){
//
//		int id = view.getId();
//
//		switch (id){
//
//			case R.id.select_blue:
//				mAc.mBorad..setPaintColor(Color.BLUE);
//				break;
//			case R.id.select_red:
//				mAc.mBorad.mDrawingView.setPaintColor(Color.RED);
//				break;
//			case R.id.select_green:
//				mAc.mBorad.mDrawingView.setPaintColor(Color.GREEN);
//				break;
//			case R.id.select_yellow:
//				mAc.mBorad.mDrawingView.setPaintColor(Color.YELLOW);
//				break;
//			case R.id.select_xian1:
//				mAc.mBorad.mDrawingView.setPaintSize(10);
//				break;
//			case R.id.select_xian2:
//				mAc.mBorad.mDrawingView.setPaintSize(20);
//				break;
//			case R.id.select_xian3:
//				mAc.mBorad.mDrawingView.setPaintSize(40);
//				break;
//
//		}
//	}



	 class SelectListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			onSelectChanged(v);
			mWindow.dismiss();
		}

	}


	public void onSelectChanged(final View view){

		int id = view.getId();

		switch (id){

			case R.id.select_blue:
				mAc.mBorad.mScaleDragView.setPaintColor(Color.BLUE);
				break;
			case R.id.select_red:
				mAc.mBorad.mScaleDragView.setPaintColor(0xFFFF0080);
				break;
			case R.id.select_green:
				mAc.mBorad.mScaleDragView.setPaintColor(Color.GREEN);
				break;
			case R.id.select_yellow:
				mAc.mBorad.mScaleDragView.setPaintColor(Color.YELLOW);
				break;
			case R.id.select_xian1:
				mAc.mBorad.mScaleDragView.setPaintSize(10);
				break;
			case R.id.select_xian2:
				mAc.mBorad.mScaleDragView.setPaintSize(20);
				break;
			case R.id.select_xian3:
				mAc.mBorad.mScaleDragView.setPaintSize(40);
				break;

		}
	}

}
