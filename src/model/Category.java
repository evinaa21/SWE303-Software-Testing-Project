package model;

import java.io.Serializable;

public class Category implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4051107329821678997L;
	private String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}