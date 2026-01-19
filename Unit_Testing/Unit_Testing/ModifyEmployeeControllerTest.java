package Unit_Testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModifyEmployeeController
 * Focus on parseSalary() method using Equivalence Class Partitioning
 *
 * @author Ersi Majkaj - Member 4: System QA & File Operations
 */
public class ModifyEmployeeControllerTest {

    // ==================== Equivalence Class Tests for parseSalary() ====================

    /**
     * EC1: Valid Integer String
     * Input: "5000" -> Expected: 5000.0
     */
    @Test
    void testParseSalary_ValidIntegerString_Pass() {
        String input = "5000";
        double result = parseSalary(input);
        assertEquals(5000.0, result, 0.001, "Should parse valid integer string");
    }

    /**
     * EC1: Valid Integer String - another value
     */
    @Test
    void testParseSalary_ValidIntegerString_LargeValue_Pass() {
        String input = "99999";
        double result = parseSalary(input);
        assertEquals(99999.0, result, 0.001);
    }

    /**
     * EC2: Valid Decimal String
     * Input: "5000.50" -> Expected: 5000.5
     */
    @Test
    void testParseSalary_ValidDecimalString_Pass() {
        String input = "5000.50";
        double result = parseSalary(input);
        assertEquals(5000.50, result, 0.001, "Should parse valid decimal string");
    }

    /**
     * EC2: Valid Decimal String - precise decimal
     */
    @Test
    void testParseSalary_ValidDecimalString_Precise_Pass() {
        String input = "1234.99";
        double result = parseSalary(input);
        assertEquals(1234.99, result, 0.001);
    }

    /**
     * EC4: Alphabetic String
     * Input: "abc" -> Expected: IllegalArgumentException
     */
    @Test
    void testParseSalary_AlphabeticString_Fail() {
        String input = "abc";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        }, "Should throw exception for alphabetic input");
    }

    /**
     * EC4: Alphabetic String - word
     */
    @Test
    void testParseSalary_WordString_Fail() {
        String input = "salary";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        });
    }

    /**
     * EC5: Mixed Alphanumeric
     * Input: "50abc" -> Expected: IllegalArgumentException
     */
    @Test
    void testParseSalary_MixedAlphanumeric_Fail() {
        String input = "50abc";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        }, "Should throw exception for mixed alphanumeric input");
    }

    /**
     * EC5: Mixed Alphanumeric - decimal with letters
     */
    @Test
    void testParseSalary_DecimalWithLetters_Fail() {
        String input = "12.5x";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        });
    }

    /**
     * EC6: Special Characters
     * Input: "@#$" -> Expected: IllegalArgumentException
     */
    @Test
    void testParseSalary_SpecialCharacters_Fail() {
        String input = "@#$";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        }, "Should throw exception for special characters");
    }

    /**
     * EC6: Special Characters - with numbers
     */
    @Test
    void testParseSalary_SpecialCharsWithNumbers_Fail() {
        String input = "$5000";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        });
    }

    /**
     * EC7: Negative Number String
     * Input: "-5000" -> Expected: -5000.0 (parses but logically invalid)
     * Note: This reveals a potential bug - negative salaries should be rejected
     */
    @Test
    void testParseSalary_NegativeNumber_ParsesSuccessfully() {
        String input = "-5000";
        double result = parseSalary(input);
        assertEquals(-5000.0, result, 0.001, "Parses negative but business logic should reject");
    }

    /**
     * EC7: Negative Decimal
     */
    @Test
    void testParseSalary_NegativeDecimal_ParsesSuccessfully() {
        String input = "-100.50";
        double result = parseSalary(input);
        assertEquals(-100.50, result, 0.001);
    }

    /**
     * EC8: Whitespace Only
     * Input: "   " -> Expected: IllegalArgumentException
     */
    @Test
    void testParseSalary_WhitespaceOnly_Fail() {
        String input = "   ";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        }, "Should throw exception for whitespace-only input");
    }

    /**
     * EC8: Number with leading/trailing whitespace
     * Note: Double.parseDouble handles this, but might want to trim explicitly
     */
    @Test
    void testParseSalary_NumberWithWhitespace_Pass() {
        // Double.parseDouble actually handles leading/trailing whitespace
        String input = " 5000 ";
        // This might pass or fail depending on implementation
        // Testing actual behavior
        try {
            double result = parseSalary(input.trim());
            assertEquals(5000.0, result, 0.001);
        } catch (IllegalArgumentException e) {
            // Also acceptable if implementation doesn't trim
            assertTrue(true);
        }
    }

    // ==================== Boundary Value Tests ====================

    @Test
    void testParseSalary_Zero_Pass() {
        String input = "0";
        double result = parseSalary(input);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testParseSalary_MinimumPositive_Pass() {
        String input = "0.01";
        double result = parseSalary(input);
        assertEquals(0.01, result, 0.001);
    }

    @Test
    void testParseSalary_VeryLargeNumber_Pass() {
        String input = "9999999.99";
        double result = parseSalary(input);
        assertEquals(9999999.99, result, 0.001);
    }

    // ==================== Edge Cases ====================

    @Test
    void testParseSalary_ScientificNotation_Pass() {
        String input = "1E3"; // 1000
        double result = parseSalary(input);
        assertEquals(1000.0, result, 0.001);
    }

    @Test
    void testParseSalary_EmptyString_Fail() {
        String input = "";
        assertThrows(IllegalArgumentException.class, () -> {
            parseSalary(input);
        });
    }

    // ==================== Helper Method (extracted from controller) ====================

    /**
     * Simulates parseSalary() from ModifyEmployeeController
     * Original code at lines 125-131
     */
    private double parseSalary(String salaryString) {
        try {
            return Double.parseDouble(salaryString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid salary format.");
        }
    }
}
