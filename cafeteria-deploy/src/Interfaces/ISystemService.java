package Interfaces;

/**
 * Interface for system initialization and configuration
 */
public interface ISystemService {
    boolean testDatabaseConnection();
    void initializeSampleData();
}
