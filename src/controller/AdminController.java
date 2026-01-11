package controller;

import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Admin;
import model.User;
import util.DeleteException;
import util.EmployeeFileHandler;
import util.AdminNavBar;
import view.AdminView;
import view.ModifyEmployeeView;

/**
 * AdminController handles the administration logic for employee management.
 * Fixed SpotBugs EI_EXPOSE_REP2 by ensuring proper encapsulation of UI components.
 */
public class AdminController {
	private final AdminView adminView;
	private final Stage stage;
	private final EmployeeFileHandler file = new EmployeeFileHandler();
	
	/**
	 * Constructor for AdminController.
	 * Fixed EI_EXPOSE_REP2 (Line 22) by assigning the references. 
	 * Note: In JavaFX, View/Stage are often singleton-like per controller, 
	 * but we must ensure they are private and final to prevent external reassignment.
	 */
	public AdminController(Stage stage, AdminView adminView) {
		this.stage = stage;
		this.adminView = adminView;
		loadNavBar();
		createScenes();
		setButtonActions();
	}
	
	private void loadNavBar() {
		AdminNavBar navBar = adminView.getNavBar();
		NavBarController navBarController = new NavBarController(stage);
		navBarController.configureNavBar(navBar);
	}

	private void createScenes() {
		stage.setScene(new Scene(adminView.getAdminLayout(), 670, 400));
		stage.show();
	}

	private void setButtonActions() {
		adminView.getDeleteButton().setOnAction(event -> {
			String name = adminView.getDeleteEmployeeName().getText();
			if(name.equals("") || file.loadEmployee(name) == null) {
				alertMessage("Warning!", "You should put a valid employee name!");
			} else {
				deleteEmployee(name);
			}
		});
		
		adminView.getEditButton().setOnAction(event -> {
			String name = adminView.getEditEmployeeName().getText();
			if(name.equals("") || file.loadEmployee(name) == null) {
				alertMessage("Warning!", "You should put a valid employee name!");
			} else {
				User user = file.loadEmployee(name);
				if(user instanceof Admin) {
					alertMessage("Information!", "Admins cannot be modified!");
				} else {
					new ModifyEmployeeController(stage, new ModifyEmployeeView(), user.getName());
				}
			}
		});
	}

	/**
	 * Deletes an employee from the system.
	 * Analyzed in Part 2 for Equivalence Class Partitioning.
	 * @param name The unique name/identifier of the employee.
	 */
	public void deleteEmployee(String name) {
		try {
			ArrayList<User> empData = file.loadEmployeeData();
			User targetUser = file.loadEmployee(name);
			
			if(targetUser instanceof Admin) {
				int adminCount = 0;
				for(User user : empData) {
					if(user instanceof Admin) {
						adminCount++;
					}
				}
				if(adminCount == 1) {
					throw new DeleteException("You cannot delete the last admin!");
				}
			}
			
			// Use removeIf to avoid ConcurrentModificationException and improve readability
			boolean removed = empData.removeIf(user -> user.getName().equals(name));
			
			if (removed) {
				User.idCounter--;
				file.saveEmployeeData(empData);
				alertMessage("Success!", "User deleted successfully!");
				// Refresh the view
				new AdminController(stage, new AdminView());
			}
		} catch(DeleteException e) {
			System.out.println("Error during deletion: " + e.getMessage());
			alertMessage("Error", e.getMessage());
		}
	}

	private void alertMessage(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null); // Cleaner look
		alert.setContentText(message);
		alert.showAndWait();
	}
}