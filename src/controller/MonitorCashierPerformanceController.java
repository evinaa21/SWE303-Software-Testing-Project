package controller;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Cashier;
import model.Manager;
import model.Sector;
import util.FileHandlerMANAGER;
import view.MonitorCashierPerformanceView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MonitorCashierPerformanceController {

    private Manager manager;
    private FileHandlerMANAGER fileHandler;
    
    public MonitorCashierPerformanceController(Manager manager, FileHandlerMANAGER fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;
    }

    public void showMonitorCashierView() {
        MonitorCashierPerformanceView monitorCashierView = new MonitorCashierPerformanceView(manager, this);
        
        Stage monitorCashierStage = new Stage();
        monitorCashierStage.setTitle("Monitor Cashier Performance");
        monitorCashierStage.setScene(new Scene(monitorCashierView.getViewContent(), 400, 300));
        monitorCashierStage.show();
    }

    public ArrayList<Cashier> loadCashiers(ArrayList<Sector> managerSectors) {
        return fileHandler.loadCashiersByRole(managerSectors);
    }

    public void monitorCashierPerformance(Cashier selectedCashier, Label totalBillsLabel, Label totalRevenueLabel) {
        int totalBills = 0;
        double totalRevenue = 0.0;

        String summaryFilePath = "src/BinaryFiles/sales_summary.txt";
        File summaryFile = new File(summaryFilePath);

        if (!summaryFile.exists()) {
            showError("Sales summary file does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(summaryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 4) {
                    String billNumber = parts[0].split(": ")[1].trim();
                    String totalAmountStr = parts[2].split(": ")[1].trim();
                    String cashierName = parts[3].split(": ")[1].trim();

                    double totalAmount = 0.0;
                    try {
                        totalAmount = Double.parseDouble(totalAmountStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid total amount format for bill " + billNumber + ": " + totalAmountStr);
                        continue;
                    }

                    if (cashierName.equals(selectedCashier.getName())) {
                        totalBills++;
                        totalRevenue += totalAmount;
                    }
                }
            }
        } catch (IOException e) {
            showError("Error reading the sales summary file: " + e.getMessage());
            return;
        }

        totalBillsLabel.setText("Total Bills: " + totalBills);
        totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
