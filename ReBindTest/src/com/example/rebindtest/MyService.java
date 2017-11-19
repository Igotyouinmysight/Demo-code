package com.example.rebindtest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	private static final String TAG = "wcc";
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.e(TAG, "onUnbind");
		return true;
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.e(TAG, "onRebind");
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}
}
