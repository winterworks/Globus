package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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

public class DataService extends Service {

    protected Binder binder;
    private static final String TAG = "DataService";

    private FirebaseFirestore db;
    private User currentUser;
    private HashMap<String, User> contactUsers = new HashMap<>();
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
                    Log.d(TAG, "User not found");
                }
            } else {
                Log.d(TAG, "Exception");
            }
        });
    }

    private void setDataFromFireBase(DocumentReference documentReference, DocumentSnapshot document) {
        currentUser = new User(document.getId(), document.getString("name"), document.getString("email"), new HashMap<>(), new HashMap<>());

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

        setCurrentUserContactsListener(documentReference);
        updateContacts();
        for (Contact contact : currentUser.getContacts().values()) {
            // Only download the actual contact(user) if the request has been accepted.
            if (contact.isAccepted() || !contact.isInitiated()) {
                addContactToUserList(contact);
            }
//            else if () {
//                // Contact is not yet accepted and not initiated by this user, so download the other user object.
//                db.collection("users").document(contact.getContactUuid()).get().addOnCompleteListener(userTask -> {
//                    addContactToUserList(contact);
//                });
//            }
        }
    }

    private void setCurrentUserContactsListener(DocumentReference documentReference) {
        documentReference.collection("contacts")
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
                                    currentUser.getContacts().put(contact.getUuid(), contact);
                                    if (contact.isAccepted() || !contact.isInitiated()) {
                                        addContactToUserList(contact);
                                    }
                                    break;
                                case MODIFIED:
                                    currentUser.getContacts().put(contact.getUuid(), contact);
                                    if (contact.isAccepted() || !contact.isInitiated()) {
                                        addContactToUserList(contact);
                                    }
                                    break;
                                case REMOVED:
                                    currentUser.getContacts().remove(contact.getUuid());
                                    break;
                            }

                            updateContacts();
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
                            switch (dc.getType()) {
                                case ADDED:
                                    user.getLocations().put(location.getUuid(), location);
                                    break;
                                case MODIFIED:
                                    user.getLocations().put(location.getUuid(), location);
                                    break;
                                case REMOVED:
                                    user.getLocations().remove(location.getUuid());
                                    break;
                            }

                            updateCurrentContactUsers();
                        }
                    }

                });

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
                    updateCurrentContactUsers();
                }
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
        // Get the user belonging to the email
        Query userQuery = db.collection("users").whereEqualTo("email", email);
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    // Check if the current user already has a contact for this user.
                    Query contactQuery = db.collection("users").document(currentUser.getUuid()).collection("contacts").whereEqualTo("contactUuid", document.getId());
                    contactQuery.get().addOnCompleteListener(checkIfContactExistsTask -> {
                        if (checkIfContactExistsTask.isSuccessful() && checkIfContactExistsTask.getResult().size() == 0) {
                            // Store the contact for the contacted user
                            Contact externalContact = new Contact(currentUser.getUuid(), false, false);
                            String id1 = db.collection("users").document(document.getId()).collection("contacts").document().getId();
                            db.collection("users").document(document.getId()).collection("contacts").document(id1).set(externalContact);

                            // Store the contact for the current user
                            Contact myContact = new Contact(document.getId(), false, true);
                            String id2 = db.collection("users").document(currentUser.getUuid()).collection("contacts").document().getId();
                            db.collection("users").document(currentUser.getUuid()).collection("contacts").document(id2).set(myContact);
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
        Query userQuery = db.collection("users").document(contact.getContactUuid()).collection("contacts").whereEqualTo("contactUuid", currentUser.getUuid());
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    Contact externalContact = new Contact(document.getData());
                    externalContact.setAccepted(true);

                    db.collection("users").document(contact.getContactUuid()).collection("contacts").document(document.getId()).set(externalContact);
                } else {
                    Log.d(TAG, "User not found");
                }
            } else {
                Log.d(TAG, "Exception");
            }
        });

        // Accept the contact for the current user
        contact.setAccepted(true);
        db.collection("users").document(currentUser.getUuid()).collection("contacts").document(contact.getUuid()).set(contact);
        currentUser.getContacts().get(contact.getUuid()).setAccepted(true);
        updateContacts();
    }

    public void removeContactForUser(User user) {
        for (Contact contact : currentUser.getContacts().values()) {
            if (contact.getContactUuid().equals(user.getUuid())) {
                // Found the contact for the user, now remove it.
                removeContact(contact);
                return;
            }
        }
    }

    public void removeContact(Contact contact) {
        db.collection("users").document(currentUser.getUuid()).collection("contacts").document(contact.getUuid()).delete();
        Query userQuery = db.collection("users").document(contact.getContactUuid()).collection("contacts").whereEqualTo("contactUuid", currentUser.getUuid());
        userQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().size() == 1) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                if (document.exists()) {
                    db.collection("users").document(contact.getContactUuid()).collection("contacts").document(document.getId()).delete();
                }
            }
        });
        currentUser.getContacts().remove(contact.getUuid());
        contactUsers.remove(contact.getContactUuid());
        updateContacts();
        updateCurrentContactUsers();
    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }
}
