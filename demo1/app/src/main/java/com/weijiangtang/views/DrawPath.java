package com.weijiangtang.views;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by wukai on 16/3/9.
 */
public class DrawPath {

	public Path path;
	public Paint paint;

	public DrawPath(Path p,Paint pt){
		this.paint = pt;
		this.path = p;
	}

}
