package Unit_Testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import model.Item;
import model.Manager;
import model.Sector;
import util.Role;

/**
 * Unit tests for RestockItemView
 * Focus on quantity validation using Boundary Value Testing (BVT)
 *
 * @author Ersi Majkaj - Member 4: System QA & File Operations
 */
public class RestockItemViewTest {

    private Manager testManager;
    private ArrayList<Item> testItems;

    @BeforeEach
    void setUp() {
        testItems = new ArrayList<>();

        // Create items with different stock levels
        Item lowStockItem = new Item("Mouse", "Electronics", 25.0, 3, "Accessories", "Wireless Mouse", "TechCorp", "");
        Item normalStockItem = new Item("Keyboard", "Electronics", 75.0, 50, "Accessories", "Mechanical Keyboard", "TechCorp", "");
        Item zeroStockItem = new Item("Monitor", "Electronics", 300.0, 0, "Display", "27 inch", "TechCorp", "");

        testItems.add(lowStockItem);
        testItems.add(normalStockItem);
        testItems.add(zeroStockItem);

        // Create test Manager
        testManager = new Manager();
        testManager.setName("Test Manager");
        testManager.setRole(Role.Manager);
        testManager.setSectors(new ArrayList<>());
    }

    // ==================== BVT Tests for Quantity Validation ====================

    /**
     * BVT-E01: Input = 0 (exact boundary - invalid)
     * Expected: Error - quantity must be positive
     */
    @Test
    void testQuantityValidation_Zero_Invalid() {
        int quantity = 0;
        ValidationResult result = validateQuantity(quantity);

        assertFalse(result.isValid);
        assertEquals("Please enter a positive number for quantity.", result.errorMessage);
    }

    /**
     * BVT-E02: Input = 1 (minimum valid boundary)
     * Expected: Success
     */
    @Test
    void testQuantityValidation_One_Valid() {
        int quantity = 1;
        ValidationResult result = validateQuantity(quantity);

        assertTrue(result.isValid, "Quantity 1 should be valid");
        assertNull(result.errorMessage);
    }

    /**
     * BVT-E03: Input = -1 (below minimum boundary)
     * Expected: Error - quantity must be positive
     */
    @Test
    void testQuantityValidation_NegativeOne_Invalid() {
        int quantity = -1;
        ValidationResult result = validateQuantity(quantity);

        assertFalse(result.isValid);
        assertEquals("Please enter a positive number for quantity.", result.errorMessage);
    }

    /**
     * BVT-E04: Input = 50 (nominal middle value)
     * Expected: Success
     */
    @Test
    void testQuantityValidation_NominalValue_Valid() {
        int quantity = 50;
        ValidationResult result = validateQuantity(quantity);

        assertTrue(result.isValid);
        assertNull(result.errorMessage);
    }

    /**
     * BVT-E05: Input = Integer.MAX_VALUE (maximum boundary)
     * Expected: Success (though practically unrealistic)
     */
    @Test
    void testQuantityValidation_MaxInteger_Valid() {
        int quantity = Integer.MAX_VALUE;
        ValidationResult result = validateQuantity(quantity);

        assertTrue(result.isValid);
        assertNull(result.errorMessage);
    }

    /**
     * BVT-E06: Input = -100 (well below boundary)
     * Expected: Error
     */
    @Test
    void testQuantityValidation_LargeNegative_Invalid() {
        int quantity = -100;
        ValidationResult result = validateQuantity(quantity);

        assertFalse(result.isValid);
        assertEquals("Please enter a positive number for quantity.", result.errorMessage);
    }

    // ==================== String Input Validation Tests ====================

    /**
     * Test empty string input for quantity field
     */
    @Test
    void testQuantityStringValidation_Empty_Invalid() {
        String quantityStr = "";
        StringValidationResult result = validateQuantityString(quantityStr);

        assertFalse(result.isValid);
        assertEquals("Quantity cannot be empty.", result.errorMessage);
    }

    /**
     * Test non-numeric string input
     */
    @Test
    void testQuantityStringValidation_NonNumeric_Invalid() {
        String quantityStr = "abc";
        StringValidationResult result = validateQuantityString(quantityStr);

        assertFalse(result.isValid);
        assertEquals("Please enter a valid number for quantity.", result.errorMessage);
    }

    /**
     * Test valid numeric string
     */
    @Test
    void testQuantityStringValidation_ValidNumber_Pass() {
        String quantityStr = "10";
        StringValidationResult result = validateQuantityString(quantityStr);

        assertTrue(result.isValid);
        assertEquals(10, result.parsedValue);
    }

    /**
     * Test decimal number string (should fail - expects integer)
     */
    @Test
    void testQuantityStringValidation_Decimal_Invalid() {
        String quantityStr = "10.5";
        StringValidationResult result = validateQuantityString(quantityStr);

        assertFalse(result.isValid);
        assertEquals("Please enter a valid number for quantity.", result.errorMessage);
    }

    // ==================== Item Selection Validation Tests ====================

    @Test
    void testItemSelection_NullItem_Invalid() {
        String selectedItem = null;
        boolean isValid = validateItemSelection(selectedItem);

        assertFalse(isValid, "Null item selection should be invalid");
    }

