package view;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Item;
import model.Manager;
import util.FileHandlerMANAGER;

public class ViewItemsView {
    private Manager manager;
    private FileHandlerMANAGER fileHandler;
    private ArrayList<Item> items = new ArrayList<Item>();

    public ViewItemsView(Manager manager, FileHandlerMANAGER fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;
    }

    public ScrollPane getViewContent() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 20px;");

        Label searchLabel = new Label("Search Items:");
        searchLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter item name...");
        styleTextField(searchField);

        Label filterLabel = new Label("Filter by Category:");
        filterLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().add("All");
        ArrayList<String> categories = fileHandler.loadCategoriesBySectors();
        filterComboBox.getItems().addAll(categories);

        Label sortLabel = new Label("Sort by Price:");
        sortLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Low to High", "High to Low");
        sortComboBox.setValue("Low to High");
        sortComboBox.setStyle("-fx-font-size: 14px; -fx-border-radius: 10px; -fx-padding: 5px;");

        FlowPane itemsFlowPane = new FlowPane();
        itemsFlowPane.setHgap(30); // Horizontal gap between items
        itemsFlowPane.setVgap(30); // Vertical gap between items
        itemsFlowPane.setAlignment(Pos.CENTER); // Center items within the FlowPane
        itemsFlowPane.setPadding(new Insets(20)); // Add padding around the FlowPane

        itemsFlowPane.prefWidthProperty().bind(layout.widthProperty());

        // Wrap the FlowPane in a ScrollPane for scrolling
        ScrollPane itemsScrollPane = new ScrollPane(itemsFlowPane);
        itemsScrollPane.setFitToWidth(true); // Ensure it resizes horizontally
        itemsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        itemsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        itemsScrollPane.setStyle("-fx-background-color: transparent;");

        // Make itemsScrollPane grow to fill remaining space
        VBox.setVgrow(itemsScrollPane, Priority.ALWAYS);

