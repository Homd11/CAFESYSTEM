package Core;

import Enums.Category;
import Enums.Currency;
import Values.Money;

public class MenuItem {
    private int id;
    private String name;
    private String description;
    private Money price;
    private Category category;

    public MenuItem(int id, String name, String description, Money price, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public MenuItem() {

    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getPrice() { return price; }
    public Category getCategory() { return category; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        // Default to EGP if no currency is set yet
        Currency currency = (this.price != null) ? this.price.getCurrency() : Enums.Currency.EGP;
        this.price = new Money(price, currency);
    }

    public void setPrice(Money price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        this.price = price;
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = category;
    }
}
