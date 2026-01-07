package view;

import controller.GenerateReportController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Manager;

public class GenerateReportView {
	private GenerateReportController controller;

	 public GenerateReportView(Manager manager, GenerateReportController controller) {
	        this.controller = controller;
	    }

    public VBox getViewContent() {
        Label titleLabel = new Label("Generate Sales Report");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 28));
        titleLabel.setTextFill(Color.WHITE);

        VBox parentLayout = new VBox(20);
        parentLayout.setStyle("-fx-background-color: #2C3E50;");
        parentLayout.setAlignment(Pos.CENTER);
        parentLayout.setPadding(new Insets(20));

        Label timePeriodLabel = new Label("Select Time Period:");
        timePeriodLabel.setTextFill(Color.WHITE);

        ComboBox<String> timePeriodCombo = new ComboBox<>();
        timePeriodCombo.getItems().addAll("Last 7 Days", "Last Month");
        timePeriodCombo.setValue("Last 7 Days");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(150);
        reportArea.setWrapText(true);
        Button generateButton = new Button("Generate Report");
        generateButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 12px 20px; -fx-border-radius: 5;");
        generateButton.setFont(new javafx.scene.text.Font("Arial", 14));
        generateButton.setOnAction(event -> {
            String timePeriod = timePeriodCombo.getValue();
            if (timePeriod == null || timePeriod.isEmpty()) {
                reportArea.setText("Please select a valid time period.");
            } else {
                // Call the controller's method to generate the report
                String report = controller.generateSalesReport(timePeriod);
                reportArea.setText(report);
            }
        });

        parentLayout.setEffect(createDropShadowEffect());
        parentLayout.getChildren().addAll(titleLabel, timePeriodLabel, timePeriodCombo, generateButton, reportArea);

        return parentLayout;
    }
  
    private DropShadow createDropShadowEffect() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        return dropShadow;
    }
}
