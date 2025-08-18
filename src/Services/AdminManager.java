package Services;

import Core.Admin;
import DB.AdminDAO;

public class AdminManager {
    private AdminDAO adminDAO;

    public AdminManager() {
        this.adminDAO = new AdminDAO();
        // Ensure admin table exists and create default admin if none exist
        initializeAdminSystem();
    }

    public AdminManager(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
        initializeAdminSystem();
    }

    private void initializeAdminSystem() {
        try {
            adminDAO.createAdminTable();
            if (!adminDAO.adminExists()) {
                adminDAO.createDefaultAdmin();
            }
        } catch (Exception e) {
            System.err.println("Error initializing admin system: " + e.getMessage());
        }
    }

    public Admin login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return null;
        }

        Admin admin = adminDAO.findByUsername(username.trim());
        if (admin != null && PasswordHasher.verifyPassword(password, admin.getPasswordHash())) {
            return admin;
        }
        return null;
    }

    public boolean createAdmin(String username, String password, String name, String role) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty()) {
            return false;
        }

        // Check if username already exists
        if (adminDAO.findByUsername(username.trim()) != null) {
            return false;
        }

        String hashedPassword = PasswordHasher.hashPassword(password);
        Admin admin = new Admin(username.trim(), hashedPassword, name.trim(), role);

        try {
            adminDAO.save(admin);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
