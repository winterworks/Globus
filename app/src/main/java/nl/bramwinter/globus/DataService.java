package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;

public class DataService extends Service {

    protected Binder binder;

    private HashMap<Long, User> users = new HashMap<>();
    private HashMap<Long, Location> locations = new HashMap<>();
    private HashMap<Long, Location> myLocations = new HashMap<>();
    private HashMap<Long, Contact> contacts = new HashMap<>();
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> myLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsLiveData = new MutableLiveData<>();

    public DataService() {
        binder = new DataServiceBinder();
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

    public Location getOneOfMyLocations(Long uuid) {
        return myLocations.get(uuid);
    }

    public void editMyLocation(Location location) {
        myLocations.remove(location.getUuid());
        myLocations.put(location.getUuid(), location);
        updateMyLocations();
    }

    public void addMyLocation(Location location) {
        myLocations.put(location.getUuid(), location);
        updateMyLocations();
    }

    public void removeMyLocation(Location location) {
        myLocations.remove(location.getUuid());
        updateMyLocations();
    }

    public void updateUsers() {
        usersLiveData.setValue(new ArrayList<>(users.values()));
    }

    public void updateLocations() {
        locationsLiveData.setValue(new ArrayList<>(locations.values()));
    }

    public void updateContacts() {
        contactsLiveData.setValue(new ArrayList<>(contacts.values()));
    }

    public void updateMyLocations() {
        myLocationsLiveData.setValue(new ArrayList<>(myLocations.values()));
    }

    public void insertTestData(){
//        users.put((long) 0, new User("Andrea", "Anders", "a@a.com"));
//        users.put((long) 1, new User("Bernard", "Bolle", "b@b.com"));
//        users.put((long) 2, new User("Candice", "Calen", "c@c.com"));
//        users.put((long) 3, new User("Dana", "Dale", "d@d.com"));
//        updateUsers();
//
//        locations.put((long) 0, new Location(1.1, 2.2, new Date(), "Home", 0));
//        locations.put((long) 1, new Location(2.2, 3.3, new Date(), "Work", 1));
//        locations.put((long) 2, new Location(3.3, 4.4, new Date(), "Bar", 2));
//        // Set uuid's for testing
//        for (long i = 0; i < locations.size(); i++) {
//            Objects.requireNonNull(locations.get(i)).setUuid(i);
//        }
//        updateLocations();
//
//        myLocations = locations;
//        updateMyLocations();
//
//        User a = new User("Andrea", "Anders", "a@a.com");
//        User b = new User("Bernard", "Bolle", "b@b.com");
//        User c = new User("Candice", "Calen", "c@c.com");
//        contacts.put((long) 0, new Contact(a, b, false));
//        contacts.put((long) 1, new Contact(b, c, true));
//        updateContacts();
    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }
}