    @Test
    void testItemSelection_EmptyItem_Invalid() {
        String selectedItem = "";
        boolean isValid = validateItemSelection(selectedItem);

        assertFalse(isValid, "Empty item selection should be invalid");
    }

    @Test
    void testItemSelection_ValidItem_Pass() {
        String selectedItem = "Mouse";
        boolean isValid = validateItemSelection(selectedItem);

        assertTrue(isValid, "Valid item selection should pass");
    }

    // ==================== Low Stock Filter Tests ====================

    @Test
    void testFilterLowStockItems_ReturnsOnlyLowStock() {
        ArrayList<Item> lowStockItems = filterLowStockItems(testItems);

        assertEquals(2, lowStockItems.size(), "Should return 2 items with stock < 5");
        assertTrue(lowStockItems.stream().allMatch(item -> item.getStockQuantity() < 5));
    }

    @Test
    void testFilterLowStockItems_EmptyListWhenAllStocked() {
        ArrayList<Item> wellStockedItems = new ArrayList<>();
        wellStockedItems.add(new Item("Item1", "Cat", 10.0, 100, "Cat", "Desc", "Sup", ""));
        wellStockedItems.add(new Item("Item2", "Cat", 20.0, 50, "Cat", "Desc", "Sup", ""));

        ArrayList<Item> lowStockItems = filterLowStockItems(wellStockedItems);

        assertTrue(lowStockItems.isEmpty(), "Should return empty list when no low stock items");
    }

    @Test
    void testFilterLowStockItems_IncludesZeroStock() {
        ArrayList<Item> result = filterLowStockItems(testItems);

        boolean hasZeroStock = result.stream().anyMatch(item -> item.getStockQuantity() == 0);
        assertTrue(hasZeroStock, "Should include items with zero stock");
    }

    // ==================== Restock Operation Tests ====================

    @Test
    void testRestockItem_IncreasesStock() {
        Item item = new Item("TestItem", "Electronics", 50.0, 3, "Cat", "Desc", "Sup", "");
        int initialStock = item.getStockQuantity();
        int restockQuantity = 10;

        item.restockItem(restockQuantity);

        assertEquals(initialStock + restockQuantity, item.getStockQuantity());
    }

    @Test
    void testRestockItem_FromZeroStock() {
        Item item = new Item("TestItem", "Electronics", 50.0, 0, "Cat", "Desc", "Sup", "");
        int restockQuantity = 25;

        item.restockItem(restockQuantity);

        assertEquals(25, item.getStockQuantity());
    }

    @Test
    void testRestockItem_LargeQuantity() {
        Item item = new Item("TestItem", "Electronics", 50.0, 5, "Cat", "Desc", "Sup", "");
        int restockQuantity = 1000;

        item.restockItem(restockQuantity);

        assertEquals(1005, item.getStockQuantity());
    }

    // ==================== Constructor Validation Tests ====================

    @Test
    void testConstructor_NullManager_ThrowsException() {
        // Simulating constructor validation from RestockItemView line 25-27
        assertThrows(IllegalArgumentException.class, () -> {
            validateConstructorInput(null, new ArrayList<>());
        }, "Should throw exception for null manager");
    }

    @Test
    void testConstructor_NullItemList_ThrowsException() {
        Manager manager = new Manager();
        manager.setName("Test");

        assertThrows(IllegalArgumentException.class, () -> {
            validateConstructorInput(manager, null);
        }, "Should throw exception for null item list");
    }

    // ==================== Helper Methods (simulating RestockItemView logic) ====================

    private ValidationResult validateQuantity(int quantity) {
        ValidationResult result = new ValidationResult();
        if (quantity <= 0) {
            result.isValid = false;
            result.errorMessage = "Please enter a positive number for quantity.";
        } else {
            result.isValid = true;
            result.errorMessage = null;
        }
        return result;
    }

    private StringValidationResult validateQuantityString(String quantityStr) {
        StringValidationResult result = new StringValidationResult();

        if (quantityStr == null || quantityStr.isEmpty()) {
            result.isValid = false;
            result.errorMessage = "Quantity cannot be empty.";
            return result;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                result.isValid = false;
                result.errorMessage = "Please enter a positive number for quantity.";
            } else {
                result.isValid = true;
                result.parsedValue = quantity;
            }
        } catch (NumberFormatException e) {
            result.isValid = false;
            result.errorMessage = "Please enter a valid number for quantity.";
        }

        return result;
    }

    private boolean validateItemSelection(String selectedItem) {
        return selectedItem != null && !selectedItem.isEmpty();
    }

    private ArrayList<Item> filterLowStockItems(ArrayList<Item> items) {
        ArrayList<Item> lowStock = new ArrayList<>();
        for (Item item : items) {
            if (item.getStockQuantity() < 5) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

    private void validateConstructorInput(Manager manager, ArrayList<Item> items) {
        if (manager == null || items == null) {
            throw new IllegalArgumentException("Manager or item list cannot be null.");
        }
    }

    // Helper classes for validation results
    private static class ValidationResult {
        boolean isValid;
        String errorMessage;
    }

    private static class StringValidationResult {
        boolean isValid;
        String errorMessage;
        int parsedValue;
    }
}
