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

public class AdminController {
	private final AdminView adminView;
	private final Stage stage;
	private final EmployeeFileHandler file = new EmployeeFileHandler();
	
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
			if(adminView.getDeleteEmployeeName().getText().equals("") || file.loadEmployee(adminView.getDeleteEmployeeName().getText()) == null) {
				alertMessage("Warning!", "You should put a valid employee name!");
			}else {
				deleteEmployee(adminView.getDeleteEmployeeName().getText());
			}
		});
		
		adminView.getEditButton().setOnAction(event -> {
			if(adminView.getEditEmployeeName().getText().equals("") || file.loadEmployee(adminView.getEditEmployeeName().getText()) == null) {
				alertMessage("Warning!", "You should put a valid employee name!");
			}else {
				User user = file.loadEmployee(adminView.getEditEmployeeName().getText());
				if(user instanceof Admin) {
					alertMessage("Information!", "Admins cannot be modified!");
				}else {
					new ModifyEmployeeController(stage, new ModifyEmployeeView(), user.getName());
				}
			}
		});
	}

	private void deleteEmployee(String name) {
		try {
			ArrayList<User> empData = file.loadEmployeeData();
			User admin = file.loadEmployee(name);
			if(admin instanceof Admin) {
				int i=0;
				for(User user : empData) {
					if(user instanceof Admin) {
						i++;
					}
				}
				if(i==1) {
					throw new DeleteException("You cannot delete the last admin!");
				}
			}
			for(User user : empData) {
				if(user.getName().equals(name)) {
					empData.removeIf(obj -> obj.equals(user));
					User.idCounter--;
					break;
				}
			}
			file.saveEmployeeData(empData);
			alertMessage("Success!", "User deleted succesfully!");
			new AdminController(stage, new AdminView());
		}catch(DeleteException e) {
			System.out.println("Error during deletion: " + e.getMessage());
			alertMessage("Error", "There should be at least one Admin");
		}
	}

	private void alertMessage(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
