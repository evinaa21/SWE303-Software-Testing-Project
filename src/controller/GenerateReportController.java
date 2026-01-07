package controller;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Manager;
import view.GenerateReportView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GenerateReportController {

    private Manager manager;
    public GenerateReportController(Manager manager) {
        this.manager = manager;
    }
    public void showGenerateReportView() {
        Stage reportStage = new Stage();

       
        GenerateReportView generateReportView = new GenerateReportView(manager, this);

        VBox layout = generateReportView.getViewContent();

        Scene scene = new Scene(layout, 500, 400);
        reportStage.setTitle("Generate Sales Report");
        reportStage.setScene(scene);
        reportStage.show();
    }

    public String generateSalesReport(String timePeriod) {
        // Calculate the total sales from the filtered bills (using data from SalesSummary.txt)
        double totalSales = calculateTotalSalesFromSummary(timePeriod);

        return String.format("Total Sales: $%.2f", totalSales);
    }

    private double calculateTotalSalesFromSummary(String timePeriod) {
        String summaryFilePath = "C:\\Users\\Evina\\git\\Electronics-Store\\src\\BinaryFiles\\sales_summary.txt";
        File summaryFile = new File(summaryFilePath);
        double totalSales = 0.0;

        if (!summaryFile.exists()) {
            System.out.println("Sales summary file does not exist.");
            return totalSales;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(summaryFile))) {
            String line;
            // Read each line
            while ((line = reader.readLine()) != null) {
                if (line.contains("Sales Report")) {
                    continue;
                }

                if (line.contains("Total Amount:") && line.contains("Cashier:")) {
                    // Split the line into parts by the "Total Amount:" label
                    String[] parts = line.split("Total Amount:");
                    if (parts.length > 1) {
                        // extract the total amount
                        String totalAmountStr = parts[1].split(",")[0].trim();  
                        try {
                            double totalAmount = Double.parseDouble(totalAmountStr);
                            if (totalAmount > 0.0) {
                                totalSales += totalAmount;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing total amount: " + parts[1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales summary: " + e.getMessage());
        }

        return totalSales;
    }

    public ArrayList<String> filterBills(ArrayList<String> billsData, String timePeriod) {
        ArrayList<String> filteredBills = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (String bill : billsData) {
           
            if (bill.contains("=========================================") || bill.contains("THANK YOU FOR SHOPPING")) {
                continue;
            }

            // it initializes date, total amount, and cashier name variables
            String billDateStr = null;
            String cashierName = null;

            String[] lines = bill.split("\n");
            for (String line : lines) {
                if (line.startsWith("Date:")) {
                    billDateStr = line.split(":")[1].trim();
                } else if (line.startsWith("Total Amount:")) {
                    String amountStr = line.split(":")[1].trim();
                    try {
                        
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid total amount: " + amountStr);
                    }
                } else if (line.startsWith("Cashier:")) {
                    
                    cashierName = line.split(":")[1].trim();
                }
            }

            if (billDateStr != null) {
                try {
                    LocalDate billDate = LocalDate.parse(billDateStr, formatter); // Parse the date
                    // Filter based on time period
                    if (timePeriod.equals("Last 7 Days") && billDate.isAfter(now.minusDays(7))) {
                        filteredBills.add(bill);
                    } else if (timePeriod.equals("Last Month") && billDate.isAfter(now.minusMonths(1))) {
                        filteredBills.add(bill);
                    }
                } catch (Exception e) {
                    System.out.println("Skipping invalid date in bill: " + billDateStr);
                }
            }
        }

        return filteredBills;
    }
}