        // Add listeners for filters using lambda
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters(searchField, filterComboBox, sortComboBox, itemsFlowPane));
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilters(searchField, filterComboBox, sortComboBox, itemsFlowPane));
        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilters(searchField, filterComboBox, sortComboBox, itemsFlowPane));

        // Reset Filters Button
        Button resetButton = new Button("Reset Filters");
        styleResetButton(resetButton);
        resetButton.setOnAction(e -> {
            searchField.clear();
            filterComboBox.setValue("All");
            sortComboBox.setValue("Low to High");
            displayItems(itemsFlowPane, "", "All", "Low to High");
        });

        HBox searchBox = new HBox(15, searchLabel, searchField);
        HBox filterBox = new HBox(15, filterLabel, filterComboBox);
        HBox sortBox = new HBox(15, sortLabel, sortComboBox);
        HBox buttonBox = new HBox(15, resetButton);

        searchBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        sortBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(searchBox, filterBox, sortBox, buttonBox, itemsScrollPane);

        displayItems(itemsFlowPane, "", "All", "Low to High");

        ScrollPane mainScrollPane = new ScrollPane(layout);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return mainScrollPane;
    }


    private void displayItems(FlowPane flowPane, String searchQuery, String categoryFilter, String sortOrder) {
        flowPane.getChildren().clear();

        items = fileHandler.loadInventory();

        if (categoryFilter == null) {
            categoryFilter = "All"; 
        }

        ArrayList<Item> filteredItems = new ArrayList<>();

        for (Item item : items) {
            boolean matchesSearchQuery = searchQuery == null || searchQuery.isEmpty() || 
                                          item.getItemName().toLowerCase().contains(searchQuery.toLowerCase());
            boolean matchesCategory = categoryFilter.equals("All") || 
                                      (item.getCategory() != null && item.getCategory().equalsIgnoreCase(categoryFilter));

            if (matchesSearchQuery && matchesCategory) {
                filteredItems.add(item);
            }
        }

        if ("Low to High".equals(sortOrder)) {
            filteredItems.sort(Comparator.comparingDouble(Item::getPrice)); // Ascending order
        } else if ("High to Low".equals(sortOrder)) {
            filteredItems.sort((item1, item2) -> Double.compare(item2.getPrice(), item1.getPrice())); // Descending order
        }

        for (Item item : filteredItems) {
            displayItem(flowPane, item);
        }
    }


    private void displayItem(FlowPane flowPane, Item item) {
        VBox itemBox = new VBox(10);
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 15px; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
        itemBox.setPrefWidth(200); // Adjust width for consistent sizing

        String imagePath = item.getImagePath();
        if (imagePath.startsWith("file:")) {
            imagePath = imagePath.substring(5); // Remove "file:" prefix
        }

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image itemImage = new Image(imageFile.toURI().toString());
                    ImageView itemImageView = new ImageView(itemImage);
                    itemImageView.setFitWidth(150); // Adjust image width
                    itemImageView.setFitHeight(150); // Adjust image height
                    itemImageView.setPreserveRatio(true);
                    itemImageView.setSmooth(true);
                    itemBox.getChildren().add(itemImageView);
                }
            } catch (Exception e) {
                System.out.println("Error loading image from path: " + imagePath);
                e.printStackTrace();
            }
        }

        Label itemName = new Label(item.getItemName());
        itemName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label itemPrice = new Label("$" + item.getPrice());
        itemPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        Label itemStock = new Label("Stock: " + item.getStockQuantity());
        itemStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");

        itemBox.getChildren().addAll(itemName, itemPrice, itemStock);

        // Add margin to itemBox
        VBox.setMargin(itemBox, new Insets(10));

        flowPane.getChildren().add(itemBox);

        itemBox.setOnMouseClicked(event -> openItemDetailsPage(item, (Stage) flowPane.getScene().getWindow()));
    }

    private void updateFilters(TextField searchField, ComboBox<String> filterComboBox, ComboBox<String> sortComboBox, FlowPane itemsFlowPane) {
        String searchQuery = searchField.getText();
        String categoryFilter = filterComboBox.getValue();
        String sortOrder = sortComboBox.getValue();
        displayItems(itemsFlowPane, searchQuery, categoryFilter, sortOrder);
    }

    private void styleResetButton(Button button) {
        button.setStyle("-fx-background-color: #003366; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10px; -fx-padding: 10px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #002244; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10px; -fx-padding: 10px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #003366; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10px; -fx-padding: 10px;"));
    }

    private void openItemDetailsPage(Item item, Stage ownerStage) {
        Stage itemStage = new Stage();
        itemStage.setTitle(item.getItemName() + " - Details");
        itemStage.initOwner(ownerStage);

        VBox itemDetailsLayout = new VBox(20);
        itemDetailsLayout.setAlignment(Pos.CENTER);
        itemDetailsLayout.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 20px;");

        try {
            String imagePath = item.getImagePath();
            if (imagePath != null && imagePath.startsWith("file:")) {
                imagePath = imagePath.substring(5);
            }
            if (imagePath != null && !imagePath.isEmpty()) {
                Image itemImage = new Image(new File(imagePath).toURI().toString());
                ImageView imageView = new ImageView(itemImage);
                imageView.setFitWidth(250);
                imageView.setFitHeight(250);
                itemDetailsLayout.getChildren().add(imageView);
            } else {
                Label errorLabel = new Label("No image available.");
                itemDetailsLayout.getChildren().add(errorLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading image.");
            itemDetailsLayout.getChildren().add(errorLabel);
        }

        Label itemName = new Label(item.getItemName());
        itemName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Text itemDescriptionText = new Text(item.getDescription());
        itemDescriptionText.setStyle("-fx-font-size: 16px;");
        itemDescriptionText.setWrappingWidth(350); 

        String description = item.getDescription();
        if (description.length() > 100) {
            itemDescriptionText.setText(description.substring(0, 100) + "...");
            Text moreText = new Text("Show More");
            moreText.setStyle("-fx-font-size: 14px; -fx-fill: #0066cc;");
            moreText.setOnMouseClicked(e -> itemDescriptionText.setText(description)); 
            itemDetailsLayout.getChildren().addAll(itemDescriptionText, moreText);
        } else {
            itemDetailsLayout.getChildren().add(itemDescriptionText);
        }

        Label itemPrice = new Label("Price: $" + item.getPrice());
        Label itemStock = new Label("Stock: " + item.getStockQuantity());

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> {
            boolean isDeleted = fileHandler.deleteItemAndUpdateSuppliers(item, manager.getItems(), manager.getSuppliers());

            if (isDeleted) {
                items = fileHandler.loadInventory();
                itemStage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item deleted successfully.", ButtonType.OK);
                alert.showAndWait();

                BorderPane rootPane = (BorderPane) ownerStage.getScene().getRoot();
                if (rootPane.getCenter() instanceof ScrollPane) {
                    ScrollPane scrollPane = (ScrollPane) rootPane.getCenter();
                    if (scrollPane.getContent() instanceof FlowPane) {
                        FlowPane itemsFlowPane = (FlowPane) scrollPane.getContent();
                        displayItems(itemsFlowPane, "", "All", "Low to High");
                    } else {
                        System.err.println("The content of ScrollPane is not a FlowPane.");
                    }
                } else {
                    System.err.println("The center of BorderPane is not a ScrollPane.");
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete the item. Please try again.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> itemStage.close());

        itemDetailsLayout.getChildren().addAll(itemName, itemPrice, itemStock, deleteButton, closeButton);

        // Set a larger window size
        Scene itemScene = new Scene(itemDetailsLayout, 500, 600); // Increased size
        itemStage.setScene(itemScene);
        itemStage.show();
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-font-size: 14px; -fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-padding: 10px; -fx-border-color: #cccccc;");
        textField.setPrefWidth(300);
    }

    public void setFileHandler(FileHandlerMANAGER fileHandler) {
        this.fileHandler = fileHandler;
    }
}
