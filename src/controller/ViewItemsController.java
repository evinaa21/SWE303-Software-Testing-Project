package controller;

import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import model.Manager;
import util.FileHandlerMANAGER;
import view.ViewItemsView;

public class ViewItemsController {

    private ViewItemsView viewItemsView;

    public ViewItemsController(Manager manager, FileHandlerMANAGER fileHandler) {
        this.viewItemsView = new ViewItemsView(manager, fileHandler);
    }

    public void showViewItemsView(VBox containerLayout) {
        ScrollPane viewContent = viewItemsView.getViewContent();
        containerLayout.getChildren().clear(); 
        containerLayout.getChildren().add(viewContent); 
    }
}
