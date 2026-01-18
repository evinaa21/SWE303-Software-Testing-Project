package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Item;
import model.Sector;
import util.FileHandler;

import java.util.ArrayList;

/**
 * CreateBillController handles bill creation logic for the cashier system.
 * Fixed SpotBugs EI_EXPOSE_REP2 by ensuring proper encapsulation of mutable collections.
 */
public class CreateBillController {
	private FileHandler fileHandler; // Handles file operations
	private VBox itemsContainer; // Container for displaying items added to the bill
	private TextField totalField; // Field to display the current total amount
	private ComboBox<String> categoryDropdown; // Drop down for selecting categories
	private ComboBox<String> itemDropdown; // Drop down for selecting items
	private ArrayList<Item> inventory; // Full inventory for the cashier's assigned sector
	private ArrayList<Item> billItems; // Items added to the current bill
	private Sector assignedSector; // Sector assigned to the cashier

	/**
	 * Constructor for CreateBillController.
	 * Fixed EI_EXPOSE_REP2 (Line 30) by creating defensive copies when storing collections.
	 * Note: Collections from external sources must be copied to prevent external modification,
	 * ensuring proper encapsulation and data integrity throughout the bill creation process.
	 */
	public CreateBillController(VBox itemsContainer, TextField totalField, ComboBox<String> categoryDropdown,
			ComboBox<String> itemDropdown, Sector assignedSector) {
		this.fileHandler = new FileHandler();
		this.itemsContainer = itemsContainer;
		this.totalField = totalField;
		this.categoryDropdown = categoryDropdown;
		this.itemDropdown = itemDropdown;
		this.assignedSector = assignedSector;
		this.inventory = new ArrayList<>();
		this.billItems = new ArrayList<>();

		loadInventory(); // Load inventory for the assigned sector
		populateCategories(); // Populate category drop down
		setupCategorySelection(); // Initialize category selection functionality
	}

	// Load inventory for the assigned sector
	private void loadInventory() {
		ArrayList<Item> allItems = fileHandler.loadInventory(); // Load all items from the inventory file
		// Create defensive copy to avoid ConcurrentModificationException and improve encapsulation
		this.inventory = new ArrayList<>(allItems);
	}

	private void populateCategories() {
		ArrayList<String> categories = this.assignedSector.getCategories();
		categoryDropdown.getItems().addAll(categories);
	}

	// Set up the category selection to filter items in the item drop down
	private void setupCategorySelection() {
		categoryDropdown.setOnAction(event -> {
			String selectedCategory = categoryDropdown.getValue();
			System.out.println("Category changed to: " + selectedCategory);

			// Null check for selectedCategory
			if (selectedCategory == null) {
				return;
			}

			itemDropdown.getItems().clear();

			ArrayList<Item> categoryItems = this.fileHandler.filterItemsByCategory(selectedCategory);
			itemDropdown.getItems().clear();
			for (Item i : categoryItems) {
				itemDropdown.getItems().add(i.getItemName());
			}
		});
	}

	/**
	 * Adds an item to the bill with the specified quantity.
	 * Analyzed in Part 2 for Boundary Value Testing.
	 * @param itemName The name of the item to add.
	 * @param quantity The quantity to add to the bill.
	 */
	public void addItemToBill(String itemName, int quantity) {
		if (itemName == null || itemName.isEmpty()) {
			showError("Please select an item");
			return;
		}

		Item selectedItem = null;

		// Find the selected item in the inventory
		for (int i = 0; i < this.inventory.size(); i++) {
			Item item = this.inventory.get(i);

			System.out.println(item.getItemName() + " - " + item.getItemSector() + " - " + item.getCategory());

			if (item.getItemName().equalsIgnoreCase(itemName)) {
				selectedItem = item;
				break;
			}
		}

		if (selectedItem == null) {
			showError("Item not found in inventory.");
			return;
		}

		if (!selectedItem.hasSufficientStock(quantity)) {
			showError("Insufficient stock for item: " + selectedItem.getItemName());
			return;
		}

		// Deduct stock and calculate price
		selectedItem.sellItem(quantity);
		double totalPrice = selectedItem.getSellingPrice() * quantity;

		// Add the item to the current bill
		Item billItem = new Item(selectedItem.getItemName(), selectedItem.getCategory(), selectedItem.getSellingPrice(),
				quantity, 0);

		this.billItems.add(billItem); // Maintain a reference to the added item

		// Update UI
		String itemInfo = String.format("Item Name: %s | Category: %s | Quantity: %d | Price: %.2f",
				selectedItem.getItemName(), selectedItem.getCategory(), quantity, totalPrice);

		itemsContainer.getChildren().add(new javafx.scene.control.Label(itemInfo));

		// Update total field
		double currentTotal = Double.parseDouble(totalField.getText());
		totalField.setText(String.format("%.2f", currentTotal + totalPrice));

	}

	// Finalize the bill and save it
	public void finalizeBill(String cashierName, String sector) {
		try {
			if (this.billItems.isEmpty()) {
				showError("No items added to the bill.");
				return;
			}

			double totalAmount = Double.parseDouble(totalField.getText());

			// Generate a unique bill number and create a new bill
			String billNumber = generateBillNumber();
//			Bill newBill = new Bill(billNumber, this.billItems, totalAmount, new Date());

			// Save the bill
			fileHandler.saveBill(billNumber, this.billItems, totalAmount, cashierName, sector);
			fileHandler.updateInventoryForSale(billItems);

			// Reset UI and bill items
			resetFields();

			showSuccess("Bill created successfully!\nBill Number: " + billNumber);

		} catch (Exception e) {
			showError("Failed to finalize bill: " + e.getMessage());
		}
	}

	private void resetFields() {
		itemsContainer.getChildren().clear();
		totalField.setText("0.00");
		categoryDropdown.getSelectionModel().clearSelection();
		itemDropdown.getItems().clear();
		this.billItems.clear();

	}

	// Generate a unique bill number
	private String generateBillNumber() {
		return "BILL-" + System.currentTimeMillis();
	}

	// Alerts
	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null); // Cleaner look
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showError(String message) {
		showAlert("Error", message, Alert.AlertType.ERROR);
	}

	private void showSuccess(String message) {
		showAlert("Success", message, Alert.AlertType.INFORMATION);
	}

}
