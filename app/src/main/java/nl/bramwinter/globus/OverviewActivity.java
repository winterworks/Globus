package nl.bramwinter.globus;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import nl.bramwinter.globus.fragments.ContactsFragment;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;
import nl.bramwinter.globus.fragments.NotificationsFragment;
import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.TestModel;
import nl.bramwinter.globus.models.User;

public class OverviewActivity extends AppCompatActivity implements
        LocationUpdatesFragment.OnListFragmentInteractionListener,
        ContactsFragment.OnListFragmentInteractionListener,
        NotificationsFragment.OnListFragmentInteractionListener,
        OnMapReadyCallback {

    private static final String TAG = "Globus Map";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    BottomNavigationView buttonNavigationUpdate;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;

    private DataService dataService;
    private ServiceConnection dataServiceConnection;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_map:

                            fragment = new SupportMapFragment();
                            ((SupportMapFragment) fragment).getMapAsync(OverviewActivity.this);
                            break;
                        case R.id.nav_updates:
                            fragment = new LocationUpdatesFragment();

                            LocationUpdatesFragment locationUpdatesFragment = (LocationUpdatesFragment) fragment;
                            locationUpdatesFragment.setLocationsLiveData(dataService.getCurrentLocations());
                            dataService.updateLocations();

                            break;
                        case R.id.nav_notifications:
                            fragment = new NotificationsFragment();

                            NotificationsFragment notificationsFragment = (NotificationsFragment) fragment;
                            notificationsFragment.setContactsLiveData(dataService.getCurrentContacts());
                            dataService.updateContacts();

                            break;
                        case R.id.nav_contact_list:
                            fragment = new ContactsFragment();

                            ContactsFragment contactsFragment = (ContactsFragment) fragment;
                            contactsFragment.setUsersLiveData(dataService.getCurrentUsers());
                            dataService.updateUsers();

                            break;
                    }
                    assert fragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fragment).commit();

                    return true;
                }

            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // TODO: Then clicking on the 'Notification' navigation button it changes the size of the font, which means that the text cannot fit and it will get cut.

        buttonNavigationUpdate = findViewById(R.id.buttom_navigation_view);
        buttonNavigationUpdate.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        setupDataService();
        bindService(new Intent(OverviewActivity.this, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);

        getMapPermissions();
        SupportMapFragment fragment = new SupportMapFragment();
        fragment.getMapAsync(OverviewActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void getCurrentDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Found location");
                        android.location.Location currentDeviceLocation = (android.location.Location) task.getResult();

                        moveCamera(new LatLng(currentDeviceLocation.getLatitude(), currentDeviceLocation.getLongitude()), 15);
                    } else {
                        Log.d(TAG, "Could not find location");
                        Toast.makeText(OverviewActivity.this, "Unable to get get current location", Toast.LENGTH_SHORT).show();
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

    private void setupDataService() {
        dataServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                dataService = ((DataService.DataServiceBinder) service).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                dataService = null;
            }
        };
    }

    @Override
    public void onLocationUpdatesFragmentInteraction(Location item) {
    }

    @Override
    public void onContactFragmentInteraction(User item) {
    }

    public void onNotificationsFragmentInteraction(Contact item) {
    }


    @Override
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
    }

}
