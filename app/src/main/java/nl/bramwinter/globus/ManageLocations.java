package nl.bramwinter.globus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Date;
import java.util.HashMap;

import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.util.MyProperties;

public class ManageLocations extends AppCompatActivity {

    private Button buttonAddLocation;
    private Button buttonCancel;
    private EditText editLocationDescription;
    private RadioGroup radioGroupSelectIcon;
    private TableLayout radioButtonsTable;
    private RadioGroup radioGroup;

    private DataService dataService;
    private ServiceConnection dataServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_locations);

        setupDataService();
        setupUi();
    }

    private void setupUi(){
        buttonAddLocation = findViewById(R.id.buttonAdd);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> cancelActivity());
        buttonAddLocation.setOnClickListener(v -> addLocation());

        int index = 0;
        radioButtonsTable = findViewById(R.id.radioButtonsTable);
        radioGroup = findViewById(R.id.radioGroupSelectIcon);
        for (Integer imageResourceId : MyProperties.iconMap) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            // RadioButton
            row.setLayoutParams(lp);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(index);
            radioGroup.addView(radioButton);

            // ImageView
            ImageView image = new ImageView(getApplicationContext());
            image.setImageResource(imageResourceId);
            row.addView(image);

            radioButtonsTable.addView(row, index);
            index++;
        }
    }

    private void cancelActivity() {
        Intent intent = getIntent();
        intent.putExtra("cancel", true);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void addLocation(){
        double latitude = getIntent().getLongExtra(MyProperties.latitude, 0);
        double longitude = getIntent().getLongExtra(MyProperties.longitude, 0);
        editLocationDescription = findViewById(R.id.editLocationDescription);
        radioGroupSelectIcon = findViewById(R.id.radioGroupSelectIcon);
        String description = editLocationDescription.getText().toString();
        int iconId = radioGroupSelectIcon.getCheckedRadioButtonId();

        Location location = new Location(latitude, longitude, new Date(), description, iconId);
        dataService.addLocation(location);

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
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
        bindService(new Intent(ManageLocations.this, DataService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);

    }
}
