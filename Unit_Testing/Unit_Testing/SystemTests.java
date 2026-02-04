package Unit_Testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import model.Item;
import model.Admin;
import model.Manager;
import model.Cashier;
import model.User;
import model.Sector;
import util.Role;

/**
 * System Tests for Electronic Store Application
 * Tests end-to-end scenarios across multiple components
 *
 * @author Ersi Majkaj - Member 4: System QA & File Operations
 */
public class SystemTests {

    private ArrayList<User> employees;
    private ArrayList<Item> inventory;
    private Admin testAdmin;
    private Manager testManager;
    private Cashier testCashier;
    private Sector testSector;

    @BeforeEach
    void setUp() {
        // Initialize test data
        employees = new ArrayList<>();
        inventory = new ArrayList<>();

        // Create sector
        testSector = new Sector("Electronics");

        // Create users
        testAdmin = new Admin();
        testAdmin.setUsername("admin");
        testAdmin.setPassword("admin123");
        testAdmin.setName("Test Admin");
        testAdmin.setRole(Role.Admin);
        testAdmin.setSalary(5000.0);

        testManager = new Manager();
        testManager.setUsername("manager");
        testManager.setPassword("manager123");
        testManager.setName("Test Manager");
        testManager.setRole(Role.Manager);
        testManager.setSalary(3500.0);
        ArrayList<Sector> sectors = new ArrayList<>();
        sectors.add(testSector);
        testManager.setSectors(sectors);

        testCashier = new Cashier();
        testCashier.setUsername("cashier");
        testCashier.setPassword("cashier123");
        testCashier.setName("Test Cashier");
        testCashier.setRole(Role.Cashier);
        testCashier.setSalary(2000.0);
        testCashier.setSector(testSector);

        employees.add(testAdmin);
        employees.add(testManager);
        employees.add(testCashier);

        // Create inventory items
        inventory.add(new Item("Laptop", "Electronics", 1000.0, 10, "Computers", "Gaming Laptop", "TechCorp", ""));
        inventory.add(new Item("Mouse", "Electronics", 25.0, 50, "Accessories", "Wireless Mouse", "TechCorp", ""));
        inventory.add(new Item("Keyboard", "Electronics", 75.0, 30, "Accessories", "Mechanical", "TechCorp", ""));
        inventory.add(new Item("Monitor", "Electronics", 300.0, 3, "Display", "27 inch", "TechCorp", "")); // Low stock
    }

    // ==================== TC-S01: Full Sale Cycle ====================
    @Test
    @DisplayName("TC-S01: Full Sale Cycle - Manager adds item, Cashier sells, inventory updates")
    void testFullSaleCycle() {
        // Step 1: Manager adds new item
        Item newItem = new Item("Headphones", "Electronics", 150.0, 20, "Audio", "Wireless", "AudioCorp", "");
        inventory.add(newItem);

        assertEquals(5, inventory.size(), "Inventory should have 5 items after adding");

        // Step 2: Cashier sells item
        Item itemToSell = findItemByName("Headphones");
        assertNotNull(itemToSell, "Item should exist in inventory");

        int initialStock = itemToSell.getStockQuantity();
        int quantityToSell = 3;

        itemToSell.sellItem(quantityToSell);

        // Step 3: Verify inventory updated
        assertEquals(initialStock - quantityToSell, itemToSell.getStockQuantity(), "Stock should decrease after sale");
        assertEquals(quantityToSell, itemToSell.getItemsSold(), "Items sold should increase");
    }

    // ==================== TC-S02: Login Security Test ====================
    @Test
    @DisplayName("TC-S02: Login Security - Multiple invalid attempts handled correctly")
    void testLoginSecurityMultipleInvalidAttempts() {
        // Simulate 3 invalid login attempts
        for (int attempt = 1; attempt <= 3; attempt++) {
            User result = authenticateUser("wronguser" + attempt, "wrongpass" + attempt);
            assertNull(result, "Attempt " + attempt + " should fail with invalid credentials");
        }

        // System should still allow valid login after failed attempts
        User validResult = authenticateUser("admin", "admin123");
        assertNotNull(validResult, "Valid credentials should still work after failed attempts");
    }

    // ==================== TC-S03: Employee Modification Flow ====================
    @Test
    @DisplayName("TC-S03: Admin modifies Manager salary - Changes persist")
    void testEmployeeModificationFlow() {
        // Step 1: Admin finds manager
        User managerUser = findUserByUsername("manager");
        assertNotNull(managerUser, "Manager should exist");

        double oldSalary = managerUser.getSalary();
        double newSalary = 4000.0;

        // Step 2: Admin modifies salary
        managerUser.setSalary(newSalary);

        // Step 3: Verify change persisted
        User updatedManager = findUserByUsername("manager");
        assertEquals(newSalary, updatedManager.getSalary(), "Salary should be updated");
        assertNotEquals(oldSalary, updatedManager.getSalary(), "Salary should differ from original");
    }

    // ==================== TC-S04: Restock Workflow ====================
    @Test
    @DisplayName("TC-S04: Manager restocks low-stock item")
    void testRestockWorkflow() {
        // Step 1: Find low stock item (stock < 5)
        Item lowStockItem = findLowStockItem();
        assertNotNull(lowStockItem, "Should find a low stock item");
        assertTrue(lowStockItem.getStockQuantity() < 5, "Item should have low stock");

        int initialStock = lowStockItem.getStockQuantity();
        int restockQuantity = 20;

        // Step 2: Manager restocks
        lowStockItem.restockItem(restockQuantity);

        // Step 3: Verify stock increased
        assertEquals(initialStock + restockQuantity, lowStockItem.getStockQuantity(),
                "Stock should increase by restock quantity");
        assertTrue(lowStockItem.getStockQuantity() >= 5, "Item should no longer be low stock");
    }

