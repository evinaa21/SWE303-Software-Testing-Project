package util;

import model.Bill;
import model.Cashier;
import model.Item;
import model.Sector;
import model.User;
import util.Role;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * FileHandler with Static Analysis fixes for EI_EXPOSE_REP2 and DateFormat thread-safety.
 * Focuses on defensive copying of mutable collections and resolving static DateFormat issues.
 */
public class FileHandler {
	// Constants for file paths
	private static final String EMPLOYEE_FILE = "src/BinaryFiles/employees.dat";
	private static final String INVENTORY_FILE = "src/BinaryFiles/items.dat";
	private static final String BILL_DIRECTORY = "src/BinaryFiles/Bills/";
	private static final String SECTOR_FILE = "src/BinaryFiles/sectors.dat";
	
	// Final reference to a helper class
	private final EmployeeFileHandler EmployeeFile = new EmployeeFileHandler();

	/**
	 * Fix for SpotBugs: Call to method of static java.text.DateFormat.
	 * SimpleDateFormat is not thread-safe. By using ThreadLocal, each thread
	 * gets its own instance, preventing potential race conditions during formatting/parsing.
	 */
	private static final ThreadLocal<SimpleDateFormat> threadSafeFormat = 
			ThreadLocal.withInitial(() -> new SimpleDateFormat("dd-MM-yyyy"));

	public FileHandler() {
	}

	// Load inventory data for a specific sector
	public ArrayList<Item> loadInventoryBySector(String sector) {
		ArrayList<Item> inventory = loadInventory();
		ArrayList<Item> sectorInventory = new ArrayList<>();

		for (Item item : inventory) {
			if (item.getItemSector().equalsIgnoreCase(sector)) {
				sectorInventory.add(item);
			}
		}

		return sectorInventory;
	}

