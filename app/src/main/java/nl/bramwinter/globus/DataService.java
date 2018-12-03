package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;

public class DataService extends Service {

    protected Binder binder;

    private User user;
    private HashMap<Long, User> users = new HashMap<>();
    private HashMap<Long, Location> locations = new HashMap<>();
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
        return user.getLocations().get(uuid);
    }

    public void editMyLocation(Location location) {
        user.getLocations().remove(location.getUuid());
        user.getLocations().put(location.getUuid(), location);
        updateMyLocations();
    }

    public void addMyLocation(Location location) {
        user.getLocations().put(location.getUuid(), location);
        updateMyLocations();
    }

    public void removeMyLocation(Location location) {
        user.getLocations().remove(location.getUuid());
        updateMyLocations();
    }

    public void updateUsers() {
        usersLiveData.setValue(new ArrayList<>(users.values()));
    }

    public void updateLocations() {
        locationsLiveData.setValue(new ArrayList<>(locations.values()));
    }

    public void updateContacts() {
        contactsLiveData.setValue(asList(user.getContacts()));
    }

    public void updateMyLocations() {
        myLocationsLiveData.setValue(asList(user.getLocations()));
    }

    public void insertTestData(){
        user = new User((long)0, "Anders", "a@a.com",
                new LongSparseArray<Location>(),
                new LongSparseArray<Contact>()
        );
        user.getLocations().append((long) 0, new Location(1.1, 2.2, new Date(), "Home", 0));
        user.getLocations().append((long) 1, new Location(2.2, 3.3, new Date(), "Work", 1));
        updateMyLocations();
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
