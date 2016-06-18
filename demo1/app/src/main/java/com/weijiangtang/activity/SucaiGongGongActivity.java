package com.weijiangtang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.weijiangtang.R;

public class SucaiGongGongActivity extends Activity implements OnClickListener {

	private ImageView sucai_hanhui_gonggong;
	private GridView gridView;
	private String[] pic_fanction = { "菊花", "雁双飞", "芬芳", "回忆" };
	private List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
	private int[] pic_image = { R.drawable.b1, R.drawable.b2, R.drawable.b3,
			R.drawable.b4 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sucai_gongong);
		sucai_hanhui_gonggong = (ImageView) findViewById(R.id.sucai_hanhui_gonggong);
		sucai_hanhui_gonggong.setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.gridview);
		for (int i = 0; i < pic_fanction.length; i++) {
			Map<String, Object> listem = new HashMap<String, Object>();
			listem.put("text", pic_fanction[i]);
			listem.put("image", pic_image[i]);
			listems.add(listem);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, listems,
				R.layout.item_grid, new String[] { "image", "text" }, new int[] {
						R.id.image_gong, R.id.text_gong });
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("drawable",pic_image[position]);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sucai_hanhui_gonggong:
			finish();
			break;

		}
	}

}
