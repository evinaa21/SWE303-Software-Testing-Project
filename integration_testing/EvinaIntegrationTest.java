package integration_testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Item;
import model.Sector;
import java.util.ArrayList;

public class EvinaIntegrationTest {

    /**
     * Integration Test: Sector → Item
     * Verified that inventory management operations maintain consistency across components.
     * Items added to or removed from a Sector are immediately reflected in the sector's item list
     * via viewItems, confirming correct interaction between inventory storage and retrieval logic.
     */
    @Test
    void testSectorToItemIntegration() {
        // Create a sector
        Sector electronicsSector = new Sector("Electronics");

        // Create test items
        Item laptop = new Item("Laptop", "Electronics", 1000.0, 10, "Laptop", "Gaming Laptop", "SupplierA", "image.jpg");
        Item mouse = new Item("Mouse", "Electronics", 25.0, 20, "Mouse", "Wireless Mouse", "SupplierB", "mouse.jpg");

        // Initially, sector should have no items
        assertTrue(electronicsSector.viewItems().isEmpty(), "Sector should start with no items");

        // Add items to sector
        electronicsSector.addItem(laptop);
        electronicsSector.addItem(mouse);

        // Verify items are reflected in viewItems
        ArrayList<Item> itemsInSector = electronicsSector.viewItems();
        assertEquals(2, itemsInSector.size(), "Sector should contain 2 items after adding");
        assertTrue(itemsInSector.contains(laptop), "Sector should contain the laptop");
        assertTrue(itemsInSector.contains(mouse), "Sector should contain the mouse");

        // Remove an item
        boolean removed = electronicsSector.removeItem(mouse);
        assertTrue(removed, "Mouse should be successfully removed");

        // Verify removal is reflected
        itemsInSector = electronicsSector.viewItems();
        assertEquals(1, itemsInSector.size(), "Sector should contain 1 item after removal");
        assertTrue(itemsInSector.contains(laptop), "Sector should still contain the laptop");
        assertFalse(itemsInSector.contains(mouse), "Sector should no longer contain the mouse");

        // Test adding another item
        Item keyboard = new Item("Keyboard", "Electronics", 50.0, 15, "Keyboard", "Mechanical Keyboard", "SupplierC", "keyboard.jpg");
        electronicsSector.addItem(keyboard);

        itemsInSector = electronicsSector.viewItems();
        assertEquals(2, itemsInSector.size(), "Sector should contain 2 items after adding keyboard");
        assertTrue(itemsInSector.contains(laptop), "Sector should contain the laptop");
        assertTrue(itemsInSector.contains(keyboard), "Sector should contain the keyboard");
    }
}