package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Sector implements Serializable {
   
	private static final long serialVersionUID = 1L;
	private String sectorName;
    private ArrayList<Item> items;
    private ArrayList<String> categories;  // Add a list to store categories

    public Sector(String sectorName) {
        this.sectorName = sectorName;
        this.items = new ArrayList<>();
        this.categories = new ArrayList<>();  // Initialize the categories list
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addCategory(String category) {
        categories.add(category);  // Add category to the list
    }

    public ArrayList<Item> viewItems() {
        return items;
    }

    public ArrayList<String> getCategories() {
        return categories;  // Get the list of categories
    }

    @Override
    public String toString() {
        return "Sector{" +
                "Name='" + sectorName + '\'' +
                ", Number of Items=" + items.size() +
                ", Categories=" + categories.size() +  // Display category count
                '}';
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public String getName() {
        return sectorName;  // Return the correct sector name
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sector sector = (Sector) obj;
        return sectorName != null && sectorName.equals(sector.sectorName); // Compare based on name or other fields
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectorName); // Consistent with equals
    }

}
