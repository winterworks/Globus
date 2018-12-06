package nl.bramwinter.globus;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.bramwinter.globus.fragments.ContactsFragment;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;
import nl.bramwinter.globus.fragments.MyLocationsFragment;
import nl.bramwinter.globus.fragments.NotificationsFragment;
import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;
import nl.bramwinter.globus.util.MyProperties;

public class OverviewActivity extends AppCompatActivity implements
        LocationUpdatesFragment.locationsFragmentListener,
        MyLocationsFragment.MyLocationsFragmentListener,
        ContactsFragment.ContactFragmentListener,
        NotificationsFragment.NotificationFragmentListener,
        OnMapReadyCallback {

    static final int ADD_LOCATION_REQUEST = 195;
    static final int EDIT_LOCATION_REQUEST = 196;
    private static final String TAG = "Globus Map";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    BottomNavigationView buttonNavigationUpdate;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private FloatingActionButton buttonAddLocation;

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
                            locationUpdatesFragment.setLocationsLiveData(dataService.getMyLocations());

                            break;
                        case R.id.nav_notifications:
                            fragment = new NotificationsFragment();

                            NotificationsFragment notificationsFragment = (NotificationsFragment) fragment;
                            notificationsFragment.setContactsLiveData(dataService.getContacts());
                            notificationsFragment.setUsersLiveData(dataService.getContactUsersRequested());

                            break;
                        case R.id.nav_contact_list:
                            fragment = new ContactsFragment();

                            ContactsFragment contactsFragment = (ContactsFragment) fragment;
                            contactsFragment.setUsersLiveData(dataService.getContactUsers());

                            break;
                        case R.id.nav_locations_list:
                            fragment = new MyLocationsFragment();

                            MyLocationsFragment myLocationsFragment = (MyLocationsFragment) fragment;
                            myLocationsFragment.setLocationsLiveData(dataService.getMyLocations());

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
        setupUi();

        getMapPermissions();
        SupportMapFragment fragment = new SupportMapFragment();
        fragment.getMapAsync(OverviewActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Log.d("Celik", user.getUid());
        DocumentReference documentReference = db.collection("users").document(String.valueOf(user.getUid()));

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d("Celik", "User" + user.getEmail() + " exists");
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", user.getDisplayName());
                        data.put("email", user.getEmail());

                        db.collection("users").document(user.getUid()).set(data);
                    }
                } else {
                    Log.d("Celik", "Exception");
                }
            }
        });
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

    private void addMarker(double latitude, double longtitude, int drawable, String title) {
        MarkerOptions markerOptions = new MarkerOptions()
                .title(title)
                .position(new LatLng(latitude, longtitude))
                .icon(bitmapDescriptorFromVector(getApplicationContext(), drawable));
        mMap.addMarker(markerOptions);
    }

    // source: https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setupUi() {
        buttonAddLocation = findViewById(R.id.FabAddLocation);
        buttonAddLocation.setImageResource(R.drawable.ic_add_location_black_24dp);
        buttonAddLocation.setOnClickListener(v -> openCreateNewLocationsActivity());
    }

    private void openCreateNewLocationsActivity() {
        Intent intent = new Intent(OverviewActivity.this, ManageLocations.class);

        LatLng location = mMap.getCameraPosition().target;
        intent.putExtra(MyProperties.latitude, location.latitude);
        intent.putExtra(MyProperties.longitude, location.longitude);

        startActivityForResult(intent, ADD_LOCATION_REQUEST);
    }

    private void openManageLocationsActivity(Location location) {
        Intent intent = new Intent(OverviewActivity.this, ManageLocations.class);
        intent.putExtra(MyProperties.locationId, location.getUuid());

        startActivityForResult(intent, EDIT_LOCATION_REQUEST);
    }

    private void moveCamera(LatLng latLng, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void setupDataService() {
        dataServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                dataService = ((DataService.DataServiceBinder) service).getService();
                setLiveDataForMapLocations();
                setLiveDataForContactMapLocations();
            }

            public void onServiceDisconnected(ComponentName className) {
                dataService = null;
            }
        };
        bindService(new Intent(OverviewActivity.this, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void locationsClickListener(Location location) {
    }

    @Override
    public void ContactClickListener(User user) {
    }

    @Override
    public void ContactAddListener(String email) {
        dataService.addContact(email);
    }

    @Override
    public void ContactPressListener(User user) {
        dataService.removeContactForUser(user);
    }

    public void NotificationClickListener(Contact contact) {
    }

    @Override
    public void NotificationAcceptListener(Contact contact) {
        dataService.acceptContact(contact);
    }

    @Override
    public void NotificationDeclineListener(Contact contact) {
        dataService.removeContact(contact);
    }

    @Override
    public void MyLocationsClickListener(Location location) {
        openManageLocationsActivity(location);
    }

    @Override
    public void MyLocationsPressListener(Location location) {
        //TODO add popup dialog before deleting location
        dataService.removeMyLocation(location);
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

    private void setLiveDataForMapLocations() {
        Observer<List<Location>> locationsObserver = locations -> showLocationsOnMap(locations);
        dataService.getMyLocations().observe(this, locationsObserver);
    }

    private void setLiveDataForContactMapLocations() {
        Observer<List<User>> userObserver = users -> showLocationsForUser(users);
        dataService.getContactUsers().observe(this, userObserver);

    }

    private void showLocationsForUser(List<User> users) {
        for (User user : users) {
            showLocationsOnMap(new ArrayList<>(user.getLocations().values()));
        }
    }

    private void showLocationsOnMap(List<Location> locations) {
        for (Location location : locations) {

            int icon = 0;

            if (location != null) {
                switch (location.getIcon()) {
                    case 0:
                        icon = R.drawable.ic_home_black_24dp;

                        break;
                    case 1:
                        icon = R.drawable.ic_location_city_black_24dp;
                        break;
                    case 2:
                        icon = R.drawable.ic_casino_black_24dp;
                        break;
                }
            }
            addMarker(location.getLatitude(), location.getLongitude(), icon, location.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setLiveDataForMapLocations();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                //TODO show success message
            }
        }
        if (requestCode == EDIT_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                //TODO show success message
            }
        }
    }
}
