package com.qf.qrcoderdemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MyDiaLog extends Dialog{
//	private ImageView imageView;
	private LinearLayout ll;
	List<Bitmap> bitmaps;
//	private SetQRcordlinsener setQRcordlinsener;


	public MyDiaLog(Context context) {
		super(context, R.style.myDiaLogTheme);
		this.bitmaps = new ArrayList<>();
//		this.setQRcordlinsener = setQRcordlinsener;
	}

	public void setBitmaps(List<Bitmap> bitmaps) {
		this.bitmaps = bitmaps;

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dialog);
		ll = (LinearLayout) findViewById(R.id.line);

		ll.removeAllViews();
		for (Bitmap bitmap : bitmaps) {
			ImageView iv = new ImageView(getContext());
			iv.setImageBitmap(bitmap);
			ll.addView(iv);
		}


//		imageView = (ImageView) this.findViewById(R.id.la_dilo_img);
		//控制dialog的大小
		//获得屏幕的宽高
//		int width = getContext().getResources().getDisplayMetrics().widthPixels;
//		int hight = getContext().getResources().getDisplayMetrics().heightPixels;

		//获得dialog的窗口对象
		Window  window = getWindow();
		//获得window的布局属性
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		layoutParams.gravity = Gravity.CENTER;

//		layoutParams.width = (int)(widht*0.6);
//		layoutParams.height = hight/3;
//		layoutParams.gravity = Gravity.CENTER;

//		layoutParams.width = width;
//		layoutParams.height = hight/3;
//		layoutParams.alpha = 0.6f;
//		layoutParams.gravity = Gravity.BOTTOM;

		
	}
	
//	public void shows() {
//		super.show();
//		setQRcordlinsener.setImage(imageView);
//	}
//
//	public interface SetQRcordlinsener{
//		void setImage(ImageView img);
//	}

//	public interface SetLayout{
//		void setImage(LinearLayout layout, View view);
//	}

}
