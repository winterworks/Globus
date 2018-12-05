package nl.bramwinter.globus.models;

import java.util.Map;

public class User {

    private String uuid;
    private String name;
    private String email;
    private Map<String, Location> locations;
    private Map<String, Contact> contacts;

    public User(String uuid, String name, String email) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
    }

    public User(String uuid, String name, String email, Map<String, Location> locations, Map<String, Contact> contacts) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.locations = locations;
        this.contacts = contacts;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Map<String, Location> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, Location> locations) {
        this.locations = locations;
    }

    public Map<String, Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, Contact> contacts) {
        this.contacts = contacts;
    }
}
