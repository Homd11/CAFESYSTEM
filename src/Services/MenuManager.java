package Services;

import Core.MenuItem;
import DB.MenuDAO;
import Interfaces.IMenuProvide;
import Values.Money;
import Enums.Category;

import java.util.List;

public class MenuManager {
    private IMenuProvide menu;

    public MenuManager() {
        this.menu = new MenuDAO();
    }

    public MenuManager(IMenuProvide menu) {
        this.menu = menu;
    }

    public void addItem(String name, String description, Money price, Category category) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);

        menu.add(item);
    }

    public void editItem(int itemId, String name, String description, Money price, Category category) {
        MenuItem item = new MenuItem();
        item.setId(itemId);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);

        menu.update(item);
    }

    public void updateItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        menu.update(item);
    }

    public void removeItem(int itemId) {
        menu.remove(itemId);
    }

    public List<MenuItem> listItems() {
        return menu.listItems();
    }
}
