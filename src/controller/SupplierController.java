package controller;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.Manager;
import model.Supplier;
import util.FileHandlerMANAGER;
import model.Item;
import view.SupplierView;

public class SupplierController {
    private Manager manager;
    private SupplierView supplierView;
    private FileHandlerMANAGER fileHandler;

    public SupplierController(Manager manager, SupplierView supplierView, FileHandlerMANAGER fileHandler) {
        this.manager = manager;
        this.supplierView = supplierView;
        this.fileHandler = fileHandler;
        ArrayList<Supplier> suppliers = fileHandler.loadSuppliers();
        ArrayList<Item> items = fileHandler.loadInventory();
        manager.setSuppliers(suppliers);
        manager.setItems(items);

    }


    public void addSupplier(String name, ListView<HBox> supplierListView) {
        Supplier supplier = new Supplier(name);  

        manager.getSuppliers().add(supplier);
        saveSuppliersToFile(); 
        supplierView.refreshSupplierList(supplierListView);
        supplierView.showSuccessDialog("Supplier added successfully.");
    }
    
    // Add an item to a supplier
    public void addItemToSupplier(Supplier supplier, Item item, ListView<HBox> supplierListView) {
        if (item != null) {
            supplier.addItem(item);
            saveSuppliersToFile();
            fileHandler.saveInventory(manager.getItems());
            supplierView.refreshSupplierList(supplierListView);
            supplierView.showSuccessDialog("Item added successfully to supplier.");
        } else {
            supplierView.showErrorDialog("Invalid item selected.");
        }
    }


    public void deleteItemFromAll(Item item, ListView<HBox> itemListView) {
        fileHandler.deleteItemAndUpdateSuppliers(item, manager.getItems(), manager.getSuppliers());

        // Refresh the items and suppliers views
        itemListView.getItems().removeIf(hbox -> ((Label) hbox.getChildren().get(0)).getText().equals(item.getItemName())); // Example logic for updating UI
        supplierView.refreshSupplierList(itemListView); // Refresh the supplier view to reflect changes
    }

    // Save suppliers to the binary file
    private void saveSuppliersToFile() {
        fileHandler.saveSuppliers(manager.getSuppliers());
    }
}
