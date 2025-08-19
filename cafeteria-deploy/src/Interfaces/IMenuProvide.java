package Interfaces;

import Core.MenuItem;

import java.util.List;

public interface IMenuProvide {
public List<MenuItem> listItems();
public void add(MenuItem item);
public void update(MenuItem item);
public void remove(int id);

}
