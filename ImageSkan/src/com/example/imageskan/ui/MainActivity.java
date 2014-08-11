package com.example.imageskan.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.example.imageskan.R;
import com.example.imageskan.domain.ImageBean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Entity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.GridView;

public class MainActivity extends Activity{
	private ProgressDialog mProgressDialog;
	private GridView mGridView;
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>(); 
	private Handler handle=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		
	}
	private void initView() {
		mGridView=(GridView) findViewById(R.id.main_grid);
		mProgressDialog= ProgressDialog.show(this, null, "���ڼ���..."); 
		mProgressDialog.show();
	}
	private void getImages(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				Cursor cursor=getContentResolver().query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "  
		                + MediaStore.Images.Media.MIME_TYPE + "=?",  
		        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				if(cursor==null){
					return;
				} while (cursor.moveToNext()) {  
                    //��ȡͼƬ��·��  
                    String path = cursor.getString(cursor  
                            .getColumnIndex(MediaStore.Images.Media.DATA));  
                      
                    //��ȡ��ͼƬ�ĸ�·����  
                    String parentName = new File(path).getParentFile().getName();                        
                    //���ݸ�·������ͼƬ���뵽mGruopMap��  
                    if(mGruopMap.containsKey(parentName)){
                    	mGruopMap.get(parentName).add(path);
                    }else{
                    	List<String> list=new ArrayList<String>();
                    	list.add(path);
                    	mGruopMap.put(parentName, list);
                    }
             
                }  
                  
                //֪ͨHandlerɨ��ͼƬ���  
                handle.sendEmptyMessage(0);  
                cursor.close();  
            }  
        }.start();  
	};
	private void getImageBeans(){
		List<ImageBean> beans=new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {  
            Map.Entry<String, List<String>> entry = it.next();  
            ImageBean mImageBean = new ImageBean();  
            String key = entry.getKey();  
            List<String> value = entry.getValue();          
            mImageBean.setFolderPath(key);  
            mImageBean.setCount(value.size());  
            mImageBean.setFirstImagePath(value.get(0));//��ȡ����ĵ�һ��ͼƬ         
            beans.add(mImageBean);  
        }  
	}
	
}
