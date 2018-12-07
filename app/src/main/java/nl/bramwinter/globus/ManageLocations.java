package nl.bramwinter.globus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Date;

import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.util.MyProperties;

public class ManageLocations extends AppCompatActivity {

    private Button buttonAddLocation;
    private Button buttonCancel;
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
        radioButtonsTable = findViewById(R.id.radioButtonsTable);
        radioGroup = findViewById(R.id.radioGroupSelectIcon);
        editLocationDescription = findViewById(R.id.editLocationDescription);

        setupDataService();
        setupUi();
    }

    private void setupUi() {
        buttonCancel.setOnClickListener(v -> cancelActivity());
        buttonAddLocation.setOnClickListener(v -> createNewLocation());

        int index = 0;
        for (Integer imageResourceId : MyProperties.ICON_MAP) {
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

        buttonAddLocation.setOnClickListener(v -> editLocation(location));
        buttonAddLocation.setText(R.string.edit);
    }

    private void cancelActivity() {
        Intent intent = getIntent();
        intent.putExtra("cancel", true);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void createNewLocation() {
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(MyProperties.LATITUDE, 0);
        double longitude = intent.getDoubleExtra(MyProperties.LONGITUDE, 0);
        String description = editLocationDescription.getText().toString();
        int iconId = radioGroup.getCheckedRadioButtonId();

        Location location = new Location(latitude, longitude, new Date(), description, iconId);
        dataService.addMyLocation(location);
        setResult(RESULT_OK, intent);
        finish();
    }

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
                if (getIntent().hasExtra(MyProperties.LOCATION_ID)) {
                    location = dataService.getMyLocationsById(getIntent().getStringExtra(MyProperties.LOCATION_ID));
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
