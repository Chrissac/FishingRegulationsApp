package com.regulationfishing.csacripante.fishingregulationsapp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;
import com.facebook.internal.ImageRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.helper.users;
import prefs.CommonFunctions;
import prefs.GeoMappingsList;
import prefs.GetGeoLocations;
import prefs.LoginRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import static prefs.CommonFunctions.PassWordMd5;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        OnItemClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {

    GoogleMap gMap;
    Context context;
    //permission settings
    static final int REQUEST_FINE_LOCATION = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_COARSE_LOCATION = 2;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String TAG = MapsActivity.class.getSimpleName();
    DatabaseHelper db;

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD7bf9O7_kTD1BuNVM9X4Zk5OKTfvAJRq0";
    private  static  ArrayList locationPlaces = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        db = new DatabaseHelper(getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.textViewName);
        TextView navEmail = (TextView) headerView.findViewById(R.id.textViewEmail);
        ImageView ProfilePic = (ImageView) headerView.findViewById(R.id.userProfilePic);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(this);

        List<users> allUsers =    db.getAllUsers();
        for (users userObj : allUsers) {
            String userNameBeforeDash = userObj.getUserName().toString().split("-")[0];
            navUsername.setText(userNameBeforeDash);
            navEmail.setText(userObj.getEmail().toString());

            Uri profilePictureUri = ImageRequest.getProfilePictureUri(Profile.getCurrentProfile().getId(), 120 , 120 );

            File fileLocation = new File(String.valueOf(profilePictureUri)); //file path, which can be String, or Uri

            Picasso.with(this)
                    .load(profilePictureUri) //extract as User instance method
                    .resize(120, 120)
                    .transform(new CircleTransformation())
                    .into(ProfilePic);
        }
        LoadGeoLocations();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public  void LoadGeoLocations()
    {
        // Response received from the server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr = new JSONArray(response);//Response string is here
                    final List<GeoMappingsList> myList=new ArrayList<GeoMappingsList>();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONArray arr2 = arr.optJSONArray(i);
                        GeoMappingsList newItem;
                        newItem = new GeoMappingsList(Integer.parseInt(arr2.get(0).toString()),
                                                                      arr2.get(1).toString(),
                                                                      arr2.get(2).toString(),
                                                                      arr2.get(3).toString(),
                                                                      dateFormat.parse(arr2.get(4).toString()),
                                                                      dateFormat.parse(arr2.get(5).toString()),
                                                                      Float.parseFloat(arr2.get(6).toString()),
                                                                      Float.parseFloat(arr2.get(7).toString()),
                                                                      Integer.parseInt(arr2.get(8).toString()));
                        myList.add(newItem);
                    }
                    //create a new thread to populate polygons
//                    Thread t1 = new Thread(new Runnable() {
//                        public void run()
//                        {
//                            for (int i = 0; i < myList.size(); i++) {
//
//                            }
//                        }});
//                    t1.start();

                    Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .add(
                                    new LatLng(myList.get(0).Latitude, myList.get(0).Longitude),
                                    new LatLng(myList.get(1).Latitude, myList.get(1).Longitude),
                                    new LatLng(myList.get(2).Latitude, myList.get(2).Longitude)
                            ));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        GetGeoLocations geoRequest = new GetGeoLocations(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(geoRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.string.style_json));

            if (!success) {

            }
        } catch (Resources.NotFoundException e) {

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapViewMain)).getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(final GoogleMap googleMap) {

                    gMap = googleMap;
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

                    googleMap.setMyLocationEnabled(true);


                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(-33.87365, 151.20689))
                            .title("Sydney")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fishingpole))
                            .snippet("Population: dont know google it."));

                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(-27.47093, 153.0235))
                            .title("Sydney")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fishingpole))
                            .snippet("Population: dont know google it."));


                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                        Location userLocation = googleMap.getMyLocation();
                        LatLng myLocation = null;
                        if (userLocation != null) {
                            myLocation = new LatLng(userLocation.getLatitude(),
                                    userLocation.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                                    googleMap.getMaxZoomLevel() - 5));
                        } else {
                            // Show rationale and request permission.
                        }

                        gMap = googleMap;
                        mMap = googleMap;


                    }
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            //handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        locationPlaces = new ArrayList();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //sb.append("&components=country:gr");
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
        } catch (Exception e) {

            return resultList;
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                locationPlaces.add(predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        } catch (JSONException e) {

        }
        return resultList;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        str = (String)locationPlaces.get(position).toString();
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        CommonFunctions pl = new CommonFunctions();

        ArrayList<Double> list = new ArrayList<Double>();
        ArrayList<Double> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {


            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON);
            sb.append("?placeid=" + URLEncoder.encode(str, "utf8"));
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {

            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            //JSONArray predsJsonArray = jsonObj.getJSONArray("html_attributions");
            JSONObject result = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            Double longitude  = result.getDouble("lng");
            Double latitude =  result.getDouble("lat");
            CameraPosition camPos = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(17)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(camUpd3);
        } catch (JSONException e) {
            ///Log.e(LOG_TAG, "Cannot process JSON results", e);
        }


    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        ArrayList resultList;
        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
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

                        // Assign the data to the FilterResultsfilterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
