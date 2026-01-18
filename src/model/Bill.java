package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bill represents a sales transaction in the Point of Sale system.
 * Fixed SpotBugs EI_EXPOSE_REP by ensuring proper encapsulation of mutable collections.
 * Analyzed in Part 2 for Boundary Value Testing and MC/DC analysis. 
 */
public class Bill {
    private String billId;
    private List<Item> items;
    private double subtotal;
    private double tax;
    private double discount;
    private double total;
    private LocalDateTime dateTime;
    private String cashierName;
    private String paymentType;
    private boolean isEmployee;
    private boolean hasDiscount;
    private boolean discountValid;
    private boolean taxable;
    private boolean taxExempt;
    
    /**
     * Default constructor for Bill.
     * Initializes empty item list and current timestamp.
     */
    public Bill() {
        this.items = new ArrayList<>();
        this.dateTime = LocalDateTime.now();
        this.billId = generateBillId();
        this.subtotal = 0.0;
        this.tax = 0.0;
        this.discount = 0.0;
        this.total = 0.0;
    }
    
    /**
     * Constructor with cashier information.
     * @param cashierName The name of the cashier creating the bill
     */
    public Bill(String cashierName) {
        this();
        this.cashierName = cashierName;
    }
    
    /**
     * Generates a unique bill ID based on timestamp.
     * @return Unique bill identifier
     */
    private String generateBillId() {
        return "BILL-" + System.currentTimeMillis();
    }
    
    /**
     * Adds an item to the bill with the specified quantity.
     * Validates that quantity is positive and does not exceed available stock.
     * Analyzed in Part 2 using Boundary Value Testing (BVT-01 through BVT-09).
     * 
     * @param item The item to add to the bill
     * @param quantity The number of units to add (must be > 0 and <= 100)
     * @throws IllegalArgumentException if item is null
     * @throws IllegalArgumentException if quantity is <= 0 or > 100
     * @throws IllegalStateException if quantity exceeds available stock
     */
    public void addItem(Item item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot exceed 100 units per transaction");
        }
        if (quantity > item.getStock()) {
            throw new IllegalStateException("Insufficient stock. Available: " + item.getStock());
        }
        
