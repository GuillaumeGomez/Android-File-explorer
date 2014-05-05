package com.fileexplorer;

import android.widget.ImageView;

public class Container {
	public MainActivity act = null;
	public ImageView img = null;
	
	Container(MainActivity a, ImageView v) {
		act = a;
		img = v;
	}
}
