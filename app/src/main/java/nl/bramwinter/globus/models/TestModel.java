package nl.bramwinter.globus.models;

public class TestModel {

    private String firstName;
    private String lastName;
    private String email;

    public TestModel() { }

    public TestModel(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