        // Add item to the bill
        items.add(item);
        item.reduceStock(quantity);
        recalculateSubtotal();
    }
    
    /**
     * Removes an item from the bill.
     * @param item The item to remove
     * @return true if item was removed, false otherwise
     */
    public boolean removeItem(Item item) {
        boolean removed = items.remove(item);
        if (removed) {
            recalculateSubtotal();
        }
        return removed;
    }
    
    /**
     * Recalculates the subtotal based on current items.
     */
    private void recalculateSubtotal() {
        subtotal = 0.0;
        for (Item item : items) {
            subtotal += item.getPrice() * item.getQuantity();
        }
    }
    
    /**
     * Calculates the final total including tax and discount.
     * Implements complex decision logic analyzed using MC/DC (MC-01 through MC-07).
     * 
     * Decision 1: hasDiscount && discountValid && !isEmployee
     * Decision 2: taxable && !taxExempt
     * 
     * @return The final bill total
     */
    public double calculateTotal() {
        double amount = subtotal;
        
        // Apply discount if conditions are met
        if (hasDiscount && discountValid && !isEmployee) {
            amount -= discount;
        }
        
        // Apply tax if conditions are met
        if (taxable && !taxExempt) {
            amount += tax;
        }
        
        this.total = amount;
        return total;
    }
    
    /**
     * Applies a percentage discount to the bill.
     * @param percentage The discount percentage (0-100)
     * @throws IllegalArgumentException if percentage is invalid
     */
    public void applyDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discount = subtotal * (percentage / 100.0);
        this.hasDiscount = true;
        this.discountValid = true;
    }
    
    /**
     * Sets the tax rate for the bill.
     * @param rate The tax rate as a percentage (e.g., 8.0 for 8%)
     */
    public void setTaxRate(double rate) {
        if (rate < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative");
        }
        this.tax = subtotal * (rate / 100.0);
        this.taxable = true;
    }
    
    /**
     * Returns the list of items in the bill.
     * Fixed EI_EXPOSE_REP (Line 88) by returning a defensive copy.
     * 
     * @return A defensive copy of the items list
     */
    public List<Item> getItems() {
        // Return defensive copy to prevent external modification
        return new ArrayList<>(items);
    }
    
    /**
     * Sets the items list for this bill.
     * Creates a defensive copy to maintain encapsulation.
     * @param items The items to set
     */
    public void setItems(List<Item> items) {
        // Create defensive copy to prevent external modification
        this.items = new ArrayList<>(items);
        recalculateSubtotal();
    }
    
    /**
     * Gets the bill ID.
     * @return The unique bill identifier
     */
    public String getBillId() {
        return billId;
    }
    
    /**
     * Sets the bill ID.
     * @param billId The bill identifier
     */
    public void setBillId(String billId) {
        this.billId = billId;
    }
    
    /**
     * Gets the subtotal before tax and discount.
     * @return The subtotal amount
     */
    public double getSubtotal() {
        return subtotal;
    }
    
    /**
     * Gets the tax amount.
     * @return The tax amount
     */
    public double getTax() {
        return tax;
    }
    
    /**
     * Gets the discount amount.
     * @return The discount amount
     */
    public double getDiscount() {
        return discount;
    }
    
    /**
     * Gets the final total.
     * @return The total bill amount
     */
    public double getTotal() {
        return total;
    }
    
    /**
     * Gets the bill date and time.
     * @return The timestamp when bill was created
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    /**
     * Sets the bill date and time.
     * @param dateTime The timestamp
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    /**
     * Gets the cashier name.
     * @return The name of the cashier who created this bill
     */
    public String getCashierName() {
        return cashierName;
    }
    
    /**
     * Sets the cashier name.
     * @param cashierName The cashier's name
     */
    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }
    
    /**
     * Gets the payment type.
     * @return The payment method used (CASH, CARD, MOBILE)
     */
    public String getPaymentType() {
        return paymentType;
    }
    
    /**
     * Sets the payment type.
     * @param paymentType The payment method
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    
    /**
     * Checks if this is an employee purchase.
     * @return true if employee purchase
     */
    public boolean isEmployee() {
        return isEmployee;
    }
    
    /**
     * Sets whether this is an employee purchase.
     * @param isEmployee true for employee purchases
     */
    public void setEmployee(boolean isEmployee) {
        this.isEmployee = isEmployee;
    }
    
    /**
     * Checks if bill has a discount.
     * @return true if discount is applied
     */
    public boolean hasDiscount() {
        return hasDiscount;
    }
    
    /**
     * Checks if discount is valid.
     * @return true if discount is valid
     */
    public boolean isDiscountValid() {
        return discountValid;
    }
    
    /**
     * Sets discount validity.
     * @param valid true if discount is valid
     */
    public void setDiscountValid(boolean valid) {
        this.discountValid = valid;
    }
    
    /**
     * Checks if bill is taxable.
     * @return true if tax should be applied
     */
    public boolean isTaxable() {
        return taxable;
    }
    
    /**
     * Sets taxable status.
     * @param taxable true if bill should be taxed
     */
    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }
    
    /**
     * Checks if tax exempt.
     * @return true if exempt from tax
     */
    public boolean isTaxExempt() {
        return taxExempt;
    }
    
    /**
     * Sets tax exemption status.
     * @param taxExempt true if exempt from tax
     */
    public void setTaxExempt(boolean taxExempt) {
        this.taxExempt = taxExempt;
    }
    
    /**
     * Returns a string representation of the bill.
     * @return String containing bill summary
     */
    @Override
    public String toString() {
        return String.format("Bill[ID=%s, Items=%d, Subtotal=%.2f, Tax=%.2f, Discount=%.2f, Total=%.2f]",
                billId, items.size(), subtotal, tax, discount, total);
    }
}
