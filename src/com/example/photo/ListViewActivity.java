package com.example.photo;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewActivity extends Activity {
	
	public ListView mListView;
	private Intent mIntent;
	private String mPath = "";
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_activity);
		mIntent = this.getIntent();
		mContext = this;
		mPath = mIntent.getStringExtra("path");
		final String[] fileList = getFileList(mPath);
		mListView = (ListView)findViewById(R.id.filelistview);
		mListView.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1,fileList));
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, fileList[arg2], Toast.LENGTH_LONG).show();
				String[] fileList1 = getFileList(fileList[arg2]);
				mListView.setAdapter(new ArrayAdapter<String>(mContext, 
						android.R.layout.simple_list_item_1,fileList1));
			}
			
		});
	}
	
	private String[] getFileList(String path) {
		File dir = new File(path);
		if(dir.isDirectory()) {
			String[] nameList = dir.list();
			return nameList;
		} else {
			
		}
		return null;
	}

}
