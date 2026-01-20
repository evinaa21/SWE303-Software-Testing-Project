package system_testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Item;
import model.Bill;
import model.SalesMetrics;
import java.util.ArrayList;
import java.util.Date;

public class EmaSystemTest {

    /**
     * System Test: Cashier Workflow
     * End-to-end test simulating a cashier's complete transaction process.
     * Covers item selection, bill creation, total calculation, stock updates,
     * and sales metrics integration to ensure the entire sales flow works correctly.
     */
    @Test
    void testCashierCompleteTransactionWorkflow() {
        // Step 1: Set up inventory (simulate manager adding items)
        Item laptop = new Item("Laptop", "Electronics", 1000.0, 10, "Laptop", "Gaming Laptop", "SupplierA", "laptop.jpg");
        Item mouse = new Item("Mouse", "Electronics", 25.0, 20, "Mouse", "Wireless Mouse", "SupplierB", "mouse.jpg");

        ArrayList<Item> availableItems = new ArrayList<>();
        availableItems.add(laptop);
        availableItems.add(mouse);

        // Step 2: Simulate cashier selecting items for bill
        ArrayList<Item> selectedItems = new ArrayList<>();
        selectedItems.add(new Item("Laptop", "Electronics", 1000.0, 1, 0));  // 1 laptop
        selectedItems.add(new Item("Mouse", "Accessories", 25.0, 2, 0));   // 2 mice

        // Step 3: Create bill and calculate total
        Bill bill = new Bill("BILL-TEST-001", selectedItems, 0.0, new Date());
        double total = bill.calculateTotal();

        // Verify bill total
        double expectedTotal = (1000.0 * 1) + (25.0 * 2);  // 1025.0
        assertEquals(expectedTotal, total, 0.001, "Bill total should be calculated correctly");

        // Step 4: Simulate transaction completion (update inventory)
        laptop.sellItem(1);  // Reduce stock by 1
        mouse.sellItem(2);   // Reduce stock by 2

        // Verify stock updates
        assertEquals(9, laptop.getStockQuantity(), "Laptop stock should be reduced after sale");
        assertEquals(18, mouse.getStockQuantity(), "Mouse stock should be reduced after sale");
        assertEquals(1, laptop.getItemsSold(), "Laptop items sold should be incremented");
        assertEquals(2, mouse.getItemsSold(), "Mouse items sold should be incremented");

        // Step 5: Verify sales metrics integration
        SalesMetrics metrics = new SalesMetrics();
        metrics.calculateMetrics(availableItems);

        double expectedRevenue = (1000.0 * 1) + (25.0 * 2);  // 1025.0
        int expectedItemsSold = 1 + 2;  // 3

        assertEquals(expectedRevenue, metrics.getTotalRevenue(), 0.001,
            "Sales metrics should reflect the transaction revenue");
        assertEquals(expectedItemsSold, metrics.getTotalItemsSold(),
            "Sales metrics should reflect total items sold");

        // Step 6: Simulate bill saving (in a real system, this would write to file)
        // For testing, just verify bill state
        assertEquals("BILL-TEST-001", bill.getBillNumber(), "Bill should have correct number");
        assertEquals(expectedTotal, bill.getTotalAmount(), "Bill total amount should match calculated total");
    }
}