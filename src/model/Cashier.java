package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import util.Role;


public class Cashier extends User implements Serializable {
	private static final long serialVersionUID = -6931271312379051945L;
	
	private Sector sector; 
	private ArrayList<Bill> bills;  
	private double totalSales; 
	private ArrayList<Item> items;
	
	public Cashier() {
		
	}
	
	public Cashier(String name, double salary, Role role, String username, String password, 
			Date dateOfBirth, String phonenumber, String email, Sector sector) {
		super(name, salary, role, username, password, dateOfBirth, phonenumber, email);
		this.sector = sector;
		this.bills = new ArrayList<>();
		this.totalSales = 0.0;
	}
	
	public Sector getSector() {
		return sector;
	}
	
	public void setSector(Sector sector) {
		this.sector = sector;
	}
	
	public ArrayList<Bill> getBills() {
		return bills;
	}
	
	public void setBills(ArrayList<Bill> bills) {
		this.bills = bills;
	}
	
	public double getTotalSales() {
		return totalSales;
	}
	
	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}
	

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	
	public ArrayList<Item> getItems() {
		return this.items;
	}
	
		
	
}