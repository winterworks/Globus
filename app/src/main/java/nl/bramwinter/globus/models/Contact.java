package nl.bramwinter.globus.models;

public class Contact {

    private User contact;
    private boolean accepted;

    public Contact(User contact, boolean accepted) {
        this.contact = contact;
        this.accepted = accepted;
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
