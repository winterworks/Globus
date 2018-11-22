package nl.bramwinter.globus;

import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import nl.bramwinter.globus.dummy.DummyContent;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;

public class OverviewActivity extends AppCompatActivity implements LocationUpdatesFragment.OnListFragmentInteractionListener, OnMapReadyCallback {

    BottomNavigationView buttonNavigationUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        // TODO: Then clicking on the 'Notification' navigation button it changes the size of the font, which means that the text cannot fit and it will get cut.

            buttonNavigationUpdate = findViewById(R.id.buttom_navigation_view);
        buttonNavigationUpdate.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SupportMapFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_map:
                            SupportMapFragment fragment1 = new SupportMapFragment();
                            fragment1.getMapAsync(OverviewActivity.this);
                            fragment = fragment1;
                            break;
                        case R.id.nav_updates:
                            fragment = new LocationUpdatesFragment();
                            break;
                        case R.id.nav_notifications:
                            fragment = new LocationUpdatesFragment();
                            break;
                        case R.id.nav_contact_list:
                            fragment = new LocationUpdatesFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fragment).commit();

                    return true;
                }

            };

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
