package nl.bramwinter.globus.models;

public class Contact {

    private Long uuid;
    private User contact;
    private boolean accepted;

    public Contact(Long uuid, User contact, boolean accepted) {
        this.uuid = uuid;
        this.contact = contact;
        this.accepted = accepted;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
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
