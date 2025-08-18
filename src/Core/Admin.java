package Core;

public class Admin {
    private int id;
    private String username;
    private String passwordHash;
    private String name;
    private String role;

    public Admin() {}

    public Admin(String username, String passwordHash, String name, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
