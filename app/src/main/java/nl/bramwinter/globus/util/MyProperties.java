package nl.bramwinter.globus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.bramwinter.globus.R;

public class MyProperties {
    // Intent extra's
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String locationId = "locationId";

    public static final List<Integer> iconMap = new ArrayList<>(Arrays.asList(
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_location_city_black_24dp,
            R.drawable.ic_casino_black_24dp
    ));
}
