package view;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Cashier;
import model.Manager;
import model.Sector;

import java.util.ArrayList;

import controller.MonitorCashierPerformanceController;

public class MonitorCashierPerformanceView {

    private Manager manager;
    private MonitorCashierPerformanceController controller;

    public MonitorCashierPerformanceView(Manager manager, MonitorCashierPerformanceController controller) {
        if (manager == null || controller == null) {
            throw new IllegalArgumentException("Manager and Controller cannot be null.");
        }
        this.manager = manager;
        this.controller = controller;
    }

    public VBox getViewContent() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px; -fx-border-radius: 15px;");

        Label cashierLabel = new Label("Select Cashier:");
        cashierLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        ComboBox<Cashier> cashierComboBox = new ComboBox<>();
        cashierComboBox.setStyle("-fx-font-size: 16px; -fx-background-color: #ffffff;");

        ArrayList<Sector> managerSectors = manager.getSectors();
        ArrayList<Cashier> cashiers = controller.loadCashiers(managerSectors);

        if (cashiers != null && !cashiers.isEmpty()) {
            cashierComboBox.getItems().addAll(cashiers);
        } else {
            showError("No cashiers found.");
            cashierComboBox.setDisable(true);
        }

        Button monitorButton = new Button("Monitor Performance");
        monitorButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px;");

        Label totalBillsLabel = new Label("Total Bills: 0");
        Label totalRevenueLabel = new Label("Total Revenue: $0.00");

        String labelStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;";

        totalBillsLabel.setStyle(labelStyle);
        totalRevenueLabel.setStyle(labelStyle);

        monitorButton.setOnAction(event -> {
            Cashier selectedCashier = cashierComboBox.getValue();

            if (selectedCashier == null) {
                showError("Please select a cashier.");
                return;
            }

            controller.monitorCashierPerformance(selectedCashier, totalBillsLabel, totalRevenueLabel);
        });

        layout.getChildren().addAll(cashierLabel, cashierComboBox, monitorButton, totalBillsLabel, totalRevenueLabel);
        return layout;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

