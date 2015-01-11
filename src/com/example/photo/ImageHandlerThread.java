package com.example.photo;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ImageHandlerThread extends HandlerThread {
	
	public static final int INIT_IMAGE = 1;
	public static final int SOURCE_FILE = 2;
	public static final int KEY_FILE = 3;
	public static final int ENCRYPT = 4;
	
	
	private Handler mainHandler = null;
	static Context mContext = null;
	BmObj bmObj = new BmObj();
	
	Bitmap mSourceBm = null;
	Bitmap mKeyBm = null;
	Bitmap mDesBm = null;

	public ImageHandlerThread(String name) {
		super(name);
	}
	
	public class BmObj {
		Bitmap sourceBm;
		Bitmap keyBm;
	}



	
	


	public static Handler stratImageHandlerThread(Handler handler, Context context) {
		ImageHandlerThread handlerThread = new ImageHandlerThread("ImageThread");
		handlerThread.mainHandler = handler;
		mContext = context;
		handlerThread.start();
        return handlerThread.mHandler;
	}
		
	@Override
	public void run() {
		mainHandler.obtainMessage(3).sendToTarget();
		super.run();
	}


    Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case INIT_IMAGE:
				initImage();
				Message.obtain(mainHandler, MainActivity.INIT_IMAGE, bmObj).sendToTarget();
				break;
			case SOURCE_FILE:
				mSourceBm = BitmapFactory.decodeFile((String)msg.obj); 
				Message.obtain(mainHandler, MainActivity.SOURCE_IMAGE, mSourceBm).sendToTarget();
				break;
			case KEY_FILE:
				mKeyBm = BitmapFactory.decodeFile((String)msg.obj); 
				Message.obtain(mainHandler, MainActivity.KEY_IMAGE, mKeyBm).sendToTarget();
				break;
			case ENCRYPT:
				mDesBm = mKeyBm; 
				Message.obtain(mainHandler, MainActivity.ENCRYPT_DONE, mDesBm).sendToTarget();
				break;
			default:
				break;
			}
		}
		
    };
    
	private void initImage() {
		InputStream sourceImg = null;
		InputStream keyImg = null;
		try {
			sourceImg=mContext.getAssets().open("source_default.png");
			bmObj.sourceBm = BitmapFactory.decodeStream(sourceImg); 
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
			keyImg=mContext.getAssets().open("key_default.png");
			bmObj.keyBm = BitmapFactory.decodeStream(keyImg); 
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

}
