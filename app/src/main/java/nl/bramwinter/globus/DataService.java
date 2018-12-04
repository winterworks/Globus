package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class DataService extends Service {

    protected Binder binder;

    private FirebaseFirestore db;
    private User currentUser;
    private HashMap<String, User> contactUsers = new HashMap<>();
    private HashMap<String, Location> locations = new HashMap<>();
    private MutableLiveData<List<User>> contactUsersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsLiveData = new MutableLiveData<>();
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users").document(user.getUid());

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    setDataFromFireBase(documentReference, document);
                } else {
                    Log.d("Globus", "User not found");
                }
            } else {
                Log.d("Globus", "Exception");
            }
        });
    }

    private void setDataFromFireBase(DocumentReference documentReference, DocumentSnapshot document) {
        currentUser = new User(document.getId(), document.getString("name"), document.getString("email"));

        // Make a separate request for the locations of this user
        documentReference.collection("locations").get().addOnCompleteListener(locationsTask -> {
            if (locationsTask.isSuccessful()) {

                HashMap<String, Location> locations = new HashMap<>();
                for (DocumentSnapshot locationDocument : locationsTask.getResult()) {
                    Location location = new Location(locationDocument.getData());
                    location.setUuid(locationDocument.getId());
                    locations.put(locationDocument.getId(), location);
                }

                currentUser.setLocations(locations);
                updateMyLocations();
            }
        });

        // Make a separate request for the contacts of this user
        documentReference.collection("contacts").get().addOnCompleteListener(contactsTask -> {
            if (contactsTask.isSuccessful()) {
                HashMap<String, Contact> contacts = new HashMap<>();
                for (DocumentSnapshot contactDocument : contactsTask.getResult()) {
                    Contact contact = new Contact(contactDocument.getData());
                    contact.setUuid(contactDocument.getId());
                    contacts.put(contactDocument.getId(), contact);
                }

                currentUser.setContacts(contacts);
                updateContacts();
                for (Contact contact : currentUser.getContacts().values()) {
                    addContactToUserList(contact);
                }
            }
        });
    }

    private void addContactToUserList(Contact contact) {
        DocumentReference documentReference = db.collection("users").document(String.valueOf(contact.getContactUuid()));
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    User newUser = new User(document.getId(), document.get("name").toString(), document.get("email").toString());
                    contactUsers.put(document.getId(), newUser);
                    updateCurrentContactUsers();
                } else {
                    // TODO
                }
            } else {
                // TODO
            }
        });
    }

    /*
     Functions to manage LiveData
      */
    public MutableLiveData<List<User>> getCurrentContactUsers() {
        return contactUsersLiveData;
    }

    public MutableLiveData<List<Location>> getCurrentLocations() {
        return locationsLiveData;
    }

    public MutableLiveData<List<Contact>> getCurrentContacts() {
        return contactsLiveData;
    }

    public MutableLiveData<List<Location>> getMyCurrentLocations() {
        return myLocationsLiveData;
    }

    public void updateCurrentContactUsers() {
        contactUsersLiveData.setValue(new ArrayList<>(contactUsers.values()));
    }

    public void updateLocations() {
        locationsLiveData.setValue(new ArrayList<>(locations.values()));
    }

    public void updateContacts() {
        contactsLiveData.setValue(new ArrayList<>(currentUser.getContacts().values()));
    }

    public void updateMyLocations() {
        myLocationsLiveData.setValue(new ArrayList<>(currentUser.getLocations().values()));
    }

    /*
     My locations
      */
    public Location getOneOfMyLocations(String uuid) {
        return currentUser.getLocations().get(uuid);
    }

    public void editMyLocation(Location location) {
        currentUser.getLocations().remove(location.getUuid());
        currentUser.getLocations().put(location.getUuid(), location);
        updateMyLocations();
    }

    public void addMyLocation(Location location) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = db.collection("users").document(currentUser.getUuid()).collection("locations").document().getId();
        db.collection("users").document(currentUser.getUuid()).collection("locations").document(id).set(location);

        location.setUuid(id);
        currentUser.getLocations().put(id, location);
        updateMyLocations();

    }

    public void removeMyLocation(Location location) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.getLocations().remove(location.getUuid());
        db.collection("users").document(user.getUid()).collection("locations").document(location.getUuid()).delete();

        updateMyLocations();
    }

    // My contacts
    public void addMyContact(String email) {
        Query userQuery = db.collection("users").whereEqualTo("email", email);
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    Contact contact = new Contact(document.getId(), false);
                    String id = db.collection("users").document(currentUser.getUuid()).collection("contacts").document().getId();
                    db.collection("users").document(currentUser.getUuid()).collection("contacts").document(id).set(contact);

                    contact.setUuid(id);
                    currentUser.getContacts().put(id, contact);
                    updateContacts();
                } else {
                    Log.d("Globus", "User not found");
                }
            } else {
                Log.d("Globus", "Exception");
            }
        });
    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }
}
