package util;
 
import model.Bill;
import model.Cashier;
import model.Item;
import model.Sector;
import model.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FileHandler {
	// Constants for file paths CHANGE IF THESE DONT WORK FOR YOU
	private static final String EMPLOYEE_FILE = "src/BinaryFiles/employees.dat"; // Binary files for employees
	private static final String INVENTORY_FILE = "src/BinaryFiles/items.dat"; // Binary files for inventory
	private static final String BILL_DIRECTORY = "src/BinaryFiles/Bills/"; // Text files for bills
	private static final String SECTOR_FILE = "src/BinaryFiles/sectors.dat"; // Path to sector file
	private final EmployeeFileHandler EmployeeFile = new EmployeeFileHandler();

	// Date format for parsing and saving dates
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");

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
			oos.writeObject(inventory); // Write the entire list of items to the file
			System.out.println("Inventory saved successfully to binary file: " + INVENTORY_FILE);
		} catch (IOException e) {
			System.err.println("Error saving inventory to binary file: " + e.getMessage());
		}
	}

	
	public void saveSectors(ArrayList<Sector> sectors) {
		if (sectors == null || sectors.isEmpty()) {
			System.out.println("No sectors to save.");
			return; // If the list is empty, do not proceed with saving.
		}

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SECTOR_FILE))) {
			out.writeObject(sectors); // Serialize the sectors list (which includes categories).
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
			e.printStackTrace();
			System.out.println("Error loading sectors: File not found or unable to read.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error loading sectors: Class not found.");
		} catch (ClassCastException e) {
			e.printStackTrace();
			System.out.println("Error loading sectors: Incorrect data format in file.");
		}

		return sectors;
	}

	// Load inventory data from the item.dat file
	// This method does not modify or update the inventory file
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
	        System.out.println("The specified directory does not exist or is not a valid directory: " + BILL_DIRECTORY);
	        return bills;
	    }

	    File[] billFiles = billDirectory.listFiles((dir, name) -> name.endsWith(".txt"));

	    if (billFiles == null || billFiles.length == 0) {
	        System.out.println("No bill files found in directory: " + BILL_DIRECTORY);
	        return bills;
	    }

	    Date today = Calendar.getInstance().getTime();

	    for (File billFile : billFiles) {
	        Bill bill = loadBillFromFile(billFile);
	        if (bill != null) {
	        	// Filter for today's bills
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
	                saleDate = dateFormat.parse(dateString); // Ensure dateFormat matches file's date format
	            } else if (line.startsWith("Total Amount:")) {
	                totalAmount = Double.parseDouble(line.split(":")[1].trim());
	            } else if (line.startsWith("Items:")) {
	                isItemSection = true;
	            } else if (isItemSection && line.startsWith("-----------------------------------------")) {
	                // Skip the header divider line
	                continue;
	            } else if (isItemSection && !line.isEmpty()) {
	                // Skip the header row or invalid lines
	                if (line.startsWith("Item Name")) {
	                    continue; // Skip the header row
	                }

	                // Parse item details (assumes columns are aligned and separated by spaces)
	                String[] itemDetails = line.split("\\s{2,}"); // Splits on 2 or more spaces
	                if (itemDetails.length >= 4) {
	                    try {
	                        String itemName = itemDetails[0].trim();
	                        String category = itemDetails[1].trim();
	                        double price = Double.parseDouble(itemDetails[3].trim());

	                        // Create and add the item to the list
	                        items.add(new Item(itemName, category, price, 0, 0));
	                    } catch (NumberFormatException e) {
	                        System.err.println("Skipping invalid item line: " + line);
	                    }
	                }
	            }
	        }

	        if (billNumber != null && !items.isEmpty() && totalAmount > 0 && saleDate != null) {
	            bill = new Bill(billNumber, items, totalAmount, saleDate);
	        }
	    } catch (IOException | NumberFormatException | java.text.ParseException e) {
	        e.printStackTrace();
	    }
	    return bill;
	}


	// Filter items by category
	public ArrayList<Item> filterItemsByCategory(String category) {
		ArrayList<Item> filteredItems = new ArrayList<>();
		ArrayList<Item> inventory = loadInventory(); // Load all items from inventory

		for (Item item : inventory) {
			if (item.getCategory().equalsIgnoreCase(category)) {
				filteredItems.add(item); // Add item to list if it matches the category
			}
		}

		return filteredItems;
	}

	

	public ArrayList<String> loadCategoriesBySectors() {
		ArrayList<String> categories = new ArrayList<>();
		ArrayList<Item> inventory = loadInventory(); // Assuming this method loads items

		for (Item item : inventory) {
			String category = item.getCategory();
			if (!categories.contains(category)) {
				categories.add(category); // Only add unique categories
			}
		}

		return categories; // Return the list of categories
	}

	

	// Method to load existing cashiers from the binary file
	public static ArrayList<Cashier> loadCashiers() {
		ArrayList<Cashier> cashiers = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
			while (true) {
				Cashier cashier = (Cashier) ois.readObject();
				cashiers.add(cashier);
			}
		} catch (EOFException e) {
			// End of file reached
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cashiers;
	}

	public ArrayList<Cashier> loadCashiersByRole() {
		ArrayList<Cashier> cashiers = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
			// First, read the entire ArrayList of Users
			@SuppressWarnings("unchecked")
			ArrayList<User> users = (ArrayList<User>) ois.readObject(); // Read the whole list

			// Now filter based on role
			for (User user : users) {
				if (user instanceof Cashier && user.getRole() == Role.Cashier) {
					cashiers.add((Cashier) user);
				}
			}
		} catch (EOFException e) {
			// Handle end of file gracefully (if needed)
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error reading from file: " + e.getMessage());
			e.printStackTrace();
		}
		return cashiers;
	}

	
	// Update inventory after a sale (used by the cashier)
	// This method validates stock availability and saves the updated inventory
	public void updateInventoryForSale(ArrayList<Item> soldItems) throws IllegalArgumentException, IOException {
	    // Load current inventory
	    ArrayList<Item> inventory = loadInventory();

	    // Validate and update inventory
	    for (Item soldItem : soldItems) {
	        boolean itemFound = false;

	        for (Item inventoryItem : inventory) {
	            if (inventoryItem.getItemName().equalsIgnoreCase(soldItem.getItemName())) {
	                itemFound = true;

	                // Check stock availability
	                if (!inventoryItem.hasSufficientStock(soldItem.getStockQuantity())) {
	                    throw new IllegalArgumentException("Insufficient stock for item: " + soldItem.getItemName());
	                }

	                // Deduct sold quantity from stock
	                inventoryItem.sellItem(soldItem.getStockQuantity());
	                System.out.println("Updated stock for item: " + soldItem.getItemName());
	                break;
	            }
	        }

	        if (!itemFound) {
	            throw new IllegalArgumentException("Item not found in inventory: " + soldItem.getItemName());
	        }
	    }

	    // Save updated inventory back to the file
	    saveInventory(inventory);
	    System.out.println("Inventory updated and saved successfully.");
	}

	

	// Save inventory data to the item.dat file
	public void saveIventory(ArrayList<Item> inventory) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE))) {
			oos.writeObject(inventory);
			System.out.println("Inventory saved successfully to binary file: " + INVENTORY_FILE);
		} catch (IOException e) {
			System.err.println("Error saving inventory to binary file: " + e.getMessage());
		}
	}

	// Add employee salary or update if employee exists
	public void addEmployeeSalary(String employeeName, double salary) {
		ArrayList<User> employees = EmployeeFile.loadEmployeeData(); // Load current employees
		boolean found = false;

		for (int i = 0; i < employees.size(); i++) {
			User user = employees.get(i);
			if (user.getName().equalsIgnoreCase(employeeName)) {
				user.setSalary(salary); // Update salary if the employee exists
				found = true;
				break;
			}
		}

		if (!found) {
			System.err.println("Employee not found: " + employeeName);
		} else {
			EmployeeFile.saveEmployeeData(employees); // Save updated employees back to the file
			System.out.println("Salary updated for employee: " + employeeName);
		}
	}
	public ArrayList<String> readBills() {
		ArrayList<String> billsData = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(BILL_DIRECTORY))) {
			String line;
			while ((line = reader.readLine()) != null) {
				billsData.add(line); // Add each bill entry to the list
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return billsData;
	}

	public ArrayList<String> readBills1() {
        ArrayList<String> billsData = new ArrayList<>();
        File folder = new File("src/BinaryFiles/Bills"); // Folder containing bill files

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(); // List all files in the folder
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                billsData.add(line); // Add each line (bill data) to the list
                            }
                        } catch (IOException e) {
                            e.printStackTrace(); // Handle file read exceptions
                        }
                    }
                }
            }
        } else {
            System.out.println("Folder does not exist or is not a directory.");
        }

        return billsData;
    }
	public void appendSalesSummary(String billNumber, double totalAmount, String date, String cashierName) {
	    String summaryFilePath = "C:\\Users\\Evina\\git\\Electronics-Store\\src\\BinaryFiles\\sales_summary.txt";
	    File summaryFile = new File(summaryFilePath);

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile, true))) {
	        // Creating the line that includes the cashier's name, bill number, date, and total amount
	        String line = "Bill Number: " + billNumber + ", Date: " + date + ", Total Amount: " + totalAmount + ", Cashier: " + cashierName;
	        writer.write(line + "\n");
	        System.out.println("Written to file: " + line);  // Debugging log
	    } catch (IOException e) {
	        System.err.println("Error updating sales summary: " + e.getMessage());
	    }
	}


	

	// Save a bill to a text file
	public void saveBill(String billNumber, ArrayList<Item> items, double total, String cashierName, String sector) {
		String date = dateFormat.format(new Date());
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

			// Loop through items and print details including category
			for (Item item : items) {
				writer.write(String.format("%-20s %-10s %-10d %-10.2f\n", item.getItemName(),
						item.getCategory() != null ? item.getCategory() : "Uncategorized", // Check for null category
						item.getStockQuantity(), item.getSellingPrice()));
			}

			writer.write("-----------------------------------------\n");
			writer.write(String.format("Total Amount: %.2f\n", total));
			writer.write("=========================================\n");
			writer.write("          THANK YOU FOR SHOPPING         \n");
			writer.write("=========================================\n");

			System.out.println("Bill saved successfully to " + fileName);
			appendSalesSummary(billNumber, total, date, cashierName);
		} catch (IOException e) {
			System.err.println("Error saving bill to file: " + fileName + ". Cause: " + e.getMessage());
		}
	}
	
	public boolean isSameDay(Date date1, Date date2) {
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    cal1.setTime(date1);
	    cal2.setTime(date2);
	    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}


	
	// Notify low stock items for the specified sector where cashier is assigned
	public boolean isItemOutOfStock(String itemName, String sector) {
		ArrayList<Item> sectorItems = loadInventoryBySector(sector); // Load items for the sector

		for (Item item : sectorItems) {
			if (item.getItemName().equalsIgnoreCase(itemName)) { // Match the item name
				return item.getStockQuantity() == 0; // Return true if stock is 0
			}
		}

		// If the item is not found in the sector inventory, treat it as unavailable
		return true;
	}

}
