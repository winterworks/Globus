package nl.bramwinter.globus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.bramwinter.globus.R;

public class MyProperties {
    // Intent extra's
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LOCATION_ID = "LOCATION_ID";

    public static final int NOTIFICATION_ID = 1001;
    public static final String NOTIFICATION_CHANNEL = "nl.winter.globus.notification";
    public static final String CHANNEL_NAME = "globus.channel.name";
    public static final String CHANNEL_ID = "nl.winter.globus.channelId";

    public static final List<Integer> ICON_MAP = new ArrayList<>(Arrays.asList(
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_location_city_black_24dp,
            R.drawable.ic_casino_black_24dp
    ));

}
