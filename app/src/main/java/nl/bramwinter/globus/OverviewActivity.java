package nl.bramwinter.globus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // TODO: Then clicking on the 'Notification' navigation button it changes the size of the font, which means that the text cannot fit and it will get cut.
    }
}
