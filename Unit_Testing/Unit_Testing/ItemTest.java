package Unit_Testing;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Item;

/**
 * Tests for Item class focusing on stock management and equivalence.
 */
public class ItemTest {

    @Test
    void testSellItemSuccess() {
        Item item = new Item("Phone", "Electronics", 500.0, 20, 0);
        item.sellItem(5);
        
        assertEquals(15, item.getStockQuantity());
        assertEquals(5, item.getItemsSold());
    }

    @Test
    void testSellItemBoundary() {
        Item item = new Item("Phone", "Electronics", 500.0, 10, 0);
        item.sellItem(10); // Exactly the stock amount
        
        assertEquals(0, item.getStockQuantity());
        assertEquals(10, item.getItemsSold());
    }

    @Test
    void testSellItemInsufficientStock() {
        Item item = new Item("Phone", "Electronics", 500.0, 5, 0);
        item.sellItem(6); // More than stock
        
        assertEquals(5, item.getStockQuantity());
        assertEquals(0, item.getItemsSold());
    }

    @Test
    void testRestockItem() {
        Item item = new Item("Phone", "Electronics", 500.0, 10, 0);
        item.restockItem(5);
        assertEquals(15, item.getStockQuantity());
    }

    @Test
    void testHasSufficientStock() {
        Item item = new Item("Phone", "Electronics", 500.0, 10, 0);
        assertTrue(item.hasSufficientStock(10));
        assertFalse(item.hasSufficientStock(11));
    }
}