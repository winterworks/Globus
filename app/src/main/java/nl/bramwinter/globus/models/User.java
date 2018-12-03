package nl.bramwinter.globus.models;

import java.util.List;

public class User {

    private Long uuid;
    private String name;
    private String email;
    private List<Location> locations;
    private List<Contact> contacts;


    public User(Long uuid, String name, String email, List<Location> locations, List<Contact> contacts) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.locations = locations;
        this.contacts = contacts;
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
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
