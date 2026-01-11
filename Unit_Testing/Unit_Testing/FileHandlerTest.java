package Unit_Testing;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import util.FileHandler;
import java.util.Date;
import java.util.Calendar;

/**
 * Tests for utility logic in FileHandler.
 */
public class FileHandlerTest {

    @Test
    void testIsSameDayTrue() {
        FileHandler fh = new FileHandler();
        Date date1 = new Date();
        Date date2 = new Date(date1.getTime() + 1000); // Same day, 1 second apart
        
        assertTrue(fh.isSameDay(date1, date2));
    }

    @Test
    void testIsSameDayFalse() {
        FileHandler fh = new FileHandler();
        Calendar cal = Calendar.getInstance();
        Date date1 = cal.getTime();
        
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date date2 = cal.getTime();
        
        assertFalse(fh.isSameDay(date1, date2));
    }

    @Test
    void testIsSameDayNullHandling() {
        FileHandler fh = new FileHandler();
        assertFalse(fh.isSameDay(null, new Date()));
        assertFalse(fh.isSameDay(new Date(), null));
    }
}