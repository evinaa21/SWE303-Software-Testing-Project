// View: ViewSectorsView
package view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Manager;
import model.Sector;
import util.FileHandlerMANAGER;
import controller.ViewSectorsController;

import java.util.ArrayList;

public class ViewSectorsView {
    private Manager manager;
    private ViewSectorsController controller;

    public ViewSectorsView(Manager manager) {
        this.manager = manager;
    }

    public Node getSceneContent() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #eaf3fc;");

        ScrollPane scrollPane = new ScrollPane();
        VBox sectorsLayout = new VBox(15);
        scrollPane.setContent(sectorsLayout);
        scrollPane.setFitToWidth(true);

        ArrayList<Sector> sectors = manager.getSectors();
        for (Sector sector : sectors) {
            VBox sectorBox = createSectorBox(sector);
            sectorsLayout.getChildren().add(sectorBox);
        }


        layout.getChildren().addAll( scrollPane);
        return layout;
    }

    private VBox createSectorBox(Sector sector) {
        VBox sectorBox = new VBox(5);
        sectorBox.setPadding(new Insets(10));
        sectorBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #003366;");

        Label sectorLabel = new Label(sector.getName());
        sectorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        sectorLabel.setTextFill(Color.web("#003366"));

        Button addCategoryButton = new Button("+ Add Category");
        addCategoryButton.setOnAction(e -> showAddCategoryDialog(sector.getName()));

        VBox categoryLayout = new VBox(5);
        categoryLayout.setPadding(new Insets(10));
        for (String category : sector.getCategories()) {
            Label categoryLabel = new Label(category);
            styleCategoryLabel(categoryLabel);
            categoryLayout.getChildren().add(categoryLabel);
        }

        categoryLayout.setVisible(false);
        sectorLabel.setOnMouseClicked(e -> categoryLayout.setVisible(!categoryLayout.isVisible()));

        HBox sectorRow = new HBox(10, sectorLabel, addCategoryButton);
        sectorBox.getChildren().addAll(sectorRow, categoryLayout);

        return sectorBox;
    }

    private void showAddCategoryDialog(String sectorName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter the name of the new category:");
        dialog.setContentText("Category name:");

        dialog.showAndWait().ifPresent(categoryName -> controller.addCategory(sectorName, categoryName));
    }

    private void styleCategoryLabel(Label label) {
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5px;");
    }

    public void setFileHandler(FileHandlerMANAGER fileHandler) {
    }

    public void setController(ViewSectorsController controller) {
        this.controller = controller;
    }
}
