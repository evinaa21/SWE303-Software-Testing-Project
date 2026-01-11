package model;

import util.Role;
import java.io.Serializable;
import java.util.Date;

/**
 * User class with Static Analysis fixes for EI_EXPOSE_REP.
 * Specifically handles mutable Date objects by using defensive copying.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static int idCounter = 0;
    private int id;
    private String name;
    private double salary;
    private Role role;
    private String username;
    private String password;
    private Date dateOfBirth; // Mutable object causing EI_EXPOSE_REP
    private String phonenumber;
    private String email;

    public User() {
    };

    public User(String name, double salary, Role role, String username, String password, Date dateOfBirth,
                String phonenumber, String email) {
        this.id = ++idCounter;
        this.name = name;
        this.salary = salary;
        this.role = role;
        this.username = username;
        this.password = password;
        // Fix for constructor: Defensive copy
        this.dateOfBirth = (dateOfBirth != null) ? new Date(dateOfBirth.getTime()) : null;
        this.phonenumber = phonenumber;
        this.email = email;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void setSalary(double salary) { this.salary = salary; }
    public double getSalary() { return salary; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public void setUsername(String username) { this.username = username; }
    public String getUsername() { return username; }
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }

    /**
     * Fix for SpotBugs: EI_EXPOSE_REP2
     * Instead of storing the reference, we store a new copy.
     */
    public void setDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth != null) {
            this.dateOfBirth = new Date(dateOfBirth.getTime());
        } else {
            this.dateOfBirth = null;
        }
    }

    /**
     * Fix for SpotBugs: EI_EXPOSE_REP (Lines 63/67)
     * Instead of returning the internal reference, we return a new copy.
     */
    public Date getDateOfBirth() {
        if (this.dateOfBirth != null) {
            return new Date(this.dateOfBirth.getTime());
        }
        return null;
    }

    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }
    public String getPhonenumber() { return phonenumber; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public static void resetIdCounter() { idCounter = 0; }
    
    @Override
    public String toString() {
        return "ID " + id + " Name " + name +
                " Salary " + salary + " Role " + role +
                " Username " + username +
                " Password " + password + " Date Of Birth " + dateOfBirth
                + " Phone number " +  phonenumber + " Email " + email;
    }
}