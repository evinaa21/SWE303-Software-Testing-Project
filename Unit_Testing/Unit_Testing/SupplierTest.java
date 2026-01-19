package Unit_Testing;

import org.junit.jupiter.api.Test;
import model.Item;
import model.Supplier;
import static org.junit.jupiter.api.Assertions.*;

class SupplierTest {

    // Verifies basic functionality works as written
    @Test
    void testAddItem_ShouldAddToSuppliedItems_Pass() {
        Supplier supplier = new Supplier("TechCorp");
        Item item = new Item("Mouse", "Hardware", 20.0, 50, "Acc", "Wireless", "TechCorp", "");

        supplier.addItem(item);

        assertEquals(1, supplier.getSuppliedItems().size());
        assertTrue(supplier.getSuppliedItems().contains(item));
    }

    //  Fails because addItem() updates 'suppliedItems' but forgets to update 'itemIds'
    @Test
    void testAddItem_ShouldSyncItemIds_Fail() {
        Supplier supplier = new Supplier("TechCorp");
        Item item = new Item("Keyboard", "Hardware", 50.0, 20, "Acc", "Mechanical", "TechCorp", "");

        supplier.addItem(item);

      
        assertFalse(supplier.getItemIds().isEmpty(), "Item IDs list should be updated when an item is added");
    }
}