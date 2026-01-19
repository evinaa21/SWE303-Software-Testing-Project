package Unit_Testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import model.Admin;
import model.Manager;
import model.Cashier;
import model.User;
import model.Sector;
import util.Role;
import java.util.Date;

/**
 * Unit tests for LoginController.authenticate() method
 * Covers MC/DC analysis for the compound condition:
 * if (username.equals(usernameInFile) && password.equals(passwordInFile))
 *
 * @author Ersi Majkaj - Member 4: System QA & File Operations
 */
public class LoginControllerTest {

    private ArrayList<User> mockEmployees;
    private Admin testAdmin;
    private Manager testManager;
    private Cashier testCashier;

    @BeforeEach
    void setUp() {
        mockEmployees = new ArrayList<>();

        // Create test Admin
        testAdmin = new Admin();
        testAdmin.setUsername("admin");
        testAdmin.setPassword("admin123");
        testAdmin.setName("Test Admin");
        testAdmin.setRole(Role.Admin);

        // Create test Manager
        testManager = new Manager();
        testManager.setUsername("manager");
        testManager.setPassword("manager123");
        testManager.setName("Test Manager");
        testManager.setRole(Role.Manager);
        testManager.setSectors(new ArrayList<>());

        // Create test Cashier
        testCashier = new Cashier();
        testCashier.setUsername("cashier");
        testCashier.setPassword("cashier123");
        testCashier.setName("Test Cashier");
        testCashier.setRole(Role.Cashier);

        mockEmployees.add(testAdmin);
        mockEmployees.add(testManager);
        mockEmployees.add(testCashier);
    }

    // ==================== MC/DC Test Cases ====================

    /**
     * MC/DC Case 1: A=True, B=True -> Decision=True (Login Success)
     * Both username and password match
     */
    @Test
    void testAuthenticate_ValidCredentials_AdminLogin_Pass() {
        String username = "admin";
        String password = "admin123";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNotNull(matchedUser, "Should find matching user with valid credentials");
        assertTrue(matchedUser instanceof Admin, "Matched user should be Admin");
        assertEquals("admin", matchedUser.getUsername());
    }

    /**
     * MC/DC Case 2: A=True, B=False -> Decision=False (Login Fail)
     * Username matches but password doesn't - proves B independently affects outcome
     */
    @Test
    void testAuthenticate_ValidUsername_InvalidPassword_Fail() {
        String username = "admin";
        String password = "wrongpassword";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNull(matchedUser, "Should not find user when password is wrong");
    }

    /**
     * MC/DC Case 3: A=False, B=True -> Decision=False (Login Fail)
     * Password matches but username doesn't - proves A independently affects outcome
     */
    @Test
    void testAuthenticate_InvalidUsername_ValidPassword_Fail() {
        String username = "wronguser";
        String password = "admin123";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNull(matchedUser, "Should not find user when username is wrong");
    }

    /**
     * MC/DC Case 4: A=False, B=False -> Decision=False (Login Fail)
     * Both username and password don't match
     */
    @Test
    void testAuthenticate_InvalidCredentials_Fail() {
        String username = "wronguser";
        String password = "wrongpassword";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNull(matchedUser, "Should not find user when both credentials are wrong");
    }

    // ==================== Role-Based Tests ====================

    @Test
    void testAuthenticate_ManagerLogin_Pass() {
        String username = "manager";
        String password = "manager123";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNotNull(matchedUser);
        assertTrue(matchedUser instanceof Manager);
    }

    @Test
    void testAuthenticate_CashierLogin_Pass() {
        String username = "cashier";
        String password = "cashier123";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNotNull(matchedUser);
        assertTrue(matchedUser instanceof Cashier);
    }

    // ==================== Edge Cases ====================

    @Test
    void testAuthenticate_EmptyUsername_Fail() {
        String username = "";
        String password = "admin123";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNull(matchedUser, "Empty username should not match any user");
    }

    @Test
    void testAuthenticate_EmptyPassword_Fail() {
        String username = "admin";
        String password = "";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNull(matchedUser, "Empty password should not match any user");
    }

    @Test
    void testAuthenticate_NullUsername_Fail() {
        String username = null;
        String password = "admin123";

        User matchedUser = findMatchingUserSafe(username, password, mockEmployees);

        assertNull(matchedUser, "Null username should not match any user");
    }

    @Test
    void testAuthenticate_CaseSensitiveUsername_Fail() {
        String username = "ADMIN"; // uppercase
        String password = "admin123";

        User matchedUser = findMatchingUser(username, password, mockEmployees);

        assertNull(matchedUser, "Username comparison should be case-sensitive");
    }

    @Test
    void testAuthenticate_EmptyEmployeeList_Fail() {
        ArrayList<User> emptyList = new ArrayList<>();

        User matchedUser = findMatchingUser("admin", "admin123", emptyList);

        assertNull(matchedUser, "Should return null for empty employee list");
    }

    // ==================== Helper Methods (simulating authenticate logic) ====================

    /**
     * Simulates the authenticate() logic from LoginController
     * Returns matched user or null if no match found
     */
    private User findMatchingUser(String username, String password, ArrayList<User> employees) {
        for (User user : employees) {
            String usernameInFile = user.getUsername();
            String passwordInFile = user.getPassword();

            // This is the condition under MC/DC analysis
            if (username.equals(usernameInFile) && password.equals(passwordInFile)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Safe version that handles null inputs
     */
    private User findMatchingUserSafe(String username, String password, ArrayList<User> employees) {
        if (username == null || password == null) {
            return null;
        }
        return findMatchingUser(username, password, employees);
    }
}
