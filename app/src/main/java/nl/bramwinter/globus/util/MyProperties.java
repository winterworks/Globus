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
    public static final String CHANNEL_ID_FRIEND_REQUEST = "nl.winter.globus.channelIdFriendRequest";
    public static final String CHANNEL_ID_CONTACT_ADD_LOCATION = "nl.winter.globus.contactAddLocation";

    public static final List<Integer> ICON_MAP = new ArrayList<>(Arrays.asList(
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_location_city_black_24dp,
            R.drawable.ic_pool_black_24dp,
            R.drawable.ic_store_mall_directory_black_24dp,
            R.drawable.ic_hotel_black_24dp
    ));

}
