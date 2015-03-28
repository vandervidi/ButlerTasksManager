package com.vandervidi.butler.butlertaskmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
//import android.widget.ExpandableListAdapter;

public class MainActivity extends ActionBarActivity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<Task>> listDataChild;
	List<Task> today = new ArrayList<Task>();
	List<Task> tomorrow = new ArrayList<Task>();
	List<Task> later = new ArrayList<Task>();
	List<Task> missed = new ArrayList<Task>();
	DBAdapter mydb;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        /**
         * Google Analytics:
         * Initialize a tracker for this activity.
         */
        //Get a Tracker (should auto-report)

        Tracker t = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        // Enable Advertising Features.
        t.enableAdvertisingIdCollection(true);


		// get 'add new task' view
		ImageButton b_addNew = (ImageButton) findViewById(R.id.imageButtonAdd);
		b_addNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AddNewTask.class);
				startActivity(intent);
			}
		});
		
		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);
		expListView.setOnItemLongClickListener(new OnItemLongClickListener() {
		      @Override
		      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Task task = null;
		          int itemType = ExpandableListView.getPackedPositionType(id);

		          if ( itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
		              int childPosition = ExpandableListView.getPackedPositionChild(id);
		              int groupPosition = ExpandableListView.getPackedPositionGroup(id);

		              //Long click on evry expandable list child
                      //get the longClicked task
		              switch(groupPosition){
		               case 0:  task = today.get(childPosition);
		            	   		break;
		               case 1:  task = tomorrow.get(childPosition);
		            	   		break;
		               case 2:  task = later.get(childPosition);
		            	   		break;
		               case 3:  task = missed.get(childPosition);
		            	   		break;
		               }

                      Intent intent = new Intent(MainActivity.this,TaskOptions.class);
                      Bundle bundle = new Bundle();
                      bundle.putSerializable("taskToEdit",task);
                      intent.putExtras(bundle);
                      startActivity(intent);
                      return true; //true if we consumed the click, false if not

		              
		              //Long click on a Expandable list main item
		          } else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
		             int  groupPosition = ExpandableListView.getPackedPositionGroup(id);
		              //do your per-group callback here
		            

		              return true; //true if we consumed the click, false if not

		          } else {
		              // null item; we don't consume the click
		              return false;
		          }
		      }
		  });
		
		
		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            	
            	Task task = null;
               switch(groupPosition){
               case 0:  task = today.get(childPosition);
            	   		break;
               case 1:  task = tomorrow.get(childPosition);
            	   		break;
               case 2:  task = later.get(childPosition);
            	   		break;
               case 3:  task = missed.get(childPosition);
            	   		break;
               }
               
               //pass the rowIdClicked
                
               Intent intent = new Intent(MainActivity.this, NotificationActivity.class);

                       Bundle bundle = new Bundle(); 
                       bundle.putSerializable("taskToPresent",task);
                       intent.putExtras(bundle); 
                       startActivity(intent);
                return true;
            }
        });
	}


    @Override
    protected void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }
    /*
     * Preparing the list data
     */
	private void prepareListData() {
		int id, tmpInt;
		String title, description, date, time;
		Calendar calendar;
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<Task>>();

		// Adding child data
		listDataHeader.add("Today");
		listDataHeader.add("Tomorrow");
		listDataHeader.add("Later");
		listDataHeader.add("Missed");

		// open Database connection
		openDB();
		Cursor cursor = mydb.getAllRows();
		// if the database table is not empty ...
		// for each row, do the following
		if (cursor.moveToFirst()) {
			do {

				Task tmpTask = new Task(cursor.getInt(DBAdapter.COL_ROWID),
						cursor.getString(DBAdapter.COL_TASKTITLE),
						cursor.getString(DBAdapter.COL_TASKDESCRIPTION),
                        cursor.getDouble(DBAdapter.COL_LAT),
                        cursor.getDouble(DBAdapter.COL_LNG));

				tmpTask.setDate(cursor.getString(DBAdapter.COL_DATE),
						cursor.getString(DBAdapter.COL_TIME));

				// check if this is this task due date is today
				tmpInt = analyzeDate(tmpTask.getTaskDate());
				switch (tmpInt) {
				case 1: 
					today.add(tmpTask);	
					break;
				case 2: 
					tomorrow.add(tmpTask);	
					break;
				case 3: 
					later.add(tmpTask);	
				break;
				case -1:
					missed.add(tmpTask);	
				break;
				}
			} while (cursor.moveToNext());
		}
		
		// close cursor and database connection
		cursor.close();
		mydb.close();

		// Header, Child data
		listDataChild.put(listDataHeader.get(0), today);
		listDataChild.put(listDataHeader.get(1), tomorrow);
		listDataChild.put(listDataHeader.get(2), later);
		listDataChild.put(listDataHeader.get(3), missed);
	}
	
	private int analyzeDate(Calendar tcal) {
		Calendar tc = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(c.DAY_OF_MONTH, Calendar.getInstance().get(Calendar. DAY_OF_MONTH));
		c.set(c.MONTH, Calendar.getInstance().get(Calendar. MONTH));
		c.set(c.YEAR, Calendar.getInstance().get(Calendar. YEAR));

		
		tc.clear();
		tc.set(tc.DAY_OF_MONTH, tcal.get(Calendar. DAY_OF_MONTH));
		tc.set(tc.MONTH, tcal.get(Calendar. MONTH));
		tc.set(tc.YEAR, tcal.get(Calendar. YEAR));

		// missed case
		if (tc.before(c)) {
			return -1;
		}
		// today case
		if (tc.equals(c)) {
			return 1;
		}
		// set current time to tomorrow
		// tomorrow case
		c.add(Calendar.DAY_OF_MONTH, 1);
		if (tc.equals(c)) {
			return 2;
		}
		//later case
		if (tc.after(c)) {
			return 3;
		}
		return 0;
	}

	private void openDB() {
		mydb = new DBAdapter(this);
		mydb.open();
	}
}