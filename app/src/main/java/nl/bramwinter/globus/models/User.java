package nl.bramwinter.globus.models;

import android.util.LongSparseArray;

public class User {

    private Long uuid;
    private String name;
    private String email;
    private LongSparseArray<Location> locations;
    private LongSparseArray<Contact> contacts;

    public User(Long uuid, String name, String email, LongSparseArray<Location> locations, LongSparseArray<Contact> contacts) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.locations = locations;
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public List<Location> getLocations() {
    public LongSparseArray<Location> getLocations() {
        return locations;
    }

    public void setLocations(LongSparseArray<Location> locations) {
        this.locations = locations;
    }

    public LongSparseArray<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(LongSparseArray<Contact> contacts) {
        this.contacts = contacts;
    }
}
