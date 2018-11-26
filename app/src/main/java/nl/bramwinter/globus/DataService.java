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
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locations = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contacts = new MutableLiveData<>();

    public DataService() {
        binder = new DataServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public MutableLiveData<List<User>> getCurrentUsers() {
        return users;
    }

    public MutableLiveData<List<Location>> getCurrentLocations() {
        return locations;
    }

    public MutableLiveData<List<Contact>> getCurrentContacts() {
        return contacts;
    }

    public void updateUsers() {
        List<User> newUsers = new ArrayList<>();

        newUsers.add(new User("Andrea", "Anders", "a@a.com"));
        newUsers.add(new User("Bernard", "Bolle", "b@b.com"));
        newUsers.add(new User("Candice", "Calen", "c@c.com"));
        newUsers.add(new User("Dana", "Dale", "d@d.com"));

        users.setValue(newUsers);
    }

    public void updateLocations() {
        List<Location> newLocations = new ArrayList<>();

        newLocations.add(new Location(1.1, 2.2, new Date(), "Home", "icon"));
        newLocations.add(new Location(2.2, 3.3, new Date(), "Work", "icon"));
        newLocations.add(new Location(3.3, 4.4, new Date(), "Bar", "icon"));

        locations.setValue(newLocations);
    }

    public void updateContacts() {
        List<Contact> newContacts = new ArrayList<>();
        User a = new User("Andrea", "Anders", "a@a.com");
        User b = new User("Bernard", "Bolle", "b@b.com");
        User c = new User("Candice", "Calen", "c@c.com");

        newContacts.add(new Contact(a, b, false));
        newContacts.add(new Contact(b, c, true));

        contacts.setValue(newContacts);
    }

    class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }
}
