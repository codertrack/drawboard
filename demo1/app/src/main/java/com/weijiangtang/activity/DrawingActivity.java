package com.weijiangtang.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharerec.recorder.OnRecorderStateListener;
import cn.sharerec.recorder.Recorder;
import cn.sharerec.recorder.impl.ViewRecorder;

import com.weijiangtang.R;
import com.weijiangtang.menu.MenuActions;
import com.weijiangtang.menu.PencilPop;
import com.weijiangtang.views.BoardViewpager;
import com.weijiangtang.views.DrawingBorad;

import com.weijiangtang.views.ScaleImageView.PaintMode;
public class DrawingActivity extends Activity implements
		OnRecorderStateListener {

	private final int MAX_NUMBER = 30;

	public DrawingBorad mBorad;

	private RadioGroup mPencilSelector;

	private MenuActions mMenuActions;

	private PencilPop mPenCilPop;
	private TextView mTv_Number;
	private int mCurrentNumber = 0;
	private ViewRecorder mRecorder;
	private BoardViewpager mViewPager;
	private ArrayList<DrawingBorad> mCacheBoards;

	private boolean mToggle = false;
	// 导出图片路径
	private final String ExportSaveImageDir = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/drawSavePics";
	private final String Tempdir = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/drawTemp";
	// 从相册或者拍照存储的临时路径
	private final File mTempFile = new File(Tempdir + "IMG_tmpfile.jpg");
	// 建材之后要保存的路径
	private final File mCropFile = new File(Tempdir + "IMG_Cropfile.jpg");
	/* 1=背景图片 2=粘贴图片 */
	public int mSelectAction = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		initView();
		mRecorder = new ViewRecorder(getWindow().getDecorView(), "105e0c5440474",
				"9c44743171383e8b0782fd577648b6be");
		if (!mRecorder.isAvailable()) {
			Toast.makeText(this, "该手机暂不支持录制视频..", Toast.LENGTH_SHORT).show();
		}

		mRecorder.setPath(ExportSaveImageDir);
		mRecorder.setOnRecorderStateListener(this);
		init();


	}

	private void initView() {
		mBtn_Recoder = (ImageButton) findViewById(R.id.ibtn_record);
		mCacheBoards = new ArrayList<DrawingBorad>();
		for (int i = 0; i < MAX_NUMBER; i++) {
			mCacheBoards.add(i,new DrawingBorad(this));
		}
		mViewPager = (BoardViewpager) findViewById(R.id.boards);

		mViewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return MAX_NUMBER;
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view==object;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(mCacheBoards.get(position));
				return mCacheBoards.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView((View) object);
			}

		});
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				mBorad = mCacheBoards.get(position);
				mViewPager.setCurrentBoard(mBorad);

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		mBorad=mCacheBoards.get(0);
		mViewPager.setCurrentBoard(mBorad);

		mTv_Number = (TextView) findViewById(R.id.text_num);
		mPenCilPop = new PencilPop(this, R.layout.pop_pencil, 800, 400);
		mMenuActions = new MenuActions(this, R.layout.pop_actions, 400, 900);
		((RadioButton) findViewById(R.id.ibtn_pen)).setChecked(true);
		mTv_Number.setText(mCurrentNumber+"");
	}

	public void onBtnClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.ibtn_actions:
			mMenuActions.showPopWindow(view);
			break;
		case R.id.ibtn_pre:
			if (mCurrentNumber ==0)return;
			mCurrentNumber--;
			mViewPager.setCurrentItem(mCurrentNumber);
			mTv_Number.setText(mCurrentNumber+"");
			break;
		case R.id.ibtn_next:
			if (mCurrentNumber ==MAX_NUMBER-1)return;
			mViewPager.setCurrentItem(++mCurrentNumber);
			mTv_Number.setText(mCurrentNumber+"");
			break;
		case R.id.ibtn_exit:
			stop();
			break;
		case R.id.ibtn_pen:
			mPenCilPop.showPopWindow(view);
			if (mBorad.mScaleDragView.mMode == PaintMode.Paint)
				return;
			mBorad.mScaleDragView.mMode = PaintMode.Paint;
			break;
		case R.id.ibtn_clear:
			mBorad.mScaleDragView.clearCanvas();
			break;
		case R.id.ibtn_fanhui:
			mBorad.mScaleDragView.undoAction();
			break;
		case R.id.ibtn_xiangpi:
			if (mBorad.mScaleDragView.mMode == PaintMode.ERASER)
				return;
			mBorad.mScaleDragView.mMode = PaintMode.ERASER;
			break;
		case R.id.ibtn_record:
			int state = mRecorder.getState();
			if (state == Recorder.STATE_STOPPED){
				start();
			}

			break;
		}



	}




	// 开始录制
	public void start() {
		System.out.println("开始录制");
		Toast.makeText(this, "开始录制", Toast.LENGTH_LONG).show();
		if (mRecorder.isAvailable()) {
			// System.out.println(recorder.getLocalVideoPath(1));
			mRecorder.setOnRecorderStateListener(this);
			mRecorder.startRecorder();

			// 如果您不知道什么时候您的画面会刷新，则可以使用下面的方式，让录像模块自动抓图，
			// 否则可以调用onTheEndOfTheFrame()方法来手动驱动录像模块抓图 (If you don't know
			// when your view will refresh, you can use the following way, let
			// ShareRec
			// capture the frame automatically. or you can call
			// onTheEndOfTheFrame()
			// manually when your frame refreshing)
			mRecorder.startAuotRefreshRate(10);
		} else {
			Toast.makeText(this, "手机不支持录制", Toast.LENGTH_SHORT).show();
		}
	}

	// 停止录制
	public void stop() {
		System.out.println("停止录制");
//		Toast.makeText(this, "停止录制", Toast.LENGTH_LONG).show();
		mRecorder.stopRecorder();
	}

	private final int TAKE_PICTURE = 1;
	private final int SELECTOR_FROM_GALLERY = 2;
	private static final int SELECT_FROM_CROP = 3;
	public static final int SelectFrom_MaterialLib = 4;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("TAG", "requestCode-->" + requestCode);

		if (requestCode == TAKE_PICTURE) {
			if (resultCode == RESULT_OK) {
				cropImageUri(Uri.fromFile(mTempFile),Uri.fromFile(mCropFile),500, 500,
						SELECT_FROM_CROP);
			}
		} else if (requestCode == SELECTOR_FROM_GALLERY) {
			if (data == null)return;
			Log.d("TAG", data.getData().toString());
			Uri uri = data.getData();

			if (uri == null) return;
			cropImageUri(uri,Uri.fromFile(mCropFile),300, 300, SELECT_FROM_CROP);

		} else if (requestCode == SelectFrom_MaterialLib) {

			if (resultCode == RESULT_OK) {
				int drawable = data.getIntExtra("drawable", -1);
				if (drawable == -1)
					return;
				Log.d("TAG", "xxx");
				mBorad.setBackgroundImage(((BitmapDrawable) getResources()
						.getDrawable(drawable)).getBitmap());
			}
		} else if (requestCode == SELECT_FROM_CROP) {
			onCropComplete(data);
		}
	}

	/**
	 * 拍照选择背景
	 */
	public void selectBgFromCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
		startActivityForResult(intent, TAKE_PICTURE);
	}

	/**
	 * 导出图片
	 */
	public void exportImage() {
		mBorad.exportImageView(ExportSaveImageDir);
	}

	/**
	 * 从相册选背景
	 */
	public void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		startActivityForResult(intent, SELECTOR_FROM_GALLERY);
	}

	/**
	 * 从素材库选背景
	 */
	public void selectFromMaterialLib() {
		Intent intent = new Intent(this, SucaiActivity.class);
		startActivityForResult(intent, SelectFrom_MaterialLib);
	}

	public void cropImageUri(Uri orgUri,Uri destUri, int width, int height, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(orgUri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", "jpge");
		intent.putExtra("noFaceDetection",true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 剪裁完成之后处理结果
	 * 
	 * @param intent
	 */
	public void onCropComplete(Intent intent) {
//		Bundle bundle = intent.getExtras();
//
//		if (bundle != null) {
//			Bitmap photo = bundle.getParcelable("data");
//			if (mSelectAction == 1) {
//				mBorad.setBackgroundImage(photo);
//			} else if (mSelectAction == 2) {
//				Log.d("tag",
//						"width=" + photo.getWidth() + "height->"
//								+ photo.getHeight());
//				mBorad.setPasteMatrial(photo);
//			}
//		}

		try {
			Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(mCropFile)));
			if (photo == null)return;
			if (mSelectAction == 1) {
				mBorad.setBackgroundImage(photo);
			} else if (mSelectAction == 2) {
				Log.d("tag",
						"width=" + photo.getWidth() + "height->"
								+ photo.getHeight());
				mBorad.setPasteMatrial(photo);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStateChange(Recorder arg0, int state) {
		switch (state) {
		case Recorder.STATE_STARTED:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					chronometer.setBase(SystemClock.elapsedRealtime());
					chronometer.start();
					chronometer.setVisibility(View.VISIBLE);

				}
			});
			onScreenRecoder();
			break;
		case Recorder.STATE_STARTING:
			System.out.println("STATE_STARTING");
			break;
		case Recorder.STATE_STOPING:
			System.out.println("STATE_STOPING");
			break;

			case Recorder.STATE_PAUSING:
				System.out.println("STATE_PAUSING");
				break;
			case Recorder.STATE_PAUSED:
				System.out.println("STATE_PAUSED");
				break;
			case Recorder.STATE_STOPPED:

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onStopRecoder();
						chronometer.setVisibility(View.GONE);
						chronometer.stop();
					}
				});


				System.out.println("STATE_STOPPED");
				System.out.println("STATE_IDLE");
				mRecorder.setText("Eraser Demo");
				mRecorder.addCustomAttr("score", "5000");
				mRecorder.addCustomAttr("name", "ShareRec Developer");
				mRecorder.addCustomAttr("brand", "hehe!");
				mRecorder.addCustomAttr("level", "10");
				mRecorder.showShare();
				break;

			default:
			break;
		}
	}


	private Chronometer chronometer;

	public void init(){
		chronometer = (Chronometer) findViewById(R.id.timer);
		chronometer.setBackgroundColor(Color.DKGRAY);
		chronometer.setTextColor(Color.WHITE);
		chronometer.setFormat("%s");
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN){
			switch (mRecorder.getState()){
				case Recorder.STATE_STARTED:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							chronometer.setVisibility(View.GONE);
						}
					});
					 break;
				default:break;
			}
		}else if (ev.getAction() == MotionEvent.ACTION_UP){
			if (mRecorder.getState()== Recorder.STATE_STARTED){
				chronometer.setVisibility(View.VISIBLE);
			}
		}

		return super.dispatchTouchEvent(ev);
	}


	private Timer mTimer;
	private boolean scheduleFlag= false;
	private ImageButton mBtn_Recoder;

	private void onScreenRecoder(){
		if (mTimer == null)mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!scheduleFlag){
							mBtn_Recoder.setBackgroundResource(R.drawable.record_run);
						}else {
							mBtn_Recoder.setBackgroundResource(R.drawable.menu_play);
						}
					}
				});

				scheduleFlag = !scheduleFlag;
			}
		},0,1000);
	}

	private void onStopRecoder(){
		if (mTimer != null)mTimer.cancel();
		mBtn_Recoder.setBackgroundResource(R.drawable.menu_play);

	}


}
