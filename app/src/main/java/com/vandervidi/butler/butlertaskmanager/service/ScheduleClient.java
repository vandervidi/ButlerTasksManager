package com.vandervidi.butler.butlertaskmanager.service;

import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


public class ScheduleClient {

	private ScheduleService mBoundService;
	private Context mContext;
	private boolean mIsBound;
	
	public ScheduleClient(Context context) {
	        mContext = context;
	}
	
	public void bindService(){
		mContext.bindService(new Intent(mContext, ScheduleService.class),mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// disconnecting from the service
			mBoundService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// when we have connetion with the service
			mBoundService = ((ScheduleService.ServiceBinder)service).getService();
			
		}
	};
	
	  public void setNotification(Calendar myCal){
		  //creating an alarm 
		 
		  mBoundService.setAlarm(myCal);
	  }
	  public void unBindService(){
		  if(mIsBound){
			  mContext.unbindService(mConnection);
			  mIsBound = false;
		  }
	  }
}
