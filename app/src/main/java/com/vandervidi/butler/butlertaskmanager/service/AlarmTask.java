package com.vandervidi.butler.butlertaskmanager.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmTask implements Runnable{

	 private final Calendar date;
	 private final AlarmManager am;
	 private final Context context;
     private String notificationTasktitle;
	 
	 public AlarmTask(Context context, Calendar date, String s_taskTitle) {
	        this.context = context;
	        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        this.date = date;
            this.notificationTasktitle = s_taskTitle;
	 }


    @Override
    public void run() {
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("taskTitle", notificationTasktitle);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
	    
}
