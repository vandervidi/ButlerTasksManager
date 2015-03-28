package com.vandervidi.butler.butlertaskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Map extends Activity implements OnMapReadyCallback {
    private static final String LOG_TAG = "Map Activity";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyALTKw5LF9W1XoLhuSWRZ1YDo8vg0JfsVg";
    private static double currSelectedLat;
    private static double currSelectedLng;
    private String intentType;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private LatLngPointSerializable latLngSerial;
    private Intent i;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        i = getIntent();
        bundle = i.getExtras();

        //Reference to Set Location button
        Button setLocation = (Button) findViewById(R.id.bt_setLocation);
        //Reference to AutoComplete map search field
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocation);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_autocomplete));

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int position, long id) {
                String chosenAddress = (String) adapterView.getItemAtPosition(position);

                selectedLocation = getLocationFromAddress(chosenAddress);
                currSelectedLat = selectedLocation.latitude;
                currSelectedLng = selectedLocation.longitude;

                googleMap.clear();
                // create marker
                MarkerOptions marker = new MarkerOptions().position(selectedLocation).title(i.getStringExtra("taskTitle"));

                // adding marker
                googleMap.addMarker(marker);

                //show location on map
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation,13));
            }
        });

        try {
            // Loading map
            initializeMap();


        } catch (Exception e) {
            e.printStackTrace();
        }


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                googleMap.clear();

                // create marker
                MarkerOptions marker = new MarkerOptions().position(point).title(i.getStringExtra("taskTitle"));
                currSelectedLat = point.latitude;
                currSelectedLng = point.longitude;

                // adding marker
                googleMap.addMarker(marker);
            }
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                latLngSerial = new LatLngPointSerializable(currSelectedLat, currSelectedLng);
                Toast.makeText(getApplicationContext(),
                        "setResult: Lat: "+ latLngSerial.getLat() + "lng: "+ latLngSerial.getLng(), Toast.LENGTH_SHORT)
                        .show();

                if((bundle.getString("requestCode")).equals("REQUEST_LOCATION")){
                          Intent backToAddNewTask= new Intent(Map.this , EditActivity.class);
                          backToAddNewTask.putExtra("latlng", latLngSerial);
                          setResult(RESULT_OK , backToAddNewTask);
                          finish();
                }else if((bundle.getString("requestCode")).equals("REQUEST_LOCATION_UPDATE")){
                          Intent backToAddNewTask= new Intent(Map.this , AddNewTask.class);
                          backToAddNewTask.putExtra("latlng", latLngSerial);
                          setResult(RESULT_OK , backToAddNewTask);
                          finish();
                }

                Intent backToAddNewTask= new Intent(Map.this , AddNewTask.class);
                backToAddNewTask.putExtra("latlng", latLngSerial);
                setResult(RESULT_OK , backToAddNewTask);
                finish();
            }
        });
    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }


    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO Auto-generated method stub
        Location myLocation = getMyLocation();
        if(myLocation==null)
            return;

        if((bundle.getString("requestCode")).equals("REQUEST_LOCATION")) {
            LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 11));

        }else if((bundle.getString("requestCode")).equals("REQUEST_LOCATION_UPDATE")){
            latLngSerial = (LatLngPointSerializable)bundle.getSerializable("taskLatLanSerializable");
            LatLng myLatLng = new LatLng(latLngSerial.getLat(), latLngSerial.getLng());

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 11));
            MarkerOptions marker = new MarkerOptions().position(myLatLng).title(i.getStringExtra("taskTitle"));
            googleMap.addMarker(marker);
        }

        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to show map. Did you turn your GPS on??", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    @Override
    public void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

}