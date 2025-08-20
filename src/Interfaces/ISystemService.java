package Interfaces;

import Core.MenuItem;
import java.util.List;

/**
 * Interface for system initialization and configuration
 */
public interface ISystemService {
    boolean testDatabaseConnection();
    void initializeSampleData();
    List<MenuItem> getAllMenuItems();
}
