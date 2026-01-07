package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Manager;
import model.Item;
import view.RestockItemView;
import util.FileHandlerMANAGER;

import java.util.ArrayList;

public class RestockItemController {

    private Manager manager;
    private FileHandlerMANAGER fileHandler;

    public RestockItemController(Manager manager, FileHandlerMANAGER fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;
    }

    public void showRestockItemView() {
        RestockItemView restockItemView = new RestockItemView(manager, fileHandler);
        
        Stage restockItemStage = new Stage();
        restockItemStage.setTitle("Restock Item");
        restockItemStage.setScene(new Scene(restockItemView.getViewContent(), 400, 300));
        restockItemStage.show();
    }

    // Move file handling logic here
    public void restockItem(String selectedItem, int quantity, ArrayList<Item> itemsToRestock) {
        Item item = findItemByName(selectedItem, itemsToRestock);
        if (item != null) {
            // Restock the item
            item.restockItem(quantity);

            // Save updated inventory list back to the file
            fileHandler.saveInventory(manager.getItems());
        }
    }

    private Item findItemByName(String itemName, ArrayList<Item> itemsToRestock) {
        for (Item item : itemsToRestock) {
            if (item.getItemName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
}

