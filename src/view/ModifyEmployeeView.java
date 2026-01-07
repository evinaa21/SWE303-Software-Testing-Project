package view;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Sector;
import util.FileHandler;
import util.AdminNavBar;
import util.Role;

public class ModifyEmployeeView {
	private GridPane layout;
	private FlowPane sectorPane;
	private TextField username, password, salary, email;
	public AdminNavBar navBar;
	private ComboBox<Role> role;
	private ArrayList<CheckBox> sectorCheckBoxes =  new ArrayList<>();
	private Button Modify;
	
	public ModifyEmployeeView() {
		navBar = new AdminNavBar();
		HBox nav = navBar.getNavBar();
		
		layout = new GridPane();
		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.setVgap(8);
		layout.setHgap(10);
		layout.setAlignment(Pos.CENTER);
		
		Text editText = new Text("Modify credentials");
		editText.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
		
		Label usernameL = new Label("Username");
		username = new TextField();
		Label passwordL = new Label("Password");
		password = new TextField();
		Label emailL = new Label("Email");
		email = new TextField();
		Label salaryL = new Label("Salary");
		salary = new TextField();
        salary.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
		Label roleL = new Label("Role");
		role = new ComboBox<>();
		role.setPrefWidth(300);
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
		
		Modify = new Button("Modify");
		HBox modifyB = new HBox();
		modifyB.setPadding(new Insets(5,0,0,0));
		modifyB.getChildren().add(Modify);
		
		layout.add(nav, 0, 0);
		layout.add(editText, 0, 1);
		layout.add(usernameL, 0, 2);
		layout.add(username, 0, 3);
		layout.add(passwordL, 0, 4);
		layout.add(password, 0, 5);
		layout.add(emailL, 0, 6);
		layout.add(email, 0, 7);
		layout.add(salaryL, 0, 8);
		layout.add(salary, 0, 9);
		layout.add(roleL, 0, 10);
		layout.add(role, 0, 11);
		layout.add(sectorL, 0, 12);
        layout.add(sectorPane, 0, 13);
        layout.add(modifyB, 0, 14);
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

	private ArrayList<Sector> loadSectors() {
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

	public TextField getUsername() {
		return username;
	}

	public TextField getPassword() {
		return password;
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

	public Button getModifyButton() {
		return Modify;
	}
	
	public ArrayList<CheckBox> getSector(){
		return sectorCheckBoxes;
	}
	
	public AdminNavBar getNavBar() {
		return navBar;
	}
}
