package view;

import controller.DailyBillsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class DailyBillsView {

    private final BorderPane mainLayout; 
    private final VBox billsContainer;
    private final Button showBillsButton; 
    private final Button calculateTotalButton;
    private final Label totalSalesLabel; 
    private final DailyBillsController controller; 

    // Constructor
    public DailyBillsView() {
        mainLayout = new BorderPane(); // Initialize 
        billsContainer = new VBox(10); // Vertical layout
        billsContainer.setPadding(new Insets(10));
        billsContainer.setStyle("-fx-background-color: #f4f4f4;");

        // Controller initialization
        controller = new DailyBillsController(billsContainer);

        // Buttons
        showBillsButton = new Button("Show Today's Bills");
        showBillsButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
        calculateTotalButton = new Button("Calculate Sales by Cashier");
        calculateTotalButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");

        // Button Actions
        showBillsButton.setOnAction(event -> {
            controller.showTodaysBills(); 
        });

        calculateTotalButton.setOnAction(event -> {
            controller.calculateSalesByCashier(); // Calculate 
        });

        // Total sales label
        totalSalesLabel = new Label("Total Sales for Today: $0.00");
        totalSalesLabel.setFont(new Font("Arial", 16));
        totalSalesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4169E1;");
        HBox totalSalesBox = new HBox(totalSalesLabel);
        totalSalesBox.setAlignment(Pos.CENTER_RIGHT);
        totalSalesBox.setPadding(new Insets(10));

        // Assemble layout
        VBox buttonBox = new VBox(10, showBillsButton, calculateTotalButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        mainLayout.setLeft(buttonBox);
        mainLayout.setCenter(billsContainer);
        mainLayout.setBottom(totalSalesBox);
    }

    // Get the main layout
    public BorderPane getViewContent() {
        return mainLayout;
    }
}