	// Save the entire inventory to the binary file
	public void saveInventory(ArrayList<Item> inventory) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE))) {
			// Fix: Defensive copy of the list before writing
			oos.writeObject(new ArrayList<>(inventory)); 
			System.out.println("Inventory saved successfully to binary file: " + INVENTORY_FILE);
		} catch (IOException e) {
			System.err.println("Error saving inventory to binary file: " + e.getMessage());
		}
	}

	/**
	 * Fix for SpotBugs: EI_EXPOSE_REP2 (Line 100)
	 * Instead of directly using the sectors list, we create a defensive copy.
	 */
	public void saveSectors(ArrayList<Sector> sectors) {
		if (sectors == null || sectors.isEmpty()) {
			System.out.println("No sectors to save.");
			return; 
		}

		// Fix: Store a copy of the list to prevent external modification
		ArrayList<Sector> sectorsCopy = new ArrayList<>(sectors);

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SECTOR_FILE))) {
			out.writeObject(sectorsCopy); 
			System.out.println("Sectors saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error saving sectors to file.");
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Sector> loadSectors() {
		ArrayList<Sector> sectors = new ArrayList<>();

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SECTOR_FILE))) {
			Object obj = ois.readObject();
			if (obj instanceof ArrayList<?>) {
				ArrayList<?> tempList = (ArrayList<?>) obj;

				if (!tempList.isEmpty() && tempList.get(0) instanceof Sector) {
					sectors = (ArrayList<Sector>) tempList;
					System.out.println("Sectors loaded successfully.");
				} else {
					System.err.println("Error: File data is not of type ArrayList<Sector>");
				}
			}
		} catch (IOException e) {
			System.out.println("Error loading sectors: File not found or unable to read.");
		} catch (ClassNotFoundException e) {
			System.out.println("Error loading sectors: Class not found.");
		}

		return sectors;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Item> loadInventory() {
		ArrayList<Item> inventory = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INVENTORY_FILE))) {
			inventory = (ArrayList<Item>) ois.readObject();
			System.out.println("Inventory loaded successfully from binary file: " + INVENTORY_FILE);
		} catch (FileNotFoundException e) {
			System.err.println("Inventory binary file not found: " + INVENTORY_FILE);
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error loading inventory from binary file: " + e.getMessage());
		}
		return inventory;
	}

	public ArrayList<Bill> loadBills() {
	    ArrayList<Bill> bills = new ArrayList<>();
	    File billDirectory = new File(BILL_DIRECTORY);

	    if (!billDirectory.exists() || !billDirectory.isDirectory()) {
	        return bills;
	    }

	    File[] billFiles = billDirectory.listFiles((dir, name) -> name.endsWith(".txt"));

	    if (billFiles == null || billFiles.length == 0) {
	        return bills;
	    }

	    Date today = new Date();

	    for (File billFile : billFiles) {
	        Bill bill = loadBillFromFile(billFile);
	        if (bill != null) {
	            if (isSameDay(bill.getSaleDate(), today)) {
	                bills.add(bill);
	            } 
	        }
	    }

	    return bills;
	}

	public Bill loadBillFromFile(File billFile) {
	    Bill bill = null;
	    try (BufferedReader br = new BufferedReader(new FileReader(billFile))) {
	        String line;
	        String billNumber = null;
	        ArrayList<Item> items = new ArrayList<>();
	        double totalAmount = 0.0;
	        Date saleDate = null;
	        boolean isItemSection = false;

	        while ((line = br.readLine()) != null) {
	            line = line.trim();

	            if (line.startsWith("Bill Number:")) {
	                billNumber = line.split(":")[1].trim();
	            } else if (line.startsWith("Date:")) {
	                String dateString = line.split(":")[1].trim();
	                // Fix: Accessed via ThreadLocal instance to resolve static DateFormat issues
	                saleDate = threadSafeFormat.get().parse(dateString); 
	            } else if (line.startsWith("Total Amount:")) {
	                totalAmount = Double.parseDouble(line.split(":")[1].trim());
	            } else if (line.startsWith("Items:")) {
	                isItemSection = true;
	            } else if (isItemSection && !line.startsWith("-") && !line.isEmpty()) {
	                if (line.startsWith("Item Name")) continue;

	                String[] itemDetails = line.split("\\s{2,}");
	                if (itemDetails.length >= 4) {
	                    String itemName = itemDetails[0].trim();
	                    String category = itemDetails[1].trim();
	                    double price = Double.parseDouble(itemDetails[3].trim());
	                    items.add(new Item(itemName, category, price, 0, 0));
	                }
	            }
	        }

	        if (billNumber != null && !items.isEmpty() && totalAmount > 0 && saleDate != null) {
	            bill = new Bill(billNumber, items, totalAmount, saleDate);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return bill;
	}

	public ArrayList<Item> filterItemsByCategory(String category) {
		ArrayList<Item> filteredItems = new ArrayList<>();
		ArrayList<Item> inventory = loadInventory();

		for (Item item : inventory) {
			if (item.getCategory().equalsIgnoreCase(category)) {
				filteredItems.add(item);
			}
		}

		return filteredItems;
	}

	public ArrayList<String> loadCategoriesBySectors() {
		ArrayList<String> categories = new ArrayList<>();
		ArrayList<Item> inventory = loadInventory();

		for (Item item : inventory) {
			String category = item.getCategory();
			if (!categories.contains(category)) {
				categories.add(category);
			}
		}

		return categories;
	}

	public static ArrayList<Cashier> loadCashiers() {
		ArrayList<Cashier> cashiers = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
			Object obj = ois.readObject();
			if (obj instanceof ArrayList) {
				ArrayList<?> list = (ArrayList<?>) obj;
				for (Object item : list) {
					if (item instanceof Cashier) {
						cashiers.add((Cashier) item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cashiers;
	}

	public ArrayList<Cashier> loadCashiersByRole() {
		ArrayList<Cashier> cashiers = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
			@SuppressWarnings("unchecked")
			ArrayList<User> users = (ArrayList<User>) ois.readObject();

			for (User user : users) {
				if (user instanceof Cashier && user.getRole() == Role.Cashier) {
					cashiers.add((Cashier) user);
				}
			}
		} catch (Exception e) {
			System.err.println("Error reading from file: " + e.getMessage());
		}
		return cashiers;
	}

	public void updateInventoryForSale(ArrayList<Item> soldItems) throws IllegalArgumentException, IOException {
	    ArrayList<Item> inventory = loadInventory();

	    for (Item soldItem : soldItems) {
	        boolean itemFound = false;

	        for (Item inventoryItem : inventory) {
	            if (inventoryItem.getItemName().equalsIgnoreCase(soldItem.getItemName())) {
	                itemFound = true;
	                if (!inventoryItem.hasSufficientStock(soldItem.getStockQuantity())) {
	                    throw new IllegalArgumentException("Insufficient stock for item: " + soldItem.getItemName());
	                }
	                inventoryItem.sellItem(soldItem.getStockQuantity());
	                break;
	            }
	        }

	        if (!itemFound) {
	            throw new IllegalArgumentException("Item not found in inventory: " + soldItem.getItemName());
	        }
	    }
	    saveInventory(inventory);
	}

	public void addEmployeeSalary(String employeeName, double salary) {
		ArrayList<User> employees = EmployeeFile.loadEmployeeData();
		boolean found = false;

		for (User user : employees) {
			if (user.getName().equalsIgnoreCase(employeeName)) {
				user.setSalary(salary);
				found = true;
				break;
			}
		}

		if (found) {
			EmployeeFile.saveEmployeeData(employees);
			System.out.println("Salary updated for employee: " + employeeName);
		} else {
			System.err.println("Employee not found: " + employeeName);
		}
	}

	public void saveBill(String billNumber, ArrayList<Item> items, double total, String cashierName, String sector) {
		// Fix: Thread-safe formatting via ThreadLocal
		String date = threadSafeFormat.get().format(new Date());
		String fileName = BILL_DIRECTORY + billNumber + "_" + date + ".txt";
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write("=========================================\n");
			writer.write("                ELECTRONIC STORE          \n");
			writer.write("=========================================\n");
			writer.write("Bill Number: " + billNumber + "\n");
			writer.write("Cashier: " + cashierName + "\n");
			writer.write("Sector: " + sector + "\n");
			writer.write("Date: " + date + "\n");
			writer.write("-----------------------------------------\n");
			writer.write("Items:\n");
			writer.write(String.format("%-20s %-10s %-10s %-10s\n", "Item Name", "Category", "Quantity", "Price"));
			writer.write("-----------------------------------------\n");

			for (Item item : items) {
				writer.write(String.format("%-20s %-10s %-10d %-10.2f\n", 
						item.getItemName(),
						item.getCategory() != null ? item.getCategory() : "Uncategorized",
						item.getStockQuantity(), item.getSellingPrice()));
			}

			writer.write("-----------------------------------------\n");
			writer.write(String.format("Total Amount: %.2f\n", total));
			writer.write("=========================================\n");
			writer.write("          THANK YOU FOR SHOPPING         \n");
			writer.write("=========================================\n");
		} catch (IOException e) {
			System.err.println("Error saving bill to file: " + fileName);
		}
	}
	
	public boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) return false;
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    cal1.setTime(date1);
	    cal2.setTime(date2);
	    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	public boolean isItemOutOfStock(String itemName, String sector) {
		ArrayList<Item> sectorItems = loadInventoryBySector(sector);
		for (Item item : sectorItems) {
			if (item.getItemName().equalsIgnoreCase(itemName)) {
				return item.getStockQuantity() == 0;
			}
		}
		return true;
	}
}