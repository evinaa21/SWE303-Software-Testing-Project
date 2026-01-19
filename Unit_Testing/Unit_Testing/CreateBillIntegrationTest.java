package Unit_Testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import model.Bill;
import model.Item;
import util.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Integration Tests for CreateBillController and FileHandler interaction.
 * Tests the "handshake" between bill creation and file persistence.
 *
 * Member 3 - Cashier & Integration Specialist
 * SWE 303 - Software Testing Project
 */
public class CreateBillIntegrationTest {

    private FileHandler fileHandler;
    private static final String BILL_DIRECTORY = "src/BinaryFiles/Bills/";
    private static final String INVENTORY_FILE = "src/BinaryFiles/items.dat";

    @BeforeEach
    void setUp() {
        fileHandler = new FileHandler();
    }

    // =========================================================================
    // INTEGRATION TEST 1: Bill Save and Load Integration
    // Tests if a saved bill can be retrieved from the file system
    // =========================================================================

    @Test
    @DisplayName("Integration: Save bill and verify it appears in bill history")
    void testSaveBillIntegration() {
        // Arrange
        String billNumber = "TEST-BILL-" + System.currentTimeMillis();
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Test Laptop", "Electronics", 999.99, 1, 0));
        items.add(new Item("Test Mouse", "Accessories", 29.99, 2, 0));
        double total = 999.99 + (29.99 * 2); // 1059.97
        String cashierName = "TestCashier";
        String sector = "TestSector";

        // Act - Save the bill
        fileHandler.saveBill(billNumber, items, total, cashierName, sector);

        // Assert - Verify bill file was created
        File billFile = findBillFile(billNumber);
        assertNotNull(billFile, "Bill file should exist after saving");
        assertTrue(billFile.exists(), "Bill file should be present in Bills directory");

        // Cleanup
        if (billFile != null && billFile.exists()) {
            billFile.delete();
        }
    }

    @Test
    @DisplayName("Integration: Load bills returns today's bills only")
    void testLoadBillsFiltersByDate() {
        // Act
        ArrayList<Bill> todaysBills = fileHandler.loadBills();

        // Assert - All returned bills should be from today
        Date today = new Date();
        for (Bill bill : todaysBills) {
            assertTrue(fileHandler.isSameDay(bill.getSaleDate(), today),
                    "All loaded bills should be from today");
        }
    }

    // =========================================================================
    // INTEGRATION TEST 2: Inventory Update After Sale
    // Tests if selling items correctly updates the inventory file
    // =========================================================================

    @Test
    @DisplayName("Integration: Inventory updates correctly after sale")
    void testInventoryUpdateAfterSale() {
        // Arrange - Get initial inventory state
        ArrayList<Item> initialInventory = fileHandler.loadInventory();

        // Skip test if no inventory
        if (initialInventory.isEmpty()) {
            System.out.println("Skipping test: No items in inventory");
            return;
        }

        // Find an item with sufficient stock
        Item testItem = null;
        int initialStock = 0;
        for (Item item : initialInventory) {
            if (item.getStockQuantity() >= 2) {
                testItem = item;
                initialStock = item.getStockQuantity();
                break;
            }
        }

        if (testItem == null) {
            System.out.println("Skipping test: No items with sufficient stock");
            return;
        }

        // Act - Create a sale item
        ArrayList<Item> soldItems = new ArrayList<>();
        Item saleItem = new Item(testItem.getItemName(), testItem.getCategory(),
                testItem.getSellingPrice(), 1, 0);
        soldItems.add(saleItem);

        try {
            fileHandler.updateInventoryForSale(soldItems);

            // Assert - Verify stock was reduced
            ArrayList<Item> updatedInventory = fileHandler.loadInventory();
            Item updatedItem = findItemByName(updatedInventory, testItem.getItemName());

            assertNotNull(updatedItem, "Item should still exist in inventory");
            assertEquals(initialStock - 1, updatedItem.getStockQuantity(),
                    "Stock should be reduced by sale quantity");

            // Restore original stock
            updatedItem.restockItem(1);
            fileHandler.saveInventory(updatedInventory);

        } catch (Exception e) {
            fail("updateInventoryForSale threw exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Integration: Insufficient stock throws exception")
    void testInsufficientStockThrowsException() {
        // Arrange
        ArrayList<Item> soldItems = new ArrayList<>();
        // Try to sell more than any item could have
        soldItems.add(new Item("NonExistentItem", "Test", 100.0, 99999, 0));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            fileHandler.updateInventoryForSale(soldItems);
        }, "Should throw exception for item not in inventory");
    }

