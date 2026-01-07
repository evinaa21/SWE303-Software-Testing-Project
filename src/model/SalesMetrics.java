package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class SalesMetrics implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3790826895954351115L;
	private double totalRevenue; // Total revenue from items sold
    private int totalItemsSold; // Total items sold
    private String timePeriod; // Time period for the report
    private double totalCosts; // Total costs (items purchased + staff salaries)

    private ArrayList<Bill> bills; // List of all bills
    private ArrayList<String> cashiers; // List of cashier names
    private ArrayList<Double> salaries; // List of salaries corresponding to employees

    public SalesMetrics() {
        this.totalRevenue = 0.0;
        this.totalItemsSold = 0;
        this.timePeriod = "";
        this.totalCosts = 0.0;
        this.bills = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.salaries = new ArrayList<>();
    }

    // Calculate sales metrics based on a list of items
    public void calculateMetrics(ArrayList<Item> items) {
        totalRevenue = 0.0;
        totalItemsSold = 0;
        for (Item item : items) {
            totalRevenue += item.getPrice() * item.getItemsSold();
            totalItemsSold += item.getItemsSold();
        }
    }

    // Record a bill 
    public void recordBill(String cashierName, Bill bill) {
        bills.add(bill);
        if (!cashiers.contains(cashierName)) {
            cashiers.add(cashierName);
        }
    }

    // Calculate the total bills and revenue for a specific cashier for the current day
    public double calculateCashierDailyTotal(Cashier cashier) {
        double total = 0.0;
        for (Bill bill : cashier.getBills()) {
            if (isSameDay(bill.getSaleDate(), new Date())) {
                total += bill.getTotalAmount();
            }
        }
        return total;
    }

    // Calculate the total income and total costs for the administrator
    public void calculateAdminMetrics(ArrayList<Item> purchasedItems, ArrayList<User> employees) {
        double totalIncome = 0.0;
        totalCosts = 0.0;

        for (Bill bill : bills) {
            totalIncome += bill.getTotalAmount();
        }

        for (Item item : purchasedItems) {
            totalCosts += item.getPrice() * item.getStockQuantity();
        }

        for (User user : employees) {
            totalCosts += user.getSalary();
        }

        System.out.println("Total Income: $" + totalIncome);
        System.out.println("Total Costs: $" + totalCosts);
        System.out.println("Net Profit: $" + (totalIncome - totalCosts));
    }

    // Helper method to compare dates for daily sales
    @SuppressWarnings("deprecation")
	private boolean isSameDay(Date date1, Date date2) {
        return date1.getYear() == date2.getYear() &&
               date1.getMonth() == date2.getMonth() &&
               date1.getDate() == date2.getDate();
    }

    // Add a new employee's salary
    public void addEmployeeSalary(String employeeName, double salary) {
        cashiers.add(employeeName);
        salaries.add(salary);
    }

    // Getters and Setters
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalItemsSold() {
        return totalItemsSold;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public double getTotalCosts() {
        return totalCosts;
    }

    public void setTotalCosts(double totalCosts) {
        this.totalCosts = totalCosts;
    }
}

