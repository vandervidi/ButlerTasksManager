package com.vandervidi.butler.butlertaskmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

public class ScheduleService extends Service{
	private final IBinder mBinder = new ServiceBinder();
	public class ServiceBinder extends Binder{
		ScheduleService getService(){
			return ScheduleService.this;
		}
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
	         
	        return START_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public void setAlarm(Calendar myCal, String s_taskTitle){

		new AlarmTask(this,myCal, s_taskTitle).run();
	}
	

}
