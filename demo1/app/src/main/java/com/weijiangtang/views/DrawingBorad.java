package com.weijiangtang.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wukai on 16/3/13.
 * 画板
 */
public class DrawingBorad extends RelativeLayout {

    //public DrawingView mDrawingView;
    //素材
    //private MaterialView mMetiral;
    //可以拖动的背景图片
    //public DragScaleImage mDragView;

    public ScaleImageView mScaleDragView;

    public LayoutParams mParams;

    public DrawingBorad(Context context) {
        super(context);
        init();
    }

    public DrawingBorad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingBorad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        //mDrawingView = new DrawingView(getContext());
        //mDrawingView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mMetiral = new MaterialView(getContext());
//		LayoutParams lp = new LayoutParams(500, LayoutParams.MATCH_PARENT);
//
//		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//		mMetiral.setLayoutParams(lp);
//		mMetiral.setScaleType(ImageView.ScaleType.FIT_XY);

        mScaleDragView = new ScaleImageView(getContext());
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mScaleDragView.setLayoutParams(mParams);
        mScaleDragView.setBackgroundColor(Color.BLUE);
//		mScaleDragView.setCallback(this);
        //addView(mMetiral,0);
        addView(mScaleDragView);
        //addView(mDrawingView,2);

    }


    private int mWidth;
    private int mHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    /**
     * 导出为图片
     *
     * @param dir
     */
    public void exportImageView(String dir) {
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!directory.isDirectory()) return;
        File bmFile = new File(dir + "/" + System.currentTimeMillis() + ".jpg");

        Bitmap exportBm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        draw(new Canvas(exportBm));
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(bmFile));
            exportBm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(getContext(), "导出成功至" + bmFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置背景图片
     *
     * @param bg
     */
    public void setBackgroundImage(Bitmap bg) {
        Log.d("TAG", "width->" + mWidth + "height->" + mHeight);
        mScaleDragView.setBitmap(bg);
        //invalidate();
    }

    /**
     * 粘贴素材
     *
     * @param bm
     */
    public void setPasteMatrial(Bitmap bm) {
        //mDragView.setBitmap(bm);
        //mMetiral.setImageDrawable(new BitmapDrawable(bm));
        //mMetiral.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //invalidate();
    }


    public void onScale(float px, float py, float sx, float sy) {
//		mScaleDragView.setPivotX(px);
//		mScaleDragView.setPivotY(py);
        mScaleDragView.setScaleX(sx);
        mScaleDragView.setScaleY(sy);
    }

    public void onDrag(int disx, int disy) {

        mParams.leftMargin = disx;
        mParams.topMargin = disy;
        mScaleDragView.setLayoutParams(mParams);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_2_DOWN:// 第二个手指按下的事件
                point.x = event.getX(0);
                point.y = event.getY(0);
                System.out.println(" point.x = " + point.x + " point.y = " + point.y);
                // 计算出两点间的初始距离
                distance = Math.sqrt(Math.pow((event.getX(0) - event.getX(1)), 2)
                        + Math.pow(event.getY(0) - event.getY(1), 2));
                // 计算中点坐标
                mid_x = (event.getX(0) + event.getX(1)) / 2;
                mid_y = (event.getY(0) + event.getY(1)) / 2;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            return true;

        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_2_DOWN:// 第二个手指按下的事件
                // 计算出两点间的初始距离
                distance = Math.sqrt(Math.pow((event.getX(0) - event.getX(1)), 2)
                        + Math.pow(event.getY(0) - event.getY(1), 2));
                // 计算中点坐标
//				mid_x = (event.getX(0) + event.getX(1)) / 2;
//				mid_y = (event.getY(0) + event.getY(1)) / 2;
                Log.d("TAG", "midx-" + mid_x + "mid_y" + mid_y);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "ACTION_MOVE point count 2");
                if (event.getPointerCount() >= 2) {
                    scale(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG", "ACTION_UP board");
//				dis_x = 0;
//				dis_y = 0;
                point.x = 0;
                point.y = 0;
                break;
            default:
                break;
        }

        return true;
    }


    private double distance;// 两个手指之间的距离
    private float mid_x;
    private float mid_y;
    private float maxScale = 5f;
    private float minScale = 0.5f;
    private Matrix mMatrix = new Matrix();
    private float curScale;
    private float dis_x;
    private float dis_y;
    private PointF point = new PointF();
    private float values[] = new float[9];

    public boolean scale(MotionEvent event) {

        double tempDistance = Math.sqrt(Math.pow(
                (event.getX(0) - event.getX(1)), 2)
                + Math.pow(event.getY(0) - event.getY(1), 2));
        // 计算出本次移动应该缩放的倍数

        float scale = (float) (tempDistance / distance);
        //Log.d("TAG","scale-->"+scale);
        // 让每次计算都是相对于上一次的操作
        distance = tempDistance;

        // if (curScale >= maxScale && scale > 1) {// 如果当前已经放到了最大，并且还想放大，则不处理
        // return true;
        // }
        //
        // if (curScale <= minScale && scale < 1) {// 如果当前已经缩到了最小，并且还想缩小，则不处理
        // return true;
        // }

        mMatrix.postScale(scale, scale, mid_x, mid_y);
        // 处理图片平移的事件
        float temp_x = event.getX(0);
        float temp_y = event.getY(0);

        // // 计算中点坐标
        // temp_x = (event.getX(0) + event.getX(1)) / 2;
        // temp_y = (event.getY(0) + event.getY(1)) / 2;
        System.out.println(" temp_x = " + temp_x + " temp_y = " + temp_y);


        float tx = temp_x - point.x;
        float ty = temp_y - point.y;

        dis_x += tx;
        dis_y += ty;

//		System.out.println(" dis_x = " + dis_x + " dis_y = " + dis_y);
        // 把矩阵中存储当前图片变换的数据拿出来
        mMatrix.getValues(values);
        curScale = values[0];

        // 让每次计算都是相对于上一次的操作
        onScale(mid_x, mid_y, curScale, curScale);
        if (dis_x != 0 && dis_y != 0) {
            mScaleDragView.setTranslationX(dis_x);
            mScaleDragView.setTranslationY(dis_y);
        }
        point.x = temp_x;
        point.y = temp_y;

        // 重绘UI
        // invalidate();
        return true;
    }
}
