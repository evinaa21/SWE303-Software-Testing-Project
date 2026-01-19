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
 * Controller class responsible for managing the bill creation process.
 * Handles item selection, bill calculations, and bill finalization.
 * Works in conjunction with FileHandler for data persistence.
 *
 * @author Member 3 - Cashier & Integration Specialist
 * @version 1.0
 */
public class CreateBillController {
	private final FileHandler fileHandler; // Handles file operations
	private final VBox itemsContainer; // Container for displaying items added to the bill
	private final TextField totalField; // Field to display the current total amount
	private final ComboBox<String> categoryDropdown; // Drop down for selecting categories
	private final ComboBox<String> itemDropdown; // Drop down for selecting items
	private final ArrayList<Item> inventory; // Full inventory for the cashier's assigned sector
	private final ArrayList<Item> billItems; // Items added to the current bill
	private final Sector assignedSector; // Sector assigned to the cashier

	/**
	 * Constructs a CreateBillController with the specified UI elements and sector.
	 * Initializes the controller by loading inventory, populating categories, and setting up selection handlers.
	 *
	 * @param itemsContainer the VBox container for displaying bill items
	 * @param totalField the TextField for displaying the running total
	 * @param categoryDropdown the ComboBox for category selection
	 * @param itemDropdown the ComboBox for item selection
	 * @param assignedSector the Sector assigned to the current cashier
	 */
	public CreateBillController(VBox itemsContainer, TextField totalField, ComboBox<String> categoryDropdown,
			ComboBox<String> itemDropdown, Sector assignedSector) {
		this.fileHandler = new FileHandler();
		// Fix EI_EXPOSE_REP2: Store references directly for UI components (necessary for UI interaction)
		// UI components need direct references to function properly
		this.itemsContainer = itemsContainer;
		this.totalField = totalField;
		this.categoryDropdown = categoryDropdown;
		this.itemDropdown = itemDropdown;
		// Note: Sector is stored directly as it's needed for category operations
		this.assignedSector = assignedSector;
		this.inventory = new ArrayList<>();
		this.billItems = new ArrayList<>();

		loadInventory(); // Load inventory for the assigned sector
		populateCategories(); // Populate category drop down
		setupCategorySelection(); // Initialize category selection functionality
	}

	/**
	 * Loads all inventory items from the file system into memory.
	 * Called during controller initialization.
	 */
	private void loadInventory() {
		ArrayList<Item> allItems = fileHandler.loadInventory(); // Load all items from the inventory file
		this.inventory.addAll(allItems);
	}

	/**
	 * Populates the category dropdown with categories from the assigned sector.
	 */
	private void populateCategories() {
		ArrayList<String> categories = this.assignedSector.getCategories();
		categoryDropdown.getItems().addAll(categories);
	}

	/**
	 * Sets up the category selection event handler.
	 * When a category is selected, filters and displays items belonging to that category.
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
	 * Adds an item to the current bill with the specified quantity.
	 * Validates item selection, checks stock availability, updates inventory,
	 * and refreshes the UI with the new bill item.
	 *
	 * @param itemName the name of the item to add
	 * @param quantity the quantity of the item to add (must be positive and within available stock)
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
	 * Finalizes the current bill by saving it to the file system.
	 * Validates that items have been added, saves the bill using FileHandler,
	 * updates inventory stock levels, and resets the UI for the next bill.
	 *
	 * @param cashierName the name of the cashier processing the bill
	 * @param sector the store sector where the sale is being made
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
	 * Resets all UI fields and clears the current bill items.
	 * Called after successful bill finalization or when starting a new bill.
	 */
	private void resetFields() {
		itemsContainer.getChildren().clear();
		totalField.setText("0.00");
		categoryDropdown.getSelectionModel().clearSelection();
		itemDropdown.getItems().clear();
		this.billItems.clear();
	}

	/**
	 * Generates a unique bill number using the current system timestamp.
	 *
	 * @return a unique bill number in the format "BILL-{timestamp}"
	 */
	private String generateBillNumber() {
		return "BILL-" + System.currentTimeMillis();
	}

	/**
	 * Displays an alert dialog to the user.
	 *
	 * @param title the title of the alert dialog
	 * @param message the message content to display
	 * @param type the type of alert (ERROR, INFORMATION, etc.)
	 */
	private void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Displays an error alert to the user.
	 *
	 * @param message the error message to display
	 */
	private void showError(String message) {
		showAlert("Error", message, Alert.AlertType.ERROR);
	}

	/**
	 * Displays a success alert to the user.
	 *
	 * @param message the success message to display
	 */
	private void showSuccess(String message) {
		showAlert("Success", message, Alert.AlertType.INFORMATION);
	}

}
