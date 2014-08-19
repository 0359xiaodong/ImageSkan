package com.example.imageskan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	private OnMeasureListener onMeasureListener;
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);      
        //��ͼƬ�����Ĵ�С�ص���onMeasureSize()������  
        Log.i("ImageSkan",widthMeasureSpec+"...."+heightMeasureSpec);
        if(onMeasureListener != null){  
            onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());  
        }  
    } 
	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		 this.onMeasureListener = onMeasureListener; 
	}
	public interface OnMeasureListener{  
        public void onMeasureSize(int width, int height);  
    } 

}
