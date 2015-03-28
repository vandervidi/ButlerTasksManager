package com.vandervidi.butler.butlertaskmanager;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vandervidi.butler.butlertaskmanager.service.ScheduleClient;

import java.util.Calendar;

public class AddNewTask extends ActionBarActivity {
	DBAdapter mydb;
	String date;
	String time;
    Calendar alertTime = Calendar.getInstance();
    private ScheduleClient scheduleClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_task);
		// Setting references to view components.

		Button btAddNewTask = (Button) findViewById(R.id.addToDB);

        scheduleClient = new ScheduleClient(this);
        scheduleClient.bindService();

		// Open DB connection.
		openDB();

		// 'Add new task' button onCLickListener
		btAddNewTask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView twTitle = (TextView) findViewById(R.id.taskTitle);
				TextView twDescription = (TextView) findViewById(R.id.taskDescription);
				String s_taskTitle = twTitle.getText().toString();
				String s_taskDescription = twDescription.getText().toString();
				mydb.insertRow(s_taskTitle, s_taskDescription,date,time);
                System.out.println(alertTime);
                scheduleClient.setNotification(alertTime, s_taskTitle);


				finish();
				Intent intent = new Intent(AddNewTask.this, MainActivity.class);
				startActivity(intent);
			}
		});

	}

	private void openDB() {
		mydb = new DBAdapter(this);
		mydb.open();
	}

	// add date
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub

            alertTime.set(year,monthOfYear,dayOfMonth); // setting date to alertTime
            date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
			/*TextView dateView = (TextView)findViewById(R.id.pickedDate);
			dateView.setText(date);*/
            Button b1 = (Button)findViewById(R.id.chooseDate);
            b1.setText(date);
			//Testing
			Log.i("DatePicker", " Day: "+ dayOfMonth);
			Log.i("DatePicker", " Month: "+ monthOfYear);
			Log.i("DatePicker", " Year: "+ year);


		}
	};
	
	//add time
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			if(minute<10){
				time=hourOfDay+":0"+minute;
			}else{
				time=hourOfDay+":"+minute;
			}
			/*TextView timeView = (TextView)findViewById(R.id.pickedTime);
			timeView.setText(time);*/
            Button b1 = (Button)findViewById(R.id.chooseHour);
            b1.setText(time);

            alertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            alertTime.set(Calendar.MINUTE, minute);
            alertTime.set(Calendar.SECOND, 0);
		}
	};
		
	
	//dialog callback
	protected Dialog onCreateDialog(int id) {

		if (id == 1)
			return new DatePickerDialog(this, mDateSetListener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		if (id==0)
			return new TimePickerDialog(this, mTimeSetListener, 1/*hour*/, 1/*minute*/, true);
		return null;
	}

	

	public void setHour(View v) {
		showDialog(0);
	}

	public void setDate(View v) {

		showDialog(1);

	}

}
