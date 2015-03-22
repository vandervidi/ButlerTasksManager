package com.vandervidi.butler.butlertaskmanager.service;

import java.util.Calendar;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
	
	public void setAlarm(Calendar myCal){
	
		System.out.println("mycal SetAlarm");
		new AlarmTask(this,myCal).run();
	}
	

}
