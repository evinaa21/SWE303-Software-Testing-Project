package Unit_Testing;

import org.junit.jupiter.api.Test;

import model.Item;
import model.Sector;

import static org.junit.jupiter.api.Assertions.*;

class SectorTest {

    @Test
    void addItem_shouldAddItemToSector() {
        Sector sector = new Sector("Electronics");
        Item item = new Item("Phone", "Electronics", 500.0, 10, "Tech", "Smartphone", "SupplierX", "");

        sector.addItem(item);

        assertTrue(sector.viewItems().contains(item), "Item should be added to sector items list");
    }

    @Test
    void removeItem_shouldRemoveExistingItem() {
        Sector sector = new Sector("Electronics");
        Item item = new Item("Phone", "Electronics", 500.0, 10, "Tech", "Smartphone", "SupplierX", "");

        sector.addItem(item);
        boolean removed = sector.removeItem(item);

        assertTrue(removed, "removeItem should return true when item exists and is removed");
        assertFalse(sector.viewItems().contains(item), "Item should no longer exist in sector after removal");
    }
}