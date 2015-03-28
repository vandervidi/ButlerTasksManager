package com.vandervidi.butler.butlertaskmanager;
/**
 * This Application was developed by: Vidran Abdovich & Gal Shalit
 * and presented as a final project in Mobile Applications course
 * in Shenkar college.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;


public class NotificationActivity extends Activity{
    private GoogleMap map;
    private LatLng myLocation;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.notification_layout);

            /**
             * Google Analytics:
             */
            //Get a Tracker (should auto-report)
            ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

            //Get view references
            TextView tw_taskTitle = (TextView) findViewById(R.id.taskTitle);
            TextView tw_taskDescription = (TextView) findViewById(R.id.taskDescription);
            TextView tw_taskDate = (TextView) findViewById(R.id.taskDate);
            Button bt_wazeIt = (Button) findViewById(R.id.NavigateWithWaze);

            //Get task object from intent
            final Task task = (Task) getIntent().getSerializableExtra("taskToPresent");

            //Set View data
            tw_taskTitle.setText(task.getTitle());
            tw_taskDescription.setText(task.getDescription());
            tw_taskDate.setText("" + task.getTaskDate().get(Calendar.DAY_OF_MONTH) + "/"
                            + task.getTaskDate().get(Calendar.MONTH) + "/"
                            + task.getTaskDate().get(Calendar.YEAR)
                            + "  At: "
                            + task.getTaskDate().get(Calendar.HOUR_OF_DAY) + ":"
                            + getProperMonthStructure(task.getTaskDate())
            );

            if (task.getLat() == 0 && task.getLng() == 0) {
                //Hide all map and navigation views

                LinearLayout navigationWrapper = (LinearLayout)findViewById(R.id.navigationWrapper);
                navigationWrapper.setVisibility(View.GONE);

            } else {

                myLocation = new LatLng(task.getLat(), task.getLng());
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                Marker hamburg = map.addMarker(new MarkerOptions().position(myLocation)
                                .title(task.getTitle())
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_launcher))
                                .snippet(task.getDescription())
                );


                // Move the camera instantly to hamburg with a zoom of 15.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                bt_wazeIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String url = "waze://?ll=" + task.getLat() + "," + task.getLng() + "&z=10";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                            startActivity(intent);
                        }

                    }
                });
            }
        }


        public String getProperMonthStructure(Calendar c){
        int minutes = c.get(Calendar.MINUTE);
            if(minutes<10) {
                return "0" + minutes;
            }
                else return ""+minutes;
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



