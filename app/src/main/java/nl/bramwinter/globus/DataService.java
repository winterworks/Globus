package nl.bramwinter.globus;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;
import nl.bramwinter.globus.util.MyProperties;

public class DataService extends Service {

    protected Binder binder;
    private static final String TAG = "DataService";

    private FirebaseFirestore db;
    private DocumentReference userReference;

    private User user;
    private HashMap<String, User> contactUsers = new HashMap<>();
    private HashMap<String, User> contactUsersRequested = new HashMap<>();
    private MutableLiveData<List<User>> contactUsersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> contactUsersRequestedLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> myLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsLiveData = new MutableLiveData<>();

    public DataService() {
        db = FirebaseFirestore.getInstance();
        getCurrentUser();
        binder = new DataServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = db.collection("users").document(user.getUid());

        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    this.user = new User(document.getId(), document.getString("name"), document.getString("email"), new HashMap<>(), new HashMap<>());
                    setUserDataFromFireBase();
                } else {
                    Log.d(TAG, "User not found");
                }
            } else {
                Log.d(TAG, "Exception");
            }
        });
    }

    private void setUserDataFromFireBase() {
        // Get all the locations for this user and add them to the model
        userReference.collection("locations").get().addOnCompleteListener(locationsTask -> {
            if (locationsTask.isSuccessful()) {

                HashMap<String, Location> locations = new HashMap<>();
                for (DocumentSnapshot locationDocument : locationsTask.getResult()) {
                    Location location = new Location(locationDocument.getData());
                    location.setUuid(locationDocument.getId());
                    locations.put(locationDocument.getId(), location);
                }

                user.setLocations(locations);
                updateMyLocations();
            }
        });

        setUserContactsListener();
    }

    private void setUserContactsListener() {
        userReference.collection("contacts")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getDocument().get("contactUuid") != null) {
                            Contact contact = new Contact(dc.getDocument().getData());
                            contact.setUuid(dc.getDocument().getId());
                            switch (dc.getType()) {
                                case ADDED:
                                    user.getContacts().put(contact.getUuid(), contact);
                                    AddContactToLists(contact);
                                    break;
                                case MODIFIED:
                                    user.getContacts().put(contact.getUuid(), contact);
                                    AddContactToLists(contact);
                                    break;
                                case REMOVED:
                                    user.getContacts().remove(contact.getUuid());
                                    contactUsers.remove(contact.getContactUuid());
                                    updateContactUsers();
                                    break;
                            }
                            updateContacts();
                        }
                    }
                });
    }

    private void AddContactToLists(Contact contact) {
        if (contact.isAccepted()) {
            stopForeground(true);
            stopSelf();
            addContactToUserList(contact);
        } else if (!contact.isInitiated()) {
            addContactToUserRequestedList(contact);
        }
    }

    private void addContactToUserList(Contact contact) {
        DocumentReference documentReference = db.collection("users").document(contact.getContactUuid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    User newUser = new User(document.getId(), document.get("name").toString(), document.get("email").toString(), new HashMap<>(), new HashMap<>());
                    contactUsers.put(document.getId(), newUser);
                    setContactUserLocationsListener(newUser);
                    updateContactUsers();
                }
            }
        });
    }

    private void setContactUserLocationsListener(User user) {
        DocumentReference documentReference = db.collection("users").document(user.getUuid());
        documentReference.collection("locations")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getDocument().get("latitude") != null) {
                            Location location = new Location(dc.getDocument().getData());
                            location.setUuid(dc.getDocument().getId());
                            String message = "User " + user.getName() + " added a new location " + location.getName();
                            switch (dc.getType()) {
                                case ADDED:
                                    user.getLocations().put(location.getUuid(), location);
                                    showNewLocationNoticication(message);
                                    break;
                                case MODIFIED:
                                    user.getLocations().put(location.getUuid(), location);
                                    break;
                                case REMOVED:
                                    user.getLocations().remove(location.getUuid());
                                    break;
                            }

                            updateContactUsers();
                        }
                    }
                });
    }

    private void addContactToUserRequestedList(Contact contact) {
        DocumentReference documentReference = db.collection("users").document(contact.getContactUuid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    User newUser = new User(document.getId(), document.get("name").toString(), document.get("email").toString(), new HashMap<>(), new HashMap<>());
                    contactUsersRequested.put(document.getId(), newUser);
                    updateContactUsersRequested();
                    String message = "User " + newUser.getName() + " has sent a friend request";
                    showPushNotification(message);
                }
            }
        });
    }

    /*
     Functions to retrieve and update LiveData
      */
    public MutableLiveData<List<User>> getContactUsersRequested() {
        return contactUsersRequestedLiveData;
    }

    public MutableLiveData<List<User>> getContactUsers() {
        return contactUsersLiveData;
    }

    public MutableLiveData<List<Contact>> getContacts() {
        return contactsLiveData;
    }

    public MutableLiveData<List<Location>> getMyLocations() {
        return myLocationsLiveData;
    }

    public void updateContactUsers() {
        contactUsersLiveData.postValue(new ArrayList<>(contactUsers.values()));
    }

    public void updateContactUsersRequested() {
        contactUsersRequestedLiveData.setValue(new ArrayList<>(contactUsersRequested.values()));
    }

    public void updateContacts() {
        contactsLiveData.setValue(new ArrayList<>(user.getContacts().values()));
    }

    public void updateMyLocations() {
        myLocationsLiveData.setValue(new ArrayList<>(user.getLocations().values()));
    }

    /*
     My locations
      */
    public Location getMyLocationsById(String uuid) {
        return user.getLocations().get(uuid);
    }

    public void editMyLocation(Location location) {
        user.getLocations().remove(location.getUuid());
        user.getLocations().put(location.getUuid(), location);
        updateMyLocations();
    }

    public void addMyLocation(Location location) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = db.collection("users").document(user.getUuid()).collection("locations").document().getId();
        db.collection("users").document(user.getUuid()).collection("locations").document(id).set(location);

        location.setUuid(id);
        user.getLocations().put(id, location);
        updateMyLocations();

    }

    public void removeMyLocation(Location location) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.user.getLocations().remove(location.getUuid());
        db.collection("users").document(user.getUid()).collection("locations").document(location.getUuid()).delete();

        updateMyLocations();
    }

    /*
     My contacts
     */
    public void addContact(String email) {
        // Get the user belonging to the email
        Query userQuery = db.collection("users").whereEqualTo("email", email);
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    // Check if the current user already has a contact for this user.
                    Query contactQuery = db.collection("users").document(user.getUuid()).collection("contacts").whereEqualTo("contactUuid", document.getId());
                    contactQuery.get().addOnCompleteListener(checkIfContactExistsTask -> {
                        if (checkIfContactExistsTask.isSuccessful() && checkIfContactExistsTask.getResult().size() == 0) {
                            // Store the contact for the contacted user
                            Contact externalContact = new Contact(user.getUuid(), false, false);
                            String id1 = db.collection("users").document(document.getId()).collection("contacts").document().getId();
                            db.collection("users").document(document.getId()).collection("contacts").document(id1).set(externalContact);

                            // Store the contact for the current user
                            Contact myContact = new Contact(document.getId(), false, true);
                            String id2 = db.collection("users").document(user.getUuid()).collection("contacts").document().getId();
                            db.collection("users").document(user.getUuid()).collection("contacts").document(id2).set(myContact);
                        }
                    });
                } else {
                    Log.d(TAG, "User not found");
                }
            } else {
                Log.d(TAG, "Exception");
            }
        });
    }

    public void acceptContact(Contact contact) {
        // Accept the contact for the contacted user
        Query userQuery = db.collection("users").document(contact.getContactUuid()).collection("contacts").whereEqualTo("contactUuid", user.getUuid());
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    Contact externalContact = new Contact(document.getData());
                    externalContact.setAccepted(true);

                    db.collection("users").document(contact.getContactUuid()).collection("contacts").document(document.getId()).set(externalContact);
                    acceptContactCurrentUser(contact);
                } else {
                    Log.d(TAG, "User not found");
                }
            } else {
                Log.d(TAG, "Exception");
            }
        });
    }

    private void acceptContactCurrentUser(Contact contact) {
        // Accept the contact for the current user
        contact.setAccepted(true);
        db.collection("users").document(user.getUuid()).collection("contacts").document(contact.getUuid()).set(contact);
        user.getContacts().get(contact.getUuid()).setAccepted(true);
        updateContacts();
        contactUsersRequested.remove(contact.getContactUuid());
        updateContactUsersRequested();
    }

    public void removeContactForUser(User user) {
        for (Contact contact : this.user.getContacts().values()) {
            if (contact.getContactUuid().equals(user.getUuid())) {
                // Found the contact for the user, now remove it.
                removeContact(contact);
                return;
            }
        }
    }

    public void removeContact(Contact contact) {
        db.collection("users").document(user.getUuid()).collection("contacts").document(contact.getUuid()).delete();
        Query userQuery = db.collection("users").document(contact.getContactUuid()).collection("contacts").whereEqualTo("contactUuid", user.getUuid());
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    db.collection("users").document(contact.getContactUuid()).collection("contacts").document(document.getId()).delete();
                }
            }
        });
        user.getContacts().remove(contact.getUuid());
        contactUsersRequested.remove(contact.getContactUuid());
        updateContacts();
        updateContactUsersRequested();
    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }

    // Source: https://developer.android.com/training/notify-user/build-notification
    public void showPushNotification(String message) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MyProperties.CHANNEL_ID_FRIEND_REQUEST);

        Intent intent = new Intent(this, OverviewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .build();
        notificationManagerCompat.notify(1, notification);
    }

    public void showNewLocationNoticication(String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MyProperties.CHANNEL_ID_CONTACT_ADD_LOCATION);
        int id = 2;

        Intent intent = new Intent(this, OverviewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(id, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
