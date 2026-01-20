package system_testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Item;
import model.Sector;
import model.SalesMetrics;
import model.Admin;
import model.Manager;
import model.Cashier;
import util.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import util.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class EvinaSystemTest {

    /**
     * System Test: Manager/Admin Workflow
     * End-to-end test simulating a manager/admin's inventory and employee management process.
     * Covers adding items to sectors, restocking, employee management, and report generation
     * to ensure complete operational workflow integrity.
     */
    @Test
    void testManagerAdminInventoryAndReportingWorkflow() {
        // Step 1: Set up sectors and initial inventory
        Sector electronicsSector = new Sector("Electronics");
        Sector accessoriesSector = new Sector("Accessories");

        Item laptop = new Item("Laptop", "Electronics", 1000.0, 5, "Laptop", "Gaming Laptop", "SupplierA", "laptop.jpg");
        Item mouse = new Item("Mouse", "Accessories", 25.0, 10, "Mouse", "Wireless Mouse", "SupplierB", "mouse.jpg");

        // Step 2: Manager adds items to sectors
        electronicsSector.addItem(laptop);
        accessoriesSector.addItem(mouse);

        // Verify items are added correctly
        assertEquals(1, electronicsSector.viewItems().size(), "Electronics sector should have 1 item");
        assertEquals(1, accessoriesSector.viewItems().size(), "Accessories sector should have 1 item");
        assertTrue(electronicsSector.viewItems().contains(laptop), "Electronics sector should contain laptop");
        assertTrue(accessoriesSector.viewItems().contains(mouse), "Accessories sector should contain mouse");

        // Step 3: Simulate low stock detection and restocking
        // Laptop has 5 stock, assume threshold is 5, so restock
        laptop.restockItem(10);  // Add 10 more laptops
        mouse.restockItem(5);    // Add 5 more mice

        // Verify restocking
        assertEquals(15, laptop.getStockQuantity(), "Laptop stock should be increased after restocking");
        assertEquals(15, mouse.getStockQuantity(), "Mouse stock should be increased after restocking");

        // Step 4: Simulate sales (reduce stock)
        laptop.sellItem(3);
        mouse.sellItem(7);

        // Verify stock after sales
        assertEquals(12, laptop.getStockQuantity(), "Laptop stock should be reduced after sales");
        assertEquals(8, mouse.getStockQuantity(), "Mouse stock should be reduced after sales");
        assertEquals(3, laptop.getItemsSold(), "Laptop items sold should be tracked");
        assertEquals(7, mouse.getItemsSold(), "Mouse items sold should be tracked");

        // Step 5: Admin manages employees
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.JANUARY, 1);
        Date birthDate = calendar.getTime();

        Admin admin = new Admin("Admin User", 60000, Role.Admin, "admin", "pass", birthDate, "123-456-7890", "admin@email.com");
        
        ArrayList<Sector> managerSectors = new ArrayList<>();
        managerSectors.add(electronicsSector);
        Manager manager = new Manager("Manager User", 45000, Role.Manager, "manager", "pass", birthDate, "098-765-4321", "manager@email.com", managerSectors);
        
        Cashier cashier = new Cashier("Cashier User", 30000, Role.Cashier, "cashier", "pass", birthDate, "111-222-3333", "cashier@email.com", electronicsSector);

        ArrayList<model.User> employees = new ArrayList<>();
        employees.add(admin);
        employees.add(manager);
        employees.add(cashier);

        // Verify employee setup
        assertEquals(3, employees.size(), "Should have 3 employees");
        assertEquals(Role.Admin, admin.getRole(), "Admin should have correct role");
        assertEquals(Role.Manager, manager.getRole(), "Manager should have correct role");
        assertEquals(Role.Cashier, cashier.getRole(), "Cashier should have correct role");

        // Step 6: Generate sales report
        ArrayList<Item> allItems = new ArrayList<>();
        allItems.add(laptop);
        allItems.add(mouse);

        SalesMetrics metrics = new SalesMetrics();
        metrics.calculateMetrics(allItems);
        metrics.calculateAdminMetrics(allItems, employees);

        // Verify metrics
        double expectedRevenue = (1000.0 * 3) + (25.0 * 7);  // 3000 + 175 = 3175
        int expectedItemsSold = 3 + 7;  // 10

        assertEquals(expectedRevenue, metrics.getTotalRevenue(), 0.001,
            "Total revenue should reflect all sales");
        assertEquals(expectedItemsSold, metrics.getTotalItemsSold(),
            "Total items sold should be correct");

        // Verify costs calculation (items cost + salaries)
        double expectedItemCosts = (1000.0 * 12) + (25.0 * 8);  // Current stock costs
        double expectedSalaries = 60000 + 45000 + 30000;  // 135000
        double expectedTotalCosts = expectedItemCosts + expectedSalaries;

        assertEquals(expectedTotalCosts, metrics.getTotalCosts(), 0.001,
            "Total costs should include item costs and salaries");
    }
}