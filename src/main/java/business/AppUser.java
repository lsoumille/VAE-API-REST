package business;

public class AppUser {

    private String firstname;
    private String lastname;
    private String address;
    private String city;

    public AppUser() {
    }

    public AppUser(String firstname, String lastname, String address, String city) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.city = city;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
