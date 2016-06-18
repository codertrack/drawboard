package com.weijiangtang.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weijiangtang.R;
import com.weijiangtang.activity.DrawingActivity;


/**
 * Created by wukai on 16/3/9.
 */
public class MenuActions extends AbsMenuPopWindow {


	private ListView mListView;

	private String[] pic_fanction = { "拍照做背景", "图片做背景", "素材库背景", "拍照粘贴",
			"图片粘贴", "素材粘贴", "导出为图片" };

	private int[] pic_image = {
			R.drawable.pic_paizhao,
			R.drawable.pic_tupian,
			R.drawable.pic_sucai,
			R.drawable.pic_paizhao,
			R.drawable.pic_tupian,
			R.drawable.pic_sucai,
			R.drawable.pic_baocun };

	public MenuActions(DrawingActivity activity, int layoutId,int width,int height) {
		super(activity, layoutId, width,height);
	}



	@Override
	public void initView() {
		mListView = (ListView) mView.findViewById(R.id.pic_list);
		mListView.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return 7;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(mAc).inflate(R.layout.item_pic,null);
				ImageView iv= (ImageView) convertView.findViewById(R.id.item_iamge);
				TextView tv= (TextView) convertView.findViewById(R.id.item_text);
				iv.setImageDrawable(mAc.getResources().getDrawable(pic_image[position]));
				tv.setText(pic_fanction[position]);
				return convertView;
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position==0){
					mAc.mSelectAction =1;
					mAc.selectBgFromCamera();
				}else if(position==1){
					mAc.mSelectAction = 1;
					mAc.getImageFromAlbum();
				}else if(position==2){
					mAc.mSelectAction = 1;
					mAc.selectFromMaterialLib();

				}else if(position==3){
					mAc.mSelectAction = 2;
					mAc.selectBgFromCamera();
				}else if(position==4){
					mAc.mSelectAction = 2;
					mAc.getImageFromAlbum();
				}else if(position==5){
					mAc.mSelectAction = 2;

				}else if(position==6){
					mAc.exportImage();
				}
				mWindow.dismiss();
			}

		});

	}







}
