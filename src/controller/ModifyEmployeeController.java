package controller;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import view.ModifyEmployeeView;

public class ModifyEmployeeController {
	private final EmployeeFileHandler file = new EmployeeFileHandler();
	private final Stage stage;
	private final ModifyEmployeeView MEV;
	private final String name;
	
	public ModifyEmployeeController(Stage stage, ModifyEmployeeView MEV, String name) {
		this.stage = stage;
		this.MEV = MEV;
		this.name = name;
		loadNavBar();
		createScenes();
		setButtonAction();
	}

	private void loadNavBar() {
		AdminNavBar nav = MEV.getNavBar();
		NavBarController navBarController = new NavBarController(stage);
		navBarController.configureNavBar(nav);
		
	}

	private void createScenes() {
		stage.setScene(new Scene(MEV.getLayout()));
		stage.show();
	}
	
	private void setButtonAction() {
		MEV.getModifyButton().setOnAction(event -> {
			User user = file.loadEmployee(this.name);
			
			String s = MEV.getSalary().getText();
			double salary = 0;
			
			if(s.isEmpty()) {
				salary = user.getSalary();
			}else {
				salary = parseSalary(s);
			}
			
			String username = MEV.getUsername().getText();
			String password = MEV.getPassword().getText();
			String email = null;
			try {
				email = MEV.getEmail().getText();
				if(!email.contains(String.valueOf("@")) && !email.isEmpty()) {
					throw new CredentialsException("Email input is wrong!");
				}
			}catch(CredentialsException e) {
				System.err.println("Error during modification: " + e.getMessage());
				showAlert("Please enter a valid email!");
			}
			
			Role role = MEV.getRole();
			if(role == null) {
				role = user.getRole();
						
			}
			ArrayList<Sector> sectors =MEV.getSelectedSectors();
			
			User newUser = null;
			try {
				if(role.equals(Role.Admin)) {
					Admin admin = modifyToAdmin(user, username, password, email, salary);
					newUser = (User) admin;
					}
					if(role.equals(Role.Manager)){
					Manager manager = modifyToManager(user, username, password, email, salary, sectors);
					newUser = (User) manager;
					}
					if(role.equals(Role.Cashier)){
					Sector sector = null;
					for(int i=0; i < sectors.size(); i++) {
						sector = sectors.get(i);
					}
					Cashier cashier = modifyToCashier(user, username, password, email, salary, sector);
					newUser = (User) cashier;
					}
					
					updateEmployeeFile(newUser);
					showSuccessMessage();
					AdminView adminView = new AdminView();
					new AdminController(stage, adminView);
			}catch(CredentialsException e) {
				System.err.println("Invalid credential input" + e.getMessage());
			}
		});
	}

	private void updateEmployeeFile(User newUser) {
		
		ArrayList<User> data = file.loadEmployeeData();
		
		for(int i=0; i < data.size(); i++ ) {
			if(data.get(i).getName().equals(name)) {
				data.set(i, newUser);
				break;
			}
		}
		
		file.saveEmployeeData(data);
		
	}

	private double parseSalary(String salaryString) {
		try {
			return Double.parseDouble(salaryString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid salary formal.");
		}
	}
	
	private Admin modifyToAdmin(User user, String username, String password, String email, double salary) {
		Admin admin = new Admin();
		admin.setUsername(username != "" ? username : user.getUsername());
	    admin.setPassword(password != "" ? password : user.getPassword());
	    admin.setEmail(email != "" ? email : user.getEmail());

	    admin.setId(user.getId());
	    admin.setName(user.getName());
	    admin.setSalary(salary);
	    admin.setRole(Role.Admin);
	    admin.setDateOfBirth(user.getDateOfBirth());
	    admin.setPhonenumber(user.getPhonenumber());
	    return admin;
	}
	
	private Manager modifyToManager(User user, String username, String password, String email, double salary,
			ArrayList<Sector> sectors) throws CredentialsException {
		Manager manager = new Manager();
	    
	    
	    manager.setUsername(username != "" ? username : user.getUsername());
	    manager.setPassword(password != "" ? password : user.getPassword());
	    manager.setEmail(email != "" ? email : user.getEmail());
	    
	    
	    if (sectors != null && !sectors.isEmpty()) {
	        manager.setSectors(sectors);
	    } else if(user instanceof Manager){
	        Manager existingManager = (Manager) user;
	        manager.setSectors(existingManager.getSectors());
	    }else {
	    	showAlert("You should select at least one sector!");
	    	throw new CredentialsException("Select a sector");
	    }

	    
	    manager.setId(user.getId());
	    manager.setName(user.getName());
	    manager.setSalary(salary);
	    manager.setRole(Role.Manager); 
	    manager.setDateOfBirth(user.getDateOfBirth());
	    manager.setPhonenumber(user.getPhonenumber());

	    return manager;
	}
	
	private Cashier modifyToCashier(User user, String username, String password, String email, double salary, Sector sector) throws CredentialsException {
		Cashier cashier = new Cashier();
		cashier.setUsername(username != "" ? username : user.getUsername());
	    cashier.setPassword(password != "" ? password : user.getPassword());
	    cashier.setEmail(email != "" ? email : user.getEmail());
	    
	    
	    if (sector != null) {
	        cashier.setSector(sector);
	    } else if(user instanceof Cashier){
	        Cashier existingCashier = (Cashier) user;
	        cashier.setSector(existingCashier.getSector());
	    }else {
	    	showAlert("You should select one sector!");
	    	throw new CredentialsException("Select a sector");
	    }

	    
	    cashier.setId(user.getId());
	    cashier.setName(user.getName());
	    cashier.setSalary(salary);
	    cashier.setRole(Role.Cashier);
	    cashier.setDateOfBirth(user.getDateOfBirth());
	    cashier.setPhonenumber(user.getPhonenumber());

	    return cashier;
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Validation Error");
		alert.setHeaderText("Invalid Input");
		alert.setContentText(message);
	    alert.showAndWait(); 
	}
	
	private void showSuccessMessage() {
		Alert successMsg = new Alert(AlertType.INFORMATION);
		successMsg.setTitle("User registered Successfully");
		successMsg.setHeaderText(null);
		successMsg.setContentText("The user was modified successfully!");
	    successMsg.showAndWait(); 
		
	}
}
