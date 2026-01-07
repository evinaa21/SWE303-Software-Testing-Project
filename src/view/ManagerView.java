package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import controller.ManagerController;
import model.Manager;
import model.Sector;
import util.FileHandlerMANAGER;
import model.Item;

import java.util.ArrayList;

public class ManagerView {

    private ManagerController managerController;

    private Manager manager;
    private FileHandlerMANAGER fileHandler;

    public ManagerView(ManagerController managerController, Stage primaryStage, Manager manager, FileHandlerMANAGER fileHandler) {
        this.managerController = managerController;
        this.manager = manager;
        this.fileHandler = fileHandler;
    }

    public void setupUI(BorderPane mainLayout, StackPane centerContent) {
        VBox homeContent = createHomeContent();
        centerContent.getChildren().add(homeContent);

        HBox navigationBar = createNavigationBar();
        mainLayout.setTop(navigationBar);
        mainLayout.setCenter(centerContent);
        mainLayout.setStyle("-fx-background-color: #2C3E50;");
    }

    private VBox createHomeContent() {
        Text welcomeMessage = new Text("Welcome, Manager!");
        welcomeMessage.setStyle("-fx-font-size: 24px; -fx-fill: white;");
        welcomeMessage.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        Text managerInfo = new Text("Name: " + manager.getName() + "\nEmail: " + manager.getEmail());
        managerInfo.setStyle("-fx-font-size: 18px; -fx-fill: white;");
        managerInfo.setEffect(new DropShadow(3, Color.GRAY));

        Text header = new Text("Dashboard");
        header.setStyle("-fx-font-size: 20px; -fx-fill: white;");
        header.setEffect(new DropShadow(3, Color.GRAY));

        VBox homeContent = new VBox(20);
        homeContent.setAlignment(Pos.CENTER);
        homeContent.getChildren().addAll(welcomeMessage, managerInfo, header);

        String lowStockInfo = getLowStockInfo();
        Text lowStockMessage = new Text(lowStockInfo);
        lowStockMessage.setStyle("-fx-font-size: 18px; -fx-fill: white;");
        homeContent.getChildren().add(lowStockMessage);

        return homeContent;
    }

    private HBox createNavigationBar() {
        HBox navigationBar = new HBox(20);
        navigationBar.setAlignment(Pos.CENTER);
        navigationBar.setStyle("-fx-background-color: #34495E;");

        Button homeButton = createNavButton("Home");
        Button addItemButton = createNavButton("Add New Item");
        Button restockItemButton = createNavButton("Restock Item");
        Button generateReportButton = createNavButton("Generate Sales Report");
        Button manageSuppliersButton = createNavButton("Manage Suppliers");
        Button monitorCashierButton = createNavButton("Monitor Cashier Performance");
        Button viewSectorsButton = createNavButton("View Sectors");
        Button viewItemsButton = createNavButton("View Items");

        homeButton.setOnAction(e -> managerController.openHomePage());
        addItemButton.setOnAction(e -> managerController.openAddItemView());
        restockItemButton.setOnAction(e -> managerController.openRestockItemView());
        generateReportButton.setOnAction(e -> managerController.openGenerateReportView());
        manageSuppliersButton.setOnAction(e -> managerController.openSupplierView());
        monitorCashierButton.setOnAction(e -> managerController.openMonitorCashierPerformanceView());
        viewSectorsButton.setOnAction(e -> managerController.openViewSectorsView());
        viewItemsButton.setOnAction(e -> managerController.openViewItemsView());

        navigationBar.getChildren().addAll(
                homeButton, addItemButton, restockItemButton,
                generateReportButton, manageSuppliersButton, monitorCashierButton, viewSectorsButton, viewItemsButton
        );

        return navigationBar;
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px 15px;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #2980B9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px 15px;" +
                        "-fx-border-color: #95a5a6;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px 15px;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"));
        return button;
    }

    public void showHomePage() {
        VBox homeContent = createHomeContent();
        StackPane centerContent = managerController.getCenterContent();
        centerContent.getChildren().clear();
        centerContent.getChildren().add(homeContent);
    }
    private String getLowStockInfo() {
        ArrayList<Sector> managerSectors = manager.getSectors();  // gets manager's assigned sectors
        ArrayList<Item> lowStockItems = fileHandler.notifyLowStockforManager(5, managerSectors);  
        if (lowStockItems.isEmpty()) {
            return "No low stock items.";
        }
        StringBuilder lowStockInfo = new StringBuilder("Low Stock Items:\n");

        for (Item item : lowStockItems) {
            boolean isCategoryInSector = false;
            for (Sector sector : managerSectors) {
                // Check if the item’s category is in the sector's categories
                if (sector.getCategories().contains(item.getCategory())) {
                    isCategoryInSector = true;
                    break;
                }
            }

            // If the category is in the sector, add the item to the list
            if (isCategoryInSector && item.getStockQuantity() <= 5) {
                lowStockInfo.append("Item Name: ").append(item.getItemName())
                            .append(", Category: ").append(item.getCategory())
                            .append(", Stock: ").append(item.getStockQuantity())
                            .append("\n");
            }
        }

        return lowStockInfo.toString();
    }

    }

