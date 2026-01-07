package controller;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import util.FileHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DailyBillsController {
    private final VBox billsContainer; // Container to display daily bills
    private ArrayList<String> dailyBills; // Cache for all daily bills
    private final String summaryFilePath = "src/BinaryFiles/sales_summary.txt";

    // Constructor
    public DailyBillsController(VBox billsContainer) {
        new FileHandler();
        this.billsContainer = billsContainer;
        this.dailyBills = new ArrayList<>();
    }

    // Load and display all bills generated today
    public void showTodaysBills() {
        try {
            this.dailyBills = loadBillsFromSummary(); // Load bills for today from summary
            displayBills(this.dailyBills); // Display all bills
        } catch (Exception e) {
            billsContainer.getChildren().add(new Label("Error loading bills: " + e.getMessage()));
        }
    }

    // Load bill data from the sales summary file
    private ArrayList<String> loadBillsFromSummary() throws IOException {
        ArrayList<String> bills = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(summaryFilePath))) {
            String line;
            StringBuilder currentBill = new StringBuilder();
           
            while ((line = reader.readLine()) != null) {
               
                currentBill.append(line).append("\n");
                if (line.contains("Cashier:")) {
                    bills.add(currentBill.toString()); 
                    currentBill.setLength(0); // Reset 
                }
            }
        }
        return bills;
    }

    // Display bills
    private void displayBills(ArrayList<String> bills) {
        billsContainer.getChildren().clear(); 
        if (bills.isEmpty()) {
            billsContainer.getChildren().add(new Label("No bills found for today."));
            return;
        }

        for (String bill : bills) {
            Label billLabel = new Label(bill); 
            billLabel.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1;");
            billsContainer.getChildren().add(billLabel);
        }
    }

   
    public void calculateTotalSales() {
        if (dailyBills.isEmpty()) {
            billsContainer.getChildren().add(new Label("No sales data available for today."));
            return;
        }
        double totalAmount = 0.0;
        for (String bill : dailyBills) {
            
            String[] lines = bill.split("\n");
            for (String line : lines) {
                if (line.startsWith("Total Amount:")) {
                    try {
                        totalAmount += Double.parseDouble(line.split(":")[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid total amount: " + line);
                    }
                }
            }
        }

        Label totalSalesLabel = new Label(String.format("Total Sales for Today: %.2f", totalAmount));
        totalSalesLabel.setStyle("-fx-padding: 10; -fx-font-weight: bold; -fx-text-fill: #4169E1;");
        billsContainer.getChildren().add(totalSalesLabel);
    }

 // Calculate and display sales performance by cashier
    public void calculateSalesByCashier() {
        ArrayList<String> cashierNames = new ArrayList<>();
        ArrayList<Double> cashierSales = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(summaryFilePath))) {
            String line;
          
            while ((line = reader.readLine()) != null) {
                if (line.contains("Cashier:")) {
                   
                    String[] parts = line.split("Total Amount: ");
                    if (parts.length > 1) {
                        
                        String totalAmountStr = parts[1].split(",")[0].trim();
                        String cashierName = parts[1].split("Cashier:")[1].trim();
                        try {
                            double totalAmount = Double.parseDouble(totalAmountStr);

                            // Check if cashier already exists in the list
                            int index = cashierNames.indexOf(cashierName);
                            if (index != -1) {
                                // Add the total amount to the existing cashier's sales
                                cashierSales.set(index, cashierSales.get(index) + totalAmount);
                            } else {
                                // Add a new cashier and their sales
                                cashierNames.add(cashierName);
                                cashierSales.add(totalAmount);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing total amount: " + totalAmountStr);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales summary: " + e.getMessage());
        }

        // Display sales performance for each cashier
        displaySalesByCashier(cashierNames, cashierSales);
    }

    // Display sales 
    private void displaySalesByCashier(ArrayList<String> cashierNames, ArrayList<Double> cashierSales) {
        billsContainer.getChildren().clear(); // Clear previous data
        if (cashierNames.isEmpty()) {
            billsContainer.getChildren().add(new Label("No sales data available for today."));
            return;
        }

        // total sales
        for (int i = 0; i < cashierNames.size(); i++) {
            String cashierPerformance = String.format("Cashier: %s\nTotal Sales: %.2f", cashierNames.get(i), cashierSales.get(i));
            Label performanceLabel = new Label(cashierPerformance);
            performanceLabel.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1;");
            billsContainer.getChildren().add(performanceLabel);
        }
    }


}

