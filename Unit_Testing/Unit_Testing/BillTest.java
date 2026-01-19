package Unit_Testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Bill;
import model.Item;
import java.util.ArrayList;
import java.util.Date;

class BillTest {

    private ArrayList<Item> validItems;

    @BeforeEach
    void setUp() {
        validItems = new ArrayList<>();
        // Constructor: itemName, category, price, stockQuantity, itemsSold
        validItems.add(new Item("Laptop", "Electronics", 1000.0, 1, 0));
        validItems.add(new Item("Mouse", "Accessories", 25.0, 2, 0));
    }

    /**
     * PASS CASE: Tests if the total is calculated correctly under normal conditions.
     * Expected: (1000.0 * 1) + (25.0 * 2) = 1050.0
     */
    @Test
    void testCalculateTotal_Success() {
        Bill bill = new Bill("B123", validItems, 0.0, new Date());
        
        double result = bill.calculateTotal();
        
        assertEquals(1050.0, result, 0.001, "The total calculation should be exactly 1050.0");
        assertEquals(1050.0, bill.getTotalAmount(), "The bill field totalAmount should be updated.");
    }

    /**
     * FAIL CASE: Tests what happens if the list contains a null object.
     * This will fail because the code does not check if items.get(i) is null.
     */
    @Test
    void testCalculateTotal_NullItemInList() {
        // Add a null entry to our valid list
        validItems.add(null); 
        Bill bill = new Bill("B124", validItems, 0.0, new Date());

        // We expect this to throw a NullPointerException
        assertThrows(NullPointerException.class, () -> {
            bill.calculateTotal();
        }, "The method should fail when encountering a null item in the list.");
    }
}