package controller;

import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import model.Admin;
import model.Cashier;
import model.Manager;
import model.User;
import util.EmployeeFileHandler;
import util.FileHandlerMANAGER;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import view.LoginView;
import view.ManagerView;
import view.AdminView;

public class LoginController {
	private final Stage stage;
	private final LoginView loginView;
	private Scene loginScene;
	private final EmployeeFileHandler file = new EmployeeFileHandler();

	public LoginController(Stage stage, LoginView loginView) {
		this.stage = stage;
		this.loginView = loginView;
		createScenes();
		setButtonAction();
	}

	private void createScenes() {
		loginScene = new Scene(loginView.getLoginPane(), 400, 300);
	}

	private void setButtonAction() {
		loginView.getLoginButton().setOnAction(event -> {
			String username = loginView.getUsernameField().getText();
			String password = loginView.getPasswordField().getText();

			authenticate(username, password);
		});
	}

	private void authenticate(String username, String password) {
		ArrayList<User> employees = new ArrayList<>();
		employees = file.loadEmployeeData();

		for (User user : employees) {
			String usernameInFile = user.getUsername();
			String passwordInFile = user.getPassword();

			if (user instanceof Admin) {
				if (username.equals(usernameInFile) && password.equals(passwordInFile)) {
					AdminView adminView = new AdminView();
					new AdminController(stage, adminView);
					break;
				} else {
					errorLabel();
				}
			}else if (user instanceof Manager) {
			    if (username.equals(usernameInFile) && password.equals(passwordInFile)) {
			        // Create the ManagerController with the primary stage and Manager
			        ManagerController managerController = new ManagerController(stage, (Manager) user);
			        
			        // Create the ManagerView, passing the ManagerController, primaryStage, Manager, and FileHandler
			       new ManagerView(managerController, stage, (Manager) user, new FileHandlerMANAGER());

			        // Set up the UI or additional logic if needed
			    }
			} else if (user instanceof Cashier) {
				if (username.equals(usernameInFile) && password.equals(passwordInFile)) {
					Cashier theCashierUser = (Cashier) user;
					new CashierController(stage, theCashierUser);
					break;
				} else {
					errorLabel();
				}
			}
		}
	}

	private void errorLabel() {
		Label errorMessageLabel = new Label();
		GridPane loginLayout = loginView.getLoginPane();

		errorMessageLabel.setText("Wrong Credentials");
		errorMessageLabel.setStyle("-fx-text-fill: red;");
		loginLayout.add(errorMessageLabel, 0, 5);

		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(e1 -> loginLayout.getChildren().remove(errorMessageLabel));
		pause.play();
	}

	public Scene getLoginScene() {
		return loginScene;
	}
}