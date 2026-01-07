package model;


import java.io.Serializable;
import java.util.Date;

import util.Role;


public class Admin extends User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Admin() {
		
	}
	
    public Admin(String name, double salary, Role role, String username, String password,
            Date dateOfBirth, String phonenumber, String email) {
        super(name, salary, role, username, password, dateOfBirth, phonenumber, email);
    }

}
