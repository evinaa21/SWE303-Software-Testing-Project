package integration_testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Item;
import model.SalesMetrics;
import java.util.ArrayList;

public class EmaIntegrationTest {

    /**
     * Integration Test: Item → SalesMetrics
     * Verified that item sales operations correctly propagate to the sales reporting module.
     * When items are sold using sellItem, the updated itemsSold values are accurately consumed
     * by SalesMetrics.calculateMetrics, ensuring total revenue and total items sold reflect
     * real sales activity without data loss or inconsistency.
     */
    @Test
    void testItemToSalesMetricsIntegration() {
        // Create test items
        Item laptop = new Item("Laptop", "Electronics", 1000.0, 10, 0);
        Item mouse = new Item("Mouse", "Accessories", 25.0, 20, 0);

        // Simulate selling items
        laptop.sellItem(2);  // Sell 2 laptops
        mouse.sellItem(5);   // Sell 5 mice

        // Create a list of items
        ArrayList<Item> items = new ArrayList<>();
        items.add(laptop);
        items.add(mouse);

        // Create SalesMetrics and calculate metrics
        SalesMetrics metrics = new SalesMetrics();
        metrics.calculateMetrics(items);

        // Verify calculations
        double expectedRevenue = (1000.0 * 2) + (25.0 * 5);  // 2000 + 125 = 2125
        int expectedItemsSold = 2 + 5;  // 7

        assertEquals(expectedRevenue, metrics.getTotalRevenue(), 0.001,
            "Total revenue should reflect sold items accurately");
        assertEquals(expectedItemsSold, metrics.getTotalItemsSold(),
            "Total items sold should match the sum of itemsSold");

        // Verify item states
        assertEquals(2, laptop.getItemsSold(), "Laptop should have 2 items sold");
        assertEquals(5, mouse.getItemsSold(), "Mouse should have 5 items sold");
        assertEquals(8, laptop.getStockQuantity(), "Laptop stock should be reduced by 2");
        assertEquals(15, mouse.getStockQuantity(), "Mouse stock should be reduced by 5");
    }
}