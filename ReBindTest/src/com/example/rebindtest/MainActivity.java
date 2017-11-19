package com.example.rebindtest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button btn_start;
	private Button btn_bind;
	private Button btn_unBind;
	private Intent intent;
	private MyServiceConnection conn;
	private Button btn_stop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	public void initView() {
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_bind = (Button) findViewById(R.id.btn_bind);
		btn_unBind = (Button) findViewById(R.id.btn_unBind);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		
		btn_start.setOnClickListener(this);
		btn_bind.setOnClickListener(this);
		btn_unBind.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		
		intent = new Intent(this, MyService.class);
		conn = new MyServiceConnection();
	}
	private int flag = 0;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//启动服务
		case R.id.btn_start:
			startService(intent);
			break;
		//停止服务	
		case R.id.btn_stop:
			stopService(intent);
			break;
		//绑定服务	
		case R.id.btn_bind:
			if (++ flag > 1) {
				Log.e("wcc", "再次绑定服务...");
			}
			bindService(intent, conn, Context.BIND_AUTO_CREATE);
			
			break;
		//解绑服务	
		case R.id.btn_unBind:
			unbindService(conn);
			break;

		default:
			break;
		}
	}
	
	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}
	
}
