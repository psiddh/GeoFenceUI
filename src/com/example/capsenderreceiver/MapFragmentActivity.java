package com.example.capsenderreceiver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Filter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.Toast;
public class MapFragmentActivity extends Activity implements OnItemClickListener {
	    private static final String TAG = "MapFragmentActivity > ";
	    GoogleMap googleMap;
	    
	    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	    private static final String OUT_JSON = "/json";
	    private static final String DISTANCE_IN_METERS = "482803"; // 300 miles
	    private static final String SANFRAN = "37.787930,-122.4074990";
	    private static final String LOCATION = "?location=" + SANFRAN;
	    private static final String RADIUS   = "&radius="+ DISTANCE_IN_METERS;
	    
	    private double mUserSelectedLatitude = 0.0;
	    private double mUserSelectedLongitude = 0.0;
	    private double  mRadius = 0.0;
	    
	    private boolean mShowMenuDoneIcon = false;

	    private static final String API_KEY = "AIzaSyC-184af5Y_c7hkV9HEIzT3Q_46KD_qxBg";
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.mapfragactivity);
	        getActionBar().setHomeButtonEnabled(true);
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        // Getting Google Play availability status
	        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        	Location mCurrentLocation = null;
            final LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            
            AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
            autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
            autoCompView.bringToFront();
            autoCompView.setOnItemClickListener((android.widget.AdapterView.OnItemClickListener) this);


	        // Showing status
	        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
	 
	            int requestCode = 10;
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	            dialog.show();
	 
	        } else { // Google Play Services are available
	 
	            // Getting reference to the SupportMapFragment of activity_main.xml
	            MapFragment fm =  (MapFragment) getFragmentManager().findFragmentById(R.id.map);
	 
	            // Getting GoogleMap object from the fragment
	            googleMap = fm.getMap();
	            mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        	if (mCurrentLocation != null)
	            {
	            	googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
	                       new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 12));

	                CameraPosition cameraPosition = new CameraPosition.Builder()
	                .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))      // Sets the center of the map to location user
	                .zoom(12)                   // Sets the zoom
	                .bearing(90)                // Sets the orientation of the camera to east
	                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
	                .build();                   // Creates a CameraPosition from the builder
	                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	                
	                Marker marker = googleMap.addMarker(new MarkerOptions()
	                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
	                .title("Current location"));
	                mUserSelectedLatitude = mCurrentLocation.getLatitude();
	                mUserSelectedLongitude = mCurrentLocation.getLongitude();
	            		            	
	            }
	           
	        }
	        
	        googleMap.setOnMapClickListener(new OnMapClickListener() {
		    	 
	            @Override
	            public void onMapClick(LatLng point) {
	            	float[] results = new float[1];
	            	googleMap.clear();
	            	Location oldLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	            	//mUserSelectedLatitude = oldLoc.getLatitude();
	            	//mUserSelectedLongitude = oldLoc.getLongitude();
	            	
	            	
	            	Marker marker = googleMap.addMarker(new MarkerOptions()
	                .position(new LatLng(mUserSelectedLatitude, mUserSelectedLongitude))
	                .title("Current location After selection"));
	            	Location.distanceBetween(mUserSelectedLatitude, mUserSelectedLongitude,
	            			point.latitude, point.longitude, results);
	            	double radius = mRadius = Math.ceil(results[0] / 1609.34D) + 1;  // 1609.3 mts = 1 mile
	            	marker.setTitle("Radius : About " + radius + " miles set");
	            	Toast.makeText(getBaseContext(), "Geofence radius is set to about ~ " + radius + " miles", Toast.LENGTH_SHORT).show();
	            	googleMap.addCircle(new CircleOptions()
	                .center(new LatLng(mUserSelectedLatitude, mUserSelectedLongitude))
	                .radius(results[0])
	                .strokeColor(Color.rgb(247, 145, 55))
	                .fillColor(Color.rgb(23, 137, 155)));
	            	mShowMenuDoneIcon = true;
	            	invalidateOptionsMenu();
	            }
	        });
    }
	    
    private boolean floatingpointAlmostEqual(Double p1[], Double p2[], Double EPSILON) {
        return ((Math.abs(p1[0] - p1[1]) < EPSILON) && (Math.abs(p2[0] - p2[1]) < EPSILON));
    }
	    
    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
 
        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);
    }
	 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	if (mShowMenuDoneIcon) {
    		getMenuInflater().inflate(R.menu.menu_map, menu);
            MenuItem item = menu.findItem(R.id.checkbox);
            item.setVisible(true);
            return true;
    	}
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
	    switch(item.getItemId()) {
		    case android.R.id.home:
		        onBackPressed();
		        break;
		    case R.id.checkbox:
		        // send the result back
		    	Intent data = new Intent();
		    	String msg = getAddress(mUserSelectedLatitude, mUserSelectedLongitude);
		    	if (mRadius > 0) {
		    		msg = "Coupon can be redeemed within " + mRadius + " miles radius in and around " + msg;
		    	} else {
		    		msg = "Coupon can be redeemed in and around " + msg;
		    	}
		    	setResult(RESULT_OK, data);
		    	data.putExtra("lat", mUserSelectedLatitude);
		    	data.putExtra("lng", mUserSelectedLongitude);
		    	data.putExtra("radius", mRadius);
		    	data.putExtra("title", msg);
		    	finish();
		    	break;
	    }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:us");
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
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
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
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    
    public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
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
        public 	Filter getFilter() {
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
                }

			};
            return filter;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		  String str = (String) parent.getItemAtPosition(position);
		  
		  Geocoder coder = new Geocoder(this);
		    try {
		        ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(str, 10);
		        for(Address add : adresses){
		                double longitude = mUserSelectedLongitude = add.getLongitude();
		                double latitude = mUserSelectedLatitude = add.getLatitude();
		                googleMap.clear();
		                if(longitude != 0 && latitude != 0) {
		                	googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
		 	                       new LatLng(latitude, longitude), 12));

		 	                CameraPosition cameraPosition = new CameraPosition.Builder()
		 	                .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
		 	                .zoom(12)                   // Sets the zoom
		 	                .bearing(90)                // Sets the orientation of the camera to east
		 	                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
		 	                .build();                   // Creates a CameraPosition from the builder
		 	                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		 	                
		 	                Marker marker = googleMap.addMarker(new MarkerOptions()
		 	                .position(new LatLng(latitude, longitude))
		 	                .title(str));
		 	                mShowMenuDoneIcon = true;
                            invalidateOptionsMenu();
		 	                break;
		                }
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch(IllegalArgumentException e){
		        e.printStackTrace();
		    }
          Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		
	}
	
	public String getAddress(double lat, double lng) {
	    Geocoder geocoder = new Geocoder(MapFragmentActivity.this, Locale.getDefault());
	    try {
	        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
	        Address obj = addresses.get(0);
	        
	        return obj.getLocality();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	    }
		return null;
	}
}


	 