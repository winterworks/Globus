package nl.bramwinter.globus;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import nl.bramwinter.globus.fragments.ContactsFragment;
import nl.bramwinter.globus.fragments.LocationUpdatesFragment;
import nl.bramwinter.globus.fragments.NotificationsFragment;
import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;

public class OverviewActivity extends AppCompatActivity implements
        LocationUpdatesFragment.OnListFragmentInteractionListener,
        ContactsFragment.OnListFragmentInteractionListener,
        NotificationsFragment.OnListFragmentInteractionListener {

    BottomNavigationView buttonNavigationUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        // TODO: Then clicking on the 'Notification' navigation button it changes the size of the font, which means that the text cannot fit and it will get cut.

            buttonNavigationUpdate = findViewById(R.id.buttom_navigation_view);
        buttonNavigationUpdate.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LocationUpdatesFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_map:
                            fragment = new LocationUpdatesFragment();
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
    public void onNotificationsFragmentInteraction(Contact item) {

    }
}
