package view;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Item;
import model.Manager;
import util.FileHandlerMANAGER;

import java.util.ArrayList;

import controller.RestockItemController;

public class RestockItemView {

    private Manager manager;
    private FileHandlerMANAGER fileHandler;

    // Constructor to pass Manager and FileHandler instances
    public RestockItemView(Manager manager, FileHandlerMANAGER fileHandler) {
        if (manager == null || manager.getItems() == null) {
            throw new IllegalArgumentException("Manager or item list cannot be null.");
        }
        this.manager = manager;
        this.fileHandler = fileHandler;
    }
    
    public VBox getViewContent() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px; -fx-border-radius: 15px;");

        Label itemLabel = new Label("Select Item to Restock:");
        itemLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        ComboBox<String> itemComboBox = new ComboBox<>();
        styleComboBox(itemComboBox);

        ArrayList<Item> itemsToRestock = new ArrayList<>();
        for (Item item : manager.getItems()) {
            if (item.getStockQuantity() < 5) {
                itemsToRestock.add(item);
            }
        }

        if (itemsToRestock.isEmpty()) {
            VBox errorLayout = new VBox(20);
            errorLayout.setAlignment(Pos.CENTER);
            errorLayout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 30px; -fx-border-radius: 15px;");
            Label errorLabel = new Label("No items need restocking.");
            errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FF6F61;");
            errorLayout.getChildren().add(errorLabel);
            return errorLayout;
        }

        for (Item item : itemsToRestock) {
            itemComboBox.getItems().add(item.getItemName());
        }

        Label quantityLabel = new Label("Restock Quantity:");
        quantityLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        TextField quantityField = new TextField();
        styleTextField(quantityField);

        Button restockButton = new Button("Restock Item");
        styleButton(restockButton);

        restockButton.setOnAction(event -> {
            String selectedItem = itemComboBox.getValue();
            int quantity;

            if (selectedItem == null || selectedItem.isEmpty()) {
                showError("Please select an item to restock.");
                return;
            }

            if (quantityField.getText().isEmpty()) {
                showError("Quantity cannot be empty.");
                return;
            }

            try {
                quantity = Integer.parseInt(quantityField.getText());

                if (quantity <= 0) {
                    showError("Please enter a positive number for quantity.");
                    return;
                }

                // Call controller method to handle restocking
                RestockItemController controller = new RestockItemController(manager, fileHandler);
                controller.restockItem(selectedItem, quantity, itemsToRestock);

                showSuccess("Item restocked successfully!");

                // Refresh the ComboBox after restocking
                itemComboBox.getItems().clear();
                itemsToRestock.forEach(i -> itemComboBox.getItems().add(i.getItemName())); // lambda expression

            } catch (NumberFormatException e) {
                showError("Please enter a valid number for quantity.");
            }
        });

        layout.getChildren().addAll(itemLabel, itemComboBox, quantityLabel, quantityField, restockButton);
        return layout;
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px;");
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle("-fx-font-size: 16px; -fx-background-color: #ffffff;");
        comboBox.setPrefWidth(280);
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-font-size: 16px; -fx-background-color: #ffffff;");
        textField.setPrefWidth(280);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setFileHandler(FileHandlerMANAGER fileHandler) {
        this.fileHandler = fileHandler;
    }
}

