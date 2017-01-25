package com.sar2016.panczuk.monstersgo;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, CatchedFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, CatchingFragment.OnFragmentInteractionListener, GoogleMap.OnCameraMoveListener {

    protected static Context context;
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
    private static final String TAG_CATCHING = "catching";
    private static final String TAG_PROFILE = "profile";
    public static String CURRENT_TAG = TAG_MAP;

    private Handler mHandler;
    private SupportMapFragment mapFragment;
    private FloatingActionButton fab;
    private ArrayList<Monster> monsters = new ArrayList<>();
    private Monster selectedMonster;
    private MediaPlayer mp;
    private boolean firstPos = false;

    private String loggedUser = "";

    private int basicZoomLevel = 17;
    private int basicDinoWidth = 255;
    private int basicDinoHeight = 100;
    private int basicRangerWidth = 53;
    private int basicRangerHeight = 75;

    private int radius = 175;
    private List<Monster> catchedList = new ArrayList<>();
    private boolean mapReady = false;
    private Drawable loggedUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedMonster != null && getDistance(selectedMonster.getMarker().getPosition(), playerMarker.getPosition()) <= radius){
                    //Removing monster !
                    if(selectedMonster != null) {
                        catchedList.add(selectedMonster);
                        selectedMonster.setUser(loggedUser);
                        CURRENT_TAG = TAG_CATCHING;
                        loadFragment();
                    }
                }else {
                    Snackbar.make(view, "You must select a valid dino first !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        loggedUser = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);


        mapFragment = SupportMapFragment.newInstance();
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
        mHandler = new Handler();

        if (savedInstanceState == null) {
            CURRENT_TAG = TAG_MAP;
            FragmentManager sfm = getSupportFragmentManager();
            sfm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mp = MediaPlayer.create(this, R.raw.theme);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                   mp.start();
                }
            });
    }

    private void loadFragment(){
        FragmentManager sfm = getSupportFragmentManager();

        if(mapFragment.isAdded()){
            sfm.beginTransaction().hide(mapFragment).commit();
        }
        fab.hide();

        Log.d("Loading Frag", CURRENT_TAG);
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
        if(CURRENT_TAG == TAG_CATCHING) {
            CatchingFragment catchingFragment = new CatchingFragment();
            return catchingFragment;
        }else if(CURRENT_TAG == TAG_CATCHED) {
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
            if(mp.isPlaying())
                mp.pause();
            return true;
        }else if(id == R.id.action_play_music){
            mp.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager sfm = getSupportFragmentManager();

        if (id == R.id.nav_catched) {
            this.CURRENT_TAG = this.TAG_CATCHED;
            this.loadFragment();
        } else if (id == R.id.nav_map) {
            this.CURRENT_TAG = this.TAG_MAP;

            fab.show();

            if(!mapFragment.isAdded())
                sfm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.map, mapFragment).commit();
            else
                sfm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(mapFragment).commit();

        } else if (id == R.id.nav_profile) {
            this.CURRENT_TAG = this.TAG_PROFILE;
            this.loadFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        super.onStart();

        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(false);
        //mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.setOnCameraMoveListener(this);
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

        LatLng paris = new LatLng(48.858093 ,2.294694);
        if(mCurrentLocation != null) {
            paris = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(paris)      // Sets the center of the map to Mountain View
                .zoom(16.5f)              // Sets the orientation of the camera to east
                .tilt(85)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //circleOptions.zIndex(1);
        circle =  mMap.addCircle(new CircleOptions()
        .center(paris)
        .radius(this.radius)
        .fillColor(R.color.colorPrimary)
        );
        this.setLoggedUserImage(getResources().getDrawable(R.drawable.ranger));

        MarkerOptions playerMarkerOptions = new MarkerOptions().position(paris).title(this.getLoggedUser());
        BitmapDrawable drawable = (BitmapDrawable)this.getLoggedUserImage();
        playerMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(((BitmapDrawable) this.getLoggedUserImage()).getBitmap(), 53, 75)));
        playerMarker = mMap.addMarker(playerMarkerOptions);

        generateMonsters();
        mapReady = true;
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

                    Monster m = Monster.getRandomMonster();
                    m.setMarker(mMap.addMarker(new MarkerOptions().position(latlng).title(m.getName()).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(m.getImageName(),255,100)))));
                    monsters.add(m);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            for(int i =0; i < monsters.size(); i++){
                                if(monsters.get(i).getMarker().equals(marker)){
                                    if(getDistance(marker.getPosition(),playerMarker.getPosition()) <= radius) {
                                        selectedMonster = monsters.get(i);
                                    }
                                }
                            }
                            return false;
                        }
                    });
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
        startService(new Intent(this, MoveDinosService.class));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mLocationRequest.setInterval(2);
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
        if (location != null && this.mapReady) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            
            LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
            playerMarker.setPosition(newLoc);
            circle.setCenter(newLoc);
            if(!firstPos) {
                firstPos = true;
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public Bitmap resizeMapIcons(String iconName, float width, float height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, (int)width, (int)height, false);
        return resizedBitmap;
    }

    public Bitmap resizeMapIcons(Bitmap bitmap, float width, float height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, false);
        return resizedBitmap;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onCameraMove() {
        /*playerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeBasedOnZoom("ranger", basicRangerWidth, basicRangerHeight, mMap.getCameraPosition().zoom)));
        for(int i = 0; i < monsters.size(); i ++){
            monsters.get(i).marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeBasedOnZoom(monsters.get(i).imageName, basicDinoWidth, basicDinoHeight, mMap.getCameraPosition().zoom)));
        }*/
    }

    public Bitmap resizeBasedOnZoom(String iconName,int basicWidth,int basicHeight, float zoom){
        float width = basicWidth * zoom / basicZoomLevel;
        float height = basicHeight * zoom / basicZoomLevel;
        return this.resizeMapIcons(iconName, width, height);
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);

        return distance;
    }

    public List<Monster> getCatchedList() {
        return catchedList;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }

    private void insertIntoDB(Monster selectedMonster) {
        MonsterDao dao = new MonsterDao(this);
        dao.open();
        dao.insertMonster(selectedMonster);
        dao.close();
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public boolean isMapReady() {
        return mapReady;
    }

    public void setLoggedUserImage(Drawable loggedUserImage) {
        this.loggedUserImage = loggedUserImage;
        if(this.playerMarker != null){
            BitmapDrawable drawable = (BitmapDrawable)loggedUserImage;
            playerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(drawable.getBitmap(), 53, 75)));
        }
    }

    public Drawable getLoggedUserImage() {
        return loggedUserImage;
    }

    public Monster getSelectedMonster() {
        return selectedMonster;
    }

    public void monsterGotCatched(){
        insertIntoDB(selectedMonster);
        selectedMonster.marker.remove();
        selectedMonster = null;

        this.CURRENT_TAG = TAG_MAP;

        fab.show();

        if(!mapFragment.isAdded())
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.map, mapFragment).commit();
        else
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(mapFragment).commit();

    }

    public void monsterCatchingFailed(){

    }
}
