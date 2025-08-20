package app;

import Services.SystemHandler;

import java.util.Scanner;

/**
 * Terminal Application Launcher for ITI Cafeteria Console Interface
 */
public class Terminal {

    public static void main(String[] args) {
        System.out.println("🏫 ITI Cafeteria - Console Interface");
        System.out.println("=====================================");

        Scanner scanner = new Scanner(System.in);
        SystemHandler systemHandler = new SystemHandler(scanner);

        try {
            // Initialize the system
            if (!systemHandler.initializeSystem()) {
                System.err.println("❌ Failed to initialize system. Exiting...");
                return; // Exit if database connection fails
            }

            // Run the main application flow
            systemHandler.runApplication();

        } catch (Exception e) {
            System.err.println("❌ Error running terminal application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }

        System.out.println("\n👋 Thank you for using ITI Cafeteria System!");
    }
}
