package com.weijiangtang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.weijiangtang.R;


public class SucaiActivity extends Activity implements OnClickListener {
	LinearLayout gonggong, moren;
	ImageView sucai_hanhui;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sucai);
		sucai_hanhui = (ImageView) findViewById(R.id.sucai_hanhui);
		gonggong = (LinearLayout) findViewById(R.id.sucai_gonggong);
		moren = (LinearLayout) findViewById(R.id.sucai_moren);
		gonggong.setOnClickListener(this);
		moren.setOnClickListener(this);
		sucai_hanhui.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sucai_hanhui:
				setResult(RESULT_CANCELED);
				finish();
				break;
			case R.id.sucai_gonggong:
				Toast.makeText(getApplicationContext(), "公共", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SucaiActivity.this, SucaiGongGongActivity.class);
				startActivityForResult(intent,DrawingActivity.SelectFrom_MaterialLib);
				break;
			case R.id.sucai_moren:
				Toast.makeText(getApplicationContext(), "默认", Toast.LENGTH_SHORT).show();
				break;
			default:break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DrawingActivity.SelectFrom_MaterialLib){
			setResult(RESULT_OK,data);
			finish();
		}
	}
}
