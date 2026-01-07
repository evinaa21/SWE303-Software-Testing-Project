package util;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.User;

public class EmployeeFileHandler {
	private static final String EMPLOYEE_FILE = "src/BinaryFiles/employees.dat"; 
	
	public void saveEmployeeData(ArrayList<User> employees) {
		if (employees.isEmpty()) {
			System.err.println("No employee data to save.");
			return;
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EMPLOYEE_FILE))) {
			oos.writeObject(employees);
			System.out.println("Employee data saved successfully to binary file: " + EMPLOYEE_FILE);
		} catch (IOException e) {
			System.out.println("Error saving employee data to binary file: " + e.getMessage());
		}
	}

	
	@SuppressWarnings("unchecked")
	public ArrayList<User> loadEmployeeData() {
		ArrayList<User> employees = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
			while (true) {
				try {
					employees = (ArrayList<User>) ois.readObject(); 
				} catch (EOFException e) {
					break;
				}
			}
			System.out.println("Employee data loaded successfully from binary file: " + EMPLOYEE_FILE);
		} catch (FileNotFoundException e) {
			System.err.println("Employee binary file not found: " + EMPLOYEE_FILE);
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error loading employee data from binary file: " + e.getMessage());
		}
		return employees;
	}

	public User loadEmployee(String Name) {
		ArrayList<User> user = new ArrayList<>();
		user = loadEmployeeData();
		for (User employee : user) {
			if (employee.getName().equals(Name)) {
				return employee;
			}
		}
		System.out.println("No user with the name of " + Name + " was found!");
		return null;
	}
}
