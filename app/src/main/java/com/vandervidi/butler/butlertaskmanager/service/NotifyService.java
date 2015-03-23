package com.vandervidi.butler.butlertaskmanager.service;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.vandervidi.butler.butlertaskmanager.NotificationActivity;

public class NotifyService extends Service{

	int mId;
	private static final int NOTIFICATION=0;
	public static final String INTENT_NOTIFY = "com.vandervidi.butler.butlertaskmanager.service.INTENT_NOTIFY";
	private NotificationManager mNM;
	private final IBinder mBinder = new ServiceBinder();
	
	public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }
	@Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
 
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
         
        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false)) {
           String testString =  intent.getStringExtra("someText");
            showNotification(testString);
        }
        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	 private void showNotification(String taskTitle) {
	        // This is the 'title' of the notification
	        CharSequence title = "myButler : Task reminder";
	        // This is the icon to use on the notification
	        int icon = R.drawable.ic_dialog_info;
	        // This is the scrolling text of the notification
	        CharSequence text = taskTitle;
	        // What time to show on the notification
	        long time = System.currentTimeMillis();
	         
	        Notification notification = new Notification(icon, text, time);
	 
	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NotificationActivity.class), 0);
	 
	        // Set the info for the views that show in the notification panel.
	        notification.setLatestEventInfo(this, title, text, contentIntent);
	 
	        // Clear the notification when it is pressed
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	         
	        // Send the notification to the system.
	        mNM.notify(NOTIFICATION, notification);
	         
	        // Stop the service when we are finished
	        stopSelf();
	    }
 

}
