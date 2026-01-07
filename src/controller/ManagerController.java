package controller;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Manager;
import model.Sector;
import model.Supplier;
import model.Item;
import util.FileHandler;
import util.FileHandlerMANAGER;
import view.*;

import java.util.ArrayList;

public class ManagerController {
    private Stage primaryStage;
    private Manager manager;
    private FileHandlerMANAGER fileHandler;
    private BorderPane mainLayout;
    private StackPane centerContent;
    private Scene managerScene;
    private FileHandler fileHandler1;

    public ManagerController(Stage primaryStage, Manager manager) {
        this.primaryStage = primaryStage;
        this.manager = manager;
        this.fileHandler = new FileHandlerMANAGER();
        this.fileHandler1 = new FileHandler();
        this.mainLayout = new BorderPane();
        this.centerContent = new StackPane();
        loadDataFromFiles();
        setupUI();
    }

    private void loadDataFromFiles() { //Loads data the moment the stage is opened
        ArrayList<Item> items = fileHandler.loadInventory();
        manager.setItems(items);
        ArrayList<Supplier> suppliers = fileHandler.loadSuppliers();
        manager.setSuppliers(suppliers);
        ArrayList<Sector> loadedSectors = fileHandler.loadManagerSectors();
        manager.setSectors(loadedSectors);
    }

    private void setupUI() {
        ManagerView managerView = new ManagerView(this, primaryStage, manager, fileHandler);
        managerView.setupUI(mainLayout, centerContent);
        managerScene = new Scene(mainLayout, 1000, 800);
        primaryStage.setTitle("Manager Dashboard");
        primaryStage.setScene(managerScene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void openHomePage() {
        ManagerView managerView = new ManagerView(this, primaryStage, manager, fileHandler);
        managerView.showHomePage();
    }

    public void openAddItemView() {
 
        FileHandlerMANAGER fileHandler = new FileHandlerMANAGER(); 
        AddItemController addItemController = new AddItemController(manager, fileHandler);
        AddItemView addItemView = new AddItemView(manager, addItemController);
        
        updateCenterContent(addItemView.getViewContent());
    }


    public void openRestockItemView() {
        RestockItemView restockItemView = new RestockItemView(manager, fileHandler);
        updateCenterContent(restockItemView.getViewContent());
    }



    public void openGenerateReportView() {
        GenerateReportController generateReportController = new GenerateReportController(manager);
        GenerateReportView generateReportView = new GenerateReportView(manager, generateReportController);
        updateCenterContent(generateReportView.getViewContent());
    }


    public void openSupplierView() {
        SupplierView supplierView = new SupplierView(manager, fileHandler);
        updateCenterContent(supplierView.getViewContent());
    }

    public void openMonitorCashierPerformanceView() {
        MonitorCashierPerformanceController controller = new MonitorCashierPerformanceController(manager, fileHandler);
       
        MonitorCashierPerformanceView monitorCashierPerformanceView = new MonitorCashierPerformanceView(manager, controller);
        updateCenterContent(monitorCashierPerformanceView.getViewContent());
    }


    public void openViewSectorsView() {
        ViewSectorsView sectorsView = new ViewSectorsView(manager);
        updateCenterContent(sectorsView.getSceneContent());
    }

    public void openViewItemsView() {
        ViewItemsController viewItemsController = new ViewItemsController(manager, fileHandler);
        VBox containerLayout = new VBox();
        viewItemsController.showViewItemsView(containerLayout);
        updateCenterContent(containerLayout);
    }

    private void updateCenterContent(Node content) {
        centerContent.getChildren().clear();
        centerContent.getChildren().add(content);
    }

    public int getLowStockItemsCount() {
        // gets sectors of manager
        ArrayList<Sector> managerSectors = manager.getSectors();

        // low stock items
        ArrayList<Item> lowStockItems = fileHandler.notifyLowStockforManager(5, managerSectors);     
        return lowStockItems.size();
    }


    public Scene getManagerScene() {
        return managerScene;
    }

    public BorderPane getMainLayout() {
        return mainLayout;
    }

    public StackPane getCenterContent() {
        return centerContent;
    }
}
