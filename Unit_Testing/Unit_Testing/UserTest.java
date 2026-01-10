package Unit_Testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import model.User;
import util.Role;
import java.util.Date;

/**
 * Tests for User class focusing on defensive copying (EI_EXPOSE_REP fixes).
 */
public class UserTest {

    // Concrete inner class to test abstract User
    private static class TestUser extends User {
        public TestUser(String name, double salary, Role role, String username, String password, Date dob, String phone, String email) {
            super(name, salary, role, username, password, dob, phone, email);
        }
    }

    private Date originalDob;

    @BeforeEach
    void setUp() {
        originalDob = new Date(100, 0, 1); // Jan 1, 2000
    }

    @Test
    void testConstructorDefensiveCopy() {
        User user = new TestUser("John", 50000, Role.Manager, "john_d", "pass", originalDob, "123", "j@e.com");
        
        // Modify the external date object
        originalDob.setTime(originalDob.getTime() + 1000000);
        
        // The internal date should remain unchanged
        assertNotEquals(originalDob.getTime(), user.getDateOfBirth().getTime(), "Internal date was modified by external reference change.");
    }

    @Test
    void testGetterDefensiveCopy() {
        User user = new TestUser("John", 50000, Role.Manager, "john_d", "pass", originalDob, "123", "j@e.com");
        
        Date dobFromGetter = user.getDateOfBirth();
        dobFromGetter.setTime(0); // Modify the object returned by getter
        
        // The internal state should still be original
        assertNotEquals(0, user.getDateOfBirth().getTime(), "Getter exposed internal mutable reference.");
    }

    @Test
    void testSetterDefensiveCopy() {
        User user = new TestUser("John", 50000, Role.Manager, "john_d", "pass", null, "123", "j@e.com");
        
        user.setDateOfBirth(originalDob);
        originalDob.setTime(0); // Modify original
        
        assertNotEquals(0, user.getDateOfBirth().getTime(), "Setter stored external reference directly.");
    }
}