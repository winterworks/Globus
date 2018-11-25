package nl.bramwinter.globus.models;

public class Contact {

    private User contactor;
    private User contacted;
    private boolean accepted;

    public Contact(User contactor, User contacted, boolean accepted) {
        this.contactor = contactor;
        this.contacted = contacted;
        this.accepted = accepted;
    }

    public User getContactor() {
        return contactor;
    }

    public void setContactor(User contactor) {
        this.contactor = contactor;
    }

    public User getContacted() {
        return contacted;
    }

    public void setContacted(User contacted) {
        this.contacted = contacted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
