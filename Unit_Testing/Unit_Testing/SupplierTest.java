package Unit_Testing;

import org.junit.jupiter.api.Test;

import model.Item;
import model.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class SupplierTest {

    @Test
    void addItem_shouldAddItemToSupplier() {
        Supplier supplier = new Supplier("ACME");
        Item item = new Item("Laptop", "Electronics", 1000.0, 5, "Tech", "Laptop", "ACME", "");

        supplier.addItem(item);

        assertTrue(supplier.getSuppliedItems().contains(item), "Supplier should contain the added item");
    }

    @Test
    void getSuppliedItems_shouldNeverReturnNull() {
        Supplier supplier = new Supplier("ACME");

        assertNotNull(supplier.getSuppliedItems(), "getSuppliedItems should never return null");
        assertTrue(supplier.getSuppliedItems().isEmpty(), "New supplier should start with empty supplied items list");
    }
}