    // ==================== TC-S05: Role-Based Access Control ====================
    @Test
    @DisplayName("TC-S05: Role-based access - Users have correct roles")
    void testRoleBasedAccessControl() {
        // Verify each user has correct role
        User admin = findUserByUsername("admin");
        User manager = findUserByUsername("manager");
        User cashier = findUserByUsername("cashier");

        assertEquals(Role.Admin, admin.getRole(), "Admin should have Admin role");
        assertEquals(Role.Manager, manager.getRole(), "Manager should have Manager role");
        assertEquals(Role.Cashier, cashier.getRole(), "Cashier should have Cashier role");

        // Verify role hierarchy (Admin > Manager > Cashier by salary)
        assertTrue(admin.getSalary() > manager.getSalary(), "Admin salary should exceed Manager");
        assertTrue(manager.getSalary() > cashier.getSalary(), "Manager salary should exceed Cashier");
    }

    // ==================== TC-S06: Admin Creates New Employee ====================
    @Test
    @DisplayName("TC-S06: Admin creates new Manager account - Manager can login")
    void testAdminCreatesManagerFlow() {
        // Step 1: Admin creates new manager
        Manager newManager = new Manager();
        newManager.setUsername("newmanager");
        newManager.setPassword("newpass123");
        newManager.setName("New Manager");
        newManager.setRole(Role.Manager);
        newManager.setSalary(3000.0);

        employees.add(newManager);

        // Step 2: Verify manager exists in system
        assertEquals(4, employees.size(), "Should have 4 employees now");

        // Step 3: New manager can authenticate
        User authenticated = authenticateUser("newmanager", "newpass123");
        assertNotNull(authenticated, "New manager should be able to login");
        assertTrue(authenticated instanceof Manager, "Authenticated user should be Manager type");
    }

    // ==================== TC-S07: Inventory Low Stock Alert ====================
    @Test
    @DisplayName("TC-S07: System identifies all low stock items correctly")
    void testInventoryLowStockAlert() {
        ArrayList<Item> lowStockItems = new ArrayList<>();

        for (Item item : inventory) {
            if (item.getStockQuantity() < 5) {
                lowStockItems.add(item);
            }
        }

        // Should find Monitor with stock = 3
        assertEquals(1, lowStockItems.size(), "Should find 1 low stock item");
        assertEquals("Monitor", lowStockItems.get(0).getItemName(), "Low stock item should be Monitor");
    }

    // ==================== TC-S08: Sale Exceeding Stock ====================
    @Test
    @DisplayName("TC-S08: Sale attempt exceeding stock is rejected")
    void testSaleExceedingStock() {
        Item item = findItemByName("Monitor"); // Has 3 in stock
        assertNotNull(item);

        int initialStock = item.getStockQuantity();
        int initialSold = item.getItemsSold();

        // Try to sell more than available
        item.sellItem(10); // Try to sell 10 when only 3 available

        // Stock should remain unchanged
        assertEquals(initialStock, item.getStockQuantity(), "Stock should not change when sale exceeds availability");
        assertEquals(initialSold, item.getItemsSold(), "Items sold should not change");
    }

    // ==================== TC-S09: Complete User Session Flow ====================
    @Test
    @DisplayName("TC-S09: Complete user session - Login, perform action, data persists")
    void testCompleteUserSessionFlow() {
        // Step 1: Cashier logs in
        User cashier = authenticateUser("cashier", "cashier123");
        assertNotNull(cashier, "Cashier should login successfully");

        // Step 2: Cashier makes a sale
        Item item = findItemByName("Mouse");
        int initialStock = item.getStockQuantity();
        item.sellItem(5);

        // Step 3: Verify sale recorded
        assertEquals(initialStock - 5, item.getStockQuantity());
        assertEquals(5, item.getItemsSold());

        // Step 4: "Logout" and re-authenticate
        User reloggedCashier = authenticateUser("cashier", "cashier123");
        assertNotNull(reloggedCashier, "Cashier should be able to re-login");

        // Step 5: Data should persist (item still shows sold)
        Item sameItem = findItemByName("Mouse");
        assertEquals(5, sameItem.getItemsSold(), "Sales data should persist across sessions");
    }

    // ==================== TC-S10: Sector-Based Item Organization ====================
    @Test
    @DisplayName("TC-S10: Items correctly organized by sector")
    void testSectorBasedItemOrganization() {
        // All items should belong to Electronics sector
        for (Item item : inventory) {
            assertEquals("Electronics", item.getItemSector(),
                    "All items should belong to Electronics sector");
        }

        // Manager should have access to Electronics sector
        assertTrue(testManager.getSectors().contains(testSector),
                "Manager should have Electronics sector assigned");

        // Cashier should be assigned to Electronics sector
        assertEquals(testSector, testCashier.getSector(),
                "Cashier should be assigned to Electronics sector");
    }

    // ==================== Helper Methods ====================

    private User authenticateUser(String username, String password) {
        for (User user : employees) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private User findUserByUsername(String username) {
        for (User user : employees) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private Item findItemByName(String name) {
        for (Item item : inventory) {
            if (item.getItemName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    private Item findLowStockItem() {
        for (Item item : inventory) {
            if (item.getStockQuantity() < 5) {
                return item;
            }
        }
        return null;
    }
}
