package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Supplier implements Serializable {
    private static final long serialVersionUID = -4499787452810759813L;

    private String supplierName;
    private ArrayList<Item> suppliedItems;
    private ArrayList<String> itemIds; 
    
    public Supplier( String supplierName) {
        this.supplierName = supplierName;
        this.itemIds = new ArrayList<>();
        this.suppliedItems = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (suppliedItems == null) {
            suppliedItems = new ArrayList<>(); 
        }
        suppliedItems.add(item);
    }

    public void removeItem(Item item) {
        suppliedItems.remove(item);
    }

    public String getSupplierName() {
        return supplierName;
    }

    public ArrayList<Item> getSuppliedItems() {
    	if(suppliedItems == null) {
    		suppliedItems = new ArrayList<Item>();
    	}
        return suppliedItems;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    
    public void setSuppliedItems(ArrayList<Item> suppliedItems) {
        this.suppliedItems = suppliedItems;
    }
    public List<String> getItemIds() {
        return itemIds;
    }
    public void removeItem(String itemId) {
        itemIds.remove(itemId);
    }


    @Override
    public String toString() {
        return "Supplier: " + supplierName;
    }


}

