package app;

import GUI.Gui;

/**
 * Simple Main Launcher - Direct way to run the ITI Cafeteria GUI
 * This assumes the project is already compiled (e.g., by your IDE)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("🚀 Launching ITI Cafeteria GUI Application...");

        // Set JavaFX system properties programmatically
        setJavaFXProperties();

        try {
            // Launch the JavaFX application
            Gui.main(args);
        } catch (Exception e) {
            System.err.println("❌ Error launching GUI: " + e.getMessage());
            e.printStackTrace();

            // Provide helpful error message
            System.err.println("\n💡 If you see module-related errors, make sure:");
            System.err.println("   1. JavaFX SDK 21 is downloaded and placed in lib/javafx/javafx-sdk-21.0.1/");
            System.err.println("   2. Your IDE's run configuration includes JavaFX modules");
            System.err.println("   3. VM options are set correctly (see README for details)");
        }
    }

    /**
     * Sets JavaFX system properties for module configuration
     */
    private static void setJavaFXProperties() {
        String javafxPath = System.getProperty("user.dir") + "/lib/javafx/javafx-sdk-21.0.1/lib";

        // Set module path and add modules
        System.setProperty("javafx.runtime.path", javafxPath);

        // Print helpful information
        System.out.println("📁 Project directory: " + System.getProperty("user.dir"));
        System.out.println("🎭 Expected JavaFX path: " + javafxPath);
        System.out.println("☕ Java version: " + System.getProperty("java.version"));
    }
}
