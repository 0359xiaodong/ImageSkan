package com.example.imageskan.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.imageskan.R;
import com.example.imageskan.adapter.MyGridAdapter;
import com.example.imageskan.domain.ImageBean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity{
	private ProgressDialog mProgressDialog;
	private GridView mGridView;
	private ArrayList<ImageBean> list;
	private HashMap<String, ArrayList<String>> mGruopMap = new HashMap<String, ArrayList<String>>(); 
	private Handler handle=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
			MyGridAdapter adapter=new MyGridAdapter(getApplicationContext(),list=getImageBeans(), mGridView);
			mGridView.setAdapter(adapter);
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initView();
		
	}
	private void initView() {
		mGridView=(GridView) findViewById(R.id.main_grid);
		mProgressDialog= ProgressDialog.show(this, null, "正在加载..."); 
		mProgressDialog.show();
		getImages();
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path=list.get(position).getFolderName();
				ArrayList<String> data=mGruopMap.get(path);
				Intent intent=new Intent(MainActivity.this,SkanActivity.class);
				intent.putExtra("data",data);
				startActivityForResult(intent,200);
			}
		});
	}
	private void getImages(){
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		new Thread(){
			@Override
			public void run() {
				Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				Cursor cursor=getContentResolver().query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "  
		                + MediaStore.Images.Media.MIME_TYPE + "=?",  
		        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				if(cursor==null){
					return;
				} while (cursor.moveToNext()) {  
                    //获取图片的路径  
                    String path = cursor.getString(cursor  
                            .getColumnIndex(MediaStore.Images.Media.DATA));  
                      
                    //获取该图片的父路径名  
                    String parentName = new File(path).getParentFile().getName();                        
                    //根据父路径名将图片放入到mGruopMap中  
                    if(mGruopMap.containsKey(parentName)){
                    	mGruopMap.get(parentName).add(path);
                    }else{
                    	ArrayList<String> list=new ArrayList<String>();
                    	list.add(path);
                    	mGruopMap.put(parentName, list);
                    }
                }  
                //通知Handler扫描图片完成  
				cursor.close();  
                handle.sendEmptyMessage(0);  
            }  
        }.start();  
	};
	private ArrayList<ImageBean> getImageBeans(){
		Iterator<Map.Entry<String, ArrayList<String>>> it = mGruopMap.entrySet().iterator();
		ArrayList<ImageBean> list = new ArrayList<ImageBean>();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			ArrayList<String> value = entry.getValue();
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
			list.add(mImageBean);
		}
		return list;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==200){			
			ArrayList<String> strs=(ArrayList<String>) data.getExtras().get("data");
			Toast.makeText(getApplicationContext(), "您选中了"+strs.size()+"个", 0).show();;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
