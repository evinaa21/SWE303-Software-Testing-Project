package controller;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Bill;
import model.Cashier;
import model.Item;
import model.Sector;
import util.FileHandler;
import view.CashierView;
import view.CreateBillView;
import view.DailyBillsView;

public class CashierController {

	private final Stage primaryStage; 
	private final Cashier cashier; 
	private final Sector assignedSector; 
	private final FileHandler fileHandler; 
	private final BorderPane mainLayout; 
	private final StackPane centerContent; 
	private Scene cashierScene;

	public CashierController(Stage primaryStage, Cashier cashier) {
		this.primaryStage = primaryStage; 
		this.cashier = cashier; 
		this.assignedSector = cashier.getSector(); 
		this.fileHandler = new FileHandler(); 
		this.mainLayout = new BorderPane(); 
		this.centerContent = new StackPane(); 
		loadDataFromFiles();
		setupUI(); 
	}

	// Load data from binary 
	private void loadDataFromFiles() {
	    ArrayList<Sector> sectors = fileHandler.loadSectors();
	    if (sectors.isEmpty()) {
	        throw new RuntimeException("Failed to load sectors.");
	    }

	    Sector sector = null;
	    for (Sector s : sectors) {
	        if (s.getName().equalsIgnoreCase(cashier.getSector().getName())) {
	            sector = s;
	            break;
	        }
	    }

	    if (sector != null) {
	        System.out.println("Sector found: " + sector.getName());
	        cashier.setSector(sector);
	    } else {
	        throw new RuntimeException("Failed to find the sector: " + cashier.getSector().getName());
	    }

	    ArrayList<Item> items = fileHandler.loadInventoryBySector(cashier.getSector().getName());
	    cashier.setItems(items);

	    // Load bills
	    try {
	        ArrayList<Bill> bills = fileHandler.loadBills();
	        cashier.setBills(bills);
	    } catch (Exception e) {
	        System.out.println("Error loading bills: " + e.getMessage());
	    }
	}

	private void setupUI() {

		CashierView cashierView = new CashierView(this, primaryStage, cashier);
		cashierView.setupUI(mainLayout, centerContent);

		cashierScene = new Scene(mainLayout, 1200, 600);
		primaryStage.setTitle("Cashier Dashboard");
		primaryStage.setScene(cashierScene);
		primaryStage.centerOnScreen();
		primaryStage.show();

	}

	public void openHomePage() {
		CashierView cashierView = new CashierView(this, primaryStage, cashier);
		cashierView.showHomePage();
	}

	public void openCreateBillView() {				
		CreateBillView createBillView = new CreateBillView(this.assignedSector);
		createBillView.setCashier(this.cashier);
		updateCenterContent(createBillView.getViewContent());
	}

	public void openDailyBillsView() {
		DailyBillsView dailyBillsView = new DailyBillsView();
		updateCenterContent(dailyBillsView.getViewContent());
	}

	private void updateCenterContent(Node content) {
		centerContent.getChildren().clear();
		centerContent.getChildren().add(content);
	}

	// Is it ou of stock
	public void handleItemSelection(String selectedItemName) {
		boolean isOutOfStock = fileHandler.isItemOutOfStock(selectedItemName, cashier.getSector().getName());

		if (isOutOfStock) {
			// Notify the cashier 
			CashierView.showOutOfStockAlert(selectedItemName);
		}
	}



	public Scene getCashierScene() {
		return cashierScene;
	}

	public BorderPane getMainLayout() {
		return mainLayout;
	}

	public StackPane getCenterContent() {
		return centerContent;
	}

}