package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Bill implements Serializable{
	private static final long serialVersionUID = -4660349044455634797L;
	
	private String billNumber;
	private ArrayList<Item> items;
	private double totalAmount;
	private Date saleDate;
	
	
	public Bill(String billNumber, ArrayList<Item> items, double totalAmount, Date saleDate) {
		this.billNumber = billNumber;
		this.items = items; 
		this.totalAmount = totalAmount;
		this.saleDate = saleDate;
	}
	
	public String getBillNumber() {
		return billNumber;
	}
	
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
	
	public void setItems(ArrayList<Item> items) {
		this.items = items;
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
