package com.example.mysimpleviewpager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mysimpleviewpager.view.SimpleViewPager;

public class MainActivity extends Activity {

	private SimpleViewPager svp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		
	}
	
	public void initView() {
		svp = (SimpleViewPager) findViewById(R.id.svp);
		
		//向SimpleViewPager添加3个宽高相同的ListView子元素
		for (int i = 0; i < 3; i++) {
			View v = View.inflate(this, R.layout.content, null);
			TextView tv = (TextView) v.findViewById(R.id.tv);
			tv.setText("页面" + (i+1));
			
			//给ListView填数据
			initListView(v);
			
			//获取手机屏幕宽度
			int widthPixels = getWindowWidth();
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(widthPixels, LayoutParams.MATCH_PARENT);
			//用java代码，将view放入SimpleViewPager中
			svp.addView(v, params);
		}
		
	}

	private int getWindowWidth() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		int widthPixels = outMetrics.widthPixels;
		return widthPixels;
	}

	private void initListView(View v) {
		ListView lv = (ListView) v.findViewById(R.id.lv);
		ArrayList<String> datas = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			datas.add("item" + i);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.lv_item, R.id.tv_item, datas);
		lv.setAdapter(adapter);
	}

}
