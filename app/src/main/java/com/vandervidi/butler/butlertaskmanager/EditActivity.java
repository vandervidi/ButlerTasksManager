package com.vandervidi.butler.butlertaskmanager;
/**
 * This Application was developed by: Vidran Abdovich & Gal Shalit
 * and presented as a final project in Mobile Applications course
 * in Shenkar college.
 */


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.Calendar;

public class EditActivity extends Activity {
    private static final String LOG_TAG = "Edit New Task Activity";
    static final int REQUEST_LOCATION_UPDATE = 2;  // Request code for location
	DBAdapter mydb;
	//static final int dialog_id = 1;
	private String date;
	private String time;
    private Task task;
    private LatLngPointSerializable latLngSerial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);

        /**
         * Google Analytics:
         */
        //Get a Tracker (should auto-report)
        ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);


		//Save task instance from an intent
		task = (Task)getIntent().getSerializableExtra("taskToEdit");

        //Save current task location cords
        latLngSerial = new LatLngPointSerializable(task.getLat(), task.getLng());

		// Setting references to view components and setting its value according to the task recieved
		final TextView twDescription = (TextView) findViewById(R.id.taskDescription);
		final TextView twTitle = (TextView) findViewById(R.id.taskTitle);
        final Button dateView = (Button)findViewById(R.id.chooseDate);
        final Button timeView = (Button)findViewById(R.id.chooseHour);
        final Button setLocation = (Button) findViewById(R.id.setLocation);
        final Button btEditTask = (Button) findViewById(R.id.updateBt);


		twTitle.setText(task.getTitle());
		twDescription.setText(task.getDescription());


		Calendar tmpCalendar = task.getTaskDate();
		dateView.setText(""+tmpCalendar.get(Calendar.DAY_OF_MONTH) + "/"+(tmpCalendar.get(Calendar.MONTH) + 1) + "/" + tmpCalendar.get(Calendar.YEAR));
		timeView.setText(""+ tmpCalendar.get(Calendar.HOUR_OF_DAY) +":" +tmpCalendar.get(Calendar.MINUTE));

		// Open DB connection.
		openDB();

        //Set/change location button click listener
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,Map.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskLatLanSerializable",latLngSerial);
                intent.putExtras(bundle);
                bundle.putString("requestCode", "REQUEST_LOCATION_UPDATE");
                bundle.putString("taskTitle", task.getTitle());
                intent.putExtras(bundle);
                //Send via intent the
                startActivityForResult(intent, REQUEST_LOCATION_UPDATE);
            }
        });

		// 'Edit task' button onCLickListener
		btEditTask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                //Get reference to view components
				String s_taskTitle = twTitle.getText().toString();
				String s_taskDescription = twDescription.getText().toString();

                String s_datePicked = dateView.getText().toString();
                String s_timePicked = timeView.getText().toString();
                if(s_taskTitle.equals("") || s_taskDescription.equals("") || s_datePicked.equals("") || s_timePicked.equals("")) {
                    Toast.makeText(getApplicationContext(), "You must enter Title, Date and Time", Toast.LENGTH_LONG).show();

                }else {
                    //Update database
                    mydb.updateRow(task.getId(), twTitle.getText().toString(), twDescription.getText().toString(), dateView.getText().toString(), timeView.getText().toString(), latLngSerial.getLat(), latLngSerial.getLng());
                    Toast.makeText(getApplicationContext(), "Task is updated!", Toast.LENGTH_LONG).show();
                    //Close this activity and move to main activity
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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
            return new DatePickerDialog(this, mDateSetListener, task.getTaskDate().get(Calendar.YEAR), task.getTaskDate().get(Calendar.MONTH), task.getTaskDate().get(Calendar.DAY_OF_MONTH));
        if (id==0)
            return new TimePickerDialog(this, mTimeSetListener, task.getTaskDate().get(Calendar.HOUR_OF_DAY), task.getTaskDate().get(Calendar.MINUTE) , true);
        return null;
    }

	

	public void setHour(View v) {
		showDialog(0);
	}

	public void setDate(View v) {

		showDialog(1);

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOCATION_UPDATE) {
            if(resultCode == RESULT_OK){
                latLngSerial  = (LatLngPointSerializable) data.getSerializableExtra("latlng");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}
