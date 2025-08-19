package Interfaces;

import Core.Admin;
import java.util.Scanner;

/**
 * Interface for admin management operations
 */
public interface IAdminManager {
    Admin login(String username, String password);
    boolean createAdmin(String username, String password, String name, String role);
    Admin performLogin(Scanner scanner);
    void performLogout(Admin admin);
    void viewAllStudents(Scanner scanner);
    void viewAllMenuItems(Scanner scanner);
    void addNewMenuItem(Scanner scanner);
    void editMenuItem(Scanner scanner);
    void removeMenuItem(Scanner scanner);
}
