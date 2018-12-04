package nl.bramwinter.globus.models;

public class Contact {

    private String uuid;
    private User contact;
    private boolean accepted;

    public Contact(String uuid, User contact, boolean accepted) {
        this.uuid = uuid;
        this.contact = contact;
        this.accepted = accepted;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getContact() {
        return contact;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
