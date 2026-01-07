package controller;
import java.util.ArrayList;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Manager;
import model.Sector;
import util.FileHandlerMANAGER;
import view.ViewSectorsView;

public class ViewSectorsController {

    private Stage primaryStage;
    private Manager manager;
    private FileHandlerMANAGER fileHandler;

    public ViewSectorsController(Stage primaryStage, Manager manager, FileHandlerMANAGER fileHandler) {
        this.primaryStage = primaryStage;
        this.manager = manager;
        this.fileHandler = fileHandler;
        loadSectors();
        setupUI();
    }

    private void loadSectors() {
        ArrayList<Sector> loadedSectors = fileHandler.loadManagerSectors();
        manager.setSectors(loadedSectors);
    }
    private void setupUI() {
        ViewSectorsView view = new ViewSectorsView(manager);
        view.setFileHandler(fileHandler);
        view.setController(this);
        primaryStage.setScene(new Scene((Parent) view.getSceneContent(), 400, 400));
        primaryStage.setTitle("Manager Dashboard");
        primaryStage.show();
    }
   
    public void addCategory(String sectorName, String categoryName) {
        if (!categoryName.isEmpty()) {
            Sector foundSector = null;
             
            for (Sector sector : manager.getSectors()) {
                if (sector.getName().equals(sectorName)) {
                    foundSector = sector;
                    break;
                }
            }

            if (foundSector != null) {
                foundSector.addCategory(categoryName); 
                fileHandler.saveSectors(manager.getSectors()); 
            } else {
                System.out.println("Sector not found: " + sectorName);
            }
        } else {
            System.out.println("Category name cannot be empty.");
        }
    }

    }

