package nl.bramwinter.globus.models;

import java.util.Map;

public class Contact {

    private String uuid;
    private String contactUuid;
    private boolean accepted;

    public Contact(String contactUuid, boolean accepted) {
        this.contactUuid = contactUuid;
        this.accepted = accepted;
    }

    public Contact(Map map) {
        try {
            this.uuid = (String) map.get("uuid");
            this.contactUuid = (String) map.get("contactUuid");
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
}
