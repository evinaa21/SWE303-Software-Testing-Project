package view;

import java.text.SimpleDateFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.SalesMetrics;
import model.User;
import util.EmployeeFileHandler;
import util.AdminNavBar;
import util.Role;

import java.util.Date;

public class AdminView {
	private final EmployeeFileHandler file = new EmployeeFileHandler();
	private ScrollPane adminLayout;
	private Button deleteEmployee, MEmployeeB;
	private TextField deleteEmployeeTF, MEmployeeTF;
	public AdminNavBar navBar;
	private TableView<User> table;
	private ObservableList<User> observableList;
	
	public AdminView() {
		navBar = new AdminNavBar();
		HBox nav = navBar.getNavBar();
		
		Text EmpTableText = new Text("Data about employees:");
		table = createEmployeeTable();
		
		Text MEmployeeT = new Text("Edit Employee");
		MEmployeeTF = new TextField();
		MEmployeeB = new Button("Edit");
		
		HBox MEmployeeHBox = new HBox(10);
		MEmployeeHBox.setAlignment(Pos.CENTER);
		MEmployeeHBox.getChildren().addAll(MEmployeeT, MEmployeeTF, MEmployeeB);
		
		Text deleteEmployeeT = new Text("Delete Employee");
		deleteEmployeeTF = new TextField();
		deleteEmployee = new Button("Delete");
		
		HBox deleteEmployeeHBox = new HBox(10);
		deleteEmployeeHBox.getChildren().addAll(deleteEmployeeT, deleteEmployeeTF, deleteEmployee);
		deleteEmployeeHBox.setAlignment(Pos.CENTER);
		
		Text totalIncome = new Text("Total Income");
		Text incomeValue = new Text();
		incomeValue.setText(String.valueOf(getTotalIncome()));
		Text totalOutcome = new Text("Total Outcome");
		Text outcomeValue = new Text();
		outcomeValue.setText(String.valueOf(getTotalOutcome()));
		
		
		HBox incomeOutcomeHBox = new HBox(10);
		incomeOutcomeHBox.setAlignment(Pos.CENTER);
		incomeOutcomeHBox.getChildren().addAll(totalIncome, incomeValue, totalOutcome, outcomeValue);
		
		VBox vbox = new VBox(15);
		vbox.getChildren().addAll(nav, EmpTableText, table, MEmployeeHBox, deleteEmployeeHBox, incomeOutcomeHBox);
		vbox.setPadding(new Insets(10,10,10,10));
		adminLayout = new ScrollPane(vbox);
	    adminLayout.setFitToWidth(true);
	    adminLayout.setFitToHeight(true);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private TableView<User> createEmployeeTable() {
		TableView<User> table = new TableView<>();
		
		TableColumn<User, Integer> idCol = new TableColumn<>("Id");
		idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
		
		TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		TableColumn<User, Date> dobCol = createFormattedDateColumn("Date of Birth", "dateOfBirth");
		
		TableColumn<User, Role> roleCol = new TableColumn<>("Role");
		roleCol.setCellValueFactory(new PropertyValueFactory<>("Role"));
		
		TableColumn<User, Double> salaryCol = new TableColumn<>("Salary");
		salaryCol.setCellValueFactory(new PropertyValueFactory<>("Salary"));
		
		TableColumn<User, String> usernameCol = new TableColumn<>("Username");
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		
		TableColumn<User, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		TableColumn<User, String> phonenumCol = new TableColumn<>("Phone number");
		phonenumCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
		
		observableList = userList();
		table.setItems(observableList);
		table.getColumns().addAll(idCol, nameCol, dobCol, roleCol, salaryCol, usernameCol, emailCol, phonenumCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		return table;
	}

	private TableColumn<User, Date> createFormattedDateColumn(String title, String property) {
		TableColumn<User, Date> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(property));
		column.setCellFactory(col -> new TableCell<User, Date> () {
			private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			
			@Override
			protected void updateItem(Date date, boolean empty) {
				super.updateItem(date, empty);
				if(empty || date == null) {
					setText(null);
				}else {
					setText(formatter.format(date));
				}
			}
		});
		return column;
	}
	
	private double getTotalOutcome() {
		SalesMetrics sm = new SalesMetrics();
		return sm.getTotalCosts();	
	}
	
	private double getTotalIncome() {
		SalesMetrics sm = new SalesMetrics();
		return sm.getTotalRevenue();
	}

	public ScrollPane getAdminLayout() {
		return adminLayout;
	}
	
	public ObservableList<User> userList(){
		return FXCollections.observableArrayList(file.loadEmployeeData());
	}

	public TextField getDeleteEmployeeName() {
		return deleteEmployeeTF;
	}
	
	public Button getDeleteButton() {
		return deleteEmployee;
	}
	
	public TextField getEditEmployeeName() {
		return MEmployeeTF;
	}
	
	public Button getEditButton() {
		return MEmployeeB;
	}
	
	public ObservableList<User> getUserList(){
		return observableList;
	}

	public AdminNavBar getNavBar() {
		return navBar;
	}
}
