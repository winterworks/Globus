package nl.bramwinter.globus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import nl.bramwinter.globus.fragments.ContactsFragment;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;
import nl.bramwinter.globus.fragments.MapFragment;
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
        MapFragment.OnFragmentInteractionListener {

    static final int ADD_LOCATION_REQUEST = 195;
    static final int EDIT_LOCATION_REQUEST = 196;
    BottomNavigationView buttonNavigationUpdate;

    private DataService dataService;
    private ServiceConnection dataServiceConnection;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_map:

                            fragment = new MapFragment();
                            ((MapFragment) fragment).setLocationLiveData(dataService.getMyLocations()); // TODO why not parsing service?
                            ((MapFragment) fragment).setContactUsersLiveData(dataService.getContactUsers());
                            break;
                        case R.id.nav_updates:
                            fragment = new LocationUpdatesFragment();

                            ((LocationUpdatesFragment) fragment).setLocationsLiveData(dataService.getMyLocations());

                            break;
                        case R.id.nav_notifications:
                            fragment = new NotificationsFragment();

                            ((NotificationsFragment) fragment).setContactsLiveData(dataService.getContacts());
                            ((NotificationsFragment) fragment).setUsersLiveData(dataService.getContactUsersRequested());

                            break;
                        case R.id.nav_contact_list:
                            fragment = new ContactsFragment();

                            ((ContactsFragment) fragment).setUsersLiveData(dataService.getContactUsers());

                            break;
                        case R.id.nav_locations_list:
                            fragment = new MyLocationsFragment();

                            ((MyLocationsFragment) fragment).setLocationsLiveData(dataService.getMyLocations());
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

    private void openManageLocationsActivity(Location location) {
        Intent intent = new Intent(OverviewActivity.this, ManageLocations.class);
        intent.putExtra(MyProperties.locationId, location.getUuid());

        startActivityForResult(intent, EDIT_LOCATION_REQUEST);
    }

    private void setupDataService() {
        dataServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                dataService = ((DataService.DataServiceBinder) service).getService();

                Fragment fragment = new MapFragment();
                ((MapFragment) fragment).setLocationLiveData(dataService.getMyLocations());
                ((MapFragment) fragment).setContactUsersLiveData(dataService.getContactUsers());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
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
    public void onFabClick(LatLng location) {
        Intent intent = new Intent(OverviewActivity.this, ManageLocations.class);

        intent.putExtra(MyProperties.latitude, location.latitude);
        intent.putExtra(MyProperties.longitude, location.longitude);

        startActivityForResult(intent, ADD_LOCATION_REQUEST);
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
