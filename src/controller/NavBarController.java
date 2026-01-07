package controller;
import javafx.stage.Stage;
import util.AdminNavBar;
import view.AdminView;
import view.RegisterEmployeeView;


public class NavBarController {
	private Stage stage;

	public NavBarController(Stage stage) {
		this.stage = stage;
	}
	
	public void configureNavBar(AdminNavBar navBar) {
		navBar.getHomeButton().setOnAction(event -> {
			AdminView admin = new AdminView();
			new AdminController(stage, admin);
		});
		
		navBar.getRegister().setOnAction(event -> {
			RegisterEmployeeView registerEmployeeView = new RegisterEmployeeView();
			new RegisterEmployeeController(stage, registerEmployeeView);
		});
	}
}
