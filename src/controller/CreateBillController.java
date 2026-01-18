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
 * Analyzed in Part 2 for Equivalence Class Testing (payment validation).
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
	 * Constructor to initialize the controller with UI elements and assigned sector.
	 * Fixed EI_EXPOSE_REP2 (Line 30) by creating defensive copies of mutable collections.
	 * Note: Collections passed to constructors should be copied to prevent external modification.
	 * 
	 * @param itemsContainer Container for displaying bill items
	 * @param totalField Field showing current bill total
	 * @param categoryDropdown Dropdown for category selection
	 * @param itemDropdown Dropdown for item selection
	 * @param assignedSector The sector assigned to this cashier
	 */
	public CreateBillController(VBox itemsContainer, TextField totalField, ComboBox<String> categoryDropdown,
			ComboBox<String> itemDropdown, Sector assignedSector) {
		this.fileHandler = new FileHandler();
		this.itemsContainer = itemsContainer;
		this.totalField = totalField;
		this.categoryDropdown = categoryDropdown;
		this.itemDropdown = itemDropdown;
		this.assignedSector = assignedSector;
		this.inventory = new ArrayList<>(); // Initialize as empty, will be populated
		this.billItems = new ArrayList<>(); // Initialize as empty

		loadInventory(); // Load inventory for the assigned sector
		populateCategories(); // Populate category drop down
		setupCategorySelection(); // Initialize category selection functionality
	}

	/**
	 * Loads inventory for the assigned sector.
	 * Fixed EI_EXPOSE_REP2 by creating a defensive copy of loaded items.
	 */
	private void loadInventory() {
		ArrayList<Item> allItems = fileHandler.loadInventory(); // Load all items from the inventory file
		// Create defensive copy to prevent external modification
		this.inventory = new ArrayList<>(allItems);
	}

	/**
	 * Populates the category dropdown with categories from the assigned sector.
	 */
	private void populateCategories() {
		ArrayList<String> categories = this.assignedSector.getCategories();
		categoryDropdown.getItems().addAll(categories);
	}

	/**
	 * Sets up the category selection to filter items in the item dropdown.
	 */
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
	 * Adds an item to the bill with specified quantity.
	 * Validates item existence and stock availability.
	 * Analyzed in Part 2 using Boundary Value Testing for quantity validation.
	 * 
	 * @param itemName The name of the item to add
	 * @param quantity The quantity to add (must be positive and within stock limits)
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

	/**
	 * Finalizes the bill and saves it to the system.
	 * Validates that bill contains items before saving.
	 * 
	 * @param cashierName The name of the cashier completing the bill
	 * @param sector The sector where the sale occurred
	 */
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

	/**
	 * Resets all form fields and clears the current bill.
	 */
	private void resetFields() {
		itemsContainer.getChildren().clear();
		totalField.setText("0.00");
		categoryDropdown.getSelectionModel().clearSelection();
		itemDropdown.getItems().clear();
		this.billItems.clear();
	}

	/**
	 * Generates a unique bill number based on current timestamp.
	 * @return A unique bill identifier
	 */
	private String generateBillNumber() {
		return "BILL-" + System.currentTimeMillis();
	}

	/**
	 * Returns a defensive copy of the current bill items.
	 * Fixed EI_EXPOSE_REP by returning a copy instead of direct reference.
	 * @return A copy of the bill items list
	 */
	public ArrayList<Item> getBillItems() {
		return new ArrayList<>(this.billItems);
	}

	/**
	 * Returns a defensive copy of the inventory.
	 * Fixed EI_EXPOSE_REP by returning a copy instead of direct reference.
	 * @return A copy of the inventory list
	 */
	public ArrayList<Item> getInventory() {
		return new ArrayList<>(this.inventory);
	}

	/**
	 * Displays an alert dialog with specified title, message, and type.
	 * @param title The alert title
	 * @param message The alert message
	 * @param type The alert type
	 */
	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null); // Cleaner look
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Displays an error alert.
	 * @param message The error message to display
	 */
	private void showError(String message) {
		showAlert("Error", message, Alert.AlertType.ERROR);
	}

	/**
	 * Displays a success alert.
	 * @param message The success message to display
	 */
	private void showSuccess(String message) {
		showAlert("Success", message, Alert.AlertType.INFORMATION);
	}
}
