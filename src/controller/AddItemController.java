package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Item;
import model.Manager;
import util.FileHandlerMANAGER;

import java.util.ArrayList;

public class AddItemController {
    private Manager manager;
    private FileHandlerMANAGER fileHandler;

    public AddItemController(Manager manager, FileHandlerMANAGER fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;
    }

    public void loadSuppliersIntoComboBox(ComboBox<String> supplierComboBox) {
        ArrayList<String> supplierNames = manager.getSupplierNames();  // Placeholder
        if (supplierNames != null && !supplierNames.isEmpty()) {
            supplierComboBox.getItems().addAll(supplierNames);
        } else {
            supplierComboBox.setPromptText("No suppliers available");
        }
    }

    public void chooseImage(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        var file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
        }
    }

	public void addNewItem(Item newItem) {
		ArrayList<Item> inventory = fileHandler.loadInventory(); 
		inventory.add(newItem); 
		fileHandler.saveInventory(inventory); 
		System.out.println("New item added to inventory: " + newItem.getItemName());
	}

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

