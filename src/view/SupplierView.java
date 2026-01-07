package view;

import controller.SupplierController;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import model.Manager;
import model.Supplier;
import model.Item;
import util.FileHandlerMANAGER;

import java.util.ArrayList;

public class SupplierView {

    private SupplierController supplierController;
    private Manager manager;

    public SupplierView(Manager manager, FileHandlerMANAGER fileHandler) {
        this.manager = manager;
        this.supplierController = new SupplierController(manager, this, fileHandler);

        // Load suppliers and items from file with null checks
        this.manager.setSuppliers(getNonNullList(fileHandler.loadSuppliers()));
        this.manager.setItems(getNonNullList(fileHandler.loadInventory()));
        for (Supplier supplier : manager.getSuppliers()) {
            if (supplier.getSuppliedItems() == null) {
                supplier.setSuppliedItems(new ArrayList<>()); 
            }
        }
    }


    public VBox getViewContent() {
        VBox supplierLayout = new VBox(30);
        ListView<HBox> supplierListView = new ListView<>();
     
        supplierLayout.setPrefWidth(Double.MAX_VALUE);
        supplierLayout.setPrefHeight(Double.MAX_VALUE);

        for (Supplier supplier : manager.getSuppliers()) {
            HBox supplierItem = createSupplierItem(supplier, supplierListView);
            supplierListView.getItems().add(supplierItem);
        }

        Button addSupplierButton = new Button("Add Supplier");
        addSupplierButton.setOnAction(e -> showAddSupplierDialog(supplierListView));

        addSupplierButton.setMaxWidth(100);

        supplierLayout.getChildren().addAll(supplierListView, addSupplierButton);
        supplierLayout.setPadding(new Insets(20));

        VBox.setVgrow(supplierListView, Priority.ALWAYS); 

        return supplierLayout;
    }


    private HBox createSupplierItem(Supplier supplier, ListView<HBox> supplierListView) {
        VBox itemListLayout = new VBox(5);
        itemListLayout.setPadding(new Insets(5));
        itemListLayout.setVisible(false);

        // Populate the item list for the supplier
        itemListLayout.getChildren().clear();
        for (Item item : supplier.getSuppliedItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);

            Label itemLabel = new Label(item.toString());
            itemRow.getChildren().add(itemLabel);
            itemListLayout.getChildren().add(itemRow);
        }

        HBox supplierItem = new HBox(10);
        supplierItem.setAlignment(Pos.CENTER_LEFT);
        supplierItem.setPadding(new Insets(5));

        Label supplierNameLabel = new Label(supplier.getSupplierName());
        Button toggleItemsButton = new Button("Show Items");

        toggleItemsButton.setOnAction(e -> {
            itemListLayout.setVisible(!itemListLayout.isVisible());
            toggleItemsButton.setText(itemListLayout.isVisible() ? "Hide Items" : "Show Items");
        });

        supplierItem.getChildren().addAll(supplierNameLabel, toggleItemsButton, itemListLayout);
        return supplierItem;
    }

   
    private void showAddSupplierDialog(ListView<HBox> supplierListView) {
        // Create a dialog to enter the supplier's name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Supplier");
        dialog.setHeaderText("Enter Supplier Name");
        dialog.setContentText("Supplier Name:");

        dialog.showAndWait().ifPresent(supplierName -> {
            if (!supplierName.trim().isEmpty()) {
                supplierController.addSupplier(supplierName, supplierListView);
                refreshSupplierList(supplierListView); 
            } else {
                showErrorDialog("Supplier name cannot be empty.");
            }
        });
    }

    public void refreshSupplierList(ListView<HBox> supplierListView) {
        supplierListView.getItems().clear(); 
        for (Supplier supplier : manager.getSuppliers()) {
            HBox supplierItem = createSupplierItem(supplier, supplierListView);
            supplierListView.getItems().add(supplierItem); 
        }
    }

    public void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public void showSuccessDialog(String successMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(successMessage);
        alert.showAndWait();
    }


    public void setFileHandler(FileHandlerMANAGER fileHandler) {
    }

    // Utility method to handle null lists
    private <T> ArrayList<T> getNonNullList(ArrayList<T> list) {
        return list != null ? list : new ArrayList<>();
    }

}
