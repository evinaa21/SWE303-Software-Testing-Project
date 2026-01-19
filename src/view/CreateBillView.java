package view;

import controller.CreateBillController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Cashier;
import model.Sector;

public class CreateBillView {
    private BorderPane mainLayout;
    private VBox itemsContainer;
    private ComboBox<String> categoryDropdown;
    private ComboBox<String> itemDropdown;
    private TextField quantityField;
    private TextField totalField;
    private Button addItemButton;
    private Button finalizeBillButton;

    private Sector sector;
    private Cashier cashier;
    private CreateBillController createBillController;

    public CreateBillView(Sector sector) {
        this.sector = sector;

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Input section
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        Label categoryLabel = new Label("Category:");
        categoryDropdown = new ComboBox<>();
        categoryDropdown.setPromptText("Select Category");


        Label itemLabel = new Label("Item:");
        itemDropdown = new ComboBox<>();
        itemDropdown.setPromptText("Select Item");

        Label quantityLabel = new Label("Quantity:");
        quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");

        addItemButton = new Button("Add Item");
        addItemButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold;");

        inputBox.getChildren().addAll(categoryLabel, categoryDropdown, itemLabel, itemDropdown, quantityLabel,
                quantityField, addItemButton);


        itemsContainer = new VBox(10);
        itemsContainer.setPadding(new Insets(10));
        itemsContainer.setStyle("-fx-background-color: #f4f4f4;"); // Background color for visibility


        HBox totalBox = new HBox(10); // Horizontal layout for total and button
        totalBox.setAlignment(Pos.CENTER_RIGHT); // Align elements to the right


        Label totalLabel = new Label("Total:");
        totalField = new TextField("0.00"); // Default value for total
        totalField.setEditable(false); // Prevent user from editing the total

        // Button to finalize the bill
        finalizeBillButton = new Button("Finalize Bill");
        finalizeBillButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");


        totalBox.getChildren().addAll(totalLabel, totalField, finalizeBillButton);

        mainLayout.setTop(inputBox);
        mainLayout.setCenter(itemsContainer);
        mainLayout.setBottom(totalBox);

        this.createBillController = new CreateBillController(itemsContainer, totalField, categoryDropdown, itemDropdown,
                sector);

        addItemButton.setOnAction(event -> {
            String selectedItemName = itemDropdown.getValue();
            String quantityValue = quantityField.getText();
            
            // --- FIXED LINE BELOW ---
            // Changed '!=' to '!quantityValue.isEmpty()' to correctly check string content
            int selectedQuantity = (quantityValue != null && !quantityValue.isEmpty()) 
                                    ? Integer.parseInt(quantityValue) : 1;
            
            this.createBillController.addItemToBill(selectedItemName, selectedQuantity);
        });

        finalizeBillButton.setOnAction(event -> {
            // Added check to prevent NullPointerException if cashier is not set yet
            String cashierName = (this.cashier != null) ? this.cashier.getName() : "Unknown";
            this.createBillController.finalizeBill(cashierName, this.sector.getName());
        });

    }

    // Getters
    public BorderPane getViewContent() {
        return mainLayout;
    }

    public ComboBox<String> getCategoryDropdown() {
        return categoryDropdown;
    }

    public ComboBox<String> getItemDropdown() {
        return itemDropdown;
    }

    public TextField getQuantityField() {
        return quantityField;
    }

    public TextField getTotalField() {
        return totalField;
    }

    public Button getAddItemButton() {
        return addItemButton;
    }

    public Button getFinalizeBillButton() {
        return finalizeBillButton;
    }

    public VBox getItemsContainer() {
        return itemsContainer;
    }

    // Add an item to the items container
    public void addItemToDisplay(String itemInfo) {
        Label itemLabel = new Label(itemInfo);
        itemsContainer.getChildren().add(itemLabel);
    }

    // Update the total field
    public void updateTotal(double total) {
        totalField.setText(String.format("%.2f", total));
    }

    // Reset all fields to their initial state
    public void resetView() {
        itemsContainer.getChildren().clear();
        totalField.setText("0.00");
        categoryDropdown.getSelectionModel().clearSelection();
        itemDropdown.getItems().clear();
        quantityField.clear();
    }

    // Validate input fields
    public boolean validateInputs() {
        boolean isValid = true;

        if (categoryDropdown.getValue() == null) {
            categoryDropdown.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            categoryDropdown.setStyle(null);
        }

        if (itemDropdown.getValue() == null) {
            itemDropdown.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            itemDropdown.setStyle(null);
        }

        if (quantityField.getText().isEmpty() || !quantityField.getText().matches("\\d+")) {
            quantityField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            quantityField.setStyle(null);
        }

        return isValid;
    }

    public void setCashier(Cashier c) {
        this.cashier = c;
    }
}