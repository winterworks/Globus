package nl.bramwinter;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import nl.bramwinter.globus.R;
import nl.bramwinter.globus.util.MyProperties;

public class SetupNotification extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupNotificationChannel();
    }

    // Source https://codinginflow.com/tutorials/android/notifications-notification-channels/part-1-notification-channels
    // Also added android:name=.SetupNotification in the Manifest.xml
    public void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannelFriendRequest = new NotificationChannel(
                    MyProperties.CHANNEL_ID_FRIEND_REQUEST,
                    MyProperties.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannelFriendRequest
                    .setDescription(getString(R.string.app_name));

            NotificationChannel  notificationChannelContactAddLocation = new NotificationChannel(
                    MyProperties.CHANNEL_ID_CONTACT_ADD_LOCATION,
                    MyProperties.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelFriendRequest);
            notificationManager.createNotificationChannel(notificationChannelContactAddLocation);
        }
    }
}
