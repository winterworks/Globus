package nl.bramwinter.globus.fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;
import nl.bramwinter.globus.util.MyProperties;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = "Globus Map Fragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;

    private FloatingActionButton buttonAddLocation;

    private LiveData<List<Location>> locationLiveData;
    private LiveData<List<User>> contactUsersLiveData;

    Activity parentActivity;
    private OnFragmentInteractionListener mListener;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public void setLocationLiveData(LiveData<List<Location>> locationLiveData) {
        this.locationLiveData = locationLiveData;
    }

    public void setContactUsersLiveData(LiveData<List<User>> contactUsersLiveData) {
        this.contactUsersLiveData = contactUsersLiveData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getContext());

        getMapPermissions();
        Fragment fragment = new SupportMapFragment();
        ((SupportMapFragment) fragment).getMapAsync(MapFragment.this);
        getFragmentManager().beginTransaction().replace(R.id.map_fragment_container,
                fragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAddLocation = getView().findViewById(R.id.FabAddLocation);
        buttonAddLocation.setImageResource(R.drawable.ic_add_location_black_24dp);
        buttonAddLocation.setOnClickListener(v -> mListener.onFabClick(mMap.getCameraPosition().target));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Observer<List<Location>> locationsObserver = locations -> showLocationsOnMap(locations);
        locationLiveData.observe(this, locationsObserver);

        Observer<List<User>> userObserver = users -> showLocationsForUser(users);
        contactUsersLiveData.observe(this, userObserver);

        if (mLocationPermissionGranted) {
            getCurrentDeviceLocation();
            if (ActivityCompat.checkSelfPermission(parentActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(parentActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void moveCamera(LatLng latLng, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void getCurrentDeviceLocation() {
        if (parentActivity == null) {
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(parentActivity);
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
                        Toast.makeText(getContext(), "Unable to get get current location", Toast.LENGTH_SHORT).show();
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
                .icon(bitmapDescriptorFromVector(parentActivity.getApplicationContext(), drawable));
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

    private void showLocationsForUser(List<User> users) {
        for (User user : users) {
            showLocationsOnMap(new ArrayList<>(user.getLocations().values()));
        }
    }

    private void showLocationsOnMap(List<Location> locations) {
        if (mMap == null) return;
        for (Location location : locations) {
            addMarker(location.getLatitude(), location.getLongitude(), MyProperties.ICON_MAP.get(location.getIcon()), location.getName());
        }
    }

    private void getMapPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                requestPermissions(permissions, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            requestPermissions(permissions, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFabClick(LatLng latLng);
    }
}
