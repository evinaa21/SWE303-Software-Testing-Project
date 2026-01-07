package util;

import model.Cashier;
import model.Item;
import model.Manager;
import model.Sector;
import model.Supplier;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandlerMANAGER{
	// Constants for file paths CHANGE IF THESE DONT WORK FOR YOU
	private static final String EMPLOYEE_FILE = "src/BinaryFiles/employees.dat"; // Binary files for employees
	private static final String INVENTORY_FILE = "src/BinaryFiles/items.dat"; // Binary files for inventory
	private static final String SECTOR_FILE = "src/BinaryFiles/sectors.dat"; // Path to sector file
	private static final String SUPPLIER_FILE = "src/BinaryFiles/suppliers.dat"; // Binary files for suppliers

	public FileHandlerMANAGER() {

	}
	
	public boolean deleteItemAndUpdateSuppliers(Item item, ArrayList<Item> inventory, ArrayList<Supplier> suppliers) {
		boolean itemRemoved = inventory.remove(item);

		if (itemRemoved) {
		
			for (Supplier supplier : suppliers) {
				supplier.getSuppliedItems().removeIf(i -> i.getItemName().equals(item.getItemName()));
			}

			// Save updated data to files
			saveInventory(inventory);
			saveSuppliers(suppliers);
			return true;
		}
		return false;
	}

	
	public void saveInventory(ArrayList<Item> inventory) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE))) {
			oos.writeObject(inventory); 
			System.out.println("Inventory saved successfully to binary file: " + INVENTORY_FILE);
		} catch (IOException e) {
			System.err.println("Error saving inventory to binary file: " + e.getMessage());
		}
	}

	public void saveSuppliers(List<Supplier> suppliers) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SUPPLIER_FILE))) {
			oos.writeObject(suppliers);
			System.out.println("Suppliers saved successfully to binary file: " + SUPPLIER_FILE);
		} catch (IOException e) {
			System.err.println("Error saving suppliers: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Supplier> loadSuppliers() {
		ArrayList<Supplier> suppliers = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SUPPLIER_FILE))) {
			suppliers = (ArrayList<Supplier>) ois.readObject();
			System.out.println("Suppliers loaded successfully from binary file: " + SUPPLIER_FILE);
		} catch (FileNotFoundException e) {
			System.err.println("Suppliers binary file not found: " + SUPPLIER_FILE);
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error loading suppliers: " + e.getMessage());
		}
		return suppliers;
	}

	public void addSupplier(Supplier newSupplier) {
		ArrayList<Supplier> suppliers = loadSuppliers();
		suppliers.add(newSupplier);
		saveSuppliers(suppliers);
		System.out.println("New supplier added: " + newSupplier.getSupplierName());
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

		// Updated method for reading low stock items based on sector categories
	public static ArrayList<Item> readLowStockItemsFromBinaryFileMANAGER(String fileName, int threshold, ArrayList<Sector> managerSectors) {
	    ArrayList<Item> lowStockItems = new ArrayList<>();
	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
	        @SuppressWarnings("unchecked")
			ArrayList<Item> inventory = (ArrayList<Item>) ois.readObject();
	        
	        for (Item item : inventory) {
	            for (Sector sector : managerSectors) {
	                if (sector.getCategories().contains(item.getCategory()) && item.getStockQuantity() <= threshold) {
	                    lowStockItems.add(item);
	                    break; 
	                }
	            }
	        }
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	    return lowStockItems;
	}
	
		public ArrayList<Item> notifyLowStockforManager(int threshold, ArrayList<Sector> managerSectors) {
		    ArrayList<Item> lowStockItems = new ArrayList<>();

		    ArrayList<Item> allItems = readLowStockItemsFromBinaryFileMANAGER(INVENTORY_FILE, threshold, managerSectors);

		    for (Item item : allItems) {
		        System.out.println("Checking item: " + item.getItemName() + ", Stock: " + item.getStockQuantity() + ", Category: " + item.getCategory());
		        
		        for (Sector sector : managerSectors) {
		          
		            if (sector.getCategories().contains(item.getCategory()) && item.getStockQuantity() <= threshold) {
		                lowStockItems.add(item);
		                break;  
		            }
		        }
		    }
		    return lowStockItems;
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

	public boolean deleteItemFromSupplier(String itemId) {
		List<Supplier> suppliers = loadSuppliers();
		boolean updated = false;

		for (Supplier supplier : suppliers) {
			if (supplier.getItemIds().contains(itemId)) {
				supplier.removeItem(itemId);
				updated = true;
			}
		}

		if (updated) {
			saveSuppliers(suppliers);
		}
		return updated;
	}

	public void loadItemsAndAssociateWithSuppliers(ArrayList<Supplier> suppliers, ArrayList<Item> items) {
		for (Item item : items) {
			// Loop through all suppliers to find the supplier that matches the item's
			// supplier name
			for (Supplier supplier : suppliers) {
				if (supplier.getSupplierName().equals(item.getSupplierName())) {
					// Add the item to the supplier's suppliedItems list
					supplier.getSuppliedItems().add(item);
				}
			}
		}
	}

	
	public ArrayList<Cashier> loadCashiersByRole(ArrayList<Sector> managerSectors) {
        ArrayList<Cashier> cashiers = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEE_FILE))) {
			ArrayList<User> users = (ArrayList<User>) ois.readObject(); // Read the whole list
            // Debug: Print Manager Sectors
            System.out.println("Manager Sectors: " + managerSectors);
            for (User user : users) {
                if (user instanceof Cashier) {
                    Cashier cashier = (Cashier) user;
                    for (Sector managerSector : managerSectors) {
                        if (cashier.getSector().equals(managerSector)) {
                            cashiers.add(cashier);
                            break; 
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
        }
        return cashiers;
    }


    public ArrayList<Sector> loadManagerSectors() {
        ArrayList<Sector> managerSectors = new ArrayList<>();
        
        ArrayList<User> allUsers = loadEmployeeData();
       
        for (User user : allUsers) {
            if (user instanceof Manager) {
                Manager manager = (Manager) user;
                managerSectors.addAll(manager.getSectors()); 
            }
        }
        
        return managerSectors;
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


	// Load employee data from a binary file
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

}
