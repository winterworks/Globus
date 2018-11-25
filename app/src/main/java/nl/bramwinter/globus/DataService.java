package nl.bramwinter.globus;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import nl.bramwinter.globus.models.User;

public class DataService extends Service {

    protected Binder binder;
    private MutableLiveData<List<User>> users;

    public DataService() {
        binder = new DataServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class DataServiceBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    }

    public MutableLiveData<List<User>> getCurrentUsers() {
        return users;
    }

    public void updateUsers() {
        List<User> newUsers = users.getValue();

        newUsers.add(new User("Andrea", "Anders", "a@a.com"));
        newUsers.add(new User("Bernard", "Bolle", "b@b.com"));
        newUsers.add(new User("Candice", "Calen", "c@c.com"));
        newUsers.add(new User("Dana", "Dale", "d@d.com"));

        users.setValue(newUsers);
    }
}
