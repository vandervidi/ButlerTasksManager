package com.vandervidi.butler.butlertaskmanager;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class EditActivity extends Activity {
	DBAdapter mydb;

	//static final int dialog_id = 1;
	String date;
	String time;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);
		//Save task instance from an intent
		final Task task = (Task)getIntent().getSerializableExtra("taskToEdit"); 
		
		// Setting references to view components and setting its value according to the task recieved
		final TextView twDescription = (TextView) findViewById(R.id.taskDescription);
		final TextView twTitle = (TextView) findViewById(R.id.taskTitle);


		twTitle.setText(task.getTitle());
		twDescription.setText(task.getDescription());
		Button btEditTask = (Button) findViewById(R.id.updateBt);
		Calendar tmpCalendar = task.getTaskDate();

		Log.i("test time",""+tmpCalendar.get(Calendar.HOUR_OF_DAY));

		// Open DB connection.
		openDB();

		// 'Edit task' button onCLickListener
		btEditTask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String s_taskTitle = twTitle.getText().toString();
				String s_taskDescription = twDescription.getText().toString();
				mydb.updateRow(task.getId(), twTitle.getText().toString(), twDescription.getText().toString(), date, time);
				
				finish();
				Intent intent = new Intent(EditActivity.this, MainActivity.class);
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
			date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
            Button b1 = (Button)findViewById(R.id.chooseDate);
            b1.setText(date);

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
            Button b1 = (Button)findViewById(R.id.chooseHour);
            b1.setText(time);
		}
	};
		
	
	//dialog callback
	protected Dialog onCreateDialog(int id) {

		if (id == 1)
			return new DatePickerDialog(this, mDateSetListener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		if (id == 0)
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
