package view;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import model.Sector;
import util.FileHandler;
import util.AdminNavBar;
import util.Role;

public class RegisterEmployeeView {
	private GridPane layout;
	private FlowPane sectorPane;
	private TextField name, salary, username, password, phone, email;
	private DatePicker dob;
	private ComboBox<Role> role;
	private AdminNavBar navBar;
	private ArrayList<CheckBox> sectorCheckBoxes =  new ArrayList<>();
	private Button register;
	
	public RegisterEmployeeView() {
		layout = new GridPane();
		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.setVgap(10);
		layout.setHgap(10);
		layout.setAlignment(Pos.CENTER);
		
		navBar = new AdminNavBar();
		navBar.getNavBar();
		
		Text registerText = new Text("Register new employee");
		
		Label nameL = new Label("Name");
		name = new TextField();
		
		Label salaryL = new Label("Salary");
		salary = new TextField();
        salary.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
		
		Label usernameL = new Label("Username");
		username = new TextField();
		Label passwordL = new Label("Password");
		password = new TextField();
		Label dobL = new Label("Date of Birth");
		dob = new DatePicker();
		dob.setPrefWidth(350);
		Label phoneL = new Label("Phone number");
		phone = new TextField();
		phone.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
		Label emailL = new Label("Email");
		email = new TextField();
		Label roleL = new Label("Role");
		role = new ComboBox<>();
		role.setItems(FXCollections.observableArrayList(Role.values()));
		
		Label sectorL = new Label("Sector");
		sectorPane = new FlowPane();
		sectorPane.setHgap(10);
		sectorPane.setVgap(5);
		
		ArrayList<Sector> sectors = loadSectors();
		for(Sector sector : sectors) {
			CheckBox sectorCheckBox = new CheckBox(sector.getName());
			sectorCheckBoxes.add(sectorCheckBox);
			sectorPane.getChildren().add(sectorCheckBox);
		}
		
        for (CheckBox checkBox : sectorCheckBoxes) {
            checkBox.setSelected(false);
            checkBox.setDisable(true);
            checkBox.setOnAction(null);
        }
		
		role.valueProperty().addListener((observable, oldValue, newValue) -> handleRoleChange(role.getValue()));
		
		register = new Button("Register");
		
		layout.add(navBar.getNavBar(), 0, 0);
		layout.add(registerText, 0, 1);
		layout.add(nameL, 0, 2);
		layout.add(name, 0, 3);
		layout.add(salaryL, 0, 4);
		layout.add(salary, 0, 5);
		layout.add(usernameL, 0, 6);
		layout.add(username, 0, 7);
		layout.add(passwordL, 0, 8);
		layout.add(password, 0, 9);
		layout.add(dobL, 0, 10);
		layout.add(dob, 0, 11);
		layout.add(emailL, 0, 12);
		layout.add(email, 0, 13);
		layout.add(phoneL, 0, 14);
		layout.add(phone, 0, 15);
		layout.add(roleL, 0, 16);
		layout.add(role, 0, 17);
		layout.add(sectorL, 0, 18);
        layout.add(sectorPane, 0, 19);
        layout.add(register, 0, 20);
	}
	
	private void handleRoleChange(Role role) {
		for (CheckBox checkBox : sectorCheckBoxes) {
	        checkBox.setSelected(false);
	        checkBox.setDisable(true);
	        checkBox.setOnAction(null);
	    }
		
		if (role == Role.Manager) {
	        for (CheckBox checkBox : sectorCheckBoxes) {
	            checkBox.setDisable(false);
	        }
	    } else if (role == Role.Cashier) {
	        for (CheckBox checkBox : sectorCheckBoxes) {
	            checkBox.setDisable(false);
	            checkBox.setOnAction(event -> {
	                if (checkBox.isSelected()) {
	                    for (CheckBox otherCheckBox : sectorCheckBoxes) {
	                        if (otherCheckBox != checkBox) {
	                            otherCheckBox.setSelected(false);
	                        }
	                    }
	                }
	            });
	        }
	    } else if (role == Role.Admin) {
	        for (CheckBox checkBox : sectorCheckBoxes) {
	            checkBox.setDisable(true);
	        }
	    }
	}

	public ArrayList<Sector> loadSectors() {
		FileHandler file = new FileHandler();
		return file.loadSectors();
	}
	
	public ArrayList<Sector> getSelectedSectors(){
		ArrayList<Sector> selectedSectors = new ArrayList<>();
		ArrayList<Sector> sectors = loadSectors();
		for(int i = 0; i < sectorCheckBoxes.size(); i++) {
			if(sectorCheckBoxes.get(i).isSelected()) {
				selectedSectors.add(sectors.get(i));
			}
		}
		
		return selectedSectors;
	}

	public GridPane getLayout() {
		return layout;
	}
	
	public TextField getName() {
		return name;
	}

	public TextField getUsername() {
		return username;
	}

	public TextField getPassword() {
		return password;
	}
	
	public DatePicker getDob() {
		return dob;
	}
	
	public TextField getPhone() {
		return phone;
	}

	public TextField getSalary() {
		return salary;
	}

	public Role getRole() {
		return role.getValue();
	}

	public TextField getEmail() {
		return email;
	}

	public Button getRegisterButton() {
		return register;
	}
	
	public ArrayList<CheckBox> getSectors(){
		return sectorCheckBoxes;
	}
	
	public AdminNavBar getNavBar() {
		return navBar;
	}
}
