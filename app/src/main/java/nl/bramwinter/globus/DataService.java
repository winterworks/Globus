package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.bramwinter.globus.models.Contact;
import nl.bramwinter.globus.models.Location;
import nl.bramwinter.globus.models.User;

public class DataService extends Service {

    protected Binder binder;

    private List<User> users = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();
    private List<Contact> contacts = new ArrayList<>();
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsLiveData = new MutableLiveData<>();
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

    public void addLocation(Location location){
        locations.add(location);
        updateLocations();
    }

    public void updateUsers() {
        usersLiveData.setValue(users);
    }

    public void updateLocations() {
        locationsLiveData.setValue(locations);
    }

    public void updateContacts() {
        contactsLiveData.setValue(contacts);
    }

    public void insertTestData(){
        users.add(new User("Andrea", "Anders", "a@a.com"));
        users.add(new User("Bernard", "Bolle", "b@b.com"));
        users.add(new User("Candice", "Calen", "c@c.com"));
        users.add(new User("Dana", "Dale", "d@d.com"));
        updateUsers();

        locations.add(new Location(1.1, 2.2, new Date(), "Home", 0));
        locations.add(new Location(2.2, 3.3, new Date(), "Work", 1));
        locations.add(new Location(3.3, 4.4, new Date(), "Bar", 2));
        updateLocations();

        User a = new User("Andrea", "Anders", "a@a.com");
        User b = new User("Bernard", "Bolle", "b@b.com");
        User c = new User("Candice", "Calen", "c@c.com");
        contacts.add(new Contact(a, b, false));
        contacts.add(new Contact(b, c, true));
        updateContacts();
    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }
}
