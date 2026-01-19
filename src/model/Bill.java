package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a sales bill/receipt in the Electronic Store system.
 * Contains information about the bill number, items purchased, total amount, and sale date.
 * This class is serializable for persistence to binary files.
 *
 * @author Member 3 - Cashier & Integration Specialist
 * @version 1.0
 */
public class Bill implements Serializable{
	private static final long serialVersionUID = -4660349044455634797L;

	private String billNumber;
	private ArrayList<Item> items;
	private double totalAmount;
	private Date saleDate;


	/**
	 * Constructs a new Bill with the specified details.
	 *
	 * @param billNumber unique identifier for the bill
	 * @param items list of items included in the bill (defensive copy is made)
	 * @param totalAmount the total monetary amount of the bill
	 * @param saleDate the date when the sale was made (defensive copy is made)
	 */
	public Bill(String billNumber, ArrayList<Item> items, double totalAmount, Date saleDate) {
		this.billNumber = billNumber;
		// Fix EI_EXPOSE_REP2: Create defensive copy of mutable ArrayList
		this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
		this.totalAmount = totalAmount;
		// Fix EI_EXPOSE_REP2: Create defensive copy of mutable Date
		this.saleDate = saleDate != null ? new Date(saleDate.getTime()) : null;
	}

	/**
	 * Gets the unique bill number.
	 *
	 * @return the bill number string
	 */
	public String getBillNumber() {
		return billNumber;
	}

	/**
	 * Sets the bill number.
	 *
	 * @param billNumber the new bill number to set
	 */
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	/**
	 * Gets a defensive copy of the items list.
	 *
	 * @return a new ArrayList containing the bill items
	 */
	public ArrayList<Item> getItems() {
		// Fix EI_EXPOSE_REP: Return defensive copy to prevent external modification
		return new ArrayList<>(items);
	}

	/**
	 * Sets the items list (stores a defensive copy).
	 *
	 * @param items the list of items to set
	 */
	public void setItems(ArrayList<Item> items) {
		// Fix EI_EXPOSE_REP2: Store defensive copy of mutable ArrayList
		this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
	}

	/**
	 * Gets the total amount of the bill.
	 *
	 * @return the total monetary amount
	 */
	public double getTotalAmount() {
		return totalAmount;
	}

	/**
	 * Sets the total amount of the bill.
	 *
	 * @param totalAmount the total amount to set
	 */
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * Gets a defensive copy of the sale date.
	 *
	 * @return a new Date object representing the sale date, or null if not set
	 */
	public Date getSaleDate() {
		// Fix EI_EXPOSE_REP: Return defensive copy to prevent external modification
		return saleDate != null ? new Date(saleDate.getTime()) : null;
	}

	/**
	 * Sets the sale date (stores a defensive copy).
	 *
	 * @param saleDate the date of sale to set
	 */
	public void setSaleDate(Date saleDate) {
		// Fix EI_EXPOSE_REP2: Store defensive copy of mutable Date
		this.saleDate = saleDate != null ? new Date(saleDate.getTime()) : null;
	}
	
	/**
	 * Calculates and updates the total amount for the bill based on item prices and quantities.
	 * The total is computed as the sum of (sellingPrice * stockQuantity) for each item.
	 *
	 * @return the calculated total amount
	 */
	public double calculateTotal() {
		double total = 0.0;
		for(int i = 0; i < items.size(); i++) {
			total += items.get(i).getSellingPrice() * items.get(i).getStockQuantity();
		}

		this.totalAmount = total; // Update totalAmount
		return total;
	}

	/**
	 * Generates a formatted string representation of the bill for display or printing.
	 * Includes store header, bill details, itemized list, and total amount.
	 *
	 * @param cashierName the name of the cashier who processed the bill
	 * @param sector the store sector where the sale was made
	 * @return a formatted string containing the complete bill information
	 */
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
		// Fix VA_FORMAT_STRING_USES_NEWLINE: Use %n for platform-independent line separator
		billDisplay.append(String.format("%-20s %-15s %-10s %-10s%n", "Item Name", "Category", "Quantity", "Price"));
		billDisplay.append("-----------------------------------------\n");

		// Loop through items to include their details
		for (Item item : items) {
			// Fix VA_FORMAT_STRING_USES_NEWLINE: Use %n for platform-independent line separator
			billDisplay.append(String.format(
				"%-20s %-15s %-10d %-10.2f%n",
				item.getItemName(),
				item.getCategory() != null ? item.getCategory() : "Uncategorized", // Handle null category
				item.getStockQuantity(),
				item.getSellingPrice()
			));
		}

		billDisplay.append("-----------------------------------------\n");
		// Fix VA_FORMAT_STRING_USES_NEWLINE: Use %n for platform-independent line separator
		billDisplay.append(String.format("Total Amount: %.2f%n", totalAmount));
		billDisplay.append("=========================================\n");
		billDisplay.append("          THANK YOU FOR SHOPPING         \n");
		billDisplay.append("=========================================\n");

		// Display the bill in the console
		System.out.println(billDisplay.toString());

		// Return the bill as a string if needed for UI display
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
