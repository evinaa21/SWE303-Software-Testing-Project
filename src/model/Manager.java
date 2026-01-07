package model;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.Date;


import util.Role;

public class Manager extends User implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7638438919947446592L;
	private ArrayList<Sector> sectors;  // Sectors used instead of categories
    private ArrayList<Supplier> suppliers;
    private ArrayList<Item> items;
    private SalesMetrics salesMetrics; 
    private ArrayList<Cashier> cashiers;



    public Manager() {
    	
    }

    public Manager(String name, double salary, Role role, String username, String password, Date dateOfBirth,
                   String phonenumber, String email, ArrayList<Sector> sectors) {
        super(name, salary, role, username, password, dateOfBirth, phonenumber, email);
        this.sectors = sectors;
        this.suppliers = new ArrayList<>();
        this.items = new ArrayList<>();
        this.salesMetrics = new SalesMetrics();
        this.cashiers = new ArrayList<>();
    }

  

    public ArrayList<String> getSupplierNames() {
        ArrayList<String> supplierNames = new ArrayList<>();
        for (Supplier supplier : suppliers) { 
            supplierNames.add(supplier.getSupplierName());
        }
        return supplierNames;
    }
    // Getter and Setter methods
    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(ArrayList<Sector> sectors) {
        this.sectors = sectors;
    }

    public ArrayList<Supplier> getSuppliers() {
        return suppliers;
    }
    
    public void setSuppliers(ArrayList<Supplier> suppliers) {
        this.suppliers = suppliers;
    }
    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public SalesMetrics getSalesMetrics() {
        return salesMetrics;
    }

    public void setSalesMetrics(SalesMetrics salesMetrics) {
        this.salesMetrics = salesMetrics;
    }

    public ArrayList<Cashier> getCashiers() {
        return cashiers;
    }

    public void setCashiers(ArrayList<Cashier> cashiers) {
        this.cashiers = cashiers;
    }

}
