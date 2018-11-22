package nl.bramwinter.globus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
<<<<<<< HEAD
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
=======
>>>>>>> origin/winter-location-update-list

import nl.bramwinter.globus.fragments.ContactsFragment;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;
import nl.bramwinter.globus.fragments.NotificationsFragment;
import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;

<<<<<<< HEAD
public class OverviewActivity extends AppCompatActivity implements LocationUpdatesFragment.OnListFragmentInteractionListener, ContactsFragment.OnListFragmentInteractionListener, OnMapReadyCallback {
=======
public class OverviewActivity extends AppCompatActivity implements
        LocationUpdatesFragment.OnListFragmentInteractionListener,
        ContactsFragment.OnListFragmentInteractionListener,
        NotificationsFragment.OnListFragmentInteractionListener {
>>>>>>> origin/winter-location-update-list

    private static final String TAG = "Globus Map";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    BottomNavigationView buttonNavigationUpdate;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        // TODO: Then clicking on the 'Notification' navigation button it changes the size of the font, which means that the text cannot fit and it will get cut.

        buttonNavigationUpdate = findViewById(R.id.buttom_navigation_view);
        buttonNavigationUpdate.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getMapPermissions();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SupportMapFragment()).commit();
    }

    public void getCurrentDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found location");
                            Location currentDeviceLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentDeviceLocation.getLatitude(), currentDeviceLocation.getLongitude()), 15);
                        } else {
                            Log.d(TAG, "Could not find location");
                            Toast.makeText(OverviewActivity.this, "Unable to get get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_map:
                            SupportMapFragment fragment1 = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment); //new SupportMapFragment();
                            fragment1.getMapAsync(OverviewActivity.this);
                            fragment = fragment1;
                            break;
                        case R.id.nav_updates:
                            fragment = new LocationUpdatesFragment();
                            break;
                        case R.id.nav_notifications:
                            fragment = new NotificationsFragment();
                            break;
                        case R.id.nav_contact_list:
                            fragment = new ContactsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fragment).commit();

                    return true;
                }

            };

    @Override
    public void onLocationUpdatesFragmentInteraction(Location item) {

    }

    @Override
    public void onContactFragmentInteraction(User item) {

    }

    @Override
<<<<<<< HEAD
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getCurrentDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
    }

    private void getMapPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0) {
                    Log.d(TAG, "permission failed");
                    return;
                }
                Log.d(TAG, "permission granted");
                mLocationPermissionGranted = true;
            }
        }
=======
    public void onNotificationsFragmentInteraction(Contact item) {

>>>>>>> origin/winter-location-update-list
    }
}
