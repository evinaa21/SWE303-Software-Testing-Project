package Unit_Testing;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Bill;
import model.Item;
import java.util.ArrayList;
import java.util.Date;

/**
 * Tests for Bill class focusing on total amount calculations.
 */
public class BillTest {

    @Test
    void testCalculateTotalWithMultipleItems() {
        ArrayList<Item> items = new ArrayList<>();
        // Price 100 * Qty 2 = 200
        items.add(new Item("A", "C1", 100.0, 2, 0));
        // Price 50 * Qty 1 = 50
        items.add(new Item("B", "C2", 50.0, 1, 0));
        
        Bill bill = new Bill("B-001", items, 0.0, new Date());
        double calculated = bill.calculateTotal();
        
        assertEquals(250.0, calculated);
        assertEquals(250.0, bill.getTotalAmount());
    }

    @Test
    void testCalculateTotalEmptyItems() {
        ArrayList<Item> items = new ArrayList<>();
        Bill bill = new Bill("B-002", items, 0.0, new Date());
        assertEquals(0.0, bill.calculateTotal());
    }
}