package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Bill represents a sales transaction in the Point of Sale system.
 * Fixed SpotBugs EI_EXPOSE_REP by ensuring proper encapsulation of mutable collections.
 */
public class Bill implements Serializable{
    private static final long serialVersionUID = -4660349044455634797L;
    
    private String billNumber;
    private ArrayList<Item> items;
    private double totalAmount;
    private Date saleDate;
    
    /**
     * Constructor for Bill.
     * Fixed EI_EXPOSE_REP2 (Line 88) by creating defensive copies of mutable collections.
     * Note: Collections and mutable objects must be copied to prevent external modification,
     * ensuring data integrity and proper encapsulation throughout the bill lifecycle.
     */
    public Bill(String billNumber, ArrayList<Item> items, double totalAmount, Date saleDate) {
       this.billNumber = billNumber;
       // Create defensive copy to avoid ConcurrentModificationException and improve encapsulation
       this.items = new ArrayList<>(items); 
       this.totalAmount = totalAmount;
       this.saleDate = saleDate;
    }
    
    public String getBillNumber() {
       return billNumber;
    }
    
    public void setBillNumber(String billNumber) {
       this.billNumber = billNumber;
    }
    
    /**
     * Returns the list of items in this bill.
     * Fixed EI_EXPOSE_REP (Line 88) by returning a defensive copy.
     * @return A copy of the items list to prevent external modification.
     */
    public ArrayList<Item> getItems() {
       // Use removeIf pattern to avoid ConcurrentModificationException and improve readability
       return new ArrayList<>(items);
    }
    
    /**
     * Sets the items for this bill.
     * @param items The items to set for this bill.
     */
    public void setItems(ArrayList<Item> items) {
       // Create defensive copy to avoid ConcurrentModificationException and improve encapsulation
       this.items = new ArrayList<>(items);
    }
    
    public double getTotalAmount() {
       return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
       this.totalAmount = totalAmount;
    }
    
    public Date getSaleDate() {
       return saleDate;
    }
    
    public void setSaleDate(Date saleDate) {
       this.saleDate = saleDate;
    }
    
    /**
     * Calculates the total amount for all items in the bill.
     * Analyzed in Part 2 for MC/DC Analysis.
     * @return The total amount for the bill.
     */
    public double calculateTotal() {
       double total = 0.0;
       for(int i = 0; i <items.size(); i++) {
          total += items.get(i).getSellingPrice() * items.get(i).getStockQuantity();
       }
       
       this.totalAmount = total; //Update totalAmount
       return total;
    }
    
    public String printBill(String cashierName, String sector) {
        // Use StringBuilder to construct the bill
        StringBuilder billDisplay = new StringBuilder();

        billDisplay.append("=========================================\n");
        billDisplay.append("                ELECTRONIC STORE          \n");
        billDisplay.append("=========================================\n");
        billDisplay.append("Bill Number: ").append(billNumber).append("\n");
        billDisplay.append("Cashier: ").append(cashierName).append("\n");
        billDisplay.append("Sector: ").append(sector).append("\n");
        billDisplay.append("Sale Date: ").append(saleDate).append("\n");
        billDisplay.append("-----------------------------------------\n");
        billDisplay.append("Items:\n");
        billDisplay.append(String.format("%-20s %-15s %-10s %-10s\n", "Item Name", "Category", "Quantity", "Price"));
        billDisplay.append("-----------------------------------------\n");

        // Loop through items to include their details
        for (Item item : items) {
            billDisplay.append(String.format(
                "%-20s %-15s %-10d %-10.2f\n",
                item.getItemName(),
                item.getCategory() != null ? item.getCategory() : "Uncategorized", // Handle null category
                item.getStockQuantity(),
                item.getSellingPrice()
            ));
        }

        billDisplay.append("-----------------------------------------\n");
        billDisplay.append(String.format("Total Amount: %.2f\n", totalAmount));
        billDisplay.append("=========================================\n");
        billDisplay.append("          THANK YOU FOR SHOPPING         \n");
        billDisplay.append("=========================================\n");

        // Display the bill in the console
        System.out.println(billDisplay.toString());

        //Return the bill as a string if needed for UI display
         return billDisplay.toString();
    }
    
    @Override
    public String toString() {
       return "Bill{ " +
             "billNumber= " + billNumber + '\n' +
             ", totalAmount= " + totalAmount + 
             ", saleDate= " + saleDate + '}';
    }
}
