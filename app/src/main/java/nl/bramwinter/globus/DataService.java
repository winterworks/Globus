package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.LongSparseArray;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;

public class DataService extends Service {

    protected Binder binder;

    private FirebaseFirestore db;
    private User currentUser;
    private HashMap<Long, User> users = new HashMap<>();
    private HashMap<Long, Location> locations = new HashMap<>();
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> myLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsLiveData = new MutableLiveData<>();

    public DataService() {
        db = FirebaseFirestore.getInstance();
        getCurrentUser();

        binder = new DataServiceBinder();
    }

    private void getCurrentUser(){
        DocumentReference documentReference = db.collection("users").document("w4Q75XXFDDdqCrraH5rO36R08Cl2");

        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
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
        documentReference.collection("locations").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {

                HashMap<String, Location> locations = new HashMap<>();
                for (DocumentSnapshot document1 : task1.getResult()) {
                    Location location = new Location(document1.getData());
                    location.setUuid(document1.getId());
                    locations.put(document1.getId(), location);
                }

                currentUser = new User(document.getId(), document.getString("name"), document.getString("email"), locations, new HashMap<>());
                updateMyLocations();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public MutableLiveData<List<User>> getCurrentUsers() {
        return usersLiveData;
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

        currentUser.getLocations().put(id, location);
        updateMyLocations();
    }

    public void removeMyLocation(Location location) {
        currentUser.getLocations().remove(location.getUuid());
        db.collection("users").document("w4Q75XXFDDdqCrraH5rO36R08Cl2").collection("locations").document(location.getUuid()).delete();

        updateMyLocations();
    }

    public void updateUsers() {
        usersLiveData.setValue(new ArrayList<>(users.values()));
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

    public void insertTestData(){

    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }

    // Function from: https://stackoverflow.com/questions/17008115/how-to-convert-a-sparsearray-to-arraylist#17008172
    private static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }
}
