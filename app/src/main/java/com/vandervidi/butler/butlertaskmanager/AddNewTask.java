package com.vandervidi.butler.butlertaskmanager;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vandervidi.butler.butlertaskmanager.service.ScheduleClient;

import java.util.Calendar;

public class AddNewTask extends ActionBarActivity {
    private static final String LOG_TAG = "Add New Task Activity";
    static final int REQUEST_LOCATION = 1;  // Request code for location

	DBAdapter mydb;
	String date,time;
	public LatLngPointSerializable latLngSerial = null ;
    Calendar alertTime = Calendar.getInstance();
    private ScheduleClient scheduleClient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_task);
        scheduleClient = new ScheduleClient(this);
        scheduleClient.bindService();

		// Setting references to view components.
        final TextView twDescription = (TextView) findViewById(R.id.taskDescription);
        final TextView twTitle = (TextView) findViewById(R.id.taskTitle);
        final Button dateView = (Button)findViewById(R.id.chooseDate);
            final Button timeView = (Button)findViewById(R.id.chooseHour);
            final Button setLocation = (Button) findViewById(R.id.setLocation);
            final Button btAddNewTask = (Button) findViewById(R.id.addToDB);
        final Button bt_map = (Button) findViewById(R.id.goToMap);

        // Set location button click listener
        bt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewTask.this,Map.class);
                intent.putExtra("requestCode" , "REQUEST_LOCATION");
                //Send via intent the
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });


		// Open DB connection.
		openDB();

		// 'Add new task' button onCLickListener
		btAddNewTask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String s_taskTitle = twTitle.getText().toString();
				String s_taskDescription = twDescription.getText().toString();
                String s_datePicked[] = (dateView.getText().toString()).split("/");
                String s_timePicked[] = (timeView.getText().toString()).split(":");
                if(s_taskTitle.equals("") || s_taskDescription.equals("") || s_datePicked.length==1 || s_timePicked.length==1) {
                    Toast.makeText(getApplicationContext(), "You must enter Title, Date and Time", Toast.LENGTH_LONG).show();

                }else {
                    if (latLngSerial != null) {
                        mydb.insertRow(s_taskTitle, s_taskDescription, date, time, latLngSerial.getLat(), latLngSerial.getLng());
                        Toast.makeText(getApplicationContext(), "Created a new task", Toast.LENGTH_LONG).show();

                    } else {
                        mydb.insertRow(s_taskTitle, s_taskDescription, date, time, 0.0, 0.0);
                        Toast.makeText(getApplicationContext(), "Created a new task", Toast.LENGTH_LONG).show();
                    }
                    scheduleClient.setNotification(alertTime, s_taskTitle);

                    Intent intent = new Intent(AddNewTask.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
			}
		});

	}

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOCATION) {
            if(resultCode == RESULT_OK){
                latLngSerial  = (LatLngPointSerializable) data.getSerializableExtra("latlng");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
			return new TimePickerDialog(this, mTimeSetListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE) , true);
		return null;
	}

	

	public void setHour(View v) {
		showDialog(0);
	}

	public void setDate(View v) {

		showDialog(1);

	}

}
