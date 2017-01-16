package com.sar2016.panczuk.monstersgo;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.CollationElementIterator;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, CatchedFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private CollationElementIterator mLatitudeText;
    private Circle circle;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private Marker playerMarker;

    private static final String TAG_MAP = "map";
    private static final String TAG_CATCHED = "catched";
    private static final String TAG_PROFILE = "profile";
    public static String CURRENT_TAG = TAG_MAP;

    private Handler mHandler;
    private SupportMapFragment mapFragment;
    private FloatingActionButton fab;
    //private Fragment catchedFragment = new CatchedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment = SupportMapFragment.newInstance();
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        mapFragment.getMapAsync(this);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mHandler = new Handler();

        if(savedInstanceState == null){
            CURRENT_TAG = TAG_MAP;
            FragmentManager sfm = getSupportFragmentManager();
            sfm.beginTransaction().add(R.id.map, mapFragment).commit();
            //loadFragment();
        }
    }

    private void loadFragment(){

        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null){
            return;
        }

        Runnable mPendingRunnable = new Runnable(){

            @Override
            public void run() {
                Fragment fragment = getCurrentFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.container, fragment, CURRENT_TAG);
                fragmentTransaction.commit();
            }
        };

        if(mPendingRunnable != null){
            mHandler.post(mPendingRunnable);
        }
    }

    private Fragment getCurrentFragment(){
        if(CURRENT_TAG == TAG_CATCHED) {
            CatchedFragment ctachedFragment = new CatchedFragment();
            return ctachedFragment;
        }else if(CURRENT_TAG == TAG_PROFILE) {
            ProfileFragment profileFragment = new ProfileFragment();
            return profileFragment;
        }else{
            return mapFragment;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pause_music) {
            return true;
        }else if(id == R.id.action_play_music){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager sfm = getSupportFragmentManager();

        if(mapFragment.isAdded()){
            sfm.beginTransaction().hide(mapFragment).commit();
        }

        Toast toast = null;
        if (id == R.id.nav_catched) {
            this.CURRENT_TAG = this.TAG_CATCHED;
            this.loadFragment();
            fab.hide();
        } else if (id == R.id.nav_map) {
            this.CURRENT_TAG = this.TAG_MAP;

            fab.show();

            if(!mapFragment.isAdded())
                sfm.beginTransaction().add(R.id.map, mapFragment).commit();
            else
                sfm.beginTransaction().show(mapFragment).commit();

        } else if (id == R.id.nav_profile) {
            this.loadFragment();
            fab.hide();
            this.CURRENT_TAG = this.TAG_PROFILE;
            this.loadFragment();
            toast = Toast.makeText(getBaseContext(), "Profile", Toast.LENGTH_LONG);
        }

        if(toast != null)
            toast.show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        fab = (FloatingActionButton)findViewById(R.id.fab);
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(false);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("Styling", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Styling", "Can't find style. Error: ", e);
        }
        LatLng paris = new LatLng(0,0);
        if(mCurrentLocation != null) {
            paris = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(paris)      // Sets the center of the map to Mountain View
                .zoom(18)              // Sets the orientation of the camera to east
                .tilt(85)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //circleOptions.zIndex(1);
        circle =  mMap.addCircle(new CircleOptions()
        .center(paris)
        .radius(175)
        .fillColor(R.color.colorPrimary)
        );
        playerMarker = mMap.addMarker(new MarkerOptions().position(paris).title("Player !"));

        generateMonsters();
    }

    private void generateMonsters() {
        String jsonStr = "";
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.sanisettesparis2011);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            jsonStr = new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonStr != null) {
            try {
                JSONArray sanisettes = new JSONArray(jsonStr);

                // Getting JSON Array node
                //JSONArray sanisettes = jsonObj.getJSONArray("records");

                // looping through All Contacts
                for (int i = 0; i < sanisettes.length(); i++) {
                    JSONObject c = sanisettes.getJSONObject(i).getJSONObject("fields");

                    String id = c.getString("objectid");
                    JSONArray latLngVals = c.getJSONArray("geom_x_y");
                    double latVal = latLngVals.getDouble(0);
                    double lngVal = latLngVals.getDouble(1);
                    LatLng latlng = new LatLng(latVal, lngVal);
                    mMap.addMarker(new MarkerOptions().position(latlng).title(id));

                }
            } catch (final JSONException e) {
                Log.e("JSON", "Json parsing error: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3);
         LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            
            LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
            playerMarker.setPosition(newLoc);
            circle.setCenter(newLoc);
            CameraPosition oldCameraPos = mMap.getCameraPosition();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newLoc)      // Sets the center of the map to Mountain View
                    .zoom(oldCameraPos.zoom)                   // Sets the zoom
                    .bearing(oldCameraPos.bearing)                // Sets the orientation of the camera to east
                    .tilt(oldCameraPos.tilt)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