    // =========================================================================
    // INTEGRATION TEST 3: Category Filter Integration
    // Tests if filtering items by category works correctly
    // =========================================================================

    @Test
    @DisplayName("Integration: Filter items by category returns correct items")
    void testFilterItemsByCategory() {
        // Arrange - Load all items and find a category
        ArrayList<Item> allItems = fileHandler.loadInventory();

        if (allItems.isEmpty()) {
            System.out.println("Skipping test: No items in inventory");
            return;
        }

        String testCategory = allItems.get(0).getCategory();

        // Act
        ArrayList<Item> filteredItems = fileHandler.filterItemsByCategory(testCategory);

        // Assert
        assertFalse(filteredItems.isEmpty(), "Should return items for valid category");
        for (Item item : filteredItems) {
            assertEquals(testCategory.toLowerCase(), item.getCategory().toLowerCase(),
                    "All filtered items should belong to the requested category");
        }
    }

    @Test
    @DisplayName("Integration: Filter by non-existent category returns empty list")
    void testFilterByInvalidCategory() {
        // Act
        ArrayList<Item> filteredItems = fileHandler.filterItemsByCategory("NonExistentCategory123");

        // Assert
        assertTrue(filteredItems.isEmpty(), "Should return empty list for non-existent category");
    }

    // =========================================================================
    // INTEGRATION TEST 4: Bill Calculation Integration
    // Tests if bill total calculation matches saved values
    // =========================================================================

    @Test
    @DisplayName("Integration: Bill total calculation is consistent")
    void testBillTotalCalculation() {
        // Arrange
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Item1", "Cat1", 100.0, 2, 0)); // 200
        items.add(new Item("Item2", "Cat2", 50.0, 3, 0));  // 150
        items.add(new Item("Item3", "Cat3", 25.0, 4, 0));  // 100

        Bill bill = new Bill("CALC-TEST", items, 0.0, new Date());

        // Act
        double calculatedTotal = bill.calculateTotal();

        // Assert
        assertEquals(450.0, calculatedTotal, 0.01, "Total should be 200+150+100=450");
        assertEquals(calculatedTotal, bill.getTotalAmount(),
                "getTotalAmount should match calculateTotal");
    }

    // =========================================================================
    // INTEGRATION TEST 5: Sector Inventory Integration
    // Tests if loading inventory by sector works correctly
    // =========================================================================

    @Test
    @DisplayName("Integration: Load inventory by sector filters correctly")
    void testLoadInventoryBySector() {
        // Arrange - Get all items and find a sector
        ArrayList<Item> allItems = fileHandler.loadInventory();

        if (allItems.isEmpty()) {
            System.out.println("Skipping test: No items in inventory");
            return;
        }

        String testSector = allItems.get(0).getItemSector();

        // Act
        ArrayList<Item> sectorItems = fileHandler.loadInventoryBySector(testSector);

        // Assert
        for (Item item : sectorItems) {
            assertEquals(testSector.toLowerCase(), item.getItemSector().toLowerCase(),
                    "All items should belong to the requested sector");
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Finds a bill file by bill number in the Bills directory.
     */
    private File findBillFile(String billNumber) {
        File billDir = new File(BILL_DIRECTORY);
        if (!billDir.exists() || !billDir.isDirectory()) {
            return null;
        }

        File[] files = billDir.listFiles((dir, name) -> name.contains(billNumber));
        return (files != null && files.length > 0) ? files[0] : null;
    }

    /**
     * Finds an item by name in the inventory list.
     */
    private Item findItemByName(ArrayList<Item> inventory, String itemName) {
        for (Item item : inventory) {
            if (item.getItemName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
}
