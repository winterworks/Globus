package nl.bramwinter.globus.models;

import java.util.Map;

public class Contact {

    private String uuid;
    private String contactUuid;
    private boolean initiated;
    private boolean accepted;

    public Contact(String contactUuid, boolean accepted, boolean initiated) {
        this.contactUuid = contactUuid;
        this.accepted = accepted;
        this.initiated = initiated;
    }

    public Contact(Map map) {
        try {
            this.uuid = (String) map.get("uuid");
            this.contactUuid = (String) map.get("contactUuid");
            this.initiated = (boolean) map.get("initiated");
            this.accepted = (boolean) map.get("accepted");
        } catch (Exception e) {

        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isInitiated() {
        return initiated;
    }

    public void setInitiated(boolean initiated) {
        this.initiated = initiated;
    }
}
