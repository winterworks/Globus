package nl.bramwinter.globus.models;

public class User {

    private Long uuid;
    private String Firstname;
    private String lastName;
    private String email;

    public User(String firstname, String lastName, String email) {
        Firstname = firstname;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
