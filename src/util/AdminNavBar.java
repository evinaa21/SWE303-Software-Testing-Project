package util;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class AdminNavBar {
	private HBox navigationBar;
	private Button homeButton, register;
	
	public AdminNavBar() {
		navigationBar = new HBox(20);
        navigationBar.setAlignment(Pos.CENTER);
        
        homeButton = new Button("Show statistics");

        register = new Button("Register a new User");
		

        navigationBar.getChildren().addAll(
                homeButton, register
                );
	}
	
	public HBox getNavBar() {
		return navigationBar;
	}

	public Button getHomeButton() {
		return homeButton;
	}

	public Button getRegister() {
		return register;
	}
}
