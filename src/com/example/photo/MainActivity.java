package com.example.photo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.example.photo.ImageHandlerThread.BmObj;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	public static final int SOURCE_REQ_CODE = 1;
	public static final int KEY_REQ_CODE = 2;
	public static final int DES_REQ_CODE = 3;
	
	public static final int INIT_IMAGE = 1;
	public static final int SOURCE_IMAGE = 2;
	public static final int KEY_IMAGE = 3;
	public static final int ENCRYPT_DONE = 4;

	
	private String sourceFile = "";
	private String keyFile = "";
	private String desFile = "";
	
	private ImageView ivSource;
	private ImageView ivKey;
	private ImageView ivDes;
	
	private boolean isSourceReady = false;
	private boolean isKeyReady = false;
	
	private Handler imageHandler = null;

	//MainHandler mainHandler = new MainHandler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ivSource = (ImageView)findViewById(R.id.imageSource);
		ivKey = (ImageView)findViewById(R.id.imageKey);
		ivDes = (ImageView)findViewById(R.id.imageDes);
		ivSource.setOnClickListener(this);
		ivKey.setOnClickListener(this);
		imageHandler = ImageHandlerThread.stratImageHandlerThread(mainHandler, this);
		imageHandler.sendEmptyMessage(ImageHandlerThread.INIT_IMAGE);
	}
	
	private void initImage() {
		InputStream sourceImg = null;
		InputStream keyImg = null;
		try {
			sourceImg=getAssets().open("source_default.png");
			
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
			try {
				sourceImg.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			keyImg=getAssets().open("key_default.png");
			
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
			try {
				keyImg.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.imageSource:
			/*intent.setClass(this, ListViewActivity.class);
			intent.putExtra("path", "/");
			startActivity(intent);*/
			
			intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
            startActivityForResult(intent, SOURCE_REQ_CODE);
			break;
			
		case R.id.imageKey:
			intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
            startActivityForResult(intent, KEY_REQ_CODE);
			break;
//		case R.id.btnDes:
//			intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("*/*");
//			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			startActivity(intent);
//			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri uri = null;
		Bitmap bm = null;
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK) {
			return;
		}
		switch(requestCode) {
		case SOURCE_REQ_CODE:
			uri = data.getData();
			sourceFile = getAbsoluteImagePath(uri);
			Message.obtain(imageHandler, ImageHandlerThread.SOURCE_FILE, sourceFile).sendToTarget();
			
			break;
		case KEY_REQ_CODE:
			uri = data.getData();
			keyFile = getAbsoluteImagePath(uri);
			Message.obtain(imageHandler, ImageHandlerThread.KEY_FILE, keyFile).sendToTarget();
			break;
		case DES_REQ_CODE:
			break;
		default:
			break;
		}
	}
	
	
	
	 Handler mainHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case INIT_IMAGE:
				Log.i("LCW","init");
				BmObj bmObj = (BmObj)msg.obj;
				ivSource.setImageBitmap(bmObj.sourceBm); 
				ivKey.setImageBitmap(bmObj.keyBm); 
				break;
			case SOURCE_IMAGE:
				ivSource.setImageBitmap((Bitmap)msg.obj);
				isSourceReady = true;
				if(isKeyReady) {
					Message.obtain(imageHandler, ImageHandlerThread.ENCRYPT).sendToTarget();
				}
				break;
			case KEY_IMAGE:
				ivKey.setImageBitmap((Bitmap)msg.obj); 
				isKeyReady = true;
				if(isSourceReady) {
					Message.obtain(imageHandler, ImageHandlerThread.ENCRYPT).sendToTarget();
				}
				break;
			case ENCRYPT_DONE:
				ivDes.setImageBitmap((Bitmap)msg.obj);
				break;
			default:
				break;
			}
		}
		
	};
	
	
	
	protected String getAbsoluteImagePath(Uri uri) {
		Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

}
