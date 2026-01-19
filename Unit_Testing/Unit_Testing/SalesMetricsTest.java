package Unit_Testing;

import org.junit.jupiter.api.Test;
import model.Item;
import model.SalesMetrics;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SalesMetricsTest {

    @Test
    void calculateMetrics_shouldCalculateRevenueAndItemsSold() {
        // Arrange
        Item item1 = new Item("Item1", "Sector", 10.0, 10, "Cat", "Desc", "Sup", "");
        Item item2 = new Item("Item2", "Sector", 5.0, 20, "Cat", "Desc", "Sup", "");

        item1.sellItem(3); // itemsSold=3 revenue=30
        item2.sellItem(4); // itemsSold=4 revenue=20

        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        SalesMetrics metrics = new SalesMetrics();

        // Act
        metrics.calculateMetrics(items);

        // Assert
        assertEquals(50.0, metrics.getTotalRevenue(), 0.0001, "Total revenue should equal sum(price * itemsSold)");
        assertEquals(7, metrics.getTotalItemsSold(), "Total items sold should equal sum(itemsSold)");
    }
}