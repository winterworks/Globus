package nl.bramwinter.globus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.util.MyProperties;

public class ManageLocations extends AppCompatActivity {

    private Button buttonAddLocation;
    private Button buttonCancel;
    private Button buttonEdit;
    private EditText editLocationDescription;
    private TableLayout radioButtonsTable;
    private RadioGroup radioGroup;

    private Location location;

    private DataService dataService;
    private ServiceConnection dataServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_locations);

        buttonAddLocation = findViewById(R.id.buttonAdd);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonEdit = findViewById(R.id.buttonEdit);
        radioButtonsTable = findViewById(R.id.radioButtonsTable);
        radioGroup = findViewById(R.id.radioGroupSelectIcon);
        editLocationDescription = findViewById(R.id.editLocationDescription);

        setupDataService();
        setupUi();
    }

    private void setupUi(){
        buttonCancel.setOnClickListener(v -> cancelActivity());
        buttonAddLocation.setOnClickListener(v -> createNewLocation());
        buttonEdit.setVisibility(View.GONE);

        int index = 0;
        for (Integer imageResourceId : MyProperties.iconMap) {
            // RadioButton
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(index);
            radioGroup.addView(radioButton);

            // ImageView
            TableRow row = new TableRow(this);
            ImageView image = new ImageView(getApplicationContext());
            image.setImageResource(imageResourceId);
            image.setMinimumHeight(83);
            row.addView(image);

            radioButtonsTable.addView(row, index);
            index++;
        }
    }

    private void showLocationInfoInUi(Location location) {
        radioGroup.check(location.getIcon());
        editLocationDescription.setText(location.getName());

        buttonAddLocation.setOnClickListener(v -> updateLocation(location));
        buttonEdit.setOnClickListener(v -> editLocation(location));
        buttonEdit.setVisibility(View.VISIBLE);

        buttonAddLocation.setText(R.string.update);
    }

    private void cancelActivity() {
        Intent intent = getIntent();
        intent.putExtra("cancel", true);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void createNewLocation() {
        double latitude = getIntent().getLongExtra(MyProperties.latitude, 0);
        double longitude = getIntent().getLongExtra(MyProperties.longitude, 0);
        String description = editLocationDescription.getText().toString();
        int iconId = radioGroup.getCheckedRadioButtonId();

        Location location = new Location(latitude, longitude, new Date(), description, iconId);
        // TODO this is a job for the database
        location.setUuid(ThreadLocalRandom.current().nextLong());
        dataService.addMyLocation(location);

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    // Update a location, keep the old one and add a new one
    private void updateLocation(Location location) {
        Location newLocation = new Location(location.getLatitude(), location.getLongitude(), new Date(), location.getName(), location.getIcon());
        newLocation.setName(editLocationDescription.getText().toString());
        newLocation.setIcon(radioGroup.getCheckedRadioButtonId());
        // TODO this is a job for the database
        newLocation.setUuid(ThreadLocalRandom.current().nextLong());

        dataService.addMyLocation(newLocation);

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    // Edit a location, do not create a new one
    private void editLocation(Location location) {
        location.setName(editLocationDescription.getText().toString());
        location.setIcon(radioGroup.getCheckedRadioButtonId());

        dataService.editMyLocation(location);

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupDataService() {
        dataServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                dataService = ((DataService.DataServiceBinder) service).getService();
                if (getIntent().hasExtra(MyProperties.locationId)) {
                    location = dataService.getOneOfMyLocations(getIntent().getLongExtra(MyProperties.locationId, 0));
                    showLocationInfoInUi(location);
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                dataService = null;
            }
        };
        bindService(new Intent(ManageLocations.this, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
