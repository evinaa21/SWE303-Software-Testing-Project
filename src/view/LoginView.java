package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LoginView{
	private GridPane loginPane;
	private TextField usernameF;
	private PasswordField passwordF;
	private Button login;
	
	public LoginView() {
		loginPane = new GridPane();
		loginPane.setAlignment(Pos.CENTER);
		loginPane.setPadding(new Insets(20, 20, 20, 20));
		loginPane.setHgap(10);
		loginPane.setVgap(10);
		
		Text text = new Text("Enter your credentials");
		text.setFont(new Font(20));
		Label usernameL = new Label("Username");
		usernameF = new TextField();
		usernameF.setPromptText("Enter username");
		usernameF.setPrefWidth(200);
		
		Label passwordL = new Label("Password");
		passwordF = new PasswordField();
		passwordF.setPromptText("Enter your password");
	    passwordF.setPrefWidth(200);
	    
	    login = new Button("Login");
	    login.setStyle("-fx-background-color : #007BFF; -fx-text-fill: white; -fx-font-size: 14; -fx-cursor: hand;");
	    
	    loginPane.add(text, 0, 0);
		loginPane.add(usernameL, 0, 1);
		loginPane.add(usernameF, 0, 2);
		loginPane.add(passwordL, 0, 3);
		loginPane.add(passwordF, 0, 4);
		loginPane.add(login, 0, 6);
	}
	
	public GridPane getLoginPane() {
		return loginPane;
	}
	
	public TextField getUsernameField() {
		return usernameF;
	}
	
	public PasswordField getPasswordField() {
		return passwordF;
	}
	
	public Button getLoginButton() {
		return login;
	}
}
