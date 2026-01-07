package controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Admin;
import model.Cashier;
import model.Manager;
import model.Sector;
import model.User;
import util.CredentialsException;
import util.EmployeeFileHandler;
import util.AdminNavBar;
import util.Role;
import view.AdminView;
import view.RegisterEmployeeView;

public class RegisterEmployeeController {
	private final Stage stage;
	private final RegisterEmployeeView regEmpView;
	private final EmployeeFileHandler file = new EmployeeFileHandler();
	
	public RegisterEmployeeController(Stage stage, RegisterEmployeeView regEmpView) {
		this.stage = stage;
		this.regEmpView = regEmpView;
		loadNavBar();
		createScene();
		setButtonAction();
	}
	
	private void loadNavBar() {
		AdminNavBar navBar = regEmpView.getNavBar();
		NavBarController navBarController = new NavBarController(stage);
		navBarController.configureNavBar(navBar);
	}
	
	private void createScene() {
		stage.setScene(new Scene(regEmpView.getLayout()));
		stage.show();
	}
	
	private void setButtonAction() {
		regEmpView.getRegisterButton().setOnAction(e -> {
			try {
				String name = regEmpView.getName().getText();
				String s = regEmpView.getSalary().getText();
				Role role = regEmpView.getRole();
				String username = regEmpView.getUsername().getText();			
				String password = regEmpView.getPassword().getText();			
				LocalDate dob = regEmpView.getDob().getValue();
				Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
				String phone = regEmpView.getPhone().getText();	
				String email = regEmpView.getEmail().getText();	
				ArrayList<Sector> sectors = getSelectedSectors();
				
				if(validateCredentials(name, s, username, password, date, phone, role, email, sectors)) {
					double salary = parseSalary(s);
					User user = null;
					if(role == Role.Admin) {
						user = new Admin(name, salary, Role.Admin, username, password, date, phone, email);
					}else if(role == Role.Manager) {
						user = new Manager(name, salary, Role.Manager, username, password, date, phone, email, sectors);
					}else if(role == Role.Cashier) {
						Sector sector = null;
						for(int i=0; i < sectors.size(); i++) {
							sector = sectors.get(i);
						}
						user = new Cashier(name, salary, Role.Cashier, username, password, date, phone, email, sector);
					}
					
					if (user != null) {
					    ArrayList<User> data = file.loadEmployeeData(); // Load existing data
					    data.add(user); // Add the new user
					    file.saveEmployeeData(data); // Save the updated data
					    showRegistrationSuccess(); // Show success message
					    new AdminController(stage, new AdminView());
					}else{
					    System.out.println("Error: User could not be created.");
					}
						
				}
			}catch(Exception e1) {
				System.out.println("Error during registration: " + e1.getMessage());
				showAlert("Error during registration!");
				return;
			}
		});
		
		
	}

	private boolean validateCredentials(String name, String salary, String username, String password, Date dob, String phone, Role role,  String email, ArrayList<Sector> sectors) throws CredentialsException {
		if(name == null || salary == "" || username == null || password == null || dob == null || phone == ""  || email == null || sectors.isEmpty() && role != Role.Admin) {
			showAlert("You should add a value for each textfield!");
			throw new CredentialsException("No value for every textfield");
		}
		if(!email.contains(String.valueOf("@"))) {
			showAlert("Please enter a valid email!");
			throw new CredentialsException("Email input is wrong!");
		}
		
		ArrayList<User> data = file.loadEmployeeData();
		for(User user : data) {
			if(user.getName().equals(name)) {
				showAlert("Please enter a valid name!");
				throw new CredentialsException("Name already exists!");
			}
			
			if(user.getUsername().equals(username)) {
				showAlert("Please enter a valid username!");
				throw new CredentialsException("Username already exists!");
			}
			
			if(user.getEmail().equals(email)) {
				showAlert("Please enter a valid email!");
				throw new CredentialsException("Email already exists!");
			}
			
		}
		
		return true;
	}
	
	private ArrayList<Sector> getSelectedSectors() {
		ArrayList<Sector> allSectors = regEmpView.loadSectors();
		ArrayList<Sector> selectedSectors = new ArrayList<>();
	    for (int i = 0; i < regEmpView.getSectors().size(); i++) {
	        CheckBox checkBox = (CheckBox) regEmpView.getSectors().get(i);
	        if (checkBox.isSelected()) {
	            selectedSectors.add(allSectors.get(i));
	        }
	    }
	    return selectedSectors;
	}

	private double parseSalary(String salaryString) {
		try {
			return Double.parseDouble(salaryString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid salary formal.");
		}
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Validation Error");
		alert.setHeaderText("Invalid Input");
		alert.setContentText(message);
	    alert.showAndWait(); 
	}
	
	private void showRegistrationSuccess() {
		Alert successMsg = new Alert(AlertType.INFORMATION);
		successMsg.setTitle("User registered Successfully");
		successMsg.setHeaderText(null);
		successMsg.setContentText("The user was registered successfully!");
	    successMsg.showAndWait(); 
	}
}
