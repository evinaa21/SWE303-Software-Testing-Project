package view;

import controller.CashierController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Cashier;
import model.Sector;

public class CashierView {

	private CashierController cashierController;
	private Cashier cashier;
	private Sector assignedSector; // Assigned sector for the cashier

	public CashierView(CashierController cashierController, Stage primaryStage, Cashier cashier) {
		this.cashierController = cashierController;
		this.cashier = cashier;
		new Label();
		this.assignedSector = cashier.getSector(); // Initialize the assigned sector
	}

	public void setupUI(BorderPane mainLayout, StackPane centerContent) {
		// Create the home screen content
		VBox homeContent = showHomeContent();
		centerContent.getChildren().add(homeContent);

		// Create navigation bar
		HBox navigationBar = createNavigationBar();

		// Add navigation bar and home content to the main layout
		mainLayout.setTop(navigationBar);
		mainLayout.setCenter(centerContent);
		mainLayout.setStyle("-fx-background-color: #2C3E50;");
	}

	// Show the default home content with a welcome message
	public VBox showHomeContent() {
		// Create welcome message and sector info
		Text welcomeMessage = new Text("Welcome, Cashier!");
		welcomeMessage.setStyle("-fx-font-size: 24px; -fx-fill: white;");
		welcomeMessage.setEffect(new DropShadow(5, Color.LIGHTGRAY));

		Text sectorInfo = new Text("Sector Assigned: " + assignedSector.getName());
		sectorInfo.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		sectorInfo.setEffect(new DropShadow(3, Color.GRAY));

		// Name and email information
		Text cashierInfo = new Text("Name: " + cashier.getName() + "\nEmail: " + cashier.getEmail());
		cashierInfo.setStyle("-fx-font-size: 16px; -fx-fill: white;");
		cashierInfo.setEffect(new DropShadow(3, Color.GRAY));

		Text header = new Text("Dashboard");
		header.setStyle("-fx-font-size: 20px; -fx-fill: white;");
		header.setEffect(new DropShadow(3, Color.GRAY));

		// Layout for the home content
		VBox homeContent = new VBox(20);
		homeContent.setAlignment(Pos.CENTER);
		homeContent.getChildren().addAll(welcomeMessage, sectorInfo, cashierInfo, header);

		return homeContent;
	}

	// Create navigation bar with buttons
	private HBox createNavigationBar() {
		HBox navigationBar = new HBox(10);
		navigationBar.setAlignment(Pos.CENTER);
		navigationBar.setPadding(new Insets(10));
		navigationBar.setStyle("-fx-background-color: #34495E;");

		Button homeButton = createNavButton("Home");
		Button createBillButton = createNavButton("Create Bill");
		Button dailyBillsButton = createNavButton("Daily Bills");
		

		// Set action for the Home button
		homeButton.setOnAction(e -> {
			cashierController.openHomePage(); // Calls the openHomePage method from CashierController
		});

		// Set action for Create Bill button
		createBillButton.setOnAction(e -> {
			cashierController.openCreateBillView(); // Calls the openCreateBillView method
		});

		// Set action for Daily Bills button
		dailyBillsButton.setOnAction(e -> {
			cashierController.openDailyBillsView(); // Calls the openDailyBillsView method
		});


		// Add navigation buttons to the bar
		navigationBar.getChildren().addAll(homeButton, createBillButton, dailyBillsButton);

		return navigationBar;
	}

	private Button createNavButton(String text) {
		Button button = new Button(text);
		button.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;" + "-fx-font-size: 14px;"
				+ "-fx-font-weight: bold;" + "-fx-padding: 10px 15px;" + "-fx-border-color: #bdc3c7;"
				+ "-fx-border-width: 1px;" + "-fx-border-radius: 5px;" + "-fx-background-radius: 5px;");

		button.setOnMouseEntered(
				e -> button.setStyle("-fx-background-color: #2980B9;" + "-fx-text-fill: white;" + "-fx-font-size: 14px;"
						+ "-fx-font-weight: bold;" + "-fx-padding: 10px 15px;" + "-fx-border-color: #95a5a6;"
						+ "-fx-border-width: 1px;" + "-fx-border-radius: 5px;" + "-fx-background-radius: 5px;"));

		button.setOnMouseExited(e -> button
				.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;" + "-fx-font-size: 14px;"
						+ "-fx-font-weight: bold;" + "-fx-padding: 10px 15px;" + "-fx-border-color: #bdc3c7;"
						+ "-fx-border-width: 1px;" + "-fx-border-radius: 5px;" + "-fx-background-radius: 5px;"));

		return button;
	}

	public void showHomePage() {
		VBox homeContent = showHomeContent();
		StackPane centerContent = cashierController.getCenterContent();
		centerContent.getChildren().clear(); // Clear existing content
		centerContent.getChildren().add(homeContent); // Add new content to the central pane
	}

	public static void showOutOfStockAlert(String selectedItemName) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Out of Stock Alert");
		alert.setHeaderText("Item Out of Stock");
		alert.setContentText("The item '" + selectedItemName + "' is out of stock. Please choose another item.");
		alert.showAndWait();
	}
}